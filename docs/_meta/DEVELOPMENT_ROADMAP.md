# PMIS Development Roadmap

> **Version:** **1.11**
>
> **Last Updated:** **2026-09-14**
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
PMIS Frontend
React + TypeScript + Vite
        ↓ REST API
PMIS Backend
Spring Boot + Spring Security + JWT
Controller / Service / Repository
DTO / Mapper / Specification
        ↓
MariaDB
        ↓
File / Object Storage (Planned)
        ↓
Spring AI
AI Assistant / Analysis / RAG
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

모든 기능은 Feature Branch에서 개발 후 `develop`으로 Merge한다.

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
- Issue Management Backend
- Issue Key Generation Hotfix

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

feat: implement issue management domain

feat(frontend): implement schedule management UI

feat(frontend): integrate schedule calendar API

refactor(frontend): split schedule calendar into components

fix: add project-based issue key generation

docs: update PMIS development roadmap
```

---

# 7. Sprint Plan

## Sprint 0 - Base Project

### Goal

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

---

## Sprint 1 - Authentication & Security

### Goal

JWT 기반 인증 / 인가 구축

### Backend

- JWT Provider
- JWT Filter
- SecurityConfig
- UserPrincipal
- Login
- Refresh Token
- Role Authorization
- Password Encoder

### Frontend Planned

- Login Page
- Login Form
- Authentication State
- Token Handling
- Protected Route
- Logout

---

## Sprint 2 - Project Management & Frontend Foundation

### Backend Completed

- Project CRUD
- Customer / PM / Status / Priority
- Validation
- Project Search
- Sorting
- Project Dashboard API
- Project Dashboard KPI
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

### Frontend Foundation

- React
- Vite
- TypeScript
- React Router DOM
- Feature-based Directory Structure
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

### Project Frontend

- Project List / Search / Detail
- Project Registration / Edit / Delete
- API Integration
- Loading / Empty / Error State

---

## Sprint 3 - WBS / Schedule Management

### WBS Backend Completed

- WBS CRUD
- Tree Structure
- Progress Calculation
- Task Relationship
- Validation
- Delete Handling
- Swagger API Verification

### WBS Frontend Completed

- WBS API Client
- WBS API Integration
- WBS Tree UI
- WBS Detail UI
- WBS Form UI
- WBS Status Form UI
- Progress Display
- WBS Delete

### WBS Remaining

- WBS Browser Verification
- WBS Integration Test

### Schedule Backend Completed

- Schedule CRUD
- Project별 Schedule 조회
- Schedule 단건 조회
- Schedule 생성 / 수정 / 삭제
- Schedule Status 변경
- Sort Order 관리
- Schedule Search
- Project / WBS / Keyword / Status 검색
- Dynamic Sorting
- Pagination
- Period Search
- Validation
- Calendar 조회 API
- Swagger / OpenAPI 검증

### Schedule Frontend Completed

- Schedule Management UI
- Schedule List / Search / Detail
- Schedule Registration / Edit / Delete
- Schedule Status Management UI
- Schedule API Client / API Integration
- Schedule Calendar UI
- Calendar API Integration
- Calendar Range Query
- Previous / Next Month Navigation
- Today Navigation
- Loading / Error State
- Calendar Schedule Selection
- Calendar Component Refactoring
- Calendar Utility Separation
- Production Build Verification

### Schedule Next

- Schedule Browser Verification
- Gantt UI
- WBS / Schedule 연계
- Timeline Visualization
- Milestone Visualization
- Progress Visualization

---

## Sprint 4 - Issue / Risk / Change Management

### Goal

프로젝트의 이슈, 리스크, 변경 요청을 통합 관리한다.

### Issue Backend Completed

- Issue Entity
- Issue CRUD
- Project별 Issue 조회
- Issue 단건 조회
- Issue Search
- Dynamic Specification
- Keyword Search
- Status Search
- Priority Search
- Assignee Search
- Pagination
- Dynamic Sorting
- Issue Status Update
- Status Workflow Validation
- Issue Delete
- Swagger API Verification
- Project-based Issue Key Generation
- `(project_id, issue_key)` Unique Constraint

### Issue API

```text
GET    /api/issues/{id}
GET    /api/projects/{projectId}/issues
GET    /api/issues
POST   /api/projects/{projectId}/issues
PUT    /api/issues/{id}
PATCH  /api/issues/{id}/status
DELETE /api/issues/{id}
```

### Issue Frontend

- Issue API Client
- Issue List
- Issue Search
- Issue Detail
- Issue Registration
- Issue Edit
- Issue Status UI
- Issue Delete
- Issue API Integration
- Issue Key Display

### Future Enhancement

- Assignment UI 고도화
- Attachment
- Issue History
- Comment / Activity
- Dashboard Integration

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

## Sprint 5 - Evidence & Inspection Management

### Goal

프로젝트 수행 과정에서 발생하는 산출물과 증적을 체계적으로 관리하고 증적의 존재 여부 및 검수 상태를 확인한다.

### Evidence Status

```text
Evidence Item
      ↓
Required?
├── YES → Evidence Exists?
│          ├── YES → PRESENT → Verification
│          │                    ├── PENDING
│          │                    ├── VERIFIED
│          │                    └── REJECTED
│          └── NO  → MISSING
└── NO → NOT_REQUIRED
```

### Evidence Dashboard

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

## Sprint 6 - CMDB

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

## Sprint 7 - Dashboard & Report

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

---

## Sprint 8 - Spring AI

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

## Sprint 9 - Finalization

### Backend

- Refactoring
- Unit Test
- Integration Test
- Swagger
- Docker / Docker Compose
- GitHub Actions
- Security Hardening
- Audit Log
- Evidence Integrity Validation

### Frontend

- Component Refactoring
- Responsive UI
- Frontend Test
- Error Handling
- Loading / Empty / Error State
- Accessibility
- Build Optimization

### Common

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
- Loading / Empty / Error State
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
├── project/
│   ├── PROJECT_DESIGN.md
│   └── PROJECT_DASHBOARD_DESIGN.md
├── report/
│   └── REPORT_DESIGN.md
├── schedule/
│   └── SCHEDULE_DESIGN.md
├── server/
│   └── SERVER_CONFIGURATION_DESIGN.md
├── wbs/
│   └── WBS_DESIGN.md
└── evidence/
    └── EVIDENCE_DESIGN.md
```

문서와 소스코드는 항상 동일한 상태를 유지한다.

---

# 12. Final Deliverables

## Backend

- PMIS Backend
- Spring Boot REST API
- JWT Authentication
- MariaDB Schema
- Project Management
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
- React / TypeScript / Vite
- React Router
- Application Layout
- Dashboard
- Project / WBS / Schedule UI
- Calendar / Gantt UI
- Issue / Risk / Change UI
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

## Backend Completed

### Foundation / Security

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
- Controller Separation

### WBS

- WBS CRUD
- WBS Validation
- WBS Delete Handling
- WBS Tree Structure

### Schedule

- Schedule CRUD
- Schedule Search
- Schedule Pagination / Sorting
- Schedule Period Search
- Schedule Validation
- Schedule Calendar API
- Schedule Swagger API Verification

### Issue

- Issue CRUD
- Project별 Issue 조회
- Issue Search
- Dynamic Specification
- Keyword / Status / Priority / Assignee Search
- Pagination
- Dynamic Sorting
- Issue Status Update
- Status Workflow Validation
- Issue Delete
- Swagger API Verification
- Project-based Issue Key Generation
- Project-scoped Unique Issue Key Constraint

### Planned

- Issue Frontend Completion
- Risk
- Change
- Evidence
- Inspection
- CMDB
- Dashboard Integration
- Report
- Spring AI

---

## Frontend Completed

### Foundation

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

### Dashboard

- HMC Reference 기반 Dashboard UI
- Dashboard Component 분리
- Production Build Verification

### Project

- Project API Client
- Project List / Search / Detail
- Project Registration / Edit / Delete
- Project CRUD API Integration
- Loading / Empty / Error State

### WBS

- WBS API Client / Integration
- WBS Tree / Detail / Form / Status Form
- WBS Delete

### Schedule

- Schedule Management UI
- Schedule List / Search / Detail
- Schedule Registration / Edit / Delete
- Schedule Status UI
- Schedule API Integration
- Schedule Calendar UI
- Calendar API Integration
- Calendar Component Separation
- Calendar Utility Separation
- Production Build Verification

### Current

- WBS Browser Verification
- WBS Integration Test
- Schedule Browser Verification
- Schedule / Calendar Integration Verification

### Next

- Issue Frontend
- Issue API Integration
- Issue Browser Verification
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
    ├── List / Search / Detail   ✓
    ├── Create / Edit / Delete   ✓
    ├── Status UI                ✓
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

---

# 17. Current Issue State

```text
Issue Backend
    ├── Entity                   ✓
    ├── Priority / Status        ✓
    ├── Repository               ✓
    ├── Create / Update DTO      ✓
    ├── Search DTO               ✓
    ├── Status Update DTO        ✓
    ├── Response DTO             ✓
    ├── Specification            ✓
    ├── CRUD Service              ✓
    ├── Search                   ✓
    ├── Status Workflow          ✓
    ├── Controller               ✓
    ├── Swagger Verification     ✓
    ├── Issue Key Generation     ✓
    └── Project-scoped Unique Key ✓
                │
                ▼
Git Integration
    ├── Feature Commit           ✓
    ├── Push                     ✓
    ├── Merge to develop         ✓
    └── develop Push             ✓
                │
                ▼
Issue Frontend
    ├── API Client               →
    ├── List                     →
    ├── Search                   →
    ├── Detail                   →
    ├── Create                   →
    ├── Edit                     →
    ├── Status                   →
    ├── Delete                   →
    └── Issue Key Display        →
```

---

# 18. Current Git Development State

## Recent Schedule Calendar Refactoring

```text
refactor/schedule-calendar-components
        ↓
develop
```

Commit:

```text
4be29e6
refactor(frontend): split schedule calendar into components
```

주요 변경:

- CalendarHeader.tsx
- CalendarGrid.tsx
- CalendarDay.tsx
- CalendarEvent.tsx
- ScheduleCalendar.tsx
- calendarUtils.ts

## Issue Management Integration

```text
feature/issue-management
        ↓
61673d0
feat: implement issue management domain
        ↓
Fast-forward Merge
        ↓
develop
        ↓
Push origin develop
```

## Issue Key Generation Hotfix

```text
hotfix/issue-key-generation
        ↓
fix: add project-based issue key generation
        ↓
develop
        ↓
Push origin develop
```

주요 변경:

- `Issue.issueKey` Backend 생성
- `UNIQUE(project_id, issue_key)` 적용
- Project별 `ISSUE-001`, `ISSUE-002`, ... 생성 정책
- Issue Key 수정 불가 정책 유지
- Frontend는 Issue Key를 입력하지 않고 표시만 수행

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

## Issue Development Flow

```text
Issue Design
    ↓
Issue Entity / Enum ✓
    ↓
Repository ✓
    ↓
DTO ✓
    ↓
Specification ✓
    ↓
Service ✓
    ↓
Controller ✓
    ↓
Swagger API Verification ✓
    ↓
Project-based Issue Key Generation ✓
    ↓
Unique Constraint ✓
    ↓
Commit / Push ✓
    ↓
Merge to develop ✓
    ↓
Frontend Issue UI
    ↓
API Integration
    ↓
Browser Verification
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

Issue 변경 시 다음 문서를 함께 검토한다.

- ISSUE_DESIGN.md
- DEVELOPMENT_ROADMAP.md
- CHANGELOG.md
- PROJECT_OVERVIEW.md

문서에는 Backend와 Frontend의 실제 개발 상태 및 Git Integration 상태를 함께 반영한다.

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
- API Integration 기능은 API Verification과 Browser Verification을 모두 수행한다.
- Feature Branch Merge 후 `develop` Integration Test를 수행한다.
- Component Refactoring 후 Production Build를 수행한다.
- Hotfix는 `hotfix/*` 브랜치에서 수정 후 검증하여 `develop`에 반영한다.

---

# 22. Current Information

| Item | Value |
|---|---|
| Last Updated | **2026-09-14** |
| Roadmap Version | **v1.11** |
| Current Release | **v0.5.3** |
| Current Integration Branch | **develop** |
| Current Sprint | **Sprint 4 - Issue / Risk / Change Management** |
| Last Completed Backend Domain | **Issue Management** |
| Last Completed Backend Feature | **Issue CRUD / Search / Status Workflow / Project-based Issue Key Generation / Swagger Verification** |
| Last Completed Frontend Domain | **Schedule Calendar Component Refactoring** |
| Development Stage | **Issue Backend + Issue Key Hotfix develop 통합 완료** |
| Current Next Stage | **Issue Frontend UI / API Integration** |
| Next Backend Domain | **Risk Management** |
| Evidence Domain | **Planned - Sprint 5** |
| Maintainer | **Seo Seokhyeon** |

> **Version Note:** `v0.5.3`은 마지막 정식 Release 기준이다.
>
> 현재 `develop`에는 Project Frontend CRUD, WBS Management, Schedule Backend Management, Schedule Frontend Management UI, Schedule Calendar Integration, Calendar Component Refactoring, Issue Management Backend 및 Project-based Issue Key Generation Hotfix가 통합되어 있다.

---

# 23. Next Milestone

## Sprint 3 Completion

```text
WBS Browser Verification       →
WBS Integration Test           →
Schedule Browser Verification  →
Schedule Integration Test      →
Gantt UI                       →
Gantt Integration              →
```

## Sprint 4 Progress

```text
Issue Backend                  ✓
Issue Swagger Verification     ✓
Issue develop Integration      ✓
Issue Key Generation           ✓
Issue Frontend                 →
Risk Backend                   →
Risk Frontend                  →
Change Backend                 →
Change Frontend                →
```

## Recommended Development Order

### Track A - Sprint 3 Completion

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
```

### Track B - Sprint 4 Domain Expansion

```text
Issue Frontend
        ↓
Issue API Integration
        ↓
Issue Browser Verification
        ↓
Risk Backend
        ↓
Risk Frontend
        ↓
Change Backend
        ↓
Change Frontend
```

---

# 24. Current Development Priority

현재는 두 개의 작업 흐름이 존재한다.

## Track A - Sprint 3 Completion

1. WBS Browser Verification
2. WBS Integration Test
3. Schedule / Calendar Browser Verification
4. Schedule Integration Test
5. Gantt UI
6. WBS / Schedule Gantt Integration
7. Dashboard API Integration
8. Dashboard Data Integration
9. E2E Verification

## Track B - Sprint 4 Domain Expansion

1. Issue Backend ✓
2. Issue Key Generation ✓
3. Issue Frontend
4. Issue API Integration
5. Issue Browser Verification
6. Risk Management
7. Change Management
8. Evidence Management

현재 Backend Domain 확장 기준으로는 **Issue Management와 Project-based Issue Key Generation이 완료되어 `develop`에 통합**되었다.

---

# 25. Schedule Development Completion Criteria

## Backend

```text
Schedule Entity                ✓
Schedule Repository             ✓
Schedule Service                ✓
Schedule Controller             ✓
Schedule CRUD                   ✓
Schedule Search                 ✓
Pagination                      ✓
Sorting                         ✓
Period Search                   ✓
Validation                      ✓
Calendar API                    ✓
Swagger Verification            ✓
```

## Frontend

```text
Schedule Management UI          ✓
Schedule List                   ✓
Schedule Search                 ✓
Schedule Detail                 ✓
Schedule Create                 ✓
Schedule Edit                   ✓
Schedule Delete                 ✓
Schedule Status                 ✓
Schedule API Client             ✓
Schedule API Integration        ✓
Loading / Empty / Error         ✓
Calendar UI                     ✓
Calendar API Integration        ✓
Calendar Component Separation   ✓
Calendar Utility Separation     ✓
Production Build                ✓
Browser Verification            →
Integration Test                →
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

> Issue Management Backend 및 Project-based Issue Key Generation은 현재 `develop`에 통합되어 있으나, 별도 정식 Release Version은 아직 생성하지 않았다.

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
├─ Security / JWT                ✓
├─ Project CRUD                  ✓
├─ Project Search                ✓
├─ Project Dashboard API         ✓
├─ Project Detail API            ✓
├─ WBS Management                ✓
├─ Schedule CRUD                 ✓
├─ Schedule Search               ✓
├─ Schedule Validation           ✓
├─ Schedule Calendar API         ✓
├─ Schedule Swagger Test         ✓
├─ Issue Management              ✓
├─ Issue Swagger Test            ✓
├─ Issue develop Integration     ✓
├─ Issue Key Generation          ✓
├─ Project-scoped Issue Key      ✓
├─ Risk                          →
├─ Change                        →
├─ Evidence                      →
├─ CMDB                          →
├─ Dashboard                    →
├─ Report                        →
└─ Spring AI                    →

Frontend
├─ React / Vite / TypeScript      ✓
├─ Router / Layout               ✓
├─ Common UI Components          ✓
├─ Dashboard Reference UI        ✓
├─ Dashboard Components          ✓
├─ Project CRUD Integration      ✓
├─ WBS API Integration           ✓
├─ WBS UI                       ✓
├─ Schedule Management UI        ✓
├─ Schedule API Integration      ✓
├─ Calendar UI                  ✓
├─ Calendar API Integration      ✓
├─ Calendar Component Refactoring ✓
├─ Issue UI                     →
└─ Gantt UI                     →

Integration
├─ Project API Integration       ✓
├─ Project CRUD Verification     ✓
├─ WBS API Integration           ✓
├─ WBS Browser Verification      →
├─ WBS Integration Test          →
├─ Schedule API Verification     ✓
├─ Schedule API Integration      ✓
├─ Calendar Integration          ✓
├─ Schedule Production Build     ✓
├─ Schedule Browser Verification →
├─ Gantt Integration             →
├─ Issue API Verification        ✓
├─ Issue Frontend Integration    →
├─ Dashboard API Integration     →
└─ E2E Verification              →
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
Issue Management Backend
    ✓
    ↓
Issue Key Generation
    ✓
    ↓
Issue Frontend
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

현재 Backend Domain 확장의 핵심 단계는 **Issue Management + Project-based Issue Key Generation 완료**이다.

다음 단계는 **Issue Frontend → Issue API Integration → Issue Browser Verification**을 우선 진행하고, 이후 Risk → Change 순으로 확장한다.

---

# 31. Schedule Calendar Refactoring Record

## Refactoring Goal

기존 `ScheduleCalendar.tsx`에 집중되어 있던 UI Rendering과 날짜 관련 책임을 분리한다.

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

# 32. Issue Management Integration Record

## Feature Branch

```text
feature/issue-management
```

## Feature Commit

```text
61673d0
feat: implement issue management domain
```

## Integration

```text
feature/issue-management
        ↓
Push origin feature/issue-management
        ↓
git checkout develop
        ↓
git merge feature/issue-management
        ↓
Fast-forward Merge
        ↓
git push origin develop
```

## Implemented Files

```text
issue/
├── controller/
│   └── IssueController.java
├── dto/
│   ├── request/
│   │   ├── IssueCreateRequest.java
│   │   ├── IssueSearchRequest.java
│   │   ├── IssueStatusUpdateRequest.java
│   │   └── IssueUpdateRequest.java
│   └── response/
│       └── IssueResponse.java
├── entity/
│   ├── Issue.java
│   ├── IssuePriority.java
│   └── IssueStatus.java
├── repository/
│   └── IssueRepository.java
├── service/
│   └── IssueService.java
└── specification/
    └── IssueSpecification.java
```

## Implemented API

```text
GET    /api/issues/{id}
GET    /api/projects/{projectId}/issues
GET    /api/issues
POST   /api/projects/{projectId}/issues
PUT    /api/issues/{id}
PATCH  /api/issues/{id}/status
DELETE /api/issues/{id}
```

## Search Conditions

```text
projectId
keyword
status
priority
assigneeId
page
size
sortBy
direction
```

검색 조건은 `IssueSpecification`에서 동적으로 조합하며 `Specification.allOf()` 기반으로 AND 조건을 구성한다.

---

# 33. Issue Key Generation Hotfix Record

## Hotfix Branch

```text
hotfix/issue-key-generation
```

## Purpose

Issue 생성 시 Frontend가 Issue Key를 직접 입력하지 않고 Backend가 프로젝트 단위로 자동 생성하도록 개선한다.

## Generation Policy

```text
Project A
├── ISSUE-001
├── ISSUE-002
└── ISSUE-003

Project B
├── ISSUE-001
├── ISSUE-002
└── ISSUE-003
```

Issue Key는 프로젝트별로 독립적인 sequence를 가진다.

## Database Constraint

```text
UNIQUE(project_id, issue_key)
```

동일 프로젝트 내에서는 동일한 Issue Key를 생성할 수 없으며, 서로 다른 프로젝트에서는 동일한 Issue Key를 사용할 수 있다.

## Backend Policy

- Issue Key는 Backend에서 생성한다.
- Create Request에서는 Issue Key를 입력하지 않는다.
- Update 시 Issue Key는 변경하지 않는다.
- Frontend는 Issue Key를 표시만 한다.
- 현재 형식은 `ISSUE-%03d`를 사용한다.

## Repository Support

```java
Optional<Issue> findTopByProject_IdOrderByIdDesc(Long projectId);
```

프로젝트 내 최근 Issue를 기준으로 다음 Issue Key sequence를 계산한다.

## Current Status

```text
Issue Management
      ✓
      ↓
Issue Key Generation
      ✓
      ↓
Project-scoped Unique Constraint
      ✓
      ↓
develop Integration
      ✓
```

> 현재 구현은 V1 범위의 단순 project-scoped sequence 방식이다. 향후 고동시성 환경에서는 별도 sequence 관리 또는 동시성 제어 전략을 검토할 수 있다.

---

# 34. Next Recommended Branch

## Option A - Issue Frontend

```text
feature/frontend-issue
```

권장 작업:

```text
Issue API Client
    ↓
Issue List
    ↓
Issue Search
    ↓
Issue Detail
    ↓
Issue Registration
    ↓
Issue Edit
    ↓
Issue Status UI
    ↓
Issue Delete
    ↓
Issue Key Display
    ↓
API Integration
    ↓
Browser Verification
    ↓
Production Build
```

## Option B - Schedule Gantt

```text
feature/frontend-schedule-gantt
```

권장 작업:

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
```

## Recommended Priority

최근 Backend 구현 흐름과 Sprint 4 진행 상태를 고려하면 다음 순서를 권장한다.

```text
Issue Frontend
        ↓
Issue API Integration
        ↓
Issue Browser Verification
        ↓
Risk Backend
        ↓
Risk Frontend
        ↓
Change Backend
        ↓
Change Frontend
```

---

# End of Document
