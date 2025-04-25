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
import io.openepcis.identifiers.validator.ValidatorFactory;
import io.openepcis.identifiers.validator.exception.ValidationException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GDTIValidatorTest {

  final ValidatorFactory validatorFactory = new ValidatorFactory();

  // Test for invalid EPC URN identifiers.
  @Test
  @Order(1)
  void invalidEpcUrnTest() throws ValidationException {
    // GDTI without serial number
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gdti:1234567890.12");

    // GDTI with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gdti:123456789A.12.13");

    // GDTI with GCP more than 12 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gdti:1234567890123.12.13");

    // GDTI with more than 13 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gdti:1234567890.12345.13");
  }

  // Test for valid EPC URN identifiers.
  @Test
  @Order(2)
  void validEpcUrnTest() throws ValidationException {
    // Valid GDTI
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gdti:1234567890.12.ABC");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gdti:124757.578484.!");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gdti:893489348949..\":?>");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gdti:434934948984..4");
  }

  // Test for invalid EPC URI identifiers.
  @Test
  @Order(3)
  void invalidEpcUriTest() throws ValidationException {
    // GDTI URI with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/253/123456789A128ABC", 10);

    // GDTI URI with over 30 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/253/1234567890123123456789012345690", 10);

    // GDTI with invalid GCP Length
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/253/1234567890123A", 13);
  }

  // Test for valid EPC URI identifiers.
  @Test
  @Order(4)
  void validEpcUriTest() throws ValidationException {
    // Valid GDTI
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/253/1234567890123A", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://benelog1.de/253/1234567890123A", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/253/8934893489494\":?>", 12);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/253/43493494898454", 12);
  }

  // Test for invalid class-level URN identifiers.
  @Test
  @Order(5)
  void invalidClassUrnTest() throws ValidationException {
    // Class level GDTI Web URI with more than 13 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:gdti:123456789012345");

    // Class level GDTI Web URI with less than 13 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:gdti:123456789012");

    // Class level GDTI Web URI with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:gdti:123456789012A");

    // Class level GDTI with invalid GCP length
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:gdti:1234567890123", 5);
  }

  // Test for valid class-level URN identifiers.
  @Test
  @Order(6)
  void validClassUrnTest() throws ValidationException {
    // Valid class level GDTI URN
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:gdti:7947834893.89.*");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:gdti:438483.743874.*");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:gdti:164759476414..*");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:gdti:79478348934.8.*");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:gdti:4394834.84756.*");
  }

  // Test for invalid class-level URI identifiers.
  @Test
  @Order(7)
  void invalidClassUriTest() throws ValidationException {
    // Invalid class-level CPI URI with more than 30 digits.
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/253/123456-789012345678901234567890", 10);

    // Invalid class-level CPI URI with GCP longer than CPI length.
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/253/1234567", 10);

    // Invalid class-level CPI URI with invalid characters.
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/253/1234567*8901234", 6);

    // Class-level CPI URI with an invalid GCP length.
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/253/12345678901234", 5);
  }

  // Test for valid class-level URI identifiers.
  @Test
  @Order(8)
  void validClassUriTest() throws ValidationException {
    // Valid class level GDTI Web URI
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/253/12345678901234", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/253/8438489238239", 12);
    ApplicationIdentifierValidationTestUtil.assertValid("https://google.fb.org/253/8438489238239", 12);
  }
}
