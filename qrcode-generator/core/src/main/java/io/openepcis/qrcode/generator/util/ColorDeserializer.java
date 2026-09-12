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
package io.openepcis.qrcode.generator.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.awt.*;
import java.io.IOException;

/**
 * Counterpart to {@link ColorSerializer}: accepts the {@code {"red":0,"green":0,"blue":0,"alpha":255}}
 * object form produced by the serializer (and the qr-design-presets endpoint), plus hex strings
 * such as {@code "#F26334"} or {@code "#F26334FF"} for convenience. Without it, {@code java.awt.Color}
 * has no Jackson creator and any posted config containing a color fails to deserialize.
 */
public class ColorDeserializer extends JsonDeserializer<Color> {

    @Override
    public Color deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        final JsonNode node = parser.getCodec().readTree(parser);

        if (node.isObject()) {
            final int red = node.path("red").asInt(0);
            final int green = node.path("green").asInt(0);
            final int blue = node.path("blue").asInt(0);
            final int alpha = node.path("alpha").asInt(255);
            return new Color(clamp(red), clamp(green), clamp(blue), clamp(alpha));
        }

        if (node.isTextual()) {
            final String text = node.asText().trim();
            final String hex = text.startsWith("#") ? text.substring(1) : text;
            if (hex.length() == 6 || hex.length() == 8) {
                try {
                    final int rgb = Integer.parseInt(hex.substring(0, 6), 16);
                    final int alpha = hex.length() == 8 ? Integer.parseInt(hex.substring(6, 8), 16) : 255;
                    return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, alpha);
                } catch (NumberFormatException ignored) {
                    // fall through to the InvalidFormatException below
                }
            }
            throw new InvalidFormatException(parser, "Expected color as #RRGGBB / #RRGGBBAA hex string", text, Color.class);
        }

        throw new InvalidFormatException(parser,
                "Expected color as {\"red\":..,\"green\":..,\"blue\":..,\"alpha\":..} object or hex string", node.toString(), Color.class);
    }

    private static int clamp(final int value) {
        return Math.max(0, Math.min(255, value));
    }
}
