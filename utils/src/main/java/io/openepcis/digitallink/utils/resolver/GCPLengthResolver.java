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
package io.openepcis.digitallink.utils.resolver;

import java.util.OptionalInt;

/**
 * SPI interface for resolving GCP (GS1 Company Prefix) lengths from external services when the static prefix table has no match.
 */
public interface GCPLengthResolver {

    /**
     * Attempt to resolve the GCP length for the given raw GS1 identifier
     * (e.g. a GTIN like "04068194000004" or a GLN like "4068194000004").
     */
    OptionalInt resolve(final String identifier);

    /**
     * Priority order — lower values are tried first. Default is 100.
     */
    default int priority() {
        return 100;
    }
}
