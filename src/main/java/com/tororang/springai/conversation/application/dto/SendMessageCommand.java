package com.tororang.springai.conversation.application.dto;

import java.util.UUID;

public record SendMessageCommand(UUID conversationId, String content) {
}
