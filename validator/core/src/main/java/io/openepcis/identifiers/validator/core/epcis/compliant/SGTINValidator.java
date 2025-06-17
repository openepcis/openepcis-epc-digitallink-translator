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
import io.openepcis.identifiers.validator.core.util.CheckDigitValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static io.openepcis.constants.ApplicationIdentifierConstants.*;

public class SGTINValidator implements ApplicationIdentifierValidator {

    private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URN_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URI_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();

    static {
        // SGTIN Instance EPC URN identifier validation rules
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:sgtin:.*",
                        "Invalid SGTIN, SGTIN should start with \"urn:epc:id:sgtin:\" (Ex: urn:epc:id:sgtin:234567890.1123.9999), Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:sgtin:[0-9]{6,12}.*",
                        "Invalid SGTIN, SGTIN should consist of GCP with 6-12 digits (Ex: urn:epc:id:sgtin:234567890.1123.9999). Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:sgtin:[0-9]{6,12}\\.[0-9]{1,7}.*",
                        "Invalid SGTIN, SGTIN should be of 14 digits with GCP of 6-12 digits (Ex: urn:epc:id:sgtin:234567890.1123.9999). Please check the provided URN: %s") {
                    @Override
                    public void validate(final String urn) throws ValidationException {
                        super.validate(urn);

                        // Check if the SGTIN has 14 digits
                        final String gcp =
                                urn.substring(
                                        urn.indexOf(SGTIN_AI_URN_PREFIX) + SGTIN_AI_URN_PREFIX.length(),
                                        StringUtils.ordinalIndexOf(urn, ".", 1));
                        String sgtin;

                        if (StringUtils.countMatches(urn, ".") >= 2) {
                            sgtin =
                                    gcp + urn.substring(StringUtils.ordinalIndexOf(urn, ".", 1) + 1,
                                            StringUtils.ordinalIndexOf(urn, ".", 2));
                        } else {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid SGTIN, SGTIN should be followed by serial numbers (Ex: urn:epc:id:sgtin:234567890.1123.9999). Please check the provided URN: %s",
                                            urn));
                        }

                        if (sgtin.length() != 13) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid SGTIN, SGTIN values should be of 14 digits (Ex: urn:epc:id:sgtin:234567890.1123.9999). Please check the provided URN: %s",
                                            urn));
                        }
                    }
                });
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:sgtin:[0-9]{6,12}\\.[0-9]{1,7}\\.[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,20}",
                        "Invalid SGTIN, SGTIN should consist of serial numbers(Ex: urn:epc:id:sgtin:234567.1890123.0000). Please check the provided URN: %s"));

        // SGTIN Class URN identifier validation rules
        URN_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "urn:epc:idpat:sgtin:.*",
                        "Invalid GTIN, Class level GTIN should start with \"urn:epc:id:sgtin:\" (Ex: urn:epc:idpat:sgtin:234567890.1123.*), Please check the provided URN: %s"));
        URN_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "urn:epc:idpat:sgtin:[0-9]{6,12}.*",
                        "Invalid GTIN, Class level GTIN should consist of GCP with 6-12 digits (Ex: urn:epc:idpat:sgtin:234567890.1123.*). Please check the provided URN: %s"));
        URN_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "urn:epc:idpat:sgtin:[0-9]{6,12}\\.[0-9]{1,7}\\.\\*",
                        "Invalid GTIN, Class level GTIN should be of 14 digits with GCP of 6-12 digits (Ex: urn:epc:idpat:sgtin:234567890.1123.*). Please check the provided URN: %s") {
                    @Override
                    public void validate(final String urn) throws ValidationException {
                        super.validate(urn);

                        // Check if the SGTIN has 14 digits
                        final String gcp = urn.substring(
                                urn.indexOf(SGTIN_AI_URN_PREFIX) + SGTIN_AI_URN_PREFIX.length(),
                                StringUtils.ordinalIndexOf(urn, ".", 1));
                        String sgtin;

                        if (StringUtils.countMatches(urn, ".") >= 2) {
                            sgtin = gcp + urn.substring(
                                    StringUtils.ordinalIndexOf(urn, ".", 1) + 1,
                                    StringUtils.ordinalIndexOf(urn, ".", 2));
                        } else {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid SGTIN, SGTIN should be followed by serial numbers (Ex: urn:epc:id:sgtin:234567890.1123.9999). Please check the provided URN: %s",
                                            urn));
                        }

                        if (sgtin.length() != 13) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid SGTIN, SGTIN values should be of 14 digits (Ex: urn:epc:id:sgtin:234567890.1123.9999). Please check the provided URN: %s",
                                            urn));
                        }
                    }
                });

        // SGTIN Instance EPC Digital Link URI identifier validation rules
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*",
                        "Invalid GTIN, DL URI should start with Domain name (Ex: https://id.gs1.org/), Please check the DL URI : %s"));
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./01/[0-9]{14}.*",
                        "Invalid GTIN, DL URI should consist of 14 digit SGTIN (Ex: https://id.gs1.org/01/12345678901234/21/9999), Please check the DL URI : %s"));
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./01/[0-9]{14}/21/[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,20}",
                        "Invalid SGTIN, DL URI should consist of 14 digit GTIN followed by Serial numbers (Ex: https://id.gs1.org/01/12345678901234/21/9999), Please check the DL URI : %s") {
                    @Override
                    public void validate(String uri, int gcpLength) throws ValidationException {
                        super.validate(uri);

                        // Check if the GCP Length matches
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

        // SGTIN Class Digital Link URI identifier validation rules
        URI_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*",
                        "Invalid GTIN, DL URI should start with Domain name (Ex: https://id.gs1.org/), Please check the DL URI : %s"));
        URI_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./01/[0-9]{14}.*",
                        "Invalid GTIN, DL URI should consist of 14 digits (Ex: https://id.gs1.org/01/12345678901234), Please check the DL URI : %s") {
                    @Override
                    public void validate(final String uri, final int gcpLength) throws ValidationException {
                        super.validate(uri);

                        // Check if the GTIN is of 14 digits
                        if (uri.substring(uri.indexOf(SGTIN_AI_URI_PREFIX) + SGTIN_AI_URI_PREFIX.length()).length() != 14) {
                            throw new ValidationException(
                                    String.format("Invalid GTIN, DL URI should consist of 14 digits (Ex: https://id.gs1.org/01/12345678901234).  Please check the provided URI : %s", uri));
                        }

                        // Check if the GCP Length matches
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
    }

    @Override
    public boolean supportsValidation(final String identifier) {
        // For URN identifier check if identifier contains URN part: ":sgtin:"
        if (identifier.contains(SGTIN_AI_URN_PREFIX)) {
            return true;
        }

        // For DL URI identifier check if identifier contains DL URI part: "/01/" and not related to LGTIN or Expiry data
        return identifier.contains(SGTIN_AI_URI_PREFIX) &&
                !(identifier.contains(LGTIN_AI_BATCH_LOT_PREFIX) || identifier.contains(EXPIRY_DATE_AI_PARAM));
    }

    @Override
    public boolean supportsValidation(final String identifier, final boolean isEpcisCompliant) {
        // As its already EPCIS compliant, return the result of the supportsValidation method
        return supportsValidation(identifier);
    }

    @Override
    public boolean validate(final String identifier, final ValidationContext validationContext) throws ValidationException {
        // Determine identifier type directly from the provided identifier
        boolean isUrn = identifier.contains(SGTIN_AI_URN_PREFIX);

        // For URNs, class-level is determined by checking for CLASS_URN_PART.
        // For Digital Link URIs, extract the numeric segment after "/01/" and if its length is exactly
        // 14, it's class-level.
        boolean isClassLevel;
        if (isUrn) {
            isClassLevel = identifier.contains(CLASS_URN_PREFIX);
        } else {
            final int idx = identifier.indexOf(SGTIN_AI_URI_PREFIX);

            // Extract the segment after "/01/" and check if it's exactly 14 characters long.
            final String value = identifier.substring(idx + SGTIN_AI_URI_PREFIX.length());

            // If the extracted numeric part is exactly 14 digits, it is class-level.
            isClassLevel = (value.length() == 14);
        }

        // Select the correct matcher list based on identifier type and level.
        List<Matcher> matchers;

        if (isUrn) {
            // Choose the appropriate URN matchers based on whether it's a class-level URN.
            matchers = isClassLevel ? URN_WITHOUT_SERIAL_MATCHERS : URN_MATCHERS;
        } else {
            // For Digital Link URIs, ensure a valid GCP length is provided.
            if (validationContext.getGcpLength() == null) {
                throw new ValidationException("Digital Link URI detected. Use validate(String, int) to validate Digital Link URIs with a GCP length.");
            }
            matchers = isClassLevel ? URI_WITHOUT_SERIAL_MATCHERS : URI_MATCHERS;
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
