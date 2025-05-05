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
import java.util.regex.Pattern;

import static io.openepcis.constants.ApplicationIdentifierConstants.*;

public class CPIValidator implements ApplicationIdentifierValidator {

    private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URN_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URI_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();

    public CPIValidator() {
        super();
    }

    static {
        // CPI EPC URN identifier validation rules
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:cpi:.*",
                        "Invalid CPI, CPI should start with \"urn:epc:id:cpi:\" (Ex: urn:epc:id:cpi:123456789.0123459.1234). Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:cpi:[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{6,12}.*",
                        "Invalid CPI, CPI should consist of GCP with 6-12 digits (Ex: urn:epc:id:cpi:123456789.0123459.1234). Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:cpi:[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{6,12}\\.[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{1,24}.*",
                        "Invalid CPI, CPI must be between 7 and 30 digits with GCP 6-12 digits (Ex: urn:epc:id:cpi:123456789.0123459.1234). Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:cpi:[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{6,12}\\.[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{1,24}\\.[0-9]{1,12}",
                        "Invalid CPI, CPI must be between 7 and 30 digits followed by Serial with 1-12 digits (Ex: urn:epc:id:cpi:123456789.0123459.1234). Please check the provided URN: %s") {

                    @Override
                    public void validate(final String urn) throws ValidationException {
                        super.validate(urn);
                        final String cpi = urn.substring(urn.lastIndexOf(":"), urn.lastIndexOf("."));
                        if (cpi.length() < 7 || cpi.length() > 31) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid CPI, CPI must be between 7 and 30 digits (Ex: urn:epc:id:cpi:123456789.0123459.1234). Please check the provided URN: %s",
                                            urn));
                        }

                        // Check if GCP contains any characters apart from decimal
                        final String gcp = urn.substring(urn.lastIndexOf(":") + 1, urn.indexOf("."));
                        if (!Pattern.matches("\\d+", gcp)) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid CPI, CPI should consist of GCP with 6-12 digits, Please check the provided URN: %s",
                                            urn));
                        }
                    }
                });

        // CPI CLASS URN identifiers validation rules
        URN_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "urn:epc:idpat:cpi:.*",
                        "Invalid CPI, Class level CPI should start with \"urn:epc:idpat:cpi:\" (Ex: urn:epc:idpat:cpi:123456789.0123459.*). Please check the provided URN: %s"));
        URN_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "urn:epc:idpat:cpi:[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{6,12}.*",
                        "Invalid CPI, Class level CPI should consist of GCP with 6-12 digits (Ex: urn:epc:idpat:cpi:123456789.0123459.*). Please check the provided URN: %s"));
        URN_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "urn:epc:idpat:cpi:[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{6,12}\\.[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{0,24}\\.\\*",
                        "Invalid CPI, Class level CPI must be between 7 and 30 digits with GCP 6-12 digits (Ex: urn:epc:idpat:cpi:123456789.0123459.*). Please check the provided URN: %s") {

                    @Override
                    public void validate(final String urn) throws ValidationException {
                        super.validate(urn);
                        String cpi = urn.substring(urn.lastIndexOf(":"), urn.lastIndexOf("."));
                        if (cpi.length() < 7 || cpi.length() > 31) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid CPI, CPI must be between 7 and 30 digits (Ex: urn:epc:idpat:cpi:123456789.0123459.*). Please check the provided URN: %s",
                                            urn));
                        }
                    }
                });

        // CPI EPC URI identifier validation rules
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*",
                        "Invalid CPI, CPI must begin with the Domain name (Ex: https://id.gs1.org/). Please check the URI: %s"));
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./8010/[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{7,30}.*",
                        "Invalid CPI, CPI must be between 7 and 30 digits (Ex: https://id.gs1.org/8010/1234567890123459/8011/1234). Please check the URI: %s") {

                    @Override
                    public void validate(final String urn, final int gcpLength) throws ValidationException {
                        super.validate(urn);

                        final String cpi =
                                urn.substring(
                                        urn.indexOf(CPI_AI_URI_PREFIX) + CPI_AI_URI_PREFIX.length(),
                                        urn.indexOf(CPI_AI_URI_SERIAL_PREFIX));

                        // Check if the GCP Length is valid
                        if (!(gcpLength >= 6 && gcpLength <= 12)) {
                            throw new ValidationException(
                                    String.format("GCP Length should be between 6-12 digits. Please check the provided URN : %s", urn));
                        }

                        // Check if CPI Length is more than GCP Length
                        if (gcpLength > cpi.length()) {
                            throw new ValidationException(
                                    String.format("GCP Length cannot be more than the CPI length. Please check the provided URI : %s", urn));
                        }

                        // Check if GCP contains any characters apart from decimal
                        final String cpiSubString = cpi.substring(0, gcpLength);
                        if (!Pattern.matches("^\\d*$", cpiSubString)) {
                            throw new ValidationException(
                                    String.format("Invalid CPI, CPI should consist of GCP with 6-12 digits, Please check the provided URI: %s", urn));
                        }
                    }
                });
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./8010/[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{7,30}/8011/[0-9]{1,12}",
                        "Invalid CPI, CPI must be 7-30 digits followed by serial of 1 to 12 digits (Ex: https://id.gs1.org/8010/1234567890123459/8011/1234). Please check the URI: %s"));

        // CPI Class URI identifier validation rules
        URI_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*",
                        "Invalid CPI, Class level CPI must begin with the Domain name (Ex: https://id.gs1.org/). Please check the URI: %s"));
        URI_WITHOUT_SERIAL_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./8010/[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{7,30}",
                        "Invalid CPI, Class level CPI must be between 7 and 30 digits (Ex: https://id.gs1.org/8010/1234567890123459). Please check the URI: %s") {

                    @Override
                    public void validate(final String uri, final int gcpLength) throws ValidationException {
                        super.validate(uri);
                        final String cpi = uri.substring(uri.indexOf(CPI_AI_URI_PREFIX) + CPI_AI_URI_PREFIX.length());

                        // Check if the GCP Length is valid
                        if (!(gcpLength >= 6 && gcpLength <= 12)) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid GCP Length, GCP Length should be between 6-12 digits. Please check the provided GCP Length: %s",
                                            gcpLength));
                        }

                        // Check if CPI Length is more than GCP Length
                        if (gcpLength > cpi.length()) {
                            throw new ValidationException(
                                    String.format("GCP Length cannot be more than the CPI length. Please check the provided URI : %s", uri));
                        }
                    }
                });
    }

    @Override
    public boolean supportsValidation(final String identifier) {
        // For URN identifier check if identifier contains URN part: ":cpi:"
        if (identifier.contains(CPI_AI_URN_PREFIX)) {
            return true;
        }

        // For DL URI identifier check if identifier contains DL URI part: "/8010/"
        return identifier.contains(CPI_AI_URI_PREFIX);
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
        boolean isUrn = identifier.contains(CPI_AI_URN_PREFIX);

        // For URNs, use CLASS_URN_PART; for URIs, if it has CPI_URI_PART without CPI_URI_SERIAL_PART,
        // it's non‑class-level.
        boolean isClassLevel =
                isUrn
                        ? identifier.contains(CLASS_URN_PREFIX)
                        : (identifier.contains(CPI_AI_URI_PREFIX) && !identifier.contains(CPI_AI_URI_SERIAL_PREFIX));

        // Select the correct matcher list.
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
                m.validate(identifier, validationContext.getGcpLength());
            }
        }

        return true;
    }
}
