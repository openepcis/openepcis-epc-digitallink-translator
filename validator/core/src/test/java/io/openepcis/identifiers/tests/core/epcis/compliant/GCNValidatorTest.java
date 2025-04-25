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
class GCNValidatorTest {

  // Test for invalid EPC URN identifiers.
  @Test
  @Order(1)
  void invalidEpcUrnTest() throws ValidationException {
    // SGCN without serial number
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgcn:1234567890.12");

    // SGCN with less than 14 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgcn:1234567890.1.1234");

    // SGCN with more than 14 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgcn:1234567890.123.1234");

    // Invalid characters in SGCN
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgcn:1234567890.1A.1234");

    // Invalid characters in Serial
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgcn:1234567890.12.1234A");

    // SGCN with invalid GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgcn:123456789012.12.1234");
  }

  // Test for valid EPC URN identifiers.
  @Test
  @Order(2)
  void validEpcUrnTest() throws ValidationException {
    // Valid SGCN
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sgcn:1234567890.12.1234");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sgcn:1234567890.12.45678901234");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sgcn:439439434939..4");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sgcn:5485948594.85.95495849");
  }

  // Test for invalid EPC URI identifiers.
  @Test
  @Order(3)
  void invalidEpcUriTest() throws ValidationException {
    // SGCN URI with less than 13 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/255/123456789012", 10);

    // SGCN with invalid GCP length
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/255/12345678901234", 13);

    // SGCN with more than 25 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/255/12345678901234567890123456", 12);

    // Invalid characters in SGCN
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/255/12345678901234A", 12);
  }

  // Test for valid EPC URI identifiers.
  @Test
  @Order(4)
  void validEpcUriTest() throws ValidationException {
    // Valid SGCN URI
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/255/12345678901234", 12);
    ApplicationIdentifierValidationTestUtil.assertValid("https://gs1.in/255/12345678901234", 12);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/255/43943943493924", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/255/300096758845486", 7);
  }

  // Test for invalid class-level URN identifiers.
  @Test
  @Order(5)
  void invalidClassUrnTest() throws ValidationException {
    // Class level GCN Web URI with more than 13 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/255/123456789012", 8);

    // Class level GCN Web URI with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/255/123456789A123", 7);

    // Class level GCN Web URI with invalid GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/255/1234567890123", 5);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/255/1234567890123", 14);

    // Class level GCN Web URI with invalid prefix
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/2554/1234567890123", 11);
  }

  // Test for valid class-level URN identifiers.
  @Test
  @Order(6)
  void validClassUrnTest() throws ValidationException {
    // Valid class level GCN URN
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:sgcn:123456789.012.*");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:sgcn:656256789012..*");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:sgcn:283892.329328.*");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:sgcn:757845748574..*");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:sgcn:93588154.8574.*");
  }

  // Test for invalid class-level URI identifiers.
  @Test
  @Order(7)
  void invalidClassUriTest() throws ValidationException {
    // Class level GCN with more than 13 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:sgcn:1234567.890981.*");

    // Class level GCN with less than 13 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:sgcn:7578457.4857.*");

    // Class level GCN with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:sgcn:7578457.4857A.*");

    // Class level GCN with GCP less than 6 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:sgcn:12345.789098.*");

    // Class level GCN with GCP more than 12 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:sgcn:1234567890981..*");

    // Class level GCN with serial numbers
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:sgcn:12345678.9012.123");

    // Class level GCN without *
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:sgcn:12345678.9012.");

    // Class level GCN with invalid prefix
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:sgcnn:12345678.9012.*");
  }

  // Test for valid class-level URI identifiers.
  @Test
  @Order(8)
  void validClassUriTest() throws ValidationException {
    // Valid class level GCN URN
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/255/1234567890128", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/255/4343884394893", 8);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/255/7438748374382", 9);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/255/5787674634636", 12);
  }
}
