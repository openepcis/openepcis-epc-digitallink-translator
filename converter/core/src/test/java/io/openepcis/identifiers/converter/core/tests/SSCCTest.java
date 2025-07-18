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

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.converter.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SSCCTest {

  private Converter converter;

  @BeforeEach
  public void before() throws Exception {
    converter = new Converter();
  }

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
        converter.toURI("urn:epc:id:sscc:123456.06663868985"));
    assertEquals(
        "https://id.gs1.org/00/474384738473874832",
        converter.toURI("urn:epc:id:sscc:743847384738.47483"));
    assertEquals(
        "https://id.gs1.org/00/758758475845784857",
        converter.toURI("urn:epc:id:sscc:587584.77584578485"));
    assertEquals(
        "https://id.gs1.org/00/994039403490349033",
        converter.toURI("urn:epc:id:sscc:9403940349.9034903"));

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
        converter.toURN("https://id.gs1.org/00/012345678901234560", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:sscc:123456.07890123456",
        converter.toURN("https://marriot.in/blr/123/00/012345678901234560", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:sscc:9403940349.9034903",
        converter.toURN("https://marriot.in/blr/123/00/994039403490349033", 10).get("asURN"));
    assertEquals(
        "urn:epc:id:sscc:587584.77584578485",
        converter.toURN("https://marriot.in/blr/123/00/758758475845784857", 6).get("asURN"));
    assertEquals(
        "urn:epc:id:sscc:3665847.9584578485",
        converter.toURN("https://marriot.in/blr/123/00/936658475845784850").get("asURN"));
    assertEquals(
        "urn:epc:id:sscc:93594046.867373473",
        converter.toURN("https://marriot.in/blr/123/00/893594046673734738").get("asURN"));
  }
}
