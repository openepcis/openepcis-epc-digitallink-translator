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

public class SGLNTest {

  private Converter converter;

  @BeforeEach
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void testSGLN() throws ValidationException {

    // SGLN with invalid GS1 format
    String sgln = "urn:epc::sgln:1234567890.1.1111";
    TestIdentifiers.toDigitalLink(sgln);

    // SGLN with invalid GCP
    sgln = "urn:epc:id:sgln:1234567890AS.1.1111";
    TestIdentifiers.toDigitalLink(sgln);

    // SGLN with GCP less than 6 digits
    sgln = "urn:epc:id:sgln:1234.1.1111";
    TestIdentifiers.toDigitalLink(sgln);

    // SGLN with GCP more than 12 digits
    sgln = "urn:epc:id:sgln:12345678901234.1.1111";
    TestIdentifiers.toDigitalLink(sgln);

    // SGLN with less than 13 digits SGLN
    sgln = "urn:epc:id:sgln:1234567890.1.1111";
    TestIdentifiers.toDigitalLink(sgln);

    // SGLN with more than 13 digits SGLN
    sgln = "urn:epc:id:sgln:12345678901.11.1111";
    TestIdentifiers.toDigitalLink(sgln);

    // SGLN without Serial Number
    sgln = "urn:epc:id:sgln:1234567890.11";
    TestIdentifiers.toDigitalLink(sgln);

    // Valid SGLN with serial Number
    assertEquals(
        "https://id.gs1.org/414/1234567890111/254/1111",
        converter.toURI("urn:epc:id:sgln:1234567890.11.1111"));
    assertEquals(
        "https://id.gs1.org/414/4374736473640/254/\"%&'()*+,-./19:;<=>?",
        converter.toURI("urn:epc:id:sgln:437473647364..\"%&'()*+,-./19:;<=>?"));
    assertEquals(
        "https://id.gs1.org/414/4374736473640", converter.toURI("urn:epc:id:sgln:437473647364..0"));
    assertEquals(
        "https://id.gs1.org/414/7857834384782/254/0394903",
        converter.toURI("urn:epc:id:sgln:785783.438478.0394903"));
    assertEquals(
        "https://id.gs1.org/414/4374736473640", converter.toURI("urn:epc:id:sgln:4374736473.64.0"));

    // SGLN URI with invalid format
    sgln = "hps://id.gs1.org/414/1234567890123/254/1111";
    TestIdentifiers.toURN(sgln, 6);

    // SGLN with 12 digit SGLN
    sgln = "https://id.gs1.org/414/123456789012/254/1111";
    TestIdentifiers.toURN(sgln, 6);

    // SGLN with GCP less than 6 digits
    sgln = "https://id.gs1.org/414/1234567890123/254/1111";
    TestIdentifiers.toURN(sgln, 5);
    sgln = "https://id.gs1.org/414/1234567890123/254/1111";
    TestIdentifiers.toURN(sgln, 13);

    // SGLN with 14 digits
    sgln = "https://id.gs1.org/414/12345678901234/254/1111";
    TestIdentifiers.toURN(sgln, 12);

    // SGLN with invalid characters
    sgln = "https://id.gs1.org/414/123456789012A/254/1111";
    TestIdentifiers.toURN(sgln, 12);

    // SGLN with invalid serial
    sgln = "https://id.gs1.org/414/5893849384938/254";
    TestIdentifiers.toURN(sgln, 12);

    // Valid SGLN URI with serial number
    assertEquals(
        "urn:epc:id:sgln:123456.789012.1234",
        converter.toURN("https://id.gs1.org/414/1234567890128/254/1234", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:sgln:123456.789012.1234",
        converter
            .toURN("https://deutscheBahn.de/train/414/1234567890128/254/1234", 6)
            .get("asURN"));
    assertEquals(
        "urn:epc:id:sgln:93590743.8478.0394903",
        converter.toURN("https://id.gs1.org/414/9359074384785/254/0394903").get("asURN"));
    assertEquals(
        "urn:epc:id:sgln:93590443.8478.0",
        converter.toURN("https://id.gs1.org/414/9359044384784").get("asURN"));
    assertEquals(
        "urn:epc:id:sgln:93592393.8493.4390493",
        converter.toURN("https://id.gs1.org/414/9359239384933/254/4390493").get("asURN"));
    assertEquals(
        "urn:epc:id:sgln:688000938493..0",
        converter.toURN("https://id.gs1.org/414/6880009384936", 12).get("asURN"));
    assertEquals(
        "urn:epc:id:sgln:4374736473.64.0",
        converter.toURN("https://id.gs1.org/414/4374736473640", 10).get("asURN"));
  }
}
