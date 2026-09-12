/*
 * Copyright 2022-2026 benelog GmbH & Co. KG
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package io.openepcis.digitallink.utils.resolver;

import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class GCPLengthResolverManager {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GCPLengthResolverManager.class);
    private static GCPLengthResolverManager gcpLengthResolverManager;
    private final List<GCPLengthResolver> resolvers;

    private GCPLengthResolverManager(final List<GCPLengthResolver> resolvers) {
        this.resolvers = resolvers == null ? List.of() : resolvers.stream().sorted(Comparator.comparingInt(GCPLengthResolver::priority)).toList();
    }

    public static synchronized GCPLengthResolverManager getInstance() {
        if (gcpLengthResolverManager == null) {
            gcpLengthResolverManager = newInstance();
        }
        return gcpLengthResolverManager;
    }

    public static synchronized GCPLengthResolverManager newInstance() {
        return new GCPLengthResolverManager(ServiceLoader.load(GCPLengthResolver.class).stream().map(ServiceLoader.Provider::get).toList());
    }

    /**
     * Try each registered resolver in priority order. Returns the first successful (non-empty) result.
     *
     * @param identifier the raw GS1 identifier
     * @return the GCP length, or empty if no resolver can determine it
     */
    public OptionalInt resolve(final String identifier) {
        for (final GCPLengthResolver resolver : resolvers) {
            try {
                final OptionalInt result = resolver.resolve(identifier);
                if (result.isPresent()) {
                    return result;
                }
            } catch (Exception e) {
                log.warn("GCPLengthResolver {} failed for identifier {}: {}", resolver.getClass().getSimpleName(), identifier, e.getMessage());
            }
        }
        return OptionalInt.empty();
    }

    /**
     * Asynchronous variant — chains resolvers sequentially via {@link CompletionStage},
     * returning the first non-empty result without blocking.
     *
     * @param identifier the raw GS1 identifier
     * @return stage completing with the GCP length, or empty if no resolver can determine it
     */
    public CompletionStage<OptionalInt> resolveAsync(final String identifier) {
        CompletionStage<OptionalInt> chain = CompletableFuture.completedFuture(OptionalInt.empty());
        for (final GCPLengthResolver resolver : resolvers) {
            chain = chain.thenCompose(result -> {
                if (result.isPresent()) {
                    return CompletableFuture.completedFuture(result);
                }
                try {
                    return resolver.resolveAsync(identifier).exceptionally(e -> {
                        log.warn("GCPLengthResolver {} failed async for identifier {}: {}", resolver.getClass().getSimpleName(), identifier, e.getMessage());
                        return OptionalInt.empty();
                    });
                } catch (Exception e) {
                    log.warn("GCPLengthResolver {} failed for identifier {}: {}", resolver.getClass().getSimpleName(), identifier, e.getMessage());
                    return CompletableFuture.completedFuture(OptionalInt.empty());
                }
            });
        }
        return chain;
    }

    // return true if at least one resolver is registered via SPI
    public boolean hasResolvers() {
        return !resolvers.isEmpty();
    }
}
