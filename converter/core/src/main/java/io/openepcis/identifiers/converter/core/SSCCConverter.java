/*
 * Copyright (c) 2022-2025 benelog GmbH & Co. KG
 * All rights reserved.
 *
 * Unauthorized copying, modification, distribution,
 * or use of this work, via any medium, is strictly prohibited.
 *
 * benelog GmbH & Co. KG reserves all rights not expressly granted herein,
 * including the right to sell licenses for using this work.
 */
package io.openepcis.identifiers.converter.core;

import io.openepcis.identifiers.converter.DefaultGCPLengthProvider;
import io.openepcis.identifiers.converter.constants.ConstantDigitalLinkTranslatorInfo;
import io.openepcis.identifiers.converter.util.ConverterUtil;
import io.openepcis.identifiers.validator.core.epcis.compliant.SSCCValidator;
import io.openepcis.identifiers.validator.exception.ValidationException;

import java.util.HashMap;
import java.util.Map;

import static io.openepcis.constants.ApplicationIdentifierConstants.SSCC_AI_URI_PREFIX;
import static io.openepcis.constants.ApplicationIdentifierConstants.SSCC_AI_URN_PREFIX;
import static io.openepcis.constants.EPCIS.GS1_IDENTIFIER_DOMAIN;

public class SSCCConverter implements Converter {
  private static final SSCCValidator SSCC_VALIDATOR = new SSCCValidator();

  // Check if the provided URN is of SSCC type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(SSCC_AI_URN_PREFIX);
  }

  // Check if the provided Digital Link URI is of SSCC Type
  public boolean supportsURN(final String dlURI) {
    return dlURI.contains(SSCC_AI_URI_PREFIX);
  }

  // Convert the provided URN to respective Digital Link URI of SSCC type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Validate the DLURI to check if they match the SSCC syntax
      SSCC_VALIDATOR.validate(urn);

      final String gcp =
          urn.charAt(urn.indexOf('.') + 1)
              + urn.substring(urn.lastIndexOf(":") + 1, urn.indexOf('.'));
      String sscc = gcp + urn.substring(urn.indexOf('.') + 2);
      sscc = sscc.substring(0, 17) + ConverterUtil.checksum(sscc.substring(0, 17));
      sscc = GS1_IDENTIFIER_DOMAIN + SSCC_AI_URI_PREFIX + sscc;
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
      SSCC_VALIDATOR.validate(dlURI, gcpLength);

      final String sscc = dlURI.substring(dlURI.indexOf(SSCC_AI_URI_PREFIX) + SSCC_AI_URI_PREFIX.length());
      return getEPCMap(dlURI, gcpLength, sscc);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SSCC identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
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
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(SSCC_AI_URI_PREFIX)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_CAPTURED, dlURI);
      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_URN, asURN);
      buildURN.put("sscc", sscc);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the SSCC identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    SSCC_VALIDATOR.validate(asURN);

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of SSCC Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;
    try {
      final String sscc = dlURI.substring(dlURI.indexOf(SSCC_AI_URI_PREFIX) + SSCC_AI_URI_PREFIX.length());
      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, sscc, SSCC_AI_URI_PREFIX);

      // Validate the URN to check if they match the SGTIN syntax
      SSCC_VALIDATOR.validate(dlURI, gcpLength);

      return getEPCMap(dlURI, gcpLength, sscc);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SSCC identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
