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

public class ITIPValidator implements PatternValidator {

  private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URN_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
  private static final String ITIP_URN_PART = ":itip:";

  static {
    // Fill the URN pattern and messages
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:itip:.*",
            "Invalid ITIP, ITIP should start with \"urn:epc:id:itip:\" (Ex: urn:epc:id:itip:23456789.10123.56.78.0000).\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:itip:[0-9]{6,12}.*",
            "Invalid ITIP, ITIP should consist of GCP with 6-12 digits (Ex: urn:epc:id:itip:23456789.10123.56.78.0000).\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:itip:[0-9]{6,12}\\.[0-9]{1,7}\\.[0-9]{2}\\.[0-9]{2}.*",
            "Invalid ITIP, ITIP should consist of 18 digits  (Ex: urn:epc:id:itip:23456789.10123.56.78.0000).\nPlease check the provided URN: %s") {

          @Override
          public void validate(String urn) throws ValidationException {
            super.validate(urn);
            String itip;

            if (StringUtils.countMatches(urn, ".") >= 4) {
              itip =
                  urn.substring(
                      urn.indexOf(ITIP_URN_PART) + ITIP_URN_PART.length(),
                      StringUtils.ordinalIndexOf(urn, ".", 4));
            } else {
              throw new ValidationException(
                  String.format(
                      "Invalid ITIP, ITIP should consist of Serial Numbers (Ex: urn:epc:id:itip:23456789.10123.56.78.0000).\nPlease check the provided URN:"
                          + " %s",
                      urn));
            }

            if (itip.length() != 20) {
              throw new ValidationException(
                  String.format(
                      "Invalid ITIP, ITIP should consist of 18 digits (Ex: urn:epc:id:itip:23456789.10123.56.78.0000).\nPlease check "
                          + "the provided URN: %s",
                      urn));
            }
          }
        });
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:itip:[0-9]{6,12}\\.[0-9]{1,7}\\.[0-9]{2}\\.[0-9]{2}\\.[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,20}",
            "Invalid ITIP, ITIP should consist Serial numbers 1 to 20 characters (Ex: urn:epc:id:itip:23456789.10123.56.78.0000),\nPlease check the provided URN : %s"));

    // Fill the URN without serial pattern and messages
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:itip:.*",
            "Invalid ITIP, Class level ITIP should start with \"urn:epc:id:itip:\" (Ex: urn:epc:idpat:itip:23456789.10123.56.78.*).\nPlease check the provided URN: %s"));
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:itip:[0-9]{6,12}.*",
            "Invalid ITIP, Class level ITIP should consist of GCP with 6-12 digits (Ex: urn:epc:idpat:itip:23456789.10123.56.78.*).\nPlease check the provided URN: %s"));
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:itip:[0-9]{6,12}\\.[0-9]{1,7}\\.[0-9]{2}\\.[0-9]{2}\\.\\*",
            "Invalid ITIP, Class level ITIP should consist of 18 digits  (Ex: urn:epc:idpat:itip:23456789.10123.56.78.*).\nPlease check the provided URN: %s") {

          @Override
          public void validate(String urn) throws ValidationException {
            super.validate(urn);
            final String itip = urn.substring(urn.lastIndexOf(":") + 1, urn.lastIndexOf("."));

            if (itip.length() != 20) {
              throw new ValidationException(
                  String.format(
                      "Invalid ITIP, Class level ITIP should consist of 18 digits (Ex: urn:epc:idpat:itip:23456789.10123.56.78.*).%nPlease check the provided URN: %s",
                      urn));
            }
          }
        });

    // Fill the DLURI pattern and messages
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid ITIP, ITIP should start with Domain name (Ex: https://id.gs1.org/8006/123456789012356756/21/100),\nPlease check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./8006/[0-9]{18}.*",
            "Invalid ITIP, ITIP must consist of 18 digits (Ex: https://id.gs1.org/8006/123456789012356756/21/100),\nPlease check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./8006/[0-9]{18}/21/[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{1,20}",
            "Invalid ITIP, ITIP must consist of Serial numbers 1 to 20 characters (Ex: https://id.gs1.org/8006/123456789012356756/21/100),\nPlease check the URI: %s") {
          @Override
          protected void validate(String uri, int gcpLength) throws ValidationException {
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

    // Fill the DLURI without serial pattern and messages
    URI_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid ITIP, Class level ITIP should start with Domain name (Ex: https://id.gs1.org/8006/123456789012356756),\nPlease check the URI: %s"));
    URI_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./8006/[0-9]{18}",
            "Invalid ITIP, Class level ITIP must consist of 18 digits (Ex: https://id.gs1.org/8006/123456789012356756),\nPlease check the URI: %s") {
          @Override
          protected void validate(String uri, int gcpLength) throws ValidationException {
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

  // Public Method to validate the ITIP URN
  public final void validateURN(final String urn) throws ValidationException {
    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the ITIP DL URI
  public final void validateURI(final String dlURI, final int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }

  // Public Method to validate the ITIP URN
  public final void validateClassLevelURN(final String urn) throws ValidationException {
    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_WITHOUT_SERIAL_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the ITIP DL URI
  public final void validateClassLevelURI(final String dlURI, final int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_WITHOUT_SERIAL_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }
}
