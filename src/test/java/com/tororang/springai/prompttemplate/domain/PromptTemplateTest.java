package com.tororang.springai.prompttemplate.domain;

import com.tororang.springai.prompttemplate.domain.exception.MissingTemplateVariableException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PromptTemplateTest {

    @Test
    void 이름이_빈_문자열이면_예외가_발생한다() {
        assertThatThrownBy(() -> PromptTemplate.create("", "안녕 {{name}}"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이름이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> PromptTemplate.create(null, "안녕 {{name}}"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void content가_빈_문자열이면_예외가_발생한다() {
        assertThatThrownBy(() -> PromptTemplate.create("greeting", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void content가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> PromptTemplate.create("greeting", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이름과_content가_유효하면_정상적으로_생성된다() {
        PromptTemplate template = PromptTemplate.create("greeting", "안녕 {{name}}");

        assertThat(template.id()).isNotNull();
        assertThat(template.name()).isEqualTo("greeting");
        assertThat(template.content()).isEqualTo("안녕 {{name}}");
    }

    @Test
    void placeholder가_없으면_원본_문자열이_그대로_반환된다() {
        PromptTemplate template = PromptTemplate.create("plain", "안녕하세요");

        String rendered = template.render(Map.of());

        assertThat(rendered).isEqualTo("안녕하세요");
    }

    @Test
    void 단일_placeholder를_변수로_치환하여_렌더링한다() {
        PromptTemplate template = PromptTemplate.create("greeting", "안녕 {{name}}님");

        String rendered = template.render(Map.of("name", "철수"));

        assertThat(rendered).isEqualTo("안녕 철수님");
    }

    @Test
    void 여러_placeholder를_모두_치환하여_렌더링한다() {
        PromptTemplate template = PromptTemplate.create("greeting", "{{greeting}} {{name}}님");

        String rendered = template.render(Map.of("greeting", "안녕하세요", "name", "철수"));

        assertThat(rendered).isEqualTo("안녕하세요 철수님");
    }

    @Test
    void 필요한_변수가_누락되면_예외가_발생한다() {
        PromptTemplate template = PromptTemplate.create("greeting", "안녕 {{name}}님");

        assertThatThrownBy(() -> template.render(Map.of()))
                .isInstanceOf(MissingTemplateVariableException.class);
    }
}
