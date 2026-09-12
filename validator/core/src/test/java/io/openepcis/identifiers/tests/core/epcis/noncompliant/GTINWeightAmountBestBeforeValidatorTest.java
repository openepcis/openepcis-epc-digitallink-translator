/*
 * Copyright 2022-2026 benelog GmbH & Co. KG
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package io.openepcis.identifiers.tests.core.epcis.noncompliant;

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.tests.core.epcis.ApplicationIdentifierValidationTestUtil;
import io.openepcis.identifiers.validator.ValidationContext;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

class GTINWeightAmountBestBeforeValidatorTest {

    // Test for invalid URI identifiers
    @Test
    @Order(0)
    void invalidUriTest() throws ValidationException {
        // Without Expiry Date
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788&3103=000195&3922=0299", false, 10);

        // Without GTIN
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/?17=201225&3103=000195&3922=0299", false, 10);

        // Without Domain
        ApplicationIdentifierValidationTestUtil.assertInvalid("/01/09520123456788?17=201225&3103=000195&3922=0299", false, 10);

        // Without Net Weight
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788&3922=0299&17=201225", false, 10);

        // With extra parameter
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788?17=201225&3103=000195&3922=0299?22=123", false, 12);

        // Invalid GCP length
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788?17=201225&3103=000195&3922=0299", false, 13);

        // EPCIS Compliant true with valid
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788?3103=000195&3922=0299&17=201225", true, 10);
    }

    // Test for valid URI identifiers
    @Test
    @Order(1)
    void validUriTest() throws ValidationException {
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788?3103=000195&3922=0299&17=201225", false, 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788?17=201225&3103=000195&3922=0299", false, 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788?17=201225&3103=000195&3922=0299", false, 12);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788?3103=000195&3922=0299&17=201225", false, 6);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788?3922=0299&3103=000195&17=201225", false, 6);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788?3103=000195&17=201225&3922=0299", false, 6);
    }

    // Test for validating Check digit
    @Test
    @Order(2)
    void validateCheckDigit() throws ValidationException {
        final ValidationContext validationContext = ValidationContext.builder().gcpLength(10).epcisCompliant(false).build();

        // Valid Check digit
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/07836491528371?3103=000195&3922=0299&17=201225", validationContext);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/10472958163095?17=201225&3103=000195&3922=0299", validationContext);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/11287593460121?3103=000195&17=201225&3922=0299", validationContext);

        // Invalid Check digit
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/07836491528376?3103=000195&3922=0299&17=201225", validationContext);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/10472958163092?17=201225&3103=000195&3922=0299", validationContext);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/11287593460125?3103=000195&17=201225&3922=0299", validationContext);
    }
}
