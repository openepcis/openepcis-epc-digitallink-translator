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
package io.openepcis.qrcode.generator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.openepcis.qrcode.generator.util.ColorSerializer;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.awt.*;

/**
 * Holds configuration for generating a QR code. Provides a Builder, so we can set only the fields we need.
 */
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
@Schema(description = "Holds configuration for generating a QR code. Provides a Builder, so we can set only the fields we need.")
public final class QrCodeConfig {

    // Required: The data (URL or text) to encode in the QR code.
    @NonNull
    @Schema(description = "The data (URL or text) to encode in the QR code.", examples = {"https://example.com"}, required = true)
    private String data;

    // Optional fields (with defaults):
    /**
     * Name associated with the QR code to be generated. If EPCIS/GS1 then corresponding default values will be used from extensions module.
     */
    @Schema(description = "The Design Preset associated with the QR code.", examples = {"openepcis", "gs1", "epcis"})
    @Builder.Default
    private String designPreset = null;

    /**
     * The output file format (e.g., "image/png", "image/jpeg). Defaults to image/png
     */
    @Schema(description = "The output file format (e.g., 'image/png', 'image/jpeg', 'image/gif').", examples = {"image/png"}, defaultValue = "image/png")
    @Builder.Default
    private String mimeType = "image/png";

    /**
     * Convert to uppercase URL for better compression. Default is true.
     **/
    @Schema(description = "Convert to uppercase URL for better compression.", examples = "false", defaultValue = "false")
    @Builder.Default
    private boolean compressWithUppercase = Boolean.FALSE;

    /**
     * The width of the QR code in pixels. Default is 400.
     */
    @Schema(description = "The width of the QR code in pixels.", examples = "400", defaultValue = "400")
    @Builder.Default
    private int qrWidth = 400;

    /**
     * The height of the QR code in pixels. Default is 400.
     */
    @Schema(description = "The height of the QR code in pixels.", examples = "400", defaultValue = "400")
    @Builder.Default
    private int qrHeight = 400;

    /**
     * Quiet zone (margin) around the QR code, in modules. Default is 4.
     */
    @Schema(description = "Quiet zone (margin) around the QR code, in modules.", examples = "4", defaultValue = "4")
    @Builder.Default
    private int margin = 4;

    /**
     * Background color of the entire QR image. Default is Color.WHITE.
     */
    @Builder.Default
    @Schema(description = "Background color of the entire QR image.", examples = "{\"red\": 255, \"green\": 255, \"blue\": 255, \"alpha\": 255}", defaultValue = "White")
    @JsonSerialize(using = ColorSerializer.class)
    private Color backgroundColor = Color.WHITE;

    /**
     * The start color of the module gradient. Default is Color.BLACK.
     */
    @Builder.Default
    @Schema(description = "The start color of the module gradient.", examples = "{\"red\": 0, \"green\": 0, \"blue\": 0, \"alpha\": 255}", defaultValue = "Black")
    @JsonSerialize(using = ColorSerializer.class)
    private Color gradientStart = Color.BLACK;

    /**
     * The end color of the module gradient. Default is Color.BLACK (i.e., no visible gradient).
     */
    @Builder.Default
    @Schema(description = "The end color of the module gradient.", examples = "{\"red\": 0, \"green\": 0, \"blue\": 0, \"alpha\": 255}", defaultValue = "Black")
    @JsonSerialize(using = ColorSerializer.class)
    private Color gradientEnd = Color.BLACK;

    /**
     * If true, use a radial gradient; if false, linear gradient. Default is false.
     */
    @Builder.Default
    @Schema(description = "If true, use a radial gradient; if false, linear gradient.", examples = "false", defaultValue = "false")
    private boolean useRadialGradient = false;

    /**
     * Fallback color for the finder patterns if not drawing them in gradient. Default: Color.BLACK.
     */
    @Builder.Default
    @Schema(description = "Fallback color for the finder patterns.", examples = "{\"red\": 0, \"green\": 0, \"blue\": 0, \"alpha\": 255}", defaultValue = "Black")
    @JsonSerialize(using = ColorSerializer.class)
    private Color finderColor = Color.BLACK;

    /**
     * If true, apply the gradient to the finder pattern as well. Default is false.
     */
    @Builder.Default
    @Schema(description = "If true, apply the gradient to the finder pattern.", examples = "false", defaultValue = "false")
    private boolean drawFinderGradient = false;

    /**
     * Shape of each QR module. Default is SQUARE.
     */
    @Builder.Default
    @Schema(description = "Shape of each QR module.", examples = "SQUARE", defaultValue = "SQUARE")
    private ModuleShape moduleShape = ModuleShape.SQUARE;

    /**
     * Text for QR code module if ModuleShape is Name. Default is A
     */
    @Builder.Default
    @Schema(description = "Text for QR code module if ModuleShape is Name.", examples = "A", defaultValue = "A")
    private String moduleName = "A";

    /**
     * If true, draw a shadow behind each filled module. Default is false.
     */
    @Builder.Default
    @Schema(description = "If true, draw a shadow behind each filled module.", examples = "false", defaultValue = "false")
    private boolean drawShadows = false;

    /**
     * The color of the shadow, can have alpha. Default is semi-transparent black.
     */
    @Builder.Default
    @Schema(description = "The color of the shadow, can have alpha.", examples = "{\"red\": 0, \"green\": 0, \"blue\": 0, \"alpha\": 50}", defaultValue = "new Color(0, 0, 0, 50)")
    @JsonSerialize(using = ColorSerializer.class)
    private Color shadowColor = new Color(0, 0, 0, 50);

    /**
     * Offset of the shadow relative to module size. E.g. 0.1f = 10%. Default is 0.1f.
     */
    @Builder.Default
    @Schema(description = "Offset of the shadow relative to module size. E.g. 0.1f = 10%.", examples = "0.1", defaultValue = "0.1")
    private float shadowOffsetPct = 0.1f;

    /**
     * Optional URL for a logo to overlay at the center. Default is null (no logo).
     */
    @Builder.Default
    @Schema(description = "Optional URL for a logo to overlay at the center.")
    private String logoResourceUrl = null;

    /**
     * The fraction of the QR code size for the logo (e.g. 0.2 = 20%). Default is 0.0f (no visible
     * logo).
     */
    @Builder.Default
    @Schema(description = "The fraction of the QR code size for the logo.", type = SchemaType.NUMBER, examples = "0.0", defaultValue = "0.0")
    private float logoScale = 0.0f;

    /**
     * The text label to be displayed at the bottom right (Ex: can be company name, tagline, ect). Default is null
     */
    @Builder.Default
    @Schema(description = "The label to be displayed at the bottom right.")
    private String displayLabel = null;

    /**
     * The company font color. Default: Color.BLACK.
     */
    @Builder.Default
    @Schema(description = "The company font color.", type = SchemaType.OBJECT, examples = "{\"red\": 0, \"green\": 0, \"blue\": 0, \"alpha\": 255}", defaultValue = "Black")
    @JsonSerialize(using = ColorSerializer.class)
    private Color displayLabelFontColor = Color.BLACK;

    /**
     * Add the HRI (Human Readable Interpretation) text below the QR code. Default is false.
     */
    @Builder.Default
    @Schema(description = "Add the HRI (Human Readable Interpretation) text below the QR code.", type = SchemaType.BOOLEAN, examples = "false", defaultValue = "false")
    private boolean addHri = false;

    /**
     * An enum to define module shapes to generate QR code with different shapes.
     */
    public enum ModuleShape {
        SQUARE,
        ROUNDED_RECT,
        CIRCLE,
        DOT,
        HEART,
        BARCODE,
        LETTER,
        STAR,
        TRIANGLE,
        DIAMOND,
        WAVE
    }
}
