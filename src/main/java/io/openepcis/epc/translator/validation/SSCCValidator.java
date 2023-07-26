/*
 * Copyright 2022-2023 benelog GmbH & Co. KG
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

public class SSCCValidator implements PatternValidator {

  private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_MATCHERS = new ArrayList<>();

  static {
    // Populate all the pattern and message for URN
    URN_MATCHERS.add(
        new Matcher(
            "(urn:epc:id:sscc:).*",
            "Invalid SSCC, SSCC should start with \"urn:epc:id:sscc:\",\nPlease check the provided URN : %s "));
    URN_MATCHERS.add(
        new Matcher(
            "(urn:epc:id:sscc:)[0-9]{6,12}.*",
            "Invalid SSCC, SSCC should consist of GCP with 6-12 digits (Ex: urn:epc:id:sscc:234567.18901234567).\nPlease check the provided URN : %s "));
    URN_MATCHERS.add(
        new Matcher(
            "(urn:epc:id:sscc:)[0-9]{6,12}\\.[0-9]{5,11}",
            "Invalid SSCC, SSCC should be 18 digits with GCP 6-12 digits(Ex: urn:epc:id:sscc:234567.18901234567).\nPlease check the provided URN : %s") {

          @Override
          public void validate(final String urn) throws ValidationException {
            super.validate(urn);

            // Check if the SSCC contains 18 digits
            final String sscc = urn.substring(urn.lastIndexOf(":") + 1);

            if (sscc.length() != 18) {
              throw new ValidationException(
                  String.format(
                      "Invalid SSCC length, SSCC should be 18 digits (Ex: urn:epc:id:sscc:234567.18901234567).%nPlease check the provided URN: %s",
                      urn));
            }
          }
        });

    // Populate all the pattern and message for DL URI
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid SSCC, SSCC should start with Domain name (Ex: https://id.gs1.org/),%nPlease check the DL URI : %s "));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*/00/[0-9]{18}",
            "Invalid SSCC, SSCC should consist of 18 digits (Ex: https://id.gs1.org/00/012345678966638624),%nPlease check the DL URI : %s ") {

          @Override
          protected void validate(final String uri, final int gcpLength)
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

  // Public Method to validate the SSCC URN
  public final void validateURN(final String urn) throws ValidationException {
    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the SSCC DL URI
  public final void validateURI(final String dlURI, final int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }
}
