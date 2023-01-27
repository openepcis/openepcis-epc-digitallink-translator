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
import io.openepcis.epc.translator.validation.SGLNValidator;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

public class SGLNConverter implements Converter {

  private static final String SGLN_SERIAL_PART = "/254/";
  private static final String SGLN_URI_PART = "/414/";
  private static final String SGLN_URN_PART = ":sgln:";
  private static final String SGLN_URN_PREFIX = "urn:epc:id:sgln:";
  private static final SGLNValidator SGLN_VALIDATOR = new SGLNValidator();

  // Check if the provided URN is of SGLN type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(SGLN_URN_PART);
  }

  // Check if the provided Digital Link URI is of SGLN Type
  public boolean supportsURN(final String dlURI) {
    return dlURI.contains(SGLN_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of SGLN type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Validate the URN to check if they match the SGLN syntax
      SGLN_VALIDATOR.validateURN(urn);

      String sgln =
          urn.substring(
              urn.indexOf(SGLN_URN_PART) + SGLN_URN_PART.length(),
              StringUtils.ordinalIndexOf(urn, ".", 1));
      sgln =
          sgln
              + urn.substring(
                  StringUtils.ordinalIndexOf(urn, ".", 1) + 1,
                  StringUtils.ordinalIndexOf(urn, ".", 2));
      sgln = sgln + UPCEANLogicImpl.calcChecksum(sgln);
      final String serial = urn.substring(StringUtils.ordinalIndexOf(urn, ".", 2) + 1);

      if (serial.length() == 0) {
        return Constants.GS1_IDENTIFIER_DOMAIN + SGLN_URI_PART + sgln;
      } else {
        // Add serial part if not 0
        return Constants.GS1_IDENTIFIER_DOMAIN
            + SGLN_URI_PART
            + sgln
            + ((!serial.equals("0")) ? SGLN_SERIAL_PART + serial : "");
      }
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SGLN identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of SGLN Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Validate the URI to check if they match the SGLN syntax
      SGLN_VALIDATOR.validateURI(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SGLN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + Constants.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(final String dlURI, final int gcpLength) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;

    try {
      String sgln;

      if (dlURI.contains(SGLN_SERIAL_PART)) {
        sgln =
            dlURI.substring(
                dlURI.indexOf(SGLN_URI_PART) + SGLN_URI_PART.length(),
                dlURI.indexOf(SGLN_SERIAL_PART));
        final String serial =
            dlURI.substring(dlURI.indexOf(SGLN_SERIAL_PART) + SGLN_SERIAL_PART.length());
        asURN =
            SGLN_URN_PREFIX
                + sgln.substring(0, gcpLength)
                + "."
                + sgln.substring(gcpLength, sgln.length() - 1)
                + "."
                + serial;
        buildURN.put("serial", serial);
      } else {
        sgln = dlURI.substring(dlURI.indexOf(SGLN_URI_PART) + SGLN_URI_PART.length());
        asURN =
            SGLN_URN_PREFIX
                + sgln.substring(0, gcpLength)
                + "."
                + sgln.substring(gcpLength, sgln.length() - 1)
                + ".0";
      }

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(Constants.GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(Constants.CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(
                dlURI.substring(0, dlURI.indexOf(SGLN_URI_PART)), Constants.GS1_IDENTIFIER_DOMAIN);
        buildURN.put(Constants.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(Constants.AS_CAPTURED, dlURI);
      buildURN.put(Constants.AS_URN, asURN);
      buildURN.put("sgln", sgln);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the SGLN identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + Constants.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // Validate the URN to check if they match the SGLN syntax
    SGLN_VALIDATOR.validateURN(asURN);

    return buildURN;
  }

  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;
    try {
      String sgln;

      if (dlURI.contains(SGLN_SERIAL_PART)) {
        sgln =
            dlURI.substring(
                dlURI.indexOf(SGLN_URI_PART) + SGLN_URI_PART.length(),
                dlURI.indexOf(SGLN_SERIAL_PART));
      } else {
        sgln = dlURI.substring(dlURI.indexOf(SGLN_URI_PART) + SGLN_URI_PART.length());
      }

      // Find the GCP Length from GS1 provided list
      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, sgln, SGLN_URI_PART);

      // Validate the URI to check if they match the SGLN syntax
      SGLN_VALIDATOR.validateURI(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SGLN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + Constants.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
