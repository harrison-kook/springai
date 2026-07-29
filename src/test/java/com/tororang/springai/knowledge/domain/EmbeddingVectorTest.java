package com.tororang.springai.knowledge.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmbeddingVectorTest {

    @Test
    void values가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> new EmbeddingVector(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void values가_비어있으면_예외가_발생한다() {
        assertThatThrownBy(() -> new EmbeddingVector(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 유효한_값이면_정상적으로_생성된다() {
        EmbeddingVector vector = new EmbeddingVector(List.of(0.1f, 0.2f, 0.3f));

        assertThat(vector.values()).containsExactly(0.1f, 0.2f, 0.3f);
    }

    @Test
    void values가_같으면_동등하다() {
        EmbeddingVector vector1 = new EmbeddingVector(List.of(0.1f, 0.2f));
        EmbeddingVector vector2 = new EmbeddingVector(List.of(0.1f, 0.2f));

        assertThat(vector1).isEqualTo(vector2);
        assertThat(vector1.hashCode()).isEqualTo(vector2.hashCode());
    }

    @Test
    void 생성_이후_원본_리스트를_변경해도_영향을_받지_않는다() {
        List<Float> original = new ArrayList<>(List.of(0.1f, 0.2f));
        EmbeddingVector vector = new EmbeddingVector(original);

        original.add(0.3f);

        assertThat(vector.values()).containsExactly(0.1f, 0.2f);
    }

    @Test
    void values는_수정할_수_없다() {
        EmbeddingVector vector = new EmbeddingVector(List.of(0.1f, 0.2f));

        assertThatThrownBy(() -> vector.values().add(0.3f))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
