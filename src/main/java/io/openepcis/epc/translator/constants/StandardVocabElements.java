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

import io.openepcis.epc.translator.exception.ValidationException;

public enum StandardVocabElements {
  BIZ_STEP("urn:epcglobal:cbv:bizstep:", Constants.GS1_CBV_DOMAIN + "Bizstep-"),
  DISPOSITION("urn:epcglobal:cbv:disp:", Constants.GS1_CBV_DOMAIN + "Disp-"),
  BIZ_TRANSACTION_TYPE("urn:epcglobal:cbv:btt:", Constants.GS1_CBV_DOMAIN + "BTT-"),
  SOURCE_DEST_TYPE("urn:epcglobal:cbv:sdt:", Constants.GS1_CBV_DOMAIN + "SDT-"),
  ERROR_REASON("urn:epcglobal:cbv:er:", Constants.GS1_CBV_DOMAIN + "ER-"),
  MEASUREMENT_TYPE("gs1:", Constants.GS1_VOC_DOMAIN),
  ALERT_TYPE("gs1:", Constants.GS1_VOC_DOMAIN);

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
