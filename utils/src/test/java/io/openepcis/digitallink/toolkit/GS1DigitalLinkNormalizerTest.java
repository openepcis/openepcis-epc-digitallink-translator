package io.openepcis.digitallink.toolkit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GS1DigitalLinkNormalizerTest {

    private final static GS1DigitalLinkNormalizer normalizer = new GS1DigitalLinkNormalizer();

    @ParameterizedTest
    @MethodSource("normalizationTestCases")
    public void testNormalize(String inputUrl, String expectedUrl) throws MalformedURLException {
        URL url = URI.create(inputUrl).toURL();
        URL normalizedUrl = normalizer.normalize(url);
        assertEquals(expectedUrl, normalizedUrl.toString());
    }

    @Test
    public void testNormalizeNullUrl() throws MalformedURLException {
        assertNull(normalizer.normalize(null));
    }

    static Stream<Arguments> normalizationTestCases() {
        return Stream.of(
            // Path normalization only
            Arguments.of(
                "https://id.gs1.org/gtin/09506000164908",
                "https://id.gs1.org/01/09506000164908"
            ),

            // Path with multiple segments
            Arguments.of(
                "https://id.gs1.org/gtin/09506000164908/lot/ABC123/ser/XYZ789",
                "https://id.gs1.org/01/09506000164908/10/ABC123/21/XYZ789"
            ),

            // Query parameter normalization only
            Arguments.of(
                "https://id.gs1.org?gtin=09506000164908&lot=ABC123",
                "https://id.gs1.org?01=09506000164908&10=ABC123"
            ),

            // Both path and query normalization
            Arguments.of(
                "https://id.gs1.org/gtin/09506000164908?lot=ABC123&ser=XYZ789",
                "https://id.gs1.org/01/09506000164908?10=ABC123&21=XYZ789"
            ),

            // Mixed AI codes and shortcodes
            Arguments.of(
                "https://id.gs1.org/01/09506000164908/lot/ABC123?21=XYZ789&exp=230101",
                "https://id.gs1.org/01/09506000164908/10/ABC123?21=XYZ789&17=230101"
            ),

            // Non-AI query parameters should be preserved
            Arguments.of(
                "https://id.gs1.org/gtin/09506000164908?linktype=all&lot=ABC123",
                "https://id.gs1.org/01/09506000164908?linktype=all&10=ABC123"
            ),

            // Empty query parameter value
            Arguments.of(
                "https://id.gs1.org/gtin/09506000164908?lot=",
                "https://id.gs1.org/01/09506000164908?10="
            ),

            // Query parameter without value
            Arguments.of(
                "https://id.gs1.org/gtin/09506000164908?novalue",
                "https://id.gs1.org/01/09506000164908?novalue"
            )
        );
    }
}
