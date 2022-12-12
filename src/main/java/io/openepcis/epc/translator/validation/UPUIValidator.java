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

public class UPUIValidator implements PatternValidator {

  private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
  private static final String UPUI_URN_PART = ":upui:";

  static {
    // Fill the URN pattern and messages
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:upui:.*",
            "Invalid UPUI, UPUI should start with \"urn:epc:id:upui:\" (Ex: urn:epc:id:upui:234567890123.1.1234ABCD5678EFGH).\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:upui:[0-9]{6,12}.*",
            "Invalid UPUI, UPUI should consist of GCP with 6-12 digits (Ex: urn:epc:id:upui:234567890123.1.1234ABCD5678EFGH).\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:upui:[0-9]{6,12}\\.[0-9]{1,7}.*",
            "Invalid UPUI, UPUI must be of 14 digits (Ex: urn:epc:id:upui:234567890123.1.1234ABCD5678EFGH).\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:upui:[0-9]{6,12}\\.[0-9]{1,7}\\.[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,28}",
            "Invalid UPUI, UPUI should consist of TPX of 1 to 28 characters(Ex: urn:epc:id:upui:234567890123.1.1234ABCD5678EFGH).\nPlease check the provided URN : %s") {

          @Override
          public void validate(final String urn) throws ValidationException {
            super.validate(urn);

            String upui =
                urn.charAt(StringUtils.ordinalIndexOf(urn, ".", 1) + 1)
                    + urn.substring(
                        urn.indexOf(UPUI_URN_PART) + UPUI_URN_PART.length(),
                        StringUtils.ordinalIndexOf(urn, ".", 1));
            upui =
                upui
                    + urn.substring(
                        StringUtils.ordinalIndexOf(urn, ".", 1) + 2,
                        StringUtils.ordinalIndexOf(urn, ".", 2));

            if (upui.length() != 13) {
              throw new ValidationException(
                  String.format(
                      "Invalid UPUI, UPUI must be of 14 digits (Ex: urn:epc:id:upui:234567890123.1.1234ABCD5678EFGH),%n Please check the provided URN: %s",
                      urn));
            }
          }
        });

    // Fill the DLURI pattern and messages
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid UPUI, UPUI should start with Domain name (Ex: https://id.gs1.org/01/12345678901231/235/9999).%nPlease check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./01/[0-9]{14}.*",
            "Invalid UPUI, UPUI must consist of 14 digits (Ex: https://id.gs1.org/01/12345678901231/235/9999).\nPlease check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./01/[0-9]{14}/235/[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,28}",
            "Invalid UPUI, UPUI must consist of TPX 1 to 28 characters (Ex: https://id.gs1.org/01/12345678901231/235/9999).\nPlease check the URI: %s") {
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

  // Public Method to validate the UPUI URN
  public final void validateURN(final String urn) throws ValidationException {
    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the UPUI DL URI
  public final void validateURI(final String dlURI, final int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }
}
