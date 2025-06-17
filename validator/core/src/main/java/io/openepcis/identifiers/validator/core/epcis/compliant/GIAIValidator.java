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

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.validator.ValidationContext;
import io.openepcis.identifiers.validator.core.ApplicationIdentifierValidator;
import io.openepcis.identifiers.validator.core.Matcher;

import java.util.ArrayList;
import java.util.List;

import static io.openepcis.constants.ApplicationIdentifierConstants.GIAI_AI_URI_PREFIX;
import static io.openepcis.constants.ApplicationIdentifierConstants.GIAI_AI_URN_PREFIX;

public class GIAIValidator implements ApplicationIdentifierValidator {

    private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URI_MATCHERS = new ArrayList<>();

    static {
        // GIAI EPC URN identifier validation rules
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:giai:.*",
                        "Invalid GIAI, GIAI should start with \"urn:epc:id:giai:\" (Ex: urn:epc:id:giai:1234567890.ABCDEF1234), Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:giai:[0-9]{6,12}.*",
                        "Invalid GIAI, GIAI should consist of GCP with 6-12 digits, Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:giai:[0-9]{6,12}\\.[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,24}",
                        "Invalid GIAI, GIAI should consist of GCP with 6-12 digits followed by alphanumeric serial up to 30 characters (Ex: urn:epc:id:giai:1234567890.ABCDEF1234). Please check the provided URN: %s"));

        // GIAI EPC URI identifier validation rules
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*",
                        "Invalid GIAI, GIAI should start with Domain name (Ex:https://id.gs1.org/8004/1234567890ABCD), Please check the URI: %s"));
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./8004/[0-9]{6,12}[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,24}$",
                        "Invalid GIAI, GIAI must be between 10 and 30 alphanumeric characters (Ex:https://id.gs1.org/8004/1234567890ABCD), Please check the URI: %s") {

                    @Override
                    public void validate(final String dlURI, final int gcpLength)
                            throws ValidationException {
                        super.validate(dlURI, gcpLength);

                        String giai = dlURI.substring(dlURI.indexOf(GIAI_AI_URI_PREFIX) + GIAI_AI_URI_PREFIX.length());

                        // Check if the GCP length matches
                        if (!(gcpLength >= 6 && gcpLength <= 12)) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid GCP Length, GCP Length must be between 6 and 12 digits. Please check the GCP Length: %s",
                                            gcpLength));
                        }

                        // Check if the GIAI length is valid
                        if (giai.length() < gcpLength) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid GIAI, GIAI length cannot be less than GCP length (Ex:https://id.gs1.org/8004/1234567890ABCD), Please check the URI: %s",
                                            dlURI));
                        }
                    }
                });
    }

    @Override
    public boolean supportsValidation(final String identifier) {
        // For URN identifier check if identifier contains the specific GIAI URN part
        if (identifier.contains(GIAI_AI_URN_PREFIX)) {
            return true;
        }

        // For URI identifier check if identifier contains specific GIAI URI part
        return identifier.contains(GIAI_AI_URI_PREFIX);
    }

    @Override
    public boolean supportsValidation(final String identifier, final boolean isEpcisCompliant) {
        // As its already EPCIS compliant, return the result of the supportsValidation method
        return supportsValidation(identifier);
    }

    @Override
    public boolean validate(final String identifier, final ValidationContext validationContext) throws ValidationException {
        // Determine identifier type directly from the provided identifier
        boolean isUrn = identifier.contains(GIAI_AI_URN_PREFIX);

        // Select the correct matcher list based on identifier type and level.
        List<Matcher> matchers;

        if (isUrn) {
            // Choose the appropriate URN matchers based on whether it's a class-level URN.
            matchers = URN_MATCHERS;
        } else {
            // For Digital Link URIs, ensure a valid GCP length is provided.
            if (validationContext.getGcpLength() == null) {
                throw new ValidationException(
                        "Digital Link URI detected. Use validate(String, int) to validate Digital Link URIs with a GCP length.");
            }
            matchers = URI_MATCHERS;
        }

        // Iterate over the chosen matchers and validate the identifier.
        for (Matcher m : matchers) {
            if (isUrn) {
                m.validate(identifier);
            } else {
                m.validate(identifier, validationContext.getGcpLength());
            }
        }

        return true;
    }
}
