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
package io.openepcis.qrcode.generator.spi.service;

import io.openepcis.qrcode.generator.QrCodeConfig;
import io.openepcis.qrcode.generator.spi.QrCodeConfigProvider;
import io.openepcis.qrcode.generator.spi.impl.CoreQrCodeConfigProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Service that applies default configuration values based on available SPI providers.
 */
public class QrCodeConfigService {

    private static QrCodeConfigService _instance;
    private final List<QrCodeConfigProvider> serviceProviders;

    private QrCodeConfigService(final List<QrCodeConfigProvider> serviceProviders) {
        this.serviceProviders = serviceProviders == null || serviceProviders.isEmpty() ? new ArrayList<>() : new ArrayList<>(serviceProviders);

        if (this.serviceProviders.isEmpty()) {
            this.serviceProviders.add(new CoreQrCodeConfigProvider());
        }

        // CoreQrCodeConfigProvider.supports() matches every preset name, so it must be consulted
        // last — otherwise ServiceLoader classpath order decides whether design presets apply.
        this.serviceProviders.sort(Comparator.comparingInt(p -> p instanceof CoreQrCodeConfigProvider ? 1 : 0));
    }

    public static synchronized QrCodeConfigService newInstance() {
        // Load all providers registered via SPI
        return new QrCodeConfigService(
                ServiceLoader.load(QrCodeConfigProvider.class)
                        .stream()
                        .map(ServiceLoader.Provider::get)
                        .toList()
        );
    }

    public static synchronized QrCodeConfigService getInstance() {
        if (_instance == null) {
            _instance = newInstance();
        }
        return _instance;
    }

    /**
     * Applies default configuration values by locating a provider that supports the given name.
     *
     * @param qrCodeConfig the user-provided {@link QrCodeConfig}.
     * @return a customized {@link QrCodeConfig} with defaults applied.
     */
    public QrCodeConfig applyDefaultConfig(final QrCodeConfig qrCodeConfig) {
        // Iterate through providers to find one that supports the given name
        for (final QrCodeConfigProvider provider : serviceProviders) {
            if (provider.supports(qrCodeConfig.getDesignPreset())) {
                // If a matching provider is found, customize the configuration
                return provider.customizeConfig(qrCodeConfig);
            }
        }

        // Fallback to the core provider if no specific provider is found
        return qrCodeConfig;
    }

    /**
     * Method to return all the providers available in the system.
     * Used within the rest endpoint qr-design-presets to get all pre-configured design presets.
     *
     * @return returns List of the available providers QrCodeConfigProvider.
     */
    public List<QrCodeConfigProvider> getAllProviders() {
        return this.serviceProviders;
    }
}
