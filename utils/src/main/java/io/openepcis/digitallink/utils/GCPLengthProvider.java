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
