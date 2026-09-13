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
