package com.tororang.springai.prompttemplate.infrastructure;

import com.tororang.springai.prompttemplate.domain.PromptTemplate;
import com.tororang.springai.prompttemplate.domain.PromptTemplateRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPromptTemplateRepository implements PromptTemplateRepository {

    private final Map<UUID, PromptTemplate> store = new ConcurrentHashMap<>();

    @Override
    public PromptTemplate save(PromptTemplate promptTemplate) {
        store.put(promptTemplate.id(), promptTemplate);
        return promptTemplate;
    }

    @Override
    public Optional<PromptTemplate> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<PromptTemplate> findByName(String name) {
        return store.values().stream()
                .filter(template -> template.name().equals(name))
                .findFirst();
    }

    @Override
    public boolean existsByName(String name) {
        return findByName(name).isPresent();
    }
}
