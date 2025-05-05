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

import static io.openepcis.constants.ApplicationIdentifierConstants.*;

public class GCNValidator implements ApplicationIdentifierValidator {

    private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URN_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URI_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();

    static {
        // GCN Instance EPC URN identifier validation rules
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:sgcn:.*",
                        "Invalid GCN, GCN should start with \"urn:epc:id:sgcn:\" (Ex: urn:epc:id:sgcn:123456.789012.4567890), Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:sgcn:[0-9]{6,12}.*",
                        "Invalid GCN, GCN should consist of GCP with 6-12 digits (Ex: urn:epc:id:sgcn:123456.789012.4567890), Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:sgcn:[0-9]{6,12}\\.[0-9]{0,7}.*",
                        "Invalid GCN, GCN should consist of 13 digits (Ex: urn:epc:id:sgcn:123456.789012.4567890), Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:sgcn:[0-9]{6,12}\\.[0-9]{0,7}\\.[0-9]{0,12}",
                        "Invalid GCN, GCN with Serial must be between 14 and 25 digits (Ex: urn:epc:id:sgcn:123456.789012.4567890), Please check the provided URN: %s") {

                    @Override
                    public void validate(String urn) throws ValidationException {
                        super.validate(urn);
                        final String sgcn = urn.substring(urn.lastIndexOf(":") + 1, urn.lastIndexOf("."));
                        if (sgcn.length() != 13) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid GCN, GCN should consist of 13 digits (Ex: urn:epc:id:sgcn:123456.789012.4567890), Please check the provided URN: %s", urn));
                        }
                    }
                });

        // GCN Instance EPC Digital Link URI identifier validation rules
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*",
                        "Invalid GCN, GCN should start with Domain name (Ex: https://id.gs1.org/255/12345678901284844274999), Please check the URI: %s"));
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./255/[0-9]{13}.*",
                        "Invalid GCN, GCN should consist of 13 digit (Ex: https://id.gs1.org/255/12345678901284844274999), Please check the URI: %s"));
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./255/[0-9]{13}[0-9]{0,12}",
                        "Invalid GCN, GCN with Serial must be between 14 and 25 digits (Ex: https://id.gs1.org/255/12345678901284844274999), Please check the URI: %s") {

                    @Override
                    public void validate(String dlURI, int gcpLength) throws ValidationException {
                        super.validate(dlURI);

                        String sgcn = dlURI.substring(dlURI.indexOf(GCN_AI_URI_PREFIX) + GCN_AI_URI_PREFIX.length());
                        sgcn = sgcn.substring(0, 13);

                        // Check if the SGCN Length is more than GCP Length
                        if (sgcn.length() < gcpLength) {
                            throw new ValidationException(String.format("Invalid GCN, GCN cannot be more than GCP length. Please check the provided URI: %s", dlURI));
                        }

                        // Check if the GCP Length is valid
                        if (!(gcpLength >= 6 && gcpLength <= 12)) {
                            throw new ValidationException(
                                    String.format("Invalid GCP Length, GCP Length should be between 6-12 digits. Please check the provided GCP Length: %s", gcpLength));
                        }
                    }

                    // Validate for Check Digit if the flag is set
                    @Override
                    public void validate(final String uri, final ValidationContext validationContext) throws ValidationException {
                        validate(uri, validationContext.getGcpLength());

                        if (!validationContext.isValidateCheckDigit()) {
                            return;
                        }

                        CheckDigitValidator.validateGCN(uri);
                    }
                });

        // GCN CLASS URN identifier validation rules
        URN_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "urn:epc:idpat:sgcn:.*",
                        "Invalid GCN, Class level GCN should start with \"urn:epc:idpat:sgcn:\" (Ex: urn:epc:idpat:sgcn:123456.789012.*), Please check the provided URN: %s"));
        URN_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "urn:epc:idpat:sgcn:[0-9]{6,12}.*",
                        "Invalid GCN, Class level GCN should consist of GCP with 6-12 digits (Ex: urn:epc:idpat:sgcn:123456.789012.*), Please check the provided URN: %s"));
        URN_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "urn:epc:idpat:sgcn:[0-9]{6,12}\\.[0-9]{0,7}\\.\\*",
                        "Invalid GCN, Class level GCN should consist of 13 digits (Ex: urn:epc:idpat:sgcn:123456.789012.*), Please check the provided URN: %s") {

                    @Override
                    public void validate(String urn) throws ValidationException {
                        super.validate(urn);
                        final String sgcn = urn.substring(urn.lastIndexOf(":") + 1, urn.lastIndexOf("."));
                        if (sgcn.length() != 13) {
                            throw new ValidationException(
                                    String.format("Invalid GCN, Class level GCN should consist of 13 digits (Ex: urn:epc:idpat:sgcn:123456.789012.*), Please check the provided URN: %s", urn));
                        }
                    }
                });

        // GCN Class Digital Link URI identifier validation rules
        URI_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*",
                        "Invalid GCN, Class level GCN should start with Domain name (Ex: https://id.gs1.org/255/9524321678904), Please check the URI: %s"));
        URI_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./255/[0-9]{13}",
                        "Invalid GCN, Class level GCN should consist of 13 digit (Ex: https://id.gs1.org/255/9524321678904), Please check the URI: %s") {

                    @Override
                    public void validate(String dlURI, int gcpLength) throws ValidationException {
                        super.validate(dlURI);
                        String sgcn = dlURI.substring(dlURI.indexOf(GCN_AI_URI_PREFIX) + GCN_AI_URI_PREFIX.length());
                        sgcn = sgcn.substring(0, 13);

                        // Check if the SGCN Length is more than GCP Length
                        if (sgcn.length() < gcpLength) {
                            throw new ValidationException(
                                    String.format("Invalid GCN, Class level GCN cannot be more than GCP length. Please check the provided URI: %s", dlURI));
                        }

                        // Check if the GCP Length is valid
                        if (!(gcpLength >= 6 && gcpLength <= 12)) {
                            throw new ValidationException(
                                    String.format("Invalid GCP Length, GCP Length should be between 6-12 digits. Please check the provided GCP Length: %s", gcpLength));
                        }
                    }

                    // Validate for Check Digit if the flag is set
                    @Override
                    public void validate(final String uri, final ValidationContext validationContext) throws ValidationException {
                        validate(uri, validationContext.getGcpLength());

                        if (!validationContext.isValidateCheckDigit()) {
                            return;
                        }

                        CheckDigitValidator.validateGCN(uri);
                    }
                });
    }

    @Override
    public boolean supportsValidation(final String identifier) {
        // For URN identifier check if identifier contains the specific GCN URN part
        if (identifier.contains(GCN_AI_URN_PREFIX)) {
            return true;
        }

        // For URI identifier check if identifier contains "/8010/" (with or without "/8011/")
        return identifier.contains(GCN_AI_URI_PREFIX);
    }

    @Override
    public boolean supportsValidation(final String identifier, final boolean isEpcisCompliant) {
        // As its already EPCIS compliant, return the result of the supportsValidation method
        return supportsValidation(identifier);
    }

    @Override
    public boolean validate(final String identifier, final ValidationContext validationContext) throws ValidationException {
        // Determine identifier type directly from the provided identifier
        boolean isUrn = identifier.contains(GCN_AI_URN_PREFIX);

        // For URNs, class-level is determined by checking for CLASS_URN_PART.
        // For Digital Link URIs, extract the numeric segment after "/255/" and if its length is exactly
        // 13, it's class-level.
        boolean isClassLevel;
        if (isUrn) {
            isClassLevel = identifier.contains(CLASS_URN_PREFIX);
        } else {
            final int idx = identifier.indexOf(GCN_AI_URI_PREFIX);

            // Extract the segment after "/255/" and check if it's exactly 13 characters long.
            final String value = identifier.substring(idx + GCN_AI_URI_PREFIX.length());

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
