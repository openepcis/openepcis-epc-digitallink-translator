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

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.converter.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GSINTest {

  private Converter converter;

  @BeforeEach
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void GSIN() throws ValidationException {
    // GSIN with invalid characters
    String gsin = "urn:epc:id:gsin:12345678A0.123456";
    TestIdentifiers.toDigitalLink(gsin);

    // GSIN with less than 6 digits GCP
    gsin = "urn:epc:id:gsin:12345.123456";
    TestIdentifiers.toDigitalLink(gsin);

    // GSIN with more than 17 digits
    gsin = "urn:epc:id:gsin:1234567890.1234567";
    TestIdentifiers.toDigitalLink(gsin);

    // GSIN with less than 17 digits
    gsin = "urn:epc:id:gsin:1234567890.12345";
    TestIdentifiers.toDigitalLink(gsin);

    // GSIN with invalid characters
    gsin = "urn:epc:id:gsin:1234567890.12345A";
    TestIdentifiers.toDigitalLink(gsin);

    // GSIN with invalid characters in GCP
    gsin = "urn:epc:id:gsin:12345A.7890123456";
    TestIdentifiers.toDigitalLink(gsin);

    // Valid GSIN
    assertEquals("https://id.gs1.org/402/12345678901234560", converter.toURI("urn:epc:id:gsin:123456.7890123456"));
    assertEquals("https://id.gs1.org/402/48398439843984392", converter.toURI("urn:epc:id:gsin:483984.3984398439"));
    assertEquals("https://id.gs1.org/402/85948594504504549", converter.toURI("urn:epc:id:gsin:859485945045.0454"));

    // GSIN URI with invalid characters
    gsin = "https://id.gs1.org/402/1234567890512345A";
    TestIdentifiers.toURN(gsin, 6);

    // GSIN with less than 17 digits
    gsin = "https://id.gs1.org/402/1234567890512345";
    TestIdentifiers.toURN(gsin, 6);

    // GSIN with more than 17 digits
    gsin = "https://id.gs1.org/402/123456789051234567";
    TestIdentifiers.toURN(gsin, 6);

    // GSIN with invalid GCP
    gsin = "https://id.gs1.org/402/95485984950459045";
    TestIdentifiers.toURN(gsin, 5);
    TestIdentifiers.toURN(gsin, 13);

    // Valid GSIN
    assertEquals(
        "urn:epc:id:gsin:1234567890.512345",
        converter.toURN("https://id.gs1.org/402/12345678905123457", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:gsin:1234567890.512345",
        converter.toURN("https://benelog.com/horrem/402/12345678905123457", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:gsin:9524989358.953939",
        converter.toURN("https://benelog.com/horrem/402/95249893589539398").get("asURN"));
    assertEquals(
        "urn:epc:id:gsin:952044.5495834983",
        converter.toURN("https://benelog.com/horrem/402/95204454958349832").get("asURN"));
    assertEquals(
        "urn:epc:id:gsin:952059.8495045904",
        converter.toURN("https://https://id.gs1.org/402/95205984950459042", 6).get("asURN"));
  }
}
