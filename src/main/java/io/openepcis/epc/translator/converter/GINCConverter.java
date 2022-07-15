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
import io.openepcis.epc.translator.validation.GINCValidator;
import java.util.HashMap;
import java.util.Map;

public class GINCConverter implements Converter {

  private static final String GINC_URI_PART = "/401/";
  private static final String GINC_URN_PART = ":ginc:";
  private static final GINCValidator GINC_VALIDATOR = new GINCValidator();

  // Check if the provided URN is of GINC type
  public boolean supportsDigitalLinkURI(String urn) {
    return urn.contains(GINC_URN_PART);
  }

  // Check if the provided Digital Link URI is of GINC Type
  public boolean supportsURN(String dlURI) {
    return dlURI.contains(GINC_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of GINC type
  public String convertToDigitalLink(String urn) throws ValidationException {

    // Validate the URN to check if they match the GINC syntax
    GINC_VALIDATOR.validateURN(urn);

    // If the URN passed the validation then convert the URN to URI
    String ginc =
        urn.substring(urn.indexOf(GINC_URN_PART) + GINC_URN_PART.length(), urn.indexOf("."));
    ginc = ginc + urn.substring(urn.indexOf(".") + 1);
    return Constants.IDENTIFIERDOMAIN + GINC_URI_PART + ginc;
  }

  // Convert the provided Digital Link URI to respective URN of GINC Type
  public Map<String, String> convertToURN(String dlURI, int gcpLength) throws ValidationException {

    // Call the Validator class for the GINC to check the DLURI syntax
    GINC_VALIDATOR.validateURI(dlURI, gcpLength);

    // If the URI passed the validation then convert the URI to URN
    final String ginc = dlURI.substring(dlURI.indexOf(GINC_URI_PART) + GINC_URI_PART.length());
    return getEPCMap(dlURI, gcpLength, ginc);
  }

  private Map<String, String> getEPCMap(String dlURI, int gcpLength, String ginc) {
    Map<String, String> buildURN = new HashMap<>();
    final String asURN =
        "urn:epc:id:ginc:" + ginc.substring(0, gcpLength) + "." + ginc.substring(gcpLength);

    if (dlURI.contains(Constants.IDENTIFIERDOMAIN)) {
      final String asCaptured =
          dlURI.replace(dlURI.substring(0, dlURI.indexOf(GINC_URI_PART)), Constants.DLDOMAIN);
      buildURN.put(Constants.ASCAPTURED, asCaptured);
      buildURN.put(Constants.CANONICALDL, dlURI);
    } else {
      final String canonicalDL =
          dlURI.replace(
              dlURI.substring(0, dlURI.indexOf(GINC_URI_PART)), Constants.IDENTIFIERDOMAIN);
      buildURN.put(Constants.ASCAPTURED, dlURI);
      buildURN.put(Constants.CANONICALDL, canonicalDL);
    }
    buildURN.put(Constants.ASURN, asURN);
    buildURN.put("ginc", ginc);
    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GINC Type
  public Map<String, String> convertToURN(String dlURI) throws ValidationException {
    final String ginc = dlURI.substring(dlURI.indexOf(GINC_URI_PART) + GINC_URI_PART.length());
    int gcpLength = GCPLengthProvider.getInstance().getGcpLength(ginc);

    // Call the Validator class for the GINC to check the DLURI syntax
    GINC_VALIDATOR.validateURI(dlURI, gcpLength);

    // If the URI passed the validation then convert the URI to URN
    return getEPCMap(dlURI, gcpLength, ginc);
  }
}
