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

public class SSCCTest {

  @Test
  public void testSSCC() throws ValidationException {

    // SSCC with less than 18 digits
    String sscc = "urn:epc:id:sscc:123456789.0666386";
    TestIdentifiers.toDigitalLink(sscc);

    // SSCC with more than 18 digits
    sscc = "urn:epc:id:sscc:123456789.066638689";
    TestIdentifiers.toDigitalLink(sscc);

    // SSCC with GCP less than 6 digits
    sscc = "urn:epc:id:sscc:12345.066638689";
    TestIdentifiers.toDigitalLink(sscc);

    // SSCC with GCP more than 12 digits
    sscc = "urn:epc:id:sscc:7438473847381.7483";
    TestIdentifiers.toDigitalLink(sscc);

    // SSCC with invalid characters in GCP
    sscc = "urn:epc:id:sscc:12345A.066638689";
    TestIdentifiers.toDigitalLink(sscc);

    // SCC with invalid characters in SSCC
    sscc = "urn:epc:id:sscc:123456.0666386898A";
    TestIdentifiers.toDigitalLink(sscc);

    // SSCC with invalid syntax
    sscc = "urn:epc::sscc:123456.066638689";
    TestIdentifiers.toDigitalLink(sscc);

    // Valid SSCC
    assertEquals(
        "https://id.gs1.org/00/012345666638689852",
        ConverterUtil.toURI("urn:epc:id:sscc:123456.06663868985"));
    assertEquals(
        "https://id.gs1.org/00/474384738473874832",
        ConverterUtil.toURI("urn:epc:id:sscc:743847384738.47483"));
    assertEquals(
        "https://id.gs1.org/00/758758475845784857",
        ConverterUtil.toURI("urn:epc:id:sscc:587584.77584578485"));
    assertEquals(
        "https://id.gs1.org/00/994039403490349033",
        ConverterUtil.toURI("urn:epc:id:sscc:9403940349.9034903"));

    // SSCC URI with more than 18 digits
    sscc = "https://id.gs1.org/00/0123456789012345678";
    TestIdentifiers.toURN(sscc, 6);

    // SSCC URI with less than 18 digits
    sscc = "https://id.gs1.org/00/01234567890123456";
    TestIdentifiers.toURN(sscc, 6);

    // SSCC URI with invalid characters in SSCC
    sscc = "https://id.gs1.org/00/01234567890123456A";
    TestIdentifiers.toURN(sscc, 6);

    // SSCC with GCP Length more than 12 digits
    sscc = "https://id.gs1.org/00/012345678901234567";
    TestIdentifiers.toURN(sscc, 13);

    // SSCC with GCP Length less than 6 digits
    sscc = "https://id.gs1.org/00/012345678901234567";
    TestIdentifiers.toURN(sscc, 5);

    // Valid SSCC URI
    assertEquals(
        "urn:epc:id:sscc:123456.07890123456",
        ConverterUtil.toURN("https://id.gs1.org/00/012345678901234567", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:sscc:123456.07890123456",
        ConverterUtil.toURN("https://marriot.in/blr/123/00/012345678901234567", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:sscc:9403940349.9034903",
        ConverterUtil.toURN("https://marriot.in/blr/123/00/994039403490349033", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:sscc:587584.77584578485",
        ConverterUtil.toURN("https://marriot.in/blr/123/00/758758475845784857", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:sscc:5875847.7584578485",
        ConverterUtil.toURN("https://marriot.in/blr/123/00/758758475845784857").get("asURN"));
    assertEquals(
        "urn:epc:id:sscc:73467346.667373473",
        ConverterUtil.toURN("https://marriot.in/blr/123/00/673467346673734737").get("asURN"));
  }
}
