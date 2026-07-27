package com.tororang.springai.conversation.infrastructure;

import com.tororang.springai.conversation.domain.Conversation;
import com.tororang.springai.conversation.domain.Message;
import com.tororang.springai.conversation.domain.MessageRole;
import com.tororang.springai.conversation.domain.ResponseGenerator;
import com.tororang.springai.conversation.domain.exception.AiResponseGenerationException;
import org.springframework.ai.chat.client.ChatClient;

public class SpringAiResponseGenerator implements ResponseGenerator {

    private final ChatClient chatClient;

    public SpringAiResponseGenerator(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Message generate(Conversation conversation) {
        String userContent = conversation.messages().getLast().content();

        try {
            String responseText = chatClient.prompt().user(userContent).call().content();
            return new Message(MessageRole.AI, responseText);
        } catch (RuntimeException e) {
            throw new AiResponseGenerationException("failed to generate AI response", e);
        }
    }
}
