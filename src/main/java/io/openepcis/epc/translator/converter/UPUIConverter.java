/*
 * Copyright 2022-2023 benelog GmbH & Co. KG
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

import static io.openepcis.constants.EPCIS.GS1_IDENTIFIER_DOMAIN;
import static io.openepcis.epc.translator.constants.ConstantDigitalLinkTranslatorInfo.*;

import io.openepcis.epc.translator.DefaultGCPLengthProvider;
import io.openepcis.epc.translator.exception.ValidationException;
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
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(UPUI_URN_PART);
  }

  // Check if the provided Digital Link URI is of UPUI Type
  public boolean supportsURN(final String dlURI) {
    return Pattern.compile("(?=.*/01/)(?=.*/235/)").matcher(dlURI).find();
  }

  // Convert the provided URN to respective Digital Link URI of UPUI type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
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
      return GS1_IDENTIFIER_DOMAIN + UPUI_URI_PART + upui + UPUI_SERIAL_PART + serialNumber;
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of UPUI identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of UPUI Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Call the Validator class for the UPUI to check the DLURI syntax
      UPUI_VALIDATOR.validateURI(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      final String upui =
          dlURI.substring(
              dlURI.indexOf(UPUI_URI_PART) + UPUI_URI_PART.length(),
              dlURI.indexOf(UPUI_SERIAL_PART));
      return getEPCMap(dlURI, gcpLength, upui);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of UPUI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(final String dlURI, final int gcpLength, String upui) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;
    try {
      upui =
          upui.substring(1, gcpLength + 1)
              + "."
              + upui.charAt(0)
              + upui.substring(gcpLength + 1, upui.length() - 1);
      final String serial =
          dlURI.substring(dlURI.indexOf(UPUI_SERIAL_PART) + UPUI_SERIAL_PART.length());
      asURN = "urn:epc:id:upui:" + upui + "." + serial;

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(UPUI_URI_PART)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(CANONICAL_DL, canonicalDL);
      }

      buildURN.put(AS_CAPTURED, dlURI);
      buildURN.put(AS_URN, asURN);
      buildURN.put("upui", upui);
      buildURN.put(SERIAL, serial);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the UPUI identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    UPUI_VALIDATOR.validateURN(asURN);

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of UPUI Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;

    try {
      final String upui =
          dlURI.substring(
              dlURI.indexOf(UPUI_URI_PART) + UPUI_URI_PART.length(),
              dlURI.indexOf(UPUI_SERIAL_PART));
      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, upui, UPUI_URI_PART);

      // Call the Validator class for the UPUI to check the DLURI syntax
      UPUI_VALIDATOR.validateURI(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, upui);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of UPUI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
