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

public class CPIValidator implements PatternValidator {

  private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URN_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
  private static final String CPI_SERIAL_PART = "/8011/";
  private static final String CPI_URI_PART = "/8010/";

  static {
    // Fill the URN pattern and messages
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:cpi:.*",
            "Invalid CPI, CPI should start with \"urn:epc:id:cpi:\" (Ex: urn:epc:id:cpi:123456789.0123459.1234).%nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:cpi:[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{6,12}.*",
            "Invalid CPI, CPI should consist of GCP with 6-12 digits (Ex: urn:epc:id:cpi:123456789.0123459.1234).%nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:cpi:[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{6,12}\\.[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{1,24}.*",
            "Invalid CPI, CPI must be between 7 and 30 digits with GCP 6-12 digits (Ex: urn:epc:id:cpi:123456789.0123459.1234).%nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:cpi:[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{6,12}\\.[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{1,24}\\.[0-9]{1,12}",
            "Invalid CPI, CPI must be between 7 and 30 digits followed by Serial with 1-12 digits (Ex: urn:epc:id:cpi:123456789.0123459.1234).%nPlease check the provided URN: %s") {

          @Override
          public void validate(final String urn) throws ValidationException {
            super.validate(urn);
            String cpi = urn.substring(urn.lastIndexOf(":"), urn.lastIndexOf("."));
            if (cpi.length() < 7 || cpi.length() > 31) {
              throw new ValidationException(
                  String.format(
                      "Invalid CPI, CPI must be between 7 and 30 digits (Ex: urn:epc:id:cpi:123456789.0123459.1234).%nPlease check the provided URN: %s",
                      urn));
            }
          }
        });

    // Fill the URN pattern without serial and messages
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:cpi:.*",
            "Invalid CPI, Class level CPI should start with \"urn:epc:idpat:cpi:\" (Ex: urn:epc:idpat:cpi:123456789.0123459.*).%nPlease check the provided URN: %s"));
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:cpi:[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{6,12}.*",
            "Invalid CPI, Class level CPI should consist of GCP with 6-12 digits (Ex: urn:epc:idpat:cpi:123456789.0123459.*).%nPlease check the provided URN: %s"));
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:cpi:[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{6,12}\\.[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{0,24}\\.\\*",
            "Invalid CPI, Class level CPI must be between 7 and 30 digits with GCP 6-12 digits (Ex: urn:epc:idpat:cpi:123456789.0123459.*).%nPlease check the provided URN: %s") {

          @Override
          public void validate(final String urn) throws ValidationException {
            super.validate(urn);
            String cpi = urn.substring(urn.lastIndexOf(":"), urn.lastIndexOf("."));
            if (cpi.length() < 7 || cpi.length() > 31) {
              throw new ValidationException(
                  String.format(
                      "Invalid CPI, CPI must be between 7 and 30 digits (Ex: urn:epc:idpat:cpi:123456789.0123459.*).%nPlease check the provided URN: %s",
                      urn));
            }
          }
        });

    // Fill the DLURI pattern and messages
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid CPI, CPI must begin with the Domain name (Ex: https://id.gs1.org/).%nPlease check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./8010/[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{7,30}.*",
            "Invalid CPI, CPI must be between 7 and 30 digits (Ex: https://id.gs1.org/8010/1234567890123459/8011/1234).%nPlease check the URI: %s") {

          @Override
          protected void validate(final String urn, final int gcpLength)
              throws ValidationException {
            super.validate(urn, gcpLength);
            final String cpi =
                urn.substring(
                    urn.indexOf(CPI_URI_PART) + CPI_URI_PART.length(),
                    urn.indexOf(CPI_SERIAL_PART));

            // Check if the GCP Length is valid
            if (!(gcpLength >= 6 && gcpLength <= 12)) {
              throw new ValidationException(
                  String.format(
                      "GCP Length should be between 6-12.%nPlease check the provided URI : %s",
                      gcpLength));
            }

            // Check if CPI Length is more than GCP Length
            if (gcpLength > cpi.length()) {
              throw new ValidationException(
                  String.format(
                      "GCP Length cannot be more than the CPI length.%nPlease check the provided URI : %s",
                      urn));
            }
          }
        });
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./8010/[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{7,30}/8011/[0-9]{1,12}",
            "Invalid CPI, CPI must be 7-30 digits followed by serial of 1 to 12 digits (Ex: https://id.gs1.org/8010/1234567890123459/8011/1234).%nPlease check the URI: %s"));

    // Fill the DLURI with no serial pattern and messages
    URI_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid CPI, Class level CPI must begin with the Domain name (Ex: https://id.gs1.org/).%nPlease check the URI: %s"));
    URI_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./8010/[\\x23\\x2D\\x2F\\x30-\\x39\\x41-\\x5A]{7,30}",
            "Invalid CPI, Class level CPI must be between 7 and 30 digits (Ex: https://id.gs1.org/8010/1234567890123459).%nPlease check the URI: %s") {

          @Override
          protected void validate(final String urn, final int gcpLength)
              throws ValidationException {
            super.validate(urn, gcpLength);
            final String cpi = urn.substring(urn.indexOf(CPI_URI_PART) + CPI_URI_PART.length());

            // Check if the GCP Length is valid
            if (!(gcpLength >= 6 && gcpLength <= 12)) {
              throw new ValidationException(
                  String.format(
                      "GCP Length should be between 6-12.%nPlease check the provided URI : %s",
                      gcpLength));
            }

            // Check if CPI Length is more than GCP Length
            if (gcpLength > cpi.length()) {
              throw new ValidationException(
                  String.format(
                      "GCP Length cannot be more than the CPI length.%nPlease check the provided URI : %s",
                      urn));
            }
          }
        });
  }

  // Public Method to validate the CPI URN
  public final void validateURN(final String urn) throws ValidationException {
    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the CPI DL URI
  public final void validateURI(final String dlURI, int gcpLength) throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }

  // Public Method to validate the CPI URN
  public final void validateClassLevelURN(final String urn) throws ValidationException {
    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_WITHOUT_SERIAL_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the CPI DL URI
  public final void validateClassLevelURI(final String dlURI, int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_WITHOUT_SERIAL_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }
}
