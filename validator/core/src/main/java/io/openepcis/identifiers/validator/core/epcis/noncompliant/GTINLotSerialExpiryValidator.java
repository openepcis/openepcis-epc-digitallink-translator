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
        // For DL URI identifier check if identifier contains DL URI part: "/01/" with Lot "/10/" and/or Serial "/21/" and optional dates:
        /*
         * 11 : Production date (YYMMDD)
         * 12 : Due date for amount on payment slip (YYMMDD)
         * 13 : Packaging date (YYMMDD)
         * 15 : Best before date (YYMMDD)
         * 16 : Sell by date (YYMMDD)
         * 17 : Expiration date (YYMMDD)
         */

        // Validate for the domain name (https://id.gs1.org/)
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*",
                        "Invalid Identifier, DL URI should start with Domain name (Ex: https://id.gs1.org/), Please check the DL URI : %s"));

        // Validate for the 14 digit GTIN (01)
        URI_MATCHERS.add(
                new Matcher(
                        "^(?:http|https)://.*/01/\\d{14}(?:[/?#].*)?$",
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

        // Ensure least one of /10 or /21 present and no duplicates.
        URI_MATCHERS.add(new Matcher("(?s).*",
                "Invalid Identifier, must contain /10 {lot} or /21 {serial} after /01/{GTIN14} (any order, at least one).") {
            @Override
            public void validate(final String uri) throws ValidationException {
                final java.util.regex.Pattern p10 = java.util.regex.Pattern.compile("/10/([^/?#]+)");
                final java.util.regex.Pattern p21 = java.util.regex.Pattern.compile("/21/([^/?#]+)");

                int c10 = 0, c21 = 0;
                String lotVal = null, serVal = null;

                final java.util.regex.Matcher m10 = p10.matcher(uri);
                while (m10.find()) {
                    c10++;
                    lotVal = m10.group(1);
                }

                final java.util.regex.Matcher m21 = p21.matcher(uri);
                while (m21.find()) {
                    c21++;
                    serVal = m21.group(1);
                }

                if (c10 + c21 < 1) {
                    throw new ValidationException("Invalid Identifier, must contain /10 {lot} or /21 {serial} after /01/{GTIN14}.");
                }
                if (c10 > 1) {
                    throw new ValidationException("Invalid Identifier, /10 {lot} must not appear more than once.");
                }
                if (c21 > 1) {
                    throw new ValidationException("Invalid Identifier, /21 {serial} must not appear more than once.");
                }

                final String allowed = "[!%-?A-Z_a-z\\x22]{1,20}";
                if (lotVal != null && !lotVal.matches(allowed)) {
                    throw new ValidationException(String.format("Invalid Identifier, contains invalid Batch/Lot (10). Got `%s` in: %s", lotVal, uri));
                }
                if (serVal != null && !serVal.matches(allowed)) {
                    throw new ValidationException(String.format("Invalid Identifier, contains invalid Serial Number (21). Got `%s` in: %s", serVal, uri));
                }
            }
        });

        // Validate optional date AIs (11,12,13,15,16,17) as query params, format YYMMDD
        URI_MATCHERS.add(
                new Matcher("(?s).*", "Invalid date AI (YYMMDD)") {
                    @Override
                    public void validate(final String uri) throws ValidationException {
                        final int q = uri.indexOf('?');
                        if (q < 0) return; // no query string

                        final String query = uri.substring(q + 1);
                        final java.util.Set<String> dateAIs = new java.util.HashSet<>(java.util.Arrays.asList("11", "12", "13", "15", "16", "17"));

                        for (final String p : query.split("&")) {
                            final int eq = p.indexOf('=');
                            if (eq <= 0) continue;

                            final String key = p.substring(0, eq);
                            final String val = p.substring(eq + 1);

                            if (dateAIs.contains(key)) {
                                // YYMMDD with basic calendar sanity checks
                                if (!val.matches("\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\\d|3[01])")) {
                                    throw new ValidationException(String.format("Invalid Identifier, contains invalid %s date (YYMMDD). Got `%s` in: %s", key, val, uri));
                                }
                            }
                        }
                    }
                }
        );

    }

    @Override
    public boolean supportsValidation(final String identifier) {
        return identifier.contains(SGTIN_AI_URI_PREFIX) &&
                (identifier.contains(LGTIN_AI_BATCH_LOT_PREFIX) || identifier.contains(SGTIN_AI_URI_SERIAL_PREFIX));
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
