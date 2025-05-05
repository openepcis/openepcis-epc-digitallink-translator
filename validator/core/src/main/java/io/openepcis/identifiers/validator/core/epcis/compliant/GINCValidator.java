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
import io.openepcis.identifiers.validator.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

import static io.openepcis.constants.ApplicationIdentifierConstants.GINC_AI_URI_PREFIX;
import static io.openepcis.constants.ApplicationIdentifierConstants.GINC_AI_URN_PREFIX;

public class GINCValidator implements ApplicationIdentifierValidator {

    private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URI_MATCHERS = new ArrayList<>();

    static {
        // GINC EPC URN identifier validation rules
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:ginc:.*",
                        "Invalid GINC, GINC should start with \"urn:epc:id:ginc:\" (Ex: urn:epc:id:ginc:1234567890.ABCDEF123456789), Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:ginc:[0-9]{6,12}.*",
                        "Invalid GINC, GINC should consist of GCP with 6-12 digits (Ex: urn:epc:id:ginc:1234567890.ABCDEF123456789), Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:ginc:[0-9]{6,12}\\.[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{0,24}",
                        "Invalid GINC, GINC should be between 7 and 30 characters with GCP 6-12 digits (Ex: urn:epc:id:ginc:1234567890.ABCDEF123456789), Please check the provided URN: %s") {

                    @Override
                    public void validate(final String urn) throws ValidationException {
                        super.validate(urn);

                        // GINC Length cannot be more than 30 characters and less than 7 characters
                        final String ginc = urn.substring(urn.indexOf(GINC_AI_URN_PREFIX) + GINC_AI_URN_PREFIX.length());

                        if (!(ginc.length() <= 31 && ginc.length() >= 7)) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid GINC, GINC should be between 7 and 30 characters (Ex: urn:epc:id:ginc:1234567890.ABCDEF123456789), Please check the provided URN: %s",
                                            urn));
                        }
                    }
                });

        // GINC EPC Digital Link URI identifier validation rules
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*",
                        "Invalid GINC, GINC should start with Domain name (Ex: https://id.gs1.org/401/123456789012100), Please check the URI: %s"));
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./401/[0-9]{6,12}[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,24}",
                        "Invalid GINC, GINC should be between 7 and 30 characters with GCP 6-12 digits (Ex: https://id.gs1.org/401/123456789012100), Please check the URI: %s") {

                    @Override
                    public void validate(final String uri, final int gcpLength)
                            throws ValidationException {
                        super.validate(uri, gcpLength);

                        // Check the provided GCP Length is between 6 and 12 digits
                        if (!(gcpLength >= 6 && gcpLength <= 12)) {
                            throw new ValidationException(
                                    String.format(
                                            "GCP Length should be between 6-12 digits. Please check the provided GCP Length: %s",
                                            gcpLength));
                        }

                        // Check the GINC Length is more than GCP Length
                        final String ginc = uri.substring(uri.indexOf(GINC_AI_URI_PREFIX) + GINC_AI_URI_PREFIX.length());

                        if (ginc.length() < gcpLength) {
                            throw new ValidationException(
                                    String.format(
                                            "GINC length should be more than GCP Length, Please check the provided URI: %s",
                                            uri));
                        }

                        // Check if the GCP contains only Digits
                        if (!(ginc.substring(0, gcpLength).matches("\\d*"))) {
                            throw new ValidationException(
                                    String.format(
                                            "GCP should contain only digits between 6-12. Please check the provided URI: %s",
                                            uri));
                        }
                    }
                });
    }

    @Override
    public boolean supportsValidation(final String identifier) {
        // For URN identifier check if identifier contains the specific :ginc: URN part
        if (identifier.contains(GINC_AI_URN_PREFIX)) {
            return true;
        }

        // For URI identifier check if identifier contains "/401/"
        return identifier.contains(GINC_AI_URI_PREFIX);
    }

    @Override
    public boolean supportsValidation(final String identifier, final boolean isEpcisCompliant) {
        // As its already EPCIS compliant, return the result of the supportsValidation method
        return supportsValidation(identifier);
    }

    @Override
    public boolean validate(final String identifier, final ValidationContext validationContext) throws ValidationException {
        // Determine identifier type directly from the provided identifier
        boolean isUrn = identifier.contains(GINC_AI_URN_PREFIX);

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
