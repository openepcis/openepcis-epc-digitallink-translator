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
package io.openepcis.identifiers.tests.core.epcis.compliant;

import io.openepcis.identifiers.tests.core.epcis.ApplicationIdentifierValidationTestUtil;
import io.openepcis.identifiers.validator.exception.ValidationException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PGLNValidatorTest {

  @Test
  @Order(1)
  void invalidEpcUrnTest() throws ValidationException {
    // Invalid GS1 syntax
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:ec:id:pgln:123456.789012");

    // PGLN with invalid GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:pgln:1234A6.789012");

    // PGLN with GCP more than 12 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:pgln:1234567890123.789012");

    // PGLN with GCP less than 6 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:pgln:12345.789012");

    // PGLN with PGLN less than 13 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:pgln:123456.78901");

    // PGLN with PGLN more than 13 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:pgln:123456.789012345");
  }

  // Test for valid EPC URN identifiers.
  @Test
  @Order(2)
  void validEpcUrnTest() throws ValidationException {
    // Valid PGLN
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:pgln:123456.789012");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:pgln:473847.837483");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:pgln:859839494502.");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:pgln:0394039.40394");
  }

  // Test for invalid EPC URI identifiers.
  @Test
  @Order(3)
  void invalidEpcUriTest() throws ValidationException {
    // PGLN URI with invalid domain name
    ApplicationIdentifierValidationTestUtil.assertInvalid("hps://id.gs1.org/417/1234567890123", 6);

    // PGLN URI with more than 13 digit
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/417/12345678901234", 6);

    // PGLN URI with less than 13 digit
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/417/123456789012", 6);

    // PGLN URI with wrong code
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/415/1234567890123", 6);

    // PGLN with GCP Length less than 6 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/417/1234567890128", 5);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/417/1234567890128", 13);
  }

  // Test for valid EPC URI identifiers.
  @Test
  @Order(4)
  void validEpcUriTest() throws ValidationException {
    // Valid PGLN
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/417/1234567890128", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://horrem.kerpen.de/417/1234567890128", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://horrem.kerpen.de/417/9359267746574", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://horrem.kerpen.de/417/7337677829387", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://horrem.kerpen.de/417/5345364356436", 12);
  }
}
