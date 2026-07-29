package com.tororang.springai.knowledge.domain;

import java.util.List;

public record EmbeddingVector(List<Float> values) {

    public EmbeddingVector {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        values = List.copyOf(values);
    }
}
