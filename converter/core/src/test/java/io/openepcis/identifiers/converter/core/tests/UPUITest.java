/*
 * Copyright (c) 2022-2025 benelog GmbH & Co. KG
 * All rights reserved.
 *
 * Unauthorized copying, modification, distribution,
 * or use of this work, via any medium, is strictly prohibited.
 *
 * benelog GmbH & Co. KG reserves all rights not expressly granted herein,
 * including the right to sell licenses for using this work.
 */
package io.openepcis.identifiers.converter.core.tests;

import static org.junit.Assert.assertEquals;

import io.openepcis.identifiers.converter.Converter;
import io.openepcis.identifiers.validator.exception.ValidationException;
import org.junit.Before;
import org.junit.Test;

public class UPUITest {

  private Converter converter;

  @Before
  public void before() throws Exception {
    converter = new Converter();
  }

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
        converter.toURI("urn:epc:id:upui:234567.1890123.1111ANC"));
    assertEquals(
        "https://id.gs1.org/01/78574584574857/235/!\"%&'()*+,-./",
        converter.toURI("urn:epc:id:upui:857458457485.7.!\"%&'()*+,-./"));
    assertEquals(
        "https://id.gs1.org/01/57875874837438/235/19:;<=>?AZ_az",
        converter.toURI("urn:epc:id:upui:787587.5483743.19:;<=>?AZ_az"));
    assertEquals(
        "https://id.gs1.org/01/78578348384737/235/8398439",
        converter.toURI("urn:epc:id:upui:857834.7838473.8398439"));

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
        converter.toURN("https://id.gs1.org/01/12345678901231/235/1111ANC", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:upui:234567.1890123.1111ANC",
        converter
            .toURN("https://maps.google.com.in.de.00/12/01/12345678901231/235/1111ANC", 6)
            .get("asURN"));
    assertEquals(
        "urn:epc:id:upui:857458457485.7.!\"%&'()*+,-./",
        converter
            .toURN("https://id.gs1.org/01/78574584574857/235/!\"%&'()*+,-./", 12)
            .get("asURN"));
    assertEquals(
        "urn:epc:id:upui:787587.5483743.19:;<=>?AZ_az",
        converter.toURN("https://id.gs1.org/01/57875874837438/235/19:;<=>?AZ_az", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:upui:93594438.84737.8398439",
        converter.toURN("https://id.gs1.org/01/89359443847373/235/8398439").get("asURN"));
    assertEquals(
        "urn:epc:id:upui:351152.9532635.0230293",
        converter.toURN("https://id.gs1.org/01/93511525326356/235/0230293").get("asURN"));
  }
}
