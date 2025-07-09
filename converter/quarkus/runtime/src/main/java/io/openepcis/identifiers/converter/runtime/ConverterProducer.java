package io.openepcis.identifiers.converter.runtime;

import io.openepcis.digitallink.utils.DefaultGCPLengthProvider;
import io.openepcis.identifiers.converter.Converter;
import io.openepcis.identifiers.converter.EventVocabularyFormatter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class ConverterProducer {

    @Produces
    public DefaultGCPLengthProvider createDefaultGCPLengthProvider() {
        return DefaultGCPLengthProvider.getInstance();
    }

    @Produces
    public Converter createConverter() {
        return new Converter();
    }

    @Produces
    public EventVocabularyFormatter createEventVocabularyFormatter() {
        return new EventVocabularyFormatter();
    }
}
