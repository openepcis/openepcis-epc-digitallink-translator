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
package io.openepcis.qrcode.generator.exception;

import io.openepcis.qrcode.generator.exception.QrCodeGeneratorException;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class ExceptionMapper {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ExceptionMapper.class);
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
        responseBody.setStatus(RestResponse.StatusCode.BAD_REQUEST);
        responseBody.setDetail(exception.getMessage());
        return RestResponse.status(RestResponse.Status.BAD_REQUEST, responseBody);
    }

    // Inner class for ProblemResponseBody
    static class ProblemResponseBody {
        private String type;
        private String title;
        private int status;
        private String detail;

        public ProblemResponseBody() {
        }

        public String getType() {
            return this.type;
        }

        public String getTitle() {
            return this.title;
        }

        public int getStatus() {
            return this.status;
        }

        public String getDetail() {
            return this.detail;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof ExceptionMapper.ProblemResponseBody)) return false;
            ExceptionMapper.ProblemResponseBody other = (ExceptionMapper.ProblemResponseBody) o;
            if (!other.canEqual((Object) this)) return false;
            if (this.getStatus() != other.getStatus()) return false;
            Object this$type = this.getType();
            Object other$type = other.getType();
            if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
            Object this$title = this.getTitle();
            Object other$title = other.getTitle();
            if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
            Object this$detail = this.getDetail();
            Object other$detail = other.getDetail();
            if (this$detail == null ? other$detail != null : !this$detail.equals(other$detail)) return false;
            return true;
        }

        protected boolean canEqual(Object other) {
            return other instanceof ExceptionMapper.ProblemResponseBody;
        }

        @Override
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * PRIME + this.getStatus();
            Object $type = this.getType();
            result = result * PRIME + ($type == null ? 43 : $type.hashCode());
            Object $title = this.getTitle();
            result = result * PRIME + ($title == null ? 43 : $title.hashCode());
            Object $detail = this.getDetail();
            result = result * PRIME + ($detail == null ? 43 : $detail.hashCode());
            return result;
        }

        @Override
        public String toString() {
            return "ExceptionMapper.ProblemResponseBody(type=" + this.getType() + ", title=" + this.getTitle() + ", status=" + this.getStatus() + ", detail=" + this.getDetail() + ")";
        }
    }
}
