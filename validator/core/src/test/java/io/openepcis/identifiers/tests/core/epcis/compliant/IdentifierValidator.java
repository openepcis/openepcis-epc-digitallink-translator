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
package io.openepcis.identifiers.tests.core.epcis.compliant;

import io.openepcis.core.exception.ValidationException;
import io.openepcis.digitallink.toolkit.GS1DigitalLinkNormalizer;
import io.openepcis.digitallink.utils.DefaultGCPLengthProvider;
import io.openepcis.identifiers.validator.ValidationContext;
import io.openepcis.identifiers.validator.ValidatorFactory;

public class IdentifierValidator {
  private static final ValidatorFactory validatorFactory = new ValidatorFactory(new GS1DigitalLinkNormalizer(), DefaultGCPLengthProvider.getInstance());

  // Same for the variant that requires a GCP length.
  public static void validate(final String identifier, final ValidationContext validationContext) throws ValidationException {
    validatorFactory.validateIdentifier(identifier, validationContext);
  }
}
