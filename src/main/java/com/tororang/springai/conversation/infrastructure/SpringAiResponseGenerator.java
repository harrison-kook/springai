package com.tororang.springai.conversation.infrastructure;

import com.tororang.springai.conversation.domain.Conversation;
import com.tororang.springai.conversation.domain.Message;
import com.tororang.springai.conversation.domain.MessageRole;
import com.tororang.springai.conversation.domain.ResponseGenerator;
import com.tororang.springai.conversation.domain.exception.AiResponseGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;

public class SpringAiResponseGenerator implements ResponseGenerator {

    private static final Logger log = LoggerFactory.getLogger(SpringAiResponseGenerator.class);

    private final ChatClient chatClient;

    public SpringAiResponseGenerator(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Message generate(Conversation conversation) {
        String userContent = conversation.messages().getLast().content();

        log.info("Calling Anthropic API... promptLength={}", userContent.length());
        long startedAt = System.currentTimeMillis();
        try {
            String responseText = chatClient.prompt().user(userContent).call().content();
            long elapsedMs = System.currentTimeMillis() - startedAt;
            log.info("Received Anthropic API response in {}ms, responseLength={}", elapsedMs, responseText.length());
            return new Message(MessageRole.AI, responseText);
        } catch (RuntimeException e) {
            long elapsedMs = System.currentTimeMillis() - startedAt;
            log.warn("Anthropic API call failed after {}ms", elapsedMs, e);
            throw new AiResponseGenerationException("failed to generate AI response", e);
        }
    }
}
