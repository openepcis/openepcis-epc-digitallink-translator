package io.openepcis.identifiers.validator.core.epcis.noncompliant;

import io.openepcis.identifiers.validator.ValidationContext;
import io.openepcis.identifiers.validator.core.ApplicationIdentifierValidator;
import io.openepcis.identifiers.validator.core.Matcher;
import io.openepcis.identifiers.validator.core.util.CheckDigitValidator;
import io.openepcis.identifiers.validator.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

import static io.openepcis.constants.ApplicationIdentifierConstants.CPV_AI_URI_PREFIX;
import static io.openepcis.constants.ApplicationIdentifierConstants.SGTIN_AI_URI_PREFIX;

/**
 * Additional class to validate the identifiers: GTIN + CPV. Example: <a href="https://id.gs1.org/01/09520123456788/22/2A">https://id.gs1.org/01/09520123456788/22/2A</a>
 * These types of are not supported by the EPCIS 2.0.0 specification but valid according to the GS1
 */
public class GTINCPVValidator implements ApplicationIdentifierValidator {

    private static final List<Matcher> DIGITAL_LINK_VALIDATION_RULES = new ArrayList<>();

    static {
        // GTIN + CPV Digital Link URI identifier validation rules

        // Validate for the domain name (https://id.gs1.org/)
        DIGITAL_LINK_VALIDATION_RULES.add(
                new Matcher(
                        "(http|https)://.*",
                        "Invalid Identifier, DL URI should start with Domain name (Ex: https://id.gs1.org/), Please check the DL URI : %s"));


        // Validate for the 14 digit GTIN (01)
        DIGITAL_LINK_VALIDATION_RULES.add(
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

        // Validate for the CPV (22)
        DIGITAL_LINK_VALIDATION_RULES.add(
                new Matcher("(http|https)://.*./01/[0-9]{14}/22/[!%-?A-Z_a-z\\x22]{1,20}",
                        "Invalid Identifier, contains invalid CPV (22) (Ex: https://id.gs1.org/01/09520123456788/22/2A). Please check the DL URI: %s"));
    }

    @Override
    public boolean supportsValidation(final String identifier) {
        return identifier.contains(SGTIN_AI_URI_PREFIX) && identifier.contains(CPV_AI_URI_PREFIX);
    }

    @Override
    public boolean supportsValidation(final String identifier, final boolean isEpcisCompliant) {
        // if isEpcisCompliant is true then return false as the identifier is not EPCIS compliant
        if (Boolean.TRUE.equals(isEpcisCompliant)) {
            return false;
        }

        // else check if identifier contains DL URI part: "/01/", "/22/"
        return supportsValidation(identifier);
    }

    @Override
    public boolean validate(final String identifier, final ValidationContext validationContext) {
        // For Digital Link URIs, ensure a valid GCP length is provided.
        if (validationContext.getGcpLength() == null) {
            throw new ValidationException("Digital Link URI detected. Use validate(String, int) to validate Digital Link URIs with a GCP length.");
        }

        for (final Matcher matcher : DIGITAL_LINK_VALIDATION_RULES) {
            matcher.validate(identifier, validationContext);
        }

        // if validation success then return true
        return true;
    }
}
