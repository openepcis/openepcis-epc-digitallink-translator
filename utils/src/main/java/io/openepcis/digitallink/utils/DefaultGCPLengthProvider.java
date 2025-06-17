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
package io.openepcis.digitallink.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.openepcis.core.exception.UnsupportedGS1IdentifierException;
import io.openepcis.core.exception.UrnDLTransformationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DefaultGCPLengthProvider implements GCPLengthProvider {
  private final Map<String, Integer> sortedGcpLengthList =
      new TreeMap<>(
          Collections.reverseOrder(
              (a, b) ->
                  a.length() != b.length()
                      ? Integer.compare(a.length(), b.length())
                      : a.compareTo(b)));

  private static DefaultGCPLengthProvider gcpLengthProviderInstance;
  private final List<String> keyStartsWithGCP = Arrays.asList("/8010/", "/255/", "/253/", "/8004/", "/401/", "/402/", "/8018/", "/8017/", "/417/", "/414/");
  private static final String NO_GCP_EXCEPTION_MESSAGE = "Visit GEPIR (https://gepir.gs1.org/) or contact GS1 MO.";

  /** Constructor to load the GCPLengthFormat file from resource folder to the sorted TreeMap */
  public DefaultGCPLengthProvider() {
    try {
      // Read the GCP Length file with JSON content
      final InputStream gcpPrefixFileContents = getClass().getResourceAsStream("/gcpprefixformatlist.json");

      // Deserialize the JSON contents to Map using Jackson ObjectMapper
      final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
      @SuppressWarnings("unchecked")
      final Map<String, Object> gcpPrefixFormatList = objectMapper.readValue(gcpPrefixFileContents, Map.class);

      // Read the ArrayList "entry" contents with GCPLength values
      @SuppressWarnings("unchecked")
      final List<Map<String, Object>> list = (List<Map<String, Object>>) ((Map<String, Object>) gcpPrefixFormatList.get("GCPPrefixFormatList")).get("entry");

      // Loop over the list and convert the values to string and integer and add to sorted list
      for (Map<String, Object> m : list) {
        sortedGcpLengthList.put(m.get("prefix").toString(), Integer.valueOf(m.get("gcpLength").toString()));
      }
    } catch (IOException e) {
      throw new UrnDLTransformationException(e.getLocalizedMessage(), e);
    }
  }

  /**
   * Method to loop over the Map to find the matching id and its associated GCP Length
   *
   * @param gs1DigitalLinkURI The digital link WebURI for which the GCP length needed
   *     ex:https://id.gs1.org/01/12345678901231/21/9999
   * @param gs1DigitalLinkURIIdentifier The identifier after stripping all GS1 standard prefixes'
   *     ex: 12345678901231
   * @return returns the GCP length if found matching value in Map else returns 7 as GCP Length
   */
  public int getGcpLength(final String gs1DigitalLinkURI, String gs1DigitalLinkURIIdentifier, final String gs1IdentifierPrefix) {
    // Check if identifier is not matching with keyStarts-map elements
    if (!keyStartsWithGCP.contains(gs1IdentifierPrefix) && gs1DigitalLinkURIIdentifier.length() > 13) {
      // For GTIN related identifiers consider from 2nd digit for finding gcp length
      gs1DigitalLinkURIIdentifier = gs1DigitalLinkURIIdentifier.substring(1);
    }

    // Make gs1DigitalLinkURIIdentifier effectively final by re-assigning to a new final variable
    final String finalGs1DigitalLinkURIIdentifier = gs1DigitalLinkURIIdentifier;

    // Loop over the sorted values to find the matching GCP and its GCP Length else default to 0
    int gcpLength =
        sortedGcpLengthList.entrySet().stream()
            .filter(entry -> finalGs1DigitalLinkURIIdentifier.startsWith(entry.getKey()))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(0);

    // Return the found GCP length if not 0 else fallback to default if set or throw exception
    if (gcpLength != 0) {
      return gcpLength;
    } else {
      // Retrieve default GCP Length from system property
      final String gcpLengthStr = System.getProperty(getClass().getName() + ".defaultGcpLength", null);
      if (gcpLengthStr != null) {
        try {
          return Integer.parseInt(gcpLengthStr);
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("Invalid default GCP length value: " + gcpLengthStr, e);
        }
      }
      throw new UnsupportedGS1IdentifierException("GCP Length not found for Digital link URI : " + gs1DigitalLinkURI + NO_GCP_EXCEPTION_MESSAGE);
    }
  }

  // Method to get the GCP Length from provided GS1 Digital Link URI by auto-detecting the prefix
  // and identifier
  public int getGcpLength(final String gs1DigitalLinkURI) {
    // If identifier is empty/null or provided identifier is URN then throw exception
    if (StringUtils.isBlank(gs1DigitalLinkURI) || gs1DigitalLinkURI.contains("urn:")) {
      throw new UnsupportedGS1IdentifierException("GCP Length not found for : " + gs1DigitalLinkURI + NO_GCP_EXCEPTION_MESSAGE);
    }

    // Regular expression to find digits followed by / or / followed by digits followed by /,
    // ignoring port numbers
    final Pattern pattern = Pattern.compile("(/|^)(\\d+/|/\\d+/)([^/]+)");
    final Matcher matcher = pattern.matcher(gs1DigitalLinkURI);

    // Check if the prefix associated with identifier is present
    if (matcher.find()) {
      final String gs1IdentifierPrefix = matcher.group(2).startsWith("/") ? matcher.group(2) : "/" + matcher.group(2);
      final String gs1DigitalLinkURIIdentifier = matcher.group(3);
      return getGcpLength(gs1DigitalLinkURI, gs1DigitalLinkURIIdentifier, gs1IdentifierPrefix);
    } else {
      // if no prefix is found then throw exception
      throw new UnsupportedGS1IdentifierException("GCP Length not found for : " + gs1DigitalLinkURI + NO_GCP_EXCEPTION_MESSAGE);
    }
  }

  /**
   * Static method to create instance of Singleton class
   *
   * @return returns the instance of DefaultGCPLengthProvider class if its null
   */
  public static DefaultGCPLengthProvider getInstance() {
    if (gcpLengthProviderInstance == null) {
      gcpLengthProviderInstance = new DefaultGCPLengthProvider();
    }
    return gcpLengthProviderInstance;
  }

  /**
   * Method to provide the instance of the class
   *
   * @return returns the instance of the DefaultGCPLengthProvider class
   */
  public DefaultGCPLengthProvider getDefaultInstance() {
    return new DefaultGCPLengthProvider();
  }
}
