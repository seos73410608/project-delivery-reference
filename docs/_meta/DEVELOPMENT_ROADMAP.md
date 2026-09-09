# PMIS Development Roadmap

> **Version:** **1.9**
>
> **Last Updated:** **2026-09-09**
>
> **Project:** **Project Management Information System (PMIS)**

---

# 1. Development Goal

본 프로젝트는 단순 CRUD 프로젝트가 아니라 실제 SI 프로젝트에서 사용하는 **Project Management Information System (PMIS)** 을 구현하는 것을 목표로 한다.

구현 대상:

- 사용자 / 권한 관리
- 프로젝트 관리
- WBS 관리
- 일정 관리
- 진척 관리
- 이슈 관리
- 리스크 관리
- 변경 관리
- 구축 관리
- 검수 관리
- 증적 관리
- CMDB
- Dashboard
- Report
- 운영 인수인계
- Spring AI Assistant

Backend API와 Frontend UI를 연계하여 실제 프로젝트 관리 업무를 지원할 수 있는 Full Stack PMIS를 구현한다.

특히 SI 프로젝트에서 중요한 **산출물 및 증적의 존재 여부, 검수 상태, 누락 여부를 시스템에서 추적할 수 있는 증적 관리 체계**를 구축한다.

---

# 2. System Development Strategy

## Full Stack Development Flow

```text
Requirement
    ↓
Domain / Data Model Design
    ↓
Backend API Design
    ↓
Backend Implementation
    ↓
API Verification
    ↓
Frontend Implementation
    ↓
API Integration
    ↓
UI Verification
    ↓
E2E Verification
    ↓
Documentation
    ↓
Commit
    ↓
Push
    ↓
Pull Request / Merge Review
    ↓
Merge to develop
    ↓
Integration Test
```

## Evidence Management Development Flow

```text
Project
   ↓
WBS / Task
   ↓
Activity / Deliverable
   ↓
Evidence Requirement
   ↓
Evidence Registration
   ↓
Evidence Verification
   ↓
Evidence Status
   ├── PRESENT
   ├── MISSING
   ├── PENDING
   └── NOT_REQUIRED
   ↓
Inspection / Approval
   ↓
Report / Dashboard
```

증적은 단순 파일 첨부 기능이 아니라 **무엇에 대한 증적이며, 반드시 필요한 증적인지, 실제 증적이 존재하는지, 검토가 완료되었는지**를 추적할 수 있어야 한다.

## Evidence Checklist

| 항목 | 설명 |
|---|---|
| Evidence Required | 증적 필수 여부 |
| Evidence Type | 증적 유형 |
| Evidence | 실제 증적 파일 / 문서 |
| Evidence Status | 존재 / 누락 / 검토중 / 불필요 |
| Verification Status | 검토 상태 |
| Reviewer | 검토자 |
| Verified Date | 검토일 |
| Remark | 비고 |
| Due Date | 증적 제출 기한 |

---

# 3. Architecture

```text
┌─────────────────────────────────────────────┐
│                PMIS Frontend                │
│ React + TypeScript + Vite                   │
│ React Router DOM                            │
│ Layout / Components / Pages / Features      │
│ Project / WBS / Schedule / Issue / Risk     │
│ Change / Evidence / CMDB / Report           │
└──────────────────────┬──────────────────────┘
                       │ REST API
                       ▼
┌─────────────────────────────────────────────┐
│                 PMIS Backend                │
│ Spring Boot + Spring Security + JWT         │
│ Controller / Service / Repository           │
│ DTO / Mapper / Specification                │
│ Evidence / File / Verification              │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────┐
│                   MariaDB                   │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
              File / Object Storage
                    Planned
                       │
                       ▼
┌─────────────────────────────────────────────┐
│                  Spring AI                  │
│ AI Assistant / Analysis / RAG               │
└─────────────────────────────────────────────┘
```

## Backend

- Spring Boot 4.1.0
- Spring Security
- JWT
- MariaDB
- Controller / Service / Repository
- DTO / Mapper / Specification
- Global Exception Handling
- Swagger / OpenAPI

## Frontend

- React
- Vite
- TypeScript
- React Router DOM
- Feature-based Directory Structure
- Layout Architecture
- Reusable Common Components
- API / Service Separation
- Component-based Feature Composition

---

# 4. Git Flow

## Development Flow

```text
main
 ▲
 │
release
 ▲
 │
develop
 ▲
 │
feature/*
```

모든 기능은 Feature Branch에서 개발 후 `develop`으로 Merge한다. 검증 완료 후 `main`으로 Release 한다.

## Backend Feature Branch

```text
feature/common
feature/security
feature/auth-jwt
feature/auth-login
feature/auth-refresh
feature/auth-role
feature/swagger
feature/project-crud
feature/project-search
feature/project-dashboard
feature/project-detail
feature/wbs-management
feature/schedule-management
feature/issue-management
feature/risk-management
feature/change-management
feature/evidence-management
feature/cmdb-management
feature/dashboard
feature/report-excel
feature/report-pdf
feature/spring-ai
feature/ai-risk-analysis
feature/ai-report-summary
```

## Frontend Feature Branch

```text
feature/frontend-layout
feature/frontend-components
feature/frontend-dashboard-hmc-reference
feature/frontend-api-integration
feature/frontend-project
feature/frontend-wbs
feature/frontend-schedule
feature/frontend-issue
feature/frontend-risk
feature/frontend-change
feature/frontend-evidence
feature/frontend-cmdb
feature/frontend-report
feature/frontend-auth
feature/frontend-ai
```

## Recent Refactoring Branch

```text
refactor/schedule-calendar-components
```

최근 Schedule Calendar를 Header / Grid / Day / Event Component로 분리하고 Calendar Utility를 정리하였다.

```text
refactor/schedule-calendar-components
        ↓
develop
```

---

# 5. Version Strategy

PMIS는 Semantic Versioning(SemVer)을 따른다.

```text
MAJOR.MINOR.PATCH
```

- **Major**: 호환되지 않는 변경
- **Minor**: 새로운 기능 추가
- **Patch**: 버그 수정 및 개선

현재 마지막 정식 Release는 **v0.5.3**이다.

현재 `develop`에는 다음 변경 사항이 추가되어 있다.

- Project Frontend CRUD
- WBS Management
- Schedule Backend Management
- Schedule Swagger API Verification
- Schedule Frontend Management UI
- Schedule Calendar API Integration
- Schedule Calendar Component Refactoring

---

# 6. Commit Convention

```text
feat:
fix:
refactor:
style:
test:
docs:
build:
chore:
perf:
security:
```

예시:

```text
feat: implement JWT authentication
feat: implement project CRUD
feat: implement WBS CRUD
feat: implement schedule CRUD
feat(frontend): implement schedule management UI
feat(frontend): integrate schedule calendar API
refactor(frontend): split schedule calendar into components
docs: update PMIS development roadmap
```

기능 구현과 문서 현행화는 가능한 경우 별도의 Commit으로 관리한다.

---

# 7. Sprint Plan

# Sprint 0 - Base Project

## Goal

프로젝트 기반 구축

### Backend

- Spring Boot
- Gradle Kotlin DSL
- MariaDB
- Package Structure
- Base Entity
- Global Response
- Exception
- JPA
- Build Configuration

### Frontend

- React / Vite / TypeScript 선정
- Backend / Frontend 프로젝트 분리
- Frontend Directory Architecture 설계

### Deliverables

- Base Project
- Backend Build Success
- Frontend Project Structure
- Git Repository

---

# Sprint 1 - Authentication & Security

## Goal

JWT 기반 인증 / 인가 구축

### Backend Tasks

- JWT Provider
- JWT Filter
- SecurityConfig
- UserPrincipal
- Login
- Refresh Token
- Role Authorization
- Password Encoder

### Frontend Planned Tasks

- Login Page
- Login Form
- Authentication State
- Token Handling
- Protected Route
- Logout

---

# Sprint 2 - Project Management & Frontend Foundation

## Goal

Project Management Backend을 구축하고 PMIS Frontend의 기본 화면 구조를 완성한다.

## Backend Completed

- Project CRUD
- Customer / PM / Status / Priority
- Validation
- Project Search
- Sorting
- Project Dashboard API
- Total Project Count
- Status Count
- Priority Count
- Recent Project KPI
- Upcoming Deadline KPI
- Project Detail API
- Project Detail Response
- Project Detail Service
- Project Detail Controller
- Project Detail Mapper
- Project Detail API 분리

### Controller Architecture

```text
project/controller
├── ProjectController
├── ProjectDashboardController
└── ProjectDetailController
```

## Frontend Foundation

- React
- Vite
- TypeScript
- npm
- React Router DOM
- Feature-based Directory Structure
- API / Service / Store 구조 준비
- AppRouter
- MainLayout
- Header
- Sidebar
- Breadcrumb
- Dashboard Page
- Button
- Card
- Loading
- EmptyState

## Project Frontend

- Project List
- Project Search
- Project Detail
- Project Registration
- Project Edit
- Project Delete
- API Integration
- Loading / Empty / Error State
- Delete Confirmation

---

# Sprint 3 - WBS / Schedule Management

## Goal

프로젝트 작업 구조와 일정을 관리하는 핵심 기능을 Backend와 Frontend에서 구현하고 실제 REST API Integration 기반의 업무 화면을 구축한다.

## WBS Backend

### Completed

- WBS CRUD
- Tree Structure
- Progress Calculation
- Task Relationship
- Validation
- Delete Handling
- Swagger API Verification

## WBS Frontend

### Completed

- WBS API Client
- WBS API Integration
- WBS Tree UI
- WBS Detail UI
- WBS Form UI
- WBS Status Form UI
- Progress Display
- WBS Delete

### Remaining

- WBS Browser Verification
- WBS Integration Test

---

## Schedule Backend

### Completed

- Schedule CRUD
- Project별 Schedule 조회
- Schedule 단건 조회
- Schedule 생성
- Schedule 수정
- Schedule 삭제
- Schedule Status 변경
- Sort Order 관리
- Schedule Search
- Project 조건 검색
- WBS 조건 검색
- Keyword 검색
- Status 검색
- Dynamic Sorting
- Pagination
- Period Search
- Validation
- Calendar 조회 API
- Swagger / OpenAPI 검증

### Schedule API Verification

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
Schedule Status
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

Not Found 및 기본 Validation 시나리오도 검증 대상으로 관리한다.

---

## Schedule Frontend

### Completed

- Schedule Management UI
- Schedule List UI
- Schedule Search UI
- Schedule Detail UI
- Schedule Registration UI
- Schedule Edit UI
- Schedule Delete UI
- Schedule Status Management UI
- Schedule Management Styling
- Schedule API Client
- Schedule API Integration
- Schedule Calendar UI
- Schedule Calendar API Integration
- Calendar Range Query
- Previous / Next Month Navigation
- Today Navigation
- Loading State
- Error State
- Calendar Schedule Selection
- Calendar Component Refactoring
- Calendar Utility Separation
- Calendar Production Build Verification

### Schedule Calendar Component Architecture

```text
ScheduleCalendar
│
├── CalendarHeader
│   ├── Previous Month
│   ├── Today
│   └── Next Month
│
└── CalendarGrid
    │
    └── CalendarDay
        │
        └── CalendarEvent
```

### Calendar Utility

```text
calendarUtils.ts

├── formatDate()
├── parseDate()
├── isSameDate()
├── isDateInRange()
└── createCalendarDays()
```

Calendar Grid 날짜 생성 로직을 Component에서 Utility로 분리하여 날짜 관련 공통 로직을 재사용할 수 있도록 정리하였다.

### Calendar API

```text
GET
/api/projects/{projectId}/schedules/calendar
```

Query:

```text
startDate
endDate
```

### Current / Next

- Schedule Browser Verification
- Gantt UI
- WBS / Schedule 연계
- Timeline Visualization
- Milestone Visualization
- Progress Visualization

---

# Sprint 4 - Issue / Risk / Change Management

## Goal

프로젝트의 이슈, 리스크, 변경 요청을 통합 관리한다.

### Backend Feature Branch

```text
feature/issue-management
feature/risk-management
feature/change-management
```

### Frontend Feature Branch

```text
feature/frontend-issue
feature/frontend-risk
feature/frontend-change
```

### Issue

- Issue CRUD
- Assignment
- Status Workflow
- Priority
- Attachment

### Risk

- Risk CRUD
- Risk Assessment
- Probability / Impact
- Response Strategy
- Risk Monitoring

### Change

- Change Request
- Approval
- History
- Impact Analysis

---

# Sprint 5 - Evidence & Inspection Management

## Goal

프로젝트 수행 과정에서 발생하는 산출물과 증적을 체계적으로 관리하고 증적의 존재 여부 및 검수 상태를 확인한다.

### Backend

```text
feature/evidence-management
```

### Frontend

```text
feature/frontend-evidence
```

## Evidence Status

```text
Evidence Item
      ↓
Required?
┌────┴────┐
YES       NO
│          └── NOT_REQUIRED
▼
Evidence Exists?
┌────┴────┐
YES       NO
│          └── MISSING
▼
PRESENT
│
▼
Verification
├── PENDING
├── VERIFIED
└── REJECTED
```

## Evidence Dashboard

- 전체 증적 대상 수
- 증적 확보 수
- 증적 누락 수
- 검토 대기 수
- 검토 완료 수
- 증적 확보율
- 증적 검수 완료율
- 기한 초과 증적
- Critical / Mandatory 증적 누락

---

# Sprint 6 - CMDB

## Goal

프로젝트 인프라와 구성 정보를 관리하는 CMDB를 구현한다.

### Tasks

- Server CI
- Software CI
- Database CI
- Relationship
- Version History
- Change Tracking
- CI List / Detail / Registration
- Relationship View
- Configuration History
- Change History

---

# Sprint 7 - Dashboard & Report

## Goal

PMO 업무에 필요한 통합 Dashboard와 Report 기능을 구현한다.

### Dashboard

- Project Dashboard
- WBS Dashboard
- Schedule Dashboard
- Issue Dashboard
- Risk Dashboard
- Change Dashboard
- Evidence Dashboard
- CMDB Dashboard

### Report

- Weekly Report
- Monthly Report
- Executive Report
- Evidence Status Report
- Evidence Missing Report
- Excel Export
- PDF Export
- Statistics API

> HMC Reference 기반 Dashboard UI는 Sprint 2에서 선행 구현되었다. Sprint 7에서는 실제 PMIS API 기반 통합 Dashboard로 확장한다.

---

# Sprint 8 - Spring AI

## Goal

PMIS 데이터를 활용하는 AI Assistant를 구현한다.

### Tasks

- OpenAI Integration
- Prompt Management
- AI Chat API
- Schedule Analysis
- Risk Analysis
- Project Analysis
- Evidence Gap Analysis
- Report Summary
- Meeting Summary
- Action Item Generation
- Change Impact Analysis

---

# Sprint 9 - Finalization

## Backend

- Refactoring
- Unit Test
- Integration Test
- Swagger
- Docker
- Docker Compose
- GitHub Actions
- Security Hardening
- Audit Log
- Evidence Integrity Validation

## Frontend

- Component Refactoring
- Responsive UI
- Frontend Test
- Error Handling
- Loading / Empty / Error State
- Accessibility
- Build Optimization

## Common

- API Integration Test
- E2E Test
- Evidence E2E Test
- README
- Architecture Documentation
- Portfolio Documentation

---

# 8. Milestones

| Milestone | Description |
|---|---|
| M1 | Base Project Complete |
| M2 | Authentication Complete |
| M3 | Project Management Backend Complete |
| M4 | Frontend Foundation Complete |
| M5 | Project Frontend CRUD Complete |
| M6 | WBS & Schedule Backend Complete |
| M7 | WBS & Schedule Frontend Complete |
| M8 | Issue / Risk / Change Complete |
| M9 | Evidence & Inspection Complete |
| M10 | CMDB Complete |
| M11 | Dashboard & Report Complete |
| M12 | Spring AI Complete |
| M13 | Production Ready |
| M14 | Portfolio Release |

---

# 9. Estimated Schedule

| Sprint | Duration |
|---|---:|
| Sprint 0 | 1 Week |
| Sprint 1 | 2 Weeks |
| Sprint 2 | 2 Weeks |
| Sprint 3 | 2 Weeks |
| Sprint 4 | 2 Weeks |
| Sprint 5 | 2 Weeks |
| Sprint 6 | 2 Weeks |
| Sprint 7 | 2 Weeks |
| Sprint 8 | 2 Weeks |
| Sprint 9 | 1 Week |

**Estimated Total: 약 18 ~ 20주**

---

# 10. Quality Objectives

## Backend Architecture

- Layered Architecture
- Clean Architecture
- SOLID Principles
- RESTful API
- DTO Pattern
- Mapper Pattern
- Specification Pattern
- Feature-based Controller Separation
- Service Layer Separation
- Audit / History Management
- Evidence Traceability

## Frontend Architecture

- Component-based Architecture
- Feature-based Directory Structure
- Layout Architecture
- Reusable Common Components
- API / Service Separation
- State Management
- TypeScript Type Safety
- Responsive UI
- Feature Component Composition
- Shared Utility Separation

## Integration

- REST API Contract
- API Error Handling
- Authentication Integration
- Loading State
- Empty State
- Error State
- Browser Verification
- Production Build Verification
- E2E Verification

---

# 11. Documentation

## Project Documentation

- PROJECT_OVERVIEW.md
- DEVELOPMENT_ROADMAP.md
- ARCHITECTURE.md
- CHANGELOG.md
- PORTFOLIO.md

## Design Documentation

```text
docs/design/

├── issue/
│   └── ISSUE_DESIGN.md
│
├── project/
│   ├── PROJECT_DESIGN.md
│   └── PROJECT_DASHBOARD_DESIGN.md
│
├── report/
│   └── REPORT_DESIGN.md
│
├── schedule/
│   └── SCHEDULE_DESIGN.md
│
├── server/
│   └── SERVER_CONFIGURATION_DESIGN.md
│
├── wbs/
│   └── WBS_DESIGN.md
│
└── evidence/
    └── EVIDENCE_DESIGN.md
```

## Frontend Documentation

- 001_frontend_architecture.md
- 002_frontend_folder_structure.md
- 003_frontend_coding_convention.md
- 004_frontend_ui_design.md
- 005_frontend_component_architecture.md
- 006_frontend_development_history.md

문서와 소스코드는 항상 동일한 상태를 유지한다.

---

# 12. Final Deliverables

## Backend

- PMIS Backend
- Spring Boot REST API
- JWT Authentication
- MariaDB Schema
- Project Management
- Project Search
- Project Dashboard
- Project Detail
- WBS Management
- Schedule Management
- Issue Management
- Risk Management
- Change Management
- Evidence Management
- Evidence Verification
- Inspection Management
- CMDB
- Dashboard API
- Report System
- Spring AI Assistant
- Swagger API

## Frontend

- PMIS Frontend
- React
- TypeScript
- Vite
- React Router
- Application Layout
- Dashboard
- Project Management UI
- WBS UI
- Schedule UI
- Calendar UI
- Gantt UI
- Issue UI
- Risk UI
- Change UI
- Evidence Dashboard UI
- CMDB UI
- Report UI
- AI Assistant UI

## Infrastructure / Delivery

- Docker
- Docker Compose
- GitHub Actions
- GitHub Repository
- Portfolio Documentation

---

# 13. Success Criteria

- 실제 SI 프로젝트 수준의 PMIS 구현
- Spring Boot 기반 엔터프라이즈 Backend 구현
- React 기반 PMIS Frontend 구현
- Backend / Frontend REST API Integration
- JWT 기반 인증 / 인가 구현
- 프로젝트 수행 데이터 통합
- 산출물 및 증적 추적 관리
- 증적 누락 자동 식별
- 증적 검수 및 승인 이력 관리
- Spring AI 기반 AI Assistant 제공
- Docker 기반 배포 환경 구축
- Git Flow 기반 개발 프로세스 적용
- 운영 가능한 Full Stack PMIS 완성
- 포트폴리오 및 기술 시연 가능한 수준의 결과물 확보

---

# 14. Current Project Status

## Backend

### Completed

- Spring Boot
- Gradle Kotlin DSL
- Common Infrastructure
- Security
- JWT Authentication
- Login
- Refresh Token
- Role Hierarchy
- Swagger / OpenAPI

### Project

- Project CRUD
- Project Search
- Project Dashboard API
- Project Dashboard KPI
- Project Detail API
- Project Controller Separation
- Project Dashboard Controller Separation
- Project Detail Controller Separation

### WBS

- WBS CRUD
- WBS Validation
- WBS Delete Handling
- WBS Tree Structure

### Schedule

- Schedule CRUD
- Schedule Search
- Schedule Pagination
- Schedule Sorting
- Schedule Period Search
- Schedule Validation
- Schedule Calendar API
- Schedule Swagger API Verification

### Planned

- Issue
- Risk
- Change
- Evidence
- Inspection
- CMDB
- Dashboard Integration
- Report
- Spring AI

---

## Frontend

### Completed

#### Foundation

- React
- Vite
- TypeScript
- React Router DOM
- Feature-based Directory Structure
- Main Layout
- Header
- Sidebar
- Breadcrumb
- Dashboard Skeleton
- Button
- Card
- Loading
- EmptyState

#### Dashboard

- HMC Reference 기반 Dashboard UI
- Dashboard Component 분리
- Dashboard Production Build Verification

#### Project

- Project API Client
- Project List UI
- Project Search UI
- Project Detail UI
- Project Registration UI
- Project Edit UI
- Project Delete UI
- Project CRUD API Integration
- Project Loading / Empty / Error State

#### WBS

- WBS API Client
- WBS API Integration
- WBS Tree UI
- WBS Detail UI
- WBS Form UI
- WBS Status Form UI
- WBS Delete

#### Schedule

- Schedule Management UI
- Schedule List UI
- Schedule Search UI
- Schedule Detail UI
- Schedule Registration UI
- Schedule Edit UI
- Schedule Delete UI
- Schedule Status UI
- Schedule Management Styling
- Schedule API Client
- Schedule API Integration
- Schedule Calendar UI
- Schedule Calendar API Integration
- Calendar Component Separation
- Calendar Utility Separation
- Production Build Verification

### Current

- WBS Browser Verification
- WBS Integration Test
- Schedule Browser Verification
- Schedule / Calendar Integration Verification

### Next

- Gantt UI
- WBS / Schedule Gantt Integration
- Dashboard API Integration
- Authentication UI

---

# 15. Current WBS State

```text
WBS Backend
    ├── WBS API                  ✓
    ├── WBS CRUD                 ✓
    ├── WBS Validation           ✓
    └── Delete Handling          ✓
                │
                ▼
WBS Frontend
    ├── API Client               ✓
    ├── API Integration          ✓
    ├── Tree                     ✓
    ├── Detail                   ✓
    ├── Form                     ✓
    ├── Status Form              ✓
    └── Delete                   ✓
                │
                ▼
Integration Verification
    ├── API Integration          ✓
    ├── Browser Verification     →
    └── Integration Test         →
```

---

# 16. Current Schedule State

```text
Schedule Backend
    ├── CRUD                     ✓
    ├── List                     ✓
    ├── Detail                   ✓
    ├── Create                   ✓
    ├── Update                   ✓
    ├── Delete                   ✓
    ├── Status                   ✓
    ├── Sort Order               ✓
    ├── Search                   ✓
    ├── Pagination               ✓
    ├── Dynamic Sorting          ✓
    ├── Period Search            ✓
    ├── Validation               ✓
    ├── Calendar API             ✓
    └── Swagger Verification     ✓
                │
                ▼
Schedule Frontend
    ├── Management UI            ✓
    ├── List UI                  ✓
    ├── Search UI                ✓
    ├── Detail UI                ✓
    ├── Create UI                ✓
    ├── Edit UI                  ✓
    ├── Delete UI                ✓
    ├── Status UI                ✓
    ├── Styling                  ✓
    └── Calendar UI              ✓
                │
                ▼
API Integration
    ├── API Client               ✓
    ├── Schedule Integration     ✓
    ├── Calendar Integration     ✓
    ├── Production Build         ✓
    └── Browser Verification     →
                │
                ▼
Visualization
    ├── Calendar                 ✓
    └── Gantt                    →
```

Schedule Domain은 **Backend API, Management UI, Calendar UI 및 Calendar API Integration**까지 구현된 상태이다.

현재 다음 핵심 단계는 **Browser Verification과 Gantt Visualization**이다.

---

# 17. Current Project Frontend State

Project Management Frontend는 CRUD 전체 기능이 구현되어 `develop`에 통합된 상태이다.

```text
Project List                 ✓
Project Search               ✓
Project Detail               ✓
Project Registration         ✓
Project Edit                 ✓
Project Delete               ✓
Project API Integration      ✓
Loading State                ✓
Empty State                  ✓
Error State                  ✓
Router Integration           ✓
Production Build             ✓
```

---

# 18. Current Git Development State

## Recent Schedule Calendar Refactoring

```text
refactor/schedule-calendar-components
        ↓
develop
```

최근 Commit:

```text
4be29e6

refactor(frontend): split schedule calendar into components
```

주요 변경:

```text
CalendarHeader.tsx
CalendarGrid.tsx
CalendarDay.tsx
CalendarEvent.tsx
ScheduleCalendar.tsx
calendarUtils.ts
```

변경 내용:

- ScheduleCalendar 책임 축소
- Calendar Header 분리
- Calendar Grid 분리
- Calendar Day 분리
- Calendar Event 분리
- Calendar 날짜 Utility 정리
- Calendar Grid 생성 로직 Utility 분리
- TypeScript Build 오류 수정
- Production Build Verification 완료

현재 통합 기준 브랜치는 다음과 같다.

```text
develop
```

최근 통합 상태:

```text
refactor/schedule-calendar-components
        ↓
Fast-forward Merge
        ↓
develop
        ↓
Push origin develop
```

---

# 19. Development Process

```text
Planning
    ↓
Feature Branch 생성
    ↓
Development
    ↓
Local Test
    ↓
Production Build
    ↓
Documentation Update
    ↓
Commit
    ↓
Push
    ↓
Pull Request / Merge Review
    ↓
Merge to develop
    ↓
Integration Test
    ↓
Release
```

## Schedule Development Flow

```text
Schedule Backend API
    ↓
Swagger Verification ✓
    ↓
Schedule Frontend UI ✓
    ↓
Frontend API Client ✓
    ↓
Schedule API Integration ✓
    ↓
Production Build ✓
    ↓
Calendar UI ✓
    ↓
Calendar API Integration ✓
    ↓
Calendar Component Refactoring ✓
    ↓
Schedule / Calendar Browser Verification
    ↓
Gantt UI
    ↓
WBS / Schedule Integration
    ↓
E2E Verification
```

---

# 20. Documentation Policy

다음 문서는 항상 최신 상태를 유지한다.

- CHANGELOG.md
- DEVELOPMENT_ROADMAP.md
- PROJECT_OVERVIEW.md
- ARCHITECTURE.md
- PORTFOLIO.md

소스코드 변경이 발생하면 관련 문서를 함께 현행화한다.

특히 Schedule 및 Calendar 변경 시 다음 문서를 함께 검토한다.

- Schedule Design
- Frontend Architecture
- Frontend Component Architecture
- Frontend Development History
- CHANGELOG
- DEVELOPMENT_ROADMAP

---

# 21. Maintenance Policy

- 모든 기능은 Feature Branch에서 개발한다.
- 모든 주요 변경 사항은 CHANGELOG에 기록한다.
- Backend와 Frontend의 개발 상태를 Roadmap에 반영한다.
- 문서와 소스코드는 항상 동일한 상태를 유지한다.
- Pull Request 검토 후 Develop 브랜치에 병합한다.
- Main 브랜치에는 검증된 코드만 Release한다.
- Semantic Versioning을 준수한다.
- Release마다 Git Tag를 생성한다.
- 증적 관련 변경은 Evidence History에 기록한다.
- 필수 증적의 누락 상태를 Dashboard에서 추적한다.
- 검수 결과와 증적 상태를 Report에 반영한다.
- API Integration 기능은 API Verification과 Browser Verification을 모두 수행한다.
- Feature Branch Merge 후 `develop` Integration Test를 수행한다.
- Component Refactoring 후 Production Build를 수행한다.

---

# 22. Current Information

| Item | Value |
|---|---|
| Last Updated | **2026-09-09** |
| Roadmap Version | **v1.9** |
| Current Release | **v0.5.3** |
| Current Integration Branch | **develop** |
| Current Sprint | **Sprint 3 - WBS / Schedule Management** |
| Last Completed Backend Domain | **Schedule Management** |
| Last Completed Backend Feature | **Schedule CRUD / Search / Calendar API / Swagger Verification** |
| Last Completed Frontend Domain | **Schedule Calendar Component Refactoring** |
| Development Stage | **Schedule Backend + Frontend Management UI + Calendar Integration 완료** |
| Current Next Stage | **Schedule / Calendar Browser Verification 및 Gantt UI** |
| Next Major Domain | **Issue Management** |
| Evidence Domain | **Planned - Sprint 5** |
| Maintainer | **Seo Seokhyeon** |

> **Version Note:** `v0.5.3`은 마지막 정식 Release 기준이다.
>
> 현재 `develop`에는 Project Frontend CRUD, WBS Management, Schedule Backend Management, Schedule Frontend Management UI, Schedule Calendar Integration 및 Calendar Component Refactoring 변경 사항이 통합되어 있다.

---

# 23. Next Milestone

# Sprint 3 Completion

## Backend

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
Schedule Calendar API          ✓
Schedule Swagger Verification  ✓
```

## Frontend

```text
Project CRUD                   ✓
WBS API Integration            ✓
WBS UI                         ✓
WBS Delete                     ✓

Schedule Management UI         ✓
Schedule List                  ✓
Schedule Search                ✓
Schedule Detail                ✓
Schedule Create                ✓
Schedule Edit                  ✓
Schedule Delete                ✓
Schedule Status                ✓
Schedule Styling               ✓

Schedule API Integration       ✓
Calendar UI                    ✓
Calendar API Integration       ✓
Calendar Component Refactoring ✓
Production Build               ✓

Schedule Browser Verification  →
Gantt UI                       →
Gantt Integration              →
```

## Recommended Development Order

```text
WBS Browser Verification
        ↓
WBS Integration Test
        ↓
Schedule / Calendar Browser Verification
        ↓
Schedule Integration Test
        ↓
Gantt UI
        ↓
WBS / Schedule Gantt Integration
        ↓
Milestone Visualization
        ↓
Progress Visualization
        ↓
Dashboard API Integration
        ↓
E2E Verification
        ↓
Issue Management
        ↓
Risk Management
        ↓
Change Management
        ↓
Evidence Management
```

---

# 24. Current Development Priority

```text
1. WBS Browser Verification
        ↓
2. WBS Integration Test
        ↓
3. Schedule / Calendar Browser Verification
        ↓
4. Schedule Integration Test
        ↓
5. Gantt UI
        ↓
6. WBS / Schedule Gantt Integration
        ↓
7. Dashboard API Integration
        ↓
8. Dashboard Data Integration
        ↓
9. E2E Verification
        ↓
10. Issue Management
        ↓
11. Risk Management
        ↓
12. Change Management
        ↓
13. Evidence Management
```

현재 개발의 중심은 다음과 같다.

```text
Schedule Management UI
        ✓
        ↓
Schedule API Integration
        ✓
        ↓
Calendar UI / API Integration
        ✓
        ↓
Calendar Component Refactoring
        ✓
        ↓
Browser Verification
        ↓
Gantt
        ↓
Issue / Risk / Change
```

---

# 25. Schedule Development Completion Criteria

## Backend

```text
Schedule Entity                ✓
Schedule Repository            ✓
Schedule Service               ✓
Schedule Controller            ✓
Schedule CRUD                  ✓
Schedule Search                ✓
Pagination                     ✓
Sorting                        ✓
Period Search                  ✓
Validation                     ✓
Calendar API                   ✓
Swagger Verification           ✓
```

## Frontend

```text
Schedule Management UI         ✓
Schedule List                  ✓
Schedule Search                ✓
Schedule Detail                ✓
Schedule Create                ✓
Schedule Edit                  ✓
Schedule Delete                ✓
Schedule Status                ✓
Schedule Styling               ✓

Schedule API Client            ✓
Schedule API Integration       ✓
Loading / Empty / Error        ✓
Calendar UI                    ✓
Calendar API Integration       ✓
Calendar Component Separation  ✓
Calendar Utility Separation    ✓
Production Build               ✓

Browser Verification           →
Integration Test               →
```

## Calendar / Gantt

```text
Schedule Data
    ↓
Calendar                       ✓
    ↓
Timeline
    ↓
Milestone
    ↓
Gantt
    ↓
WBS Relationship
    ↓
Progress Visualization
```

Schedule Domain의 최종 완료 기준은 다음과 같다.

```text
Backend
    ↓
Swagger
    ↓
Frontend UI
    ↓
API Integration
    ↓
Calendar
    ↓
Browser Verification
    ↓
Gantt
    ↓
WBS Relationship
    ↓
E2E Verification
```

---

# 26. Release Summary

| Version | Description |
|---|---|
| v0.1.0 | Project Initialization |
| v0.2.0 | Common Infrastructure |
| v0.3.0 | Security Foundation |
| v0.3.1 | Authentication & Authorization |
| v0.3.2 | Swagger / OpenAPI |
| v0.4.0 | Project Management |
| v0.5.0 | Frontend Foundation |
| v0.5.1 | Frontend Layout & Common Components |
| v0.5.2 | Project Detail API & Controller Separation |
| v0.5.3 | Frontend Dashboard HMC Reference |
| Next Release | Project / WBS / Schedule Integration |
| v0.7.x | Issue / Risk / Change |
| v0.8.x | Evidence & Inspection |
| v0.9.x | CMDB |
| v0.10.x | Dashboard & Reporting |
| v1.0.0 | AI Powered PMIS |

---

# 27. Long-term Roadmap

| Version | Goal |
|---|---|
| 1.x | PMIS Core Platform |
| 2.x | Enterprise PMIS |
| 3.x | AI Native PMIS Platform |

---

# 28. Evidence Management Long-term Direction

## Phase 1 - Evidence Checklist

- 증적 요구사항 정의
- 필수 / 선택 증적 구분
- 증적 존재 여부 확인
- 누락 증적 조회
- 증적 확보율 Dashboard

## Phase 2 - Evidence Verification

- 검수자 지정
- 검수 상태 관리
- 승인 / 반려
- 검수 의견
- 검수 이력

## Phase 3 - Evidence Traceability

```text
Project
    ↓
WBS
    ↓
Task
    ↓
Activity
    ↓
Deliverable
    ↓
Evidence Requirement
    ↓
Evidence
    ↓
Verification
    ↓
Inspection
    ↓
Report
```

## Phase 4 - AI Evidence Assistant

- 필수 증적 누락 탐지
- WBS 대비 증적 미등록 탐지
- 일정 대비 증적 지연 탐지
- 검수 반려 증적 분석
- 반복적인 증적 누락 패턴 분석
- 증적 제출 우선순위 추천
- 주간 증적 상태 요약
- 증적 관련 질의응답

---

# 29. Current Development Snapshot

```text
Backend

├─ Common Infrastructure          ✓
├─ Security / JWT                 ✓
├─ Project CRUD                   ✓
├─ Project Search                 ✓
├─ Project Dashboard API          ✓
├─ Project Detail API             ✓
├─ WBS Management                 ✓
├─ Schedule CRUD                  ✓
├─ Schedule Search                ✓
├─ Schedule Validation            ✓
├─ Schedule Calendar API          ✓
├─ Schedule Swagger Test          ✓
├─ Issue                          →
├─ Risk                           →
├─ Change                         →
├─ Evidence                       →
├─ CMDB                           →
├─ Dashboard                      →
├─ Report                         →
└─ Spring AI                      →


Frontend

├─ React / Vite / TypeScript      ✓
├─ Router / Layout                ✓
├─ Common UI Components           ✓
├─ Dashboard Reference UI         ✓
├─ Dashboard Components           ✓
├─ Project CRUD Integration       ✓
├─ WBS API Integration            ✓
├─ WBS UI                         ✓
├─ Schedule Management UI         ✓
├─ Schedule List UI               ✓
├─ Schedule Search UI             ✓
├─ Schedule Detail UI             ✓
├─ Schedule Create UI             ✓
├─ Schedule Edit UI               ✓
├─ Schedule Delete UI             ✓
├─ Schedule Status UI             ✓
├─ Schedule Styling               ✓
├─ Schedule API Client            ✓
├─ Schedule API Integration       ✓
├─ Calendar UI                    ✓
├─ Calendar API Integration       ✓
├─ Calendar Component Refactoring ✓
└─ Gantt UI                       →


Integration

├─ Project API Integration        ✓
├─ Project CRUD Verification      ✓
├─ WBS API Integration            ✓
├─ WBS Browser Verification       →
├─ WBS Integration Test           →
├─ Schedule API Verification      ✓
├─ Schedule API Integration       ✓
├─ Calendar Integration           ✓
├─ Schedule Production Build      ✓
├─ Schedule Browser Verification  →
├─ Gantt Integration              →
├─ Dashboard API Integration      →
└─ E2E Verification               →
```

---

# 30. Current Development Direction

```text
Foundation
    ✓
    ↓
Security / JWT
    ✓
    ↓
Project Management
    ✓
    ↓
Frontend Foundation
    ✓
    ↓
Project Frontend CRUD
    ✓
    ↓
WBS Management
    ✓
    ↓
Schedule Backend
    ✓
    ↓
Schedule Frontend Management UI
    ✓
    ↓
Schedule API Integration
    ✓
    ↓
Calendar UI / API Integration
    ✓
    ↓
Calendar Component Refactoring
    ✓
    ↓
Schedule / Calendar Verification
    →
    ↓
Gantt
    →
    ↓
Issue Management
    →
    ↓
Risk Management
    →
    ↓
Change Management
    →
    ↓
Evidence Management
    →
```

현재 개발 단계의 핵심 목표는 **Schedule Domain의 Backend와 Frontend Integration을 Browser 수준에서 검증하고 Gantt Visualization으로 확장하는 것**이다.

그 이후 PMO 핵심 업무 Domain인 **Issue → Risk → Change**로 확장한다.

---

# 31. Schedule Calendar Refactoring Record

## Refactoring Goal

기존 `ScheduleCalendar.tsx`에 집중되어 있던 UI Rendering과 날짜 관련 책임을 분리한다.

## Before

```text
ScheduleCalendar.tsx

├── Calendar Header
├── Month Navigation
├── Calendar Grid 생성
├── Calendar Day Rendering
├── Schedule Event Rendering
├── API 호출
└── Date Utility Logic
```

## After

```text
ScheduleCalendar.tsx
├── State Management
├── Calendar API 호출
├── Month Navigation
└── Component Composition

CalendarHeader.tsx
└── Month Navigation UI

CalendarGrid.tsx
└── Calendar Grid Rendering

CalendarDay.tsx
└── Daily Schedule Rendering

CalendarEvent.tsx
└── Schedule Event Rendering

calendarUtils.ts
├── formatDate()
├── parseDate()
├── isSameDate()
├── isDateInRange()
└── createCalendarDays()
```

## Result

- Component Responsibility Separation
- Calendar Rendering Structure 개선
- Date Utility 재사용성 향상
- ScheduleCalendar 복잡도 감소
- 향후 Gantt / Timeline 구현 시 Date Utility 재사용 가능
- `npm run build` 검증 완료

---

# 32. Next Recommended Branch

다음 작업은 기존 Sprint 3 완료 기준에 따라 **Browser Verification 및 Gantt UI**를 진행하는 것을 권장한다.

예상 Branch:

```text
feature/frontend-schedule-gantt
```

또는 기존 Branch 정책을 유지한다면:

```text
feature/frontend-schedule
```

권장 작업 순서:

```text
Schedule / Calendar Browser Verification
        ↓
Schedule Integration Test
        ↓
Gantt Data Model 설계
        ↓
Gantt UI
        ↓
WBS / Schedule Relationship
        ↓
Milestone Visualization
        ↓
Progress Visualization
        ↓
Browser Verification
        ↓
Production Build
        ↓
Documentation Update
        ↓
Commit / Push / Merge
```

---

# End of Document
