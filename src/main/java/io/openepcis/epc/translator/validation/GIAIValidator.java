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

public class GIAIValidator implements PatternValidator {

  private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
  private static final String GIAI_URI_PART = "/8004/";

  static {
    // Fill the URN pattern and messages
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:giai:.*",
            "Invalid GIAI, GIAI should start with \"urn:epc:id:giai:\" (Ex: urn:epc:id:giai:1234567890.ABCDEF1234),\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:giai:[0-9]{6,12}.*",
            "Invalid GIAI, GIAI should consist of GCP with 6-12 digits,\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:giai:[0-9]{6,12}\\.[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,24}",
            "Invalid GIAI, GIAI should consist of GCP with 6-12 digits followed by alphanumeric serial up to 30 characters (Ex: urn:epc:id:giai:1234567890.ABCDEF1234).\nPlease check the provided URN: %s"));

    // Fill the URI pattern and messages
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid GIAI, GIAI should start with Domain name (Ex:https://id.gs1.org/8004/1234567890ABCD),\nPlease check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./8004/[0-9]{6,12}[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,24}$",
            "Invalid GIAI, GIAI must be between 10 and 30 alphanumeric characters (Ex:https://id.gs1.org/8004/1234567890ABCD),\nPlease check the URI: %s") {

          @Override
          protected void validate(final String dlURI, final int gcpLength)
              throws ValidationException {
            super.validate(dlURI, gcpLength);

            String giai = dlURI.substring(dlURI.indexOf(GIAI_URI_PART) + GIAI_URI_PART.length());

            // Check if the GCP length matches
            if (!(gcpLength >= 6 && gcpLength <= 12)) {
              throw new ValidationException(
                  String.format(
                      "Invalid GCP Length, GCP Length must be between 6 and 12 digits.%nPlease check the GCP Length: %s",
                      gcpLength));
            }

            // Check if the GIAI length is valid
            if (giai.length() < gcpLength) {
              throw new ValidationException(
                  String.format(
                      "Invalid GIAI, GIAI length cannot be less than GCP length (Ex:https://id.gs1.org/8004/1234567890ABCD),%nPlease check the URI: %s",
                      dlURI));
            }
          }
        });
  }

  // Public Method to validate the GIAI URN
  public final void validateURN(final String urn) throws ValidationException {

    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the GIAI DL URI
  public final void validateURI(final String dlURI, final int gcpLength)
      throws ValidationException {

    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }
}
