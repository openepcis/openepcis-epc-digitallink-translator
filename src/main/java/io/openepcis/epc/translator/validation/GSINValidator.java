/*
 * Copyright 2022-2024 benelog GmbH & Co. KG
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

public class GSINValidator implements PatternValidator {

  private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
  private static final String GSIN_URN_PART = ":gsin:";

  static {
    // Fill the URN pattern and messages
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:gsin:.*",
            "Invalid GSIN, GSIN should start with \"urn:epc:id:gsin:\" (Ex: urn:epc:id:gsin:123456.7890123456).\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:gsin:[0-9]{6,12}.*",
            "Invalid GSIN, GSIN should consist of GCP with 6-12 digits (Ex: urn:epc:id:gsin:123456.7890123456).\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:gsin:[0-9]{6,12}\\.[0-9]{4,10}",
            "Invalid GSIN, GSIN should consist of 17 digits with GCP 6-12 digits (Ex: urn:epc:id:gsin:123456.7890123456).\nPlease check the provided URN: %s") {

          @Override
          public void validate(final String urn) throws ValidationException {
            super.validate(urn);
            final String gsin =
                urn.substring(urn.indexOf(GSIN_URN_PART) + GSIN_URN_PART.length(), urn.indexOf("."))
                    + urn.substring(urn.indexOf(".") + 1);

            if (gsin.length() != 16 || gsin.matches(".*\\D.*")) {
              throw new ValidationException(
                  String.format(
                      "Invalid GSIN, GSIN should consist of 17 digits (Ex: urn:epc:id:gsin:123456.7890123456).%nPlease check the provided URN: %s",
                      urn));
            }
          }
        });

    // Fill the DLURI pattern and messages
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid GSIN, GSIN should start with Domain name (Ex: https://id.gs1.org/402/12345607890123456).\nPlease check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./402/[0-9]{17}",
            "Invalid GSIN, GSIN should consist of 17 digits (Ex: https://id.gs1.org/402/12345607890123456).\nPlease check the URI: %s") {

          @Override
          protected void validate(final String uri, final int gcpLength)
              throws ValidationException {
            super.validate(uri, gcpLength);

            // Check the provided GCP Length is between 6 and 12 digits
            if (!(gcpLength >= 6 && gcpLength <= 12)) {
              throw new ValidationException(
                  String.format(
                      "GCP Length should be between 6-12 digits.%nPlease check the provided GCP Length: %s",
                      gcpLength));
            }
          }
        });
  }

  // Public Method to validate the GSIN URN
  public final void validateURN(final String urn) throws ValidationException {
    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the GSIN DL URI
  public final void validateURI(final String dlURI, final int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }
}
