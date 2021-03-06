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
import io.openepcis.epc.translator.validation.GCNValidator;
import java.util.HashMap;
import java.util.Map;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

public class GCNConverter implements Converter {

  private static final String GCN_URI_PART = "/255/";
  private static final GCNValidator GCN_VALIDATOR = new GCNValidator();
  private boolean isClassLevel;

  public GCNConverter() {}

  public GCNConverter(boolean isClassLevel) {
    this.isClassLevel = isClassLevel;
  }

  // Check if the provided URN is of GCN type
  public boolean supportsDigitalLinkURI(String urn) {
    return urn.contains(":sgcn:");
  }

  // Check if the provided Digital Link URI is of GCN Type
  public boolean supportsURN(String dlURI) {
    return dlURI.contains(GCN_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of GCN type
  public String convertToDigitalLink(String urn) throws ValidationException {

    // Call the Validator class for the GCN to check the URN syntax
    if (isClassLevel) {
      GCN_VALIDATOR.validateClassLevelURN(urn);
    } else {
      GCN_VALIDATOR.validateURN(urn);
    }

    // If the URN passed the validation then convert the URN to URI
    final String gcp = urn.substring(urn.lastIndexOf(":") + 1, urn.indexOf("."));
    String sgcn = gcp + urn.substring(urn.indexOf('.') + 1, urn.indexOf(".", urn.indexOf(".") + 1));
    sgcn = sgcn.substring(0, 12) + UPCEANLogicImpl.calcChecksum(sgcn.substring(0, 12));

    if (!isClassLevel) {
      sgcn = sgcn + urn.substring(urn.indexOf(".", urn.indexOf(".") + 1) + 1);
    }
    return Constants.IDENTIFIERDOMAIN + GCN_URI_PART + sgcn;
  }

  // Convert the provided Digital Link URI to respective URN of GCN Type
  public Map<String, String> convertToURN(String dlURI, int gcpLength) throws ValidationException {
    // Call the Validator class for the GCN to check the DLURI syntax
    if (isClassLevel) {
      GCN_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
    } else {
      GCN_VALIDATOR.validateURI(dlURI, gcpLength);
    }

    // If the URI passed the validation then convert the URI to URN
    String sgcn = dlURI.substring(dlURI.indexOf(GCN_URI_PART) + GCN_URI_PART.length());
    return getEPCMap(dlURI, gcpLength, sgcn);
  }

  private Map<String, String> getEPCMap(String dlURI, int gcpLength, String sgcn) {
    Map<String, String> buildURN = new HashMap<>();
    String asURN;
    String tempSgcn = sgcn.substring(0, 13);
    tempSgcn =
        tempSgcn.substring(0, gcpLength)
            + "."
            + tempSgcn.substring(gcpLength, tempSgcn.length() - 1);

    if (isClassLevel) {
      asURN = "urn:epc:idpat:sgcn:" + tempSgcn + ".*";
    } else {
      final String serial = sgcn.substring(13);
      asURN = "urn:epc:id:sgcn:" + tempSgcn + "." + serial;
      buildURN.put(Constants.SERIAL, serial);
    }

    if (dlURI.contains(Constants.IDENTIFIERDOMAIN)) {
      final String asCaptured =
          dlURI.replace(dlURI.substring(0, dlURI.indexOf(GCN_URI_PART)), Constants.DLDOMAIN);
      buildURN.put(Constants.ASCAPTURED, asCaptured);
      buildURN.put(Constants.CANONICALDL, dlURI);
    } else {
      final String canonicalDL =
          dlURI.replace(
              dlURI.substring(0, dlURI.indexOf(GCN_URI_PART)), Constants.IDENTIFIERDOMAIN);
      buildURN.put(Constants.ASCAPTURED, dlURI);
      buildURN.put(Constants.CANONICALDL, canonicalDL);
    }
    buildURN.put(Constants.ASURN, asURN);
    buildURN.put("sgcn", sgcn);
    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GCN Type
  public Map<String, String> convertToURN(String dlURI) throws ValidationException {
    String sgcn = dlURI.substring(dlURI.indexOf(GCN_URI_PART) + GCN_URI_PART.length());
    int gcpLength = GCPLengthProvider.getInstance().getGcpLength(sgcn);

    // Call the Validator class for the GCN to check the DLURI syntax
    if (isClassLevel) {
      GCN_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
    } else {
      GCN_VALIDATOR.validateURI(dlURI, gcpLength);
    }
    // If the URI passed the validation then convert the URI to URN
    return getEPCMap(dlURI, gcpLength, sgcn);
  }
}
