/*
 * Copyright (c) 2022-2026 benelog GmbH & Co. KG
 * All rights reserved.
 *
 * Unauthorized copying, modification, distribution,
 * or use of this work, via any medium, is strictly prohibited.
 *
 * benelog GmbH & Co. KG reserves all rights not expressly granted herein,
 * including the right to sell licenses for using this work.
 */
package io.openepcis.qrcode.generator.resource.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.openepcis.qrcode.generator.QrCodeConfig;
import io.openepcis.qrcode.generator.QrCodeGenerator;
import io.openepcis.qrcode.generator.resource.params.QrCodeGenerationParams;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class QrCodeServiceCacheTest {

    private static final String DL_URL = "https://id.gs1.org/01/09506000134352/10/ABC123";

    private QrCodeService service;

    @BeforeEach
    void setUp() {
        service = new QrCodeService();
        service.qrCodeGenerator = new QrCodeGenerator();
        service.objectMapper = new ObjectMapper();
        service.cacheEnabled = true;
        service.cacheMaximumWeightBytes = 1024 * 1024;
        service.cacheExpireAfterWrite = Duration.ofMinutes(5);
        service.httpCacheMaxAgeSeconds = 3600;
        service.initCache();
    }

    private Response generate(QrCodeGenerationParams params) {
        return service.generate(params, QrCodeConfig.builder().data(DL_URL).build())
                .await().atMost(Duration.ofSeconds(10));
    }

    @Test
    void secondIdenticalRequestIsServedFromCache() {
        final Response first = generate(new QrCodeGenerationParams());
        assertEquals(200, first.getStatus());
        assertEquals("MISS", first.getHeaderString("X-Cache"));
        final byte[] firstBytes = (byte[]) first.getEntity();
        assertTrue(firstBytes.length > 0);

        final Response second = generate(new QrCodeGenerationParams());
        assertEquals(200, second.getStatus());
        assertEquals("HIT", second.getHeaderString("X-Cache"));
        assertArrayEquals(firstBytes, (byte[]) second.getEntity());
    }

    @Test
    void responsesCarryEtagAndCacheControl() {
        final Response response = generate(new QrCodeGenerationParams());
        assertNotNull(response.getHeaderString("ETag"));
        final String cacheControl = response.getHeaderString("Cache-Control");
        assertNotNull(cacheControl);
        assertTrue(cacheControl.contains("max-age=3600"), cacheControl);
    }

    @Test
    void matchingIfNoneMatchReturns304WithoutBody() {
        final Response first = generate(new QrCodeGenerationParams());
        final String etag = first.getHeaderString("ETag");

        final QrCodeGenerationParams conditional = new QrCodeGenerationParams();
        conditional.ifNoneMatchHeader = etag;
        final Response second = generate(conditional);
        assertEquals(304, second.getStatus());
        assertNull(second.getEntity());
    }

    @Test
    void differentConfigsGetDifferentCacheKeys() {
        final QrCodeConfig png = QrCodeConfig.builder().data(DL_URL).build();
        final QrCodeConfig larger = QrCodeConfig.builder().data(DL_URL).qrWidth(600).qrHeight(600).build();
        assertNotEquals(service.cacheKeyFor(png), service.cacheKeyFor(larger));

        final QrCodeConfig same = QrCodeConfig.builder().data(DL_URL).build();
        assertEquals(service.cacheKeyFor(png), service.cacheKeyFor(same));
    }

    @Test
    void advertisedAcceptTypesProduceNonEmptyImages() {
        for (final String accept : new String[]{"image/png", "image/jpeg", "image/jpg", "image/gif", "image/bmp", "image/tiff"}) {
            final QrCodeGenerationParams params = new QrCodeGenerationParams();
            params.accept = accept;
            final Response response = generate(params);
            assertEquals(200, response.getStatus(), accept);
            assertTrue(((byte[]) response.getEntity()).length > 0, "zero-byte image for " + accept);
        }
    }

    @Test
    void cacheDisabledAlwaysRegenerates() {
        service.cacheEnabled = false;
        final Response first = generate(new QrCodeGenerationParams());
        final Response second = generate(new QrCodeGenerationParams());
        assertEquals("MISS", first.getHeaderString("X-Cache"));
        assertEquals("MISS", second.getHeaderString("X-Cache"));
    }
}
