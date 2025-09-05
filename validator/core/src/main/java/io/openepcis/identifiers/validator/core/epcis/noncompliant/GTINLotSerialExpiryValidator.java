package io.openepcis.identifiers.validator.core.epcis.noncompliant;

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.validator.ValidationContext;
import io.openepcis.identifiers.validator.core.ApplicationIdentifierValidator;
import io.openepcis.identifiers.validator.core.Matcher;
import io.openepcis.identifiers.validator.core.util.CheckDigitValidator;

import java.util.ArrayList;
import java.util.List;

import static io.openepcis.constants.ApplicationIdentifierConstants.*;

/**
 * Additional class to validate the identifiers: GTIN + Batch/Lot + Serial Number + Expiry Date.
 * Example: <a href="https://id.gs1.org/01/09520123456788/10/ABC1/21/12345?17=180426">https://id.gs1.org/01/09520123456788/10/ABC1/21/12345?17=180426</a>
 * These types of are not supported by the EPCIS 2.0.0 specification but valid according to the GS1
 */
public class GTINLotSerialExpiryValidator implements ApplicationIdentifierValidator {
    private static final List<Matcher> URI_MATCHERS = new ArrayList<>();

    static {
        // GTIN + LOT + INSTANCE + EXPIRY Digital Link URI identifier validation rules

        // Validate for the domain name (https://id.gs1.org/)
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*",
                        "Invalid Identifier, DL URI should start with Domain name (Ex: https://id.gs1.org/), Please check the DL URI : %s"));

        // Validate for the 14 digit GTIN (01)
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./01/[0-9]{14}(/.*|\\?.*)?",
                        "Invalid Identifier, DL URI should consist of 14 digit GTIN (Ex: https://id.gs1.org/01/09520123456788), Please check the DL URI : %s") {
                    @Override
                    public void validate(final String uri, final int gcpLength) throws ValidationException {
                        super.validate(uri);

                        // Check the provided GCP Length is between 6 and 12 digits
                        if (!(gcpLength >= 6 && gcpLength <= 12)) {
                            throw new ValidationException(
                                    String.format("Invalid GCP Length, GCP Length should be between 6-12 digits. Please check the provided GCP Length: %s", gcpLength));
                        }
                    }

                    @Override
                    public void validate(final String uri, final ValidationContext validationContext) throws ValidationException {
                        validate(uri, validationContext.getGcpLength());

                        if (!validationContext.isValidateCheckDigit()) {
                            return;
                        }

                        CheckDigitValidator.validateGTIN(uri);
                    }
                });

        // Validate for the Batch/Lot (10)
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./01/[0-9]{14}/10/[!%-?A-Z_a-z\\x22]{1,20}(/.*|\\?.*)?",
                        "Invalid Identifier, contains invalid Batch/Lot (10) (Ex: https://id.gs1.org/01/09520123456788/10/1111). Please check the DL URI: %s"));

        // Validate for the Serial Number (21)
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./01/[0-9]{14}/10/[!%-?A-Z_a-z\\x22]{1,20}/21/[!%-?A-Z_a-z\\x22]{1,20}.*",
                        "Invalid Identifier, contains invalid Serial Number (21) (Ex: https://id.gs1.org/01/09520123456788/10/ABC1/21/12345). Please check the DL URI: %s"));

        // Validate for the optional Expiry Date (17) - if provided validate format
        URI_MATCHERS.add(
                new Matcher("(?s).*", "Invalid 17 date") {
                    @Override
                    public void validate(final String uri) throws ValidationException {
                        final int q = uri.indexOf('?');
                        if (q < 0) return; // no query string

                        final String query = uri.substring(q + 1);
                        for (final String p : query.split("&")) {
                            if (p.startsWith("17=")) {
                                final String v = p.substring(3);
                                if (!v.matches("\\d{2}(?:0\\d|1[0-2])(?:[0-2]\\d|3[01])")) {
                                    throw new ValidationException(
                                            String.format("Invalid Identifier, contains invalid Expiry Date (17). Expected YYMMDD, got `%s` in: %s", v, uri)
                                    );
                                }
                            }
                        }
                    }
                });

    }

    @Override
    public boolean supportsValidation(final String identifier) {
        // For DL URI identifier check if identifier contains DL URI part: "/01/", "/10/", "/21/" and optional "?17="
        return identifier.contains(SGTIN_AI_URI_PREFIX) &&
                identifier.contains(LGTIN_AI_BATCH_LOT_PREFIX) &&
                identifier.contains(SGTIN_AI_URI_SERIAL_PREFIX);
    }

    @Override
    public boolean supportsValidation(final String identifier, final boolean isEpcisCompliant) {
        // if isEpcisCompliant is true then return false as the identifier is not EPCIS compliant
        if (Boolean.TRUE.equals(isEpcisCompliant)) {
            return false;
        }

        // else check if identifier contains DL URI part: "/01/", "/10/", "/21/" and optional "?17="
        return supportsValidation(identifier);
    }

    @Override
    public boolean validate(final String identifier, final ValidationContext validationContext) {
        // For Digital Link URIs, ensure a valid GCP length is provided.
        if (validationContext.getGcpLength() == null) {
            throw new ValidationException("Digital Link URI detected. Use validate(String, int) to validate Digital Link URIs with a GCP length.");
        }

        for (final Matcher matcher : URI_MATCHERS) {
            matcher.validate(identifier, validationContext);
        }

        // if validation success then return true
        return true;
    }
}
