package com.tororang.springai.conversation.infrastructure;

import com.tororang.springai.conversation.domain.Conversation;
import com.tororang.springai.conversation.domain.Message;
import com.tororang.springai.conversation.domain.MessageRole;
import com.tororang.springai.conversation.domain.exception.AiResponseGenerationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringAiResponseGeneratorTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @Test
    void ChatClient가_정상_텍스트를_반환하면_AI_메시지로_변환한다() {
        Conversation conversation = Conversation.start();
        conversation.addMessage(new Message(MessageRole.USER, "안녕하세요"));

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("안녕하세요")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("반갑습니다");

        SpringAiResponseGenerator generator = new SpringAiResponseGenerator(chatClient);
        Message aiMessage = generator.generate(conversation);

        assertThat(aiMessage.role()).isEqualTo(MessageRole.AI);
        assertThat(aiMessage.content()).isEqualTo("반갑습니다");
    }

    @Test
    void ChatClient_호출_중_예외가_발생하면_AiResponseGenerationException으로_변환된다() {
        Conversation conversation = Conversation.start();
        conversation.addMessage(new Message(MessageRole.USER, "안녕하세요"));

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("안녕하세요")).thenReturn(requestSpec);
        when(requestSpec.call()).thenThrow(new RuntimeException("model unavailable"));

        SpringAiResponseGenerator generator = new SpringAiResponseGenerator(chatClient);

        assertThatThrownBy(() -> generator.generate(conversation))
                .isInstanceOf(AiResponseGenerationException.class)
                .hasCauseInstanceOf(RuntimeException.class);
    }
}
