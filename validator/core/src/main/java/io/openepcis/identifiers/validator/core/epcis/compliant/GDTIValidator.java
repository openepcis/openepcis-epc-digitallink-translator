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

import java.util.ArrayList;
import java.util.List;

import static io.openepcis.constants.ApplicationIdentifierConstants.*;

public class GDTIValidator implements ApplicationIdentifierValidator {

    private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URN_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URI_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
    private static final String GCP_ERROR_MESSAGE = "GCP Length must be between 6 and 12, Please check the provided URI : %s";
    private static final String CPI_ERROR_MESSAGE = "GCP Length cannot be more than the CPI length, Please check the provided URI : %s";
    private static final String URI_PREFIX = "(http|https)://.*";

    static {
        // GDTI EPC URN identifier validation rules
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:gdti:.*",
                        "Invalid GDTI, GDTI should start with \"urn:epc:id:gdti:\" (Ex: urn:epc:id:gdti:123456.789012.ABC123). Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:gdti:[0-9]{6,12}.*",
                        "Invalid GDTI, GDTI should consist of GCP with 6-12 digits (Ex: urn:epc:id:gdti:123456.789012.ABC123). Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:gdti:[0-9]{6,12}\\.[0-9]{0,6}.*",
                        "Invalid GDTI, GDTI should be of 13 digits (Ex: urn:epc:id:gdti:123456.789012.ABC123). Please check the provided URN: %s") {

                    @Override
                    public void validate(final String urn) throws ValidationException {
                        super.validate(urn);
                        String gdti;
                        if (urn.indexOf(".", urn.indexOf(".") + 1) == -1) {
                            gdti = urn.substring(urn.indexOf(GDTI_AI_URN_PREFIX) + GDTI_AI_URN_PREFIX.length());
                        } else {
                            gdti = urn.substring(urn.indexOf(GDTI_AI_URN_PREFIX) + GDTI_AI_URN_PREFIX.length(), urn.lastIndexOf("."));
                        }

                        // GDTI length should be 13 digits
                        if (gdti.length() != 13) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid GDTI, GDTI should be of 13 digits (Ex: urn:epc:id:gdti:123456.789012.ABC123). Please check the provided URN: %s",
                                            urn));
                        }
                    }
                });
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:gdti:[0-9]{6,12}\\.[0-9]{0,6}\\.[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,17}$",
                        "Invalid GDTI, GDTI with Serial must be 13 digits followed by 1 to 17 alphanumeric characters (Ex: urn:epc:id:gdti:123456.789012.ABC123). Please check the provided URN: %s"));

        // GDTI Class URN identifier validation rules
        URN_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "urn:epc:idpat:gdti:.*",
                        "Invalid GDTI, Class level GDTI should start with \"urn:epc:idpat:gdti:\" (Ex: urn:epc:id:gdti:123456.789012.*). Please check the provided URN: %s"));
        URN_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "urn:epc:idpat:gdti:[0-9]{6,12}.*",
                        "Invalid GDTI, Class level GDTI should consist of GCP with 6-12 digits (Ex: urn:epc:id:gdti:123456.789012.*). Please check the provided URN: %s"));
        URN_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "urn:epc:idpat:gdti:[0-9]{6,12}\\.[0-9]{0,6}\\.\\*",
                        "Invalid GDTI, Class level GDTI should be of 13 digits (Ex: urn:epc:idpat:gdti:123456.789012.*). Please check the provided URN: %s") {

                    @Override
                    public void validate(final String urn) throws ValidationException {
                        super.validate(urn);
                        String gdti;
                        if (urn.indexOf(".", urn.indexOf(".") + 1) == -1) {
                            gdti = urn.substring(urn.lastIndexOf(":") + 1);
                        } else {
                            gdti = urn.substring(urn.lastIndexOf(":") + 1, urn.lastIndexOf("."));
                        }

                        // GDTI length should be 13 digits
                        if (gdti.length() != 13) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid GDTI, Class level GDTI should be of 13 digits (Ex: urn:epc:idpat:gdti:123456.789012.*). Please check the provided URN: %s",
                                            urn));
                        }
                    }
                });

        // GDTI EPC URI identifier validation rules
        URI_MATCHERS.add(
                new Matcher(
                        URI_PREFIX,
                        "Invalid GDTI, GDTI must begin with the Domain name (Ex: https://id.gs1.org/), Please check the URI: %s"));
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./253/[0-9]{13}.*",
                        "Invalid GDTI, GDTI must be 13 digits (Ex: https://id.gs1.org/253/1234567890128ABC123), Please check the URI: %s"));
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./253/[0-9]{13}[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,17}",
                        "Invalid GDTI, GDTI must be 13 digits followed by 1 to 17 alphanumeric characters (Ex: https://id.gs1.org/253/1234567890128ABC123), Please check the URI: %s") {

                    @Override
                    public void validate(final String uri, final int gcpLength) throws ValidationException {
                        super.validate(uri);

                        String gdti = uri.substring(
                                        uri.indexOf(GDTI_AI_URI_PREFIX) + GDTI_AI_URI_PREFIX.length(),
                                        uri.indexOf(GDTI_AI_URI_PREFIX) + GDTI_AI_URI_PREFIX.length() + 13);

                        // Check if the GCPLength is valid
                        if (!(gcpLength >= 6 && gcpLength <= 12)) {
                            throw new ValidationException(String.format(GCP_ERROR_MESSAGE, gcpLength));
                        }

                        // Check if the GDTI length more than GCP Length
                        if (gdti.length() < gcpLength) {
                            throw new ValidationException(String.format(CPI_ERROR_MESSAGE, uri));
                        }
                    }

                    // Validate for Check Digit if the flag is set
                    @Override
                    public void validate(final String uri, final ValidationContext validationContext) throws ValidationException {
                        validate(uri, validationContext.getGcpLength());

                        if (!validationContext.isValidateCheckDigit()) {
                            return;
                        }

                        CheckDigitValidator.validateGDTI(uri);
                    }
                });

        // GDTI Class URI identifier validation rules
        URI_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        URI_PREFIX,
                        "Invalid GDTI, Class level GDTI must begin with the Domain name (Ex: https://id.gs1.org/), Please check the URI: %s"));
        URI_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./253/[0-9]{13}",
                        "Invalid GDTI, Class level GDTI must be 13 digits (Ex: https://id.gs1.org/253/9524321400017), Please check the URI: %s") {

                    @Override
                    public void validate(final String uri, final int gcpLength) throws ValidationException {
                        super.validate(uri);

                        String gdti = uri.substring(
                                        uri.indexOf(GDTI_AI_URI_PREFIX) + GDTI_AI_URI_PREFIX.length(),
                                        uri.indexOf(GDTI_AI_URI_PREFIX) + GDTI_AI_URI_PREFIX.length() + 13);

                        // Check if the GCPLength is valid
                        if (!(gcpLength >= 6 && gcpLength <= 12)) {
                            throw new ValidationException(String.format(GCP_ERROR_MESSAGE, gcpLength));
                        }

                        // Check if the GDTI length more than GCP Length
                        if (gdti.length() < gcpLength) {
                            throw new ValidationException(String.format(CPI_ERROR_MESSAGE, uri));
                        }
                    }

                    // Validate for Check Digit if the flag is set
                    @Override
                    public void validate(final String uri, final ValidationContext validationContext) throws ValidationException {
                        validate(uri, validationContext.getGcpLength());

                        if (!validationContext.isValidateCheckDigit()) {
                            return;
                        }

                        CheckDigitValidator.validateGDTI(uri);
                    }
                });
    }

    @Override
    public boolean supportsValidation(final String identifier) {
        // For URN identifier check if identifier contains the specific GDTI URN part
        if (identifier.contains(GDTI_AI_URN_PREFIX)) {
            return true;
        }

        // For URI identifier check if identifier contains specific GDTI URI part
        return identifier.contains(GDTI_AI_URI_PREFIX);
    }

    @Override
    public boolean supportsValidation(final String identifier, final boolean isEpcisCompliant) {
        // As its already EPCIS compliant, return the result of the supportsValidation method
        return supportsValidation(identifier);
    }

    @Override
    public boolean validate(final String identifier, final ValidationContext validationContext) throws ValidationException {
        // Determine identifier type directly from the provided identifier
        boolean isUrn = identifier.contains(GDTI_AI_URN_PREFIX);

        // For URNs, class-level is determined by checking for GDTI.
        // For Digital Link URIs, extract the numeric segment after "/253/" and if its length is exactly
        // 13, it's class-level.
        boolean isClassLevel;
        if (isUrn) {
            isClassLevel = identifier.contains(CLASS_URN_PREFIX);
        } else {
            final int idx = identifier.indexOf(GDTI_AI_URI_PREFIX);

            // Extract the segment after "/253/" and check if it's exactly 13 characters long.
            final String value = identifier.substring(idx + GDTI_AI_URI_PREFIX.length());

            // If the extracted numeric part is exactly 13 digits, it is class-level.
            isClassLevel = (value.length() == 13);
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
