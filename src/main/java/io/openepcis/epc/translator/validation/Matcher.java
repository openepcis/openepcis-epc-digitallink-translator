/*
 * Copyright 2022 benelog GmbH & Co. KG
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
package io.openepcis.epc.translator.validation;

import io.openepcis.epc.translator.ValidationException;

public class Matcher {
  private final String pattern;
  private final String message;

  Matcher(final String pattern, final String message) {
    this.pattern = pattern;
    this.message = message;
  }

  public void validate(final String urn) throws ValidationException {
    if (!urn.matches(pattern)) {
      throw new ValidationException(String.format(message, urn));
    }
  }

  protected void validate(final String uri, final int gcpLength) throws ValidationException {
    this.validate(uri);
  }
}
