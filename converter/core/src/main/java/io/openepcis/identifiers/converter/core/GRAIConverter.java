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
import io.openepcis.identifiers.validator.core.epcis.compliant.GRAIValidator;
import io.openepcis.identifiers.validator.exception.ValidationException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static io.openepcis.constants.ApplicationIdentifierConstants.GRAI_AI_URI_PREFIX;
import static io.openepcis.constants.ApplicationIdentifierConstants.GRAI_AI_URN_PREFIX;
import static io.openepcis.constants.EPCIS.GS1_IDENTIFIER_DOMAIN;

public class GRAIConverter implements Converter {
  private static final GRAIValidator GRAI_VALIDATOR = new GRAIValidator();
  private boolean isClassLevel;

  public GRAIConverter() {
    super();
  }

  public GRAIConverter(final boolean isClassLevel) {
    this.isClassLevel = isClassLevel;
  }

  // Check if the provided URN is of GRAI type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(GRAI_AI_URN_PREFIX);
  }

  // Check if the provided Digital Link URI is of GRAI Type
  public boolean supportsURN(final String dlURI) {
    return dlURI.contains(GRAI_AI_URI_PREFIX);
  }

  // Convert the provided URN to respective Digital Link URI of GRAI type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Call the Validator class for the GRAI to check the URN syntax
      GRAI_VALIDATOR.validate(urn);

      // If the URN passed the validation then convert the URN to URI
      final String gcp =
          urn.substring(urn.indexOf(GRAI_AI_URN_PREFIX) + GRAI_AI_URN_PREFIX.length(), urn.indexOf('.'));
      String grai =
          gcp + urn.substring(urn.indexOf('.') + 1, urn.indexOf(".", urn.indexOf(".") + 1));
      grai = grai.substring(0, 12) + ConverterUtil.checksum(grai.substring(0, 12));

      if (isClassLevel) {
        return GS1_IDENTIFIER_DOMAIN + GRAI_AI_URI_PREFIX + grai;
      } else {
        final String serialNumber = urn.substring(urn.indexOf(".", urn.indexOf(".") + 1) + 1);
        return GS1_IDENTIFIER_DOMAIN + GRAI_AI_URI_PREFIX + grai + serialNumber;
      }
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GRAI identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of GRAI Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Call the Validator class for the GRAI to check the DLURI syntax
      GRAI_VALIDATOR.validate(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      final String grai =
          dlURI.substring(
              dlURI.indexOf(GRAI_AI_URI_PREFIX) + GRAI_AI_URI_PREFIX.length(),
              dlURI.indexOf(GRAI_AI_URI_PREFIX) + 19);
      return getEPCMap(dlURI, gcpLength, grai);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GRAI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(
      final String dlURI, final int gcpLength, final String grai) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;

    try {
      String serial;
      final String graiSubString = grai.substring(gcpLength, grai.length() - 1);

      if (isClassLevel) {
        asURN = "urn:epc:idpat:grai:" + grai.substring(0, gcpLength) + "." + graiSubString + ".*";
      } else {
        serial = dlURI.substring(dlURI.indexOf(GRAI_AI_URI_PREFIX) + 19);
        final String urnBase =
            "urn:epc:id:grai:" + grai.substring(0, gcpLength) + "." + graiSubString;
        asURN = StringUtils.isNotBlank(serial) ? urnBase + "." + serial : urnBase;
      }

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(GRAI_AI_URI_PREFIX)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_CAPTURED, dlURI);
      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_URN, asURN);
      buildURN.put("grai", grai);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the GRAI identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    GRAI_VALIDATOR.validate(asURN);

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GRAI Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;
    try {
      String grai;

      if (isClassLevel) {
        grai = dlURI.substring(dlURI.indexOf(GRAI_AI_URI_PREFIX) + GRAI_AI_URI_PREFIX.length());
      } else {
        grai =
            dlURI.substring(
                dlURI.indexOf(GRAI_AI_URI_PREFIX) + GRAI_AI_URI_PREFIX.length(),
                dlURI.indexOf(GRAI_AI_URI_PREFIX) + 19);
      }

      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, grai, GRAI_AI_URI_PREFIX);

      // Call the Validator class for the GRAI to check the DLURI syntax
      GRAI_VALIDATOR.validate(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, grai);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GRAI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
