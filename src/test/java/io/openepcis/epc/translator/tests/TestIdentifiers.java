/*
 * Copyright 2022-2023 benelog GmbH & Co. KG
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

import static org.junit.Assert.assertThrows;

import io.openepcis.epc.translator.Converter;
import io.openepcis.epc.translator.exception.ValidationException;

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
