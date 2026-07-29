-- knowledge 컨텍스트용 pgvector 스키마. 로컬 Postgres에 수동으로 1회 실행한다.
-- 임베딩 모델(nomic-embed-text)의 출력 차원(768)과 vector(N)의 N이 일치해야 한다.

CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS knowledge_chunk (
    id UUID PRIMARY KEY,
    document_id UUID NOT NULL,
    chunk_order INT NOT NULL,
    content TEXT NOT NULL,
    embedding vector(768) NOT NULL
);
