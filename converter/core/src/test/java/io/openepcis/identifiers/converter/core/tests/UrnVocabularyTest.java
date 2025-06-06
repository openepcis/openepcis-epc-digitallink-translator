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

public class UrnVocabularyTest {

  private Converter converter;

  @BeforeEach
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void BizStepUrnTest() {
    String bizStep = "https://ref.gs1.org/cbv/BizStep-departing";
    assertEquals("urn:epcglobal:cbv:bizstep:departing", converter.toUrnVocabulary(bizStep));

    bizStep = "https://ref.gs1.org/cbv/BizStep-inspecting";
    assertEquals("urn:epcglobal:cbv:bizstep:inspecting", converter.toUrnVocabulary(bizStep));

    bizStep = "https://ref.gs1.org/cbv/BizStep-receiving";
    assertEquals("urn:epcglobal:cbv:bizstep:receiving", converter.toUrnVocabulary(bizStep));

    bizStep = "https://ref.gs1.org/cbv/BizStep-commissioning";
    assertEquals("urn:epcglobal:cbv:bizstep:commissioning", converter.toUrnVocabulary(bizStep));

    bizStep = "https://example.com/department/My_Own_BizStep";
    assertEquals(
        "https://example.com/department/My_Own_BizStep", converter.toUrnVocabulary(bizStep));

    assertEquals("", converter.toUrnVocabulary(""));
    assertEquals(" ", converter.toUrnVocabulary(" "));
    assertNull(converter.toUrnVocabulary(null));
  }

  @Test
  public void DispositionUrnTest() {
    String disposition = "https://ref.gs1.org/cbv/Disp-in_transit";
    assertEquals("urn:epcglobal:cbv:disp:in_transit", converter.toUrnVocabulary(disposition));

    disposition = "https://ref.gs1.org/cbv/Disp-recalled";
    assertEquals("urn:epcglobal:cbv:disp:recalled", converter.toUrnVocabulary(disposition));

    disposition = "https://ref.gs1.org/cbv/Disp-in_progress";
    assertEquals("urn:epcglobal:cbv:disp:in_progress", converter.toUrnVocabulary(disposition));

    disposition = "https://ref.gs1.org/cbv/Disp-needs_replacement";
    assertEquals(
        "urn:epcglobal:cbv:disp:needs_replacement", converter.toUrnVocabulary(disposition));

    disposition = "https://example.com/department/My_Own_Disposition";
    assertEquals(
        "https://example.com/department/My_Own_Disposition",
        converter.toUrnVocabulary(disposition));

    assertEquals(null, converter.toUrnVocabulary(null));
    assertEquals("", converter.toUrnVocabulary(""));
  }

  @Test
  public void BizTransactionUrnTest() {
    String bizTransactionType = "https://ref.gs1.org/cbv/BTT-inv";
    assertEquals("urn:epcglobal:cbv:btt:inv", converter.toUrnVocabulary(bizTransactionType));

    bizTransactionType = "https://ref.gs1.org/cbv/BTT-desadv";
    assertEquals("urn:epcglobal:cbv:btt:desadv", converter.toUrnVocabulary(bizTransactionType));

    bizTransactionType = "https://ref.gs1.org/cbv/BTT-po";
    assertEquals("urn:epcglobal:cbv:btt:po", converter.toUrnVocabulary(bizTransactionType));

    bizTransactionType = "https://example.com/department/My_Own_Biz_Type";
    assertEquals(
        "https://example.com/department/My_Own_Biz_Type",
        converter.toUrnVocabulary(bizTransactionType));
  }

  @Test
  public void SourceDestinationUrnTest() {
    String canonicalString = "https://ref.gs1.org/cbv/SDT-possessing_party";
    assertEquals(
        "urn:epcglobal:cbv:sdt:possessing_party", converter.toUrnVocabulary(canonicalString));

    canonicalString = "https://ref.gs1.org/cbv/SDT-owning_party";
    assertEquals("urn:epcglobal:cbv:sdt:owning_party", converter.toUrnVocabulary(canonicalString));

    canonicalString = "https://ref.gs1.org/cbv/SDT-location";
    assertEquals("urn:epcglobal:cbv:sdt:location", converter.toUrnVocabulary(canonicalString));

    canonicalString = "https://example.com/source/My_Own_Source";
    assertEquals(
        "https://example.com/source/My_Own_Source", converter.toUrnVocabulary(canonicalString));

    canonicalString = "mySource";
    assertEquals("mySource", converter.toUrnVocabulary(canonicalString));

    canonicalString = "myDestination";
    assertEquals("myDestination", converter.toUrnVocabulary(canonicalString));
  }

  @Test
  public void ErrorDeclarationReasonUrnTest() {
    String errorReason = "https://ref.gs1.org/cbv/ER-incorrect_data";
    assertEquals("urn:epcglobal:cbv:er:incorrect_data", converter.toUrnVocabulary(errorReason));

    errorReason = "https://ref.gs1.org/cbv/ER-did_not_occur";
    assertEquals("urn:epcglobal:cbv:er:did_not_occur", converter.toUrnVocabulary(errorReason));

    errorReason = "https://ref.gs1.org/cbv/ER-other";
    assertEquals("urn:epcglobal:cbv:er:other", converter.toUrnVocabulary(errorReason));

    errorReason = "https://example.com/error/My_Own_Reason";
    assertEquals("https://example.com/error/My_Own_Reason", converter.toUrnVocabulary(errorReason));

    errorReason = "myErrorReason";
    assertEquals("myErrorReason", converter.toUrnVocabulary(errorReason));
  }
}
