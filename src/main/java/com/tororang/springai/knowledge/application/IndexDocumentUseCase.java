package com.tororang.springai.knowledge.application;

import com.tororang.springai.knowledge.application.dto.IndexDocumentCommand;
import com.tororang.springai.knowledge.domain.Document;
import com.tororang.springai.knowledge.domain.DocumentChunk;
import com.tororang.springai.knowledge.domain.DocumentChunker;
import com.tororang.springai.knowledge.domain.EmbeddedChunk;
import com.tororang.springai.knowledge.domain.EmbeddingGenerator;
import com.tororang.springai.knowledge.domain.EmbeddingVector;
import com.tororang.springai.knowledge.domain.KnowledgeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IndexDocumentUseCase {

    private final DocumentChunker documentChunker;
    private final EmbeddingGenerator embeddingGenerator;
    private final KnowledgeRepository knowledgeRepository;
    private final int chunkSize;

    public IndexDocumentUseCase(DocumentChunker documentChunker, EmbeddingGenerator embeddingGenerator,
            KnowledgeRepository knowledgeRepository, int chunkSize) {
        this.documentChunker = documentChunker;
        this.embeddingGenerator = embeddingGenerator;
        this.knowledgeRepository = knowledgeRepository;
        this.chunkSize = chunkSize;
    }

    public UUID index(IndexDocumentCommand command) {
        Document document = Document.create(command.title(), command.content());
        List<DocumentChunk> chunks = documentChunker.chunk(document, chunkSize);

        List<String> contents = chunks.stream().map(DocumentChunk::content).toList();
        List<EmbeddingVector> embeddings = embeddingGenerator.generateAll(contents);

        List<EmbeddedChunk> embeddedChunks = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            embeddedChunks.add(new EmbeddedChunk(chunks.get(i), embeddings.get(i)));
        }
        knowledgeRepository.save(embeddedChunks);

        return document.id();
    }
}
