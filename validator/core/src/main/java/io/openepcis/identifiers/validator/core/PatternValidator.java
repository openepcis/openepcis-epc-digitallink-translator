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
package io.openepcis.identifiers.validator.core;

import io.openepcis.core.exception.ValidationException;

public interface PatternValidator {

  // Validate the URN against each of the Pattern populated in urnPatterns()
  void validateURN(final String urn) throws ValidationException;

  // Validate the Web URI against each of the Pattern populated in dluriPatterns()
  void validateURI(final String uri, final int GCPLength) throws ValidationException;
}
