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
package io.openepcis.identifiers.converter.core.tests;

import io.openepcis.identifiers.converter.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WebURIVocabularyTest {

  private Converter converter;

  @BeforeEach
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

    assertEquals(null, converter.toWebURIVocabulary(null));
    assertEquals("", converter.toWebURIVocabulary(""));
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
