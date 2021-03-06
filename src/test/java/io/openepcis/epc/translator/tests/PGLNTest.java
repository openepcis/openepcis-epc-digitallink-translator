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

import io.openepcis.epc.translator.ConverterUtil;
import io.openepcis.epc.translator.ValidationException;
import org.junit.Test;

public class PGLNTest {

  @Test
  public void testPGLN() throws ValidationException {

    // Invalid GS1 syntax
    String pgln = "urn:ec:id:pgln:123456.789012";
    TestIdentifiers.toDigitalLink(pgln);

    // PGLN with invalid GCP
    pgln = "urn:epc:id:pgln:1234A6.789012";
    TestIdentifiers.toDigitalLink(pgln);

    // PGLN with GCP more than 12 digits
    pgln = "urn:epc:id:pgln:1234567890123.789012";
    TestIdentifiers.toDigitalLink(pgln);

    // PGLN with GCP less than 6 digits
    pgln = "urn:epc:id:pgln:12345.789012";
    TestIdentifiers.toDigitalLink(pgln);

    // PGLN with PGLN less than 13 digits
    pgln = "urn:epc:id:pgln:123456.78901";
    TestIdentifiers.toDigitalLink(pgln);

    // PGLN with PGLN more than 13 digits
    pgln = "urn:epc:id:pgln:123456.789012345";
    TestIdentifiers.toDigitalLink(pgln);

    // Valid PGLN
    assertEquals(
        "https://id.gs1.org/417/1234567890128",
        ConverterUtil.toURI("urn:epc:id:pgln:123456.789012"));
    assertEquals(
        "https://id.gs1.org/417/4738478374830",
        ConverterUtil.toURI("urn:epc:id:pgln:473847.837483"));
    assertEquals(
        "https://id.gs1.org/417/8598394945028",
        ConverterUtil.toURI("urn:epc:id:pgln:859839494502."));
    assertEquals(
        "https://id.gs1.org/417/0394039403940",
        ConverterUtil.toURI("urn:epc:id:pgln:0394039.40394"));

    // PGLN URI with invalid domain name
    pgln = "hps://id.gs1.org/417/1234567890123";
    TestIdentifiers.toURN(pgln, 6);

    // PGLN URI with more than 13 digit
    pgln = "https://id.gs1.org/417/12345678901234";
    TestIdentifiers.toURN(pgln, 6);

    // PGLN URI with less than 13 digit
    pgln = "https://id.gs1.org/417/123456789012";
    TestIdentifiers.toURN(pgln, 6);

    // PGLN URI with wrong code
    pgln = "https://id.gs1.org/415/1234567890123";
    TestIdentifiers.toURN(pgln, 6);

    // PGLN with GCP Length less than 6 digits
    pgln = "https://id.gs1.org/417/1234567890128";
    TestIdentifiers.toURN(pgln, 5);
    pgln = "https://id.gs1.org/417/1234567890128";
    TestIdentifiers.toURN(pgln, 13);

    // Valid PGLN
    assertEquals(
        "urn:epc:id:pgln:123456.789012",
        ConverterUtil.toURN("https://id.gs1.org/417/1234567890128", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:pgln:123456.789012",
        ConverterUtil.toURN("https://horrem.kerpen.de/417/1234567890128", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:pgln:5457456.74657",
        ConverterUtil.toURN("https://horrem.kerpen.de/417/5457456746574").get("asURN"));
    assertEquals(
        "urn:epc:id:pgln:4524637.82938",
        ConverterUtil.toURN("https://horrem.kerpen.de/417/4524637829387").get("asURN"));
    assertEquals(
        "urn:epc:id:pgln:534536435643.",
        ConverterUtil.toURN("https://horrem.kerpen.de/417/5345364356436", 12).get("asURN"));
  }
}
