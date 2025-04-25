package io.openepcis.qrcode.generator.spi.impl;

import io.openepcis.qrcode.generator.QrCodeConfig;
import io.openepcis.qrcode.generator.spi.QrCodeConfigProvider;

import java.awt.*;

/**
 * SPI provider for "GS1" that applies default configurations for GS1 theme.
 */
public class GS1QrCodeConfigHandler implements QrCodeConfigProvider {

    /**
     * Checks whether this provider supports the provided name.
     * <p> Returns true if the name equals "GS1" (ignoring case).</p>
     *
     * @param name the name from the QR code configuration.
     * @return true if name matches "GS1".
     */
    @Override
    public boolean supports(final String name) {
        return "GS1".equalsIgnoreCase(name);
    }

    /**
     * Customizes the provided {@link QrCodeConfig} by applying GS1-specific default values.
     *
     * @param qrCodeConfig the original QR code configuration.
     * @return a new {@link QrCodeConfig} instance with GS1 defaults applied.
     */
    @Override
    public QrCodeConfig customizeConfig(QrCodeConfig qrCodeConfig) {
        final String logoResourceUrl = GS1QrCodeConfigHandler.class.getClassLoader().getResource("gs1-logo.png").toString();

        return QrCodeConfig.builder()
                .data(qrCodeConfig.getData())
                .designPreset("GS1")
                .mimeType(qrCodeConfig.getMimeType() != null ? qrCodeConfig.getMimeType() : "image/png")
                .qrWidth(600)
                .qrHeight(600)
                .margin(2)
                .backgroundColor(new Color(242, 99, 52, 255))
                .gradientStart(new Color(0, 44, 108, 255))
                .gradientEnd(new Color(0, 44, 108, 255))
                .finderColor(new Color(0, 44, 108, 255))
                .useRadialGradient(true)
                .drawFinderGradient(true)
                .displayLabel("GS1")
                .displayLabelFontColor(new Color(0, 44, 108, 255))
                .logoResourceUrl(logoResourceUrl)
                .logoScale(0.16f)
                .build();
    }
}
