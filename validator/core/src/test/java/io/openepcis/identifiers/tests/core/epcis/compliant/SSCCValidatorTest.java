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
class SSCCValidatorTest {
  // Test for invalid EPC URN identifiers.
  @Test
  @Order(1)
  void invalidEpcUrnTest() throws ValidationException {
    // SSCC with less than 18 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sscc:123456789.0666386");

    // SSCC with more than 18 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sscc:123456789.066638689");

    // SSCC with GCP less than 6 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sscc:12345.066638689");

    // SSCC with GCP more than 12 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sscc:7438473847381.7483");

    // SSCC with invalid characters in GCP
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sscc:12345A.066638689");

    // SCC with invalid characters in SSCC
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sscc:123456.0666386898A");

    // SSCC with invalid syntax
    ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc::sscc:123456.066638689");
  }

  // Test for valid EPC URN identifiers.
  @Test
  @Order(2)
  void validEpcUrnTest() throws ValidationException {
    // Valid SSCC
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sscc:123456.06663868985");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sscc:743847384738.47483");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sscc:587584.77584578485");
    ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sscc:9403940349.9034903");
  }

  // Test for invalid EPC URI identifiers.
  @Test
  @Order(3)
  void invalidEpcUriTest() throws ValidationException {
    // SSCC URI with more than 18 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/00/0123456789012345678", 6);

    // SSCC URI with less than 18 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/00/01234567890123456", 6);

    // SSCC URI with invalid characters in SSCC
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/00/01234567890123456A", 6);

    // SSCC with GCP Length more than 12 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/00/012345678901234567", 13);

    // SSCC with GCP Length less than 6 digits
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/00/012345678901234567", 5);
  }

  // Test for valid EPC URI identifiers.
  @Test
  @Order(4)
  void validEpcUriTest() throws ValidationException {
    // Valid SSCC URI
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/00/012345678901234567", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://marriot.in/blr/123/00/012345678901234567", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://marriot.in/blr/123/00/994039403490349033", 10);
    ApplicationIdentifierValidationTestUtil.assertValid("https://marriot.in/blr/123/00/758758475845784857", 6);
    ApplicationIdentifierValidationTestUtil.assertValid("https://marriot.in/blr/123/00/936658475845784857", 12);
    ApplicationIdentifierValidationTestUtil.assertValid("https://marriot.in/blr/123/00/893594046673734737", 12);
  }

  // Test for valid EPC URI identifiers.
  @Test
  @Order(5)
  void validateCheckDigitTest() throws ValidationException {
    final ValidationContext validationContext = ValidationContext.builder().validateCheckDigit(true).gcpLength(10).build();

    // Valid Check Digit
    ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/00/012345678901234560", validationContext);
    ApplicationIdentifierValidationTestUtil.assertValid("https://marriot.in/blr/123/00/012345678901232344", validationContext);
    ApplicationIdentifierValidationTestUtil.assertValid("https://marriot.in/blr/123/00/994039403490349033", validationContext);

    // Invalid Check Digit
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://marriot.in/blr/123/00/012345678901234561", validationContext);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://marriot.in/blr/123/00/012345678901232345", validationContext);
    ApplicationIdentifierValidationTestUtil.assertInvalid("https://marriot.in/blr/123/00/994039403490349035", validationContext);
  }
}
