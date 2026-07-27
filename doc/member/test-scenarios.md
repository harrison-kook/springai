# Member 도메인 테스트 시나리오 (TDD Red 단계)

`doc/member/package-structure.md`의 패키지 구조를 기준으로, 실제 구현 코드를 작성하기 전에 먼저 정리한 테스트 케이스 목록입니다. `doc/conversation`, `doc/prompt-template`과 동일하게 domain → application → infrastructure → presentation 순서로 Red → Green 사이클을 진행합니다.

체크박스는 구현 진행 상태 표시용입니다(`[ ]` 미구현, `[x]` 완료).

## 1. Domain 계층

### 1.1 Member (엔티티)
- [x] email이 빈 문자열이거나 `null`이면 `IllegalArgumentException`이 발생한다.
- [x] email에 `@`가 없으면 `IllegalArgumentException`이 발생한다.
- [x] name이 빈 문자열이거나 `null`이면 `IllegalArgumentException`이 발생한다.
- [x] email과 name이 유효하면 정상적으로 생성되고 id/email/name을 조회할 수 있다.
- [x] `changeName`을 호출하면 name이 새 값으로 갱신된다.
- [x] `changeName`에 빈 문자열을 전달하면 `IllegalArgumentException`이 발생한다.

## 2. Application 계층

테스트에서 `MemberRepository`는 목(mock)으로 대체하여 애플리케이션 서비스의 오케스트레이션 로직만 검증한다.

### 2.1 RegisterMemberUseCase
- [x] 새 회원 등록을 요청하면 `Member`가 저장소에 저장되고 생성된 id가 반환된다.
- [x] 이미 존재하는 이메일로 등록을 요청하면 `DuplicateEmailException`이 발생하고 저장소에는 저장되지 않는다.

### 2.2 FindMemberUseCase
- [x] 존재하는 id로 조회하면 email/name이 포함된 결과가 반환된다.
- [x] 존재하지 않는 id로 조회하면 `MemberNotFoundException`이 발생한다.

## 3. Infrastructure 계층

### 3.1 InMemoryMemberRepository
- [x] `save` 후 `findById`로 동일한 회원을 조회할 수 있다.
- [x] 존재하지 않는 id로 `findById`를 호출하면 빈 `Optional`을 반환한다.
- [x] `save` 후 `findByEmail`으로 동일한 회원을 조회할 수 있다.
- [x] 저장된 이메일에 대해 `existsByEmail`은 `true`, 저장되지 않은 이메일에 대해서는 `false`를 반환한다.

## 4. Presentation 계층 (MockMvc 슬라이스 테스트)

### 4.1 MemberController
- [x] `POST /api/members` 요청 시 `201 Created`와 함께 생성된 회원 id를 반환한다.
- [x] 이메일 형식이 올바르지 않은 등록 요청 시 `400 Bad Request`를 반환한다.
- [x] 이미 존재하는 이메일로 등록 요청 시 `409 Conflict`를 반환한다.
- [x] `GET /api/members/{id}` 요청 시 `200 OK`와 함께 회원 정보를 반환한다.
- [x] 존재하지 않는 id로 조회하면 `404 Not Found`를 반환한다.

## 구현 순서 (TDD)

1. `domain`: `Member`
2. `application`: `RegisterMemberUseCase` → `FindMemberUseCase` (Repository는 목으로 대체)
3. `infrastructure`: `InMemoryMemberRepository` → `MemberConfig`
4. `presentation`: `MemberController`

각 시나리오는 "실패하는 테스트 작성(Red) → 최소 구현으로 통과(Green) → 구조 정리(Refactor)" 순서로 하나씩 완료 처리하며, 위 체크박스를 갱신합니다.
