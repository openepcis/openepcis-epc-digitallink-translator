package io.openepcis.digitallink.model;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the properties of a GS1 Application Identifier.
 */
@Builder
@AllArgsConstructor
public class ApplicationIdentifier {
    final String title, label, shortcode, ai, format, type, checkDigit, regex;
    final boolean fixedLength;
    final List<String> qualifiers;

    public ApplicationIdentifier(String title, String label, String shortcode, String ai, String format, String type, boolean fixedLength, String checkDigit, String regex, List<String> qualifiers) {
        this.title = title;
        this.label = label;
        this.shortcode = shortcode;
        this.ai = ai;
        this.format = format;
        this.type = type;
        this.fixedLength = fixedLength;
        this.checkDigit = checkDigit;
        this.regex = regex;
        this.qualifiers = qualifiers != null ? qualifiers : new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public String getLabel() {
        return label;
    }

    public String getShortcode() {
        return shortcode;
    }

    public String getAi() {
        return ai;
    }

    public String getFormat() {
        return format;
    }

    public String getType() {
        return type;
    }

    public String getCheckDigit() {
        return checkDigit;
    }

    public String getRegex() {
        return regex;
    }

    public boolean isFixedLength() {
        return fixedLength;
    }

    public boolean getFixedLength() {
        return fixedLength;
    }

    public List<String> getQualifiers() {
        return qualifiers;
    }
}
