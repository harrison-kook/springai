package com.tororang.springai.prompttemplate.domain;

import java.util.Optional;
import java.util.UUID;

public interface PromptTemplateRepository {

    PromptTemplate save(PromptTemplate promptTemplate);

    Optional<PromptTemplate> findById(UUID id);

    Optional<PromptTemplate> findByName(String name);

    boolean existsByName(String name);
}
