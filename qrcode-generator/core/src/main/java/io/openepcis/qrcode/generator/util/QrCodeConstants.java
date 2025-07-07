package io.openepcis.qrcode.generator.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QrCodeConstants {
    public static final String[] ACCEPT_HEADER = {"image/png", "image/jpeg", "image/jpg", "image/gif", "image/bmp", "image/tiff"};
    public static final String API_TAG_NAME = "QR Code Generator";
    public static final String API_TAG_DESCRIPTION = "Endpoints for generating QR codes.";
    public static final String POST_API_OPERATION_SUMMARY = "Generate a QR code with custom configuration and Digital Link WebURI";
    public static final String POST_API_OPERATION_DESCRIPTION = "Accepts a JSON configuration `QrCodeConfig` specifying the desired parameters (e.g., data, format, color, label etc.) and returns a QR code image. Multiple image formats are supported (PNG, JPEG, etc.) via the `Accept` header.";
    public static final String POST_API_TAG_DESCRIPTION = "Endpoint for generating QR codes with customizable configurations.";
    public static final String GET_API_OPERATION_SUMMARY = "Generate a QR code with default config for provided Digital Link WebURI";
    public static final String GET_API_OPERATION_DESCRIPTION = "Given a path that will be appended to the provided domain or defaults to GS1 Identifier Domain (`https://id.gs1.org/`), this endpoint generates a QR code image. If the `Accept` header matches a supported image MIME type (as determined by `ImageIO`), the QR code is returned in that format. Otherwise, a defaults to `image/png` format.";
    public static final String GET_API_TAG_DESCRIPTION = "Endpoint for generating QR codes with default configurations.";
    public static final String API_SUCCESS_RESPONSE = "Successfully generated QR code image.";
    public static final String API_INVALID_REQUEST_RESPONSE = "Invalid request or configuration.";
    public static final String API_UNSUPPORTED_ACCEPT_RESPONSE = "Unsupported Accept header value.";
    public static final String API__SERVER_ERROR_RESPONSE = "Internal error generating the QR code";
    public static final String GS1_IDENTIFIER_DOMAIN = "https://id.gs1.org/";
    public static final String GET_API_PATH_PARAMETER_DESCRIPTION = "Identifiers to be encoded in generated QR code.";
    public static final String API_ACCEPT_PARAMETER_DESCRIPTION = "Accept header to control image media type (defaults to image/png if not provided).";
    public static final String API_DESIGN_PRESET_PARAMETER_DESCRIPTION = "Specifies the pre-defined design preset associated with the QR code configuration. This may trigger custom defaults if set to a value like 'openepcis, gs1, etc.'.";
    public static final String API_HRI_PARAMETER_DESCRIPTION = "Specifies whether the Human Readable Interpretation (HRI) should be included in the QR code. Defaults to false if not specified.";
    public static final String API_COMPRESSED_PARAMETER_DESCRIPTION = "Specifies whether DL URL should be compressed before generating QR Code. Defaults to false if not specified and if enabled cannot generate HRI.";
    public static final String OPTIONS_API_OPERATION_SUMMARY = "Retrieve available QR code generation options";
    public static final String OPTIONS_API_OPERATION_DESCRIPTION = "This endpoint provides metadata about the supported QR code image formats (for both GET and POST requests) via custom response headers. Call this method to see what MIME types are available from the server before generating or retrieving QR codes.";
    public static final String OPTIONS_API_RESPONSE_SUCCESS_DESCRIPTION = "Successfully retrieved options for QR code generation and retrieval.";
    public static final String OPTIONS_API_RESPONSE_SUCCESS_HEADER_GET = "Comma-separated list of supported image MIME types for GET requests (e.g., image/png, image/jpeg).";
    public static final String OPTIONS_API_RESPONSE_SUCCESS_HEADER_POST = "Comma-separated list of supported image MIME types for POST requests (e.g., image/png, image/jpeg).";
    public static final String OPTIONS_API_RESPONSE_ERROR_DESCRIPTION = "Internal server error retrieving the options.";
    public static final String GET_DESIGN_PRESET_API_OPERATION_SUMMARY = "List all available QR code design presets";
    public static final String GET_DESIGN_PRESET_API_OPERATION_DESCRIPTION = "Fetches a list of predefined QR code design presets (`QrCodeConfig`). Each preset includes default styling attributes such as dimensions, gradient colors, background, and logo positioning, which can be applied when generating QR codes.";
}
