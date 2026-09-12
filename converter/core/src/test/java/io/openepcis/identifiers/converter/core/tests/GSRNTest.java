/*
 * Copyright 2022-2026 benelog GmbH & Co. KG
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
package io.openepcis.identifiers.converter.core.tests;

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.converter.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GSRNTest {

  private Converter converter;

  @BeforeEach
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
    assertEquals("https://id.gs1.org/8018/123456789012345675", converter.toURI("urn:epc:id:gsrn:1234567890.1234567"));
    assertEquals("https://id.gs1.org/8018/142512451421525110", converter.toURI("urn:epc:id:gsrn:142512.45142152511"));
    assertEquals("https://id.gs1.org/8018/673674637437477836", converter.toURI("urn:epc:id:gsrn:673674637437.47783"));
    assertEquals("https://id.gs1.org/8018/654683828923023096", converter.toURI("urn:epc:id:gsrn:654683828.92302309"));

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
    assertEquals("urn:epc:id:gsrn:1234567890.1234567", converter.toURN("https://id.gs1.org/8018/123456789012345675", 10).get("asURN"));
    assertEquals("urn:epc:id:gsrn:1234567890.1234567", converter.toURN("https://hp.com/laptop/8018/123456789012345675", 10).get("asURN"));
    assertEquals("urn:epc:id:gsrn:301037.67524514251", converter.toURN("https://hp.com/laptop/8018/301037675245142512", 6).get("asURN"));
    assertEquals("urn:epc:id:gsrn:302241.75382839283", converter.toURN("https://hp.com/laptop/8018/302241753828392832").get("asURN"));
    assertEquals("urn:epc:id:gsrn:65467348373.982982", converter.toURN("https://hp.com/laptop/8018/654673483739829824", 11).get("asURN"));
  }
}
