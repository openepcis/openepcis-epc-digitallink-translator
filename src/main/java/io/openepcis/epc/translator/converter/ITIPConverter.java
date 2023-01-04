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
import io.openepcis.epc.translator.validation.ITIPValidator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

public class ITIPConverter implements Converter {

  private static final String ITIP_SERIAL_PART = "/21/";
  private static final String ITIP_URI_PART = "/8006/";
  private static final String ITIP_URN_PART = ":itip:";
  private static final ITIPValidator ITIP_VALIDATOR = new ITIPValidator();
  private boolean isClassLevel;

  public ITIPConverter() {
    super();
  }

  public ITIPConverter(final boolean isClassLevel) {
    this.isClassLevel = isClassLevel;
  }

  // Check if the provided URN is of ITIP type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(ITIP_URN_PART);
  }

  // Check if the provided Digital Link URI is of ITIP Type
  public boolean supportsURN(final String dlURI) {
    return Pattern.compile("(?=.*/8006/)").matcher(dlURI).find();
  }

  // Convert the provided URN to respective Digital Link URI of ITIP type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Call the Validator class for the ITIP to check the URN syntax
      if (isClassLevel) {
        ITIP_VALIDATOR.validateClassLevelURN(urn);
      } else {
        ITIP_VALIDATOR.validateURN(urn);
      }

      // If the URN passed the validation then convert the URN to URI
      String itip =
          urn.charAt(urn.indexOf('.') + 1)
              + urn.substring(
                  urn.indexOf(ITIP_URN_PART) + ITIP_URN_PART.length(),
                  StringUtils.ordinalIndexOf(urn, ".", 1));
      itip =
          itip
              + urn.substring(
                  StringUtils.ordinalIndexOf(urn, ".", 1) + 2,
                  StringUtils.ordinalIndexOf(urn, ".", 2));
      itip =
          itip
              + urn.substring(
                  StringUtils.ordinalIndexOf(urn, ".", 2) + 1,
                  StringUtils.ordinalIndexOf(urn, ".", 3));
      itip =
          itip
              + urn.substring(
                  StringUtils.ordinalIndexOf(urn, ".", 3) + 1,
                  StringUtils.ordinalIndexOf(urn, ".", 4));
      itip =
          itip.substring(0, 13)
              + UPCEANLogicImpl.calcChecksum(itip.substring(0, 13))
              + itip.substring(13);

      if (isClassLevel) {
        return Constants.GS1_IDENTIFIER_DOMAIN + ITIP_URI_PART + itip;
      } else {
        final String serialNumber = urn.substring(StringUtils.ordinalIndexOf(urn, ".", 4) + 1);
        return Constants.GS1_IDENTIFIER_DOMAIN
            + ITIP_URI_PART
            + itip
            + ITIP_SERIAL_PART
            + serialNumber;
      }
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of ITIP identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of ITIP Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Call the Validator class for the ITIP to check the DLURI syntax
      if (isClassLevel) {
        ITIP_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
      } else {
        ITIP_VALIDATOR.validateURI(dlURI, gcpLength);
      }

      // If the URI passed the validation then convert the URI to URN
      String itip;
      if (isClassLevel) {
        itip = dlURI.substring(dlURI.indexOf(ITIP_URI_PART) + ITIP_URI_PART.length());
      } else {
        itip =
            dlURI.substring(
                dlURI.indexOf(ITIP_URI_PART) + ITIP_URI_PART.length(),
                dlURI.indexOf(ITIP_SERIAL_PART));
      }
      return getEPCMap(dlURI, gcpLength, itip);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of ITIP identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + Constants.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(String dlURI, int gcpLength, String itip) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;

    try {
      if (isClassLevel) {
        asURN = "urn:epc:idpat:itip:" + itip.substring(1, gcpLength + 1) + "." + itip.charAt(0);
        asURN =
            asURN
                + itip.substring(gcpLength + 1, 13)
                + "."
                + itip.substring(14, 16)
                + "."
                + itip.substring(16, 18)
                + ".*";
      } else {
        asURN = "urn:epc:id:itip:" + itip.substring(1, gcpLength + 1) + "." + itip.charAt(0);
        final String serial =
            dlURI.substring(dlURI.indexOf(ITIP_SERIAL_PART) + ITIP_SERIAL_PART.length());
        asURN =
            asURN
                + itip.substring(gcpLength + 1, 13)
                + "."
                + itip.substring(14, 16)
                + "."
                + itip.substring(16, 18)
                + "."
                + serial;
        buildURN.put(Constants.SERIAL, serial);
      }

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(Constants.GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(Constants.CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(
                dlURI.substring(0, dlURI.indexOf(ITIP_URI_PART)), Constants.GS1_IDENTIFIER_DOMAIN);
        buildURN.put(Constants.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(Constants.AS_CAPTURED, dlURI);
      buildURN.put(Constants.AS_URN, asURN);
      buildURN.put("itip", itip);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the ITIP identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + Constants.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    if (isClassLevel) {
      ITIP_VALIDATOR.validateClassLevelURN(asURN);
    } else {
      ITIP_VALIDATOR.validateURN(asURN);
    }

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of ITIP Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;
    try {
      String itip;

      if (isClassLevel) {
        itip = dlURI.substring(dlURI.indexOf(ITIP_URI_PART) + ITIP_URI_PART.length());
      } else {
        itip =
            dlURI.substring(
                dlURI.indexOf(ITIP_URI_PART) + ITIP_URI_PART.length(),
                dlURI.indexOf(ITIP_SERIAL_PART));
      }

      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(itip);

      // Call the Validator class for the ITIP to check the DLURI syntax
      if (isClassLevel) {
        ITIP_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
      } else {
        ITIP_VALIDATOR.validateURI(dlURI, gcpLength);
      }

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, itip);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of ITIP identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + Constants.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
