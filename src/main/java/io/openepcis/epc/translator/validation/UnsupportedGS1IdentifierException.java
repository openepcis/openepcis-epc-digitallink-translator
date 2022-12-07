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

import io.openepcis.epc.translator.exception.ValidationException;
import java.io.Serial;

public class UnsupportedGS1IdentifierException extends ValidationException {

  @Serial private static final long serialVersionUID = 1L;

  public UnsupportedGS1IdentifierException() {}

  public UnsupportedGS1IdentifierException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public UnsupportedGS1IdentifierException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnsupportedGS1IdentifierException(String message) {
    super(message);
  }

  public UnsupportedGS1IdentifierException(Throwable cause) {
    super(cause);
  }
}
