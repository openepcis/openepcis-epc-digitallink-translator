/*
 * Copyright (c) 2022-2025 benelog GmbH & Co. KG
 * All rights reserved.
 *
 * Unauthorized copying, modification, distribution,
 * or use of this work, via any medium, is strictly prohibited.
 *
 * benelog GmbH & Co. KG reserves all rights not expressly granted herein,
 * including the right to sell licenses for using this work.
 */
package io.openepcis.qrcode.generator.resource;

import io.openepcis.qrcode.generator.QrCodeConfig;
import io.openepcis.qrcode.generator.resource.params.QrCodeGenerationParams;
import io.openepcis.qrcode.generator.resource.service.QrCodeService;
import io.openepcis.qrcode.generator.resource.specs.QrCodeGeneratorApi;
import io.openepcis.qrcode.generator.util.QrCodeConstants;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class QrCodeGeneratorResource implements QrCodeGeneratorApi {

    private final QrCodeService qrCodeService;

    @ConfigProperty(name = "gs1.digital-link.base-url", defaultValue = QrCodeConstants.GS1_IDENTIFIER_DOMAIN)
    String baseUrl;


    // Method to generate the QR Code based on user provided specifications as QrCodeConfig and return
    @Override
    public Uni<Response> generate(@BeanParam QrCodeGenerationParams params,
                                        QrCodeConfig qrConfig) {

        // delegate to service which applies params (Accept, designPreset, hri, compressed)
        return qrCodeService.generate(params, qrConfig);
    }

    // Method to generate the QR Code based on a Digital Link path by adding domain and return
    @Override
    public Uni<Response> fetch(@BeanParam QrCodeGenerationParams params,
                                   @PathParam("linkPath") String linkPath) {
        final String dlUrl = baseUrl + Optional.ofNullable(linkPath).orElse("");
        final QrCodeConfig qrCodeConfig = QrCodeConfig.builder().data(dlUrl).build();
        return qrCodeService.generate(params, qrCodeConfig);
    }

    // Method to provide various QR code generation options like img/png, img/svg, etc.
    @Override
    public Uni<Response> options() {
        return Uni.createFrom().item(
                Response.ok()
                        .header("Accept-Get", String.join(",", QrCodeConstants.ACCEPT_HEADER))
                        .header("Accept-Post", String.join(",", QrCodeConstants.ACCEPT_HEADER)).build()
        );
    }

    // Method to list all available design presets for QR Code generation
    @Override
    public Uni<List<QrCodeConfig>> listDesignPresets() {
        return qrCodeService.listPresets();
    }
}
