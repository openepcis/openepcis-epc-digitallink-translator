package io.openepcis.identifiers.validator.core.epcis.noncompliant;

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.validator.ValidationContext;
import io.openepcis.identifiers.validator.core.ApplicationIdentifierValidator;
import io.openepcis.identifiers.validator.core.Matcher;
import io.openepcis.identifiers.validator.core.util.CheckDigitValidator;

import java.util.ArrayList;
import java.util.List;

import static io.openepcis.constants.ApplicationIdentifierConstants.*;

public class GTINWeightAmountBestBeforeValidator implements ApplicationIdentifierValidator {

    private static final List<Matcher> DIGITAL_LINK_VALIDATION_RULES = new ArrayList<>();

    static {
        // GTIN + Net weight + Amount payable + Best before date Digital Link URI identifier validation rules

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

        // Validate for the Net Weight (3103) & Validate for the Expiry Date (17) & Amount(3922) in any order of query parameter
        DIGITAL_LINK_VALIDATION_RULES.add(
                new Matcher(
                        "(http|https)://.*./01/[0-9]{14}\\?(?=[^?]*\\b3103=\\d{6})(?=[^?]*\\b17=\\d{6})(?=[^?]*\\b3922=\\d{1,15})([^?]*)$",
                        "Invalid Identifier, must include Net Weight (3103), Expiry Date (17), and Amount (3922) in any order, with a valid query string structure. Example: https://id.gs1.org/01/09520123456788?3103=000195&3922=0299&17=201225. Please check the DL URI: %s"
                )
        );
    }

    @Override
    public boolean supportsValidation(final String identifier) {
        return identifier.contains(SGTIN_AI_URI_PREFIX) &&
                identifier.contains(EXPIRY_DATE_AI_PARAM) &&
                identifier.contains(NET_WEIGHT_AI_PARAM) &&
                identifier.contains(AMOUNT_PAYABLE_AI_PARAM);
    }

    @Override
    public boolean supportsValidation(final String identifier, final boolean isEpcisCompliant) {
        // if isEpcisCompliant is true then return false as the identifier is not EPCIS compliant
        if (Boolean.TRUE.equals(isEpcisCompliant)) {
            return false;
        }

        // else check if identifier contains DL URI part: "/01/", "?17=", "?3103=", "?3922="
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
