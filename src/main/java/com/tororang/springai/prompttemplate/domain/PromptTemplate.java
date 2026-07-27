package com.tororang.springai.prompttemplate.domain;

import com.tororang.springai.prompttemplate.domain.exception.MissingTemplateVariableException;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PromptTemplate {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{(\\w+)\\}\\}");

    private final UUID id;
    private final String name;
    private final String content;

    private PromptTemplate(UUID id, String name, String content) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be empty");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content must not be empty");
        }
        this.id = id;
        this.name = name;
        this.content = content;
    }

    public static PromptTemplate create(String name, String content) {
        return new PromptTemplate(UUID.randomUUID(), name, content);
    }

    public String render(Map<String, String> variables) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(content);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String variableName = matcher.group(1);
            String value = variables.get(variableName);
            if (value == null) {
                throw new MissingTemplateVariableException("missing template variable: " + variableName);
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String content() {
        return content;
    }
}
