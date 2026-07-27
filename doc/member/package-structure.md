# Member 도메인 패키지 구조

`doc/conversation`, `doc/prompt-template`과 동일한 DDD 4계층 구조를 적용한 세 번째 바운디드 컨텍스트입니다. 이메일과 이름을 가진 회원을 등록하고 조회하는 기능을 다룹니다.

## 패키지 구조

```
com.tororang.springai.member
├── domain
│   ├── Member.java                    # 엔티티: email + name, changeName(이름 변경) 로직 보유
│   ├── MemberRepository.java          # 포트: 저장/조회 인터페이스
│   └── exception
│       ├── DuplicateEmailException.java
│       └── MemberNotFoundException.java
│
├── application
│   ├── RegisterMemberUseCase.java     # 유스케이스: 새 회원 등록 (이메일 중복 검사)
│   ├── FindMemberUseCase.java         # 유스케이스: id로 회원 조회
│   └── dto
│       ├── RegisterMemberCommand.java
│       └── MemberResult.java
│
├── infrastructure
│   ├── InMemoryMemberRepository.java  # MemberRepository 초기 구현
│   └── MemberConfig.java              # 포트 ↔ 구현체, 유스케이스 빈 와이어링
│
└── presentation
    ├── MemberController.java
    └── dto
        ├── RegisterMemberRequest.java / RegisterMemberResponse.java
        └── MemberResponse.java
```

## conversation / prompt-template과의 차이

- 이 도메인은 AI 호출도, 텍스트 렌더링도 없는 가장 단순한 CRUD성 도메인입니다. infrastructure에는 순수 저장소 구현체만 있고, `PromptTemplate`의 이름 중복 검사와 유사하게 `email` 중복 검사(`existsByEmail`)를 포트에 둡니다.
- presentation에 조회용 `GET` 엔드포인트가 처음 등장합니다(`GET /api/members/{id}`). 존재하지 않는 리소스에 대한 404 처리를 다룹니다.

## 계층 의존 방향

```
presentation → application → domain ← infrastructure
```

conversation, prompt-template과 동일하게 domain은 프레임워크에 의존하지 않고 순수 자바로 유지합니다.
