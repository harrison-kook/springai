package com.tororang.springai.knowledge.application;

import com.tororang.springai.knowledge.application.dto.IndexDocumentCommand;
import com.tororang.springai.knowledge.application.dto.IndexDocumentFileCommand;
import com.tororang.springai.knowledge.domain.DocumentContentExtractor;
import com.tororang.springai.knowledge.domain.DocumentFileType;

import java.util.UUID;

public class IndexDocumentFileUseCase {

    private final DocumentContentExtractor contentExtractor;
    private final IndexDocumentUseCase indexDocumentUseCase;

    public IndexDocumentFileUseCase(DocumentContentExtractor contentExtractor,
            IndexDocumentUseCase indexDocumentUseCase) {
        this.contentExtractor = contentExtractor;
        this.indexDocumentUseCase = indexDocumentUseCase;
    }

    public UUID index(IndexDocumentFileCommand command) {
        DocumentFileType fileType = DocumentFileType.fromFilename(command.filename());
        String content = contentExtractor.extract(fileType, command.fileContent());
        return indexDocumentUseCase.index(new IndexDocumentCommand(command.title(), content));
    }
}