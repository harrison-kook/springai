package com.tororang.springai.conversation.infrastructure;

import com.tororang.springai.knowledge.application.SearchKnowledgeUseCase;
import com.tororang.springai.knowledge.application.dto.RetrievedChunkResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KnowledgeSearchRetrieverTest {

    @Mock
    private SearchKnowledgeUseCase searchKnowledgeUseCase;

    @Test
    void 검색_결과의_content만_추출해서_반환한다() {
        when(searchKnowledgeUseCase.search("질문"))
                .thenReturn(List.of(new RetrievedChunkResult("청크1", 0.9), new RetrievedChunkResult("청크2", 0.8)));
        KnowledgeSearchRetriever retriever = new KnowledgeSearchRetriever(searchKnowledgeUseCase);

        List<String> context = retriever.retrieve("질문");

        assertThat(context).containsExactly("청크1", "청크2");
    }

    @Test
    void 검색_결과가_없으면_빈_리스트를_반환한다() {
        when(searchKnowledgeUseCase.search("질문")).thenReturn(List.of());
        KnowledgeSearchRetriever retriever = new KnowledgeSearchRetriever(searchKnowledgeUseCase);

        List<String> context = retriever.retrieve("질문");

        assertThat(context).isEmpty();
    }

    @Test
    void 전달받은_쿼리가_그대로_검색에_사용된다() {
        when(searchKnowledgeUseCase.search("질문")).thenReturn(List.of());
        KnowledgeSearchRetriever retriever = new KnowledgeSearchRetriever(searchKnowledgeUseCase);

        retriever.retrieve("질문");

        verify(searchKnowledgeUseCase).search("질문");
    }
}
