/*
 * Copyright (c) 2022-2025 benelog GmbH & Co. KG
 * All rights reserved.
 *
 * Unauthorized copying, modification, distribution,
 * or use of this work, via any medium, is strictly prohibited.
 *
 * benelog GmbH & Co. KG reserves all rights not expressly granted herein,
 * including the right to sell licenses for using this work.
 */
package io.openepcis.identifiers.converter;

import io.openepcis.constants.EPCIS;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.openepcis.constants.EPCIS.WEBURI;

public class EventVocabularyFormatter implements VocabularyFormat {
  private static final List<String> URN_FORMATTED_CBV_STRING =
      Arrays.asList(
          EPCIS.BIZ_STEP_URN_PREFIX,
          EPCIS.DISPOSITION_URN_PREFIX,
          EPCIS.BIZ_TRANSACTION_URN_PREFIX,
          EPCIS.SRC_DEST_URN_PREFIX,
          EPCIS.ERROR_REASON_URN_PREFIX);
  private static final List<String> CURIE_FORMATTED_CBV_STRING =
      Arrays.asList(
          EPCIS.BIZ_STEP_CURIE_PREFIX,
          EPCIS.DISPOSITION_CURIE_PREFIX,
          EPCIS.BIZ_TRANSACTION_CURIE_PREFIX,
          EPCIS.SRC_DEST_CURIE_PREFIX,
          EPCIS.ERR_REASON_CURIE_PREFIX);
  private static final List<String> WEBURI_FORMATTED_CBV_STRING =
      Arrays.asList(
          EPCIS.BIZ_STEP_WEBURI_CBV_PREFIX,
          EPCIS.DISPOSITION_WEBURI_CBV_PREFIX,
          EPCIS.BIZ_TRANSACTION_WEBURI_CBV_PREFIX,
          EPCIS.SRC_DEST_WEBURI_CBV_PREFIX,
          EPCIS.ERR_REASON_WEBURI_CBV_PREFIX,
          EPCIS.BIZ_STEP_WEBURI_VOC_PREFIX,
          EPCIS.DISPOSITION_WEBURI_VOC_PREFIX,
          EPCIS.BIZ_TRANSACTION_WEBURI_VOC_PREFIX,
          EPCIS.SRC_DEST_WEBURI_VOC_PREFIX,
          EPCIS.ERR_REASON_WEBURI_VOC_PREFIX);

  private static final Map<String, String> CURIE_PREFIX_MAPPER = new HashMap<>();

  private static final Map<String, String> shortNameKeyIdentifier = new HashMap<>();
  private static final List<String> excludeSerial = List.of("/lot/", "/ser/", "/10/", "/21/");

  static {
    CURIE_PREFIX_MAPPER.put(EPCIS.BIZ_STEP.toLowerCase(), EPCIS.BIZ_STEP_CURIE_PREFIX);
    CURIE_PREFIX_MAPPER.put(EPCIS.DISPOSITION, EPCIS.DISPOSITION_CURIE_PREFIX);
    CURIE_PREFIX_MAPPER.put(
        EPCIS.PERSISTENT_DISPOSITION.toLowerCase(), EPCIS.DISPOSITION_CURIE_PREFIX);
    CURIE_PREFIX_MAPPER.put(
        EPCIS.BIZ_TRANSACTION_LIST.toLowerCase(), EPCIS.BIZ_TRANSACTION_CURIE_PREFIX);
    CURIE_PREFIX_MAPPER.put(
        EPCIS.BIZ_TRANSACTION.toLowerCase(), EPCIS.BIZ_TRANSACTION_CURIE_PREFIX);
    CURIE_PREFIX_MAPPER.put(EPCIS.SOURCE_LIST.toLowerCase(), EPCIS.SRC_DEST_CURIE_PREFIX);
    CURIE_PREFIX_MAPPER.put(EPCIS.DESTINATION_LIST.toLowerCase(), EPCIS.SRC_DEST_CURIE_PREFIX);
    CURIE_PREFIX_MAPPER.put(EPCIS.SOURCE, EPCIS.SRC_DEST_CURIE_PREFIX);
    CURIE_PREFIX_MAPPER.put(EPCIS.DESTINATION, EPCIS.SRC_DEST_CURIE_PREFIX);
    CURIE_PREFIX_MAPPER.put(EPCIS.ERROR_DECLARATION.toLowerCase(), EPCIS.ERR_REASON_CURIE_PREFIX);
    CURIE_PREFIX_MAPPER.put(EPCIS.REASON, EPCIS.ERR_REASON_CURIE_PREFIX);

    // Add the key value pair for the identifier
    shortNameKeyIdentifier.put("/gtin/", "/01/");
    shortNameKeyIdentifier.put("/itip/", "/8006/");
    shortNameKeyIdentifier.put("/cpi/", "/8010/");
    shortNameKeyIdentifier.put("/gln/", "/414/");
    shortNameKeyIdentifier.put("/party/", "/417/");
    shortNameKeyIdentifier.put("/gsrnp/", "/8017/");
    shortNameKeyIdentifier.put("/gsrn/", "/8018/");
    shortNameKeyIdentifier.put("/gcn/", "/255/");
    shortNameKeyIdentifier.put("/sscc/", "/00/");
    shortNameKeyIdentifier.put("/gdti/", "/253/");
    shortNameKeyIdentifier.put("/ginc/", "/401/");
    shortNameKeyIdentifier.put("/gsin/", "/402/");
    shortNameKeyIdentifier.put("/grai/", "/8003/");
    shortNameKeyIdentifier.put("/giai/", "/8004/");
    shortNameKeyIdentifier.put("/cpv/", "/22/");
    shortNameKeyIdentifier.put("/lot/", "/10/");
    shortNameKeyIdentifier.put("/ser/", "/21/");
  }

  // Method to convert the CBV URN formatted vocabularies into WebURI vocabulary. Used during event
  // hash generator.
  public String canonicalWebURIVocabulary(final String urnVocabulary) {

    String webURI;

    if (urnVocabulary.startsWith(EPCIS.BIZ_STEP_URN_PREFIX)) {
      // Business Step remove the urn:epcglobal:cbv:bizstep: and replace with
      // https://ref.gs1.org/cbv/BizStep-
      webURI = EPCIS.BIZ_STEP_WEBURI_CBV_PREFIX;

    } else if (urnVocabulary.startsWith(EPCIS.DISPOSITION_URN_PREFIX)) {
      // Disposition remove the urn:epcglobal:cbv:disp: and replace with
      // https://ref.gs1.org/cbv/Disp-
      webURI = EPCIS.DISPOSITION_WEBURI_CBV_PREFIX;

    } else if (urnVocabulary.startsWith(EPCIS.BIZ_TRANSACTION_URN_PREFIX)) {
      // BizTransaction type remove the urn:epcglobal:cbv:btt and replace with
      // https://ref.gs1.org/cbv/BTT-
      webURI = EPCIS.BIZ_TRANSACTION_WEBURI_CBV_PREFIX;

    } else if (urnVocabulary.startsWith(EPCIS.SRC_DEST_URN_PREFIX)) {
      // Source and Destination remove the urn:epcglobal:cbv:sdt: and replace with
      // https://ref.gs1.org/cbv/SDT-
      webURI = EPCIS.SRC_DEST_WEBURI_CBV_PREFIX;

    } else if (urnVocabulary.startsWith(EPCIS.ERROR_REASON_URN_PREFIX)) {
      // For ErrorDeclaration reason remove the urn:epcglobal:cbv:er: and replace with
      // https://ref.gs1.org/cbv/ER-
      webURI = EPCIS.ERR_REASON_WEBURI_CBV_PREFIX;
    } else {
      return urnVocabulary;
    }

    return webURI + urnVocabulary.substring(urnVocabulary.lastIndexOf(":") + 1);
  }

  // Method to convert the CBV WebURI formatted vocabularies into URN vocabulary. Used during
  // JSON/JSON-LD conversion to XML.
  public String canonicalString(final String webUriVocabulary) {

    if (startsWithIgnoreCase(webUriVocabulary, EPCIS.BIZ_STEP_WEBURI_CBV_PREFIX)) {
      return EPCIS.BIZ_STEP_URN_PREFIX
          + webUriVocabulary.substring(webUriVocabulary.lastIndexOf("-") + 1);

    } else if (startsWithIgnoreCase(webUriVocabulary, EPCIS.DISPOSITION_WEBURI_CBV_PREFIX)) {
      return EPCIS.DISPOSITION_URN_PREFIX
          + webUriVocabulary.substring(webUriVocabulary.lastIndexOf("-") + 1);

    } else if (startsWithIgnoreCase(webUriVocabulary, EPCIS.BIZ_TRANSACTION_WEBURI_CBV_PREFIX)) {
      return EPCIS.BIZ_TRANSACTION_URN_PREFIX
          + webUriVocabulary.substring(webUriVocabulary.lastIndexOf("-") + 1);

    } else if (startsWithIgnoreCase(webUriVocabulary, EPCIS.SRC_DEST_WEBURI_CBV_PREFIX)) {
      return EPCIS.SRC_DEST_URN_PREFIX
          + webUriVocabulary.substring(webUriVocabulary.lastIndexOf("-") + 1);

    } else if (startsWithIgnoreCase(webUriVocabulary, EPCIS.ERR_REASON_WEBURI_CBV_PREFIX)) {
      return EPCIS.ERROR_REASON_URN_PREFIX
          + webUriVocabulary.substring(webUriVocabulary.lastIndexOf("-") + 1);
    }

    return webUriVocabulary;
  }

  // Method to convert the CBV URN/WebURI formatted vocabularies into BareString vocabulary. Used
  // during XML -> JSON/JSON-LD conversion.
  public String bareString(final String cbvVocabulary) {
    if (URN_FORMATTED_CBV_STRING.stream().anyMatch(cbvVocabulary::startsWith)) {
      return cbvVocabulary.substring(cbvVocabulary.lastIndexOf(":") + 1);
    }
    // Case-insensitive matching for WebURI/CURIE prefixes to handle JAXB/MOXy case normalization
    if (WEBURI_FORMATTED_CBV_STRING.stream().anyMatch(p -> startsWithIgnoreCase(cbvVocabulary, p))) {
      return cbvVocabulary.substring(cbvVocabulary.lastIndexOf("-") + 1);
    } else if (CURIE_FORMATTED_CBV_STRING.stream().anyMatch(p -> startsWithIgnoreCase(cbvVocabulary, p))) {
      return cbvVocabulary.substring(cbvVocabulary.lastIndexOf("-") + 1);
    }
    return cbvVocabulary;
  }

  // Method to convert the BareString vocabularies into CBV formatted URN/WebURI vocabulary. Used
  // during JSON/JSON-LD -> XML conversion.
  public String toCbvVocabulary(
      final String bareString, final String fieldName, final String format) {
    String prefix;
    String bareStringValue = curieStringFinder(fieldName, bareString);

    // Check for the fieldName and based on that return the respective CBV formatted vocabulary in
    // either WebURI or URN format.
    switch (fieldName.toLowerCase()) {
      case "bizstep":
        // Convert BareString bizStep into CBV formatted URN/WebURI bizStep
        prefix =
            format.equalsIgnoreCase(WEBURI)
                ? EPCIS.BIZ_STEP_WEBURI_CBV_PREFIX
                : EPCIS.BIZ_STEP_URN_PREFIX;
        break;
      case "disposition", "persistentdisposition":
        // Convert BareString Disposition/PersistentDisposition into CBV formatted URN/WebURI
        prefix =
            format.equalsIgnoreCase(WEBURI)
                ? EPCIS.DISPOSITION_WEBURI_CBV_PREFIX
                : EPCIS.DISPOSITION_URN_PREFIX;
        break;
      case "biztransactionlist", "biztransaction":
        // Convert BareString bizTransaction type into CBV formatted URN/WebURI bizTransaction
        prefix =
            format.equalsIgnoreCase(WEBURI)
                ? EPCIS.BIZ_TRANSACTION_WEBURI_CBV_PREFIX
                : EPCIS.BIZ_TRANSACTION_URN_PREFIX;
        break;
      case "sourcelist", "destinationlist", "source", "destination":
        // Convert BareString Source/Destination type into CBV formatted URN/WebURI
        // Source/Destination
        prefix =
            format.equalsIgnoreCase(WEBURI)
                ? EPCIS.SRC_DEST_WEBURI_CBV_PREFIX
                : EPCIS.SRC_DEST_URN_PREFIX;
        break;
      case "errordeclaration", "reason":
        // Convert BareString ErrorDeclaration Reason into CBV formatted URN/WebURI ErrorDeclaration
        // Reason
        prefix =
            format.equalsIgnoreCase(WEBURI)
                ? EPCIS.ERR_REASON_WEBURI_CBV_PREFIX
                : EPCIS.ERROR_REASON_URN_PREFIX;
        break;
      default:
        return bareString;
    }

    // If bareString value contains any of the GS1 standard character then return the same if not
    // send transformed value with prefix.
    return bareStringValue.contains(":") || bareStringValue.contains("/")
        ? bareStringValue
        : prefix + bareStringValue;
  }

  // Method to check if the value matches any of the curie string if so format according to curie
  // string
  private String curieStringFinder(final String fieldName, String fieldValue) {

    String prefix = CURIE_PREFIX_MAPPER.get(fieldName.toLowerCase());

    if (prefix == null) {
      return fieldValue;
    }

    return fieldValue.contains(prefix) ? fieldValue.substring(prefix.length()) : fieldValue;
  }

  private static boolean startsWithIgnoreCase(String value, String prefix) {
    return value.regionMatches(true, 0, prefix, 0, prefix.length());
  }

  // Method to replace the domain and key with respective identifier during preHash generation
  public final String shortNameReplacer(String gs1Identifier) {
    // If the identifier contains any key from hash map replace with key extension number
    for (Map.Entry<String, String> entry : shortNameKeyIdentifier.entrySet()) {
      if (gs1Identifier.contains(entry.getKey())) {
        if (!excludeSerial.contains(entry.getKey())) {
          gs1Identifier =
              gs1Identifier.replace(
                  gs1Identifier.substring(0, gs1Identifier.indexOf(entry.getKey())),
                  EPCIS.GS1_IDENTIFIER_DOMAIN);
        }
        gs1Identifier = gs1Identifier.replace(entry.getKey(), entry.getValue());
      } else if (gs1Identifier.contains(entry.getValue())
          && (!excludeSerial.contains(entry.getValue()))
          && !gs1Identifier.startsWith(EPCIS.GS1_IDENTIFIER_DOMAIN)) {
        // If the identifier key is already present then only replace the domain to GS1
        gs1Identifier =
            gs1Identifier.replace(
                gs1Identifier.substring(0, gs1Identifier.indexOf(entry.getValue())),
                EPCIS.GS1_IDENTIFIER_DOMAIN);
      }
    }
    return gs1Identifier;
  }
}
