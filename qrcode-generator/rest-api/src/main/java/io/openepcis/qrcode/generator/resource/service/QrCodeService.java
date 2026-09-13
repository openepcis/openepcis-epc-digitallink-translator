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
package io.openepcis.qrcode.generator.resource.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.openepcis.qrcode.generator.QrCodeConfig;
import io.openepcis.qrcode.generator.QrCodeGenerator;
import io.openepcis.qrcode.generator.exception.QrCodeGeneratorException;
import io.openepcis.qrcode.generator.resource.params.QrCodeGenerationParams;
import io.openepcis.qrcode.generator.spi.service.QrCodeConfigService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.imageio.ImageIO;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class QrCodeService {

    // ImageIO writer MIME types never change at runtime; avoid scanning the registry per request.
    private static final Set<String> SUPPORTED_MIME_TYPES = Set.copyOf(Arrays.asList(ImageIO.getWriterMIMETypes()));

    @Inject
    QrCodeGenerator qrCodeGenerator;

    @Inject
    ObjectMapper objectMapper;

    /**
     * QR code images are deterministic for a given config, so identical requests (same Digital
     * Link, preset, size, flags) are served from an in-memory cache instead of re-rasterizing.
     */
    @ConfigProperty(name = "openepcis.qr.cache.enabled", defaultValue = "true")
    boolean cacheEnabled;

    // Upper bound on cached image bytes (default 32 MiB; a 400px PNG is ~16 KiB -> ~2000 entries).
    @ConfigProperty(name = "openepcis.qr.cache.maximum-weight-bytes", defaultValue = "33554432")
    long cacheMaximumWeightBytes;

    // Entries expire so design-preset changes (logos, colors) are picked up without a restart.
    @ConfigProperty(name = "openepcis.qr.cache.expire-after-write", defaultValue = "PT6H")
    Duration cacheExpireAfterWrite;

    // max-age (seconds) for the Cache-Control header on successful responses.
    @ConfigProperty(name = "openepcis.qr.http-cache-max-age-seconds", defaultValue = "3600")
    int httpCacheMaxAgeSeconds;

    private Cache<String, byte[]> qrCodeCache;

    @PostConstruct
    void initCache() {
        qrCodeCache = Caffeine.newBuilder()
                .maximumWeight(cacheMaximumWeightBytes)
                .<String, byte[]>weigher((key, value) -> key.length() + value.length)
                .expireAfterWrite(cacheExpireAfterWrite)
                .build();
    }

    public Uni<Response> generate(final QrCodeGenerationParams params, final QrCodeConfig qrCodeConfig) {
        // normalize accept header
        final String mime = validateMime(params.accept);
        qrCodeConfig.setMimeType(mime);

        // apply design, HRI, compression flags
        qrCodeConfig.setDesignPreset(Optional.ofNullable(params.getDesignPresetHeader()).orElse(qrCodeConfig.getDesignPreset()));
        qrCodeConfig.setAddHri(params.getHriHeader());
        qrCodeConfig.setCompressDigitalLink(params.getCompressedHeader());

        // The config now fully determines the output image, so it is the cache key.
        final String cacheKey = cacheKey(qrCodeConfig);
        final String etag = '"' + cacheKey + '"';

        // Conditional request: the client already holds the image.
        if (etag.equals(params.getIfNoneMatchHeader())) {
            return Uni.createFrom().item(withCacheHeaders(Response.notModified(), etag).build());
        }

        if (cacheEnabled) {
            final byte[] cached = qrCodeCache.getIfPresent(cacheKey);
            if (cached != null) {
                return Uni.createFrom().item(
                        withCacheHeaders(Response.ok(cached, qrCodeConfig.getMimeType()), etag)
                                .header("X-Cache", "HIT")
                                .build());
            }
        }

        // Generate on a worker thread: rasterization (and a possible logo fetch) is CPU/IO-heavy
        // and must not run on the Vert.x event loop the reactive endpoint subscribes on.
        return Uni.createFrom().item(() -> {
                    try {
                        final byte[] qrBytes = qrCodeGenerator.generateQRCode(qrCodeConfig);
                        if (cacheEnabled && qrBytes != null && qrBytes.length > 0) {
                            qrCodeCache.put(cacheKey, qrBytes);
                        }
                        return withCacheHeaders(Response.ok(qrBytes, qrCodeConfig.getMimeType()), etag)
                                .header("X-Cache", "MISS")
                                .build();
                    } catch (Exception e) {
                        throw new QrCodeGeneratorException("QR generation failed: " + e.getMessage(), e);
                    }
                })
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
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
        // ACCEPT_HEADER advertises image/jpg, but ImageIO only registers image/jpeg.
        final String normalized = "image/jpg".equalsIgnoreCase(StringUtils.trim(accept)) ? "image/jpeg" : accept;
        return SUPPORTED_MIME_TYPES.stream()
                .filter(m -> m.equalsIgnoreCase(normalized))
                .findFirst()
                .orElse("image/png");
    }

    /**
     * SHA-256 over the Jackson serialization of the fully-populated config. Field order is the
     * declaration order, and Colors go through {@code ColorSerializer}, so the key is stable for
     * equal configs. Doubles as the ETag value.
     */
    private String cacheKey(final QrCodeConfig config) {
        try {
            final byte[] json = objectMapper.writeValueAsBytes(config);
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(json));
        } catch (Exception e) {
            throw new QrCodeGeneratorException("Failed to compute QR cache key: " + e.getMessage(), e);
        }
    }

    private Response.ResponseBuilder withCacheHeaders(final Response.ResponseBuilder builder, final String etag) {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(httpCacheMaxAgeSeconds);
        return builder.cacheControl(cacheControl).header("ETag", etag);
    }

    // package-private test hook
    Cache<String, byte[]> cache() {
        return qrCodeCache;
    }

    String cacheKeyFor(final QrCodeConfig config) {
        return cacheKey(config);
    }
}
