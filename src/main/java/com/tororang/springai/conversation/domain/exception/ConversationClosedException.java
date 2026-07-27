package com.tororang.springai.conversation.domain.exception;

public class ConversationClosedException extends RuntimeException {

    public ConversationClosedException(String message) {
        super(message);
    }
}
