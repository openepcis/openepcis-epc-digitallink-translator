package io.openepcis.digitallink.compression;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.openepcis.digitallink.model.ApplicationIdentifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A Java port of the GS1DigitalLinkToolkit.js library.
 * This toolkit provides functionalities to handle GS1 Digital Link URIs,
 * including building, parsing, compressing, and decompressing them.
 * <p>
 * NOTE: This class requires the Jackson Databind library for JSON processing
 * in the analyseURIsemantics method.
 *
 * @version 1.2
 * @see <a href="https://github.com/gs1/GS1DigitalLink-Toolkit">Original JS Toolkit</a>
 */
public class GS1DigitalLinkCompressor {

    //region Helper Classes for Data Structures

    /**
     * Holds the result of a URI analysis.
     */
    public static class AnalysisResult {
        public String uriStem = "";
        public String pathComponents = "";
        public String queryString = "";
        public String fragment = "";
        public String detected = "";
        public String uncompressedPath = "";
        public String compressedPath = "";
        public Map<String, String> pathCandidates = new LinkedHashMap<>();
        public Map<String, String> queryStringCandidates = new LinkedHashMap<>();
        public StructuredOutput structuredOutput;
        public String elementStringsOutput;
    }

    /**
     * Holds the structured output of parsed AIs.
     */
    public static class StructuredOutput {
        public List<Map<String, String>> identifiers = new ArrayList<>();
        public List<Map<String, String>> qualifiers = new ArrayList<>();
        public List<Map<String, String>> dataAttributes = new ArrayList<>();
        public List<Map<String, String>> other = new ArrayList<>();
    }

    /**
     * Holds the result of extracting AIs from a URI.
     */
    public static class ExtractionResult {
        public Map<String, String> gs1 = new LinkedHashMap<>();
        public Map<String, String> other = new LinkedHashMap<>();
    }

    /**
     * Holds the result of separating identifiers from other AIs.
     */
    private static class SeparatedAIResult {
        Map<String, String> id = new LinkedHashMap<>();
        Map<String, String> nonId = new LinkedHashMap<>();
    }

    /**
     * Holds the result of a decoding operation.
     */
    private static class DecodeResult {
        Map<String, String> gs1AIarray;
        int cursor;
        String s;
    }

    //endregion

    //region Member Variables (Data Resources)

    private final List<ApplicationIdentifier> aitable = new ArrayList<>();
    private final Map<String, List<Map<String, String>>> tableF = new LinkedHashMap<>();
    private final Map<String, String[]> tableOpt = new LinkedHashMap<>();
    private final Map<String, Map<String, Object>> tableS1 = new LinkedHashMap<>();
    private final Map<String, String[]> pathSequenceConstraints = new HashMap<>();
    private final Map<String, String[]> stringSemantics = new HashMap<>();
    private final Map<String, String[]> classSemantics = new HashMap<>();
    private final Map<String, String[]> dateSemantics = new HashMap<>();
    private final Map<String, String[]> dateTimeSecondsSemantics = new HashMap<>();
    private final Map<String, String[]> dateTimeMinutesSemantics = new HashMap<>();
    private final Map<String, String[]> dateRangeSemantics = new HashMap<>();
    private final Map<String, Map<String, Object>> quantitativeValueSemantics = new LinkedHashMap<>();

    private static final String safeBase64Alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
    private static final String hexAlphabet = "0123456789ABCDEF";

    private final Pattern regexAllNum = Pattern.compile("^[0-9]+$");
    private final Pattern regexHexLower = Pattern.compile("^[0-9a-f]+$");
    private final Pattern regexHexUpper = Pattern.compile("^[0-9A-F]+$");
    private final Pattern regexSafe64 = Pattern.compile("^[A-Za-z0-9_-]+$");

    private final Map<String, String> tableOptReverse = new HashMap<>();
    private final Map<String, Pattern> aiRegex = new HashMap<>();
    private final Map<String, String> aiShortCode = new HashMap<>();
    private final Map<String, List<String>> aiQualifiers = new HashMap<>();
    private final Map<String, String> aiCheckDigitPosition = new HashMap<>();

    public final Map<String, List<String>> aiMaps = new HashMap<>();
    public final Map<String, String> shortCodeToNumeric = new HashMap<>();
    public final List<String>[] AIsByLength;
    public final char groupSeparator = (char) 29;

    //endregion

    /**
     * Constructor for the GS1DigitalLinkToolkit.
     * Initializes all the necessary data tables and regular expressions.
     */
    public GS1DigitalLinkCompressor() {
        initializeData(); // Populate all data tables

        for (Map.Entry<String, String[]> entry : tableOpt.entrySet()) {
            List<String> sortedValues = new ArrayList<>(Arrays.asList(entry.getValue()));
            Collections.sort(sortedValues);
            tableOptReverse.put(sortedValues.toString(), entry.getKey());
        }

        for (ApplicationIdentifier ai : aitable) {
            if (ai != null) {
                aiRegex.put(ai.getAi(), Pattern.compile("^" + ai.getRegex() + "$"));
                if (ai.getShortcode() != null && !ai.getShortcode().isEmpty()) {
                    aiShortCode.put(ai.getAi(), ai.getShortcode());
                }
                if (ai.getQualifiers() != null && !ai.getQualifiers().isEmpty()) {
                    aiQualifiers.put(ai.getAi(), ai.getQualifiers());
                }
                if (ai.getCheckDigit() != null && !ai.getCheckDigit().isEmpty()) {
                    aiCheckDigitPosition.put(ai.getAi(), ai.getCheckDigit());
                }
            }
        }

        aiMaps.put("identifiers", aitable.stream().filter(ai -> "I".equals(ai.getType())).map(ai -> ai.getAi()).collect(Collectors.toList()));
        aiMaps.put("qualifiers", aitable.stream().filter(ai -> "Q".equals(ai.getType())).map(ai -> ai.getAi()).collect(Collectors.toList()));
        aiMaps.put("dataAttributes", aitable.stream().filter(ai -> "D".equals(ai.getType())).map(ai -> ai.getAi()).collect(Collectors.toList()));
        aiMaps.put("fixedLength", aitable.stream().filter(ai -> ai.isFixedLength()).map(ai -> ai.getAi()).collect(Collectors.toList()));
        aiMaps.put("variableLength", aitable.stream().filter(ai -> !ai.isFixedLength()).map(ai -> ai.getAi()).collect(Collectors.toList()));

        for (Map.Entry<String, String> entry : aiShortCode.entrySet()) {
            shortCodeToNumeric.put(entry.getValue(), entry.getKey());
        }

        AIsByLength = new ArrayList[5];
        for (int i = 2; i <= 4; i++) {
            final int len = i;
            AIsByLength[i] = aitable.stream().filter(ai -> ai.getAi().length() == len).map(ai -> ai.getAi()).collect(Collectors.toList());
        }
    }

    //region Public API Methods

    /**
     * Calculates the expected GS1 Check Digit for a given AI and value.
     *
     * @param ai         The GS1 Application Identifier.
     * @param gs1IDValue The value of the identifier.
     * @return The calculated check digit.
     */
    public int calculateCheckDigit(String ai, String gs1IDValue) {
        int counter = 0;
        int total = 0;
        int l;
        if (!aiCheckDigitPosition.containsKey(ai)) return -1; // Not applicable

        String pos = aiCheckDigitPosition.get(ai);
        l = "L".equals(pos) ? gs1IDValue.length() : Integer.parseInt(pos);

        for (int i = l - 2; i >= 0; i--) {
            int d = Character.getNumericValue(gs1IDValue.charAt(i));
            int multiplier = ((counter % 2) == 0) ? 3 : 1;
            total += (d * multiplier);
            counter++;
        }
        return (10 - (total % 10)) % 10;
    }

    /**
     * Verifies if the check digit in a GS1 ID value is correct.
     *
     * @param ai         The GS1 Application Identifier.
     * @param gs1IDValue The value of the identifier.
     * @return true if the check digit is valid or not applicable.
     * @throws IllegalArgumentException if the check digit is invalid.
     */
    public boolean verifyCheckDigit(String ai, String gs1IDValue) {
        if (aiCheckDigitPosition.containsKey(ai)) {
            int expectedCheckDigit = calculateCheckDigit(ai, gs1IDValue);
            String checkDigitPositionStr = aiCheckDigitPosition.get(ai);
            int checkDigitPosition = "L".equals(checkDigitPositionStr) ? gs1IDValue.length() : Integer.parseInt(checkDigitPositionStr);
            int actualCheckDigit = Character.getNumericValue(gs1IDValue.charAt(checkDigitPosition - 1));

            if (actualCheckDigit != expectedCheckDigit) {
                throw new IllegalArgumentException("INVALID CHECK DIGIT: An invalid check digit was found for the primary identification key (" + ai + ")" + gs1IDValue + " ; the correct check digit should be " + expectedCheckDigit + " at position " + checkDigitPosition);
            }
        }
        return true;
    }

    /**
     * Verifies the syntax of an AI's value against its expected format.
     *
     * @param ai    The GS1 Application Identifier.
     * @param value The value to check.
     * @throws IllegalArgumentException if the syntax is invalid.
     */
    public void verifySyntax(String ai, String value) {
        if (ai != null && regexAllNum.matcher(ai).matches()) {
            if (!aiRegex.containsKey(ai) || !aiRegex.get(ai).matcher(value).matches()) {
                throw new IllegalArgumentException("SYNTAX ERROR: invalid syntax for value of (" + ai + ")" + value);
            }
        }
    }

    /**
     * Percent-encodes reserved characters as defined in the GS1 Digital Link standard.
     *
     * @param input The string to encode.
     * @return The percent-encoded string.
     */
    public String percentEncode(String input) {
        String charsToEscape = "#/%&+,!()*':;<=>?";
        StringBuilder escaped = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (charsToEscape.indexOf(c) > -1) {
                escaped.append(String.format("%%%02X", (int) c));
            } else {
                escaped.append(c);
            }
        }
        return escaped.toString();
    }

    /**
     * Decodes a percent-encoded string.
     *
     * @param input The string to decode.
     * @return The decoded string.
     */
    public String percentDecode(String input) {
        try {
            return URLDecoder.decode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e); // Should not happen with UTF-8
        }
    }

    /**
     * Converts an associative array of GS1 AIs and values into GS1 element strings.
     *
     * @param gs1AIarray Map of AI keys to values.
     * @param brackets   If true, adds brackets around AIs for human readability.
     * @return A single concatenated element string.
     */
    public String buildGS1elementStrings(Map<String, String> gs1AIarray, boolean brackets) {
        List<String> identifiers = new ArrayList<>();
        List<String> qualifiers = new ArrayList<>();
        List<String> attributes = new ArrayList<>();

        List<String> variableLengthAIs = aiMaps.get("variableLength");

        gs1AIarray.keySet().forEach(key -> {
            if (aiMaps.get("identifiers").contains(key)) identifiers.add(key);
            if (aiMaps.get("qualifiers").contains(key)) qualifiers.add(key);
            if (aiMaps.get("dataAttributes").contains(key)) attributes.add(key);
        });

        StringBuilder elementStrings = new StringBuilder();

        if (brackets) {
            if (identifiers.size() != 1) {
                throw new IllegalArgumentException("The associative array should contain exactly one primary identification key.");
            }
            String primaryId = identifiers.get(0);
            verifySyntax(primaryId, gs1AIarray.get(primaryId));
            verifyCheckDigit(primaryId, gs1AIarray.get(primaryId));
            elementStrings.append("(").append(primaryId).append(")").append(padGTIN(primaryId, gs1AIarray.get(primaryId)));

            if (aiQualifiers.containsKey(primaryId)) {
                for (String q : aiQualifiers.get(primaryId)) {
                    if (qualifiers.contains(q) && gs1AIarray.containsKey(q)) {
                        elementStrings.append("(").append(q).append(")").append(padGTIN(q, gs1AIarray.get(q)));
                    }
                }
            }

            List<String> sortedAttributes = attributes.stream()
                    .filter(a -> !(aiQualifiers.containsKey(primaryId) && aiQualifiers.get(primaryId).contains(a)))
                    .sorted().collect(Collectors.toList());

            for (String attr : sortedAttributes) {
                elementStrings.append("(").append(attr).append(")").append(padGTIN(attr, gs1AIarray.get(attr)));
            }
        } else {
            List<String> sortedAIs = new ArrayList<>(gs1AIarray.keySet());
            sortedAIs.sort((s1, s2) -> {
                boolean s1IsVar = variableLengthAIs.contains(s1);
                boolean s2IsVar = variableLengthAIs.contains(s2);
                if (s1IsVar == s2IsVar) {
                    return s1.compareTo(s2);
                }
                return s1IsVar ? 1 : -1;
            });

            for (int i = 0; i < sortedAIs.size(); i++) {
                String ai = sortedAIs.get(i);
                elementStrings.append(ai).append(padGTIN(ai, gs1AIarray.get(ai)));
                if (variableLengthAIs.contains(ai) && i < sortedAIs.size() - 1) {
                    elementStrings.append(groupSeparator);
                }
            }
        }
        return elementStrings.toString();
    }

    /**
     * Parses a GS1 element string (bracketed or unbracketed) into a map of AIs and values.
     *
     * @param elementStrings The concatenated element string.
     * @return A map of AI keys to values.
     */
    public Map<String, String> extractFromGS1elementStrings(String elementStrings) {
        String processedStrings = elementStrings.replaceFirst("^(]C1|]e0|]d2|]Q3)", "");

        Pattern re = Pattern.compile("^\\((\\d{2,4}?)\\)");
        if (re.matcher(processedStrings).find()) {
            // Bracketed element string
            Map<String, String> obj = new LinkedHashMap<>();
            Pattern r1 = Pattern.compile("\\((\\d{2,4}?)\\)|([^(]+)");
            Matcher m = r1.matcher(processedStrings);
            String k = null;
            while (m.find()) {
                if (m.group(1) != null) {
                    k = m.group(1);
                } else if (m.group(2) != null && k != null) {
                    String value = m.group(2);
                    if (aiRegex.containsKey(k)) {
                        if (aiRegex.get(k).matcher(value).matches()) {
                            obj.put(k, value);
                        } else {
                            throw new IllegalArgumentException("SYNTAX ERROR: invalid syntax for value of (" + k + ") : " + value);
                        }
                    }
                    k = null;
                }
            }
            return obj;
        } else {
            // Unbracketed element string
            Map<String, String> obj = new LinkedHashMap<>();
            String remaining = processedStrings;
            while (!remaining.isEmpty()) {
                String ai = findAI(remaining);
                if (ai == null) {
                    throw new IllegalArgumentException("Could not determine AI for string: " + remaining);
                }
                int aiLen = ai.length();
                List<Map<String, String>> format = tableF.get(ai);
                if (format == null) {
                    throw new IllegalArgumentException("No format definition for AI: " + ai);
                }

                int valLen;
                if (!aiMaps.get("variableLength").contains(ai)) {
                    // Fixed length
                    valLen = format.stream().mapToInt(f -> Integer.parseInt(f.get("L"))).sum();
                } else {
                    // Variable length
                    int sepIndex = remaining.indexOf(groupSeparator);
                    if (sepIndex != -1) {
                        valLen = sepIndex - aiLen;
                    } else {
                        valLen = remaining.length() - aiLen;
                    }
                }

                String value = remaining.substring(aiLen, aiLen + valLen);
                obj.put(ai, value);
                remaining = remaining.substring(aiLen + valLen);
                if (remaining.startsWith(String.valueOf(groupSeparator))) {
                    remaining = remaining.substring(1);
                }
            }
            return obj;
        }
    }

    /**
     * Constructs a GS1 Digital Link URI from an associative array of AIs and values.
     *
     * @param gs1AIarray          Map of GS1 AI keys to values.
     * @param useShortText        If true, use short alphabetic mnemonics (e.g., /gtin/).
     * @param uriStem             The domain and optional path prefix (e.g., https://example.org). Defaults to https://id.gs1.org.
     * @param nonGS1keyvaluePairs A map of other query parameters to include.
     * @return The constructed GS1 Digital Link URI.
     */
    public String buildGS1digitalLink(Map<String, String> gs1AIarray, boolean useShortText, String uriStem, Map<String, String> nonGS1keyvaluePairs) {
        List<String> identifiers = new ArrayList<>();
        List<String> qualifiers = new ArrayList<>();
        List<String> attributes = new ArrayList<>();

        gs1AIarray.keySet().forEach(key -> {
            if (aiMaps.get("identifiers").contains(key)) identifiers.add(key);
            if (aiMaps.get("qualifiers").contains(key)) qualifiers.add(key);
            if (aiMaps.get("dataAttributes").contains(key)) attributes.add(key);
        });

        if (identifiers.size() != 1) {
            throw new IllegalArgumentException("The element string must contain exactly one primary identification key. Found: " + identifiers.size() + " " + identifiers);
        }

        String primaryIdentifier = identifiers.get(0);
        verifySyntax(primaryIdentifier, gs1AIarray.get(primaryIdentifier));
        verifyCheckDigit(primaryIdentifier, gs1AIarray.get(primaryIdentifier));

        StringBuilder path = new StringBuilder();
        String primaryIdentifierName = useShortText && aiShortCode.containsKey(primaryIdentifier) ? aiShortCode.get(primaryIdentifier) : primaryIdentifier;
        path.append("/").append(primaryIdentifierName).append("/").append(percentEncode(gs1AIarray.get(primaryIdentifier)));

        if (aiQualifiers.containsKey(primaryIdentifier)) {
            for (String q : aiQualifiers.get(primaryIdentifier)) {
                if (qualifiers.contains(q) && gs1AIarray.containsKey(q)) {
                    String qualifierName = useShortText && aiShortCode.containsKey(q) ? aiShortCode.get(q) : q;
                    path.append("/").append(qualifierName).append("/").append(percentEncode(gs1AIarray.get(q)));
                }
            }
        }

        List<String> queryStringArray = new ArrayList<>();
        List<String> sortedAttributes = attributes.stream()
                .filter(attr -> !(aiQualifiers.containsKey(primaryIdentifier) && aiQualifiers.get(primaryIdentifier).contains(attr)))
                .sorted()
                .toList();

        for (String attr : sortedAttributes) {
            if (gs1AIarray.containsKey(attr)) {
                String attrName = useShortText && aiShortCode.containsKey(attr) ? aiShortCode.get(attr) : attr;
                queryStringArray.add(attrName + "=" + percentEncode(gs1AIarray.get(attr)));
            }
        }

        if (nonGS1keyvaluePairs != null) {
            nonGS1keyvaluePairs.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> queryStringArray.add(entry.getKey() + "=" + percentEncode(entry.getValue())));
        }

        StringBuilder webURI = new StringBuilder();
        if (uriStem == null || uriStem.isEmpty()) {
            webURI.append("https://id.gs1.org");
        } else {
            webURI.append(uriStem.endsWith("/") ? uriStem.substring(0, uriStem.length() - 1) : uriStem);
        }

        webURI.append(path);

        if (!queryStringArray.isEmpty()) {
            webURI.append("?").append(String.join("&", queryStringArray));
        }

        return webURI.toString();
    }

    /**
     * Parses a GS1 Digital Link URI into its constituent parts.
     *
     * @param gs1DigitalLinkURI The URI to analyze.
     * @param extended          If true, performs a deeper analysis including structuring the output.
     * @return An {@link AnalysisResult} object containing the parsed components.
     */
    public AnalysisResult analyseURI(String gs1DigitalLinkURI, boolean extended) {
        AnalysisResult rv = new AnalysisResult();

        String tempUri = gs1DigitalLinkURI;
        int fragmentIndex = tempUri.indexOf('#');
        if (fragmentIndex > -1) {
            rv.fragment = tempUri.substring(fragmentIndex + 1);
            tempUri = tempUri.substring(0, fragmentIndex);
        }

        int queryIndex = tempUri.indexOf('?');
        if (queryIndex > -1) {
            rv.queryString = tempUri.substring(queryIndex + 1);
            tempUri = tempUri.substring(0, queryIndex);
        }

        if (tempUri.endsWith("/")) {
            tempUri = tempUri.substring(0, tempUri.length() - 1);
        }

        Pattern protocolPattern = Pattern.compile("^(https?://)");
        Matcher protocolMatcher = protocolPattern.matcher(tempUri);
        String protocol = "";
        if (protocolMatcher.find()) {
            protocol = protocolMatcher.group(1);
        }

        String afterProtocol = tempUri.substring(protocol.length());
        int firstSlash = afterProtocol.indexOf('/');
        if (firstSlash == -1) { // No path
            rv.uriStem = protocol + afterProtocol;
            return rv;
        }

        String domain = afterProtocol.substring(0, firstSlash);
        String pathInfo = afterProtocol.substring(firstSlash);

        String[] pathSegments = pathInfo.length() > 1 ? pathInfo.substring(1).split("/") : new String[0];

        int primaryIdPathIndex = -1;
        String numericPrimaryIdentifier = "";

        for (int i = 0; i < pathSegments.length; i += 2) {
            String pcc = pathSegments[i];
            String numkey = regexAllNum.matcher(pcc).matches() ? pcc : shortCodeToNumeric.get(pcc);
            if (numkey != null && aiMaps.get("identifiers").contains(numkey)) {
                primaryIdPathIndex = i;
                numericPrimaryIdentifier = numkey;
                break;
            }
        }

        if (primaryIdPathIndex != -1) {
            // UNCOMPRESSED / PARTIALLY COMPRESSED
            String[] relevantPathArray = Arrays.copyOfRange(pathSegments, primaryIdPathIndex, pathSegments.length);
            String[] stemPathArray = Arrays.copyOfRange(pathSegments, 0, primaryIdPathIndex);

            rv.pathComponents = "/" + String.join("/", relevantPathArray);
            rv.uriStem = protocol + domain + (stemPathArray.length > 0 ? "/" + String.join("/", stemPathArray) : "");

            for (int i = 0; i < relevantPathArray.length - 1; i += 2) {
                rv.pathCandidates.put(relevantPathArray[i], percentDecode(relevantPathArray[i + 1]));
            }

            // Detection for uncompressed
            if (relevantPathArray.length >= 2 && relevantPathArray.length % 2 == 0) {
                String value = relevantPathArray[1];
                if (aiRegex.containsKey(numericPrimaryIdentifier) && aiRegex.get(numericPrimaryIdentifier).matcher(value).matches()) {
                    rv.detected = "uncompressed GS1 Digital Link";
                    rv.uncompressedPath = rv.pathComponents;
                }
            }

            // Detection for partially compressed
            if (rv.detected.isEmpty() && relevantPathArray.length == 3 && regexSafe64.matcher(relevantPathArray[2]).matches()) {
                String value = relevantPathArray[1];
                if (aiRegex.containsKey(numericPrimaryIdentifier) && aiRegex.get(numericPrimaryIdentifier).matcher(value).matches()) {
                    rv.detected = "partially compressed GS1 Digital Link";
                    rv.uncompressedPath = "/" + relevantPathArray[0] + "/" + relevantPathArray[1];
                    rv.compressedPath = relevantPathArray[2];
                }
            }
        } else if (pathSegments.length > 0 && regexSafe64.matcher(pathSegments[pathSegments.length - 1]).matches()) {
            // FULLY COMPRESSED
            String compressedSegment = pathSegments[pathSegments.length - 1];
            String[] stemPathArray = Arrays.copyOfRange(pathSegments, 0, pathSegments.length - 1);

            rv.pathComponents = "/" + compressedSegment;
            rv.uriStem = protocol + domain + (stemPathArray.length > 0 ? "/" + String.join("/", stemPathArray) : "");
            rv.detected = "fully compressed GS1 Digital Link";
            rv.compressedPath = compressedSegment;
        } else {
            // NO GS1 PATH
            rv.uriStem = protocol + domain + pathInfo;
        }

        if (!rv.queryString.isEmpty()) {
            String cleanQueryString = rv.queryString.replace(';', '&');
            String[] pairs = cleanQueryString.split("&");
            for (String pair : pairs) {
                String[] p = pair.split("=", 2);
                if (p.length == 2) {
                    rv.queryStringCandidates.put(p[0], percentDecode(p[1]));
                }
            }
        }

        if (extended && !rv.detected.isEmpty()) {
            ExtractionResult extracted;
            if (rv.detected.contains("uncompressed")) {
                extracted = extractFromGS1digitalLink(gs1DigitalLinkURI);
                rv.elementStringsOutput = gs1digitalLinkToGS1elementStrings(gs1DigitalLinkURI, true);
            } else {
                extracted = extractFromCompressedGS1digitalLink(gs1DigitalLinkURI);
                rv.elementStringsOutput = gs1compressedDigitalLinkToGS1elementStrings(gs1DigitalLinkURI, true);
            }
            rv.structuredOutput = buildStructuredArray(extracted.gs1, extracted.other);
        }

        return rv;
    }

    /**
     * Extracts GS1 data from an uncompressed GS1 Digital Link URI.
     *
     * @param gs1DigitalLinkURI The URI to parse.
     * @return An {@link ExtractionResult} object with GS1 data and other parameters.
     */
    public ExtractionResult extractFromGS1digitalLink(String gs1DigitalLinkURI) {
        ExtractionResult rv = new ExtractionResult();
        AnalysisResult s = analyseURI(gs1DigitalLinkURI, false);

        Map<String, String> candidates = new LinkedHashMap<>();
        candidates.putAll(s.pathCandidates);
        candidates.putAll(s.queryStringCandidates);

        candidates.forEach((k, v) -> {
            String numkey = regexAllNum.matcher(k).matches() ? k : shortCodeToNumeric.get(k);
            if (numkey != null && (aiMaps.get("identifiers").contains(numkey) || aiMaps.get("qualifiers").contains(numkey) || aiMaps.get("dataAttributes").contains(numkey))) {
                verifySyntax(numkey, v);
                verifyCheckDigit(numkey, v);
                rv.gs1.put(numkey, padGTIN(numkey, v));
            } else {
                rv.other.put(k, v);
            }
        });

        return rv;
    }

    /**
     * Extracts GS1 data from a compressed or partially compressed GS1 Digital Link URI.
     *
     * @param gs1DigitalLinkURI The URI to parse.
     * @return An {@link ExtractionResult} containing the decompressed data.
     */
    public ExtractionResult extractFromCompressedGS1digitalLink(String gs1DigitalLinkURI) {
        ExtractionResult rv = new ExtractionResult();
        AnalysisResult s = analyseURI(gs1DigitalLinkURI, false);

        final Map<String, String> decompressedAIs = new HashMap<>();

        if ("fully compressed GS1 Digital Link".equals(s.detected)) {
            String binstr = base642bin(s.compressedPath);
            decompressedAIs.putAll(decompressBinaryToGS1AIarray(binstr));
        } else if ("partially compressed GS1 Digital Link".equals(s.detected)) {
            String binstr = base642bin(s.compressedPath);
            decompressedAIs.putAll(decompressBinaryToGS1AIarray(binstr));
            // Add the uncompressed primary key from the path
            s.pathCandidates.forEach((key, value) -> {
                String numkey = shortCodeToNumeric.getOrDefault(key, key);
                decompressedAIs.put(numkey, value);
            });
        }

        // Handle query string parameters
        s.queryStringCandidates.forEach((key, value) -> {
            String numkey = shortCodeToNumeric.getOrDefault(key, key);
            if (aiMaps.values().stream().anyMatch(list -> list.contains(numkey))) {
                decompressedAIs.put(numkey, value);
            } else {
                rv.other.put(key, value);
            }
        });

        // Separate any non-GS1 keys that might have been decompressed
        decompressedAIs.forEach((key, value) -> {
            if (aiMaps.values().stream().anyMatch(list -> list.contains(key))) {
                rv.gs1.put(key, value);
            } else {
                rv.other.put(key, value);
            }
        });

        return rv;
    }

    /**
     * Translates an uncompressed GS1 Digital Link URI into a string of concatenated GS1 element strings.
     *
     * @param digitalLinkURI The uncompressed URI.
     * @param brackets       Whether to enclose AIs in brackets.
     * @return The concatenated element string.
     */
    public String gs1digitalLinkToGS1elementStrings(String digitalLinkURI, boolean brackets) {
        return buildGS1elementStrings(extractFromGS1digitalLink(digitalLinkURI).gs1, brackets);
    }

    /**
     * Translates a compressed GS1 Digital Link URI into a string of concatenated GS1 element strings.
     *
     * @param digitalLinkURI The compressed URI.
     * @param brackets       Whether to enclose AIs in brackets.
     * @return The concatenated element string.
     */
    public String gs1compressedDigitalLinkToGS1elementStrings(String digitalLinkURI, boolean brackets) {
        return buildGS1elementStrings(extractFromCompressedGS1digitalLink(digitalLinkURI).gs1, brackets);
    }

    /**
     * Compresses a GS1 Digital Link URI.
     *
     * @param digitalLinkURI             The uncompressed URI.
     * @param useOptimisations           Whether to use pre-defined AI sequence optimisations.
     * @param compressOtherKeyValuePairs If true, non-GS1 query params are also compressed.
     * @return The compressed URI.
     */
    public String compressGS1DigitalLink(String digitalLinkURI, boolean useOptimisations, boolean compressOtherKeyValuePairs) {
        AnalysisResult s = analyseURI(digitalLinkURI, false);
        ExtractionResult extracted = extractFromGS1digitalLink(digitalLinkURI);

        Map<String, String> nonGS1PairsToCompress = compressOtherKeyValuePairs ? extracted.other : new HashMap<>();
        Map<String, String> nonGS1PairsToKeepInQuery = compressOtherKeyValuePairs ? new HashMap<>() : extracted.other;

        String binStr = compressGS1AIarrayToBinary(extracted.gs1, useOptimisations, nonGS1PairsToCompress);
        String compressedPath = "/" + bin2base64(binStr);

        String finalURI = s.uriStem + compressedPath;

        if (!nonGS1PairsToKeepInQuery.isEmpty()) {
            String queryString = nonGS1PairsToKeepInQuery.entrySet().stream()
                    .map(e -> e.getKey() + "=" + percentEncode(e.getValue()))
                    .collect(Collectors.joining("&"));
            finalURI += "?" + queryString;
        }

        return finalURI;
    }

    /**
     * Decompresses a compressed GS1 Digital Link URI.
     *
     * @param compressedDigitalLinkURI The compressed URI.
     * @return The uncompressed URI.
     */
    public String decompressGS1DigitalLink(String compressedDigitalLinkURI) {
        AnalysisResult analysis = analyseURI(compressedDigitalLinkURI, false);
        ExtractionResult extracted = extractFromCompressedGS1digitalLink(compressedDigitalLinkURI);
        return buildGS1digitalLink(extracted.gs1, false, analysis.uriStem, extracted.other);
    }

    /**
     * Generates a semantic, JSON-LD representation of a GS1 Digital Link URI.
     *
     * @param gs1DigitalLinkURI The URI to analyze.
     * @return An {@link ObjectNode} from the Jackson library representing the JSON-LD output.
     */
    public ObjectNode analyseURIsemantics(String gs1DigitalLinkURI) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode outputObject = mapper.createObjectNode();
        ObjectNode context = mapper.createObjectNode();

        // Initialize context
        context.put("schema", "http://schema.org/");
        context.put("gs1", "https://gs1.org/voc/");
        context.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

        AnalysisResult rv = analyseURI(gs1DigitalLinkURI, true);
        if (rv.structuredOutput == null) return outputObject; // Cannot proceed

        String uncompressedDL = gs1DigitalLinkURI;
        if (rv.detected.contains("compressed")) {
            uncompressedDL = decompressGS1DigitalLink(gs1DigitalLinkURI);
        }

        String excludeQueryString = uncompressedDL.split("\\?")[0];

        Map<String, String> elementStrings = new HashMap<>();
        rv.structuredOutput.identifiers.forEach(elementStrings::putAll);
        rv.structuredOutput.qualifiers.forEach(elementStrings::putAll);
        rv.structuredOutput.dataAttributes.forEach(elementStrings::putAll);

        String primaryIdentifier = rv.structuredOutput.identifiers.get(0).keySet().iterator().next();

        boolean isInstanceIdentifier = false;
        if (tableS1.containsKey(primaryIdentifier)) {
            Map<String, Object> iiaqV = tableS1.get(primaryIdentifier);
            if (iiaqV.get("requires") == null) {
                isInstanceIdentifier = true;
            } else {
                List<String> requiredAIs = (List<String>) iiaqV.get("requires");
                isInstanceIdentifier = requiredAIs.stream().anyMatch(elementStrings::containsKey);
            }
        }

        outputObject.put("@id", isInstanceIdentifier ? excludeQueryString : "_:1");

        ArrayNode otype = mapper.createArrayNode().add("rdfs:Class").add("owl:Class");
        classSemantics.entrySet().stream()
                .filter(entry -> elementStrings.containsKey(entry.getKey()))
                .forEach(entry -> Arrays.stream(entry.getValue()).forEach(otype::add));
        outputObject.set("@type", otype);

        stringSemantics.entrySet().stream()
                .filter(entry -> elementStrings.containsKey(entry.getKey()))
                .forEach(entry -> {
                    for (String predicate : entry.getValue()) {
                        outputObject.put(predicate, elementStrings.get(entry.getKey()));
                    }
                });

        dateSemantics.entrySet().stream()
                .filter(entry -> elementStrings.containsKey(entry.getKey()))
                .forEach(entry -> {
                    for (String predicate : entry.getValue()) {
                        context.set(predicate, mapper.createObjectNode().put("@type", "xsd:date"));
                        outputObject.put(predicate, sixDigitToXsdDate(elementStrings.get(entry.getKey())));
                    }
                });

        outputObject.put("gs1:elementStrings", rv.elementStringsOutput);

        ObjectNode finalResult = mapper.createObjectNode();
        finalResult.set("@context", context);
        finalResult.setAll(outputObject);

        return finalResult;
    }

    //endregion

    //region Private and Helper Methods

    private String padToLength(String input, int requiredLength) {
        if (input.length() < requiredLength) {
            return String.join("", Collections.nCopies(requiredLength - input.length(), "0")) + input;
        }
        return input;
    }

    private String padGTIN(String ai, String value) {
        if ("01".equals(ai) || "(01)".equals(ai) || "02".equals(ai) || "(02)".equals(ai)) {
            switch (value.length()) {
                case 8:
                    return "000000" + value;
                case 12:
                    return "00" + value;
                case 13:
                    return "0" + value;
            }
        }
        return value;
    }

    private String findAI(String s) {
        for (int i = 4; i >= 2; i--) {
            if (s.length() >= i) {
                String potentialAI = s.substring(0, i);
                if (AIsByLength[i].contains(potentialAI)) {
                    return potentialAI;
                }
            }
        }
        return null;
    }

    private String sixDigitToXsdDate(String sixDigit) {
        if (!sixDigit.matches("\\d{6}")) {
            throw new IllegalArgumentException("Date must be 6 digits (YYMMDD)");
        }
        String yearStr = sixDigit.substring(0, 2);
        String monthStr = sixDigit.substring(2, 4);
        String dayStr = sixDigit.substring(4, 6);

        int year = Integer.parseInt(determineFourDigitYear(yearStr));
        int month = Integer.parseInt(monthStr);
        int day = Integer.parseInt(dayStr);

        if (day == 0) {
            LocalDate date = LocalDate.of(year, month, 1);
            return date.withDayOfMonth(date.lengthOfMonth()).format(DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            return LocalDate.of(year, month, day).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    private String determineFourDigitYear(String twoDigitYearStr) {
        int twoDigitYear = Integer.parseInt(twoDigitYearStr);
        int currentYear = Year.now().getValue();
        int currentCentury = (currentYear / 100) * 100;
        int currentTwoDigitYear = currentYear % 100;
        int difference = twoDigitYear - currentTwoDigitYear;

        if (difference > 50) return String.valueOf(currentCentury - 100 + twoDigitYear);
        if (difference < -50) return String.valueOf(currentCentury + 100 + twoDigitYear);
        return String.valueOf(currentCentury + twoDigitYear);
    }

    private int numberOfValueBits(int valueLength) {
        if (valueLength == 0) return 0;
        return (int) Math.ceil(valueLength * Math.log(10) / Math.log(2) + 0.01);
    }

    private int numberOfLengthBits(int maxLength) {
        if (maxLength == 0) return 0;
        return (int) Math.ceil(Math.log(maxLength) / Math.log(2) + 0.01);
    }

    private String bin2base64(String binstr) {
        StringBuilder rv = new StringBuilder();
        if (binstr.length() % 6 > 0) {
            int numberRightPadZeros = 6 - (binstr.length() % 6);
            binstr += String.join("", Collections.nCopies(numberRightPadZeros, "0"));
        }
        for (int i = 0; i < binstr.length(); i += 6) {
            String binFrag = binstr.substring(i, i + 6);
            rv.append(safeBase64Alphabet.charAt(Integer.parseInt(binFrag, 2)));
        }
        return rv.toString();
    }

    private String base642bin(String base64str) {
        StringBuilder rv = new StringBuilder();
        for (char c : base64str.toCharArray()) {
            int dec = safeBase64Alphabet.indexOf(c);
            String bin = Integer.toBinaryString(dec);
            rv.append(padToLength(bin, 6));
        }
        return rv.toString();
    }

    private String binstrToHex(String binstr) {
        if (binstr.isEmpty()) return "";
        return new BigInteger(binstr, 2).toString(16).toUpperCase();
    }

    private String compressGS1AIarrayToBinary(Map<String, String> gs1AIarray, boolean useOptimisations, Map<String, String> nonGS1keyvaluePairs) {
        StringBuilder binstr = new StringBuilder();
        List<String> akeysa = new ArrayList<>(gs1AIarray.keySet());
        Collections.sort(akeysa);
        List<String> optimisations = new ArrayList<>();

        if (useOptimisations) {
            while (true) {
                Map<String, Integer> candidatesFromTableOpt = findCandidatesFromTableOpt(akeysa);
                if (candidatesFromTableOpt.isEmpty()) break;

                String bestCandidate = findBestOptimisationCandidate(candidatesFromTableOpt);
                if (bestCandidate.isEmpty()) break;

                optimisations.add(bestCandidate);
                akeysa = removeOptimisedKeysFromAIlist(akeysa, tableOpt.get(bestCandidate));
            }
        }

        for (String key : optimisations) {
            binstr.append(binaryEncodingOfGS1AIKey(key));
            for (String k : tableOpt.get(key)) {
                binstr.append(binaryEncodingOfValue(gs1AIarray, k));
            }
        }

        for (String key : akeysa) {
            binstr.append(binaryEncodingOfGS1AIKey(key));
            binstr.append(binaryEncodingOfValue(gs1AIarray, key));
        }

        if (nonGS1keyvaluePairs != null) {
            for (Map.Entry<String, String> entry : nonGS1keyvaluePairs.entrySet()) {
                binstr.append(binaryEncodingOfNonGS1KeyValuePair(entry.getKey(), entry.getValue()));
            }
        }

        return binstr.toString();
    }

    private Map<String, Integer> findCandidatesFromTableOpt(List<String> akeysa) {
        Map<String, Integer> candidates = new HashMap<>();
        for (Map.Entry<String, String[]> entry : tableOpt.entrySet()) {
            if (new HashSet<>(akeysa).containsAll(Arrays.asList(entry.getValue()))) {
                candidates.put(entry.getKey(), String.join("", entry.getValue()).length());
            }
        }
        return candidates;
    }

    private String findBestOptimisationCandidate(Map<String, Integer> candidates) {
        return candidates.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
    }

    private List<String> removeOptimisedKeysFromAIlist(List<String> akeysa, String[] toRemove) {
        List<String> remaining = new ArrayList<>(akeysa);
        remaining.removeAll(Arrays.asList(toRemove));
        return remaining;
    }

    private String binaryEncodingOfGS1AIKey(String key) {
        StringBuilder binAI = new StringBuilder();
        for (char c : key.toCharArray()) {
            binAI.append(padToLength(Integer.toBinaryString(Character.digit(c, 16)), 4));
        }
        return binAI.toString();
    }

    private String binaryEncodingOfValue(Map<String, String> gs1AIarray, String key) {
        StringBuilder binstr = new StringBuilder();
        if (tableF.containsKey(key)) {
            int cursor = 0;
            String value = gs1AIarray.get(key);
            for (Map<String, String> tx : tableF.get(key)) {
                if (tx.containsKey("L") && "N".equals(tx.get("E"))) {
                    int len = Integer.parseInt(tx.get("L"));
                    String charstr = value.substring(cursor, cursor + len);
                    cursor += len;
                    String binValue = padToLength(new BigInteger(charstr).toString(2), numberOfValueBits(len));
                    binstr.append(binValue);
                } else if (tx.containsKey("M") && "N".equals(tx.get("E"))) {
                    int maxLen = Integer.parseInt(tx.get("M"));
                    String charstr = value.substring(cursor);
                    cursor += charstr.length();
                    String lengthBits = padToLength(Integer.toBinaryString(charstr.length()), numberOfLengthBits(maxLen));
                    String binValue = charstr.isEmpty() ? "" : padToLength(new BigInteger(charstr).toString(2), numberOfValueBits(charstr.length()));
                    binstr.append(lengthBits).append(binValue);
                } else if ("X".equals(tx.get("E"))) { // Alphanumeric
                    String charstr;
                    String lengthBits = "";
                    if (tx.containsKey("L")) {
                        int len = Integer.parseInt(tx.get("L"));
                        charstr = value.substring(cursor, cursor + len);
                        cursor += len;
                    } else { // Variable length
                        int maxLen = Integer.parseInt(tx.get("M"));
                        charstr = value.substring(cursor);
                        cursor += charstr.length();
                        lengthBits = padToLength(Integer.toBinaryString(charstr.length()), numberOfLengthBits(maxLen));
                    }
                    int enc = determineEncoding(charstr);
                    binstr.append(padToLength(Integer.toBinaryString(enc), 3)); // Encoding indicator
                    binstr.append(lengthBits); // Length bits (if variable)
                    binstr.append(buildBinaryValue(charstr, enc));
                }
            }
        }
        return binstr.toString();
    }

    private String binaryEncodingOfNonGS1KeyValuePair(String key, String value) {
        StringBuilder binstr = new StringBuilder();
        // Flag for non-GS1 key
        binstr.append("1111");
        // Key length (7 bits)
        binstr.append(padToLength(Integer.toBinaryString(key.length()), 7));
        // Key value (6 bits per char)
        binstr.append(buildBinaryValue(key, 3)); // 3 is Base64 encoding
        // Value encoding
        int enc = determineEncoding(value);
        binstr.append(padToLength(Integer.toBinaryString(enc), 3));
        // Value length (7 bits)
        binstr.append(padToLength(Integer.toBinaryString(value.length()), 7));
        // Value
        binstr.append(buildBinaryValue(value, enc));

        return binstr.toString();
    }

    private int determineEncoding(String charstr) {
        if (regexAllNum.matcher(charstr).matches()) return 0;
        if (regexHexLower.matcher(charstr).matches()) return 1;
        if (regexHexUpper.matcher(charstr).matches()) return 2;
        if (regexSafe64.matcher(charstr).matches()) return 3;
        return 4; // Default to 7-bit ASCII
    }

    private String buildBinaryValue(String charstr, int enc) {
        StringBuilder binValue = new StringBuilder();
        switch (enc) {
            case 0: // numeric
                if (!charstr.isEmpty()) {
                    binValue.append(padToLength(new BigInteger(charstr).toString(2), numberOfValueBits(charstr.length())));
                }
                break;
            case 1: // hex lower
            case 2: // hex upper
                for (char c : charstr.toUpperCase().toCharArray()) {
                    binValue.append(padToLength(Integer.toBinaryString(hexAlphabet.indexOf(c)), 4));
                }
                break;
            case 3: // base64
                for (char c : charstr.toCharArray()) {
                    binValue.append(padToLength(Integer.toBinaryString(safeBase64Alphabet.indexOf(c)), 6));
                }
                break;
            case 4: // ascii
                for (char c : charstr.toCharArray()) {
                    binValue.append(padToLength(Integer.toBinaryString(c), 7));
                }
                break;
        }
        return binValue.toString();
    }

    private Map<String, String> decompressBinaryToGS1AIarray(String binstr) {
        Map<String, String> gs1AIarray = new LinkedHashMap<>();
        int cursor = 0;

        while ((binstr.length() - cursor) >= 8) {
            String h1 = binstrToHex(binstr.substring(cursor, cursor + 4));
            String h2 = binstrToHex(binstr.substring(cursor + 4, cursor + 8));
            String h1h2 = h1 + h2;
            cursor += 8;

            if (regexAllNum.matcher(h1h2).matches()) { // Numeric AI
                String ai = findAINumeric(h1h2, binstr, cursor);
                cursor += (ai.length() - 2) * 4; // Advance cursor for 3 or 4 digit AIs
                DecodeResult res = decodeBinaryValue(ai, binstr, cursor);
                gs1AIarray.putAll(res.gs1AIarray);
                cursor = res.cursor;
            } else if ("F".equals(h1)) { // Non-GS1 Key
                int keyLength = Integer.parseInt(binstr.substring(cursor - 4, cursor - 4 + 7), 2);
                cursor += 3;
                String key = buildString(keyLength, 3, binstr, cursor).s;
                cursor += keyLength * 6;

                int enc = Integer.parseInt(binstr.substring(cursor, cursor + 3), 2);
                cursor += 3;
                int valLength = Integer.parseInt(binstr.substring(cursor, cursor + 7), 2);
                cursor += 7;
                String value = buildString(valLength, enc, binstr, cursor).s;
                cursor += getBitsForValue(valLength, enc);
                gs1AIarray.put(key, value);

            } else if (tableOpt.containsKey(h1h2)) { // Optimisation
                for (String ai : tableOpt.get(h1h2)) {
                    DecodeResult res = decodeBinaryValue(ai, binstr, cursor);
                    gs1AIarray.putAll(res.gs1AIarray);
                    cursor = res.cursor;
                }
            } else {
                throw new IllegalArgumentException("Decompression error: Unrecognized AI or optimisation code: " + h1h2);
            }
        }
        return gs1AIarray;
    }

    private String findAINumeric(String firstTwoDigits, String binstr, int cursor) {
        if (!tableF.containsKey(firstTwoDigits)) { // It could be a 3 or 4 digit AI
            String h3 = binstrToHex(binstr.substring(cursor, cursor + 4));
            String threeDigitAI = firstTwoDigits + h3;
            if (tableF.containsKey(threeDigitAI)) {
                return threeDigitAI;
            }
            String h4 = binstrToHex(binstr.substring(cursor + 4, cursor + 8));
            String fourDigitAI = threeDigitAI + h4;
            if (tableF.containsKey(fourDigitAI)) {
                return fourDigitAI;
            }
        }
        return firstTwoDigits;
    }

    private DecodeResult decodeBinaryValue(String key, String binstr, int cursor) {
        DecodeResult res = new DecodeResult();
        res.gs1AIarray = new LinkedHashMap<>();
        StringBuilder value = new StringBuilder();

        if (tableF.containsKey(key)) {
            for (Map<String, String> tx : tableF.get(key)) {
                if ("N".equals(tx.get("E"))) { // Numeric component
                    int numDigits;
                    if (tx.containsKey("L")) {
                        numDigits = Integer.parseInt(tx.get("L"));
                    } else { // Variable length
                        int maxLen = Integer.parseInt(tx.get("M"));
                        int lenBitsCount = numberOfLengthBits(maxLen);
                        numDigits = Integer.parseInt(binstr.substring(cursor, cursor + lenBitsCount), 2);
                        cursor += lenBitsCount;
                    }
                    if (numDigits > 0) {
                        int numBitsForValue = numberOfValueBits(numDigits);
                        String rbv = binstr.substring(cursor, cursor + numBitsForValue);
                        cursor += numBitsForValue;
                        String s = new BigInteger(rbv, 2).toString();
                        value.append(padToLength(s, numDigits));
                    }
                } else if ("X".equals(tx.get("E"))) { // Alphanumeric
                    int enc = Integer.parseInt(binstr.substring(cursor, cursor + 3), 2);
                    cursor += 3;
                    int numChars;
                    if (tx.containsKey("L")) {
                        numChars = Integer.parseInt(tx.get("L"));
                    } else { // Variable
                        int maxLen = Integer.parseInt(tx.get("M"));
                        int lenBitsCount = numberOfLengthBits(maxLen);
                        numChars = Integer.parseInt(binstr.substring(cursor, cursor + lenBitsCount), 2);
                        cursor += lenBitsCount;
                    }
                    DecodeResult strRes = buildString(numChars, enc, binstr, cursor);
                    value.append(strRes.s);
                    cursor = strRes.cursor;
                }
            }
        }
        res.gs1AIarray.put(key, value.toString());
        res.cursor = cursor;
        return res;
    }

    private DecodeResult buildString(int numChars, int enc, String binstr, int cursor) {
        DecodeResult res = new DecodeResult();
        StringBuilder s = new StringBuilder();
        int bitsPerChar = getBitsPerChar(enc);

        int numBitsForValue = numChars * bitsPerChar;
        if (enc == 0) { // Special handling for numeric
            numBitsForValue = numberOfValueBits(numChars);
        }

        String rbv = binstr.substring(cursor, cursor + numBitsForValue);

        if (enc == 0) {
            s.append(new BigInteger(rbv, 2).toString());
        } else {
            for (int i = 0; i < numChars; i++) {
                String chunk = rbv.substring(i * bitsPerChar, (i + 1) * bitsPerChar);
                int index = Integer.parseInt(chunk, 2);
                if (enc == 4) { // ASCII
                    s.append((char) index);
                } else {
                    String alphabet = (enc == 3) ? safeBase64Alphabet : hexAlphabet;
                    s.append(alphabet.charAt(index));
                }
            }
        }

        res.s = s.toString();
        res.cursor = cursor + numBitsForValue;
        return res;
    }

    private int getBitsPerChar(int enc) {
        switch (enc) {
            case 1:
            case 2:
                return 4;
            case 3:
                return 6;
            case 4:
                return 7;
            default:
                return 0; // Numeric is variable
        }
    }

    private int getBitsForValue(int numChars, int enc) {
        if (enc == 0) return numberOfValueBits(numChars);
        return numChars * getBitsPerChar(enc);
    }

    private StructuredOutput buildStructuredArray(Map<String, String> gs1AIarray, Map<String, String> otherArray) {
        StructuredOutput map = new StructuredOutput();
        gs1AIarray.forEach((key, value) -> {
            Map<String, String> entry = Collections.singletonMap(key, value);
            if (aiMaps.get("identifiers").contains(key)) map.identifiers.add(entry);
            else if (aiMaps.get("qualifiers").contains(key)) map.qualifiers.add(entry);
            else if (aiMaps.get("dataAttributes").contains(key)) map.dataAttributes.add(entry);
            else map.other.add(entry);
        });
        if (otherArray != null) {
            otherArray.forEach((key, value) -> map.other.add(Collections.singletonMap(key, value)));
        }

        if (map.identifiers.size() != 1) {
            throw new IllegalArgumentException("The element string should contain exactly one primary identification key - it contained " + map.identifiers.size() + " " + map.identifiers);
        }

        Map<String, String> primaryIdentifierMap = map.identifiers.get(0);
        String primaryIdentifier = primaryIdentifierMap.keySet().iterator().next();
        verifySyntax(primaryIdentifier, primaryIdentifierMap.get(primaryIdentifier));
        verifyCheckDigit(primaryIdentifier, primaryIdentifierMap.get(primaryIdentifier));

        return map;
    }

    /**
     * Populates all the data tables from the GS1 specification.
     * This method loads AI data from JSON file and initializes other tables.
     */
    private void initializeData() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Load from classpath resource
            InputStream jsonStream = getClass().getResourceAsStream("/aitable.json");
            if (jsonStream == null) {
                throw new IOException("Could not find aitable.json in classpath");
            }
            List<Map<String, Object>> aiList = mapper.readValue(jsonStream,
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            for (Map<String, Object> aiMap : aiList) {
                String title = (String) aiMap.get("title");
                String label = (String) aiMap.get("label");
                String shortcode = (String) aiMap.get("shortcode");
                String ai = (String) aiMap.get("ai");
                String format = (String) aiMap.get("format");
                String type = (String) aiMap.get("type");
                boolean fixedLength = (boolean) aiMap.get("fixedLength");
                String checkDigit = (String) aiMap.get("checkDigit");
                String regex = (String) aiMap.get("regex");

                List<String> qualifiers = null;
                if (aiMap.get("qualifiers") != null) {
                    qualifiers = (List<String>) aiMap.get("qualifiers");
                }

                aitable.add(
                        ApplicationIdentifier.builder()
                                .title(title)
                                .label(label)
                                .shortcode(shortcode)
                                .ai(ai)
                                .format(format)
                                .type(type)
                                .fixedLength(fixedLength)
                                .checkDigit(checkDigit)
                                .regex(regex)
                                .qualifiers(qualifiers)
                                .build()
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load AI table from JSON file", e);
        }

        // --- tableF ---
        tableF.put("00", Collections.singletonList(new HashMap<String, String>() {{
            put("E", "N");
            put("L", "18");
        }}));
        tableF.put("01", Collections.singletonList(new HashMap<String, String>() {{
            put("E", "N");
            put("L", "14");
        }}));
        tableF.put("10", Collections.singletonList(new HashMap<String, String>() {{
            put("E", "X");
            put("M", "20");
        }}));
        tableF.put("17", Collections.singletonList(new HashMap<String, String>() {{
            put("E", "N");
            put("L", "6");
        }}));
        tableF.put("21", Collections.singletonList(new HashMap<String, String>() {{
            put("E", "X");
            put("M", "20");
        }}));
        tableF.put("414", Collections.singletonList(new HashMap<String, String>() {{
            put("E", "N");
            put("L", "13");
        }}));
        tableF.put("254", Collections.singletonList(new HashMap<String, String>() {{
            put("E", "X");
            put("M", "20");
        }}));
        tableF.put("8018", Collections.singletonList(new HashMap<String, String>() {{
            put("E", "N");
            put("L", "18");
        }}));
        tableF.put("8019", Collections.singletonList(new HashMap<String, String>() {{
            put("E", "N");
            put("M", "10");
        }}));
        tableF.put("253", Arrays.asList(new HashMap<String, String>() {{
            put("E", "N");
            put("L", "13");
        }}, new HashMap<String, String>() {{
            put("E", "X");
            put("M", "17");
        }}));
        tableF.put("255", Arrays.asList(new HashMap<String, String>() {{
            put("E", "N");
            put("L", "13");
        }}, new HashMap<String, String>() {{
            put("E", "N");
            put("M", "12");
        }}));

        // --- tableOpt ---
        tableOpt.put("0B", new String[]{"01", "10"});
        tableOpt.put("0C", new String[]{"01", "21"});
        tableOpt.put("0D", new String[]{"01", "17"});
        tableOpt.put("9C", new String[]{"8018", "8019"});
        tableOpt.put("9D", new String[]{"414", "254"});

        // --- tableS1 ---
        tableS1.put("01", new HashMap<String, Object>() {{
            put("requires", Arrays.asList("21", "235"));
        }});
        tableS1.put("00", new HashMap<String, Object>() {{
            put("requires", null);
        }});

        // --- pathSequenceConstraints ---
        pathSequenceConstraints.put("01", new String[]{"22", "10", "21"});
        pathSequenceConstraints.put("8006", new String[]{"22", "10", "21"});

        // --- Semantics tables ---
        stringSemantics.put("01", new String[]{"gs1:gtin", "schema:gtin"});
        stringSemantics.put("10", new String[]{"gs1:hasBatchLot"});
        stringSemantics.put("21", new String[]{"gs1:hasSerialNumber"});
        classSemantics.put("01", new String[]{"gs1:Product", "schema:Product"});
        classSemantics.put("414", new String[]{"gs1:Place", "schema:Place"});
        dateSemantics.put("17", new String[]{"gs1:expirationDate"});
    }

    //endregion
}
