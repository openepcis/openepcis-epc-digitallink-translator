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

public class CbvVocabularyTest {

  @Test
  public void BizStepCbvVocabularyTest() {
    // Converting BizStep BareString to CBV vocabulary in URN format.
    assertEquals(
        "urn:epcglobal:cbv:bizstep:shipping",
        ConverterUtil.toCbvVocabulary("shipping", "bizStep", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:bizstep:packing",
        ConverterUtil.toCbvVocabulary("packing", "bizStep", "urn"));

    // Converting BizStep BareString to CBV vocabulary in WebURI format.
    assertEquals(
        "https://ref.gs1.org/voc/Bizstep-shipping",
        ConverterUtil.toCbvVocabulary("shipping", "bizStep", "webUri"));
    assertEquals(
        "https://ref.gs1.org/voc/Bizstep-packing",
        ConverterUtil.toCbvVocabulary("packing", "bizStep", "webUri"));

    assertEquals("", ConverterUtil.toCbvVocabulary("", "bizStep", ""));
    assertEquals(" ", ConverterUtil.toCbvVocabulary(" ", "bizStep", "urn"));
    assertNull(ConverterUtil.toCbvVocabulary(null, "bizStep", "webUri"));
    assertEquals("shipping", ConverterUtil.toCbvVocabulary("shipping", null, "webUri"));
  }

  @Test
  public void DispositionCbvVocabularyTest() {
    // Converting Disposition BareString to CBV vocabulary in URN format.
    assertEquals(
        "urn:epcglobal:cbv:disp:in_transit",
        ConverterUtil.toCbvVocabulary("in_transit", "disposition", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:disp:partially_dispensed",
        ConverterUtil.toCbvVocabulary("partially_dispensed", "disposition", "urn"));

    // Converting Disposition BareString to CBV vocabulary in WebURI format.
    assertEquals(
        "https://ref.gs1.org/voc/Disp-in_transit",
        ConverterUtil.toCbvVocabulary("in_transit", "disposition", "WebURI"));
    assertEquals(
        "https://ref.gs1.org/voc/Disp-partially_dispensed",
        ConverterUtil.toCbvVocabulary("partially_dispensed", "disposition", "WebURI"));

    // Converting PersistentDisposition BareString to CBV vocabulary in URN format.
    assertEquals(
        "urn:epcglobal:cbv:disp:in_transit",
        ConverterUtil.toCbvVocabulary("in_transit", "persistentDisposition", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:disp:partially_dispensed",
        ConverterUtil.toCbvVocabulary("partially_dispensed", "persistentDisposition", "urn"));

    // Converting PersistentDisposition BareString to CBV vocabulary in URN format.
    assertEquals(
        "https://ref.gs1.org/voc/Disp-in_transit",
        ConverterUtil.toCbvVocabulary("in_transit", "persistentDisposition", "WebURI"));
    assertEquals(
        "https://ref.gs1.org/voc/Disp-partially_dispensed",
        ConverterUtil.toCbvVocabulary("partially_dispensed", "persistentDisposition", "WebURI"));
  }

  @Test
  public void BizTransactionCbvVocabularyTest() {
    // Converting BizTransaction/BizTransactionList BareString to CBV vocabulary in URN format.
    assertEquals(
        "urn:epcglobal:cbv:btt:po",
        ConverterUtil.toCbvVocabulary("po", "bizTransactionLIST", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:btt:inv",
        ConverterUtil.toCbvVocabulary("inv", "bizTransactionLIST", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:btt:pedigree",
        ConverterUtil.toCbvVocabulary("pedigree", "bizTransactionLIST", "urn"));

    // Converting BizTransaction/BizTransactionList BareString to CBV vocabulary in WebURI format.
    assertEquals(
        "https://ref.gs1.org/voc/BTT-po",
        ConverterUtil.toCbvVocabulary("po", "bizTransaction", "webURi"));
    assertEquals(
        "https://ref.gs1.org/voc/BTT-inv",
        ConverterUtil.toCbvVocabulary("inv", "bizTransaction", "webURi"));
    assertEquals(
        "https://ref.gs1.org/voc/BTT-pedigree",
        ConverterUtil.toCbvVocabulary("pedigree", "bizTransaction", "webURi"));
  }

  @Test
  public void SourceDestinationCbvVocabularyTest() {
    // Converting Source/SourceList BareString to CBV vocabulary in URN format.
    assertEquals(
        "urn:epcglobal:cbv:sdt:owning_party",
        ConverterUtil.toCbvVocabulary("owning_party", "sourceList", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:sdt:location",
        ConverterUtil.toCbvVocabulary("location", "sourceList", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:sdt:processing_party",
        ConverterUtil.toCbvVocabulary("processing_party", "sourceList", "urn"));

    // Converting Source/SourceList BareString to CBV vocabulary in WebURI format.
    assertEquals(
        "https://ref.gs1.org/voc/SDT-owning_party",
        ConverterUtil.toCbvVocabulary("owning_party", "source", "weburi"));
    assertEquals(
        "https://ref.gs1.org/voc/SDT-location",
        ConverterUtil.toCbvVocabulary("location", "source", "weburi"));
    assertEquals(
        "https://ref.gs1.org/voc/SDT-processing_party",
        ConverterUtil.toCbvVocabulary("processing_party", "source", "weburi"));

    // Converting Destination/DestinationList BareString to CBV vocabulary in URN format.
    assertEquals(
        "urn:epcglobal:cbv:sdt:owning_party",
        ConverterUtil.toCbvVocabulary("owning_party", "destinationList", "URN"));
    assertEquals(
        "urn:epcglobal:cbv:sdt:location",
        ConverterUtil.toCbvVocabulary("location", "destinationList", "URN"));
    assertEquals(
        "urn:epcglobal:cbv:sdt:processing_party",
        ConverterUtil.toCbvVocabulary("processing_party", "destinationList", "URN"));

    // Converting Destination/DestinationList BareString to CBV vocabulary in WebURI format.
    assertEquals(
        "https://ref.gs1.org/voc/SDT-owning_party",
        ConverterUtil.toCbvVocabulary("owning_party", "Destination", "weburi"));
    assertEquals(
        "https://ref.gs1.org/voc/SDT-location",
        ConverterUtil.toCbvVocabulary("location", "Destination", "weburi"));
    assertEquals(
        "https://ref.gs1.org/voc/SDT-processing_party",
        ConverterUtil.toCbvVocabulary("processing_party", "Destination", "weburi"));
  }

  @Test
  public void ErrorReasonCbvVocabularyTest() {
    // Convert ErrorDeclaration Reason BareString to CBV vocabulary in URN format.
    assertEquals(
        "urn:epcglobal:cbv:er:did_not_occur",
        ConverterUtil.toCbvVocabulary("did_not_occur", "errorDeclaration", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:er:incorrect_data",
        ConverterUtil.toCbvVocabulary("incorrect_data", "errorDeclaration", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:er:other",
        ConverterUtil.toCbvVocabulary("other", "errorDeclaration", "urn"));

    // Convert ErrorDeclaration Reason BareString to CBV vocabulary in WebURI format.
    assertEquals(
        "https://ref.gs1.org/voc/ER-did_not_occur",
        ConverterUtil.toCbvVocabulary("did_not_occur", "reason", "WebURI"));
    assertEquals(
        "https://ref.gs1.org/voc/ER-incorrect_data",
        ConverterUtil.toCbvVocabulary("incorrect_data", "reason", "WebURI"));
    assertEquals(
        "https://ref.gs1.org/voc/ER-other",
        ConverterUtil.toCbvVocabulary("other", "reason", "WebURI"));
  }
}
