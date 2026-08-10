# Knowledge 도메인 테스트 시나리오

`doc/knowledge/package-structure.md`의 패키지 구조를 기준으로 domain → application → infrastructure → presentation 순서로 Red → Green 사이클을 진행했다. Postgres/Ollama 실제 연동이 필요한 테스트는 `@Tag("integration")`으로 분리해 `./gradlew integrationTest`로만 실행한다(`./gradlew test`에서는 제외).

체크박스는 구현 진행 상태 표시용입니다(`[ ]` 미구현, `[x]` 완료).

## 1. Domain 계층

### 1.1 Document
- [x] title이 빈 문자열이거나 `null`이면 `IllegalArgumentException`이 발생한다.
- [x] content가 빈 문자열이거나 `null`이면 `IllegalArgumentException`이 발생한다.
- [x] title과 content가 유효하면 정상적으로 생성된다.

### 1.2 DocumentChunk
- [x] documentId가 `null`이면 `IllegalArgumentException`이 발생한다.
- [x] content가 빈 문자열이거나 `null`이면 `IllegalArgumentException`이 발생한다.
- [x] order가 음수이면 `IllegalArgumentException`이 발생한다.
- [x] 유효한 값이면 정상적으로 생성되고, 같은 값이면 동등하다.

### 1.3 DocumentChunker
- [x] chunkSize가 0 이하이면 `IllegalArgumentException`이 발생한다.
- [x] content가 chunkSize보다 짧으면 청크가 하나만 생성된다.
- [x] content가 chunkSize로 정확히 나누어떨어지면 그 개수만큼 청크가 생성된다.
- [x] content가 chunkSize로 나누어떨어지지 않으면 마지막 청크는 나머지만큼 생성된다.
- [x] 청크는 순서대로 order가 증가한다.

### 1.4 DocumentFileType
- [x] pdf/xlsx/docx/md 확장자면 해당 파일타입을 반환한다.
- [x] 확장자 대소문자를 구분하지 않는다.
- [x] 지원하지 않는 확장자, 확장자 없음, 파일명이 null/빈문자열이면 `IllegalArgumentException`이 발생한다.

### 1.5 EmbeddingVector
- [x] values가 `null`이거나 비어있으면 `IllegalArgumentException`이 발생한다.
- [x] 유효한 값이면 정상적으로 생성되고, 같은 값이면 동등하다.
- [x] 생성 이후 원본 리스트를 변경해도 영향을 받지 않고, values는 외부에서 수정할 수 없다(방어적 복사).

### 1.6 EmbeddedChunk / RetrievedChunk
- [x] chunk 또는 embedding이 `null`이면 `IllegalArgumentException`이 발생한다(EmbeddedChunk).
- [x] chunk가 `null`이면 `IllegalArgumentException`이 발생한다(RetrievedChunk).

## 2. Application 계층

테스트에서 `EmbeddingGenerator`, `KnowledgeRepository`는 목(mock)으로 대체하여 애플리케이션 서비스의 오케스트레이션 로직만 검증한다. `DocumentChunker`는 순수 로직이라 실제 구현을 그대로 사용한다.

### 2.1 IndexDocumentUseCase
- [x] 문서를 색인하면 청크로 분할되어 임베딩과 함께 저장된다.
- [x] 임베딩 생성기에는 청크 내용 목록이 순서대로 전달된다.
- [x] 색인된 문서의 id가 반환된다.

### 2.2 SearchKnowledgeUseCase
- [x] 쿼리로 검색하면 임베딩을 생성한 뒤 저장소에서 유사한 청크를 조회한다.
- [x] 검색 결과가 없으면 빈 리스트를 반환한다.
- [x] 저장소 조회에는 설정된 topK가 그대로 전달된다.

### 2.3 IndexDocumentFileUseCase (`DocumentContentExtractor` mock, 실제 `IndexDocumentUseCase`는 mock 포트로 구성)
- [x] 파일을 색인하면 추출된 텍스트가 청크로 분할되어 저장된다.
- [x] 추출기에는 파일명으로 판별한 파일타입과 파일 내용이 그대로 전달된다.
- [x] 지원하지 않는 확장자면 예외가 발생하고 추출기와 저장소는 호출되지 않는다.
- [x] 색인된 문서의 id가 반환된다.

## 3. Infrastructure 계층

### 3.1 SpringAiEmbeddingGenerator (단위 테스트, `EmbeddingModel` mock)
- [x] 단일 텍스트를 임베딩하면 `EmbeddingVector`로 변환된다.
- [x] 여러 텍스트를 배치로 임베딩하면 순서대로 변환된다.

### 3.2 SpringAiEmbeddingGenerator (`@Tag("integration")`, 실제 Ollama 호출)
- [x] 실제 Ollama로 단일 텍스트를 임베딩하면 768차원 벡터가 반환된다.
- [x] 실제 Ollama로 여러 텍스트를 배치 임베딩하면 순서대로 반환된다.

### 3.3 PgVectorKnowledgeRepository (`@Tag("integration")`, 실제 Postgres 호출)
- [x] 저장한 청크 중 쿼리 벡터와 가장 유사한 청크가 먼저 조회된다(코사인 유사도).
- [x] topK만큼만 조회된다.

### 3.4 ApacheDocumentContentExtractor (단위 테스트, PDFBox/POI로 샘플 파일을 직접 생성해 검증, 외부 연동 없음)
- [x] PDF에서 텍스트를 추출한다.
- [x] XLSX에서 텍스트를 추출한다.
- [x] DOCX에서 텍스트를 추출한다.
- [x] MD는 파싱 없이 그대로 텍스트로 추출한다.
- [x] 손상된 파일이면 `IllegalArgumentException`이 발생한다.

## 4. Presentation 계층 (MockMvc 슬라이스 테스트)

### 4.1 KnowledgeController
- [x] 문서 등록 요청 시 `201 Created`와 함께 documentId를 반환한다.
- [x] title이나 content가 비어있으면 `400 Bad Request`를 반환한다.
- [x] 파일 업로드(multipart)로 문서 등록 요청 시 `201 Created`와 함께 documentId를 반환한다.
- [x] 파일 업로드 시 title이 비어있으면 `400 Bad Request`를 반환한다.
- [x] 파일 업로드 시 파일이 비어있으면 `400 Bad Request`를 반환한다.
- [x] 지원하지 않는 확장자면 `400 Bad Request`를 반환한다.
- [x] 검색 요청 시 `200 OK`와 함께 결과 목록을 반환한다.
- [x] query가 비어있으면 `400 Bad Request`를 반환한다.
- [x] 검색 결과가 없으면 빈 목록을 반환한다.

### 4.2 KnowledgePageController
- [x] `/knowledge` 요청 시 `200 OK`와 `knowledge` 뷰를 반환한다.

## 5. conversation 컨텍스트 통합 (RAG)

### 5.1 KnowledgeSearchRetriever (`conversation.infrastructure`, `SearchKnowledgeUseCase` mock)
- [x] 검색 결과의 content만 추출해서 반환한다.
- [x] 검색 결과가 없으면 빈 리스트를 반환한다.
- [x] 전달받은 쿼리가 그대로 검색에 사용된다.

### 5.2 SendMessageUseCase 확장
- [x] 사용자 메시지 내용으로 지식 검색이 수행된다.
- [x] 검색된 컨텍스트가 응답 생성기에 그대로 전달된다.
- [x] CLOSED 상태의 대화에는 지식 검색도, 응답 생성도 호출되지 않는다.

### 5.3 SpringAiResponseGenerator 확장
- [x] 컨텍스트가 있으면 참고 정보와 질문이 함께 프롬프트에 포함된다.
- [x] 컨텍스트가 없으면 질문만 그대로 프롬프트로 전달된다.

## 6. 수동 검증 (실제 앱 기동, `.http` / 브라우저)

- [x] `POST /api/knowledge/documents`로 문서를 등록하고 `POST /api/knowledge/search`로 관련 질의를 검색하면, 관련 문서가 관련 없는 문서보다 높은 score로 반환된다.
- [x] `/api/conversations/{id}/messages`로 메시지를 보내면 로그(`promptLength`)에 검색된 컨텍스트가 프롬프트에 포함된 것이 확인된다(Claude 호출 자체는 유효한 `ANTHROPIC_API_KEY`가 있어야 성공한다).
- [x] `/knowledge` 화면에서 "파일 업로드" 탭으로 전환해 md 파일을 업로드하면 등록되고, 이후 검색에서 해당 내용이 조회된다.

## 구현 순서 (TDD)

1. `domain`: `Document` → `DocumentChunk` → `DocumentChunker` → `EmbeddingVector` → `EmbeddedChunk`/`RetrievedChunk` → `EmbeddingGenerator`/`KnowledgeRepository`(포트)
2. `application`: `IndexDocumentUseCase` → `SearchKnowledgeUseCase` (포트는 목으로 대체)
3. `infrastructure`: `SpringAiEmbeddingGenerator` → `PgVectorKnowledgeRepository` → `KnowledgeConfig`
4. `presentation`: `KnowledgeController` → `KnowledgePageController`
5. `conversation` 통합: `KnowledgeRetriever`(포트) → `SendMessageUseCase`/`ResponseGenerator` 확장 → `KnowledgeSearchRetriever`(구현체) → `ConversationConfig` 재와이어링
6. 파일 업로드: `DocumentFileType`(포트 아님, 값 객체) → `DocumentContentExtractor`(포트) → `IndexDocumentFileUseCase` → `ApacheDocumentContentExtractor` → `KnowledgeController`(multipart 엔드포인트) → `knowledge.html`(업로드 폼)

각 시나리오는 "실패하는 테스트 작성(Red) → 최소 구현으로 통과(Green) → 구조 정리(Refactor)" 순서로 하나씩 완료 처리했다.
