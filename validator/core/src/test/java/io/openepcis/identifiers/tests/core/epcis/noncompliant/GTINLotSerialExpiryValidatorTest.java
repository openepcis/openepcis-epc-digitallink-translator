package io.openepcis.identifiers.tests.core.epcis.noncompliant;

import io.openepcis.identifiers.tests.core.epcis.ApplicationIdentifierValidationTestUtil;
import io.openepcis.identifiers.validator.exception.ValidationException;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

class GTINLotSerialExpiryValidatorTest {

    // Test for invalid URI identifiers
    @Test
    @Order(0)
    void invalidUriTest() throws ValidationException {
        // Without Expiry Date
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788/10/ABC1/21/12345", false, 10);

        // Without GTIN
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/10/ABC1/21/12345?17=180426", false, 10);

        // Without Domain
        ApplicationIdentifierValidationTestUtil.assertInvalid("/01/09520123456788/10/ABC1/21/12345?17=180426", false, 10);

        // Without Lot Number
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788?17=180426", false, 10);

        // Without Serial Number
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788/10/1/21/12345?17=180426/23/123", false, 12);

        // Invalid GCP
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788/10/1/21/12345?17=180426", false, 13);

        // EPCIS Compliant true with valid
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788/10/1/21/12345?17=180426", true, 10);
    }

    // Test for valid URI identifiers
    @Test
    @Order(1)
    void validUriTest() throws ValidationException {
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788/10/1/21/12345?17=180426", false, 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788/10/1/21/12345?17=180426", false, 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123435678/10/ABC1/21/12345?17=000101", false, 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123435674/10/ABC1/21/12345?17=000101", false, 10);
    }

}
