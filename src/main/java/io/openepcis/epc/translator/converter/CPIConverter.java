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
import io.openepcis.epc.translator.validation.CPIValidator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CPIConverter implements Converter {

  private static final String CPI_SERIAL_PART = "/8011/";
  private static final String CPI_URI_PART = "/8010/";
  private static final String CPI_URN_PART = ":cpi:";
  private static final CPIValidator CPI_VALIDATOR = new CPIValidator();
  private boolean isClassLevel;

  public CPIConverter() {
    super();
  }

  public CPIConverter(final boolean isClassLevel) {
    this.isClassLevel = isClassLevel;
  }

  // Check if the provided URN is of CPI type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(CPI_URN_PART);
  }

  // Check if the provided Digital Link URI is of CPI Type
  public boolean supportsURN(final String dlURI) {
    if (isClassLevel) return Pattern.compile("(?=.*/8010/)").matcher(dlURI).find();
    else return Pattern.compile("(?=.*/8010/)(?=.*/8011/)").matcher(dlURI).find();
  }

  // Convert the provided URN to respective Digital Link URI of CPI type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Validate the URN to check if they match the CPI syntax
      if (isClassLevel) {
        CPI_VALIDATOR.validateClassLevelURN(urn);
      } else {
        CPI_VALIDATOR.validateURN(urn);
      }

      // If the URN passed the validation then convert the URN to URI
      final String gcp =
          urn.substring(urn.indexOf(CPI_URN_PART) + CPI_URN_PART.length(), urn.indexOf("."));
      final String cpi =
          gcp + urn.substring(urn.indexOf(".") + 1, urn.indexOf(".", urn.indexOf(".") + 1));
      if (isClassLevel) {
        return Constants.GS1_IDENTIFIER_DOMAIN + CPI_URI_PART + cpi;
      } else {
        final String serialNumber = urn.substring(urn.lastIndexOf(".") + 1);
        return Constants.GS1_IDENTIFIER_DOMAIN
            + CPI_URI_PART
            + cpi
            + CPI_SERIAL_PART
            + serialNumber;
      }
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of CPI identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of CPI Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {

    try {
      // Validate the DLURI to check if they match the CPI syntax
      if (isClassLevel) {
        CPI_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
      } else {
        CPI_VALIDATOR.validateURI(dlURI, gcpLength);
      }

      // If the URI passed the validation then convert the URI to URN
      String cpi;
      if (isClassLevel) {
        cpi = dlURI.substring(dlURI.indexOf(CPI_URI_PART) + CPI_URI_PART.length());
      } else {
        cpi =
            dlURI.substring(
                dlURI.indexOf(CPI_URI_PART) + CPI_URI_PART.length(),
                dlURI.indexOf(CPI_SERIAL_PART));
      }
      return getEPCMap(dlURI, gcpLength, cpi);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of CPI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + " GCP Length : "
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(final String dlURI, final int gcpLength, final String cpi) {
    try {
      Map<String, String> buildURN = new HashMap<>();
      String asURN;
      if (isClassLevel) {
        asURN =
            "urn:epc:idpat:cpi:"
                + cpi.substring(0, gcpLength)
                + "."
                + cpi.substring(gcpLength)
                + ".*";
      } else {
        final String serial =
            dlURI.substring(dlURI.indexOf(CPI_SERIAL_PART) + CPI_SERIAL_PART.length());
        asURN =
            "urn:epc:id:cpi:"
                + cpi.substring(0, gcpLength)
                + "."
                + cpi.substring(gcpLength)
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
                dlURI.substring(0, dlURI.indexOf(CPI_URI_PART)), Constants.GS1_IDENTIFIER_DOMAIN);
        buildURN.put(Constants.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(Constants.AS_CAPTURED, dlURI);
      buildURN.put(Constants.AS_URN, asURN);
      buildURN.put("cpi", cpi);
      return buildURN;
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the CPI identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + " GCP Length : "
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  public Map<String, String> convertToURN(final String dlURI) {
    try {
      String cpi;

      if (isClassLevel) {
        cpi = dlURI.substring(dlURI.indexOf(CPI_URI_PART) + CPI_URI_PART.length());
      } else {
        cpi =
            dlURI.substring(
                dlURI.indexOf(CPI_URI_PART) + CPI_URI_PART.length(),
                dlURI.indexOf(CPI_SERIAL_PART));
      }

      final int gcpLength =
          DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, CPI_URI_PART);

      // Validate the DLURI to check if they match the CPI syntax
      if (isClassLevel) {
        CPI_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
      } else {
        CPI_VALIDATOR.validateURI(dlURI, gcpLength);
      }

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, cpi);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of CPI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + "\n"
              + exception.getMessage());
    }
  }
}
