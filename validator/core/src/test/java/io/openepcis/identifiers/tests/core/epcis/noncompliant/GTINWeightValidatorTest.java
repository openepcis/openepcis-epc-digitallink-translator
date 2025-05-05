package io.openepcis.identifiers.tests.core.epcis.noncompliant;

import io.openepcis.identifiers.tests.core.epcis.ApplicationIdentifierValidationTestUtil;
import io.openepcis.identifiers.validator.ValidationContext;
import io.openepcis.identifiers.validator.exception.ValidationException;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

class GTINWeightValidatorTest {
    // Test for invalid URI identifiers
    @Test
    @Order(0)
    void invalidUriTest() throws ValidationException {
        // Without Net Weight
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://example.com/01/09520123456788?3103=", false, 10);

        // Without GTIN
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://example.com/01/?3103=000195", false, 10);

        // Without Domain
        ApplicationIdentifierValidationTestUtil.assertInvalid("/01/09520123456788?3103=000195", false, 10);

        // With extra digit in SGTIN
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://example.com/01/095420123456788?3103=000195", false, 10);

        // Invalid GCP length
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://example.com/01/09520123456788?3103=000195", false, 5);

        // EPCIS Compliant true with valid
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://example.com/01/09520123456788?3103=000195", true, 10);

    }


    // Test for valid URI identifiers
    @Test
    @Order(1)
    void validUriTest() throws ValidationException {
        ApplicationIdentifierValidationTestUtil.assertValid("https://example.com/01/09520123456788?3103=000195", false, 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://example.com/01/09520123456788?3103=123456", false, 12);
    }

    // Test for validating Check digit
    @Test
    @Order(2)
    void validateCheckDigit() throws ValidationException {
        final ValidationContext validationContext = ValidationContext.builder().gcpLength(10).epcisCompliant(false).build();

        // Valid Check digit
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/07836491528371?3103=000195", validationContext);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/10472958163095?3103=123456", validationContext);
        ApplicationIdentifierValidationTestUtil.assertValid("https://example.com/01/11287593460121?3103=000195", validationContext);

        // Invalid Check digit
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/07836491528376?3103=000195", validationContext);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/10472958163092?3103=123456", validationContext);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://example.com/01/11287593460125?3103=000195", validationContext);
    }

}
