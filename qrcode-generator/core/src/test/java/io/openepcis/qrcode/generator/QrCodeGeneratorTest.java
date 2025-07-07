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
package io.openepcis.qrcode.generator;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Slf4j
public class QrCodeGeneratorTest {

    private QrCodeGenerator barCodeGenerator;

    // Attempt to load a logo from resources
    private final String logoResourceUrl = QrCodeGeneratorTest.class.getClassLoader().getResource("images/Logo.png").toString();

    @BeforeEach
    public void before() throws Exception {
        barCodeGenerator = new QrCodeGenerator();
    }

    @Test
    public void simpleQrCodePlainTest() throws IOException {
        // Qr code with only data and outputFilename (required properties only)
        final QrCodeConfig minimalConfig = QrCodeConfig.builder()
                .data("https://id.gs1.org/01/095201234567898989899889?17=201225&3103=000195&3922=0299&19=201225&21=000195")
                .mimeType("image/png")
                .moduleShape(QrCodeConfig.ModuleShape.ROUNDED_RECT)
                .addHri(true)
                .build();

        // assert that no exception is thrown during QR code generation
        assertDoesNotThrow(() -> barCodeGenerator.generateQRCode(minimalConfig), "QR code generation should not throw an exception");
    }

    @Test
    public void simpleQrCodeWithGradientHeartsTest() {
        // Qr code with only data and outputFilename (required properties only)
        final QrCodeConfig minimalConfig =
                QrCodeConfig.builder()
                        .data("https://www.example.com")
                        .qrWidth(500)
                        .qrHeight(500)
                        .margin(5)
                        .gradientStart(Color.RED)
                        .gradientEnd(Color.BLUE)
                        .useRadialGradient(false)
                        .drawFinderGradient(true)
                        .moduleShape(QrCodeConfig.ModuleShape.HEART)
                        .addHri(true)
                        .build();

        // assert that no exception is thrown during QR code generation
        assertDoesNotThrow(() -> barCodeGenerator.generateQRCode(minimalConfig), "QR code generation should not throw an exception");
    }

    @Test
    public void simpleQrCodeWithGradientDotsTest() {
        // Qr code with only data and outputFilename (required properties only)
        final QrCodeConfig minimalConfig =
                QrCodeConfig.builder()
                        .data("https://www.example.com")
                        .qrWidth(500)
                        .qrHeight(500)
                        .margin(5)
                        .gradientStart(Color.DARK_GRAY)
                        .gradientEnd(Color.GREEN)
                        .moduleShape(QrCodeConfig.ModuleShape.DOT)
                        .build();

        // assert that no exception is thrown during QR code generation
        assertDoesNotThrow(() -> barCodeGenerator.generateQRCode(minimalConfig), "QR code generation should not throw an exception");
    }

    @Test
    public void simpleQrCodeWithLogoest() {
        final QrCodeConfig minimalConfig =
                QrCodeConfig.builder()
                        .data("https://www.example.com")
                        .logoResourceUrl(logoResourceUrl)
                        .logoScale(0.2f)
                        .build();

        // assert that no exception is thrown during QR code generation
        assertDoesNotThrow(() -> barCodeGenerator.generateQRCode(minimalConfig), "QR code generation should not throw an exception");
    }

    @Test
    public void qrCodeWithCustomGradientLogoTest() {
        final QrCodeConfig config =
                QrCodeConfig.builder()
                        .data("https://www.example.com")
                        .qrWidth(400)
                        .qrHeight(400)
                        .margin(4)
                        .backgroundColor(Color.WHITE)
                        .gradientStart(new Color(255, 112, 158)) // Pink
                        .gradientEnd(new Color(148, 0, 211)) // Dark Purple
                        .useRadialGradient(true)
                        .finderColor(Color.BLACK)
                        .drawFinderGradient(false)
                        .moduleShape(QrCodeConfig.ModuleShape.CIRCLE)
                        .drawShadows(false)
                        .shadowColor(new Color(0, 0, 0, 50))
                        .shadowOffsetPct(0.1f)
                        .logoResourceUrl(logoResourceUrl)
                        .logoScale(0.2f)
                        .build();

        // assert that no exception is thrown during QR code generation
        assertDoesNotThrow(() -> barCodeGenerator.generateQRCode(config), "QR code generation should not throw an exception");
    }

    @Test
    public void qrCodeWithCustomGradientLogo2Test() {
        final QrCodeConfig config =
                QrCodeConfig.builder()
                        .data("https://www.example.com")
                        .qrWidth(500)
                        .qrHeight(500)
                        .margin(6)
                        .backgroundColor(new Color(240, 240, 240)) // light gray
                        .gradientStart(new Color(64, 224, 208)) // Turquoise
                        .gradientEnd(new Color(0, 0, 128)) // Navy
                        .useRadialGradient(true)
                        .finderColor(new Color(128, 0, 128)) // Purple for fallback
                        .drawFinderGradient(true)
                        .moduleShape(QrCodeConfig.ModuleShape.ROUNDED_RECT)
                        .drawShadows(false)
                        .shadowColor(null)
                        .shadowOffsetPct(0.0f)
                        .logoResourceUrl(null)
                        .logoScale(0.0f)
                        .build();

        // assert that no exception is thrown during QR code generation
        assertDoesNotThrow(() -> barCodeGenerator.generateQRCode(config), "QR code generation should not throw an exception");
    }

    @Test
    public void qrCodeWithCustomGradientDLTest() {
        final QrCodeConfig config =
                QrCodeConfig.builder()
                        .data("https://id.gs1.org/01/03453120000011")
                        .qrWidth(400)
                        .qrHeight(400)
                        .margin(8)
                        .backgroundColor(Color.WHITE)
                        .gradientStart(new Color(255, 127, 39)) // GS1 Orange
                        .gradientEnd(new Color(0, 102, 153)) // GS1 Blue
                        .useRadialGradient(false)
                        .finderColor(Color.BLACK)
                        .drawFinderGradient(true)
                        .moduleShape(QrCodeConfig.ModuleShape.SQUARE)
                        .drawShadows(false)
                        .shadowColor(null)
                        .shadowOffsetPct(0f)
                        .logoResourceUrl(null)
                        .logoScale(0f)
                        .build();

        // assert that no exception is thrown during QR code generation
        assertDoesNotThrow(() -> barCodeGenerator.generateQRCode(config), "QR code generation should not throw an exception");
    }

    @Test
    public void qrCodeWithCustomGradientDL2Test() {
        final QrCodeConfig config =
                QrCodeConfig.builder()
                        .data("https://id.gs1.org/01/09521568256452/21/200")
                        .qrWidth(400)
                        .qrHeight(400)
                        .margin(4)
                        .backgroundColor(new Color(0, 0, 0, 0)) // Fully transparent background
                        .gradientStart(new Color(0xF26334)) // GS1 Primary
                        .gradientEnd(new Color(0xF26334)) // GS1 Primary
                        .useRadialGradient(false)
                        .finderColor(new Color(0xF26334))
                        .drawFinderGradient(false)
                        .moduleShape(QrCodeConfig.ModuleShape.ROUNDED_RECT)
                        .drawShadows(true)
                        .shadowColor(new Color(0, 0, 0, 50))
                        .shadowOffsetPct(0.1f)
                        .logoResourceUrl(logoResourceUrl)
                        .logoScale(0.2f)
                        .build();

        // assert that no exception is thrown during QR code generation
        assertDoesNotThrow(() -> barCodeGenerator.generateQRCode(config), "QR code generation should not throw an exception");
    }

    @Test
    public void qrCodeWithGS1ColorsTest() {
        final QrCodeConfig config =
                QrCodeConfig.builder()
                        .data("https://id.gs1.org/01/09521568256452/21/200")
                        .qrWidth(400)
                        .qrHeight(400)
                        .margin(4)
                        .backgroundColor(new Color(0x002C6C)) // Fully transparent background
                        .gradientStart(new Color(0xF26334)) // GS1 Primary
                        .gradientEnd(new Color(0xF26334)) // GS1 Primary
                        .useRadialGradient(false)
                        .finderColor(new Color(0xF26334))
                        .drawFinderGradient(false)
                        .moduleShape(QrCodeConfig.ModuleShape.ROUNDED_RECT)
                        .drawShadows(true)
                        .shadowColor(new Color(0, 0, 0, 50))
                        .shadowOffsetPct(0.1f)
                        .displayLabel("GS1 Germany")
                        .displayLabelFontColor(new Color(0xF26334))
                        .build();

        barCodeGenerator.generateQRCode(config);
    }

    @Test
    public void qrCodeWithTransparentTest3() {
        final QrCodeConfig config =
                QrCodeConfig.builder()
                        .data("https://id.gs1.org/01/09521568256452/21/200")
                        .qrWidth(400)
                        .qrHeight(400)
                        .margin(4)
                        .backgroundColor(new Color(0xF26334)) // Fully transparent background
                        .gradientStart(new Color(0x002C6C)) // GS1 Primary
                        .gradientEnd(new Color(0x002C6C)) // GS1 Primary
                        .useRadialGradient(false)
                        .finderColor(new Color(0x002C6C))
                        .drawFinderGradient(false)
                        .moduleShape(QrCodeConfig.ModuleShape.LETTER)
                        .moduleName("GS1")
                        .drawShadows(true)
                        .shadowColor(new Color(0, 0, 0, 50))
                        .shadowOffsetPct(0.1f)
                        .logoResourceUrl(logoResourceUrl)
                        .logoScale(0.2f)
                        .displayLabel("GS1 Germany")
                        .displayLabelFontColor(new Color(0x002C6C))
                        .build();

        // assert that no exception is thrown during QR code generation
        assertDoesNotThrow(() -> barCodeGenerator.generateQRCode(config), "QR code generation should not throw an exception");
    }

    @Test
    public void qrCodeWithTransparentTest4() {
        final QrCodeConfig config =
                QrCodeConfig.builder()
                        .data("https://id.gs1.org/01/09521568256452/21/200")
                        .qrWidth(400)
                        .qrHeight(400)
                        .margin(4)
                        .backgroundColor(new Color(0xF26334)) // Fully transparent background
                        .gradientStart(new Color(0x002C6C)) // GS1 Primary
                        .gradientEnd(new Color(0x002C6C)) // GS1 Primary
                        .useRadialGradient(false)
                        .finderColor(new Color(0x002C6C))
                        .drawFinderGradient(false)
                        .moduleShape(QrCodeConfig.ModuleShape.STAR)
                        .moduleName("GS1")
                        .drawShadows(true)
                        .shadowColor(new Color(0, 0, 0, 50))
                        .shadowOffsetPct(0.1f)
                        .logoResourceUrl(logoResourceUrl)
                        .logoScale(0.2f)
                        .displayLabel("GS1 Germany")
                        .displayLabelFontColor(new Color(0x002C6C))
                        .addHri(true)
                        .build();

        // assert that no exception is thrown during QR code generation
        assertDoesNotThrow(() -> barCodeGenerator.generateQRCode(config), "QR code generation should not throw an exception");
    }

    // Ensure not to use the default OpenEPCIS config from extensions module to generate the QR Code
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

    // Ensure not to use the default GS1 config from extensions module to generate the QR Code
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

    // Ensure not to use the default OpenEPCIS config from extensions module to generate the QR Code
    @Test
    public void openEPCISNameQrCodeTest() throws IOException {
        // Qr code with only data and outputFilename (required properties only)
        final QrCodeConfig minimalConfig =
                QrCodeConfig.builder()
                        .data("https://openepcis.io/")
                        .designPreset("OpenEPCIS")
                        .qrWidth(500)
                        .qrHeight(500)
                        .margin(5)
                        .gradientStart(Color.RED)
                        .gradientEnd(Color.BLUE)
                        .useRadialGradient(false)
                        .drawFinderGradient(true)
                        .moduleShape(QrCodeConfig.ModuleShape.HEART)
                        .build();

        // assert that no exception is thrown during QR code generation
        assertDoesNotThrow(() -> barCodeGenerator.generateQRCode(minimalConfig), "QR code generation should not throw an exception");
    }
}
