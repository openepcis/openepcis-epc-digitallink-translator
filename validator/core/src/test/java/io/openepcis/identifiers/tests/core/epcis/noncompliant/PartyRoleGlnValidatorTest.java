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
package io.openepcis.identifiers.tests.core.epcis.noncompliant;

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.tests.core.epcis.ApplicationIdentifierValidationTestUtil;
import io.openepcis.identifiers.validator.ValidationContext;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Party-role GLNs: ship-to (410), bill-to (411), purchased-from (412), ship-for (413) and
 * pay-to (415).
 *
 * <p>Every identifier here is asserted with {@code isEpcisCompliant = false}, because these
 * AIs have no EPCIS URN form — the resolver asks the same way
 * ({@code epcisCompliant(false)} in {@code DigitalLinkValidator}).
 *
 * <p>GLNs are from the 952… GS1 demo range with real check digits, computed as MOD-10 over
 * the leading 12 digits.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PartyRoleGlnValidatorTest {

    private static final String[] AIS = {"410", "411", "412", "413", "415"};

    /** 952… demo GLNs whose 13th digit is the correct MOD-10 check digit. */
    private static final String[] VALID_GLNS = {"9521890340331", "9521000000124", "9521234000099"};

    @Test
    @Order(1)
    void validDigitalLinkUriTest() throws ValidationException {
        for (final String ai : AIS) {
            for (final String gln : VALID_GLNS) {
                ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/" + ai + "/" + gln, false, 6);
            }
            // any domain, and the upper end of the GCP range
            ApplicationIdentifierValidationTestUtil.assertValid("https://horrem.kerpen.de/" + ai + "/9521890340331", false, 12);
            // a query string must not break the match
            ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/" + ai + "/9521890340331?linkType=gs1:pip", false, 6);
        }
    }

    @Test
    @Order(2)
    void invalidDigitalLinkUriTest() throws ValidationException {
        for (final String ai : AIS) {
            // not an http(s) URI
            ApplicationIdentifierValidationTestUtil.assertInvalid("hps://id.gs1.org/" + ai + "/9521890340331", false, 6);
            // 12 digits instead of 13
            ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/" + ai + "/952189034033", false, 6);
            // 14 digits instead of 13
            ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/" + ai + "/95218903403312", false, 6);
            // non-numeric
            ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/" + ai + "/95218903403A1", false, 6);
            // GCP length outside 6-12
            ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/" + ai + "/9521890340331", false, 5);
            ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/" + ai + "/9521890340331", false, 13);
        }
    }

    @Test
    @Order(3)
    void checkDigitTest() throws ValidationException {
        final ValidationContext ctx = ValidationContext.builder()
                .validateCheckDigit(true).gcpLength(10).epcisCompliant(false).build();

        for (final String ai : AIS) {
            for (final String gln : VALID_GLNS) {
                ApplicationIdentifierValidationTestUtil.assertValid("https://id.gs1.org/" + ai + "/" + gln, ctx);
            }
            // last digit bumped: 9521890340331 -> ...330
            ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/" + ai + "/9521890340330", ctx);
            ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/" + ai + "/9521000000125", ctx);
        }
    }

    /**
     * EPCIS mode must not accept these: there is no {@code urn:epc:id:} form for a ship-to
     * GLN, so a caller enforcing EPCIS compliance has to be told no.
     */
    @Test
    @Order(4)
    void rejectedInEpcisCompliantModeTest() throws ValidationException {
        for (final String ai : AIS) {
            ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/" + ai + "/9521890340331", true, 6);
        }
    }

    /**
     * AI 416 (production/service location GLN) is deliberately NOT handled here: GS1's
     * resolver-description schema does not permit it in {@code supportedPrimaryKeys}, so
     * the resolver must not advertise or resolve it.
     */
    @Test
    @Order(5)
    void productionServiceLocationNotSupportedTest() throws ValidationException {
        ApplicationIdentifierValidationTestUtil.assertInvalid("https://id.gs1.org/416/9521890340331", false, 6);
    }
}
