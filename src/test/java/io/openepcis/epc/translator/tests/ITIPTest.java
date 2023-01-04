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

public class ITIPTest {

  private Converter converter;

  @Before
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void ITIP() throws ValidationException {

    // ITIP with less than 18 digits
    String itip = "urn:epc:id:itip:234567.189012.56.78.1111";
    TestIdentifiers.toDigitalLink(itip);

    // ITIP with invalid characters
    itip = "urn:epc:id:itip:234567.189012A.56.78.1111";
    TestIdentifiers.toDigitalLink(itip);

    // ITIP without serial numbers
    itip = "urn:epc:id:itip:234567.1890123.56.78";
    TestIdentifiers.toDigitalLink(itip);

    // ITIP with more than 18 digits
    itip = "urn:epc:id:itip:234567.1890123.561.78.1111";
    TestIdentifiers.toDigitalLink(itip);

    // ITIP with GCP less than 12 digits
    itip = "urn:epc:id:itip:23456.1890123.56.78.1111";
    TestIdentifiers.toDigitalLink(itip);

    // ITIP with Invalid character in GCP
    itip = "urn:epc:id:itip:23456A.1890123.56.78.1111";
    TestIdentifiers.toDigitalLink(itip);

    // ITIP with Invalid digit serparation
    itip = "urn:epc:id:itip:234567.1890123..78.1111";
    TestIdentifiers.toDigitalLink(itip);
    itip = "urn:epc:id:itip:234567.1890123.56..1111";
    TestIdentifiers.toDigitalLink(itip);

    // Valid ITIP
    assertEquals(
        "https://id.gs1.org/8006/123456789012315678/21/1111",
        converter.toURI("urn:epc:id:itip:234567.1890123.56.78.1111"));
    assertEquals(
        "https://id.gs1.org/8006/646734858938493844/21/!\"%&'()*+,-./1:;<=>=",
        converter.toURI("urn:epc:id:itip:467348.6589384.38.44.!\"%&'()*+,-./1:;<=>="));
    assertEquals(
        "https://id.gs1.org/8006/758348347384713474/21/5785",
        converter.toURI("urn:epc:id:itip:583483473847.7.34.74.5785"));
    assertEquals(
        "https://id.gs1.org/8006/635645365356453564/21/85945894",
        converter.toURI("urn:epc:id:itip:3564536535.664.35.64.85945894"));

    // ITIP with less than 18 digit URI
    itip = "https://id.gs1.org/8006/12345678901235678/21/1111";
    TestIdentifiers.toURN(itip, 6);

    // ITIP with invalid characters in URI
    itip = "https://id.gs1.org/8006/12345678901235678A/21/1111";
    TestIdentifiers.toURN(itip, 6);

    // ITIP without serial numbers
    itip = "https://id.gs1.org/8006/123456789012356787";
    TestIdentifiers.toURN(itip, 6);

    // ITIP with invalid GCP Length
    itip = "https://id.gs1.org/8006/123456789012356787/21/1111";
    TestIdentifiers.toURN(itip, 5);

    // Valid ITIP
    assertEquals(
        "urn:epc:id:itip:2345678901.123.67.87.1111", converter.toURN(itip, 10).get("asURN"));
    assertEquals(
        "urn:epc:id:itip:2345678901.123.67.87.1111",
        converter
            .toURN("https://riseagainst.com/songs/8006/123456789012356787/21/1111", 10)
            .get("asURN"));
    assertEquals(
        "urn:epc:id:itip:467348.6589384.38.44.!\"%&'()*+,-./1:;<=>=",
        converter
            .toURN("https://id.gs1.org/8006/646734858938493844/21/!\"%&'()*+,-./1:;<=>=", 6)
            .get("asURN"));
    assertEquals(
        "urn:epc:id:itip:8706185.356565.56.65.85945894",
        converter.toURN("https://id.gs1.org/8006/387061855656575665/21/85945894").get("asURN"));
    assertEquals(
        "urn:epc:id:itip:9044841.552323.83.93.547564756",
        converter.toURN("https://id.gs1.org/8006/590448415232328393/21/547564756").get("asURN"));

    /** Class level ITIP identifiers conversion */

    // Class level Web URI to URN conversion

    // Class level ITIP Web URI with more than 18 digits
    itip = "https://id.gs1.org/8006/1234567890123456789";
    TestIdentifiers.toURNForClassLevelIdentifier(itip);
    TestIdentifiers.toURNForClassLevelIdentifier(itip, 10);

    // Class level ITIP Web URI with less than 18 digits
    itip = "https://id.gs1.org/8006/12345678901234567";
    TestIdentifiers.toURNForClassLevelIdentifier(itip);
    TestIdentifiers.toURNForClassLevelIdentifier(itip, 11);

    // Class level ITIP Web URI with invalid characters
    itip = "https://id.gs1.org/8006/12345678901234567A";
    TestIdentifiers.toURNForClassLevelIdentifier(itip);
    TestIdentifiers.toURNForClassLevelIdentifier(itip, 12);

    // Class level ITIP Web URI with invalid GCP Length
    itip = "https://id.gs1.org/8006/123456789012345678";
    TestIdentifiers.toURNForClassLevelIdentifier(itip, 5);
    TestIdentifiers.toURNForClassLevelIdentifier(itip, 13);

    // Class level ITIP Web URI with invalid prefix
    itip = "https://id.gs1.org/8101/123456789012345678";
    TestIdentifiers.toURNForClassLevelIdentifier(itip, 6);

    itip = "/8006/123456789012345678";
    TestIdentifiers.toURNForClassLevelIdentifier(itip, 12);

    // Class level ITIP Web URI with serial numbers
    itip = "https://riseagainst.com/songs/8006/123456789012356787/21/1111";
    TestIdentifiers.toURNForClassLevelIdentifier(itip, 12);

    // Valid Class level WebURI
    assertEquals(
        "urn:epc:idpat:itip:2345678901.123.56.78.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/8006/123456789012345678", 10)
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:itip:9589231.440988.65.44.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/8006/495892314098876544")
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:itip:439483.8934948.34.34.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/8006/843948393494893434", 6)
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:itip:943549030943.3.04.93.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/8006/394354903094390493", 12)
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:itip:9046452.563344.45.45.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/8006/590464526334474545")
            .get("asURN"));

    // URN to Web URI conversion

    // Class level ITIP URN with more than 18 digits
    itip = "urn:epc:idpat:itip:2345678901.1234.56.78.*";
    TestIdentifiers.toURIForClassLevelIdentifier(itip);

    // Class level ITIP URN with less than 18 digits
    itip = "urn:epc:idpat:itip:234567890.123.56.78.*";
    TestIdentifiers.toURIForClassLevelIdentifier(itip);

    // Class level ITIP URN with invalid characters
    itip = "urn:epc:idpat:itip:943549A.330943.04.93.*";
    TestIdentifiers.toURIForClassLevelIdentifier(itip);

    // Class level ITIP URN with GCP less than 6 digits
    itip = "urn:epc:idpat:itip:94354.3030943.04.93.*";
    TestIdentifiers.toURIForClassLevelIdentifier(itip);

    // Class level ITIP URN with GCP more than 12 digits
    itip = "urn:epc:idpat:itip:9435490309431.3.04.93.*";
    TestIdentifiers.toURIForClassLevelIdentifier(itip);

    // Class level ITIP URN with serial number
    itip = "urn:epc:idpat:itip:943549030943.3.04.93.124";
    TestIdentifiers.toURIForClassLevelIdentifier(itip);

    // Class level ITIP URN without *
    itip = "urn:epc:idpat:itip:943549030943.3.04.93.";
    TestIdentifiers.toURIForClassLevelIdentifier(itip);

    // Class level ITIP URN invalid prefix
    itip = "urn:epc:idr:itip:943549030.3943.04.93.*";
    TestIdentifiers.toURIForClassLevelIdentifier(itip);
    itip = "urn:epc:idpat:itip1:943549030.3943.04.93.*";
    TestIdentifiers.toURIForClassLevelIdentifier(itip);

    // Class level ITIP URN with serial number
    itip = "urn:epc:id:itip:2345678901.123.67.87.1111";
    TestIdentifiers.toURIForClassLevelIdentifier(itip);

    // Valid class level ITIP URN
    assertEquals(
        "https://id.gs1.org/8006/394354903094320493",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:itip:943549030.3943.04.93.*"));
    assertEquals(
        "https://id.gs1.org/8006/748347837483483489",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:itip:483478374834.7.34.89.*"));
    assertEquals(
        "https://id.gs1.org/8006/748347834783449293",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:itip:483478.7347834.92.93.*"));
    assertEquals(
        "https://id.gs1.org/8006/349349439943004039",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:itip:493494.3399430.40.39.*"));
  }
}
