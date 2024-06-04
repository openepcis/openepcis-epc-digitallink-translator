/*
 * Copyright 2022-2024 benelog GmbH & Co. KG
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package io.openepcis.epc.translator.tests;

import static org.junit.Assert.assertEquals;

import io.openepcis.epc.translator.Converter;
import org.junit.Before;
import org.junit.Test;

public class ShortNameReplacerTest {

  private Converter converter;

  @Before
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
