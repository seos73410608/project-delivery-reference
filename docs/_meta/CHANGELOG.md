# CHANGELOG

> PMIS(Project Management Information System) 변경 이력 관리 문서  
> 프로젝트 진행 과정에서 발생한 주요 변경 사항과 릴리즈 내역을 기록한다.

---

# 변경 이력 관리 원칙

본 문서는 프로젝트의 모든 주요 변경 사항을 기록한다.

기록 대상은 다음과 같다.

- 기능 추가 (Feature)
- 기능 개선 (Improvement)
- 버그 수정 (Bug Fix)
- 리팩토링 (Refactoring)
- 성능 개선 (Performance)
- 보안 강화 (Security)
- 문서 변경 (Documentation)
- 의존성 변경 (Build)

---

# Versioning

본 프로젝트는 Semantic Versioning(SemVer)을 따른다.

```
MAJOR.MINOR.PATCH
```

예시

```
1.0.0
│ │ └─ Patch
│ └──── Minor
└────── Major
```

- **Major** : 호환되지 않는 변경
- **Minor** : 새로운 기능 추가
- **Patch** : 버그 수정 및 개선

---

# Release History

## v0.1.0 (Planning)

### 프로젝트 초기 구성

#### Added

- Spring Boot 프로젝트 생성
- Java 21 적용
- Gradle Kotlin DSL 적용
- GitHub Repository 생성
- 기본 프로젝트 구조 생성

---

## v0.2.0 (Project Structure)

### 패키지 구조 설계

#### Added

- Domain 중심 패키지 구성
- auth
- user
- project
- schedule
- wbs
- issue
- risk
- change
- report
- dashboard
- cmdb
- file
- batch
- common

#### Added

공통 패키지

- annotation
- constant
- dto
- enums
- util
- validation

---

## v0.3.0 (Common Module)

### 공통 모듈 구축

#### Added

- BaseEntity
- ApiResponse
- ErrorCode
- BusinessException
- GlobalExceptionHandler
- JpaConfig

#### Changed

- API 응답 표준화
- 예외 처리 구조 통일

---

## v0.4.0 (Security)

### 인증 및 보안

#### Added

- Spring Security
- JWT 인증
- BCrypt Password Encoder
- Authentication Filter
- JWT Provider
- UserDetails 구현

#### Changed

- Stateless Authentication 적용

---

## v0.5.0 (User)

### 사용자 관리

#### Planned

- 회원가입
- 로그인
- 사용자 조회
- 권한 관리
- 비밀번호 변경

---

## v0.6.0 (Project)

### 프로젝트 관리

#### Planned

- 프로젝트 생성
- 프로젝트 수정
- 프로젝트 종료
- 프로젝트 상태 관리

---

## v0.7.0 (Schedule)

### 일정 관리

#### Planned

- 일정 등록
- 일정 변경
- 일정 진척률
- 일정 조회

---

## v0.8.0 (WBS)

### 작업 관리

#### Planned

- WBS 생성
- 작업 등록
- 담당자 지정
- 진척률 계산

---

## v0.9.0 (Issue)

### 이슈 관리

#### Planned

- 이슈 등록
- 담당자 지정
- 조치 관리
- 완료 처리

---

## v0.10.0 (Risk)

### 리스크 관리

#### Planned

- Risk 등록
- Impact 분석
- 대응 전략
- 상태 관리

---

## v0.11.0 (Change)

### 변경 관리

#### Planned

- 변경 요청
- 영향도 분석
- 승인 프로세스
- 변경 이력 관리

---

## v0.12.0 (Dashboard)

### Dashboard

#### Planned

- 프로젝트 현황
- 일정 현황
- 이슈 현황
- 리스크 현황
- KPI Dashboard

---

## v0.13.0 (CMDB)

### Configuration Management

#### Planned

- CI 관리
- Server CI
- Database CI
- Software CI
- 관계 관리

---

## v0.14.0 (Report)

### 보고서

#### Planned

- 주간 보고서
- 월간 보고서
- 완료 보고서
- PDF 출력
- Excel 출력

---

## v0.15.0 (Spring AI)

### AI 기능

#### Planned

- AI PM Assistant
- 프로젝트 요약
- 일정 예측
- 리스크 예측
- 변경 영향도 분석
- 회의록 요약
- 보고서 자동 생성

---

# Git Commit Convention

본 프로젝트는 Conventional Commits를 사용한다.

| Prefix | 설명 |
|---------|------|
| feat | 새로운 기능 |
| fix | 버그 수정 |
| refactor | 리팩토링 |
| docs | 문서 변경 |
| style | 코드 스타일 |
| test | 테스트 |
| build | Build 변경 |
| chore | 기타 변경 |
| perf | 성능 개선 |
| security | 보안 관련 |

예시

```text
feat: add project management API

fix: resolve login authentication issue

refactor: improve exception handling

docs: update development roadmap

build: upgrade Spring Boot dependencies

chore: initialize project package structure
```

---

# 브랜치 전략

Git Flow 기반으로 브랜치를 운영한다.

```
main
│
develop
│
├── feature/security
├── feature/user
├── feature/project
├── feature/wbs
├── feature/issue
├── feature/risk
├── feature/change
├── feature/report
└── feature/spring-ai
```

브랜치 역할

- **main** : 운영(Release)
- **develop** : 통합 개발
- **feature/** : 기능 개발
- **hotfix/** : 긴급 수정
- **release/** : 릴리즈 준비

---

# 변경 기록 작성 규칙

각 변경 사항은 아래 형식으로 기록한다.

```markdown
## v0.x.x

### YYYY-MM-DD

#### Added

- 새로운 기능

#### Changed

- 변경 사항

#### Fixed

- 버그 수정

#### Removed

- 제거된 기능
```

---

# 향후 릴리즈 계획

| Version | 내용 |
|----------|------|
| v0.5.0 | 사용자 관리 |
| v0.6.0 | 프로젝트 관리 |
| v0.7.0 | 일정 관리 |
| v0.8.0 | WBS |
| v0.9.0 | 이슈 관리 |
| v0.10.0 | 리스크 관리 |
| v0.11.0 | 변경 관리 |
| v0.12.0 | Dashboard |
| v0.13.0 | CMDB |
| v0.14.0 | 보고서 |
| v0.15.0 | Spring AI |

---

# 프로젝트 목표 버전

| Version | 목표 |
|----------|------|
| 0.x | 기능 개발 |
| 1.0 | PMIS 핵심 기능 완료 |
| 1.5 | Dashboard 및 CMDB 고도화 |
| 2.0 | Spring AI 기반 AI PM Assistant 적용 |
| 3.0 | Enterprise PMIS Platform 완성 |

---

# 유지보수 정책

- 모든 변경 사항은 CHANGELOG에 기록한다.
- 모든 기능은 Feature Branch에서 개발한다.
- Pull Request 검토 후 Develop 브랜치에 병합한다.
- 테스트 완료 후 Main 브랜치에 릴리즈한다.
- 버전 태그(Tag)를 생성하여 릴리즈 이력을 관리한다.

---

**Last Updated** : 2026-08-03
**Project** : PMIS (Project Management Information System)
**Maintainer** : SEO S
**License** : MIT