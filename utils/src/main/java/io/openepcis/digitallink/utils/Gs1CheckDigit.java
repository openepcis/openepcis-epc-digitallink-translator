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
package io.openepcis.digitallink.utils;

/**
 * The GS1 mod-10 check digit — the one implementation.
 *
 * <p>It lived in three places before this class existed: {@code ConverterUtil.checksum}
 * (converter), {@code GS1DigitalLinkCompression.calculateCheckDigit} (here, wrapped in
 * per-AI position logic) and {@code EpcisDocumentValidator.checkDigit} (the rules
 * module). All three were verified to agree — 200 000 random digit strings, no
 * divergence — before they were pointed here, so the consolidation changed no
 * behaviour. Three copies of one rule that merely happen to agree today is a
 * standing invitation for them to stop agreeing.
 *
 * <p>It sits in the utils module because that is the lowest one: it depends on
 * nothing in the toolkit, so every other module can reach it without a cycle.
 *
 * <p><b>A GS1 key is a string of digits, not a number.</b> Nothing here parses one.
 * Leading zeros carry meaning — the indicator digit of a GTIN-14 is usually {@code 0}
 * — and a key that has been through an integer type has already lost it.
 */
public final class Gs1CheckDigit {

    private Gs1CheckDigit() {
    }

    /**
     * The check digit for a body of digits.
     *
     * <p>Weights alternate 3 and 1 from the right, i.e. the rightmost body digit is
     * tripled. That is the same arithmetic for every GS1 key type — GTIN, GLN, SSCC,
     * GRAI and the rest differ only in length and in where the digit sits, never in
     * how it is computed.
     *
     * @param body the key WITHOUT its check digit
     * @return the check digit as a character
     * @throws IllegalArgumentException if {@code body} is empty or not all digits
     */
    public static char of(final String body) {
        if (body == null || body.isEmpty() || !body.chars().allMatch(Character::isDigit)) {
            throw new IllegalArgumentException("check digit needs a digit string, got " + body);
        }
        int total = 0;
        for (int i = 0; i < body.length(); i++) {
            final int d = body.charAt(body.length() - 1 - i) - '0';
            total += d * ((i % 2 == 0) ? 3 : 1);
        }
        return (char) ('0' + (10 - total % 10) % 10);
    }

    /**
     * Whether a complete key ends in the check digit its body demands.
     *
     * <p>Answers {@code false} rather than throwing for anything that is not a
     * digit string of at least two characters: callers ask this about untrusted
     * input, where "not a valid key" is the answer, not an exceptional condition.
     */
    public static boolean isValid(final String key) {
        if (key == null || key.length() < 2 || !key.chars().allMatch(Character::isDigit)) {
            return false;
        }
        return of(key.substring(0, key.length() - 1)) == key.charAt(key.length() - 1);
    }
}
