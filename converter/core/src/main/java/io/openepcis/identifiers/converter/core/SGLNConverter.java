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
import io.openepcis.identifiers.validator.core.epcis.compliant.SGLNValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static io.openepcis.constants.ApplicationIdentifierConstants.*;
import static io.openepcis.constants.EPCIS.GS1_IDENTIFIER_DOMAIN;

public class SGLNConverter implements Converter {

  private static final String SGLN_URN_PREFIX = "urn:epc:id:sgln:";
  private static final SGLNValidator SGLN_VALIDATOR = new SGLNValidator();

  // Check if the provided URN is of SGLN type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(SGLN_AI_URN_PREFIX);
  }

  // Check if the provided Digital Link URI is of SGLN Type
  public boolean supportsURN(final String dlURI) {
    return dlURI.contains(SGLN_AI_URI_PREFIX);
  }

  // Convert the provided URN to respective Digital Link URI of SGLN type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Validate the URN to check if they match the SGLN syntax
      SGLN_VALIDATOR.validate(urn);

      String sgln =
          urn.substring(
              urn.indexOf(SGLN_AI_URN_PREFIX) + SGLN_AI_URN_PREFIX.length(),
              StringUtils.ordinalIndexOf(urn, ".", 1));
      sgln =
          sgln
              + urn.substring(
                  StringUtils.ordinalIndexOf(urn, ".", 1) + 1,
                  StringUtils.ordinalIndexOf(urn, ".", 2));
      sgln = sgln + ConverterUtil.checksum(sgln);
      final String serial = urn.substring(StringUtils.ordinalIndexOf(urn, ".", 2) + 1);

      if (serial.length() == 0) {
        return GS1_IDENTIFIER_DOMAIN + SGLN_AI_URI_PREFIX + sgln;
      } else {
        // Add serial part if not 0
        return GS1_IDENTIFIER_DOMAIN
            + SGLN_AI_URI_PREFIX
            + sgln
            + ((!serial.equals("0")) ? SGLN_AI_URI_SERIAL_PREFIX + serial : "");
      }
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SGLN identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of SGLN Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Validate the URI to check if they match the SGLN syntax
      SGLN_VALIDATOR.validate(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SGLN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(final String dlURI, final int gcpLength) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;

    try {
      String sgln;

      if (dlURI.contains(SGLN_AI_URI_SERIAL_PREFIX)) {
        sgln =
            dlURI.substring(
                dlURI.indexOf(SGLN_AI_URI_PREFIX) + SGLN_AI_URI_PREFIX.length(),
                dlURI.indexOf(SGLN_AI_URI_SERIAL_PREFIX));
        final String serial =
            dlURI.substring(dlURI.indexOf(SGLN_AI_URI_SERIAL_PREFIX) + SGLN_AI_URI_SERIAL_PREFIX.length());
        asURN =
            SGLN_URN_PREFIX
                + sgln.substring(0, gcpLength)
                + "."
                + sgln.substring(gcpLength, sgln.length() - 1)
                + "."
                + serial;
        buildURN.put("serial", serial);
      } else {
        sgln = dlURI.substring(dlURI.indexOf(SGLN_AI_URI_PREFIX) + SGLN_AI_URI_PREFIX.length());
        asURN =
            SGLN_URN_PREFIX
                + sgln.substring(0, gcpLength)
                + "."
                + sgln.substring(gcpLength, sgln.length() - 1)
                + ".0";
      }

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(SGLN_AI_URI_PREFIX)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_CAPTURED, dlURI);
      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_URN, asURN);
      buildURN.put("sgln", sgln);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the SGLN identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // Validate the URN to check if they match the SGLN syntax
    SGLN_VALIDATOR.validate(asURN);

    return buildURN;
  }

  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;
    try {
      String sgln;

      if (dlURI.contains(SGLN_AI_URI_SERIAL_PREFIX)) {
        sgln =
            dlURI.substring(
                dlURI.indexOf(SGLN_AI_URI_PREFIX) + SGLN_AI_URI_PREFIX.length(),
                dlURI.indexOf(SGLN_AI_URI_SERIAL_PREFIX));
      } else {
        sgln = dlURI.substring(dlURI.indexOf(SGLN_AI_URI_PREFIX) + SGLN_AI_URI_PREFIX.length());
      }

      // Find the GCP Length from GS1 provided list
      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, sgln, SGLN_AI_URI_PREFIX);

      // Validate the URI to check if they match the SGLN syntax
      SGLN_VALIDATOR.validate(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SGLN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
