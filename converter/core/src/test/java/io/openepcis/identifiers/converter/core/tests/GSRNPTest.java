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

public class GSRNPTest {

  private Converter converter;

  @BeforeEach
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void testGSRNP() throws ValidationException {

    // Invalid GSRNP syntax
    String gsrnp = "un:epc:id:gsrnp:1234567890.1234567";
    TestIdentifiers.toDigitalLink(gsrnp);

    // GSRNP with invalid characters in GCP
    gsrnp = "urn:epc:id:gsrnp:123456789A.1234567";
    TestIdentifiers.toDigitalLink(gsrnp);

    // GSRNP with more than 12 digits GCP
    gsrnp = "urn:epc:id:gsrnp:1234567890123.1234567";
    TestIdentifiers.toDigitalLink(gsrnp);

    // GSRNP with more than 18 digits
    gsrnp = "urn:epc:id:gsrnp:1234567890.123456712";
    TestIdentifiers.toDigitalLink(gsrnp);

    // GSRNP with less than 18 digits
    gsrnp = "urn:epc:id:gsrnp:1234567890.123456712";
    TestIdentifiers.toDigitalLink(gsrnp);

    // GSRNP without GCP
    gsrnp = "urn:epc:id:gsrnp:1234567890123456712";
    TestIdentifiers.toDigitalLink(gsrnp);

    // Valid GSRNP
    assertEquals(
        "https://id.gs1.org/8017/123456789012345675",
        converter.toURI("urn:epc:id:gsrnp:1234567890.1234567"));
    assertEquals(
        "https://id.gs1.org/8017/843984934394394932",
        converter.toURI("urn:epc:id:gsrnp:843984.93439439493"));
    assertEquals(
        "https://id.gs1.org/8017/578457847548758479",
        converter.toURI("urn:epc:id:gsrnp:578457847548.75847"));
    assertEquals(
        "https://id.gs1.org/8017/345425435235243521",
        converter.toURI("urn:epc:id:gsrnp:3454254352.3524352"));

    // GSRNP URI with invalid character
    gsrnp = "https://id.gs1.org/8017/12345678901234567A";
    TestIdentifiers.toURN(gsrnp, 10);

    // GSRNP URI with more than 18 digits
    gsrnp = "https://id.gs1.org/8017/1234567890123456789";
    TestIdentifiers.toURN(gsrnp, 10);

    // GSRNP URI with less than 18 digits
    gsrnp = "https://id.gs1.org/8017/12345678901234567";
    TestIdentifiers.toURN(gsrnp, 10);

    // GSRNP URI with GCP more than 12
    gsrnp = "https://id.gs1.org/8017/123456789012345678";
    TestIdentifiers.toURN(gsrnp, 13);

    // Valid GSRNP
    assertEquals(
        "urn:epc:id:gsrnp:1234567890.1234567",
        converter.toURN("https://id.gs1.org/8017/123456789012345678", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:gsrnp:1234567890.1234567",
        converter.toURN("https://google.com/8017/123456789012345678", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:gsrnp:643963.76437473634",
        converter.toURN("https://google.com/8017/643963764374736343").get("asURN"));
    assertEquals(
        "urn:epc:id:gsrnp:645933932413.12123",
        converter.toURN("https://google.com/8017/645933932413121231").get("asURN"));
    assertEquals(
        "urn:epc:id:gsrnp:667602231213.12123",
        converter.toURN("https://google.com/8017/667602231213121237", 12).get("asURN"));
  }
}
