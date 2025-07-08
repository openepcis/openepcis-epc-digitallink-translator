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
import io.openepcis.identifiers.validator.core.epcis.compliant.CPIValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static io.openepcis.constants.ApplicationIdentifierConstants.*;
import static io.openepcis.constants.EPCIS.GS1_IDENTIFIER_DOMAIN;

public class CPIConverter implements Converter {

  private static final CPIValidator CPI_VALIDATOR = new CPIValidator();
  private boolean isClassLevel;

  public CPIConverter() {
    super();
  }

  public CPIConverter(final boolean isClassLevel) {
    this.isClassLevel = isClassLevel;
  }

  // Check if the provided URN is of CPI type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(CPI_AI_URN_PREFIX);
  }

  // Check if the provided Digital Link URI is of CPI Type
  public boolean supportsURN(final String dlURI) {
    if (isClassLevel) return Pattern.compile("(?=.*/8010/)").matcher(dlURI).find();
    else return Pattern.compile("(?=.*/8010/)(?=.*/8011/)").matcher(dlURI).find();
  }

  // Convert the provided URN to respective Digital Link URI of CPI type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Validate the URN to check if they match the CPI syntax
      CPI_VALIDATOR.validate(urn);

      // If the URN passed the validation then convert the URN to URI
      final String gcp =
          urn.substring(urn.indexOf(CPI_AI_URN_PREFIX) + CPI_AI_URN_PREFIX.length(), urn.indexOf("."));
      final String cpi =
          gcp + urn.substring(urn.indexOf(".") + 1, urn.indexOf(".", urn.indexOf(".") + 1));
      if (isClassLevel) {
        return GS1_IDENTIFIER_DOMAIN + CPI_AI_URI_PREFIX + cpi;
      } else {
        final String serialNumber = urn.substring(urn.lastIndexOf(".") + 1);
        return GS1_IDENTIFIER_DOMAIN + CPI_AI_URI_PREFIX + cpi + CPI_AI_URI_SERIAL_PREFIX + serialNumber;
      }
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of CPI identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of CPI Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {

    try {
      // Validate the DLURI to check if they match the CPI syntax
      CPI_VALIDATOR.validate(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      String cpi;
      if (isClassLevel) {
        cpi = dlURI.substring(dlURI.indexOf(CPI_AI_URI_PREFIX) + CPI_AI_URI_PREFIX.length());
      } else {
        cpi =
            dlURI.substring(
                dlURI.indexOf(CPI_AI_URI_PREFIX) + CPI_AI_URI_PREFIX.length(),
                dlURI.indexOf(CPI_AI_URI_SERIAL_PREFIX));
      }
      return getEPCMap(dlURI, gcpLength, cpi);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of CPI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(final String dlURI, final int gcpLength, final String cpi) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;

    try {
      if (isClassLevel) {
        asURN =
            "urn:epc:idpat:cpi:"
                + cpi.substring(0, gcpLength)
                + "."
                + cpi.substring(gcpLength)
                + ".*";
      } else {
        final String serial =
            dlURI.substring(dlURI.indexOf(CPI_AI_URI_SERIAL_PREFIX) + CPI_AI_URI_SERIAL_PREFIX.length());
        asURN =
            "urn:epc:id:cpi:"
                + cpi.substring(0, gcpLength)
                + "."
                + cpi.substring(gcpLength)
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
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(CPI_AI_URI_PREFIX)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_CAPTURED, dlURI);
      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_URN, asURN);
      buildURN.put("cpi", cpi);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the CPI identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    CPI_VALIDATOR.validate(asURN);

    return buildURN;
  }

  public Map<String, String> convertToURN(final String dlURI) {
    int gcpLength = 0;
    try {
      String cpi;

      if (isClassLevel) {
        cpi = dlURI.substring(dlURI.indexOf(CPI_AI_URI_PREFIX) + CPI_AI_URI_PREFIX.length());
      } else {
        cpi =
            dlURI.substring(
                dlURI.indexOf(CPI_AI_URI_PREFIX) + CPI_AI_URI_PREFIX.length(),
                dlURI.indexOf(CPI_AI_URI_SERIAL_PREFIX));
      }

      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, cpi, CPI_AI_URI_PREFIX);

      // Validate the DLURI to check if they match the CPI syntax
      CPI_VALIDATOR.validate(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, cpi);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of CPI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
