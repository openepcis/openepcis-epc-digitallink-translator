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

        final Color gs1Blue = new Color(0, 44, 108, 255);
        final Color gs1Orange = new Color(242, 99, 52, 255);

        // Theme values only fill fields the caller left at their builder defaults, so every
        // QrCodeConfig option stays usable together with the preset. (Reference comparison with
        // the Color constants is intentional: Jackson-provided values are always new instances.)
        return QrCodeConfig.builder()
                .data(qrCodeConfig.getData())
                .designPreset("GS1")
                .mimeType(qrCodeConfig.getMimeType() != null ? qrCodeConfig.getMimeType() : "image/png")
                .qrWidth(qrCodeConfig.getQrWidth() != 400 ? qrCodeConfig.getQrWidth() : 600)
                .qrHeight(qrCodeConfig.getQrHeight() != 400 ? qrCodeConfig.getQrHeight() : 600)
                .margin(qrCodeConfig.getMargin() != 4 ? qrCodeConfig.getMargin() : 2)
                .backgroundColor(qrCodeConfig.getBackgroundColor() != Color.WHITE ? qrCodeConfig.getBackgroundColor() : gs1Orange)
                .gradientStart(qrCodeConfig.getGradientStart() != Color.BLACK ? qrCodeConfig.getGradientStart() : gs1Blue)
                .gradientEnd(qrCodeConfig.getGradientEnd() != Color.BLACK ? qrCodeConfig.getGradientEnd() : gs1Blue)
                .finderColor(qrCodeConfig.getFinderColor() != Color.BLACK ? qrCodeConfig.getFinderColor() : gs1Blue)
                .useRadialGradient(true)
                .drawFinderGradient(true)
                .moduleShape(qrCodeConfig.getModuleShape())
                .moduleName(qrCodeConfig.getModuleName())
                .drawShadows(qrCodeConfig.isDrawShadows())
                .shadowColor(qrCodeConfig.getShadowColor())
                .shadowOffsetPct(qrCodeConfig.getShadowOffsetPct())
                .displayLabel(qrCodeConfig.getDisplayLabel())
                .displayLabelFontColor(qrCodeConfig.getDisplayLabelFontColor())
                .logoResourceUrl(logoResourceUrl)
                .logoScale(0.16f)
                .addHri(qrCodeConfig.isAddHri())
                .compressDigitalLink(qrCodeConfig.isCompressDigitalLink())
                .compressWithUppercase(qrCodeConfig.isCompressWithUppercase())
                .build();
    }
}
