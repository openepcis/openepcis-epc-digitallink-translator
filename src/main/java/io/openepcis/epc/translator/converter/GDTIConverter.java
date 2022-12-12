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
import io.openepcis.epc.translator.validation.GDTIValidator;
import java.util.HashMap;
import java.util.Map;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

public class GDTIConverter implements Converter {

  private static final String GDTI_URI_PART = "/253/";
  private static final String GDTI_URN_PART = "gdti:";
  private static final GDTIValidator GDTI_VALIDATOR = new GDTIValidator();
  private boolean isClassLevel;

  public GDTIConverter() {}

  public GDTIConverter(boolean isClassLevel) {
    this.isClassLevel = isClassLevel;
  }

  // Check if the provided URN is of GDTI type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(":gdti:");
  }

  // Check if the provided Digital Link URI is of GDTI Type
  public boolean supportsURN(final String dlURI) {
    return dlURI.contains(GDTI_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of GDTI type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {

      // Call the Validator class for the GDTI to check the URN syntax
      if (isClassLevel) {
        GDTI_VALIDATOR.validateClassLevelURN(urn);
      } else {
        GDTI_VALIDATOR.validateURN(urn);
      }

      // If the URN passed the validation then convert the URN to URI
      final String gcp =
          urn.substring(urn.indexOf(GDTI_URN_PART) + GDTI_URN_PART.length(), urn.indexOf('.'));
      String gdti =
          gcp + urn.substring(urn.indexOf('.') + 1, urn.indexOf(".", urn.indexOf(".") + 1));
      gdti = gdti.substring(0, 12) + UPCEANLogicImpl.calcChecksum(gdti.substring(0, 12));

      if (!isClassLevel) {
        gdti = gdti + urn.substring(urn.indexOf(".", urn.indexOf(".") + 1) + 1);
      }
      return Constants.IDENTIFIERDOMAIN + GDTI_URI_PART + gdti;
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GDTI identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of GDTI Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Call the Validator class for the GDTI to check the DLURI syntax
      if (isClassLevel) {
        GDTI_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
      } else {
        GDTI_VALIDATOR.validateURI(dlURI, gcpLength);
      }

      // If the URI passed the validation then convert the URI to URN

      final String gdti =
          dlURI.substring(
              dlURI.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length(),
              dlURI.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length() + 13);

      return getEPCMap(dlURI, gcpLength, gdti);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GDTI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + " GCP Length : "
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(
      final String dlURI, final int gcpLength, final String gdti) {
    try {
      final Map<String, String> buildURN = new HashMap<>();
      String asURN;
      final String gdtiSubString = gdti.substring(gcpLength, gdti.length() - 1);

      if (isClassLevel) {
        asURN = "urn:epc:idpat:gdti:" + gdti.substring(0, gcpLength) + "." + gdtiSubString + ".*";
      } else {
        final String serial =
            dlURI.substring(dlURI.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length() + 13);
        asURN =
            "urn:epc:id:gdti:" + gdti.substring(0, gcpLength) + "." + gdtiSubString + "." + serial;
        buildURN.put(Constants.SERIAL, serial);
      }

      if (dlURI.contains(Constants.IDENTIFIERDOMAIN)) {
        final String asCaptured =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(GDTI_URI_PART)), Constants.DLDOMAIN);
        buildURN.put(Constants.ASCAPTURED, asCaptured);
        buildURN.put(Constants.CANONICALDL, dlURI);
      } else {
        final String canonicalDL =
            dlURI.replace(
                dlURI.substring(0, dlURI.indexOf(GDTI_URI_PART)), Constants.IDENTIFIERDOMAIN);
        buildURN.put(Constants.ASCAPTURED, dlURI);
        buildURN.put(Constants.CANONICALDL, canonicalDL);
      }
      buildURN.put(Constants.ASURN, asURN);
      buildURN.put("gdti", gdti);
      return buildURN;
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the GDTI identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + " GCP Length : "
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of GDTI Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    try {
      String gdti;

      if (isClassLevel) {
        gdti = dlURI.substring(dlURI.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length());
      } else {
        gdti =
            dlURI.substring(
                dlURI.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length(),
                dlURI.indexOf(GDTI_URI_PART) + GDTI_URI_PART.length() + 13);
      }

      final int gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(gdti);

      // Call the Validator class for the GDTI to check the DLURI syntax
      if (isClassLevel) {
        GDTI_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
      } else {
        GDTI_VALIDATOR.validateURI(dlURI, gcpLength);
      }

      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, gdti);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GDTI identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + "\n"
              + exception.getMessage());
    }
  }
}
