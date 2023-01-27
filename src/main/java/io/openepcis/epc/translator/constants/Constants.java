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
package io.openepcis.epc.translator.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
  public static final String GS1_IDENTIFIER_DOMAIN = "https://id.gs1.org";
  public static final String GS1_CBV_DOMAIN = "https://ref.gs1.org/cbv/";
  public static final String GS1_VOC_DOMAIN = "https://gs1.org/voc/";
  public static final String GS1_URN_CBV_PREFIX = "urn:epcglobal:cbv:";

  public static final String AS_CAPTURED = "asCaptured";
  public static final String CANONICAL_DL = "canonicalDL";
  public static final String AS_URN = "asURN";
  public static final String SERIAL = "serial";
  public static final String GCP_LENGTH = " GCP Length : ";
  public static final String WEBURI_FORMATTED = "WebURI";
}
