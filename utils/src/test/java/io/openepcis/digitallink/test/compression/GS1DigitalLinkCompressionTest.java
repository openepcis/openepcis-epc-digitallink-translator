/*
 * Copyright 2022-2026 benelog GmbH & Co. KG
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
package io.openepcis.digitallink.test.compression;

import io.openepcis.digitallink.toolkit.GS1DigitalLinkCompression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GS1DigitalLinkCompressionTest {

    private static final GS1DigitalLinkCompression compressor = new GS1DigitalLinkCompression();


    @Test
    public void verifyVariousDigitalLinks() {
        var digitalLinks = List.of("https://id.dev.epcis.cloud/01/09312345678907",
                "https://example.org/01/00054123450013/10/ABC123");

        /*
            TODO: fix below where padding of 0 gets altered for the decompressed digital link.
             Note: This issue is present in javascript based GS1DigitalLinkToolkit.js as well.
             input: https://example.com/01/614141123452/lot/ABC/21/00001?17=190400
             output:  https://example.com/01/00614141123452/10/ABC/21/1?17=190400

             TODO: fix index out of bound exception for the below digital link
              https://example.org/01/00054123450013/10/ABC123?3103=000189&3923=2172
         */


        digitalLinks.forEach(uri -> {
            var compressed = compressor.compressGS1DigitalLink(uri, false, false);
            var decompressed = compressor.decompressGS1DigitalLink(compressed);
            Assertions.assertEquals(uri, decompressed);
        });
    }
}
