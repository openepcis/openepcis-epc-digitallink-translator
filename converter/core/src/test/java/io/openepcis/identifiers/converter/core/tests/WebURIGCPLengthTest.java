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

import io.openepcis.core.exception.UnsupportedGS1IdentifierException;
import io.openepcis.digitallink.utils.DefaultGCPLengthProvider;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WebURIGCPLengthTest {

  @Test
  public void gcpLengthTest() {
    // Valid GS1 Identifiers
    String identifier1 = "https://id.gs1.org/8010/12345678A9012/8011/1010"; // GCP 10
    String identifier2 = "https://benelog.com/8010/123456789012/8011/1010"; // GCP 10
    String identifier3 = "https://id.gs1.org/8010/30056867890#1294-5A/8011/4893"; // GCP 8
    String identifier4 = "https://id.gs1.org/255/12345678901281234"; // GCP 10
    String identifier5 = "https://id.gs1.org/255/12345678901234"; // GCP 10
    String identifier6 = "https://id.gs1.org/01/1234567890123";
    String identifier7 = "/255/12345678901234"; // GCP 10
    String identifier8 = "/01/1234567890123/21/12345678"; // GCP 10
    String identifier9 = "01/1234567890123/21/12345678"; // GCP 10
    String identifier10 = "http://localhost:8080/414/1234567890999";

    // Invalid GS1 Identifiers
    String identifier20 = "bad identifier";
    String identifier14 = "urn:epc:id:sgtin:4012345.012345.1234"; // URN
    String identifier15 = ""; // Empty string
    String identifier16 = null; // Null
    String identifier17 = "https://example.com/badprefix/1234567890"; // Invalid prefix
    String identifier18 = "https://example.com/01/"; // No identifier value
    String identifier19 = "https://example.com/8010/"; // No identifier value

    final DefaultGCPLengthProvider detector = DefaultGCPLengthProvider.getInstance();

    // Test valid identifiers
    assertEquals(10, detector.getGcpLength(identifier1));
    assertEquals(10, detector.getGcpLength(identifier2));
    assertEquals(8, detector.getGcpLength(identifier3));
    assertEquals(10, detector.getGcpLength(identifier4));
    assertEquals(10, detector.getGcpLength(identifier6));
    assertEquals(10, detector.getGcpLength(identifier5));
    assertEquals(10, detector.getGcpLength(identifier6));
    assertEquals(10, detector.getGcpLength(identifier7));
    assertEquals(10, detector.getGcpLength(identifier8));
    assertEquals(10, detector.getGcpLength(identifier9));
    assertEquals(10, detector.getGcpLength(identifier10));

    // Test invalid identifiers
    testInvalidIdentifier(detector, identifier20);
    testInvalidIdentifier(detector, identifier14);
    testInvalidIdentifier(detector, identifier15);
    testInvalidIdentifier(detector, identifier16);
    testInvalidIdentifier(detector, identifier17);
    testInvalidIdentifier(detector, identifier18);
    testInvalidIdentifier(detector, identifier19);
  }

  private static UnsupportedGS1IdentifierException testInvalidIdentifier(
      final DefaultGCPLengthProvider detector, final String identifier) {
    return assertThrows(
        UnsupportedGS1IdentifierException.class, () -> detector.getGcpLength(identifier));
  }
}
