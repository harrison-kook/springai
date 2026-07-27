package com.tororang.springai.conversation.domain;

import com.tororang.springai.conversation.domain.exception.ConversationClosedException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConversationTest {

    @Test
    void 새로운_대화를_생성하면_메시지_목록은_비어있다() {
        Conversation conversation = Conversation.start();

        assertThat(conversation.messages()).isEmpty();
    }

    @Test
    void 새로운_대화를_생성하면_상태는_OPEN이다() {
        Conversation conversation = Conversation.start();

        assertThat(conversation.status()).isEqualTo(ConversationStatus.OPEN);
    }

    @Test
    void OPEN_상태의_대화에_메시지를_추가하면_목록_마지막에_추가된다() {
        Conversation conversation = Conversation.start();

        conversation.addMessage(new Message(MessageRole.USER, "안녕"));

        assertThat(conversation.messages())
                .last()
                .isEqualTo(new Message(MessageRole.USER, "안녕"));
    }

    @Test
    void 메시지를_여러_번_추가하면_추가한_순서대로_쌓인다() {
        Conversation conversation = Conversation.start();
        Message first = new Message(MessageRole.USER, "첫번째");
        Message second = new Message(MessageRole.AI, "두번째");

        conversation.addMessage(first);
        conversation.addMessage(second);

        assertThat(conversation.messages()).containsExactly(first, second);
    }

    @Test
    void 대화를_종료하면_상태가_CLOSED로_바뀐다() {
        Conversation conversation = Conversation.start();

        conversation.close();

        assertThat(conversation.status()).isEqualTo(ConversationStatus.CLOSED);
    }

    @Test
    void CLOSED_상태의_대화에_메시지를_추가하면_예외가_발생한다() {
        Conversation conversation = Conversation.start();
        conversation.close();

        assertThatThrownBy(() -> conversation.addMessage(new Message(MessageRole.USER, "안녕")))
                .isInstanceOf(ConversationClosedException.class);
    }

    @Test
    void 이미_CLOSED_상태인_대화를_다시_종료하면_예외가_발생한다() {
        Conversation conversation = Conversation.start();
        conversation.close();

        assertThatThrownBy(conversation::close)
                .isInstanceOf(IllegalStateException.class);
    }
}
