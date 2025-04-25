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
class GSRNValidatorTest {

  @Test
  @Order(1)
  void invalidGSRNUrnTest() throws ValidationException {
    // GSRN with invalid characters in gcp
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsrn:1234567A90.1234567");

    // GSRN with less than 18 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsrn:1234567890.123456");

    // GSRN with more than 18 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsrn:1234567890.12345678");

    // GSRN without GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:gsrn:123456789012345678");
  }

  @Test
  @Order(2)
  void validGSRNUrnTest() throws ValidationException {
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gsrn:1234567890.1234567");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gsrn:142512.45142152511");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gsrn:673674637437.47783");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:gsrn:654683828.92302309");
  }

  @Test
  @Order(3)
  void invalidGSRNUriTest() throws ValidationException {
    // GSRN URI with more than 18 characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8018/1234567890123456751", 10);

    // GSRN URI with less than 18 characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8018/12345678901234567", 10);

    // GSRN URI with invalid characters
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8018/12345678901234567A", 10);

    // GSRN URI with invalid GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/8018/123456789012345675", 5);
  }

  @Test
  @Order(4)
  void validGSRNUriTest() throws ValidationException {
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/8018/123456789012345675", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://hp.com/laptop/8018/123456789012345675", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://hp.com/laptop/8018/301037675245142514", 12);
    ApplicationIdentifierValidationTestUtil.assertValid("https://hp.com/laptop/8018/302241753828392839", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://hp.com/laptop/8018/654673483739829829", 11);
  }
}
