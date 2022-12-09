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
  public boolean supportsDigitalLinkURI(String urn) {
    return urn.contains(SGLN_URN_PART);
  }

  // Check if the provided Digital Link URI is of SGLN Type
  public boolean supportsURN(String dlURI) {
    return dlURI.contains(SGLN_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of SGLN type
  public String convertToDigitalLink(String urn) throws ValidationException {

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
      return Constants.IDENTIFIERDOMAIN + SGLN_URI_PART + sgln;
    } else {
      return Constants.IDENTIFIERDOMAIN + SGLN_URI_PART + sgln + SGLN_SERIAL_PART + serial;
    }
  }

  // Convert the provided Digital Link URI to respective URN of SGLN Type
  public Map<String, String> convertToURN(String dlURI, int gcpLength) throws ValidationException {

    // Validate the URI to check if they match the SGLN syntax
    SGLN_VALIDATOR.validateURI(dlURI, gcpLength);

    Map<String, String> buildURN = new HashMap<>();
    String sgln;
    String asURN;

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

    if (dlURI.contains(Constants.IDENTIFIERDOMAIN)) {
      final String asCaptured =
          dlURI.replace(dlURI.substring(0, dlURI.indexOf(SGLN_URI_PART)), Constants.DLDOMAIN);
      buildURN.put(Constants.ASCAPTURED, asCaptured);
      buildURN.put(Constants.CANONICALDL, dlURI);
    } else {
      final String canonicalDL =
          dlURI.replace(
              dlURI.substring(0, dlURI.indexOf(SGLN_URI_PART)), Constants.IDENTIFIERDOMAIN);
      buildURN.put(Constants.ASCAPTURED, dlURI);
      buildURN.put(Constants.CANONICALDL, canonicalDL);
    }
    buildURN.put(Constants.ASURN, asURN);
    buildURN.put("sgln", sgln);
    return buildURN;
  }

  public Map<String, String> convertToURN(String dlURI) throws ValidationException {

    // Validate the URI to check if they match the SGLN syntax

    Map<String, String> buildURN = new HashMap<>();
    String sgln;
    String asURN;

    if (dlURI.contains(SGLN_SERIAL_PART)) {
      sgln =
          dlURI.substring(
              dlURI.indexOf(SGLN_URI_PART) + SGLN_URI_PART.length(),
              dlURI.indexOf(SGLN_SERIAL_PART));
      int gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(sgln);
      SGLN_VALIDATOR.validateURI(dlURI, gcpLength);
      final String serial = dlURI.substring(dlURI.indexOf(SGLN_SERIAL_PART) + 5);
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
      int gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(sgln);
      SGLN_VALIDATOR.validateURI(dlURI, gcpLength);
      asURN =
          SGLN_URN_PREFIX
              + sgln.substring(0, gcpLength)
              + "."
              + sgln.substring(gcpLength, sgln.length() - 1)
              + ".0";
    }

    if (dlURI.contains(Constants.IDENTIFIERDOMAIN)) {
      final String asCaptured =
          dlURI.replace(dlURI.substring(0, dlURI.indexOf(SGLN_URI_PART)), Constants.DLDOMAIN);
      buildURN.put(Constants.ASCAPTURED, asCaptured);
      buildURN.put(Constants.CANONICALDL, dlURI);
    } else {
      final String canonicalDL =
          dlURI.replace(
              dlURI.substring(0, dlURI.indexOf(SGLN_URI_PART)), Constants.IDENTIFIERDOMAIN);
      buildURN.put(Constants.ASCAPTURED, dlURI);
      buildURN.put(Constants.CANONICALDL, canonicalDL);
    }
    buildURN.put(Constants.ASURN, asURN);
    buildURN.put("sgln", sgln);
    return buildURN;
  }
}
