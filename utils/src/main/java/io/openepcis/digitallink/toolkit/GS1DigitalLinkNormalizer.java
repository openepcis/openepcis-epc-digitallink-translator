package io.openepcis.digitallink.toolkit;


import io.openepcis.digitallink.model.ApplicationIdentifier;
import io.openepcis.digitallink.utils.AiEntries;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
public class GS1DigitalLinkNormalizer {

    @Inject
     private AiEntries aiEntries;

    /**
     * Normalizes Digital Link paths by converting human-readable shortcodes to GS1 Application Identifiers (AI).
     * <p>
     * This method processes a path string, examining each segment to convert any shortcode identifiers
     * (like "gtin", "lot", "ser") to their corresponding GS1 Application Identifiers (like "01", "10", "21").
     * The method preserves any query parameters and only transforms the path segments.
     * <p>
     * Examples:
     * <ul>
     *   <li>Input: {@code /gtin/09506000164908/10/lot1?linktype=linkset}
     *       <br>Output: {@code /01/09506000164908/10/lot1?linktype=linkset}</li>
     *   <li>Input: {@code /gtin/09506000164908/ser/abc123}
     *       <br>Output: {@code /01/09506000164908/21/abc123}</li>
     * </ul>
     * <p>
     * Note: The method preserves segments that don't have a corresponding AI entry.
     * Empty or null inputs are returned as-is.
     *
     * @param dlUrl The Digital Link path to normalize
     * @return The normalized path with shortcodes converted to GS1 Application Identifiers
     */

    public String normalize(String dlUrl) {
        // Handle empty or null params
        if (StringUtils.isEmpty(dlUrl)) {
            return dlUrl;
        }

        // Remove trailing slash
        String normalizedUrl = StringUtils.removeEnd(dlUrl, "/");

        // Split the params by slash to process each segment
        String[] segments = normalizedUrl.split("/");

        // Process each segment
        for (int i = 0; i < segments.length; i++) {
            ApplicationIdentifier entry = aiEntries.getEntry(segments[i]);
            if (entry != null && entry.getAi() != null) {
                segments[i] = entry.getAi();
            }
        }

        // Join segments with slashes
        return String.join("/", segments);
    }

}