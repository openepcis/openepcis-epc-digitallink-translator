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
