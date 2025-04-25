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
     * Validates the provided GS1 identifier.
     *
     * <p>This method determines whether the identifier should be validated as an instance-level (URN)
     * or class-level (Digital Link URI) identifier based on the presence of a GCP length parameter.
     * If a GCP length is provided, the identifier is validated as a URI; otherwise, it is validated
     * as a URN.
     *
     * @param identifier the GS1 identifier to validate
     * @param gcpLength  an optional parameter representing the Global Company Prefix length to use in
     *                   validation for DL URI
     * @return true if the identifier is valid according to the first supporting validator, false
     * otherwise
     * @throws UnsupportedGS1IdentifierException if no validator supports the provided identifier
     *                                           format
     */
    public boolean validateIdentifier(final String identifier, final Integer... gcpLength) {
        return validateIdentifier(identifier, true, gcpLength);
    }

    /**
     * Validates the provided GS1 identifier.
     *
     * <p>This method determines whether the identifier should be validated as an instance-level (URN)
     * or class-level (Digital Link URI) identifier based on the presence of a GCP length parameter.
     * If a GCP length is provided, the identifier is validated as a URI; otherwise, it is validated
     * as a URN.
     *
     * @param identifier       the GS1 identifier to validate
     * @param isEpcisCompliant true if the identifier should be validated as an EPCIS compliant identifier
     * @param gcpLength        an optional parameter representing the Global Company Prefix length to use in validation for DL URI
     * @return true if the identifier is valid according to the first supporting validator, false
     * otherwise
     * @throws UnsupportedGS1IdentifierException if no validator supports the provided identifier
     *                                           format
     */

    public boolean validateIdentifier(final String identifier, final boolean isEpcisCompliant, final Integer... gcpLength) {
        // No GCP length provided: validate URN identifiers
        if (gcpLength == null || gcpLength.length == 0) {
            for (final ApplicationIdentifierValidator validator : validators) {
                if (validator.supportsValidation(identifier, isEpcisCompliant)) {
                    return validator.validate(identifier, isEpcisCompliant);
                }
            }
        } else {
            // GCP length provided: validate as URI identifier with the specified GCP length.
            for (final ApplicationIdentifierValidator validator : validators) {
                if (validator.supportsValidation(identifier, isEpcisCompliant)) {
                    // Use the first element of the gcpLength array since only one length is expected.
                    return validator.validate(identifier, isEpcisCompliant, gcpLength[0]);
                }
            }
        }

        // If no validator supports the identifier, throw an exception.
        throw new UnsupportedGS1IdentifierException(
                String.format("Identifier did not match any GS1 identifiers format: %s", identifier));
    }
}
