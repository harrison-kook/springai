# Conversation 도메인 패키지 구조 예시

`CLAUDE.md`의 DDD 아키텍처 원칙을 적용한 예시 바운디드 컨텍스트입니다. 사용자가 메시지를 보내면 AI가 응답하는 "대화(Conversation)" 기능을 도메인으로 표현합니다.

아직 코드는 작성하지 않았습니다. `doc/conversation/test-scenarios.md`의 테스트 시나리오를 확인/합의한 뒤, 아래 구조를 기준으로 TDD(Red → Green → Refactor)로 구현을 진행할 예정입니다.

## 패키지 구조 (예정)

```
com.tororang.springai.conversation
├── domain
│   ├── Conversation.java              # 엔티티: 대화 단위, 메시지 목록과 상태(OPEN/CLOSED) 보유
│   ├── Message.java                   # 값 객체: role + content
│   ├── MessageRole.java               # enum: USER, AI
│   ├── ConversationRepository.java    # 포트: 저장/조회 인터페이스
│   ├── ResponseGenerator.java         # 포트: AI 응답 생성 인터페이스
│   └── exception
│       ├── ConversationClosedException.java
│       └── ConversationNotFoundException.java
│
├── application
│   ├── StartConversationUseCase.java  # 유스케이스: 새 대화 시작
│   ├── SendMessageUseCase.java        # 유스케이스: 메시지 전송 + AI 응답 생성
│   └── dto
│       ├── SendMessageCommand.java
│       └── ConversationResult.java
│
├── infrastructure
│   ├── InMemoryConversationRepository.java  # ConversationRepository 초기 구현 (추후 JPA 등으로 교체 가능)
│   ├── SpringAiResponseGenerator.java       # ResponseGenerator 구현체, 내부에서 ChatClient 사용
│   └── ConversationConfig.java              # 포트(도메인 인터페이스) ↔ 구현체 빈 와이어링, ChatModel은 anthropicChatModel로 고정
│
└── presentation
    ├── ConversationController.java
    └── dto
        ├── SendMessageRequest.java
        └── ConversationResponse.java
```

## 계층 의존 방향

```
presentation → application → domain ← infrastructure
```

- `domain`은 Spring, Spring AI 등 프레임워크에 의존하지 않는다. `ConversationRepository`, `ResponseGenerator`는 domain에 정의된 포트(인터페이스)이며 구현체는 infrastructure에 위치한다.
- `application`은 포트 인터페이스만 사용하며 구현체(`InMemoryConversationRepository`, `SpringAiResponseGenerator`)를 직접 알지 못한다(생성자 주입, 의존성 역전).
- `ChatClient` 등 Spring AI 관련 코드는 `SpringAiResponseGenerator` 내부에서만 사용되며, 도메인 계층에는 벤더 세부사항이 노출되지 않는다.
- `presentation`은 요청 검증과 application 서비스 호출만 담당한다.