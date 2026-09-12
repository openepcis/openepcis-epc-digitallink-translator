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
package io.openepcis.digitallink.model;

import java.util.List;

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

    public String getTitle() {
        return this.title;
    }

    public String getLabel() {
        return this.label;
    }

    public String getShortcode() {
        return this.shortcode;
    }

    public String getAi() {
        return this.ai;
    }

    public String getFormat() {
        return this.format;
    }

    public String getType() {
        return this.type;
    }

    public Boolean getFixedLength() {
        return this.fixedLength;
    }

    public String getCheckDigit() {
        return this.checkDigit;
    }

    public String getRegex() {
        return this.regex;
    }

    public List<String> getQualifiers() {
        return this.qualifiers;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    public void setAi(String ai) {
        this.ai = ai;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFixedLength(Boolean fixedLength) {
        this.fixedLength = fixedLength;
    }

    public void setCheckDigit(String checkDigit) {
        this.checkDigit = checkDigit;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public void setQualifiers(List<String> qualifiers) {
        this.qualifiers = qualifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ApplicationIdentifier)) return false;
        ApplicationIdentifier other = (ApplicationIdentifier) o;
        if (!other.canEqual((Object) this)) return false;
        Object this$fixedLength = this.getFixedLength();
        Object other$fixedLength = other.getFixedLength();
        if (this$fixedLength == null ? other$fixedLength != null : !this$fixedLength.equals(other$fixedLength)) return false;
        Object this$title = this.getTitle();
        Object other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
        Object this$label = this.getLabel();
        Object other$label = other.getLabel();
        if (this$label == null ? other$label != null : !this$label.equals(other$label)) return false;
        Object this$shortcode = this.getShortcode();
        Object other$shortcode = other.getShortcode();
        if (this$shortcode == null ? other$shortcode != null : !this$shortcode.equals(other$shortcode)) return false;
        Object this$ai = this.getAi();
        Object other$ai = other.getAi();
        if (this$ai == null ? other$ai != null : !this$ai.equals(other$ai)) return false;
        Object this$format = this.getFormat();
        Object other$format = other.getFormat();
        if (this$format == null ? other$format != null : !this$format.equals(other$format)) return false;
        Object this$type = this.getType();
        Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        Object this$checkDigit = this.getCheckDigit();
        Object other$checkDigit = other.getCheckDigit();
        if (this$checkDigit == null ? other$checkDigit != null : !this$checkDigit.equals(other$checkDigit)) return false;
        Object this$regex = this.getRegex();
        Object other$regex = other.getRegex();
        if (this$regex == null ? other$regex != null : !this$regex.equals(other$regex)) return false;
        Object this$qualifiers = this.getQualifiers();
        Object other$qualifiers = other.getQualifiers();
        if (this$qualifiers == null ? other$qualifiers != null : !this$qualifiers.equals(other$qualifiers)) return false;
        return true;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ApplicationIdentifier;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $fixedLength = this.getFixedLength();
        result = result * PRIME + ($fixedLength == null ? 43 : $fixedLength.hashCode());
        Object $title = this.getTitle();
        result = result * PRIME + ($title == null ? 43 : $title.hashCode());
        Object $label = this.getLabel();
        result = result * PRIME + ($label == null ? 43 : $label.hashCode());
        Object $shortcode = this.getShortcode();
        result = result * PRIME + ($shortcode == null ? 43 : $shortcode.hashCode());
        Object $ai = this.getAi();
        result = result * PRIME + ($ai == null ? 43 : $ai.hashCode());
        Object $format = this.getFormat();
        result = result * PRIME + ($format == null ? 43 : $format.hashCode());
        Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        Object $checkDigit = this.getCheckDigit();
        result = result * PRIME + ($checkDigit == null ? 43 : $checkDigit.hashCode());
        Object $regex = this.getRegex();
        result = result * PRIME + ($regex == null ? 43 : $regex.hashCode());
        Object $qualifiers = this.getQualifiers();
        result = result * PRIME + ($qualifiers == null ? 43 : $qualifiers.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ApplicationIdentifier(title=" + this.getTitle() + ", label=" + this.getLabel() + ", shortcode=" + this.getShortcode() + ", ai=" + this.getAi() + ", format=" + this.getFormat() + ", type=" + this.getType() + ", fixedLength=" + this.getFixedLength() + ", checkDigit=" + this.getCheckDigit() + ", regex=" + this.getRegex() + ", qualifiers=" + this.getQualifiers() + ")";
    }

    public ApplicationIdentifier(String title, String label, String shortcode, String ai, String format, String type, Boolean fixedLength, String checkDigit, String regex, List<String> qualifiers) {
        this.title = title;
        this.label = label;
        this.shortcode = shortcode;
        this.ai = ai;
        this.format = format;
        this.type = type;
        this.fixedLength = fixedLength;
        this.checkDigit = checkDigit;
        this.regex = regex;
        this.qualifiers = qualifiers;
    }

    public ApplicationIdentifier() {
    }
}
