package io.openepcis.identifiers.validator.runtime;

import io.openepcis.identifiers.validator.ValidatorFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class ValidatorFactoryProducer {
    @Produces
    public ValidatorFactory createValidatorFactory() {
        return new ValidatorFactory();
    }
}
