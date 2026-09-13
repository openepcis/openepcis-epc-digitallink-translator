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
package io.openepcis.identifiers.tests.core.epcis.noncompliant;

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.tests.core.epcis.ApplicationIdentifierValidationTestUtil;
import io.openepcis.identifiers.validator.ValidationContext;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * GMN (Global Model Number, AI 8013).
 *
 * <p>The check character pair arithmetic is verified exhaustively against GS1's own vectors
 * in {@code CheckCharacterPairValidatorTest}; this covers the Digital Link wrapper around
 * it — field length, CSET 82 in a path segment, GCP length, and the EPCIS refusal.
 *
 * <p>Asserted with {@code isEpcisCompliant = false} because GMN has no EPCIS URN form, the
 * same way the resolver asks.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GMNValidatorTest {

    private static final String HOST = "https://id.gs1.org/8013/";

    /** GS1 reference vectors that are also expressible in a URI path (no / or ?). */
    private static final String[] VALID_GMNS = {
            "1987654Ad4X4bL5ttr2310c2K",   // 25 chars: the AI 8013 maximum
            "12345678901234567890123NT",
            "12345_ABCDEFGHIJKLMCP",
            "12345_abcdefghijklmN3",
            "99999zzzzzzzzzzzzzzzzzzT2",
            "7907665Bm8v2AB", "97850l6KZm0yCD", "225803106GSpEF", "62577B8fRG7HJK",
            "515942070CYxLM", "390800494sP6NP", "957813138Sb6UV", "530790no0qOgWX",
            "62185314IvwmYZ", "794394895ic045", "0881063PhHvY89",
            "149512464PM+GH", "386830132uO+QR",   // '+' is CSET 82 and legal in a path
            "53395376X1:nST",                     // ':' likewise
            "23956qk1&dB!23",                     // '&' and '!' likewise
            "00000!HV"
    };

    @Test
    @Order(1)
    void validDigitalLinkUriTest() throws ValidationException {
        for (final String gmn : VALID_GMNS) {
            ApplicationIdentifierValidationTestUtil.assertValid(HOST + gmn, false, 6);
        }
        // any domain, upper end of the GCP range, and a query string
        ApplicationIdentifierValidationTestUtil.assertValid("https://horrem.kerpen.de/8013/1987654Ad4X4bL5ttr2310c2K", false, 12);
        ApplicationIdentifierValidationTestUtil.assertValid(HOST + "1987654Ad4X4bL5ttr2310c2K?linkType=gs1:pip", false, 6);
    }

    @Test
    @Order(2)
    void invalidDigitalLinkUriTest() throws ValidationException {
        // not an http(s) URI
        ApplicationIdentifierValidationTestUtil.assertInvalid("hps://id.gs1.org/8013/1987654Ad4X4bL5ttr2310c2K", false, 6);
        // 26 characters exceeds the AI 8013 field length
        ApplicationIdentifierValidationTestUtil.assertInvalid(HOST + "1987654Ad4X4bL5ttr2310c2KX", false, 6);
        // a single character cannot carry a check character pair
        ApplicationIdentifierValidationTestUtil.assertInvalid(HOST + "2", false, 6);
        // empty field
        ApplicationIdentifierValidationTestUtil.assertInvalid(HOST, false, 6);
        // space is not in CSET 82
        ApplicationIdentifierValidationTestUtil.assertInvalid(HOST + "1987654Ad4X4bL5tt 2310c2K", false, 6);
        // GCP length outside 6-12
        ApplicationIdentifierValidationTestUtil.assertInvalid(HOST + "1987654Ad4X4bL5ttr2310c2K", false, 5);
        ApplicationIdentifierValidationTestUtil.assertInvalid(HOST + "1987654Ad4X4bL5ttr2310c2K", false, 13);
    }

    @Test
    @Order(3)
    void checkCharacterPairTest() throws ValidationException {
        final ValidationContext ctx = ValidationContext.builder()
                .validateCheckDigit(true).gcpLength(10).epcisCompliant(false).build();

        for (final String gmn : VALID_GMNS) {
            ApplicationIdentifierValidationTestUtil.assertValid(HOST + gmn, ctx);
        }

        // each half of the pair wrong in turn — GS1's own negative vectors
        ApplicationIdentifierValidationTestUtil.assertInvalid(HOST + "1987654Ad4X4bL5ttr2310cXK", ctx);
        ApplicationIdentifierValidationTestUtil.assertInvalid(HOST + "1987654Ad4X4bL5ttr2310c2X", ctx);
        // structurally fine but the pair does not match the data
        ApplicationIdentifierValidationTestUtil.assertInvalid(HOST + "12345678901234567890123NA", ctx);
    }

    /** No {@code urn:epc:id:gmn:} exists, so EPCIS mode must refuse. */
    @Test
    @Order(4)
    void rejectedInEpcisCompliantModeTest() throws ValidationException {
        ApplicationIdentifierValidationTestUtil.assertInvalid(HOST + "1987654Ad4X4bL5ttr2310c2K", true, 6);
    }
}
