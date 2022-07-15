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
package io.openepcis.epc.translator.tests;

import static org.junit.Assert.assertEquals;

import io.openepcis.epc.translator.ConverterUtil;
import org.junit.Test;

public class UrnVocabularyTest {

  @Test
  public void BizStepUrnTest() {
    String bizStep = "https://ref.gs1.org/voc/Bizstep-departing";
    assertEquals("urn:epcglobal:cbv:bizstep:departing", ConverterUtil.toUrnVocabulary(bizStep));

    bizStep = "https://ref.gs1.org/voc/Bizstep-inspecting";
    assertEquals("urn:epcglobal:cbv:bizstep:inspecting", ConverterUtil.toUrnVocabulary(bizStep));

    bizStep = "https://ref.gs1.org/voc/Bizstep-receiving";
    assertEquals("urn:epcglobal:cbv:bizstep:receiving", ConverterUtil.toUrnVocabulary(bizStep));

    bizStep = "https://ref.gs1.org/voc/Bizstep-commissioning";
    assertEquals("urn:epcglobal:cbv:bizstep:commissioning", ConverterUtil.toUrnVocabulary(bizStep));

    bizStep = "https://example.com/department/My_Own_BizStep";
    assertEquals(
        "https://example.com/department/My_Own_BizStep", ConverterUtil.toUrnVocabulary(bizStep));
  }

  @Test
  public void DispositionUrnTest() {
    String disposition = "https://ref.gs1.org/voc/Disp-in_transit";
    assertEquals("urn:epcglobal:cbv:disp:in_transit", ConverterUtil.toUrnVocabulary(disposition));

    disposition = "https://ref.gs1.org/voc/Disp-recalled";
    assertEquals("urn:epcglobal:cbv:disp:recalled", ConverterUtil.toUrnVocabulary(disposition));

    disposition = "https://ref.gs1.org/voc/Disp-in_progress";
    assertEquals("urn:epcglobal:cbv:disp:in_progress", ConverterUtil.toUrnVocabulary(disposition));

    disposition = "https://ref.gs1.org/voc/Disp-needs_replacement";
    assertEquals(
        "urn:epcglobal:cbv:disp:needs_replacement", ConverterUtil.toUrnVocabulary(disposition));

    disposition = "https://example.com/department/My_Own_Disposition";
    assertEquals(
        "https://example.com/department/My_Own_Disposition",
        ConverterUtil.toUrnVocabulary(disposition));
  }

  @Test
  public void BizTransactionUrnTest() {
    String bizTransactionType = "https://ref.gs1.org/voc/BTT-inv";
    assertEquals("urn:epcglobal:cbv:btt:inv", ConverterUtil.toUrnVocabulary(bizTransactionType));

    bizTransactionType = "https://ref.gs1.org/voc/BTT-desadv";
    assertEquals("urn:epcglobal:cbv:btt:desadv", ConverterUtil.toUrnVocabulary(bizTransactionType));

    bizTransactionType = "https://ref.gs1.org/voc/BTT-po";
    assertEquals("urn:epcglobal:cbv:btt:po", ConverterUtil.toUrnVocabulary(bizTransactionType));

    bizTransactionType = "https://example.com/department/My_Own_Biz_Type";
    assertEquals(
        "https://example.com/department/My_Own_Biz_Type",
        ConverterUtil.toUrnVocabulary(bizTransactionType));
  }

  @Test
  public void SourceDestinationUrnTest() {
    String canonicalString = "https://ref.gs1.org/voc/SDT-possessing_party";
    assertEquals(
        "urn:epcglobal:cbv:sdt:possessing_party", ConverterUtil.toUrnVocabulary(canonicalString));

    canonicalString = "https://ref.gs1.org/voc/SDT-owning_party";
    assertEquals(
        "urn:epcglobal:cbv:sdt:owning_party", ConverterUtil.toUrnVocabulary(canonicalString));

    canonicalString = "https://ref.gs1.org/voc/SDT-location";
    assertEquals("urn:epcglobal:cbv:sdt:location", ConverterUtil.toUrnVocabulary(canonicalString));

    canonicalString = "https://example.com/source/My_Own_Source";
    assertEquals(
        "https://example.com/source/My_Own_Source", ConverterUtil.toUrnVocabulary(canonicalString));

    canonicalString = "mySource";
    assertEquals("mySource", ConverterUtil.toUrnVocabulary(canonicalString));

    canonicalString = "myDestination";
    assertEquals("myDestination", ConverterUtil.toUrnVocabulary(canonicalString));
  }

  @Test
  public void ErrorDeclarationReasonUrnTest() {
    String errorReason = "https://ref.gs1.org/voc/ER-incorrect_data";
    assertEquals("urn:epcglobal:cbv:er:incorrect_data", ConverterUtil.toUrnVocabulary(errorReason));

    errorReason = "https://ref.gs1.org/voc/ER-did_not_occur";
    assertEquals("urn:epcglobal:cbv:er:did_not_occur", ConverterUtil.toUrnVocabulary(errorReason));

    errorReason = "https://ref.gs1.org/voc/ER-other";
    assertEquals("urn:epcglobal:cbv:er:other", ConverterUtil.toUrnVocabulary(errorReason));

    errorReason = "https://example.com/error/My_Own_Reason";
    assertEquals(
        "https://example.com/error/My_Own_Reason", ConverterUtil.toUrnVocabulary(errorReason));

    errorReason = "myErrorReason";
    assertEquals("myErrorReason", ConverterUtil.toUrnVocabulary(errorReason));
  }
}
