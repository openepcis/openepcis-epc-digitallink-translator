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
package io.openepcis.identifiers.validator;


import io.openepcis.identifiers.validator.core.ApplicationIdentifierValidator;
import io.openepcis.identifiers.validator.core.epcis.compliant.*;
import io.openepcis.identifiers.validator.core.epcis.noncompliant.*;
import io.openepcis.identifiers.validator.exception.UnsupportedGS1IdentifierException;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Factory class for managing GS1 Application Identifier validators.
 *
 * <p>This class initializes and holds a collection of validator implementations. It provides a
 * method to validate a given identifier by delegating the validation process to the appropriate
 * validator class.
 */
public class ValidatorFactory {
    // Collection of all available validators.
    private final Set<ApplicationIdentifierValidator> validators;

    public ValidatorFactory() {
        this.validators = new LinkedHashSet<>();
        initializeValidators();
    }

    // Initializes all known GS1 identifier validators.
    private void initializeValidators() {
        // Validators not compliant with EPCIS but compliant with GS1 Digital Link
        this.validators.add(new GTINWeightAmountBestBeforeValidator());
        this.validators.add(new GTINLotSerialExpiryValidator());
        this.validators.add(new GTINCPVValidator());
        this.validators.add(new GTINWeightValidator());

        // Initialize all well known EPC validator implementations which are compliant with EPCIS
        this.validators.add(new CPIValidator());
        this.validators.add(new GCNValidator());
        this.validators.add(new GDTIValidator());
        this.validators.add(new GIAIValidator());
        this.validators.add(new GINCValidator());
        this.validators.add(new GRAIValidator());
        this.validators.add(new GSINValidator());
        this.validators.add(new GSRNPValidator());
        this.validators.add(new GSRNValidator());
        this.validators.add(new ITIPValidator());
        this.validators.add(new PGLNValidator());
        this.validators.add(new SGLNValidator());
        this.validators.add(new SSCCValidator());
        this.validators.add(new LGTINValidator());
        this.validators.add(new UPUIValidator());
        this.validators.add(new SGTINValidator());
    }


    /**
     * Validate a GS1 identifier string using the provided options.
     *
     * @param identifier        the raw GS1 identifier to validate (URN or Digital Link URI)
     * @param validationContext the validation flags:
     *                          epcisCompliant    – enforce EPCIS-compliant if true
     *                          validateCheckDigit– perform check-digit verification if true
     *                          gcpLength         – non-null for URI mode, null for URN mode
     * @return true if the identifier passes all checks in the first matching validator;
     * false if that validator’s validate method returns false
     * @throws UnsupportedGS1IdentifierException if no registered validator supports this identifier under the
     *                                           given epcisCompliant setting
     */
    public boolean validateIdentifier(final String identifier, final ValidationContext validationContext) {
        // Iterate through all registered validators and check if they support the identifier.
        for (final ApplicationIdentifierValidator validator : validators) {
            if (validator.supportsValidation(identifier, validationContext.isEpcisCompliant())) {
                return validator.validate(identifier, validationContext);
            }
        }

        // If no validator supports the identifier, throw an exception.
        throw new UnsupportedGS1IdentifierException(String.format("Identifier did not match any GS1 identifiers format: %s", identifier));
    }
}
