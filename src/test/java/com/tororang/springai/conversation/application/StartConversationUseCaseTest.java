package com.tororang.springai.conversation.application;

import com.tororang.springai.conversation.domain.Conversation;
import com.tororang.springai.conversation.domain.ConversationRepository;
import com.tororang.springai.conversation.domain.ConversationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StartConversationUseCaseTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Test
    void 새_대화를_시작하면_OPEN_상태로_저장되고_id가_반환된다() {
        when(conversationRepository.save(any(Conversation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        StartConversationUseCase useCase = new StartConversationUseCase(conversationRepository);

        UUID id = useCase.start();

        ArgumentCaptor<Conversation> captor = ArgumentCaptor.forClass(Conversation.class);
        verify(conversationRepository).save(captor.capture());
        Conversation saved = captor.getValue();
        assertThat(saved.id()).isEqualTo(id);
        assertThat(saved.status()).isEqualTo(ConversationStatus.OPEN);
        assertThat(saved.messages()).isEmpty();
    }
}
