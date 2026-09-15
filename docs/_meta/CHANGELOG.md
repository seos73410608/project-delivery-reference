# CHANGELOG

> PMIS (Project Management Information System) 변경 이력 관리 문서  
>
> 프로젝트 진행 과정에서 실제 개발이 완료되고 `develop` 브랜치에 통합된 주요 변경 사항과 Release 이력을 기록한다.

---

# 변경 이력 관리 원칙

본 문서는 PMIS 프로젝트의 주요 변경 사항과 Release 이력을 관리한다.

## 기록 대상

- Feature
- Improvement
- Bug Fix
- Refactoring
- Security
- Performance
- Documentation
- Build

CHANGELOG에는 **실제 개발이 완료되고 `develop` 브랜치에 통합된 주요 변경 사항**을 기록한다.

개발 예정 사항과 향후 구현 계획은 `DEVELOPMENT_ROADMAP.md`에서 관리한다.

`Unreleased`는 현재 `develop` 브랜치에 통합되었지만 정식 Release Tag가 생성되지 않은 변경 사항을 관리한다.

---

# Versioning

PMIS는 Semantic Versioning(SemVer)을 따른다.

```text
MAJOR.MINOR.PATCH
```

- **Major**: 기존 API 또는 시스템과 호환되지 않는 변경
- **Minor**: 기존 기능과 호환되는 기능 추가
- **Patch**: 기존 기능과 호환되는 버그 수정

현재 PMIS는 초기 개발 단계이므로 `0.x.x` 버전을 사용한다.

---

# Release History

## v0.1.0 (2026-08-03)

### Project Initialization

#### Added

- Spring Boot 프로젝트 생성
- Java 21 적용
- Gradle Kotlin DSL 적용
- MariaDB 연동
- Git Repository 구성
- Git Flow 브랜치 전략 적용
- 기본 프로젝트 구조 생성

---

## v0.2.0 (2026-08-03)

### Common Infrastructure

#### Added

- Domain 기반 Package 구조
- `BaseEntity`
- `ApiResponse`
- `ErrorCode`
- `CommonErrorCode`
- `BusinessException`
- `GlobalExceptionHandler`
- `JpaConfig`

#### Changed

- API Response 표준화
- Global Exception 구조 적용
- JPA Auditing 기반 생성/수정 시간 관리 적용

---

## v0.3.0 (2026-08-04)

### Security Foundation

#### Added

Spring Security:

- `SecurityConfig`
- BCrypt PasswordEncoder
- Stateless Authentication
- CORS Configuration

JWT:

- `JwtProvider`
- `JwtAuthenticationFilter`
- `JwtAuthenticationEntryPoint`
- `JwtAccessDeniedHandler`
- JWT Properties

Test API:

- GET `/api/test/public`
- GET `/api/test/private`
- GET `/api/test/token`

#### Changed

- Session 기반 인증 제거
- JWT 기반 Stateless Authentication 적용

#### Fixed

- Spring Boot / JWT 라이브러리 호환성 문제 수정
- Security Handler 개선
- Security Configuration 오류 수정

---

## v0.3.1 (2026-08-05)

### Authentication & Authorization

#### Added

Authentication:

- Login API
- Refresh Token API
- Token Reissue
- Authentication Service
- User Authentication Flow

JWT:

- Access Token Validation
- Refresh Token Validation

Authorization:

- Role Hierarchy
- USER Role
- PM Role
- ADMIN Role

Role Test API:

- GET `/api/role/authenticated`
- GET `/api/role/user`
- GET `/api/role/pm`
- GET `/api/role/admin`

Authentication API:

- POST `/api/auth/login`
- POST `/api/auth/refresh`

#### Changed

- Authentication Flow 개선
- Authorization 처리 개선
- Role 기반 접근 제어 적용

#### Fixed

- JWT Validation 문제 수정
- Security Exception 처리 개선

---

## v0.3.2 (2026-08-06)

### API Documentation

#### Added

Swagger / OpenAPI:

- OpenAPI 3
- Swagger UI
- JWT Authorization
- API Documentation

#### Changed

- API 문서 자동화
- JWT 인증 API 테스트 환경 개선

---

## v0.4.0 (2026-08-06)

### Project Management Backend

#### Added

Project Domain:

- `Project` Entity
- `ProjectService`
- `ProjectRepository`
- `ProjectController`

Project API:

- POST `/api/projects`
- GET `/api/projects`
- GET `/api/projects/{projectId}`
- PUT `/api/projects/{projectId}`
- DELETE `/api/projects/{projectId}`

Search:

- Project Search
- Sorting
- Sorting Column Validation

Dashboard:

- Project Dashboard API
- Project Status KPI
- Project Priority KPI
- Recent Project KPI
- Upcoming Deadline KPI

#### Changed

- Project Domain 구조 개선
- Service Layer 리팩토링
- Validation 구조 개선
- 검색 API 정렬 컬럼 검증 적용

#### Fixed

- Project Validation 오류 수정
- Search 조건 처리 개선

---

## v0.5.0 (2026-08-07)

### Frontend Foundation

#### Added

Frontend:

- React
- Vite
- TypeScript
- npm
- React Router DOM

Frontend Architecture:

- Backend / Frontend 프로젝트 분리
- Feature 기반 구조 적용
- Layout 기반 화면 구성
- Frontend Path Alias 구성

React Router:

- `AppRouter`
- `MainLayout`
- `DashboardPage`

Dashboard:

- PMIS Dashboard Skeleton
- Development Status 화면

#### Changed

- 기존 Vite Sample 제거
- React Example 제거
- Counter Example 제거
- Hero Image 제거
- React Logo 제거
- Vite Logo 제거
- Frontend Path Alias 구성
- Vite / TypeScript Path Resolution 구성

#### Removed

- Vite Example Application
- Sample Assets
- Sample Counter

---

## v0.5.1 (2026-08-08)

### Frontend Layout & Common UI Components

#### Added

Layout Components:

- Header Component
- Sidebar Component
- Breadcrumb Component

Common UI Components:

- Button Component
- Card Component
- Loading Component
- EmptyState Component

Main Layout:

- Header / Sidebar / Breadcrumb 독립 Component 분리
- MainLayout Refactoring
- Router 기반 Content 영역 구성

Breadcrumb:

- 현재 Router Path 기반 Breadcrumb 구성

#### Changed

- MainLayout 직접 구현 영역을 독립 Component로 분리
- Layout Component와 Common UI Component 분리
- Frontend Component 구조 정리
- `@/components/...` Alias 기반 Import 구조 적용
- Vite Path Resolution 설정 정리

#### Documentation

- `006_frontend_development_history.md` 현행화
- Frontend Layout 및 Common UI Component 개발 이력 추가

---

## v0.5.2 (2026-08-10)

### Project Detail API & Controller Separation

#### Added

Project Detail:

- `ProjectDetailController`
- `ProjectDetailService`
- `ProjectDetailResponse`
- Project Detail API

API:

- GET `/api/projects/{projectId}/detail`

#### Changed

Controller 책임 분리:

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

- 기존 Project 단건 조회 API와 상세 조회 책임 분리
- 프로젝트 상세 조회 전용 Controller 구성
- 프로젝트 상세 조회 전용 Service 구성
- 프로젝트 상세 조회 전용 Response DTO 구성

Dashboard:

- Project Dashboard Controller 분리
- Dashboard 관련 API와 Project CRUD API 책임 분리

#### Refactoring

- Controller 단일 책임 구조 개선
- Service 단일 책임 구조 개선
- Project Domain API 구조 정리
- WBS / Schedule / Issue / Risk / Change 연계 기반 마련

#### Documentation

- Project Detail API Swagger 문서화
- Project Domain API 구조 문서화

---

## v0.5.3 (2026-08-11)

### Frontend Dashboard HMC Reference

#### Added

Dashboard Reference:

- Power HMC(Hardware Management Console) 스타일을 참고한 PMIS Dashboard 구성
- 프로젝트 운영 현황 중심의 통합 Dashboard 형태로 확장
- Project Overview 영역
- KPI 영역
- WBS Progress 영역
- Schedule Summary 영역
- Issue Summary 영역
- Recent Activity 영역

Dashboard Components:

- `KpiCard`
- `ProjectOverview`
- `WbsProgress`
- `ScheduleSummary`
- `IssueSummary`
- `RecentActivity`

#### Changed

- 기존 Dashboard Skeleton을 PMIS 운영 Dashboard 형태로 확장
- DashboardPage를 Dashboard Component 단위로 분리
- KPI 영역을 `KpiCard` 재사용 Component로 분리
- Project Overview를 `ProjectOverview` Component로 분리
- WBS Progress를 `WbsProgress` Component로 분리
- Schedule Summary를 `ScheduleSummary` Component로 분리
- Issue Summary를 `IssueSummary` Component로 분리
- Recent Activity를 `RecentActivity` Component로 분리
- Dashboard Component Import 구조 정리

#### Build

- TypeScript Build 오류 수정
- TypeScript 6.x `baseUrl` deprecated 대응
- `ignoreDeprecations` 설정 적용
- `npm run build` 성공
- Vite Production Build 성공

#### Git

Feature Branch:

```text
feature/frontend-dashboard-hmc-reference
```

- Feature Branch Push 완료
- `develop` Branch Merge 완료
- `develop` Push 완료

#### Note

- Dashboard 데이터는 Reference UI 구현을 위한 정적 데이터
- Project / WBS / Schedule / Issue 실제 API 연동은 후속 Feature에서 진행
- 현재 단계에서는 Dashboard UI 구조와 Component 책임 분리를 우선 확보

---

# Unreleased

> 현재 `develop` 브랜치에 통합되었으며, 다음 정식 Release 후보로 관리한다.

---

## Project Frontend CRUD

### Added

- Project API Client
- Project List UI
- Project Search UI
- Project Detail UI
- Project Registration UI
- Project Edit UI
- Project Delete UI
- Project List / Search / Detail / Create / Update / Delete API Integration
- Loading / Empty / Error State
- Delete Confirmation
- API Error Handling
- CRUD 결과 UI 반영

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

## WBS Management Backend & Frontend Integration

### Added

WBS Domain:

- WBS CRUD
- WBS Tree Structure
- WBS 단건 조회
- WBS 생성
- WBS 수정
- WBS 삭제
- WBS Validation

WBS Relationship:

- Parent WBS
- Child WBS
- Tree Structure

Delete Handling:

- 하위 WBS 존재 여부 처리
- Parent / Child 관계 고려
- 삭제 Validation

### API

- WBS Tree API
- WBS Search API
- WBS Detail API
- WBS Create API
- WBS Update API
- WBS Delete API

### Frontend

API Client:

- `apiClient`
- `getWbsTree`
- `searchWbs`
- `getWbs`
- `createWbs`
- `updateWbs`
- `deleteWbs`

Components:

- `WbsTree`
- `WbsTreeNode`
- `WbsDetail`
- `WbsForm`
- `WbsStatusForm`

UI:

- WBS Tree
- WBS Detail
- WBS Search
- WBS Create
- WBS Edit
- WBS Status Change
- WBS Delete

### Changed

- 기존 WBS UI를 실제 Backend API 기반으로 전환
- WBS Tree 조회 결과와 Frontend Tree 구조 연동
- WBS Search 결과 처리 개선
- WBS 생성 후 최신 Tree 재조회
- WBS 수정 후 최신 Tree 재조회
- WBS 상태 변경 후 최신 데이터 반영
- WBS 삭제 후 최신 Tree 재조회
- WBS 삭제 성공 후 선택 상태 초기화
- Parent WBS 관계를 Tree 기반으로 처리

### Validation

- 동일 상태 변경 방지
- 하위 WBS 존재 여부 확인
- 하위 WBS가 존재하는 경우 삭제 제한
- 삭제 가능 조건 확인 후 DELETE API 호출

### UX

- Loading State
- Error State
- Empty State
- Delete Confirmation
- Delete 후 Tree Refresh
- 선택 WBS 상태 초기화

### Verification

- WBS CRUD API Integration 검증
- WBS Status Change 검증
- WBS Delete 검증
- Browser 기반 기능 검증

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

## Schedule Management Backend & Frontend Integration

### Added

Schedule Domain:

- Schedule Entity
- Schedule Repository
- Schedule Service
- Schedule Controller
- Schedule DTO
- Schedule Validation

Schedule CRUD:

- Schedule List
- Schedule Detail
- Schedule Create
- Schedule Update
- Schedule Delete

Schedule Status / Order:

- Schedule Status 변경
- Sort Order 관리

Schedule Search:

- Project 조건 검색
- WBS 조건 검색
- Keyword 검색
- Status 검색
- Start Date / End Date 기간 조건 검색

Pagination / Sorting:

- Pagination
- Page / Size 처리
- Dynamic Sorting
- Sorting 조건 처리

### Completed

```text
Schedule CRUD                ✓
Schedule List                ✓
Schedule Detail              ✓
Schedule Create              ✓
Schedule Update              ✓
Schedule Delete              ✓
Schedule Status              ✓
Schedule Sort Order          ✓
Schedule Search              ✓
Project Search               ✓
WBS Search                   ✓
Keyword Search               ✓
Status Search                ✓
Pagination                   ✓
Dynamic Sorting              ✓
Period Search                ✓
Validation                   ✓
Swagger Verification         ✓
```

### API Verification

Schedule API는 Swagger / OpenAPI를 통해 주요 CRUD 및 검색 시나리오를 검증하였다.

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

- Validation
- Not Found
- Search 조건
- Pagination
- Sorting
- Period 조건

### Frontend

Schedule Frontend:

- Schedule API Client
- Schedule Type 정의
- Schedule List UI
- Schedule Search UI
- Schedule Detail UI
- Schedule Registration UI
- Schedule Edit UI
- Schedule Delete UI
- Schedule Status UI

Schedule API Integration:

- Schedule List API Integration
- Schedule Detail API Integration
- Schedule Create API Integration
- Schedule Update API Integration
- Schedule Delete API Integration
- Schedule Search API Integration

State Handling:

- Loading State
- Empty State
- Error State

### Changed

- 기존 Schedule UI를 실제 Backend Schedule API와 연계
- Schedule 데이터를 API 기반으로 조회하도록 변경
- Create / Update 이후 최신 데이터 반영
- Delete 이후 목록 데이터 재조회
- Search 조건과 Backend API Query Parameter 연동
- Status 정보를 실제 Schedule 데이터와 연동

### Verification

- Schedule CRUD 확인
- Schedule Search 확인
- Schedule Detail 확인
- Schedule Create 확인
- Schedule Edit 확인
- Schedule Delete 확인
- Browser UI 확인
- Production Build 확인

### Git

Backend Feature:

```text
feature/schedule-management
```

Frontend Feature:

```text
feature/frontend-schedule
```

Schedule Backend와 Frontend를 포함한 일정 관리 기능은 `develop` 브랜치에 통합되었다.

---

## Issue Management Hotfix — Project-scoped Issue Key

### Added

Issue Key 정책을 프로젝트 단위로 명확화하였다.

- 프로젝트별 Issue Key 생성
- 동일 프로젝트 내 Issue Key 중복 방지
- Backend에서 Issue Key 자동 생성
- Frontend는 Issue Key를 입력하지 않고 Backend 생성 값을 표시

Issue Key Format:

```text
ISSUE-001
ISSUE-002
ISSUE-003
...
```

Project Scope:

```text
Project A
├─ ISSUE-001
├─ ISSUE-002
└─ ISSUE-003

Project B
├─ ISSUE-001
├─ ISSUE-002
└─ ISSUE-003
```

### Database

`issues` 테이블에 다음 정책을 적용한다.

```text
UNIQUE(project_id, issue_key)
```

즉, 동일 프로젝트에서는 동일한 Issue Key를 사용할 수 없지만 서로 다른 프로젝트에서는 동일한 Issue Key를 사용할 수 있다.

### Backend

Issue 생성 시 프로젝트 기준으로 최근 Issue를 조회하여 다음 Sequence를 생성한다.

```text
findTopByProject_IdOrderByIdDesc(Long projectId)
```

생성 흐름:

```text
Project
   ↓
Issue Create
   ↓
Project 기준 최근 Issue 조회
   ↓
Issue Sequence 증가
   ↓
ISSUE-%03d 생성
   ↓
Issue 저장
```

Issue Key는 생성 이후 수정 대상에서 제외하며 프로젝트별 식별자로 유지한다.

### Git

```text
hotfix/issue-key-generation
```

예정 Commit:

```text
fix: add project-based issue key generation
```

> Hotfix가 아직 `develop`에 통합되지 않은 경우, 위 항목은 실제 CHANGELOG 반영 시점에 맞춰 `Unreleased` 상태로 유지한다.

---

# Current Development Status

## Backend

### Completed

Foundation:

- Spring Boot
- Gradle Kotlin DSL
- MariaDB
- Common Infrastructure
- JPA Auditing

Security:

- Spring Security
- JWT Authentication
- Login
- Refresh Token
- Role Hierarchy
- Role Authorization

Documentation:

- Swagger
- OpenAPI

Project:

- Project CRUD
- Project Search
- Project Sorting
- Project Dashboard API
- Project Dashboard KPI
- Project Detail API
- Project Controller Separation
- Project Dashboard Controller Separation
- Project Detail Controller Separation

WBS:

- WBS CRUD
- WBS Tree Structure
- WBS Validation
- WBS Delete Handling

Schedule:

- Schedule CRUD
- Schedule Search
- Project / WBS / Keyword / Status Search
- Pagination
- Dynamic Sorting
- Period Search
- Schedule Status
- Schedule Sort Order
- Validation
- Swagger Verification

Issue:

- Issue Domain Backend
- Project-scoped Issue Key 설계 및 구현 진행

### Next

- Issue Management Frontend
- Calendar / Gantt Visualization
- Risk Management
- Change Management
- Evidence Management
- CMDB Management
- Dashboard Integration
- Report
- Spring AI

---

## Frontend

### Completed

Foundation:

- React
- Vite
- TypeScript
- React Router DOM
- Feature-based Directory Structure
- Path Alias

Layout:

- MainLayout
- Header
- Sidebar
- Breadcrumb

Common Components:

- Button
- Card
- Loading
- EmptyState

Dashboard:

- Dashboard Skeleton
- HMC Reference UI
- Dashboard Component Separation
- KPI Card
- Project Overview
- WBS Progress
- Schedule Summary
- Issue Summary
- Recent Activity

Project:

- Project API Client
- Project List / Search / Detail
- Project Create / Edit / Delete
- Project CRUD API Integration
- Loading / Empty / Error State

WBS:

- WBS API Client
- WBS Tree
- WBS Detail
- WBS Search
- WBS Form
- WBS Create / Edit
- WBS Status Change
- WBS Delete
- WBS API Integration

Schedule:

- Schedule API Client
- Schedule List / Search / Detail
- Schedule Create / Edit / Delete
- Schedule Status
- Schedule API Integration
- Loading / Empty / Error State

### In Progress

Issue:

- Issue UI development
- Issue API Integration
- Issue Key display

### Next

- Calendar UI
- Calendar API Integration
- Gantt UI
- WBS / Schedule Relationship Visualization
- Project Dashboard API Integration
- Dashboard Data Integration
- Authentication UI
- Risk UI
- Change UI
- Evidence UI
- CMDB UI
- Report UI
- AI Assistant UI

---

# Integration

## Completed

Project:

- Project API Integration
- Project CRUD Verification
- Project Detail Verification
- Project Delete Verification

WBS:

- WBS API Integration
- WBS CRUD Integration
- WBS Status Integration
- WBS Delete Integration
- Browser Verification

Schedule:

- Schedule Backend API Verification
- Schedule API Client
- Schedule CRUD Integration
- Schedule Search Integration
- Schedule UI Verification
- Backend / Frontend Integration

## In Progress

Issue:

- Issue Backend / Frontend Integration
- Issue Key API Contract Integration

## Next

- Calendar Integration
- Gantt Integration
- Dashboard API Integration
- Dashboard Data Integration
- Authentication Integration
- Issue E2E Integration
- Risk E2E Integration
- Change E2E Integration
- Full E2E Verification

---

# Current Development Snapshot

```text
Backend
├─ Common Infrastructure          ✓
├─ Security / JWT                 ✓
│
├─ Project CRUD                   ✓
├─ Project Search                 ✓
├─ Project Dashboard API          ✓
├─ Project Detail API             ✓
│
├─ WBS Management                 ✓
│
├─ Schedule CRUD                  ✓
├─ Schedule Search                ✓
├─ Schedule Pagination            ✓
├─ Schedule Sorting               ✓
├─ Schedule Period Search         ✓
├─ Schedule Validation            ✓
├─ Schedule Swagger Test          ✓
│
├─ Issue Domain                   ✓
├─ Issue Key Generation           →
├─ Risk                           →
├─ Change                         →
├─ Evidence                       →
├─ CMDB                           →
├─ Dashboard                      →
├─ Report                         →
│
└─ Spring AI                      →


Frontend
├─ React / Vite / TypeScript      ✓
├─ Router / Layout                ✓
├─ Common UI Components           ✓
├─ Dashboard Reference UI         ✓
│
├─ Project CRUD                   ✓
├─ WBS API Integration            ✓
├─ WBS UI                         ✓
│
├─ Schedule API Client             ✓
├─ Schedule Integration            ✓
├─ Schedule UI                     ✓
│
├─ Issue UI                        →
├─ Issue API Integration           →
│
├─ Calendar                        →
└─ Gantt                           →


Integration
├─ Project API Integration         ✓
├─ Project CRUD Verification       ✓
│
├─ WBS API Integration             ✓
├─ WBS Browser Verification        ✓
│
├─ Schedule API Verification       ✓
├─ Schedule API Integration        ✓
├─ Schedule Browser Verification   ✓
│
├─ Issue API Integration            →
├─ Issue Browser Verification      →
├─ Calendar Integration             →
├─ Gantt Integration                →
├─ Dashboard API Integration        →
│
└─ E2E Verification                →
```

---

# Current Sprint

## Sprint 3

### WBS / Schedule / Frontend API Integration

Sprint 3의 핵심 Backend / Frontend API Integration 목표는 완료되었다.

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
```

### Sprint 3 Remaining Scope

Schedule Visualization:

```text
Calendar
    ↓
Calendar API Integration
    ↓
Timeline / Milestone
    ↓
Gantt UI
    ↓
WBS / Schedule Relationship
    ↓
Sprint 3 E2E Verification
```

> Calendar과 Gantt는 Schedule Domain의 시각화 확장 기능으로 관리한다.

---

# Current Development Priority

```text
1. Issue Key Hotfix Completion
        ↓
2. Issue Management Frontend
        ↓
3. Issue API Integration
        ↓
4. Issue Browser Verification
        ↓
5. Calendar UI
        ↓
6. Calendar API Integration
        ↓
7. Gantt UI
        ↓
8. WBS / Schedule Relationship
        ↓
9. Schedule Timeline Visualization
        ↓
10. Project Dashboard API Integration
        ↓
11. Dashboard Data Integration
        ↓
12. Sprint 3 E2E Verification
        ↓
13. Risk Management
        ↓
14. Change Management
        ↓
15. Evidence Management
```

---

# Next Feature

현재 Issue Management Backend가 개발되어 있으며, 프로젝트별 Issue Key 생성 Hotfix를 진행한다.

```text
hotfix/issue-key-generation
```

Hotfix 완료 후 Frontend Issue Management를 진행한다.

```text
feature/issue-management-ui
```

권장 개발 순서:

```text
Issue Backend
        ✓
        ↓
Issue Key Hotfix
        ↓
Issue Frontend UI
        ↓
Issue API Integration
        ↓
Issue Browser Verification
        ↓
Issue E2E Verification
        ↓
Calendar / Gantt
```

---

# Planned Releases

## v0.6.x

### WBS & Schedule Management Integration

#### Completed

WBS:

- WBS CRUD
- WBS Tree Structure
- WBS Frontend Integration
- WBS Status Change
- WBS Delete
- WBS Browser Verification

Schedule:

- Schedule CRUD
- Schedule Search
- Schedule Pagination
- Dynamic Sorting
- Schedule Period Search
- Schedule Validation
- Swagger Verification
- Schedule Frontend API Client
- Schedule Frontend API Integration
- Schedule UI
- Schedule Browser Verification

#### Planned

Schedule Visualization:

- Calendar
- Timeline
- Milestone
- Gantt
- WBS / Schedule Relationship
- Progress Visualization

---

## v0.7.x

### Issue / Risk / Change Management

#### Planned

Issue:

- Issue CRUD
- Project-scoped Issue Key
- Issue Assignment
- Issue Status Workflow
- Priority Management
- Attachment Management

Risk:

- Risk CRUD
- Risk Assessment
- Probability / Impact
- Risk Matrix
- Response Strategy
- Risk Monitoring

Change:

- Change Request
- Change Approval
- Change History
- Change Impact Analysis

---

## v0.8.x

### Evidence & Inspection Management

#### Planned

Evidence:

- Evidence Requirement
- Evidence Checklist
- Evidence Registration
- Evidence Status
- Missing Evidence Detection

Verification:

- Reviewer
- Verification Status
- Approval
- Rejection
- Verification History

Inspection:

- Inspection Status
- Inspection Result
- Evidence Inspection
- Approval History

Dashboard:

- Evidence Count
- Missing Evidence
- Verification Pending
- Evidence Completion Rate

---

## v0.9.x

### CMDB

#### Planned

Configuration Item:

- Server CI
- Database CI
- Software CI

Relationship:

- CI Relationship
- Dependency Relationship

History:

- Version Management
- Configuration History
- Change Tracking

---

## v0.10.x

### Dashboard & Reporting

#### Planned

Dashboard:

- Executive Dashboard
- Project Dashboard
- WBS Dashboard
- Schedule Dashboard
- Issue Dashboard
- Risk Dashboard
- Change Dashboard
- Evidence Dashboard
- CMDB Dashboard

Reporting:

- Weekly Report
- Monthly Report
- Executive Report
- Evidence Status Report
- Excel Export
- PDF Export
- Statistics API

> PMIS Dashboard Reference UI는 v0.5.3에서 선행 구현되었다.
>
> v0.10.x에서는 실제 Backend API와 Project / WBS / Schedule / Issue / Risk / Change / Evidence / CMDB 데이터를 통합한다.

---

## v1.0.0

### AI Powered PMIS

#### Planned

Spring AI:

- AI PM Assistant
- AI Project Summary
- AI Schedule Analysis
- AI Issue Summary
- AI Risk Analysis
- AI Change Impact Analysis
- AI Evidence Gap Analysis
- AI Meeting Summary
- AI Report Generation

LLM Integration:

- OpenAI
- Azure OpenAI
- Ollama

Knowledge Base:

- PMIS Document Search
- Semantic Search
- RAG 기반 질의응답

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
 ├── feature/security
 ├── feature/auth-jwt
 ├── feature/auth-login
 ├── feature/auth-refresh
 ├── feature/auth-role
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
 ├── feature/frontend-calendar
 │
 ├── feature/issue-management
 ├── feature/issue-management-ui
 ├── feature/risk-management
 ├── feature/change-management
 ├── feature/evidence-management
 ├── feature/cmdb-management
 ├── feature/dashboard
 ├── feature/report-excel
 ├── feature/report-pdf
 │
 ├── hotfix/issue-key-generation
 │
 └── feature/spring-ai
      ├── feature/ai-risk-analysis
      └── feature/ai-report-summary
```

---

# Branch Policy

- `main`: Production Release
- `develop`: Integration Branch
- `feature/*`: Feature Development
- `release/*`: Release Preparation
- `hotfix/*`: Production Hot Fix

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

Hotfix는 별도의 `hotfix/*` Branch에서 수정 후 검증하며, 실제 `develop` 통합 전까지는 작업 중인 변경 사항으로 관리한다.

---

# Commit Convention

| Prefix | Description |
| --- | --- |
| `feat` | Feature |
| `fix` | Bug Fix |
| `refactor` | Refactoring |
| `docs` | Documentation |
| `style` | Code Style |
| `test` | Test |
| `build` | Build |
| `chore` | Maintenance |
| `perf` | Performance |
| `security` | Security |

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
fix: add project-based issue key generation
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

# Issue Development Flow

Issue Domain은 Backend CRUD만으로 완료되지 않는다.

```text
Issue Backend
    │
    ├── CRUD                         ✓
    ├── Validation                   ✓
    ├── Status Workflow              ✓
    ├── Priority                     ✓
    ├── Assignment                   ✓
    └── Project-scoped Issue Key     →
                 │
                 ▼
Issue Frontend
    │
    ├── API Client                   →
    ├── List                         →
    ├── Search                       →
    ├── Detail                       →
    ├── Create                       →
    ├── Edit                         →
    └── Issue Key Display            →
                 │
                 ▼
Integration
    │
    ├── API Verification             →
    ├── Browser Verification         →
    └── E2E Verification             →
```

> Issue Key는 Backend가 생성하고 Frontend는 표시하는 구조로 관리한다.

---

# Schedule Development Flow

Schedule Domain은 Backend CRUD만으로 완료되지 않는다.

```text
Schedule Backend
    │
    ├── CRUD                     ✓
    ├── Search                   ✓
    ├── Pagination               ✓
    ├── Sorting                  ✓
    ├── Period Search            ✓
    └── Swagger Verification     ✓
              │
              ▼
Schedule Frontend
    │
    ├── API Client               ✓
    ├── List                     ✓
    ├── Search                   ✓
    ├── Detail                   ✓
    ├── Create                   ✓
    ├── Edit                     ✓
    ├── Delete                   ✓
    └── API Integration          ✓
              │
              ▼
Visualization
    │
    ├── Calendar                 →
    ├── Timeline                 →
    ├── Milestone                →
    └── Gantt                    →
```

---

# Documentation Policy

다음 문서는 항상 최신 상태를 유지한다.

- `CHANGELOG.md`
- `DEVELOPMENT_ROADMAP.md`
- `PROJECT_OVERVIEW.md`
- `ARCHITECTURE.md`
- `PORTFOLIO.md`

Design 문서:

- `docs/design/project/PROJECT_DESIGN.md`
- `docs/design/project/PROJECT_DASHBOARD_DESIGN.md`
- `docs/design/wbs/WBS_DESIGN.md`
- `docs/design/schedule/SCHEDULE_DESIGN.md`
- `docs/design/issue/ISSUE_DESIGN.md`
- `docs/design/report/REPORT_DESIGN.md`
- `docs/design/server/SERVER_CONFIGURATION_DESIGN.md`
- `docs/design/evidence/EVIDENCE_DESIGN.md`

Frontend 문서:

- `001_frontend_architecture.md`
- `002_frontend_folder_structure.md`
- `003_frontend_coding_convention.md`
- `004_frontend_ui_design.md`
- `005_frontend_component_architecture.md`
- `006_frontend_development_history.md`

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

- 모든 기능은 Feature Branch에서 개발한다.
- 모든 주요 변경 사항은 CHANGELOG에 기록한다.
- Backend와 Frontend의 개발 상태를 Roadmap에 반영한다.
- 문서와 소스코드는 항상 동일한 상태를 유지한다.
- Pull Request 검토 후 `develop` 브랜치에 병합한다.
- `main` 브랜치에는 검증된 코드만 Release한다.
- Semantic Versioning을 준수한다.
- Release마다 Git Tag를 생성한다.
- API Integration 기능은 API Verification과 Browser Verification을 수행한다.
- Feature Merge 후 `develop` Integration Test를 수행한다.
- UI Reference 구현과 실제 API Integration 단계를 구분한다.
- Backend와 Frontend 완료 상태를 독립적으로 관리한다.
- Domain 완료는 Backend 구현만으로 판단하지 않는다.
- Domain 완료 여부는 Backend / Frontend / Integration / Verification을 기준으로 판단한다.
- Hotfix는 `develop` 통합 전까지 실제 Release History가 아닌 작업 중인 변경 사항으로 관리한다.

---

# Current Information

| Item | Value |
| --- | --- |
| Current Branch | **`hotfix/issue-key-generation`** |
| Current Sprint | **Sprint 3 - WBS / Schedule / Frontend API Integration** |
| Project Frontend | **CRUD Complete** |
| WBS Backend | **Complete** |
| WBS Frontend | **Integration Complete** |
| Schedule Backend | **CRUD / Search / Swagger Verification Complete** |
| Schedule Frontend | **UI / API Integration Complete** |
| Issue Backend | **Domain / CRUD Complete, Issue Key Hotfix In Progress** |
| Issue Frontend | **In Progress - `feature/issue-management-ui`** |
| Schedule Visualization | **Calendar / Gantt Planned** |
| Current Integration Branch | **develop** |
| Next Development Stage | **Issue Frontend / Issue API Integration** |
| Next Sprint | **Sprint 4 - Issue / Risk / Change** |
| Evidence Domain | **Sprint 5 Planned** |
| Maintainer | **Seo Seokhyeon** |

---

# Next Milestone

## Issue Management Completion

### Backend

```text
Issue CRUD                         ✓
Issue Validation                  ✓
Issue Status Workflow             ✓
Issue Priority                    ✓
Issue Assignment                  ✓
Project-scoped Issue Key          →
```

### Frontend

```text
Issue API Client                  →
Issue List                        →
Issue Search                      →
Issue Detail                      →
Issue Create                      →
Issue Edit                        →
Issue Key Display                 →
Browser Verification              →
Production Build                  →
```

### Integration

```text
Issue API Verification            →
Issue API Integration             →
Issue Browser Verification        →
Issue E2E Verification            →
```

---

# Release Summary

| Version | Description |
| --- | --- |
| v0.1.0 | Project Initialization |
| v0.2.0 | Common Infrastructure |
| v0.3.0 | Security Foundation |
| v0.3.1 | Authentication & Authorization |
| v0.3.2 | Swagger / OpenAPI |
| v0.4.0 | Project Management Backend |
| v0.5.0 | Frontend Foundation |
| v0.5.1 | Frontend Layout & Common Components |
| v0.5.2 | Project Detail API & Controller Separation |
| v0.5.3 | Frontend Dashboard HMC Reference |
| Unreleased | Project Frontend CRUD |
| Unreleased | WBS Management & Frontend Integration |
| Unreleased | Schedule Backend & Frontend Integration |
| Unreleased | Issue Key Hotfix - In Progress |
| Next Release | WBS / Schedule / Issue Management Integration |
| v0.7.x | Issue / Risk / Change |
| v0.8.x | Evidence & Inspection |
| v0.9.x | CMDB |
| v0.10.x | Dashboard & Reporting |
| v1.0.0 | AI Powered PMIS |

---

# End of Document
