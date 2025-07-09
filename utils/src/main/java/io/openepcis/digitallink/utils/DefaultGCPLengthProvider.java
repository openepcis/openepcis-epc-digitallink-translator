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

import com.fasterxml.jackson.databind.JsonNode;
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

/**
 * Thread-safe singleton that resolves GS1 company-prefix lengths
 * from <code>gcpprefixformatlist.json</code>.
 */
@Slf4j
public final class DefaultGCPLengthProvider implements GCPLengthProvider {

  /* ------------------------------------------------------------------ *
   *  Static initialisation                                              *
   * ------------------------------------------------------------------ */

  private static final String RESOURCE = "/gcpprefixformatlist.json";
  private static final String NO_GCP_HINT =
          "Visit GEPIR (https://gepir.gs1.org/) or contact your GS1 MO.";

  /** Identifiers whose full value is already a GCP. */
  private static final Set<String> PREFIXES_WITH_GCP = Set.of(
          "/8010/", "/255/", "/253/", "/8004/", "/401/", "/402/",
          "/8018/", "/8017/", "/417/", "/414/"
  );

  /**
   * Immutable list sorted by <em>longest prefix first</em> for fast
   * longest-match scanning.
   */
  private static final List<Entry> PREFIX_ENTRIES;

  static {
    log.info("Loading {}", RESOURCE);
    PREFIX_ENTRIES = loadPrefixEntries();
    log.info("Loaded {} GCP prefixes", PREFIX_ENTRIES.size());
  }

  private static List<Entry> loadPrefixEntries() {
    try (InputStream in = Objects.requireNonNull(
            DefaultGCPLengthProvider.class.getResourceAsStream(RESOURCE),
            RESOURCE + " not found on classpath")) {

      final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
      final JsonNode root = mapper.readTree(in)
              .path("GCPPrefixFormatList")
              .path("entry");

      final List<Entry> list = new ArrayList<>(root.size());
      for (JsonNode n : root) {
        list.add(new Entry(
                n.get("prefix").asText(),
                n.get("gcpLength").asInt()
        ));
      }

      list.sort(Comparator
              .comparingInt((Entry e) -> e.prefix().length())
              .reversed()
              .thenComparing(Entry::prefix));

      return List.copyOf(list);            // make immutable
    } catch (IOException e) {
      log.error("Failed to read {}", RESOURCE, e);
      throw new UrnDLTransformationException("Cannot initialise GCP length map", e);
    }
  }

  /* ------------------------------------------------------------------ *
   *  Singleton boiler-plate                                             *
   * ------------------------------------------------------------------ */

  private static final DefaultGCPLengthProvider INSTANCE =
          new DefaultGCPLengthProvider();

  private DefaultGCPLengthProvider() {}     // prevent external instantiation

  public static DefaultGCPLengthProvider getInstance() {
    return INSTANCE;
  }

  /* ------------------------------------------------------------------ *
   *  Public API                                                         *
   * ------------------------------------------------------------------ */

  /**
   * Resolve the GCP length for a full Digital Link URI.
   *
   * @throws UnsupportedGS1IdentifierException if the URI is blank, a URN,
   *         or no matching GCP can be found and no default is configured
   */
  public int getGcpLength(final String gs1DigitalLinkURI) {
    if (StringUtils.isBlank(gs1DigitalLinkURI) ||
            gs1DigitalLinkURI.contains("urn:")) {
      throw new UnsupportedGS1IdentifierException(
              "GCP length not found for: " + gs1DigitalLinkURI + ". " + NO_GCP_HINT);
    }

    // pattern: /<digits>/…  or  …/<digits>/…
    final Pattern p = Pattern.compile("(/|^)(\\d+/|/\\d+/)([^/]+)");
    final Matcher m = p.matcher(gs1DigitalLinkURI);

    if (m.find()) {
      final String prefix = m.group(2).startsWith("/")
              ? m.group(2)
              : "/" + m.group(2);
      final String identifier = m.group(3);
      return getGcpLength(gs1DigitalLinkURI, identifier, prefix);
    }
    throw new UnsupportedGS1IdentifierException(
            "GCP length not found for: " + gs1DigitalLinkURI + ". " + NO_GCP_HINT);
  }

  /**
   * Core lookup that assumes the caller already split out the GS1 prefix.
   */
  public int getGcpLength(final String gs1DigitalLinkURI,
                          String identifier,
                          final String gs1IdentifierPrefix) {

    // GTINs: ignore first digit unless prefix itself embeds full GCP
    if (!PREFIXES_WITH_GCP.contains(gs1IdentifierPrefix) &&
            identifier.length() > 13) {
      identifier = identifier.substring(1);
    }

    for (Entry e : PREFIX_ENTRIES) {
      if (identifier.startsWith(e.prefix())) {
        return e.len();
      }
    }

    // optional JVM override: -Dio.openepcis...defaultGcpLength=7
    final String prop = System.getProperty(
            getClass().getName() + ".defaultGcpLength");
    if (prop != null) {
      try {
        return Integer.parseInt(prop);
      } catch (NumberFormatException nfe) {
        throw new IllegalArgumentException(
                "Invalid default GCP length value: " + prop, nfe);
      }
    }

    throw new UnsupportedGS1IdentifierException(
            "GCP length not found for Digital Link URI: " +
                    gs1DigitalLinkURI + ". " + NO_GCP_HINT);
  }

  /* ------------------------------------------------------------------ *
   *  Internal value object                                              *
   * ------------------------------------------------------------------ */

  private record Entry(String prefix, int len) {}
}
