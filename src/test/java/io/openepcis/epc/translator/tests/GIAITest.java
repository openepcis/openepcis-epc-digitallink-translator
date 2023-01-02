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

public class GIAITest {

  private Converter converter;

  @Before
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void giaiTest() throws ValidationException {

    // GIAI with invalid GS1 syntax
    String giai = "urn:ec:id:giai:1234567890";
    TestIdentifiers.toDigitalLink(giai);

    // GIAI without serial number
    giai = "urn:epc:id:giai:1234567890";
    TestIdentifiers.toDigitalLink(giai);

    // GIAI with invalid characters in GCP
    giai = "urn:epc:id:giai:123456789A.123";
    TestIdentifiers.toDigitalLink(giai);

    // GIAI with less than 6 digits GCP
    giai = "urn:epc:id:giai:12345.123";
    TestIdentifiers.toDigitalLink(giai);

    // GIAI with more than 12 digits GCP
    giai = "urn:epc:id:giai:1234567890123.123";
    TestIdentifiers.toDigitalLink(giai);

    // Valid GIAI
    assertEquals(
        "https://id.gs1.org/8004/123456789012123",
        converter.toURI("urn:epc:id:giai:123456789012.123"));
    assertEquals("https://id.gs1.org/8004/1234567", converter.toURI("urn:epc:id:giai:123456.7"));
    assertEquals(
        "https://id.gs1.org/8004/8394958495850", converter.toURI("urn:epc:id:giai:839495849585.0"));
    assertEquals(
        "https://id.gs1.org/8004/8394958495850!\"/%_",
        converter.toURI("urn:epc:id:giai:839495849585.0!\"/%_"));

    // GIAI with invalid URI format
    giai = "/8004/123456789012123";
    TestIdentifiers.toURN(giai, 10);

    // GIAI with invalid characters
    giai = "https://id.gs1.org/8004/123A6789012123";
    TestIdentifiers.toURN(giai, 10);

    // GIAI with gcp less than 6
    giai = "https://id.gs1.org/8004/123456789012123";
    TestIdentifiers.toURN(giai, 5);

    // GIAI with gcp more than 12
    giai = "https://id.gs1.org/8004/123456789012123";
    TestIdentifiers.toURN(giai, 13);

    assertEquals(
        "urn:epc:id:giai:1234567890.12123",
        converter.toURN("https://id.gs1.org/8004/123456789012123", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:giai:1234567890.12123",
        converter.toURN("https://news.google.in/8004/123456789012123", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:giai:8394958.495850!\"/%_",
        converter.toURN("https://id.gs1.org/8004/8394958495850!\"/%_").get("asURN"));
    assertEquals(
        "urn:epc:id:giai:732487.8;><=?",
        converter.toURN("https://id.gs1.org/8004/7324878;><=?").get("asURN"));
  }
}
