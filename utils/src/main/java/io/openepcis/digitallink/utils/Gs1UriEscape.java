/*
 * Copyright (c) 2022-2026 benelog GmbH & Co. KG
 * All rights reserved.
 *
 * Unauthorized copying, modification, distribution,
 * or use of this work, via any medium, is strictly prohibited.
 *
 * benelog GmbH & Co. KG reserves all rights not expressly granted herein,
 * including the right to sell licenses for using this work.
 */
package io.openepcis.digitallink.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Gs1UriEscape {

    // EPC TDS "URI Form" triplets that each stand for ONE logical character. It contains both:
    //   CSET-82 (X): " % & / < > ?   ->  %22 %25 %26 %2F %3C %3E %3F
    //   CPI    (Y): # /             ->  %23 %2F
    private static final Map<String, String> ESCAPES = Map.of(
            "%22", "\"",
            "%23", "#",
            "%25", "%",
            "%26", "&",
            "%2F", "/",
            "%3C", "<",
            "%3E", ">",
            "%3F", "?");

    // Collapse escape triplets to logical characters
    public static String decode(final String value) {
        // if the value is blank or does not contain any escape triplets, return it as-is
        if (StringUtils.isBlank(value) || !value.contains("%")) {
            return value;
        }

        String out = value;
        for (Map.Entry<String, String> e : ESCAPES.entrySet()) {
            // replace both the upper-case and lower-case versions of the escape triplet
            out = out.replace(e.getKey(), e.getValue())
                    .replace(e.getKey().toLowerCase(), e.getValue());
        }
        return out;
    }

}
