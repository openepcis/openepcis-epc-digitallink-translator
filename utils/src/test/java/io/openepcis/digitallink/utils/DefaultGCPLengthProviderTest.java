/*
 * Copyright (c) 2022-2025 benelog GmbH & Co. KG
 * All rights reserved.
 */
package io.openepcis.digitallink.utils;

import io.openepcis.core.exception.UnsupportedGS1IdentifierException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class DefaultGCPLengthProviderTest {

    private static final String SYS_PROP = "io.openepcis.digitallink.utils.DefaultGCPLengthProvider.defaultGcpLength";

    @AfterEach
    void clearSystemProperty() {
        System.clearProperty(SYS_PROP);
    }

    @ParameterizedTest
    @CsvSource({
            "https://id.gs1.org/01/04068194000004",
            "https://id.gs1.org/01/04012345000009",
            "https://id.gs1.org/01/00614141000036",
    })
    void testStep1_PrefixTableLookup(String uri) {
        final DefaultGCPLengthProvider provider = DefaultGCPLengthProvider.getInstance();
        final int gcpLength = provider.getGcpLength(uri);
        assertTrue(gcpLength >= 4 && gcpLength <= 12, "GCP length should be 4-12, got " + gcpLength + " for " + uri);
    }

    @Test
    void testStep3_JvmPropertyDefault() {
        System.setProperty(SYS_PROP, "9");
        final DefaultGCPLengthProvider provider = DefaultGCPLengthProvider.getInstance();

        // Step 1 fails → Step 2 fails (no CDI) → Step 3: JVM property = 9
        try {
            final int gcpLength = provider.getGcpLength("https://id.gs1.org/01/09889999999999");
            assertEquals(9, gcpLength, "Should fall to JVM default of 9");
        } catch (UnsupportedGS1IdentifierException e) {
            fail("Should not throw when JVM default is set");
        }
    }

    @Test
    void testNoMatchNoDefault_Throws() {
        System.clearProperty(SYS_PROP);
        final DefaultGCPLengthProvider provider = DefaultGCPLengthProvider.getInstance();
        assertThrows(UnsupportedGS1IdentifierException.class, () -> provider.getGcpLength("https://id.gs1.org/01/09889999999999"));
    }

    @Test
    void testBlankUri_Throws() {
        final DefaultGCPLengthProvider provider = DefaultGCPLengthProvider.getInstance();
        assertThrows(UnsupportedGS1IdentifierException.class, () -> provider.getGcpLength(""));
    }

    @Test
    void testNullUri_Throws() {
        final DefaultGCPLengthProvider provider = DefaultGCPLengthProvider.getInstance();
        assertThrows(UnsupportedGS1IdentifierException.class, () -> provider.getGcpLength(null));
    }

    @Test
    void testUrnUri_Throws() {
        final DefaultGCPLengthProvider provider = DefaultGCPLengthProvider.getInstance();
        assertThrows(UnsupportedGS1IdentifierException.class, () -> provider.getGcpLength("urn:epc:id:sgtin:4012345.099999.1234"));
    }
}
