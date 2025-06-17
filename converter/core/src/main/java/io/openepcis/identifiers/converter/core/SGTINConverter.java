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
import io.openepcis.identifiers.validator.core.epcis.compliant.SGTINValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static io.openepcis.constants.ApplicationIdentifierConstants.*;
import static io.openepcis.constants.EPCIS.GS1_IDENTIFIER_DOMAIN;

public class SGTINConverter implements Converter {
  private static final SGTINValidator SGTIN_VALIDATOR = new SGTINValidator();
  private boolean isClassLevel;

  public SGTINConverter() {
    super();
  }

  public SGTINConverter(boolean isClassLevel) {
    this.isClassLevel = isClassLevel;
  }

  // Check if the provided URN is SGTIN
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(SGTIN_AI_URN_PREFIX);
  }

  // Check if the provided DL URI is SGTIN
  public boolean supportsURN(final String dlURI) {
    if (isClassLevel) {
      return Pattern.compile("(?=.*/01/)(?!.*/10/)").matcher(dlURI).find();
    } else {
      return Pattern.compile("(?=.*/01/)(?=.*/21/)").matcher(dlURI).find();
    }
  }

  // Convert to SGTIN Digital Link URI
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Validate the URN to check if they match the SGTIN syntax
      SGTIN_VALIDATOR.validate(urn);

      final String gcp =
          urn.charAt(urn.indexOf('.') + 1)
              + urn.substring(
                  urn.indexOf(SGTIN_AI_URN_PREFIX) + SGTIN_AI_URN_PREFIX.length(), urn.indexOf('.'));
      String sgtin =
          gcp + urn.substring(urn.indexOf('.') + 2, urn.indexOf(".", urn.indexOf(".") + 1));
      sgtin = sgtin.substring(0, 13) + ConverterUtil.checksum(sgtin.substring(0, 13));

      if (isClassLevel) {
        sgtin = GS1_IDENTIFIER_DOMAIN + SGTIN_AI_URI_PREFIX + sgtin;
      } else {
        final String serialNumber = urn.substring(urn.indexOf(".", urn.indexOf(".") + 1) + 1);
        sgtin =
            GS1_IDENTIFIER_DOMAIN + SGTIN_AI_URI_PREFIX + sgtin + SGTIN_AI_URI_SERIAL_PREFIX + serialNumber;
      }
      return sgtin;
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SGTIN identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert to SGTIN URN
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      String sgtin;

      // Validate the URN to check if they match the SGTIN syntax
      if (isClassLevel) {
        sgtin = dlURI.substring(dlURI.indexOf(SGTIN_AI_URI_PREFIX) + SGTIN_AI_URI_PREFIX.length());
        SGTIN_VALIDATOR.validate(dlURI, gcpLength);
      } else {
        sgtin =
            dlURI.substring(
                dlURI.indexOf(SGTIN_AI_URI_PREFIX) + SGTIN_AI_URI_PREFIX.length(),
                dlURI.indexOf(SGTIN_AI_URI_SERIAL_PREFIX));
        SGTIN_VALIDATOR.validate(dlURI, gcpLength);
      }

      return getEPCMap(dlURI, gcpLength, sgtin);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SGTIN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(
      final String dlURI, final int gcpLength, final String sgtin) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;

    try {
      final String sgtinUrn =
          sgtin.substring(1, gcpLength + 1)
              + "."
              + sgtin.charAt(0)
              + sgtin.substring(gcpLength + 1, sgtin.length() - 1);

      if (isClassLevel) {
        asURN = "urn:epc:idpat:sgtin:" + sgtinUrn + ".*";
      } else {
        final String serial =
            dlURI.substring(dlURI.indexOf(SGTIN_AI_URI_SERIAL_PREFIX) + SGTIN_AI_URI_SERIAL_PREFIX.length());
        asURN = "urn:epc:id:sgtin:" + sgtinUrn + "." + serial;
        buildURN.put(ConstantDigitalLinkTranslatorInfo.SERIAL, serial);
      }

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(SGTIN_AI_URI_PREFIX)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_CAPTURED, dlURI);
      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_URN, asURN);
      buildURN.put("gtin", sgtin);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the SGTIN identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    SGTIN_VALIDATOR.validate(asURN);

    return buildURN;
  }

  // Convert to SGTIN URN
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;

    try {
      String sgtin;
      if (isClassLevel) {
        sgtin = dlURI.substring(dlURI.indexOf(SGTIN_AI_URI_PREFIX) + SGTIN_AI_URI_PREFIX.length());
      } else {
        sgtin =
            dlURI.substring(
                dlURI.indexOf(SGTIN_AI_URI_PREFIX) + SGTIN_AI_URI_PREFIX.length(),
                dlURI.indexOf(SGTIN_AI_URI_SERIAL_PREFIX));
      }

      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, sgtin, SGTIN_AI_URI_PREFIX);

      // Validate the URN to check if they match the SGTIN syntax
      SGTIN_VALIDATOR.validate(dlURI, gcpLength);

      return getEPCMap(dlURI, gcpLength, sgtin);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of SGTIN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
