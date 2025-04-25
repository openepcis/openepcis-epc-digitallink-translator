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

import io.openepcis.identifiers.validator.ValidatorFactory;
import io.openepcis.identifiers.validator.exception.ValidationException;
import org.junit.jupiter.api.Assertions;

public class ApplicationIdentifierValidationTestUtil {

    // Create a shared ValidatorFactory instance for all tests.
    private static final ValidatorFactory validatorFactory = new ValidatorFactory();

    /**
     * Asserts that the given identifier is valid. If a ValidationException is thrown, the test will
     * fail.
     *
     * @param identifier       the GS1 identifier to validate
     * @param isEpcisCompliant true if the identifier should be validated as an EPCIS compliant identifier
     * @param gcpLength        optional GCP length parameter (if applicable)
     */
    public static void assertValid(final String identifier, final boolean isEpcisCompliant, Integer... gcpLength) {
        try {
            boolean result = validatorFactory.validateIdentifier(identifier, isEpcisCompliant, gcpLength);
            Assertions.assertTrue(result, "Identifier should be valid: " + identifier);
        } catch (ValidationException e) {
            //System.out.println(e.getMessage());
            Assertions.fail("Did not expect ValidationException for identifier: " + identifier + " - " + e.getMessage());
        }
    }

    /**
     * Asserts that the given identifier is valid. If a ValidationException is thrown, the test will
     * fail.
     *
     * @param identifier the GS1 identifier to validate
     * @param gcpLength  optional GCP length parameter (if applicable)
     */
    public static void assertValid(final String identifier, Integer... gcpLength) {
        assertValid(identifier, true, gcpLength);
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
        try {
            validatorFactory.validateIdentifier(identifier, isEpcisCompliant, gcpLength);
            Assertions.fail("Expected ValidationException for identifier: " + identifier);
        } catch (Exception e) {
            // Expected exception; test passes.
            //System.out.println(e.getMessage());
        }
    }
}
