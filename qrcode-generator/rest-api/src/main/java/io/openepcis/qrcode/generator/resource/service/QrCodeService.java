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
package io.openepcis.qrcode.generator.resource.service;

import io.openepcis.qrcode.generator.QrCodeConfig;
import io.openepcis.qrcode.generator.QrCodeGenerator;
import io.openepcis.qrcode.generator.exception.QrCodeGeneratorException;
import io.openepcis.qrcode.generator.resource.params.QrCodeGenerationParams;
import io.openepcis.qrcode.generator.spi.service.QrCodeConfigService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class QrCodeService {

    @Inject
    QrCodeGenerator qrCodeGenerator;

    public Uni<Response> generate(final QrCodeGenerationParams params, final QrCodeConfig qrCodeConfig) {
        // normalize accept header
        final String mime = validateMime(params.accept);
        qrCodeConfig.setMimeType(mime);

        // apply design, HRI, compression flags
        qrCodeConfig.setDesignPreset(Optional.ofNullable(params.getDesignPresetHeader()).orElse(qrCodeConfig.getDesignPreset()));
        qrCodeConfig.setAddHri(params.getHriHeader());
        qrCodeConfig.setCompressDigitalLink(params.getCompressedHeader());

        // Generate and wrap in a Response
        return Uni.createFrom().item(() -> {
            try {
                final byte[] qrBytes = qrCodeGenerator.generateQRCode(qrCodeConfig);
                return Response.ok(qrBytes, qrCodeConfig.getMimeType()).build();
            } catch (Exception e) {
                throw new QrCodeGeneratorException("QR generation failed: " + e.getMessage(), e);
            }
        });
    }

    public Uni<java.util.List<QrCodeConfig>> listPresets() {
        final List<QrCodeConfig> presets = QrCodeConfigService.getInstance()
                .getAllProviders().stream()
                .map(provider -> provider.customizeConfig(QrCodeConfig.builder().data("").build()))
                .filter(config -> StringUtils.isNotBlank(config.getDesignPreset()))
                .peek(config -> {
                    // Replace full logoResourceUrl with a relative path or file name if present
                    if (StringUtils.isNotBlank(config.getLogoResourceUrl())) {
                        final String fileName = Paths.get(config.getLogoResourceUrl()).getFileName().toString();
                        config.setLogoResourceUrl(fileName);
                    }
                }).toList();

        return Uni.createFrom().item(presets);
    }

    public String normalizePathWithBaseUrl(final String baseUrl, final String linkPath) {
        // Append baseUrl + normalized relative path
        final String normalizedBase = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        final String normalizedPath = linkPath.startsWith("/") ? linkPath.substring(1) : linkPath;
        return normalizedBase + normalizedPath;
    }

    private String validateMime(final String accept) {
        return Arrays.stream(ImageIO.getWriterMIMETypes())
                .filter(m -> m.equalsIgnoreCase(accept))
                .findFirst()
                .orElse("image/png");
    }


}
