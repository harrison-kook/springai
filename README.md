# SpringAI

Spring AI(Anthropic Claude)를 사용해 DDD 아키텍처와 TDD 개발 방식을 연습하기 위한 예제 프로젝트입니다. 개발 컨벤션은 [CLAUDE.md](./CLAUDE.md)에, 각 도메인의 설계 배경과 테스트 시나리오는 [doc/](./doc) 폴더에 정리되어 있습니다.

## 기술 스택

- Spring Boot 4.1.0 (Java 26)
- Spring AI 2.0.0 (Anthropic Claude, Ollama Embedding)
- Spring MVC + Thymeleaf
- PostgreSQL + pgvector (RAG 벡터 저장소, JdbcTemplate + pgvector-java로 직접 연동)
- Gradle
- JUnit 5, AssertJ, Mockito

## 도메인 구성

각 도메인은 `domain / application / infrastructure / presentation` 4계층 구조로 구현되어 있습니다.

| 도메인 | 설명 | REST API | 예제 페이지 |
| --- | --- | --- | --- |
| `conversation` | Claude와 대화를 주고받는 도메인 (knowledge 검색 결과를 컨텍스트로 활용하는 RAG 응답 포함) | `POST /api/conversations`, `POST /api/conversations/{id}/messages` | `/chat` |
| `prompt-template` | `{{변수}}` placeholder를 가진 프롬프트 템플릿을 등록/렌더링하는 도메인 | `POST /api/prompt-templates`, `POST /api/prompt-templates/{name}/render` | `/prompt-templates` |
| `member` | 회원을 등록/조회하는 도메인 | `POST /api/members`, `GET /api/members/{id}` | `/members` |
| `knowledge` | 문서를 청킹/임베딩해 pgvector에 저장하고 유사도 검색하는 RAG 색인/검색 도메인 | `POST /api/knowledge/documents`, `POST /api/knowledge/search` | `/knowledge` |

각 도메인의 패키지 구조와 테스트 시나리오는 `doc/{도메인}/package-structure.md`, `doc/{도메인}/test-scenarios.md`를 참고하세요.

## 시작하기

### 1. Anthropic API 키 설정

`conversation` 도메인이 실제 Claude API를 호출하므로 환경변수로 API 키를 설정해야 합니다.

```powershell
# PowerShell
$env:ANTHROPIC_API_KEY = "sk-ant-..."
```

```bash
# Git Bash
export ANTHROPIC_API_KEY="sk-ant-..."
```

키가 없어도 앱은 기동되지만(`prompt-template`, `member`, `knowledge` 도메인은 Claude를 호출하지 않음), `/chat`에서 메시지를 전송하는 시점에 실패합니다.

### 2. RAG(knowledge) 로컬 실행 준비

`knowledge` 도메인은 로컬 Postgres(pgvector 확장 포함)와 Ollama(임베딩 모델)가 필요합니다. 없어도 앱은 기동되지만 `/knowledge`, 그리고 `/chat`의 RAG 컨텍스트 조회 시점에 실패합니다.

**Postgres + pgvector (Docker)**

Windows용 pgvector는 별도 빌드가 필요해서, pgvector가 이미 포함된 공식 Docker 이미지를 사용하는 걸 권장합니다.

```powershell
docker run -d --name springai-pgvector `
  -e POSTGRES_PASSWORD=<비밀번호> `
  -e POSTGRES_DB=springai_rag `
  -p 5432:5432 `
  -v springai_pgvector_data:/var/lib/postgresql/data `
  pgvector/pgvector:pg16
```

컨테이너가 뜨면 `springai_rag` DB에 `src/main/resources/db/knowledge-schema.sql`을 1회 실행합니다(`CREATE EXTENSION vector` + `knowledge_chunk` 테이블 생성). 접속 정보는 환경변수로 오버라이드할 수 있습니다(기본값은 `application.yaml` 참고).

```powershell
$env:POSTGRES_URL = "jdbc:postgresql://localhost:5432/springai_rag"
$env:POSTGRES_USERNAME = "postgres"
$env:POSTGRES_PASSWORD = "<비밀번호>"
```

**Ollama (임베딩 모델)**

[ollama.com](https://ollama.com/download)에서 설치 후, 이 프로젝트가 쓰는 임베딩 모델을 받습니다.

```bash
ollama pull nomic-embed-text
```

기본적으로 `http://localhost:11434`를 사용하며, `OLLAMA_BASE_URL` 환경변수로 오버라이드할 수 있습니다.

### 3. 실행

```bash
./gradlew bootRun
```

기본 포트는 `8080`이며, 브라우저에서 `http://localhost:8080`에 접속하면 각 도메인 예제 페이지로 이동할 수 있는 홈 화면이 표시됩니다.

### 4. 테스트

```bash
./gradlew test            # 단위/슬라이스 테스트 (외부 연동 불필요)
./gradlew integrationTest # 실제 Postgres/Ollama에 붙는 통합 테스트 (@Tag("integration"))
```

## 개발 방식

- **TDD**: 모든 기능은 실패하는 테스트(Red) → 최소 구현(Green) → 리팩터링(Refactor) 순서로 개발합니다.
- **DDD**: 바운디드 컨텍스트(도메인)별로 패키지를 나누고, domain 계층은 Spring 등 프레임워크에 의존하지 않습니다.

자세한 내용은 [CLAUDE.md](./CLAUDE.md)를 참고하세요.
