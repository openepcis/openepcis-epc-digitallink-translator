package io.openepcis.digitallink.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.openepcis.digitallink.model.ApplicationIdentifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Getter
@ApplicationScoped
public class AiEntries {

    private static final Map<String, ApplicationIdentifier> aiEntriesMap;

    // Static initializer to load the AI entries only once
    static {
        aiEntriesMap = initializeAiEntries(new ObjectMapper());
    }

    private static Map<String, ApplicationIdentifier> initializeAiEntries(ObjectMapper objectMapper) {
        try (InputStream inputStream = AiEntries.class.getResourceAsStream("/aitable.json")) {
            if (Objects.isNull(inputStream)) {
                // log.warn("AI entries configuration file not found");
                return Collections.emptyMap();
            }

            final List<ApplicationIdentifier> entries = objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ApplicationIdentifier.class));

            final Map<String, ApplicationIdentifier> entriesMap = new HashMap<>(entries.size() * 2);

            entries.forEach(entry -> {
                Optional.ofNullable(entry.getAi()).ifPresent(k -> entriesMap.put(k, entry));
                Optional.ofNullable(entry.getShortcode()).ifPresent(k -> entriesMap.put(k, entry));
            });

            // log.info("Loaded {} AI entries into map with {} keys", entries.size(), entriesMap.size());
            return Collections.unmodifiableMap(entriesMap);

        } catch (IOException e) {
            //   log.error("Failed to load AI entries from JSON", e);
            return Collections.emptyMap();
        }
    }

    public ApplicationIdentifier getEntry(String key) {
        return aiEntriesMap.get(key);
    }

}
