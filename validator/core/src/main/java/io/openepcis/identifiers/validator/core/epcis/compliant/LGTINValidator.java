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
package io.openepcis.identifiers.validator.core.epcis.compliant;

import io.openepcis.identifiers.validator.core.ApplicationIdentifierValidator;
import io.openepcis.identifiers.validator.core.Matcher;
import io.openepcis.identifiers.validator.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

import static io.openepcis.constants.ApplicationIdentifierConstants.*;
public class LGTINValidator implements ApplicationIdentifierValidator {
  private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_MATCHERS = new ArrayList<>();

  static {
    // LGTIN Class URN identifier validation rules
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:class:lgtin:.*",
            "Invalid LGTIN, LGTIN should start with \"urn:epc:class:lgtin:\" (Ex: urn:epc:class:lgtin:234567890.1123.9999). Please check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:class:lgtin:[0-9]{6,12}.*",
            "Invalid LGTIN, LGTIN should consist of GCP with 6-12 digits (Ex: urn:epc:class:lgtin:234567890.1123.9999). Please check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:class:lgtin:[0-9]{6,12}\\.[0-9]{1,7}.*",
            "Invalid LGTIN, LGTIN should be of 14 digits and GCP should match 6-12 digits (Ex: urn:epc:class:lgtin:234567890.1123.9999). Please check the provided URN: %s") {
          @Override
          public void validate(final String urn) throws ValidationException {
            super.validate(urn);

            final String gcp =
                urn.substring(
                    urn.indexOf(LGTIN_AI_URN_PREFIX) + LGTIN_AI_URN_PREFIX.length(), urn.indexOf('.'));
            String lgtin;
            if (urn.indexOf(".", urn.indexOf(".") + 1) == -1) {
              lgtin = gcp + urn.substring(urn.indexOf('.') + 1);
            } else {
              lgtin =
                  gcp + urn.substring(urn.indexOf('.') + 1, urn.indexOf(".", urn.indexOf(".") + 1));
            }

            if (lgtin.length() != 13) {
              throw new ValidationException(
                  String.format(
                      "Invalid LGTIN, LGTIN values should be of 14 digits (Ex: urn:epc:class:lgtin:234567890.1123.9999). Please check the provided URN : %s",
                      urn));
            }
          }
        });
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:class:lgtin:[0-9]{6,12}\\.[0-9]{1,7}\\.[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,20}",
            "Invalid LGTIN, LGTIN should consist of serial numbers(Ex: urn:epc:class:lgtin:234567890.1123.9999). Please check the provided URN : %s"));

    // LGTIN Class Digital Link URI identifier validation rules
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid LGTIN, LGTIN should start with Domain name (Ex: https://id.gs1.org/01/12345678901234/10/1111). Please check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./01/[0-9]{14}.*",
            "Invalid LGTIN, LGTIN should consist of 14 digit LGTIN (Ex: https://id.gs1.org/01/12345678901234/10/1111). Please check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./01/[0-9]{14}/10/[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,20}",
            "Invalid LGTIN, LGTIN should consist of 14 digit LGTIN followed by Serial numbers (Ex: https://id.gs1.org/01/12345678901234/10/1111). Please check the DL URI: %s") {
          @Override
          public void validate(final String uri, final int gcpLength) throws ValidationException {
            super.validate(uri, gcpLength);

            // Check if the GCP Length matches
            if (!(gcpLength >= 6 && gcpLength <= 12)) {
              throw new ValidationException(
                  String.format("Invalid GCP Length, GCP Length should be between 6-12 digits. Please check the provided GCP Length: %s", gcpLength));
            }
          }
        });
  }

  @Override
  public boolean supportsValidation(final String identifier) {
    // For URN identifier check if identifier contains URN part: ":lgtin:"
    if (identifier.contains(LGTIN_AI_URN_PREFIX)) {
      return true;
    }

    // For DL URI identifier check if identifier contains DL URI part: "/01/" and "/10/" but not the SGTIN related parts
    return (identifier.contains(LGTIN_AI_URI_PREFIX) && identifier.contains(LGTIN_AI_BATCH_LOT_PREFIX)) &&
            !(identifier.contains(SGTIN_AI_URI_SERIAL_PREFIX) || identifier.contains(EXPIRY_DATE_AI_PARAM));
  }

  @Override
  public boolean supportsValidation(final String identifier, final boolean isEpcisCompliant) {
    // As its already EPCIS compliant, return the result of the supportsValidation method
    return supportsValidation(identifier);
  }

  @Override
  public boolean validate(final String identifier, final Integer... gcpLength)
      throws ValidationException {
    // Determine identifier type directly from the provided identifier
    boolean isUrn = identifier.contains(LGTIN_AI_URN_PREFIX);

    // Select the correct matcher list.
    List<Matcher> matchers;

    if (isUrn) {
      // Choose the appropriate URN matchers based on whether it's a class-level URN.
      matchers = URN_MATCHERS;
    } else {
      // For Digital Link URIs, ensure a valid GCP length is provided.
      if (gcpLength == null || gcpLength.length == 0 || gcpLength[0] == null) {
        throw new ValidationException(
            "Digital Link URI detected. Use validate(String, int) to validate Digital Link URIs with a GCP length.");
      }
      matchers = URI_MATCHERS;
    }

    // Iterate over the chosen matchers and validate the identifier.
    for (Matcher m : matchers) {
      if (isUrn) {
        m.validate(identifier);
      } else {
        m.validate(identifier, gcpLength[0]);
      }
    }

    return true;
  }

  @Override
  public boolean validate(final String identifier) throws ValidationException {
    return validate(identifier, (Integer) null);
  }

  @Override
  public boolean validate(
      final String identifier, final boolean isEpcisCompliant, final Integer... gcpLength) {
    return validate(identifier, gcpLength);
  }
}
