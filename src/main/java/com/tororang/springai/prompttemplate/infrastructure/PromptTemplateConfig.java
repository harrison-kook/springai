package com.tororang.springai.prompttemplate.infrastructure;

import com.tororang.springai.prompttemplate.application.RegisterPromptTemplateUseCase;
import com.tororang.springai.prompttemplate.application.RenderPromptTemplateUseCase;
import com.tororang.springai.prompttemplate.domain.PromptTemplateRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PromptTemplateConfig {

    @Bean
    public PromptTemplateRepository promptTemplateRepository() {
        return new InMemoryPromptTemplateRepository();
    }

    @Bean
    public RegisterPromptTemplateUseCase registerPromptTemplateUseCase(
            PromptTemplateRepository promptTemplateRepository) {
        return new RegisterPromptTemplateUseCase(promptTemplateRepository);
    }

    @Bean
    public RenderPromptTemplateUseCase renderPromptTemplateUseCase(PromptTemplateRepository promptTemplateRepository) {
        return new RenderPromptTemplateUseCase(promptTemplateRepository);
    }
}
