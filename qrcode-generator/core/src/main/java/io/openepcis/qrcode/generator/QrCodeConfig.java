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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.openepcis.qrcode.generator.util.ColorDeserializer;
import io.openepcis.qrcode.generator.util.ColorSerializer;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import java.awt.*;

/**
 * Holds configuration for generating a QR code. Provides a Builder, so we can set only the fields we need.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Holds configuration for generating a QR code. Provides a Builder, so we can set only the fields we need.")
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(builder = QrCodeConfig.QrCodeConfigBuilder.class)
public final class QrCodeConfig {
    // Required: The data (URL or text) to encode in the QR code.
    @Schema(description = "The data (URL or text) to encode in the QR code.", examples = {"https://example.com"}, required = true)
    private String data;
    // Optional fields (with defaults):
    /**
     * Name associated with the QR code to be generated. If EPCIS/GS1 then corresponding default values will be used from extensions module.
     */
    @Schema(description = "The Design Preset associated with the QR code.", examples = {"openepcis", "gs1", "epcis"})
    private String designPreset;
    /**
     * The output file format (e.g., "image/png", "image/jpeg). Defaults to image/png
     */
    @Schema(description = "The output file format (e.g., \'image/png\', \'image/jpeg\', \'image/gif\').", examples = {"image/png"}, defaultValue = "image/png")
    private String mimeType;
    /**
     * Convert to uppercase URL for better compression. Default is true.
     */
    @Schema(description = "Convert to uppercase URL for better compression.", examples = "false", defaultValue = "false")
    private boolean compressWithUppercase;
    /**
     * The width of the QR code in pixels. Default is 400.
     */
    @Schema(description = "The width of the QR code in pixels.", examples = "400", defaultValue = "400")
    private int qrWidth;
    /**
     * The height of the QR code in pixels. Default is 400.
     */
    @Schema(description = "The height of the QR code in pixels.", examples = "400", defaultValue = "400")
    private int qrHeight;
    /**
     * Quiet zone (margin) around the QR code, in modules. Default is 4.
     */
    @Schema(description = "Quiet zone (margin) around the QR code, in modules.", examples = "4", defaultValue = "4")
    private int margin;
    /**
     * Background color of the entire QR image. Default is Color.WHITE.
     */
    @Schema(description = "Background color of the entire QR image.", examples = "{\"red\": 255, \"green\": 255, \"blue\": 255, \"alpha\": 255}", defaultValue = "White")
    @JsonSerialize(using = ColorSerializer.class)
    @JsonDeserialize(using = ColorDeserializer.class)
    private Color backgroundColor;
    /**
     * The start color of the module gradient. Default is Color.BLACK.
     */
    @Schema(description = "The start color of the module gradient.", examples = "{\"red\": 0, \"green\": 0, \"blue\": 0, \"alpha\": 255}", defaultValue = "Black")
    @JsonSerialize(using = ColorSerializer.class)
    @JsonDeserialize(using = ColorDeserializer.class)
    private Color gradientStart;
    /**
     * The end color of the module gradient. Default is Color.BLACK (i.e., no visible gradient).
     */
    @Schema(description = "The end color of the module gradient.", examples = "{\"red\": 0, \"green\": 0, \"blue\": 0, \"alpha\": 255}", defaultValue = "Black")
    @JsonSerialize(using = ColorSerializer.class)
    @JsonDeserialize(using = ColorDeserializer.class)
    private Color gradientEnd;
    /**
     * If true, use a radial gradient; if false, linear gradient. Default is false.
     */
    @Schema(description = "If true, use a radial gradient; if false, linear gradient.", examples = "false", defaultValue = "false")
    private boolean useRadialGradient;
    /**
     * Fallback color for the finder patterns if not drawing them in gradient. Default: Color.BLACK.
     */
    @Schema(description = "Fallback color for the finder patterns.", examples = "{\"red\": 0, \"green\": 0, \"blue\": 0, \"alpha\": 255}", defaultValue = "Black")
    @JsonSerialize(using = ColorSerializer.class)
    @JsonDeserialize(using = ColorDeserializer.class)
    private Color finderColor;
    /**
     * If true, apply the gradient to the finder pattern as well. Default is false.
     */
    @Schema(description = "If true, apply the gradient to the finder pattern.", examples = "false", defaultValue = "false")
    private boolean drawFinderGradient;
    /**
     * Shape of each QR module. Default is SQUARE.
     */
    @Schema(description = "Shape of each QR module.", examples = "SQUARE", defaultValue = "SQUARE")
    private ModuleShape moduleShape;
    /**
     * Text for QR code module if ModuleShape is Name. Default is A
     */
    @Schema(description = "Text for QR code module if ModuleShape is Name.", examples = "A", defaultValue = "A")
    private String moduleName;
    /**
     * If true, draw a shadow behind each filled module. Default is false.
     */
    @Schema(description = "If true, draw a shadow behind each filled module.", examples = "false", defaultValue = "false")
    private boolean drawShadows;
    /**
     * The color of the shadow, can have alpha. Default is semi-transparent black.
     */
    @Schema(description = "The color of the shadow, can have alpha.", examples = "{\"red\": 0, \"green\": 0, \"blue\": 0, \"alpha\": 50}", defaultValue = "new Color(0, 0, 0, 50)")
    @JsonSerialize(using = ColorSerializer.class)
    @JsonDeserialize(using = ColorDeserializer.class)
    private Color shadowColor;
    /**
     * Offset of the shadow relative to module size. E.g. 0.1f = 10%. Default is 0.1f.
     */
    @Schema(description = "Offset of the shadow relative to module size. E.g. 0.1f = 10%.", examples = "0.1", defaultValue = "0.1")
    private float shadowOffsetPct;
    /**
     * Optional URL for a logo to overlay at the center. Default is null (no logo).
     */
    @Schema(description = "Optional URL for a logo to overlay at the center.")
    private String logoResourceUrl;
    /**
     * The fraction of the QR code size for the logo (e.g. 0.2 = 20%). Default is 0.0f (no visible
     * logo).
     */
    @Schema(description = "The fraction of the QR code size for the logo.", type = SchemaType.NUMBER, examples = "0.0", defaultValue = "0.0")
    private float logoScale;
    /**
     * The text label to be displayed at the bottom right (Ex: can be company name, tagline, ect). Default is null
     */
    @Schema(description = "The label to be displayed at the bottom right.")
    private String displayLabel;
    /**
     * The company font color. Default: Color.BLACK.
     */
    @Schema(description = "The company font color.", type = SchemaType.OBJECT, examples = "{\"red\": 0, \"green\": 0, \"blue\": 0, \"alpha\": 255}", defaultValue = "Black")
    @JsonSerialize(using = ColorSerializer.class)
    @JsonDeserialize(using = ColorDeserializer.class)
    private Color displayLabelFontColor;
    /**
     * Add the HRI (Human Readable Interpretation) text below the QR code. Default is false.
     */
    @Schema(description = "Add the HRI (Human Readable Interpretation) text below the QR code.", type = SchemaType.BOOLEAN, examples = "false", defaultValue = "false")
    private boolean addHri;
    /**
     * Whether to compress the GS1 Digital Link URL before generating the QR code. Default is false.
     */
    @Schema(description = "Whether to compress the GS1 Digital Link URI before encoding it into the QR code.", type = SchemaType.BOOLEAN, examples = "false", defaultValue = "false")
    private boolean compressDigitalLink;


    /**
     * An enum to define module shapes to generate QR code with different shapes.
     */
    public enum ModuleShape {
        SQUARE, ROUNDED_RECT, CIRCLE, DOT, HEART, BARCODE, LETTER, STAR, TRIANGLE, DIAMOND, WAVE;
    }

    private static String $default$designPreset() {
        return null;
    }

    private static String $default$mimeType() {
        return "image/png";
    }

    private static boolean $default$compressWithUppercase() {
        return Boolean.FALSE;
    }

    private static int $default$qrWidth() {
        return 400;
    }

    private static int $default$qrHeight() {
        return 400;
    }

    private static int $default$margin() {
        return 4;
    }

    private static Color $default$backgroundColor() {
        return Color.WHITE;
    }

    private static Color $default$gradientStart() {
        return Color.BLACK;
    }

    private static Color $default$gradientEnd() {
        return Color.BLACK;
    }

    private static boolean $default$useRadialGradient() {
        return false;
    }

    private static Color $default$finderColor() {
        return Color.BLACK;
    }

    private static boolean $default$drawFinderGradient() {
        return false;
    }

    private static ModuleShape $default$moduleShape() {
        return ModuleShape.SQUARE;
    }

    private static String $default$moduleName() {
        return "A";
    }

    private static boolean $default$drawShadows() {
        return false;
    }

    private static Color $default$shadowColor() {
        return new Color(0, 0, 0, 50);
    }

    private static float $default$shadowOffsetPct() {
        return 0.1F;
    }

    private static String $default$logoResourceUrl() {
        return null;
    }

    private static float $default$logoScale() {
        return 0.0F;
    }

    private static String $default$displayLabel() {
        return null;
    }

    private static Color $default$displayLabelFontColor() {
        return Color.BLACK;
    }

    private static boolean $default$addHri() {
        return false;
    }

    private static boolean $default$compressDigitalLink() {
        return false;
    }

    /**
     * Creates a new {@code QrCodeConfig} instance.
     *
     * @param data
     * @param designPreset Name associated with the QR code to be generated. If EPCIS/GS1 then corresponding default values will be used from extensions module.
     * @param mimeType The output file format (e.g., "image/png", "image/jpeg). Defaults to image/png
     * @param compressWithUppercase Convert to uppercase URL for better compression. Default is true.
     * @param qrWidth The width of the QR code in pixels. Default is 400.
     * @param qrHeight The height of the QR code in pixels. Default is 400.
     * @param margin Quiet zone (margin) around the QR code, in modules. Default is 4.
     * @param backgroundColor Background color of the entire QR image. Default is Color.WHITE.
     * @param gradientStart The start color of the module gradient. Default is Color.BLACK.
     * @param gradientEnd The end color of the module gradient. Default is Color.BLACK (i.e., no visible gradient).
     * @param useRadialGradient If true, use a radial gradient; if false, linear gradient. Default is false.
     * @param finderColor Fallback color for the finder patterns if not drawing them in gradient. Default: Color.BLACK.
     * @param drawFinderGradient If true, apply the gradient to the finder pattern as well. Default is false.
     * @param moduleShape Shape of each QR module. Default is SQUARE.
     * @param moduleName Text for QR code module if ModuleShape is Name. Default is A
     * @param drawShadows If true, draw a shadow behind each filled module. Default is false.
     * @param shadowColor The color of the shadow, can have alpha. Default is semi-transparent black.
     * @param shadowOffsetPct Offset of the shadow relative to module size. E.g. 0.1f = 10%. Default is 0.1f.
     * @param logoResourceUrl Optional URL for a logo to overlay at the center. Default is null (no logo).
     * @param logoScale The fraction of the QR code size for the logo (e.g. 0.2 = 20%). Default is 0.0f (no visible
     * logo).
     * @param displayLabel The text label to be displayed at the bottom right (Ex: can be company name, tagline, ect). Default is null
     * @param displayLabelFontColor The company font color. Default: Color.BLACK.
     * @param addHri Add the HRI (Human Readable Interpretation) text below the QR code. Default is false.
     * @param compressDigitalLink Whether to compress the GS1 Digital Link URL before generating the QR code. Default is false.
     */
    QrCodeConfig(String data, String designPreset, String mimeType, boolean compressWithUppercase, int qrWidth, int qrHeight, int margin, Color backgroundColor, Color gradientStart, Color gradientEnd, boolean useRadialGradient, Color finderColor, boolean drawFinderGradient, ModuleShape moduleShape, String moduleName, boolean drawShadows, Color shadowColor, float shadowOffsetPct, String logoResourceUrl, float logoScale, String displayLabel, Color displayLabelFontColor, boolean addHri, boolean compressDigitalLink) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        this.data = data;
        this.designPreset = designPreset;
        this.mimeType = mimeType;
        this.compressWithUppercase = compressWithUppercase;
        this.qrWidth = qrWidth;
        this.qrHeight = qrHeight;
        this.margin = margin;
        this.backgroundColor = backgroundColor;
        this.gradientStart = gradientStart;
        this.gradientEnd = gradientEnd;
        this.useRadialGradient = useRadialGradient;
        this.finderColor = finderColor;
        this.drawFinderGradient = drawFinderGradient;
        this.moduleShape = moduleShape;
        this.moduleName = moduleName;
        this.drawShadows = drawShadows;
        this.shadowColor = shadowColor;
        this.shadowOffsetPct = shadowOffsetPct;
        this.logoResourceUrl = logoResourceUrl;
        this.logoScale = logoScale;
        this.displayLabel = displayLabel;
        this.displayLabelFontColor = displayLabelFontColor;
        this.addHri = addHri;
        this.compressDigitalLink = compressDigitalLink;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    @com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
    public static class QrCodeConfigBuilder {
        private String data;
        private boolean designPreset$set;
        private String designPreset$value;
        private boolean mimeType$set;
        private String mimeType$value;
        private boolean compressWithUppercase$set;
        private boolean compressWithUppercase$value;
        private boolean qrWidth$set;
        private int qrWidth$value;
        private boolean qrHeight$set;
        private int qrHeight$value;
        private boolean margin$set;
        private int margin$value;
        private boolean backgroundColor$set;
        private Color backgroundColor$value;
        private boolean gradientStart$set;
        private Color gradientStart$value;
        private boolean gradientEnd$set;
        private Color gradientEnd$value;
        private boolean useRadialGradient$set;
        private boolean useRadialGradient$value;
        private boolean finderColor$set;
        private Color finderColor$value;
        private boolean drawFinderGradient$set;
        private boolean drawFinderGradient$value;
        private boolean moduleShape$set;
        private ModuleShape moduleShape$value;
        private boolean moduleName$set;
        private String moduleName$value;
        private boolean drawShadows$set;
        private boolean drawShadows$value;
        private boolean shadowColor$set;
        private Color shadowColor$value;
        private boolean shadowOffsetPct$set;
        private float shadowOffsetPct$value;
        private boolean logoResourceUrl$set;
        private String logoResourceUrl$value;
        private boolean logoScale$set;
        private float logoScale$value;
        private boolean displayLabel$set;
        private String displayLabel$value;
        private boolean displayLabelFontColor$set;
        private Color displayLabelFontColor$value;
        private boolean addHri$set;
        private boolean addHri$value;
        private boolean compressDigitalLink$set;
        private boolean compressDigitalLink$value;

        QrCodeConfigBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder data(String data) {
            if (data == null) {
                throw new NullPointerException("data is marked non-null but is null");
            }
            this.data = data;
            return this;
        }

        /**
         * Name associated with the QR code to be generated. If EPCIS/GS1 then corresponding default values will be used from extensions module.
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder designPreset(String designPreset) {
            this.designPreset$value = designPreset;
            designPreset$set = true;
            return this;
        }

        /**
         * The output file format (e.g., "image/png", "image/jpeg). Defaults to image/png
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder mimeType(String mimeType) {
            this.mimeType$value = mimeType;
            mimeType$set = true;
            return this;
        }

        /**
         * Convert to uppercase URL for better compression. Default is true.
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder compressWithUppercase(boolean compressWithUppercase) {
            this.compressWithUppercase$value = compressWithUppercase;
            compressWithUppercase$set = true;
            return this;
        }

        /**
         * The width of the QR code in pixels. Default is 400.
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder qrWidth(int qrWidth) {
            this.qrWidth$value = qrWidth;
            qrWidth$set = true;
            return this;
        }

        /**
         * The height of the QR code in pixels. Default is 400.
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder qrHeight(int qrHeight) {
            this.qrHeight$value = qrHeight;
            qrHeight$set = true;
            return this;
        }

        /**
         * Quiet zone (margin) around the QR code, in modules. Default is 4.
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder margin(int margin) {
            this.margin$value = margin;
            margin$set = true;
            return this;
        }

        /**
         * Background color of the entire QR image. Default is Color.WHITE.
         * @return {@code this}.
         */
        @JsonDeserialize(using = ColorDeserializer.class)
        public QrCodeConfig.QrCodeConfigBuilder backgroundColor(Color backgroundColor) {
            this.backgroundColor$value = backgroundColor;
            backgroundColor$set = true;
            return this;
        }

        /**
         * The start color of the module gradient. Default is Color.BLACK.
         * @return {@code this}.
         */
        @JsonDeserialize(using = ColorDeserializer.class)
        public QrCodeConfig.QrCodeConfigBuilder gradientStart(Color gradientStart) {
            this.gradientStart$value = gradientStart;
            gradientStart$set = true;
            return this;
        }

        /**
         * The end color of the module gradient. Default is Color.BLACK (i.e., no visible gradient).
         * @return {@code this}.
         */
        @JsonDeserialize(using = ColorDeserializer.class)
        public QrCodeConfig.QrCodeConfigBuilder gradientEnd(Color gradientEnd) {
            this.gradientEnd$value = gradientEnd;
            gradientEnd$set = true;
            return this;
        }

        /**
         * If true, use a radial gradient; if false, linear gradient. Default is false.
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder useRadialGradient(boolean useRadialGradient) {
            this.useRadialGradient$value = useRadialGradient;
            useRadialGradient$set = true;
            return this;
        }

        /**
         * Fallback color for the finder patterns if not drawing them in gradient. Default: Color.BLACK.
         * @return {@code this}.
         */
        @JsonDeserialize(using = ColorDeserializer.class)
        public QrCodeConfig.QrCodeConfigBuilder finderColor(Color finderColor) {
            this.finderColor$value = finderColor;
            finderColor$set = true;
            return this;
        }

        /**
         * If true, apply the gradient to the finder pattern as well. Default is false.
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder drawFinderGradient(boolean drawFinderGradient) {
            this.drawFinderGradient$value = drawFinderGradient;
            drawFinderGradient$set = true;
            return this;
        }

        /**
         * Shape of each QR module. Default is SQUARE.
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder moduleShape(ModuleShape moduleShape) {
            this.moduleShape$value = moduleShape;
            moduleShape$set = true;
            return this;
        }

        /**
         * Text for QR code module if ModuleShape is Name. Default is A
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder moduleName(String moduleName) {
            this.moduleName$value = moduleName;
            moduleName$set = true;
            return this;
        }

        /**
         * If true, draw a shadow behind each filled module. Default is false.
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder drawShadows(boolean drawShadows) {
            this.drawShadows$value = drawShadows;
            drawShadows$set = true;
            return this;
        }

        /**
         * The color of the shadow, can have alpha. Default is semi-transparent black.
         * @return {@code this}.
         */
        @JsonDeserialize(using = ColorDeserializer.class)
        public QrCodeConfig.QrCodeConfigBuilder shadowColor(Color shadowColor) {
            this.shadowColor$value = shadowColor;
            shadowColor$set = true;
            return this;
        }

        /**
         * Offset of the shadow relative to module size. E.g. 0.1f = 10%. Default is 0.1f.
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder shadowOffsetPct(float shadowOffsetPct) {
            this.shadowOffsetPct$value = shadowOffsetPct;
            shadowOffsetPct$set = true;
            return this;
        }

        /**
         * Optional URL for a logo to overlay at the center. Default is null (no logo).
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder logoResourceUrl(String logoResourceUrl) {
            this.logoResourceUrl$value = logoResourceUrl;
            logoResourceUrl$set = true;
            return this;
        }

        /**
         * The fraction of the QR code size for the logo (e.g. 0.2 = 20%). Default is 0.0f (no visible
         * logo).
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder logoScale(float logoScale) {
            this.logoScale$value = logoScale;
            logoScale$set = true;
            return this;
        }

        /**
         * The text label to be displayed at the bottom right (Ex: can be company name, tagline, ect). Default is null
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder displayLabel(String displayLabel) {
            this.displayLabel$value = displayLabel;
            displayLabel$set = true;
            return this;
        }

        /**
         * The company font color. Default: Color.BLACK.
         * @return {@code this}.
         */
        @JsonDeserialize(using = ColorDeserializer.class)
        public QrCodeConfig.QrCodeConfigBuilder displayLabelFontColor(Color displayLabelFontColor) {
            this.displayLabelFontColor$value = displayLabelFontColor;
            displayLabelFontColor$set = true;
            return this;
        }

        /**
         * Add the HRI (Human Readable Interpretation) text below the QR code. Default is false.
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder addHri(boolean addHri) {
            this.addHri$value = addHri;
            addHri$set = true;
            return this;
        }

        /**
         * Whether to compress the GS1 Digital Link URL before generating the QR code. Default is false.
         * @return {@code this}.
         */
        public QrCodeConfig.QrCodeConfigBuilder compressDigitalLink(boolean compressDigitalLink) {
            this.compressDigitalLink$value = compressDigitalLink;
            compressDigitalLink$set = true;
            return this;
        }

        public QrCodeConfig build() {
            String designPreset$value = this.designPreset$value;
            if (!this.designPreset$set) designPreset$value = QrCodeConfig.$default$designPreset();
            String mimeType$value = this.mimeType$value;
            if (!this.mimeType$set) mimeType$value = QrCodeConfig.$default$mimeType();
            boolean compressWithUppercase$value = this.compressWithUppercase$value;
            if (!this.compressWithUppercase$set) compressWithUppercase$value = QrCodeConfig.$default$compressWithUppercase();
            int qrWidth$value = this.qrWidth$value;
            if (!this.qrWidth$set) qrWidth$value = QrCodeConfig.$default$qrWidth();
            int qrHeight$value = this.qrHeight$value;
            if (!this.qrHeight$set) qrHeight$value = QrCodeConfig.$default$qrHeight();
            int margin$value = this.margin$value;
            if (!this.margin$set) margin$value = QrCodeConfig.$default$margin();
            Color backgroundColor$value = this.backgroundColor$value;
            if (!this.backgroundColor$set) backgroundColor$value = QrCodeConfig.$default$backgroundColor();
            Color gradientStart$value = this.gradientStart$value;
            if (!this.gradientStart$set) gradientStart$value = QrCodeConfig.$default$gradientStart();
            Color gradientEnd$value = this.gradientEnd$value;
            if (!this.gradientEnd$set) gradientEnd$value = QrCodeConfig.$default$gradientEnd();
            boolean useRadialGradient$value = this.useRadialGradient$value;
            if (!this.useRadialGradient$set) useRadialGradient$value = QrCodeConfig.$default$useRadialGradient();
            Color finderColor$value = this.finderColor$value;
            if (!this.finderColor$set) finderColor$value = QrCodeConfig.$default$finderColor();
            boolean drawFinderGradient$value = this.drawFinderGradient$value;
            if (!this.drawFinderGradient$set) drawFinderGradient$value = QrCodeConfig.$default$drawFinderGradient();
            ModuleShape moduleShape$value = this.moduleShape$value;
            if (!this.moduleShape$set) moduleShape$value = QrCodeConfig.$default$moduleShape();
            String moduleName$value = this.moduleName$value;
            if (!this.moduleName$set) moduleName$value = QrCodeConfig.$default$moduleName();
            boolean drawShadows$value = this.drawShadows$value;
            if (!this.drawShadows$set) drawShadows$value = QrCodeConfig.$default$drawShadows();
            Color shadowColor$value = this.shadowColor$value;
            if (!this.shadowColor$set) shadowColor$value = QrCodeConfig.$default$shadowColor();
            float shadowOffsetPct$value = this.shadowOffsetPct$value;
            if (!this.shadowOffsetPct$set) shadowOffsetPct$value = QrCodeConfig.$default$shadowOffsetPct();
            String logoResourceUrl$value = this.logoResourceUrl$value;
            if (!this.logoResourceUrl$set) logoResourceUrl$value = QrCodeConfig.$default$logoResourceUrl();
            float logoScale$value = this.logoScale$value;
            if (!this.logoScale$set) logoScale$value = QrCodeConfig.$default$logoScale();
            String displayLabel$value = this.displayLabel$value;
            if (!this.displayLabel$set) displayLabel$value = QrCodeConfig.$default$displayLabel();
            Color displayLabelFontColor$value = this.displayLabelFontColor$value;
            if (!this.displayLabelFontColor$set) displayLabelFontColor$value = QrCodeConfig.$default$displayLabelFontColor();
            boolean addHri$value = this.addHri$value;
            if (!this.addHri$set) addHri$value = QrCodeConfig.$default$addHri();
            boolean compressDigitalLink$value = this.compressDigitalLink$value;
            if (!this.compressDigitalLink$set) compressDigitalLink$value = QrCodeConfig.$default$compressDigitalLink();
            return new QrCodeConfig(this.data, designPreset$value, mimeType$value, compressWithUppercase$value, qrWidth$value, qrHeight$value, margin$value, backgroundColor$value, gradientStart$value, gradientEnd$value, useRadialGradient$value, finderColor$value, drawFinderGradient$value, moduleShape$value, moduleName$value, drawShadows$value, shadowColor$value, shadowOffsetPct$value, logoResourceUrl$value, logoScale$value, displayLabel$value, displayLabelFontColor$value, addHri$value, compressDigitalLink$value);
        }

        @Override
        public String toString() {
            return "QrCodeConfig.QrCodeConfigBuilder(data=" + this.data + ", designPreset$value=" + this.designPreset$value + ", mimeType$value=" + this.mimeType$value + ", compressWithUppercase$value=" + this.compressWithUppercase$value + ", qrWidth$value=" + this.qrWidth$value + ", qrHeight$value=" + this.qrHeight$value + ", margin$value=" + this.margin$value + ", backgroundColor$value=" + this.backgroundColor$value + ", gradientStart$value=" + this.gradientStart$value + ", gradientEnd$value=" + this.gradientEnd$value + ", useRadialGradient$value=" + this.useRadialGradient$value + ", finderColor$value=" + this.finderColor$value + ", drawFinderGradient$value=" + this.drawFinderGradient$value + ", moduleShape$value=" + this.moduleShape$value + ", moduleName$value=" + this.moduleName$value + ", drawShadows$value=" + this.drawShadows$value + ", shadowColor$value=" + this.shadowColor$value + ", shadowOffsetPct$value=" + this.shadowOffsetPct$value + ", logoResourceUrl$value=" + this.logoResourceUrl$value + ", logoScale$value=" + this.logoScale$value + ", displayLabel$value=" + this.displayLabel$value + ", displayLabelFontColor$value=" + this.displayLabelFontColor$value + ", addHri$value=" + this.addHri$value + ", compressDigitalLink$value=" + this.compressDigitalLink$value + ")";
        }
    }

    public static QrCodeConfig.QrCodeConfigBuilder builder() {
        return new QrCodeConfig.QrCodeConfigBuilder();
    }

    public String getData() {
        return this.data;
    }

    /**
     * Name associated with the QR code to be generated. If EPCIS/GS1 then corresponding default values will be used from extensions module.
     */
    public String getDesignPreset() {
        return this.designPreset;
    }

    /**
     * The output file format (e.g., "image/png", "image/jpeg). Defaults to image/png
     */
    public String getMimeType() {
        return this.mimeType;
    }

    /**
     * Convert to uppercase URL for better compression. Default is true.
     */
    public boolean isCompressWithUppercase() {
        return this.compressWithUppercase;
    }

    /**
     * The width of the QR code in pixels. Default is 400.
     */
    public int getQrWidth() {
        return this.qrWidth;
    }

    /**
     * The height of the QR code in pixels. Default is 400.
     */
    public int getQrHeight() {
        return this.qrHeight;
    }

    /**
     * Quiet zone (margin) around the QR code, in modules. Default is 4.
     */
    public int getMargin() {
        return this.margin;
    }

    /**
     * Background color of the entire QR image. Default is Color.WHITE.
     */
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    /**
     * The start color of the module gradient. Default is Color.BLACK.
     */
    public Color getGradientStart() {
        return this.gradientStart;
    }

    /**
     * The end color of the module gradient. Default is Color.BLACK (i.e., no visible gradient).
     */
    public Color getGradientEnd() {
        return this.gradientEnd;
    }

    /**
     * If true, use a radial gradient; if false, linear gradient. Default is false.
     */
    public boolean isUseRadialGradient() {
        return this.useRadialGradient;
    }

    /**
     * Fallback color for the finder patterns if not drawing them in gradient. Default: Color.BLACK.
     */
    public Color getFinderColor() {
        return this.finderColor;
    }

    /**
     * If true, apply the gradient to the finder pattern as well. Default is false.
     */
    public boolean isDrawFinderGradient() {
        return this.drawFinderGradient;
    }

    /**
     * Shape of each QR module. Default is SQUARE.
     */
    public ModuleShape getModuleShape() {
        return this.moduleShape;
    }

    /**
     * Text for QR code module if ModuleShape is Name. Default is A
     */
    public String getModuleName() {
        return this.moduleName;
    }

    /**
     * If true, draw a shadow behind each filled module. Default is false.
     */
    public boolean isDrawShadows() {
        return this.drawShadows;
    }

    /**
     * The color of the shadow, can have alpha. Default is semi-transparent black.
     */
    public Color getShadowColor() {
        return this.shadowColor;
    }

    /**
     * Offset of the shadow relative to module size. E.g. 0.1f = 10%. Default is 0.1f.
     */
    public float getShadowOffsetPct() {
        return this.shadowOffsetPct;
    }

    /**
     * Optional URL for a logo to overlay at the center. Default is null (no logo).
     */
    public String getLogoResourceUrl() {
        return this.logoResourceUrl;
    }

    /**
     * The fraction of the QR code size for the logo (e.g. 0.2 = 20%). Default is 0.0f (no visible
     * logo).
     */
    public float getLogoScale() {
        return this.logoScale;
    }

    /**
     * The text label to be displayed at the bottom right (Ex: can be company name, tagline, ect). Default is null
     */
    public String getDisplayLabel() {
        return this.displayLabel;
    }

    /**
     * The company font color. Default: Color.BLACK.
     */
    public Color getDisplayLabelFontColor() {
        return this.displayLabelFontColor;
    }

    /**
     * Add the HRI (Human Readable Interpretation) text below the QR code. Default is false.
     */
    public boolean isAddHri() {
        return this.addHri;
    }

    /**
     * Whether to compress the GS1 Digital Link URL before generating the QR code. Default is false.
     */
    public boolean isCompressDigitalLink() {
        return this.compressDigitalLink;
    }

    public void setData(String data) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        this.data = data;
    }

    /**
     * Name associated with the QR code to be generated. If EPCIS/GS1 then corresponding default values will be used from extensions module.
     */
    public void setDesignPreset(String designPreset) {
        this.designPreset = designPreset;
    }

    /**
     * The output file format (e.g., "image/png", "image/jpeg). Defaults to image/png
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Convert to uppercase URL for better compression. Default is true.
     */
    public void setCompressWithUppercase(boolean compressWithUppercase) {
        this.compressWithUppercase = compressWithUppercase;
    }

    /**
     * The width of the QR code in pixels. Default is 400.
     */
    public void setQrWidth(int qrWidth) {
        this.qrWidth = qrWidth;
    }

    /**
     * The height of the QR code in pixels. Default is 400.
     */
    public void setQrHeight(int qrHeight) {
        this.qrHeight = qrHeight;
    }

    /**
     * Quiet zone (margin) around the QR code, in modules. Default is 4.
     */
    public void setMargin(int margin) {
        this.margin = margin;
    }

    /**
     * Background color of the entire QR image. Default is Color.WHITE.
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * The start color of the module gradient. Default is Color.BLACK.
     */
    public void setGradientStart(Color gradientStart) {
        this.gradientStart = gradientStart;
    }

    /**
     * The end color of the module gradient. Default is Color.BLACK (i.e., no visible gradient).
     */
    public void setGradientEnd(Color gradientEnd) {
        this.gradientEnd = gradientEnd;
    }

    /**
     * If true, use a radial gradient; if false, linear gradient. Default is false.
     */
    public void setUseRadialGradient(boolean useRadialGradient) {
        this.useRadialGradient = useRadialGradient;
    }

    /**
     * Fallback color for the finder patterns if not drawing them in gradient. Default: Color.BLACK.
     */
    public void setFinderColor(Color finderColor) {
        this.finderColor = finderColor;
    }

    /**
     * If true, apply the gradient to the finder pattern as well. Default is false.
     */
    public void setDrawFinderGradient(boolean drawFinderGradient) {
        this.drawFinderGradient = drawFinderGradient;
    }

    /**
     * Shape of each QR module. Default is SQUARE.
     */
    public void setModuleShape(ModuleShape moduleShape) {
        this.moduleShape = moduleShape;
    }

    /**
     * Text for QR code module if ModuleShape is Name. Default is A
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * If true, draw a shadow behind each filled module. Default is false.
     */
    public void setDrawShadows(boolean drawShadows) {
        this.drawShadows = drawShadows;
    }

    /**
     * The color of the shadow, can have alpha. Default is semi-transparent black.
     */
    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
    }

    /**
     * Offset of the shadow relative to module size. E.g. 0.1f = 10%. Default is 0.1f.
     */
    public void setShadowOffsetPct(float shadowOffsetPct) {
        this.shadowOffsetPct = shadowOffsetPct;
    }

    /**
     * Optional URL for a logo to overlay at the center. Default is null (no logo).
     */
    public void setLogoResourceUrl(String logoResourceUrl) {
        this.logoResourceUrl = logoResourceUrl;
    }

    /**
     * The fraction of the QR code size for the logo (e.g. 0.2 = 20%). Default is 0.0f (no visible
     * logo).
     */
    public void setLogoScale(float logoScale) {
        this.logoScale = logoScale;
    }

    /**
     * The text label to be displayed at the bottom right (Ex: can be company name, tagline, ect). Default is null
     */
    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    /**
     * The company font color. Default: Color.BLACK.
     */
    public void setDisplayLabelFontColor(Color displayLabelFontColor) {
        this.displayLabelFontColor = displayLabelFontColor;
    }

    /**
     * Add the HRI (Human Readable Interpretation) text below the QR code. Default is false.
     */
    public void setAddHri(boolean addHri) {
        this.addHri = addHri;
    }

    /**
     * Whether to compress the GS1 Digital Link URL before generating the QR code. Default is false.
     */
    public void setCompressDigitalLink(boolean compressDigitalLink) {
        this.compressDigitalLink = compressDigitalLink;
    }

    @Override
    public String toString() {
        return "QrCodeConfig(data=" + this.getData() + ", designPreset=" + this.getDesignPreset() + ", mimeType=" + this.getMimeType() + ", compressWithUppercase=" + this.isCompressWithUppercase() + ", qrWidth=" + this.getQrWidth() + ", qrHeight=" + this.getQrHeight() + ", margin=" + this.getMargin() + ", backgroundColor=" + this.getBackgroundColor() + ", gradientStart=" + this.getGradientStart() + ", gradientEnd=" + this.getGradientEnd() + ", useRadialGradient=" + this.isUseRadialGradient() + ", finderColor=" + this.getFinderColor() + ", drawFinderGradient=" + this.isDrawFinderGradient() + ", moduleShape=" + this.getModuleShape() + ", moduleName=" + this.getModuleName() + ", drawShadows=" + this.isDrawShadows() + ", shadowColor=" + this.getShadowColor() + ", shadowOffsetPct=" + this.getShadowOffsetPct() + ", logoResourceUrl=" + this.getLogoResourceUrl() + ", logoScale=" + this.getLogoScale() + ", displayLabel=" + this.getDisplayLabel() + ", displayLabelFontColor=" + this.getDisplayLabelFontColor() + ", addHri=" + this.isAddHri() + ", compressDigitalLink=" + this.isCompressDigitalLink() + ")";
    }
}
