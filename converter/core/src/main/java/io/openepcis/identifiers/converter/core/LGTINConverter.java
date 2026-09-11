/*
 * Copyright (c) 2022-2026 benelog GmbH & Co. KG
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
import io.openepcis.identifiers.converter.util.DigitalLinkQualifiers;
import io.openepcis.identifiers.validator.core.epcis.compliant.LGTINValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.regex.Pattern;

import static io.openepcis.constants.ApplicationIdentifierConstants.*;
import static io.openepcis.constants.EPCIS.GS1_IDENTIFIER_DOMAIN;

public class LGTINConverter implements Converter {
  private static final LGTINValidator LGTIN_VALIDATOR = new LGTINValidator();

  // Check if the provided URN is of LGTIN type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(LGTIN_AI_URN_PREFIX);
  }

  // Check if the provided Digital Link URI is of LGTIN Type
  public boolean supportsURN(final String dlURI) {
    return Pattern.compile("(?=.*/01/)(?=.*/10/)").matcher(dlURI).find();
  }

  // Convert the provided URN to respective Digital Link URI of LGTIN type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Call the Validator class for the LGTIN to check the URN syntax
      LGTIN_VALIDATOR.validate(urn);

      // If the URN passed the validation then convert the URN to URI
      final String gcp =
          urn.charAt(urn.indexOf('.') + 1)
              + urn.substring(
                  urn.indexOf(LGTIN_AI_URN_PREFIX) + LGTIN_AI_URN_PREFIX.length(), urn.indexOf('.'));
      String lgtin =
          gcp + urn.substring(urn.indexOf('.') + 2, urn.indexOf(".", urn.indexOf(".") + 1));
      lgtin = lgtin.substring(0, 13) + ConverterUtil.checksum(lgtin.substring(0, 13));
      final String serialNumber = urn.substring(urn.indexOf(".", urn.indexOf(".") + 1) + 1);
      return GS1_IDENTIFIER_DOMAIN + LGTIN_AI_URI_PREFIX + lgtin + LGTIN_AI_BATCH_LOT_PREFIX + serialNumber;
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of LGTIN identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  /**
   * The Digital Link as the LGTIN rules read it: a serial segment ({@code /21/{ser}})
   * is cut out first. At CLASS level the URI names the lot; the serial belongs to one
   * instance of that lot and has no place in an LGTIN. The plain form is what the
   * validator and the parsing were written for — before this, the lot value was read
   * up to the END of the URI and swallowed {@code /21/{ser}} into the lot. The captured
   * URI, the lot and the dropped serial are put back into the result by
   * {@link #withCaptured}.
   */
  private static String normalized(final String dlURI) {
    // The consumer product variant (/22/) never reaches an EPC either: an LGTIN is
    // GTIN + lot, the variant is the lot's attribute.
    return DigitalLinkQualifiers.withoutSegment(
        DigitalLinkQualifiers.withoutSegment(dlURI, CPV_AI_URI_PREFIX), SGTIN_AI_URI_SERIAL_PREFIX);
  }

  /** Report the URI as captured, the lot under its own key, a dropped serial and the variant. */
  private static Map<String, String> withCaptured(final Map<String, String> result, final String dlURI) {
    result.put(ConstantDigitalLinkTranslatorInfo.AS_CAPTURED, dlURI);
    result.put(ConstantDigitalLinkTranslatorInfo.LOT, result.get(ConstantDigitalLinkTranslatorInfo.SERIAL));
    final String serial = DigitalLinkQualifiers.segmentValue(dlURI, SGTIN_AI_URI_SERIAL_PREFIX);
    if (serial != null) {
      result.put(ConstantDigitalLinkTranslatorInfo.SERIAL_NUMBER, serial);
    }
    final String cpv = DigitalLinkQualifiers.segmentValue(dlURI, CPV_AI_URI_PREFIX);
    if (cpv != null) {
      result.put(ConstantDigitalLinkTranslatorInfo.CPV, cpv);
    }
    return result;
  }

  // Convert the provided Digital Link URI to respective URN of LGTIN Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      final String plain = normalized(dlURI);
      // Call the Validator class for the LGTIN to check the DLURI syntax
      LGTIN_VALIDATOR.validate(plain, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      final String lgtin =
          plain.substring(
              plain.indexOf(LGTIN_AI_URI_PREFIX) + LGTIN_AI_URI_PREFIX.length(),
              plain.indexOf(LGTIN_AI_BATCH_LOT_PREFIX));
      return withCaptured(getEPCMap(plain, gcpLength, lgtin), dlURI);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of LGTIN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(String dlURI, int gcpLength, String lgtin) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;

    try {
      final String serial =
          dlURI.substring(dlURI.indexOf(LGTIN_AI_BATCH_LOT_PREFIX) + LGTIN_AI_BATCH_LOT_PREFIX.length());
      asURN =
          "urn:epc:class:lgtin:"
              + lgtin.substring(1, gcpLength + 1)
              + "."
              + lgtin.charAt(0)
              + lgtin.substring(gcpLength + 1, lgtin.length() - 1)
              + "."
              + serial;

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(LGTIN_AI_URI_PREFIX)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(ConstantDigitalLinkTranslatorInfo.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_CAPTURED, dlURI);
      buildURN.put(ConstantDigitalLinkTranslatorInfo.AS_URN, asURN);
      buildURN.put("lgtin", lgtin);
      buildURN.put(ConstantDigitalLinkTranslatorInfo.SERIAL, serial);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the LGTIN identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    LGTIN_VALIDATOR.validate(asURN);

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of LGTIN Type
  public Map<String, String> convertToURN(String dlURI) throws ValidationException {
    int gcpLength = 0;
    try {
      final String plain = normalized(dlURI);
      final String lgtin =
          plain.substring(
              plain.indexOf(LGTIN_AI_URI_PREFIX) + LGTIN_AI_URI_PREFIX.length(),
              plain.indexOf(LGTIN_AI_BATCH_LOT_PREFIX));
      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(plain, lgtin, LGTIN_AI_URI_PREFIX);

      // Call the Validator class for the LGTIN to check the DLURI syntax
      LGTIN_VALIDATOR.validate(plain, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      return withCaptured(getEPCMap(plain, gcpLength, lgtin), dlURI);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of LGTIN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + ConstantDigitalLinkTranslatorInfo.GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  @Override
  public CompletionStage<Map<String, String>> convertToURNAsync(final String dlURI) {
    final String plain = normalized(dlURI);
    final String lgtin = plain.substring(
        plain.indexOf(LGTIN_AI_URI_PREFIX) + LGTIN_AI_URI_PREFIX.length(),
        plain.indexOf(LGTIN_AI_BATCH_LOT_PREFIX));

    return DefaultGCPLengthProvider.getInstance()
        .getGcpLengthAsync(plain, lgtin, LGTIN_AI_URI_PREFIX)
        .thenApply(gcpLength -> {
          LGTIN_VALIDATOR.validate(plain, gcpLength);
          return withCaptured(getEPCMap(plain, gcpLength, lgtin), dlURI);
        });
  }
}
