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
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static io.openepcis.constants.ApplicationIdentifierConstants.*;

public class UPUIValidator implements ApplicationIdentifierValidator {

    private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URI_MATCHERS = new ArrayList<>();

    static {
        // UPUI Instance EPC URN identifier validation rules
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:upui:.*",
                        "Invalid UPUI, UPUI should start with \"urn:epc:id:upui:\" (Ex: urn:epc:id:upui:234567890123.1.1234ABCD5678EFGH). Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:upui:[0-9]{6,12}.*",
                        "Invalid UPUI, UPUI should consist of GCP with 6-12 digits (Ex: urn:epc:id:upui:234567890123.1.1234ABCD5678EFGH). Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:upui:[0-9]{6,12}\\.[0-9]{1,7}.*",
                        "Invalid UPUI, UPUI must be of 14 digits (Ex: urn:epc:id:upui:234567890123.1.1234ABCD5678EFGH). Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "urn:epc:id:upui:[0-9]{6,12}\\.[0-9]{1,7}\\.[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,28}",
                        "Invalid UPUI, UPUI should consist of TPX of 1 to 28 characters(Ex: urn:epc:id:upui:234567890123.1.1234ABCD5678EFGH). Please check the provided URN : %s") {

                    @Override
                    public void validate(final String urn) throws ValidationException {
                        super.validate(urn);

                        String upui =
                                urn.charAt(StringUtils.ordinalIndexOf(urn, ".", 1) + 1)
                                        + urn.substring(
                                        urn.indexOf(UPUI_AI_URN_PREFIX) + UPUI_AI_URN_PREFIX.length(),
                                        StringUtils.ordinalIndexOf(urn, ".", 1));
                        upui =
                                upui
                                        + urn.substring(
                                        StringUtils.ordinalIndexOf(urn, ".", 1) + 2,
                                        StringUtils.ordinalIndexOf(urn, ".", 2));

                        if (upui.length() != 13) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid UPUI, UPUI must be of 14 digits (Ex: urn:epc:id:upui:234567890123.1.1234ABCD5678EFGH),%n Please check the provided URN: %s",
                                            urn));
                        }
                    }
                });

        // UPUI Instance EPC Digital Link URI identifier validation rules
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*",
                        "Invalid UPUI, UPUI should start with Domain name (Ex: https://id.gs1.org/01/12345678901231/235/9999). Please check the URI: %s"));
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./01/[0-9]{14}.*",
                        "Invalid UPUI, UPUI must consist of 14 digits (Ex: https://id.gs1.org/01/12345678901231/235/9999). Please check the URI: %s"));
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*./01/[0-9]{14}/235/[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,28}",
                        "Invalid UPUI, UPUI must consist of TPX 1 to 28 characters (Ex: https://id.gs1.org/01/12345678901231/235/9999). Please check the URI: %s") {
                    @Override
                    public void validate(final String uri, final int gcpLength)
                            throws ValidationException {
                        super.validate(uri, gcpLength);

                        // Check the provided GCP Length is between 6 and 12 digits
                        if (!(gcpLength >= 6 && gcpLength <= 12)) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid GCP Length, GCP Length should be between 6-12 digits. Please check the provided GCP Length: %s",
                                            gcpLength));
                        }
                    }
                });
    }

    @Override
    public boolean supportsValidation(final String identifier) {
        // For URN identifier check if identifier contains URN part: ":upui:"
        if (identifier.contains(UPUI_AI_URN_PREFIX)) {
            return true;
        }

        // For DL URI identifier check if identifier contains DL URI part: "/01/" and "/235/"
        return identifier.contains(UPUI_AI_URI_PREFIX) && identifier.contains(UPUI_AI_URI_SERIAL_PREFIX);
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
        boolean isUrn = identifier.contains(UPUI_AI_URN_PREFIX);

        // Select the correct matcher list.
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
