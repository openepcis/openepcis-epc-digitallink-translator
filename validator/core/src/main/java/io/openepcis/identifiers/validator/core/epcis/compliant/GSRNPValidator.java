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

import static io.openepcis.constants.ApplicationIdentifierConstants.GSRNP_AI_URI_PREFIX;
import static io.openepcis.constants.ApplicationIdentifierConstants.GSRNP_AI_URN_PREFIX;
public class GSRNPValidator implements ApplicationIdentifierValidator {

  private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_MATCHERS = new ArrayList<>();

  static {
    // GSRN EPC URN identifier validation rules
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:gsrnp:.*",
            "Invalid GSRNP, GSRNP should start with \"urn:epc:id:gsrnp:\" (Ex: urn:epc:id:gsrnp:123456.78901234567). Please check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:gsrnp:[0-9]{6,12}\\..*",
            "Invalid GSRNP, GSRNP should consist of GCP with 6-12 digits (Ex: urn:epc:id:gsrnp:123456.78901234567). Please check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:gsrnp:[0-9]{6,12}\\.[0-9]{5,11}",
            "Invalid GSRNP, GSRNP should be of 18 digits (Ex: urn:epc:id:gsrnp:123456.78901234567). Please check the provided URN: %s") {
          @Override
          public void validate(final String urn) throws ValidationException {
            super.validate(urn);
            final String gcp = urn.substring(urn.lastIndexOf(":") + 1, urn.indexOf('.'));
            final String gsrnp = gcp + urn.substring(urn.indexOf('.') + 1);

            if (gsrnp.length() != 17) {
              throw new ValidationException(
                  String.format(
                      "Invalid GSRNP, GSRNP should be of 18 digits (Ex: urn:epc:id:gsrnp:123456.78901234567). Please check the provided URN: %s",
                      urn));
            }
          }
        });

    // GSRN EPC Digital Link URI identifier validation rules
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid GSRNP, GSRNP URI should start with Domain name (Ex: https://id.gs1.org/8017/123456789091429723), Please check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./8017/[0-9]{18}",
            "Invalid GSRNP, GSRNP URI should consist of 18 digit GSRNP (Ex: https://id.gs1.org/8017/123456789091429723), Please check the URI: %s") {
          @Override
          public void validate(final String uri, final int gcpLength)
              throws ValidationException {
            super.validate(uri, gcpLength);

            if (!(gcpLength >= 6 && gcpLength <= 12)) {
              throw new ValidationException(
                  String.format(
                      "Invalid GCP Length, GCP Length must be between 6 and 12 digits. Please check the GCP Length: %s",
                      gcpLength));
            }
          }
        });
  }

  @Override
  public boolean supportsValidation(final String identifier) {
    // For URN identifier check if identifier contains the specific :gsrnp: URN part
    if (identifier.contains(GSRNP_AI_URN_PREFIX)) {
      return true;
    }

    // For URI identifier check if identifier contains "/8017/"
    return identifier.contains(GSRNP_AI_URI_PREFIX);
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
    boolean isUrn = identifier.contains(GSRNP_AI_URN_PREFIX);

    // Select the correct matcher list based on identifier type and level.
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
