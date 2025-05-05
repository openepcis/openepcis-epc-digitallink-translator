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
class GRAIValidatorTest {
  @Test
  @Order(1)
  void invalidGRAIUrnTest() throws ValidationException {
    // GRAI with invalid GS1 syntax
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:i:grai:1234567890.12.ABC");

    // GRAI without serial number
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:grai:1234567890.123");

    // GRAI with less than 6 digit gcp
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:grai:12345.12.4ABC");

    // GRAI with more than 12 digit gcp
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:grai:1234567890123.12.4ABC");

    // GRAI not equal to 14 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:grai:123456.12.4ABC");

    // GRAI with invalid characters in GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:grai:123456789A.12.4ABC");
  }

  @Test
  @Order(2)
  void validGRAIUrnTest() throws ValidationException {
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:grai:1234567890.12.4ABC");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:grai:438478.374837.3:;<>=?AZ_az");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:grai:123456789012..!\"%&'()*+,-.");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:grai:123456789012..4");
  }

  @Test
  @Order(3)
  void invalidGRAIUriTest() throws ValidationException {
    // GRAI URI with invalid domain
    ApplicationIdentifierValidationTestUtil.assertInvalid("hps://id.gs1.org/8003/12345678901284ABC");

    // GRAI URI with GRAI less than 14 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8003/1234567890123");

    // GRAI with GCP length more than 12 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8003/12345678901234");
  }

  @Test
  @Order(4)
  void validGRAIUriTest() throws ValidationException {
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8003/12345678901234ABCD", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://youtube.com.org/8003/12345678901234ABCD", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8003/43847837483703:;<>=?AZ_az", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8003/1234567890128!\"%&'()*+,-.", 12);
  }

  @Test
  @Order(5)
  void invalidClassUrnTest() throws ValidationException {
    // Class level GRAI with less than 13 digit
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:grai:123456789012");

    // Class level GRAI with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:grai:123456789012A");

    // Class level GRAI with invalid prefix
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:grai:123456789012A");

    // Class level GRAI with invalid GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:grai:1234567890123", 5);
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:grai:1234567890123", 13);
  }

  @Test
  @Order(6)
  void validClassUrnTest() throws ValidationException {
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:grai:784283728372..*", 12);
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:grai:7842837.28372.*", 12);
  }

  @Test
  @Order(7)
  void invalidClassUriTest() throws ValidationException {
    // Class level GRAI URN with invalid character
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:grai:7842837.A8372.*");

    // Class level GRAI URN with invalid GCP length
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:grai:7842837283723..*");

    // Class level GRAI with invalid GRAI length
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:grai:784283.28372.*");

    // Class level GRAI without serial *
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:grai:1234567890.12.");

    // Class level GRAI with serial number
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:grai:1234567890.12.124");
  }

  @Test
  @Order(8)
  void validClassUriTest() throws ValidationException {
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8003/4843847384737", 7);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8003/8493394839844", 7);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8003/8439483984392", 7);
  }

  @Test
  @Order(8)
  void validateCheckDigitTest() throws ValidationException {
    final ValidationContext validationContext = ValidationContext.builder().validateCheckDigit(true).gcpLength(10).build();

    // GRAI with valid check digit
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8003/4843847384737", validationContext);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8003/8493394839844", validationContext);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8003/8439483984392", validationContext);

    // GRAI with invalid check digit
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8003/4843847384739", validationContext);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8003/8493394839840", validationContext);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8003/8439483984391", validationContext);
  }
}
