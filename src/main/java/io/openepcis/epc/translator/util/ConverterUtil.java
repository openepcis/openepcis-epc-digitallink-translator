package io.openepcis.epc.translator.util;

import io.openepcis.epc.translator.Converter;
import io.openepcis.epc.translator.exception.ValidationException;
import java.util.Map;

// Class to use the Converter method in a static way.
public class ConverterUtil {

  private static final Converter converter;

  static {
    converter = new Converter();
  }

  // Check through each class and find URN belongs to which particular class
  public static String toURI(final String urn) throws ValidationException {
    return converter.toURI(urn);
  }

  // Check through each class and find DL URI belongs to which particular class
  public static Map<String, String> toURN(final String dlURI, final int gcpLength)
      throws ValidationException {
    return converter.toURN(dlURI, gcpLength);
  }

  // Check through each class and find DL URI belongs to which particular class
  public static Map<String, String> toURN(final String dlURI) throws ValidationException {
    return converter.toURN(dlURI);
  }

  // Check through each class and find URN belongs to which particular class
  public static String toURIForClassLevelIdentifier(final String urn) throws ValidationException {
    return converter.toURIForClassLevelIdentifier(urn);
  }

  // Check through each class and find DL URI belongs to which particular class
  public static Map<String, String> toURNForClassLevelIdentifier(final String dlURI)
      throws ValidationException {
    return converter.toURNForClassLevelIdentifier(dlURI);
  }

  // Check through each class and find DL URI belongs to which particular class
  public static Map<String, String> toURNForClassLevelIdentifier(
      final String dlURI, final int gcpLength) throws ValidationException {
    return converter.toURNForClassLevelIdentifier(dlURI, gcpLength);
  }

  // Convert the CBV URN formatted vocabularies into WebURI vocabulary. Used during event hash
  // generator.
  public static String toWebURIVocabulary(final String urnVocabulary) {
    return converter.toWebURIVocabulary(urnVocabulary);
  }

  // Convert the CBV WebURI formatted vocabularies into URN vocabulary. Used during JSON -> XML.
  public static String toUrnVocabulary(final String webUriVocabulary) {
    return converter.toUrnVocabulary(webUriVocabulary);
  }

  // Convert the CBV URN/WebURI formatted vocabularies into BareString vocabulary. Used during XML
  // -> JSON conversion.
  public static String toBareStringVocabulary(final String eventVocabulary) {
    return converter.toBareStringVocabulary(eventVocabulary);
  }

  // Convert bareString values to CBV formatted vocabularies. Used during JSON -> XML conversion.
  public static String toCbvVocabulary(
      final String bareString, final String fieldName, final String format) {
    return converter.toCbvVocabulary(bareString, fieldName, format);
  }
}