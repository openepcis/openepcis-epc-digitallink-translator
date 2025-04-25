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
class GSRNPValidatorTest {
  @Test
  @Order(1)
  void invalidGSRNPUrnTest() throws ValidationException {
    // Invalid GSRNP syntax
    ApplicationIdentifierValidationTestUtil.assertInvalid("un:epc:id:gsrnp:1234567890.1234567");

    // GSRNP with invalid characters in GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsrnp:123456789A.1234567");

    // GSRNP with more than 12 digits GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsrnp:1234567890123.1234567");

    // GSRNP with more than 18 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsrnp:1234567890.123456712");

    // GSRNP with less than 18 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsrnp:1234567890.123456712");

    // GSRNP without GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsrnp:1234567890123456712");
  }

  @Test
  @Order(2)
  void validGSRNPUrnTest() throws ValidationException {
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gsrnp:1234567890.1234567");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gsrnp:843984.93439439493");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gsrnp:578457847548.75847");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gsrnp:3454254352.3524352");
  }

  @Test
  @Order(3)
  void invalidGSRNPUriTest() throws ValidationException {
    // GSRNP URI with invalid character
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8017/12345678901234567A", 10);

    // GSRNP URI with more than 18 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8017/1234567890123456789", 10);

    // GSRNP URI with less than 18 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8017/12345678901234567", 10);

    // GSRNP URI with GCP more than 12
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8017/123456789012345678", 13);

    // GSRNP URI with GCP less than 6
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8017/123456789012345678", 5);
  }

  @Test
  @Order(4)
  void validGSRNPUriTest() throws ValidationException {
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8017/123456789012345678", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://google.com/8017/123456789012345678", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://google.com/8017/643963764374736343", 9);
    ApplicationIdentifierValidationTestUtil.assertValid("https://google.com/8017/645933932413121231", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://google.com/8017/667602231213121237", 12);
  }
}
