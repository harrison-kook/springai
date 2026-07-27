package com.tororang.springai.conversation.infrastructure;

import com.tororang.springai.conversation.domain.Conversation;
import com.tororang.springai.conversation.domain.ConversationRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryConversationRepository implements ConversationRepository {

    private final Map<UUID, Conversation> store = new ConcurrentHashMap<>();

    @Override
    public Conversation save(Conversation conversation) {
        store.put(conversation.id(), conversation);
        return conversation;
    }

    @Override
    public Optional<Conversation> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }
}
