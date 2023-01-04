/*
 * Copyright 2022 benelog GmbH & Co. KG
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package io.openepcis.epc.translator.validation;

import io.openepcis.epc.translator.exception.ValidationException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class SGTINValidator implements PatternValidator {

  private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URN_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
  private static final String SGTIN_URN_PART = ":sgtin:";
  private static final String SGTIN_URI_PART = "/01/";

  static {
    // Fill the URN pattern and messages
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:sgtin:.*",
            "Invalid SGTIN, SGTIN should start with \"urn:epc:id:sgtin:\" (Ex: urn:epc:id:sgtin:234567890.1123.9999),%nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:sgtin:[0-9]{6,12}.*",
            "Invalid SGTIN, SGTIN should consist of GCP with 6-12 digits (Ex: urn:epc:id:sgtin:234567890.1123.9999).%nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:sgtin:[0-9]{6,12}\\.[0-9]{1,7}.*",
            "Invalid SGTIN, SGTIN should be of 14 digits with GCP of 6-12 digits (Ex: urn:epc:id:sgtin:234567890.1123.9999).%nPlease check the provided URN: %s") {
          @Override
          public void validate(final String urn) throws ValidationException {
            super.validate(urn);

            // Check if the SGTIN has 14 digits
            final String gcp =
                urn.substring(
                    urn.indexOf(SGTIN_URN_PART) + SGTIN_URN_PART.length(),
                    StringUtils.ordinalIndexOf(urn, ".", 1));
            String sgtin;

            if (StringUtils.countMatches(urn, ".") >= 2) {
              sgtin =
                  gcp
                      + urn.substring(
                          StringUtils.ordinalIndexOf(urn, ".", 1) + 1,
                          StringUtils.ordinalIndexOf(urn, ".", 2));
            } else {
              throw new ValidationException(
                  String.format(
                      "Invalid SGTIN, SGTIN should be followed by serial numbers (Ex: urn:epc:id:sgtin:234567890.1123.9999).%nPlease check the provided URN: %s",
                      urn));
            }

            if (sgtin.length() != 13) {
              throw new ValidationException(
                  String.format(
                      "Invalid SGTIN, SGTIN values should be of 14 digits (Ex: urn:epc:id:sgtin:234567890.1123.9999).%nPlease check the provided URN: %s",
                      urn));
            }
          }
        });
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:sgtin:[0-9]{6,12}\\.[0-9]{1,7}\\.[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,20}",
            "Invalid SGTIN, SGTIN should consist of serial numbers(Ex: urn:epc:id:sgtin:234567.1890123.0000).%nPlease check the provided URN: %s"));

    // Fill the URN pattern and messages for class-level
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:sgtin:.*",
            "Invalid GTIN, Class level GTIN should start with \"urn:epc:id:sgtin:\" (Ex: urn:epc:idpat:sgtin:234567890.1123.*),%nPlease check the provided URN: %s"));
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:sgtin:[0-9]{6,12}.*",
            "Invalid GTIN, Class level GTIN should consist of GCP with 6-12 digits (Ex: urn:epc:idpat:sgtin:234567890.1123.*).%nPlease check the provided URN: %s"));
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:sgtin:[0-9]{6,12}\\.[0-9]{1,7}\\.\\*",
            "Invalid GTIN, Class level GTIN should be of 14 digits with GCP of 6-12 digits (Ex: urn:epc:idpat:sgtin:234567890.1123.*).%nPlease check the provided URN: %s") {
          @Override
          public void validate(final String urn) throws ValidationException {
            super.validate(urn);

            // Check if the SGTIN has 14 digits
            final String gcp =
                urn.substring(
                    urn.indexOf(SGTIN_URN_PART) + SGTIN_URN_PART.length(),
                    StringUtils.ordinalIndexOf(urn, ".", 1));
            String sgtin;

            if (StringUtils.countMatches(urn, ".") >= 2) {
              sgtin =
                  gcp
                      + urn.substring(
                          StringUtils.ordinalIndexOf(urn, ".", 1) + 1,
                          StringUtils.ordinalIndexOf(urn, ".", 2));
            } else {
              throw new ValidationException(
                  String.format(
                      "Invalid SGTIN, SGTIN should be followed by serial numbers (Ex: urn:epc:id:sgtin:234567890.1123.9999).%nPlease check the provided URN: %s",
                      urn));
            }

            if (sgtin.length() != 13) {
              throw new ValidationException(
                  String.format(
                      "Invalid SGTIN, SGTIN values should be of 14 digits (Ex: urn:epc:id:sgtin:234567890.1123.9999).%nPlease check the provided URN: %s",
                      urn));
            }
          }
        });

    // Fill the DLURI pattern and messages
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid SGTIN, DL URI should start with Domain name (Ex: https://id.gs1.org/),%nPlease check the DL URI : %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./01/[0-9]{14}.*",
            "Invalid SGTIN, DL URI should consist of 14 digit SGTIN (Ex: https://id.gs1.org/01/12345678901234/21/9999),%nPlease check the DL URI : %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./01/[0-9]{14}/21/[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,20}",
            "Invalid SGTIN, DL URI should consist of 14 digit SGTIN followed by Serial numbers (Ex: https://id.gs1.org/01/12345678901234/21/9999),%nPlease check the DL URI : %s") {
          @Override
          protected void validate(String uri, int gcpLength) throws ValidationException {
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

    // Fill the DLURI pattern and messages for class-level
    URI_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid GTIN, DL URI should start with Domain name (Ex: https://id.gs1.org/),%nPlease check the DL URI : %s"));
    URI_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./01/[0-9]{14}.*",
            "Invalid GTIN, DL URI should consist of 14 digits (Ex: https://id.gs1.org/01/12345678901234),%nPlease check the DL URI : %s") {
          @Override
          protected void validate(final String uri, final int gcpLength)
              throws ValidationException {
            super.validate(uri, gcpLength);

            // Check if the GTIN is of 14 digits
            if (uri.substring(uri.indexOf(SGTIN_URI_PART) + SGTIN_URI_PART.length()).length()
                != 14) {
              throw new ValidationException(
                  String.format(
                      "Invalid GTIN, DL URI should consist of 14 digits (Ex: https://id.gs1.org/01/12345678901234). %nPlease check the provided URI : %s",
                      uri));
            }

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

  // Public Method to validate the SGTIN URN
  public final void validateURN(final String urn) throws ValidationException {
    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the SGTIN DL URI
  public final void validateURI(final String dlURI, final int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }

  // Public Method to validate the SGTIN URN
  public final void validateClassLevelURN(final String urn) throws ValidationException {
    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_WITHOUT_SERIAL_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the SGTIN DL URI
  public final void validateClassLevelURI(final String dlURI, final int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_WITHOUT_SERIAL_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }
}
