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
class ITIPValidatorTest {
  // Test for invalid EPC URN identifiers.
  @Test
  @Order(1)
  void invalidEpcUrnTest() throws ValidationException {
    // ITIP with less than 18 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:itip:234567.189012.56.78.1111");

    // ITIP with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:itip:234567.189012A.56.78.1111");

    // ITIP without serial numbers
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:itip:234567.1890123.56.78");

    // ITIP with more than 18 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:itip:234567.1890123.561.78.1111");

    // ITIP with GCP less than 12 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:itip:23456.1890123.56.78.1111");

    // ITIP with Invalid character in GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:itip:23456A.1890123.56.78.1111");

    // ITIP with Invalid digit separation
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:itip:234567.1890123..78.1111");
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:itip:234567.1890123.56..1111");
  }

  // Test for valid EPC URN identifiers.
  @Test
  @Order(2)
  void validEpcUrnTest() throws ValidationException {
    // Valid ITIP
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:itip:2345678901.123.67.87.1111");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:itip:467348.6589384.38.44.!\"%&'()*+,-./1:;<=>=");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:itip:583483473847.7.34.74.5785");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:itip:3564536535.664.35.64.85945894");
  }

  // Test for invalid EPC URI identifiers.
  @Test
  @Order(3)
  void invalidEpcUriTest() throws ValidationException {
    // ITIP with less than 18 digit URI
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8006/12345678901235678/21/1111");

    // ITIP with invalid characters in URI
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8006/12345678901235678A/21/1111");

    // ITIP without serial numbers
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8006/123456789012356787");

    // ITIP with invalid GCP Length
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8006/123456789012356787/21/1111", 5);
  }

  // Test for valid EPC URI identifiers.
  @Test
  @Order(4)
  void validEpcUriTest() throws ValidationException {
    // Valid ITIP
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8006/123456789012345678/21/1111", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://riseagainst.com/songs/8006/123456789012356787/21/1111", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8006/646734858938493844/21/!\"%&'()*+,-./1:;<=>=", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8006/387061855656575665/21/85945894", 8);
  }

  // Test for invalid class-level URN identifiers.
  @Test
  @Order(5)
  void invalidClassUrnTest() throws ValidationException {
    // Class level ITIP Web URI with more than 18 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8006/1234567890123456789", 10);

    // Class level ITIP Web URI with less than 18 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8006/12345678901234567", 11);

    // Class level ITIP Web URI with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8006/12345678901234567A", 12);

    // Class level ITIP Web URI with invalid GCP Length
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8006/123456789012345678", 5);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8006/123456789012345678", 13);

    // Class level ITIP Web URI with invalid prefix
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8101/123456789012345678", 6);
    ApplicationIdentifierValidationTestUtil.assertInvalid("/8006/123456789012345678", 12);
  }

  // Test for valid class-level URI identifiers.
  @Test
  @Order(6)
  void validClassUriTest() throws ValidationException {
    // Valid Class level WebURI
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8006/123456789012345678", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8006/123457575787456278", 12);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8006/858588503943443038", 12);
  }

  // Test for invalid class-level URI identifiers.
  @Test
  @Order(7)
  void invalidClassUriTest() throws ValidationException {
    // Class level ITIP URN with more than 18 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:itip:2345678901.1234.56.78.*");

    // Class level ITIP URN with less than 18 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:itip:234567890.123.56.78.*");

    // Class level ITIP URN with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:itip:943549A.330943.04.93.*");

    // Class level ITIP URN with GCP less than 6 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:itip:94354.3030943.04.93.*");

    // Class level ITIP URN with GCP more than 12 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:itip:9435490309431.3.04.93.*");

    // Class level ITIP URN with serial number
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:itip:943549030943.3.04.93.124");

    // Class level ITIP URN without *
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:itip:943549030943.3.04.93.");

    // Class level ITIP URN invalid prefix
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idr:itip:943549030.3943.04.93.*");
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:itip1:943549030.3943.04.93.*");
  }

  // Test for valid class-level URN identifiers.
  @Test
  @Order(8)
  void validClassUrnTest() throws ValidationException {
    // Valid class level ITIP URN
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:itip:2345678901.123.67.87.*");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:itip:8585885039.463.36.44.*");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:itip:43848343.74387.45.64.*");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:itip:164759476414.5.56.34.*");
  }
}
