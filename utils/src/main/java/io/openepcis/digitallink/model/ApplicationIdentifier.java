package io.openepcis.digitallink.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
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

    public boolean isFixedLength() {
        return fixedLength;
    }
}

