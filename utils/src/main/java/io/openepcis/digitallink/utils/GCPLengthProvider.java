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
package io.openepcis.digitallink.utils;

import java.util.concurrent.CompletionStage;

public interface GCPLengthProvider {

    /**
     * Synchronous GCP length resolution — may block.
     */
    int getGcpLength(String gs1DigitalLinkURI);

    /**
     * Asynchronous GCP length resolution — never blocks the calling thread.
     */
    CompletionStage<Integer> getGcpLengthAsync(String gs1DigitalLinkURI);
}
