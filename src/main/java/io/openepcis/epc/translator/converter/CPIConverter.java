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

import io.openepcis.epc.translator.GCPLengthProvider;
import io.openepcis.epc.translator.ValidationException;
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

  public CPIConverter(boolean isClassLevel) {
    this.isClassLevel = isClassLevel;
  }

  // Check if the provided URN is of CPI type
  public boolean supportsDigitalLinkURI(String urn) {
    return urn.contains(CPI_URN_PART);
  }

  // Check if the provided Digital Link URI is of CPI Type
  public boolean supportsURN(String dlURI) {
    if (isClassLevel) return Pattern.compile("(?=.*/8010/)").matcher(dlURI).find();
    else return Pattern.compile("(?=.*/8010/)(?=.*/8011/)").matcher(dlURI).find();
  }

  // Convert the provided URN to respective Digital Link URI of CPI type
  public String convertToDigitalLink(String urn) throws ValidationException {

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
      return Constants.IDENTIFIERDOMAIN + CPI_URI_PART + cpi;
    } else {
      final String serialNumber = urn.substring(urn.lastIndexOf(".") + 1);
      return Constants.IDENTIFIERDOMAIN + CPI_URI_PART + cpi + CPI_SERIAL_PART + serialNumber;
    }
  }

  // Convert the provided Digital Link URI to respective URN of CPI Type
  public Map<String, String> convertToURN(String dlURI, int gcpLength) throws ValidationException {

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
              dlURI.indexOf(CPI_URI_PART) + CPI_URI_PART.length(), dlURI.indexOf(CPI_SERIAL_PART));
    }
    return getEPCMap(dlURI, gcpLength, cpi);
  }

  private Map<String, String> getEPCMap(String dlURI, int gcpLength, String cpi) {
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

    if (dlURI.contains(Constants.IDENTIFIERDOMAIN)) {
      final String asCaptured =
          dlURI.replace(dlURI.substring(0, dlURI.indexOf(CPI_URI_PART)), Constants.DLDOMAIN);
      buildURN.put(Constants.ASCAPTURED, asCaptured);
      buildURN.put(Constants.CANONICALDL, dlURI);
    } else {
      final String canonicalDL =
          dlURI.replace(
              dlURI.substring(0, dlURI.indexOf(CPI_URI_PART)), Constants.IDENTIFIERDOMAIN);
      buildURN.put(Constants.ASCAPTURED, dlURI);
      buildURN.put(Constants.CANONICALDL, canonicalDL);
    }

    buildURN.put(Constants.ASURN, asURN);
    buildURN.put("cpi", cpi);
    return buildURN;
  }

  public Map<String, String> convertToURN(String dlURI) throws ValidationException {
    String cpi;

    if (isClassLevel) {
      cpi = dlURI.substring(dlURI.indexOf(CPI_URI_PART) + CPI_URI_PART.length());
    } else {
      cpi =
          dlURI.substring(
              dlURI.indexOf(CPI_URI_PART) + CPI_URI_PART.length(), dlURI.indexOf(CPI_SERIAL_PART));
    }

    final int gcpLength = GCPLengthProvider.getInstance().getGcpLength(cpi);

    // Validate the DLURI to check if they match the CPI syntax
    if (isClassLevel) {
      CPI_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
    } else {
      CPI_VALIDATOR.validateURI(dlURI, gcpLength);
    }

    // If the URI passed the validation then convert the URI to URN
    return getEPCMap(dlURI, gcpLength, cpi);
  }
}
