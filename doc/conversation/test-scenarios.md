# Conversation 도메인 테스트 시나리오 (TDD Red 단계)

`doc/conversation/package-structure.md`의 패키지 구조를 기준으로, 실제 구현 코드를 작성하기 전에 먼저 정리한 테스트 케이스 목록입니다. 아래 순서(domain → application → infrastructure → presentation)대로 Red(실패하는 테스트 작성) → Green(최소 구현) → Refactor 사이클을 하나씩 진행합니다.

체크박스는 구현 진행 상태 표시용입니다(`[ ]` 미구현, `[x]` 완료).

## 1. Domain 계층

### 1.1 Message (값 객체)
- [x] 내용이 빈 문자열("")인 메시지를 생성하면 `IllegalArgumentException`이 발생한다.
- [x] 내용이 `null`인 메시지를 생성하면 `IllegalArgumentException`이 발생한다.
- [x] role과 content가 유효하면 정상적으로 `Message`가 생성되고 각 값을 조회할 수 있다.
- [x] role과 content가 동일한 두 `Message`는 `equals`/`hashCode` 기준으로 동등하다.

### 1.2 Conversation (엔티티)
- [x] 새로운 대화를 생성하면 메시지 목록은 비어있다.
- [x] 새로운 대화를 생성하면 상태는 `OPEN`이다.
- [x] `OPEN` 상태의 대화에 메시지를 추가하면 메시지 목록의 마지막에 추가된다.
- [x] 메시지를 여러 번 추가하면 추가한 순서대로 메시지 목록에 쌓인다.
- [x] 대화를 종료(`close()`)하면 상태가 `CLOSED`로 바뀐다.
- [x] `CLOSED` 상태의 대화에 메시지를 추가하면 `ConversationClosedException`이 발생한다.
- [x] 이미 `CLOSED` 상태인 대화를 다시 `close()`하면 `IllegalStateException`이 발생한다.

## 2. Application 계층

테스트에서 `ConversationRepository`, `ResponseGenerator`는 모두 목(mock)으로 대체하여 애플리케이션 서비스의 오케스트레이션 로직만 검증한다.

### 2.1 StartConversationUseCase
- [x] 새 대화 시작을 요청하면 `OPEN` 상태의 새 `Conversation`이 생성되어 저장소에 저장되고, 생성된 대화의 id가 반환된다.

### 2.2 SendMessageUseCase
- [x] 존재하는 `OPEN` 대화에 사용자 메시지를 보내면 사용자 메시지가 대화에 추가된 뒤 저장된다.
- [x] 사용자 메시지 추가 후 `ResponseGenerator`를 호출하여 AI 응답 메시지를 생성하고 대화에 추가한다.
- [x] 최종적으로 사용자 메시지와 AI 응답 메시지가 모두 포함된 대화 결과가 반환된다.
- [x] 존재하지 않는 대화 id로 메시지를 보내면 `ConversationNotFoundException`이 발생한다.
- [x] `CLOSED` 상태의 대화에 메시지를 보내면 `ConversationClosedException`이 그대로 전파된다.
- [x] `ResponseGenerator` 호출이 예외를 던지면 `SendMessageUseCase`도 예외를 전파하며, 저장소에는 사용자 메시지가 반영되지 않는다(트랜잭션 롤백).

## 3. Infrastructure 계층 (슬라이스/통합 테스트)

### 3.1 SpringAiResponseGenerator
- [x] `ChatClient`(목 대체)가 정상 텍스트를 반환하면 `ResponseGenerator`는 이를 role=AI인 `Message`로 변환하여 반환한다.
- [x] `ChatClient` 호출 중 예외가 발생하면 도메인에 정의된 예외(`AiResponseGenerationException` 등)로 변환하여 던진다.

> 실제 모델을 호출하지 않고 `ChatClient`를 목으로 대체하여 검증한다. 실제 모델 호출 테스트가 필요하면 `@Tag("integration")`으로 별도 분리한다.

### 3.2 InMemoryConversationRepository
- [x] `save` 후 `findById`로 동일한 `Conversation`을 조회할 수 있다.
- [x] 존재하지 않는 id로 `findById`를 호출하면 빈 `Optional`을 반환한다.

## 4. Presentation 계층 (MockMvc 슬라이스 테스트)

### 4.1 ConversationController
- [x] `POST /api/conversations` 요청 시 `201 Created`와 함께 생성된 대화 id를 응답 본문에 포함하여 반환한다.
- [x] `POST /api/conversations/{id}/messages` 요청 시 `200 OK`와 함께 사용자 메시지 + AI 응답이 포함된 응답 본문을 반환한다.
- [x] 존재하지 않는 대화 id로 메시지 전송을 요청하면 `404 Not Found`를 반환한다.
- [x] 빈 content로 메시지 전송을 요청하면 `400 Bad Request`를 반환한다.

## 구현 순서 (TDD)

1. `domain`: `Message` → `Conversation`
2. `application`: `StartConversationUseCase` → `SendMessageUseCase` (Repository/ResponseGenerator는 목으로 대체)
3. `infrastructure`: `InMemoryConversationRepository` → `SpringAiResponseGenerator`
4. `presentation`: `ConversationController`

각 시나리오는 "실패하는 테스트 작성(Red) → 최소 구현으로 통과(Green) → 구조 정리(Refactor)" 순서로 하나씩 완료 처리하며, 위 체크박스를 갱신합니다.