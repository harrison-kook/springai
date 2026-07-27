package com.tororang.springai.conversation.domain;

import com.tororang.springai.conversation.domain.exception.ConversationClosedException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Conversation {

    private final UUID id;
    private final List<Message> messages = new ArrayList<>();
    private ConversationStatus status;

    private Conversation(UUID id) {
        this.id = id;
        this.status = ConversationStatus.OPEN;
    }

    public static Conversation start() {
        return new Conversation(UUID.randomUUID());
    }

    public void addMessage(Message message) {
        if (status == ConversationStatus.CLOSED) {
            throw new ConversationClosedException("closed conversation cannot accept new messages: " + id);
        }
        messages.add(message);
    }

    public void close() {
        if (status == ConversationStatus.CLOSED) {
            throw new IllegalStateException("conversation is already closed: " + id);
        }
        status = ConversationStatus.CLOSED;
    }

    public UUID id() {
        return id;
    }

    public List<Message> messages() {
        return List.copyOf(messages);
    }

    public ConversationStatus status() {
        return status;
    }
}
