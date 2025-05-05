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

import io.openepcis.identifiers.validator.ValidationContext;
import io.openepcis.identifiers.validator.exception.ValidationException;

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
     * @param identifier       the GS1 identifier to check
     * @param isEpcisCompliant flag indicating if EPCIS compliant validation should be applied
     * @return true if this validator supports validating the identifier under the specified
     * conditions, false otherwise
     */
    boolean supportsValidation(final String identifier, final boolean isEpcisCompliant);


    /**
     * Validates the given GS1 identifier according to the rules encapsulated in {@link ValidationContext}.
     *
     * <p>The {@code opts} argument carries all of:
     * <ul>
     *   <li><strong>epcisCompliant</strong> – whether to enforce EPCIS-compliant subset of GS1 AIs</li>
     *   <li><strong>validateCheckDigit</strong> – whether to perform GS1 check-digit verification</li>
     *   <li><strong>gcpLength</strong> – optional Global Company Prefix length; if present, the
     *       identifier is treated as a Digital Link URI and the GCP length is enforced; if absent,
     *       the identifier is treated as a URN</li>
     * </ul>
     *
     * @param identifier        the GS1 identifier string to validate
     * @param validationContext a {@link ValidationContext} instance containing all validation flags
     *                          gcpLength - optional Global Company Prefix length;
     *                          if present, the identifier is treated as a Digital Link URI and the GCP length is enforced;
     *                          if absent, the identifier is treated as a URN
     *                          epcisCompliant  – whether to enforce EPCIS-compliant subset of GS1 AIs
     *                          validateCheckDigit -  whether to perform GS1 check-digit verification
     * @return {@code true} if the identifier satisfies all applicable validation rules;
     * {@code false} if it fails any rule (pattern, length, check-digit, etc.)
     */
    boolean validate(final String identifier, final ValidationContext validationContext);

    default boolean validate(final String identifier) {
        return validate(identifier, ValidationContext.defaultContext());
    }

    default boolean validate(final String identifier, final Integer gcpLength) {
        return validate(identifier, ValidationContext.builder().gcpLength(gcpLength).build());
    }
}
