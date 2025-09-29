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

        // /10 value too long
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788/10/THIS_IS_OVER_20_CHARS_LONG", true, 10);

        // /21 value contains disallowed chars
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788/21/invalid~char", false, 10);

        // Duplicate /10
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788/10/L1/10/L2", false, 10);

        // Bad date formats
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788/10/L?17=2025-09-30", false, 10);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788/21/S?11=251332", false, 10);
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/01/09520123456788/10/L?15=250000", false, 10);
    }

    @Test
    void test(){
        ApplicationIdentifierValidationTestUtil.assertValid("http://localhost:8080/01/09520000000028/21/1234?17=25-02-04", false, 10);
    }

    // Test for valid URI identifiers
    @Test
    @Order(1)
    void validUriTest() throws ValidationException {
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788/10/1/21/12345?17=180426", false, 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788/10/1/21/12345?17=180426", false, 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123435678/10/ABC1/21/12345?17=000101", false, 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123435674/10/ABC1/21/12345?17=000101", false, 10);

        // GTIN + lot only
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788/10/LOT1", false, 10);

        // GTIN + serial only
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788/10/LOT1", false, 10);

        // GTIN + serial only
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788/10/AB/21/XYZ", false, 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788/21/XYZ/10/AB", false, 10);

        // GTIN + lot + multiple dates
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788/10/ABCD?11=250101&17=251231", false, 10);

        // GTIN + serial + several dates
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/01/09520123456788/21/ZZ9?12=241231&13=241001&15=241220", false, 10);

        // With 15
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.dev.epcis.cloud/01/04006784292842/10/240?15=251010", false, 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.dev.epcis.cloud/01/04006784292842/21/240?15=251010", false, 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.dev.epcis.cloud/01/04006784292842/10/240?17=251010", false, 10);

        // With 16
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.dev.epcis.cloud/01/04006784292842/10/240?16=251010", false, 10);

        // With 17
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.dev.epcis.cloud/01/04006784292842/10/240/21/00001?17=250930", false, 10);

        //With any date as path parameter
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.dev.epcis.cloud/01/04006784292842/10/240/17/51010", false, 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.dev.epcis.cloud/01/04006784292842/10/240/15/51010", false, 10);
        ApplicationIdentifierValidationTestUtil.assertValid("https://id.dev.epcis.cloud/01/04006784292842/10/240/16/51010/17/51010", false, 10);


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
