package io.openepcis.digitallink.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GS1DigitalLinkParser {

    // match /<AI>/<value> in the path
    private static final Pattern PATH_PATTERN = Pattern.compile("/(\\d{2,4})/([^/]*)");

    // match <AI>=<value> in query string
    private static final Pattern QUERY_PATTERN = Pattern.compile("(\\d{2,4})=([^&]*)");


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
    public static Map<String, String> parse(final URL digitalLink,
                                            final boolean includeUrlMeta) {
        final LinkedHashMap<String, String> extractedData = new LinkedHashMap<>();

        // Optionally include protocol and domain
        if (includeUrlMeta) {
            extractedData.put("protocol", digitalLink.getProtocol());
            extractedData.put("domain", digitalLink.getHost());
            extractedData.put("port", String.valueOf(digitalLink.getPort() == -1 ? digitalLink.getDefaultPort() : digitalLink.getPort()));
        }

        // Extract path segments using the defined pattern
        final String path = digitalLink.getPath();
        final Matcher pathMatcher = PATH_PATTERN.matcher(path);

        while (pathMatcher.find()) {
            final String ai = pathMatcher.group(1);
            final String value = URLDecoder.decode(pathMatcher.group(2), StandardCharsets.UTF_8);
            extractedData.put(ai, value);
        }


        // Extract Query Parameters
        final String query = digitalLink.getQuery();
        if (StringUtils.isNotBlank(query)) {
            final Matcher queryMatcher = QUERY_PATTERN.matcher(query);
            while (queryMatcher.find()) {
                final String ai = queryMatcher.group(1);
                final String value = URLDecoder.decode(queryMatcher.group(2), StandardCharsets.UTF_8);
                extractedData.put(ai, value);
            }
        }

        return extractedData;
    }

    /**
     * Method overload: always excludes protocol and domain.
     */
    public static Map<String, String> parse(final URL digitalLink) {
        return parse(digitalLink, false);
    }
}
