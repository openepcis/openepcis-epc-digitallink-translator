/*
 * Copyright 2022-2024 benelog GmbH & Co. KG
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

import static io.openepcis.constants.EPCIS.GS1_IDENTIFIER_DOMAIN;
import static io.openepcis.epc.translator.constants.ConstantDigitalLinkTranslatorInfo.*;

import io.openepcis.epc.translator.DefaultGCPLengthProvider;
import io.openepcis.epc.translator.exception.ValidationException;
import io.openepcis.epc.translator.util.ConverterUtil;
import io.openepcis.epc.translator.validation.GCNValidator;
import java.util.HashMap;
import java.util.Map;

public class GCNConverter implements Converter {

  private static final String GCN_URI_PART = "/255/";
  private static final GCNValidator GCN_VALIDATOR = new GCNValidator();
  private boolean isClassLevel;

  public GCNConverter() {}

  public GCNConverter(final boolean isClassLevel) {
    this.isClassLevel = isClassLevel;
  }

  // Check if the provided URN is of GCN type
  public boolean supportsDigitalLinkURI(final String urn) {
    return urn.contains(":sgcn:");
  }

  // Check if the provided Digital Link URI is of GCN Type
  public boolean supportsURN(final String dlURI) {
    return dlURI.contains(GCN_URI_PART);
  }

  // Convert the provided URN to respective Digital Link URI of GCN type
  public String convertToDigitalLink(final String urn) throws ValidationException {
    try {
      // Call the Validator class for the GCN to check the URN syntax
      if (isClassLevel) {
        GCN_VALIDATOR.validateClassLevelURN(urn);
      } else {
        GCN_VALIDATOR.validateURN(urn);
      }

      // If the URN passed the validation then convert the URN to URI
      final String gcp = urn.substring(urn.lastIndexOf(":") + 1, urn.indexOf("."));
      String sgcn =
          gcp + urn.substring(urn.indexOf('.') + 1, urn.indexOf(".", urn.indexOf(".") + 1));
      sgcn = sgcn.substring(0, 12) + ConverterUtil.checksum(sgcn.substring(0, 12));

      if (!isClassLevel) {
        sgcn = sgcn + urn.substring(urn.indexOf(".", urn.indexOf(".") + 1) + 1);
      }
      return GS1_IDENTIFIER_DOMAIN + GCN_URI_PART + sgcn;
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GCN identifier from URN to digital link WebURI,\nPlease check the provided identifier : "
              + urn
              + "\n"
              + exception.getMessage());
    }
  }

  // Convert the provided Digital Link URI to respective URN of GCN Type
  public Map<String, String> convertToURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    try {
      // Call the Validator class for the GCN to check the DLURI syntax
      if (isClassLevel) {
        GCN_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
      } else {
        GCN_VALIDATOR.validateURI(dlURI, gcpLength);
      }

      // If the URI passed the validation then convert the URI to URN
      String sgcn = dlURI.substring(dlURI.indexOf(GCN_URI_PART) + GCN_URI_PART.length());
      return getEPCMap(dlURI, gcpLength, sgcn);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GCN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }

  private Map<String, String> getEPCMap(String dlURI, int gcpLength, String sgcn) {
    final Map<String, String> buildURN = new HashMap<>();
    String asURN;

    try {
      String tempSgcn = sgcn.substring(0, 13);
      tempSgcn =
          tempSgcn.substring(0, gcpLength)
              + "."
              + tempSgcn.substring(gcpLength, tempSgcn.length() - 1);

      if (isClassLevel) {
        asURN = "urn:epc:idpat:sgcn:" + tempSgcn + ".*";
      } else {
        final String serial = sgcn.substring(13);
        asURN = "urn:epc:id:sgcn:" + tempSgcn + "." + serial;
        buildURN.put(SERIAL, serial);
      }

      // If dlURI contains GS1 domain then captured and canonical are same
      if (dlURI.contains(GS1_IDENTIFIER_DOMAIN)) {
        buildURN.put(CANONICAL_DL, dlURI);
      } else {
        // If dlURI does not contain GS1 domain then canonicalDL is based on GS1 domain
        final String canonicalDL =
            dlURI.replace(dlURI.substring(0, dlURI.indexOf(GCN_URI_PART)), GS1_IDENTIFIER_DOMAIN);
        buildURN.put(CANONICAL_DL, canonicalDL);
      }

      buildURN.put(AS_CAPTURED, dlURI);
      buildURN.put(AS_URN, asURN);
      buildURN.put("sgcn", sgcn);
    } catch (Exception exception) {
      throw new ValidationException(
          "The conversion of the GCN identifier from digital link WebURI to URN when creating the URN map encountered an error,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }

    // After generating the URN validate it again and ensure GCP validates
    if (isClassLevel) {
      GCN_VALIDATOR.validateClassLevelURN(asURN);
    } else {
      GCN_VALIDATOR.validateURN(asURN);
    }

    return buildURN;
  }

  // Convert the provided Digital Link URI to respective URN of GCN Type
  public Map<String, String> convertToURN(final String dlURI) throws ValidationException {
    int gcpLength = 0;

    try {
      final String sgcn = dlURI.substring(dlURI.indexOf(GCN_URI_PART) + GCN_URI_PART.length());
      gcpLength = DefaultGCPLengthProvider.getInstance().getGcpLength(dlURI, sgcn, GCN_URI_PART);

      // Call the Validator class for the GCN to check the DLURI syntax
      if (isClassLevel) {
        GCN_VALIDATOR.validateClassLevelURI(dlURI, gcpLength);
      } else {
        GCN_VALIDATOR.validateURI(dlURI, gcpLength);
      }
      // If the URI passed the validation then convert the URI to URN
      return getEPCMap(dlURI, gcpLength, sgcn);
    } catch (Exception exception) {
      throw new ValidationException(
          "Exception occurred during the conversion of GCN identifier from digital link WebURI to URN,\nPlease check the provided identifier : "
              + dlURI
              + GCP_LENGTH
              + gcpLength
              + "\n"
              + exception.getMessage());
    }
  }
}
