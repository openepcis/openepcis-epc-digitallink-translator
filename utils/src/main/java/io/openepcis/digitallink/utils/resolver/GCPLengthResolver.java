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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * SPI interface for resolving GCP (GS1 Company Prefix) lengths from external services
 * when the static prefix table has no match.
 */
public interface GCPLengthResolver {

    /**
     * Synchronous resolution — blocks the calling thread.
     * <p>Prefer {@link #resolveAsync(String)} in reactive / non-blocking contexts.
     *
     * @param identifier raw GS1 identifier (e.g. GTIN {@code "04068194000004"} or GLN {@code "4068194000004"})
     * @return the GCP length, or empty if this resolver cannot determine it
     */
    OptionalInt resolve(final String identifier);

    /**
     * Asynchronous resolution — returns a {@link CompletionStage} that completes
     * without blocking the calling thread.
     * <p>The default implementation delegates to {@link #resolve(String)} on the
     * {@link CompletableFuture} default async executor. Implementations backed by
     * a reactive HTTP client should override this to avoid unnecessary thread-hopping.
     *
     * @param identifier raw GS1 identifier
     * @return stage completing with the GCP length, or empty
     */
    default CompletionStage<OptionalInt> resolveAsync(final String identifier) {
        return CompletableFuture.supplyAsync(() -> resolve(identifier));
    }

    /**
     * Priority order — lower values are tried first. Default is 100.
     */
    default int priority() {
        return 100;
    }
}
