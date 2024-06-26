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
package io.openepcis.epc.translator.converter;

import static io.openepcis.constants.EPCIS.GS1_IDENTIFIER_DOMAIN;
import static io.openepcis.epc.translator.constants.ConstantDigitalLinkTranslatorInfo.*;

import io.openepcis.epc.translator.DefaultGCPLengthProvider;
import io.openepcis.epc.translator.exception.ValidationException;
import io.openepcis.epc.translator.util.ConverterUtil;
import io.openepcis.epc.translator.validation.GDTIValidator;
import java.util.HashMap;
import java.util.Map;

public class GDTIConverter implements Converter {

  private static final String GDTI_URI_PART = "/253/";
  private static final String GDTI_URN_PART = "gdti:";
  private static final GDTIValidator GDTI_VALIDATOR = new GDTIValidator();
  private boolean isClassLevel;

  public GDTIConverter() {}

  public GDTIConverter(boolean isClassLevel) {
    this.isClassLevel = isClassLevel;
  }

  // Check if the provided URN is of GDTI type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(":gdti:");
  }

  // Check if the provided Digital Link URI is of GDTI Type
  public boolean supportsURN(final String dlURI) {
    return dlURI.contains(GDTI_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of GDTI type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {

      // Call the Validator class for the GDTI to check the URN syntax
      if (isClassLevel) {
        GDTI_VALIDATOR.validateClassLevelURN(urn);
      } else {
        GDTI_VALIDATOR.validateURN(urn);
      }

      // If the URN passed the validation then convert the URN to URI
      final String gcp =
          urn.substring(urn.indexOf(GDTI_URN_PART) + GDTI_URN_PART.length(), urn.indexOf('.'));
      String gdti =
          gcp + urn.substring(urn.indexOf('.') + 1, urn.indexOf(".", urn.indexOf(".") + 1));
      gdti = gdti.substring(0, 12) + ConverterUtil.checksum(gdti.substring(0, 12));

      if (!isClassLevel) {
        gdti = gdti + urn.substring(urn.indexOf(".", urn.indexOf(".") + 1) + 1);
      }
      return GS1_IDENTIFIER_DOMAIN + GDTI_URI_PART + gdti;
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GDTI identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of GDTI Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Call the Validator class for the GDTI to check the DLURI syntax
      if (isClassLevel) {
        GDTI_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
      } else {
        GDTI_VALIDATOR.validateURI(dlURI, gcpLength);
      }

      // If the URI passed the validation then convert the URI to URN

      final String gdti =
          dlURI.substring(
              dlURI.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length(),
              dlURI.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length() + 13);

      return getEPCMap(dlURI, gcpLength, gdti);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GDTI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(
      final String dlURI, final int gcpLength, final String gdti) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;

    try {
      final String gdtiSubString = gdti.substring(gcpLength, gdti.length() - 1);

      if (isClassLevel) {
        asURN = "urn:epc:idpat:gdti:" + gdti.substring(0, gcpLength) + "." + gdtiSubString + ".*";
      } else {
        final String serial =
            dlURI.substring(dlURI.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length() + 13);
        asURN =
            "urn:epc:id:gdti:" + gdti.substring(0, gcpLength) + "." + gdtiSubString + "." + serial;
        buildURN.put(SERIAL, serial);
      }

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(GDTI_URI_PART)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(CANONICAL_DL, canonicalDL);
      }

      buildURN.put(AS_CAPTURED, dlURI);
      buildURN.put(AS_URN, asURN);
      buildURN.put("gdti", gdti);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the GDTI identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    if (isClassLevel) {
      GDTI_VALIDATOR.validateClassLevelURN(asURN);
    } else {
      GDTI_VALIDATOR.validateURN(asURN);
    }

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GDTI Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;
    try {
      String gdti;

      if (isClassLevel) {
        gdti = dlURI.substring(dlURI.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length());
      } else {
        gdti =
            dlURI.substring(
                dlURI.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length(),
                dlURI.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length() + 13);
      }

      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, gdti, GDTI_URI_PART);

      // Call the Validator class for the GDTI to check the DLURI syntax
      if (isClassLevel) {
        GDTI_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
      } else {
        GDTI_VALIDATOR.validateURI(dlURI, gcpLength);
      }

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, gdti);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GDTI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
