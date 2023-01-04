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

public class GCNTest {

  private Converter converter;

  @Before
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void SGCN() throws ValidationException {

    // SGCN without serial number
    String sgcn = "urn:epc:id:sgcn:1234567890.12";
    TestIdentifiers.toDigitalLink(sgcn);

    // SGCN with less than 14 digits
    sgcn = "urn:epc:id:sgcn:1234567890.1.1234";
    TestIdentifiers.toDigitalLink(sgcn);

    // SGCN with more than 14 digits
    sgcn = "urn:epc:id:sgcn:1234567890.123.1234";
    TestIdentifiers.toDigitalLink(sgcn);

    // Invalid characters in SGCN
    sgcn = "urn:epc:id:sgcn:1234567890.1A.1234";
    TestIdentifiers.toDigitalLink(sgcn);

    // Invalid characters in Serial
    sgcn = "urn:epc:id:sgcn:1234567890.12.1234A";
    TestIdentifiers.toDigitalLink(sgcn);

    // SGCN with invalid GCP
    sgcn = "urn:epc:id:sgcn:123456789012.12.1234";
    TestIdentifiers.toDigitalLink(sgcn);

    // Valid SGCN
    assertEquals(
        "https://id.gs1.org/255/12345678901281234",
        converter.toURI("urn:epc:id:sgcn:1234567890.12.1234"));
    assertEquals(
        "https://id.gs1.org/255/123456789012845678901234",
        converter.toURI("urn:epc:id:sgcn:1234567890.12.45678901234"));
    assertEquals(
        "https://id.gs1.org/255/43943943493924",
        converter.toURI("urn:epc:id:sgcn:439439434939..4"));
    assertEquals(
        "https://id.gs1.org/255/548594859485295495849",
        converter.toURI("urn:epc:id:sgcn:5485948594.85.95495849"));

    // SGCN URI with less than 14 digits
    sgcn = "https://id.gs1.org/255/1234567890123";
    TestIdentifiers.toURN(sgcn, 10);

    // SGCN with invalid GCP length
    sgcn = "https://id.gs1.org/255/12345678901234";
    TestIdentifiers.toURN(sgcn, 13);

    // SGCN with more than 25 digits
    sgcn = "https://id.gs1.org/255/12345678901234567890123456";
    TestIdentifiers.toURN(sgcn, 12);

    // Invalid characters in SGCN
    sgcn = "https://id.gs1.org/255/12345678901234A";
    TestIdentifiers.toURN(sgcn, 12);

    // Valid SGCN URI
    assertEquals(
        converter.toURN("https://id.gs1.org/255/12345678901234", 12).get("asURN"),
        "urn:epc:id:sgcn:123456789012..4");
    assertEquals(
        converter.toURN("https://gs1.in/255/12345678901234", 12).get("asURN"),
        "urn:epc:id:sgcn:123456789012..4");
    assertEquals(
        converter.toURN("https://id.gs1.org/255/43943943493924", 6).get("asURN"),
        "urn:epc:id:sgcn:439439.434939.4");
    assertEquals(
        converter.toURN("https://id.gs1.org/255/43943943493924", 12).get("asURN"),
        "urn:epc:id:sgcn:439439434939..4");
    assertEquals(
        converter.toURN("https://id.gs1.org/255/300096758845486").get("asURN"),
        "urn:epc:id:sgcn:300096.758845.86");

    /** Class level GCN identifier conversion */

    // Class level Web URI to URN conversion

    // Class level GCN Web URI with more than 13 digits
    sgcn = "https://id.gs1.org/255/12345678901234";
    TestIdentifiers.toURNForClassLevelIdentifier(sgcn);
    TestIdentifiers.toURNForClassLevelIdentifier(sgcn, 6);

    // Class level GCN Web URI with more than 13 digits
    sgcn = "https://id.gs1.org/255/123456789012";
    TestIdentifiers.toURNForClassLevelIdentifier(sgcn);
    TestIdentifiers.toURNForClassLevelIdentifier(sgcn, 8);

    // Class level GCN Web URI with invalid characters
    sgcn = "https://id.gs1.org/255/123456789A123";
    TestIdentifiers.toURNForClassLevelIdentifier(sgcn);
    TestIdentifiers.toURNForClassLevelIdentifier(sgcn, 7);

    // Class level GCN Web URI with invalid GCP
    sgcn = "https://id.gs1.org/255/1234567890123";
    TestIdentifiers.toURNForClassLevelIdentifier(sgcn, 5);
    TestIdentifiers.toURNForClassLevelIdentifier(sgcn, 14);

    // Class level GCN Web URI with invalid prefix
    sgcn = "https://id.gs1.org/2554/1234567890123";
    TestIdentifiers.toURNForClassLevelIdentifier(sgcn);
    TestIdentifiers.toURNForClassLevelIdentifier(sgcn, 11);

    // Valid class level GCN Web URI
    assertEquals(
        "urn:epc:idpat:sgcn:123456789.012.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/255/1234567890123", 9)
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:sgcn:656256789012..*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/255/6562567890123")
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:sgcn:283892.329328.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/255/2838923293289", 6)
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:sgcn:757845748574..*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/255/7578457485748", 12)
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:sgcn:93588154.8574.*",
        converter
            .toURNForClassLevelIdentifier("https://example.com/255/9358815485748")
            .get("asURN"));

    // Class level URN to Web URI Conversion

    // Class level GCN with more than 13 digits
    sgcn = "urn:epc:idpat:sgcn:1234567.890981.*";
    TestIdentifiers.toURIForClassLevelIdentifier(sgcn);

    // Class level GCN with less than 13 digits
    sgcn = "urn:epc:idpat:sgcn:7578457.4857.*";
    TestIdentifiers.toURIForClassLevelIdentifier(sgcn);

    // Class level GCN with invalid characters
    sgcn = "urn:epc:idpat:sgcn:7578457.4857A.*";
    TestIdentifiers.toURIForClassLevelIdentifier(sgcn);

    // Class level GCN with GCP less than 6 digits
    sgcn = "urn:epc:idpat:sgcn:12345.789098.*";
    TestIdentifiers.toURIForClassLevelIdentifier(sgcn);

    // Class level GCN with GCP more than 12 digits
    sgcn = "urn:epc:idpat:sgcn:1234567890981..*";
    TestIdentifiers.toURIForClassLevelIdentifier(sgcn);

    // Class level GCN with serial numbers
    sgcn = "urn:epc:idpat:sgcn:12345678.9012.123";
    TestIdentifiers.toURIForClassLevelIdentifier(sgcn);

    // Class level GCN without *
    sgcn = "urn:epc:idpat:sgcn:12345678.9012.";
    TestIdentifiers.toURIForClassLevelIdentifier(sgcn);

    // Class level GCN with invalid prefix
    sgcn = "urn:epc:idpat:sgcnn:12345678.9012.*";
    TestIdentifiers.toURIForClassLevelIdentifier(sgcn);

    // Valid class level GCN URN
    assertEquals(
        "https://id.gs1.org/255/1234567890128",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:sgcn:12345678.9012.*"));
    assertEquals(
        "https://id.gs1.org/255/4343884394893",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:sgcn:434388.439489.*"));
    assertEquals(
        "https://id.gs1.org/255/7438748374382",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:sgcn:743874837438..*"));
    assertEquals(
        "https://id.gs1.org/255/5787674634636",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:sgcn:5787674634.63.*"));
  }
}
