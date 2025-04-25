package io.openepcis.qrcode.generator;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Slf4j
public class QrCodeGeneratorTest {

    private QrCodeGenerator barCodeGenerator;

    @Before
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
