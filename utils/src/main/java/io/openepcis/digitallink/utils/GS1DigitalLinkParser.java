package io.openepcis.digitallink.utils;

import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GS1DigitalLinkParser {

    private static final String GS1_PATTERN = "(?<=/)(\\d{2,4})/(\\d+)";
    private static final Pattern pattern = Pattern.compile(GS1_PATTERN);

    /**
     * Parses a GS1 Digital Link URL, extracting GS1 AIs (2–4 digit keys) from both path and query.
     * Includes protocol and domain only if requested.
     *
     * @param digitalLink    the full URL string
     * @param includeUrlMeta if true, adds "protocol" and "domain" entries
     * @return a LinkedHashMap where keys are AI codes (and optionally "protocol"/"domain")
     * @throws IllegalArgumentException if the input is not a well-formed URL
     */
    public static LinkedHashMap<String, String> digitalLinkToHRI(final String digitalLink, final boolean includeUrlMeta) {
        final LinkedHashMap<String, String> extractedData = new LinkedHashMap<>();

        URL digitalLinkUrl;
        try {
            final URI uri = new URI(digitalLink);
            digitalLinkUrl = uri.toURL();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL format: " + digitalLink, e);
        }

        // Optionally include protocol and domain
        if (includeUrlMeta) {
            extractedData.put("protocol", digitalLinkUrl.getProtocol());
            extractedData.put("domain", digitalLinkUrl.getHost());
            extractedData.put("port", String.valueOf(digitalLinkUrl.getPort() == -1 ? digitalLinkUrl.getDefaultPort() : digitalLinkUrl.getPort()));
        }

        // Extract GS1 AIs from path
        final String path = digitalLinkUrl.getPath();
        final Matcher matcher = pattern.matcher(path);

        while (matcher.find()) {
            extractedData.put(matcher.group(1), matcher.group(2));
        }


        // Extract GS1 AIs from query parameters
        final String query = digitalLinkUrl.getQuery();
        if (StringUtils.isNotBlank(query)) {
            for (String pair : query.split("&")) {
                final String[] parts = pair.split("=");
                if (parts.length == 2 && parts[0].matches("\\d{2,4}")) {
                    extractedData.put(parts[0], parts[1]);
                }
            }
        }

        return extractedData;
    }

    /**
     * Method overload: always excludes protocol and domain.
     */
    public static LinkedHashMap<String, String> digitalLinkToHRI(final String digitalLink) {
        return digitalLinkToHRI(digitalLink, false);
    }
}
