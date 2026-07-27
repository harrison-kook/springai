package com.tororang.springai.prompttemplate.infrastructure;

import com.tororang.springai.prompttemplate.domain.PromptTemplate;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryPromptTemplateRepositoryTest {

    private final InMemoryPromptTemplateRepository repository = new InMemoryPromptTemplateRepository();

    @Test
    void save_후_findById로_동일한_템플릿을_조회할_수_있다() {
        PromptTemplate template = PromptTemplate.create("greeting", "안녕 {{name}}");

        repository.save(template);

        assertThat(repository.findById(template.id())).contains(template);
    }

    @Test
    void 존재하지_않는_id로_조회하면_빈_Optional을_반환한다() {
        Optional<PromptTemplate> found = repository.findById(UUID.randomUUID());

        assertThat(found).isEmpty();
    }

    @Test
    void save_후_findByName으로_동일한_템플릿을_조회할_수_있다() {
        PromptTemplate template = PromptTemplate.create("greeting", "안녕 {{name}}");

        repository.save(template);

        assertThat(repository.findByName("greeting")).contains(template);
    }

    @Test
    void 존재하지_않는_이름으로_조회하면_빈_Optional을_반환한다() {
        assertThat(repository.findByName("unknown")).isEmpty();
    }

    @Test
    void 저장된_이름은_존재하고_저장되지_않은_이름은_존재하지_않는다() {
        PromptTemplate template = PromptTemplate.create("greeting", "안녕 {{name}}");
        repository.save(template);

        assertThat(repository.existsByName("greeting")).isTrue();
        assertThat(repository.existsByName("unknown")).isFalse();
    }
}
