package com.tororang.springai.conversation.application;

import com.tororang.springai.conversation.application.dto.ConversationResult;
import com.tororang.springai.conversation.application.dto.SendMessageCommand;
import com.tororang.springai.conversation.domain.Conversation;
import com.tororang.springai.conversation.domain.ConversationRepository;
import com.tororang.springai.conversation.domain.Message;
import com.tororang.springai.conversation.domain.MessageRole;
import com.tororang.springai.conversation.domain.ResponseGenerator;
import com.tororang.springai.conversation.domain.exception.ConversationClosedException;
import com.tororang.springai.conversation.domain.exception.ConversationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendMessageUseCaseTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ResponseGenerator responseGenerator;

    private SendMessageUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SendMessageUseCase(conversationRepository, responseGenerator);
    }

    @Test
    void 사용자_메시지가_추가된_뒤_응답_생성기가_호출된다() {
        Conversation conversation = Conversation.start();
        when(conversationRepository.findById(conversation.id())).thenReturn(Optional.of(conversation));
        AtomicReference<List<Message>> messagesAtCallTime = new AtomicReference<>();
        when(responseGenerator.generate(any())).thenAnswer(invocation -> {
            Conversation argument = invocation.getArgument(0);
            messagesAtCallTime.set(List.copyOf(argument.messages()));
            return new Message(MessageRole.AI, "AI 응답");
        });

        useCase.send(new SendMessageCommand(conversation.id(), "안녕하세요"));

        assertThat(messagesAtCallTime.get())
                .last()
                .isEqualTo(new Message(MessageRole.USER, "안녕하세요"));
    }

    @Test
    void AI_응답_메시지가_추가된_대화가_저장된다() {
        Conversation conversation = Conversation.start();
        when(conversationRepository.findById(conversation.id())).thenReturn(Optional.of(conversation));
        Message aiMessage = new Message(MessageRole.AI, "AI 응답");
        when(responseGenerator.generate(any())).thenReturn(aiMessage);

        useCase.send(new SendMessageCommand(conversation.id(), "안녕하세요"));

        ArgumentCaptor<Conversation> captor = ArgumentCaptor.forClass(Conversation.class);
        verify(conversationRepository).save(captor.capture());
        assertThat(captor.getValue().messages()).last().isEqualTo(aiMessage);
    }

    @Test
    void 결과에는_사용자_메시지와_AI_응답이_모두_포함된다() {
        Conversation conversation = Conversation.start();
        when(conversationRepository.findById(conversation.id())).thenReturn(Optional.of(conversation));
        Message aiMessage = new Message(MessageRole.AI, "AI 응답");
        when(responseGenerator.generate(any())).thenReturn(aiMessage);

        ConversationResult result = useCase.send(new SendMessageCommand(conversation.id(), "안녕하세요"));

        assertThat(result.conversationId()).isEqualTo(conversation.id());
        assertThat(result.messages())
                .containsExactly(new Message(MessageRole.USER, "안녕하세요"), aiMessage);
    }

    @Test
    void 존재하지_않는_대화_id로_메시지를_보내면_예외가_발생한다() {
        UUID unknownId = UUID.randomUUID();
        when(conversationRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.send(new SendMessageCommand(unknownId, "안녕하세요")))
                .isInstanceOf(ConversationNotFoundException.class);
    }

    @Test
    void CLOSED_상태의_대화에_메시지를_보내면_예외가_전파된다() {
        Conversation conversation = Conversation.start();
        conversation.close();
        when(conversationRepository.findById(conversation.id())).thenReturn(Optional.of(conversation));

        assertThatThrownBy(() -> useCase.send(new SendMessageCommand(conversation.id(), "안녕하세요")))
                .isInstanceOf(ConversationClosedException.class);

        verify(responseGenerator, never()).generate(any());
        verify(conversationRepository, never()).save(any());
    }

    @Test
    void 응답_생성_중_예외가_발생하면_저장소에_저장되지_않는다() {
        Conversation conversation = Conversation.start();
        when(conversationRepository.findById(conversation.id())).thenReturn(Optional.of(conversation));
        when(responseGenerator.generate(any())).thenThrow(new RuntimeException("AI 호출 실패"));

        assertThatThrownBy(() -> useCase.send(new SendMessageCommand(conversation.id(), "안녕하세요")))
                .isInstanceOf(RuntimeException.class);

        verify(conversationRepository, never()).save(any());
    }
}
