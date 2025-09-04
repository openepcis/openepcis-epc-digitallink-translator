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
import io.openepcis.digitallink.toolkit.GS1DigitalLinkCompression;
import io.openepcis.digitallink.utils.GS1DigitalLinkParser;
import io.openepcis.qrcode.generator.exception.QrCodeGeneratorException;
import io.openepcis.qrcode.generator.spi.service.QrCodeConfigService;
import io.quarkus.logging.Log;
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
import java.util.List;
import java.util.*;

/**
 * QrCodeGenerator generates a QR code image with optional logo, display label, and HRI,
 * ensuring all visual elements are proportionate to the QR code's dimensions.
 */
@Slf4j
public class QrCodeGenerator {

    private final GS1DigitalLinkCompression compressor = new GS1DigitalLinkCompression();
    private final QrCodeConfigService qrCodeConfigService = QrCodeConfigService.getInstance();
    private static Font OCR_B_FONT;


    static {
        try {
            // Load the OCR-B font from the system or classpath
            OCR_B_FONT = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(QrCodeGenerator.class.getResourceAsStream("/OCR-B/font/OCR-B.ttf")))
                    .deriveFont(Font.PLAIN, 12);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(OCR_B_FONT);
            log.info("Loaded OCR-B font: " + OCR_B_FONT.getFontName());
        } catch (Exception e) {
            log.error("Failed to load OCR-B font: " + e.getMessage(), e);
            OCR_B_FONT = new Font("SansSerif", Font.PLAIN, 12); // Fallback to default font if loading fails
        }
    }

    /**
     * Generates a QR code image according to the provided configuration.
     *
     * @param qrCodeConfig Configuration object containing parameters for QR code generation.
     * @return Byte array of the generated image (QR code, optionally with HRI).
     */
    public byte[] generateQRCode(final QrCodeConfig qrCodeConfig) {
        log.debug("Generating QR code with config: {}", qrCodeConfig);

        try {
            // Use SPI to apply default configuration values (if any provider supports the provided name)
            final QrCodeConfig config = qrCodeConfigService.applyDefaultConfig(qrCodeConfig);

            // Create a HashMap to store encoding hints (including CHARACTER_SET or MARGIN).
            final Map<EncodeHintType, Object> encodingHints = new HashMap<>();
            encodingHints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.ISO_8859_1);
            encodingHints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            encodingHints.put(EncodeHintType.MARGIN, config.getMargin());

            // Create a Zxing QRCodeWriter object to generate the QR code.
            final String qrContent = prepareQrData(config);
            final QRCodeWriter qrWriter = new QRCodeWriter();
            final BitMatrix bitMatrix = qrWriter.encode(qrContent, BarcodeFormat.QR_CODE, 1, 1, encodingHints);

            // Create a BufferedImage (ARGB) to hold the QR code image.
            final BufferedImage qrImage = new BufferedImage(config.getQrWidth(), config.getQrHeight(), BufferedImage.TYPE_INT_RGB);
            final Graphics2D qrGraphics = qrImage.createGraphics();
            qrGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            qrGraphics.setColor(config.getBackgroundColor());
            qrGraphics.fillRect(0, 0, config.getQrWidth(), config.getQrHeight());

            // Compute module size (pixels per matrix cell)
            final int matrixWidth = bitMatrix.getWidth();
            final int matrixHeight = bitMatrix.getHeight();
            final float moduleSizeX = (float) config.getQrWidth() / matrixWidth;
            final float moduleSizeY = (float) config.getQrHeight() / matrixHeight;
            final float moduleSize = Math.min(moduleSizeX, moduleSizeY);

            // Paint for modules (gradient or solid)
            final Paint modulePaint =
                    createGradientPaint(config.getQrWidth(), config.getQrHeight(),
                            config.getGradientStart(), config.getGradientEnd(), config.isUseRadialGradient());

            // Calculate logo bounding box (centered, proportional to QR size)
            int logoX = 0, logoY = 0, logoW = 0, logoH = 0;
            boolean skipLogoArea = false;
            if (config.getLogoResourceUrl() != null && StringUtils.isNotBlank(config.getLogoResourceUrl())) {
                // Pre-calculate the bounding box for the logo
                logoW = (int) (config.getQrWidth() * config.getLogoScale());
                logoH = (int) (config.getQrHeight() * config.getLogoScale());
                logoX = (config.getQrWidth() - logoW) / 2;
                logoY = (config.getQrHeight() - logoH) / 2;
                skipLogoArea = true; // We want to skip drawing modules in that center area
            }

            // Draw regular modules (excluding finder pattern areas, and excluding center area if skipLogoArea is true
            for (int y = 0; y < matrixHeight; y++) {
                for (int x = 0; x < matrixWidth; x++) {
                    boolean isFinder = isInFinderPattern(x, y, matrixWidth, matrixHeight);

                    if (bitMatrix.get(x, y) && !isFinder) {
                        // Convert (x,y) in the matrix to actual pixel cords
                        final float px = x * moduleSizeX;
                        final float py = y * moduleSizeY;
                        final float moduleRight = px + moduleSizeX;
                        final float moduleBottom = py + moduleSizeY;

                        // If we must skip the center bounding box for the logo:
                        if (skipLogoArea
                                && doesOverlap(px, py, moduleRight, moduleBottom, logoX, logoY, logoW, logoH)) {
                            // do NOT draw here, leave blank for the Logo
                            continue;
                        }

                        // Otherwise, draw the module
                        drawSingleModule(qrGraphics, x, y, moduleSizeX, moduleSizeY, moduleSize, config.getModuleShape(), config.isDrawShadows(), config.getShadowColor(), config.getShadowOffsetPct(), modulePaint);
                    }
                }
            }

            // Draw the finder patterns (3 corner squares) for easy detection by QR code readers
            final Paint finderPaint = (config.isDrawFinderGradient()) ? modulePaint : config.getFinderColor();
            drawFinderPattern(qrGraphics, bitMatrix, 0, 0, moduleSize, config, finderPaint); // top-left
            drawFinderPattern(qrGraphics, bitMatrix, matrixWidth - 7, 0, moduleSize, config, finderPaint);  // top-right
            drawFinderPattern(qrGraphics, bitMatrix, 0, matrixHeight - 7, moduleSize, config, finderPaint); // bottom-left

            // Step-3: Draw the logo in the center if provided
            if (skipLogoArea) {
                drawLogo(qrGraphics, config.getLogoResourceUrl(), logoX, logoY, logoW, logoH);
            }

            // Draw display label, right-aligned, proportional font size
            if (StringUtils.isNotBlank(config.getDisplayLabel())) {
                log.debug("Adding label on the right based on provided in config.");
                final float fontSize = config.getQrHeight() * 0.03f;
                qrGraphics.setColor(config.getDisplayLabelFontColor());
                qrGraphics.setFont(new Font("Arial", Font.PLAIN, Math.round(fontSize)));
                final FontMetrics fm = qrGraphics.getFontMetrics();
                final int labelWidth = fm.stringWidth(config.getDisplayLabel());
                final int x = Math.round(config.getQrWidth() - labelWidth - config.getQrWidth() * 0.02f);
                final int y = Math.round(config.getQrHeight() - config.getQrHeight() * 0.02f - fm.getHeight());
                qrGraphics.drawString(config.getDisplayLabel(), x, y + fm.getAscent());
            }

            // Dispose Graphics
            qrGraphics.dispose();

            // Try to parse the URL, silently skip HRI if it fails
            boolean isValidUrl = false;
            URL digitalLinkURL = null;

            try {
                digitalLinkURL = new URI(config.getData()).toURL();
                isValidUrl = true;
            } catch (Exception e) {
                // Not a valid URL, skip HRI
                log.warn("Failed to parse URL for HRI data, hence skipping them : " + e.getMessage());
            }

            // If HRI enabled return generated QR Code image with HRI image
            if (config.isAddHri() && isValidUrl) {
                log.debug("Adding HRI data to the QR code image");
                return writeHRIData(digitalLinkURL, config, qrImage);
            }

            // If HRI is not enabled, return only the QR code image
            log.debug("Returning only the QR code image");
            return writeImageToBytes(qrImage, config.getMimeType());
        } catch (Exception e) {
            log.error("Error generating the QR code: " + e.getMessage(), e);
            throw new QrCodeGeneratorException("Error generating the QR code: " + e.getMessage(), e);
        }
    }

    // Method to prepare the content to be added for the generated QR Code
    private String prepareQrData(final QrCodeConfig config) {
        // Get the raw content
        String qrData = StringUtils.trim(config.getData());

        // If compression enabled then compress
        if (config.isCompressDigitalLink()) {
            qrData = compressor.compressGS1DigitalLink(qrData, true, true);
        }

        // If uppercase is enabled then convert to uppercase
        if (config.isCompressWithUppercase()) {
            qrData = StringUtils.upperCase(qrData);
        }

        return qrData;
    }


    /**
     * Writes an image to a byte array in the requested format.
     */
    private byte[] writeImageToBytes(final BufferedImage image, final String mimeType) {
        try {
            final String formatName = StringUtils.substringAfter(mimeType, "/");
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, formatName, byteArrayOutputStream);
            //ImageIO.write(image, formatName, new File("qrCode" + ".png"));
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error writing image to bytes: " + e.getMessage(), e);
            throw new QrCodeGeneratorException("Error writing image to bytes: " + e.getMessage(), e);
        }
    }

    /**
     * Helper to see if the module’s bounding box overlaps the center logo area
     */
    private boolean doesOverlap(float left, float top,
                                float right, float bottom,
                                int logoX, int logoY,
                                int logoW, int logoH) {
        if (right < logoX) return false; // module is left of logo
        if (left > logoX + logoW) return false; // module is right of logo
        if (bottom < logoY) return false; // module is above logo
        return !(top > logoY + logoH); // module is below logo
    }

    /**
     * Creates either a linear or radial gradient paint based on user preferences.
     */
    private Paint createGradientPaint(final float x2, final float y2,
                                      final Color color1, final Color color2,
                                      final boolean useRadialGradient) {
        // Check if a radial gradient is requested.
        if (useRadialGradient) {
            final float cx = ((float) 0 + x2) / 2f; // Calculate the center x-coordinate of the radial gradient.
            final float cy = ((float) 0 + y2) / 2f; // Calculate the center y-coordinate of the radial gradient.
            final float radius = Math.max(x2 - (float) 0, y2 - (float) 0) / 2f; // Calculate the radius of the radial gradient.
            final float[] dist = {0f, 1f}; // Define the distribution of colors in the gradient (start and end).
            final Color[] colors = {color1, color2}; // Define the colors for the gradient.

            // Create and return a RadialGradientPaint object.
            return new RadialGradientPaint(
                    cx, cy, radius, dist, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE);
        } else {
            // Create and return a Linear GradientPaint object. Linear gradient from top-left to bottom-right
            return new GradientPaint((float) 0, (float) 0, color1, x2, y2, color2);
        }
    }

    /**
     * Checks if a module belongs to one of the 3 standard finder patterns (7x7).
     */
    private boolean isInFinderPattern(final int x, final int y,
                                      final int matrixWidth, final int matrixHeight) {
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
        return x < finderSize && y >= matrixHeight - finderSize;

        // If none of the finder pattern conditions are met, return false.
    }

    /**
     * Draws a single module (pixel) with optional shadow and custom shape.
     */
    private void drawSingleModule(final Graphics2D g2d, final int x, final int y,
                                  final float moduleSizeX, final float moduleSizeY, final float moduleSize,
                                  final QrCodeConfig.ModuleShape shape, final boolean drawShadows,
                                  final Color shadowColor, final float shadowOffsetPct, final Paint modulePaint) {

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
    private void drawFinderPattern(final Graphics2D g2d, final BitMatrix matrix,
                                   final int startX, final int startY,
                                   final float moduleSize, final QrCodeConfig config,
                                   final Paint finderPaint) {
        // Iterate through each row of the finder pattern.
        for (int row = 0; row < 7; row++) {
            // Iterate through each column of the finder pattern.
            for (int col = 0; col < 7; col++) {
                // Check if the module at the current position is set in the BitMatrix.
                if (matrix.get(startX + col, startY + row)) {
                    float px = (startX + col) * ((float) config.getQrWidth() / matrix.getWidth()); // Calculate the pixel x-coordinate of the module.
                    float py = (startY + row) * ((float) config.getQrHeight() / matrix.getHeight()); // Calculate the pixel y-coordinate of the module.

                    // Check if shadows should be drawn.
                    if (config.isDrawFinderGradient() && config.getShadowColor() != null) {
                        g2d.setColor(config.getShadowColor()); // Set the color for the shadow.
                        float offset = moduleSize * config.getShadowOffsetPct(); // Calculate the offset for the shadow.
                        createShape(g2d, px + offset, py + offset, moduleSize, config.getModuleShape()); // Create & fill the shape for the shadow.
                    }

                    // Main fill
                    g2d.setPaint(finderPaint); // Set the paint for the module.
                    createShape(g2d, px, py, moduleSize, config.getModuleShape()); // Create & fill the shape for the module.
                }
            }
        }
    }

    /**
     * Creates a shape for the module based on the chosen CustomShape (SQUARE, ROUNDED_RECT, CIRCLE, etc.).
     */
    private void createShape(final Graphics2D g2d, final float x, final float y,
                             final float moduleSize, final QrCodeConfig.ModuleShape shape) {

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

    /**
     * Draw logo image (keeps proportions and center).
     */
    private void drawLogo(final Graphics2D g2d, final String logoResourceUrl,
                          final int x, final int y,
                          final int w, final int h) {
        try {
            BufferedImage logo;
            Log.debug("reading logo from " + logoResourceUrl);
            final URI logoUri = new URI(logoResourceUrl);

            if (!logoUri.isAbsolute()) {
                Log.debug("use logo from absolute file path");
                // Treat as a relative file path. Adjust the base directory as needed.
                final File logoFile = new File(logoResourceUrl);
                if (!logoFile.exists()) {
                    log.error("Relative logo file not found: " + logoFile.getAbsolutePath());
                    throw new QrCodeGeneratorException("Relative logo file not found: " + logoFile.getAbsolutePath());
                }
                logo = ImageIO.read(logoFile);
            } else {
                Log.debug("use logo from url");
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
                g2d.drawImage(logo, x, y, w, h, null); // Draw the logo onto the blank region
            } else {
                log.warn("Logo image could not be loaded from URL: {}", logoResourceUrl);
            }
        } catch (Exception ex) {
            log.error("Could not draw logo: " + ex.getMessage(), ex);
            throw new QrCodeGeneratorException("Error generating the QR code: " + ex.getMessage(), ex);
        }
    }

    /**
     * Writes HRI (Human Readable Interpretation) data below the QR code image if enabled in QRCodeConfig.
     */
    private byte[] writeHRIData(final URL digitalLinkURL, final QrCodeConfig config, final BufferedImage qrImage) {
        // All HRI paddings and font-size proportional to QR height/width
        final float hriFontSize = 12; // Font size for HRI text
        final int hriPaddingX = Math.round(config.getQrWidth() * 0.05f); // Padding on left/right of HRI text
        final int hriAvailableWidth = config.getQrWidth() - 2 * hriPaddingX; // Available width for HRI text
        final int hriPaddingTop = Math.round(config.getMargin() * 0.045f); // Padding above HRI text

        // Get HRI font metrics
        final BufferedImage tempImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        final Graphics2D tempG = tempImg.createGraphics();
        tempG.setFont(OCR_B_FONT.deriveFont(hriFontSize));
        final FontMetrics hriFM = tempG.getFontMetrics();
        tempG.dispose();

        // Parse and Format HRI lines to fit in available width
        final Map<String, String> hriData = GS1DigitalLinkParser.parse(digitalLinkURL);
        final List<String> hriLines = formatHRIForQr(hriData, config.getQrWidth(), qrImage.createGraphics().getFontMetrics(OCR_B_FONT));
        final int hriLineHeight = hriFM.getHeight();
        final int hriBlockHeight = Math.max(1, hriPaddingTop + hriLines.size() * hriLineHeight);

        // Create HRI image
        final BufferedImage hriImage = new BufferedImage(config.getQrWidth(), hriBlockHeight, BufferedImage.TYPE_INT_RGB);
        final Graphics2D hriG = hriImage.createGraphics();
        hriG.setColor(Color.WHITE);
        hriG.fillRect(0, 0, config.getQrWidth(), hriBlockHeight);
        hriG.setFont(OCR_B_FONT.deriveFont(hriFontSize));
        hriG.setColor(Color.BLACK);

        // Draw each line of HRI text centered within the available width
        int hriY = hriPaddingTop + hriFM.getAscent();
        for (String line : hriLines) {
            int lineWidth = hriFM.stringWidth(line);
            int hriX = hriPaddingX + Math.max(0, (hriAvailableWidth - lineWidth) / 2);
            // Clamp within padded area
            if (hriX + lineWidth > config.getQrWidth() - hriPaddingX) {
                hriX = config.getQrWidth() - hriPaddingX - lineWidth;
                if (hriX < hriPaddingX) hriX = hriPaddingX;
            }
            hriG.drawString(line, hriX, hriY);
            hriY += hriLineHeight;
        }
        hriG.dispose();

        // Combine QR and HRI images vertically
        int combinedHeight = config.getQrHeight() + hriBlockHeight;
        BufferedImage combinedImage = new BufferedImage(config.getQrWidth(), combinedHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D cg = combinedImage.createGraphics();
        cg.drawImage(qrImage, 0, 0, null);
        cg.drawImage(hriImage, 0, config.getQrHeight(), null);
        cg.dispose();

        // Write the combined image to bytes and return
        return writeImageToBytes(combinedImage, config.getMimeType());
    }

    /**
     * Format GS1 HRI lines so each fits within maxLineWidth.
     */
    private List<String> formatHRIForQr(final Map<String, String> gs1Data,
                                        final int availableLineWidth,
                                        final FontMetrics fm) {
        final List<String> formattedHRI = new ArrayList<>();
        final StringBuilder currentLine = new StringBuilder();
        final int spaceWidth = fm.stringWidth(" "); // Width of a space character

        for (Map.Entry<String, String> entry : gs1Data.entrySet()) {
            final String formattedPair = "(" + entry.getKey() + ")" + entry.getValue();
            final int pairWidth = fm.stringWidth(formattedPair);

            // If the current line is empty or adding the new pair doesn't exceed max width, append it
            if (currentLine.length() == 0 ||
                    fm.stringWidth(currentLine.toString()) + spaceWidth + pairWidth <= availableLineWidth) {
                // If text fits, add it to the current line and Add a space before appending
                if (currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(formattedPair);
            } else {
                // If text exceeds width, move to next line
                formattedHRI.add(currentLine.toString());
                currentLine.setLength(0); // Clear the current line
                currentLine.append(formattedPair); // Start a new line with the current pair
            }
        }

        // add the remaining line
        if (currentLine.length() > 0) formattedHRI.add(currentLine.toString());
        return formattedHRI;
    }
}
