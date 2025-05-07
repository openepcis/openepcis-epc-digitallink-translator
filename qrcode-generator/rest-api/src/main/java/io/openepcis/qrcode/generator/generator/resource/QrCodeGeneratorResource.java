package io.openepcis.qrcode.generator.generator.resource;

import io.openepcis.qrcode.generator.QrCodeConfig;
import io.openepcis.qrcode.generator.QrCodeGenerator;
import io.openepcis.qrcode.generator.exception.QrCodeGeneratorException;
import io.openepcis.qrcode.generator.spi.service.QrCodeConfigService;
import io.openepcis.qrcode.generator.util.QrCodeConstants;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestHeader;

import javax.imageio.ImageIO;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Path("/")
@Tag(name = QrCodeConstants.API_TAG_NAME, description = QrCodeConstants.API_TAG_DESCRIPTION)
@RequiredArgsConstructor
public class QrCodeGeneratorResource {
    @Inject
    QrCodeGenerator qrCodeGenerator;

    @ConfigProperty(name = "gs1.digital-link.base-url", defaultValue = QrCodeConstants.GS1_IDENTIFIER_DOMAIN)
    String gs1DigitalLinkBaseUrl;


    // Method to generate the QR Code based on user provided specifications as QrCodeConfig and return
    @POST
    @Path("/generateQrCode")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("*/*")
    @Operation(summary = QrCodeConstants.POST_API_OPERATION_SUMMARY, description = QrCodeConstants.POST_API_OPERATION_DESCRIPTION)
    @RequestBody(
            description = "QR Code Generator Config",
            required = true,
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
            })
    @APIResponses(
            {
                    @APIResponse(responseCode = "200", description = QrCodeConstants.API_SUCCESS_RESPONSE, content = @Content(mediaType = "image/*")),
                    @APIResponse(responseCode = "400", description = QrCodeConstants.API_INVALID_REQUEST_RESPONSE),
                    @APIResponse(responseCode = "406", description = QrCodeConstants.API_UNSUPPORTED_ACCEPT_RESPONSE),
                    @APIResponse(responseCode = "500", description = QrCodeConstants.API__SERVER_ERROR_RESPONSE)
            }
    )
    @Tag(name = QrCodeConstants.API_TAG_NAME, description = QrCodeConstants.POST_API_TAG_DESCRIPTION)
    public Uni<Response> generateQrCode(
            final QrCodeConfig qrCodeConfig,
            @Parameter(
                    name = "Accept",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = QrCodeConstants.API_ACCEPT_PARAMETER_DESCRIPTION,
                    schema = @Schema(defaultValue = "image/png")
            )
            @RestHeader("Accept") String accept,
            @Parameter(
                    name = "QR-Design-Preset",
                    in = ParameterIn.HEADER,
                    description = QrCodeConstants.API_DESIGN_PRESET_PARAMETER_DESCRIPTION,
                    schema = @Schema(defaultValue = "")
            )
            @RestHeader("QR-Design-Preset") String designPreset) {

        // If output MIME type is provided then add the respective MIME type else default
        if (StringUtils.isNotBlank(accept)) {
            qrCodeConfig.setMimeType(accept);
        }

        // If name is provided then set the name in the config so default config is used if described
        if (StringUtils.isNotBlank(designPreset)) {
            qrCodeConfig.setDesignPreset(designPreset);
        }

        return Uni.createFrom().item(() -> {
            try {
                return Response.ok(
                        qrCodeGenerator.generateQRCode(qrCodeConfig),
                        qrCodeConfig.getMimeType()
                ).build();
            } catch (Exception e) {
                throw new QrCodeGeneratorException("Error generating the QR code : " + e.getMessage(), e);
            }
        });
    }

    @GET
    @Path("/qr/{path: .*}")
    @Produces("*/*")
    @Operation(summary = QrCodeConstants.GET_API_OPERATION_SUMMARY, description = QrCodeConstants.GET_API_OPERATION_DESCRIPTION)
    @APIResponses(
            {
                    @APIResponse(responseCode = "200", description = QrCodeConstants.API_SUCCESS_RESPONSE, content = @Content(mediaType = "image/*")),
                    @APIResponse(responseCode = "400", description = QrCodeConstants.API_INVALID_REQUEST_RESPONSE),
                    @APIResponse(responseCode = "406", description = QrCodeConstants.API_UNSUPPORTED_ACCEPT_RESPONSE),
                    @APIResponse(responseCode = "500", description = QrCodeConstants.API__SERVER_ERROR_RESPONSE)
            }
    )
    @Tag(name = QrCodeConstants.API_TAG_NAME, description = QrCodeConstants.GET_API_TAG_DESCRIPTION)
    public Uni<Response> getQrCode(
            @Parameter(name = "path", in = ParameterIn.PATH, required = true, description = QrCodeConstants.GET_API_PATH_PARAMETER_DESCRIPTION)
            @PathParam("path") final String path,
            @Parameter(name = "Accept",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = QrCodeConstants.API_ACCEPT_PARAMETER_DESCRIPTION,
                    schema = @Schema(defaultValue = "image/png")
            )
            @RestHeader("accept") final String accept,
            @Parameter(
                    name = "QR-Design-Preset",
                    in = ParameterIn.HEADER,
                    description = QrCodeConstants.API_DESIGN_PRESET_PARAMETER_DESCRIPTION,
                    schema = @Schema(defaultValue = "")
            )
            @RestHeader("QR-Design-Preset") String designPreset) {

        final String dlUrl = gs1DigitalLinkBaseUrl + path;
        final QrCodeConfig.QrCodeConfigBuilder cfgBuilder = QrCodeConfig.builder().data(dlUrl);

        // Set the mimeType if matches one of the value from Image.io mime type
        if (accept != null && Arrays.asList(ImageIO.getWriterMIMETypes()).contains(accept.toLowerCase())) {
            cfgBuilder.mimeType(accept);
        }

        // If name is provided then set the name in the config so default config is used if described
        if (StringUtils.isNotBlank(designPreset)) {
            cfgBuilder.designPreset(designPreset);
        }

        final QrCodeConfig cfg = cfgBuilder.build();

        return Uni.createFrom().item(
                Response.ok(
                        qrCodeGenerator.generateQRCode(cfg),
                        cfg.getMimeType()
                ).build()
        );
    }

    @OPTIONS
    @Path("/{path: .*}")
    @Operation(summary = QrCodeConstants.OPTIONS_API_OPERATION_SUMMARY, description = QrCodeConstants.OPTIONS_API_OPERATION_DESCRIPTION)
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = QrCodeConstants.OPTIONS_API_RESPONSE_SUCCESS_DESCRIPTION,
                    headers = {
                            @Header(
                                    name = "Accept-Get",
                                    description = QrCodeConstants.OPTIONS_API_RESPONSE_SUCCESS_HEADER_GET,
                                    required = true,
                                    schema = @Schema(type = SchemaType.STRING)
                            ),
                            @Header(
                                    name = "Accept-Post",
                                    description = QrCodeConstants.OPTIONS_API_RESPONSE_SUCCESS_HEADER_POST,
                                    required = true,
                                    schema = @Schema(type = SchemaType.STRING)
                            )
                    }),
            @APIResponse(responseCode = "500", description = QrCodeConstants.OPTIONS_API_RESPONSE_ERROR_DESCRIPTION)
    })
    @Tag(name = QrCodeConstants.API_TAG_NAME, description = QrCodeConstants.GET_API_TAG_DESCRIPTION)
    public Uni<Response> getOptions() {
        return Uni.createFrom().item(
                Response.ok()
                        .header("Accept-Get", String.join(",", QrCodeConstants.ACCEPT_HEADER))
                        .header("Accept-Post", String.join(",", QrCodeConstants.ACCEPT_HEADER)).build()
        );
    }

    @GET
    @Path("/qr-design-presets")
    @Operation(summary = QrCodeConstants.GET_DESIGN_PRESET_API_OPERATION_SUMMARY, description = QrCodeConstants.GET_DESIGN_PRESET_API_OPERATION_DESCRIPTION)
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = QrCodeConstants.API_TAG_NAME, description = QrCodeConstants.GET_API_TAG_DESCRIPTION)
    public Uni<List<QrCodeConfig>> listAllDesignPresets() {
        List<QrCodeConfig> presets = QrCodeConfigService.getInstance()
                .getAllProviders()
                .stream()
                .map(provider -> provider.customizeConfig(QrCodeConfig.builder()
                        .data("")
                        .build()))
                .filter(config -> StringUtils.isNotBlank(config.getDesignPreset()))
                .map(config -> {
                    // Replace full logoResourceUrl with a relative path or file name if present
                    if(StringUtils.isNotBlank(config.getLogoResourceUrl())){
                        final String fileName = Paths.get(config.getLogoResourceUrl()).getFileName().toString();
                        config.setLogoResourceUrl(fileName);
                    }
                    return config;
                })
                .toList();

        return Uni.createFrom().item(presets);
    }

}
