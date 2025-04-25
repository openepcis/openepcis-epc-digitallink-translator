package io.openepcis.qrcode.generator.generator.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.openepcis.qrcode.generator.util.QrCodeConstants;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.Components;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.examples.Example;
import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Dynamically inject the Options using the Custom OASFilter
 */
@RegisterForReflection
public class CustomOASFilter implements OASFilter {

    public static final String GS1_THEME_QR_CODE_CONFIG = "GS1ThemeQrConfig";
    public static final String MINIMAL_MONOCHROME_QR_CODE_CONFIG = "MinimalMonoChromeQrConfig";
    public static final String OPEN_EPCIS_THEME_QR_CODE_CONFIG = "OpenEPCISThemeQrConfig";
    public static final String SHADOW_GRADIENT_QR_CODE_CONFIG = "ShadowGradientQrConfig";

    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected Components defaultComponents;

    @Override
    public void filterOpenAPI(OpenAPI openAPI) {
        try {
            defaultComponents = OASFactory.createComponents();
            if (openAPI.getComponents() == null) {
                openAPI.setComponents(defaultComponents);
            }

            generateExamples().forEach(openAPI.getComponents()::addExample);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Map<String, Example> generateExamples() throws IOException {
        final Map<String, Example> examples = new LinkedHashMap<>();
        // Adding the QR Code Config samples to OpenAPI examples
        // Read and get all config
        final String gs1ThemeQrConfig = new String(
                Objects.requireNonNull(
                        getClass()
                                .getClassLoader()
                                .getResourceAsStream("QrCodeConfig/GS1ThemeQrConfig.json")).readAllBytes());

        final String minimalMonoChromeQrConfig = new String(
                Objects.requireNonNull(
                        getClass()
                                .getClassLoader()
                                .getResourceAsStream("QrCodeConfig/MinimalMonoChromeQrConfig.json")).readAllBytes());

        final String openEPCISThemeQrConfig = new String(
                Objects.requireNonNull(
                        getClass()
                                .getClassLoader()
                                .getResourceAsStream("QrCodeConfig/OpenEPCISThemeQrConfig.json")).readAllBytes());

        final String shadowGradientQrConfig = new String(
                Objects.requireNonNull(
                        getClass()
                                .getClassLoader()
                                .getResourceAsStream("QrCodeConfig/ShadowGradientQrConfig.json")).readAllBytes());

        // Adding all QR code config file
        examples.put(GS1_THEME_QR_CODE_CONFIG, OASFactory.createExample().value(objectMapper.readValue(gs1ThemeQrConfig, ObjectNode.class)));
        examples.put(MINIMAL_MONOCHROME_QR_CODE_CONFIG, OASFactory.createExample().value(objectMapper.readValue(minimalMonoChromeQrConfig, ObjectNode.class)));
        examples.put(OPEN_EPCIS_THEME_QR_CODE_CONFIG, OASFactory.createExample().value(objectMapper.readValue(openEPCISThemeQrConfig, ObjectNode.class)));
        examples.put(SHADOW_GRADIENT_QR_CODE_CONFIG, OASFactory.createExample().value(objectMapper.readValue(shadowGradientQrConfig, ObjectNode.class)));

        return examples;
    }

    // Dynamically add the ACCEPT_HEADER values to @Produces of POST/GET endpoints from QrCodeGeneratorResource
    @Override
    public Operation filterOperation(final Operation operation) {
        if (operation.getOperationId().equalsIgnoreCase("generateQrCode") || operation.getOperationId().equalsIgnoreCase("getQrCode")) {
            // Get all responses for the API endpoint
            final APIResponses responses = operation.getResponses();

            // If null then return and do nothing
            if (responses == null) {
                return operation;
            }

            // Remove the auto‐generated 200 response
            responses.removeAPIResponse("200");

            // Create a new 200 response from scratch
            final APIResponse okResponse = OASFactory.createAPIResponse().description("OK — generated QR code image successfully.");
            responses.addAPIResponse("200", okResponse);

            // Attach dynamic MIME types
            final Content content = OASFactory.createContent();
            okResponse.setContent(content);

            // For each MIME type, add a MediaType entry
            for (String mime : QrCodeConstants.ACCEPT_HEADER) {
                content.addMediaType(mime, OASFactory.createMediaType());
            }
        }
        return operation;
    }
}
