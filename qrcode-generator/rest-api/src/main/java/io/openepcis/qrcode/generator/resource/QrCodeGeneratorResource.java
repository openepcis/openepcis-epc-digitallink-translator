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
package io.openepcis.qrcode.generator.resource;

import io.openepcis.qrcode.generator.QrCodeConfig;
import io.openepcis.qrcode.generator.exception.QrCodeGeneratorException;
import io.openepcis.qrcode.generator.resource.params.QrCodeGenerationParams;
import io.openepcis.qrcode.generator.resource.service.QrCodeService;
import io.openepcis.qrcode.generator.resource.specs.QrCodeGeneratorApi;
import io.openepcis.qrcode.generator.util.QrCodeConstants;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class QrCodeGeneratorResource implements QrCodeGeneratorApi {
    private final QrCodeService qrCodeService;
    @ConfigProperty(name = "gs1.digital-link.base-url", defaultValue = QrCodeConstants.GS1_IDENTIFIER_DOMAIN)
    String baseUrl;

    // Method to generate the QR Code based on user provided specifications as QrCodeConfig and return
    @Override
    public Uni<Response> generate(@BeanParam QrCodeGenerationParams params, QrCodeConfig qrConfig) {
        // delegate to service which applies params (Accept, designPreset, hri, compressed)
        return qrCodeService.generate(params, qrConfig);
    }

    // Method to generate the QR Code based on a Digital Link path by adding domain and return
    @Override
    public Uni<Response> fetch(@BeanParam QrCodeGenerationParams params, @PathParam("linkPath") String linkPath) {
        if (StringUtils.isBlank(linkPath)) {
            throw new QrCodeGeneratorException("Cannot generate QR Code : Invalid Digital Link path, cannot be blank.");
        }
        String dlUrl;
        try {
            final URI uri = new URI(linkPath);
            // If linkPath is absolute, use as-is without appending baseUrl
            if (uri.isAbsolute()) {
                dlUrl = linkPath;
            } else {
                // Append baseUrl + normalized relative path
                dlUrl = qrCodeService.normalizePathWithBaseUrl(baseUrl, linkPath);
            }
        } catch (URISyntaxException e) {
            // If URI is invalid then treat it as a relative path
            dlUrl = qrCodeService.normalizePathWithBaseUrl(baseUrl, linkPath);
        }
        final QrCodeConfig qrCodeConfig = QrCodeConfig.builder().data(dlUrl).build();
        return qrCodeService.generate(params, qrCodeConfig);
    }

    // Method to provide various QR code generation options like img/png, img/svg, etc.
    @Override
    public Uni<Response> options() {
        return Uni.createFrom().item(Response.ok().header("Accept-Get", String.join(",", QrCodeConstants.ACCEPT_HEADER)).header("Accept-Post", String.join(",", QrCodeConstants.ACCEPT_HEADER)).build());
    }

    // Method to list all available design presets for QR Code generation
    @Override
    public Uni<List<QrCodeConfig>> listDesignPresets() {
        return qrCodeService.listPresets();
    }

    public QrCodeGeneratorResource(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }
}
