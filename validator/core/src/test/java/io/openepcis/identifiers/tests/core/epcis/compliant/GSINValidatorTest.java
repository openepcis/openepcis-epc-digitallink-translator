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
import io.openepcis.identifiers.validator.ValidationContext;
import io.openepcis.identifiers.validator.exception.ValidationException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GSINValidatorTest {

  @Test
  @Order(1)
  void invalidGSINUrnTest() throws ValidationException {
    // GSIN with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsin:12345678A0.123456");

    // GSIN with less than 6 digits GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsin:12345.123456");

    // GSIN with more than 17 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsin:1234567890.1234567");

    // GSIN with less than 17 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsin:1234567890.12345");

    // GSIN with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsin:1234567890.12345A");

    // GSIN with invalid characters in GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsin:12345A.7890123456");
  }

  @Test
  @Order(2)
  void validGSINUrnTest() throws ValidationException {
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gsin:123456.7890123456");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gsin:483984.3984398439");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gsin:859485945045.0454");
  }

  @Test
  @Order(3)
  void invalidGSINUriTest() throws ValidationException {
    // GSIN URI with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/402/1234567890512345A", 6);

    // GSIN with less than 17 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/402/1234567890512345", 6);

    // GSIN with more than 17 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/402/123456789051234567", 6);

    // GSIN with invalid GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/402/95485984950459045", 5);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/402/95485984950459045", 13);
  }

  @Test
  @Order(4)
  void validGSINUriTest() throws ValidationException {
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/402/12345678905123456", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://benelog.com/horrem/402/12345678905123456", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://benelog.com/horrem/402/95249893589539394", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://benelog.com/horrem/402/95204454958349836", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://https://id.gs1.org/402/95205984950459045", 6);
  }

  @Test
  @Order(5)
  void checkDigitTest() throws ValidationException {
    final ValidationContext validationContext = ValidationContext.builder().validateCheckDigit(true).gcpLength(10).build();

    // Valid Check digit
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/402/12345678905123457", validationContext);
    ApplicationIdentifierValidationTestUtil.assertValid("https://benelog.com/horrem/402/95204454958349832", validationContext);
    ApplicationIdentifierValidationTestUtil.assertValid("https://benelog.com/horrem/402/95485984950459046", validationContext);

    // Invalid Check digit
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://benelog.com/horrem/402/12345678905123459", validationContext);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://benelog.com/horrem/402/95204454958349831", validationContext);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://https://id.gs1.org/402/95485984950459047", validationContext);
  }
}
