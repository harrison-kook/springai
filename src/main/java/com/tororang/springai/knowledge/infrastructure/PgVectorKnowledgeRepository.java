package com.tororang.springai.knowledge.infrastructure;

import com.pgvector.PGvector;
import com.tororang.springai.knowledge.domain.DocumentChunk;
import com.tororang.springai.knowledge.domain.EmbeddedChunk;
import com.tororang.springai.knowledge.domain.EmbeddingVector;
import com.tororang.springai.knowledge.domain.KnowledgeRepository;
import com.tororang.springai.knowledge.domain.RetrievedChunk;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.UUID;

public class PgVectorKnowledgeRepository implements KnowledgeRepository {

    private static final String INSERT_SQL = "INSERT INTO knowledge_chunk "
            + "(id, document_id, chunk_order, content, embedding) VALUES (?, ?, ?, ?, ?)";

    private static final String SEARCH_SQL = "SELECT document_id, chunk_order, content, "
            + "1 - (embedding <=> ?) AS score "
            + "FROM knowledge_chunk ORDER BY embedding <=> ? LIMIT ?";

    private final JdbcTemplate jdbcTemplate;

    public PgVectorKnowledgeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(List<EmbeddedChunk> chunks) {
        for (EmbeddedChunk embeddedChunk : chunks) {
            DocumentChunk chunk = embeddedChunk.chunk();
            jdbcTemplate.update(INSERT_SQL, UUID.randomUUID(), chunk.documentId(), chunk.order(), chunk.content(),
                    toPgVector(embeddedChunk.embedding()));
        }
    }

    @Override
    public List<RetrievedChunk> search(EmbeddingVector queryVector, int topK) {
        PGvector vector = toPgVector(queryVector);
        return jdbcTemplate.query(SEARCH_SQL,
                (rs, rowNum) -> new RetrievedChunk(
                        new DocumentChunk(rs.getObject("document_id", UUID.class), rs.getString("content"),
                                rs.getInt("chunk_order")),
                        rs.getDouble("score")),
                vector, vector, topK);
    }

    private PGvector toPgVector(EmbeddingVector vector) {
        return new PGvector(vector.values());
    }
}
