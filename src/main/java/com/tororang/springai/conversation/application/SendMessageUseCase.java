package com.tororang.springai.conversation.application;

import com.tororang.springai.conversation.application.dto.ConversationResult;
import com.tororang.springai.conversation.application.dto.SendMessageCommand;
import com.tororang.springai.conversation.domain.Conversation;
import com.tororang.springai.conversation.domain.ConversationRepository;
import com.tororang.springai.conversation.domain.KnowledgeRetriever;
import com.tororang.springai.conversation.domain.Message;
import com.tororang.springai.conversation.domain.MessageRole;
import com.tororang.springai.conversation.domain.ResponseGenerator;
import com.tororang.springai.conversation.domain.exception.ConversationNotFoundException;

import java.util.List;

public class SendMessageUseCase {

    private final ConversationRepository conversationRepository;
    private final ResponseGenerator responseGenerator;
    private final KnowledgeRetriever knowledgeRetriever;

    public SendMessageUseCase(ConversationRepository conversationRepository, ResponseGenerator responseGenerator,
            KnowledgeRetriever knowledgeRetriever) {
        this.conversationRepository = conversationRepository;
        this.responseGenerator = responseGenerator;
        this.knowledgeRetriever = knowledgeRetriever;
    }

    public ConversationResult send(SendMessageCommand command) {
        Conversation conversation = conversationRepository.findById(command.conversationId())
                .orElseThrow(() -> new ConversationNotFoundException(
                        "conversation not found: " + command.conversationId()));

        conversation.addMessage(new Message(MessageRole.USER, command.content()));

        List<String> context = knowledgeRetriever.retrieve(command.content());
        Message aiMessage = responseGenerator.generate(conversation, context);
        conversation.addMessage(aiMessage);

        conversationRepository.save(conversation);

        return new ConversationResult(conversation.id(), conversation.messages());
    }
}
