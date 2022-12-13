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
package io.openepcis.epc.translator.converter;

import io.openepcis.epc.translator.DefaultGCPLengthProvider;
import io.openepcis.epc.translator.constants.Constants;
import io.openepcis.epc.translator.exception.ValidationException;
import io.openepcis.epc.translator.validation.LGTINValidator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

public class LGTINConverter implements Converter {

  private static final String LGTIN_SERIAL_PART = "/10/";
  private static final String LGTIN_URI_PART = "/01/";
  private static final LGTINValidator LGTIN_VALIDATOR = new LGTINValidator();
  private static final String LGTIN_URN_PART = ":lgtin:";

  // Check if the provided URN is of LGTIN type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(LGTIN_URN_PART);
  }

  // Check if the provided Digital Link URI is of LGTIN Type
  public boolean supportsURN(final String dlURI) {
    return Pattern.compile("(?=.*/01/)(?=.*/10/)").matcher(dlURI).find();
  }

  // Convert the provided URN to respective Digital Link URI of LGTIN type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Call the Validator class for the LGTIN to check the URN syntax
      LGTIN_VALIDATOR.validateURN(urn);

      // If the URN passed the validation then convert the URN to URI
      final String gcp =
          urn.charAt(urn.indexOf('.') + 1)
              + urn.substring(
                  urn.indexOf(LGTIN_URN_PART) + LGTIN_URN_PART.length(), urn.indexOf('.'));
      String lgtin =
          gcp + urn.substring(urn.indexOf('.') + 2, urn.indexOf(".", urn.indexOf(".") + 1));
      lgtin = lgtin.substring(0, 13) + UPCEANLogicImpl.calcChecksum(lgtin.substring(0, 13));
      final String serialNumber = urn.substring(urn.indexOf(".", urn.indexOf(".") + 1) + 1);
      return Constants.GS1_IDENTIFIER_DOMAIN
          + LGTIN_URI_PART
          + lgtin
          + LGTIN_SERIAL_PART
          + serialNumber;
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of LGTIN identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of LGTIN Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Call the Validator class for the LGTIN to check the DLURI syntax
      LGTIN_VALIDATOR.validateURI(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      final String lgtin =
          dlURI.substring(
              dlURI.indexOf(LGTIN_URI_PART) + LGTIN_URI_PART.length(),
              dlURI.indexOf(LGTIN_SERIAL_PART));
      return getEPCMap(dlURI, gcpLength, lgtin);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of LGTIN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + " GCP Length : "
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(String dlURI, int gcpLength, String lgtin) {
    try {
      final Map<String, String> buildURN = new HashMap<>();
      final String serial =
          dlURI.substring(dlURI.indexOf(LGTIN_SERIAL_PART) + LGTIN_SERIAL_PART.length());
      final String asURN =
          "urn:epc:class:lgtin:"
              + lgtin.substring(1, gcpLength + 1)
              + "."
              + lgtin.charAt(0)
              + lgtin.substring(gcpLength + 1, lgtin.length() - 1)
              + "."
              + serial;

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(Constants.GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(Constants.CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(
                dlURI.substring(0, dlURI.indexOf(LGTIN_URI_PART)), Constants.GS1_IDENTIFIER_DOMAIN);
        buildURN.put(Constants.CANONICAL_DL, canonicalDL);
      }

      buildURN.put(Constants.AS_CAPTURED, dlURI);
      buildURN.put(Constants.AS_URN, asURN);
      buildURN.put("lgtin", lgtin);
      buildURN.put(Constants.SERIAL, serial);
      return buildURN;
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the LGTIN identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + " GCP Length : "
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of LGTIN Type
  public Map<String, String> convertToURN(String dlURI) throws ValidationException {
    try {
      final String lgtin =
          dlURI.substring(
              dlURI.indexOf(LGTIN_URI_PART) + LGTIN_URI_PART.length(),
              dlURI.indexOf(LGTIN_SERIAL_PART));
      final int gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(lgtin);

      // Call the Validator class for the LGTIN to check the DLURI syntax
      LGTIN_VALIDATOR.validateURI(dlURI, gcpLength);

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, lgtin);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of LGTIN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + "\n"
              + exception.getMessage());
    }
  }
}
