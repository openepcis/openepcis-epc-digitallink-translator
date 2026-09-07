/*
 * Copyright (c) 2022-2026 benelog GmbH & Co. KG
 * All rights reserved.
 *
 * Unauthorized copying, modification, distribution,
 * or use of this work, via any medium, is strictly prohibited.
 *
 * benelog GmbH & Co. KG reserves all rights not expressly granted herein,
 * including the right to sell licenses for using this work.
 */
package io.openepcis.identifiers.validator.core.util;

import io.openepcis.core.exception.ValidationException;

/**
 * GS1 alphanumeric check character pair, used by AI 8013 (GMN).
 *
 * <p>A different algorithm from {@link CheckDigitValidator}'s MOD-10 check digit: the data
 * characters are valued by their position in CSET 82, weighted by ascending primes from the
 * right, summed modulo 1021, and that 10-bit result is split into two 5-bit halves which
 * index CSET 32 to give the two trailing check characters.
 *
 * <p>Transcribed from GS1's reference linter {@code gs1_lint_csumalpha} in the
 * <a href="https://github.com/gs1/gs1-syntax-dictionary">GS1 Syntax Dictionary</a>, and
 * verified against that implementation's own test vectors in
 * {@code CheckCharacterPairValidatorTest}. Two details are easy to get wrong and are worth
 * naming: CSET 82 <em>does</em> contain {@code I} and {@code O} (only CSET 32 omits them),
 * and the smallest prime weights the <em>rightmost</em> data character.
 */
public final class CheckCharacterPairValidator {

    /** CSET 32, indexed by check character value 0-31. Omits I and O. */
    private static final String CSET_32 = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    /** CSET 82, indexed by data character value 0-81. */
    private static final String CSET_82 =
            "!\"%&'()*+,-./0123456789:;<=>?ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz";

    /** Character -> CSET 82 value, or -1 when the character is not in CSET 82. */
    private static final int[] CSET_82_VALUES = new int[128];

    /**
     * Ascending primes; index 0 weights the rightmost data character. GS1's reference
     * carries the first 97 because no AI exceeds 99 characters; GMN needs only 23 of them
     * (25 characters less the check pair), and the extra headroom costs nothing.
     */
    private static final int[] PRIMES = {
            2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37,
            41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89,
            97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151,
            157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223,
            227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281,
            283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359,
            367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433,
            439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503,
            509
    };

    static {
        java.util.Arrays.fill(CSET_82_VALUES, -1);
        for (int i = 0; i < CSET_82.length(); i++) {
            CSET_82_VALUES[CSET_82.charAt(i)] = i;
        }
    }

    private CheckCharacterPairValidator() {
    }

    /** The CSET 82 value of {@code c}, or -1 when {@code c} is not a CSET 82 character. */
    public static int cset82ValueOf(final char c) {
        return c < CSET_82_VALUES.length ? CSET_82_VALUES[c] : -1;
    }

    /**
     * Verify that the last two characters of {@code value} are its correct check character
     * pair.
     *
     * @param value       the complete component including its trailing check character pair
     * @param elementName identifier name used in the error message (e.g. {@code "GMN"})
     * @throws ValidationException if the value is too short or too long, contains a
     *                             character outside CSET 82, or carries the wrong pair
     */
    public static void validate(final String value, final String elementName) throws ValidationException {
        if (value == null || value.length() < 2) {
            throw new ValidationException(String.format("%s is too short to carry a check character pair: %s", elementName, value));
        }
        if (value.length() > PRIMES.length + 2) {
            throw new ValidationException(String.format("%s is too long for the check character pair algorithm (max %d): %s", elementName, PRIMES.length + 2, value));
        }

        final int dataLength = value.length() - 2;
        long sum = 0;
        for (int pos = 0; pos < dataLength; pos++) {
            final char c = value.charAt(pos);
            final int charValue = cset82ValueOf(c);
            if (charValue < 0) {
                throw new ValidationException(String.format("%s contains a character outside CSET 82 ('%s' at position %d): %s", elementName, c, pos + 1, value));
            }
            // The smallest prime weights the rightmost data character.
            sum += (long) charValue * PRIMES[dataLength - 1 - pos];
        }
        final int checksum = (int) (sum % 1021);

        final char expectedFirst = CSET_32.charAt(checksum >> 5);
        final char expectedSecond = CSET_32.charAt(checksum & 31);
        final char actualFirst = value.charAt(value.length() - 2);
        final char actualSecond = value.charAt(value.length() - 1);

        if (actualFirst != expectedFirst || actualSecond != expectedSecond) {
            throw new ValidationException(String.format(
                    "%s has invalid check character pair: expected \"%s%s\" but found \"%s%s\" in %s",
                    elementName, expectedFirst, expectedSecond, actualFirst, actualSecond, value));
        }
    }
}
