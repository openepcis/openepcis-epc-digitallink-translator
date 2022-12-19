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
package io.openepcis.epc.translator.converter;

import io.openepcis.epc.translator.DefaultGCPLengthProvider;
import io.openepcis.epc.translator.constants.Constants;
import io.openepcis.epc.translator.exception.ValidationException;
import io.openepcis.epc.translator.validation.GRAIValidator;
import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

@NoArgsConstructor
public class GRAIConverter implements Converter {

  private static final String GRAI_URI_PART = "/8003/";
  private static final String GRAI_URN_PART = ":grai:";
  private static final GRAIValidator GRAI_VALIDATOR = new GRAIValidator();
  private boolean isClassLevel;

  public GRAIConverter(final boolean isClassLevel) {
    this.isClassLevel = isClassLevel;
  }

  // Check if the provided URN is of GRAI type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(GRAI_URN_PART);
  }

  // Check if the provided Digital Link URI is of GRAI Type
  public boolean supportsURN(final String dlURI) {
    return dlURI.contains(GRAI_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of GRAI type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Call the Validator class for the GRAI to check the URN syntax
      if (isClassLevel) {
        GRAI_VALIDATOR.validateClassLevelURN(urn);
      } else {
        GRAI_VALIDATOR.validateURN(urn);
      }

      // If the URN passed the validation then convert the URN to URI
      final String gcp =
          urn.substring(urn.indexOf(GRAI_URN_PART) + GRAI_URN_PART.length(), urn.indexOf('.'));
      String grai =
          gcp + urn.substring(urn.indexOf('.') + 1, urn.indexOf(".", urn.indexOf(".") + 1));
      grai = grai.substring(0, 12) + UPCEANLogicImpl.calcChecksum(grai.substring(0, 12));

      if (isClassLevel) {
        return Constants.GS1_IDENTIFIER_DOMAIN + GRAI_URI_PART + grai;
      } else {
        final String serialNumber = urn.substring(urn.indexOf(".", urn.indexOf(".") + 1) + 1);
        return Constants.GS1_IDENTIFIER_DOMAIN + GRAI_URI_PART + grai + serialNumber;
      }
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GRAI identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of GRAI Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Call the Validator class for the GRAI to check the DLURI syntax
      if (isClassLevel) {
        GRAI_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
      } else {
        GRAI_VALIDATOR.validateURI(dlURI, gcpLength);
      }

      // If the URI passed the validation then convert the URI to URN
      final String grai =
          dlURI.substring(
              dlURI.indexOf(GRAI_URI_PART) + GRAI_URI_PART.length(),
              dlURI.indexOf(GRAI_URI_PART) + 19);
      return getEPCMap(dlURI, gcpLength, grai);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GRAI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + " GCP Length : "
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(
      final String dlURI, final int gcpLength, final String grai) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;

    try {
      String serial;
      final String graiSubString = grai.substring(gcpLength, grai.length() - 1);

      if (isClassLevel) {
        asURN = "urn:epc:idpat:grai:" + grai.substring(0, gcpLength) + "." + graiSubString + ".*";
      } else {
        serial = dlURI.substring(dlURI.indexOf(GRAI_URI_PART) + 19);
        asURN =
            "urn:epc:id:grai:" + grai.substring(0, gcpLength) + "." + graiSubString + "." + serial;
      }

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(Constants.GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(Constants.CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(
                dlURI.substring(0, dlURI.indexOf(GRAI_URI_PART)), Constants.GS1_IDENTIFIER_DOMAIN);
        buildURN.put(Constants.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(Constants.AS_CAPTURED, dlURI);
      buildURN.put(Constants.AS_URN, asURN);
      buildURN.put("grai", grai);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the GRAI identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + " GCP Length : "
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    if (isClassLevel) {
      GRAI_VALIDATOR.validateClassLevelURN(asURN);
    } else {
      GRAI_VALIDATOR.validateURN(asURN);
    }

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GRAI Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;
    try {
      String grai;

      if (isClassLevel) {
        grai = dlURI.substring(dlURI.indexOf(GRAI_URI_PART) + GRAI_URI_PART.length());
      } else {
        grai =
            dlURI.substring(
                dlURI.indexOf(GRAI_URI_PART) + GRAI_URI_PART.length(),
                dlURI.indexOf(GRAI_URI_PART) + 19);
      }

      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, GRAI_URI_PART);

      // Call the Validator class for the GRAI to check the DLURI syntax
      if (isClassLevel) {
        GRAI_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
      } else {
        GRAI_VALIDATOR.validateURI(dlURI, gcpLength);
      }

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, grai);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GRAI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + " GCP Length : "
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
