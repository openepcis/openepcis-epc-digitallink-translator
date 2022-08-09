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
import static org.junit.Assert.assertNull;

import io.openepcis.epc.translator.ConverterUtil;
import org.junit.Test;

public class WebURIVocabularyTest {

  @Test
  public void BizStepWebUriTest() {
    String bizStep = "urn:epcglobal:cbv:bizstep:departing";
    assertEquals(
        "https://ref.gs1.org/voc/Bizstep-departing", ConverterUtil.toWebURIVocabulary(bizStep));

    bizStep = "urn:epcglobal:cbv:bizstep:inspecting";
    assertEquals(
        "https://ref.gs1.org/voc/Bizstep-inspecting", ConverterUtil.toWebURIVocabulary(bizStep));

    bizStep = "urn:epcglobal:cbv:bizstep:receiving";
    assertEquals(
        "https://ref.gs1.org/voc/Bizstep-receiving", ConverterUtil.toWebURIVocabulary(bizStep));

    bizStep = "urn:epcglobal:cbv:bizstep:commissioning";
    assertEquals(
        "https://ref.gs1.org/voc/Bizstep-commissioning", ConverterUtil.toWebURIVocabulary(bizStep));

    bizStep = "urn:example:department:bizstep:custom_business";
    assertEquals(
        "urn:example:department:bizstep:custom_business",
        ConverterUtil.toWebURIVocabulary(bizStep));

    assertEquals("", ConverterUtil.toWebURIVocabulary(""));
    assertEquals(" ", ConverterUtil.toWebURIVocabulary(" "));
    assertNull(ConverterUtil.toWebURIVocabulary(null));
  }

  @Test
  public void DispositionWebUriTest() {
    String disposition = "urn:epcglobal:cbv:disp:in_transit";
    assertEquals(
        "https://ref.gs1.org/voc/Disp-in_transit", ConverterUtil.toWebURIVocabulary(disposition));

    disposition = "urn:epcglobal:cbv:disp:recalled";
    assertEquals(
        "https://ref.gs1.org/voc/Disp-recalled", ConverterUtil.toWebURIVocabulary(disposition));

    disposition = "urn:epcglobal:cbv:disp:in_progress";
    assertEquals(
        "https://ref.gs1.org/voc/Disp-in_progress", ConverterUtil.toWebURIVocabulary(disposition));

    disposition = "urn:epcglobal:cbv:disp:needs_replacement";
    assertEquals(
        "https://ref.gs1.org/voc/Disp-needs_replacement",
        ConverterUtil.toWebURIVocabulary(disposition));

    disposition = "urn:example:department:disposition:custom_disposition";
    assertEquals(
        "urn:example:department:disposition:custom_disposition",
        ConverterUtil.toWebURIVocabulary(disposition));
  }

  @Test
  public void BizTransactionWebUriTest() {
    String bizTransactionType = "urn:epcglobal:cbv:btt:inv";
    assertEquals(
        "https://ref.gs1.org/voc/BTT-inv", ConverterUtil.toWebURIVocabulary(bizTransactionType));

    bizTransactionType = "urn:epcglobal:cbv:btt:desadv";
    assertEquals(
        "https://ref.gs1.org/voc/BTT-desadv", ConverterUtil.toWebURIVocabulary(bizTransactionType));

    bizTransactionType = "urn:epcglobal:cbv:btt:po";
    assertEquals(
        "https://ref.gs1.org/voc/BTT-po", ConverterUtil.toWebURIVocabulary(bizTransactionType));

    bizTransactionType = "urn:example:department:error:custom_error";
    assertEquals(
        "urn:example:department:error:custom_error",
        ConverterUtil.toWebURIVocabulary(bizTransactionType));
  }

  @Test
  public void SourceDestinationWebUriTest() {
    String sourceType = "urn:epcglobal:cbv:sdt:possessing_party";
    assertEquals(
        "https://ref.gs1.org/voc/SDT-possessing_party",
        ConverterUtil.toWebURIVocabulary(sourceType));

    sourceType = "urn:epcglobal:cbv:sdt:owning_party";
    assertEquals(
        "https://ref.gs1.org/voc/SDT-owning_party", ConverterUtil.toWebURIVocabulary(sourceType));

    sourceType = "urn:epcglobal:cbv:sdt:location";
    assertEquals(
        "https://ref.gs1.org/voc/SDT-location", ConverterUtil.toWebURIVocabulary(sourceType));

    sourceType = "urn:example:department:source:custom_source";
    assertEquals(
        "urn:example:department:source:custom_source",
        ConverterUtil.toWebURIVocabulary(sourceType));

    sourceType = "mySource";
    assertEquals("mySource", ConverterUtil.toWebURIVocabulary(sourceType));
  }

  @Test
  public void ErrorDeclarationReasonWebUriTest() {
    String errorReason = "urn:epcglobal:cbv:er:incorrect_data";
    assertEquals(
        "https://ref.gs1.org/voc/ER-incorrect_data", ConverterUtil.toWebURIVocabulary(errorReason));

    errorReason = "urn:epcglobal:cbv:er:did_not_occur";
    assertEquals(
        "https://ref.gs1.org/voc/ER-did_not_occur", ConverterUtil.toWebURIVocabulary(errorReason));

    errorReason = "urn:epcglobal:cbv:er:other";
    assertEquals("https://ref.gs1.org/voc/ER-other", ConverterUtil.toWebURIVocabulary(errorReason));

    errorReason = "urn:example:department:error:custom_error";
    assertEquals(
        "urn:example:department:error:custom_error", ConverterUtil.toWebURIVocabulary(errorReason));
  }
}
