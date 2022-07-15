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

public class UPUITest {

  @Test
  public void UPUI() throws ValidationException {

    // UPUI with less than 14 digit
    String upui = "urn:epc:id:upui:234567.189012.1111";
    TestIdentifiers.toDigitalLink(upui);

    // UPUI with invalid characters
    upui = "urn:epc:id:upui:234567.189012A.1111";
    TestIdentifiers.toDigitalLink(upui);

    // UPUI with more than 14 digits
    upui = "urn:epc:id:upui:234567.18901234.1111";
    TestIdentifiers.toDigitalLink(upui);

    // UPUI without serial number
    upui = "urn:epc:id:upui:234567.18901234";
    TestIdentifiers.toDigitalLink(upui);

    // Valid UPUI
    assertEquals(
        "https://id.gs1.org/01/12345678901231/235/1111ANC",
        ConverterUtil.toURI("urn:epc:id:upui:234567.1890123.1111ANC"));
    assertEquals(
        "https://id.gs1.org/01/78574584574857/235/!\"%&'()*+,-./",
        ConverterUtil.toURI("urn:epc:id:upui:857458457485.7.!\"%&'()*+,-./"));
    assertEquals(
        "https://id.gs1.org/01/57875874837438/235/19:;<=>?AZ_az",
        ConverterUtil.toURI("urn:epc:id:upui:787587.5483743.19:;<=>?AZ_az"));
    assertEquals(
        "https://id.gs1.org/01/78578348384737/235/8398439",
        ConverterUtil.toURI("urn:epc:id:upui:857834.7838473.8398439"));

    // UPUI URI with less than 14 digits
    upui = "https://id.gs1.org/01/1234567890123/235/1111ANC";
    TestIdentifiers.toURN(upui, 10);

    // UPUI URI with invalid characters
    upui = "https://id.gs1.org/01/1234567890123A/235/1111ANC";
    TestIdentifiers.toURN(upui, 10);

    // UPUI URI without serial numbers
    upui = "https://id.gs1.org/01/12345678901231/235/";
    TestIdentifiers.toURN(upui, 10);

    // UPUI with invalid GCP Lenghth
    upui = "https://id.gs1.org/01/12345678901231/235/1111ANC";
    TestIdentifiers.toURN(upui, 13);

    // Valid UPUI
    assertEquals(
        "urn:epc:id:upui:234567.1890123.1111ANC",
        ConverterUtil.toURN("https://id.gs1.org/01/12345678901231/235/1111ANC", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:upui:234567.1890123.1111ANC",
        ConverterUtil.toURN("https://maps.google.com.in.de.00/12/01/12345678901231/235/1111ANC", 6)
            .get("asURN"));
    assertEquals(
        "urn:epc:id:upui:857458457485.7.!\"%&'()*+,-./",
        ConverterUtil.toURN("https://id.gs1.org/01/78574584574857/235/!\"%&'()*+,-./", 12)
            .get("asURN"));
    assertEquals(
        "urn:epc:id:upui:787587.5483743.19:;<=>?AZ_az",
        ConverterUtil.toURN("https://id.gs1.org/01/57875874837438/235/19:;<=>?AZ_az", 6)
            .get("asURN"));
    assertEquals(
        "urn:epc:id:upui:857834838473.7.8398439",
        ConverterUtil.toURN("https://id.gs1.org/01/78578348384737/235/8398439").get("asURN"));
    assertEquals(
        "urn:epc:id:upui:5362325.532635.0230293",
        ConverterUtil.toURN("https://id.gs1.org/01/55362325326356/235/0230293").get("asURN"));
  }
}
