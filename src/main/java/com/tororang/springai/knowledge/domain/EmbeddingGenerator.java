package com.tororang.springai.knowledge.domain;

import java.util.List;

public interface EmbeddingGenerator {

    EmbeddingVector generate(String text);

    List<EmbeddingVector> generateAll(List<String> texts);
}
