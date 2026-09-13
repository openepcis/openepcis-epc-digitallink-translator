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

import io.openepcis.core.exception.ValidationException;
import io.openepcis.identifiers.validator.ValidationContext;

import java.util.regex.Pattern;

public class Matcher {
    private final Pattern compiledPattern;
    private final String message;

    public Matcher(final String pattern, final String message) {
        this.compiledPattern = Pattern.compile(pattern);
        this.message = message;
    }

    /**
     * Validates the input string against the pattern. Returns true if valid, false otherwise.
     */
    public void validate(final String urn) throws ValidationException {
        if (!compiledPattern.matcher(urn).matches()) {
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
