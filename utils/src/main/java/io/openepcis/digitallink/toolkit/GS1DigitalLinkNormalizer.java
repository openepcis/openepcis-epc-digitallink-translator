package io.openepcis.digitallink.toolkit;

import io.openepcis.digitallink.model.ApplicationIdentifier;
import io.openepcis.digitallink.utils.AiEntries;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Utility class for normalizing GS1 Digital Link URLs by converting human-readable shortcodes
 * to their corresponding GS1 Application Identifiers (AI).
 */
@ApplicationScoped
public class GS1DigitalLinkNormalizer {

    /**
     * Normalizes Digital Link URLs by converting human-readable shortcodes to GS1 Application Identifiers (AI).
     * <p>
     * This method processes a URL's path and query parameters, examining each segment and parameter name
     * to convert any shortcode identifiers (like "gtin", "lot", "ser") to their corresponding
     * GS1 Application Identifiers (like "01", "10", "21").
     * <p>
     * Examples:
     * <ul>
     *   <li>Input URL path: {@code /gtin/09506000164908/10/lot1?linktype=linkset}
     *       <br>Output URL path: {@code /01/09506000164908/10/lot1?linktype=linkset}</li>
     *   <li>Input URL path: {@code /gtin/09506000164908/ser/abc123}
     *       <br>Output URL path: {@code /01/09506000164908/21/abc123}</li>
     *   <li>Input URL path: {@code /01/09506000164908?lot=ABC123}
     *       <br>Output URL path: {@code /01/09506000164908?10=ABC123}</li>
     * </ul>
     * <p>
     * Note: The method preserves segments and query parameters that don't have a corresponding AI entry.
     * Empty or null inputs are returned as-is.
     *
     * @param digitalLink The Digital Link URL to normalize
     * @return A new URL with the normalized path and query parameters where shortcodes are converted to GS1 Application Identifiers
     * @throws MalformedURLException If the resulting URL is not properly formatted
     */
    public URL normalize(URL digitalLink) throws MalformedURLException {
        // Handle null input
        if (digitalLink == null) {
            return null;
        }

        // Normalize path and query components
        String normalizedPath = normalizePath(digitalLink.getPath());
        String normalizedQuery = normalizeQuery(digitalLink.getQuery());

        // Construct the normalized URL
        return constructNormalizedUrl(
                digitalLink.getProtocol(),
                digitalLink.getHost(),
                digitalLink.getPort(),
                normalizedPath,
                normalizedQuery
        );
    }

    /**
     * Normalizes the path component of a URL by converting shortcodes to GS1 Application Identifiers.
     *
     * @param path The path to normalize
     * @return The normalized path
     */
    private String normalizePath(String path) {
        if (StringUtils.isEmpty(path)) {
            return path;
        }

        // Remove trailing slash
        String cleanPath = StringUtils.removeEnd(path, "/");

        // Split the path by slash to process each segment
        String[] segments = cleanPath.split("/");

        // Process each segment
        for (int i = 0; i < segments.length; i++) {
            segments[i] = normalizeIdentifier(segments[i]);
        }

        // Join segments with slashes
        return String.join("/", segments);
    }

    /**
     * Normalizes the query component of a URL by converting shortcodes to GS1 Application Identifiers.
     *
     * @param query The query to normalize
     * @return The normalized query, or null if the input is empty
     */
    private String normalizeQuery(String query) {
        if (StringUtils.isEmpty(query)) {
            return null;
        }

        String[] queryParams = query.split("&");
        StringBuilder queryBuilder = new StringBuilder(query.length());

        for (int i = 0; i < queryParams.length; i++) {
            if (i > 0) {
                queryBuilder.append('&');
            }

            normalizeQueryParameter(queryParams[i], queryBuilder);
        }

        return queryBuilder.toString();
    }

    /**
     * Normalizes a single query parameter and appends it to the query builder.
     *
     * @param queryParam The query parameter to normalize
     * @param queryBuilder The StringBuilder to append the normalized parameter to
     */
    private void normalizeQueryParameter(String queryParam, StringBuilder queryBuilder) {
        if (queryParam.contains("=")) {
            String[] keyValue = queryParam.split("=", 2);
            String key = keyValue[0];
            String value = keyValue.length > 1 ? keyValue[1] : "";

            // Normalize the key
            String normalizedKey = normalizeIdentifier(key);
            queryBuilder.append(normalizedKey).append('=').append(value);
        } else {
            // Parameter without value (no equals sign)
            String normalizedKey = normalizeIdentifier(queryParam);
            queryBuilder.append(normalizedKey);
        }
    }

    /**
     * Normalizes an identifier by converting it to its corresponding GS1 Application Identifier if applicable.
     *
     * @param identifier The identifier to normalize
     * @return The normalized identifier, or the original identifier if no mapping exists
     */
    private String normalizeIdentifier(String identifier) {
        ApplicationIdentifier applicationIdentifier = AiEntries.getEntry(identifier);
        if (applicationIdentifier != null && applicationIdentifier.getAi() != null) {
            return applicationIdentifier.getAi();
        }
        return identifier;
    }

    /**
     * Constructs a new URL from the given components.
     *
     * @param protocol The URL protocol
     * @param host The URL host
     * @param port The URL port
     * @param path The URL path
     * @param query The URL query, or null if there is no query
     * @return A new URL constructed from the given components
     * @throws MalformedURLException If the resulting URL is not properly formatted
     */
    private URL constructNormalizedUrl(String protocol, String host, int port, String path, String query) 
            throws MalformedURLException {
        try {
            // Create a URI with the components and convert it to a URL
            // Note: We pass the query as a separate parameter, not as part of the path
            URI uri = new URI(protocol, null, host, port, path, query, null);
            return uri.toURL();
        } catch (URISyntaxException e) {
            throw new MalformedURLException("Invalid URL components: " + e.getMessage());
        }
    }
}
