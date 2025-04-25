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
class GINCValidatorTest {
  // Test for invalid EPC URN identifiers.
  @Test
  @Order(1)
  void invalidEpcUrnTest() throws ValidationException {
    // Invalid GS1 Syntax
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:i:ginc:1234567890.12ABC");

    // GINC with invalid characters in GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:ginc:1234567890A.12ABC");

    // GINC without serial number
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:ginc:1234567890");

    // GINC more than 30 characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:ginc:1234567890.123456789012345678901");
  }

  // Test for valid EPC URN identifiers.
  @Test
  @Order(2)
  void validEpcUrnTest() throws ValidationException {
    // Valid GINC URN
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:ginc:1234567890.1234");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:ginc:473847.3");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:ginc:484930473847.3");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:ginc:48493047.38473!\"%/%&'()*+,-.:=");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:ginc:48493047.38473!\"\"%/%&'()*+,-.:=");
  }

  // Test for invalid EPC URI identifiers.
  @Test
  @Order(3)
  void invalidEpcUriTest() throws ValidationException {
    // GINC less than GCP Length
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/401/12345678901", 12);

    // GINC with GCP Length more than 12
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/401/123456789012", 15);

    // Invalid Characters in GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/401/12345678A012", 10);

    // Invalid Web URI Prefix
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/405/1234567890.1234&/", 10);
  }

  // Test for valid EPC URI identifiers.
  @Test
  @Order(4)
  void validEpcUriTest() throws ValidationException {
    // Valid GINC URI
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/401/12345678901234A", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://eclipse.org/401/12345678901234A", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/401/4849304738473!\"\"%/%&'()*+,-.:=", 8);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/401/656987789012", 12);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/401/123456789012d", 12);
  }
}
