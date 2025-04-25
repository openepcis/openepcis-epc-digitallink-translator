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

import static org.junit.Assert.assertThrows;

import io.openepcis.identifiers.converter.Converter;
import io.openepcis.identifiers.validator.exception.ValidationException;

public class TestIdentifiers {

  private static final Converter converter = new Converter();

  public static ValidationException toDigitalLink(final String urn) {
    return assertThrows(ValidationException.class, () -> converter.toURI(urn));
  }

  public static ValidationException toURN(final String uri, final int gcpLength) {
    return assertThrows(ValidationException.class, () -> converter.toURN(uri, gcpLength));
  }

  // Throw error for validation of class level identifiers during the conversion from URN - Web URI
  public static ValidationException toURIForClassLevelIdentifier(final String urn) {
    return assertThrows(
        ValidationException.class, () -> converter.toURIForClassLevelIdentifier(urn));
  }

  // Throw error for validation of class level identifiers during the conversion from Web URI - URN
  public static ValidationException toURNForClassLevelIdentifier(
      final String uri, final int gcpLength) {
    return assertThrows(
        ValidationException.class, () -> converter.toURNForClassLevelIdentifier(uri, gcpLength));
  }

  public static ValidationException toURNForClassLevelIdentifier(final String uri) {
    return assertThrows(
        ValidationException.class, () -> converter.toURNForClassLevelIdentifier(uri));
  }
}
