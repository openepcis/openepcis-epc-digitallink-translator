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


import io.openepcis.core.exception.UnsupportedGS1IdentifierException;
import io.openepcis.core.exception.ValidationException;
import io.openepcis.digitallink.toolkit.GS1DigitalLinkNormalizer;
import io.openepcis.digitallink.utils.DefaultGCPLengthProvider;
import io.openepcis.identifiers.validator.core.ApplicationIdentifierValidator;
import io.openepcis.identifiers.validator.core.epcis.compliant.*;
import io.openepcis.identifiers.validator.core.epcis.noncompliant.GTINCPVValidator;
import io.openepcis.identifiers.validator.core.epcis.noncompliant.GTINLotSerialExpiryValidator;
import io.openepcis.identifiers.validator.core.epcis.noncompliant.GTINWeightAmountBestBeforeValidator;
import io.openepcis.identifiers.validator.core.epcis.noncompliant.GTINWeightValidator;

import java.net.MalformedURLException;
import java.net.URL;
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
    private final GS1DigitalLinkNormalizer gs1DigitalLinkNormalizer;
    private final DefaultGCPLengthProvider gcpLengthProvider;

    public ValidatorFactory(GS1DigitalLinkNormalizer gs1DigitalLinkNormalizer, DefaultGCPLengthProvider gcpLengthProvider) {
        this.validators = new LinkedHashSet<>();
        this.gs1DigitalLinkNormalizer = gs1DigitalLinkNormalizer;
        this.gcpLengthProvider = gcpLengthProvider;
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
    public boolean validateIdentifier(final String identifier,
                                      final ValidationContext validationContext) {
        // Iterate through all registered validators and check if they support the identifier.
        for (final ApplicationIdentifierValidator validator : validators) {
            if (validator.supportsValidation(identifier, validationContext.isEpcisCompliant())) {
                return validator.validate(identifier, validationContext);
            }
        }

        // If no validator supports the identifier, throw an exception.
        throw new UnsupportedGS1IdentifierException(String.format("Identifier did not match any GS1 identifiers format: %s", identifier));
    }


    /**
     * Validate GS1 identifier from Digital Link URI using default ValidationContext.
     */
    public URL validateIdentifier(final URL digitalLink) throws MalformedURLException {
        final URL normalizedUrl = gs1DigitalLinkNormalizer.normalize(digitalLink);
        final String normalizedIdentifier = normalizedUrl.toString();
        final ValidationContext contextWithGcp = ValidationContext.builder()
                .gcpLength(gcpLengthProvider.getGcpLength(normalizedIdentifier))
                .build();

        if (validateIdentifier(normalizedIdentifier, contextWithGcp)) {
            return normalizedUrl;
        }

        throw new UnsupportedGS1IdentifierException("Identifier did not match any GS1 identifier format: " + digitalLink);
    }

    /**
     * Validate GS1 identifier from Digital Link URI using custom ValidationContext.
     */
    public URL validateIdentifier(final URL digitalLink,
                                  final ValidationContext context) {
        try {
            final URL normalizedUrl = gs1DigitalLinkNormalizer.normalize(digitalLink);
            final String normalizedIdentifier = normalizedUrl.toString();

            if (validateIdentifier(normalizedIdentifier, context)) {
                return normalizedUrl;
            }

            throw new UnsupportedGS1IdentifierException("Identifier did not match any GS1 identifier format: " + digitalLink);
        } catch (Exception e) {
            throw new ValidationException("Invalid GS1 Digital Link URI: " + digitalLink, e);
        }

    }

    /**
     * Validate a GS1 identifier string using the provided options.
     *
     * @param identifier the raw GS1 identifier to validate (URN or Digital Link URI)
     * @param gcpLength  GCP length
     * @return true if the identifier passes all checks in the first matching validator;
     * false if that validator’s validate method returns false
     * @throws UnsupportedGS1IdentifierException if no registered validator supports this identifier under the
     *                                           given epcisCompliant setting
     */
    @Deprecated(forRemoval = true, since = "1.0.0")
    public boolean validateIdentifier(final String identifier, final int gcpLength) {
        return validateIdentifier(identifier, ValidationContext.builder().gcpLength(gcpLength).build());
    }
}
