package io.openepcis.identifiers.tests.core.epcis.noncompliant;

import io.openepcis.identifiers.tests.core.epcis.ApplicationIdentifierValidationTestUtil;
import io.openepcis.identifiers.validator.exception.ValidationException;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

class GTINWeightAmountBestBeforeValidatorTest {

    // Test for invalid URI identifiers
    @Test
    @Order(0)
    void invalidUriTest() throws ValidationException {
        // Without Expiry Date
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788&3103=000195&3922=0299", false, 10);

        // Without GTIN
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/?17=201225&3103=000195&3922=0299", false, 10);

        // Without Domain
        ApplicationIdentifierValidationTestUtil.assertInvalid("/01/09520123456788?17=201225&3103=000195&3922=0299", false, 10);

        // Without Net Weight
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788&3922=0299&17=201225", false, 10);

        // With extra parameter
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788?17=201225&3103=000195&3922=0299?22=123", false, 12);

        // Invalid GCP length
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788?17=201225&3103=000195&3922=0299", false, 13);

        // EPCIS Compliant true with valid
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788?3103=000195&3922=0299&17=201225", true, 10);
    }

    // Test for valid URI identifiers
    @Test
    @Order(1)
    void validUriTest() throws ValidationException {
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788?3103=000195&3922=0299&17=201225", false, 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788?17=201225&3103=000195&3922=0299", false, 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788?17=201225&3103=000195&3922=0299", false, 12);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788?3103=000195&3922=0299&17=201225", false, 6);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788?3922=0299&3103=000195&17=201225", false, 6);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788?3103=000195&17=201225&3922=0299", false, 6);
    }
}
