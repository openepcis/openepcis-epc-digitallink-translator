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

import io.openepcis.epc.translator.DefaultGCPLengthProvider;
import io.openepcis.epc.translator.constants.Constants;
import io.openepcis.epc.translator.exception.ValidationException;
import io.openepcis.epc.translator.validation.GIAIValidator;
import java.util.HashMap;
import java.util.Map;

public class GIAIConverter implements Converter {

  private static final String GIAI_URI_PART = "/8004/";
  private static final String GIAI_URN_PART = ":giai:";
  private static final GIAIValidator GIAI_VALIDATOR = new GIAIValidator();

  // Check if the provided URN is of GIAI type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(GIAI_URN_PART);
  }

  // Check if the provided Digital Link URI is of GIAI Type
  public boolean supportsURN(final String dlURI) {
    return dlURI.contains(GIAI_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of GIAI type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {

      // Call the Validator class for the GIAI to check the URN syntax
      GIAI_VALIDATOR.validateURN(urn);

      // If the URN passed the validation then convert the URN to URI
      final String gcp =
          urn.substring(urn.indexOf(GIAI_URN_PART) + GIAI_URN_PART.length(), urn.indexOf('.'));
      final String giai = gcp + urn.substring(urn.indexOf('.') + 1);
      return Constants.GS1_IDENTIFIER_DOMAIN + GIAI_URI_PART + giai;
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GIAI identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of GIAI Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Call the Validator class for the GIAI to check the DLURI syntax
      GIAI_VALIDATOR.validateURI(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      final String giai = dlURI.substring(dlURI.indexOf(GIAI_URI_PART) + GIAI_URI_PART.length());
      return getEPCMap(dlURI, gcpLength, giai);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GIAI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + Constants.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(
      final String dlURI, final int gcpLength, final String giai) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;
    try {
      asURN = "urn:epc:id:giai:" + giai.substring(0, gcpLength) + "." + giai.substring(gcpLength);

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(Constants.GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(Constants.CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(
                dlURI.substring(0, dlURI.indexOf(GIAI_URI_PART)), Constants.GS1_IDENTIFIER_DOMAIN);
        buildURN.put(Constants.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(Constants.AS_CAPTURED, dlURI);
      buildURN.put(Constants.AS_URN, asURN);
      buildURN.put("giai", giai);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the GIAI identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + Constants.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    GIAI_VALIDATOR.validateURN(asURN);

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GIAI Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;

    try {
      final String giai = dlURI.substring(dlURI.indexOf(GIAI_URI_PART) + GIAI_URI_PART.length());

      // Get the gcpLength from the GS1 provided list
      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, giai, GIAI_URI_PART);

      // Call the Validator class for the GIAI to check the DLURI syntax
      GIAI_VALIDATOR.validateURI(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, giai);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GIAI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + Constants.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
