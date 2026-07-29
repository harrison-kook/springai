package com.tororang.springai.knowledge.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentChunkerTest {

    private final DocumentChunker chunker = new DocumentChunker();

    @Test
    void chunkSize가_0이면_예외가_발생한다() {
        Document document = Document.create("제목", "본문 내용");

        assertThatThrownBy(() -> chunker.chunk(document, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void chunkSize가_음수이면_예외가_발생한다() {
        Document document = Document.create("제목", "본문 내용");

        assertThatThrownBy(() -> chunker.chunk(document, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void content가_chunkSize보다_짧으면_청크가_하나만_생성된다() {
        Document document = Document.create("제목", "짧은 내용");

        List<DocumentChunk> chunks = chunker.chunk(document, 100);

        assertThat(chunks).hasSize(1);
        assertThat(chunks.get(0).content()).isEqualTo("짧은 내용");
        assertThat(chunks.get(0).order()).isEqualTo(0);
        assertThat(chunks.get(0).documentId()).isEqualTo(document.id());
    }

    @Test
    void content가_chunkSize로_정확히_나누어떨어지면_그_개수만큼_청크가_생성된다() {
        Document document = Document.create("제목", "1234567890");

        List<DocumentChunk> chunks = chunker.chunk(document, 5);

        assertThat(chunks).hasSize(2);
        assertThat(chunks.get(0).content()).isEqualTo("12345");
        assertThat(chunks.get(1).content()).isEqualTo("67890");
    }

    @Test
    void content가_chunkSize로_나누어떨어지지_않으면_마지막_청크는_나머지만큼_생성된다() {
        Document document = Document.create("제목", "1234567");

        List<DocumentChunk> chunks = chunker.chunk(document, 5);

        assertThat(chunks).hasSize(2);
        assertThat(chunks.get(0).content()).isEqualTo("12345");
        assertThat(chunks.get(1).content()).isEqualTo("67");
    }

    @Test
    void 청크는_순서대로_order가_증가한다() {
        Document document = Document.create("제목", "123456789012");

        List<DocumentChunk> chunks = chunker.chunk(document, 5);

        assertThat(chunks).extracting(DocumentChunk::order)
                .containsExactly(0, 1, 2);
    }
}
