package com.tororang.springai.knowledge.presentation;

import com.tororang.springai.knowledge.application.IndexDocumentUseCase;
import com.tororang.springai.knowledge.application.SearchKnowledgeUseCase;
import com.tororang.springai.knowledge.application.dto.IndexDocumentCommand;
import com.tororang.springai.knowledge.application.dto.RetrievedChunkResult;
import com.tororang.springai.knowledge.presentation.dto.IndexDocumentRequest;
import com.tororang.springai.knowledge.presentation.dto.IndexDocumentResponse;
import com.tororang.springai.knowledge.presentation.dto.SearchKnowledgeRequest;
import com.tororang.springai.knowledge.presentation.dto.SearchKnowledgeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final IndexDocumentUseCase indexDocumentUseCase;
    private final SearchKnowledgeUseCase searchKnowledgeUseCase;

    public KnowledgeController(IndexDocumentUseCase indexDocumentUseCase,
            SearchKnowledgeUseCase searchKnowledgeUseCase) {
        this.indexDocumentUseCase = indexDocumentUseCase;
        this.searchKnowledgeUseCase = searchKnowledgeUseCase;
    }

    @PostMapping("/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public IndexDocumentResponse indexDocument(@RequestBody IndexDocumentRequest request) {
        if (request.title() == null || request.title().isBlank()
                || request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("title and content must not be blank");
        }

        return new IndexDocumentResponse(
                indexDocumentUseCase.index(new IndexDocumentCommand(request.title(), request.content())));
    }

    @PostMapping("/search")
    public SearchKnowledgeResponse search(@RequestBody SearchKnowledgeRequest request) {
        if (request.query() == null || request.query().isBlank()) {
            throw new IllegalArgumentException("query must not be blank");
        }

        List<RetrievedChunkResult> results = searchKnowledgeUseCase.search(request.query());
        return SearchKnowledgeResponse.from(results);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgument() {
    }
}
