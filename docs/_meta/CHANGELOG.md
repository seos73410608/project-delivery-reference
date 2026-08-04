# CHANGELOG

> PMIS(Project Management Information System) 변경 이력 관리 문서  
> 프로젝트 진행 과정에서 발생한 주요 변경 사항과 릴리즈 내역을 기록한다.

---

# 변경 이력 관리 원칙

본 문서는 프로젝트의 모든 주요 변경 사항을 기록한다.

기록 대상은 다음과 같다.

- Feature
- Improvement
- Bug Fix
- Refactoring
- Security
- Performance
- Documentation
- Build

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

---

## v0.1.0 (2026-08-03)

### Project Initialization

#### Added

- Spring Boot 프로젝트 생성
- Java 21 적용
- Gradle Kotlin DSL 적용
- MariaDB 연동
- Git Repository 생성
- Git Flow 브랜치 전략 적용
- 기본 프로젝트 구조 생성

---

## v0.2.0 (2026-08-03)

### Common Infrastructure

#### Added

- Domain 중심 Package 구조
- BaseEntity
- ApiResponse
- ErrorCode
- CommonErrorCode
- BusinessException
- GlobalExceptionHandler
- JpaConfig
- WebConfig

#### Changed

- API 응답 표준화
- 공통 예외 처리 구조 적용

---

## v0.3.0 (2026-08-04)

### Security Infrastructure

#### Added

- Spring Security 적용
- JWT Provider
- JwtAuthenticationFilter
- JwtAuthenticationEntryPoint
- JwtAccessDeniedHandler
- UserPrincipal
- BCrypt PasswordEncoder
- SecurityConfig
- Stateless Authentication
- CORS 설정
- JWT Property 구성

#### Added

Test API

- GET /api/test/public
- GET /api/test/private
- GET /api/test/token

#### Changed

- Session 기반 인증 제거
- JWT 기반 인증 구조 적용

---

## v0.3.1 (2026-08-04)

### Security Verification

#### Added

- JWT Access Token 생성 테스트
- Authorization Header 기반 인증 테스트
- Protected API 접근 테스트

#### Fixed

- Spring Boot 4.x → 3.5.x 변경
- JWT 라이브러리 호환성 수정
- ObjectMapper 의존성 문제 해결
- Security Handler 개선

---

## v0.4.0 (Planned)

### Authentication

#### Planned

- User Entity
- Role Entity
- Login API
- Logout API
- Refresh Token
- Token Reissue
- UserDetailsService
- AuthenticationManager

---

## v0.5.0 (Planned)

### Project Management

#### Planned

- Project CRUD
- Project Search
- Pagination
- Specification
- Project Status

---

## v0.6.0 (Planned)

### WBS & Schedule

#### Planned

- WBS CRUD
- Schedule CRUD
- Progress Calculation
- Calendar API
- Gantt API

---

## v0.7.0 (Planned)

### Issue / Risk / Change

#### Planned

Issue

- CRUD
- Assignment
- Status

Risk

- Risk Assessment
- Response Strategy

Change

- Request
- Approval
- History

---

## v0.8.0 (Planned)

### CMDB

#### Planned

- Server CI
- Database CI
- Software CI
- Relationship Model
- Version History

---

## v0.9.0 (Planned)

### Dashboard & Reporting

#### Planned

Dashboard

- Project Dashboard
- Schedule Dashboard
- Issue Dashboard
- Risk Dashboard

Report

- Excel
- PDF
- Statistics

---

## v1.0.0 (Planned)

### Spring AI Integration

#### Planned

- AI PM Assistant
- AI Risk Analysis
- AI Report Summary
- AI Schedule Analysis
- AI Meeting Summary
- AI Change Impact Analysis

---

# Git Flow

```
main
│
develop
│
├── feature/auth-jwt
├── feature/auth-login
├── feature/auth-role
│
├── feature/project-crud
├── feature/project-search
│
├── feature/wbs-management
├── feature/schedule-management
│
├── feature/issue-management
├── feature/risk-management
├── feature/change-management
│
├── feature/cmdb-management
│
├── feature/report-excel
├── feature/report-pdf
│
├── feature/dashboard
│
├── feature/spring-ai-assistant
├── feature/ai-risk-analysis
└── feature/ai-report-summary
```

브랜치 정책

- **main** : Release
- **develop** : Integration
- **feature/** : Feature Development
- **release/** : Release Preparation
- **hotfix/** : Production Fix

---

# Commit Convention

| Prefix | Description |
|---------|-------------|
| feat | Feature |
| fix | Bug Fix |
| refactor | Refactoring |
| docs | Documentation |
| style | Code Style |
| test | Test |
| build | Build |
| chore | Maintenance |
| perf | Performance |
| security | Security |

예시

```text
feat: implement JWT authentication

feat: add project CRUD API

fix: resolve JWT validation issue

docs: update roadmap

refactor: improve exception handling

build: upgrade Spring Boot to 3.5.x
```

---

# Change Log Template

```markdown
## vX.Y.Z

### YYYY-MM-DD

#### Added

-

#### Changed

-

#### Fixed

-

#### Removed

-
```

---

# Target Releases

| Version | Goal |
|----------|------|
| v0.4 | Authentication 완료 |
| v0.5 | Project CRUD |
| v0.6 | WBS / Schedule |
| v0.7 | Issue / Risk / Change |
| v0.8 | CMDB |
| v0.9 | Dashboard / Report |
| v1.0 | Spring AI Assistant |

---

# Long-term Roadmap

| Version | Goal |
|----------|------|
| 1.x | PMIS Core Complete |
| 2.x | Enterprise PMIS |
| 3.x | AI Native PMIS Platform |

---

# Maintenance Policy

- 모든 변경 사항은 CHANGELOG에 기록한다.
- 모든 기능은 Feature Branch에서 개발한다.
- Pull Request 검토 후 Develop 브랜치에 병합한다.
- Main 브랜치에는 검증된 코드만 릴리즈한다.
- Semantic Versioning을 준수한다.
- Release마다 Git Tag를 생성한다.

---

**Last Updated** : 2026-08-04

**Current Version** : v0.3.1

**Current Branch** : feature/auth-jwt

**Current Sprint** : Sprint 1 - Authentication & Security

**Maintainer** : Seo Seokhyeon