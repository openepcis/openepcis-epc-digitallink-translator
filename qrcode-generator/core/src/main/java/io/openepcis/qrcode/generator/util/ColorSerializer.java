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
