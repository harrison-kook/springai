package com.tororang.springai.conversation.infrastructure;

import com.tororang.springai.conversation.domain.KnowledgeRetriever;
import com.tororang.springai.knowledge.application.SearchKnowledgeUseCase;
import com.tororang.springai.knowledge.application.dto.RetrievedChunkResult;

import java.util.List;

public class KnowledgeSearchRetriever implements KnowledgeRetriever {

    private final SearchKnowledgeUseCase searchKnowledgeUseCase;

    public KnowledgeSearchRetriever(SearchKnowledgeUseCase searchKnowledgeUseCase) {
        this.searchKnowledgeUseCase = searchKnowledgeUseCase;
    }

    @Override
    public List<String> retrieve(String query) {
        return searchKnowledgeUseCase.search(query).stream()
                .map(RetrievedChunkResult::content)
                .toList();
    }
}
