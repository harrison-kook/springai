package com.tororang.springai.prompttemplate.application;

import com.tororang.springai.prompttemplate.application.dto.RegisterPromptTemplateCommand;
import com.tororang.springai.prompttemplate.domain.PromptTemplate;
import com.tororang.springai.prompttemplate.domain.PromptTemplateRepository;
import com.tororang.springai.prompttemplate.domain.exception.DuplicateTemplateNameException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterPromptTemplateUseCaseTest {

    @Mock
    private PromptTemplateRepository promptTemplateRepository;

    @Test
    void 새_템플릿을_등록하면_저장소에_저장되고_id가_반환된다() {
        when(promptTemplateRepository.existsByName("greeting")).thenReturn(false);
        when(promptTemplateRepository.save(any(PromptTemplate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        RegisterPromptTemplateUseCase useCase = new RegisterPromptTemplateUseCase(promptTemplateRepository);

        UUID id = useCase.register(new RegisterPromptTemplateCommand("greeting", "안녕 {{name}}"));

        ArgumentCaptor<PromptTemplate> captor = ArgumentCaptor.forClass(PromptTemplate.class);
        verify(promptTemplateRepository).save(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
        assertThat(captor.getValue().name()).isEqualTo("greeting");
    }

    @Test
    void 이미_존재하는_이름으로_등록하면_예외가_발생하고_저장되지_않는다() {
        when(promptTemplateRepository.existsByName("greeting")).thenReturn(true);
        RegisterPromptTemplateUseCase useCase = new RegisterPromptTemplateUseCase(promptTemplateRepository);

        assertThatThrownBy(() -> useCase.register(new RegisterPromptTemplateCommand("greeting", "안녕 {{name}}")))
                .isInstanceOf(DuplicateTemplateNameException.class);

        verify(promptTemplateRepository, never()).save(any());
    }
}
