package com.tororang.springai.knowledge.presentation;

import com.tororang.springai.knowledge.application.IndexDocumentFileUseCase;
import com.tororang.springai.knowledge.application.IndexDocumentUseCase;
import com.tororang.springai.knowledge.application.SearchKnowledgeUseCase;
import com.tororang.springai.knowledge.application.dto.IndexDocumentCommand;
import com.tororang.springai.knowledge.application.dto.IndexDocumentFileCommand;
import com.tororang.springai.knowledge.application.dto.RetrievedChunkResult;
import com.tororang.springai.knowledge.presentation.dto.IndexDocumentRequest;
import com.tororang.springai.knowledge.presentation.dto.IndexDocumentResponse;
import com.tororang.springai.knowledge.presentation.dto.SearchKnowledgeRequest;
import com.tororang.springai.knowledge.presentation.dto.SearchKnowledgeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final IndexDocumentUseCase indexDocumentUseCase;
    private final SearchKnowledgeUseCase searchKnowledgeUseCase;
    private final IndexDocumentFileUseCase indexDocumentFileUseCase;

    public KnowledgeController(IndexDocumentUseCase indexDocumentUseCase,
            SearchKnowledgeUseCase searchKnowledgeUseCase, IndexDocumentFileUseCase indexDocumentFileUseCase) {
        this.indexDocumentUseCase = indexDocumentUseCase;
        this.searchKnowledgeUseCase = searchKnowledgeUseCase;
        this.indexDocumentFileUseCase = indexDocumentFileUseCase;
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

    @PostMapping(value = "/documents/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public IndexDocumentResponse indexDocumentFile(@RequestParam("title") String title,
            @RequestParam("file") MultipartFile file) {
        if (title == null || title.isBlank() || file == null || file.isEmpty()) {
            throw new IllegalArgumentException("title and file must not be blank");
        }

        try {
            IndexDocumentFileCommand command =
                    new IndexDocumentFileCommand(title, file.getOriginalFilename(), file.getBytes());
            return new IndexDocumentResponse(indexDocumentFileUseCase.index(command));
        } catch (IOException e) {
            throw new UncheckedIOException("failed to read uploaded file", e);
        }
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
