/*
 * Copyright 2022-2024 benelog GmbH & Co. KG
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

import io.openepcis.epc.translator.Converter;
import org.junit.Before;
import org.junit.Test;

public class BareStringVocabularyTest {

  private Converter converter;

  @Before
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void BizStepBareStringTest() {
    String bizStep = "https://ref.gs1.org/cbv/BizStep-departing";
    assertEquals("departing", converter.toBareStringVocabulary(bizStep));

    bizStep = "https://ref.gs1.org/cbv/BizStep-commissioning";
    assertEquals("commissioning", converter.toBareStringVocabulary(bizStep));

    bizStep = "https://example.com/cbv/My-Own-Vocabulary";
    assertEquals(
        "https://example.com/cbv/My-Own-Vocabulary", converter.toBareStringVocabulary(bizStep));

    bizStep = "urn:epcglobal:cbv:bizstep:inspecting";
    assertEquals("inspecting", converter.toBareStringVocabulary(bizStep));

    bizStep = "urn:epcglobal:cbv:bizstep:receiving";
    assertEquals("receiving", converter.toBareStringVocabulary(bizStep));

    bizStep = "urn:example:department:bizstep:my_own_vocabulary";
    assertEquals(
        "urn:example:department:bizstep:my_own_vocabulary",
        converter.toBareStringVocabulary(bizStep));

    bizStep = "cbv:BizStep-receiving";
    assertEquals("receiving", converter.toBareStringVocabulary(bizStep));

    bizStep = "https://ref.gs1.org/cbv/BizStep-accepting";
    assertEquals("accepting", converter.toBareStringVocabulary(bizStep));

    bizStep = "https://ref.gs1.org/cbv/BizStep-disassembling";
    assertEquals("disassembling", converter.toBareStringVocabulary(bizStep));

    assertEquals("cbv:receiving", converter.toBareStringVocabulary("cbv:receiving"));
    assertEquals("", converter.toBareStringVocabulary(""));
    assertEquals(" ", converter.toBareStringVocabulary(" "));
    assertNull(converter.toBareStringVocabulary(null));
  }

  @Test
  public void DispositionBareStringTest() {
    String disposition = "https://ref.gs1.org/cbv/Disp-in_transit";
    assertEquals("in_transit", converter.toBareStringVocabulary(disposition));

    disposition = "https://ref.gs1.org/cbv/Disp-recalled";
    assertEquals("recalled", converter.toBareStringVocabulary(disposition));

    disposition = "https://example.com/My-Own-Disposition";
    assertEquals(
        "https://example.com/My-Own-Disposition", converter.toBareStringVocabulary(disposition));

    disposition = "urn:epcglobal:cbv:disp:in_progress";
    assertEquals("in_progress", converter.toBareStringVocabulary(disposition));

    disposition = "urn:epcglobal:cbv:disp:needs_replacement";
    assertEquals("needs_replacement", converter.toBareStringVocabulary(disposition));

    disposition = "urn:example:department:bizstep:my_own_vocabulary";
    assertEquals(
        "urn:example:department:bizstep:my_own_vocabulary",
        converter.toBareStringVocabulary(disposition));

    disposition = "cbv:Disp-in_progress";
    assertEquals("in_progress", converter.toBareStringVocabulary(disposition));

    disposition = "https://ref.gs1.org/cbv/Disp-completeness_verified";
    assertEquals("completeness_verified", converter.toBareStringVocabulary(disposition));

    disposition = "https://ref.gs1.org/cbv/Disp-returned";
    assertEquals("returned", converter.toBareStringVocabulary(disposition));

    assertEquals("cbv:in_progress", converter.toBareStringVocabulary("cbv:in_progress"));
    assertNull(converter.toBareStringVocabulary(null));
  }

  @Test
  public void BizTransactionBareStringTest() {
    String bizTransactionType = "https://ref.gs1.org/cbv/BTT-inv";
    assertEquals("inv", converter.toBareStringVocabulary(bizTransactionType));

    bizTransactionType = "https://ref.gs1.org/cbv/BTT-desadv";
    assertEquals("desadv", converter.toBareStringVocabulary(bizTransactionType));

    bizTransactionType = "https://ref.gs1.org/cbv/BTT-po";
    assertEquals("po", converter.toBareStringVocabulary(bizTransactionType));

    bizTransactionType = "https://example.com/department/My_Own_Biz_Type";
    assertEquals(
        "https://example.com/department/My_Own_Biz_Type",
        converter.toBareStringVocabulary(bizTransactionType));

    bizTransactionType = "urn:epcglobal:cbv:btt:inv";
    assertEquals("inv", converter.toBareStringVocabulary(bizTransactionType));

    bizTransactionType = "urn:epcglobal:cbv:btt:desadv";
    assertEquals("desadv", converter.toBareStringVocabulary(bizTransactionType));

    bizTransactionType = "urn:epcglobal:cbv:btt:po";
    assertEquals("po", converter.toBareStringVocabulary(bizTransactionType));

    bizTransactionType = "urn:example:department:error:custom_error";
    assertEquals(
        "urn:example:department:error:custom_error",
        converter.toBareStringVocabulary(bizTransactionType));

    bizTransactionType = "cbv:BTT-desadv";
    assertEquals("desadv", converter.toBareStringVocabulary(bizTransactionType));

    bizTransactionType = "https://ref.gs1.org/cbv/BTT-pedigree";
    assertEquals("pedigree", converter.toBareStringVocabulary(bizTransactionType));

    bizTransactionType = "https://ref.gs1.org/cbv/BTT-bol";
    assertEquals("bol", converter.toBareStringVocabulary(bizTransactionType));

    assertEquals("cbv:desadv", converter.toBareStringVocabulary("cbv:desadv"));
    assertNull(converter.toBareStringVocabulary(null));
  }

  @Test
  public void SourceDestinationBareStringTest() {
    String srcDestinationString = "https://ref.gs1.org/cbv/SDT-possessing_party";
    assertEquals("possessing_party", converter.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "https://ref.gs1.org/cbv/SDT-owning_party";
    assertEquals("owning_party", converter.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "https://ref.gs1.org/cbv/SDT-location";
    assertEquals("location", converter.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "https://example.com/source/My_Own_Source";
    assertEquals(
        "https://example.com/source/My_Own_Source",
        converter.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "myDestination";
    assertEquals("myDestination", converter.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "urn:epcglobal:cbv:sdt:possessing_party";
    assertEquals("possessing_party", converter.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "urn:epcglobal:cbv:sdt:owning_party";
    assertEquals("owning_party", converter.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "urn:epcglobal:cbv:sdt:location";
    assertEquals("location", converter.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "urn:example:department:source:custom_source";
    assertEquals(
        "urn:example:department:source:custom_source",
        converter.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "mySource";
    assertEquals("mySource", converter.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "cbv:SDT-owning_party";
    assertEquals("owning_party", converter.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "https://ref.gs1.org/cbv/SDT-possessing_party";
    assertEquals("possessing_party", converter.toBareStringVocabulary(srcDestinationString));

    srcDestinationString = "https://ref.gs1.org/cbv/SDT-location";
    assertEquals("location", converter.toBareStringVocabulary(srcDestinationString));

    assertEquals("cbv:owning_party", converter.toBareStringVocabulary("cbv:owning_party"));
    assertNull(converter.toBareStringVocabulary(null));
  }

  @Test
  public void ErrorDeclarationReasonBareStringTest() {
    String errorReason = "https://ref.gs1.org/cbv/ER-incorrect_data";
    assertEquals("incorrect_data", converter.toBareStringVocabulary(errorReason));

    errorReason = "https://ref.gs1.org/cbv/ER-did_not_occur";
    assertEquals("did_not_occur", converter.toBareStringVocabulary(errorReason));

    errorReason = "https://ref.gs1.org/cbv/ER-other";
    assertEquals("other", converter.toBareStringVocabulary(errorReason));

    errorReason = "https://example.com/myReason/ReasonDescription";
    assertEquals(
        "https://example.com/myReason/ReasonDescription",
        converter.toBareStringVocabulary(errorReason));

    errorReason = "urn:epcglobal:cbv:er:incorrect_data";
    assertEquals("incorrect_data", converter.toBareStringVocabulary(errorReason));

    errorReason = "urn:epcglobal:cbv:er:did_not_occur";
    assertEquals("did_not_occur", converter.toBareStringVocabulary(errorReason));

    errorReason = "urn:epcglobal:cbv:er:other";
    assertEquals("other", converter.toBareStringVocabulary(errorReason));

    errorReason = "urn:example:error:reason:my_own_reason";
    assertEquals(
        "urn:example:error:reason:my_own_reason", converter.toBareStringVocabulary(errorReason));

    errorReason = "cbv:ER-did_not_occur";
    assertEquals("did_not_occur", converter.toBareStringVocabulary(errorReason));

    errorReason = "https://ref.gs1.org/cbv/ER-did_not_occur";
    assertEquals("did_not_occur", converter.toBareStringVocabulary(errorReason));

    errorReason = "https://ref.gs1.org/cbv/ER-incorrect_data";
    assertEquals("incorrect_data", converter.toBareStringVocabulary(errorReason));

    assertEquals("cbv:did_not_occur", converter.toBareStringVocabulary("cbv:did_not_occur"));
    assertEquals("", converter.toBareStringVocabulary(""));
    assertEquals(" ", converter.toBareStringVocabulary(" "));
    assertNull(converter.toBareStringVocabulary(null));
  }
}
