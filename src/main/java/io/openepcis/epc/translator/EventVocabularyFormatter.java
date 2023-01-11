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
package io.openepcis.epc.translator;

import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public class EventVocabularyFormatter {

  private static final String WEB_URI_PREFIX = "https://ref.gs1.org/";
  private static final String URN_PREFIX = "urn:epcglobal:cbv:";
  private static final String WEBURI_FORMATTED = "WebURI";
  private static final String BIZ_STEP_URN_PREFIX = URN_PREFIX + "bizstep:";
  private static final String DISPOSITION_URN_PREFIX = URN_PREFIX + "disp:";
  private static final String BIZ_TRANSACTION_URN_PREFIX = URN_PREFIX + "btt:";
  private static final String SRC_DEST_URN_PREFIX = URN_PREFIX + "sdt:";
  private static final String ERR_REASON_URN_PREFIX = URN_PREFIX + "er:";
  private static final String BIZ_STEP_WEB_URI_PREFIX = WEB_URI_PREFIX + "cbv/Bizstep-";
  private static final String DISPOSITION_WEB_URI_PREFIX = WEB_URI_PREFIX + "cbv/Disp-";
  private static final String BIZ_TRANSACTION_WEB_URI_PREFIX = WEB_URI_PREFIX + "cbv/BTT-";
  private static final String SRC_DEST_WEB_URI_PREFIX = WEB_URI_PREFIX + "cbv/SDT-";
  private static final String ERR_REASON_WEB_URI_PREFIX = WEB_URI_PREFIX + "cbv/ER-";
  private static final List<String> URN_FORMATTED_CBV_STRING =
      Arrays.asList(
          BIZ_STEP_URN_PREFIX,
          DISPOSITION_URN_PREFIX,
          BIZ_TRANSACTION_URN_PREFIX,
          SRC_DEST_URN_PREFIX,
          ERR_REASON_URN_PREFIX);
  private static final List<String> WEBURI_FORMATTED_CBV_STRING =
      Arrays.asList(
          BIZ_STEP_WEB_URI_PREFIX,
          DISPOSITION_WEB_URI_PREFIX,
          BIZ_TRANSACTION_WEB_URI_PREFIX,
          SRC_DEST_WEB_URI_PREFIX,
          ERR_REASON_WEB_URI_PREFIX);

  // Method to convert the CBV URN formatted vocabularies into WebURI vocabulary. Used during event
  // hash generator.
  public static String canonicalWebURIVocabulary(final String urnVocabulary) {

    if (urnVocabulary.startsWith(BIZ_STEP_URN_PREFIX)) {
      // For Business Step remove the urn:epcglobal:cbv:bizstep: and replace with
      // https://ns.gs1.org/voc/Bizstep-
      return BIZ_STEP_WEB_URI_PREFIX + urnVocabulary.substring(urnVocabulary.lastIndexOf(":") + 1);

    } else if (urnVocabulary.startsWith(DISPOSITION_URN_PREFIX)) {
      // For Disposition remove the urn:epcglobal:cbv:disp: and replace with
      // https://ns.gs1.org/voc/Disp-
      return DISPOSITION_WEB_URI_PREFIX
          + urnVocabulary.substring(urnVocabulary.lastIndexOf(":") + 1);

    } else if (urnVocabulary.startsWith(BIZ_TRANSACTION_URN_PREFIX)) {
      // For BizTransaction type remove the urn:epcglobal:cbv:btt and replace with
      // https://ns.gs1.org/voc/BTT-
      return BIZ_TRANSACTION_WEB_URI_PREFIX
          + urnVocabulary.substring(urnVocabulary.lastIndexOf(":") + 1);

    } else if (urnVocabulary.startsWith(SRC_DEST_URN_PREFIX)) {
      // For Source and Destination remove the urn:epcglobal:cbv:sdt: and replace with
      // https://ns.gs1.org/voc/SDT-
      return SRC_DEST_WEB_URI_PREFIX + urnVocabulary.substring(urnVocabulary.lastIndexOf(":") + 1);

    } else if (urnVocabulary.startsWith(ERR_REASON_URN_PREFIX)) {
      // For ErrorDeclaration reason remove the urn:epcglobal:cbv:er: and replace with
      // https://ns.gs1.org/voc/ER-
      return ERR_REASON_WEB_URI_PREFIX
          + urnVocabulary.substring(urnVocabulary.lastIndexOf(":") + 1);
    }
    return urnVocabulary;
  }

  // Method to convert the CBV WebURI formatted vocabularies into URN vocabulary. Used during
  // JSON/JSON-LD conversion to XML.
  public static String canonicalString(final String webUriVocabulary) {

    if (webUriVocabulary.startsWith(BIZ_STEP_WEB_URI_PREFIX)) {
      // For Business Step remove the https://ns.gs1.org/voc/Bizstep- and replace with
      // urn:epcglobal:cbv:bizstep:
      return BIZ_STEP_URN_PREFIX
          + webUriVocabulary.substring(webUriVocabulary.lastIndexOf("-") + 1);

    } else if (webUriVocabulary.startsWith(DISPOSITION_WEB_URI_PREFIX)) {
      // For Disposition remove https://ns.gs1.org/voc/Disp- and replace with
      // urn:epcglobal:cbv:disp:
      return DISPOSITION_URN_PREFIX
          + webUriVocabulary.substring(webUriVocabulary.lastIndexOf("-") + 1);

    } else if (webUriVocabulary.startsWith(BIZ_TRANSACTION_WEB_URI_PREFIX)) {
      // For Business Transaction remove https://ns.gs1.org/voc/BTT- and replace with
      // urn:epcglobal:cbv:btt:
      return BIZ_TRANSACTION_URN_PREFIX
          + webUriVocabulary.substring(webUriVocabulary.lastIndexOf("-") + 1);

    } else if (webUriVocabulary.startsWith(SRC_DEST_WEB_URI_PREFIX)) {
      // For Source/Destination remove prefix https://ns.gs1.org/voc/SDT- and replace with
      // urn:epcglobal:cbv:sdt:
      return SRC_DEST_URN_PREFIX
          + webUriVocabulary.substring(webUriVocabulary.lastIndexOf("-") + 1);

    } else if (webUriVocabulary.startsWith(ERR_REASON_WEB_URI_PREFIX)) {
      // For Error Reason remove https://ns.gs1.org/voc/ER- and replace with urn:epcglobal:cbv:er:
      return ERR_REASON_URN_PREFIX
          + webUriVocabulary.substring(webUriVocabulary.lastIndexOf("-") + 1);
    }

    return webUriVocabulary;
  }

  // Method to convert the CBV URN/WebURI formatted vocabularies into BareString vocabulary. Used
  // during XML -> JSON/JSON-LD conversion.
  public static String bareString(final String cbvVocabulary) {
    if (URN_FORMATTED_CBV_STRING.stream().anyMatch(cbvVocabulary::startsWith)) {
      // Check if the CBV URN Vocabulary matches any of the pre-defined CBV URN string if so remove
      // the URN prefix and return bare string.
      return cbvVocabulary.substring(cbvVocabulary.lastIndexOf(":") + 1);

    } else if (WEBURI_FORMATTED_CBV_STRING.stream().anyMatch(cbvVocabulary::startsWith)) {
      // Check if the CBV WebURI vocabulary matches any of the pre-defined CBV WebURI string if so
      // remove the WebURI prefix and return bare string.
      return cbvVocabulary.substring(cbvVocabulary.lastIndexOf("-") + 1);
    }
    return cbvVocabulary;
  }

  // Method to convert the BareString vocabularies into CBV formatted URN/WebURI vocabulary. Used
  // during JSON/JSON-LD -> XML conversion.
  public static String cbvVocabulary(
      final String bareString, final String fieldName, final String format) {
    // Check for the fieldName and based on that return the respective CBV formatted vocabulary in
    // either WebURI or URN format.
    if (fieldName.equals("bizStep")) {
      // Convert BareString bizStep into CBV formatted URN/WebURI bizStep
      return format.equalsIgnoreCase(WEBURI_FORMATTED)
          ? BIZ_STEP_WEB_URI_PREFIX + bareString
          : BIZ_STEP_URN_PREFIX + bareString;

    } else if (fieldName.equalsIgnoreCase("disposition")
        || fieldName.equalsIgnoreCase("persistentDisposition")) {
      // Convert BareString Disposition/PersistentDisposition into CBV formatted URN/WebURI
      // Disposition/PersistentDisposition
      return format.equalsIgnoreCase(WEBURI_FORMATTED)
          ? DISPOSITION_WEB_URI_PREFIX + bareString
          : DISPOSITION_URN_PREFIX + bareString;

    } else if (fieldName.equalsIgnoreCase("bizTransactionList")
        || fieldName.equalsIgnoreCase("bizTransaction")) {
      // Convert BareString bizTransaction type into CBV formatted URN/WebURI bizTransaction
      return format.equalsIgnoreCase(WEBURI_FORMATTED)
          ? BIZ_TRANSACTION_WEB_URI_PREFIX + bareString
          : BIZ_TRANSACTION_URN_PREFIX + bareString;

    } else if (fieldName.equalsIgnoreCase("sourceList")
        || fieldName.equalsIgnoreCase("destinationList")
        || fieldName.equalsIgnoreCase("source")
        || fieldName.equalsIgnoreCase("destination")) {
      // Convert BareString Source/Destination type into CBV formatted URN/WebURI Source/Destination
      return format.equalsIgnoreCase(WEBURI_FORMATTED)
          ? SRC_DEST_WEB_URI_PREFIX + bareString
          : SRC_DEST_URN_PREFIX + bareString;

    } else if (fieldName.equalsIgnoreCase("errorDeclaration")
        || fieldName.equalsIgnoreCase("reason")) {
      // Convert BareString ErrorDeclaration Reason into CBV formatted URN/WebURI ErrorDeclaration
      // Reason
      return format.equalsIgnoreCase(WEBURI_FORMATTED)
          ? ERR_REASON_WEB_URI_PREFIX + bareString
          : ERR_REASON_URN_PREFIX + bareString;
    }

    return bareString;
  }
}
