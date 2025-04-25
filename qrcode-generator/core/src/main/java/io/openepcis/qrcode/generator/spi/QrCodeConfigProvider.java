package io.openepcis.qrcode.generator.spi;

import io.openepcis.qrcode.generator.QrCodeConfig;

/**
 * SPI interface for customizing {@link QrCodeConfig}.
 * <p> Implementations can provide default configuration values based on pre-defined names. </p>
 */
public interface QrCodeConfigProvider {

    /**
     * Determines whether this provider supports the given configuration name.
     *
     * @param designPreset the name provided in the QR code configuration.
     * @return {@code true} if the provider supports the given name; otherwise {@code false}.
     */
    boolean supports(final String designPreset);

    /**
     * Customizes the provided {@link QrCodeConfig} by applying default values.
     *
     * @param qrCodeConfig the original QR code configuration supplied by the user.
     * @return a new {@link QrCodeConfig} instance with default values applied.
     */
    QrCodeConfig customizeConfig(final QrCodeConfig qrCodeConfig);
}
