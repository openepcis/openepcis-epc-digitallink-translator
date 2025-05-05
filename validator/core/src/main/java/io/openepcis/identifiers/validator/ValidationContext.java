package io.openepcis.identifiers.validator;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ValidationContext {

    /**
     * If true, enforce EPCIS‐compliance rules and validates GS1 AI against only EPCIS supported AI. Defaults to true.
     */
    @Builder.Default
    boolean epcisCompliant = true;

    /**
     * If true, run the GS1 check-digit validation along with normal validation. Defaults to true.
     */
    @Builder.Default
    boolean validateCheckDigit = true;

    /**
     * When present, validates for Digital-Link URI using this GCP length. Empty validates for URN mode.
     */
    @Builder.Default
    Integer gcpLength = null;

    public static ValidationContext defaultContext() {
        return ValidationContext.builder().build();
    }
}
