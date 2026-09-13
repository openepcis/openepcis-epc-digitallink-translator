/*
 * Copyright 2022-2026 benelog GmbH & Co. KG
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package io.openepcis.identifiers.tests.core.util;

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.validator.core.util.CheckCharacterPairValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the check character pair against the test vectors carried by GS1's own reference
 * linter {@code gs1_lint_csumalpha} in the GS1 Syntax Dictionary. These are GS1's vectors,
 * not ones invented here — the point is that our transcription agrees with the reference on
 * every case the reference itself checks, including the awkward ones (two-character input,
 * the full CSET 82 repertoire, and the 99-character maximum).
 */
@DisplayName("GS1 alphanumeric check character pair")
class CheckCharacterPairValidatorTest {

    private static final String NAME = "GMN";

    private static final String[] VALID = {
            "22", "!22", "!!22",
            "1987654Ad4X4bL5ttr2310c2K",
            "12345678901234567890123NT",
            "12345_ABCDEFGHIJKLMCP",
            "12345_NOPQRSTUVWXYZDN",
            "12345_abcdefghijklmN3",
            "12345_nopqrstuvwxyzP2",
            "12345_!\"%&'()*+,-./LC",
            "12345_0123456789:;<=>?62",
            "7907665Bm8v2AB", "97850l6KZm0yCD", "225803106GSpEF", "149512464PM+GH",
            "62577B8fRG7HJK", "515942070CYxLM", "390800494sP6NP", "386830132uO+QR",
            "53395376X1:nST", "957813138Sb6UV", "530790no0qOgWX", "62185314IvwmYZ",
            "23956qk1&dB!23", "794394895ic045", "57453Uq3qA<H67", "0881063PhHvY89",
            "00000!HV",
            "99999zzzzzzzzzzzzzzzzzzT2",
            // 99 characters: the reference implementation's maximum
            "12345678901234567890123456789012345678901234567890"
                    + "12345678901234567890123456789012345678901234567HA",
            // maximum sum the 97-prime implementation can produce
            "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"
                    + "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzUA"
    };

    @Test
    @DisplayName("accepts every valid vector from GS1's reference linter")
    void acceptsReferenceVectors() {
        for (final String v : VALID) {
            assertDoesNotThrow(() -> CheckCharacterPairValidator.validate(v, NAME),
                    "GS1 reference accepts \"" + v + "\" but we rejected it");
        }
    }

    @Test
    @DisplayName("rejects a wrong check character pair")
    void rejectsWrongPair() {
        // len 2 must be exactly "22"
        assertThrows(ValidationException.class, () -> CheckCharacterPairValidator.validate("33", NAME));
        // first and second check character each wrong in turn
        assertThrows(ValidationException.class, () -> CheckCharacterPairValidator.validate("1987654Ad4X4bL5ttr2310cXK", NAME));
        assertThrows(ValidationException.class, () -> CheckCharacterPairValidator.validate("1987654Ad4X4bL5ttr2310c2X", NAME));
        assertThrows(ValidationException.class, () -> CheckCharacterPairValidator.validate("12345678901234567890123 T", NAME));
        assertThrows(ValidationException.class, () -> CheckCharacterPairValidator.validate("12345678901234567890123N ", NAME));
    }

    @Test
    @DisplayName("rejects characters outside CSET 82 and out-of-range lengths")
    void rejectsBadInput() {
        // space is not in CSET 82, in leading, middle and trailing data position
        assertThrows(ValidationException.class, () -> CheckCharacterPairValidator.validate(" 2345678901234567890123NT", NAME));
        assertThrows(ValidationException.class, () -> CheckCharacterPairValidator.validate("123456789 1234567890123NT", NAME));
        assertThrows(ValidationException.class, () -> CheckCharacterPairValidator.validate("1234567890123456789012 NT", NAME));
        // too short to carry a pair
        assertThrows(ValidationException.class, () -> CheckCharacterPairValidator.validate("", NAME));
        assertThrows(ValidationException.class, () -> CheckCharacterPairValidator.validate("2", NAME));
        assertThrows(ValidationException.class, () -> CheckCharacterPairValidator.validate(null, NAME));
        // 100 characters exceeds the 97-prime implementation
        assertThrows(ValidationException.class, () -> CheckCharacterPairValidator.validate(
                "12345678901234567890123456789012345678901234567890"
                        + "123456789012345678901234567890123456789012345678ZZ", NAME));
    }

    /**
     * CSET 82 contains I and O; CSET 32 does not. Getting this backwards is the classic
     * transcription error, so it is asserted directly rather than left to the vectors.
     */
    @Test
    @DisplayName("CSET 82 includes I and O, and has 82 members")
    void cset82IncludesIAndO() {
        assertTrue(CheckCharacterPairValidator.cset82ValueOf('I') >= 0, "I must be in CSET 82");
        assertTrue(CheckCharacterPairValidator.cset82ValueOf('O') >= 0, "O must be in CSET 82");
        assertEquals(-1, CheckCharacterPairValidator.cset82ValueOf(' '), "space must not be in CSET 82");
        assertEquals(-1, CheckCharacterPairValidator.cset82ValueOf('#'), "# must not be in CSET 82");
        // exact repertoire: values 0..81 assigned, nothing else
        int members = 0;
        for (char c = 0; c < 128; c++) {
            if (CheckCharacterPairValidator.cset82ValueOf(c) >= 0) {
                members++;
            }
        }
        assertEquals(82, members, "CSET 82 must have exactly 82 members");
        // spot-check the boundaries of the ordering
        assertEquals(0, CheckCharacterPairValidator.cset82ValueOf('!'));
        assertEquals(81, CheckCharacterPairValidator.cset82ValueOf('z'));
    }
}
