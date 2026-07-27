package com.tororang.springai.prompttemplate.application.dto;

import java.util.Map;

public record RenderPromptTemplateCommand(String templateName, Map<String, String> variables) {
}
