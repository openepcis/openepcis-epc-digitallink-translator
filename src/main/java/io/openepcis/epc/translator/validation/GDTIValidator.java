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

public class GDTIValidator implements PatternValidator {

  private static final String GDTI_URI_PART = "/253/";
  private static final String GDTI_URN_PART = "gdti:";
  private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URN_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
  private static final String GCP_ERROR_MESSAGE =
      "GCP Length must be between 6 and 12,%nPlease check the provided URI : %s";
  private static final String CPI_ERROR_MESSAGE =
      "GCP Length cannot be more than the CPI length,%nPlease check the provided URI : %s";
  private static final String URI_PREFIX = "(http|https)://.*";

  static {
    // Fill the URN pattern and messages
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:gdti:.*",
            "Invalid GDTI, GDTI should start with \"urn:epc:id:gdti:\" (Ex: urn:epc:id:gdti:123456.789012.ABC123).\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:gdti:[0-9]{6,12}.*",
            "Invalid GDTI, GDTI should consist of GCP with 6-12 digits (Ex: urn:epc:id:gdti:123456.789012.ABC123).\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:gdti:[0-9]{6,12}\\.[0-9]{0,6}.*",
            "Invalid GDTI, GDTI should be of 13 digits (Ex: urn:epc:id:gdti:123456.789012.ABC123).\nPlease check the provided URN: %s") {

          @Override
          public void validate(String urn) throws ValidationException {
            super.validate(urn);
            String gdti;
            if (urn.indexOf(".", urn.indexOf(".") + 1) == -1) {
              gdti = urn.substring(urn.indexOf(GDTI_URN_PART) + GDTI_URN_PART.length());
            } else {
              gdti =
                  urn.substring(
                      urn.indexOf(GDTI_URN_PART) + GDTI_URN_PART.length(), urn.lastIndexOf("."));
            }

            // GDTI length should be 13 digits
            if (gdti.length() != 13) {
              throw new ValidationException(
                  String.format(
                      "Invalid GDTI, GDTI should be of 13 digits (Ex: urn:epc:id:gdti:123456.789012.ABC123).%nPlease check the provided URN: %s",
                      urn));
            }
          }
        });
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:gdti:[0-9]{6,12}\\.[0-9]{0,6}\\.[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,17}$",
            "Invalid GDTI, GDTI with Serial must be 13 digits followed by 1 to 17 alphanumeric characters (Ex: urn:epc:id:gdti:123456.789012.ABC123).\nPlease check the "
                + "provided URN: %s"));

    // Fill the URN pattern and messages for class-level
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:gdti:.*",
            "Invalid GDTI, Class level GDTI should start with \"urn:epc:idpat:gdti:\" (Ex: urn:epc:id:gdti:123456.789012.*).\nPlease check the provided URN: %s"));
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:gdti:[0-9]{6,12}.*",
            "Invalid GDTI, Class level GDTI should consist of GCP with 6-12 digits (Ex: urn:epc:id:gdti:123456.789012.*).\nPlease check the provided URN: %s"));
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:gdti:[0-9]{6,12}\\.[0-9]{0,6}\\.\\*",
            "Invalid GDTI, Class level GDTI should be of 13 digits (Ex: urn:epc:idpat:gdti:123456.789012.*).\nPlease check the provided URN: %s") {

          @Override
          public void validate(String urn) throws ValidationException {
            super.validate(urn);
            String gdti;
            if (urn.indexOf(".", urn.indexOf(".") + 1) == -1) {
              gdti = urn.substring(urn.lastIndexOf(":") + 1);
            } else {
              gdti = urn.substring(urn.lastIndexOf(":") + 1, urn.lastIndexOf("."));
            }

            // GDTI length should be 13 digits
            if (gdti.length() != 13) {
              throw new ValidationException(
                  String.format(
                      "Invalid GDTI, Class level GDTI should be of 13 digits (Ex: urn:epc:idpat:gdti:123456.789012.*).\nPlease check "
                          + "the provided URN: %s",
                      urn));
            }
          }
        });

    // Fill the URI pattern and messages
    URI_MATCHERS.add(
        new Matcher(
            URI_PREFIX,
            "Invalid GDTI, GDTI must begin with the Domain name (Ex: https://id.gs1.org/),\nPlease check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./253/[0-9]{13}.*",
            "Invalid GDTI, GDTI must be 13 digits (Ex: https://id.gs1.org/253/1234567890128ABC123),\nPlease check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./253/[0-9]{13}[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,17}",
            "Invalid GDTI, GDTI must be 13 digits followed by 1 to 17 alphanumeric characters (Ex: "
                + "https://id.gs1.org/253/1234567890128ABC123),\nPlease check the URI: %s") {

          @Override
          protected void validate(String uri, int gcpLength) throws ValidationException {

            super.validate(uri, gcpLength);

            String gdti =
                uri.substring(
                    uri.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length(),
                    uri.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length() + 13);

            // Check if the GCPLength is valid
            if (!(gcpLength >= 6 && gcpLength <= 12)) {
              throw new ValidationException(String.format(GCP_ERROR_MESSAGE, gcpLength));
            }

            // Check if the GDTI length more than GCP Length
            if (gdti.length() < gcpLength) {
              throw new ValidationException(String.format(CPI_ERROR_MESSAGE, uri));
            }
          }
        });

    // Fill the URI pattern and messages for class-level
    URI_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            URI_PREFIX,
            "Invalid GDTI, Class level GDTI must begin with the Domain name (Ex: https://id.gs1.org/),\nPlease check the URI: %s"));
    URI_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./253/[0-9]{13}",
            "Invalid GDTI, Class level GDTI must be 13 digits (Ex: https://id.gs1.org/253/9524321400017),\nPlease check the URI: %s") {

          @Override
          protected void validate(String uri, int gcpLength) throws ValidationException {

            super.validate(uri, gcpLength);

            String gdti =
                uri.substring(
                    uri.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length(),
                    uri.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length() + 13);

            // Check if the GCPLength is valid
            if (!(gcpLength >= 6 && gcpLength <= 12)) {
              throw new ValidationException(String.format(GCP_ERROR_MESSAGE, gcpLength));
            }

            // Check if the GDTI length more than GCP Length
            if (gdti.length() < gcpLength) {
              throw new ValidationException(String.format(CPI_ERROR_MESSAGE, uri));
            }
          }
        });

    // Fill the URI pattern and messages for class-level
    URI_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            URI_PREFIX,
            "Invalid GDTI, GDTI must begin with the Domain name (Ex: https://id.gs1.org/),\nPlease check the URI: %s"));
    URI_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./253/[0-9]{13}",
            "Invalid GDTI, GDTI must be 13 digits (Ex: https://id.gs1.org/253/9524321400017),\nPlease check the URI: %s") {

          @Override
          protected void validate(String uri, int gcpLength) throws ValidationException {

            super.validate(uri, gcpLength);

            String gdti =
                uri.substring(
                    uri.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length(),
                    uri.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length() + 13);

            // Check if the GCPLength is valid
            if (!(gcpLength >= 6 && gcpLength <= 12)) {
              throw new ValidationException(String.format(GCP_ERROR_MESSAGE, gcpLength));
            }

            // Check if the GDTI length more than GCP Length
            if (gdti.length() < gcpLength) {
              throw new ValidationException(String.format(CPI_ERROR_MESSAGE, uri));
            }
          }
        });
  }

  // Public Method to validate the GDTI URN
  public final void validateURN(final String urn) throws ValidationException {

    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the GDTI DL URI
  public final void validateURI(final String dlURI, final int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }

  // Public Method to validate the GDTI URN
  public final void validateClassLevelURN(final String urn) throws ValidationException {

    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_WITHOUT_SERIAL_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the GDTI DL URI
  public final void validateClassLevelURI(final String dlURI, final int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_WITHOUT_SERIAL_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }
}
