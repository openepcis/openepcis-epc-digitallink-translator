package io.openepcis.digitallink.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationIdentifier {
    private String title;
    private String label;
    private String shortcode;
    private String ai;
    private String format;
    private String type;
    private Boolean fixedLength;
    private String checkDigit;
    private String regex;
    private List<String> qualifiers;
}

