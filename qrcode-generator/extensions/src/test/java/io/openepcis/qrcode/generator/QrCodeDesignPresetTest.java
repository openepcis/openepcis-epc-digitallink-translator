package io.openepcis.qrcode.generator;

import io.openepcis.qrcode.generator.spi.QrCodeConfigProvider;
import io.openepcis.qrcode.generator.spi.impl.CoreQrCodeConfigProvider;
import io.openepcis.qrcode.generator.spi.service.QrCodeConfigService;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression tests for design-preset resolution: the catch-all core provider must never shadow
 * the theme providers, presets must be applicable from the bare logo file names exposed by the
 * design-presets endpoint, and user-provided config values must survive preset application.
 */
public class QrCodeDesignPresetTest {

    private final QrCodeConfigService configService = QrCodeConfigService.getInstance();
    private final QrCodeGenerator generator = new QrCodeGenerator();

    @Test
    public void coreProviderIsConsultedLast() {
        final List<QrCodeConfigProvider> providers = configService.getAllProviders();
        assertInstanceOf(CoreQrCodeConfigProvider.class, providers.get(providers.size() - 1),
                "catch-all core provider must be last, otherwise presets are silently ignored");
    }

    @Test
    public void openEpcisPresetAppliesThemeRegardlessOfProviderOrder() {
        final QrCodeConfig resolved = configService.applyDefaultConfig(
                QrCodeConfig.builder().data("https://openepcis.io/").designPreset("openepcis").build());

        assertEquals("OpenEPCIS", resolved.getDesignPreset());
        assertNotNull(resolved.getLogoResourceUrl(), "preset logo must be applied");
        assertTrue(resolved.getLogoScale() > 0f);
    }

    @Test
    public void gs1PresetAppliesThemeRegardlessOfProviderOrder() {
        final QrCodeConfig resolved = configService.applyDefaultConfig(
                QrCodeConfig.builder().data("https://www.gs1.org/").designPreset("gs1").build());

        assertEquals("GS1", resolved.getDesignPreset());
        assertNotNull(resolved.getLogoResourceUrl(), "preset logo must be applied");
        assertEquals(600, resolved.getQrWidth());
    }

    @Test
    public void unknownPresetFallsBackToUnmodifiedConfig() {
        final QrCodeConfig original = QrCodeConfig.builder().data("https://openepcis.io/").designPreset("does-not-exist").build();
        final QrCodeConfig resolved = configService.applyDefaultConfig(original);
        assertNull(resolved.getLogoResourceUrl());
    }

    @Test
    public void presetGenerationProducesLogoBearingImage() {
        // With the preset applied the module renders a logo; must not throw and must not be empty.
        final byte[] bytes = generator.generateQRCode(
                QrCodeConfig.builder().data("https://id.gs1.org/01/09520123456788").designPreset("OpenEPCIS").build());
        assertTrue(bytes.length > 0);
    }

    @Test
    public void bareClasspathLogoFileNameIsResolved() {
        // /qr/design-presets lists logos by bare file name; generating from such a config must work.
        final byte[] bytes = generator.generateQRCode(
                QrCodeConfig.builder()
                        .data("https://id.gs1.org/01/09520123456788")
                        .logoResourceUrl("openepcis-logo.png")
                        .logoScale(0.2f)
                        .build());
        assertTrue(bytes.length > 0);
    }

    @Test
    public void userOverridesSurvivePresetApplication() {
        final QrCodeConfig resolved = configService.applyDefaultConfig(
                QrCodeConfig.builder()
                        .data("https://www.gs1.org/")
                        .designPreset("gs1")
                        .qrWidth(800)
                        .qrHeight(800)
                        .gradientStart(Color.RED)
                        .gradientEnd(Color.RED)
                        .moduleShape(QrCodeConfig.ModuleShape.DOT)
                        .displayLabel("ACME GmbH")
                        .build());

        assertEquals(800, resolved.getQrWidth());
        assertEquals(800, resolved.getQrHeight());
        assertEquals(Color.RED, resolved.getGradientStart());
        assertEquals(QrCodeConfig.ModuleShape.DOT, resolved.getModuleShape());
        assertEquals("ACME GmbH", resolved.getDisplayLabel());
    }
}
