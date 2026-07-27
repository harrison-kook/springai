package com.tororang.springai.conversation.infrastructure;

import com.tororang.springai.conversation.domain.Conversation;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryConversationRepositoryTest {

    private final InMemoryConversationRepository repository = new InMemoryConversationRepository();

    @Test
    void save_후_findById로_동일한_대화를_조회할_수_있다() {
        Conversation conversation = Conversation.start();

        repository.save(conversation);
        Optional<Conversation> found = repository.findById(conversation.id());

        assertThat(found).contains(conversation);
    }

    @Test
    void 존재하지_않는_id로_조회하면_빈_Optional을_반환한다() {
        Optional<Conversation> found = repository.findById(UUID.randomUUID());

        assertThat(found).isEmpty();
    }
}
