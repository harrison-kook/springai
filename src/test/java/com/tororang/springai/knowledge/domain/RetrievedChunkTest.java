package com.tororang.springai.knowledge.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RetrievedChunkTest {

    @Test
    void chunk가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> new RetrievedChunk(null, 0.9))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 유효한_값이면_정상적으로_생성된다() {
        DocumentChunk chunk = new DocumentChunk(UUID.randomUUID(), "청크 내용", 0);

        RetrievedChunk retrievedChunk = new RetrievedChunk(chunk, 0.9);

        assertThat(retrievedChunk.chunk()).isEqualTo(chunk);
        assertThat(retrievedChunk.score()).isEqualTo(0.9);
    }
}
