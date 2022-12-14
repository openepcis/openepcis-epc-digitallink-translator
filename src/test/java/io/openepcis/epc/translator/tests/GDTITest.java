/*
 * Copyright 2022 benelog GmbH & Co. KG
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
import io.openepcis.epc.translator.exception.ValidationException;
import org.junit.Before;
import org.junit.Test;

public class GDTITest {

  private Converter converter;

  @Before
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void testGDTI() throws ValidationException {

    // GDTI without serial number
    String gdti = "urn:epc:id:gdti:1234567890.12";
    TestIdentifiers.toDigitalLink(gdti);

    // GDTI with invalid characters
    gdti = "urn:epc:id:gdti:123456789A.12.13";
    TestIdentifiers.toDigitalLink(gdti);

    // GDTI with GCP more than 12 digits
    gdti = "urn:epc:id:gdti:1234567890123.12.13";
    TestIdentifiers.toDigitalLink(gdti);

    // GDTI with more than 13 digits
    gdti = "urn:epc:id:gdti:1234567890.12345.13";
    TestIdentifiers.toDigitalLink(gdti);

    // Valid GDTI
    assertEquals(
        converter.toURI("urn:epc:id:gdti:1234567890.12.ABC"),
        "https://id.gs1.org/253/1234567890128ABC");
    assertEquals(
        converter.toURI("urn:epc:id:gdti:124757.578484.!"),
        "https://id.gs1.org/253/1247575784846!");
    assertEquals(
        converter.toURI("urn:epc:id:gdti:893489348949..\":?>"),
        "https://id.gs1.org/253/8934893489494\":?>");
    assertEquals(
        converter.toURI("urn:epc:id:gdti:434934948984..4"),
        "https://id.gs1.org/253/43493494898454");

    // GDTI URI with invalid characters
    gdti = "https://id.gs1.org/253/123456789A128ABC";
    TestIdentifiers.toURN(gdti, 10);

    // GDTI URI without serial
    gdti = "https://id.gs1.org/253/1234567890123";
    TestIdentifiers.toURN(gdti, 10);

    // GDTI with invalid GCP Length
    gdti = "https://id.gs1.org/253/1234567890123A";
    TestIdentifiers.toURN(gdti, 13);

    // Valid GDTI
    assertEquals(
        "urn:epc:id:gdti:1234567890.12.A",
        converter.toURN("https://id.gs1.org/253/1234567890123A", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:gdti:1234567890.12.A",
        converter.toURN("https://benelog1.de/253/1234567890123A", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:gdti:893489348949..\":?>",
        converter.toURN("https://id.gs1.org/253/8934893489494\":?>", 12).get("asURN"));
    assertEquals(
        "urn:epc:id:gdti:434934948984..4",
        converter.toURN("https://id.gs1.org/253/43493494898454", 12).get("asURN"));
    assertEquals(
        "urn:epc:id:gdti:124757.578484.!",
        converter.toURN("https://id.gs1.org/253/1247575784846!", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:gdti:1849348394.44.8!\"%&\"+",
        converter.toURN("https://id.gs1.org/253/18493483944448!\"%&\"+").get("asURN"));

    /** GDTI Class level identifiers conversion */

    // Web URI to URN

    // Class level GDTI Web URI with more than 13 digits
    gdti = "https://id.gs1.org/253/12345678901234";
    TestIdentifiers.toURNForClassLevelIdentifier(gdti);
    TestIdentifiers.toURNForClassLevelIdentifier(gdti, 7);

    // Class level GDTI Web URI with less than 13 digits
    gdti = "https://id.gs1.org/253/123456789012";
    TestIdentifiers.toURNForClassLevelIdentifier(gdti);
    TestIdentifiers.toURNForClassLevelIdentifier(gdti, 10);

    // Class level GDTI Web URI with invalid characters
    gdti = "https://id.gs1.org/253/123456789012A";
    TestIdentifiers.toURNForClassLevelIdentifier(gdti);
    TestIdentifiers.toURNForClassLevelIdentifier(gdti, 12);

    // Class level GDTI with invalid GCP length
    gdti = "https://id.gs1.org/253/1234567890123";
    TestIdentifiers.toURNForClassLevelIdentifier(gdti, 5);
    TestIdentifiers.toURNForClassLevelIdentifier(gdti, 13);

    // Class level GDTI with invalid prefix
    gdti = "https://id.gs1.org/2534/1234567890123";
    TestIdentifiers.toURNForClassLevelIdentifier(gdti);
    TestIdentifiers.toURNForClassLevelIdentifier(gdti, 11);

    // Valid class level GDTI Web URI
    assertEquals(
        "urn:epc:idpat:gdti:1234567890.12.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/253/1234567890123", 10)
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:gdti:1234567.89012.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/253/1234567890123")
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:gdti:843848923823..*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/253/8438489238239", 12)
            .get("asURN"));
    // TODO: find out why this test is failing
    // assertEquals("urn:epc:idpat:gdti:8438489238.23.*",
    // converterUtil.toURNForClassLevelIdentifier("https://google.fb.org/253/8438489238239").get("asURN"));

    // Class level URN to Web URI conversion

    // Class level GDTI URN with more than 13 digits
    gdti = "urn:epc:idpat:gdti:8438489238.233.*";
    TestIdentifiers.toURIForClassLevelIdentifier(gdti);

    // Class level GDTI URN with less than 13 digits
    gdti = "urn:epc:idpat:gdti:843848923.23.*";
    TestIdentifiers.toURIForClassLevelIdentifier(gdti);

    // Class level GDTI URN with invalid characters
    gdti = "urn:epc:idpat:gdti:8438A89238.23.*";
    TestIdentifiers.toURIForClassLevelIdentifier(gdti);

    // Class level GDTI URN with invalid GCP less than 6 digits
    gdti = "urn:epc:idpat:gdti:43948.483943.*";
    TestIdentifiers.toURIForClassLevelIdentifier(gdti);

    // Class level GDTI URN with invalid GCP more than 12 digits
    gdti = "urn:epc:idpat:gdti:4394834839433..*";
    TestIdentifiers.toURIForClassLevelIdentifier(gdti);

    // Class level GDTI URN with serial numbers
    gdti = "urn:epc:idpat:gdti:1234567890.12.999";
    TestIdentifiers.toURIForClassLevelIdentifier(gdti);

    // Class level GDTI URN without *
    gdti = "urn:epc:idpat:gdti:439483483943..";
    TestIdentifiers.toURIForClassLevelIdentifier(gdti);

    // Valid Class level GDTI URN
    assertEquals(
        "https://id.gs1.org/253/4394834839438",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:gdti:439483483943..*"));
    assertEquals(
        "https://id.gs1.org/253/4748343847383",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:gdti:474834.384738.*"));
    assertEquals(
        "https://id.gs1.org/253/6374637643768",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:gdti:637463764.376.*"));
    assertEquals(
        "https://id.gs1.org/253/5757834883747",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:gdti:57578348837.4.*"));
  }
}
