package com.tororang.springai.knowledge.domain;

import java.util.UUID;

public record DocumentChunk(UUID documentId, String content, int order) {

    public DocumentChunk {
        if (documentId == null) {
            throw new IllegalArgumentException("documentId must not be null");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content must not be empty");
        }
        if (order < 0) {
            throw new IllegalArgumentException("order must not be negative");
        }
    }
}
