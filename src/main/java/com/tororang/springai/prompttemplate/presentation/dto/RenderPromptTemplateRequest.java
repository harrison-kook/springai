package com.tororang.springai.prompttemplate.presentation.dto;

import java.util.Map;

public record RenderPromptTemplateRequest(Map<String, String> variables) {
}
