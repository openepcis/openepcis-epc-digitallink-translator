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
import io.openepcis.identifiers.validator.core.epcis.compliant.GINCValidator;
import io.openepcis.identifiers.validator.exception.ValidationException;

import java.util.HashMap;
import java.util.Map;

import static io.openepcis.constants.ApplicationIdentifierConstants.GINC_AI_URI_PREFIX;
import static io.openepcis.constants.ApplicationIdentifierConstants.GINC_AI_URN_PREFIX;
import static io.openepcis.constants.EPCIS.GS1_IDENTIFIER_DOMAIN;

public class GINCConverter implements Converter {
  private static final GINCValidator GINC_VALIDATOR = new GINCValidator();

  // Check if the provided URN is of GINC type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(GINC_AI_URN_PREFIX);
  }

  // Check if the provided Digital Link URI is of GINC Type
  public boolean supportsURN(final String dlURI) {
    return dlURI.contains(GINC_AI_URI_PREFIX);
  }

  // Convert the provided URN to respective Digital Link URI of GINC type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Validate the URN to check if they match the GINC syntax
      GINC_VALIDATOR.validate(urn);

      // If the URN passed the validation then convert the URN to URI
      String ginc =
          urn.substring(urn.indexOf(GINC_AI_URN_PREFIX) + GINC_AI_URN_PREFIX.length(), urn.indexOf("."));
      ginc = ginc + urn.substring(urn.indexOf(".") + 1);
      return GS1_IDENTIFIER_DOMAIN + GINC_AI_URI_PREFIX + ginc;
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
      GINC_VALIDATOR.validate(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      final String ginc = dlURI.substring(dlURI.indexOf(GINC_AI_URI_PREFIX) + GINC_AI_URI_PREFIX.length());
      return getEPCMap(dlURI, gcpLength, ginc);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GINC identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
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
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(GINC_AI_URI_PREFIX)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_CAPTURED, dlURI);
      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_URN, asURN);
      buildURN.put("ginc", ginc);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the GINC identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    GINC_VALIDATOR.validate(asURN);

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GINC Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;

    try {
      final String ginc = dlURI.substring(dlURI.indexOf(GINC_AI_URI_PREFIX) + GINC_AI_URI_PREFIX.length());
      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, ginc, GINC_AI_URI_PREFIX);

      // Call the Validator class for the GINC to check the DLURI syntax
      GINC_VALIDATOR.validate(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, ginc);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GINC identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
