package com.tororang.springai.prompttemplate.application;

import com.tororang.springai.prompttemplate.application.dto.RenderPromptTemplateCommand;
import com.tororang.springai.prompttemplate.domain.PromptTemplate;
import com.tororang.springai.prompttemplate.domain.PromptTemplateRepository;
import com.tororang.springai.prompttemplate.domain.exception.MissingTemplateVariableException;
import com.tororang.springai.prompttemplate.domain.exception.PromptTemplateNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RenderPromptTemplateUseCaseTest {

    @Mock
    private PromptTemplateRepository promptTemplateRepository;

    @Test
    void 존재하는_템플릿_이름과_변수로_렌더링하면_치환된_문자열이_반환된다() {
        PromptTemplate template = PromptTemplate.create("greeting", "안녕 {{name}}님");
        when(promptTemplateRepository.findByName("greeting")).thenReturn(Optional.of(template));
        RenderPromptTemplateUseCase useCase = new RenderPromptTemplateUseCase(promptTemplateRepository);

        String rendered = useCase.render(new RenderPromptTemplateCommand("greeting", Map.of("name", "철수")));

        assertThat(rendered).isEqualTo("안녕 철수님");
    }

    @Test
    void 존재하지_않는_템플릿_이름으로_요청하면_예외가_발생한다() {
        when(promptTemplateRepository.findByName("unknown")).thenReturn(Optional.empty());
        RenderPromptTemplateUseCase useCase = new RenderPromptTemplateUseCase(promptTemplateRepository);

        assertThatThrownBy(() -> useCase.render(new RenderPromptTemplateCommand("unknown", Map.of())))
                .isInstanceOf(PromptTemplateNotFoundException.class);
    }

    @Test
    void 필요한_변수가_누락되면_예외가_전파된다() {
        PromptTemplate template = PromptTemplate.create("greeting", "안녕 {{name}}님");
        when(promptTemplateRepository.findByName("greeting")).thenReturn(Optional.of(template));
        RenderPromptTemplateUseCase useCase = new RenderPromptTemplateUseCase(promptTemplateRepository);

        assertThatThrownBy(() -> useCase.render(new RenderPromptTemplateCommand("greeting", Map.of())))
                .isInstanceOf(MissingTemplateVariableException.class);
    }
}
