package com.tororang.springai.conversation.application;

import com.tororang.springai.conversation.domain.Conversation;
import com.tororang.springai.conversation.domain.ConversationRepository;

import java.util.UUID;

public class StartConversationUseCase {

    private final ConversationRepository conversationRepository;

    public StartConversationUseCase(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public UUID start() {
        Conversation conversation = Conversation.start();
        conversationRepository.save(conversation);
        return conversation.id();
    }
}
