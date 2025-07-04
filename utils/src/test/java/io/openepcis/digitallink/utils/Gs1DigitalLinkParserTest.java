package io.openepcis.digitallink.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Gs1DigitalLinkParserTest {

    @ParameterizedTest
    @MethodSource("digitalLinkTestCases")
    public void testParse(final String dlURL,
                          final boolean includeMeta,
                          final Map<String, String> expected,
                          final boolean valid) throws MalformedURLException {

        final URL url = URI.create(dlURL).toURL();
        final Map<String, String> result = GS1DigitalLinkParser.parse(url, includeMeta);

        if (valid) {
            assertEquals(expected, result, "Should extract exactly the expected entries");
        } else {
            assertTrue(result.isEmpty(), "Invalid URI should yield an empty map");
        }
    }

    static Stream<Arguments> digitalLinkTestCases() {
        return Stream.of(
                // 1. Canonical GTIN only
                Arguments.of("https://id.gs1.org/01/09520123456788",
                        false,
                        Map.of("01", "09520123456788"),
                        true),

                // 2. Non-canonical domain + GTIN+CPV
                Arguments.of(
                        "https://brand.example.com/01/09520123456788/22/2A",
                        false,
                        Map.of("01", "09520123456788",
                                "22", "2A"),
                        true
                ),

                // 3. Canonical GTIN+Batch/Lot
                Arguments.of(
                        "https://id.gs1.org/01/09520123456788/10/ABC123",
                        false,
                        Map.of("01", "09520123456788",
                                "10", "ABC123"),
                        true
                ),

                // 4. Canonical GTIN+Serial
                Arguments.of(
                        "https://id.gs1.org/01/09520123456788/21/12345",
                        false,
                        Map.of("01", "09520123456788",
                                "21", "12345"),
                        true
                ),

                // 5. Mixed path & query (Batch, Serial, Expiry)
                Arguments.of(
                        "https://id.gs1.org/01/09520123456788/10/ABC1/21/12345?17=180426",
                        false,
                        Map.of("01", "09520123456788",
                                "10", "ABC1",
                                "21", "12345",
                                "17", "180426"),
                        true
                ),

                // 6. Query-only form
                Arguments.of(
                        "https://id.gs1.org?01=09520123456788&21=12345",
                        false,
                        Map.of("01", "09520123456788",
                                "21", "12345"),
                        true
                ),

                // 7. Invalid extra path segment (12345) – only GTIN extracted
                Arguments.of(
                        "https://id.gs1.org/01/09520123456788/12345/XYZ",
                        false,
                        Map.of("01", "09520123456788"),
                        true
                ),

                // 8. Invalid character '@' in Batch → only GTIN extracted
                Arguments.of(
                        "https://id.gs1.org/01/09520123456788/10/LOT@123",
                        false,
                        Map.of("01", "09520123456788",
                                "10", "LOT@123"),
                        true
                ),

                // 9. Trailing slash with no value → only GTIN extracted
                Arguments.of(
                        "https://id.gs1.org/01/09520123456788/21/",
                        false,
                        Map.of("01", "09520123456788",
                                "21", ""),
                        true
                ),

                // 10. Mixed valid/invalid characters in Batch + an extra unknown AI=99
                Arguments.of(
                        "https://id.gs1.org/01/09520123456788/10/ABC-123_€?99=VALUE",
                        false,
                        Map.of("01", "09520123456788",
                                "10", "ABC-123_€",
                                "99", "VALUE"),
                        true
                ),

                // 11. includeUrlMeta = true (adds protocol/domain/port)
                Arguments.of(
                        "https://id.gs1.org/01/09520123456788/10/ABC123",
                        true,
                        Map.of(
                                "protocol", "https",
                                "domain", "id.gs1.org",
                                "port", "443",
                                "01", "09520123456788",
                                "10", "ABC123"
                        ),
                        true
                ),

                // 12. Completely malformed segments → nothing extracted
                Arguments.of(
                        "https://id.gs1.org/123456/AB@CD",
                        false,
                        Map.of(),
                        false
                )
        );
    }
}

