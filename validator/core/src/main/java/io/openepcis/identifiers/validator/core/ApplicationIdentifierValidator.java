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
package io.openepcis.identifiers.validator.core;

import io.openepcis.identifiers.validator.ValidationContext;

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
        return validate(identifier, ValidationContext.builder().validateCheckDigit(false).gcpLength(gcpLength).build());
    }
}
