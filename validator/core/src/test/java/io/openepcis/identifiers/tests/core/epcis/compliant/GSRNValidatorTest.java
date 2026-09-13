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
package io.openepcis.identifiers.tests.core.epcis.compliant;

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.tests.core.epcis.ApplicationIdentifierValidationTestUtil;
import io.openepcis.identifiers.validator.ValidationContext;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GSRNValidatorTest {

    @Test
    @Order(1)
    void invalidGSRNUrnTest() throws ValidationException {
        // GSRN with invalid characters in gcp
        ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsrn:1234567A90.1234567");

        // GSRN with less than 18 digits
        ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsrn:1234567890.123456");

        // GSRN with more than 18 digits
        ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsrn:1234567890.12345678");

        // GSRN without GCP
        ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsrn:123456789012345678");
    }

    @Test
    @Order(2)
    void validGSRNUrnTest() throws ValidationException {
        ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gsrn:1234567890.1234567");
        ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gsrn:142512.45142152511");
        ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gsrn:673674637437.47783");
        ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gsrn:654683828.92302309");
    }

    @Test
    @Order(3)
    void invalidGSRNUriTest() throws ValidationException {
        // GSRN URI with more than 18 characters
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8018/1234567890123456751", 10);

        // GSRN URI with less than 18 characters
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8018/12345678901234567", 10);

        // GSRN URI with invalid characters
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8018/12345678901234567A", 10);

        // GSRN URI with invalid GCP
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8018/123456789012345675", 5);
    }

    @Test
    @Order(4)
    void validGSRNUriTest() throws ValidationException {
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8018/123456789012345675", 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://hp.com/laptop/8018/123456789012345675", 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://hp.com/laptop/8018/301037675245142514", 12);
        ApplicationIdentifierValidationTestUtil.assertValid("https://hp.com/laptop/8018/302241753828392839", 6);
        ApplicationIdentifierValidationTestUtil.assertValid("https://hp.com/laptop/8018/654673483739829829", 11);
    }

    @Test
    @Order(5)
    void validateCheckDigitTest() throws ValidationException {
        final ValidationContext validationContext = ValidationContext.builder().validateCheckDigit(true).gcpLength(10).build();

        // Valid check digit
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8018/123456789012345675", validationContext);
        ApplicationIdentifierValidationTestUtil.assertValid("https://hp.com/laptop/8018/302241753828392832", validationContext);
        ApplicationIdentifierValidationTestUtil.assertValid("https://hp.com/laptop/8018/301037675245142512", validationContext);

        // Invalid check digit
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8018/123456789012345674", validationContext);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://hp.com/laptop/8018/302241753828392839", validationContext);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://hp.com/laptop/8018/301037675245142519", validationContext);
    }
}
