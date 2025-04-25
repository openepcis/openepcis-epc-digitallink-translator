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
class LGTINValidatorTest {
  // Test for invalid LGTIN identifiers.
  @Test
  @Order(1)
  void invalidLgtinTest() throws ValidationException {
    // No Serial Number
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:class:lgtin:234567.1890123");

    // LGTIN less than 14 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:class:lgtin:234567.189012.1234");

    // LGTIN with invalid characters in GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:class:lgtin:23A567.189012.1234");

    // LGTIN with invalid characters in GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:class:lgtin:234567.189A12.1234");

    // LGTIN with GCP less than 6 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:class:lgtin:23456.189012.1234");

    // SGTIN with GCP more than 12 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:class:lgtin:23456789012345.189012.1234");
  }

  // Test for valid LGTIN identifiers.
  @Test
  @Order(2)
  void validLgtinTest() throws ValidationException {
    // Valid LGTIN
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:class:lgtin:4023333.002000.2019-10-07");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:class:lgtin:234567.1890124.1234");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:class:lgtin:4384738478.734.8484892%");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:class:lgtin:2345678901.123.!\"%&'()*+,-./19:;<=>");
  }

  // Test for invalid LGTIN URI identifiers.
  @Test
  @Order(3)
  void invalidLgtinUriTest() throws ValidationException {
    // LGTIN URI with LGTIN less than 14 digit
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/1234567890123/10/9999");

    // LGTIN URI with LGTIN more than 14 digit
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/123456789012312/10/9999");

    // LGTIN URI with LGTIN without serial number
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/123456789012312");

    // LGTIN with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/5985035903859A/10/2z32746");

    // LGTIN with invalid GCP Length
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/59850359038590/10/2z32746", 5);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/59850359038590/10/2z32746", 13);
  }

  // Test for valid LGTIN URI identifiers.
  @Test
  @Order(4)
  void validLgtinUriTest() throws ValidationException {
    // Valid LGTIN validation from WebURI to URN
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/59850359038590/10/232746", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/59046477538590/10/2z32746", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/59046537003451/10/998877", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://samsung.com/de/01/12345678901234/10/9999", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/12475775757579/10/488484", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/74778478489849/10//777474", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/49557283728732/10//8484892%", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/12345678901231/10/!\"%&'()*+,-./19:;<=>", 10);
  }
}
