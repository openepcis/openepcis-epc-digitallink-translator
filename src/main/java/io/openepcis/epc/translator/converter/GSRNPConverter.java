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
import io.openepcis.epc.translator.validation.GSRNPValidator;
import java.util.HashMap;
import java.util.Map;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

public class GSRNPConverter implements Converter {

  private static final String GSRNP_URI_PART = "/8017/";
  private static final GSRNPValidator GSRNP_VALIDATOR = new GSRNPValidator();

  // Check if the provided URN is of GSRNP type
  public boolean supportsDigitalLinkURI(String urn) {
    return urn.contains(":gsrnp:");
  }

  // Check if the provided Digital Link URI is of GSRNP Type
  public boolean supportsURN(String dlURI) {
    return dlURI.contains(GSRNP_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of GSRNP type
  public String convertToDigitalLink(String urn) throws ValidationException {
    // Call the Validator class for the GSRNP to check the URN syntax
    GSRNP_VALIDATOR.validateURN(urn);

    // If the URN passed the validation then convert the URN to URI
    final String gcp = urn.substring(urn.lastIndexOf(":") + 1, urn.indexOf('.'));
    String gsrnp = gcp + urn.substring(urn.indexOf('.') + 1);
    gsrnp = gsrnp.substring(0, 17) + UPCEANLogicImpl.calcChecksum(gsrnp.substring(0, 17));
    return Constants.IDENTIFIERDOMAIN + GSRNP_URI_PART + gsrnp;
  }

  // Convert the provided Digital Link URI to respective URN of GSRNP Type
  public Map<String, String> convertToURN(String dlURI, int gcpLength) throws ValidationException {
    // Call the Validator class for the GSRNP to check the DLURI syntax
    GSRNP_VALIDATOR.validateURI(dlURI, gcpLength);

    // If the URI passed the validation then convert the URI to URN
    final String gsrnp = dlURI.substring(dlURI.indexOf(GSRNP_URI_PART) + GSRNP_URI_PART.length());
    return getEPCMap(dlURI, gcpLength, gsrnp);
  }

  private Map<String, String> getEPCMap(String dlURI, int gcpLength, String gsrnp) {
    Map<String, String> buildURN = new HashMap<>();
    final String asURN =
        "urn:epc:id:gsrnp:"
            + gsrnp.substring(0, gcpLength)
            + "."
            + gsrnp.substring(gcpLength, gsrnp.length() - 1);

    if (dlURI.contains(Constants.IDENTIFIERDOMAIN)) {
      final String asCaptured =
          dlURI.replace(dlURI.substring(0, dlURI.indexOf(GSRNP_URI_PART)), Constants.DLDOMAIN);
      buildURN.put(Constants.ASCAPTURED, asCaptured);
      buildURN.put(Constants.CANONICALDL, dlURI);
    } else {
      final String canonicalDL =
          dlURI.replace(
              dlURI.substring(0, dlURI.indexOf(GSRNP_URI_PART)), Constants.IDENTIFIERDOMAIN);
      buildURN.put(Constants.ASCAPTURED, dlURI);
      buildURN.put(Constants.CANONICALDL, canonicalDL);
    }
    buildURN.put(Constants.ASURN, asURN);
    buildURN.put("gsrnp", gsrnp);
    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GSRNP Type
  public Map<String, String> convertToURN(String dlURI) throws ValidationException {
    final String gsrnp = dlURI.substring(dlURI.indexOf(GSRNP_URI_PART) + GSRNP_URI_PART.length());
    int gcpLength = GCPLengthProvider.getInstance().getGcpLength(gsrnp);

    // Call the Validator class for the GSRNP to check the DLURI syntax
    GSRNP_VALIDATOR.validateURI(dlURI, gcpLength);

    // If the URI passed the validation then convert the URI to URN
    return getEPCMap(dlURI, gcpLength, gsrnp);
  }
}
