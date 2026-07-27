# PromptTemplate 도메인 테스트 시나리오 (TDD Red 단계)

`doc/prompt-template/package-structure.md`의 패키지 구조를 기준으로, 실제 구현 코드를 작성하기 전에 먼저 정리한 테스트 케이스 목록입니다. `doc/conversation/test-scenarios.md`와 동일하게 domain → application → infrastructure → presentation 순서로 Red → Green 사이클을 진행합니다.

체크박스는 구현 진행 상태 표시용입니다(`[ ]` 미구현, `[x]` 완료).

## 1. Domain 계층

### 1.1 PromptTemplate (엔티티)
- [x] 이름이 빈 문자열이거나 `null`이면 `IllegalArgumentException`이 발생한다.
- [x] content가 빈 문자열이거나 `null`이면 `IllegalArgumentException`이 발생한다.
- [x] 이름과 content가 유효하면 정상적으로 생성되고 id/name/content를 조회할 수 있다.
- [x] placeholder(`{{변수}}`)가 없는 content를 렌더링하면 원본 문자열이 그대로 반환된다.
- [x] 단일 placeholder가 있는 content를 변수 맵으로 렌더링하면 값이 치환된다.
- [x] 여러 placeholder가 있는 content를 렌더링하면 모두 치환된다.
- [x] 렌더링에 필요한 변수가 변수 맵에 없으면 `MissingTemplateVariableException`이 발생한다.

## 2. Application 계층

테스트에서 `PromptTemplateRepository`는 목(mock)으로 대체하여 애플리케이션 서비스의 오케스트레이션 로직만 검증한다.

### 2.1 RegisterPromptTemplateUseCase
- [x] 새 템플릿 등록을 요청하면 `PromptTemplate`이 저장소에 저장되고 생성된 id가 반환된다.
- [x] 이미 존재하는 이름으로 등록을 요청하면 `DuplicateTemplateNameException`이 발생하고 저장소에는 저장되지 않는다.

### 2.2 RenderPromptTemplateUseCase
- [x] 존재하는 템플릿 이름과 변수로 렌더링을 요청하면 치환된 문자열이 반환된다.
- [x] 존재하지 않는 템플릿 이름으로 요청하면 `PromptTemplateNotFoundException`이 발생한다.
- [x] 필요한 변수가 누락되면 도메인에서 발생한 `MissingTemplateVariableException`이 그대로 전파된다.

## 3. Infrastructure 계층

### 3.1 InMemoryPromptTemplateRepository
- [x] `save` 후 `findById`로 동일한 템플릿을 조회할 수 있다.
- [x] 존재하지 않는 id로 `findById`를 호출하면 빈 `Optional`을 반환한다.
- [x] `save` 후 `findByName`으로 동일한 템플릿을 조회할 수 있다.
- [x] 존재하지 않는 이름으로 `findByName`을 호출하면 빈 `Optional`을 반환한다.
- [x] 저장된 이름에 대해 `existsByName`은 `true`, 저장되지 않은 이름에 대해서는 `false`를 반환한다.

## 4. Presentation 계층 (MockMvc 슬라이스 테스트)

### 4.1 PromptTemplateController
- [x] `POST /api/prompt-templates` 요청 시 `201 Created`와 함께 생성된 템플릿 id를 반환한다.
- [x] 이름 또는 content가 비어있는 등록 요청 시 `400 Bad Request`를 반환한다.
- [x] 이미 존재하는 이름으로 등록 요청 시 `409 Conflict`를 반환한다.
- [x] `POST /api/prompt-templates/{name}/render` 요청 시 `200 OK`와 함께 렌더링된 문자열을 반환한다.
- [x] 존재하지 않는 템플릿 이름으로 렌더링을 요청하면 `404 Not Found`를 반환한다.
- [x] 필요한 변수가 누락된 렌더링 요청 시 `400 Bad Request`를 반환한다.

## 구현 순서 (TDD)

1. `domain`: `PromptTemplate`
2. `application`: `RegisterPromptTemplateUseCase` → `RenderPromptTemplateUseCase` (Repository는 목으로 대체)
3. `infrastructure`: `InMemoryPromptTemplateRepository` → `PromptTemplateConfig`
4. `presentation`: `PromptTemplateController`

각 시나리오는 "실패하는 테스트 작성(Red) → 최소 구현으로 통과(Green) → 구조 정리(Refactor)" 순서로 하나씩 완료 처리하며, 위 체크박스를 갱신합니다.
