package com.tororang.springai.knowledge.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmbeddedChunkTest {

    @Test
    void chunk가_null이면_예외가_발생한다() {
        EmbeddingVector embedding = new EmbeddingVector(List.of(0.1f, 0.2f));

        assertThatThrownBy(() -> new EmbeddedChunk(null, embedding))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void embedding이_null이면_예외가_발생한다() {
        DocumentChunk chunk = new DocumentChunk(UUID.randomUUID(), "청크 내용", 0);

        assertThatThrownBy(() -> new EmbeddedChunk(chunk, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 유효한_값이면_정상적으로_생성된다() {
        DocumentChunk chunk = new DocumentChunk(UUID.randomUUID(), "청크 내용", 0);
        EmbeddingVector embedding = new EmbeddingVector(List.of(0.1f, 0.2f));

        EmbeddedChunk embeddedChunk = new EmbeddedChunk(chunk, embedding);

        assertThat(embeddedChunk.chunk()).isEqualTo(chunk);
        assertThat(embeddedChunk.embedding()).isEqualTo(embedding);
    }
}
