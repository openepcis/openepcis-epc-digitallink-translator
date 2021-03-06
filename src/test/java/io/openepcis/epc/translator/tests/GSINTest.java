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

public class GSINTest {

  @Test
  public void GSIN() throws ValidationException {
    // GSIN with invalid characters
    String gsin = "urn:epc:id:gsin:12345678A0.123456";
    TestIdentifiers.toDigitalLink(gsin);

    // GSIN with less than 6 digits GCP
    gsin = "urn:epc:id:gsin:12345.123456";
    TestIdentifiers.toDigitalLink(gsin);

    // GSIN with more than 17 digits
    gsin = "urn:epc:id:gsin:1234567890.1234567";
    TestIdentifiers.toDigitalLink(gsin);

    // GSIN with less than 17 digits
    gsin = "urn:epc:id:gsin:1234567890.12345";
    TestIdentifiers.toDigitalLink(gsin);

    // GSIN with invalid characters
    gsin = "urn:epc:id:gsin:1234567890.12345A";
    TestIdentifiers.toDigitalLink(gsin);

    // GSIN with invalid characters in GCP
    gsin = "urn:epc:id:gsin:12345A.7890123456";
    TestIdentifiers.toDigitalLink(gsin);

    // Valid GSIN
    assertEquals(
        ConverterUtil.toURI("urn:epc:id:gsin:123456.7890123456"),
        "https://id.gs1.org/402/12345678901234560");
    assertEquals(
        ConverterUtil.toURI("urn:epc:id:gsin:483984.3984398439"),
        "https://id.gs1.org/402/48398439843984392");
    assertEquals(
        ConverterUtil.toURI("urn:epc:id:gsin:859485945045.0454"),
        "https://id.gs1.org/402/85948594504504549");

    // GSIN URI with invalid characters
    gsin = "https://id.gs1.org/402/1234567890512345A";
    TestIdentifiers.toURN(gsin, 6);

    // GSIN with less than 17 digits
    gsin = "https://id.gs1.org/402/1234567890512345";
    TestIdentifiers.toURN(gsin, 6);

    // GSIN with more than 17 digits
    gsin = "https://id.gs1.org/402/123456789051234567";
    TestIdentifiers.toURN(gsin, 6);

    // GSIN with invalid GCP
    gsin = "https://id.gs1.org/402/95485984950459045";
    TestIdentifiers.toURN(gsin, 5);
    TestIdentifiers.toURN(gsin, 13);

    // Valid GSIN
    assertEquals(
        "urn:epc:id:gsin:1234567890.512345",
        ConverterUtil.toURN("https://id.gs1.org/402/12345678905123456", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:gsin:1234567890.512345",
        ConverterUtil.toURN("https://benelog.com/horrem/402/12345678905123456", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:gsin:843958935895.3939",
        ConverterUtil.toURN("https://benelog.com/horrem/402/84395893589539394").get("asURN"));
    assertEquals(
        "urn:epc:id:gsin:9549854.549583498",
        ConverterUtil.toURN("https://benelog.com/horrem/402/95498545495834983").get("asURN"));
    assertEquals(
        "urn:epc:id:gsin:954859.8495045904",
        ConverterUtil.toURN("https://https://id.gs1.org/402/95485984950459045", 6).get("asURN"));
  }
}
