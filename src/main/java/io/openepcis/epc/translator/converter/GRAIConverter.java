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
import io.openepcis.epc.translator.validation.GRAIValidator;
import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

@NoArgsConstructor
public class GRAIConverter implements Converter {

  private static final String GRAI_URI_PART = "/8003/";
  private static final String GRAI_URN_PART = ":grai:";
  private static final GRAIValidator GRAI_VALIDATOR = new GRAIValidator();
  private boolean isClassLevel;

  public GRAIConverter(boolean isClassLevel) {
    this.isClassLevel = isClassLevel;
  }

  // Check if the provided URN is of GRAI type
  public boolean supportsDigitalLinkURI(String urn) {
    return urn.contains(GRAI_URN_PART);
  }

  // Check if the provided Digital Link URI is of GRAI Type
  public boolean supportsURN(String dlURI) {
    return dlURI.contains(GRAI_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of GRAI type
  public String convertToDigitalLink(String urn) throws ValidationException {

    // Call the Validator class for the GRAI to check the URN syntax
    if (isClassLevel) {
      GRAI_VALIDATOR.validateClassLevelURN(urn);
    } else {
      GRAI_VALIDATOR.validateURN(urn);
    }

    // If the URN passed the validation then convert the URN to URI
    final String gcp =
        urn.substring(urn.indexOf(GRAI_URN_PART) + GRAI_URN_PART.length(), urn.indexOf('.'));
    String grai = gcp + urn.substring(urn.indexOf('.') + 1, urn.indexOf(".", urn.indexOf(".") + 1));
    grai = grai.substring(0, 12) + UPCEANLogicImpl.calcChecksum(grai.substring(0, 12));

    if (isClassLevel) {
      return Constants.IDENTIFIERDOMAIN + GRAI_URI_PART + grai;
    } else {
      final String serialNumber = urn.substring(urn.indexOf(".", urn.indexOf(".") + 1) + 1);
      return Constants.IDENTIFIERDOMAIN + GRAI_URI_PART + grai + serialNumber;
    }
  }

  // Convert the provided Digital Link URI to respective URN of GRAI Type
  public Map<String, String> convertToURN(String dlURI, int gcpLength) throws ValidationException {
    // Call the Validator class for the GRAI to check the DLURI syntax
    if (isClassLevel) {
      GRAI_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
    } else {
      GRAI_VALIDATOR.validateURI(dlURI, gcpLength);
    }

    // If the URI passed the validation then convert the URI to URN
    final String grai =
        dlURI.substring(
            dlURI.indexOf(GRAI_URI_PART) + GRAI_URI_PART.length(),
            dlURI.indexOf(GRAI_URI_PART) + 19);
    return getEPCMap(dlURI, gcpLength, grai);
  }

  private Map<String, String> getEPCMap(String dlURI, int gcpLength, String grai) {
    Map<String, String> buildURN = new HashMap<>();
    String asURN;
    String serial;
    final String graiSubString = grai.substring(gcpLength, grai.length() - 1);

    if (isClassLevel) {
      asURN = "urn:epc:idpat:grai:" + grai.substring(0, gcpLength) + "." + graiSubString + ".*";
    } else {
      serial = dlURI.substring(dlURI.indexOf(GRAI_URI_PART) + 19);
      asURN =
          "urn:epc:id:grai:" + grai.substring(0, gcpLength) + "." + graiSubString + "." + serial;
    }

    if (dlURI.contains(Constants.IDENTIFIERDOMAIN)) {
      final String asCaptured =
          dlURI.replace(dlURI.substring(0, dlURI.indexOf(GRAI_URI_PART)), Constants.DLDOMAIN);
      buildURN.put(Constants.ASCAPTURED, asCaptured);
      buildURN.put(Constants.CANONICALDL, dlURI);
    } else {
      final String canonicalDL =
          dlURI.replace(
              dlURI.substring(0, dlURI.indexOf(GRAI_URI_PART)), Constants.IDENTIFIERDOMAIN);
      buildURN.put(Constants.ASCAPTURED, dlURI);
      buildURN.put(Constants.CANONICALDL, canonicalDL);
    }
    buildURN.put(Constants.ASURN, asURN);
    buildURN.put("grai", grai);
    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GRAI Type
  public Map<String, String> convertToURN(String dlURI) throws ValidationException {
    String grai;

    if (isClassLevel) {
      grai = dlURI.substring(dlURI.indexOf(GRAI_URI_PART) + GRAI_URI_PART.length());
    } else {
      grai =
          dlURI.substring(
              dlURI.indexOf(GRAI_URI_PART) + GRAI_URI_PART.length(),
              dlURI.indexOf(GRAI_URI_PART) + 19);
    }

    int gcpLength = GCPLengthProvider.getInstance().getGcpLength(grai);

    // Call the Validator class for the GRAI to check the DLURI syntax
    if (isClassLevel) {
      GRAI_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
    } else {
      GRAI_VALIDATOR.validateURI(dlURI, gcpLength);
    }

    // If the URI passed the validation then convert the URI to URN
    return getEPCMap(dlURI, gcpLength, grai);
  }
}
