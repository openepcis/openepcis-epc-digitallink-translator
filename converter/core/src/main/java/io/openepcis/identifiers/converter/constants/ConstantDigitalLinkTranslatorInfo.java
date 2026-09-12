/*
 * Copyright 2022-2026 benelog GmbH & Co. KG
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
