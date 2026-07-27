package com.tororang.springai.conversation.domain;

public interface ResponseGenerator {

    Message generate(Conversation conversation);
}
