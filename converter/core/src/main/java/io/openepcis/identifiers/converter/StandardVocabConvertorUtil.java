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
package io.openepcis.identifiers.converter;

import io.openepcis.core.exception.UnsupportedGS1IdentifierException;
import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.converter.constants.StandardVocabElements;

public class StandardVocabConvertorUtil {
  private static final String ERROR_MESSAGE = "Provided URN format does not match with any of the GS1 identifiers format.%nPlease check the URN: %s";

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
    throw new ValidationException(String.format("Provided URI format does not match with any of the GS1 identifiers format.%nPlease check the URI: %s", dlURI));
  }

  private StandardVocabConvertorUtil() {
  }
}
