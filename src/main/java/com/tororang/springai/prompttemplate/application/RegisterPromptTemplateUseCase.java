package com.tororang.springai.prompttemplate.application;

import com.tororang.springai.prompttemplate.application.dto.RegisterPromptTemplateCommand;
import com.tororang.springai.prompttemplate.domain.PromptTemplate;
import com.tororang.springai.prompttemplate.domain.PromptTemplateRepository;
import com.tororang.springai.prompttemplate.domain.exception.DuplicateTemplateNameException;

import java.util.UUID;

public class RegisterPromptTemplateUseCase {

    private final PromptTemplateRepository promptTemplateRepository;

    public RegisterPromptTemplateUseCase(PromptTemplateRepository promptTemplateRepository) {
        this.promptTemplateRepository = promptTemplateRepository;
    }

    public UUID register(RegisterPromptTemplateCommand command) {
        if (promptTemplateRepository.existsByName(command.name())) {
            throw new DuplicateTemplateNameException("template name already exists: " + command.name());
        }

        PromptTemplate promptTemplate = PromptTemplate.create(command.name(), command.content());
        promptTemplateRepository.save(promptTemplate);
        return promptTemplate.id();
    }
}
