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
import io.openepcis.identifiers.validator.core.epcis.compliant.UPUIValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static io.openepcis.constants.ApplicationIdentifierConstants.*;
import static io.openepcis.constants.EPCIS.GS1_IDENTIFIER_DOMAIN;

public class UPUIConverter implements Converter {
  private static final UPUIValidator UPUI_VALIDATOR = new UPUIValidator();

  // Check if the provided URN is of UPUI type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(UPUI_AI_URN_PREFIX);
  }

  // Check if the provided Digital Link URI is of UPUI Type
  public boolean supportsURN(final String dlURI) {
    return Pattern.compile("(?=.*/01/)(?=.*/235/)").matcher(dlURI).find();
  }

  // Convert the provided URN to respective Digital Link URI of UPUI type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Call the Validator class for the UPUI to check the URN syntax
      UPUI_VALIDATOR.validate(urn);

      // If the URN passed the validation then convert the URN to URI
      String upui =
          urn.charAt(StringUtils.ordinalIndexOf(urn, ".", 1) + 1)
              + urn.substring(
                  urn.indexOf(UPUI_AI_URN_PREFIX) + UPUI_AI_URN_PREFIX.length(),
                  StringUtils.ordinalIndexOf(urn, ".", 1));
      upui =
          upui
              + urn.substring(
                  StringUtils.ordinalIndexOf(urn, ".", 1) + 2,
                  StringUtils.ordinalIndexOf(urn, ".", 2));
      upui = upui + ConverterUtil.checksum(upui);
      final String serialNumber = urn.substring(StringUtils.ordinalIndexOf(urn, ".", 2) + 1);
      return GS1_IDENTIFIER_DOMAIN + UPUI_AI_URI_PREFIX + upui + UPUI_AI_URI_SERIAL_PREFIX + serialNumber;
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of UPUI identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of UPUI Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Call the Validator class for the UPUI to check the DLURI syntax
      UPUI_VALIDATOR.validate(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      final String upui =
          dlURI.substring(
              dlURI.indexOf(UPUI_AI_URI_PREFIX) + UPUI_AI_URI_PREFIX.length(),
              dlURI.indexOf(UPUI_AI_URI_SERIAL_PREFIX));
      return getEPCMap(dlURI, gcpLength, upui);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of UPUI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(final String dlURI, final int gcpLength, String upui) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;
    try {
      upui =
          upui.substring(1, gcpLength + 1)
              + "."
              + upui.charAt(0)
              + upui.substring(gcpLength + 1, upui.length() - 1);
      final String serial =
          dlURI.substring(dlURI.indexOf(UPUI_AI_URI_SERIAL_PREFIX) + UPUI_AI_URI_SERIAL_PREFIX.length());
      asURN = "urn:epc:id:upui:" + upui + "." + serial;

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(UPUI_AI_URI_PREFIX)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_CAPTURED, dlURI);
      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_URN, asURN);
      buildURN.put("upui", upui);
      buildURN.put(ConstantDigitalLinkTranslatorInfo.SERIAL, serial);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the UPUI identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    UPUI_VALIDATOR.validate(asURN);

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of UPUI Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;

    try {
      final String upui =
          dlURI.substring(
              dlURI.indexOf(UPUI_AI_URI_PREFIX) + UPUI_AI_URI_PREFIX.length(),
              dlURI.indexOf(UPUI_AI_URI_SERIAL_PREFIX));
      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, upui, UPUI_AI_URI_PREFIX);

      // Call the Validator class for the UPUI to check the DLURI syntax
      UPUI_VALIDATOR.validate(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, upui);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of UPUI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
