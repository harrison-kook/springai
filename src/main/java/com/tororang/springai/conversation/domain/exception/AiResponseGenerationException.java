package com.tororang.springai.conversation.domain.exception;

public class AiResponseGenerationException extends RuntimeException {

    public AiResponseGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
