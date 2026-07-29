package com.tororang.springai.conversation.infrastructure;

import com.tororang.springai.conversation.domain.Conversation;
import com.tororang.springai.conversation.domain.Message;
import com.tororang.springai.conversation.domain.MessageRole;
import com.tororang.springai.conversation.domain.ResponseGenerator;
import com.tororang.springai.conversation.domain.exception.AiResponseGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;
import java.util.stream.Collectors;

public class SpringAiResponseGenerator implements ResponseGenerator {

    private static final Logger log = LoggerFactory.getLogger(SpringAiResponseGenerator.class);

    private final ChatClient chatClient;

    public SpringAiResponseGenerator(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Message generate(Conversation conversation, List<String> context) {
        String userContent = conversation.messages().getLast().content();
        String prompt = buildPrompt(userContent, context);

        log.info("Calling Anthropic API... promptLength={}", prompt.length());
        log.debug("Prompt sent to Anthropic API:\n{}", prompt);
        long startedAt = System.currentTimeMillis();
        try {
            String responseText = chatClient.prompt().user(prompt).call().content();
            long elapsedMs = System.currentTimeMillis() - startedAt;
            log.info("Received Anthropic API response in {}ms, responseLength={}", elapsedMs, responseText.length());
            return new Message(MessageRole.AI, responseText);
        } catch (RuntimeException e) {
            long elapsedMs = System.currentTimeMillis() - startedAt;
            log.warn("Anthropic API call failed after {}ms", elapsedMs, e);
            throw new AiResponseGenerationException("failed to generate AI response", e);
        }
    }

    private String buildPrompt(String userContent, List<String> context) {
        if (context.isEmpty()) {
            return userContent;
        }
        String contextText = context.stream().map(chunk -> "- " + chunk).collect(Collectors.joining("\n"));
        return "다음 참고 정보를 활용해서 답변하세요.\n\n참고 정보:\n" + contextText + "\n\n질문: " + userContent;
    }
}
