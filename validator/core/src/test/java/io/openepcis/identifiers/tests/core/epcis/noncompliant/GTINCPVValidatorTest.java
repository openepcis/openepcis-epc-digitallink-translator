package io.openepcis.identifiers.tests.core.epcis.noncompliant;

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.tests.core.epcis.ApplicationIdentifierValidationTestUtil;
import io.openepcis.identifiers.validator.ValidationContext;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

class GTINCPVValidatorTest {
    // Test for invalid URI identifiers
    @Test
    @Order(0)
    void invalidUriTest() throws ValidationException {
        // Without CPV
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788/22/", false, 10);

        // Without GTIN
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/22/2A", false, 10);

        // Without Domain
        ApplicationIdentifierValidationTestUtil.assertInvalid("/01/09520123456788/22/22A", false, 10);

        // With extra digit in SGTIN
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/095201234567884/22/22A", false, 10);

        // Invalid GCP length
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456784/22/22A", false, 5);

        // EPCIS Compliant true with valid
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456784/22/22A", true, 10);
    }


    // Test for valid URI identifiers
    @Test
    @Order(1)
    void validUriTest() throws ValidationException {
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456784/22/22A", false, 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456784/22/422A", false, 12);
    }

    // Test for validating Check digit
    @Test
    @Order(2)
    void validateCheckDigit() throws ValidationException {
        final ValidationContext validationContext = ValidationContext.builder().gcpLength(10).epcisCompliant(false).build();

        // Valid Check digit
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/07836491528371/22/22BDA", validationContext);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/10472958163095/22/422A", validationContext);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/11287593460121/22/2AS2A", validationContext);

        // Invalid Check digit
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/07836491528376/22/22A", validationContext);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/10472958163092/22/422A", validationContext);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/11287593460125/22/22A", validationContext);
    }
}
