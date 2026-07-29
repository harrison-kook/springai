# Knowledge 도메인 패키지 구조

`conversation`, `prompt-template`, `member`와 동일한 DDD 4계층 구조를 적용한 네 번째 바운디드 컨텍스트입니다. 문서를 청크로 나누어 임베딩한 뒤 pgvector에 저장하고, 질의와 유사한 청크를 검색하는 RAG(Retrieval-Augmented Generation)의 색인/검색 기능을 다룹니다.

## 패키지 구조

```
com.tororang.springai.knowledge
├── domain
│   ├── Document.java               # 엔티티: title + content
│   ├── DocumentChunk.java          # 값 객체: documentId + content + order
│   ├── DocumentChunker.java        # 도메인 서비스: Document를 고정 크기로 분할 (순수 로직, 외부 의존 없음)
│   ├── EmbeddingVector.java        # 값 객체: 임베딩 벡터 (List<Float>, 불변)
│   ├── EmbeddedChunk.java          # 값 객체: 저장용 (DocumentChunk + EmbeddingVector)
│   ├── RetrievedChunk.java         # 값 객체: 검색 결과용 (DocumentChunk + score)
│   ├── EmbeddingGenerator.java     # 포트: 텍스트 → 벡터 변환 인터페이스
│   └── KnowledgeRepository.java    # 포트: 벡터 저장/유사도 검색 인터페이스
│
├── application
│   ├── IndexDocumentUseCase.java   # 유스케이스: 문서 등록 → 청킹 → 임베딩 → 저장
│   ├── SearchKnowledgeUseCase.java # 유스케이스: 질의 임베딩 → 유사 청크 검색
│   └── dto
│       ├── IndexDocumentCommand.java
│       └── RetrievedChunkResult.java
│
├── infrastructure
│   ├── SpringAiEmbeddingGenerator.java   # EmbeddingGenerator 구현체, Ollama EmbeddingModel(nomic-embed-text) 사용
│   ├── PgVectorKnowledgeRepository.java  # KnowledgeRepository 구현체, JdbcTemplate + pgvector-java(PGvector)로 직접 SQL 실행
│   └── KnowledgeConfig.java              # 포트 ↔ 구현체, 유스케이스 빈 와이어링. chunk-size/top-k는 application.yaml(`knowledge.*`)에서 주입
│
└── presentation
    ├── KnowledgeController.java       # POST /api/knowledge/documents, POST /api/knowledge/search
    ├── KnowledgePageController.java   # GET /knowledge (Thymeleaf 페이지)
    └── dto
        ├── IndexDocumentRequest.java / IndexDocumentResponse.java
        └── SearchKnowledgeRequest.java / SearchKnowledgeResponse.java
```

DB 스키마(`CREATE EXTENSION vector`, `knowledge_chunk` 테이블)는 `src/main/resources/db/knowledge-schema.sql`에 있으며, 애플리케이션이 자동으로 실행하지 않고 로컬 Postgres에 1회 수동으로 적용해야 합니다.

## Spring AI 연동 방식: VectorStore 대신 직접 구현한 이유

Spring AI의 `VectorStore` 추상화(`similaritySearch` 등)는 텍스트를 받아 내부적으로 자체 `EmbeddingModel`을 호출해 임베딩까지 처리한다. 이 프로젝트는 `EmbeddingGenerator`(임베딩 계산)와 `KnowledgeRepository`(벡터 저장/검색)를 분리해 애플리케이션 계층에서 명시적으로 제어하는 구조를 택했기 때문에, `VectorStore`를 그대로 쓰면 임베딩이 이중으로 계산되는 문제가 생긴다. 그래서 `PgVectorKnowledgeRepository`는 `VectorStore`가 아니라 `JdbcTemplate` + `pgvector-java`로 직접 구현되어 있다.

## conversation 컨텍스트와의 연동 (RAG 통합)

`knowledge`는 `conversation`에 단방향으로 의존한다(`conversation` → `knowledge`). `conversation.domain`에는 knowledge 세부사항을 노출하지 않는 별도 포트가 있고, 구현체가 `knowledge.application`의 유스케이스를 재사용한다.

```
com.tororang.springai.conversation
├── domain
│   └── KnowledgeRetriever.java        # 포트: List<String> retrieve(String query)
├── application
│   └── SendMessageUseCase.java        # KnowledgeRetriever로 컨텍스트 조회 → ResponseGenerator.generate(conversation, context)
└── infrastructure
    └── KnowledgeSearchRetriever.java  # KnowledgeRetriever 구현체, knowledge.application.SearchKnowledgeUseCase에 위임 (검색 로직 재사용, 중복 없음)
```

`SpringAiResponseGenerator`는 컨텍스트가 있으면 "참고 정보 + 질문" 형태로 프롬프트를 재구성하고, 없으면 기존처럼 질문만 그대로 전달한다.

## 계층 의존 방향

```
presentation → application → domain ← infrastructure
```

- `domain`은 Spring, Spring AI, JDBC 등 프레임워크에 의존하지 않는다. `DocumentChunker`는 외부 연동이 없는 순수 도메인 서비스로 domain에 직접 둔다.
- `application`은 포트 인터페이스(`EmbeddingGenerator`, `KnowledgeRepository`)만 사용하며 구현체를 알지 못한다.
- `EmbeddingModel`, `JdbcTemplate`, `PGvector` 등 벤더/인프라 세부사항은 `infrastructure` 밖으로 새어나가지 않는다.
