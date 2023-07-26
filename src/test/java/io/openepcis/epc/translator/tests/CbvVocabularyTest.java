/*
 * Copyright 2022-2023 benelog GmbH & Co. KG
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
        "https://ref.gs1.org/cbv/BizStep-shipping",
        converter.toCbvVocabulary("shipping", "bizStep", "webUri"));
    assertEquals(
        "https://ref.gs1.org/cbv/BizStep-packing",
        converter.toCbvVocabulary("packing", "bizStep", "webUri"));

    assertEquals("", converter.toCbvVocabulary("", "bizStep", ""));
    assertEquals(" ", converter.toCbvVocabulary(" ", "bizStep", "urn"));
    assertNull(converter.toCbvVocabulary(null, "bizStep", "webUri"));
    assertEquals("shipping", converter.toCbvVocabulary("shipping", null, "webUri"));

    assertEquals(
        "https://ref.gs1.org/cbv/BizStep-receiving",
        converter.toCbvVocabulary("cbv:BizStep-receiving", "bizStep", "webUri"));
    assertEquals(
        "https://ref.gs1.org/cbv/BizStep-packing",
        converter.toCbvVocabulary("cbv:BizStep-packing", "bizStep", "webUri"));
    assertEquals(
        "https://ref.gs1.org/cbv/BizStep-shipping",
        converter.toCbvVocabulary("cbv:BizStep-shipping", "bizStep", "webUri"));

    assertEquals(
        "urn:epcglobal:cbv:bizstep:shipping",
        converter.toCbvVocabulary("cbv:BizStep-shipping", "bizStep", "urn"));

    assertEquals(
        "urn:gs1:epcisapp:rail:BizStep:shipping",
        converter.toCbvVocabulary("urn:gs1:epcisapp:rail:BizStep:shipping", "bizStep", "urn"));

    assertNull(converter.toCbvVocabulary(null, "bizStep", "urn"));
    assertEquals(
        "urn:gs1:epcisapp:rail:BizStep:shipping",
        converter.toCbvVocabulary("urn:gs1:epcisapp:rail:BizStep:shipping", null, "urn"));
    assertEquals(
        "urn:gs1:epcisapp:rail:BizStep:shipping",
        converter.toCbvVocabulary("urn:gs1:epcisapp:rail:BizStep:shipping", "bizStep", null));
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

    assertEquals(
        "https://ref.gs1.org/cbv/Disp-partially_dispensed",
        converter.toCbvVocabulary("cbv:Disp-partially_dispensed", "disposition", "webUri"));
    assertEquals(
        "https://ref.gs1.org/cbv/Disp-in_transit",
        converter.toCbvVocabulary("cbv:Disp-in_transit", "disposition", "webUri"));
    assertEquals(
        "https://ref.gs1.org/cbv/Disp-in_progress",
        converter.toCbvVocabulary("cbv:Disp-in_progress", "disposition", "webUri"));

    assertEquals(
        "urn:epcglobal:cbv:disp:in_progress",
        converter.toCbvVocabulary("cbv:Disp-in_progress", "disposition", "Urn"));

    assertEquals(
        "urn:gs1:epcisapp:rail:Disp:in_progress",
        converter.toCbvVocabulary("urn:gs1:epcisapp:rail:Disp:in_progress", "disposition", "urn"));

    assertNull(converter.toCbvVocabulary(null, "disposition", "urn"));
    assertNull(converter.toCbvVocabulary(null, "disposition", "webURI"));
    assertEquals(
        "urn:gs1:epcisapp:rail:Disp:in_progress",
        converter.toCbvVocabulary("urn:gs1:epcisapp:rail:Disp:in_progress", null, "urn"));
    assertEquals(
        "urn:gs1:epcisapp:rail:Disp:in_progress",
        converter.toCbvVocabulary("urn:gs1:epcisapp:rail:Disp:in_progress", "disposition", null));
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

    assertEquals(
        "https://ref.gs1.org/cbv/BTT-po",
        converter.toCbvVocabulary("cbv:BTT-po", "bizTransaction", "webUri"));
    assertEquals(
        "https://ref.gs1.org/cbv/BTT-inv",
        converter.toCbvVocabulary("cbv:BTT-inv", "bizTransactionList", "webUri"));
    assertEquals(
        "https://ref.gs1.org/cbv/BTT-pedigree",
        converter.toCbvVocabulary("cbv:BTT-pedigree", "bizTransaction", "webUri"));

    assertEquals(
        "urn:epcglobal:cbv:btt:po",
        converter.toCbvVocabulary("cbv:BTT-po", "bizTransaction", "Urn"));
    assertEquals(
        "urn:epcglobal:cbv:btt:inv",
        converter.toCbvVocabulary("cbv:BTT-inv", "bizTransaction", "Urn"));

    assertEquals(
        "urn:gs1:epcisapp:rail:btt:passage",
        converter.toCbvVocabulary("urn:gs1:epcisapp:rail:btt:passage", "bizTransaction", "weburi"));

    assertNull(converter.toCbvVocabulary(null, "bizTransaction", "urn"));
    assertNull(converter.toCbvVocabulary(null, "bizTransaction", "webURI"));
    assertEquals(
        "urn:gs1:epcisapp:rail:btt:passage",
        converter.toCbvVocabulary("urn:gs1:epcisapp:rail:btt:passage", null, "urn"));
    assertEquals(
        "urn:gs1:epcisapp:rail:btt:passage",
        converter.toCbvVocabulary("urn:gs1:epcisapp:rail:btt:passage", "bizTransaction", null));
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

    assertEquals(
        "https://ref.gs1.org/cbv/SDT-processing_party",
        converter.toCbvVocabulary("cbv:SDT-processing_party", "destinationList", "webUri"));
    assertEquals(
        "https://ref.gs1.org/cbv/SDT-location",
        converter.toCbvVocabulary("cbv:SDT-location", "source", "webUri"));
    assertEquals(
        "https://ref.gs1.org/cbv/SDT-owning_party",
        converter.toCbvVocabulary("cbv:SDT-owning_party", "Destination", "webUri"));

    assertEquals(
        "urn:epcglobal:cbv:sdt:owning_party",
        converter.toCbvVocabulary("cbv:SDT-owning_party", "source", "Urn"));
    assertEquals(
        "urn:epcglobal:cbv:sdt:location",
        converter.toCbvVocabulary("cbv:SDT-location", "Destination", "Urn"));

    assertEquals(
        "urn:gs1:epcisapp:rail:SDT:location",
        converter.toCbvVocabulary("urn:gs1:epcisapp:rail:SDT:location", "destination", "urn"));

    assertNull(converter.toCbvVocabulary(null, "destination", "urn"));
    assertNull(converter.toCbvVocabulary(null, "destination", "webURI"));
    assertEquals(" ", converter.toCbvVocabulary(" ", null, "urn"));
    assertEquals(
        "urn:gs1:epcisapp:rail:SDT:location",
        converter.toCbvVocabulary("urn:gs1:epcisapp:rail:SDT:location", null, "urn"));
    assertEquals(
        "urn:gs1:epcisapp:rail:SDT:location",
        converter.toCbvVocabulary("urn:gs1:epcisapp:rail:SDT:location", "destination", null));
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

    assertEquals(
        "https://ref.gs1.org/cbv/ER-did_not_occur",
        converter.toCbvVocabulary("cbv:ER-did_not_occur", "reason", "webUri"));
    assertEquals(
        "https://ref.gs1.org/cbv/ER-incorrect_data",
        converter.toCbvVocabulary("cbv:ER-incorrect_data", "reason", "webUri"));

    assertEquals(
        "urn:epcglobal:cbv:er:other",
        converter.toCbvVocabulary("cbv:ER-other", "errorDeclaration", "Urn"));
    assertEquals(
        "urn:epcglobal:cbv:er:did_not_occur",
        converter.toCbvVocabulary("cbv:ER-did_not_occur", "reason", "Urn"));

    assertEquals(
        "urn:gs1:epcisapp:rail:er:did_not_occur",
        converter.toCbvVocabulary("urn:gs1:epcisapp:rail:er:did_not_occur", "reason", "WebURI"));

    assertNull(converter.toCbvVocabulary(null, "reason", "urn"));
    assertNull(converter.toCbvVocabulary(null, "reason", "webURI"));
    assertEquals(" ", converter.toCbvVocabulary(" ", "reason", "urn"));
    assertEquals(
        "urn:gs1:epcisapp:rail:er:did_not_occur",
        converter.toCbvVocabulary("urn:gs1:epcisapp:rail:er:did_not_occur", null, "urn"));
    assertEquals(
        "urn:gs1:epcisapp:rail:er:did_not_occur",
        converter.toCbvVocabulary("urn:gs1:epcisapp:rail:er:did_not_occur", "reason", null));
  }
}
