package com.tororang.springai.knowledge.infrastructure;

import com.tororang.springai.knowledge.domain.EmbeddingVector;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class SpringAiEmbeddingGeneratorIntegrationTest {

    private final OllamaApi ollamaApi = OllamaApi.builder()
            .baseUrl(System.getProperty("test.ollama.base-url", "http://localhost:11434"))
            .build();

    private final OllamaEmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
            .ollamaApi(ollamaApi)
            .options(OllamaEmbeddingOptions.builder().model("nomic-embed-text").build())
            .build();

    private final SpringAiEmbeddingGenerator generator = new SpringAiEmbeddingGenerator(embeddingModel);

    @Test
    void 실제_Ollama로_단일_텍스트를_임베딩하면_768차원_벡터가_반환된다() {
        EmbeddingVector vector = generator.generate("고양이는 귀엽다");

        assertThat(vector.values()).hasSize(768);
    }

    @Test
    void 실제_Ollama로_여러_텍스트를_배치_임베딩하면_순서대로_반환된다() {
        List<EmbeddingVector> vectors = generator.generateAll(List.of("고양이", "강아지"));

        assertThat(vectors).hasSize(2);
        assertThat(vectors.get(0).values()).hasSize(768);
        assertThat(vectors.get(1).values()).hasSize(768);
    }
}
