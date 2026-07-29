package com.tororang.springai.knowledge.infrastructure;

import com.tororang.springai.knowledge.domain.EmbeddingGenerator;
import com.tororang.springai.knowledge.domain.EmbeddingVector;
import org.springframework.ai.embedding.EmbeddingModel;

import java.util.ArrayList;
import java.util.List;

public class SpringAiEmbeddingGenerator implements EmbeddingGenerator {

    private final EmbeddingModel embeddingModel;

    public SpringAiEmbeddingGenerator(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public EmbeddingVector generate(String text) {
        return toVector(embeddingModel.embed(text));
    }

    @Override
    public List<EmbeddingVector> generateAll(List<String> texts) {
        return embeddingModel.embed(texts).stream().map(this::toVector).toList();
    }

    private EmbeddingVector toVector(float[] values) {
        List<Float> boxed = new ArrayList<>(values.length);
        for (float value : values) {
            boxed.add(value);
        }
        return new EmbeddingVector(boxed);
    }
}
