package com.tororang.springai.conversation.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageTest {

    @Test
    void 내용이_빈_문자열이면_예외가_발생한다() {
        assertThatThrownBy(() -> new Message(MessageRole.USER, ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 내용이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> new Message(MessageRole.USER, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void role과_content가_유효하면_정상적으로_생성된다() {
        Message message = new Message(MessageRole.USER, "안녕하세요");

        assertThat(message.role()).isEqualTo(MessageRole.USER);
        assertThat(message.content()).isEqualTo("안녕하세요");
    }

    @Test
    void role과_content가_같으면_동등하다() {
        Message message1 = new Message(MessageRole.AI, "안녕하세요");
        Message message2 = new Message(MessageRole.AI, "안녕하세요");

        assertThat(message1).isEqualTo(message2);
        assertThat(message1.hashCode()).isEqualTo(message2.hashCode());
    }
}
