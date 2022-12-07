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

public class SGLNValidator implements PatternValidator {

  private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
  private static final String SGLN_URN_PART = ":sgln:";

  static {
    // Populate all the pattern and message for URN
    URN_MATCHERS.add(
        new Matcher(
            "(urn:epc:id:sgln:).*",
            "Invalid SGLN,SGLN should start with \"urn:epc:id:sgln:\" (Ex: urn:epc:id:sgln:1234567890.12.1111).%nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "(urn:epc:id:sgln:)[0-9]{6,12}.*",
            "Invalid SGLN,SGLN should consist of GCP with 6-12 digits (Ex: urn:epc:id:sgln:1234567890.12.1111).%nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "(urn:epc:id:sgln:)[0-9]{6,12}\\.[0-9]{0,6}.*",
            "Invalid SGLN,SGLN should be of 13 digits with GCP 6-12 digits (Ex: urn:epc:id:sgln:1234567890.12.1111).%nPlease check the provided URN: %s") {
          @Override
          public void validate(String urn) throws ValidationException {
            super.validate(urn);

            String sgln;

            if (StringUtils.countMatches(urn, ".") >= 2) {
              sgln =
                  urn.substring(
                      urn.indexOf(SGLN_URN_PART) + SGLN_URN_PART.length(),
                      StringUtils.ordinalIndexOf(urn, ".", 2));
            } else {
              throw new ValidationException(
                  String.format(
                      "Invalid SGLN, SGLN should be of 13 digits followed by extension (Ex: urn:epc:id:sgln:1234567890.12.1111).%nPlease check the provided"
                          + " URN: %s",
                      urn));
            }

            if (sgln.length() != 13) {
              throw new ValidationException(
                  String.format(
                      "Invalid SGLN, SGLN should be of 13 digits (Ex: urn:epc:id:sgln:1234567890.12.1111).%nPlease check the provided URN: %s",
                      urn));
            }
          }
        });
    URN_MATCHERS.add(
        new Matcher(
            "(urn:epc:id:sgln:)[0-9]{6,12}\\.[0-9]{0,6}(?:\\.[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,20})?$",
            "Invalid SGLN,SGLN should consist of Serial numbers (Ex: urn:epc:id:sgln:1234567890.12.1111).%nPlease check the provided URN: %s"));

    // Populate all the pattern and message for DL URI
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid SGLN, SGLN should start with Domain name (Ex: https://id.gs1.org/),%nPlease check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https):?://.*/414/[0-9]{13}.*",
            "Invalid SGLN, SGLN should consist of 13 digit SGLN (Ex: https://id.gs1.org/414/1234567890128/254/1111),%nPlease check the URI: %s") {
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
    URI_MATCHERS.add(
        new Matcher(
            "(http|https):?://.*/414/[0-9]{13}(/254/[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,20})?",
            "Invalid SGLN, SGLN should consist of 13 digit SGLN with Seral number  (Ex: https://id.gs1.org/414/1234567890128/254/1111),%nPlease check the URI: %s"));
  }

  // Public Method to validate the SGLN URN
  public final void validateURN(final String urn) throws ValidationException {
    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the SGLN DL URI
  public final void validateURI(final String dlURI, final int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }
}
