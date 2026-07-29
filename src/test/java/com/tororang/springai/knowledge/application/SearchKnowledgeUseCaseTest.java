package com.tororang.springai.knowledge.application;

import com.tororang.springai.knowledge.application.dto.RetrievedChunkResult;
import com.tororang.springai.knowledge.domain.DocumentChunk;
import com.tororang.springai.knowledge.domain.EmbeddingGenerator;
import com.tororang.springai.knowledge.domain.EmbeddingVector;
import com.tororang.springai.knowledge.domain.KnowledgeRepository;
import com.tororang.springai.knowledge.domain.RetrievedChunk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchKnowledgeUseCaseTest {

    @Mock
    private EmbeddingGenerator embeddingGenerator;

    @Mock
    private KnowledgeRepository knowledgeRepository;

    private SearchKnowledgeUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SearchKnowledgeUseCase(embeddingGenerator, knowledgeRepository, 3);
    }

    @Test
    void 쿼리로_검색하면_임베딩을_생성한_뒤_저장소에서_유사한_청크를_조회한다() {
        EmbeddingVector queryVector = new EmbeddingVector(List.of(0.1f, 0.2f));
        when(embeddingGenerator.generate("질문")).thenReturn(queryVector);
        DocumentChunk chunk = new DocumentChunk(UUID.randomUUID(), "관련 내용", 0);
        when(knowledgeRepository.search(queryVector, 3)).thenReturn(List.of(new RetrievedChunk(chunk, 0.9)));

        List<RetrievedChunkResult> results = useCase.search("질문");

        assertThat(results).containsExactly(new RetrievedChunkResult("관련 내용", 0.9));
    }

    @Test
    void 검색_결과가_없으면_빈_리스트를_반환한다() {
        EmbeddingVector queryVector = new EmbeddingVector(List.of(0.1f, 0.2f));
        when(embeddingGenerator.generate("질문")).thenReturn(queryVector);
        when(knowledgeRepository.search(queryVector, 3)).thenReturn(List.of());

        List<RetrievedChunkResult> results = useCase.search("질문");

        assertThat(results).isEmpty();
    }

    @Test
    void 저장소_조회에는_설정된_topK가_그대로_전달된다() {
        EmbeddingVector queryVector = new EmbeddingVector(List.of(0.1f, 0.2f));
        when(embeddingGenerator.generate("질문")).thenReturn(queryVector);
        when(knowledgeRepository.search(queryVector, 3)).thenReturn(List.of());

        useCase.search("질문");

        verify(knowledgeRepository).search(queryVector, 3);
    }
}
