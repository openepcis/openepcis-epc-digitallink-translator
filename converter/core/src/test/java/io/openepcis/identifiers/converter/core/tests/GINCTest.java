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

public class GINCTest {

  private Converter converter;

  @BeforeEach
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void GINC() throws ValidationException {

    // Invalid GS1 Syntax
    String ginc = "urn:epc:i:ginc:1234567890.12ABC";
    TestIdentifiers.toDigitalLink(ginc);

    // GINC with invalid characters in GCP
    ginc = "urn:epc:id:ginc:1234567890A.12ABC";
    TestIdentifiers.toDigitalLink(ginc);

    // GINC without serial number
    ginc = "urn:epc:id:ginc:1234567890";
    TestIdentifiers.toDigitalLink(ginc);
    ginc = "urn:epc:id:ginc:1234567890";
    TestIdentifiers.toDigitalLink(ginc);

    // GINC more than 30 characters
    ginc = "urn:epc:id:ginc:1234567890.123456789012345678901";
    TestIdentifiers.toDigitalLink(ginc);

    // Valid GINC URN
    assertEquals(
        "https://id.gs1.org/401/12345678901234",
        converter.toURI("urn:epc:id:ginc:1234567890.1234"));
    assertEquals("https://id.gs1.org/401/4738473", converter.toURI("urn:epc:id:ginc:473847.3"));
    assertEquals(
        "https://id.gs1.org/401/4849304738473", converter.toURI("urn:epc:id:ginc:484930473847.3"));
    assertEquals(
        "https://id.gs1.org/401/4849304738473!\"%/%&'()*+,-.:=",
        converter.toURI("urn:epc:id:ginc:48493047.38473!\"%/%&'()*+,-.:="));
    assertEquals(
        "https://id.gs1.org/401/4849304738473!\"\"%/%&'()*+,-.:=",
        converter.toURI("urn:epc:id:ginc:48493047.38473!\"\"%/%&'()*+,-.:="));

    // GINC less than GCP Length
    ginc = "https://id.gs1.org/401/12345678901";
    TestIdentifiers.toURN(ginc, 12);

    // GINC with GCP Length more than 12
    ginc = "https://id.gs1.org/401/123456789012";
    TestIdentifiers.toURN(ginc, 15);

    // Invalid Characters in GCP
    ginc = "https://id.gs1.org/401/12345678A012";
    TestIdentifiers.toURN(ginc, 10);

    // Invalid Web URI Prefix
    ginc = "https://id.gs1.org/405/1234567890.1234&/";
    TestIdentifiers.toURN(ginc, 10);

    assertEquals(
        "urn:epc:id:ginc:1234567890.1234A",
        converter.toURN("https://id.gs1.org/401/12345678901234A", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:ginc:1234567890.1234A",
        converter.toURN("https://eclipse.org/401/12345678901234A", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:ginc:48493047.38473!\"\"%/%&'()*+,-.:=",
        converter.toURN("https://id.gs1.org/401/4849304738473!\"\"%/%&'()*+,-.:=", 8).get("asURN"));
    assertEquals(
        "urn:epc:id:ginc:656987789012.",
        converter.toURN("https://id.gs1.org/401/656987789012").get("asURN"));
    assertEquals(
        "urn:epc:id:ginc:123456789012.d",
        converter.toURN("https://id.gs1.org/401/123456789012d", 12).get("asURN"));
  }
}
