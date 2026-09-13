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
    public String accept = "image/png";

    @HeaderParam("Design-Preset")
    @Parameter(description = API_DESIGN_PRESET_PARAMETER_DESCRIPTION, schema = @Schema(type = SchemaType.STRING))
    public String designPresetHeader;

    @QueryParam("_designPreset")
    @Parameter(hidden = true, description = API_DESIGN_PRESET_PARAMETER_DESCRIPTION, schema = @Schema(type = SchemaType.STRING))
    public String designPresetQuery;

    @HeaderParam("HRI")
    @Parameter(description = API_HRI_PARAMETER_DESCRIPTION, schema = @Schema(type = SchemaType.BOOLEAN, defaultValue = "false"))
    public boolean hriHeader;

    @QueryParam("_hri")
    @Parameter(hidden = true, description = API_HRI_PARAMETER_DESCRIPTION, schema = @Schema(type = SchemaType.BOOLEAN, defaultValue = "false"))
    public boolean hriQuery;

    @HeaderParam("Compressed")
    @Parameter(description = API_COMPRESSED_PARAMETER_DESCRIPTION, schema = @Schema(type = SchemaType.BOOLEAN, defaultValue = "false"))
    public boolean compressedHeader;

    @QueryParam("_compressed")
    @Parameter(hidden = true, description = API_COMPRESSED_PARAMETER_DESCRIPTION, schema = @Schema(type = SchemaType.BOOLEAN, defaultValue = "false"))
    public boolean compressedQuery;

    @HeaderParam("If-None-Match")
    @Parameter(hidden = true, description = "Conditional request: returns 304 Not Modified when the ETag still matches.", schema = @Schema(type = SchemaType.STRING))
    public String ifNoneMatchHeader;

    public String getDesignPresetHeader() {
        return StringUtils.isNotBlank(designPresetHeader) ? designPresetHeader : designPresetQuery;
    }

    public boolean getHriHeader() {
        return hriHeader || hriQuery;
    }

    public boolean getCompressedHeader() {
        return compressedHeader || compressedQuery;
    }

    public String getIfNoneMatchHeader() {
        return ifNoneMatchHeader;
    }
}