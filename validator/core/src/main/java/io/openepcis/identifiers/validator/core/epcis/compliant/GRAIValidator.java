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
public class GRAIValidator implements ApplicationIdentifierValidator {

  private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URN_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();

  static {
    // GRAI Instance EPC URN identifier validation rules
    URN_MATCHERS.add(
        new Matcher(
            "(urn:epc:id:grai:).*",
            "Invalid GRAI, GRAI should start with \"urn:epc:id:grai:\" (Ex: urn:epc:id:grai:1234567890.12.1ABC), Please check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:grai:[0-9]{6,12}.*",
            "Invalid GRAI, GRAI should consist of GCP with 6-12 digits (Ex: urn:epc:id:grai:1234567890.12.1ABC), Please check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:grai:[0-9]{6,12}\\.[0-9]{0,6}.*",
            "Invalid GRAI, GRAI with Serial must be 14 digits followed by 1 to 16 alphanumeric characters (Ex: urn:epc:id:grai:1234567890.12.1ABC), Please check the provided URN: %s") {

          @Override
          public void validate(final String urn) throws ValidationException {
            super.validate(urn);
            final String gcp =
                urn.substring(
                    urn.indexOf(GRAI_AI_URN_PREFIX) + GRAI_AI_URN_PREFIX.length(), urn.indexOf('.'));
            String grai;

            if (urn.indexOf(".", urn.indexOf(".") + 1) == -1) {
              grai = gcp + urn.substring(urn.indexOf('.') + 1);
            } else {
              grai =
                  gcp + urn.substring(urn.indexOf('.') + 1, urn.indexOf(".", urn.indexOf(".") + 1));
            }

            if (grai.length() != 12) {
              throw new ValidationException(
                  String.format(
                      "Invalid GRAI Length, GRAI with Serial must be 14 digits followed by 1 to 16 alphanumeric characters (Ex: urn:epc:id:grai:1234567890.12.1ABC), Please check the provided URN: %s",
                      urn));
            }
          }
        });
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:grai:[0-9]{6,12}\\.[0-9]{0,6}\\.[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,16}",
            "Invalid GRAI, GRAI with Serial must be 14 digits followed by 1 to 16 alphanumeric characters (Ex: urn:epc:id:grai:1234567890.12.1ABC), Please check the provided URN: %s"));

    // GRAI Class URN identifier validation rules
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "(urn:epc:idpat:grai:).*",
            "Invalid GRAI, Class level GRAI should start with \"urn:epc:idpat:grai:\" (Ex: urn:epc:idpat:grai:1234567890.12.*), Please check the provided URN: %s"));
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:grai:[0-9]{6,12}.*",
            "Invalid GRAI, Class level GRAI should consist of GCP with 6-12 digits (Ex: urn:epc:idpat:grai:1234567890.12.*), Please check the provided URN: %s"));
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:grai:[0-9]{6,12}\\.[0-9]{0,6}\\.\\*",
            "Invalid GRAI,\n The first 14 characters of the GRAI with Serial must be digits (Ex: urn:epc:idpat:grai:1234567890.12.*). Please check the provided URN: %s") {

          @Override
          public void validate(final String urn) throws ValidationException {
            super.validate(urn);
            final String gcp = urn.substring(urn.lastIndexOf(":") + 1, urn.indexOf('.'));
            String grai;

            if (urn.indexOf(".", urn.indexOf(".") + 1) == -1) {
              grai = gcp + urn.substring(urn.indexOf('.') + 1);
            } else {
              grai =
                  gcp + urn.substring(urn.indexOf('.') + 1, urn.indexOf(".", urn.indexOf(".") + 1));
            }
            if (grai.length() != 12) {
              throw new ValidationException(
                  String.format(
                      "Invalid GRAI Length, GRAI with Serial must be 14 digits followed by 1 to 16 alphanumeric characters (Ex: urn:epc:idpat:grai:1234567890.12.*), Please check the provided URN: %s",
                      urn));
            }
          }
        });

    // GRAI Instance EPC Digital Link URI identifier validation rules
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid GRAI, GRAI should start with Domain name (Ex: https://id.gs1.org/), Please check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*/8003/[0-9]{13}[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,16}$",
            "Invalid GRAI, GRAI with Serial must be 14 digits followed by 1 to 16 alphanumeric characters (Ex: https://id.gs1.org/8003/0123456789012123ABCD), Please check the URI: %s") {
          @Override
          public void validate(final String uri, final int gcpLength)
              throws ValidationException {
            super.validate(uri, gcpLength);

            // Check if the GCP Length matches
            if (!(gcpLength >= 6 && gcpLength <= 12)) {
              throw new ValidationException(
                  String.format(
                      "Invalid GCP Length, GCP Length should be between 6-12 digits. Please check the provided GCP Length: %s",
                      gcpLength));
            }
          }
        });

    // GRAI Class Digital Link URI identifier validation rules
    URI_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid GRAI, GRAI should start with Domain name (Ex: https://id.gs1.org/), Please check the URI: %s"));
    URI_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "(http|https)://.*/8003/[0-9]{13}",
            "Invalid GRAI, GRAI must be 13 digits (Ex: https://id.gs1.org/8003/9524321890009), Please ®check the URI: %s") {
          @Override
          public void validate(final String uri, final int gcpLength)
              throws ValidationException {
            super.validate(uri, gcpLength);

            // Check if the GCP Length matches
            if (!(gcpLength >= 6 && gcpLength <= 12)) {
              throw new ValidationException(
                  String.format(
                      "Invalid GCP Length, GCP Length should be between 6-12 digits. Please check the provided GCP Length: %s",
                      gcpLength));
            }
          }
        });
  }

  @Override
  public boolean supportsValidation(final String identifier) {
    // For URN identifier check if identifier contains URN part: ":grai:"
    if (identifier.contains(GRAI_AI_URN_PREFIX)) {
      return true;
    }

    // For URI identifier check if identifier contains DL URI part: "/8003/"
    return identifier.contains(GRAI_AI_URI_PREFIX);
  }

  @Override
  public boolean supportsValidation(final String identifier, final boolean isEpcisCompliant) {
    // As its already EPCIS compliant, return the result of the supportsValidation method
    return supportsValidation(identifier);
  }

  /** Validate without gcpLength. This method is intended for URN validations. */
  @Override
  public boolean validate(final String identifier, final Integer... gcpLength)
      throws ValidationException {
    // Determine identifier type directly from the provided identifier
    boolean isUrn = identifier.contains(GRAI_AI_URN_PREFIX);

    // For URNs, class-level is determined by checking for CLASS_URN_PART.
    // For Digital Link URIs, extract the numeric segment after "/8003/" and if its length is
    // exactly 13, it's class-level.
    boolean isClassLevel;
    if (isUrn) {
      isClassLevel = identifier.contains(CLASS_URN_PREFIX);
    } else {
      final int idx = identifier.indexOf(GRAI_AI_URI_PREFIX);

      // Extract the segment after "/8003/" and check if it's exactly 13 characters long.
      final String value = identifier.substring(idx + GRAI_AI_URI_PREFIX.length());

      // If the extracted numeric part is exactly 13 digits, it is class-level.
      isClassLevel = (value.length() == 13);
    }

    // Select the correct matcher list.
    List<Matcher> matchers;

    if (isUrn) {
      // Choose the appropriate URN matchers based on whether it's a class-level URN.
      matchers = isClassLevel ? URN_WITHOUT_SERIAL_MATCHERS : URN_MATCHERS;
    } else {
      // For Digital Link URIs, ensure a valid GCP length is provided.
      if (gcpLength == null || gcpLength.length == 0 || gcpLength[0] == null) {
        throw new ValidationException(
            "Digital Link URI detected. Use validate(String, int) to validate Digital Link URIs with a GCP length.");
      }
      matchers = isClassLevel ? URI_WITHOUT_SERIAL_MATCHERS : URI_MATCHERS;
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
