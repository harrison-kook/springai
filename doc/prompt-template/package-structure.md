# PromptTemplate 도메인 패키지 구조

`doc/conversation`과 동일한 DDD 4계층 구조를 적용한 두 번째 바운디드 컨텍스트입니다. 이름을 가진 프롬프트 템플릿을 등록하고, `{{변수}}` 형태의 placeholder를 값으로 치환해 AI에게 보낼 프롬프트 문자열을 만들어내는 기능을 다룹니다.

## 패키지 구조

```
com.tororang.springai.prompttemplate
├── domain
│   ├── PromptTemplate.java                    # 엔티티: name + content, render(변수 치환) 로직 보유
│   ├── PromptTemplateRepository.java          # 포트: 저장/조회 인터페이스
│   └── exception
│       ├── MissingTemplateVariableException.java
│       ├── DuplicateTemplateNameException.java
│       └── PromptTemplateNotFoundException.java
│
├── application
│   ├── RegisterPromptTemplateUseCase.java     # 유스케이스: 새 템플릿 등록 (이름 중복 검사)
│   ├── RenderPromptTemplateUseCase.java       # 유스케이스: 이름 + 변수로 렌더링된 문자열 생성
│   └── dto
│       ├── RegisterPromptTemplateCommand.java
│       └── RenderPromptTemplateCommand.java
│
├── infrastructure
│   ├── InMemoryPromptTemplateRepository.java  # PromptTemplateRepository 초기 구현
│   └── PromptTemplateConfig.java              # 포트 ↔ 구현체, 유스케이스 빈 와이어링
│
└── presentation
    ├── PromptTemplateController.java
    └── dto
        ├── RegisterPromptTemplateRequest.java / RegisterPromptTemplateResponse.java
        └── RenderPromptTemplateRequest.java / RenderPromptTemplateResponse.java
```

## conversation과의 차이

- 이 도메인은 AI 모델을 직접 호출하지 않으므로(순수 템플릿 렌더링 로직), infrastructure에 `ChatClient` 연동 어댑터가 없습니다. 필요하지 않은 계층 구성 요소는 만들지 않는다는 CLAUDE.md 원칙을 그대로 따른 결과입니다.
- 이름 중복 검사(`existsByName`), 이름 기반 조회(`findByName`)처럼 이 도메인 고유의 포트 메서드가 추가되어 있습니다.

## 계층 의존 방향

```
presentation → application → domain ← infrastructure
```

conversation과 동일하게, domain은 프레임워크에 의존하지 않고 순수 자바로 유지합니다.
