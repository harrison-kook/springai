# CLAUDE.md

이 파일은 이 저장소에서 작업하는 Claude Code에게 제공하는 가이드입니다.

## 프로젝트 개요

Spring AI를 활용하는 Spring Boot 애플리케이션입니다. 이제 막 생성된 프로젝트이며, 앞으로의 모든 기능 개발은 **TDD(Test-Driven Development)** 방식과 **DDD(Domain-Driven Design)** 아키텍처를 따릅니다.

- Group: `com.tororang`
- Base package: `com.tororang.springai`
- Spring Boot: 4.1.0
- Spring AI: 2.0.0 (Anthropic, OpenAI 모델 스타터 사용)
- Java: 26 (toolchain)
- Build: Gradle (`gradlew`)
- View: Thymeleaf, Web: Spring MVC
- Lombok 사용

## 빌드 & 테스트 명령어

```bash
./gradlew test          # 전체 테스트 실행
./gradlew test --tests "com.tororang.springai.some.SomeTest"  # 단일 테스트
./gradlew build          # 빌드 (테스트 포함)
./gradlew bootRun        # 애플리케이션 실행
```

Windows 환경이므로 `gradlew.bat`을 사용해도 무방하지만, 기본 셸(Git Bash)에서는 `./gradlew`를 사용합니다.

## 개발 원칙: TDD

**모든 기능 개발은 예외 없이 Red → Green → Refactor 순서로 진행합니다.**

1. **Red**: 실패하는 테스트를 먼저 작성한다. 아직 구현이 없으므로 컴파일 에러 또는 실패가 정상이다.
2. **Green**: 테스트를 통과시키는 가장 단순한 코드를 작성한다. 과설계하지 않는다.
3. **Refactor**: 테스트가 통과하는 상태를 유지하면서 중복 제거, 이름 개선 등 구조를 정리한다.

원칙:
- 프로덕션 코드를 먼저 작성하지 않는다. 테스트 없는 기능 코드는 작성하지 않는다.
- 테스트는 `given-when-then`(또는 `arrange-act-assert`) 구조로 명확히 구분한다.
- 테스트 메서드명은 한글로 시나리오를 설명해도 좋다 (예: `주문이_생성되면_상태는_대기중이다`).
- 도메인 로직은 단위 테스트로, 외부 연동(Spring AI 모델 호출, DB 등)은 별도의 통합 테스트/슬라이스 테스트로 분리한다.
- 외부 API(LLM 호출 등)에 의존하는 도메인 로직은 인터페이스로 추상화하고, 테스트에서는 페이크/목 구현체를 사용한다. 실제 모델 호출 테스트는 최소화하고 명시적으로 분리한다(`@Tag("integration")` 등).
- JUnit 5 + AssertJ를 기본으로 사용한다. Mockito는 꼭 필요한 경계(포트/어댑터 mocking)에서만 사용한다.

## 아키텍처: DDD

바운디드 컨텍스트(도메인) 단위로 패키지를 나누고, 각 컨텍스트 내부는 계층형으로 구성합니다. 계층 간 의존 방향은 항상 **presentation → application → domain ← infrastructure** 이며, `domain`은 다른 계층을 알지 못합니다.

```
com.tororang.springai
├── {context}                    # 바운디드 컨텍스트 (예: chat, conversation, member ...)
│   ├── domain                   # 엔티티, 값 객체, 도메인 서비스, 리포지토리/포트 인터페이스
│   ├── application              # 유스케이스(애플리케이션 서비스), DTO, 트랜잭션 경계
│   ├── infrastructure            # 리포지토리 구현체, Spring AI ChatClient 연동, 외부 API 어댑터
│   └── presentation              # 컨트롤러(REST/MVC), 요청/응답 모델
└── common (또는 global)          # 공통 설정, 예외, 유틸 (특정 도메인에 속하지 않는 것만)
```

계층별 규칙:
- **domain**: 순수 자바 + Lombok만 허용. Spring, Spring AI, JPA 등 프레임워크 어노테이션을 두지 않는다. 외부 연동이 필요하면 domain에 인터페이스(포트)만 정의한다.
- **application**: 유스케이스를 표현하는 서비스. 트랜잭션 경계는 여기서 관리한다. domain의 인터페이스를 호출하고, infrastructure 구현체는 알지 못한다(의존성 역전, 생성자 주입).
- **infrastructure**: domain에 정의된 포트의 구현체. `ChatClient`, `VectorStore` 등 Spring AI 관련 코드는 반드시 이 계층에 위치하며, 도메인에 벤더/모델 세부사항이 새어나가지 않도록 감싼다.
- **presentation**: 컨트롤러. 요청 검증과 application 서비스 호출만 담당하고 비즈니스 로직을 두지 않는다.
- 새 바운디드 컨텍스트를 추가할 때만 위 4개 하위 패키지를 생성하고, 불필요한 계층(예: 아직 infrastructure가 필요 없는 경우)은 미리 만들지 않는다.

## Spring AI 사용 가이드라인

- `ChatClient`, `ChatModel` 등 Spring AI 컴포넌트는 `infrastructure` 계층의 어댑터 클래스 안에서만 직접 사용한다.
- 도메인/애플리케이션 계층은 Spring AI가 아닌, 도메인 언어로 정의된 자체 포트 인터페이스(예: `ResponseGenerator`, `Summarizer`)를 통해 AI 기능을 사용한다.
- 프롬프트 템플릿, 모델 파라미터(temperature 등)는 infrastructure 계층 또는 설정 파일에 둔다.
- 실제 모델 호출은 비용/비결정성이 있으므로, 단위 테스트에서는 포트 인터페이스를 목/페이크로 대체하고 실제 호출 테스트는 명시적으로 분리해 최소한으로만 작성한다.

## 코딩 컨벤션

- Lombok: `@Getter`, `@RequiredArgsConstructor`, `@Builder` 등은 적극 활용하되, `@Data`는 엔티티에 사용하지 않는다(불변식이 깨지기 쉬움).
- 생성자 주입만 사용한다(필드 주입 금지).
- 불필요한 추상화/설정 플래그/과도한 방어 코드를 추가하지 않는다. 요구되지 않은 범용성을 미리 설계하지 않는다.
- 커밋 단위는 Red-Green-Refactor 사이클에 맞춰 작게 유지하는 것을 권장한다(사용자가 명시적으로 커밋을 요청할 때만 커밋한다).

# 데이터 접근 계층 규칙 (JPA / MyBatis 선택 기준)

이 프로젝트는 JPA(+QueryDSL)를 기본으로 하고, 조회 특성에 따라 MyBatis/네이티브 쿼리를 병행하는 하이브리드 구조다. 데이터 접근 코드를 작성하거나 수정할 때 아래 기준을 따른다.

## 기본 원칙

- 쓰기(Command)와 도메인 상태 변경은 JPA를 사용한다.
- 화면 전용 조회, 집계, 대량 처리는 MyBatis 또는 네이티브 쿼리를 사용한다.
- 어느 쪽을 쓸지 애매하면 아래 판정 순서를 적용하고, 판단 근거를 코드 리뷰에서 설명할 수 있어야 한다.

## 판정 순서

1. 데이터를 읽은 뒤 상태를 변경하는가? → **JPA** (더티 체킹 활용)
2. 조회 전용이라면, 결과가 도메인 엔티티 형태인가 화면 전용 DTO인가?
   - 엔티티 형태이고 join 3개 이하 → **JPA / QueryDSL**
   - 화면 전용 DTO → **DTO 프로젝션 또는 MyBatis**
3. SQL 자체를 튜닝(실행계획, 힌트)할 가능성이 있는가? → 처음부터 **MyBatis/네이티브**

## JPA(+QueryDSL)로 작성

- 단건/소량 조회 후 상태 변경이 따라오는 로직
- join 2~3개 이내, 결과가 엔티티 그래프로 자연스러운 조회
- 동적 조건 검색 (QueryDSL로 타입 안전하게 조합)
- 연관 엔티티 접근 시 N+1이 발생하지 않도록 fetch join 또는 @EntityGraph를 명시한다.
- 대량 조회 시 엔티티 대신 DTO 프로젝션을 우선 검토한다.

## MyBatis/네이티브로 작성

- 집계·통계: GROUP BY, 윈도우 함수, 서브쿼리 중첩
- join 4개 이상 또는 특정 엔티티에 속하지 않는 화면 전용 조회
- DB 특화 기능: 힌트, CTE, UPSERT, 파티션 지정
- 대량 UPDATE/DELETE (JPA 벌크 연산 대신 SQL로 명시)
- 성능 튜닝 대상으로 지목된 쿼리

## 코드 배치 규칙

- 쓰기: `*.repository` 패키지의 JPA Repository
- 조회 전용: `*.repository.query` 패키지의 `~QueryRepository` 또는 `*.mapper` 패키지의 MyBatis Mapper
- 하나의 Repository에 JPA와 MyBatis 로직을 섞지 않는다.
- JPQL로 작성한 쿼리가 튜닝 대상이 되면 역추적하지 말고 MyBatis/네이티브로 이관한다.

## 금지 사항

- 루프 안에서 LAZY 연관 엔티티에 접근하는 코드 (N+1)
- 조회 전용 로직에서 영속성 컨텍스트에 대량 엔티티 적재
- "복잡해 보여서"라는 이유만으로 기준 없이 MyBatis로 작성하는 것 — 판정 순서를 먼저 적용한다.
