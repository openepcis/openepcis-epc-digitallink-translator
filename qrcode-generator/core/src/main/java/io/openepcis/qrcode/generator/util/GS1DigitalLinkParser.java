package io.openepcis.qrcode.generator.util;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GS1DigitalLinkParser {

    private static final String GS1_PATTERN = "(?<=/)(\\d{2,4})/(\\d+)";
    private static final Pattern pattern = Pattern.compile(GS1_PATTERN);

    public static LinkedHashMap<String, String> digitalLinkToHRI(final String digitalLink) {
        final LinkedHashMap<String, String> extractedData = new LinkedHashMap<>();

        // Extracting AI & values from path parameters
        final Matcher matcher = pattern.matcher(digitalLink);

        while (matcher.find()) {
            extractedData.put(matcher.group(1), matcher.group(2));
        }

        // Extracting AI & values from query parameters
        final String[] queryParams = digitalLink.split("\\?");
        if (queryParams.length > 1) {
            final String[] queryPairs = queryParams[1].split("&");
            for (String pair : queryPairs) {
                final String[] parts = pair.split("=");
                if (parts.length == 2) {
                    extractedData.put(parts[0], parts[1]);
                }
            }
        }

        return extractedData;
    }
}
