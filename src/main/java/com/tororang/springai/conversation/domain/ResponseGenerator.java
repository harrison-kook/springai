package com.tororang.springai.conversation.domain;

import java.util.List;

public interface ResponseGenerator {

    Message generate(Conversation conversation, List<String> context);
}
