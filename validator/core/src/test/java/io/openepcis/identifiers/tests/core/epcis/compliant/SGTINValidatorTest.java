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
class SGTINValidatorTest {
    // Test for invalid EPC URN identifiers.
    @Test
    @Order(1)
    void invalidEpcUrnTest() throws ValidationException {
        // No Serial Number
        ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgtin:234567.1890123");

        // SGTIN less than 14 digits
        ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgtin:234567.189012.1234");

        // SGTIN with more than 14 digits
        ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgtin:234567.18901234.00");

        // SGTIN with invalid characters in GCP
        ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgtin:23A567.18012.1234");

        // SGTIN with invalid characters in GTIN
        ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgtin:234567.18A012.1234");

        // SGTIN with GCP less than 6 digits
        ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgtin:23567.18012.1234");

        // SGTIN with GCP more than 12 digits
        ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:id:sgtin:2356789012345.18012.1234");
    }

    // Test for valid EPC URN identifiers.
    @Test
    @Order(2)
    void validEpcUrnTest() throws ValidationException {
        // Valid SGTIN
        ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sgtin:234567890.1123.9999");
        ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sgtin:234567.1890123.!\"%&'()*+,-./");
        ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sgtin:234567.1890123./19:;<=>?AZ_az");
        ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sgtin:387583.7784374.9302932");
        ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:id:sgtin:387583784374.7.9302932");
    }

    // Test for invalid EPC URI identifiers.
    @Test
    @Order(3)
    void invalidEpcUriTest() throws ValidationException {
        // SGTIN URI with SGTIN less than 14 digit
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/1234567890123/21/9999", 6);

        // SGTIN URI with SGTIN more than 14 digit
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/123456789012312/21/9999", 6);

        // SGTIN URI with SGTIN without serial number
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/123456789012312", 6);

        // SGTIN URI with GCP more than 12 digits
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/12345678901231/21/9090", 13);
    }

    // Test for valid EPC URI identifiers.
    @Test
    @Order(4)
    void validEpcUriTest() throws ValidationException {
        // Valid SGTIN
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/12356789080128/21/1234", 6);
        ApplicationIdentifierValidationTestUtil.assertValid("https://lidl.de/food/frozen/01/12356789080128/21/1234", 6);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/12345678901231/21/!\"%&'()*+,-./", 6);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/12345678901231/21//19:;<=>?AZ_az", 6);
        ApplicationIdentifierValidationTestUtil.assertValid("https://lidl.de/food/frozen/01/59046539203740/21/9302932", 12);
        ApplicationIdentifierValidationTestUtil.assertValid("https://lidl.de/food/frozen/01/60085859894506/21/94304903", 10);
    }

    // Test for invalid class-level URN identifiers.
    @Test
    @Order(5)
    void invalidClassUrnTest() throws ValidationException {
        // Invalid characters in GTIN
        ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:sgtin:234567.A189012.*");

        // Invalid prefix for GTIN
        ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:gtin:8588588.858545.*");

        // Invalid GTIN with more than 14 digits
        ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:sgtin:8588588.8585451.*");

        // Invalid GTIN with less than 14 digits
        ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:sgtin:8588588.85854.*");

        // Invalid GTIN with invalid GCP length
        ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:sgtin:8588588.85854.*");

        // Invalid GTIN without *
        ApplicationIdentifierValidationTestUtil.assertInvalid("urn:epc:idpat:sgtin:88538.4838493.*");
    }

    // Test for valid class-level URN identifiers.
    @Test
    @Order(6)
    void validClassUrnTest() throws ValidationException {
        // Valid GTIN
        ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:sgtin:234567.1890123.*");
        ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:sgtin:858858858545.8.*");
        ApplicationIdentifierValidationTestUtil.assertValid("urn:epc:idpat:sgtin:93593755.88545.*");
    }

    // Test for invalid class-level URI identifiers.
    @Test
    @Order(7)
    void invalidClassUriTest() throws ValidationException {
        // Invalid characters in GTIN
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/13345A78901432");

        // More than 14 digits in GTIN
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/123456789012345");

        // Less than 14 digits in GTIN
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/1234567890123");
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/1234567890123", 12);

        // Serial Numbers in GTIN
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/12345678901235/1234");
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/12345678901235/1234", 10);

        // Throw error if GCP is returned as 0 when no provided
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/02045678901234");
    }

    // Test for valid class-level URI identifiers.
    @Test
    @Order(8)
    void validClassUriTest() throws ValidationException {
        // Valid GTIN
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/88853849384934", 6);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/85394839489381", 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/93489348394895", 12);
    }

    // Test for GTIN DL Instance-Level URI with check digit validation
    @Test
    @Order(9)
    void validateEpcUriWithCheckDigit() throws ValidationException {
        final ValidationContext validationContext = ValidationContext.builder().validateCheckDigit(true).gcpLength(10).build();

        // Valid GTIN
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/88853849384934/21/1234", validationContext);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09312345678907/21/!\"%&'()*+,-./", validationContext);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/50999888777669/21//19:;<=>?AZ_az", validationContext);

        // Invalid check digit
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/88853849384936/21/1234", validationContext);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09312345678908/21/!\"%&'()*+,-./", validationContext);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/50999888777660/21//19:;<=>?AZ_az", validationContext);

    }

    // Test for GTIN DL Class-Level URI with check digit validation
    @Test
    @Order(10)
    void validateClassUriWithCheckDigit() throws ValidationException {
        final ValidationContext validationContext = ValidationContext.builder().validateCheckDigit(true).gcpLength(10).build();

        // Valid GTIN
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/88853849384934", validationContext);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09312345678907", validationContext);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/50999888777669", validationContext);

        // Invalid check digit
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/88853849384936", validationContext);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09312345678908", validationContext);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/50999888777660", validationContext);

        // GTIN not 14 digits
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/888538493844934", validationContext);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/0931234567907", validationContext);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/5099988877669", validationContext);


    }
}
