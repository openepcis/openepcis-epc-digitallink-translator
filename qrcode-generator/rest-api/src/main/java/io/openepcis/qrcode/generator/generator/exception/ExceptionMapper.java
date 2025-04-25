package io.openepcis.qrcode.generator.generator.exception;

import io.openepcis.qrcode.generator.exception.QrCodeGeneratorException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

@Slf4j
public class ExceptionMapper {
    public static final String EXCEPTION_OCCURRED_DURING_CREATION_OF_QR_CODE = "Exception occurred during creation of QR code based on provided config";

    @ServerExceptionMapper
    public final RestResponse<ProblemResponseBody> mapException(final QrCodeGeneratorException exception) {
        log.info(exception.getMessage());
        final ProblemResponseBody responseBody = new ProblemResponseBody();
        responseBody.setType(EXCEPTION_OCCURRED_DURING_CREATION_OF_QR_CODE);
        responseBody.setTitle("Exception Occurred During QR Code Generation");
        responseBody.setStatus(RestResponse.StatusCode.BAD_REQUEST);
        responseBody.setDetail(exception.getMessage());
        return RestResponse.status(RestResponse.Status.BAD_REQUEST, responseBody);
    }

    @ServerExceptionMapper
    public final RestResponse<ProblemResponseBody> mapException(final IllegalArgumentException exception) {
        log.info(exception.getMessage());
        final ProblemResponseBody responseBody = new ProblemResponseBody();
        responseBody.setType(EXCEPTION_OCCURRED_DURING_CREATION_OF_QR_CODE);
        responseBody.setTitle("Bad request to QR Code Generator");
        responseBody.setStatus(RestResponse.StatusCode.BAD_REQUEST);
        responseBody.setDetail(exception.getMessage());
        return RestResponse.status(RestResponse.Status.BAD_REQUEST, responseBody);
    }

    @ServerExceptionMapper
    public final RestResponse<ProblemResponseBody> mapException(final Exception exception) {
        log.info(exception.getMessage());
        final ProblemResponseBody responseBody = new ProblemResponseBody();
        responseBody.setType(EXCEPTION_OCCURRED_DURING_CREATION_OF_QR_CODE);
        responseBody.setTitle("Access denied for QR Code Generator");
        responseBody.setStatus(RestResponse.StatusCode.UNAUTHORIZED);
        responseBody.setDetail(exception.getMessage());
        return RestResponse.status(RestResponse.Status.UNAUTHORIZED, responseBody);
    }


    // Inner class for ProblemResponseBody
    @Data
    static class ProblemResponseBody {
        private String type;
        private String title;
        private int status;
        private String detail;
    }
}
