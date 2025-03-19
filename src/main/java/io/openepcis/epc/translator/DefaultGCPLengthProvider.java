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
package io.openepcis.epc.translator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.openepcis.epc.translator.exception.UnsupportedGS1IdentifierException;
import io.openepcis.epc.translator.exception.UrnDLTransformationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import lombok.extern.slf4j.Slf4j;

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

  private final List<String> keyStartsWithGCP =
      Arrays.asList(
          "/8010/", "/255/", "/253/", "/8004/", "/401/", "/402/", "/8018/", "/8017/", "/417/",
          "/414/");

  /** Constructor to load the GCPLengthFormat file from resource folder to the sorted TreeMap */
  private DefaultGCPLengthProvider() {
    try {
      // Read the GCP Length file with JSON content
      final InputStream gcpPrefixFileContents =
          getClass().getResourceAsStream("/gcpprefixformatlist.json");

      // Deserialize the JSON contents to Map using Jackson ObjectMapper
      final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
      @SuppressWarnings("unchecked")
      final Map<String, Object> gcpPrefixFormatList =
          objectMapper.readValue(gcpPrefixFileContents, Map.class);

      // Read the ArrayList "entry" contents with GCPLength values
      @SuppressWarnings("unchecked")
      final List<Map<String, Object>> list =
          (List<Map<String, Object>>)
              ((Map<String, Object>) gcpPrefixFormatList.get("GCPPrefixFormatList")).get("entry");

      // Loop over the list and convert the values to string and integer and add to sorted list
      for (Map<String, Object> m : list) {
        sortedGcpLengthList.put(
            m.get("prefix").toString(), Integer.valueOf(m.get("gcpLength").toString()));
      }
    } catch (IOException e) {
      log.error("GCPPrefixFormatList cannot be retrieved", e);
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
  public int getGcpLength(
      final String gs1DigitalLinkURI,
      String gs1DigitalLinkURIIdentifier,
      final String gs1IdentifierPrefix) {
    // Check if identifier is not matching with keyStarts-map elements
    if (!keyStartsWithGCP.contains(gs1IdentifierPrefix)
        && gs1DigitalLinkURIIdentifier.length() > 13) {
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
      final String gcpLengthStr =
          System.getProperty(getClass().getName() + ".defaultGcpLength", null);
      if (gcpLengthStr != null) {
        try {
          return Integer.parseInt(gcpLengthStr);
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException(
              "Invalid default GCP length value: " + gcpLengthStr, e);
        }
      }
      throw new UnsupportedGS1IdentifierException(
          "GCP Length not found for Digital link URI : "
              + gs1DigitalLinkURI
              + ". \nVisit GEPIR (https://gepir.gs1.org/) or contact GS1 MO.");
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
