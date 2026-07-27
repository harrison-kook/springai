package com.tororang.springai.conversation.infrastructure;

import com.tororang.springai.conversation.application.SendMessageUseCase;
import com.tororang.springai.conversation.application.StartConversationUseCase;
import com.tororang.springai.conversation.domain.ConversationRepository;
import com.tororang.springai.conversation.domain.ResponseGenerator;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConversationConfig {

    @Bean
    public ConversationRepository conversationRepository() {
        return new InMemoryConversationRepository();
    }

    @Bean
    public ResponseGenerator responseGenerator(ChatModel chatModel) {
        return new SpringAiResponseGenerator(ChatClient.create(chatModel));
    }

    @Bean
    public StartConversationUseCase startConversationUseCase(ConversationRepository conversationRepository) {
        return new StartConversationUseCase(conversationRepository);
    }

    @Bean
    public SendMessageUseCase sendMessageUseCase(ConversationRepository conversationRepository,
            ResponseGenerator responseGenerator) {
        return new SendMessageUseCase(conversationRepository, responseGenerator);
    }
}
