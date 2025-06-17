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

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.tests.core.epcis.ApplicationIdentifierValidationTestUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CPIValidatorTest {

  // Test for invalid EPC URN identifiers.
  @Test
  @Order(1)
  void invalidEpcUrnTest() throws ValidationException {
    // CPI without serial.
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:cpi:1234567890.1234");
    // CPI with less than 7 digits.
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:cpi:123456..1111");
    // CPI with more than 31 digits.
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:cpi:123456.7890123456789012345678901.1111");
    // CPI with invalid characters in serial number.
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:cpi:123456.7890.111A");
    // CPI with invalid GCP.
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:cpi:1234567890123.7890.111A");
  }

  // Test for valid EPC URN identifiers.
  @Test
  @Order(2)
  void validEpcUrnTest() throws ValidationException {
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:cpi:3813667.83201294-5A.489332");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:cpi:123456.789012345.1111");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:cpi:0614141.123ABC.123456789");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:cpi:6282274.234748947/.94304");
  }

  // Test for invalid EPC URI identifiers.
  @Test
  @Order(3)
  void invalidEpcUriTest() throws ValidationException {
    // Invalid characters in CPI URI.
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/12A456789012345/8011/1111", 10);

    // CPI URI with invalid GCP length.
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/12345678901234/8011/1111", 5);

    // CPI less than GCP length.
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/1234567890/8011/1111", 12);

    // CPI without serial number.
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/123456789012/8011/", 10);

    // CPI without GCP length.
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/30056867890#1294-5A/8011/4893");

    // Invalid characters in CPI URI
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/12345678901234A/8011/1111");

    // CPI URI with invalid GCP Length
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/12345678901234/8011/1111", 5);

    // CPI less than GCP Length
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/1234567890/8011/1111", 12);

    // CPI without serial number
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/123456789012/8011/", 10);

    // CPI without GCP length
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/123456789012/8011/");
  }

  // Test for valid EPC URI identifiers.
  @Test
  @Order(4)
  void validEpcUriTest() throws ValidationException {
    // Valid CPI
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8010/12345678A9012/8011/1010", 8);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8010/123456789012/8011/1010", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://benelog.com/8010/123456789012/8011/1010", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://benelog.com/8010/1234567890ANC/8011/124", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8010/4748374/23748#94//8011/94304", 7);
  }

  // Test for invalid class-level URN identifiers.
  @Test
  @Order(5)
  void invalidClassUrnTest() throws ValidationException {
    // Class level CPI without serial
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:cpi:3636636.36636366363");

    // Class level CPI with more than 12 digits GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:cpi:1234567890123.123.*");

    // Class level CPI with less than 6 digits GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:cpi:12345.36636366363.*");

    // Class level CPI with more than 30 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:cpi:123456.7890123456789012345678901.*");

    // Class level CPI with serial number
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:cpi:123456.36636366363.123");
  }

  // Test for valid class-level URN identifiers.
  @Test
  @Order(6)
  void validClassUrnTest() throws ValidationException {
    // Valid class level CPI URN
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:cpi:858588503943.4/38.*");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:cpi:438483.74387483.*");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:cpi:164759476414..*");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:cpi:794783489348.89348934EJECTION.*");
  }

  // Test for invalid class-level URI identifiers.
  @Test
  @Order(7)
  void invalidClassUriTest() {
    // Invalid class-level CPI URI with more than 30 digits.
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/123456-789012345678901234567890", 10);

    // Invalid class-level CPI URI with GCP longer than CPI length.
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/1234567", 10);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/123457-5757", 12);

    // Invalid class-level CPI URI with invalid characters.
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/12345678901234*", 6);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/1234567890123456789012345k7890", 6);

    // Class-level CPI URI with an invalid GCP length.
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/12345678901234", 5);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/12345678901234", 13);

    // Class-level CPI URI without GCP length.
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8010/9520885039434/38");
  }

  // Test for valid class-level URI identifiers.
  @Test
  void validClassUriTest() {
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8010/12345678901234", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8010/123457-57578", 12);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8010/8585885039434/38", 12);
  }
}
