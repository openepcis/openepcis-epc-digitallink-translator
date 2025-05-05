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
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static io.openepcis.constants.ApplicationIdentifierConstants.*;

public class ITIPValidator implements ApplicationIdentifierValidator {

    private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URN_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URI_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();

    static {
        // ITIP EPC URN identifier validation rules
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:itip:.*",
                        "Invalid ITIP, ITIP should start with \"urn:epc:id:itip:\" (Ex: urn:epc:id:itip:23456789.10123.56.78.0000). Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:itip:[0-9]{6,12}.*",
                        "Invalid ITIP, ITIP should consist of GCP with 6-12 digits (Ex: urn:epc:id:itip:23456789.10123.56.78.0000). Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:itip:[0-9]{6,12}\\.[0-9]{1,7}\\.[0-9]{2}\\.[0-9]{2}.*",
                        "Invalid ITIP, ITIP should consist of 18 digits  (Ex: urn:epc:id:itip:23456789.10123.56.78.0000). Please check the provided URN: %s") {

                    @Override
                    public void validate(final String urn) throws ValidationException {
                        super.validate(urn);
                        String itip;

                        if (StringUtils.countMatches(urn, ".") >= 4) {
                            itip =
                                    urn.substring(
                                            urn.indexOf(ITIP_AI_URN_PREFIX) + ITIP_AI_URN_PREFIX.length(),
                                            StringUtils.ordinalIndexOf(urn, ".", 4));
                        } else {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid ITIP, ITIP should consist of Serial Numbers (Ex: urn:epc:id:itip:23456789.10123.56.78.0000). Please check the provided URN: %s",
                                            urn));
                        }

                        if (itip.length() != 20) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid ITIP, ITIP should consist of 18 digits (Ex: urn:epc:id:itip:23456789.10123.56.78.0000). Please check the provided URN: %s",
                                            urn));
                        }
                    }
                });
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:itip:[0-9]{6,12}\\.[0-9]{1,7}\\.[0-9]{2}\\.[0-9]{2}\\.[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,20}",
                        "Invalid ITIP, ITIP should consist Serial numbers 1 to 20 characters (Ex: urn:epc:id:itip:23456789.10123.56.78.0000), Please check the provided URN : %s"));

        // ITIP EPC Digital Link URI identifier validation rules
        URN_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "urn:epc:idpat:itip:.*",
                        "Invalid ITIP, Class level ITIP should start with \"urn:epc:id:itip:\" (Ex: urn:epc:idpat:itip:23456789.10123.56.78.*). Please check the provided URN: %s"));
        URN_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "urn:epc:idpat:itip:[0-9]{6,12}.*",
                        "Invalid ITIP, Class level ITIP should consist of GCP with 6-12 digits (Ex: urn:epc:idpat:itip:23456789.10123.56.78.*). Please check the provided URN: %s"));
        URN_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "urn:epc:idpat:itip:[0-9]{6,12}\\.[0-9]{1,7}\\.[0-9]{2}\\.[0-9]{2}\\.\\*",
                        "Invalid ITIP, Class level ITIP should consist of 18 digits  (Ex: urn:epc:idpat:itip:23456789.10123.56.78.*). Please check the provided URN: %s") {

                    @Override
                    public void validate(final String urn) throws ValidationException {
                        super.validate(urn);
                        final String itip = urn.substring(urn.lastIndexOf(":") + 1, urn.lastIndexOf("."));

                        if (itip.length() != 20) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid ITIP, Class level ITIP should consist of 18 digits (Ex: urn:epc:idpat:itip:23456789.10123.56.78.*). Please check the provided URN: %s",
                                            urn));
                        }
                    }
                });

        // ITIP Class URN identifier validation rules
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*",
                        "Invalid ITIP, ITIP should start with Domain name (Ex: https://id.gs1.org/8006/123456789012356756/21/100), Please check the URI: %s"));
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./8006/[0-9]{18}.*",
                        "Invalid ITIP, ITIP must consist of 18 digits (Ex: https://id.gs1.org/8006/123456789012356756/21/100), Please check the URI: %s"));
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./8006/[0-9]{18}/21/[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,20}",
                        "Invalid ITIP, ITIP must consist of Serial numbers 1 to 20 characters (Ex: https://id.gs1.org/8006/123456789012356756/21/100), Please check the URI: %s") {
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
                    }
                });

        // ITIP Class Digital Link URI identifier validation rules
        URI_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*",
                        "Invalid ITIP, Class level ITIP should start with Domain name (Ex: https://id.gs1.org/8006/123456789012356756), Please check the URI: %s"));
        URI_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./8006/[0-9]{18}",
                        "Invalid ITIP, Class level ITIP must consist of 18 digits (Ex: https://id.gs1.org/8006/123456789012356756), Please check the URI: %s") {
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
                    }
                });
    }

    @Override
    public boolean supportsValidation(final String identifier) {
        // For URN identifier check if identifier contains the specific :itip: URN part
        if (identifier.contains(ITIP_AI_URN_PREFIX)) {
            return true;
        }

        // For URI identifier check if identifier contains "/8006/"
        return identifier.contains(ITIP_AI_URI_PREFIX);
    }

    @Override
    public boolean supportsValidation(final String identifier, final boolean isEpcisCompliant) {
        // As its already EPCIS compliant, return the result of the supportsValidation method
        return supportsValidation(identifier);
    }

    @Override
    public boolean validate(final String identifier, final ValidationContext validationContext)
            throws ValidationException {
        // Determine identifier type directly from the provided identifier
        boolean isUrn = identifier.contains(ITIP_AI_URN_PREFIX);

        // For URNs, class-level is determined by checking for CLASS_URN_PART.
        // For Digital Link URIs, extract the numeric segment after "/8006/" and if its length is
        // exactly 13, it's class-level.
        boolean isClassLevel;
        if (isUrn) {
            isClassLevel = identifier.contains(CLASS_URN_PREFIX);
        } else {
            final int idx = identifier.indexOf(ITIP_AI_URI_PREFIX);

            // Extract the segment after "/8006/" and check if it's exactly 18 characters long.
            final String value = identifier.substring(idx + ITIP_AI_URI_PREFIX.length());

            // If the extracted numeric part is exactly 18 digits, it is class-level.
            isClassLevel = (value.length() == 18);
        }

        // Select the correct matcher list based on identifier type and level.
        List<Matcher> matchers;

        if (isUrn) {
            // Choose the appropriate URN matchers based on whether it's a class-level URN.
            matchers = isClassLevel ? URN_WITHOUT_SERIAL_MATCHERS : URN_MATCHERS;
        } else {
            // For Digital Link URIs, ensure a valid GCP length is provided.
            if (validationContext.getGcpLength() == null) {
                throw new ValidationException(
                        "Digital Link URI detected. Use validate(String, int) to validate Digital Link URIs with a GCP length.");
            }

            matchers = isClassLevel ? URI_WITHOUT_SERIAL_MATCHERS : URI_MATCHERS;
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
