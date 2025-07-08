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
import io.openepcis.identifiers.validator.core.epcis.compliant.ITIPValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static io.openepcis.constants.ApplicationIdentifierConstants.*;
import static io.openepcis.constants.EPCIS.GS1_IDENTIFIER_DOMAIN;

public class ITIPConverter implements Converter {

  private static final ITIPValidator ITIP_VALIDATOR = new ITIPValidator();
  private boolean isClassLevel;

  public ITIPConverter() {
    super();
  }

  public ITIPConverter(final boolean isClassLevel) {
    this.isClassLevel = isClassLevel;
  }

  // Check if the provided URN is of ITIP type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(ITIP_AI_URN_PREFIX);
  }

  // Check if the provided Digital Link URI is of ITIP Type
  public boolean supportsURN(final String dlURI) {
    return Pattern.compile("(?=.*/8006/)").matcher(dlURI).find();
  }

  // Convert the provided URN to respective Digital Link URI of ITIP type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Call the Validator class for the ITIP to check the URN syntax
      ITIP_VALIDATOR.validate(urn);

      // If the URN passed the validation then convert the URN to URI
      String itip =
          urn.charAt(urn.indexOf('.') + 1)
              + urn.substring(
                  urn.indexOf(ITIP_AI_URN_PREFIX) + ITIP_AI_URN_PREFIX.length(),
                  StringUtils.ordinalIndexOf(urn, ".", 1));
      itip =
          itip
              + urn.substring(
                  StringUtils.ordinalIndexOf(urn, ".", 1) + 2,
                  StringUtils.ordinalIndexOf(urn, ".", 2));
      itip =
          itip
              + urn.substring(
                  StringUtils.ordinalIndexOf(urn, ".", 2) + 1,
                  StringUtils.ordinalIndexOf(urn, ".", 3));
      itip =
          itip
              + urn.substring(
                  StringUtils.ordinalIndexOf(urn, ".", 3) + 1,
                  StringUtils.ordinalIndexOf(urn, ".", 4));
      itip =
          itip.substring(0, 13)
              + ConverterUtil.checksum(itip.substring(0, 13))
              + itip.substring(13);

      if (isClassLevel) {
        return GS1_IDENTIFIER_DOMAIN + ITIP_AI_URI_PREFIX + itip;
      } else {
        final String serialNumber = urn.substring(StringUtils.ordinalIndexOf(urn, ".", 4) + 1);
        return GS1_IDENTIFIER_DOMAIN + ITIP_AI_URI_PREFIX + itip + ITIP_AI_URI_SERIAL_PREFIX + serialNumber;
      }
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of ITIP identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of ITIP Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Call the Validator class for the ITIP to check the DLURI syntax
      ITIP_VALIDATOR.validate(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      String itip;
      if (isClassLevel) {
        itip = dlURI.substring(dlURI.indexOf(ITIP_AI_URI_PREFIX) + ITIP_AI_URI_PREFIX.length());
      } else {
        itip =
            dlURI.substring(
                dlURI.indexOf(ITIP_AI_URI_PREFIX) + ITIP_AI_URI_PREFIX.length(),
                dlURI.indexOf(ITIP_AI_URI_SERIAL_PREFIX));
      }
      return getEPCMap(dlURI, gcpLength, itip);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of ITIP identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(String dlURI, int gcpLength, String itip) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;

    try {
      if (isClassLevel) {
        asURN = "urn:epc:idpat:itip:" + itip.substring(1, gcpLength + 1) + "." + itip.charAt(0);
        asURN =
            asURN
                + itip.substring(gcpLength + 1, 13)
                + "."
                + itip.substring(14, 16)
                + "."
                + itip.substring(16, 18)
                + ".*";
      } else {
        asURN = "urn:epc:id:itip:" + itip.substring(1, gcpLength + 1) + "." + itip.charAt(0);
        final String serial =
            dlURI.substring(dlURI.indexOf(ITIP_AI_URI_SERIAL_PREFIX) + ITIP_AI_URI_SERIAL_PREFIX.length());
        asURN =
            asURN
                + itip.substring(gcpLength + 1, 13)
                + "."
                + itip.substring(14, 16)
                + "."
                + itip.substring(16, 18)
                + "."
                + serial;
        buildURN.put(ConstantDigitalLinkTranslatorInfo.SERIAL, serial);
      }

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(ITIP_AI_URI_PREFIX)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_CAPTURED, dlURI);
      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_URN, asURN);
      buildURN.put("itip", itip);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the ITIP identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    ITIP_VALIDATOR.validate(asURN);

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of ITIP Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;
    try {
      String itip;

      if (isClassLevel) {
        itip = dlURI.substring(dlURI.indexOf(ITIP_AI_URI_PREFIX) + ITIP_AI_URI_PREFIX.length());
      } else {
        itip =
            dlURI.substring(
                dlURI.indexOf(ITIP_AI_URI_PREFIX) + ITIP_AI_URI_PREFIX.length(),
                dlURI.indexOf(ITIP_AI_URI_SERIAL_PREFIX));
      }

      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, itip, ITIP_AI_URI_PREFIX);

      // Call the Validator class for the ITIP to check the DLURI syntax
      ITIP_VALIDATOR.validate(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, itip);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of ITIP identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
