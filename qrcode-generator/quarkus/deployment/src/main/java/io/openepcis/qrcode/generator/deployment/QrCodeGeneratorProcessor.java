package io.openepcis.qrcode.generator.deployment;

import io.openepcis.qrcode.generator.runtime.QrCodeGeneratorProducer;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class QrCodeGeneratorProcessor {

    private static final String FEATURE = "openepcis-qr-code-generator";

    // During build time scan Inform Quarkus about new feature/extension to be included in application
    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    // Ensure Quarkus does not remove/strip QrCodeGeneratorProducer bean during the build time.
    @BuildStep
    AdditionalBeanBuildItem buildOpenEPCISQrCodeContext() {
        return AdditionalBeanBuildItem.unremovableOf(QrCodeGeneratorProducer.class);
    }
}
