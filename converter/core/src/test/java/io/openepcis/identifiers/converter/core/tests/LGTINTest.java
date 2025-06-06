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

import io.openepcis.identifiers.converter.Converter;
import io.openepcis.identifiers.validator.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LGTINTest {

  private Converter converter;

  @BeforeEach
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void LGTIN() throws ValidationException {
    // No Serial Number
    String lgtin = "urn:epc:class:lgtin:234567.1890123";
    TestIdentifiers.toURIForClassLevelIdentifier(lgtin);

    // LGTIN less than 14 digits
    lgtin = "urn:epc:class:lgtin:234567.189012.1234";
    TestIdentifiers.toURIForClassLevelIdentifier(lgtin);

    // LGTIN with invalid characters in GCP
    lgtin = "urn:epc:class:lgtin:23A567.189012.1234";
    TestIdentifiers.toURIForClassLevelIdentifier(lgtin);

    // LGTIN with invalid characters in GCP
    lgtin = "urn:epc:class:lgtin:234567.189A12.1234";
    TestIdentifiers.toURIForClassLevelIdentifier(lgtin);

    // LGTIN with GCP less than 6 digits
    lgtin = "urn:epc:class:lgtin:23456.189012.1234";
    TestIdentifiers.toURIForClassLevelIdentifier(lgtin);

    // SGTIN with GCP more than 12 digits
    lgtin = "urn:epc:class:lgtin:23456789012345.189012.1234";
    TestIdentifiers.toURIForClassLevelIdentifier(lgtin);

    // LGTIN Fixes - Valid LGTIN
    assertEquals("https://id.gs1.org/01/04023333020008/10/2019-10-07", converter.toURIForClassLevelIdentifier("urn:epc:class:lgtin:4023333.002000.2019-10-07"));
    assertEquals("https://id.gs1.org/01/12345678901248/10/1234", converter.toURIForClassLevelIdentifier("urn:epc:class:lgtin:234567.1890124.1234"));
    assertEquals("https://id.gs1.org/01/74384738478344/10/8484892%", converter.toURIForClassLevelIdentifier("urn:epc:class:lgtin:4384738478.734.8484892%"));
    assertEquals("https://id.gs1.org/01/12345678901231/10/!\"%&'()*+,-./19:;<=>", converter.toURIForClassLevelIdentifier("urn:epc:class:lgtin:2345678901.123.!\"%&'()*+,-./19:;<=>"));

    // LGTIN URI with LGTIN less than 14 digit
    lgtin = "https://id.gs1.org/01/1234567890123/10/9999";
    TestIdentifiers.toURNForClassLevelIdentifier(lgtin, 6);

    // LGTIN URI with LGTIN more than 14 digit
    lgtin = "https://id.gs1.org/01/123456789012312/10/9999";
    TestIdentifiers.toURNForClassLevelIdentifier(lgtin, 6);

    // LGTIN URI with LGTIN without serial number
    lgtin = "https://id.gs1.org/01/123456789012312";
    TestIdentifiers.toURNForClassLevelIdentifier(lgtin, 6);

    // LGTIN with invalid characters
    lgtin = "https://id.gs1.org/01/5985035903859A/10/2z32746";
    TestIdentifiers.toURNForClassLevelIdentifier(lgtin, 6);

    // LGTIN with invalid GCP Length
    lgtin = "https://id.gs1.org/01/59850359038590/10/2z32746";
    TestIdentifiers.toURNForClassLevelIdentifier(lgtin, 5);
    lgtin = "https://id.gs1.org/01/59850359038590/10/2z32746";
    TestIdentifiers.toURNForClassLevelIdentifier(lgtin, 13);

    // Valid LGTIN validation from WebURI to URN
    assertEquals(
        "urn:epc:class:lgtin:9850359038.559.2z32746",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/01/59850359038590/10/2z32746", 10)
            .get("asURN"));
    assertEquals(
        "urn:epc:class:lgtin:9046477.553859.2z32746",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/01/59046477538590/10/2z32746")
            .get("asURN"));
    assertEquals(
        "urn:epc:class:lgtin:9046537.500345.998877",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/01/59046537003451/10/998877")
            .get("asURN"));
    assertEquals(
        "urn:epc:class:lgtin:234567.1890123.9999",
        converter
            .toURNForClassLevelIdentifier("https://samsung.com/de/01/12345678901234/10/9999", 6)
            .get("asURN"));
    assertEquals(
        "urn:epc:class:lgtin:2475775757.157.488484",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/01/12475775757579/10/488484", 10)
            .get("asURN"));
    assertEquals(
        "urn:epc:class:lgtin:4778478489.784./777474",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/01/74778478489849/10//777474", 10)
            .get("asURN"));
    assertEquals(
        "urn:epc:class:lgtin:9557283.472873./8484892%",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/01/49557283728732/10//8484892%")
            .get("asURN"));
    assertEquals(
        "https://id.gs1.org/01/74384738478344/10/8484892%",
        converter.toURIForClassLevelIdentifier("urn:epc:class:lgtin:4384738478.734.8484892%"));
    assertEquals(
        "urn:epc:class:lgtin:2475775757.157.488484",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/01/12475775757579/10/488484", 10)
            .get("asURN"));
    assertEquals(
        "urn:epc:class:lgtin:4778478489.784./777474",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/01/74778478489849/10//777474", 10)
            .get("asURN"));
    assertEquals(
        "urn:epc:class:lgtin:93672837.42873./8484892%",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/01/49367283728732/10//8484892%")
            .get("asURN"));
    assertEquals(
        "urn:epc:class:lgtin:2345678901.123.!\"%&'()*+,-./19:;<=>",
        converter
            .toURNForClassLevelIdentifier(
                "https://id.gs1.org/01/12345678901231/10/!\"%&'()*+,-./19:;<=>", 10)
            .get("asURN"));
  }
}
