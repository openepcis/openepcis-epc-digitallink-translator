package io.openepcis.qrcode.generator.spi.impl;

import io.openepcis.qrcode.generator.QrCodeConfig;
import io.openepcis.qrcode.generator.spi.QrCodeConfigProvider;

import java.awt.*;

/**
 * SPI provider for "OpenEPCIS" that applies default configurations for OpenEPCIS.
 */
public class OpenEPCISQrCodeConfigHandler implements QrCodeConfigProvider {

    /**
     * Checks whether this provider supports the given name.
     * <p> For OpenEPCIS, this returns true if the name equals "OpenEPCIS" (case-insensitive). </p>
     *
     * @param name the name provided in the QR code configuration.
     * @return true if name matches "OpenEPCIS".
     */
    @Override
    public boolean supports(final String name) {
        return "OpenEPCIS".equalsIgnoreCase(name);
    }

    /**
     * Customizes the provided {@link QrCodeConfig} by applying OpenEPCIS-specific default values.
     *
     * @param qrCodeConfig the original QR code configuration.
     * @return a new {@link QrCodeConfig} instance with OpenEPCIS defaults applied.
     */
    @Override
    public QrCodeConfig customizeConfig(final QrCodeConfig qrCodeConfig) {
        final String logoResourceUrl = OpenEPCISQrCodeConfigHandler.class.getClassLoader().getResource("openepcis-logo.png").toString();

        final Color color = new Color( 45, 77, 157);

        return QrCodeConfig.builder()
                .data(qrCodeConfig.getData())
                .designPreset("OpenEPCIS")
                .mimeType(qrCodeConfig.getMimeType() != null ? qrCodeConfig.getMimeType() : "image/png")
                .qrWidth(qrCodeConfig.getQrWidth())
                .qrHeight(qrCodeConfig.getQrHeight())
                .margin(qrCodeConfig.getMargin())
                .backgroundColor(qrCodeConfig.getBackgroundColor())
                .gradientStart(qrCodeConfig.getGradientStart() != Color.BLACK ? qrCodeConfig.getGradientStart():color)
                .gradientEnd(qrCodeConfig.getGradientEnd() != Color.BLACK ? qrCodeConfig.getGradientEnd():color)
                .useRadialGradient(true)
                .drawFinderGradient(true)
                .drawShadows(qrCodeConfig.isDrawShadows())
                .moduleShape(qrCodeConfig.getModuleShape())
                .logoResourceUrl(logoResourceUrl)
                .logoScale(0.2f)
                .addHri(qrCodeConfig.isAddHri())
                .compressDigitalLink(qrCodeConfig.isCompressDigitalLink())
                .compressWithUppercase(qrCodeConfig.isCompressWithUppercase())
                .build();
    }
}
