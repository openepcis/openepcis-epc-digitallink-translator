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

import io.openepcis.epc.translator.Converter;
import org.junit.Before;
import org.junit.Test;

public class WebURIVocabularyTest {

  private Converter converter;

  @Before
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void BizStepWebUriTest() {
    String bizStep = "urn:epcglobal:cbv:bizstep:departing";
    assertEquals(
        "https://ref.gs1.org/cbv/BizStep-departing", converter.toWebURIVocabulary(bizStep));

    bizStep = "urn:epcglobal:cbv:bizstep:inspecting";
    assertEquals(
        "https://ref.gs1.org/cbv/BizStep-inspecting", converter.toWebURIVocabulary(bizStep));

    bizStep = "urn:epcglobal:cbv:bizstep:receiving";
    assertEquals(
        "https://ref.gs1.org/cbv/BizStep-receiving", converter.toWebURIVocabulary(bizStep));

    bizStep = "urn:epcglobal:cbv:bizstep:commissioning";
    assertEquals(
        "https://ref.gs1.org/cbv/BizStep-commissioning", converter.toWebURIVocabulary(bizStep));

    bizStep = "urn:example:department:bizstep:custom_business";
    assertEquals(
        "urn:example:department:bizstep:custom_business", converter.toWebURIVocabulary(bizStep));

    assertEquals("", converter.toWebURIVocabulary(""));
    assertEquals(" ", converter.toWebURIVocabulary(" "));
    assertNull(converter.toWebURIVocabulary(null));
  }

  @Test
  public void DispositionWebUriTest() {
    String disposition = "urn:epcglobal:cbv:disp:in_transit";
    assertEquals(
        "https://ref.gs1.org/cbv/Disp-in_transit", converter.toWebURIVocabulary(disposition));

    disposition = "urn:epcglobal:cbv:disp:recalled";
    assertEquals(
        "https://ref.gs1.org/cbv/Disp-recalled", converter.toWebURIVocabulary(disposition));

    disposition = "urn:epcglobal:cbv:disp:in_progress";
    assertEquals(
        "https://ref.gs1.org/cbv/Disp-in_progress", converter.toWebURIVocabulary(disposition));

    disposition = "urn:epcglobal:cbv:disp:needs_replacement";
    assertEquals(
        "https://ref.gs1.org/cbv/Disp-needs_replacement",
        converter.toWebURIVocabulary(disposition));

    disposition = "urn:example:department:disposition:custom_disposition";
    assertEquals(
        "urn:example:department:disposition:custom_disposition",
        converter.toWebURIVocabulary(disposition));
  }

  @Test
  public void BizTransactionWebUriTest() {
    String bizTransactionType = "urn:epcglobal:cbv:btt:inv";
    assertEquals(
        "https://ref.gs1.org/cbv/BTT-inv", converter.toWebURIVocabulary(bizTransactionType));

    bizTransactionType = "urn:epcglobal:cbv:btt:desadv";
    assertEquals(
        "https://ref.gs1.org/cbv/BTT-desadv", converter.toWebURIVocabulary(bizTransactionType));

    bizTransactionType = "urn:epcglobal:cbv:btt:po";
    assertEquals(
        "https://ref.gs1.org/cbv/BTT-po", converter.toWebURIVocabulary(bizTransactionType));

    bizTransactionType = "urn:example:department:error:custom_error";
    assertEquals(
        "urn:example:department:error:custom_error",
        converter.toWebURIVocabulary(bizTransactionType));
  }

  @Test
  public void SourceDestinationWebUriTest() {
    String sourceType = "urn:epcglobal:cbv:sdt:possessing_party";
    assertEquals(
        "https://ref.gs1.org/cbv/SDT-possessing_party", converter.toWebURIVocabulary(sourceType));

    sourceType = "urn:epcglobal:cbv:sdt:owning_party";
    assertEquals(
        "https://ref.gs1.org/cbv/SDT-owning_party", converter.toWebURIVocabulary(sourceType));

    sourceType = "urn:epcglobal:cbv:sdt:location";
    assertEquals("https://ref.gs1.org/cbv/SDT-location", converter.toWebURIVocabulary(sourceType));

    sourceType = "urn:example:department:source:custom_source";
    assertEquals(
        "urn:example:department:source:custom_source", converter.toWebURIVocabulary(sourceType));

    sourceType = "mySource";
    assertEquals("mySource", converter.toWebURIVocabulary(sourceType));
  }

  @Test
  public void ErrorDeclarationReasonWebUriTest() {
    String errorReason = "urn:epcglobal:cbv:er:incorrect_data";
    assertEquals(
        "https://ref.gs1.org/cbv/ER-incorrect_data", converter.toWebURIVocabulary(errorReason));

    errorReason = "urn:epcglobal:cbv:er:did_not_occur";
    assertEquals(
        "https://ref.gs1.org/cbv/ER-did_not_occur", converter.toWebURIVocabulary(errorReason));

    errorReason = "urn:epcglobal:cbv:er:other";
    assertEquals("https://ref.gs1.org/cbv/ER-other", converter.toWebURIVocabulary(errorReason));

    errorReason = "urn:example:department:error:custom_error";
    assertEquals(
        "urn:example:department:error:custom_error", converter.toWebURIVocabulary(errorReason));
  }
}
