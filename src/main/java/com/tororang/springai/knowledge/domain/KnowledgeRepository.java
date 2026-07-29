package com.tororang.springai.knowledge.domain;

import java.util.List;

public interface KnowledgeRepository {

    void save(List<EmbeddedChunk> chunks);

    List<RetrievedChunk> search(EmbeddingVector queryVector, int topK);
}
