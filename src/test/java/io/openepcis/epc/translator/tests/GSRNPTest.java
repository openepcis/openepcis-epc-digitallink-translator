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

public class GSRNPTest {

  private Converter converter;

  @Before
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void testGSRNP() throws ValidationException {

    // Invalid GSRNP syntax
    String gsrnp = "un:epc:id:gsrnp:1234567890.1234567";
    TestIdentifiers.toDigitalLink(gsrnp);

    // GSRNP with invalid characters in GCP
    gsrnp = "urn:epc:id:gsrnp:123456789A.1234567";
    TestIdentifiers.toDigitalLink(gsrnp);

    // GSRNP with more than 12 digits GCP
    gsrnp = "urn:epc:id:gsrnp:1234567890123.1234567";
    TestIdentifiers.toDigitalLink(gsrnp);

    // GSRNP with more than 18 digits
    gsrnp = "urn:epc:id:gsrnp:1234567890.123456712";
    TestIdentifiers.toDigitalLink(gsrnp);

    // GSRNP with less than 18 digits
    gsrnp = "urn:epc:id:gsrnp:1234567890.123456712";
    TestIdentifiers.toDigitalLink(gsrnp);

    // GSRNP without GCP
    gsrnp = "urn:epc:id:gsrnp:1234567890123456712";
    TestIdentifiers.toDigitalLink(gsrnp);

    // Valid GSRNP
    assertEquals(
        "https://id.gs1.org/8017/123456789012345675",
        converter.toURI("urn:epc:id:gsrnp:1234567890.1234567"));
    assertEquals(
        "https://id.gs1.org/8017/843984934394394932",
        converter.toURI("urn:epc:id:gsrnp:843984.93439439493"));
    assertEquals(
        "https://id.gs1.org/8017/578457847548758479",
        converter.toURI("urn:epc:id:gsrnp:578457847548.75847"));
    assertEquals(
        "https://id.gs1.org/8017/345425435235243521",
        converter.toURI("urn:epc:id:gsrnp:3454254352.3524352"));

    // GSRNP URI with invalid character
    gsrnp = "https://id.gs1.org/8017/12345678901234567A";
    TestIdentifiers.toURN(gsrnp, 10);

    // GSRNP URI with more than 18 digits
    gsrnp = "https://id.gs1.org/8017/1234567890123456789";
    TestIdentifiers.toURN(gsrnp, 10);

    // GSRNP URI with less than 18 digits
    gsrnp = "https://id.gs1.org/8017/12345678901234567";
    TestIdentifiers.toURN(gsrnp, 10);

    // GSRNP URI with GCP more than 12
    gsrnp = "https://id.gs1.org/8017/123456789012345678";
    TestIdentifiers.toURN(gsrnp, 13);

    // Valid GSRNP
    assertEquals(
        "urn:epc:id:gsrnp:1234567890.1234567",
        converter.toURN("https://id.gs1.org/8017/123456789012345678", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:gsrnp:1234567890.1234567",
        converter.toURN("https://google.com/8017/123456789012345678", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:gsrnp:643963.76437473634",
        converter.toURN("https://google.com/8017/643963764374736343").get("asURN"));
    assertEquals(
        "urn:epc:id:gsrnp:645933932413.12123",
        converter.toURN("https://google.com/8017/645933932413121231").get("asURN"));
    assertEquals(
        "urn:epc:id:gsrnp:667602231213.12123",
        converter.toURN("https://google.com/8017/667602231213121237", 12).get("asURN"));
  }
}
