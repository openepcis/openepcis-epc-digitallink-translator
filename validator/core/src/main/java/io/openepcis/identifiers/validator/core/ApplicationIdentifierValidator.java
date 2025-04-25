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

/**
 * Interface for validating GS1 Application Identifiers (AI).
 *
 * <p>Implementations of this interface should provide specific validation rules for different types
 * of GS1 identifiers.
 */
public interface ApplicationIdentifierValidator {
  /**
   * Determines if the given identifier is supported by this validator.
   *
   * <p>This method checks if the identifier conforms to the expected format or rules that this
   * validator can process.
   *
   * @param identifier the GS1 identifier to check
   * @return true if this validator supports validating the identifier, false otherwise
   */
  boolean supportsValidation(final String identifier);

  /**
   * Determines if the given identifier is supported by this validator, considering whether EPCIS
   * compliance is required.
   *
   * <p>If the identifier needs to be part of EPCIS events then the EPCIS compliance is verified as
   * not all GS1 AI are EPCIS compliant.
   *
   * @param identifier the GS1 identifier to check
   * @param isEpcisCompliant flag indicating if EPCIS compliant validation should be applied
   * @return true if this validator supports validating the identifier under the specified
   *     conditions, false otherwise
   */
  boolean supportsValidation(final String identifier, final boolean isEpcisCompliant);

  /**
   * Validates the given GS1 identifier according to its specific rules.
   *
   * <p>This method assumes that EPCIS compliance is not required (default behavior).
   *
   * @param identifier the GS1 identifier to validate
   * @return true if the identifier is valid, false otherwise
   */
  boolean validate(final String identifier);

  /**
   * Validates the given GS1 identifier using the provided GCP length.
   *
   * <p>This method assumes EPCIS compliance is false by default. Given GS1 AI is validated along
   * with the GCP (Global Company Prefix) length if provided.
   *
   * @param identifier the GS1 identifier to validate
   * @param gcpLength an optional parameter specifying the expected GCP length
   * @return true if the identifier is valid based on the GCP length, false otherwise
   */
  boolean validate(final String identifier, final Integer... gcpLength);

  /**
   * Validates the given GS1 identifier considering both EPCIS compliance and GCP length.
   *
   * <p>If the identifier needs to be part of EPCIS events then the EPCIS compliance is verified as
   * not all GS1 AI are EPCIS compliant.
   *
   * @param identifier the GS1 identifier to validate
   * @param isEpcisCompliant flag indicating if EPCIS compliant validation should be applied
   * @param gcpLength an optional parameter specifying the expected GCP length
   * @return true if the identifier is valid under the specified conditions, false otherwise
   */
  boolean validate(
      final String identifier, final boolean isEpcisCompliant, final Integer... gcpLength);
}
