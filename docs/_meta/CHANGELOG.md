# CHANGELOG

> PMIS (Project Management Information System) 변경 이력 관리 문서
>
> 프로젝트 진행 과정에서 발생한 주요 변경 사항과 Release 이력을 기록한다.

---

# 변경 이력 관리 원칙

본 문서는 PMIS 프로젝트의 주요 변경 사항과 Release 이력을 기록한다.

기록 대상:

* Feature
* Improvement
* Bug Fix
* Refactoring
* Security
* Performance
* Documentation
* Build

CHANGELOG에는 **실제 개발이 완료되고 `develop` 브랜치에 통합된 주요 변경 사항**을 기록한다.

개발 예정 사항은 `DEVELOPMENT_ROADMAP.md`에서 관리한다.

CHANGELOG의 Planned Releases는 향후 Release 방향을 요약하여 기록한다.

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

### Project Management Backend

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

├── pmis
│
├── pmis-frontend
│
└── docs
```

Frontend Directory:

```text
src

├── api
├── assets
├── components
│   ├── common
│   ├── form
│   └── layout
├── features
│   ├── dashboard
│   ├── project
│   ├── wbs
│   ├── schedule
│   ├── issue
│   ├── risk
│   ├── change
│   ├── cmdb
│   └── report
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

* MainLayout 직접 구현 영역을 독립 Component로 분리
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

#### Git

Feature Branch:

```text
feature/frontend-dashboard-hmc-reference
```

* Feature Branch Push 완료
* `develop` Branch Merge 완료
* `develop` Push 완료

#### Note

* Dashboard 데이터는 Reference UI 구현을 위한 정적 데이터
* Project / WBS / Schedule / Issue 실제 API 연동은 후속 Feature에서 진행
* 현재 단계에서는 Dashboard UI 구조와 Component 책임 분리를 우선 확보

---

# Unreleased

> 현재 `develop` 브랜치에서 개발 및 통합된 변경 사항이다.
>
> 다음 정식 Release 후보로 관리한다.

---

## Project Frontend CRUD

### Added

Project Frontend:

* Project API Client
* Project List UI
* Project Search UI
* Project Detail UI
* Project Registration UI
* Project Edit UI
* Project Delete UI

Project API Integration:

* Project List API Integration
* Project Search API Integration
* Project Detail API Integration
* Project Create API Integration
* Project Update API Integration
* Project Delete API Integration

State Management:

* Loading State
* Empty State
* Error State

UX:

* Delete Confirmation
* API Error Handling
* CRUD 결과 UI 반영

### Completed

```text
Project List              ✓

Project Search            ✓

Project Detail            ✓

Project Create            ✓

Project Edit              ✓

Project Delete            ✓

Project API Integration   ✓

Loading State             ✓

Empty State               ✓

Error State               ✓

Browser Verification      ✓

Production Build          ✓
```

### Git

```text
8d7df6d

feat(frontend): complete project CRUD
```

Project Frontend Feature는 `develop` 브랜치에 통합되었다.

---

## WBS Management Backend

### Added

WBS Domain:

* WBS CRUD
* WBS Tree Structure
* WBS 단건 조회
* WBS 생성
* WBS 수정
* WBS 삭제
* WBS Validation

WBS Relationship:

* Parent WBS
* Child WBS
* Tree Structure

Delete Handling:

* 하위 WBS 존재 여부 처리
* Parent / Child 관계 고려
* 삭제 Validation

### API

* WBS Tree API
* WBS Search API
* WBS Detail API
* WBS Create API
* WBS Update API
* WBS Delete API

---

## WBS Frontend Integration

### Added

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

WBS UI:

* WBS Tree
* WBS Detail
* WBS Search
* WBS Create
* WBS Edit
* WBS Status Change
* WBS Delete

### Changed

* 기존 WBS UI를 실제 Backend API 기반으로 전환
* WBS Tree 조회 결과와 Frontend Tree 구조 연동
* WBS Search 결과 처리 개선
* WBS 생성 후 최신 Tree 재조회
* WBS 수정 후 최신 Tree 재조회
* WBS 상태 변경 후 최신 데이터 반영
* WBS 삭제 후 최신 Tree 재조회
* WBS 삭제 성공 후 선택 상태 초기화
* Parent WBS 관계를 Tree 기반으로 처리

### Validation

* 동일 상태 변경 방지
* 하위 WBS 존재 여부 확인
* 하위 WBS가 존재하는 경우 삭제 제한
* 삭제 가능 조건 확인 후 DELETE API 호출

### UX

* Loading State
* Error State
* Empty State
* Delete Confirmation
* Delete 후 Tree Refresh
* 선택 WBS 상태 초기화

### Verification

* WBS CRUD API Integration 검증
* WBS Status Change 검증
* WBS Delete 검증
* Browser 기반 기능 검증

### Git

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

Feature Integration:

```text
feature/frontend-wbs

        ↓

develop
```

---

## Schedule Management Backend

### Added

Schedule Domain:

* Schedule Entity
* Schedule Repository
* Schedule Service
* Schedule Controller
* Schedule DTO
* Schedule Validation

Schedule CRUD:

* Schedule List
* Schedule Detail
* Schedule Create
* Schedule Update
* Schedule Delete

Schedule Status:

* Schedule Status 변경

Schedule Sort Order:

* Sort Order 관리

Schedule Search:

* Project 조건 검색
* WBS 조건 검색
* Keyword 검색
* Status 검색

Pagination:

* Pagination
* Page / Size 처리

Dynamic Sorting:

* Dynamic Sorting
* Sorting 조건 처리

Period Search:

* Start Date
* End Date
* 기간 조건 검색

### Completed

```text
Schedule CRUD               ✓

Schedule List               ✓

Schedule Detail             ✓

Schedule Create             ✓

Schedule Update             ✓

Schedule Delete             ✓

Schedule Status             ✓

Schedule Sort Order         ✓

Schedule Search             ✓

Project Search              ✓

WBS Search                  ✓

Keyword Search              ✓

Status Search               ✓

Pagination                  ✓

Dynamic Sorting             ✓

Period Search               ✓

Validation                  ✓

Swagger Verification        ✓
```

### API Verification

Schedule API는 Swagger / OpenAPI를 통해 주요 CRUD 및 검색 시나리오를 검증하였다.

검증 흐름:

```text
Schedule List

    ↓

Schedule Create

    ↓

Schedule List Verification

    ↓

Schedule Detail

    ↓

Schedule Update

    ↓

Schedule Status Change

    ↓

Schedule Sort Order

    ↓

Schedule Search

    ↓

Schedule Period Search

    ↓

Schedule Delete

    ↓

Deleted Schedule Verification
```

추가 검증:

* Validation
* Not Found
* Search 조건
* Pagination
* Sorting
* Period 조건

### Git

Feature Branch:

```text
feature/schedule-management
```

Schedule Backend 개발 및 API Verification이 완료되어 `develop` 브랜치에 통합되었다.

---

## Schedule Frontend Management

### Added

Schedule Frontend:

* Schedule API Client
* Schedule Type 정의
* Schedule List UI
* Schedule Search UI
* Schedule Detail UI
* Schedule Registration UI
* Schedule Edit UI
* Schedule Delete UI
* Schedule Status UI

Schedule API Integration:

* Schedule List API Integration
* Schedule Detail API Integration
* Schedule Create API Integration
* Schedule Update API Integration
* Schedule Delete API Integration
* Schedule Search API Integration

State Handling:

* Loading State
* Empty State
* Error State

### Changed

* 기존 Schedule UI를 실제 Backend Schedule API와 연계
* Schedule 데이터를 API 기반으로 조회하도록 변경
* Create / Update 이후 최신 데이터 반영
* Delete 이후 목록 데이터 재조회
* Search 조건과 Backend API Query Parameter 연동
* Status 정보를 실제 Schedule 데이터와 연동

### UI

Schedule Management 화면:

```text
Schedule Management

├── Schedule List
│
├── Search
│
├── Detail
│
├── Create
│
├── Edit
│
├── Delete
│
└── Status Management
```

### Verification

* Schedule CRUD 확인
* Schedule Search 확인
* Schedule Detail 확인
* Schedule Create 확인
* Schedule Edit 확인
* Schedule Delete 확인
* Browser UI 확인
* Production Build 확인

### Git

Schedule Frontend Feature:

```text
feature/frontend-schedule
```

Schedule Backend와 Frontend를 포함한 일정 관리 기능은 `develop` 브랜치에 통합되었다.

---

# Current Development Status

## Backend

### Completed

* Spring Boot
* Gradle Kotlin DSL
* MariaDB
* Common Infrastructure
* JPA Auditing

Security:

* Spring Security
* JWT Authentication
* Login
* Refresh Token
* Role Hierarchy
* Role Authorization

Documentation:

* Swagger
* OpenAPI

Project:

* Project CRUD
* Project Search
* Project Sorting
* Project Dashboard API
* Project Dashboard KPI
* Project Detail API
* Project Controller Separation
* Project Dashboard Controller Separation
* Project Detail Controller Separation

WBS:

* WBS CRUD
* WBS Tree Structure
* WBS Validation
* WBS Delete Handling

Schedule:

* Schedule CRUD
* Schedule List
* Schedule Detail
* Schedule Create
* Schedule Update
* Schedule Delete
* Schedule Status
* Schedule Sort Order
* Schedule Search
* Project Search
* WBS Search
* Keyword Search
* Status Search
* Pagination
* Dynamic Sorting
* Period Search
* Validation
* Swagger Verification

### Next

* Issue Management
* Risk Management
* Change Management
* Evidence Management
* CMDB Management
* Dashboard Integration
* Report
* Spring AI

---

# Frontend

## Completed

Foundation:

* React
* Vite
* TypeScript
* React Router DOM
* Feature-based Directory Structure
* Path Alias

Layout:

* MainLayout
* Header
* Sidebar
* Breadcrumb

Common Components:

* Button
* Card
* Loading
* EmptyState

Dashboard:

* Dashboard Skeleton
* HMC Reference UI
* Dashboard Component Separation
* KPI Card
* Project Overview
* WBS Progress
* Schedule Summary
* Issue Summary
* Recent Activity

Project:

* Project API Client
* Project List
* Project Search
* Project Detail
* Project Create
* Project Edit
* Project Delete
* Project CRUD API Integration
* Loading / Empty / Error State

WBS:

* WBS API Client
* WBS Tree
* WBS Detail
* WBS Search
* WBS Form
* WBS Create
* WBS Edit
* WBS Status Form
* WBS Status Change
* WBS Delete
* WBS API Integration

Schedule:

* Schedule API Client
* Schedule List
* Schedule Search
* Schedule Detail
* Schedule Create
* Schedule Edit
* Schedule Delete
* Schedule Status
* Schedule API Integration
* Loading / Empty / Error State

### Next

* Calendar UI
* Gantt UI
* Project Dashboard API Integration
* Dashboard Data Integration
* Authentication UI
* Issue UI
* Risk UI
* Change UI
* Evidence UI
* CMDB UI
* Report UI
* AI Assistant UI

---

# Integration

## Completed

Project:

* Project API Integration
* Project CRUD Verification
* Project Detail Verification
* Project Delete Verification

WBS:

* WBS API Integration
* WBS CRUD Integration
* WBS Status Integration
* WBS Delete Integration
* Browser Verification

Schedule:

* Schedule Backend API Verification
* Schedule API Client
* Schedule CRUD Integration
* Schedule Search Integration
* Schedule UI Verification
* Backend / Frontend Integration

## Next

* Calendar Integration
* Gantt Integration
* Dashboard API Integration
* Dashboard Data Integration
* Authentication Integration
* Issue E2E Integration
* Risk E2E Integration
* Change E2E Integration
* Full E2E Verification

---

# Current Development Snapshot

```text
Backend

├─ Common Infrastructure        ✓
│
├─ Security / JWT               ✓
│
├─ Project CRUD                 ✓
├─ Project Search               ✓
├─ Project Dashboard API        ✓
├─ Project Detail API           ✓
│
├─ WBS Management               ✓
│
├─ Schedule CRUD                ✓
├─ Schedule Search              ✓
├─ Schedule Pagination          ✓
├─ Schedule Sorting             ✓
├─ Schedule Period Search       ✓
├─ Schedule Validation          ✓
├─ Schedule Swagger Test        ✓
│
├─ Issue                        →
├─ Risk                         →
├─ Change                       →
├─ Evidence                     →
├─ CMDB                         →
├─ Dashboard                    →
├─ Report                       →
│
└─ Spring AI                    →


Frontend

├─ React / Vite / TypeScript    ✓
├─ Router / Layout              ✓
├─ Common UI Components         ✓
├─ Dashboard Reference UI       ✓
│
├─ Project CRUD                 ✓
│
├─ WBS API Integration          ✓
├─ WBS UI                       ✓
│
├─ Schedule API Client          ✓
├─ Schedule Integration         ✓
├─ Schedule UI                  ✓
│
├─ Calendar                     →
│
└─ Gantt                        →


Integration

├─ Project API Integration      ✓
├─ Project CRUD Verification    ✓
│
├─ WBS API Integration          ✓
├─ WBS Browser Verification     ✓
│
├─ Schedule API Verification    ✓
├─ Schedule API Integration     ✓
├─ Schedule Browser Verification✓
│
├─ Calendar Integration         →
├─ Gantt Integration            →
├─ Dashboard API Integration    →
│
└─ E2E Verification             →
```

---

# Current Sprint

## Sprint 3

### WBS / Schedule / Frontend API Integration

현재 Sprint의 핵심 목표는 다음과 같다.

```text
WBS

Backend API

    ↓

Frontend API Integration

    ↓

Browser Verification

    ✓


Schedule

Backend API

    ↓

Swagger Verification

    ↓

Frontend API Client

    ↓

Frontend API Integration

    ↓

Schedule UI

    ↓

Browser Verification

    ✓


Next

    ↓

Calendar

    ↓

Gantt

    ↓

Dashboard API Integration

    ↓

E2E Verification
```

---

# Current Development Priority

현재 개발 우선순위는 다음과 같다.

```text
1. Calendar UI

        ↓

2. Calendar API Integration

        ↓

3. Gantt UI

        ↓

4. WBS / Schedule Relationship

        ↓

5. Schedule Timeline Visualization

        ↓

6. Project Dashboard API Integration

        ↓

7. Dashboard Data Integration

        ↓

8. Sprint 3 E2E Verification

        ↓

9. Issue Management

        ↓

10. Risk Management

        ↓

11. Change Management

        ↓

12. Evidence Management
```

---

# Next Feature

다음 핵심 개발 기능은 다음과 같다.

```text
feature/frontend-calendar
```

단, 현재 Git Branch Strategy에 Calendar를 별도 Feature Branch로 관리하지 않는 경우 다음과 같이 관리할 수 있다.

```text
feature/frontend-schedule-calendar
```

또는 기존 Schedule Feature 확장:

```text
feature/frontend-schedule-calendar
```

권장 개발 순서:

```text
Schedule Management

        ✓

        ↓

Calendar UI

        ↓

Calendar API Integration

        ↓

Timeline

        ↓

Milestone

        ↓

Gantt UI

        ↓

WBS / Schedule Integration

        ↓

Dashboard API Integration
```

---

# Planned Releases

## v0.6.x

### WBS & Schedule Management

#### Completed

WBS:

* WBS CRUD
* WBS Tree Structure
* WBS Frontend Integration
* WBS Status Change
* WBS Delete
* WBS Browser Verification

Schedule:

* Schedule CRUD
* Schedule Search
* Schedule Pagination
* Dynamic Sorting
* Schedule Period Search
* Schedule Validation
* Swagger Verification
* Schedule Frontend API Client
* Schedule Frontend API Integration
* Schedule UI
* Schedule Browser Verification

#### Planned

Schedule Visualization:

* Calendar
* Timeline
* Milestone
* Gantt
* WBS / Schedule Relationship
* Progress Visualization

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
* Probability / Impact
* Risk Matrix
* Response Strategy
* Risk Monitoring

Change:

* Change Request
* Change Approval
* Change History
* Change Impact Analysis

---

## v0.8.x

### Evidence & Inspection Management

#### Planned

Evidence:

* Evidence Requirement
* Evidence Checklist
* Evidence Registration
* Evidence Status
* Missing Evidence Detection

Verification:

* Reviewer
* Verification Status
* Approval
* Rejection
* Verification History

Inspection:

* Inspection Status
* Inspection Result
* Evidence Inspection
* Approval History

Dashboard:

* Evidence Count
* Missing Evidence
* Verification Pending
* Evidence Completion Rate

---

## v0.9.x

### CMDB

#### Planned

Configuration Item:

* Server CI
* Database CI
* Software CI

Relationship:

* CI Relationship
* Dependency Relationship

History:

* Version Management
* Configuration History
* Change Tracking

---

## v0.10.x

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
* Evidence Dashboard
* CMDB Dashboard

Reporting:

* Weekly Report
* Monthly Report
* Executive Report
* Evidence Status Report
* Excel Export
* PDF Export
* Statistics API

> Note:
>
> PMIS Dashboard Reference UI는 v0.5.3에서 선행 구현되었다.
>
> v0.10.x에서는 실제 Backend API와 Project / WBS / Schedule / Issue / Risk / Change / Evidence / CMDB 데이터를 통합한다.

---

## v1.0.0

### AI Powered PMIS

#### Planned

Spring AI:

* AI PM Assistant
* AI Project Summary
* AI Schedule Analysis
* AI Issue Summary
* AI Risk Analysis
* AI Change Impact Analysis
* AI Evidence Gap Analysis
* AI Meeting Summary
* AI Report Generation

LLM Integration:

* OpenAI
* Azure OpenAI
* Ollama

Knowledge Base:

* PMIS Document Search
* Semantic Search
* RAG 기반 질의응답

---

# Git Flow

```text
main

 │

release

 │

develop

 │

 ├── feature/common
 │
 ├── feature/security
 │
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
 ├── feature/wbs-management
 ├── feature/schedule-management
 │
 ├── feature/frontend-layout
 ├── feature/frontend-components
 ├── feature/frontend-dashboard-hmc-reference
 ├── feature/frontend-project
 ├── feature/frontend-wbs
 ├── feature/frontend-schedule
 │
 ├── feature/issue-management
 ├── feature/risk-management
 ├── feature/change-management
 │
 ├── feature/evidence-management
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

---

# Branch Policy

* `main` : Production Release
* `develop` : Integration Branch
* `feature/*` : Feature Development
* `release/*` : Release Preparation
* `hotfix/*` : Production Hot Fix

모든 주요 기능은 Feature Branch에서 개발한다.

```text
Feature Development

        ↓

Local Test

        ↓

Build

        ↓

Documentation

        ↓

Commit

        ↓

Push

        ↓

Pull Request

        ↓

Merge to develop

        ↓

Integration Test
```

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

feat: implement WBS CRUD

feat: implement schedule management API

feat(frontend): complete project CRUD

feat(frontend): implement WBS API integration

feat(frontend): implement WBS status change

feat(frontend): implement WBS delete

feat(frontend): implement schedule management UI

fix: resolve JWT validation issue

docs: update development roadmap

refactor: improve exception handling
```

---

# Development Process

모든 기능은 아래 절차를 따른다.

```text
Planning

    ↓

Requirement

    ↓

Domain / Data Model Design

    ↓

Feature Branch 생성

    ↓

Backend API Design

    ↓

Backend Implementation

    ↓

API Verification

    ↓

Frontend API Client

    ↓

Frontend Implementation

    ↓

API Integration

    ↓

Browser Verification

    ↓

E2E Verification

    ↓

Production Build

    ↓

Documentation Update

    ↓

Commit

    ↓

Push

    ↓

Pull Request

    ↓

Merge to develop

    ↓

Integration Test

    ↓

Release
```

---

# Frontend API Integration Process

Frontend API Integration은 다음 흐름을 따른다.

```text
Backend API

    ↓

Swagger / API Verification

    ↓

Frontend API Client

    ↓

Type Definition

    ↓

Feature Component

    ↓

Page UI

    ↓

API Integration

    ↓

Loading State

    ↓

Empty State

    ↓

Error State

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

# Schedule Development Flow

Schedule Domain은 Backend CRUD만으로 완료되지 않는다.

```text
Schedule Backend

    │

    ├── CRUD                     ✓
    │
    ├── Search                   ✓
    │
    ├── Pagination               ✓
    │
    ├── Sorting                  ✓
    │
    ├── Period Search            ✓
    │
    └── Swagger Verification     ✓

                │

                ▼

Schedule Frontend

    │

    ├── API Client               ✓
    │
    ├── List                     ✓
    │
    ├── Search                   ✓
    │
    ├── Detail                   ✓
    │
    ├── Create                   ✓
    │
    ├── Edit                     ✓
    │
    ├── Delete                   ✓
    │
    └── API Integration          ✓

                │

                ▼

Visualization

    │

    ├── Calendar                 →
    │
    ├── Timeline                 →
    │
    ├── Milestone                →
    │
    └── Gantt                    →
```

---

# Documentation Policy

다음 문서는 항상 최신 상태를 유지한다.

* `CHANGELOG.md`
* `DEVELOPMENT_ROADMAP.md`
* `PROJECT_OVERVIEW.md`
* `ARCHITECTURE.md`
* `PORTFOLIO.md`

Design 문서:

* `docs/design/project/PROJECT_DESIGN.md`
* `docs/design/project/PROJECT_DASHBOARD_DESIGN.md`
* `docs/design/wbs/WBS_DESIGN.md`
* `docs/design/schedule/SCHEDULE_DESIGN.md`
* `docs/design/issue/ISSUE_DESIGN.md`
* `docs/design/report/REPORT_DESIGN.md`
* `docs/design/server/SERVER_CONFIGURATION_DESIGN.md`
* `docs/design/evidence/EVIDENCE_DESIGN.md`

Frontend 문서:

* `001_frontend_architecture.md`
* `002_frontend_folder_structure.md`
* `003_frontend_coding_convention.md`
* `004_frontend_ui_design.md`
* `005_frontend_component_architecture.md`
* `006_frontend_development_history.md`

소스코드 변경이 발생하면 관련 문서를 함께 검토하고 현행화한다.

특히 API Integration 변경 시 다음 항목을 함께 검토한다.

```text
Source Code

        ↓

API Contract

        ↓

Design Document

        ↓

Frontend Architecture

        ↓

Component Architecture

        ↓

Development History

        ↓

CHANGELOG

        ↓

DEVELOPMENT ROADMAP
```

---

# Maintenance Policy

* 모든 기능은 Feature Branch에서 개발한다.
* 모든 주요 변경 사항은 CHANGELOG에 기록한다.
* Backend와 Frontend의 개발 상태를 Roadmap에 반영한다.
* 문서와 소스코드는 항상 동일한 상태를 유지한다.
* Pull Request 검토 후 `develop` 브랜치에 병합한다.
* `main` 브랜치에는 검증된 코드만 Release한다.
* Semantic Versioning을 준수한다.
* Release마다 Git Tag를 생성한다.
* API Integration 기능은 API Verification과 Browser Verification을 수행한다.
* Feature Merge 후 `develop` Integration Test를 수행한다.
* UI Reference 구현과 실제 API Integration 단계를 구분한다.
* Backend와 Frontend 완료 상태를 독립적으로 관리한다.
* Domain 완료는 Backend 구현만으로 판단하지 않는다.
* Domain 완료 여부는 Backend / Frontend / Integration / Verification을 기준으로 판단한다.

---

# Current Information

| Item                       | Value                                                    |
| -------------------------- | -------------------------------------------------------- |
| Current Branch             | **develop**                                              |
| Current Sprint             | **Sprint 3 - WBS / Schedule / Frontend API Integration** |
| Project Frontend           | **CRUD Complete**                                        |
| WBS Backend                | **Complete**                                             |
| WBS Frontend               | **Integration Complete**                                 |
| Schedule Backend           | **CRUD / Search / Swagger Verification Complete**        |
| Schedule Frontend          | **UI / API Integration Complete**                        |
| Schedule Visualization     | **Calendar / Gantt Planned**                             |
| Current Integration Branch | **develop**                                              |
| Next Development Stage     | **Calendar UI / Schedule Visualization**                 |
| Next Sprint                | **Sprint 4 - Issue / Risk / Change**                     |
| Evidence Domain            | **Sprint 5 Planned**                                     |
| Maintainer                 | **Seo Seokhyeon**                                        |

---

# Next Milestone

## Sprint 3 Completion

### Backend

```text
WBS CRUD                       ✓

WBS Tree Structure             ✓

WBS Validation                 ✓

WBS Delete                     ✓


Schedule CRUD                  ✓

Schedule Search                ✓

Schedule Pagination            ✓

Schedule Sorting               ✓

Schedule Period Search         ✓

Schedule Validation            ✓

Schedule Swagger Verification  ✓
```

### Frontend

```text
Project CRUD                   ✓

WBS API Integration            ✓

WBS UI                         ✓

WBS Delete                     ✓

Schedule API Client            ✓

Schedule API Integration       ✓

Schedule UI                    ✓

Schedule Browser Verification  ✓

Calendar UI                    →

Gantt UI                       →
```

### Integration

```text
Project API Integration        ✓

Project Browser Verification   ✓

WBS API Integration            ✓

WBS Browser Verification       ✓

Schedule Backend Verification  ✓

Schedule API Integration       ✓

Schedule Browser Verification  ✓

Calendar Integration           →

Gantt Integration              →

Dashboard API Integration      →

E2E Verification               →
```

---

# Release Summary

| Version      | Description                                |
| ------------ | ------------------------------------------ |
| v0.1.0       | Project Initialization                     |
| v0.2.0       | Common Infrastructure                      |
| v0.3.0       | Security Foundation                        |
| v0.3.1       | Authentication & Authorization             |
| v0.3.2       | Swagger / OpenAPI                          |
| v0.4.0       | Project Management Backend                 |
| v0.5.0       | Frontend Foundation                        |
| v0.5.1       | Frontend Layout & Common Components        |
| v0.5.2       | Project Detail API & Controller Separation |
| v0.5.3       | Frontend Dashboard HMC Reference           |
| Unreleased   | Project Frontend CRUD                      |
| Unreleased   | WBS Management & Frontend Integration      |
| Unreleased   | Schedule Backend Management                |
| Unreleased   | Schedule Frontend API Integration          |
| Next Release | WBS / Schedule Management Integration      |
| v0.7.x       | Issue / Risk / Change                      |
| v0.8.x       | Evidence & Inspection                      |
| v0.9.x       | CMDB                                       |
| v0.10.x      | Dashboard & Reporting                      |
| v1.0.0       | AI Powered PMIS                            |

---

# End of Document
