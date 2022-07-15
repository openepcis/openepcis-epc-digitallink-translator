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
import io.openepcis.epc.translator.validation.GSRNValidator;
import java.util.HashMap;
import java.util.Map;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

public class GSRNConverter implements Converter {

  private static final String GSRN_URI_PART = "/8018/";
  private static final GSRNValidator GSRN_VALIDATOR = new GSRNValidator();

  // Check if the provided URN is of GSRN type
  public boolean supportsDigitalLinkURI(String urn) {
    return urn.contains(":gsrn:");
  }

  // Check if the provided Digital Link URI is of GSRN Type
  public boolean supportsURN(String dlURI) {
    return dlURI.contains(GSRN_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of GSRN type
  public String convertToDigitalLink(String urn) throws ValidationException {
    // Call the Validator class for the GSRN to check the URN syntax
    GSRN_VALIDATOR.validateURN(urn);

    // If the URN passed the validation then convert the URN to URI
    final String gcp = urn.substring(urn.lastIndexOf(":") + 1, urn.indexOf('.'));
    String gsrn = gcp + urn.substring(urn.indexOf('.') + 1);
    gsrn = gsrn.substring(0, 17) + UPCEANLogicImpl.calcChecksum(gsrn.substring(0, 17));
    return Constants.IDENTIFIERDOMAIN + GSRN_URI_PART + gsrn;
  }

  // Convert the provided Digital Link URI to respective URN of GSRN Type
  public Map<String, String> convertToURN(String dlURI, int gcpLength) throws ValidationException {
    // Call the Validator class for the GSRN to check the DLURI syntax
    GSRN_VALIDATOR.validateURI(dlURI, gcpLength);

    // If the URI passed the validation then convert the URI to URN
    final String gsrn = dlURI.substring(dlURI.indexOf(GSRN_URI_PART) + GSRN_URI_PART.length());
    return getEPCMap(dlURI, gcpLength, gsrn);
  }

  private Map<String, String> getEPCMap(String dlURI, int gcpLength, String gsrn) {
    Map<String, String> buildURN = new HashMap<>();
    final String asURN =
        "urn:epc:id:gsrn:"
            + gsrn.substring(0, gcpLength)
            + "."
            + gsrn.substring(gcpLength, gsrn.length() - 1);
    if (dlURI.contains(Constants.IDENTIFIERDOMAIN)) {
      final String asCaptured =
          dlURI.replace(dlURI.substring(0, dlURI.indexOf(GSRN_URI_PART)), Constants.DLDOMAIN);
      buildURN.put(Constants.ASCAPTURED, asCaptured);
      buildURN.put(Constants.CANONICALDL, dlURI);
    } else {
      final String canonicalDL =
          dlURI.replace(
              dlURI.substring(0, dlURI.indexOf(GSRN_URI_PART)), Constants.IDENTIFIERDOMAIN);
      buildURN.put(Constants.ASCAPTURED, dlURI);
      buildURN.put(Constants.CANONICALDL, canonicalDL);
    }
    buildURN.put(Constants.ASURN, asURN);
    buildURN.put("gsrn", gsrn);
    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GSRN Type
  public Map<String, String> convertToURN(String dlURI) throws ValidationException {
    final String gsrn = dlURI.substring(dlURI.indexOf(GSRN_URI_PART) + GSRN_URI_PART.length());
    int gcpLength = GCPLengthProvider.getInstance().getGcpLength(gsrn);

    // Call the Validator class for the GSRN to check the DLURI syntax
    GSRN_VALIDATOR.validateURI(dlURI, gcpLength);

    // If the URI passed the validation then convert the URI to URN
    return getEPCMap(dlURI, gcpLength, gsrn);
  }
}
