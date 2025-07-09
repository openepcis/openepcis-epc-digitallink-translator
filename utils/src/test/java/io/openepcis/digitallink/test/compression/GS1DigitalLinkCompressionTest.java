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
