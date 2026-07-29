package com.tororang.springai.knowledge.application;

import com.tororang.springai.knowledge.application.dto.IndexDocumentCommand;
import com.tororang.springai.knowledge.domain.DocumentChunker;
import com.tororang.springai.knowledge.domain.EmbeddedChunk;
import com.tororang.springai.knowledge.domain.EmbeddingGenerator;
import com.tororang.springai.knowledge.domain.EmbeddingVector;
import com.tororang.springai.knowledge.domain.KnowledgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndexDocumentUseCaseTest {

    @Mock
    private EmbeddingGenerator embeddingGenerator;

    @Mock
    private KnowledgeRepository knowledgeRepository;

    private IndexDocumentUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new IndexDocumentUseCase(new DocumentChunker(), embeddingGenerator, knowledgeRepository, 5);
    }

    @Test
    void 문서를_색인하면_청크로_분할되어_임베딩과_함께_저장된다() {
        EmbeddingVector vector1 = new EmbeddingVector(List.of(0.1f));
        EmbeddingVector vector2 = new EmbeddingVector(List.of(0.2f));
        when(embeddingGenerator.generateAll(anyList())).thenReturn(List.of(vector1, vector2));

        useCase.index(new IndexDocumentCommand("제목", "1234567890"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<EmbeddedChunk>> captor = ArgumentCaptor.forClass(List.class);
        verify(knowledgeRepository).save(captor.capture());
        List<EmbeddedChunk> saved = captor.getValue();
        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).chunk().content()).isEqualTo("12345");
        assertThat(saved.get(0).embedding()).isEqualTo(vector1);
        assertThat(saved.get(1).chunk().content()).isEqualTo("67890");
        assertThat(saved.get(1).embedding()).isEqualTo(vector2);
    }

    @Test
    void 임베딩_생성기에는_청크_내용_목록이_순서대로_전달된다() {
        when(embeddingGenerator.generateAll(anyList()))
                .thenReturn(List.of(new EmbeddingVector(List.of(0.1f)), new EmbeddingVector(List.of(0.2f))));

        useCase.index(new IndexDocumentCommand("제목", "1234567890"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(embeddingGenerator).generateAll(captor.capture());
        assertThat(captor.getValue()).containsExactly("12345", "67890");
    }

    @Test
    void 색인된_문서의_id가_반환된다() {
        when(embeddingGenerator.generateAll(anyList()))
                .thenReturn(List.of(new EmbeddingVector(List.of(0.1f)), new EmbeddingVector(List.of(0.2f))));

        UUID documentId = useCase.index(new IndexDocumentCommand("제목", "1234567890"));

        assertThat(documentId).isNotNull();
    }
}
