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

public class GINCTest {

  private Converter converter;

  @Before
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void GINC() throws ValidationException {

    // Invalid GS1 Syntax
    String ginc = "urn:epc:i:ginc:1234567890.12ABC";
    TestIdentifiers.toDigitalLink(ginc);

    // GINC with invalid characters in GCP
    ginc = "urn:epc:id:ginc:1234567890A.12ABC";
    TestIdentifiers.toDigitalLink(ginc);

    // GINC without serial number
    ginc = "urn:epc:id:ginc:1234567890";
    TestIdentifiers.toDigitalLink(ginc);
    ginc = "urn:epc:id:ginc:1234567890";
    TestIdentifiers.toDigitalLink(ginc);

    // GINC more than 30 characters
    ginc = "urn:epc:id:ginc:1234567890.123456789012345678901";
    TestIdentifiers.toDigitalLink(ginc);

    // Valid GINC URN
    assertEquals(
        "https://id.gs1.org/401/12345678901234",
        converter.toURI("urn:epc:id:ginc:1234567890.1234"));
    assertEquals("https://id.gs1.org/401/4738473", converter.toURI("urn:epc:id:ginc:473847.3"));
    assertEquals(
        "https://id.gs1.org/401/4849304738473", converter.toURI("urn:epc:id:ginc:484930473847.3"));
    assertEquals(
        "https://id.gs1.org/401/4849304738473!\"%/%&'()*+,-.:=",
        converter.toURI("urn:epc:id:ginc:48493047.38473!\"%/%&'()*+,-.:="));
    assertEquals(
        "https://id.gs1.org/401/4849304738473!\"\"%/%&'()*+,-.:=",
        converter.toURI("urn:epc:id:ginc:48493047.38473!\"\"%/%&'()*+,-.:="));

    // GINC less than GCP Length
    ginc = "https://id.gs1.org/401/12345678901";
    TestIdentifiers.toURN(ginc, 12);

    // GINC with GCP Length more than 12
    ginc = "https://id.gs1.org/401/123456789012";
    TestIdentifiers.toURN(ginc, 15);

    // Invalid Characters in GCP
    ginc = "https://id.gs1.org/401/12345678A012";
    TestIdentifiers.toURN(ginc, 10);

    // Invalid Web URI Prefix
    ginc = "https://id.gs1.org/405/1234567890.1234&/";
    TestIdentifiers.toURN(ginc, 10);

    assertEquals(
        "urn:epc:id:ginc:1234567890.1234A",
        converter.toURN("https://id.gs1.org/401/12345678901234A", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:ginc:1234567890.1234A",
        converter.toURN("https://eclipse.org/401/12345678901234A", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:ginc:48493047.38473!\"\"%/%&'()*+,-.:=",
        converter.toURN("https://id.gs1.org/401/4849304738473!\"\"%/%&'()*+,-.:=", 8).get("asURN"));
    assertEquals(
        "urn:epc:id:ginc:656987789012.",
        converter.toURN("https://id.gs1.org/401/656987789012").get("asURN"));
    assertEquals(
        "urn:epc:id:ginc:123456789012.d",
        converter.toURN("https://id.gs1.org/401/123456789012d", 12).get("asURN"));
  }
}
