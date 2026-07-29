package com.tororang.springai.knowledge.domain;

import java.util.ArrayList;
import java.util.List;

public class DocumentChunker {

    public List<DocumentChunk> chunk(Document document, int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be positive");
        }

        String content = document.content();
        List<DocumentChunk> chunks = new ArrayList<>();
        int order = 0;
        for (int start = 0; start < content.length(); start += chunkSize) {
            int end = Math.min(start + chunkSize, content.length());
            chunks.add(new DocumentChunk(document.id(), content.substring(start, end), order));
            order++;
        }
        return chunks;
    }
}
