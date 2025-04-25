package io.openepcis.identifiers.tests.core.epcis.noncompliant;

import io.openepcis.identifiers.tests.core.epcis.ApplicationIdentifierValidationTestUtil;
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

}
