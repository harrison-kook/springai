package com.tororang.springai.conversation.presentation.dto;

import com.tororang.springai.conversation.application.dto.ConversationResult;
import com.tororang.springai.conversation.domain.Message;

import java.util.List;
import java.util.UUID;

public record ConversationResponse(UUID conversationId, List<MessageResponse> messages) {

    public static ConversationResponse from(ConversationResult result) {
        List<MessageResponse> messages = result.messages().stream()
                .map(MessageResponse::from)
                .toList();
        return new ConversationResponse(result.conversationId(), messages);
    }

    public record MessageResponse(String role, String content) {

        public static MessageResponse from(Message message) {
            return new MessageResponse(message.role().name(), message.content());
        }
    }
}
