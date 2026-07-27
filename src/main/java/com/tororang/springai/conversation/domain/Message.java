package com.tororang.springai.conversation.domain;

public record Message(MessageRole role, String content) {

    public Message {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content must not be empty");
        }
    }
}
