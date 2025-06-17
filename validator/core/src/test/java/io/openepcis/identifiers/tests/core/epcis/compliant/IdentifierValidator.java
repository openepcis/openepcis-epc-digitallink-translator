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
package io.openepcis.identifiers.tests.core.epcis.compliant;

import io.openepcis.core.exception.ValidationException;
import io.openepcis.digitallink.toolkit.GS1DigitalLinkNormalizer;
import io.openepcis.digitallink.utils.DefaultGCPLengthProvider;
import io.openepcis.identifiers.validator.ValidationContext;
import io.openepcis.identifiers.validator.ValidatorFactory;

public class IdentifierValidator {
  private static final ValidatorFactory validatorFactory = new ValidatorFactory(new GS1DigitalLinkNormalizer(), new DefaultGCPLengthProvider());

  // Same for the variant that requires a GCP length.
  public static void validate(final String identifier, final ValidationContext validationContext) throws ValidationException {
    validatorFactory.validateIdentifier(identifier, validationContext);
  }
}
