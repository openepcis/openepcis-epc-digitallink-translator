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
package io.openepcis.qrcode.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class QrCodeGeneratorTest {

    private QrCodeGenerator barCodeGenerator;

    @BeforeEach
    public void before() throws Exception {
        barCodeGenerator = new QrCodeGenerator();
    }

    // Use the default OpenEPCIS config to generate the QR Code
    @Test
    public void openEPCISQrCodeTest() throws IOException {
        // Qr code with only data and outputFilename (required properties only)
        final QrCodeConfig minimalConfig = QrCodeConfig.builder()
                .data("https://openepcis.io/")
                .designPreset("OpenEPCIS")
                .build();

        // assert that no exception is thrown during QR code generation
        assertDoesNotThrow(() -> barCodeGenerator.generateQRCode(minimalConfig), "QR code generation should not throw an exception");
    }

    // Use the default GS1 config to generate the QR Code
    @Test
    public void gs1QrCodeTest() throws IOException {
        // Qr code with only data and outputFilename (required properties only)
        final QrCodeConfig minimalConfig = QrCodeConfig.builder()
                .data("https://www.gs1.org/")
                .designPreset("GS1")
                .build();

        // assert that no exception is thrown during QR code generation
        assertDoesNotThrow(() -> barCodeGenerator.generateQRCode(minimalConfig), "QR code generation should not throw an exception");
    }

    // Use the default plain config to generate QR code
    @Test
    public void genericQrCodeTest(){
        // Qr code with only data and outputFilename (required properties only)
        final QrCodeConfig minimalConfig = QrCodeConfig.builder()
                .data("https://openepcis.io/")
                .designPreset("Test")
                .build();

        // assert that no exception is thrown during QR code generation
        assertDoesNotThrow(() -> barCodeGenerator.generateQRCode(minimalConfig), "QR code generation should not throw an exception");
    }
}
