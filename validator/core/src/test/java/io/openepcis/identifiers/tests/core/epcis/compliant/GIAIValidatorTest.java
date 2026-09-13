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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GIAIValidatorTest {
  // Test for invalid EPC URN identifiers.
  @Test
  @Order(1)
  void invalidEpcUrnTest() throws ValidationException {
    // GIAI with invalid GS1 syntax
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:ec:id:giai:1234567890");

    // GIAI without serial number
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:giai:1234567890");

    // GIAI with invalid characters in GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:giai:123456789A.123");

    // GIAI with less than 6 digits GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:giai:12345.123");

    // GIAI with more than 12 digits GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:giai:1234567890123.123");
  }

  // Test for valid EPC URN identifiers.
  @Test
  @Order(2)
  void validEpcUrnTest() throws ValidationException {
    // Valid GIAI
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:giai:123456789012.123");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:giai:123456.7");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:giai:839495849585.0");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:giai:839495849585.0!\"/%_");
  }

  // Test for invalid EPC URI identifiers.
  @Test
  @Order(3)
  void invalidEpcUriTest() throws ValidationException {
    // GIAI with invalid URI format
    ApplicationIdentifierValidationTestUtil.assertInvalid("/8004/123456789012123", 10);

    // GIAI with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8004/123A6789012123", 10);

    // GIAI with gcp less than 6
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8004/123456789012123", 5);

    // GIAI with gcp more than 12
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8004/123456789012123", 13);
  }

  // Test for valid EPC URI identifiers.
  @Test
  @Order(4)
  void validEpcUriTest() throws ValidationException {
    // Valid GIAI
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8004/123456789012123", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://news.google.in/8004/123456789012123", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8004/3575958495850!\"/%_", 12);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8004/7324878;><=?", 8);
  }
}
