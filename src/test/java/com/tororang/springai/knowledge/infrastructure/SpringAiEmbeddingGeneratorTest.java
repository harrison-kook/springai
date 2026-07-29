package com.tororang.springai.knowledge.infrastructure;

import com.tororang.springai.knowledge.domain.EmbeddingVector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.embedding.EmbeddingModel;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringAiEmbeddingGeneratorTest {

    @Mock
    private EmbeddingModel embeddingModel;

    @Test
    void 단일_텍스트를_임베딩하면_EmbeddingVector로_변환된다() {
        when(embeddingModel.embed("안녕하세요")).thenReturn(new float[] { 0.1f, 0.2f });
        SpringAiEmbeddingGenerator generator = new SpringAiEmbeddingGenerator(embeddingModel);

        EmbeddingVector vector = generator.generate("안녕하세요");

        assertThat(vector.values()).containsExactly(0.1f, 0.2f);
    }

    @Test
    void 여러_텍스트를_배치로_임베딩하면_순서대로_변환된다() {
        when(embeddingModel.embed(List.of("첫번째", "두번째")))
                .thenReturn(List.of(new float[] { 0.1f }, new float[] { 0.2f }));
        SpringAiEmbeddingGenerator generator = new SpringAiEmbeddingGenerator(embeddingModel);

        List<EmbeddingVector> vectors = generator.generateAll(List.of("첫번째", "두번째"));

        assertThat(vectors).containsExactly(
                new EmbeddingVector(List.of(0.1f)),
                new EmbeddingVector(List.of(0.2f)));
    }
}
