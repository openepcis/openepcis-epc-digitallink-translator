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
package io.openepcis.qrcode.generator.resource.params;


import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import static io.openepcis.qrcode.generator.util.QrCodeConstants.*;

/**
 * Grouped header & query parameters for QR Code generation.
 */
@Schema(name = "QrGenerationParams", description = "Common parameters for QR generation")
public class QrCodeGenerationParams {
    @HeaderParam("Accept")
    @Parameter(description = API_ACCEPT_PARAMETER_DESCRIPTION, schema = @Schema(type = SchemaType.STRING, defaultValue = "image/png"))
    public String accept;

    @HeaderParam("designPreset")
    @Parameter(description = API_DESIGN_PRESET_PARAMETER_DESCRIPTION, schema = @Schema(type = SchemaType.STRING))
    public String designPresetHeader;

    @QueryParam("_designPreset")
    @Parameter(hidden = true, description = API_DESIGN_PRESET_PARAMETER_DESCRIPTION, schema = @Schema(type = SchemaType.STRING))
    public String designPresetQuery;

    @HeaderParam("hri")
    @Parameter(description = API_HRI_PARAMETER_DESCRIPTION, schema = @Schema(type = SchemaType.BOOLEAN, defaultValue = "false"))
    public boolean hriHeader;

    @QueryParam("_hri")
    @Parameter(hidden = true, description = API_HRI_PARAMETER_DESCRIPTION, schema = @Schema(type = SchemaType.BOOLEAN, defaultValue = "false"))
    public boolean hriQuery;

    @HeaderParam("compressed")
    @Parameter(description = API_COMPRESSED_PARAMETER_DESCRIPTION, schema = @Schema(type = SchemaType.BOOLEAN, defaultValue = "false"))
    public boolean compressedHeader;

    @QueryParam("_compressed")
    @Parameter(hidden = true, description = API_COMPRESSED_PARAMETER_DESCRIPTION, schema = @Schema(type = SchemaType.BOOLEAN, defaultValue = "false"))
    public boolean compressedQuery;

    public String getDesignPresetHeader() {
        return StringUtils.isNotBlank(designPresetHeader) ? designPresetHeader : designPresetQuery;
    }

    public boolean getHriHeader() {
        return hriHeader || hriQuery;
    }

    public boolean getCompressedHeader() {
        return compressedHeader || compressedQuery;
    }
}