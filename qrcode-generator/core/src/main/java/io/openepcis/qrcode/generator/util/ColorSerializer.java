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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.awt.*;
import java.io.IOException;

/**
 * Class to exclude the information not necessary from JSON during the building of the JSON from QrCodeConfig for all Color dataType.
 * Used during qr-design-presets end-point request to get all available Design pre-sets from the system.
 */
public class ColorSerializer extends JsonSerializer<Color> {

    @Override
    public void serialize(final Color color, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("red", color.getRed());
        gen.writeNumberField("green", color.getGreen());
        gen.writeNumberField("blue", color.getBlue());
        gen.writeNumberField("alpha", color.getAlpha());
        gen.writeEndObject();
    }
}
