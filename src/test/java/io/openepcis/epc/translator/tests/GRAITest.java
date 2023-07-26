/*
 * Copyright 2022-2023 benelog GmbH & Co. KG
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

public class GRAITest {

  private Converter converter;

  @Before
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void testGRAI() throws ValidationException {

    // GRAI with invalid GS1 syntax
    String grai = "urn:epc:i:grai:1234567890.12.ABC";
    TestIdentifiers.toDigitalLink(grai);

    // GRAI without serial number
    grai = "urn:epc:id:grai:1234567890.123";
    TestIdentifiers.toDigitalLink(grai);

    // GRAI with more less than 6 digit gcp
    grai = "urn:epc:id:grai:12345.12.4ABC";
    TestIdentifiers.toDigitalLink(grai);

    // GRAI with more less than 12 digit gcp
    grai = "urn:epc:id:grai:1234567890123.12.4ABC";
    TestIdentifiers.toDigitalLink(grai);

    // GRAI not equal to 14 digits
    grai = "urn:epc:id:grai:123456.12.4ABC";
    TestIdentifiers.toDigitalLink(grai);

    // GRAI with invalid characters in GCP
    grai = "urn:epc:id:grai:123456789A.12.4ABC";
    TestIdentifiers.toDigitalLink(grai);

    // Valid GRAI
    assertEquals(
        "https://id.gs1.org/8003/12345678901284ABC",
        converter.toURI("urn:epc:id:grai:1234567890.12.4ABC"));
    assertEquals(
        "https://id.gs1.org/8003/1234567890128!\"%&'()*+,-.",
        converter.toURI("urn:epc:id:grai:123456789012..!\"%&'()*+,-."));
    assertEquals(
        "https://id.gs1.org/8003/43847837483703:;<>=?AZ_az",
        converter.toURI("urn:epc:id:grai:438478.374837.3:;<>=?AZ_az"));
    assertEquals(
        "https://id.gs1.org/8003/12345678901284",
        converter.toURI("urn:epc:id:grai:123456789012..4"));

    // GRAI URI with invalid domain
    grai = "hps://id.gs1.org/8003/12345678901284ABC";
    TestIdentifiers.toURN(grai, 6);

    // GRAI URI with GRAI less than 14 digits
    grai = "https://id.gs1.org/8003/1234567890123";
    TestIdentifiers.toURN(grai, 6);

    // GRAI with GCP length more than 12 digits
    grai = "https://id.gs1.org/8003/12345678901234";
    TestIdentifiers.toURN(grai, 13);

    // GRAI with valid URI
    assertEquals(
        "urn:epc:id:grai:123456.789012.4ABCD",
        converter.toURN("https://id.gs1.org/8003/12345678901234ABCD", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:grai:123456.789012.4ABCD",
        converter.toURN("https://youtube.com.org/8003/12345678901234ABCD", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:grai:1234567890.12.AABC",
        converter.toURN("https://id.gs1.org/8003/1234567890123AABC", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:grai:438478.374837.3:;<>=?AZ_az",
        converter.toURN("https://id.gs1.org/8003/43847837483703:;<>=?AZ_az", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:grai:123456789012..!\"%&'()*+,-.",
        converter.toURN("https://id.gs1.org/8003/1234567890128!\"%&'()*+,-.", 12).get("asURN"));
    assertEquals(
        "urn:epc:id:grai:1234567890.12.4",
        converter.toURN("https://id.gs1.org/8003/12345678901284").get("asURN"));

    /** GRAI Class identifier validations */

    // Web URI to URN conversion

    // Class level GRAI with more than 13 digits
    grai = "https://id.gs1.org/8003/12345678901234";
    TestIdentifiers.toURNForClassLevelIdentifier(grai, 7);
    TestIdentifiers.toURNForClassLevelIdentifier(grai);

    // Class level GRAI with less than 13 digit
    grai = "https://id.gs1.org/8003/123456789012";
    TestIdentifiers.toURNForClassLevelIdentifier(grai, 7);
    TestIdentifiers.toURNForClassLevelIdentifier(grai);

    // Class level GRAI with invalid characters
    grai = "https://id.gs1.org/8003/123456789012A";
    TestIdentifiers.toURNForClassLevelIdentifier(grai, 7);
    TestIdentifiers.toURNForClassLevelIdentifier(grai);

    // Class level GRAI with invalid prefix
    grai = "https://id.gs1.org/80030/123456789012A";
    TestIdentifiers.toURNForClassLevelIdentifier(grai, 7);
    TestIdentifiers.toURNForClassLevelIdentifier(grai);

    // Class level GRAI with invalid GCP
    grai = "https://id.gs1.org/8003/1234567890123";
    TestIdentifiers.toURNForClassLevelIdentifier(grai, 5);
    TestIdentifiers.toURNForClassLevelIdentifier(grai, 13);

    // Class level GRAI with valid identifiers
    assertEquals(
        "urn:epc:idpat:grai:1234567890.12.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/8003/1234567890123", 10)
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:grai:1234567890.12.*",
        converter
            .toURNForClassLevelIdentifier("https://id.gs1.org/8003/1234567890123")
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:grai:784283728372..*",
        converter
            .toURNForClassLevelIdentifier("https://id.example.com/8003/7842837283728", 12)
            .get("asURN"));
    assertEquals(
        "urn:epc:idpat:grai:7842837.28372.*",
        converter
            .toURNForClassLevelIdentifier("https://id.example.com/8003/7842837283728")
            .get("asURN"));

    // Conversion from URN to Web URI

    // Class level GRAI URN with invalid character
    grai = "urn:epc:idpat:grai:7842837.A8372.*";
    TestIdentifiers.toURIForClassLevelIdentifier(grai);

    // Class level GRAI URN with invalid GCP length
    grai = "urn:epc:idpat:grai:7842837283723..*";
    TestIdentifiers.toURIForClassLevelIdentifier(grai);

    // Class level GRAI with invalid GRAI length
    grai = "urn:epc:idpat:grai:784283.28372.*";
    TestIdentifiers.toURIForClassLevelIdentifier(grai);

    // Class level GRAI without serial *
    grai = "urn:epc:idpat:grai:1234567890.12.";
    TestIdentifiers.toURIForClassLevelIdentifier(grai);

    // Class level GRAI with serial number
    grai = "urn:epc:idpat:grai:1234567890.12.124";
    TestIdentifiers.toURIForClassLevelIdentifier(grai);

    // Valid Class level GRAI to Web URI conversion
    assertEquals(
        "https://id.gs1.org/8003/4843847384737",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:grai:4843847.38473.*"));
    assertEquals(
        "https://id.gs1.org/8003/8493394839844",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:grai:8493394.83984.*"));
    assertEquals(
        "https://id.gs1.org/8003/8439483984392",
        converter.toURIForClassLevelIdentifier("urn:epc:idpat:grai:8439483.98439.*"));
  }
}
