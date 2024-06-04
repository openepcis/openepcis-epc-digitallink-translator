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
import io.openepcis.epc.translator.validation.SGTINValidator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SGTINConverter implements Converter {

  private static final String SGTIN_SERIAL_PART = "/21/";
  private static final String SGTIN_URI_PART = "/01/";
  private static final SGTINValidator SGTIN_VALIDATOR = new SGTINValidator();
  private boolean isClassLevel;
  private static final String SGTIN_URN_PART = ":sgtin:";

  public SGTINConverter() {
    super();
  }

  public SGTINConverter(boolean isClassLevel) {
    this.isClassLevel = isClassLevel;
  }

  // Check if the provided URN is SGTIN
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(SGTIN_URN_PART);
  }

  // Check if the provided DL URI is SGTIN
  public boolean supportsURN(final String dlURI) {
    if (isClassLevel) {
      return Pattern.compile("(?=.*/01/)(?!.*/10/)").matcher(dlURI).find();
    } else {
      return Pattern.compile("(?=.*/01/)(?=.*/21/)").matcher(dlURI).find();
    }
  }

  // Convert to SGTIN Digital Link URI
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Validate the URN to check if they match the SGTIN syntax
      if (isClassLevel) {
        SGTIN_VALIDATOR.validateClassLevelURN(urn);
      } else {
        SGTIN_VALIDATOR.validateURN(urn);
      }

      final String gcp =
          urn.charAt(urn.indexOf('.') + 1)
              + urn.substring(
                  urn.indexOf(SGTIN_URN_PART) + SGTIN_URN_PART.length(), urn.indexOf('.'));
      String sgtin =
          gcp + urn.substring(urn.indexOf('.') + 2, urn.indexOf(".", urn.indexOf(".") + 1));
      sgtin = sgtin.substring(0, 13) + ConverterUtil.checksum(sgtin.substring(0, 13));

      if (isClassLevel) {
        sgtin = GS1_IDENTIFIER_DOMAIN + SGTIN_URI_PART + sgtin;
      } else {
        final String serialNumber = urn.substring(urn.indexOf(".", urn.indexOf(".") + 1) + 1);
        sgtin = GS1_IDENTIFIER_DOMAIN + SGTIN_URI_PART + sgtin + SGTIN_SERIAL_PART + serialNumber;
      }
      return sgtin;
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SGTIN identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert to SGTIN URN
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      String sgtin;

      // Validate the URN to check if they match the SGTIN syntax
      if (isClassLevel) {
        sgtin = dlURI.substring(dlURI.indexOf(SGTIN_URI_PART) + SGTIN_URI_PART.length());
        SGTIN_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
      } else {
        sgtin =
            dlURI.substring(
                dlURI.indexOf(SGTIN_URI_PART) + SGTIN_URI_PART.length(),
                dlURI.indexOf(SGTIN_SERIAL_PART));
        SGTIN_VALIDATOR.validateURI(dlURI, gcpLength);
      }

      return getEPCMap(dlURI, gcpLength, sgtin);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SGTIN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(
      final String dlURI, final int gcpLength, final String sgtin) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;

    try {
      final String sgtinUrn =
          sgtin.substring(1, gcpLength + 1)
              + "."
              + sgtin.charAt(0)
              + sgtin.substring(gcpLength + 1, sgtin.length() - 1);

      if (isClassLevel) {
        asURN = "urn:epc:idpat:sgtin:" + sgtinUrn + ".*";
      } else {
        final String serial =
            dlURI.substring(dlURI.indexOf(SGTIN_SERIAL_PART) + SGTIN_SERIAL_PART.length());
        asURN = "urn:epc:id:sgtin:" + sgtinUrn + "." + serial;
        buildURN.put(SERIAL, serial);
      }

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(SGTIN_URI_PART)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(CANONICAL_DL, canonicalDL);
      }

      buildURN.put(AS_CAPTURED, dlURI);
      buildURN.put(AS_URN, asURN);
      buildURN.put("gtin", sgtin);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the SGTIN identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    if (isClassLevel) {
      SGTIN_VALIDATOR.validateClassLevelURN(asURN);
    } else {
      SGTIN_VALIDATOR.validateURN(asURN);
    }

    return buildURN;
  }

  // Convert to SGTIN URN
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;

    try {
      String sgtin;
      if (isClassLevel) {
        sgtin = dlURI.substring(dlURI.indexOf(SGTIN_URI_PART) + SGTIN_URI_PART.length());
      } else {
        sgtin =
            dlURI.substring(
                dlURI.indexOf(SGTIN_URI_PART) + SGTIN_URI_PART.length(),
                dlURI.indexOf(SGTIN_SERIAL_PART));
      }

      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, sgtin, SGTIN_URI_PART);

      // Validate the URN to check if they match the SGTIN syntax
      if (isClassLevel) {
        SGTIN_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
      } else {
        SGTIN_VALIDATOR.validateURI(dlURI, gcpLength);
      }

      return getEPCMap(dlURI, gcpLength, sgtin);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SGTIN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
