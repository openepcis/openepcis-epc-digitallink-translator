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

import static io.openepcis.constants.ApplicationIdentifierConstants.PGLN_AI_URI_PREFIX;
import static io.openepcis.constants.ApplicationIdentifierConstants.PGLN_AI_URN_PREFIX;

public class PGLNValidator implements ApplicationIdentifierValidator {

    private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
    private static final List<Matcher> URI_MATCHERS = new ArrayList<>();

    static {
        // PGLN URN identifier validation rules
        URN_MATCHERS.add(
                new Matcher(
                        "(urn:epc:id:pgln:).*",
                        "Invalid PGLN,PGLN should start with \"urn:epc:id:pgln:\",\n Please check the provided URN: %s"));
        URN_MATCHERS.add(
                new Matcher(
                        "(urn:epc:id:pgln:)[0-9]{6,12}\\..*",
                        "Invalid PGLN,PGLN should consist of GCP with 6-12 digits (Ex: urn:epc:id:pgln:123456.789012).\n Please check the provided URN: %s") {

                    @Override
                    public void validate(final String urn) throws ValidationException {
                        super.validate(urn);
                        String pgln = urn.substring(urn.lastIndexOf(":") + 1);

                        if (pgln.length() != 13) {
                            throw new ValidationException(
                                    String.format(
                                            "Invalid PGLN length, PGLN length should be 12 digits (Ex: urn:epc:id:pgln:123456.789012).  Please check the provided URN: %s", urn));
                        }
                    }
                });

        // PGLN Web URI identifier validation rules
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https)://.*",
                        "Invalid PGLN, PGLN should start with Domain name (Ex: https://id.gs1.org/), Please check the DL URI: %s"));
        URI_MATCHERS.add(
                new Matcher(
                        "(http|https):?://.*/417/[0-9]{13}",
                        "Invalid PGLN, PGLN should consist of 13 digit PGLN (Ex: https://id.gs1.org/417/1234567890128), Please check the URI: %s") {
                    @Override
                    public void validate(final String uri, final int gcpLength) throws ValidationException {
                        super.validate(uri);

                        // Check if the GCP Length matches
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

                        CheckDigitValidator.validatePGLN(uri);
                    }
                });
    }

    @Override
    public boolean supportsValidation(final String identifier) {
        // For URN identifier check if identifier contains URN part: ":pgln:"
        if (identifier.contains(PGLN_AI_URN_PREFIX)) {
            return true;
        }

        // For DL URI identifier check if identifier contains DL URI part: "/417/"
        return identifier.contains(PGLN_AI_URI_PREFIX);
    }

    @Override
    public boolean supportsValidation(final String identifier, final boolean isEpcisCompliant) {
        // As its already EPCIS compliant, return the result of the supportsValidation method
        return supportsValidation(identifier);
    }

    @Override
    public boolean validate(final String identifier, final ValidationContext validationContext) throws ValidationException {
        // Determine identifier type directly from the provided identifier
        boolean isUrn = identifier.contains(PGLN_AI_URN_PREFIX);

        // Select the correct matcher list.
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
