package com.tororang.springai.knowledge.infrastructure;

import com.tororang.springai.knowledge.application.IndexDocumentUseCase;
import com.tororang.springai.knowledge.application.SearchKnowledgeUseCase;
import com.tororang.springai.knowledge.domain.DocumentChunker;
import com.tororang.springai.knowledge.domain.EmbeddingGenerator;
import com.tororang.springai.knowledge.domain.KnowledgeRepository;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class KnowledgeConfig {

    @Bean
    public EmbeddingGenerator embeddingGenerator(EmbeddingModel embeddingModel) {
        return new SpringAiEmbeddingGenerator(embeddingModel);
    }

    @Bean
    public KnowledgeRepository knowledgeRepository(JdbcTemplate jdbcTemplate) {
        return new PgVectorKnowledgeRepository(jdbcTemplate);
    }

    @Bean
    public IndexDocumentUseCase indexDocumentUseCase(EmbeddingGenerator embeddingGenerator,
            KnowledgeRepository knowledgeRepository, @Value("${knowledge.chunk-size}") int chunkSize) {
        return new IndexDocumentUseCase(new DocumentChunker(), embeddingGenerator, knowledgeRepository, chunkSize);
    }

    @Bean
    public SearchKnowledgeUseCase searchKnowledgeUseCase(EmbeddingGenerator embeddingGenerator,
            KnowledgeRepository knowledgeRepository, @Value("${knowledge.top-k}") int topK) {
        return new SearchKnowledgeUseCase(embeddingGenerator, knowledgeRepository, topK);
    }
}
