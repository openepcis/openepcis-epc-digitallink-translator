package io.openepcis.identifiers.validator.deployment;

import io.openepcis.identifiers.validator.runtime.ValidatorFactoryProducer;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class ValidatorFactoryProcessor {

    private static final String FEATURE = "openepcis-digital-link-validator";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem buildValidatorFactory() {
        return AdditionalBeanBuildItem.unremovableOf(ValidatorFactoryProducer.class);
    }
}
