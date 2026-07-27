package com.tororang.springai.prompttemplate.domain.exception;

public class DuplicateTemplateNameException extends RuntimeException {

    public DuplicateTemplateNameException(String message) {
        super(message);
    }
}
