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
package io.openepcis.identifiers.converter.core;


import io.openepcis.core.exception.ValidationException;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public interface Converter {
  // Check if the URN is supported and belongs to which class in the project
  boolean supportsDigitalLinkURI(String urn);

  // Process the URN and return the URL URI
  String convertToDigitalLink(String urn) throws ValidationException;

  // Check if the URI is supported and belongs to which class in the project
  boolean supportsURN(String dlURI);

  // Process the URI and return the URN with other information
  Map<String, String> convertToURN(String dlURI, int gcpLength) throws ValidationException;

  Map<String, String> convertToURN(String dlURI) throws ValidationException;

  /**
   * Asynchronous variant of {@link #convertToURN(String)} — resolves GCP length
   * without blocking the calling thread.
   * <p>Default implementation delegates to the sync method on the common fork-join pool.
   * Converters that call {@code DefaultGCPLengthProvider.getGcpLength()} should override
   * this to use {@code getGcpLengthAsync()} instead.
   */
  default CompletionStage<Map<String, String>> convertToURNAsync(String dlURI) {
    return CompletableFuture.supplyAsync(() -> convertToURN(dlURI));
  }
}
