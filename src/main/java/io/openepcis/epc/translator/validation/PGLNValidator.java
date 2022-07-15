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

import io.openepcis.epc.translator.ValidationException;
import java.util.ArrayList;
import java.util.List;

public class PGLNValidator implements PatternValidator {

  private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_MATCHERS = new ArrayList<>();

  static {
    // Populate all the pattern and message for URN
    URN_MATCHERS.add(
        new Matcher(
            "(urn:epc:id:pgln:).*",
            "Invalid PGLN,PGLN should start with \"urn:epc:id:pgln:\",\n Please check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "(urn:epc:id:pgln:)[0-9]{6,12}\\..*",
            "Invalid PGLN,PGLN should consist of GCP with 6-12 digits (Ex: urn:epc:id:pgln:123456.789012).\n Please check the provided URN: %s") {

          @Override
          public void validate(String urn) throws ValidationException {
            super.validate(urn);
            String pgln = urn.substring(urn.lastIndexOf(":") + 1);

            if (pgln.length() != 13) {
              throw new ValidationException(
                  String.format(
                      "Invalid PGLN length, PGLN length should be 12 digits (Ex: urn:epc:id:pgln:123456.789012). %nPlease check the provided URN: %s",
                      urn));
            }
          }
        });

    // Populate all the pattern and message for DL URI
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid PGLN, PGLN should start with Domain name (Ex: https://id.gs1.org/),\nPlease check the DL URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https):?://.*/417/[0-9]{13}",
            "Invalid PGLN, PGLN should consist of 13 digit PGLN (Ex: https://id.gs1.org/417/1234567890128),\nPlease check the URI: %s") {
          @Override
          protected void validate(String uri, int gcpLength) throws ValidationException {
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

  // Public Method to validate the PGLN URN
  public final void validateURN(final String urn) throws ValidationException {
    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the PGLN DL URI
  public final void validateURI(final String dlURI, final int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }
}
