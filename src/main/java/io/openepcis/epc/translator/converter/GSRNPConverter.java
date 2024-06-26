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
import io.openepcis.epc.translator.constants.ConstantDigitalLinkTranslatorInfo;
import io.openepcis.epc.translator.exception.ValidationException;
import io.openepcis.epc.translator.util.ConverterUtil;
import io.openepcis.epc.translator.validation.GSRNPValidator;
import java.util.HashMap;
import java.util.Map;

public class GSRNPConverter implements Converter {

  private static final String GSRNP_URI_PART = "/8017/";
  private static final GSRNPValidator GSRNP_VALIDATOR = new GSRNPValidator();

  // Check if the provided URN is of GSRNP type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(":gsrnp:");
  }

  // Check if the provided Digital Link URI is of GSRNP Type
  public boolean supportsURN(final String dlURI) {
    return dlURI.contains(GSRNP_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of GSRNP type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Call the Validator class for the GSRNP to check the URN syntax
      GSRNP_VALIDATOR.validateURN(urn);

      // If the URN passed the validation then convert the URN to URI
      final String gcp = urn.substring(urn.lastIndexOf(":") + 1, urn.indexOf('.'));
      String gsrnp = gcp + urn.substring(urn.indexOf('.') + 1);
      gsrnp = gsrnp.substring(0, 17) + ConverterUtil.checksum(gsrnp.substring(0, 17));
      return GS1_IDENTIFIER_DOMAIN + GSRNP_URI_PART + gsrnp;
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GSRNP identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of GSRNP Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Call the Validator class for the GSRNP to check the DLURI syntax
      GSRNP_VALIDATOR.validateURI(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      final String gsrnp = dlURI.substring(dlURI.indexOf(GSRNP_URI_PART) + GSRNP_URI_PART.length());
      return getEPCMap(dlURI, gcpLength, gsrnp);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GSRNP identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(
      final String dlURI, final int gcpLength, final String gsrnp) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;
    try {
      asURN =
          "urn:epc:id:gsrnp:"
              + gsrnp.substring(0, gcpLength)
              + "."
              + gsrnp.substring(gcpLength, gsrnp.length() - 1);

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(GSRNP_URI_PART)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(CANONICAL_DL, canonicalDL);
      }

      buildURN.put(AS_CAPTURED, dlURI);
      buildURN.put(AS_URN, asURN);
      buildURN.put("gsrnp", gsrnp);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the GSRNP identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    GSRNP_VALIDATOR.validateURN(asURN);

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GSRNP Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;
    try {
      final String gsrnp = dlURI.substring(dlURI.indexOf(GSRNP_URI_PART) + GSRNP_URI_PART.length());
      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, gsrnp, GSRNP_URI_PART);

      // Call the Validator class for the GSRNP to check the DLURI syntax
      GSRNP_VALIDATOR.validateURI(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, gsrnp);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GSRNP identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
