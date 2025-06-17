package io.openepcis.identifiers.validator.core.util;

import io.openepcis.core.exception.ValidationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

import static io.openepcis.constants.ApplicationIdentifierConstants.*;

/**
 * Utility for validating GS1 AI check digits in Digital Link URIs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckDigitValidator {

    private static void validate(final String uri, final String aiPrefix, final int payloadLength, final String elementName) {
        final int idx = uri.indexOf(aiPrefix);

        // Check if the prefix is present in the URI
        if (idx < 0) {
            throw new ValidationException(elementName + " prefix not found in: " + uri);
        }

        final int start = idx + aiPrefix.length();
        final int end = start + payloadLength + 1;

        // Check if the segment is long enough
        if (end > uri.length()) {
            throw new ValidationException(elementName + " segment too short (" + payloadLength + "+1 digits) in: " + uri);
        }


        final String segment = uri.substring(start, end);
        final String data = segment.substring(0, payloadLength);
        final char checksumChar = UPCEANLogicImpl.calcChecksum(data);

        // compute the expected and actual check digit
        final int expected = Character.getNumericValue(checksumChar);
        final int actual = segment.charAt(payloadLength) - '0';

        if (expected != actual) {
            throw new ValidationException(String.format("%s has invalid check digit: expected %d but found %d in %s", elementName, expected, actual, uri));
        }
    }

    /**
     * Validate GTIN (AI /01/, 13-digit + check digit).
     */
    public static void validateGTIN(final String uri) throws ValidationException {
        validate(uri, SGTIN_AI_URI_PREFIX, 13, "GTIN");
    }

    /**
     * Validate GLN/SGLN (AI /414/, 12-digit + check digit).
     */
    public static void validateGLN(final String uri) throws ValidationException {
        validate(uri, SGLN_AI_URI_PREFIX, 12, "GLN");
    }

    /**
     * Validate PGLN (AI /417/, 12-digit + check digit).
     */
    public static void validatePGLN(final String uri) throws ValidationException {
        validate(uri, PGLN_AI_URI_PREFIX, 12, "PGLN");
    }

    /**
     * Validate SSCC (AI /00/, 17-digit + check digit).
     */
    public static void validateSSCC(final String uri) throws ValidationException {
        validate(uri, SSCC_AI_URI_PREFIX, 17, "SSCC");
    }

    /**
     * Validate GSIN (AI "/402/", 16-digit + check digit).
     */
    public static void validateGSIN(final String uri) throws ValidationException {
        validate(uri, GSIN_AI_URI_PREFIX, 16, "GSIN");
    }

    /**
     * Validate GRAI (AI "/8003/", 12-digit + check digit).
     */
    public static void validateGRAI(final String uri) throws ValidationException {
        validate(uri, GRAI_AI_URI_PREFIX, 12, "GRAI");
    }

    /**
     * Validate GSRN (AI "/8018/", 17-digit + check digit).
     */
    public static void validateGSRN(final String uri) throws ValidationException {
        validate(uri, GSRN_AI_URI_PREFIX, 17, "GSRN");
    }

    /**
     * Validate GDTI (AI "/253/", 12-digit + check digit).
     */
    public static void validateGDTI(final String uri) throws ValidationException {
        validate(uri, GDTI_AI_URI_PREFIX, 12, "GDTI");
    }

    /**
     * Validate GCN (AI "/255/", 12-digit + check digit).
     */
    public static void validateGCN(final String uri) throws ValidationException {
        validate(uri, GCN_AI_URI_PREFIX, 12, "GCN");
    }

}
