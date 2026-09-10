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
package io.openepcis.identifiers.converter.core.tests;

import io.openepcis.identifiers.converter.Converter;
import io.openepcis.identifiers.converter.util.DigitalLinkQualifiers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * A GTIN Digital Link naming BOTH a lot and a serial converts to the EPC of the level the
 * caller asks for: SGTIN at instance level (lot = attribute), LGTIN at class level
 * (serial dropped). Before this, the instance conversion failed validation and the
 * class conversion read {@code l/21/s} as the lot.
 */
@DisplayName("Digital Link with lot AND serial: instance → SGTIN, class → LGTIN")
class LotSerialDigitalLinkTest {

  /** 952…-range GTIN (default GCP length 7 in the fallback), lot LOT-A, serial SER-1. */
  private static final String BOTH = "https://id.gs1.org/01/09521000020115/10/LOT-A/21/SER-1";
  private Converter converter;

  @BeforeEach
  void before() {
    converter = new Converter();
  }

  @Test
  @DisplayName("Instance level keeps the serial, reports the lot as attribute")
  void instanceLevelIsTheSgtin() {
    final Map<String, String> result = converter.toURN(BOTH, 7);
    assertEquals("urn:epc:id:sgtin:9521000.002011.SER-1", result.get("asURN"));
    assertEquals("SER-1", result.get("serial"));
    assertEquals("LOT-A", result.get("lot"), "the lot travels as an attribute of the instance");
    assertEquals("https://id.gs1.org/01/09521000020115/21/SER-1", result.get("canonicalDL"),
        "the canonical Digital Link is the identity, GTIN + serial");
    assertEquals(BOTH, result.get("asCaptured"));
    assertEquals("09521000020115", result.get("gtin"));
  }

  @Test
  @DisplayName("Class level keeps the lot, drops the serial and says so")
  void classLevelIsTheLgtin() {
    final Map<String, String> result = converter.toURNForClassLevelIdentifier(BOTH, 7);
    assertEquals("urn:epc:class:lgtin:9521000.002011.LOT-A", result.get("asURN"));
    assertEquals("LOT-A", result.get("lot"));
    assertEquals("LOT-A", result.get("serial"), "legacy key: the LGTIN result always called its lot 'serial'");
    assertEquals("SER-1", result.get("serialNumber"), "the dropped serial is reported, not silently lost");
    assertEquals("https://id.gs1.org/01/09521000020115/10/LOT-A", result.get("canonicalDL"));
    assertEquals(BOTH, result.get("asCaptured"));
  }

  @Test
  @DisplayName("A vanity host canonicalises to the GS1 domain in the level's form")
  void vanityHostCanonicalisesPerLevel() {
    final String vanity = "https://id.demo.epcis.cloud/01/09521000020115/10/LOT-A/21/SER-1";
    assertEquals("https://id.gs1.org/01/09521000020115/21/SER-1",
        converter.toURN(vanity, 7).get("canonicalDL"));
    assertEquals("https://id.gs1.org/01/09521000020115/10/LOT-A",
        converter.toURNForClassLevelIdentifier(vanity, 7).get("canonicalDL"));
  }

  @Test
  @DisplayName("The plain forms are unchanged, and a lone lot or serial reports no other qualifier")
  void plainFormsUnchanged() {
    final Map<String, String> sgtin = converter.toURN("https://id.gs1.org/01/09521000020115/21/SER-1", 7);
    assertEquals("urn:epc:id:sgtin:9521000.002011.SER-1", sgtin.get("asURN"));
    assertNull(sgtin.get("lot"));

    final Map<String, String> lgtin = converter.toURNForClassLevelIdentifier("https://id.gs1.org/01/09521000020115/10/LOT-A", 7);
    assertEquals("urn:epc:class:lgtin:9521000.002011.LOT-A", lgtin.get("asURN"));
    assertEquals("LOT-A", lgtin.get("lot"));
    assertNull(lgtin.get("serialNumber"));

    // URN → Digital Link never produces the combined form: an EPC has one qualifier.
    assertEquals("https://id.gs1.org/01/09521000020115/21/SER-1", converter.toURI("urn:epc:id:sgtin:9521000.002011.SER-1"));
    assertEquals("https://id.gs1.org/01/09521000020115/10/LOT-A", converter.toURIForClassLevelIdentifier("urn:epc:class:lgtin:9521000.002011.LOT-A"));
  }

  @Test
  @DisplayName("Qualifier helpers stop at the next segment or the query string")
  void qualifierHelpers() {
    assertEquals("LOT-A", DigitalLinkQualifiers.segmentValue(BOTH, "/10/"));
    assertEquals("SER-1", DigitalLinkQualifiers.segmentValue(BOTH, "/21/"));
    assertNull(DigitalLinkQualifiers.segmentValue("https://id.gs1.org/01/09521000020115/21/SER-1", "/10/"));
    assertEquals("https://id.gs1.org/01/09521000020115/21/SER-1", DigitalLinkQualifiers.withoutSegment(BOTH, "/10/"));
    assertEquals("https://id.gs1.org/01/09521000020115/10/LOT-A", DigitalLinkQualifiers.withoutSegment(BOTH, "/21/"));
    assertEquals("LOT-A", DigitalLinkQualifiers.segmentValue(BOTH + "?17=261231", "/10/"));
    assertEquals("SER-1", DigitalLinkQualifiers.segmentValue(BOTH + "?17=261231", "/21/"));
    assertEquals("https://id.gs1.org/01/09521000020115/21/SER-1?17=261231", DigitalLinkQualifiers.withoutSegment(BOTH + "?17=261231", "/10/"));
  }
}
