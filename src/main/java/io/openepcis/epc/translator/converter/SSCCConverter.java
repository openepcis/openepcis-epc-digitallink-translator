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
import io.openepcis.epc.translator.util.ConverterUtil;
import io.openepcis.epc.translator.validation.SSCCValidator;
import java.util.HashMap;
import java.util.Map;

public class SSCCConverter implements Converter {

  private static final String SSCC_URI_PART = "/00/";
  private static final SSCCValidator SSCC_VALIDATOR = new SSCCValidator();

  // Check if the provided URN is of SSCC type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(":sscc:");
  }

  // Check if the provided Digital Link URI is of SSCC Type
  public boolean supportsURN(final String dlURI) {
    return dlURI.contains(SSCC_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of SSCC type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Validate the DLURI to check if they match the SSCC syntax
      SSCC_VALIDATOR.validateURN(urn);

      final String gcp =
          urn.charAt(urn.indexOf('.') + 1)
              + urn.substring(urn.lastIndexOf(":") + 1, urn.indexOf('.'));
      String sscc = gcp + urn.substring(urn.indexOf('.') + 2);
      sscc = sscc.substring(0, 17) + ConverterUtil.checksum(sscc.substring(0, 17));
      sscc = GS1_IDENTIFIER_DOMAIN + SSCC_URI_PART + sscc;
      return sscc;
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SSCC identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of SSCC Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Validate the URN to check if they match the SGTIN syntax
      SSCC_VALIDATOR.validateURI(dlURI, gcpLength);

      final String sscc = dlURI.substring(dlURI.indexOf(SSCC_URI_PART) + SSCC_URI_PART.length());
      return getEPCMap(dlURI, gcpLength, sscc);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SSCC identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(
      final String dlURI, final int gcpLength, final String sscc) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;

    try {
      final String ssccURN =
          sscc.substring(1, gcpLength + 1)
              + "."
              + sscc.charAt(0)
              + sscc.substring(gcpLength + 1, sscc.length() - 1);
      asURN = "urn:epc:id:sscc:" + ssccURN;

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(SSCC_URI_PART)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(CANONICAL_DL, canonicalDL);
      }

      buildURN.put(AS_CAPTURED, dlURI);
      buildURN.put(AS_URN, asURN);
      buildURN.put("sscc", sscc);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the SSCC identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    SSCC_VALIDATOR.validateURN(asURN);

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of SSCC Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;
    try {
      final String sscc = dlURI.substring(dlURI.indexOf(SSCC_URI_PART) + SSCC_URI_PART.length());
      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, sscc, SSCC_URI_PART);

      // Validate the URN to check if they match the SGTIN syntax
      SSCC_VALIDATOR.validateURI(dlURI, gcpLength);

      return getEPCMap(dlURI, gcpLength, sscc);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SSCC identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
