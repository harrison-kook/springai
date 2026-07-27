package com.tororang.springai.prompttemplate.domain.exception;

public class MissingTemplateVariableException extends RuntimeException {

    public MissingTemplateVariableException(String message) {
        super(message);
    }
}
