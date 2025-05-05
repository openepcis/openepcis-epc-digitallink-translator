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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.openepcis.qrcode.generator.exception.QrCodeGeneratorException;
import io.openepcis.qrcode.generator.spi.service.QrCodeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
public class QrCodeGenerator {

    protected final QrCodeConfigService qrCodeConfigService = QrCodeConfigService.getInstance();

    /**
     * Generates a QR code image based on the provided configuration.
     *
     * @param qrCodeConfig The configuration object containing all the parameters for QR code generation.
     */
    public byte[] generateQRCode(final QrCodeConfig qrCodeConfig) {
        log.debug("Started generating the QR code based on provided config.");
        try {
            // Use SPI to apply default configuration values (if any provider supports the provided name)
            final QrCodeConfig config = qrCodeConfigService.applyDefaultConfig(qrCodeConfig);

            // Create a HashMap to store encoding hints (including CHARACTER_SET or MARGIN).
            final Map<EncodeHintType, Object> encodingHints = new HashMap<>();
            encodingHints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.ISO_8859_1);
            encodingHints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            encodingHints.put(EncodeHintType.MARGIN, config.getMargin());

            // Create a Zxing QRCodeWriter object to generate the QR code.
            final QRCodeWriter writer = new QRCodeWriter();

            // Encode the data into a BitMatrix, specifying format, dimensions, and hints.
            final String encodeURL = config.isCompressWithUppercase() ? StringUtils.upperCase(config.getData()) : config.getData();
            final BitMatrix bitMatrix = writer.encode(StringUtils.trim(encodeURL), BarcodeFormat.QR_CODE, 1, 1, encodingHints);

            // Create a BufferedImage (ARGB) to hold the QR code image.
            final BufferedImage bufferedImage = new BufferedImage(config.getQrWidth(), config.getQrHeight(), BufferedImage.TYPE_INT_RGB);

            // Graphics2D for drawing on the image.
            final Graphics2D g2d = bufferedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fill background
            g2d.setColor(config.getBackgroundColor());
            g2d.fillRect(0, 0, config.getQrWidth(), config.getQrHeight());

            // Compute module sizes from the BitMatrix
            final int matrixWidth = bitMatrix.getWidth();
            final int matrixHeight = bitMatrix.getHeight();
            final float moduleSizeX = (float) config.getQrWidth() / matrixWidth;
            final float moduleSizeY = (float) config.getQrHeight() / matrixHeight;
            final float moduleSize = Math.min(moduleSizeX, moduleSizeY);

            // Prepare the Paint (gradient or solid) for modules
            final Paint modulePaint =
                    createGradientPaint(0, 0, config.getQrWidth(), config.getQrHeight(), config.getGradientStart(), config.getGradientEnd(), config.isUseRadialGradient());

            // Determine the Logos center bounding box to skip any QR code modules being drawn there
            int centerX = 0, centerY = 0, logoW = 0, logoH = 0;
            boolean skipCenter = false;
            if (config.getLogoResourceUrl() != null && StringUtils.isNotBlank(config.getLogoResourceUrl().toString())) {
                // Pre-calculate the bounding box for the logo
                logoW = (int) (config.getQrWidth() * config.getLogoScale());
                logoH = (int) (config.getQrHeight() * config.getLogoScale());
                centerX = (config.getQrWidth() - logoW) / 2;
                centerY = (config.getQrHeight() - logoH) / 2;
                skipCenter = true; // We want to skip drawing modules in that center area
            }

            // Step-1 : Draw regular modules (excluding finder pattern areas, and excluding center area if
            // skipCenter is true)
            for (int y = 0; y < matrixHeight; y++) {
                for (int x = 0; x < matrixWidth; x++) {
                    boolean isFinder = isInFinderPattern(x, y, matrixWidth, matrixHeight);

                    if (bitMatrix.get(x, y) && !isFinder) {
                        // Convert (x,y) in the matrix to actual pixel coords
                        final float px = x * moduleSizeX;
                        final float py = y * moduleSizeY;
                        final float moduleRight = px + moduleSizeX;
                        final float moduleBottom = py + moduleSizeY;

                        // If we must skip the center bounding box for the logo:
                        if (skipCenter
                                && doesOverlap(px, py, moduleRight, moduleBottom, centerX, centerY, logoW, logoH)) {
                            // do NOT draw here, leave blank for the Logo
                            continue;
                        }

                        // Otherwise, draw the module
                        drawSingleModule(
                                g2d,
                                x,
                                y,
                                moduleSizeX,
                                moduleSizeY,
                                moduleSize,
                                config.getModuleShape(),
                                config.isDrawShadows(),
                                config.getShadowColor(),
                                config.getShadowOffsetPct(),
                                modulePaint);
                    }
                }
            }

            // Step-2: Draw the finder patterns (3 corner squares) for easy detection by QR code readers
            final Paint finderPaint = (config.isDrawFinderGradient()) ? modulePaint : config.getFinderColor();
            // top-left
            drawFinderPattern(
                    g2d,
                    bitMatrix,
                    0,
                    0,
                    7,
                    moduleSizeX,
                    moduleSizeY,
                    moduleSize,
                    config.getModuleShape(),
                    config.isDrawShadows(),
                    config.getShadowColor(),
                    config.getShadowOffsetPct(),
                    finderPaint);

            // top-right
            drawFinderPattern(
                    g2d,
                    bitMatrix,
                    matrixWidth - 7,
                    0,
                    7,
                    moduleSizeX,
                    moduleSizeY,
                    moduleSize,
                    config.getModuleShape(),
                    config.isDrawShadows(),
                    config.getShadowColor(),
                    config.getShadowOffsetPct(),
                    finderPaint);

            // bottom-left
            drawFinderPattern(
                    g2d,
                    bitMatrix,
                    0,
                    matrixHeight - 7,
                    7,
                    moduleSizeX,
                    moduleSizeY,
                    moduleSize,
                    config.getModuleShape(),
                    config.isDrawShadows(),
                    config.getShadowColor(),
                    config.getShadowOffsetPct(),
                    finderPaint);

            // Step-3: Place the logo in the center if provided
            if (skipCenter) {
                try {
                    final BufferedImage logo;
                    final String logoInput = config.getLogoResourceUrl();
                    final URI logoUri = new URI(logoInput);

                    if (!logoUri.isAbsolute()) {
                        // Treat as a relative file path. Adjust the base directory as needed.
                        final File logoFile = new File(logoInput);
                        if (!logoFile.exists()) {
                            log.error("Relative logo file not found: " + logoFile.getAbsolutePath());
                            throw new QrCodeGeneratorException("Relative logo file not found: " + logoFile.getAbsolutePath());
                        }
                        logo = ImageIO.read(logoFile);
                    } else {
                        // Absolute URI; convert to URL
                        final URL logoUrl = logoUri.toURL();
                        if ("file".equalsIgnoreCase(logoUrl.getProtocol())) {
                            // For local file URLs, read from file.
                            final File logoFile = new File(logoUrl.toURI());

                            if (!logoFile.exists()) {
                                log.error("Logo file not found: " + logoFile.getAbsolutePath());
                                throw new QrCodeGeneratorException("Logo file not found: " + logoFile.getAbsolutePath());
                            }
                            logo = ImageIO.read(logoFile);
                        } else {
                            // For remote URLs, read directly.
                            logo = ImageIO.read(logoUrl);
                        }
                    }

                    if (logo != null) {
                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                        // Draw the logo onto the blank region
                        g2d.drawImage(logo, centerX, centerY, logoW, logoH, null);
                    }
                } catch (Exception ex) {
                    // You can either log the error or wrap it in your custom exception.
                    log.error("Error generating the QR code: " + ex.getMessage(), ex);
                    throw new QrCodeGeneratorException("Error generating the QR code: " + ex.getMessage(), ex);
                }
            }

            // ---- Step-4: Draw the company name, if provided
            if (config.getDisplayLabel() != null && !config.getDisplayLabel().isEmpty()) {
                g2d.setColor(config.getDisplayLabelFontColor());
                g2d.setFont(new Font("Arial", Font.PLAIN, (int) (config.getQrWidth() * 0.03)));
                final FontMetrics fm = g2d.getFontMetrics();
                final int textWidth = fm.stringWidth(config.getDisplayLabel());
                final int textHeight = fm.getHeight();
                final int x = config.getQrWidth() - textWidth - 10;
                final int y = config.getQrHeight() - 10 - textHeight;
                g2d.drawString(config.getDisplayLabel(), x, y + fm.getAscent());
            }

            // Dispose Graphics
            g2d.dispose();

            // Step-5: Write the image to a ByteArrayOutputStream using that format and return bytes[]
            final String mimeFormat = StringUtils.substringAfter(config.getMimeType(), "/");
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, mimeFormat, byteArrayOutputStream);
            ImageIO.write(bufferedImage, mimeFormat, new File("fileName" + ".png"));

            log.debug("Generating the QR code completed.");
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating the QR Code " + e.getMessage(), e);
            throw new QrCodeGeneratorException(e.getMessage(), e);
        }
    }

    /**
     * Helper to see if the module’s bounding box overlaps the center logo area
     */
    private boolean doesOverlap(
            float left,
            float top,
            float right,
            float bottom,
            int logoX,
            int logoY,
            int logoW,
            int logoH) {
        if (right < logoX) return false; // module is left of logo
        if (left > logoX + logoW) return false; // module is right of logo
        if (bottom < logoY) return false; // module is above logo
        if (top > logoY + logoH) return false; // module is below logo
        return true;
    }

    /**
     * Creates either a linear or radial gradient paint based on user preferences.
     */
    private Paint createGradientPaint(
            final float x1,
            final float y1,
            final float x2,
            final float y2,
            final Color color1,
            final Color color2,
            final boolean useRadialGradient) {
        // Check if a radial gradient is requested.
        if (useRadialGradient) {
            // Calculate the center x-coordinate of the radial gradient.
            final float cx = (x1 + x2) / 2f;

            // Calculate the center y-coordinate of the radial gradient.
            final float cy = (y1 + y2) / 2f;

            // Calculate the radius of the radial gradient.
            final float radius = Math.max(x2 - x1, y2 - y1) / 2f;

            // Define the distribution of colors in the gradient (start and end).
            final float[] dist = {0f, 1f};

            // Define the colors for the gradient.
            final Color[] colors = {color1, color2};

            // Create and return a RadialGradientPaint object.
            return new RadialGradientPaint(
                    cx, cy, radius, dist, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE);
        } else {
            // Create and return a Linear GradientPaint object. Linear gradient from top-left to
            // bottom-right
            return new GradientPaint(x1, y1, color1, x2, y2, color2);
        }
    }

    /**
     * Checks if a module belongs to one of the 3 standard finder patterns (7x7).
     */
    private boolean isInFinderPattern(
            final int x, final int y, final int matrixWidth, final int matrixHeight) {
        // Define the size of the finder pattern (7x7).
        final int finderSize = 7;

        // Check if the module is within the top-left finder pattern.
        if (x < finderSize && y < finderSize) {
            return true;
        }

        // Check if the module is within the top-right finder pattern.
        if (x >= matrixWidth - finderSize && y < finderSize) {
            return true;
        }

        // Check if the module is within the bottom-left finder pattern.
        if (x < finderSize && y >= matrixHeight - finderSize) {
            return true;
        }

        // If none of the finder pattern conditions are met, return false.
        return false;
    }

    /**
     * Draws a single module (pixel) with optional shadow and custom shape.
     */
    private void drawSingleModule(
            final Graphics2D g2d,
            final int x,
            final int y,
            final float moduleSizeX,
            final float moduleSizeY,
            final float moduleSize,
            final QrCodeConfig.ModuleShape shape,
            final boolean drawShadows,
            final Color shadowColor,
            final float shadowOffsetPct,
            final Paint modulePaint) {

        final float px = x * moduleSizeX;
        final float py = y * moduleSizeY;

        // Check if shadows should be drawn and a shadow color is provided.
        if (drawShadows && shadowColor != null) {
            g2d.setColor(shadowColor); // Set the color for the shadow.

            final float offset = moduleSize * shadowOffsetPct; // Calculate the offset for the shadow.
            createShape(
                    g2d,
                    px + offset,
                    py + offset,
                    moduleSize,
                    shape); // Create & fill the shape for the shadow.
        }

        // Fill the module with the gradient or paint
        g2d.setPaint(modulePaint); // Set the paint for the module.
        createShape(g2d, px, py, moduleSize, shape); // Create & fill the shape for the module.
    }

    /**
     * Draws the 7x7 finder pattern block by iterating each module in that region.
     */
    private void drawFinderPattern(
            final Graphics2D g2d,
            final BitMatrix matrix,
            final int startX,
            final int startY,
            final int size,
            final float moduleSizeX,
            final float moduleSizeY,
            final float moduleSize,
            final QrCodeConfig.ModuleShape shape,
            final boolean drawShadows,
            final Color shadowColor,
            final float shadowOffsetPct,
            final Paint finderPaint) {
        // Iterate through each row of the finder pattern.
        for (int row = 0; row < size; row++) {
            // Iterate through each column of the finder pattern.
            for (int col = 0; col < size; col++) {
                // Check if the module at the current position is set in the BitMatrix.
                if (matrix.get(startX + col, startY + row)) {
                    final float px =
                            (startX + col) * moduleSizeX; // Calculate the pixel x-coordinate of the module.
                    final float py =
                            (startY + row) * moduleSizeY; // Calculate the pixel y-coordinate of the module.

                    // Check if shadows should be drawn.
                    if (drawShadows && shadowColor != null) {
                        g2d.setColor(shadowColor); // Set the color for the shadow.

                        final float offset =
                                moduleSize * shadowOffsetPct; // Calculate the offset for the shadow.
                        createShape(
                                g2d,
                                px + offset,
                                py + offset,
                                moduleSize,
                                shape); // Create & fill the shape for the shadow.
                    }

                    // Main fill
                    g2d.setPaint(finderPaint); // Set the paint for the module.
                    createShape(g2d, px, py, moduleSize, shape); // Create & fill the shape for the module.
                }
            }
        }
    }

    /**
     * Creates a shape for the module based on the chosen CustomShape (SQUARE, ROUNDED_RECT, CIRCLE, etc.).
     */
    private void createShape(
            final Graphics2D g2d,
            final float x,
            final float y,
            final float moduleSize,
            final QrCodeConfig.ModuleShape shape) {

        switch (shape) {
            // If the shape is SQUARE, create a Rectangle2D.Float.
            case SQUARE -> {
                // Create a new Rectangle2D.Float with the given x, y, and size.
                final Rectangle2D.Float rectAngle2d = new Rectangle2D.Float(x, y, moduleSize, moduleSize);
                g2d.fill(rectAngle2d);
            }
            // If the shape is ROUNDED_RECT, create a RoundRectangle2D.Float.
            case ROUNDED_RECT -> {
                // Calculate the arc size for the rounded corners (30% of the size).
                final float arc = moduleSize * 0.3f;
                final RoundRectangle2D.Float roundRectAngle2d = new RoundRectangle2D.Float(x, y, moduleSize, moduleSize, arc, arc);
                g2d.fill(roundRectAngle2d);
            }
            case DOT -> {
                int dotSize = (int) (moduleSize * 0.75);
                int dotOffset = (int) (moduleSize - dotSize) / 2;
                g2d.fillOval((int) x + dotOffset, (int) y + dotOffset, dotSize, dotSize);
            }
            case HEART -> {
                Path2D.Double heart = new Path2D.Double();
                // Adjust coordinates and curve control points to increase fill area
                heart.moveTo(x + moduleSize / 2, y + moduleSize * 0.2); // Adjusted top point
                heart.curveTo(x + moduleSize * 0.15, y + moduleSize * 0.1, x, y + moduleSize * 0.4, x + moduleSize / 2, y + moduleSize * 0.9); // Adjusted curve
                heart.curveTo(x + moduleSize, y + moduleSize * 0.4, x + moduleSize * 0.85, y + moduleSize * 0.1, x + moduleSize / 2, y + moduleSize * 0.2); // Adjusted curve
                heart.closePath();
                g2d.fill(heart);
            }
            case BARCODE -> {
                // Create BARCODE shape (vertical strip in the middle)
                float lineWidth = moduleSize * 0.4f;
                float offsetX = x + (moduleSize - lineWidth) / 2f;
                g2d.fillRect(Math.round(offsetX), Math.round(y), Math.round(lineWidth), Math.round(moduleSize));

            }
            case LETTER -> {
                // Custom NAME shape (if not provided draw letter A)
                g2d.setFont(new Font("Arial", Font.BOLD, (int) moduleSize));
                g2d.drawString("A", (int) x, (int) y + (int) moduleSize);
            }
            case STAR -> {
                final Path2D.Double star = new Path2D.Double();
                final double centerX = x + moduleSize / 2;
                final double centerY = y + moduleSize / 2;
                final double outerRadius = moduleSize / 2 * 1.1;
                final double innerRadius = outerRadius * 0.5;
                final int points = 4;

                for (int i = 0; i < points * 2; i++) {
                    final double angle = Math.PI / points * i;
                    final double radius = (i % 2 == 0) ? outerRadius : innerRadius;
                    final double dx = centerX + Math.cos(angle) * radius;
                    final double dy = centerY + Math.sin(angle) * radius;

                    if (i == 0) {
                        star.moveTo(dx, dy);
                    } else {
                        star.lineTo(dx, dy);
                    }
                }
                star.closePath();
                g2d.fill(star);
            }
            case TRIANGLE -> {
                final Path2D.Double triangle = new Path2D.Double();
                triangle.moveTo(x + moduleSize / 2, y); // top point
                triangle.lineTo(x, y + moduleSize); // bottom-left point
                triangle.lineTo(x + moduleSize, y + moduleSize); // bottom-right point
                triangle.closePath();
                g2d.fill(triangle);
            }
            case DIAMOND -> {
                final Path2D.Double diamond = new Path2D.Double();
                diamond.moveTo(x + moduleSize / 2, y); //top point
                diamond.lineTo(x + moduleSize, y + moduleSize / 2); // right point
                diamond.lineTo(x + moduleSize / 2, y + moduleSize); // bottom point
                diamond.lineTo(x, y + moduleSize / 2); // left point
                diamond.closePath();
                g2d.fill(diamond);
            }
            case WAVE -> {
                final Path2D.Double wave = new Path2D.Double();
                wave.moveTo(x, y + moduleSize / 2);
                for (double i = 0; i < moduleSize; i++) {
                    double waveY = y + moduleSize / 2 + Math.sin(i / (moduleSize / (2 * Math.PI))) * (moduleSize / 4);
                    wave.lineTo(x + i, waveY);
                }
                wave.lineTo(x + moduleSize, y + moduleSize);
                wave.lineTo(x, y + moduleSize);
                wave.closePath();
                g2d.fill(wave);
            }
            // Default to circle
            default -> {
                final Ellipse2D.Float ellipse2d = new Ellipse2D.Float(x, y, moduleSize, moduleSize);
                g2d.fill(ellipse2d);
            }
        }
    }
}
