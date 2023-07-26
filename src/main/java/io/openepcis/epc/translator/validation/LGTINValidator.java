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

public class LGTINValidator {
  private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
  private static final String LGTIN_URN_PART = ":lgtin:";

  static {
    // Fill the URN pattern and messages
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:class:lgtin:.*",
            "Invalid LGTIN, LGTIN should start with \"urn:epc:class:lgtin:\" (Ex: urn:epc:class:lgtin:234567890.1123.9999).\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:class:lgtin:[0-9]{6,12}.*",
            "Invalid LGTIN, LGTIN should consist of GCP with 6-12 digits (Ex: urn:epc:class:lgtin:234567890.1123.9999).\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:class:lgtin:[0-9]{6,12}\\.[0-9]{1,7}.*",
            "Invalid LGTIN, LGTIN should be of 14 digits and GCP should match 6-12 digits (Ex: urn:epc:class:lgtin:234567890.1123.9999).\nPlease check the provided URN: %s") {
          @Override
          public void validate(final String urn) throws ValidationException {
            super.validate(urn);

            final String gcp =
                urn.substring(
                    urn.indexOf(LGTIN_URN_PART) + LGTIN_URN_PART.length(), urn.indexOf('.'));
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
                      "Invalid LGTIN, LGTIN values should be of 14 digits (Ex: urn:epc:class:lgtin:234567890.1123.9999).%nPlease check the provided URN : %s",
                      urn));
            }
          }
        });
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:class:lgtin:[0-9]{6,12}\\.[0-9]{1,7}\\.[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,20}",
            "Invalid LGTIN, LGTIN should consist of serial numbers(Ex: urn:epc:class:lgtin:234567890.1123.9999).\nPlease check the provided URN : %s"));

    // Fill the DLURI pattern and messages
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid LGTIN, LGTIN should start with Domain name (Ex: https://id.gs1.org/01/12345678901234/10/1111).\nPlease check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./01/[0-9]{14}.*",
            "Invalid LGTIN, LGTIN should consist of 14 digit LGTIN (Ex: https://id.gs1.org/01/12345678901234/10/1111).\nPlease check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./01/[0-9]{14}/10/[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,20}",
            "Invalid LGTIN, LGTIN should consist of 14 digit LGTIN followed by Serial numbers (Ex: https://id.gs1.org/01/12345678901234/10/1111).\nPlease check the DL URI: %s") {
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

  // Public Method to validate the LGTIN URN
  public final void validateURN(final String urn) throws ValidationException {
    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the LGTIN DL URI
  public final void validateURI(final String dlURI, final int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }
}
