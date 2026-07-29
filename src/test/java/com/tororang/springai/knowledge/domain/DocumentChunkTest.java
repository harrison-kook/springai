package com.tororang.springai.knowledge.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentChunkTest {

    @Test
    void documentId가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> new DocumentChunk(null, "청크 내용", 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void content가_빈_문자열이면_예외가_발생한다() {
        assertThatThrownBy(() -> new DocumentChunk(UUID.randomUUID(), "", 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void content가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> new DocumentChunk(UUID.randomUUID(), null, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void order가_음수이면_예외가_발생한다() {
        assertThatThrownBy(() -> new DocumentChunk(UUID.randomUUID(), "청크 내용", -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 유효한_값이면_정상적으로_생성된다() {
        UUID documentId = UUID.randomUUID();

        DocumentChunk chunk = new DocumentChunk(documentId, "청크 내용", 0);

        assertThat(chunk.documentId()).isEqualTo(documentId);
        assertThat(chunk.content()).isEqualTo("청크 내용");
        assertThat(chunk.order()).isEqualTo(0);
    }

    @Test
    void documentId_content_order가_같으면_동등하다() {
        UUID documentId = UUID.randomUUID();

        DocumentChunk chunk1 = new DocumentChunk(documentId, "청크 내용", 0);
        DocumentChunk chunk2 = new DocumentChunk(documentId, "청크 내용", 0);

        assertThat(chunk1).isEqualTo(chunk2);
        assertThat(chunk1.hashCode()).isEqualTo(chunk2.hashCode());
    }
}
