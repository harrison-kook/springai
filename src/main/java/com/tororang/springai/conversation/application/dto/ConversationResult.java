package com.tororang.springai.conversation.application.dto;

import com.tororang.springai.conversation.domain.Message;

import java.util.List;
import java.util.UUID;

public record ConversationResult(UUID conversationId, List<Message> messages) {
}
