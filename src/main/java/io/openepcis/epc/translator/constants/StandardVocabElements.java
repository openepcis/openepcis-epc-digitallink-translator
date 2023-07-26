/*
 * Copyright 2022-2023 benelog GmbH & Co. KG
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

import static io.openepcis.constants.EPCIS.GS1_CBV_DOMAIN;
import static io.openepcis.constants.EPCIS.GS1_VOC_DOMAIN;

import io.openepcis.constants.EPCIS;
import io.openepcis.epc.translator.exception.ValidationException;

public enum StandardVocabElements {
  BIZ_STEP(EPCIS.BIZ_STEP_URN_PREFIX, GS1_CBV_DOMAIN + EPCIS.BIZ_STEP_WEBURI_PREFIX),
  DISPOSITION(EPCIS.DISPOSITION_URN_PREFIX, GS1_CBV_DOMAIN + EPCIS.DISP_WEBURI_PREFIX),
  BIZ_TRANSACTION_TYPE(
      EPCIS.BIZ_TRANSACTION_URN_PREFIX, GS1_CBV_DOMAIN + EPCIS.BIZ_TRANSACTION_WEBURI_PREFIX),
  SOURCE_DEST_TYPE(EPCIS.SRC_DEST_URN_PREFIX, GS1_CBV_DOMAIN + EPCIS.SRC_DEST_WEBURI_PREFIX),
  ERROR_REASON(EPCIS.ERROR_REASON_URN_PREFIX, GS1_CBV_DOMAIN + EPCIS.ERROR_REASON_WEBURI_PREFIX),
  MEASUREMENT_TYPE(EPCIS.GS1_PREFIX, GS1_VOC_DOMAIN),
  ALERT_TYPE(EPCIS.GS1_PREFIX, GS1_VOC_DOMAIN);

  private final String urnPrefix;
  private final String dlPrefix;

  StandardVocabElements(final String urnPrefix, final String dlPrefix) {
    this.urnPrefix = urnPrefix;
    this.dlPrefix = dlPrefix;
  }

  public boolean supportsDL(String urn) {
    return urn.startsWith(this.urnPrefix);
  }

  // Check DL URI belongs to which standard vocab element
  public boolean supportsURN(String dlURI) {
    return dlURI.startsWith(this.dlPrefix);
  }

  // Convert to corresponding standard vocab element Digital Link URI
  public String convertToDigitalLink(String urn) throws ValidationException {
    final String payload = urn.substring(urnPrefix.length());
    return dlPrefix.concat(payload);
  }

  // Convert to corresponding standard vocab element URN
  public String convertToURN(String dlURI) throws ValidationException {
    final String payload = dlURI.substring(dlPrefix.length());
    return urnPrefix.concat(payload);
  }
}
