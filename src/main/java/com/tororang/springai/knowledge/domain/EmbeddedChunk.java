package com.tororang.springai.knowledge.domain;

public record EmbeddedChunk(DocumentChunk chunk, EmbeddingVector embedding) {

    public EmbeddedChunk {
        if (chunk == null) {
            throw new IllegalArgumentException("chunk must not be null");
        }
        if (embedding == null) {
            throw new IllegalArgumentException("embedding must not be null");
        }
    }
}
