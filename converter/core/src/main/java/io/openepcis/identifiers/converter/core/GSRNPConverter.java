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
import io.openepcis.identifiers.validator.core.epcis.compliant.GSRNPValidator;
import io.openepcis.identifiers.validator.exception.ValidationException;

import java.util.HashMap;
import java.util.Map;

import static io.openepcis.constants.ApplicationIdentifierConstants.GSRNP_AI_URN_PREFIX;
import static io.openepcis.constants.EPCIS.GS1_IDENTIFIER_DOMAIN;

public class GSRNPConverter implements Converter {

  private static final String GSRNP_URI_PART = "/8017/";
  private static final GSRNPValidator GSRNP_VALIDATOR = new GSRNPValidator();

  // Check if the provided URN is of GSRNP type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(GSRNP_AI_URN_PREFIX);
  }

  // Check if the provided Digital Link URI is of GSRNP Type
  public boolean supportsURN(final String dlURI) {
    return dlURI.contains(GSRNP_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of GSRNP type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Call the Validator class for the GSRNP to check the URN syntax
      GSRNP_VALIDATOR.validate(urn);

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
      GSRNP_VALIDATOR.validate(dlURI, gcpLength);

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
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(GSRNP_URI_PART)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_CAPTURED, dlURI);
      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_URN, asURN);
      buildURN.put("gsrnp", gsrnp);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the GSRNP identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    GSRNP_VALIDATOR.validate(asURN);

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GSRNP Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;
    try {
      final String gsrnp = dlURI.substring(dlURI.indexOf(GSRNP_URI_PART) + GSRNP_URI_PART.length());
      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, gsrnp, GSRNP_URI_PART);

      // Call the Validator class for the GSRNP to check the DL URI syntax
      GSRNP_VALIDATOR.validate(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
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
}
