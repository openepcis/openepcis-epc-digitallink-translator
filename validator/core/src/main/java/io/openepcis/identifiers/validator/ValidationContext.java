package io.openepcis.identifiers.validator;

public final class ValidationContext {
    /**
     * If true, enforce EPCIS‐compliance rules and validates GS1 AI against only EPCIS supported AI. Defaults to true.
     */
    private final boolean epcisCompliant;
    /**
     * If true, run the GS1 check-digit validation along with normal validation. Defaults to true.
     */
    private final boolean validateCheckDigit;
    /**
     * When present, validates for Digital-Link URI using this GCP length. Empty validates for URN mode.
     */
    private final Integer gcpLength;

    public static ValidationContext defaultContext() {
        return ValidationContext.builder().build();
    }

    private static boolean $default$epcisCompliant() {
        return true;
    }

    private static boolean $default$validateCheckDigit() {
        return true;
    }

    private static Integer $default$gcpLength() {
        return null;
    }

    /**
     * Creates a new {@code ValidationContext} instance.
     *
     * @param epcisCompliant If true, enforce EPCIS‐compliance rules and validates GS1 AI against only EPCIS supported AI. Defaults to true.
     * @param validateCheckDigit If true, run the GS1 check-digit validation along with normal validation. Defaults to true.
     * @param gcpLength When present, validates for Digital-Link URI using this GCP length. Empty validates for URN mode.
     */
    ValidationContext(boolean epcisCompliant, boolean validateCheckDigit, Integer gcpLength) {
        this.epcisCompliant = epcisCompliant;
        this.validateCheckDigit = validateCheckDigit;
        this.gcpLength = gcpLength;
    }


    public static class ValidationContextBuilder {
        private boolean epcisCompliant$set;
        private boolean epcisCompliant$value;
        private boolean validateCheckDigit$set;
        private boolean validateCheckDigit$value;
        private boolean gcpLength$set;
        private Integer gcpLength$value;

        ValidationContextBuilder() {
        }

        /**
         * If true, enforce EPCIS‐compliance rules and validates GS1 AI against only EPCIS supported AI. Defaults to true.
         * @return {@code this}.
         */
        public ValidationContext.ValidationContextBuilder epcisCompliant(boolean epcisCompliant) {
            this.epcisCompliant$value = epcisCompliant;
            epcisCompliant$set = true;
            return this;
        }

        /**
         * If true, run the GS1 check-digit validation along with normal validation. Defaults to true.
         * @return {@code this}.
         */
        public ValidationContext.ValidationContextBuilder validateCheckDigit(boolean validateCheckDigit) {
            this.validateCheckDigit$value = validateCheckDigit;
            validateCheckDigit$set = true;
            return this;
        }

        /**
         * When present, validates for Digital-Link URI using this GCP length. Empty validates for URN mode.
         * @return {@code this}.
         */
        public ValidationContext.ValidationContextBuilder gcpLength(Integer gcpLength) {
            this.gcpLength$value = gcpLength;
            gcpLength$set = true;
            return this;
        }

        public ValidationContext build() {
            boolean epcisCompliant$value = this.epcisCompliant$value;
            if (!this.epcisCompliant$set) epcisCompliant$value = ValidationContext.$default$epcisCompliant();
            boolean validateCheckDigit$value = this.validateCheckDigit$value;
            if (!this.validateCheckDigit$set) validateCheckDigit$value = ValidationContext.$default$validateCheckDigit();
            Integer gcpLength$value = this.gcpLength$value;
            if (!this.gcpLength$set) gcpLength$value = ValidationContext.$default$gcpLength();
            return new ValidationContext(epcisCompliant$value, validateCheckDigit$value, gcpLength$value);
        }

        @Override
        public String toString() {
            return "ValidationContext.ValidationContextBuilder(epcisCompliant$value=" + this.epcisCompliant$value + ", validateCheckDigit$value=" + this.validateCheckDigit$value + ", gcpLength$value=" + this.gcpLength$value + ")";
        }
    }

    public static ValidationContext.ValidationContextBuilder builder() {
        return new ValidationContext.ValidationContextBuilder();
    }

    /**
     * If true, enforce EPCIS‐compliance rules and validates GS1 AI against only EPCIS supported AI. Defaults to true.
     */
    public boolean isEpcisCompliant() {
        return this.epcisCompliant;
    }

    /**
     * If true, run the GS1 check-digit validation along with normal validation. Defaults to true.
     */
    public boolean isValidateCheckDigit() {
        return this.validateCheckDigit;
    }

    /**
     * When present, validates for Digital-Link URI using this GCP length. Empty validates for URN mode.
     */
    public Integer getGcpLength() {
        return this.gcpLength;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ValidationContext)) return false;
        ValidationContext other = (ValidationContext) o;
        if (this.isEpcisCompliant() != other.isEpcisCompliant()) return false;
        if (this.isValidateCheckDigit() != other.isValidateCheckDigit()) return false;
        Object this$gcpLength = this.getGcpLength();
        Object other$gcpLength = other.getGcpLength();
        if (this$gcpLength == null ? other$gcpLength != null : !this$gcpLength.equals(other$gcpLength)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isEpcisCompliant() ? 79 : 97);
        result = result * PRIME + (this.isValidateCheckDigit() ? 79 : 97);
        Object $gcpLength = this.getGcpLength();
        result = result * PRIME + ($gcpLength == null ? 43 : $gcpLength.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ValidationContext(epcisCompliant=" + this.isEpcisCompliant() + ", validateCheckDigit=" + this.isValidateCheckDigit() + ", gcpLength=" + this.getGcpLength() + ")";
    }
}
