package io.openepcis.digitallink.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Represents the properties of a GS1 Application Identifier.
 */
@Data
@Builder
public class ApplicationIdentifier {
    final String title, label, shortcode, ai, format, type, checkDigit, regex;
    final boolean fixedLength;
    final List<String> qualifiers;
}
