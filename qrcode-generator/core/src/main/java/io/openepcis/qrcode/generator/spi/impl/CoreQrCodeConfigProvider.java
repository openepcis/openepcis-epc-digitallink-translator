package io.openepcis.qrcode.generator.spi.impl;

import io.openepcis.qrcode.generator.QrCodeConfig;
import io.openepcis.qrcode.generator.spi.QrCodeConfigProvider;

/**
 * Default SPI provider for {@link QrCodeConfig}.
 * <p> This provider is used when the configuration name is {@code null} or empty, or when no matching extension provider is found. </p>
 */
public class CoreQrCodeConfigProvider implements QrCodeConfigProvider {

    /**
     * Returns true for all so if custom is not provided then use default.
     *
     * @param designPreset the name from the QR code configuration.
     * @return {@code true} if the name is null or empty.
     */
    @Override
    public boolean supports(final String designPreset) {
        return true;
    }

    /**
     * Returns the original configuration without modification.
     *
     * @param qrCodeConfig the original QR code configuration.
     * @return the same {@link QrCodeConfig} without additional defaults.
     */
    @Override
    public QrCodeConfig customizeConfig(final QrCodeConfig qrCodeConfig) {
        return qrCodeConfig;
    }
}
