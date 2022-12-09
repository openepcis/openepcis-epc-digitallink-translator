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
import io.openepcis.epc.translator.validation.GIAIValidator;
import java.util.HashMap;
import java.util.Map;

public class GIAIConverter implements Converter {

  private static final String GIAI_URI_PART = "/8004/";
  private static final String GIAI_URN_PART = ":giai:";
  private static final GIAIValidator GIAI_VALIDATOR = new GIAIValidator();

  // Check if the provided URN is of GIAI type
  public boolean supportsDigitalLinkURI(String urn) {
    return urn.contains(GIAI_URN_PART);
  }

  // Check if the provided Digital Link URI is of GIAI Type
  public boolean supportsURN(String dlURI) {
    return dlURI.contains(GIAI_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of GIAI type
  public String convertToDigitalLink(String urn) throws ValidationException {

    // Call the Validator class for the GIAI to check the URN syntax
    GIAI_VALIDATOR.validateURN(urn);

    // If the URN passed the validation then convert the URN to URI
    final String gcp =
        urn.substring(urn.indexOf(GIAI_URN_PART) + GIAI_URN_PART.length(), urn.indexOf('.'));
    final String giai = gcp + urn.substring(urn.indexOf('.') + 1);
    return Constants.IDENTIFIERDOMAIN + GIAI_URI_PART + giai;
  }

  // Convert the provided Digital Link URI to respective URN of GIAI Type
  public Map<String, String> convertToURN(String dlURI, int gcpLength) throws ValidationException {

    // Call the Validator class for the GIAI to check the DLURI syntax
    GIAI_VALIDATOR.validateURI(dlURI, gcpLength);

    // If the URI passed the validation then convert the URI to URN
    final String giai = dlURI.substring(dlURI.indexOf(GIAI_URI_PART) + GIAI_URI_PART.length());
    return getEPCMap(dlURI, gcpLength, giai);
  }

  private Map<String, String> getEPCMap(String dlURI, int gcpLength, String giai) {
    Map<String, String> buildURN = new HashMap<>();
    final String asURN =
        "urn:epc:id:giai:" + giai.substring(0, gcpLength) + "." + giai.substring(gcpLength);

    if (dlURI.contains(Constants.IDENTIFIERDOMAIN)) {
      final String asCaptured =
          dlURI.replace(dlURI.substring(0, dlURI.indexOf(GIAI_URI_PART)), Constants.DLDOMAIN);
      buildURN.put(Constants.ASCAPTURED, asCaptured);
      buildURN.put(Constants.CANONICALDL, dlURI);
    } else {
      final String canonicalDL =
          dlURI.replace(
              dlURI.substring(0, dlURI.indexOf(GIAI_URI_PART)), Constants.IDENTIFIERDOMAIN);
      buildURN.put(Constants.ASCAPTURED, dlURI);
      buildURN.put(Constants.CANONICALDL, canonicalDL);
    }

    buildURN.put(Constants.ASURN, asURN);
    buildURN.put("giai", giai);
    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GIAI Type
  public Map<String, String> convertToURN(String dlURI) throws ValidationException {
    final String giai = dlURI.substring(dlURI.indexOf(GIAI_URI_PART) + GIAI_URI_PART.length());

    // GIAI always starts with the "pure" GCP - only pass 13 characters
    int gcpLength =
        DefaultGCPLengthProvider.getInstance()
            .getGcpLength(giai.length() > 13 ? giai.substring(0, 13) : giai);

    // Call the Validator class for the GIAI to check the DLURI syntax
    GIAI_VALIDATOR.validateURI(dlURI, gcpLength);

    // If the URI passed the validation then convert the URI to URN
    return getEPCMap(dlURI, gcpLength, giai);
  }
}
