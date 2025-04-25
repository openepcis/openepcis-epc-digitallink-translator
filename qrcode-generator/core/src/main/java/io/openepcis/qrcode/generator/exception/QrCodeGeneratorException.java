package io.openepcis.qrcode.generator.exception;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serial;

@RegisterForReflection
public class QrCodeGeneratorException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public QrCodeGeneratorException() {
        super();
    }

    public QrCodeGeneratorException(
            final String message,
            final Throwable cause,
            final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public QrCodeGeneratorException(final String message,
                                    final Throwable throwable) {
        super(message, throwable);
    }

    public QrCodeGeneratorException(final String message) {
        super(message);
    }

    public QrCodeGeneratorException(final Throwable throwable) {
        super(throwable);
    }
}
