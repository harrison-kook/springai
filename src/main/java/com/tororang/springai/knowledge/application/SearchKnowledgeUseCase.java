package com.tororang.springai.knowledge.application;

import com.tororang.springai.knowledge.application.dto.RetrievedChunkResult;
import com.tororang.springai.knowledge.domain.EmbeddingGenerator;
import com.tororang.springai.knowledge.domain.EmbeddingVector;
import com.tororang.springai.knowledge.domain.KnowledgeRepository;
import com.tororang.springai.knowledge.domain.RetrievedChunk;

import java.util.List;

public class SearchKnowledgeUseCase {

    private final EmbeddingGenerator embeddingGenerator;
    private final KnowledgeRepository knowledgeRepository;
    private final int topK;

    public SearchKnowledgeUseCase(EmbeddingGenerator embeddingGenerator, KnowledgeRepository knowledgeRepository,
            int topK) {
        this.embeddingGenerator = embeddingGenerator;
        this.knowledgeRepository = knowledgeRepository;
        this.topK = topK;
    }

    public List<RetrievedChunkResult> search(String query) {
        EmbeddingVector queryVector = embeddingGenerator.generate(query);
        List<RetrievedChunk> retrieved = knowledgeRepository.search(queryVector, topK);

        return retrieved.stream()
                .map(chunk -> new RetrievedChunkResult(chunk.chunk().content(), chunk.score()))
                .toList();
    }
}
