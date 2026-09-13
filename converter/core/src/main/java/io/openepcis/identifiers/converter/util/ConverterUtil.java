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
package io.openepcis.identifiers.converter.util;

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.converter.Converter;
import java.util.Map;
// Class to use the Converter method in a static way.
public class ConverterUtil {
  private static final Converter converter;

  static {
    converter = new Converter();
  }

  // Check through each class and find URN belongs to which particular class
  public static String toURI(final String urn) throws ValidationException {
    return converter.toURI(urn);
  }

  // Check through each class and find DL URI belongs to which particular class
  public static Map<String, String> toURN(final String dlURI, final int gcpLength) throws ValidationException {
    return converter.toURN(dlURI, gcpLength);
  }

  // Check through each class and find DL URI belongs to which particular class
  public static Map<String, String> toURN(final String dlURI) throws ValidationException {
    return converter.toURN(dlURI);
  }

  // Check through each class and find URN belongs to which particular class
  public static String toURIForClassLevelIdentifier(final String urn) throws ValidationException {
    return converter.toURIForClassLevelIdentifier(urn);
  }

  // Check through each class and find DL URI belongs to which particular class
  public static Map<String, String> toURNForClassLevelIdentifier(final String dlURI) throws ValidationException {
    return converter.toURNForClassLevelIdentifier(dlURI);
  }

  // Check through each class and find DL URI belongs to which particular class
  public static Map<String, String> toURNForClassLevelIdentifier(final String dlURI, final int gcpLength) throws ValidationException {
    return converter.toURNForClassLevelIdentifier(dlURI, gcpLength);
  }

  // Convert the CBV URN formatted vocabularies into WebURI vocabulary. Used during event hash
  // generator.
  public static String toWebURIVocabulary(final String urnVocabulary) {
    return converter.toWebURIVocabulary(urnVocabulary);
  }

  // Convert the CBV WebURI formatted vocabularies into URN vocabulary. Used during JSON -> XML.
  public static String toUrnVocabulary(final String webUriVocabulary) {
    return converter.toUrnVocabulary(webUriVocabulary);
  }

  // Convert the CBV URN/WebURI formatted vocabularies into BareString vocabulary. Used during XML
  // -> JSON conversion.
  public static String toBareStringVocabulary(final String eventVocabulary) {
    return converter.toBareStringVocabulary(eventVocabulary);
  }

  // Convert bareString values to CBV formatted vocabularies. Used during JSON -> XML conversion.
  public static String toCbvVocabulary(final String bareString, final String fieldName, final String format) {
    return converter.toCbvVocabulary(bareString, fieldName, format);
  }

  // Convert the short names with corresponding identifier
  public static String shortNameReplacer(final String gs1Identifier) {
    return converter.shortNameReplacer(gs1Identifier);
  }

  /**
   * The GS1 mod-10 check digit for a body of digits.
   *
   * <p>Delegates to {@link io.openepcis.digitallink.utils.Gs1CheckDigit}, which is the
   * single home for this arithmetic. This method used to carry its own copy; it was
   * verified to agree with the other two implementations before being pointed there,
   * so the behaviour is unchanged.
   */
  public static char checksum(final String s) {
    return io.openepcis.digitallink.utils.Gs1CheckDigit.of(s);
  }

  private ConverterUtil() {
  }
}
