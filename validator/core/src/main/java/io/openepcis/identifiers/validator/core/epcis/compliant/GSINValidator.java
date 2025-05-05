/*
 * Copyright (c) 2022-2025 benelog GmbH & Co. KG
 * All rights reserved.
 *
 * Unauthorized copying, modification, distribution,
 * or use of this work, via any medium, is strictly prohibited.
 *
 * benelog GmbH & Co. KG reserves all rights not expressly granted herein,
 * including the right to sell licenses for using this work.
 */
package io.openepcis.identifiers.validator.core.epcis.compliant;

import io.openepcis.identifiers.validator.ValidationContext;
import io.openepcis.identifiers.validator.core.ApplicationIdentifierValidator;
import io.openepcis.identifiers.validator.core.Matcher;
import io.openepcis.identifiers.validator.core.util.CheckDigitValidator;
import io.openepcis.identifiers.validator.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

import static io.openepcis.constants.ApplicationIdentifierConstants.GSIN_AI_URI_PREFIX;
import static io.openepcis.constants.ApplicationIdentifierConstants.GSIN_AI_URN_PREFIX;

public class GSINValidator implements ApplicationIdentifierValidator {

    private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URI_MATCHERS = new ArrayList<>();

    static {
        // GSIN Instance EPC URN identifier validation rules
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:gsin:.*",
                        "Invalid GSIN, GSIN should start with \"urn:epc:id:gsin:\" (Ex: urn:epc:id:gsin:123456.7890123456). Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:gsin:[0-9]{6,12}.*",
                        "Invalid GSIN, GSIN should consist of GCP with 6-12 digits (Ex: urn:epc:id:gsin:123456.7890123456). Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:gsin:[0-9]{6,12}\\.[0-9]{4,10}",
                        "Invalid GSIN, GSIN should consist of 17 digits with GCP 6-12 digits (Ex: urn:epc:id:gsin:123456.7890123456). Please check the provided URN: %s") {

                    @Override
                    public void validate(final String urn) throws ValidationException {
                        super.validate(urn);

                        final String gsin =
                                urn.substring(urn.indexOf(GSIN_AI_URN_PREFIX) + GSIN_AI_URN_PREFIX.length(), urn.indexOf("."))
                                        + urn.substring(urn.indexOf(".") + 1);

                        if (gsin.length() != 16 || gsin.matches(".*\\D.*")) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid GSIN, GSIN should consist of 17 digits (Ex: urn:epc:id:gsin:123456.7890123456). Please check the provided URN: %s", urn));
                        }
                    }
                });

        // GCN Instance EPC Digital Link URI identifier validation rules
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*",
                        "Invalid GSIN, GSIN should start with Domain name (Ex: https://id.gs1.org/402/12345607890123456). Please check the URI: %s"));
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./402/[0-9]{17}",
                        "Invalid GSIN, GSIN should consist of 17 digits (Ex: https://id.gs1.org/402/12345607890123456). Please check the URI: %s") {

                    @Override
                    public void validate(final String uri, final int gcpLength) throws ValidationException {
                        super.validate(uri);

                        // Check the provided GCP Length is between 6 and 12 digits
                        if (!(gcpLength >= 6 && gcpLength <= 12)) {
                            throw new ValidationException(String.format("GCP Length should be between 6-12 digits. Please check the provided GCP Length: %s", gcpLength));
                        }
                    }

                    @Override
                    public void validate(final String uri, final ValidationContext validationContext) throws ValidationException {
                        validate(uri, validationContext.getGcpLength());

                        if (!validationContext.isValidateCheckDigit()) {
                            return;
                        }

                        CheckDigitValidator.validateGSIN(uri);
                    }
                });
    }

    @Override
    public boolean supportsValidation(final String identifier) {
        // For URN identifier check if identifier contains the specific GSIN URN part: ":gsin:
        if (identifier.contains(GSIN_AI_URN_PREFIX)) {
            return true;
        }

        // For URI identifier check if identifier contains "/8010/" (with or without "/402/")
        return identifier.contains(GSIN_AI_URI_PREFIX);
    }

    @Override
    public boolean supportsValidation(final String identifier, final boolean isEpcisCompliant) {
        // As its already EPCIS compliant, return the result of the supportsValidation method
        return supportsValidation(identifier);
    }

    /**
     * Validate without gcpLength. This method is intended for URN validations.
     */
    @Override
    public boolean validate(final String identifier, final ValidationContext validationContext) throws ValidationException {
        // Determine identifier type directly from the provided identifier
        boolean isUrn = identifier.contains(GSIN_AI_URN_PREFIX);

        // Select the correct matcher list based on identifier type and level.
        List<Matcher> matchers;

        if (isUrn) {
            // Choose the appropriate URN matchers based on whether it's a class-level URN.
            matchers = URN_MATCHERS;
        } else {
            // For Digital Link URIs, ensure a valid GCP length is provided.
            if (validationContext.getGcpLength() == null) {
                throw new ValidationException("Digital Link URI detected. Use validate(String, int) to validate Digital Link URIs with a GCP length.");
            }
            matchers = URI_MATCHERS;
        }

        // Iterate over the chosen matchers and validate the identifier.
        for (Matcher m : matchers) {
            if (isUrn) {
                m.validate(identifier);
            } else {
                m.validate(identifier, validationContext);
            }
        }

        return true;
    }
}
