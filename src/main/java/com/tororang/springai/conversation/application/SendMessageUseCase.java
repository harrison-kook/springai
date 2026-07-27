package com.tororang.springai.conversation.application;

import com.tororang.springai.conversation.application.dto.ConversationResult;
import com.tororang.springai.conversation.application.dto.SendMessageCommand;
import com.tororang.springai.conversation.domain.Conversation;
import com.tororang.springai.conversation.domain.ConversationRepository;
import com.tororang.springai.conversation.domain.Message;
import com.tororang.springai.conversation.domain.MessageRole;
import com.tororang.springai.conversation.domain.ResponseGenerator;
import com.tororang.springai.conversation.domain.exception.ConversationNotFoundException;

public class SendMessageUseCase {

    private final ConversationRepository conversationRepository;
    private final ResponseGenerator responseGenerator;

    public SendMessageUseCase(ConversationRepository conversationRepository, ResponseGenerator responseGenerator) {
        this.conversationRepository = conversationRepository;
        this.responseGenerator = responseGenerator;
    }

    public ConversationResult send(SendMessageCommand command) {
        Conversation conversation = conversationRepository.findById(command.conversationId())
                .orElseThrow(() -> new ConversationNotFoundException(
                        "conversation not found: " + command.conversationId()));

        conversation.addMessage(new Message(MessageRole.USER, command.content()));

        Message aiMessage = responseGenerator.generate(conversation);
        conversation.addMessage(aiMessage);

        conversationRepository.save(conversation);

        return new ConversationResult(conversation.id(), conversation.messages());
    }
}
