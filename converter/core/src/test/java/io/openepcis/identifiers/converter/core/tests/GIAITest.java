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

public class GIAITest {

  private Converter converter;

  @BeforeEach
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void giaiTest() throws ValidationException {

    // GIAI with invalid GS1 syntax
    String giai = "urn:ec:id:giai:1234567890";
    TestIdentifiers.toDigitalLink(giai);

    // GIAI without serial number
    giai = "urn:epc:id:giai:1234567890";
    TestIdentifiers.toDigitalLink(giai);

    // GIAI with invalid characters in GCP
    giai = "urn:epc:id:giai:123456789A.123";
    TestIdentifiers.toDigitalLink(giai);

    // GIAI with less than 6 digits GCP
    giai = "urn:epc:id:giai:12345.123";
    TestIdentifiers.toDigitalLink(giai);

    // GIAI with more than 12 digits GCP
    giai = "urn:epc:id:giai:1234567890123.123";
    TestIdentifiers.toDigitalLink(giai);

    // Valid GIAI
    assertEquals(
        "https://id.gs1.org/8004/123456789012123",
        converter.toURI("urn:epc:id:giai:123456789012.123"));
    assertEquals("https://id.gs1.org/8004/1234567", converter.toURI("urn:epc:id:giai:123456.7"));
    assertEquals(
        "https://id.gs1.org/8004/8394958495850", converter.toURI("urn:epc:id:giai:839495849585.0"));
    assertEquals(
        "https://id.gs1.org/8004/8394958495850!\"/%_",
        converter.toURI("urn:epc:id:giai:839495849585.0!\"/%_"));

    // GIAI with invalid URI format
    giai = "/8004/123456789012123";
    TestIdentifiers.toURN(giai, 10);

    // GIAI with invalid characters
    giai = "https://id.gs1.org/8004/123A6789012123";
    TestIdentifiers.toURN(giai, 10);

    // GIAI with gcp less than 6
    giai = "https://id.gs1.org/8004/123456789012123";
    TestIdentifiers.toURN(giai, 5);

    // GIAI with gcp more than 12
    giai = "https://id.gs1.org/8004/123456789012123";
    TestIdentifiers.toURN(giai, 13);

    assertEquals(
        "urn:epc:id:giai:1234567890.12123",
        converter.toURN("https://id.gs1.org/8004/123456789012123", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:giai:1234567890.12123",
        converter.toURN("https://news.google.in/8004/123456789012123", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:giai:357595.8495850!\"/%_",
        converter.toURN("https://id.gs1.org/8004/3575958495850!\"/%_").get("asURN"));
    assertEquals(
        "urn:epc:id:giai:732487.8;><=?",
        converter.toURN("https://id.gs1.org/8004/7324878;><=?").get("asURN"));
    assertEquals(
        "urn:epc:id:giai:42512183..SENSOR.TR-07-AVE-583:1",
        converter.toURN("https://id.gs1.org/8004/42512183.SENSOR.TR-07-AVE-583:1").get("asURN"));
  }
}
