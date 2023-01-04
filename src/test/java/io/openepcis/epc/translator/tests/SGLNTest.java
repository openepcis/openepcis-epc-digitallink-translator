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

public class SGLNTest {

  private Converter converter;

  @Before
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void testSGLN() throws ValidationException {

    // SGLN with invalid GS1 format
    String sgln = "urn:epc::sgln:1234567890.1.1111";
    TestIdentifiers.toDigitalLink(sgln);

    // SGLN with invalid GCP
    sgln = "urn:epc:id:sgln:1234567890AS.1.1111";
    TestIdentifiers.toDigitalLink(sgln);

    // SGLN with GCP less than 6 digits
    sgln = "urn:epc:id:sgln:1234.1.1111";
    TestIdentifiers.toDigitalLink(sgln);

    // SGLN with GCP more than 12 digits
    sgln = "urn:epc:id:sgln:12345678901234.1.1111";
    TestIdentifiers.toDigitalLink(sgln);

    // SGLN with less than 13 digits SGLN
    sgln = "urn:epc:id:sgln:1234567890.1.1111";
    TestIdentifiers.toDigitalLink(sgln);

    // SGLN with more than 13 digits SGLN
    sgln = "urn:epc:id:sgln:12345678901.11.1111";
    TestIdentifiers.toDigitalLink(sgln);

    // SGLN without Serial Number
    sgln = "urn:epc:id:sgln:1234567890.11";
    TestIdentifiers.toDigitalLink(sgln);

    // Valid SGLN with serial Number
    assertEquals(
        "https://id.gs1.org/414/1234567890111/254/1111",
        converter.toURI("urn:epc:id:sgln:1234567890.11.1111"));
    assertEquals(
        "https://id.gs1.org/414/4374736473640/254/\"%&'()*+,-./19:;<=>?",
        converter.toURI("urn:epc:id:sgln:437473647364..\"%&'()*+,-./19:;<=>?"));
    assertEquals(
        "https://id.gs1.org/414/4374736473640/254/0",
        converter.toURI("urn:epc:id:sgln:437473647364..0"));
    assertEquals(
        "https://id.gs1.org/414/7857834384782/254/0394903",
        converter.toURI("urn:epc:id:sgln:785783.438478.0394903"));
    assertEquals(
        "https://id.gs1.org/414/4374736473640/254/0",
        converter.toURI("urn:epc:id:sgln:4374736473.64.0"));

    // SGLN URI with invalid format
    sgln = "hps://id.gs1.org/414/1234567890123/254/1111";
    TestIdentifiers.toURN(sgln, 6);

    // SGLN with 12 digit SGLN
    sgln = "https://id.gs1.org/414/123456789012/254/1111";
    TestIdentifiers.toURN(sgln, 6);

    // SGLN with GCP less than 6 digits
    sgln = "https://id.gs1.org/414/1234567890123/254/1111";
    TestIdentifiers.toURN(sgln, 5);
    sgln = "https://id.gs1.org/414/1234567890123/254/1111";
    TestIdentifiers.toURN(sgln, 13);

    // SGLN with 14 digits
    sgln = "https://id.gs1.org/414/12345678901234/254/1111";
    TestIdentifiers.toURN(sgln, 12);

    // SGLN with invalid characters
    sgln = "https://id.gs1.org/414/123456789012A/254/1111";
    TestIdentifiers.toURN(sgln, 12);

    // SGLN with invalid serial
    sgln = "https://id.gs1.org/414/5893849384938/254";
    TestIdentifiers.toURN(sgln, 12);

    // Valid SGLN URI with serial number
    assertEquals(
        "urn:epc:id:sgln:123456.789012.1234",
        converter.toURN("https://id.gs1.org/414/1234567890123/254/1234", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:sgln:123456.789012.1234",
        converter
            .toURN("https://deutscheBahn.de/train/414/1234567890123/254/1234", 6)
            .get("asURN"));
    assertEquals(
        "urn:epc:id:sgln:93590743.8478.0394903",
        converter.toURN("https://id.gs1.org/414/9359074384782/254/0394903").get("asURN"));
    assertEquals(
        "urn:epc:id:sgln:93590443.8478.0",
        converter.toURN("https://id.gs1.org/414/9359044384782").get("asURN"));
    assertEquals(
        "urn:epc:id:sgln:93592393.8493.4390493",
        converter.toURN("https://id.gs1.org/414/9359239384938/254/4390493").get("asURN"));
    assertEquals(
        "urn:epc:id:sgln:688000938493..0",
        converter.toURN("https://id.gs1.org/414/6880009384938", 12).get("asURN"));
    assertEquals(
        "urn:epc:id:sgln:4374736473.64.0",
        converter.toURN("https://id.gs1.org/414/4374736473640", 10).get("asURN"));
  }
}
