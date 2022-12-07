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
import io.openepcis.epc.translator.exception.ValidationException;
import io.openepcis.epc.translator.validation.PGLNValidator;
import java.util.HashMap;
import java.util.Map;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

public class PGLNConverter implements Converter {

  private static final String PGLN_URI_PART = "/417/";
  private static final PGLNValidator PGLN_VALIDATOR = new PGLNValidator();

  // Check if the provided URN is of PGLN type
  public boolean supportsDigitalLinkURI(String urn) {
    return urn.contains(":pgln:");
  }

  // Check if the provided Digital Link URI is of PGLN Type
  public boolean supportsURN(String dlURI) {
    return dlURI.contains(PGLN_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of PGLN type
  public String convertToDigitalLink(String urn) throws ValidationException {

    // Call the Validator class for the PGLN to check the URN syntax
    PGLN_VALIDATOR.validateURN(urn);

    // If the URN passed the validation then convert the URN to URI
    final String gcp = urn.substring(urn.lastIndexOf(":") + 1, urn.indexOf("."));
    String pgln = gcp + urn.substring(urn.indexOf(".") + 1);
    pgln = pgln.substring(0, 12) + UPCEANLogicImpl.calcChecksum(pgln.substring(0, 12));
    return Constants.IDENTIFIERDOMAIN + PGLN_URI_PART + pgln;
  }

  // Convert the provided Digital Link URI to respective URN of PGLN Type
  public Map<String, String> convertToURN(String dlURI, int gcpLength) throws ValidationException {

    // Call the Validator class for the PGLN to check the DLURI syntax
    PGLN_VALIDATOR.validateURI(dlURI, gcpLength);

    // If the URI passed the validation then convert the URI to URN
    final String pgln = dlURI.substring(dlURI.indexOf(PGLN_URI_PART) + PGLN_URI_PART.length());
    return getEPCMap(dlURI, gcpLength, pgln);
  }

  private Map<String, String> getEPCMap(String dlURI, int gcpLength, String pgln) {
    Map<String, String> buildURN = new HashMap<>();
    final String asURN =
        "urn:epc:id:pgln:"
            + pgln.substring(0, gcpLength)
            + "."
            + pgln.substring(gcpLength, pgln.length() - 1);

    if (dlURI.contains(Constants.IDENTIFIERDOMAIN)) {
      final String asCaptured =
          dlURI.replace(dlURI.substring(0, dlURI.indexOf(PGLN_URI_PART)), Constants.DLDOMAIN);
      buildURN.put(Constants.ASCAPTURED, asCaptured);
      buildURN.put(Constants.CANONICALDL, dlURI);
    } else {
      final String canonicalDL =
          dlURI.replace(
              dlURI.substring(0, dlURI.indexOf(PGLN_URI_PART)), Constants.IDENTIFIERDOMAIN);
      buildURN.put(Constants.ASCAPTURED, dlURI);
      buildURN.put(Constants.CANONICALDL, canonicalDL);
    }
    buildURN.put(Constants.ASURN, asURN);
    buildURN.put("pgln", pgln);
    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of PGLN Type
  public Map<String, String> convertToURN(String dlURI) throws ValidationException {
    final String pgln = dlURI.substring(dlURI.indexOf(PGLN_URI_PART) + PGLN_URI_PART.length());
    int gcpLength = GCPLengthProvider.getInstance().getGcpLength(pgln);

    // Call the Validator class for the PGLN to check the DLURI syntax
    PGLN_VALIDATOR.validateURI(dlURI, gcpLength);

    // If the URI passed the validation then convert the URI to URN
    return getEPCMap(dlURI, gcpLength, pgln);
  }
}
