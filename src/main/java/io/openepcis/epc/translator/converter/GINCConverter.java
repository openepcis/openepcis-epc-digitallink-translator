/*
 * Copyright 2022-2024 benelog GmbH & Co. KG
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
import io.openepcis.epc.translator.validation.GINCValidator;
import java.util.HashMap;
import java.util.Map;

public class GINCConverter implements Converter {

  private static final String GINC_URI_PART = "/401/";
  private static final String GINC_URN_PART = ":ginc:";
  private static final GINCValidator GINC_VALIDATOR = new GINCValidator();

  // Check if the provided URN is of GINC type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(GINC_URN_PART);
  }

  // Check if the provided Digital Link URI is of GINC Type
  public boolean supportsURN(final String dlURI) {
    return dlURI.contains(GINC_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of GINC type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Validate the URN to check if they match the GINC syntax
      GINC_VALIDATOR.validateURN(urn);

      // If the URN passed the validation then convert the URN to URI
      String ginc =
          urn.substring(urn.indexOf(GINC_URN_PART) + GINC_URN_PART.length(), urn.indexOf("."));
      ginc = ginc + urn.substring(urn.indexOf(".") + 1);
      return GS1_IDENTIFIER_DOMAIN + GINC_URI_PART + ginc;
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GINC identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of GINC Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Call the Validator class for the GINC to check the DLURI syntax
      GINC_VALIDATOR.validateURI(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      final String ginc = dlURI.substring(dlURI.indexOf(GINC_URI_PART) + GINC_URI_PART.length());
      return getEPCMap(dlURI, gcpLength, ginc);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GINC identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(
      final String dlURI, final int gcpLength, final String ginc) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;
    try {
      asURN = "urn:epc:id:ginc:" + ginc.substring(0, gcpLength) + "." + ginc.substring(gcpLength);

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(GINC_URI_PART)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(CANONICAL_DL, canonicalDL);
      }

      buildURN.put(AS_CAPTURED, dlURI);
      buildURN.put(AS_URN, asURN);
      buildURN.put("ginc", ginc);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the GINC identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    GINC_VALIDATOR.validateURN(asURN);

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GINC Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;
    try {
      final String ginc = dlURI.substring(dlURI.indexOf(GINC_URI_PART) + GINC_URI_PART.length());
      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, ginc, GINC_URI_PART);

      // Call the Validator class for the GINC to check the DLURI syntax
      GINC_VALIDATOR.validateURI(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, ginc);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GINC identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
