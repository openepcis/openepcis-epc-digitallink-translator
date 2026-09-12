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
