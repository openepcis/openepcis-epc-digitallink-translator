/*
 * Copyright (c) 2022-2025 benelog GmbH & Co. KG
 * All rights reserved.
 *
 * Unauthorized copying, modification, distribution,
 * or use of this work, via any medium, is strictly prohibited.
 *
 * benelog GmbH & Co. KG reserves all rights not expressly granted herein,
 * including the right to sell licenses for using this work.
 */
package io.openepcis.identifiers.converter.core.tests;

import static org.junit.Assert.assertEquals;

import io.openepcis.identifiers.converter.Converter;
import io.openepcis.identifiers.validator.exception.ValidationException;
import org.junit.Before;
import org.junit.Test;

public class SGTINTest {

  private Converter converter;

  @Before
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void testSGTIN() throws ValidationException {
    // No Serial Number
    String sgtin = "urn:epc:id:sgtin:234567.1890123";
    TestIdentifiers.toDigitalLink(sgtin);

    // SGTIN less than 14 digits
    sgtin = "urn:epc:id:sgtin:234567.189012.1234";
    TestIdentifiers.toDigitalLink(sgtin);

    // SGTIN with more than 14 digits
    sgtin = "urn:epc:id:sgtin:234567.18901234.00";
    TestIdentifiers.toDigitalLink(sgtin);

    // SGTIN with invalid characters in GCP
    sgtin = "urn:epc:id:sgtin:23A567.18012.1234";
    TestIdentifiers.toDigitalLink(sgtin);

    // SGTIN with invalid characters in GTIN
    sgtin = "urn:epc:id:sgtin:234567.18A012.1234";
    TestIdentifiers.toDigitalLink(sgtin);

    // SGTIN with GCP less than 6 digits
    sgtin = "urn:epc:id:sgtin:23567.18012.1234";
    TestIdentifiers.toDigitalLink(sgtin);

    // SGTIN with GCP more than 12 digits
    sgtin = "urn:epc:id:sgtin:2356789012345.18012.1234";
    TestIdentifiers.toDigitalLink(sgtin);

    // Valid SGTIN
    assertEquals(
        "https://id.gs1.org/01/12345678901231/21/9999",
        converter.toURI("urn:epc:id:sgtin:234567890.1123.9999"));
    assertEquals(
        "https://id.gs1.org/01/12345678901231/21/!\"%&'()*+,-./",
        converter.toURI("urn:epc:id:sgtin:234567.1890123.!\"%&'()*+,-./"));
    assertEquals(
        "https://id.gs1.org/01/12345678901231/21//19:;<=>?AZ_az",
        converter.toURI("urn:epc:id:sgtin:234567.1890123./19:;<=>?AZ_az"));
    assertEquals(
        "https://id.gs1.org/01/73875837843740/21/9302932",
        converter.toURI("urn:epc:id:sgtin:387583.7784374.9302932"));
    assertEquals(
        "https://id.gs1.org/01/73875837843740/21/9302932",
        converter.toURI("urn:epc:id:sgtin:387583784374.7.9302932"));

    // SGTIN URI with SGTIN less than 14 digit
    sgtin = "https://id.gs1.org/01/1234567890123/21/9999";
    TestIdentifiers.toURN(sgtin, 6);

    // SGTIN URI with SGTIN more than 14 digit
    sgtin = "https://id.gs1.org/01/123456789012312/21/9999";
    TestIdentifiers.toURN(sgtin, 6);

    // SGTIN URI with SGTIN without serial number
    sgtin = "https://id.gs1.org/01/123456789012312";
    TestIdentifiers.toURN(sgtin, 6);

    // SGTIN URI with GCP more than 12 digits
    sgtin = "https://id.gs1.org/01/12345678901231/21/9090";
    TestIdentifiers.toURN(sgtin, 13);

    // Valid SGTIN
    assertEquals(
        "urn:epc:id:sgtin:235678.1908012.1234",
        converter.toURN("https://id.gs1.org/01/12356789080128/21/1234", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:sgtin:235678.1908012.1234",
        converter.toURN("https://lidl.de/food/frozen/01/12356789080128/21/1234", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:sgtin:234567.1890123.!\"%&'()*+,-./",
        converter.toURN("https://id.gs1.org/01/12345678901231/21/!\"%&'()*+,-./", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:sgtin:234567.1890123./19:;<=>?AZ_az",
        converter.toURN("https://id.gs1.org/01/12345678901231/21//19:;<=>?AZ_az", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:sgtin:9046539.520374.9302932",
        converter.toURN("https://lidl.de/food/frozen/01/59046539203740/21/9302932").get("asURN"));
    assertEquals(
        "urn:epc:id:sgtin:0085859.689450.94304903",
        converter.toURN("https://lidl.de/food/frozen/01/60085859894506/21/94304903").get("asURN"));

    /** Class level GTIN identifiers testing with valid and invalid scenarios */

    // WebURI to URN class level conversion

    // Invalid characters in GTIN
    sgtin = "https://id.gs1.org/01/13345A78901432";
    TestIdentifiers.toURNForClassLevelIdentifier(sgtin);

    // More than 14 digits in GTIN
    sgtin = "https://id.gs1.o	rg/01/123456789012345";
    TestIdentifiers.toURNForClassLevelIdentifier(sgtin);

    // Less than 14 digits in GTIN
    sgtin = "https://id.gs1.org/01/1234567890123";
    TestIdentifiers.toURNForClassLevelIdentifier(sgtin);
    TestIdentifiers.toURNForClassLevelIdentifier(sgtin, 12);

    // Serial Numbers in GTIN
    sgtin = "https://id.gs1.org/01/12345678901235/1234";
    TestIdentifiers.toURNForClassLevelIdentifier(sgtin);
    TestIdentifiers.toURNForClassLevelIdentifier(sgtin, 10);

    // Throw error if GCP is returned as 0 when no provided
    sgtin = "https://id.gs1.org/01/02045678901234";
    TestIdentifiers.toURNForClassLevelIdentifier(sgtin);

    // Valid GTIN Web URI to URN conversion
    assertEquals(
        "urn:epc:idpat:sgtin:234567.1890123.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/01/12345678901234", 6)
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:sgtin:858858858545.8.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/01/88588588585452", 12)
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:sgtin:93593755.88545.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/01/89359375585452")
            .get("asURN"));

    // URN to Web URI conversion

    // Invalid characters in GTIN
    sgtin = "urn:epc:idpat:sgtin:234567.A189012.*";
    TestIdentifiers.toURIForClassLevelIdentifier(sgtin);

    // Invalid prefix for GTIN
    sgtin = "urn:epc:idpat:gtin:8588588.858545.*";
    TestIdentifiers.toURIForClassLevelIdentifier(sgtin);

    // Invalid GTIN with more than 14 digits
    sgtin = "urn:epc:idpat:sgtin:8588588.8585451.*";
    TestIdentifiers.toURIForClassLevelIdentifier(sgtin);

    // Invalid GTIN with less than 14 digits
    sgtin = "urn:epc:idpat:sgtin:8588588.85854.*";
    TestIdentifiers.toURIForClassLevelIdentifier(sgtin);

    // Invalid GTIN with invalid GCP length
    sgtin = "urn:epc:idpat:sgtin:8588588.85854.*";
    TestIdentifiers.toURIForClassLevelIdentifier(sgtin);

    // Invalid GTIN without *
    sgtin = "urn:epc:idpat:sgtin:88538.4838493.*";
    TestIdentifiers.toURIForClassLevelIdentifier(sgtin);

    // Valid GTIN
    assertEquals(
        "https://id.gs1.org/01/88853849384934",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:sgtin:8853849.838493.*"));
    assertEquals(
        "https://id.gs1.org/01/85394839489381",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:sgtin:5394839.848938.*"));
    assertEquals(
        "https://id.gs1.org/01/93489348394895",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:sgtin:3489348.939489.*"));
  }
}
