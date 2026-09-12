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
package io.openepcis.qrcode.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * QrCodeConfig must survive a JSON round-trip: the design-presets endpoint serializes configs
 * (colors via ColorSerializer) and clients POST them back to /qr/generate. Colors previously
 * had no deserializer, so any posted config containing a color failed with a 500.
 */
class QrCodeConfigJsonRoundTripTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializedConfigWithColorsCanBeDeserialized() throws Exception {
        final QrCodeConfig original = QrCodeConfig.builder()
                .data("https://id.gs1.org/01/09506000134352")
                .backgroundColor(new Color(242, 99, 52, 255))
                .gradientStart(new Color(0, 44, 108, 255))
                .gradientEnd(new Color(0, 44, 108, 128))
                .finderColor(Color.RED)
                .build();

        final String json = objectMapper.writeValueAsString(original);
        final QrCodeConfig restored = objectMapper.readValue(json, QrCodeConfig.class);

        assertEquals(original.getData(), restored.getData());
        assertEquals(original.getBackgroundColor(), restored.getBackgroundColor());
        assertEquals(original.getGradientStart(), restored.getGradientStart());
        assertEquals(original.getGradientEnd(), restored.getGradientEnd());
        assertEquals(original.getFinderColor(), restored.getFinderColor());
    }

    @Test
    void hexColorStringsAreAccepted() throws Exception {
        final QrCodeConfig config = objectMapper.readValue(
                "{\"data\":\"https://example.com\",\"gradientStart\":\"#F26334\",\"gradientEnd\":\"#002C6C80\"}",
                QrCodeConfig.class);

        assertEquals(new Color(242, 99, 52, 255), config.getGradientStart());
        assertEquals(new Color(0, 44, 108, 128), config.getGradientEnd());
    }
}
