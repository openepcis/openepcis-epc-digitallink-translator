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

import io.openepcis.core.exception.ValidationException;
import io.openepcis.digitallink.utils.DefaultGCPLengthProvider;
import io.openepcis.identifiers.converter.constants.ConstantDigitalLinkTranslatorInfo;
import io.openepcis.identifiers.converter.util.ConverterUtil;
import io.openepcis.identifiers.validator.core.epcis.compliant.GSRNValidator;

import java.util.HashMap;
import java.util.Map;

import static io.openepcis.constants.ApplicationIdentifierConstants.GSRN_AI_URI_PREFIX;
import static io.openepcis.constants.ApplicationIdentifierConstants.GSRN_AI_URN_PREFIX;
import static io.openepcis.constants.EPCIS.GS1_IDENTIFIER_DOMAIN;

public class GSRNConverter implements Converter {

  private static final GSRNValidator GSRN_VALIDATOR = new GSRNValidator();

  // Check if the provided URN is of GSRN type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(GSRN_AI_URN_PREFIX);
  }

  // Check if the provided Digital Link URI is of GSRN Type
  public boolean supportsURN(final String dlURI) {
    return dlURI.contains(GSRN_AI_URI_PREFIX);
  }

  // Convert the provided URN to respective Digital Link URI of GSRN type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Call the Validator class for the GSRN to check the URN syntax
      GSRN_VALIDATOR.validate(urn);

      // If the URN passed the validation then convert the URN to URI
      final String gcp = urn.substring(urn.lastIndexOf(":") + 1, urn.indexOf('.'));
      String gsrn = gcp + urn.substring(urn.indexOf('.') + 1);
      gsrn = gsrn.substring(0, 17) + ConverterUtil.checksum(gsrn.substring(0, 17));
      return GS1_IDENTIFIER_DOMAIN + GSRN_AI_URI_PREFIX + gsrn;
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GSRN identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of GSRN Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Call the Validator class for the GSRN to check the DLURI syntax
      GSRN_VALIDATOR.validate(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      final String gsrn = dlURI.substring(dlURI.indexOf(GSRN_AI_URI_PREFIX) + GSRN_AI_URI_PREFIX.length());
      return getEPCMap(dlURI, gcpLength, gsrn);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GSRN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(
      final String dlURI, final int gcpLength, final String gsrn) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;

    try {
      asURN =
          "urn:epc:id:gsrn:"
              + gsrn.substring(0, gcpLength)
              + "."
              + gsrn.substring(gcpLength, gsrn.length() - 1);

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(GSRN_AI_URI_PREFIX)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_CAPTURED, dlURI);
      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_URN, asURN);
      buildURN.put("gsrn", gsrn);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the GSRN identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    GSRN_VALIDATOR.validate(asURN);

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GSRN Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;
    try {
      final String gsrn = dlURI.substring(dlURI.indexOf(GSRN_AI_URI_PREFIX) + GSRN_AI_URI_PREFIX.length());
      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, gsrn, GSRN_AI_URI_PREFIX);

      // Call the Validator class for the GSRN to check the DLURI syntax
      GSRN_VALIDATOR.validate(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, gsrn);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GSRN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
