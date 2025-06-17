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
import io.openepcis.identifiers.validator.core.epcis.compliant.GDTIValidator;

import java.util.HashMap;
import java.util.Map;

import static io.openepcis.constants.ApplicationIdentifierConstants.GDTI_AI_URI_PREFIX;
import static io.openepcis.constants.ApplicationIdentifierConstants.GDTI_AI_URN_PREFIX;
import static io.openepcis.constants.EPCIS.GS1_IDENTIFIER_DOMAIN;

public class GDTIConverter implements Converter {
  private static final GDTIValidator GDTI_VALIDATOR = new GDTIValidator();
  private boolean isClassLevel;

  public GDTIConverter() {}

  public GDTIConverter(boolean isClassLevel) {
    this.isClassLevel = isClassLevel;
  }

  // Check if the provided URN is of GDTI type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(GDTI_AI_URN_PREFIX);
  }

  // Check if the provided Digital Link URI is of GDTI Type
  public boolean supportsURN(final String dlURI) {
    return dlURI.contains(GDTI_AI_URI_PREFIX);
  }

  // Convert the provided URN to respective Digital Link URI of GDTI type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {

      // Call the Validator class for the GDTI to check the URN syntax
      GDTI_VALIDATOR.validate(urn);

      // If the URN passed the validation then convert the URN to URI
      final String gcp =
          urn.substring(urn.indexOf(GDTI_AI_URN_PREFIX) + GDTI_AI_URN_PREFIX.length(), urn.indexOf('.'));
      String gdti =
          gcp + urn.substring(urn.indexOf('.') + 1, urn.indexOf(".", urn.indexOf(".") + 1));
      gdti = gdti.substring(0, 12) + ConverterUtil.checksum(gdti.substring(0, 12));

      if (!isClassLevel) {
        gdti = gdti + urn.substring(urn.indexOf(".", urn.indexOf(".") + 1) + 1);
      }
      return GS1_IDENTIFIER_DOMAIN + GDTI_AI_URI_PREFIX + gdti;
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GDTI identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of GDTI Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Call the Validator class for the GDTI to check the DLURI syntax
      GDTI_VALIDATOR.validate(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN

      final String gdti =
          dlURI.substring(
              dlURI.indexOf(GDTI_AI_URI_PREFIX) + GDTI_AI_URI_PREFIX.length(),
              dlURI.indexOf(GDTI_AI_URI_PREFIX) + GDTI_AI_URI_PREFIX.length() + 13);

      return getEPCMap(dlURI, gcpLength, gdti);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GDTI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(
      final String dlURI, final int gcpLength, final String gdti) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;

    try {
      final String gdtiSubString = gdti.substring(gcpLength, gdti.length() - 1);

      if (isClassLevel) {
        asURN = "urn:epc:idpat:gdti:" + gdti.substring(0, gcpLength) + "." + gdtiSubString + ".*";
      } else {
        final String serial =
            dlURI.substring(dlURI.indexOf(GDTI_AI_URI_PREFIX) + GDTI_AI_URI_PREFIX.length() + 13);
        asURN =
            "urn:epc:id:gdti:" + gdti.substring(0, gcpLength) + "." + gdtiSubString + "." + serial;
        buildURN.put(ConstantDigitalLinkTranslatorInfo.SERIAL, serial);
      }

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(GDTI_AI_URI_PREFIX)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_CAPTURED, dlURI);
      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_URN, asURN);
      buildURN.put("gdti", gdti);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the GDTI identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    GDTI_VALIDATOR.validate(asURN);

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GDTI Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;

    try {
      String gdti;

      if (isClassLevel) {
        gdti = dlURI.substring(dlURI.indexOf(GDTI_AI_URI_PREFIX) + GDTI_AI_URI_PREFIX.length());
      } else {
        gdti =
            dlURI.substring(
                dlURI.indexOf(GDTI_AI_URI_PREFIX) + GDTI_AI_URI_PREFIX.length(),
                dlURI.indexOf(GDTI_AI_URI_PREFIX) + GDTI_AI_URI_PREFIX.length() + 13);
      }

      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, gdti, GDTI_AI_URI_PREFIX);

      // Call the Validator class for the GDTI to check the DLURI syntax
      GDTI_VALIDATOR.validate(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, gdti);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GDTI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
