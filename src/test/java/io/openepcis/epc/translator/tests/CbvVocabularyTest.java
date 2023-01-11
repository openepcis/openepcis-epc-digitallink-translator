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

public class CbvVocabularyTest {

  private Converter converter;

  @Before
  public void before() throws Exception {
    converter = new Converter();
  }

  @Test
  public void BizStepCbvVocabularyTest() {
    // Converting BizStep BareString to CBV vocabulary in URN format.
    assertEquals(
        "urn:epcglobal:cbv:bizstep:shipping",
        converter.toCbvVocabulary("shipping", "bizStep", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:bizstep:packing",
        converter.toCbvVocabulary("packing", "bizStep", "urn"));

    // Converting BizStep BareString to CBV vocabulary in WebURI format.
    assertEquals(
        "https://ref.gs1.org/cbv/Bizstep-shipping",
        converter.toCbvVocabulary("shipping", "bizStep", "webUri"));
    assertEquals(
        "https://ref.gs1.org/cbv/Bizstep-packing",
        converter.toCbvVocabulary("packing", "bizStep", "webUri"));

    assertEquals("", converter.toCbvVocabulary("", "bizStep", ""));
    assertEquals(" ", converter.toCbvVocabulary(" ", "bizStep", "urn"));
    assertNull(converter.toCbvVocabulary(null, "bizStep", "webUri"));
    assertEquals("shipping", converter.toCbvVocabulary("shipping", null, "webUri"));
  }

  @Test
  public void DispositionCbvVocabularyTest() {
    // Converting Disposition BareString to CBV vocabulary in URN format.
    assertEquals(
        "urn:epcglobal:cbv:disp:in_transit",
        converter.toCbvVocabulary("in_transit", "disposition", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:disp:partially_dispensed",
        converter.toCbvVocabulary("partially_dispensed", "disposition", "urn"));

    // Converting Disposition BareString to CBV vocabulary in WebURI format.
    assertEquals(
        "https://ref.gs1.org/cbv/Disp-in_transit",
        converter.toCbvVocabulary("in_transit", "disposition", "WebURI"));
    assertEquals(
        "https://ref.gs1.org/cbv/Disp-partially_dispensed",
        converter.toCbvVocabulary("partially_dispensed", "disposition", "WebURI"));

    // Converting PersistentDisposition BareString to CBV vocabulary in URN format.
    assertEquals(
        "urn:epcglobal:cbv:disp:in_transit",
        converter.toCbvVocabulary("in_transit", "persistentDisposition", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:disp:partially_dispensed",
        converter.toCbvVocabulary("partially_dispensed", "persistentDisposition", "urn"));

    // Converting PersistentDisposition BareString to CBV vocabulary in URN format.
    assertEquals(
        "https://ref.gs1.org/cbv/Disp-in_transit",
        converter.toCbvVocabulary("in_transit", "persistentDisposition", "WebURI"));
    assertEquals(
        "https://ref.gs1.org/cbv/Disp-partially_dispensed",
        converter.toCbvVocabulary("partially_dispensed", "persistentDisposition", "WebURI"));
  }

  @Test
  public void BizTransactionCbvVocabularyTest() {
    // Converting BizTransaction/BizTransactionList BareString to CBV vocabulary in URN format.
    assertEquals(
        "urn:epcglobal:cbv:btt:po", converter.toCbvVocabulary("po", "bizTransactionLIST", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:btt:inv", converter.toCbvVocabulary("inv", "bizTransactionLIST", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:btt:pedigree",
        converter.toCbvVocabulary("pedigree", "bizTransactionLIST", "urn"));

    // Converting BizTransaction/BizTransactionList BareString to CBV vocabulary in WebURI format.
    assertEquals(
        "https://ref.gs1.org/cbv/BTT-po",
        converter.toCbvVocabulary("po", "bizTransaction", "webURi"));
    assertEquals(
        "https://ref.gs1.org/cbv/BTT-inv",
        converter.toCbvVocabulary("inv", "bizTransaction", "webURi"));
    assertEquals(
        "https://ref.gs1.org/cbv/BTT-pedigree",
        converter.toCbvVocabulary("pedigree", "bizTransaction", "webURi"));
  }

  @Test
  public void SourceDestinationCbvVocabularyTest() {
    // Converting Source/SourceList BareString to CBV vocabulary in URN format.
    assertEquals(
        "urn:epcglobal:cbv:sdt:owning_party",
        converter.toCbvVocabulary("owning_party", "sourceList", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:sdt:location",
        converter.toCbvVocabulary("location", "sourceList", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:sdt:processing_party",
        converter.toCbvVocabulary("processing_party", "sourceList", "urn"));

    // Converting Source/SourceList BareString to CBV vocabulary in WebURI format.
    assertEquals(
        "https://ref.gs1.org/cbv/SDT-owning_party",
        converter.toCbvVocabulary("owning_party", "source", "weburi"));
    assertEquals(
        "https://ref.gs1.org/cbv/SDT-location",
        converter.toCbvVocabulary("location", "source", "weburi"));
    assertEquals(
        "https://ref.gs1.org/cbv/SDT-processing_party",
        converter.toCbvVocabulary("processing_party", "source", "weburi"));

    // Converting Destination/DestinationList BareString to CBV vocabulary in URN format.
    assertEquals(
        "urn:epcglobal:cbv:sdt:owning_party",
        converter.toCbvVocabulary("owning_party", "destinationList", "URN"));
    assertEquals(
        "urn:epcglobal:cbv:sdt:location",
        converter.toCbvVocabulary("location", "destinationList", "URN"));
    assertEquals(
        "urn:epcglobal:cbv:sdt:processing_party",
        converter.toCbvVocabulary("processing_party", "destinationList", "URN"));

    // Converting Destination/DestinationList BareString to CBV vocabulary in WebURI format.
    assertEquals(
        "https://ref.gs1.org/cbv/SDT-owning_party",
        converter.toCbvVocabulary("owning_party", "Destination", "weburi"));
    assertEquals(
        "https://ref.gs1.org/cbv/SDT-location",
        converter.toCbvVocabulary("location", "Destination", "weburi"));
    assertEquals(
        "https://ref.gs1.org/cbv/SDT-processing_party",
        converter.toCbvVocabulary("processing_party", "Destination", "weburi"));
  }

  @Test
  public void ErrorReasonCbvVocabularyTest() {
    // Convert ErrorDeclaration Reason BareString to CBV vocabulary in URN format.
    assertEquals(
        "urn:epcglobal:cbv:er:did_not_occur",
        converter.toCbvVocabulary("did_not_occur", "errorDeclaration", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:er:incorrect_data",
        converter.toCbvVocabulary("incorrect_data", "errorDeclaration", "urn"));
    assertEquals(
        "urn:epcglobal:cbv:er:other",
        converter.toCbvVocabulary("other", "errorDeclaration", "urn"));

    // Convert ErrorDeclaration Reason BareString to CBV vocabulary in WebURI format.
    assertEquals(
        "https://ref.gs1.org/cbv/ER-did_not_occur",
        converter.toCbvVocabulary("did_not_occur", "reason", "WebURI"));
    assertEquals(
        "https://ref.gs1.org/cbv/ER-incorrect_data",
        converter.toCbvVocabulary("incorrect_data", "reason", "WebURI"));
    assertEquals(
        "https://ref.gs1.org/cbv/ER-other", converter.toCbvVocabulary("other", "reason", "WebURI"));
  }
}
