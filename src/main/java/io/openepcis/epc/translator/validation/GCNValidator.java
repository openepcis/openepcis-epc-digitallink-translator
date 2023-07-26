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

public class GCNValidator implements PatternValidator {

  private static final String GCN_URI_PART = "/255/";
  private static final List<Matcher> URN_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URN_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();
  private static final List<Matcher> URI_WITHOUT_SERIAL_MATCHERS = new ArrayList<>();

  static {
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:sgcn:.*",
            "Invalid GCN, GCN should start with \"urn:epc:id:sgcn:\" (Ex: urn:epc:id:sgcn:123456.789012.4567890),\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:sgcn:[0-9]{6,12}.*",
            "Invalid GCN, GCN should consist of GCP with 6-12 digits (Ex: urn:epc:id:sgcn:123456.789012.4567890),\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:sgcn:[0-9]{6,12}\\.[0-9]{0,7}.*",
            "Invalid GCN, GCN should consist of 13 digits (Ex: urn:epc:id:sgcn:123456.789012.4567890),\nPlease check the provided URN: %s"));
    URN_MATCHERS.add(
        new Matcher(
            "urn:epc:id:sgcn:[0-9]{6,12}\\.[0-9]{0,7}\\.[0-9]{0,12}",
            "Invalid GCN, GCN with Serial must be between 14 and 25 digits (Ex: urn:epc:id:sgcn:123456.789012.4567890),\nPlease check the provided URN: %s") {

          @Override
          public void validate(String urn) throws ValidationException {
            super.validate(urn);
            final String sgcn = urn.substring(urn.lastIndexOf(":") + 1, urn.lastIndexOf("."));
            if (sgcn.length() != 13) {
              throw new ValidationException(
                  String.format(
                      "Invalid GCN, GCN should consist of 13 digits (Ex: urn:epc:id:sgcn:123456.789012.4567890),%nPlease check the provided URN: %s",
                      urn));
            }
          }
        });

    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid GCN, GCN should start with Domain name (Ex: https://id.gs1.org/255/12345678901284844274999),\nPlease check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./255/[0-9]{13}.*",
            "Invalid GCN, GCN should consist of 13 digit (Ex: https://id.gs1.org/255/12345678901284844274999),\nPlease check the URI: %s"));
    URI_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./255/[0-9]{13}[0-9]{1,12}",
            "Invalid GCN, GCN with Serial must be between 14 and 25 digits (Ex: https://id.gs1.org/255/12345678901284844274999),\nPlease check the URI: %s") {

          @Override
          protected void validate(String dlURI, int gcpLength) throws ValidationException {
            super.validate(dlURI, gcpLength);
            String sgcn = dlURI.substring(dlURI.indexOf(GCN_URI_PART) + GCN_URI_PART.length());
            sgcn = sgcn.substring(0, 13);

            // Check if the SGCN Length is more than GCP Length
            if (sgcn.length() < gcpLength) {
              throw new ValidationException(
                  String.format(
                      "Invalid GCN, GCN cannot be more than GCP length. Please check the provided URI: %s",
                      dlURI));
            }

            // Check if the GCP Length is valid
            if (!(gcpLength >= 6 && gcpLength <= 12)) {
              throw new ValidationException(
                  String.format(
                      "Invalid GCP Length, GCP Length should be between 6-12 digits. Please check the provided GCP Length: %s",
                      gcpLength));
            }
          }
        });

    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:sgcn:.*",
            "Invalid GCN, Class level GCN should start with \"urn:epc:idpat:sgcn:\" (Ex: urn:epc:idpat:sgcn:123456.789012.*),\nPlease check the provided URN: %s"));
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:sgcn:[0-9]{6,12}.*",
            "Invalid GCN, Class level GCN should consist of GCP with 6-12 digits (Ex: urn:epc:idpat:sgcn:123456.789012.*),\nPlease check the provided URN: %s"));
    URN_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "urn:epc:idpat:sgcn:[0-9]{6,12}\\.[0-9]{0,7}\\.\\*",
            "Invalid GCN, Class level GCN should consist of 13 digits (Ex: urn:epc:idpat:sgcn:123456.789012.*),\nPlease check the provided URN: %s") {

          @Override
          public void validate(String urn) throws ValidationException {
            super.validate(urn);
            final String sgcn = urn.substring(urn.lastIndexOf(":") + 1, urn.lastIndexOf("."));
            if (sgcn.length() != 13) {
              throw new ValidationException(
                  String.format(
                      "Invalid GCN, Class level GCN should consist of 13 digits (Ex: urn:epc:idpat:sgcn:123456.789012.*),%nPlease check the provided URN: %s",
                      urn));
            }
          }
        });

    URI_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "(http|https)://.*",
            "Invalid GCN, Class level GCN should start with Domain name (Ex: https://id.gs1.org/255/9524321678904),\nPlease check the URI: %s"));
    URI_WITHOUT_SERIAL_MATCHERS.add(
        new Matcher(
            "(http|https)://.*./255/[0-9]{13}",
            "Invalid GCN, Class level GCN should consist of 13 digit (Ex: https://id.gs1.org/255/9524321678904),\nPlease check the URI: %s") {

          @Override
          protected void validate(String dlURI, int gcpLength) throws ValidationException {
            super.validate(dlURI, gcpLength);
            String sgcn = dlURI.substring(dlURI.indexOf(GCN_URI_PART) + GCN_URI_PART.length());
            sgcn = sgcn.substring(0, 13);

            // Check if the SGCN Length is more than GCP Length
            if (sgcn.length() < gcpLength) {
              throw new ValidationException(
                  String.format(
                      "Invalid GCN, Class level GCN cannot be more than GCP length.%nPlease check the provided URI: %s",
                      dlURI));
            }

            // Check if the GCP Length is valid
            if (!(gcpLength >= 6 && gcpLength <= 12)) {
              throw new ValidationException(
                  String.format(
                      "Invalid GCP Length, GCP Length should be between 6-12 digits. Please check the provided GCP Length: %s",
                      gcpLength));
            }
          }
        });
  }

  // Public Method to validate the SGCN URN
  public final void validateURN(final String urn) throws ValidationException {
    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the SGCN DL URI
  public final void validateURI(final String dlURI, final int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }

  public final void validateClassLevelURN(final String urn) throws ValidationException {
    // Loop and check if URN matches the GS1 syntax
    for (Matcher m : URN_WITHOUT_SERIAL_MATCHERS) {
      m.validate(urn);
    }
  }

  // Public Method to validate the SGCN DL URI
  public final void validateClassLevelURI(final String dlURI, final int gcpLength)
      throws ValidationException {
    // Loop and check if DL URI matches the GS1 syntax
    for (Matcher m : URI_WITHOUT_SERIAL_MATCHERS) {
      m.validate(dlURI, gcpLength);
    }
  }
}
