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
import io.openepcis.epc.translator.validation.UPUIValidator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

public class UPUIConverter implements Converter {

  private static final String UPUI_SERIAL_PART = "/235/";
  private static final String UPUI_URI_PART = "/01/";
  private static final String UPUI_URN_PART = ":upui:";
  private static final UPUIValidator UPUI_VALIDATOR = new UPUIValidator();

  // Check if the provided URN is of UPUI type
  public boolean supportsDigitalLinkURI(String urn) {
    return urn.contains(UPUI_URN_PART);
  }

  // Check if the provided Digital Link URI is of UPUI Type
  public boolean supportsURN(String dlURI) {
    return Pattern.compile("(?=.*/01/)(?=.*/235/)").matcher(dlURI).find();
  }

  // Convert the provided URN to respective Digital Link URI of UPUI type
  public String convertToDigitalLink(String urn) throws ValidationException {

    // Call the Validator class for the UPUI to check the URN syntax
    UPUI_VALIDATOR.validateURN(urn);

    // If the URN passed the validation then convert the URN to URI
    String upui =
        urn.charAt(StringUtils.ordinalIndexOf(urn, ".", 1) + 1)
            + urn.substring(
                urn.indexOf(UPUI_URN_PART) + UPUI_URN_PART.length(),
                StringUtils.ordinalIndexOf(urn, ".", 1));
    upui =
        upui
            + urn.substring(
                StringUtils.ordinalIndexOf(urn, ".", 1) + 2,
                StringUtils.ordinalIndexOf(urn, ".", 2));
    upui = upui + UPCEANLogicImpl.calcChecksum(upui);
    final String serialNumber = urn.substring(StringUtils.ordinalIndexOf(urn, ".", 2) + 1);
    return Constants.IDENTIFIERDOMAIN + UPUI_URI_PART + upui + UPUI_SERIAL_PART + serialNumber;
  }

  // Convert the provided Digital Link URI to respective URN of UPUI Type
  public Map<String, String> convertToURN(String dlURI, int gcpLength) throws ValidationException {

    // Call the Validator class for the UPUI to check the DLURI syntax
    UPUI_VALIDATOR.validateURI(dlURI, gcpLength);

    // If the URI passed the validation then convert the URI to URN
    String upui =
        dlURI.substring(
            dlURI.indexOf(UPUI_URI_PART) + UPUI_URI_PART.length(), dlURI.indexOf(UPUI_SERIAL_PART));
    return getEPCMap(dlURI, gcpLength, upui);
  }

  private Map<String, String> getEPCMap(String dlURI, int gcpLength, String upui) {
    Map<String, String> buildURN = new HashMap<>();
    upui =
        upui.substring(1, gcpLength + 1)
            + "."
            + upui.charAt(0)
            + upui.substring(gcpLength + 1, upui.length() - 1);
    final String serial =
        dlURI.substring(dlURI.indexOf(UPUI_SERIAL_PART) + UPUI_SERIAL_PART.length());
    final String asURN = "urn:epc:id:upui:" + upui + "." + serial;

    if (dlURI.contains(Constants.IDENTIFIERDOMAIN)) {
      final String asCaptured =
          dlURI.replace(dlURI.substring(0, dlURI.indexOf(UPUI_URI_PART)), Constants.DLDOMAIN);
      buildURN.put(Constants.ASCAPTURED, asCaptured);
      buildURN.put(Constants.CANONICALDL, dlURI);
    } else {
      final String canonicalDL =
          dlURI.replace(
              dlURI.substring(0, dlURI.indexOf(UPUI_URI_PART)), Constants.IDENTIFIERDOMAIN);
      buildURN.put(Constants.ASCAPTURED, dlURI);
      buildURN.put(Constants.CANONICALDL, canonicalDL);
    }
    buildURN.put(Constants.ASURN, asURN);
    buildURN.put("upui", upui);
    buildURN.put(Constants.SERIAL, serial);
    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of UPUI Type
  public Map<String, String> convertToURN(String dlURI) throws ValidationException {
    String upui =
        dlURI.substring(
            dlURI.indexOf(UPUI_URI_PART) + UPUI_URI_PART.length(), dlURI.indexOf(UPUI_SERIAL_PART));
    int gcpLength = GCPLengthProvider.getInstance().getGcpLength(upui);

    // Call the Validator class for the UPUI to check the DLURI syntax
    UPUI_VALIDATOR.validateURI(dlURI, gcpLength);

    // If the URI passed the validation then convert the URI to URN
    return getEPCMap(dlURI, gcpLength, upui);
  }
}
