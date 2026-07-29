package com.tororang.springai.knowledge.domain;

public record RetrievedChunk(DocumentChunk chunk, double score) {

    public RetrievedChunk {
        if (chunk == null) {
            throw new IllegalArgumentException("chunk must not be null");
        }
    }
}
