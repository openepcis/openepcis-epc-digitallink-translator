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
class UPUIValidatorTest {
  // Test for invalid EPC URN identifiers.
  @Test
  @Order(1)
  void invalidEpcUrnTest() throws ValidationException {
    // UPUI with less than 14 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:upui:234567.189012.1111");
    // UPUI with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:upui:234567.189012A.1111");
    // UPUI with more than 14 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:upui:234567.18901234.1111");
    // UPUI without serial number
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:upui:234567.18901234");
  }

  // Test for valid EPC URN identifiers.
  @Test
  @Order(2)
  void validEpcUrnTest() throws ValidationException {
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:upui:234567.1890123.1111ANC");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:upui:857458457485.7.!\"%&'()*+,-./");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:upui:787587.5483743.19:;<=>?AZ_az");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:upui:857834.7838473.8398439");
  }

  // Test for invalid EPC URI identifiers.
  @Test
  @Order(3)
  void invalidEpcUriTest() throws ValidationException {
    // UPUI URI with less than 14 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/1234567890123/235/1111ANC", 10);
    // UPUI URI with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/1234567890123A/235/1111ANC", 10);
    // UPUI URI without serial numbers
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/12345678901231/235/", 10);
    // UPUI with invalid GCP Length
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/12345678901231/235/1111ANC", 13);
  }

  // Test for valid EPC URI identifiers.
  @Test
  @Order(4)
  void validEpcUriTest() throws ValidationException {
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/12345678901231/235/1111ANC", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://maps.google.com.in.de.00/12/01/12345678901231/235/1111ANC", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/78574584574857/235/!\"%&'()*+,-./", 12);
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/57875874837438/235/19:;<=>?AZ_az", 6);
  }
}
