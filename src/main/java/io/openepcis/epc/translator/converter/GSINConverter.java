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
import io.openepcis.epc.translator.validation.GSINValidator;
import java.util.HashMap;
import java.util.Map;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

public class GSINConverter implements Converter {

  private static final String GSIN_URI_PART = "/402/";
  private static final String GSIN_URN_PART = ":gsin:";
  private final GSINValidator gsinValidator = new GSINValidator();

  // Check if the provided URN is of GSIN type
  public boolean supportsDigitalLinkURI(String urn) {
    return urn.contains(GSIN_URN_PART);
  }

  // Check if the provided Digital Link URI is of GSIN Type
  public boolean supportsURN(String dlURI) {
    return dlURI.contains(GSIN_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of GSIN type
  public String convertToDigitalLink(String urn) throws ValidationException {
    // Call the Validator class for the GSIN to check the URN syntax
    gsinValidator.validateURN(urn);

    // If the URN passed the validation then convert the URN to URI
    String gsin =
        urn.substring(urn.indexOf(GSIN_URN_PART) + GSIN_URN_PART.length(), urn.indexOf("."))
            + urn.substring(urn.indexOf(".") + 1);
    gsin = gsin.substring(0, 16) + UPCEANLogicImpl.calcChecksum(gsin.substring(0, 16));
    return Constants.IDENTIFIERDOMAIN + GSIN_URI_PART + gsin;
  }

  // Convert the provided Digital Link URI to respective URN of GSIN Type
  public Map<String, String> convertToURN(String dlURI, int gcpLength) throws ValidationException {
    // Call the Validator class for the GSIN to check the DLURI syntax
    gsinValidator.validateURI(dlURI, gcpLength);

    // If the URI passed the validation then convert the URI to URN
    final String gsin = dlURI.substring(dlURI.indexOf(GSIN_URI_PART) + GSIN_URI_PART.length());
    return getEPCMap(dlURI, gcpLength, gsin);
  }

  private Map<String, String> getEPCMap(String dlURI, int gcpLength, String gsin) {
    Map<String, String> buildURN = new HashMap<>();
    final String asURN =
        "urn:epc:id:gsin:"
            + gsin.substring(0, gcpLength)
            + "."
            + gsin.substring(gcpLength, gsin.length() - 1);

    if (dlURI.contains(Constants.IDENTIFIERDOMAIN)) {
      final String asCaptured =
          dlURI.replace(dlURI.substring(0, dlURI.indexOf(GSIN_URI_PART)), Constants.DLDOMAIN);
      buildURN.put(Constants.ASCAPTURED, asCaptured);
      buildURN.put(Constants.CANONICALDL, dlURI);
    } else {
      final String canonicalDL =
          dlURI.replace(
              dlURI.substring(0, dlURI.indexOf(GSIN_URI_PART)), Constants.IDENTIFIERDOMAIN);
      buildURN.put(Constants.ASCAPTURED, dlURI);
      buildURN.put(Constants.CANONICALDL, canonicalDL);
    }
    buildURN.put(Constants.ASURN, asURN);
    buildURN.put("gsin", gsin);
    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GSIN Type
  public Map<String, String> convertToURN(String dlURI) throws ValidationException {
    final String gsin = dlURI.substring(dlURI.indexOf(GSIN_URI_PART) + GSIN_URI_PART.length());
    int gcpLength = GCPLengthProvider.getInstance().getGcpLength(gsin);

    // Call the Validator class for the GSIN to check the DLURI syntax
    gsinValidator.validateURI(dlURI, gcpLength);

    // If the URI passed the validation then convert the URI to URN
    return getEPCMap(dlURI, gcpLength, gsin);
  }
}
