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
