/*
 * Copyright 2022-2024 benelog GmbH & Co. KG
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
import org.apache.commons.lang3.StringUtils;

public class Converter {

  private static final String INVALID_URI_MESSAGE =
      "Provided URI format does not match with any of the GS1 identifiers format.%nPlease check the URI: %s";
  private final Set<io.openepcis.epc.translator.converter.Converter> dl = new HashSet<>();
  private final Set<io.openepcis.epc.translator.converter.Converter> classLevelTranslator =
      new HashSet<>();
  private final EventVocabularyFormatter eventVocabularyFormatter = new EventVocabularyFormatter();

  public Converter() {
    // Add all EPC instance-level converter
    dl.add(new SGTINConverter());
    dl.add(new SSCCConverter());
    dl.add(new SGLNConverter());
    dl.add(new GRAIConverter());
    dl.add(new GIAIConverter());
    dl.add(new GSRNConverter());
    dl.add(new GSRNPConverter());
    dl.add(new GDTIConverter());
    dl.add(new CPIConverter());
    dl.add(new GCNConverter());
    dl.add(new GINCConverter());
    dl.add(new GSINConverter());
    dl.add(new ITIPConverter());
    dl.add(new UPUIConverter());
    dl.add(new PGLNConverter());

    // Add all EPC class-level converter
    classLevelTranslator.add(new LGTINConverter());
    classLevelTranslator.add(new SGTINConverter(true));
    classLevelTranslator.add(new GRAIConverter(true));
    classLevelTranslator.add(new GDTIConverter(true));
    classLevelTranslator.add(new GCNConverter(true));
    classLevelTranslator.add(new CPIConverter(true));
    classLevelTranslator.add(new ITIPConverter(true));
  }

  /**
   * Method to convert the instance level GS1 formatted application identifiers from URN to digital
   * link WebURI format.
   *
   * @param urn instance level application identifier in URN format ex:
   *     urn:epc:id:sgtin:234567890.1123.9999
   * @return returns the instance level application identifier in WebURI format ex:
   *     https://id.gs1.org/01/12345678901231/21/9999
   * @throws ValidationException throws the exception with appropriate information if some error
   *     occurred during the conversion
   */
  public String toURI(final String urn) throws ValidationException {
    for (io.openepcis.epc.translator.converter.Converter uri : dl) {
      if (uri.supportsDigitalLinkURI(urn)) {
        return uri.convertToDigitalLink(urn);
      }
    }
    throw new UnsupportedGS1IdentifierException(
        String.format(
            "Provided URN format does not match with any of the GS1 identifiers format.%nPlease check the URN: %s",
            urn));
  }

  /**
   * Method to convert the instance level GS1 formatted application identifiers from Digital Link
   * WebURI to URN format. It will search through each class and find DL URI belongs to which
   * particular class and accordingly convert to appropriate URN.
   *
   * @param dlURI Instance level DigitalLink URI that needs to be converted ex:
   *     https://id.gs1.org/01/12345678901231/21/9999
   * @param gcpLength GCP Length based on which URN needs to be generated (6-12 digit). Ex: 9
   * @return returns the Map with all the converted information including the URN ex:
   *     urn:epc:id:sgtin:234567890.1123.9999
   * @throws ValidationException throws the exception with appropriate information if some error
   *     occurred during the conversion
   */
  public Map<String, String> toURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    for (io.openepcis.epc.translator.converter.Converter inputuri : dl) {
      if (inputuri.supportsURN(dlURI)) {
        return inputuri.convertToURN(dlURI, gcpLength);
      }
    }
    throw new UnsupportedGS1IdentifierException(String.format(INVALID_URI_MESSAGE, dlURI));
  }

  /**
   * Method to convert the instance level GS1 formatted application identifiers from Digital Link
   * WebURI to URN format by searching for GCP length from GS1 provided list.
   *
   * @param dlURI Instance level DigitalLink URI that needs to be converted ex:
   *     https://id.gs1.org/01/12345678901231/21/9999
   * @return returns the Map with all the converted information including the URN ex:
   *     urn:epc:id:sgtin:234567890.1123.9999
   * @throws ValidationException throws the exception with appropriate information if some error
   *     occurred during the conversion
   */
  public Map<String, String> toURN(final String dlURI) throws ValidationException {
    for (io.openepcis.epc.translator.converter.Converter inputuri : dl) {
      if (inputuri.supportsURN(dlURI)) {
        return inputuri.convertToURN(dlURI);
      }
    }
    throw new UnsupportedGS1IdentifierException(String.format(INVALID_URI_MESSAGE, dlURI));
  }

  /**
   * Method to convert the class level GS1 formatted application identifiers from URN to digital
   * link WebURI format.
   *
   * @param urn class level application identifier in URN format ex:
   *     urn:epc:idpat:sgtin:234567.1890123.*
   * @return returns the application identifier in WebURI format ex:
   *     https://id.gs1.org/01/12345678901234
   * @throws ValidationException throws the exception with appropriate information if some error
   *     occurred during the conversion
   */
  public String toURIForClassLevelIdentifier(final String urn) throws ValidationException {
    for (io.openepcis.epc.translator.converter.Converter uri : classLevelTranslator) {
      if (uri.supportsDigitalLinkURI(urn)) {
        return uri.convertToDigitalLink(urn);
      }
    }
    throw new UnsupportedGS1IdentifierException(
        String.format(
            "Provided URN format does not match with any of the GS1 identifiers format.%nPlease check the URN: %s",
            urn));
  }

  /**
   * Method to convert the class level GS1 formatted class application identifiers from Digital Link
   * WebURI to URN format by searching for GCP length from GS1 provided list.
   *
   * @param dlURI Class level DigitalLink Class URI that needs to be converted ex:
   *     https://id.gs1.org/01/12345678901234
   * @return returns the Map with all the converted information including the URN ex:
   *     urn:epc:idpat:sgtin:234567.1890123.*
   * @throws ValidationException throws the exception with appropriate information if some error
   *     occurred during the conversion
   */
  public Map<String, String> toURNForClassLevelIdentifier(final String dlURI)
      throws ValidationException {
    for (io.openepcis.epc.translator.converter.Converter inputuri : classLevelTranslator) {
      if (inputuri.supportsURN(dlURI)) {
        return inputuri.convertToURN(dlURI);
      }
    }
    throw new UnsupportedGS1IdentifierException(String.format(INVALID_URI_MESSAGE, dlURI));
  }

  /**
   * Method to convert the class level GS1 formatted class application identifiers from Digital Link
   * WebURI to URN format. It will search through each class and find DL URI belongs to which
   * particular class and accordingly convert to appropriate URN.
   *
   * @param dlURI Class level DigitalLink Class URI that needs to be converted ex:
   *     https://id.gs1.org/01/12345678901234
   * @param gcpLength GCP Length based on which URN needs to be generated (6-12 digit). Ex: 10
   * @return returns the Map with all the converted information including the URN ex:
   *     urn:epc:idpat:sgtin:234567.1890123.*
   * @throws ValidationException throws the exception with appropriate information if some error
   *     occurred during the conversion
   */
  public Map<String, String> toURNForClassLevelIdentifier(final String dlURI, int gcpLength)
      throws ValidationException {
    for (io.openepcis.epc.translator.converter.Converter inputuri : classLevelTranslator) {
      if (inputuri.supportsURN(dlURI)) {
        return inputuri.convertToURN(dlURI, gcpLength);
      }
    }
    throw new UnsupportedGS1IdentifierException(String.format(INVALID_URI_MESSAGE, dlURI));
  }

  /**
   * Method to convert the CBV formatted URN vocabularies into CBV formatted WebURI vocabulary.
   *
   * @param urnVocabulary CBV formatted URN vocabulary ex: urn:epcglobal:cbv:bizstep:departing or
   *     urn:epcglobal:cbv:btt:po.
   * @return returns CBV formatted WebURI vocabulary ex: https://ref.gs1.org/voc/Bizstep-departing
   *     or https://ref.gs1.org/voc/BTT-po
   */
  public String toWebURIVocabulary(final String urnVocabulary) {
    return StringUtils.isBlank(urnVocabulary)
        ? urnVocabulary
        : eventVocabularyFormatter.canonicalWebURIVocabulary(urnVocabulary);
  }

  /**
   * Method to convert the CBV formatted WebURI vocabularies into CBV formatted URN vocabulary.
   *
   * @param webUriVocabulary CBV formatted WebURI vocabulary ex:
   *     https://ref.gs1.org/voc/Bizstep-departing or https://ref.gs1.org/voc/BTT-po
   * @return returns the CBV formatted URN vocabulary ex: urn:epcglobal:cbv:bizstep:departing or
   *     urn:epcglobal:cbv:btt:po.
   */
  public String toUrnVocabulary(final String webUriVocabulary) {
    return StringUtils.isBlank(webUriVocabulary)
        ? webUriVocabulary
        : eventVocabularyFormatter.canonicalString(webUriVocabulary);
  }

  /**
   * Method to convert the CBV formatted URN/WebURI vocabularies into BareString vocabulary.
   *
   * @param eventVocabulary CBV Vocabulary that needs to be converted to bare string ex:
   *     https://ref.gs1.org/voc/Bizstep-departing or urn:epcglobal:cbv:bizstep:receiving.
   * @return returns the converted bare string vocabulary ex: departing or receiving.
   */
  public String toBareStringVocabulary(final String eventVocabulary) {
    return StringUtils.isBlank(eventVocabulary)
        ? eventVocabulary
        : eventVocabularyFormatter.bareString(eventVocabulary);
  }

  /**
   * Method to convert the BareString vocabularies into CBV formatted URN/WebURI vocabulary.
   *
   * @param bareString Vocabulary that needs to be converted to CBV format. Ex: shipping, po,
   *     in_transit, etc.
   * @param fieldName Field names as per the EPCIS event. Ex: bizStep, disposition, source,
   *     destination, bizTransaction, etc.
   * @param format Type of formatting needed for the field either URN or WebURI.
   */
  public String toCbvVocabulary(
      final String bareString, final String fieldName, final String format) {
    return StringUtils.isBlank(bareString)
            || StringUtils.isBlank(fieldName)
            || StringUtils.isBlank(format)
        ? bareString
        : eventVocabularyFormatter.toCbvVocabulary(bareString, fieldName, format);
  }

  /**
   * Method to replace the short names for keys/key extensions with AIs. Used during sensorElement
   * deviceId, rawData, etc. during pre-hash string generation in event hash generator.
   *
   * @param gs1Identifier GS1 WebURI vocabulary whose identifier vocabulary needs to be replaced ex:
   *     https://example.org/giai/401234599999
   * @return it would return the corresponding converted identifier ex:
   *     https://id.gs1.org/8004/401234599999
   */
  public final String shortNameReplacer(final String gs1Identifier) {
    return StringUtils.isBlank(gs1Identifier)
        ? gs1Identifier
        : eventVocabularyFormatter.shortNameReplacer(gs1Identifier);
  }
}
