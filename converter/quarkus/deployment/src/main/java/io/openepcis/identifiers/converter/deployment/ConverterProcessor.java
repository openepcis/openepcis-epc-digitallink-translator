package io.openepcis.identifiers.converter.deployment;

import io.openepcis.identifiers.converter.runtime.ConverterProducer;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class ConverterProcessor {
    private static final String FEATURE = "openepcis-digital-link-converter";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem buildValidatorFactory() {
        return AdditionalBeanBuildItem.unremovableOf(ConverterProducer.class);
    }
}
