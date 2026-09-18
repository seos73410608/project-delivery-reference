# PMIS Development Roadmap

> **Version:** **1.12**
>
> **Last Updated:** **2026-09-17**
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
- Issue Frontend Management UI
- Risk Management Frontend UI

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

feat: implement risk management UI

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

### Issue Frontend Completed

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
- Loading / Empty / Error State
- Production Build Verification

### Risk Backend

- Risk CRUD
- Risk Assessment
- Probability / Impact
- Risk Score / Level Calculation
- Response Strategy
- Risk Monitoring
- Project-scoped Risk Key Generation
- Risk Search / Filter
- Risk Status Workflow
- Swagger API Verification

### Risk Frontend Completed

- Risk API Client
- Risk List
- Risk Search / Filter
- Risk Detail
- Risk Registration
- Risk Edit
- Risk Status UI
- Risk Delete
- Risk Summary
- Risk Matrix
- Risk API Integration
- Risk Key Display
- Loading / Empty / Error State
- Risk Form CSS
- Production Build Verification

### Risk API

```text
GET    /api/risks/{id}
GET    /api/projects/{projectId}/risks
GET    /api/risks
POST   /api/projects/{projectId}/risks
PUT    /api/risks/{id}
PATCH  /api/risks/{id}/status
DELETE /api/risks/{id}
```

### Risk Matrix

```text
              Impact
            LOW  MED  HIGH  CRIT
Probability
LOW          L    L    M     H
MED          L    M    H     H
HIGH         M    H    H     C
```

Risk Matrix는 Probability × Impact 기반으로 화면에서 계산한다.

Risk Score / Level은 계산 결과이며, V1에서는 별도 DB 저장 없이 조회 및 UI에서 활용한다.

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

│         ├── YES → PRESENT → Verification
│         │                    ├── PENDING
│         │                    ├── VERIFIED
│         │                    └── REJECTED
│         │
│         └── NO → MISSING

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

### Risk

- Risk CRUD
- Project별 Risk 조회
- Risk Search
- Dynamic Search / Filter
- Risk Status Update
- Risk Status Workflow Validation
- Risk Delete
- Risk Score / Level Calculation
- Project-based Risk Key Generation
- Swagger API Verification

### Planned

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

### Issue

- Issue API Client
- Issue List / Search / Detail
- Issue Registration / Edit / Delete
- Issue Status UI
- Issue API Integration
- Issue Key Display

### Risk

- Risk API Client
- Risk List
- Risk Search / Filter
- Risk Detail
- Risk Registration / Edit / Delete
- Risk Status UI
- Risk API Integration
- Risk Key Display
- Risk Summary
- Risk Matrix
- Risk Form CSS
- Loading / Empty / Error State

### Current Integration Verification

- Project API Integration ✓
- Project CRUD Verification ✓
- Schedule API Verification ✓
- Schedule API Integration ✓
- Calendar Integration ✓
- Schedule Production Build ✓
- Issue API Verification ✓
- Issue Frontend Integration ✓
- Risk API Verification ✓
- Risk API Integration ✓
- Risk Frontend Build Verification ✓

### Next

- Issue Browser Verification
- Risk Browser Verification
- Gantt UI
- WBS / Schedule Gantt Integration
- Dashboard API Integration
- Authentication UI
- Change Management

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
    ├── CRUD Service             ✓
    ├── Search                   ✓
    ├── Status Workflow          ✓
    ├── Controller               ✓
    ├── Swagger Verification     ✓
    ├── Issue Key Generation     ✓
    └── Project-scoped Unique Key ✓

                │
                ▼

Git Integration

    ├── Feature Commit            ✓
    ├── Push                      ✓
    ├── Merge to develop          ✓
    └── develop Push              ✓

                │
                ▼

Issue Frontend

    ├── API Client                ✓
    ├── List                      ✓
    ├── Search                    ✓
    ├── Detail                    ✓
    ├── Create                    ✓
    ├── Edit                      ✓
    ├── Status                    ✓
    ├── Delete                    ✓
    ├── API Integration           ✓
    └── Issue Key Display         ✓
```

---

# 18. Current Risk State

```text
Risk Backend

    ├── Entity                   ✓
    ├── Priority / Status        ✓
    ├── Probability / Impact     ✓
    ├── Repository               ✓
    ├── Create / Update DTO      ✓
    ├── Search DTO               ✓
    ├── Status Update DTO        ✓
    ├── Response DTO             ✓
    ├── Specification            ✓
    ├── CRUD Service             ✓
    ├── Search / Filter          ✓
    ├── Risk Score / Level       ✓
    ├── Status Workflow          ✓
    ├── Controller               ✓
    ├── Swagger Verification     ✓
    ├── Risk Key Generation      ✓
    └── Project-scoped Unique Key ✓

                │
                ▼

Risk Frontend

    ├── API Client                ✓
    ├── List                      ✓
    ├── Search / Filter           ✓
    ├── Summary                   ✓
    ├── Matrix                    ✓
    ├── Detail                    ✓
    ├── Create                    ✓
    ├── Edit                      ✓
    ├── Status                    ✓
    ├── Delete                    ✓
    ├── API Integration           ✓
    ├── Risk Key Display          ✓
    ├── Loading / Empty / Error   ✓
    └── Production Build         ✓

                │
                ▼

Git Integration

    ├── Feature Commit            ✓
    ├── Push                      ✓
    ├── Merge to develop          ✓
    └── develop Push              ✓
```

---

# 19. Current Git Development State

## Schedule Calendar Refactoring

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

## Risk Management Frontend Integration

```text
feature/risk-management-ui
        ↓
85e1b01

feat: implement risk management UI

        ↓
Push origin feature/risk-management-ui
        ↓
Fast-forward Merge
        ↓
develop
        ↓
Push origin develop
```

주요 변경:

- Risk Frontend 전체 UI 구현
- Risk API Client 구현
- Risk Type / Response Model 구현
- Risk List / Search / Filter 구현
- Risk Summary 구현
- Risk Matrix 구현
- Risk Detail 구현
- Risk Create / Edit 구현
- Risk Status UI 구현
- Risk Delete 구현
- Risk Key Display 구현
- Risk Form CSS 적용
- `/risk` Router 등록
- Backend `ApiResponse<PageResponse<RiskResponse>>` 응답 구조 연계
- Risk API Integration
- Production Build Verification

---

# 20. Development Process

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
Frontend Issue UI ✓
    ↓
API Integration ✓
    ↓
Browser Verification →
    ↓
E2E Verification →
```

## Risk Development Flow

```text
Risk Design
    ↓
Risk Entity / Enum ✓
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
Risk Score / Level Calculation ✓
    ↓
Project-based Risk Key Generation ✓
    ↓
Swagger API Verification ✓
    ↓
Frontend Risk UI ✓
    ↓
API Integration ✓
    ↓
Production Build ✓
    ↓
Browser Verification →
    ↓
E2E Verification →
```

---

# 21. Documentation Policy

다음 문서는 항상 최신 상태를 유지한다.

- CHANGELOG.md
- DEVELOPMENT_ROADMAP.md
- PROJECT_OVERVIEW.md
- ARCHITECTURE.md
- PORTFOLIO.md

Risk 변경 시 다음 문서를 함께 검토한다.

- RISK_DESIGN.md
- DEVELOPMENT_ROADMAP.md
- CHANGELOG.md
- PROJECT_OVERVIEW.md

문서에는 Backend와 Frontend의 실제 개발 상태 및 Git Integration 상태를 함께 반영한다.

---

# 22. Maintenance Policy

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

# 23. Current Information

| Item | Value |
|---|---|
| Last Updated | **2026-09-17** |
| Roadmap Version | **v1.12** |
| Current Release | **v0.5.3** |
| Current Integration Branch | **develop** |
| Current Sprint | **Sprint 4 - Issue / Risk / Change Management** |
| Last Completed Backend Domain | **Risk Management** |
| Last Completed Backend Feature | **Risk CRUD / Search / Status Workflow / Risk Score / Risk Key Generation / Swagger Verification** |
| Last Completed Frontend Domain | **Risk Management UI** |
| Last Completed Frontend Feature | **Risk UI / API Integration / Matrix / Summary / Form / Status / Delete** |
| Last Integrated Commit | **85e1b01** |
| Development Stage | **Issue + Risk Management develop 통합 완료** |
| Current Next Stage | **Risk Browser Verification / Change Management** |
| Next Backend Domain | **Change Management** |
| Evidence Domain | **Planned - Sprint 5** |
| Maintainer | **Seo Seokhyeon** |

> **Version Note:** `v0.5.3`은 마지막 정식 Release 기준이다.
>
> 현재 `develop`에는 Project Frontend CRUD, WBS Management, Schedule Backend Management, Schedule Frontend Management UI, Schedule Calendar Integration, Calendar Component Refactoring, Issue Management Backend, Project-based Issue Key Generation Hotfix 및 Risk Management Frontend UI가 통합되어 있다.

---

# 24. Next Milestone

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

Issue Frontend                 ✓

Issue API Integration          ✓

Risk Backend                   ✓

Risk Frontend                  ✓

Risk API Integration           ✓

Risk Matrix                    ✓

Risk develop Integration       ✓

Change Backend                 →

Change Frontend                →
```

## Recommended Development Order

```text
Issue Browser Verification
        ↓
Risk Browser Verification
        ↓
Gantt UI
        ↓
WBS / Schedule Gantt Integration
        ↓
Change Backend
        ↓
Change Frontend
        ↓
Evidence Management
        ↓
CMDB
```

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
Calendar Component Separation  ✓
Calendar Utility Separation    ✓
Production Build               ✓
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

> Issue Management, Project-based Issue Key Generation 및 Risk Management Frontend는 현재 `develop`에 통합되어 있으나, 별도 정식 Release Version은 아직 생성하지 않았다.

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
├─ Risk Management               ✓
├─ Risk Swagger Test             ✓
├─ Risk Score / Level            ✓
├─ Risk Key Generation           ✓
├─ Risk develop Integration      ✓
├─ Change                        →
├─ Evidence                      →
├─ CMDB                          →
├─ Dashboard                     →
├─ Report                        →
└─ Spring AI                     →

Frontend

├─ React / Vite / TypeScript     ✓
├─ Router / Layout               ✓
├─ Common UI Components          ✓
├─ Dashboard Reference UI        ✓
├─ Dashboard Components          ✓
├─ Project CRUD Integration      ✓
├─ WBS API Integration           ✓
├─ WBS UI                        ✓
├─ Schedule Management UI        ✓
├─ Schedule API Integration      ✓
├─ Calendar UI                   ✓
├─ Calendar API Integration      ✓
├─ Calendar Component Refactoring ✓
├─ Issue UI                      ✓
├─ Issue API Integration         ✓
├─ Risk UI                       ✓
├─ Risk API Integration          ✓
├─ Risk Summary                  ✓
├─ Risk Matrix                   ✓
├─ Risk Form / Status UI         ✓
├─ Risk Production Build         ✓
└─ Gantt UI                      →

Integration

├─ Project API Integration       ✓
├─ Project CRUD Verification     ✓
├─ WBS API Integration           ✓
├─ WBS Browser Verification      →
├─ WBS Integration Test          →
├─ Schedule API Verification     ✓
├─ Schedule API Integration      ✓
├─ Calendar Integration           ✓
├─ Schedule Production Build      ✓
├─ Schedule Browser Verification  →
├─ Gantt Integration              →
├─ Issue API Verification         ✓
├─ Issue Frontend Integration     ✓
├─ Issue Browser Verification     →
├─ Risk API Verification          ✓
├─ Risk Frontend Integration      ✓
├─ Risk Browser Verification      →
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
Issue Management Backend
    ✓
    ↓
Issue Key Generation
    ✓
    ↓
Issue Frontend
    ✓
    ↓
Issue API Integration
    ✓
    ↓
Risk Management Backend
    ✓
    ↓
Risk Management Frontend
    ✓
    ↓
Risk API Integration
    ✓
    ↓
Risk Matrix / Summary
    ✓
    ↓
Risk develop Integration
    ✓
    ↓
Risk Browser Verification
    →
    ↓
Change Management
    →
    ↓
Evidence Management
    →
```

현재 Sprint 4의 핵심 단계는 **Issue Management + Risk Management 완료 및 `develop` 통합**이다.

다음 단계는 **Risk Browser Verification**을 마친 후 **Change Management**로 확장한다.

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

# 34. Risk Management Integration Record

## Feature Branch

```text
feature/risk-management-ui
```

## Feature Commit

```text
85e1b01

feat: implement risk management UI
```

## Integration

```text
feature/risk-management-ui
        ↓
Push origin feature/risk-management-ui
        ↓
git checkout develop
        ↓
git pull origin develop
        ↓
git merge feature/risk-management-ui
        ↓
Fast-forward Merge
        ↓
git push origin develop
```

## Implemented Frontend Files

```text
risk/

├── RiskPage.tsx
│
├── api/
│   └── riskApi.ts
│
├── components/
│   ├── RiskDetail.tsx
│   ├── RiskForm.tsx
│   ├── RiskList.tsx
│   ├── RiskMatrix.tsx
│   ├── RiskRow.tsx
│   ├── RiskStatusForm.tsx
│   ├── RiskSummary.tsx
│   └── RiskToolbar.tsx
│
├── styles/
│   └── Risk.css
│
└── types/
    └── risk.ts
```

## Implemented Risk Frontend Features

```text
Risk List
    ✓

Risk Search / Filter
    ✓

Risk Summary
    ✓

Risk Matrix
    ✓

Risk Detail
    ✓

Risk Create
    ✓

Risk Edit
    ✓

Risk Status
    ✓

Risk Delete
    ✓

Risk Key Display
    ✓

Risk API Integration
    ✓

Loading / Empty / Error State
    ✓

Production Build Verification
    ✓
```

## Risk API Integration

Backend Response:

```text
ApiResponse<PageResponse<RiskResponse>>
```

Project Risk List:

```text
GET /api/projects/{projectId}/risks
```

Risk Search:

```text
GET /api/risks
```

Risk Detail:

```text
GET /api/risks/{id}
```

Risk Create:

```text
POST /api/projects/{projectId}/risks
```

Risk Update:

```text
PUT /api/risks/{id}
```

Risk Status:

```text
PATCH /api/risks/{id}/status
```

Risk Delete:

```text
DELETE /api/risks/{id}
```

## Risk Matrix

```text
              Impact
            LOW  MED  HIGH  CRIT
Probability
LOW          L    L    M     H
MED          L    M    H     H
HIGH         M    H    H     C
```

Risk Matrix는 Probability × Impact 기반으로 계산하며 V1에서는 결과를 DB에 별도 저장하지 않는다.

## Risk Key Policy

- Risk Key는 Backend에서 생성한다.
- Create Request에서는 Risk Key를 입력하지 않는다.
- Update 시 Risk Key는 변경하지 않는다.
- Frontend는 Risk Key를 표시만 한다.
- Risk Key는 Project 단위로 관리한다.
- Project별 Risk sequence를 사용한다.

## Current Status

```text
Risk Frontend
      ✓
      ↓
Risk API Integration
      ✓
      ↓
Risk Matrix / Summary
      ✓
      ↓
Production Build
      ✓
      ↓
Git Integration
      ✓
      ↓
develop
      ✓
      ↓
Browser Verification
      →
```

---

# 35. Next Recommended Branch

## Option A - Risk Browser Verification

```text
feature/frontend-risk
```

권장 작업:

```text
Risk Browser Verification
        ↓
Risk API / UI Integration Verification
        ↓
Risk Create / Edit Verification
        ↓
Risk Status Verification
        ↓
Risk Delete Verification
        ↓
Risk Matrix Verification
        ↓
Production Build
        ↓
Integration Test
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

## Option C - Change Management Backend

```text
feature/change-management
```

권장 작업:

```text
Change Design
        ↓
Change Entity / Enum
        ↓
Repository
        ↓
DTO
        ↓
Specification
        ↓
Service
        ↓
Controller
        ↓
Swagger API Verification
        ↓
Frontend Change UI
```

## Recommended Priority

```text
Risk Browser Verification
        ↓
Risk Integration Test
        ↓
Schedule Gantt
        ↓
Change Backend
        ↓
Change Frontend
        ↓
Evidence Management
        ↓
CMDB
        ↓
Dashboard / Report
        ↓
Spring AI
```

---

# End of Document
