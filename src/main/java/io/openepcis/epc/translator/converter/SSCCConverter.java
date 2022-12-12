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
import io.openepcis.epc.translator.validation.SSCCValidator;
import java.util.HashMap;
import java.util.Map;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

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
      sscc = sscc.substring(0, 17) + UPCEANLogicImpl.calcChecksum(sscc.substring(0, 17));
      sscc = Constants.IDENTIFIERDOMAIN + SSCC_URI_PART + sscc;
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
              + " GCP Length : "
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(
      final String dlURI, final int gcpLength, final String sscc) {
    try {
      final Map<String, String> buildURN = new HashMap<>();
      final String ssccURN =
          sscc.substring(1, gcpLength + 1)
              + "."
              + sscc.charAt(0)
              + sscc.substring(gcpLength + 1, sscc.length() - 1);
      final String asURN = "urn:epc:id:sscc:" + ssccURN;

      if (dlURI.contains(Constants.IDENTIFIERDOMAIN)) {
        final String asCaptured =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(SSCC_URI_PART)), Constants.DLDOMAIN);
        buildURN.put(Constants.ASCAPTURED, asCaptured);
        buildURN.put(Constants.CANONICALDL, dlURI);
      } else {
        final String canonicalDL =
            dlURI.replace(
                dlURI.substring(0, dlURI.indexOf(SSCC_URI_PART)), Constants.IDENTIFIERDOMAIN);
        buildURN.put(Constants.ASCAPTURED, dlURI);
        buildURN.put(Constants.CANONICALDL, canonicalDL);
      }

      buildURN.put(Constants.ASURN, asURN);
      buildURN.put("sscc", sscc);
      return buildURN;
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the SSCC identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of SSCC Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    try {
      final String sscc = dlURI.substring(dlURI.indexOf(SSCC_URI_PART) + SSCC_URI_PART.length());
      int gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(sscc);
      // Validate the URN to check if they match the SGTIN syntax
      SSCC_VALIDATOR.validateURI(dlURI, gcpLength);
      return getEPCMap(dlURI, gcpLength, sscc);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SSCC identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + "\n"
              + exception.getMessage());
    }
  }
}
