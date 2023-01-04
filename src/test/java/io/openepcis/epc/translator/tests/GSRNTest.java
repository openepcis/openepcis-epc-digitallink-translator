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

public class GSRNTest {

  private Converter converter;

  @Before
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void testGSRN() throws ValidationException {

    // GSRN with invalid characters in gcp
    String gsrn = "urn:epc:id:gsrn:1234567A90.1234567";
    TestIdentifiers.toDigitalLink(gsrn);

    // GSRN with less than 18 digits
    gsrn = "urn:epc:id:gsrn:1234567890.123456";
    TestIdentifiers.toDigitalLink(gsrn);

    // GSRN with more than 18 digits
    gsrn = "urn:epc:id:gsrn:1234567890.12345678";
    TestIdentifiers.toDigitalLink(gsrn);

    // GSRN without GCP
    gsrn = "urn:epc:id:gsrn:123456789012345678";
    TestIdentifiers.toDigitalLink(gsrn);

    // Valid GSRN
    assertEquals(
        converter.toURI("urn:epc:id:gsrn:1234567890.1234567"),
        "https://id.gs1.org/8018/123456789012345675");
    assertEquals(
        converter.toURI("urn:epc:id:gsrn:142512.45142152511"),
        "https://id.gs1.org/8018/142512451421525110");
    assertEquals(
        converter.toURI("urn:epc:id:gsrn:673674637437.47783"),
        "https://id.gs1.org/8018/673674637437477836");
    assertEquals(
        converter.toURI("urn:epc:id:gsrn:654683828.92302309"),
        "https://id.gs1.org/8018/654683828923023096");

    // GSRN URI with more than 18 characters
    gsrn = "https://id.gs1.org/8018/1234567890123456751";
    TestIdentifiers.toURN(gsrn, 10);

    // GSRN URI with less than 18 characters
    gsrn = "https://id.gs1.org/8018/12345678901234567";
    TestIdentifiers.toURN(gsrn, 10);

    // GSRN URI with invalid characters
    gsrn = "https://id.gs1.org/8018/12345678901234567A";
    TestIdentifiers.toURN(gsrn, 10);

    // GSRN URI with invalid GCP
    gsrn = "https://id.gs1.org/8018/123456789012345675";
    TestIdentifiers.toURN(gsrn, 5);

    // Valid GSRN URI
    assertEquals(
        converter.toURN("https://id.gs1.org/8018/123456789012345675", 10).get("asURN"),
        "urn:epc:id:gsrn:1234567890.1234567");
    assertEquals(
        converter.toURN("https://hp.com/laptop/8018/123456789012345675", 10).get("asURN"),
        "urn:epc:id:gsrn:1234567890.1234567");
    assertEquals(
        converter.toURN("https://hp.com/laptop/8018/146037675245142514").get("asURN"),
        "urn:epc:id:gsrn:1460376.7524514251");
    assertEquals(
        converter.toURN("https://hp.com/laptop/8018/659041753828392839").get("asURN"),
        "urn:epc:id:gsrn:6590417.5382839283");
    assertEquals(
        converter.toURN("https://hp.com/laptop/8018/654673483739829829", 11).get("asURN"),
        "urn:epc:id:gsrn:65467348373.982982");
  }
}
