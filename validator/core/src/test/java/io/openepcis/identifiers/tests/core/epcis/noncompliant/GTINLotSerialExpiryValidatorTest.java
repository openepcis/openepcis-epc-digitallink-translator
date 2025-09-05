package io.openepcis.identifiers.tests.core.epcis.noncompliant;

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.tests.core.epcis.ApplicationIdentifierValidationTestUtil;
import io.openepcis.identifiers.validator.ValidationContext;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

class GTINLotSerialExpiryValidatorTest {

    // Test for invalid URI identifiers
    @Test
    @Order(0)
    void invalidUriTest() throws ValidationException {
        // Invalid Expiry Date format
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788/10/ABC1/21/12345?17=55", false, 10);

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

    // Test for validating Check digit
    @Test
    @Order(2)
    void validateCheckDigit() throws ValidationException {
        final ValidationContext validationContext = ValidationContext.builder().gcpLength(10).epcisCompliant(false).build();

        // Valid Check digit
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/07836491528371/10/1/21/12345?17=180426", validationContext);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/10472958163095/10/ABC1/21/12345?17=000101", validationContext);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/11287593460121/10/ABC1/21/12345?17=000101", validationContext);

        // Invalid Check digit
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/07836491528376/10/1/21/12345?17=180426", validationContext);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/10472958163092/10/ABC1/21/12345?17=000101", validationContext);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/11287593460125/10/ABC1/21/12345?17=000101", validationContext);
    }
}
