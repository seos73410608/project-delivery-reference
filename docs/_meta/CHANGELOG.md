# CHANGELOG

> PMIS (Project Management Information System) 변경 이력 관리 문서
>
> 프로젝트 진행 과정에서 발생한 주요 변경 사항과 릴리즈 이력을 기록한다.

---

# 변경 이력 관리 원칙

본 문서는 PMIS 프로젝트의 주요 변경 사항을 기록한다.

기록 대상:

* Feature
* Improvement
* Bug Fix
* Refactoring
* Security
* Performance
* Documentation
* Build

CHANGELOG에는 실제 개발이 완료되고 해당 Release에 포함된 변경 사항을 기록한다.

개발 예정 사항은 `DEVELOPMENT_ROADMAP.md`에서 관리하며, CHANGELOG의 Planned Releases에는 Release 방향을 요약해서 기록한다.

---

# Versioning

PMIS는 Semantic Versioning(SemVer)을 따른다.

```text
MAJOR.MINOR.PATCH
```

예:

```text
1.0.0
│ │ └─ Patch
│ └─── Minor
└───── Major
```

* **Major** : 기존 API 또는 시스템과 호환되지 않는 변경
* **Minor** : 기존 기능과 호환되는 기능 추가
* **Patch** : 기존 기능과 호환되는 버그 수정

현재 PMIS는 초기 개발 단계이므로 `0.x.x` 버전을 사용한다.

---

# Release History

---

## v0.1.0 (2026-08-03)

### Project Initialization

#### Added

* Spring Boot 프로젝트 생성
* Java 21 적용
* Gradle Kotlin DSL 적용
* MariaDB 연동
* Git Repository 구성
* Git Flow 브랜치 전략 적용
* 기본 프로젝트 구조 생성

---

## v0.2.0 (2026-08-03)

### Common Infrastructure

#### Added

* Domain 기반 Package 구조
* `BaseEntity`
* `ApiResponse`
* `ErrorCode`
* `CommonErrorCode`
* `BusinessException`
* `GlobalExceptionHandler`
* `JpaConfig`

#### Changed

* API Response 표준화
* Global Exception 구조 적용
* JPA Auditing 기반 생성/수정 시간 관리 적용

---

## v0.3.0 (2026-08-04)

### Security Foundation

#### Added

Spring Security:

* `SecurityConfig`
* BCrypt PasswordEncoder
* Stateless Authentication
* CORS Configuration

JWT:

* `JwtProvider`
* `JwtAuthenticationFilter`
* `JwtAuthenticationEntryPoint`
* `JwtAccessDeniedHandler`
* JWT Properties

Test API:

* GET `/api/test/public`
* GET `/api/test/private`
* GET `/api/test/token`

#### Changed

* Session 기반 인증 제거
* JWT 기반 Stateless Authentication 적용

#### Fixed

* Spring Boot / JWT 라이브러리 호환성 문제 수정
* Security Handler 개선
* Security Configuration 오류 수정

---

## v0.3.1 (2026-08-05)

### Authentication & Authorization

#### Added

Authentication:

* Login API
* Refresh Token API
* Token Reissue
* Authentication Service
* User Authentication Flow

JWT:

* Access Token Validation
* Refresh Token Validation

Authorization:

* Role Hierarchy
* USER Role
* PM Role
* ADMIN Role

Role Test API:

* GET `/api/role/authenticated`
* GET `/api/role/user`
* GET `/api/role/pm`
* GET `/api/role/admin`

Authentication API:

* POST `/api/auth/login`
* POST `/api/auth/refresh`

#### Changed

* Authentication Flow 개선
* Authorization 처리 개선
* Role 기반 접근 제어 적용

#### Fixed

* JWT Validation 문제 수정
* Security Exception 처리 개선

---

## v0.3.2 (2026-08-06)

### API Documentation

#### Added

Swagger / OpenAPI:

* OpenAPI 3
* Swagger UI
* JWT Authorization
* API Documentation

#### Changed

* API 문서 자동화
* JWT 인증 API 테스트 환경 개선

---

## v0.4.0 (2026-08-06)

### Project Management

#### Added

Project Domain:

* `Project` Entity
* `ProjectService`
* `ProjectRepository`
* `ProjectController`

Project API:

* POST `/api/projects`
* GET `/api/projects`
* GET `/api/projects/{projectId}`
* PUT `/api/projects/{projectId}`
* DELETE `/api/projects/{projectId}`

Search:

* Project Search
* Sorting
* Sorting Column Validation

Dashboard:

* Project Dashboard API
* Project Status KPI
* Project Priority KPI
* Recent Project KPI
* Upcoming Deadline KPI

#### Changed

* Project Domain 구조 개선
* Service Layer 리팩토링
* Validation 구조 개선
* 검색 API 정렬 컬럼 검증 적용

#### Fixed

* Project Validation 오류 수정
* Search 조건 처리 개선

---

## v0.5.0 (2026-08-07)

### Frontend Foundation

#### Added

Frontend:

* React
* Vite
* TypeScript
* npm
* React Router DOM

Frontend Architecture:

* Backend / Frontend 프로젝트 분리
* Feature 기반 구조 적용
* Layout 기반 화면 구성
* Frontend Path Alias 구성

Project Structure:

```text
project-delivery-reference
│
├── pmis-backend
├── pmis-frontend
└── docs
```

Frontend Directory:

```text
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

React Router:

* `AppRouter`
* `MainLayout`
* `DashboardPage`

Dashboard:

* PMIS Dashboard Skeleton
* Development Status 화면

#### Changed

* 기존 Vite Sample 제거
* React Example 제거
* Counter Example 제거
* Hero Image 제거
* React Logo 제거
* Vite Logo 제거
* Frontend Path Alias 구성
* Vite / TypeScript Path Resolution 구성

#### Removed

* Vite Example Application
* Sample Assets
* Sample Counter

---

## v0.5.1 (2026-08-08)

### Frontend Layout & Common UI Components

#### Added

Layout Components:

* Header Component
* Sidebar Component
* Breadcrumb Component

Common UI Components:

* Button Component
* Card Component
* Loading Component
* EmptyState Component

Main Layout:

* Header / Sidebar / Breadcrumb 독립 Component 분리
* MainLayout Refactoring
* Router 기반 Content 영역 구성

Breadcrumb:

* 현재 Router Path 기반 Breadcrumb 구성

#### Changed

* MainLayout의 직접 구현 영역을 독립 Component로 분리
* Layout Component와 Common UI Component 분리
* Frontend Component 구조 정리
* `@/components/...` Alias 기반 Import 구조 적용
* Vite Path Resolution 설정 정리

#### Documentation

* `006_frontend_development_history.md` 현행화
* Frontend Layout 및 Common UI Component 개발 이력 추가

---

## v0.5.2 (2026-08-10)

### Project Detail API & Controller Separation

#### Added

Project Detail:

* `ProjectDetailController`
* `ProjectDetailService`
* `ProjectDetailResponse`
* Project Detail API

API:

* GET `/api/projects/{projectId}/detail`

#### Changed

Project Controller 책임 분리:

```text
ProjectController
ProjectDashboardController
ProjectDetailController
```

Service 책임 분리:

```text
ProjectService
ProjectDashboardService
ProjectDetailService
```

Project Detail:

* 기존 Project 단건 조회 API와 상세 조회 책임 분리
* 프로젝트 상세 조회 전용 Controller 구성
* 프로젝트 상세 조회 전용 Service 구성
* 프로젝트 상세 조회 전용 Response DTO 구성

Dashboard:

* Project Dashboard Controller 분리
* Dashboard 관련 API와 Project CRUD API 책임 분리

#### Refactoring

* Controller 단일 책임 구조 개선
* Service 단일 책임 구조 개선
* Project Domain API 구조 정리
* WBS / Schedule / Issue / Risk / Change 연계 기반 마련

#### Documentation

* Project Detail API Swagger 문서화
* Project Domain API 구조 문서화

---

## v0.5.3 (2026-08-11)

### Frontend Dashboard HMC Reference

#### Added

Dashboard Reference:

* Power HMC(Hardware Management Console) 스타일을 참고한 PMIS Dashboard 구성
* 프로젝트 운영 현황 중심의 통합 Dashboard 형태로 확장
* Project Overview 영역
* KPI 영역
* WBS Progress 영역
* Schedule Summary 영역
* Issue Summary 영역
* Recent Activity 영역

Dashboard Components:

* `KpiCard`
* `ProjectOverview`
* `WbsProgress`
* `ScheduleSummary`
* `IssueSummary`
* `RecentActivity`

Dashboard Layout:

```text
Project Overview
       │
       ├── Project Status KPI
       ├── Progress KPI
       ├── Schedule KPI
       └── Issues KPI
       │
       ├── WBS Progress
       ├── Schedule Summary
       ├── Issues / Risks
       └── Recent Activity
```

#### Changed

* 기존 Dashboard Skeleton을 PMIS 운영 Dashboard 형태로 확장
* DashboardPage를 Dashboard Component 단위로 분리
* KPI 영역을 `KpiCard` 재사용 Component로 분리
* Project Overview를 `ProjectOverview` Component로 분리
* WBS Progress를 `WbsProgress` Component로 분리
* Schedule Summary를 `ScheduleSummary` Component로 분리
* Issue Summary를 `IssueSummary` Component로 분리
* Recent Activity를 `RecentActivity` Component로 분리
* Dashboard Component Import 구조 정리

#### Build

* TypeScript Build 오류 수정
* TypeScript 6.x `baseUrl` deprecated 대응
* `ignoreDeprecations` 설정 적용
* `npm run build` 성공
* Vite Production Build 성공

Build Result:

```text
vite v8.2.1 building client environment for production...

✓ 36 modules transformed.

✓ built in 1.73s
```

#### Git

* Feature Branch 생성: `feature/frontend-dashboard-hmc-reference`
* Feature Branch Push 완료
* `develop` Branch Merge 완료
* `develop` Push 완료

#### Documentation

* Dashboard Component 구조 및 개발 상태 Roadmap 반영
* Frontend Dashboard 개발 이력 현행화 대상 추가

#### Note

* Dashboard 데이터는 Reference UI 구현을 위한 정적 데이터
* Project / WBS / Schedule / Issue 실제 API 연동은 후속 Feature에서 진행
* 현재 단계에서는 Dashboard UI 구조와 Component 책임 분리를 우선 확보

---

## v0.6.0 (2026-08-21)

### WBS & Frontend API Integration

#### Added

WBS Backend API:

* WBS Tree 조회 API
* WBS 검색 API
* WBS 단건 조회 API
* WBS 생성 API
* WBS 수정 API
* WBS 삭제 API

Frontend API Client:

* `apiClient`
* `getWbsTree`
* `searchWbs`
* `getWbs`
* `createWbs`
* `updateWbs`
* `deleteWbs`

WBS Components:

* `WbsTree`
* `WbsTreeNode`
* `WbsDetail`
* `WbsForm`
* `WbsStatusForm`

WBS Page:

* WBS Tree 화면
* WBS Search
* WBS Create
* WBS Edit
* WBS Status Change
* WBS Delete

#### Changed

* 기존 WBS UI를 실제 Backend API 기반 화면으로 전환
* WBS Tree 조회 결과와 Frontend Tree 구조 연동
* WBS Search 결과를 Tree Node 구조로 변환
* WBS 생성 후 최신 Tree 재조회
* WBS 수정 후 최신 Tree 재조회
* WBS 상태 변경을 기존 WBS Update API 기반으로 처리
* WBS 삭제 후 최신 Tree 재조회
* WBS 삭제 성공 후 선택 상태 초기화
* WBS 수정 시 실제 Parent WBS를 Tree에서 탐색하여 Form에 전달

#### Validation

* WBS 상태 변경 시 현재 상태와 동일한 상태 선택 방지
* WBS 삭제 전 하위 WBS 존재 여부 검증
* 하위 WBS가 존재하면 삭제 차단
* 하위 WBS가 없는 경우 DELETE API 호출

#### UX

* WBS 생성 Form
* WBS 수정 Form
* WBS 상태 변경 Form
* API Loading 상태
* API Error 상태
* Search Empty State
* Delete Confirmation
* Delete 성공 후 Tree 재조회
* Delete 성공 후 선택된 WBS 해제

#### Build

* Frontend Production Build 검증
* WBS API Integration 관련 TypeScript 오류 수정
* Browser 기반 WBS CRUD 검증
* Browser 기반 WBS Status Change 검증
* Browser 기반 WBS Delete 검증

#### Git

Feature Branch:

```text
feature/frontend-api-integration
```

WBS Status Change:

```text
75e9d5b
feat(frontend): implement WBS status change
```

WBS Delete:

```text
d468f70
feat(frontend): implement WBS delete
```

Branch Integration:

```text
feature/frontend-api-integration
        ↓
develop
```

* Fast-forward Merge 완료
* `develop` Push 완료

#### Documentation

* WBS Frontend API Integration 개발 상태 반영
* WBS CRUD / Status / Delete 개발 이력 반영
* Frontend Integration 상태 현행화

#### Note

WBS Delete의 업무 제약은 현재 Frontend에서 우선 검증한다.

```text
Frontend
   │
   ├─ 하위 WBS 존재 여부 확인
   │
   └─ 삭제 가능 여부 판단
            │
            ↓
Backend DELETE API
            │
            ↓
실제 삭제
```

포트폴리오 단계에서는 Frontend와 Backend의 책임을 분리하여 업무 규칙과 API Integration 흐름을 명확하게 표현한다.

---

# Planned Releases

## v0.6.x

### WBS & Schedule

#### Completed

WBS:

* WBS CRUD
* WBS Tree Structure
* WBS Search
* WBS Status Change
* WBS Delete
* WBS Frontend API Integration

#### In Progress / Planned

Schedule:

* Schedule CRUD
* Calendar
* Gantt Chart
* Milestone
* WBS Progress Calculation
* Schedule API Integration

---

## v0.7.x

### Issue / Risk / Change Management

#### Planned

Issue:

* Issue CRUD
* Issue Assignment
* Issue Status Workflow
* Priority Management
* Attachment Management

Risk:

* Risk CRUD
* Risk Assessment
* Probability / Impact Matrix
* Response Strategy
* Risk Monitoring

Change:

* Change Request
* Change Approval
* Change History
* Change Impact Analysis

---

## v0.8.x

### CMDB

#### Planned

Configuration Item:

* Server CI
* Database CI
* Software CI

Relationship:

* CI Relationship
* Dependency Graph

History:

* Version Management
* Configuration History
* Audit Log

---

## v0.9.x

### Dashboard & Reporting

#### Planned

Dashboard:

* Executive Dashboard
* Project Dashboard
* WBS Dashboard
* Schedule Dashboard
* Issue Dashboard
* Risk Dashboard
* Change Dashboard
* CMDB Dashboard

Reporting:

* Weekly Report
* Monthly Report
* Executive Report
* Excel Export
* PDF Export
* Statistics Dashboard

> Note: PMIS Frontend Dashboard Reference UI는 v0.5.3에서 선행 구현되었으며, v0.9.x에서는 Backend Dashboard API 및 실제 데이터 연동을 중심으로 확장한다.

---

## v1.0.0

### AI Powered PMIS

#### Planned

Spring AI:

* AI PM Assistant
* AI Project Summary
* AI Issue Summary
* AI Risk Analysis
* AI Change Impact Analysis
* AI Schedule Analysis
* AI Meeting Summary
* AI Report Generation

LLM Integration:

* OpenAI
* Azure OpenAI
* Ollama (Local LLM)

Knowledge Base:

* PMIS Document Search
* Semantic Search
* RAG 기반 질의응답

---

# Git Flow

```text
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
 ├── feature/project-crud
 ├── feature/project-search
 ├── feature/project-dashboard
 ├── feature/project-detail
 │
 ├── feature/frontend-layout
 ├── feature/frontend-components
 ├── feature/frontend-dashboard-hmc-reference
 ├── feature/frontend-api-integration
 │
 ├── feature/wbs-management
 ├── feature/schedule-management
 ├── feature/issue-management
 ├── feature/risk-management
 ├── feature/change-management
 │
 ├── feature/cmdb-management
 │
 ├── feature/dashboard
 ├── feature/report-excel
 ├── feature/report-pdf
 │
 └── feature/spring-ai
      ├── feature/ai-risk-analysis
      └── feature/ai-report-summary
```

### Branch Policy

* `main` : Production Release
* `develop` : Integration Branch
* `feature/*` : Feature Development
* `release/*` : Release Preparation
* `hotfix/*` : Production Hot Fix

---

# Commit Convention

| Prefix     | Description   |
| ---------- | ------------- |
| `feat`     | Feature       |
| `fix`      | Bug Fix       |
| `refactor` | Refactoring   |
| `docs`     | Documentation |
| `style`    | Code Style    |
| `test`     | Test          |
| `build`    | Build         |
| `chore`    | Maintenance   |
| `perf`     | Performance   |
| `security` | Security      |

Examples:

```text
feat: implement JWT authentication

feat: implement project CRUD

feat: implement project search

feat: implement project dashboard api

feat: separate project detail api

feat(frontend): implement React router

feat(frontend): add sidebar component

feat(frontend): add common UI components

feat(frontend): add dashboard reference layout

feat(frontend): add dashboard widgets

feat(frontend): implement WBS API integration

feat(frontend): implement WBS status change

feat(frontend): implement WBS delete

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

#### Refactoring

- 

#### Documentation

- 

#### Build

- 

#### Git

- 

#### Note

- 
```

---

# Release Summary

| Version | Description                                |
| ------- | ------------------------------------------ |
| v0.1.0  | Project Initialization                     |
| v0.2.0  | Common Infrastructure                      |
| v0.3.0  | Security Foundation                        |
| v0.3.1  | Authentication & Authorization             |
| v0.3.2  | Swagger / OpenAPI                          |
| v0.4.0  | Project Management                         |
| v0.5.0  | Frontend Foundation                        |
| v0.5.1  | Frontend Layout & Common UI Components     |
| v0.5.2  | Project Detail API & Controller Separation |
| v0.5.3  | Frontend Dashboard HMC Reference           |
| v0.6.0  | WBS & Frontend API Integration             |
| v0.6.x  | Schedule & WBS Progress                    |
| v0.7.x  | Issue / Risk / Change                      |
| v0.8.x  | CMDB                                       |
| v0.9.x  | Dashboard & Reporting                      |
| v1.0.0  | AI Powered PMIS                            |

---

# Current Project Status

## Backend

### Completed

* Spring Boot
* Common Infrastructure
* Security
* JWT Authentication
* Login
* Refresh Token
* Role Hierarchy
* Swagger / OpenAPI
* Project CRUD
* Project Search
* Project Search Sorting Validation
* Project Dashboard API
* Project Dashboard KPI
* Project Dashboard Controller
* Project Detail API
* Project Detail Controller
* Project Detail Service
* Project Detail Response DTO
* Project Controller / Dashboard Controller / Detail Controller 책임 분리
* WBS CRUD
* WBS Tree Structure
* WBS Search API
* WBS Delete API

### In Progress

* WBS Progress Calculation
* Schedule CRUD
* Calendar API
* Gantt Data
* Milestone

---

## Frontend

### Completed

* React
* Vite
* TypeScript
* React Router
* Main Layout
* Dashboard Skeleton
* Feature-based Directory Structure
* Header Component
* Sidebar Component
* Breadcrumb Component
* Button Component
* Card Component
* Loading Component
* EmptyState Component
* Frontend Path Alias
* Layout Component Separation
* Dashboard Reference Layout
* `ProjectOverview`
* `KpiCard`
* `WbsProgress`
* `ScheduleSummary`
* `IssueSummary`
* `RecentActivity`
* Dashboard Component Separation
* Dashboard Production Build Verification
* WBS Tree UI
* WBS Search UI
* WBS Create Form
* WBS Edit Form
* WBS Status Form
* WBS Delete UI
* WBS API Client
* WBS API Integration
* WBS CRUD Integration
* WBS Status Change Integration
* WBS Delete Integration

### Current

* Dashboard Reference UI
* Project Module
* WBS Module
* Theme
* API Integration
* Form Components

### Next

* Project List
* Project Search
* Project Detail
* Project Dashboard API Integration
* Project API Integration
* Schedule UI
* Calendar UI
* Gantt UI

---

## Integration

### Completed

* WBS API Integration
* WBS CRUD Integration
* WBS Status Integration
* WBS Delete Integration

### In Progress

* Project API Integration
* Project Dashboard API Integration
* Project Detail API Integration
* WBS Progress Integration
* Schedule API Integration

---

# Development Process

모든 기능은 아래 절차를 따른다.

```text
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
Integration Test
   ↓
Release
```

Frontend Dashboard와 같이 Backend API가 아직 준비되지 않은 기능은 다음 흐름을 적용한다.

```text
Reference / Requirement
        ↓
UI Structure Design
        ↓
Component Design
        ↓
Static Data Implementation
        ↓
Browser Verification
        ↓
Production Build
        ↓
Documentation
        ↓
Commit
        ↓
Push
        ↓
Merge to develop
        ↓
Backend API Integration
        ↓
E2E Verification
```

Frontend API Integration 기능은 다음 흐름을 적용한다.

```text
Backend API 확인
        ↓
Frontend API Client 구현
        ↓
Type 정의
        ↓
Component / Page 구현
        ↓
API Integration
        ↓
Loading / Error / Empty State 처리
        ↓
Browser Verification
        ↓
Production Build
        ↓
Documentation
        ↓
Commit
        ↓
Push
        ↓
Merge to develop
        ↓
Integration Test
        ↓
E2E Verification
```

---

# Git Branch Strategy

```text
main
 │
 develop
 │
 ├── feature/*
 │
 ├── release/*
 │
 └── hotfix/*
```

Release는 반드시 `develop` 브랜치에서 충분히 검증한 후 `main`으로 병합한다.

---

# Documentation Policy

다음 문서는 항상 최신 상태를 유지한다.

* `CHANGELOG.md`
* `DEVELOPMENT_ROADMAP.md`
* `PROJECT_OVERVIEW.md`
* `ARCHITECTURE.md`

Design 문서:

* `docs/design/project/PROJECT_DESIGN.md`
* `docs/design/project/PROJECT_DASHBOARD_DESIGN.md`
* `docs/design/wbs/WBS_DESIGN.md`
* `docs/design/schedule/SCHEDULE_DESIGN.md`
* `docs/design/issue/ISSUE_DESIGN.md`
* `docs/design/report/REPORT_DESIGN.md`
* `docs/design/server/SERVER_CONFIGURATION_DESIGN.md`

Frontend 문서:

* `001_frontend_architecture.md`
* `002_frontend_folder_structure.md`
* `003_frontend_coding_convention.md`
* `004_frontend_ui_design.md`
* `005_frontend_component_architecture.md`
* `006_frontend_development_history.md`

Dashboard Component 변경 시 관련 Frontend Architecture / Component / Development History 문서를 함께 현행화한다.

WBS Frontend API Integration 변경 시 관련 Frontend Architecture / Component / Development History 문서를 함께 현행화한다.

---

# Maintenance Policy

* 모든 기능은 Feature Branch에서 개발한다.
* 모든 Release 변경 사항은 CHANGELOG에 기록한다.
* Backend와 Frontend의 개발 상태를 Roadmap에 반영한다.
* 문서와 소스코드는 항상 동일한 상태를 유지한다.
* Pull Request 검토 후 `develop` 브랜치에 병합한다.
* `main` 브랜치에는 검증된 코드만 Release한다.
* Semantic Versioning을 준수한다.
* Release마다 Git Tag를 생성한다.
* UI Reference 구현과 실제 API Integration 단계를 구분하여 기록한다.
* Frontend 업무 제약은 필요한 경우 UI / API Integration 단계에서 명시적으로 검증한다.

---

# Current Information

| Item                          | Value                                                               |
| ----------------------------- | ------------------------------------------------------------------- |
| Last Updated                  | **2026-08-21**                                                      |
| Current Version               | **v0.6.0**                                                          |
| Current Branch                | **develop**                                                         |
| Current Sprint                | **Sprint 3 - Project Management & Frontend Integration**            |
| Development Stage             | **WBS API Integration Complete / Schedule Development In Progress** |
| Last Completed Feature Branch | **feature/frontend-api-integration**                                |
| Last Completed Commit         | **d468f70 - feat(frontend): implement WBS delete**                  |
| Current Integration Branch    | **develop**                                                         |
| Frontend Build Status         | **Production Build Success**                                        |
| Maintainer                    | **Seo Seokhyeon**                                                   |

---

# Next Milestone

## Sprint 3

### Frontend

* Project List
* Project Search
* Project Detail
* Project Dashboard UI
* Project API Integration
* Project Dashboard API Integration
* Schedule UI
* Calendar UI
* Gantt UI

### Backend

* WBS Progress Calculation
* Schedule CRUD
* Calendar API
* Gantt Data
* Milestone

### Integration

* Project API Integration
* Project Dashboard API Integration
* Project Detail API Integration
* WBS Progress Integration
* Schedule API Integration
* Loading / Empty / Error State
* API Error Handling
* Browser Verification
* E2E Verification

---

# Current Development Snapshot

```text
Backend

 ├─ Common Infrastructure        ✓
 ├─ Security / JWT               ✓
 ├─ Project CRUD                 ✓
 ├─ Project Search               ✓
 ├─ Project Dashboard API        ✓
 ├─ Project Detail API           ✓
 ├─ WBS CRUD                     ✓
 ├─ WBS Tree Structure           ✓
 ├─ WBS Search API               ✓
 ├─ WBS Progress Calculation     →
 └─ Schedule                     🚧


Frontend

 ├─ React / Vite / TypeScript    ✓
 ├─ Router / Layout              ✓
 ├─ Common UI Components         ✓
 ├─ Dashboard Reference UI       ✓
 ├─ Dashboard Components         ✓
 ├─ Project UI                   🚧
 ├─ WBS UI                       ✓
 ├─ WBS API Integration          ✓
 └─ Schedule UI                  🚧


Integration

 ├─ Project API Integration      →
 ├─ Dashboard API Integration    →
 ├─ Project Detail Integration   →
 ├─ WBS API Integration          ✓
 ├─ WBS CRUD Integration         ✓
 ├─ WBS Status Integration       ✓
 ├─ WBS Delete Integration       ✓
 └─ Schedule API Integration     →
```

---

# End of Document
