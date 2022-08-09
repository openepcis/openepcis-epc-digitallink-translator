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

public class BareStringVocabularyTest {
  @Test
  public void BizStepBareStringTest() {
    String bizStep = "https://ref.gs1.org/voc/Bizstep-departing";
    assertEquals("departing", ConverterUtil.toBareStringVocabulary(bizStep));

    bizStep = "https://ref.gs1.org/voc/Bizstep-commissioning";
    assertEquals("commissioning", ConverterUtil.toBareStringVocabulary(bizStep));

    bizStep = "https://example.com/voc/My-Own-Vocabulary";
    assertEquals(
        "https://example.com/voc/My-Own-Vocabulary", ConverterUtil.toBareStringVocabulary(bizStep));

    bizStep = "urn:epcglobal:cbv:bizstep:inspecting";
    assertEquals("inspecting", ConverterUtil.toBareStringVocabulary(bizStep));

    bizStep = "urn:epcglobal:cbv:bizstep:receiving";
    assertEquals("receiving", ConverterUtil.toBareStringVocabulary(bizStep));

    bizStep = "urn:example:department:bizstep:my_own_vocabulary";
    assertEquals(
        "urn:example:department:bizstep:my_own_vocabulary",
        ConverterUtil.toBareStringVocabulary(bizStep));

    assertEquals("", ConverterUtil.toBareStringVocabulary(""));
    assertEquals(" ", ConverterUtil.toBareStringVocabulary(" "));
    assertNull(ConverterUtil.toBareStringVocabulary(null));
  }

  @Test
  public void DispositionBareStringTest() {
    String disposition = "https://ref.gs1.org/voc/Disp-in_transit";
    assertEquals("in_transit", ConverterUtil.toBareStringVocabulary(disposition));

    disposition = "https://ref.gs1.org/voc/Disp-recalled";
    assertEquals("recalled", ConverterUtil.toBareStringVocabulary(disposition));

    disposition = "https://example.com/My-Own-Disposition";
    assertEquals(
        "https://example.com/My-Own-Disposition",
        ConverterUtil.toBareStringVocabulary(disposition));

    disposition = "urn:epcglobal:cbv:disp:in_progress";
    assertEquals("in_progress", ConverterUtil.toBareStringVocabulary(disposition));

    disposition = "urn:epcglobal:cbv:disp:needs_replacement";
    assertEquals("needs_replacement", ConverterUtil.toBareStringVocabulary(disposition));

    disposition = "urn:example:department:bizstep:my_own_vocabulary";
    assertEquals(
        "urn:example:department:bizstep:my_own_vocabulary",
        ConverterUtil.toBareStringVocabulary(disposition));
  }

  @Test
  public void BizTransactionBareStringTest() {
    String bizTransactionType = "https://ref.gs1.org/voc/BTT-inv";
    assertEquals("inv", ConverterUtil.toBareStringVocabulary(bizTransactionType));

    bizTransactionType = "https://ref.gs1.org/voc/BTT-desadv";
    assertEquals("desadv", ConverterUtil.toBareStringVocabulary(bizTransactionType));

    bizTransactionType = "https://ref.gs1.org/voc/BTT-po";
    assertEquals("po", ConverterUtil.toBareStringVocabulary(bizTransactionType));

    bizTransactionType = "https://example.com/department/My_Own_Biz_Type";
    assertEquals(
        "https://example.com/department/My_Own_Biz_Type",
        ConverterUtil.toBareStringVocabulary(bizTransactionType));

    bizTransactionType = "urn:epcglobal:cbv:btt:inv";
    assertEquals("inv", ConverterUtil.toBareStringVocabulary(bizTransactionType));

    bizTransactionType = "urn:epcglobal:cbv:btt:desadv";
    assertEquals("desadv", ConverterUtil.toBareStringVocabulary(bizTransactionType));

    bizTransactionType = "urn:epcglobal:cbv:btt:po";
    assertEquals("po", ConverterUtil.toBareStringVocabulary(bizTransactionType));

    bizTransactionType = "urn:example:department:error:custom_error";
    assertEquals(
        "urn:example:department:error:custom_error",
        ConverterUtil.toBareStringVocabulary(bizTransactionType));
  }

  @Test
  public void SourceDestinationBareStringTest() {
    String srcDestinationString = "https://ref.gs1.org/voc/SDT-possessing_party";
    assertEquals("possessing_party", ConverterUtil.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "https://ref.gs1.org/voc/SDT-owning_party";
    assertEquals("owning_party", ConverterUtil.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "https://ref.gs1.org/voc/SDT-location";
    assertEquals("location", ConverterUtil.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "https://example.com/source/My_Own_Source";
    assertEquals(
        "https://example.com/source/My_Own_Source",
        ConverterUtil.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "myDestination";
    assertEquals("myDestination", ConverterUtil.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "urn:epcglobal:cbv:sdt:possessing_party";
    assertEquals("possessing_party", ConverterUtil.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "urn:epcglobal:cbv:sdt:owning_party";
    assertEquals("owning_party", ConverterUtil.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "urn:epcglobal:cbv:sdt:location";
    assertEquals("location", ConverterUtil.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "urn:example:department:source:custom_source";
    assertEquals(
        "urn:example:department:source:custom_source",
        ConverterUtil.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "mySource";
    assertEquals("mySource", ConverterUtil.toBareStringVocabulary(srcDestinationString));
  }

  @Test
  public void ErrorDeclarationReasonBareStringTest() {
    String errorReason = "https://ref.gs1.org/voc/ER-incorrect_data";
    assertEquals("incorrect_data", ConverterUtil.toBareStringVocabulary(errorReason));

    errorReason = "https://ref.gs1.org/voc/ER-did_not_occur";
    assertEquals("did_not_occur", ConverterUtil.toBareStringVocabulary(errorReason));

    errorReason = "https://ref.gs1.org/voc/ER-other";
    assertEquals("other", ConverterUtil.toBareStringVocabulary(errorReason));

    errorReason = "https://example.com/myReason/ReasonDescription";
    assertEquals(
        "https://example.com/myReason/ReasonDescription",
        ConverterUtil.toBareStringVocabulary(errorReason));

    errorReason = "urn:epcglobal:cbv:er:incorrect_data";
    assertEquals("incorrect_data", ConverterUtil.toBareStringVocabulary(errorReason));

    errorReason = "urn:epcglobal:cbv:er:did_not_occur";
    assertEquals("did_not_occur", ConverterUtil.toBareStringVocabulary(errorReason));

    errorReason = "urn:epcglobal:cbv:er:other";
    assertEquals("other", ConverterUtil.toBareStringVocabulary(errorReason));

    errorReason = "urn:example:error:reason:my_own_reason";
    assertEquals(
        "urn:example:error:reason:my_own_reason",
        ConverterUtil.toBareStringVocabulary(errorReason));

    assertEquals("", ConverterUtil.toBareStringVocabulary(""));
    assertEquals(" ", ConverterUtil.toBareStringVocabulary(" "));
    assertNull(ConverterUtil.toBareStringVocabulary(null));
  }
}
