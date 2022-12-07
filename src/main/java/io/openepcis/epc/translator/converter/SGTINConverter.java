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
import io.openepcis.epc.translator.validation.SGTINValidator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

public class SGTINConverter implements Converter {

  private static final String SGTIN_SERIAL_PART = "/21/";
  private static final String SGTIN_URI_PART = "/01/";
  private static final SGTINValidator SGTIN_VALIDATOR = new SGTINValidator();
  private boolean isClassLevel;
  private static final String SGTIN_URN_PART = ":sgtin:";

  public SGTINConverter() {
    super();
  }

  public SGTINConverter(boolean isClassLevel) {
    this.isClassLevel = isClassLevel;
  }

  // Check if the provided URN is SGTIN
  public boolean supportsDigitalLinkURI(String urn) {
    return urn.contains(SGTIN_URN_PART);
  }

  // Check if the provided DL URI is SGTIN
  public boolean supportsURN(String dlURI) {
    if (isClassLevel) {
      return Pattern.compile("(?=.*/01/)(?!.*/10/)").matcher(dlURI).find();
    } else {
      return Pattern.compile("(?=.*/01/)(?=.*/21/)").matcher(dlURI).find();
    }
  }

  // Convert to SGTIN Digital Link URI
  public String convertToDigitalLink(String urn) throws ValidationException {

    // Validate the URN to check if they match the SGTIN syntax
    if (isClassLevel) {
      SGTIN_VALIDATOR.validateClassLevelURN(urn);
    } else {
      SGTIN_VALIDATOR.validateURN(urn);
    }

    final String gcp =
        urn.charAt(urn.indexOf('.') + 1)
            + urn.substring(
                urn.indexOf(SGTIN_URN_PART) + SGTIN_URN_PART.length(), urn.indexOf('.'));
    String sgtin =
        gcp + urn.substring(urn.indexOf('.') + 2, urn.indexOf(".", urn.indexOf(".") + 1));
    sgtin = sgtin.substring(0, 13) + UPCEANLogicImpl.calcChecksum(sgtin.substring(0, 13));

    if (isClassLevel) {
      sgtin = Constants.IDENTIFIERDOMAIN + SGTIN_URI_PART + sgtin;
    } else {
      final String serialNumber = urn.substring(urn.indexOf(".", urn.indexOf(".") + 1) + 1);
      sgtin =
          Constants.IDENTIFIERDOMAIN + SGTIN_URI_PART + sgtin + SGTIN_SERIAL_PART + serialNumber;
    }
    return sgtin;
  }

  // Convert to SGTIN URN
  public Map<String, String> convertToURN(String dlURI, int gcpLength) throws ValidationException {

    String sgtin;

    // Validate the URN to check if they match the SGTIN syntax
    if (isClassLevel) {
      sgtin = dlURI.substring(dlURI.indexOf(SGTIN_URI_PART) + SGTIN_URI_PART.length());
      SGTIN_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
    } else {
      sgtin =
          dlURI.substring(
              dlURI.indexOf(SGTIN_URI_PART) + SGTIN_URI_PART.length(),
              dlURI.indexOf(SGTIN_SERIAL_PART));
      SGTIN_VALIDATOR.validateURI(dlURI, gcpLength);
    }

    return getEPCMap(dlURI, gcpLength, sgtin);
  }

  private Map<String, String> getEPCMap(String dlURI, int gcpLength, String sgtin) {
    Map<String, String> buildURN = new HashMap<>();
    final String sgtinUrn =
        sgtin.substring(1, gcpLength + 1)
            + "."
            + sgtin.charAt(0)
            + sgtin.substring(gcpLength + 1, sgtin.length() - 1);

    String asURN;
    if (isClassLevel) {
      asURN = "urn:epc:idpat:sgtin:" + sgtinUrn + ".*";
    } else {
      final String serial =
          dlURI.substring(dlURI.indexOf(SGTIN_SERIAL_PART) + SGTIN_SERIAL_PART.length());
      asURN = "urn:epc:id:sgtin:" + sgtinUrn + "." + serial;
      buildURN.put(Constants.SERIAL, serial);
    }

    if (dlURI.contains(Constants.IDENTIFIERDOMAIN)) {
      final String asCaptured =
          dlURI.replace(dlURI.substring(0, dlURI.indexOf(SGTIN_URI_PART)), Constants.DLDOMAIN);
      buildURN.put(Constants.ASCAPTURED, asCaptured);
      buildURN.put(Constants.CANONICALDL, dlURI);
    } else {
      final String canonicalDL =
          dlURI.replace(
              dlURI.substring(0, dlURI.indexOf(SGTIN_URI_PART)), Constants.IDENTIFIERDOMAIN);
      buildURN.put(Constants.ASCAPTURED, dlURI);
      buildURN.put(Constants.CANONICALDL, canonicalDL);
    }
    buildURN.put(Constants.ASURN, asURN);
    buildURN.put("gtin", sgtin);
    return buildURN;
  }

  // Convert to SGTIN URN
  public Map<String, String> convertToURN(String dlURI) throws ValidationException {
    String sgtin;
    if (isClassLevel) {
      sgtin = dlURI.substring(dlURI.indexOf(SGTIN_URI_PART) + SGTIN_URI_PART.length());
    } else {
      sgtin =
          dlURI.substring(
              dlURI.indexOf(SGTIN_URI_PART) + SGTIN_URI_PART.length(),
              dlURI.indexOf(SGTIN_SERIAL_PART));
    }

    int gcpLength = GCPLengthProvider.getInstance().getGcpLength(sgtin);

    // Validate the URN to check if they match the SGTIN syntax
    if (isClassLevel) {
      SGTIN_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
    } else {
      SGTIN_VALIDATOR.validateURI(dlURI, gcpLength);
    }

    return getEPCMap(dlURI, gcpLength, sgtin);
  }
}
