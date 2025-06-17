package io.openepcis.digitallink.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GS1DigitalLinkParser {

    private static final Pattern GS1_DIGITAL_LINK_PATTERN_STRING = Pattern.compile("(?<=/)(\\d{2,4})/(\\d+)");

    /**
     * Parses GS1 identifiers and metadata from a Digital Link URL.
     * Ex: 01, 12345678901234
     * 10, 123456
     * 21, ABC123
     *
     * @param digitalLink    the full Digital Link URL
     * @param includeUrlMeta if true, adds "protocol" and "domain" entries
     * @return a Map (insertion-ordered) of extracted elements
     */
    public static Map<String, String> parseIdentifiersDataFromDigitalLink(final URL digitalLink,
                                                                          final boolean includeUrlMeta) {
        final LinkedHashMap<String, String> extractedData = new LinkedHashMap<>();

        // Optionally include protocol and domain
        if (includeUrlMeta) {
            extractedData.put("protocol", digitalLink.getProtocol());
            extractedData.put("domain", digitalLink.getHost());
            extractedData.put("port", String.valueOf(digitalLink.getPort() == -1 ? digitalLink.getDefaultPort() : digitalLink.getPort()));
        }

        // Extract GS1 AIs from path
        final String path = digitalLink.getPath();
        final Matcher matcher = GS1_DIGITAL_LINK_PATTERN_STRING.matcher(path);

        while (matcher.find()) {
            extractedData.put(matcher.group(1), matcher.group(2));
        }


        // Extract GS1 AIs from query parameters
        final String query = digitalLink.getQuery();
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
    public static Map<String, String> parseIdentifiersDataFromDigitalLink(final URL digitalLink) {
        return parseIdentifiersDataFromDigitalLink(digitalLink, false);
    }
}
