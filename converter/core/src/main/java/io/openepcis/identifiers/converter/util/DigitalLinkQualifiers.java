/*
 * Copyright (c) 2022-2026 benelog GmbH & Co. KG
 * All rights reserved.
 *
 * Unauthorized copying, modification, distribution,
 * or use of this work, via any medium, is strictly prohibited.
 *
 * benelog GmbH & Co. KG reserves all rights not expressly granted herein,
 * including the right to sell licenses for using this work.
 */
package io.openepcis.identifiers.converter.util;

/**
 * Reading and dropping single qualifier segments of a GTIN Digital Link.
 *
 * <p>The URI syntax allows {@code /01/{gtin}/22/{cpv}/10/{lot}/21/{ser}} in that order,
 * every qualifier optional. EPCS has no key that carries BOTH a lot and a serial: an
 * item is an SGTIN (GTIN + serial, {@code urn:epc:id:sgtin}), a batch an LGTIN (GTIN +
 * lot, {@code urn:epc:class:lgtin}). Converting a URI that names both therefore means
 * choosing the level — the INSTANCE conversion keeps the serial and treats the lot as
 * an attribute, the CLASS conversion keeps the lot and drops the serial — and these
 * helpers cut the other qualifier out of the URI so the per-key converters see the
 * plain form they were written for.
 */
public final class DigitalLinkQualifiers {

  private DigitalLinkQualifiers() {
  }

  /**
   * The value of the qualifier introduced by {@code prefix} (e.g. {@code "/10/"}): the
   * characters up to the next {@code /}, a {@code ?} or the end. {@code null} when the
   * URI does not carry that qualifier.
   */
  public static String segmentValue(final String dlURI, final String prefix) {
    if (dlURI == null) {
      return null;
    }
    final int at = dlURI.indexOf(prefix);
    if (at < 0) {
      return null;
    }
    final int start = at + prefix.length();
    int end = start;
    while (end < dlURI.length() && dlURI.charAt(end) != '/' && dlURI.charAt(end) != '?') {
      end++;
    }
    return end > start ? dlURI.substring(start, end) : null;
  }

  /**
   * {@code dlURI} without the qualifier introduced by {@code prefix} (prefix and value).
   * Unchanged when the qualifier is absent.
   */
  public static String withoutSegment(final String dlURI, final String prefix) {
    if (dlURI == null) {
      return null;
    }
    final int at = dlURI.indexOf(prefix);
    if (at < 0) {
      return dlURI;
    }
    int end = at + prefix.length();
    while (end < dlURI.length() && dlURI.charAt(end) != '/' && dlURI.charAt(end) != '?') {
      end++;
    }
    return dlURI.substring(0, at) + dlURI.substring(end);
  }
}
