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
