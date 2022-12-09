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
package io.openepcis.epc.translator;

import io.openepcis.epc.translator.converter.*;
import io.openepcis.epc.translator.exception.UnsupportedGS1IdentifierException;
import io.openepcis.epc.translator.exception.ValidationException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Converter {

  private final String INVALID_URI_MESSAGE =
      "Provided URI format does not match with any of the GS1 identifiers format.%nPlease check the URI: %s";
  private final Set<io.openepcis.epc.translator.converter.Converter> DL = new HashSet<>();
  private final Set<io.openepcis.epc.translator.converter.Converter> CLASS_LEVEL_TRANSLATOR =
      new HashSet<>();

  public Converter() {
    // Add all EPC instance-level converter
    DL.add(new SGTINConverter());
    DL.add(new SSCCConverter());
    DL.add(new SGLNConverter());
    DL.add(new GRAIConverter());
    DL.add(new GIAIConverter());
    DL.add(new GSRNConverter());
    DL.add(new GSRNPConverter());
    DL.add(new GDTIConverter());
    DL.add(new CPIConverter());
    DL.add(new GCNConverter());
    DL.add(new GINCConverter());
    DL.add(new GSINConverter());
    DL.add(new ITIPConverter());
    DL.add(new UPUIConverter());
    DL.add(new PGLNConverter());

    // Add all EPC class-level converter
    CLASS_LEVEL_TRANSLATOR.add(new LGTINConverter());
    CLASS_LEVEL_TRANSLATOR.add(new SGTINConverter(true));
    CLASS_LEVEL_TRANSLATOR.add(new GRAIConverter(true));
    CLASS_LEVEL_TRANSLATOR.add(new GDTIConverter(true));
    CLASS_LEVEL_TRANSLATOR.add(new GCNConverter(true));
    CLASS_LEVEL_TRANSLATOR.add(new CPIConverter(true));
    CLASS_LEVEL_TRANSLATOR.add(new ITIPConverter(true));
  }

  // Check through each class and find URN belongs to which particular class
  public String toURI(final String urn) throws ValidationException {
    for (io.openepcis.epc.translator.converter.Converter uri : DL) {
      if (uri.supportsDigitalLinkURI(urn)) {
        return uri.convertToDigitalLink(urn);
      }
    }
    throw new UnsupportedGS1IdentifierException(
        String.format(
            "Provided URN format does not match with any of the GS1 identifiers format.%nPlease check the URN: %s",
            urn));
  }

  // Check through each class and find DL URI belongs to which particular class
  public Map<String, String> toURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    for (io.openepcis.epc.translator.converter.Converter inputuri : DL) {
      if (inputuri.supportsURN(dlURI)) {
        return inputuri.convertToURN(dlURI, gcpLength);
      }
    }
    throw new UnsupportedGS1IdentifierException(String.format(INVALID_URI_MESSAGE, dlURI));
  }

  // Check through each class and find DL URI belongs to which particular class
  public Map<String, String> toURN(final String dlURI) throws ValidationException {
    for (io.openepcis.epc.translator.converter.Converter inputuri : DL) {
      if (inputuri.supportsURN(dlURI)) {
        return inputuri.convertToURN(dlURI);
      }
    }
    throw new UnsupportedGS1IdentifierException(String.format(INVALID_URI_MESSAGE, dlURI));
  }

  // Check through each class and find URN belongs to which particular class
  public String toURIForClassLevelIdentifier(final String urn) throws ValidationException {
    for (io.openepcis.epc.translator.converter.Converter uri : CLASS_LEVEL_TRANSLATOR) {
      if (uri.supportsDigitalLinkURI(urn)) {
        return uri.convertToDigitalLink(urn);
      }
    }
    throw new UnsupportedGS1IdentifierException(
        String.format(
            "Provided URN format does not match with any of the GS1 identifiers format.%nPlease check the URN: %s",
            urn));
  }

  // Check through each class and find DL URI belongs to which particular class
  public Map<String, String> toURNForClassLevelIdentifier(final String dlURI)
      throws ValidationException {
    for (io.openepcis.epc.translator.converter.Converter inputuri : CLASS_LEVEL_TRANSLATOR) {
      if (inputuri.supportsURN(dlURI)) {
        return inputuri.convertToURN(dlURI);
      }
    }
    throw new UnsupportedGS1IdentifierException(String.format(INVALID_URI_MESSAGE, dlURI));
  }

  // Check through each class and find DL URI belongs to which particular class
  public Map<String, String> toURNForClassLevelIdentifier(final String dlURI, int gcpLength)
      throws ValidationException {
    for (io.openepcis.epc.translator.converter.Converter inputuri : CLASS_LEVEL_TRANSLATOR) {
      if (inputuri.supportsURN(dlURI)) {
        return inputuri.convertToURN(dlURI, gcpLength);
      }
    }
    throw new UnsupportedGS1IdentifierException(String.format(INVALID_URI_MESSAGE, dlURI));
  }

  // Method to convert the CBV URN formatted vocabularies into WebURI vocabulary. Used during event
  // hash generator.
  public String toWebURIVocabulary(final String urnVocabulary) {
    return urnVocabulary == null || urnVocabulary.trim().equals("")
        ? urnVocabulary
        : EventVocabularyFormatter.canonicalWebURIVocabulary(urnVocabulary);
  }

  // Method to convert the CBV WebURI formatted vocabularies into URN vocabulary. Used during
  // JSON/JSON-LD conversion to XML.
  public String toUrnVocabulary(final String webUriVocabulary) {
    return webUriVocabulary == null || webUriVocabulary.trim().equals("")
        ? webUriVocabulary
        : EventVocabularyFormatter.canonicalString(webUriVocabulary);
  }

  // Method to convert the CBV URN/WebURI formatted vocabularies into BareString vocabulary. Used
  // during XML -> JSON/JSON-LD conversion.
  public String toBareStringVocabulary(final String eventVocabulary) {
    return eventVocabulary == null || eventVocabulary.trim().equals("")
        ? eventVocabulary
        : EventVocabularyFormatter.bareString(eventVocabulary);
  }

  /**
   * Method to convert the BareString vocabularies into CBV formatted URN/WebURI vocabulary. Used
   * during JSON/JSON-LD -> XML conversion.
   *
   * @param bareString Vocabulary that needs to be converted to CBV format. Ex: shipping, po,
   *     in_transit, etc.
   * @param fieldName Field names as per the EPCIS event. Ex: bizStep, disposition, source,
   *     destination, bizTransaction, etc.
   * @param format Type of formatting needed for the field either URN or WebURI.
   */
  public String toCbvVocabulary(
      final String bareString, final String fieldName, final String format) {
    return bareString == null || bareString.trim().equals("") || fieldName == null
        ? bareString
        : EventVocabularyFormatter.cbvVocabulary(bareString, fieldName, format);
  }
}
