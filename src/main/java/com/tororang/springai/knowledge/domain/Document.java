package com.tororang.springai.knowledge.domain;

import java.util.UUID;

public class Document {

    private final UUID id;
    private final String title;
    private final String content;

    private Document(UUID id, String title, String content) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be empty");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content must not be empty");
        }
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public static Document create(String title, String content) {
        return new Document(UUID.randomUUID(), title, content);
    }

    public UUID id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String content() {
        return content;
    }
}
