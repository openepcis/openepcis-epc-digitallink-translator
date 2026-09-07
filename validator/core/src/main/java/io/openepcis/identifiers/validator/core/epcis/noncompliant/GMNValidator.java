/*
 * Copyright (c) 2022-2026 benelog GmbH & Co. KG
 * All rights reserved.
 *
 * Unauthorized copying, modification, distribution,
 * or use of this work, via any medium, is strictly prohibited.
 *
 * benelog GmbH & Co. KG reserves all rights not expressly granted herein,
 * including the right to sell licenses for using this work.
 */
package io.openepcis.identifiers.validator.core.epcis.noncompliant;

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.validator.ValidationContext;
import io.openepcis.identifiers.validator.core.ApplicationIdentifierValidator;
import io.openepcis.identifiers.validator.core.Matcher;
import io.openepcis.identifiers.validator.core.util.CheckCharacterPairValidator;

import java.util.ArrayList;
import java.util.List;

import static io.openepcis.constants.ApplicationIdentifierConstants.GMN_AI_URI_PREFIX;

/**
 * Validates a GMN (Global Model Number, AI 8013) as a Digital Link primary key. Example:
 * <a href="https://id.gs1.org/8013/1987654Ad4X4bL5ttr2310c2K">https://id.gs1.org/8013/1987654Ad4X4bL5ttr2310c2K</a>
 *
 * <p>GMN is the odd one out among the primary keys: up to 25 CSET 82 characters, and the
 * last two are a check character <em>pair</em> (MOD 1021-37) rather than the MOD-10 check
 * digit every other key uses. That arithmetic lives in
 * {@link CheckCharacterPairValidator}, verified against GS1's reference vectors.
 *
 * <p>Like the party-role GLNs this is Digital-Link-only — there is no
 * {@code urn:epc:id:gmn:} — so it declines EPCIS-compliant callers.
 *
 * <p><b>Known limit:</b> the value is validated as it appears in the path. CSET 82 includes
 * {@code /} and {@code ?}, which cannot appear unencoded in a path segment and are
 * therefore not accepted here; a GMN containing them would have to be percent-decoded
 * before validation. Real GMNs are alphanumeric, so this has no practical effect today, but
 * it is a limit rather than a rule.
 */
public class GMNValidator implements ApplicationIdentifierValidator {

    /**
     * CSET 82 minus {@code /} and {@code ?} (structural in a URI path), 2 to 25 characters:
     * one data character plus the pair at minimum, 25 per the AI 8013 field length.
     */
    private static final String GMN_PATH_CHARS = "[!\"%&'()*+,\\-.0-9:;<=>A-Z_a-z]{2,25}";

    private static final List<Matcher> DIGITAL_LINK_VALIDATION_RULES = new ArrayList<>();

    static {
        // Validate for the domain name (https://id.gs1.org/)
        DIGITAL_LINK_VALIDATION_RULES.add(
                new Matcher(
                        "(http|https)://.*",
                        "Invalid GMN, GMN should start with Domain name (Ex: https://id.gs1.org/), Please check the DL URI: %s"));

        // Validate the GMN field: up to 25 CSET 82 characters, optionally followed by a query string
        DIGITAL_LINK_VALIDATION_RULES.add(
                new Matcher(
                        "(http|https):?://.*/8013/" + GMN_PATH_CHARS + "(\\?.*)?",
                        "Invalid GMN, GMN should consist of up to 25 CSET 82 characters ending in a check character pair (Ex: https://id.gs1.org/8013/1987654Ad4X4bL5ttr2310c2K), Please check the DL URI: %s") {
                    @Override
                    public void validate(final String uri, final int gcpLength) throws ValidationException {
                        super.validate(uri);

                        // Check the provided GCP Length is between 6 and 12 digits
                        if (!(gcpLength >= 6 && gcpLength <= 12)) {
                            throw new ValidationException(
                                    String.format("Invalid GCP Length, GCP Length should be between 6-12 digits. Please check the provided GCP Length: %s", gcpLength));
                        }
                    }

                    // Validate the check character pair if the flag is set
                    @Override
                    public void validate(final String uri, final ValidationContext validationContext) throws ValidationException {
                        validate(uri, validationContext.getGcpLength());

                        if (!validationContext.isValidateCheckDigit()) {
                            return;
                        }

                        CheckCharacterPairValidator.validate(gmnFieldOf(uri), "GMN");
                    }
                });
    }

    /** The GMN field itself: everything after {@code /8013/} up to the query string. */
    private static String gmnFieldOf(final String uri) {
        final int start = uri.indexOf(GMN_AI_URI_PREFIX) + GMN_AI_URI_PREFIX.length();
        final int query = uri.indexOf('?', start);
        return query < 0 ? uri.substring(start) : uri.substring(start, query);
    }

    @Override
    public boolean supportsValidation(final String identifier) {
        // Digital-Link-only: there is no GMN URN form.
        return identifier.contains(GMN_AI_URI_PREFIX);
    }

    @Override
    public boolean supportsValidation(final String identifier, final boolean isEpcisCompliant) {
        // GMN has no EPCIS URN representation, so an EPCIS-compliant caller must not be
        // handed one.
        return !isEpcisCompliant && supportsValidation(identifier);
    }

    @Override
    public boolean validate(final String identifier, final ValidationContext validationContext) throws ValidationException {
        // Digital Link URIs require a GCP length, and there is no URN branch to fall back on.
        if (validationContext.getGcpLength() == null) {
            throw new ValidationException("Digital Link URI detected. Use validate(String, int) to validate Digital Link URIs with a GCP length.");
        }

        for (final Matcher m : DIGITAL_LINK_VALIDATION_RULES) {
            m.validate(identifier, validationContext);
        }

        return true;
    }
}
