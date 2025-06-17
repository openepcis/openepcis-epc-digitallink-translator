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
import io.openepcis.identifiers.validator.ValidationContext;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SGLNValidatorTest {
  // Test for invalid EPC URN identifiers.
  @Test
  @Order(1)
  void invalidEpcUrnTest() throws ValidationException {
    // SGLN with invalid GS1 format
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc::sgln:1234567890.1.1111");

    // SGLN with invalid GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgln:1234567890AS.1.1111");

    // SGLN with GCP less than 6 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgln:1234.1.1111");

    // SGLN with GCP more than 12 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgln:12345678901234.1.1111");

    // SGLN with less than 13 digits SGLN
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgln:1234567890.1.1111");

    // SGLN with more than 13 digits SGLN
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgln:12345678901.11.1111");

    // SGLN without Serial Number
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgln:1234567890.11");
  }

  // Test for valid EPC URN identifiers.
  @Test
  @Order(2)
  void validEpcUrnTest() throws ValidationException {
    // Valid SGLN with serial Number
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sgln:1234567890.11.1111");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sgln:437473647364..\"%&'()*+,-./19:;<=>?");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sgln:437473647364..0");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sgln:785783.438478.0394903");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sgln:4374736473.64.0");
  }

  // Test for invalid EPC URI identifiers.
  @Test
  @Order(3)
  void invalidEpcUriTest() throws ValidationException {
    // SGLN URI with invalid format
    ApplicationIdentifierValidationTestUtil.assertInvalid("hps://id.gs1.org/414/1234567890123/254/1111", 6);

    // SGLN with 12 digit SGLN
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/414/123456789012/254/1111", 6);

    // SGLN with GCP less than 6 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/414/1234567890123/254/1111", 5);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/414/1234567890123/254/1111", 13);

    // SGLN with 14 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/414/12345678901234/254/1111", 12);

    // SGLN with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/414/123456789012A/254/1111", 12);

    // SGLN with invalid serial
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/414/5893849384938/254", 12);

    // SGLN with extension prefix only
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/414/5893849384938/254/", 12);
  }

  // Test for valid EPC URI identifiers.
  @Test
  @Order(4)
  void validEpcUriTest() throws ValidationException {
    // Valid SGLN URI with serial number
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/414/1234567890123/254/1234", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://deutscheBahn.de/train/414/1234567890123/254/1234", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/414/9359074384782/254/0394903", 12);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/414/9359044384782/254/12", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/414/9359239384938/254/4390493", 12);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/414/6880009384938/254/12", 12);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/414/4374736473640/254/1243", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/414/4374736473640", 10);
  }

  // Test for Check digit
  @Test
  @Order(5)
  void validateCheckDigit() throws ValidationException {
    final ValidationContext validationContext = ValidationContext.builder().validateCheckDigit(true).gcpLength(10).build();

    // Valid Check Digit
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/414/1234567890128/254/1234", validationContext);
    ApplicationIdentifierValidationTestUtil.assertValid("https://deutscheBahn.de/train/414/1234567121222/254/1234", validationContext);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/414/9359074384785/254/0394903", validationContext);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/414/9359044384784", validationContext);

    // Invalid Check Digit
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/414/1234567890127/254/4390493", validationContext);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/414/1234567121225/254/12", validationContext);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/414/9359074384783/254/1243", validationContext);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/414/9359044384789", validationContext);
  }

  // Test for Check digit
  @Test
  @Order(5)
  void validateCheckDigit1() throws ValidationException {
    final ValidationContext validationContext = ValidationContext.builder().validateCheckDigit(true).gcpLength(10).build();

    // Valid Check Digit
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/414/1234567890128/254/1234", validationContext);
 }
}
