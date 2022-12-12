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

public class GINCValidator implements PatternValidator {

  private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
  private static final String GINC_URI_PART = "/401/";
  private static final String GINC_URN_PART = ":ginc:";

  static {
    // Fill the URN pattern and messages
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:ginc:.*",
            "Invalid GINC, GINC should start with \"urn:epc:id:ginc:\" (Ex: urn:epc:id:ginc:1234567890.ABCDEF123456789),\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:ginc:[0-9]{6,12}.*",
            "Invalid GINC, GINC should consist of GCP with 6-12 digits (Ex: urn:epc:id:ginc:1234567890.ABCDEF123456789),\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:ginc:[0-9]{6,12}\\.[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,24}",
            "Invalid GINC, GINC should be between 7 and 30 characters with GCP 6-12 digits (Ex: urn:epc:id:ginc:1234567890.ABCDEF123456789),\nPlease check the provided URN: %s") {

          @Override
          public void validate(final String urn) throws ValidationException {
            super.validate(urn);

            // GINC Length cannot be more than 30 characters and less than 7 characters
            String ginc = urn.substring(urn.indexOf(GINC_URN_PART) + GINC_URN_PART.length());

            if (!(ginc.length() <= 31 && ginc.length() >= 7)) {
              throw new ValidationException(
                  String.format(
                      "Invalid GINC, GINC should be between 7 and 30 characters (Ex: urn:epc:id:ginc:1234567890.ABCDEF123456789),%nPlease check the provided URN: %s",
                      urn));
            }
          }
        });

    // Fill the DLURI pattern and messages
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid GINC, GINC should start with Domain name (Ex: https://id.gs1.org/401/123456789012100),\nPlease check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./401/[0-9]{6,12}[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,24}",
            "Invalid GINC, GINC should be between 7 and 30 characters with GCP 6-12 digits (Ex: https://id.gs1.org/401/123456789012100),\nPlease check the URI: %s") {

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

            // Check the GINC Length is more than GCP Length
            final String ginc = uri.substring(uri.indexOf(GINC_URI_PART) + GINC_URI_PART.length());
            if (ginc.length() < gcpLength) {
              throw new ValidationException(
                  String.format(
                      "GINC length should be more than GCP Length,%nPlease check the provided URI: %s",
                      uri));
            }

            // Check if the GCP contains only Digits
            if (!(ginc.substring(0, gcpLength).matches("\\d*"))) {
              throw new ValidationException(
                  String.format(
                      "GCP should contain only digits between 6-12.%nPlease check the provided URI: %s",
                      uri));
            }
          }
        });
  }

  // Public Method to validate the GINC URN
  public final void validateURN(final String urn) throws ValidationException {
    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the GINC DL URI
  public final void validateURI(final String dlURI, final int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }
}
