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
package io.openepcis.identifiers.tests.core.epcis;

import io.openepcis.core.exception.ValidationException;
import io.openepcis.digitallink.toolkit.GS1DigitalLinkNormalizer;
import io.openepcis.digitallink.utils.DefaultGCPLengthProvider;
import io.openepcis.identifiers.validator.ValidationContext;
import io.openepcis.identifiers.validator.ValidatorFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;

/**
 * Utility for asserting validity or invalidity of GS1 application identifiers.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationIdentifierValidationTestUtil {

    // Create a shared ValidatorFactory instance for all tests.
    private static final ValidatorFactory VALIDATOR_FACTORY = new ValidatorFactory(new GS1DigitalLinkNormalizer(), DefaultGCPLengthProvider.getInstance());


    /**
     * Asserts that the given identifier is valid. If a ValidationException is thrown, the test will fail.
     *
     * @param identifier the GS1 identifier to validate
     * @param gcpLength  optional GCP length parameter (if applicable)
     */
    public static void assertValid(final String identifier, Integer... gcpLength) {
        assertValid(identifier, true, gcpLength);
    }

    /**
     * Asserts that the given identifier is valid. If a ValidationException is thrown, the test will fail.
     *
     * @param identifier       the GS1 identifier to validate
     * @param isEpcisCompliant true if the identifier should be validated as an EPCIS compliant identifier
     * @param gcpLength        optional GCP length parameter (if applicable)
     */
    public static void assertValid(final String identifier, final boolean isEpcisCompliant, final Integer... gcpLength) {
        assertValid(identifier, buildContext(isEpcisCompliant, gcpLength));
    }

    /**
     * Asserts that the given identifier is valid under the provided context.
     *
     * @param identifier        the GS1 identifier to validate
     * @param validationContext the validation context
     */
    public static void assertValid(final String identifier, final ValidationContext validationContext) {
        try {
            boolean result = VALIDATOR_FACTORY.validateIdentifier(identifier, validationContext);
            Assertions.assertTrue(result, "Identifier should be valid: " + identifier);
        } catch (ValidationException e) {
            //System.out.println(e.getMessage());
            Assertions.fail("Did not expect ValidationException for identifier: " + identifier + " - " + e.getMessage());
        }
    }


    /**
     * Asserts that the given identifier is invalid by expecting a ValidationException. If a valid
     * result is returned, the test will fail.
     *
     * @param identifier the GS1 identifier to validate
     * @param gcpLength  optional GCP length parameter (if applicable)
     */
    public static void assertInvalid(final String identifier, Integer... gcpLength) {
        assertInvalid(identifier, true, gcpLength);
    }

    /**
     * Asserts that the given identifier is invalid by expecting a ValidationException. If a valid
     * result is returned, the test will fail.
     *
     * @param identifier       the GS1 identifier to validate
     * @param isEpcisCompliant true if the identifier should be validated as an EPCIS compliant identifier
     * @param gcpLength        optional GCP length parameter (if applicable)
     */
    public static void assertInvalid(final String identifier, final boolean isEpcisCompliant, Integer... gcpLength) {
        assertInvalid(identifier, buildContext(isEpcisCompliant, gcpLength));
    }

    public static void assertInvalid(final String identifier, final ValidationContext validationContext) {
        try {
            VALIDATOR_FACTORY.validateIdentifier(identifier, validationContext);
            Assertions.fail("Expected ValidationException for identifier: " + identifier);
        } catch (Exception e) {
            // Expected exception; test passes.
            //System.out.println(e.getMessage());
        }
    }

    /**
     * Builds a {@link ValidationContext} with the given EPCIS compliance flag
     * and optional GCP length.
     *
     * @param isEpcisCompliant  whether to enforce EPCIS compliance
     * @param gcpLengthOptional optional GCP length (first element used if present)
     * @return a configured ValidationContext
     */
    private static ValidationContext buildContext(final boolean isEpcisCompliant, final Integer... gcpLengthOptional) {
        final ValidationContext.ValidationContextBuilder builder = ValidationContext.builder()
                .epcisCompliant(isEpcisCompliant)
                .validateCheckDigit(false);

        if (gcpLengthOptional != null && gcpLengthOptional.length > 0) {
            builder.gcpLength(gcpLengthOptional[0]);
        }

        return builder.build();
    }
}
