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
