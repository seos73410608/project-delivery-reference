# CHANGELOG

> PMIS (Project Management Information System) 변경 이력 관리 문서
>
> 프로젝트 진행 과정에서 발생한 주요 변경 사항과 릴리즈 이력을 기록한다.

---

# 변경 이력 관리 원칙

본 문서는 PMIS 프로젝트의 주요 변경 사항을 관리한다.

기록 대상

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

PMIS는 Semantic Versioning(SemVer)을 따른다.

```
MAJOR.MINOR.PATCH
```

예)

```
1.0.0
│ │ └─ Patch
│ └──── Minor
└────── Major
```

- Major : 호환되지 않는 변경
- Minor : 기능 추가
- Patch : 버그 수정

---

# Release History

---

# v0.1.0 (2026-08-03)

## Project Initialization

### Added

- Spring Boot 프로젝트 생성
- Java 21 적용
- Gradle Kotlin DSL 적용
- MariaDB 연동
- Git Repository 구성
- Git Flow 브랜치 전략 적용
- 기본 프로젝트 구조 생성

---

# v0.2.0 (2026-08-03)

## Common Infrastructure

### Added

- Domain 기반 Package 구조
- BaseEntity
- ApiResponse
- ErrorCode
- CommonErrorCode
- BusinessException
- GlobalExceptionHandler
- JpaConfig

### Changed

- API Response 표준화
- Global Exception 구조 적용

---

# v0.3.0 (2026-08-04)

## Security Foundation

### Added

Spring Security

- SecurityConfig
- BCrypt PasswordEncoder
- Stateless Authentication
- CORS Configuration

JWT

- JwtProvider
- JwtAuthenticationFilter
- JwtAuthenticationEntryPoint
- JwtAccessDeniedHandler
- JWT Properties

Test API

- GET /api/test/public
- GET /api/test/private
- GET /api/test/token

### Changed

- Session 인증 제거
- JWT 기반 인증 적용

### Fixed

- Spring Boot 4.x → 3.5.x 변경
- JWT 라이브러리 호환성 수정
- Security Handler 개선

---

# v0.3.1 (2026-08-05)

## Authentication

### Added

Authentication

- Login API
- Refresh Token API
- Token Reissue
- Authentication Service
- User Authentication Flow

JWT

- Access Token Validation
- Refresh Token Validation

Authorization

- Role Hierarchy
- USER Role
- PM Role
- ADMIN Role

Role Test API

- GET /api/role/authenticated
- GET /api/role/user
- GET /api/role/pm
- GET /api/role/admin

Authentication API

- POST /api/auth/login
- POST /api/auth/refresh

### Changed

- Authentication Flow 개선
- Authorization 처리 개선

### Fixed

- JWT Validation
- Security Exception 처리

---

# v0.3.2 (2026-08-06)

## API Documentation

### Added

Swagger

- OpenAPI 3
- Swagger UI
- JWT Authorization
- API Documentation

### Changed

- API 문서 자동화
---

# v0.4.0 (2026-08-06)

## Project Management

### Added

Project Domain

- Project Entity
- Project Service
- Project Repository
- Project Controller

Project API

- POST /api/projects
- GET /api/projects
- GET /api/projects/{projectId}
- PUT /api/projects/{projectId}
- DELETE /api/projects/{projectId}

Search

- Project Search
- Sorting
- Validation

Dashboard

- Project Dashboard API

### Changed

- Project Domain 구조 개선
- Service Layer 리팩토링
- Validation 구조 개선

### Fixed

- Project Validation 오류 수정
- Search 조건 처리 개선

---

# v0.5.0 (2026-08-07)

## Frontend Foundation

### Added

Frontend Project

- React
- Vite
- TypeScript
- npm
- React Router DOM

Frontend Architecture

- Backend / Frontend 프로젝트 분리
- Feature 기반 구조 적용
- Layout 기반 화면 구성

Project Structure

```
project-delivery-reference
│
├── pmis-backend
├── pmis-frontend
└── docs
```

Frontend Directory

```
src
│
├── api
├── assets
├── components
│   ├── common
│   ├── form
│   └── layout
│
├── features
│   ├── admin
│   ├── cmdb
│   ├── dashboard
│   ├── issue
│   ├── project
│   ├── report
│   ├── risk
│   ├── schedule
│   └── task
│
├── hooks
├── layouts
├── pages
├── router
├── services
├── store
├── styles
├── types
└── utils
```

React Router

- AppRouter
- MainLayout
- DashboardPage

Dashboard

- PMIS Dashboard Skeleton
- Development Status 화면

### Changed

- 기존 Vite Sample 제거
- React Example 제거
- Counter Example 제거
- Hero Image 제거
- React Logo 제거
- Vite Logo 제거

### Removed

- Vite Example Application
- Sample Assets
- Sample Counter

---

# Planned Releases

---

## v0.6.0

### WBS & Schedule

#### Planned

WBS

- WBS CRUD
- Tree Structure
- Progress Calculation

Schedule

- Schedule CRUD
- Calendar
- Gantt Chart
- Milestone
---

## v0.7.0 (Planned)

### Issue / Risk / Change Management

#### Planned

Issue

- Issue CRUD
- Issue Assignment
- Issue Status Workflow
- Priority Management
- Attachment Management

Risk

- Risk CRUD
- Risk Assessment
- Probability / Impact Matrix
- Response Strategy
- Risk Monitoring

Change

- Change Request
- Change Approval
- Change History
- Change Impact Analysis

---

## v0.8.0 (Planned)

### CMDB

#### Planned

Configuration Item

- Server CI
- Database CI
- Software CI
- Network CI

Relationship

- CI Relationship
- Dependency Graph

History

- Version Management
- Configuration History
- Audit Log

---

## v0.9.0 (Planned)

### Dashboard & Reporting

#### Planned

Dashboard

- Executive Dashboard
- Project Dashboard
- WBS Dashboard
- Schedule Dashboard
- Issue Dashboard
- Risk Dashboard
- Change Dashboard
- CMDB Dashboard

Reporting

- Weekly Report
- Monthly Report
- Executive Report
- Excel Export
- PDF Export
- Statistics Dashboard

---

## v1.0.0 (Planned)

### AI Powered PMIS

#### Planned

Spring AI

- AI PM Assistant
- AI Project Summary
- AI Issue Summary
- AI Risk Analysis
- AI Change Impact Analysis
- AI Schedule Analysis
- AI Meeting Summary
- AI Report Generation

LLM Integration

- OpenAI
- Azure OpenAI
- Ollama (Local LLM)

Knowledge Base

- PMIS Document Search
- Semantic Search
- RAG 기반 질의응답

---

# Git Flow

```
main
│
develop
│
├── feature/common
├── feature/security
├── feature/auth-jwt
├── feature/auth-login
├── feature/auth-refresh
├── feature/auth-role
│
├── feature/swagger
│
├── feature/project-search
├── feature/project-dashboard
│
├── feature/frontend-layout
│
├── feature/wbs
├── feature/schedule
├── feature/issue
├── feature/risk
├── feature/change
│
├── feature/cmdb
│
├── feature/dashboard
├── feature/report
│
└── feature/spring-ai
```

### Branch Policy

- **main** : Production Release
- **develop** : Integration Branch
- **feature/** : Feature Development
- **release/** : Release Preparation
- **hotfix/** : Production Hot Fix

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

Example

```text
feat: implement JWT authentication

feat: implement project CRUD

feat(frontend): implement React router

fix: resolve JWT validation issue

docs: update roadmap

refactor: improve exception handling

build: upgrade Spring Boot
```
---

# Change Log Template

새로운 Release는 아래 템플릿을 사용한다.

```markdown
## vX.Y.Z (YYYY-MM-DD)

### Module Name

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
| v0.6.x | WBS & Schedule |
| v0.7.x | Issue / Risk / Change |
| v0.8.x | CMDB |
| v0.9.x | Dashboard & Reporting |
| v1.0.0 | AI Powered PMIS |

---

# Long-term Roadmap

| Version | Goal |
|----------|------|
| 1.x | PMIS Core Platform |
| 2.x | Enterprise PMIS |
| 3.x | AI Native PMIS Platform |

---

# Current Project Status

## Backend

### Completed

- Spring Boot
- Common Infrastructure
- Security
- JWT Authentication
- Login
- Refresh Token
- Role Hierarchy
- Swagger
- Project CRUD
- Project Search
- Dashboard API

### In Progress

- WBS
- Schedule

---

## Frontend

### Completed

- React
- Vite
- TypeScript
- React Router
- Main Layout
- Dashboard Skeleton
- Feature-based Directory Structure

### In Progress

- Header Component
- Sidebar Component
- Breadcrumb
- Theme
- Common Components

---

# Development Process

모든 기능은 아래 절차를 따른다.

```
Planning

↓

Feature Branch 생성

↓

Development

↓

Local Test

↓

Documentation Update

↓

Commit

↓

Push

↓

Pull Request

↓

Code Review

↓

Merge to develop

↓

Release
```

---

# Git Branch Strategy

```
main
    │
develop
    │
feature/*
    │
release/*
    │
hotfix/*
```

Release는 반드시 `develop` 브랜치에서 충분히 검증한 후 `main`으로 병합한다.

---

# Documentation Policy

다음 문서는 항상 최신 상태를 유지한다.

- CHANGELOG.md
- DEVELOPMENT_ROADMAP.md
- PROJECT_OVERVIEW.md
- ARCHITECTURE.md

Frontend 문서

- 001_frontend_architecture.md
- 002_frontend_folder_structure.md
- 003_frontend_coding_convention.md
- 004_frontend_ui_design.md
- 005_frontend_component_architecture.md
- 006_frontend_development_history.md

---

# Maintenance Policy

- 모든 기능은 Feature Branch에서 개발한다.
- 모든 변경 사항은 CHANGELOG에 기록한다.
- 문서와 소스코드는 항상 동일한 상태를 유지한다.
- Pull Request 검토 후 Develop 브랜치에 병합한다.
- Main 브랜치에는 검증된 코드만 Release한다.
- Semantic Versioning을 준수한다.
- Release마다 Git Tag를 생성한다.

---

# Current Information

| Item | Value |
|------|-------|
| Last Updated | 2026-08-07 |
| Current Version | **v0.5.0** |
| Current Branch | **feature/frontend-layout** |
| Current Sprint | **Sprint 2 - Frontend Foundation** |
| Development Stage | **Frontend Foundation & Project Management** |
| Maintainer | **Seo Seokhyeon** |

---

# Next Milestone

## Sprint 3

Frontend UI

- Header
- Sidebar
- Breadcrumb
- Dashboard Widgets
- Common Components

Backend

- WBS CRUD
- Schedule CRUD
- Progress Calculation
- Calendar API

---

# Release Summary

| Version | Description |
|----------|-------------|
| v0.1.0 | Project Initialization |
| v0.2.0 | Common Infrastructure |
| v0.3.0 | Security Foundation |
| v0.3.1 | Authentication & Authorization |
| v0.3.2 | Swagger / OpenAPI |
| v0.4.0 | Project Management |
| v0.5.0 | Frontend Foundation |
| v0.6.x | WBS & Schedule |
| v0.7.x | Issue / Risk / Change |
| v0.8.x | CMDB |
| v0.9.x | Dashboard & Reporting |
| v1.0.0 | AI Powered PMIS |

---

**End of Document**