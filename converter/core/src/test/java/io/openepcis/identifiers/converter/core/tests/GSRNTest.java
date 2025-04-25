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
    assertEquals("urn:epc:id:gsrn:301037.67524514251", converter.toURN("https://hp.com/laptop/8018/301037675245142514", 6).get("asURN"));
    assertEquals("urn:epc:id:gsrn:302241.75382839283", converter.toURN("https://hp.com/laptop/8018/302241753828392839").get("asURN"));
    assertEquals("urn:epc:id:gsrn:65467348373.982982", converter.toURN("https://hp.com/laptop/8018/654673483739829829", 11).get("asURN"));
  }
}
