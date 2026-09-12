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
package io.openepcis.identifiers.validator.core.epcis.noncompliant;

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.validator.ValidationContext;
import io.openepcis.identifiers.validator.core.ApplicationIdentifierValidator;
import io.openepcis.identifiers.validator.core.Matcher;
import io.openepcis.identifiers.validator.core.util.CheckDigitValidator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.openepcis.constants.ApplicationIdentifierConstants.BILL_TO_AI_URI_PREFIX;
import static io.openepcis.constants.ApplicationIdentifierConstants.PAY_TO_AI_URI_PREFIX;
import static io.openepcis.constants.ApplicationIdentifierConstants.PURCHASED_FROM_AI_URI_PREFIX;
import static io.openepcis.constants.ApplicationIdentifierConstants.SHIP_FOR_AI_URI_PREFIX;
import static io.openepcis.constants.ApplicationIdentifierConstants.SHIP_TO_AI_URI_PREFIX;

/**
 * Validates the five party-role GLNs as Digital Link primary keys: ship-to (410), bill-to
 * (411), purchased-from (412), ship-for (413) and pay-to (415). Example:
 * <a href="https://id.gs1.org/410/9521890340331">https://id.gs1.org/410/9521890340331</a>
 *
 * <p>All five are a 13-digit GLN — structurally identical to SGLN (414) and PGLN (417) — so
 * they share one validator rather than five near-copies; only the AI and the role name in
 * the error message differ.
 *
 * <p>Unlike SGLN and PGLN these have no EPCIS URN form (there is no {@code urn:epc:id:} for
 * a ship-to GLN), hence this class lives with the other Digital-Link-only identifiers and
 * declines any URN outright. GS1's resolver-description schema does permit all five in
 * {@code supportedPrimaryKeys}, which is why the resolver needs to resolve them.
 */
public class PartyRoleGlnValidator implements ApplicationIdentifierValidator {

    /** AI prefix -> role name used in validation messages, in AI order. */
    private static final Map<String, String> ROLE_NAMES = new LinkedHashMap<>();

    /** AI prefix -> the rules an identifier carrying that AI must satisfy. */
    private static final Map<String, List<Matcher>> RULES = new LinkedHashMap<>();

    static {
        ROLE_NAMES.put(SHIP_TO_AI_URI_PREFIX, "Ship-to GLN");
        ROLE_NAMES.put(BILL_TO_AI_URI_PREFIX, "Bill-to GLN");
        ROLE_NAMES.put(PURCHASED_FROM_AI_URI_PREFIX, "Purchased-from GLN");
        ROLE_NAMES.put(SHIP_FOR_AI_URI_PREFIX, "Ship-for GLN");
        ROLE_NAMES.put(PAY_TO_AI_URI_PREFIX, "Pay-to GLN");

        ROLE_NAMES.forEach((aiPrefix, roleName) -> {
            final String ai = aiPrefix.replace("/", "");
            final List<Matcher> matchers = new ArrayList<>();

            // Validate for the domain name (https://id.gs1.org/)
            matchers.add(
                    new Matcher(
                            "(http|https)://.*",
                            "Invalid " + roleName + ", it should start with Domain name (Ex: https://id.gs1.org/), Please check the DL URI: %s"));

            // Validate for the 13 digit GLN, optionally followed by a query string
            matchers.add(
                    new Matcher(
                            "(http|https):?://.*/" + ai + "/[0-9]{13}(\\?.*)?",
                            "Invalid " + roleName + ", it should consist of 13 digit GLN (Ex: https://id.gs1.org/" + ai + "/9521890340331), Please check the DL URI: %s") {
                        @Override
                        public void validate(final String uri, final int gcpLength) throws ValidationException {
                            super.validate(uri);

                            // Check the provided GCP Length is between 6 and 12 digits
                            if (!(gcpLength >= 6 && gcpLength <= 12)) {
                                throw new ValidationException(
                                        String.format("Invalid GCP Length, GCP Length should be between 6-12 digits. Please check the provided GCP Length: %s", gcpLength));
                            }
                        }

                        // Validate for Check Digit if the flag is set
                        @Override
                        public void validate(final String uri, final ValidationContext validationContext) throws ValidationException {
                            validate(uri, validationContext.getGcpLength());

                            if (!validationContext.isValidateCheckDigit()) {
                                return;
                            }

                            CheckDigitValidator.validatePartyRoleGln(uri, aiPrefix, roleName);
                        }
                    });

            RULES.put(aiPrefix, matchers);
        });
    }

    /** The AI prefix this identifier carries, or null when it carries none of the five. */
    private static String aiPrefixOf(final String identifier) {
        for (final String aiPrefix : RULES.keySet()) {
            if (identifier.contains(aiPrefix)) {
                return aiPrefix;
            }
        }
        return null;
    }

    @Override
    public boolean supportsValidation(final String identifier) {
        // Digital-Link-only: a URN can never carry one of these AIs.
        return aiPrefixOf(identifier) != null;
    }

    @Override
    public boolean supportsValidation(final String identifier, final boolean isEpcisCompliant) {
        // These identifiers have no EPCIS URN representation, so an EPCIS-compliant
        // caller must not be handed one.
        return !isEpcisCompliant && supportsValidation(identifier);
    }

    @Override
    public boolean validate(final String identifier, final ValidationContext validationContext) throws ValidationException {
        final String aiPrefix = aiPrefixOf(identifier);
        if (aiPrefix == null) {
            throw new ValidationException(String.format("Invalid identifier, no party-role GLN AI (410, 411, 412, 413, 415) found in: %s", identifier));
        }

        // Digital Link URIs require a GCP length, and there is no URN branch to fall back on.
        if (validationContext.getGcpLength() == null) {
            throw new ValidationException("Digital Link URI detected. Use validate(String, int) to validate Digital Link URIs with a GCP length.");
        }

        for (final Matcher m : RULES.get(aiPrefix)) {
            m.validate(identifier, validationContext);
        }

        return true;
    }
}
