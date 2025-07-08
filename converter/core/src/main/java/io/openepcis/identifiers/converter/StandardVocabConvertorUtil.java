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
package io.openepcis.identifiers.converter;

import io.openepcis.core.exception.UnsupportedGS1IdentifierException;
import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.converter.constants.StandardVocabElements;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StandardVocabConvertorUtil {

  private static final String ERROR_MESSAGE =
      "Provided URN format does not match with any of the GS1 identifiers format.%nPlease check the URN: %s";

  public static String toURI(String urn) throws ValidationException {
    for (StandardVocabElements element : StandardVocabElements.values()) {
      if (element.supportsDL(urn)) {
        return element.convertToDigitalLink(urn);
      }
    }
    if (urn.startsWith("http://") || urn.startsWith("https://")) {
      throw new UnsupportedGS1IdentifierException(String.format(ERROR_MESSAGE, urn));
    }
    throw new ValidationException(String.format(ERROR_MESSAGE, urn));
  }

  // Check through enum and find DL URI belongs to which particular value
  public static String toURN(String dlURI) throws ValidationException {
    for (StandardVocabElements element : StandardVocabElements.values()) {
      if (element.supportsURN(dlURI)) {
        return element.convertToURN(dlURI);
      }
    }
    if (dlURI.startsWith("http://") || dlURI.startsWith("https://")) {
      throw new UnsupportedGS1IdentifierException(String.format(ERROR_MESSAGE, dlURI));
    }
    throw new ValidationException(
        String.format(
            "Provided URI format does not match with any of the GS1 identifiers format.%nPlease check the URI: %s",
            dlURI));
  }
}
