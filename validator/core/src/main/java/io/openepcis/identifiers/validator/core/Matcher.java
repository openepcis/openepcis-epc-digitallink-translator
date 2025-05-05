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

public class Matcher {
    private final String pattern;
    private final String message;

    public Matcher(final String pattern, final String message) {
        this.pattern = pattern;
        this.message = message;
    }

    /**
     * Validates the input string against the pattern. Returns true if valid, false otherwise.
     */
    public void validate(final String urn) throws ValidationException {
        if (!urn.matches(pattern)) {
            throw new ValidationException(String.format(message, urn));
        }
    }

    public void validate(final String uri, final int gcpLength) throws ValidationException {
        this.validate(uri);
    }

    public void validate(final String uri, final ValidationContext validationContext) throws ValidationException {
        this.validate(uri, validationContext.getGcpLength());
    }
}
