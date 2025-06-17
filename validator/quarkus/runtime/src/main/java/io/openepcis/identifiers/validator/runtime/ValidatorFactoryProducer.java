package io.openepcis.identifiers.validator.runtime;

import io.openepcis.digitallink.toolkit.GS1DigitalLinkNormalizer;
import io.openepcis.digitallink.utils.DefaultGCPLengthProvider;
import io.openepcis.identifiers.validator.ValidatorFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class ValidatorFactoryProducer {

    @Inject
    GS1DigitalLinkNormalizer gs1DigitalLinkNormalizer;

    @Inject
    DefaultGCPLengthProvider gcpLengthProvider;

    @Produces
    public ValidatorFactory createValidatorFactory() {
        return new ValidatorFactory(gs1DigitalLinkNormalizer, gcpLengthProvider);
    }
}
