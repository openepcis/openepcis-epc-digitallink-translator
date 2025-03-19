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
import io.openepcis.epc.translator.exception.ValidationException;
import org.junit.Before;
import org.junit.Test;

public class CPITest {

  private Converter converter;

  @Before
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void CPI() throws ValidationException {

    // CPI without serial
    String cpi = "urn:epc:id:cpi:1234567890.1234";
    TestIdentifiers.toDigitalLink(cpi);

    // CPI with less than 7 digits
    cpi = "urn:epc:id:cpi:123456..1111";
    TestIdentifiers.toDigitalLink(cpi);

    // CPI with more than 31 digits
    cpi = "urn:epc:id:cpi:123456.7890123456789012345678901.1111";
    TestIdentifiers.toDigitalLink(cpi);

    // CPI with invalid characters in Serial number
    cpi = "urn:epc:id:cpi:123456.7890.111A";
    TestIdentifiers.toDigitalLink(cpi);

    // CPI with invalid GCP
    cpi = "urn:epc:id:cpi:1234567890123.7890.111A";
    TestIdentifiers.toDigitalLink(cpi);

    // Valid CPI
    assertEquals(
        converter.toURI("urn:epc:id:cpi:3813667.83201294-5A.489332"),
        "https://id.gs1.org/8010/381366783201294-5A/8011/489332");
    assertEquals(
        converter.toURI("urn:epc:id:cpi:123456.789012345.1111"),
        "https://id.gs1.org/8010/123456789012345/8011/1111");
    assertEquals(
        converter.toURI("urn:epc:id:cpi:0614141.123ABC.123456789"),
        "https://id.gs1.org/8010/0614141123ABC/8011/123456789");
    assertEquals(
        converter.toURI("urn:epc:id:cpi:6282274.234748947/.94304"),
        "https://id.gs1.org/8010/6282274234748947//8011/94304");

    // Invlaid characters in CPI URI
    cpi = "https://id.gs1.org/8010/12345678901234A/8011/1111";
    TestIdentifiers.toURN(cpi, 5);

    // CPI URI with invalid GCP Length
    cpi = "https://id.gs1.org/8010/12345678901234/8011/1111";
    TestIdentifiers.toURN(cpi, 5);

    // CPI less than GCP Length
    cpi = "https://id.gs1.org/8010/1234567890/8011/1111";
    TestIdentifiers.toURN(cpi, 12);

    // CPI without serial number
    cpi = "https://id.gs1.org/8010/123456789012/8011/";
    TestIdentifiers.toURN(cpi, 10);

    // Valid CPI
    assertEquals(
        "urn:epc:id:cpi:12345678.A9012.1010",
        converter.toURN("https://id.gs1.org/8010/12345678A9012/8011/1010", 8).get("asURN"));
    assertEquals(
        "urn:epc:id:cpi:1234567890.12.1010",
        converter.toURN("https://id.gs1.org/8010/123456789012/8011/1010", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:cpi:1234567890.12.1010",
        converter.toURN("https://benelog.com/8010/123456789012/8011/1010", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:cpi:1234567890.ANC.124",
        converter.toURN("https://benelog.com/8010/1234567890ANC/8011/124", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:cpi:4748374./23748#94/.94304",
        converter.toURN("https://id.gs1.org/8010/4748374/23748#94//8011/94304", 7).get("asURN"));
    assertEquals(
        "urn:epc:id:cpi:30056867.890#1294-5A.4893",
        converter.toURN("https://id.gs1.org/8010/30056867890#1294-5A/8011/4893").get("asURN"));

    /** Conversion of class level identifiers */

    // Class level Web URI to URN

    // Invalid Class level CPI Web URI with more than 30 digits
    cpi = "https://id.gs1.org/8010/123456-789012345678901234567890";
    TestIdentifiers.toURNForClassLevelIdentifier(cpi, 10);

    // Invalid Class level CPI Web URI with GCP more than CPI length
    cpi = "https://id.gs1.org/8010/1234567";
    TestIdentifiers.toURNForClassLevelIdentifier(cpi, 10);

    cpi = "https://id.gs1.org/8010/123457-5757";
    TestIdentifiers.toURNForClassLevelIdentifier(cpi, 12);

    // Invalid class level CPI Web URI with invalid characters in CPI
    cpi = "https://id.gs1.org/8010/12345678901234*";
    TestIdentifiers.toURNForClassLevelIdentifier(cpi, 6);

    cpi = "https://id.gs1.org/8010/1234567890123456789012345k7890";
    TestIdentifiers.toURNForClassLevelIdentifier(cpi, 6);

    // Class level CPI with invalid invalid GCP Length
    cpi = "https://id.gs1.org/8010/12345678901234";
    TestIdentifiers.toURNForClassLevelIdentifier(cpi, 5);
    TestIdentifiers.toURNForClassLevelIdentifier(cpi, 13);

    // Valid class level CPI Web URI
    assertEquals(
        "urn:epc:idpat:cpi:1234567890.1234.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/8010/12345678901234", 10)
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:cpi:1234567890.1234.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/8010/12345678901234", 10)
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:cpi:123457-57578..*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/8010/123457-57578", 12)
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:cpi:858588503943.4/38.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/8010/8585885039434/38", 12)
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:cpi:952088.5039434/38.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/8010/9520885039434/38")
            .get("asURN"));

    // Class level URN to Web URI

    // Class level CPI without serial
    cpi = "urn:epc:idpat:cpi:3636636.36636366363";
    TestIdentifiers.toURIForClassLevelIdentifier(cpi);

    // Class level CPI with more than 12 digits GCP
    cpi = "urn:epc:idpat:cpi:1234567890123.123.*";
    TestIdentifiers.toURIForClassLevelIdentifier(cpi);

    // Class level CPI with less than 6 digits GCP
    cpi = "urn:epc:idpat:cpi:12345.36636366363.*";
    TestIdentifiers.toURIForClassLevelIdentifier(cpi);

    // Class level CPI with more than 30 digits
    cpi = "urn:epc:idpat:cpi:123456.7890123456789012345678901.*";
    TestIdentifiers.toURIForClassLevelIdentifier(cpi);

    // Class level CPI with serial number
    cpi = "urn:epc:idpat:cpi:123456.36636366363.123";
    TestIdentifiers.toURIForClassLevelIdentifier(cpi);

    // Valid class level CPI
    cpi = "urn:epc:idpat:cpi:123456.78901234.*";
    assertEquals(
        "https://id.gs1.org/8010/12345678901234", converter.toURIForClassLevelIdentifier(cpi));

    // Valid class level CPI URN
    assertEquals(
        "https://id.gs1.org/8010/8585885039434/38",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:cpi:858588503943.4/38.*"));
    assertEquals(
        "https://id.gs1.org/8010/43848374387483",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:cpi:438483.74387483.*"));
    assertEquals(
        "https://id.gs1.org/8010/164759476414",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:cpi:164759476414..*"));
    assertEquals(
        "https://id.gs1.org/8010/79478348934889348934EJEJIEDJE",
        converter.toURIForClassLevelIdentifier(
            "urn:epc:idpat:cpi:794783489348.89348934EJEJIEDJE.*"));
  }
}
