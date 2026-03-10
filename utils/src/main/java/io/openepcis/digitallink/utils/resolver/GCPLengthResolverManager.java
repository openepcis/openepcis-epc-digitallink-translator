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


import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.ServiceLoader;

@Slf4j
public class GCPLengthResolverManager {

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

    // return true if at least one resolver is registered via SPI
    public boolean hasResolvers() {
        return !resolvers.isEmpty();
    }
}
