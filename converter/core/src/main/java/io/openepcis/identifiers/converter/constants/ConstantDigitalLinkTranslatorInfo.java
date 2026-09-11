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
package io.openepcis.identifiers.converter.constants;

public class ConstantDigitalLinkTranslatorInfo {
  public static final String AS_CAPTURED = "asCaptured";
  public static final String CANONICAL_DL = "canonicalDL";
  public static final String AS_URN = "asURN";
  public static final String SERIAL = "serial";
  /**
   * The lot (AI 10) a converted Digital Link carried. On an SGTIN result it is the
   * instance's lot ATTRIBUTE (the EPC has no place for it); on an LGTIN result it is
   * the class's own lot — the same value {@link #SERIAL} holds there for historical
   * reasons.
   */
  public static final String LOT = "lot";
  /**
   * The serial (AI 21) a Digital Link carried that was converted at CLASS level (to an
   * LGTIN): the serial is dropped from the EPC and reported here so a caller can see
   * that the URI named an instance.
   */
  public static final String SERIAL_NUMBER = "serialNumber";
  /**
   * The consumer product variant (AI 22) a Digital Link carried. No EPC carries a CPV
   * (CBV 2.0 §8): it is cut out of the URI before the SGTIN/LGTIN rules read it and
   * reported here as the attribute {@code gs1:consumerProductVariant} of the class or
   * instance the EPC names.
   */
  public static final String CPV = "cpv";
  public static final String GCP_LENGTH = " GCP Length : ";

  private ConstantDigitalLinkTranslatorInfo() {
  }
}
