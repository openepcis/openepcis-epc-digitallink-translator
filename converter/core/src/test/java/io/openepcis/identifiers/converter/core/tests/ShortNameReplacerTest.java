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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ShortNameReplacerTest {

  private Converter converter;

  @BeforeEach
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void ShortNameReplaceTest() {
    String gs1Identifier = "https://example.org/giai/401234599999";
    assertEquals(
        "https://id.gs1.org/8004/401234599999", converter.shortNameReplacer(gs1Identifier));

    gs1Identifier = "https://example.com/gdti/4012345000054987";
    assertEquals(
        "https://id.gs1.org/253/4012345000054987", converter.shortNameReplacer(gs1Identifier));

    gs1Identifier = "https://www.ncbi.nlm.nih.gov/taxonomy/1126011";
    assertEquals(
        "https://www.ncbi.nlm.nih.gov/taxonomy/1126011",
        converter.shortNameReplacer(gs1Identifier));

    gs1Identifier = "https://identifiers.org/inchikey:CZMRCDWAGMRECN-UGDNZRGBSA-N";
    assertEquals(
        "https://identifiers.org/inchikey:CZMRCDWAGMRECN-UGDNZRGBSA-N",
        converter.shortNameReplacer(gs1Identifier));

    gs1Identifier = "https://example.org/giai/401234599999";
    assertEquals(
        "https://id.gs1.org/8004/401234599999", converter.shortNameReplacer(gs1Identifier));

    gs1Identifier = "https://id.gs1.org/giai/4000001111";
    assertEquals("https://id.gs1.org/8004/4000001111", converter.shortNameReplacer(gs1Identifier));

    gs1Identifier = "https://hello.comain//cpi/381366783201294-5A";
    assertEquals(
        "https://id.gs1.org/8010/381366783201294-5A", converter.shortNameReplacer(gs1Identifier));

    gs1Identifier = "https://id.gs1.org/gln/1234567890111";
    assertEquals(
        "https://id.gs1.org/414/1234567890111", converter.shortNameReplacer(gs1Identifier));

    gs1Identifier = "https://id.gs1.org/gcn/4343884394893";
    assertEquals(
        "https://id.gs1.org/255/4343884394893", converter.shortNameReplacer(gs1Identifier));

    gs1Identifier = "https://myownDomain/gtin/12345678901231/ser/9999";
    assertEquals(
        "https://id.gs1.org/01/12345678901231/21/9999", converter.shortNameReplacer(gs1Identifier));

    gs1Identifier = "testing:123";
    assertEquals("testing:123", converter.shortNameReplacer(gs1Identifier));

    gs1Identifier = "urn:epc:id:gsin:8439589358.953939";
    assertEquals("urn:epc:id:gsin:8439589358.953939", converter.shortNameReplacer(gs1Identifier));

    gs1Identifier = "https://id.example/4343884394893";
    assertEquals("https://id.example/4343884394893", converter.shortNameReplacer(gs1Identifier));

    gs1Identifier = "https://example.com/253/4012345000054987";
    assertEquals(
        "https://id.gs1.org/253/4012345000054987", converter.shortNameReplacer(gs1Identifier));

    gs1Identifier = "https://id.gs1.de/01/04012345999990/21/XYZ-1234";
    assertEquals(
        "https://id.gs1.org/01/04012345999990/21/XYZ-1234",
        converter.shortNameReplacer(gs1Identifier));

    gs1Identifier = "https://id.gs1.de/01/84384384898340/ser/894893894838934893";
    assertEquals(
        "https://id.gs1.org/01/84384384898340/21/894893894838934893",
        converter.shortNameReplacer(gs1Identifier));
    assertEquals(null, converter.shortNameReplacer(null));
    assertEquals("", converter.shortNameReplacer(""));
  }
}
