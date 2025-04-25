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
package io.openepcis.identifiers.converter.constants;

import io.openepcis.constants.EPCIS;
import io.openepcis.identifiers.validator.exception.ValidationException;

import static io.openepcis.constants.EPCIS.GS1_CBV_DOMAIN;
import static io.openepcis.constants.EPCIS.GS1_VOC_DOMAIN;

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
