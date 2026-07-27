package com.tororang.springai.prompttemplate.application;

import com.tororang.springai.prompttemplate.application.dto.RenderPromptTemplateCommand;
import com.tororang.springai.prompttemplate.domain.PromptTemplate;
import com.tororang.springai.prompttemplate.domain.PromptTemplateRepository;
import com.tororang.springai.prompttemplate.domain.exception.PromptTemplateNotFoundException;

public class RenderPromptTemplateUseCase {

    private final PromptTemplateRepository promptTemplateRepository;

    public RenderPromptTemplateUseCase(PromptTemplateRepository promptTemplateRepository) {
        this.promptTemplateRepository = promptTemplateRepository;
    }

    public String render(RenderPromptTemplateCommand command) {
        PromptTemplate promptTemplate = promptTemplateRepository.findByName(command.templateName())
                .orElseThrow(() -> new PromptTemplateNotFoundException(
                        "template not found: " + command.templateName()));

        return promptTemplate.render(command.variables());
    }
}
