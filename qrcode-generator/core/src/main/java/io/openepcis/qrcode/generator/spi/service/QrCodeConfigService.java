package io.openepcis.qrcode.generator.spi.service;

import io.openepcis.qrcode.generator.QrCodeConfig;
import io.openepcis.qrcode.generator.spi.QrCodeConfigProvider;
import io.openepcis.qrcode.generator.spi.impl.CoreQrCodeConfigProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Service that applies default configuration values based on available SPI providers.
 */
public class QrCodeConfigService {

    private static QrCodeConfigService _instance;
    private final List<QrCodeConfigProvider> serviceProviders;

    private QrCodeConfigService(final List<QrCodeConfigProvider> serviceProviders) {
        this.serviceProviders = serviceProviders == null || serviceProviders.isEmpty() ? new ArrayList<>() : serviceProviders;

        if (this.serviceProviders.isEmpty()) {
            this.serviceProviders.add(new CoreQrCodeConfigProvider());
        }
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
