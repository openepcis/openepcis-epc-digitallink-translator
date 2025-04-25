package io.openepcis.qrcode.generator.runtime;

import io.openepcis.qrcode.generator.QrCodeGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class QrCodeGeneratorProducer {

    @Produces
    @RequestScoped
    public QrCodeGenerator createQrCodeGenerator() {
        return new QrCodeGenerator();
    }
}
