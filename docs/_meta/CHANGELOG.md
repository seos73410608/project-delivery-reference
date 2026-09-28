# CHANGELOG

> PMIS (Project Management Information System) 변경 이력 관리 문서\
> 프로젝트 진행 과정에서 실제 개발이 완료되고 `develop` 브랜치에 통합된
> 주요 변경 사항과 Release 이력을 기록한다.

------------------------------------------------------------------------

# 변경 이력 관리 원칙

본 문서는 PMIS 프로젝트의 주요 변경 사항과 Release 이력을 관리한다.

## 기록 대상

-   Feature
-   Improvement
-   Bug Fix
-   Refactoring
-   Security
-   Performance
-   Documentation
-   Build

CHANGELOG에는 **실제 개발이 완료되고 `develop` 브랜치에 통합된 주요 변경
사항**을 기록한다. 개발 예정 사항과 향후 구현 계획은
`DEVELOPMENT_ROADMAP.md`에서 관리한다.

`Unreleased`는 현재 `develop` 브랜치에 통합되었지만 정식 Release Tag가
생성되지 않은 변경 사항을 관리한다. 작업 중이며 아직 `develop`에
통합되지 않은 항목은 Unreleased의 완료 항목으로 기록하지 않는다.

------------------------------------------------------------------------

# Versioning

PMIS는 Semantic Versioning(SemVer)을 따른다.

``` text
MAJOR.MINOR.PATCH
```

-   **Major**: 기존 API 또는 시스템과 호환되지 않는 변경
-   **Minor**: 기존 기능과 호환되는 기능 추가
-   **Patch**: 기존 기능과 호환되는 버그 수정

초기 개발 단계에서는 `0.x.x` 버전을 사용한다.

------------------------------------------------------------------------

# Release History

## v0.1.0 (2026-08-03)

### Project Initialization

#### Added

-   Spring Boot 프로젝트 생성
-   Java 21 적용
-   Gradle Kotlin DSL 적용
-   MariaDB 연동
-   Git Repository 구성
-   Git Flow 브랜치 전략 적용
-   기본 프로젝트 구조 생성

------------------------------------------------------------------------

## v0.2.0 (2026-08-03)

### Common Infrastructure

#### Added

-   Domain 기반 Package 구조
-   `BaseEntity`
-   `ApiResponse`
-   `ErrorCode`
-   `CommonErrorCode`
-   `BusinessException`
-   `GlobalExceptionHandler`
-   `JpaConfig`

#### Changed

-   API Response 표준화
-   Global Exception 구조 적용
-   JPA Auditing 기반 생성/수정 시간 관리 적용

------------------------------------------------------------------------

## v0.3.0 (2026-08-04)

### Security Foundation

#### Added

-   Spring Security `SecurityConfig`
-   BCrypt PasswordEncoder
-   Stateless Authentication
-   CORS Configuration
-   JWT `JwtProvider`
-   `JwtAuthenticationFilter`
-   `JwtAuthenticationEntryPoint`
-   `JwtAccessDeniedHandler`
-   JWT Properties
-   Test API: `GET /api/test/public`, `GET /api/test/private`,
    `GET /api/test/token`

#### Changed

-   Session 기반 인증 제거
-   JWT 기반 Stateless Authentication 적용

#### Fixed

-   Spring Boot / JWT 라이브러리 호환성 문제 수정
-   Security Handler 개선
-   Security Configuration 오류 수정

------------------------------------------------------------------------

## v0.3.1 (2026-08-05)

### Authentication & Authorization

#### Added

-   Login API 및 Authentication Service
-   Refresh Token API 및 Token Reissue
-   Access Token / Refresh Token 검증
-   Role Hierarchy 및 `USER`, `PM`, `ADMIN` Role
-   Role Test API: `GET /api/role/authenticated`, `/api/role/user`,
    `/api/role/pm`, `/api/role/admin`
-   Authentication API: `POST /api/auth/login`, `POST /api/auth/refresh`

#### Changed

-   Authentication Flow 및 Authorization 처리 개선
-   Role 기반 접근 제어 적용

#### Fixed

-   JWT Validation 문제 수정
-   Security Exception 처리 개선

------------------------------------------------------------------------

## v0.3.2 (2026-08-06)

### API Documentation

#### Added

-   OpenAPI 3
-   Swagger UI
-   JWT Authorization
-   API Documentation

#### Changed

-   API 문서 자동화
-   JWT 인증 API 테스트 환경 개선

------------------------------------------------------------------------

## v0.4.0 (2026-08-06)

### Project Management Backend

#### Added

-   Project Entity / Repository / Service / Controller
-   Project CRUD API
    -   `POST /api/projects`
    -   `GET /api/projects`
    -   `GET /api/projects/{projectId}`
    -   `PUT /api/projects/{projectId}`
    -   `DELETE /api/projects/{projectId}`
-   Project Search 및 Sorting
-   Sorting Column Validation
-   Project Dashboard API 및 Status / Priority / Recent Project /
    Upcoming Deadline KPI

#### Changed

-   Project Domain 구조 및 Service Layer 개선
-   Validation 구조 개선
-   검색 API 정렬 컬럼 검증 적용

#### Fixed

-   Project Validation 오류 수정
-   Search 조건 처리 개선

------------------------------------------------------------------------

## v0.5.0 (2026-08-07)

### Frontend Foundation

#### Added

-   React, Vite, TypeScript, npm, React Router DOM
-   Backend / Frontend 프로젝트 분리
-   Feature 기반 구조 및 Layout 기반 화면 구성
-   Frontend Path Alias
-   `AppRouter`, `MainLayout`, `DashboardPage`
-   PMIS Dashboard Skeleton 및 Development Status 화면

#### Changed

-   기존 Vite / React 예제 및 샘플 자산 제거
-   Vite / TypeScript Path Resolution 구성
-   Frontend Path Alias 구성

#### Removed

-   Vite Example Application
-   Sample Assets 및 Counter

------------------------------------------------------------------------

## v0.5.1 (2026-08-08)

### Frontend Layout & Common UI Components

#### Added

-   Header, Sidebar, Breadcrumb
-   Button, Card, Loading, EmptyState
-   MainLayout 내 Layout / Common UI Component 분리
-   Router Path 기반 Breadcrumb

#### Changed

-   MainLayout 직접 구현 영역을 독립 Component로 분리
-   Layout Component와 Common UI Component 분리
-   `@/components/...` Alias 기반 Import 구조 적용
-   Vite Path Resolution 설정 정리

#### Documentation

-   `006_frontend_development_history.md` 현행화
-   Frontend Layout 및 Common UI Component 개발 이력 추가

------------------------------------------------------------------------

## v0.5.2 (2026-08-10)

### Project Detail API & Controller Separation

#### Added

-   `ProjectDetailController`
-   `ProjectDetailService`
-   `ProjectDetailResponse`
-   `GET /api/projects/{projectId}/detail`

#### Changed

-   Project CRUD / Dashboard / Detail Controller 책임 분리
-   Project CRUD / Dashboard / Detail Service 책임 분리
-   단건 조회와 상세 조회 책임 분리
-   상세 조회 전용 Response DTO 구성

#### Refactoring

-   Controller 및 Service 단일 책임 구조 개선
-   Project Domain API 구조 정리
-   WBS / Schedule / Issue / Risk / Change 연계 기반 마련

#### Documentation

-   Project Detail API Swagger 문서화
-   Project Domain API 구조 문서화

------------------------------------------------------------------------

## v0.5.3 (2026-08-11)

### Frontend Dashboard HMC Reference

#### Added

-   Power HMC (Hardware Management Console) 스타일을 참고한 PMIS
    Dashboard
-   Project Overview, KPI, WBS Progress, Schedule Summary, Issue
    Summary, Recent Activity 영역
-   `KpiCard`, `ProjectOverview`, `WbsProgress`, `ScheduleSummary`,
    `IssueSummary`, `RecentActivity`

#### Changed

-   Dashboard Skeleton을 운영 현황 중심의 Dashboard UI로 확장
-   DashboardPage를 Component 단위로 분리
-   Dashboard Component Import 구조 정리

#### Build

-   TypeScript Build 오류 수정
-   TypeScript 6.x `baseUrl` deprecated 대응 및 `ignoreDeprecations`
    설정
-   `npm run build` 및 Vite Production Build 성공

#### Git

-   Feature Branch: `feature/frontend-dashboard-hmc-reference`
-   Feature Branch Push, `develop` Merge 및 Push 완료

#### Note

-   Dashboard 데이터는 Reference UI 구현을 위한 정적 데이터
-   실제 Project / WBS / Schedule / Issue API 연동은 후속 작업으로 분리

------------------------------------------------------------------------

# Unreleased

> `develop` 브랜치에 통합되었으며 아직 정식 Release Tag가 생성되지 않은
> 변경 사항.

## Project Frontend CRUD

### Added

-   Project API Client
-   Project List / Search / Detail / Registration / Edit / Delete UI
-   Project CRUD API Integration
-   Loading / Empty / Error State
-   Delete Confirmation 및 API Error Handling
-   CRUD 결과 UI 반영

### Completed

-   Project List, Search, Detail, Create, Edit, Delete
-   Project API Integration
-   Loading / Empty / Error State
-   Browser Verification 및 Production Build

### Git

-   Commit: `8d7df6d` --- `feat(frontend): complete project CRUD`
-   Project Frontend Feature `develop` 통합 완료

------------------------------------------------------------------------

## WBS Management Backend & Frontend Integration

### Added

-   WBS CRUD, Tree Structure, 단건 조회 및 Validation
-   Parent / Child 관계 기반 Tree 처리
-   WBS Tree / Search / Detail / Create / Update / Delete API
-   Frontend API Client: `apiClient`, `getWbsTree`, `searchWbs`,
    `getWbs`, `createWbs`, `updateWbs`, `deleteWbs`
-   `WbsTree`, `WbsTreeNode`, `WbsDetail`, `WbsForm`, `WbsStatusForm`
-   WBS Tree / Search / Detail / Create / Edit / Status Change / Delete
    UI

### Changed

-   WBS UI를 실제 Backend API 기반으로 전환
-   생성 / 수정 / 상태 변경 / 삭제 후 최신 Tree 데이터 반영
-   삭제 성공 후 선택 상태 초기화
-   Parent WBS 관계를 Tree 기반으로 처리

### Validation & UX

-   동일 상태 변경 방지
-   하위 WBS 존재 시 삭제 제한 및 삭제 조건 확인
-   Loading / Empty / Error State
-   Delete Confirmation 및 Tree Refresh

### Verification

-   WBS CRUD / Status Change / Delete API Integration 검증
-   Browser 기반 기능 검증

### Git

-   `75e9d5b` --- `feat(frontend): implement WBS status change`
-   `d468f70` --- `feat(frontend): implement WBS delete`
-   Feature: `feature/frontend-wbs` → `develop`

------------------------------------------------------------------------

## Schedule Management Backend & Frontend Integration

### Added

-   Schedule Entity / Repository / Service / Controller / DTO /
    Validation
-   Schedule CRUD 및 Status / Sort Order 관리
-   Project / WBS / Keyword / Status / 기간 조건 검색
-   Pagination 및 Dynamic Sorting
-   Schedule Frontend API Client 및 Type 정의
-   Schedule List / Search / Detail / Create / Edit / Delete / Status UI
-   Loading / Empty / Error State

### Changed

-   Schedule UI를 실제 Backend API와 연계
-   Create / Update 이후 최신 데이터 반영, Delete 이후 목록 재조회
-   Frontend Search 조건과 Backend Query Parameter 연동

### Verification

-   Swagger / OpenAPI를 통한 CRUD, Search, Pagination, Sorting, Period
    Search, Validation 및 Not Found 검증
-   Frontend CRUD / Search / Detail / Browser UI 및 Production Build
    확인
-   Backend / Frontend Integration `develop` 통합 완료

### Git

-   Backend Feature: `feature/schedule-management`
-   Frontend Feature: `feature/frontend-schedule`

------------------------------------------------------------------------

## Common Search Filter & Pagination Standardization

### Added

-   공통 검색 조건 및 Pageable 생성 규약 문서화:
    `docs/_meta/SEARCH_FILTER_DESIGN.md`
-   `SearchPageableFactory` 공통 Factory 구현
-   Issue / Risk / Change / Schedule / WBS 검색 API에 공통 Pagination /
    Sorting 처리 적용

### Changed

-   각 Domain에 분산되어 있던 Pagination 및 Sorting 처리 로직을 공통
    Factory 기반으로 정리
-   WBS 검색 Service의 로컬 페이지 파싱 및 정렬 처리 제거
-   허용 정렬 필드와 검색 정렬 조건 검증 구조 통일

### Documentation

-   공통 Search Filter 계약 문서 추가

### Git

-   `d744bfb` --- `docs: define common search filter contract`
-   `b3450e7` --- `feat: implement common search pageable factory`
-   `b878017` --- `feat: standardize issue search pagination`
-   `64c76ca` --- `feat: standardize risk search pagination`
-   `04cf172` --- `feat: standardize schedule search pagination`
-   `7c5328d` --- `feat: standardize change search pagination`
-   `1f7f011` --- `feat: standardize WBS search pagination`
-   Feature: `feature/common-search-filter` → `develop` 병합 및 Push
    완료

------------------------------------------------------------------------

## Issue Key Generation Hotfix

### In Progress --- Not Yet Included in Release History

Issue Key는 프로젝트 단위로 생성하고, Backend가 자동 생성한 값을
Frontend가 표시하는 정책으로 진행한다. 아래 항목은 Hotfix가 실제
`develop`에 통합되기 전까지 완료된 Unreleased 변경 사항으로 취급하지
않는다.

#### Intended Change

-   프로젝트별 Issue Key Sequence 생성
-   동일 프로젝트 내 Issue Key 중복 방지
-   Issue 생성 시 Backend 자동 생성
-   Issue Key Format: `ISSUE-001`, `ISSUE-002`, ...
-   Database Unique Constraint: `UNIQUE(project_id, issue_key)`
-   Issue Key는 생성 후 수정 대상에서 제외

#### Working Branch

-   `hotfix/issue-key-generation`

#### Planned Commit Message

-   `fix: add project-based issue key generation`

------------------------------------------------------------------------

# Current Development Status

## Backend

### Completed

-   Foundation: Spring Boot, Gradle Kotlin DSL, MariaDB, Common
    Infrastructure, JPA Auditing
-   Security: Spring Security, JWT Authentication, Login, Refresh Token,
    Role Hierarchy / Authorization
-   Documentation: Swagger / OpenAPI
-   Project: CRUD, Search, Sorting, Dashboard API / KPI, Detail API,
    Controller / Service 책임 분리
-   WBS: CRUD, Tree Structure, Validation, Delete Handling
-   Schedule: CRUD, Search, Pagination, Dynamic Sorting, Period Search,
    Status, Sort Order, Validation, Swagger Verification
-   Issue: Domain Backend 및 CRUD
-   Common Search: Issue / Risk / Change / Schedule / WBS Pagination /
    Sorting 공통화

### Next

-   Issue Key Hotfix 검증 및 `develop` 통합
-   Risk Management
-   Change Management
-   Evidence Management
-   CMDB Management
-   Dashboard Integration / Reporting
-   Spring AI

## Frontend

### Completed

-   React / Vite / TypeScript / Router / Feature-based Structure / Path
    Alias
-   MainLayout, Header, Sidebar, Breadcrumb
-   Button, Card, Loading, EmptyState
-   Dashboard Reference UI 및 Component 분리
-   Project CRUD 및 API Integration
-   WBS Tree / Detail / Search / Form / Status / Delete 및 API
    Integration
-   Schedule API Client / List / Search / Detail / CRUD / Status 및 API
    Integration

### In Progress

-   Issue Management UI 및 API Integration
-   Issue Key 표시 연동

### Next

-   Issue Browser / E2E Verification
-   Calendar / Timeline / Milestone / Gantt
-   WBS / Schedule Relationship Visualization
-   Dashboard 실제 API / 데이터 연동
-   Authentication UI
-   Risk / Change / Evidence / CMDB / Report / AI Assistant UI

## Integration

### Completed

-   Project API Integration 및 CRUD / Detail / Delete Verification
-   WBS API Integration, CRUD / Status / Delete Integration 및 Browser
    Verification
-   Schedule Backend API Verification, Frontend API Client / Integration
    / UI Verification
-   Common Search Filter / Pagination 변경의 `develop` 통합

### In Progress / Next

-   Issue Key Hotfix 및 Issue API Contract Integration
-   Issue Browser / E2E Verification
-   Calendar / Gantt / Dashboard Integration
-   Risk / Change E2E Verification
-   Full E2E Verification

------------------------------------------------------------------------

# Current Development Snapshot

``` text
Backend
├─ Common Infrastructure / SearchPageableFactory     ✓
├─ Security / JWT / Auth                              ✓
├─ Project CRUD / Search / Dashboard / Detail        ✓
├─ WBS Management                                    ✓
├─ Schedule CRUD / Search / Pagination / Sorting     ✓
├─ Issue Domain / CRUD                                ✓
├─ Issue Key Hotfix                                   →
├─ Risk / Change / Evidence / CMDB                    →
├─ Dashboard Integration / Report                     →
└─ Spring AI                                          →

Frontend
├─ React / Vite / TypeScript / Layout / Common UI     ✓
├─ Dashboard Reference UI                             ✓
├─ Project CRUD / API Integration                     ✓
├─ WBS UI / API Integration                           ✓
├─ Schedule UI / API Integration                      ✓
├─ Issue UI / API Integration                         →
├─ Calendar / Gantt / WBS-Schedule Visualization      →
└─ Risk / Change / Evidence / CMDB / Report / AI UI    →

Integration
├─ Project / WBS / Schedule                            ✓
├─ Common Search Pagination Standardization           ✓
├─ Issue API / Browser / E2E                            →
└─ Full E2E                                            →
```

`✓` 완료, `→` 진행 예정 또는 진행 중. 세부 완료 여부는 해당 Feature의
검증 결과와 `DEVELOPMENT_ROADMAP.md`를 기준으로 갱신한다.

------------------------------------------------------------------------

# Current Sprint & Priority

## Sprint 3 --- WBS / Schedule / Frontend API Integration

WBS / Schedule의 Backend와 Frontend API Integration 및 주요 Browser
Verification은 완료되었다. Calendar / Gantt 및 WBS-Schedule 관계
시각화는 Schedule의 후속 시각화 범위로 관리한다.

## Current Priority

1.  Issue Key Hotfix 완료 및 `develop` 통합
2.  Issue Management Frontend
3.  Issue API Integration
4.  Issue Browser / E2E Verification
5.  Calendar UI 및 API Integration
6.  Timeline / Milestone / Gantt UI
7.  WBS / Schedule Relationship Visualization
8.  Project Dashboard API / Data Integration
9.  Sprint 3 E2E Verification
10. Risk Management
11. Change Management
12. Evidence Management

------------------------------------------------------------------------

# Planned Releases

## v0.6.x --- WBS & Schedule Management Integration

### Completed

-   WBS CRUD / Tree / Frontend Integration / Status Change / Delete /
    Browser Verification
-   Schedule CRUD / Search / Pagination / Dynamic Sorting / Period
    Search / Validation / Swagger Verification
-   Schedule Frontend API Client / API Integration / UI / Browser
    Verification

### Planned

-   Calendar, Timeline, Milestone, Gantt
-   WBS / Schedule Relationship
-   Progress Visualization

## v0.7.x --- Issue / Risk / Change Management

### Planned

-   Issue: CRUD, Project-scoped Issue Key, Assignment, Status Workflow,
    Priority, Attachments
-   Risk: CRUD, Assessment, Probability / Impact, Risk Matrix, Response
    Strategy, Monitoring
-   Change: Change Request, Approval, History, Impact Analysis

## v0.8.x --- Evidence & Inspection Management

### Planned

-   Evidence Requirement / Checklist / Registration / Status
-   Missing Evidence Detection
-   Reviewer / Verification Status / Approval / Rejection / Verification
    History
-   Inspection Status / Result / Evidence Inspection / Approval History
-   Evidence Dashboard: Count, Missing Evidence, Pending Verification,
    Completion Rate

## v0.9.x --- CMDB

### Planned

-   Configuration Items: Server CI, Database CI, Software CI
-   CI / Dependency Relationships
-   Version Management, Configuration History, Change Tracking

## v0.10.x --- Dashboard & Reporting

### Planned

-   Executive / Project / WBS / Schedule / Issue / Risk / Change /
    Evidence / CMDB Dashboard
-   Weekly / Monthly / Executive / Evidence Status Reports
-   Excel / PDF Export 및 Statistics API

> Dashboard Reference UI는 v0.5.3에서 선행 구현되었다. v0.10.x에서는
> 실제 Backend API와 각 Domain 데이터를 통합한다.

## v1.0.0 --- AI Powered PMIS

### Planned

-   Spring AI 기반 PM Assistant
-   Project Summary, Schedule Analysis, Issue Summary, Risk Analysis
-   Change Impact / Evidence Gap Analysis
-   Meeting Summary 및 Report Generation
-   OpenAI / Azure OpenAI / Ollama 연동 검토
-   PMIS Document Search, Semantic Search, RAG 기반 질의응답

------------------------------------------------------------------------

# Git Flow

``` text
main
│
├─ release/*
│
└─ develop
   ├─ feature/*
   └─ hotfix/*
```

-   `main`: Production Release
-   `develop`: Integration Branch
-   `feature/*`: Feature Development
-   `release/*`: Release Preparation
-   `hotfix/*`: 긴급 수정

기본 흐름:

``` text
Feature Development
        ↓
Local Test
        ↓
Build / Documentation
        ↓
Commit / Push
        ↓
Pull Request
        ↓
Merge to develop
        ↓
Integration Test
        ↓
Release
```

Hotfix는 별도 브랜치에서 수정·검증하며, 실제 `develop` 통합 전까지
Release History에 완료 사항으로 기록하지 않는다.

------------------------------------------------------------------------

# Commit Convention

  Prefix       Description
  ------------ ---------------
  `feat`       Feature
  `fix`        Bug Fix
  `refactor`   Refactoring
  `docs`       Documentation
  `style`      Code Style
  `test`       Test
  `build`      Build
  `chore`      Maintenance
  `perf`       Performance
  `security`   Security

Examples:

``` text
feat: implement JWT authentication
feat: implement project CRUD
feat: implement WBS CRUD
feat: implement schedule management API
feat(frontend): complete project CRUD
feat(frontend): implement WBS API integration
fix: resolve JWT validation issue
fix: add project-based issue key generation
docs: update development roadmap
refactor: improve exception handling
```

------------------------------------------------------------------------

# Development Process

``` text
Planning
   ↓
Requirement
   ↓
Domain / Data Model Design
   ↓
Feature Branch
   ↓
Backend API Design & Implementation
   ↓
API Verification
   ↓
Frontend API Client / Type Definition
   ↓
Frontend Implementation
   ↓
API Integration
   ↓
Browser / E2E Verification
   ↓
Production Build
   ↓
Documentation Update
   ↓
Commit / Push / Pull Request
   ↓
Merge to develop
   ↓
Integration Test
   ↓
Release
```

## Frontend API Integration Process

``` text
Backend API
   ↓
Swagger / API Verification
   ↓
Frontend API Client
   ↓
Type Definition
   ↓
Feature Component / Page UI
   ↓
API Integration
   ↓
Loading / Empty / Error State
   ↓
Browser Verification
   ↓
Production Build
   ↓
Documentation
   ↓
Commit / Push / Merge
   ↓
Integration Test / E2E Verification
```

------------------------------------------------------------------------

# Issue Development Flow

``` text
Issue Backend
├─ CRUD                         ✓
├─ Validation                   ✓
├─ Status Workflow              ✓
├─ Priority                     ✓
├─ Assignment                   ✓
└─ Project-scoped Issue Key     →

        ↓

Issue Frontend
├─ API Client                   →
├─ List / Search / Detail       →
├─ Create / Edit                →
└─ Issue Key Display            →

        ↓

Integration
├─ API Verification             →
├─ Browser Verification         →
└─ E2E Verification             →
```

Issue Key는 Backend가 생성하고 Frontend는 표시하는 구조로 관리한다.

------------------------------------------------------------------------

# Schedule Development Flow

``` text
Schedule Backend
├─ CRUD                         ✓
├─ Search                       ✓
├─ Pagination / Sorting         ✓
├─ Period Search                ✓
└─ Swagger Verification         ✓

        ↓

Schedule Frontend
├─ API Client                   ✓
├─ List / Search / Detail       ✓
├─ Create / Edit / Delete       ✓
├─ Status                       ✓
└─ API Integration              ✓

        ↓

Visualization
├─ Calendar                     →
├─ Timeline / Milestone         →
├─ Gantt                        →
└─ WBS / Schedule Relationship  →
```

------------------------------------------------------------------------

# Documentation Policy

다음 문서는 소스코드 변경 시 관련 내용을 함께 검토하고 최신 상태로
유지한다.

-   `CHANGELOG.md`
-   `DEVELOPMENT_ROADMAP.md`
-   `PROJECT_OVERVIEW.md`
-   `ARCHITECTURE.md`
-   `PORTFOLIO.md`

주요 Design 문서: - `docs/design/project/PROJECT_DESIGN.md` -
`docs/design/project/PROJECT_DASHBOARD_DESIGN.md` -
`docs/design/wbs/WBS_DESIGN.md` -
`docs/design/schedule/SCHEDULE_DESIGN.md` -
`docs/design/issue/ISSUE_DESIGN.md` -
`docs/design/report/REPORT_DESIGN.md` -
`docs/design/server/SERVER_CONFIGURATION_DESIGN.md` -
`docs/design/evidence/EVIDENCE_DESIGN.md`

Frontend 문서: - `001_frontend_architecture.md` -
`002_frontend_folder_structure.md` -
`003_frontend_coding_convention.md` - `004_frontend_ui_design.md` -
`005_frontend_component_architecture.md` -
`006_frontend_development_history.md`

API Integration 변경 시 Source Code, API Contract, Design Document,
Frontend Architecture / Component Architecture, Development History,
CHANGELOG 및 Roadmap을 함께 검토한다.

------------------------------------------------------------------------

# Maintenance Policy

-   모든 기능은 Feature Branch에서 개발한다.
-   주요 변경 사항은 `develop` 통합 후 CHANGELOG에 기록한다.
-   Backend / Frontend / Integration / Verification 상태를 독립적으로
    관리한다.
-   Domain 완료는 Backend 구현만으로 판단하지 않고 Frontend,
    Integration, Verification까지 고려한다.
-   API Integration 기능은 API Verification과 Browser Verification을
    수행한다.
-   Feature Merge 후 `develop` Integration Test를 수행한다.
-   UI Reference 구현과 실제 API Integration 단계를 구분한다.
-   Release마다 Git Tag를 생성하고 Semantic Versioning을 준수한다.
-   `main`에는 검증된 코드만 Release한다.

------------------------------------------------------------------------

# Current Information

  -----------------------------------------------------------------------
  Item                                Value
  ----------------------------------- -----------------------------------
  Current Branch                      `hotfix/issue-key-generation`
                                      (사용자 제공 상태; 최신 로컬 상태
                                      확인 필요)

  Current Sprint                      Sprint 3 --- WBS / Schedule /
                                      Frontend API Integration

  Project Frontend                    CRUD / API Integration Complete

  WBS Backend / Frontend              CRUD / Tree / Integration Complete

  Schedule Backend / Frontend         CRUD / Search / UI / API
                                      Integration Complete

  Common Search Pagination            Issue / Risk / Change / Schedule /
                                      WBS 공통화, `develop` 통합 완료

  Issue Backend                       Domain / CRUD Complete; Issue Key
                                      Hotfix 진행 중

  Issue Frontend                      In Progress ---
                                      `feature/issue-management-ui`

  Schedule Visualization              Calendar / Gantt Planned

  Integration Branch                  `develop`

  Next Development Stage              Issue Key Hotfix 완료 및 Issue
                                      Frontend / API Integration

  Next Sprint                         Sprint 4 --- Issue / Risk / Change

  Evidence Domain                     Sprint 5 Planned

  Maintainer                          Seo Seokhyeon
  -----------------------------------------------------------------------

> Current Branch 값은 제공된 변경 문서의 시점 정보이며, 최신 Git 작업
> 트리에서 확인해 갱신한다.

------------------------------------------------------------------------

# Next Milestone --- Issue Management Completion

## Backend

``` text
Issue CRUD                         ✓
Issue Validation                  ✓
Issue Status Workflow             ✓
Issue Priority                    ✓
Issue Assignment                  ✓
Project-scoped Issue Key          →
```

## Frontend

``` text
Issue API Client                  →
Issue List / Search / Detail      →
Issue Create / Edit               →
Issue Key Display                 →
Browser Verification              →
Production Build                  →
```

## Integration

``` text
Issue API Verification            →
Issue API Integration             →
Issue Browser Verification        →
Issue E2E Verification            →
```

------------------------------------------------------------------------

# Release Summary

  Version        Description
  -------------- ---------------------------------------------------
  v0.1.0         Project Initialization
  v0.2.0         Common Infrastructure
  v0.3.0         Security Foundation
  v0.3.1         Authentication & Authorization
  v0.3.2         Swagger / OpenAPI
  v0.4.0         Project Management Backend
  v0.5.0         Frontend Foundation
  v0.5.1         Frontend Layout & Common Components
  v0.5.2         Project Detail API & Controller Separation
  v0.5.3         Frontend Dashboard HMC Reference
  Unreleased     Project Frontend CRUD
  Unreleased     WBS Management & Frontend Integration
  Unreleased     Schedule Backend & Frontend Integration
  Unreleased     Common Search Filter / Pagination Standardization
  In Progress    Issue Key Hotfix
  Next Release   WBS / Schedule / Issue Management Integration
  v0.7.x         Issue / Risk / Change Management
  v0.8.x         Evidence & Inspection
  v0.9.x         CMDB
  v0.10.x        Dashboard & Reporting
  v1.0.0         AI Powered PMIS

------------------------------------------------------------------------

# End of Document
