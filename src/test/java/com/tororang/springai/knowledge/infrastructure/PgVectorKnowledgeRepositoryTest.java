package com.tororang.springai.knowledge.infrastructure;

import com.tororang.springai.knowledge.domain.DocumentChunk;
import com.tororang.springai.knowledge.domain.EmbeddedChunk;
import com.tororang.springai.knowledge.domain.EmbeddingVector;
import com.tororang.springai.knowledge.domain.RetrievedChunk;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class PgVectorKnowledgeRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private PgVectorKnowledgeRepository repository;

    @BeforeEach
    void setUp() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(System.getProperty("test.postgres.url", "jdbc:postgresql://localhost:5432/springai_rag"));
        dataSource.setUsername(System.getProperty("test.postgres.username", "postgres"));
        dataSource.setPassword(System.getProperty("test.postgres.password", "mysecret"));
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("TRUNCATE TABLE knowledge_chunk");
        repository = new PgVectorKnowledgeRepository(jdbcTemplate);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE knowledge_chunk");
    }

    @Test
    void 저장한_청크_중_쿼리_벡터와_가장_유사한_청크가_먼저_조회된다() {
        UUID documentId = UUID.randomUUID();
        DocumentChunk closeChunk = new DocumentChunk(documentId, "고양이에 대한 내용", 0);
        DocumentChunk farChunk = new DocumentChunk(documentId, "자동차 엔진에 대한 내용", 1);
        EmbeddingVector closeVector = randomVector(768, 1L);
        EmbeddingVector farVector = randomVector(768, 2L);
        repository.save(List.of(
                new EmbeddedChunk(closeChunk, closeVector),
                new EmbeddedChunk(farChunk, farVector)));

        List<RetrievedChunk> results = repository.search(closeVector, 1);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).chunk().content()).isEqualTo("고양이에 대한 내용");
    }

    @Test
    void topK만큼만_조회된다() {
        UUID documentId = UUID.randomUUID();
        repository.save(List.of(
                new EmbeddedChunk(new DocumentChunk(documentId, "청크1", 0), randomVector(768, 1L)),
                new EmbeddedChunk(new DocumentChunk(documentId, "청크2", 1), randomVector(768, 2L)),
                new EmbeddedChunk(new DocumentChunk(documentId, "청크3", 2), randomVector(768, 3L))));

        List<RetrievedChunk> results = repository.search(randomVector(768, 1L), 2);

        assertThat(results).hasSize(2);
    }

    private EmbeddingVector randomVector(int dimensions, long seed) {
        Random random = new Random(seed);
        List<Float> values = new ArrayList<>(dimensions);
        for (int i = 0; i < dimensions; i++) {
            values.add(random.nextFloat());
        }
        return new EmbeddingVector(values);
    }
}
