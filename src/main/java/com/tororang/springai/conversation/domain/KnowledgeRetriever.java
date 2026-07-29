package com.tororang.springai.conversation.domain;

import java.util.List;

public interface KnowledgeRetriever {

    List<String> retrieve(String query);
}
