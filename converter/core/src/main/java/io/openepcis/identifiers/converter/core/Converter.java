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
package io.openepcis.identifiers.converter.core;


import io.openepcis.core.exception.ValidationException;

import java.util.Map;

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
}
