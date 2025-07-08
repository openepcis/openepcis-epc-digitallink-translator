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
package io.openepcis.qrcode.generator.resource.specs;

import io.openepcis.qrcode.generator.QrCodeConfig;
import io.openepcis.qrcode.generator.resource.params.QrCodeGenerationParams;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

import static io.openepcis.qrcode.generator.util.QrCodeConstants.*;
@Path("/qr")
@Tag(name = API_TAG_NAME, description = API_TAG_DESCRIPTION)
public interface QrCodeGeneratorApi {

    // Method to generate the QR Code based on user provided specifications as QrCodeConfig and return
    @POST
    @Path("/generate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("*/*")
    @Operation(summary = POST_API_OPERATION_SUMMARY, description = POST_API_OPERATION_DESCRIPTION)
    @RequestBody(
            description = "QR Code Generator Config",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = QrCodeConfig.class),
                            examples = {
                                    @ExampleObject(name = "OpenEPCIS Theme", ref = "OpenEPCISThemeQrConfig", description = "OpenEPCIS Theme QR Code Config."),
                                    @ExampleObject(name = "GS1 Theme", ref = "GS1ThemeQrConfig", description = "GS1 Theme QR Code Config."),
                                    @ExampleObject(name = "Shadow Gradient", ref = "ShadowGradientQrConfig", description = "Shadow Gradient Theme QR Code Config."),
                                    @ExampleObject(name = "Minimal MonoChrome", ref = "MinimalMonoChromeQrConfig", description = "Minimal MonoChrome Theme QR Code Config.")
                            }),
            }
    )
    @APIResponse(responseCode = "200", description = API_SUCCESS_RESPONSE, content = @Content(mediaType = "image/*"))
    @APIResponse(responseCode = "400", description = API_INVALID_REQUEST_RESPONSE)
    @APIResponse(responseCode = "406", description = API_UNSUPPORTED_ACCEPT_RESPONSE)
    @APIResponse(responseCode = "500", description = API__SERVER_ERROR_RESPONSE)
    Uni<Response> generate(
            @BeanParam final QrCodeGenerationParams params,
            final QrCodeConfig qrCodeConfig);

    @GET
    @Path("/{linkPath:.*}")
    @Produces("*/*")
    @Operation(summary = GET_API_OPERATION_SUMMARY, description = GET_API_OPERATION_DESCRIPTION)
    @APIResponse(responseCode = "200", description = API_SUCCESS_RESPONSE, content = @Content(mediaType = "image/*"))
    @APIResponse(responseCode = "400", description = API_INVALID_REQUEST_RESPONSE)
    @APIResponse(responseCode = "406", description = API_UNSUPPORTED_ACCEPT_RESPONSE)
    @APIResponse(responseCode = "500", description = API__SERVER_ERROR_RESPONSE)
    Uni<Response> fetch(@BeanParam final QrCodeGenerationParams params,
                            @PathParam("linkPath")
                            @Parameter(description = GET_API_PATH_PARAMETER_DESCRIPTION) final String linkPath);

    @OPTIONS
    @Operation(summary = OPTIONS_API_OPERATION_SUMMARY, description = OPTIONS_API_OPERATION_DESCRIPTION)
    @APIResponse(
            responseCode = "200",
            description = OPTIONS_API_RESPONSE_SUCCESS_DESCRIPTION,
            headers = {
                    @Header(name = "Accept-Get", description = OPTIONS_API_RESPONSE_SUCCESS_HEADER_GET, required = true, schema = @Schema(type = SchemaType.STRING)),
                    @Header(name = "Accept-Post", description = OPTIONS_API_RESPONSE_SUCCESS_HEADER_POST, required = true, schema = @Schema(type = SchemaType.STRING))
            })
    @APIResponse(responseCode = "500", description = OPTIONS_API_RESPONSE_ERROR_DESCRIPTION)
    Uni<Response> options();

    @GET
    @Path("/design-presets")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = GET_DESIGN_PRESET_API_OPERATION_SUMMARY, description = GET_DESIGN_PRESET_API_OPERATION_DESCRIPTION)
    Uni<List<QrCodeConfig>> listDesignPresets();

}
