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

public class GRAIValidator implements PatternValidator {

  private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URN_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
  private static final String GRAI_URN_PART = ":grai:";

  static {
    // Populate all the pattern and message for URN
    URN_MATCHERS.add(
        new Matcher(
            "(urn:epc:id:grai:).*",
            "Invalid GRAI, GRAI should start with \"urn:epc:id:grai:\" (Ex: urn:epc:id:grai:1234567890.12.1ABC),\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:grai:[0-9]{6,12}.*",
            "Invalid GRAI, GRAI should consist of GCP with 6-12 digits (Ex: urn:epc:id:grai:1234567890.12.1ABC),\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:grai:[0-9]{6,12}\\.[0-9]{0,6}.*",
            "Invalid GRAI, GRAI with Serial must be 14 digits followed by 1 to 16 alphanumeric characters (Ex: urn:epc:id:grai:1234567890.12.1ABC),\nPlease check the provided URN: %s") {

          @Override
          public void validate(final String urn) throws ValidationException {
            super.validate(urn);
            final String gcp =
                urn.substring(
                    urn.indexOf(GRAI_URN_PART) + GRAI_URN_PART.length(), urn.indexOf('.'));
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
                      "Invalid GRAI Length, GRAI with Serial must be 14 digits followed by 1 to 16 alphanumeric characters (Ex: urn:epc:id:grai:1234567890.12.1ABC),%nPlease check the provided URN: %s",
                      urn));
            }
          }
        });
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:grai:[0-9]{6,12}\\.[0-9]{0,6}\\.[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,16}",
            "Invalid GRAI, GRAI with Serial must be 14 digits followed by 1 to 16 alphanumeric characters (Ex: urn:epc:id:grai:1234567890.12.1ABC),\nPlease check the provided URN: %s"));

    // Populate all the pattern and message for URN for class-level
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "(urn:epc:idpat:grai:).*",
            "Invalid GRAI, Class level GRAI should start with \"urn:epc:idpat:grai:\" (Ex: urn:epc:idpat:grai:1234567890.12.*),\nPlease check the provided URN: %s"));
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:grai:[0-9]{6,12}.*",
            "Invalid GRAI, Class level GRAI should consist of GCP with 6-12 digits (Ex: urn:epc:idpat:grai:1234567890.12.*),\nPlease check the provided URN: %s"));
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:grai:[0-9]{6,12}\\.[0-9]{0,6}\\.\\*",
            "Invalid GRAI,\n The first 14 characters of the GRAI with Serial must be digits (Ex: urn:epc:idpat:grai:1234567890.12.*).\nPlease check the provided URN: %s") {

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
                      "Invalid GRAI Length, GRAI with Serial must be 14 digits followed by 1 to 16 alphanumeric characters (Ex: urn:epc:idpat:grai:1234567890.12.*),%nPlease check the provided URN: %s",
                      urn));
            }
          }
        });

    // Populate all the pattern and message for DL URI
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid GRAI, GRAI should start with Domain name (Ex: https://id.gs1.org/),\nPlease check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*/8003/[0-9]{13}[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,16}$",
            "Invalid GRAI, GRAI with Serial must be 14 digits followed by 1 to 16 alphanumeric characters (Ex: https://id.gs1.org/8003/0123456789012123ABCD),\nPlease check the URI: %s") {
          @Override
          protected void validate(final String uri, final int gcpLength)
              throws ValidationException {
            super.validate(uri, gcpLength);

            // Check if the GCP Length matches
            if (!(gcpLength >= 6 && gcpLength <= 12)) {
              throw new ValidationException(
                  String.format(
                      "GCP Length should be between 6-12.%nPlease check the provided URI : %s",
                      gcpLength));
            }
          }
        });

    // Populate all the pattern and message for DL URI for class-level
    URI_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid GRAI, GRAI should start with Domain name (Ex: https://id.gs1.org/),\nPlease check the URI: %s"));
    URI_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "(http|https)://.*/8003/[0-9]{13}",
            "Invalid GRAI, GRAI must be 13 digits (Ex: https://id.gs1.org/8003/9524321890009),\nPlease check the URI: %s") {
          @Override
          protected void validate(final String uri, final int gcpLength)
              throws ValidationException {
            super.validate(uri, gcpLength);

            // Check if the GCP Length matches
            if (!(gcpLength >= 6 && gcpLength <= 12)) {
              throw new ValidationException(
                  String.format(
                      "GCP Length should be between 6-12.%nPlease check the provided URI : %s",
                      gcpLength));
            }
          }
        });
  }

  // Public Method to validate the GRAI URN
  public final void validateURN(final String urn) throws ValidationException {
    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the GRAI DL URI
  public final void validateURI(final String dlURI, final int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }

  // Public Method to validate the GRAI URN
  public final void validateClassLevelURN(final String urn) throws ValidationException {
    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_WITHOUT_SERIAL_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the GRAI DL URI
  public final void validateClassLevelURI(final String dlURI, final int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_WITHOUT_SERIAL_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }
}
