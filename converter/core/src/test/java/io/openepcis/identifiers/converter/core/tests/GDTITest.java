/*
 * Copyright (c) 2022-2025 benelog GmbH & Co. KG
 * All rights reserved.
 *
 * Unauthorized copying, modification, distribution,
 * or use of this work, via any medium, is strictly prohibited.
 *
 * benelog GmbH & Co. KG reserves all rights not expressly granted herein,
 * including the right to sell licenses for using this work.
 */
package io.openepcis.identifiers.converter.core.tests;

import static org.junit.Assert.assertEquals;

import io.openepcis.identifiers.converter.Converter;
import io.openepcis.identifiers.validator.exception.ValidationException;
import org.junit.Before;
import org.junit.Test;

public class GDTITest {

    private Converter converter;

    @Before
    public void before() throws Exception {
        converter = new Converter();
    }

    @Test
    public void testGDTI() throws ValidationException {

        // GDTI without serial number
        String gdti = "urn:epc:id:gdti:1234567890.12";
        TestIdentifiers.toDigitalLink(gdti);

        // GDTI with invalid characters
        gdti = "urn:epc:id:gdti:123456789A.12.13";
        TestIdentifiers.toDigitalLink(gdti);

        // GDTI with GCP more than 12 digits
        gdti = "urn:epc:id:gdti:1234567890123.12.13";
        TestIdentifiers.toDigitalLink(gdti);

        // GDTI with more than 13 digits
        gdti = "urn:epc:id:gdti:1234567890.12345.13";
        TestIdentifiers.toDigitalLink(gdti);

        // Valid GDTI
        assertEquals("https://id.gs1.org/253/1234567890128ABC", converter.toURI("urn:epc:id:gdti:1234567890.12.ABC"));
        assertEquals("https://id.gs1.org/253/1247575784846!", converter.toURI("urn:epc:id:gdti:124757.578484.!"));
        assertEquals("https://id.gs1.org/253/8934893489494\":?>", converter.toURI("urn:epc:id:gdti:893489348949..\":?>"));
        assertEquals("https://id.gs1.org/253/43493494898454", converter.toURI("urn:epc:id:gdti:434934948984..4"));

        // GDTI URI with invalid characters
        gdti = "https://id.gs1.org/253/123456789A128ABC";
        TestIdentifiers.toURN(gdti, 10);

        // GDTI URI without serial
        gdti = "https://id.gs1.org/253/1234567890123";
        TestIdentifiers.toURN(gdti, 10);

        // GDTI with invalid GCP Length
        gdti = "https://id.gs1.org/253/1234567890123A";
        TestIdentifiers.toURN(gdti, 13);

        // Valid GDTI
        assertEquals(
                "urn:epc:id:gdti:1234567890.12.A",
                converter.toURN("https://id.gs1.org/253/1234567890123A", 10).get("asURN"));
        assertEquals(
                "urn:epc:id:gdti:1234567890.12.A",
                converter.toURN("https://benelog1.de/253/1234567890123A", 10).get("asURN"));
        assertEquals(
                "urn:epc:id:gdti:893489348949..\":?>",
                converter.toURN("https://id.gs1.org/253/8934893489494\":?>", 12).get("asURN"));
        assertEquals(
                "urn:epc:id:gdti:434934948984..4",
                converter.toURN("https://id.gs1.org/253/43493494898454", 12).get("asURN"));
        assertEquals(
                "urn:epc:id:gdti:124757.578484.!",
                converter.toURN("https://id.gs1.org/253/1247575784846!", 6).get("asURN"));
        assertEquals(
                "urn:epc:id:gdti:93589983.9444.8!\"%&\"+",
                converter.toURN("https://id.gs1.org/253/93589983944448!\"%&\"+").get("asURN"));

        /** GDTI Class level identifiers conversion */

        // Web URI to URN

        // Class level GDTI Web URI with more than 13 digits
        gdti = "https://id.gs1.org/253/123456789012345";
        TestIdentifiers.toURNForClassLevelIdentifier(gdti);

        // Class level GDTI Web URI with less than 13 digits
        gdti = "https://id.gs1.org/253/123456789012";
        TestIdentifiers.toURNForClassLevelIdentifier(gdti);
        TestIdentifiers.toURNForClassLevelIdentifier(gdti, 10);

        // Class level GDTI Web URI with invalid characters
        gdti = "https://id.gs1.org/253/123456789012A";
        TestIdentifiers.toURNForClassLevelIdentifier(gdti);
        TestIdentifiers.toURNForClassLevelIdentifier(gdti, 12);

        // Class level GDTI with invalid GCP length
        gdti = "https://id.gs1.org/253/1234567890123";
        TestIdentifiers.toURNForClassLevelIdentifier(gdti, 5);
        TestIdentifiers.toURNForClassLevelIdentifier(gdti, 13);

        // Class level GDTI with invalid prefix
        gdti = "https://id.gs1.org/2534/1234567890123";
        TestIdentifiers.toURNForClassLevelIdentifier(gdti);
        TestIdentifiers.toURNForClassLevelIdentifier(gdti, 11);

        // Valid class level GDTI Web URI
        assertEquals(
                "urn:epc:idpat:gdti:1234567890.12.*",
                converter
                        .toURNForClassLevelIdentifier("https://id.gs1.org/253/1234567890123", 10)
                        .get("asURN"));
        assertEquals(
                "urn:epc:idpat:gdti:656980789012..*",
                converter
                        .toURNForClassLevelIdentifier("https://id.gs1.org/253/6569807890123")
                        .get("asURN"));
        assertEquals(
                "urn:epc:idpat:gdti:843848923823..*",
                converter
                        .toURNForClassLevelIdentifier("https://id.gs1.org/253/8438489238239", 12)
                        .get("asURN"));
        assertEquals(
                "urn:epc:idpat:gdti:8438001238.23.*",
                converter
                        .toURNForClassLevelIdentifier("https://google.fb.org/253/8438001238239")
                        .get("asURN"));

        // Class level URN to Web URI conversion

        // Class level GDTI URN with more than 13 digits
        gdti = "urn:epc:idpat:gdti:8438001238.233.*";
        TestIdentifiers.toURIForClassLevelIdentifier(gdti);

        // Class level GDTI URN with less than 13 digits
        gdti = "urn:epc:idpat:gdti:843800123.23.*";
        TestIdentifiers.toURIForClassLevelIdentifier(gdti);

        // Class level GDTI URN with invalid characters
        gdti = "urn:epc:idpat:gdti:8438A89238.23.*";
        TestIdentifiers.toURIForClassLevelIdentifier(gdti);

        // Class level GDTI URN with invalid GCP less than 6 digits
        gdti = "urn:epc:idpat:gdti:43948.483943.*";
        TestIdentifiers.toURIForClassLevelIdentifier(gdti);

        // Class level GDTI URN with invalid GCP more than 12 digits
        gdti = "urn:epc:idpat:gdti:4394834839433..*";
        TestIdentifiers.toURIForClassLevelIdentifier(gdti);

        // Class level GDTI URN with serial numbers
        gdti = "urn:epc:idpat:gdti:1234567890.12.999";
        TestIdentifiers.toURIForClassLevelIdentifier(gdti);

        // Class level GDTI URN without *
        gdti = "urn:epc:idpat:gdti:439483483943..";
        TestIdentifiers.toURIForClassLevelIdentifier(gdti);

        // Valid Class level GDTI URN
        assertEquals(
                "https://id.gs1.org/253/4394834839438",
                converter.toURIForClassLevelIdentifier("urn:epc:idpat:gdti:439483483943..*"));
        assertEquals(
                "https://id.gs1.org/253/4748343847383",
                converter.toURIForClassLevelIdentifier("urn:epc:idpat:gdti:474834.384738.*"));
        assertEquals(
                "https://id.gs1.org/253/6374637643768",
                converter.toURIForClassLevelIdentifier("urn:epc:idpat:gdti:637463764.376.*"));
        assertEquals(
                "https://id.gs1.org/253/5757834883747",
                converter.toURIForClassLevelIdentifier("urn:epc:idpat:gdti:57578348837.4.*"));
    }
}
