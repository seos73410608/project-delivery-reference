# PMIS Development Roadmap

> **Version:** **1.4**  
> **Last Updated:** **2026-08-11**  
> **Project:** **Project Management Information System (PMIS)**

**Architecture:**
- Backend: Spring Boot 4.1.0, Spring Security, JWT, MariaDB
- Frontend: React, Vite, TypeScript, React Router DOM
- AI: Spring AI (planned)

**Development Methodology:** Agile Scrum + Git Flow

---

# 1. Development Goal

본 프로젝트는 단순 CRUD 프로젝트가 아니라 실제 SI 프로젝트에서 사용하는 **Project Management Information System(PMIS)**을 구현하는 것을 목표로 한다.

구현 대상:
- 사용자/권한 관리
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
Pull Request
    ↓
Merge to develop
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
| --- | --- |
| Evidence Required | 증적 필수 여부 |
| Evidence Type | 증적 유형 |
| Evidence | 실제 증적 파일/문서 |
| Evidence Status | 존재/누락/검토중/불필요 |
| Verification Status | 검토 여부 |
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
│ Change / CMDB / Evidence / Report           │
└──────────────────────┬──────────────────────┘
                       │ REST API
                       ▼
┌─────────────────────────────────────────────┐
│                PMIS Backend                 │
│ Spring Boot + Spring Security + JWT         │
│ Controller / Service / Repository            │
│ DTO / Mapper / Specification                 │
│ Evidence / File / Verification               │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────┐
│                  MariaDB                    │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
                File / Object Storage

                    Planned
                       │
                       ▼
┌─────────────────────────────────────────────┐
│                 Spring AI                   │
│ AI Assistant / Analysis / RAG               │
└─────────────────────────────────────────────┘
```

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

모든 기능은 Feature Branch에서 개발 후 Develop으로 Merge한다. 검증 완료 후 Main으로 Release 한다.

## Branch Strategy

### Backend

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

### Frontend

```text
feature/frontend-layout
feature/frontend-components
feature/frontend-dashboard
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

Feature Branch 단위의 개발 완료와 Release Version은 별도로 관리한다.

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
feat: implement project search
feat: implement project dashboard api
feat: separate project detail api
feat: implement evidence management
feat(frontend): implement React router
feat(frontend): add common UI components
feat(frontend): add HMC reference dashboard layout
feat(frontend): add evidence checklist
fix: resolve JWT validation issue
docs: update architecture documentation
refactor: improve exception handling
build: upgrade Spring Boot
```

---

# 7. Sprint Plan

# Sprint 0

## Base Project

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
- Frontend Project Planning
- React / Vite / TypeScript 선정
- Backend / Frontend 프로젝트 분리 계획
- Frontend Directory Architecture 설계

### Deliverables
- Base Project
- Backend Build Success
- Frontend Project Structure
- Git Repository

---

# Sprint 1

## Authentication & Security

### Goal
JWT 기반 인증/인가 구축

### Backend Feature Branch
```text
feature/auth-jwt
feature/auth-login
feature/auth-refresh
feature/auth-role
```

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

### Deliverables
Backend:
- JWT Authentication
- Role Authorization
- Security Framework

Frontend:
- Authentication UI
- Protected Route

---

# Sprint 2

## Project Management & Frontend Foundation

### Goal
Project Management Backend을 구축하고 PMIS Frontend의 기본 화면 구조를 완성한다.

### Backend Feature Branch
```text
feature/project-crud
feature/project-search
feature/project-dashboard
feature/project-detail
```

### Backend Tasks
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
│
├── ProjectController
├── ProjectDashboardController
└── ProjectDetailController
```

### Frontend Feature Branch
```text
feature/frontend-layout
feature/frontend-components
feature/frontend-dashboard
```

### Frontend Foundation
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

### Dashboard Reference & Component Architecture

HMC(Hardware Management Console) 스타일의 운영 Dashboard를 Reference로 활용하여 PMIS Dashboard의 정보 배치와 시각적 구조를 설계한다.

```text
DashboardPage
│
├── ProjectOverview
├── KPI
│   └── KpiCard
├── WbsProgress
├── ScheduleSummary
├── IssueSummary
└── RecentActivity
```

구현된 Dashboard Component:

```text
pmis-frontend/src/components/dashboard/
├── KpiCard.tsx
├── ProjectOverview.tsx
├── WbsProgress.tsx
├── ScheduleSummary.tsx
├── IssueSummary.tsx
└── RecentActivity.tsx
```

현재 Dashboard 데이터는 정적 Reference Data이며 향후 Project Dashboard, WBS, Schedule, Issue/Risk API와 연계한다.

### Deliverables
Backend:
- Project CRUD API
- Project Search API
- Project Dashboard API
- Project Detail API
- Project Controller 분리
- Project Dashboard Controller 분리
- Project Detail Controller 분리

Frontend:
- PMIS Application Layout
- Dashboard UI Skeleton
- Dashboard Component Architecture
- Header / Sidebar / Breadcrumb
- Common UI Components
- HMC Reference Dashboard Layout

---

# Sprint 3

## WBS & Schedule

### Goal
프로젝트 일정과 작업 구조를 관리하는 핵심 기능을 Backend와 Frontend에서 구현한다.

### Backend Feature Branch
```text
feature/wbs-management
feature/schedule-management
```

### Frontend Feature Branch
```text
feature/frontend-project
feature/frontend-wbs
feature/frontend-schedule
```

### Backend Tasks
WBS:
- WBS CRUD
- Tree Structure
- Progress Calculation
- Task Relationship

Schedule:
- Schedule CRUD
- Calendar API
- Gantt Data
- Milestone

### Frontend Tasks
Project:
- Project List
- Project Search
- Project Detail
- Project Dashboard
- Project API Integration

WBS:
- WBS Tree
- WBS Detail
- Progress Display
- WBS API Integration

Schedule:
- Calendar UI
- Gantt UI
- Milestone UI
- Schedule Detail
- Schedule API Integration

### Deliverables
- WBS API
- Schedule API
- Project Management UI
- Project Detail UI
- Project Dashboard UI
- WBS UI
- Schedule UI

---

# Sprint 4

## Issue / Risk / Change Management

### Goal
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

### Backend Tasks
Issue:
- Issue CRUD
- Assignment
- Status Workflow
- Priority
- Attachment

Risk:
- Risk CRUD
- Risk Assessment
- Probability / Impact
- Response Strategy
- Risk Monitoring

Change:
- Change Request
- Approval
- History
- Impact Analysis

### Frontend Tasks
Issue:
- Issue List
- Issue Detail
- Issue Registration
- Assignment
- Status Management

Risk:
- Risk List
- Risk Detail
- Risk Matrix
- Response Strategy

Change:
- Change Request
- Approval UI
- Change History
- Impact Analysis

### Deliverables
- Issue Module
- Risk Module
- Change Module
- Integrated PMIS UI

---

# Sprint 5

## Evidence & Inspection Management

### Goal
프로젝트 수행 과정에서 발생하는 산출물과 증적을 체계적으로 관리하고, 증적의 존재 여부 및 검수 상태를 확인할 수 있는 기능을 구현한다.

### Backend Feature Branch
```text
feature/evidence-management
```

### Frontend Feature Branch
```text
feature/frontend-evidence
```

## Evidence Management Concept

```text
Project
   ↓
WBS / Task
   ↓
Activity / Deliverable
   ↓
Evidence Requirement
   ↓
Evidence
   ↓
Verification
   ↓
Inspection
```

증적은 프로젝트 활동 또는 검수 대상이 실제로 수행되었음을 확인할 수 있는 파일, 문서, 화면 캡처, 로그, 결과서 등의 근거 자료를 의미한다.

## Evidence Status

```text
Evidence Item
     ↓
Required?
 ┌───┴────┐
YES       NO
 │         └── NOT_REQUIRED
 ▼
Evidence Exists?
 ┌───┴────┐
YES       NO
 │         └── MISSING
 ▼
PRESENT
 │
 ▼
Verification
 ├── PENDING
 ├── VERIFIED
 └── REJECTED
```

## Evidence Checklist

| Field | Description |
| --- | --- |
| Requirement ID | 증적 요구사항 ID |
| Project | 프로젝트 |
| WBS | 관련 WBS |
| Activity | 관련 업무 |
| Deliverable | 관련 산출물 |
| Evidence Required | 증적 필수 여부 |
| Evidence Type | 파일/문서/화면/로그/결과서 등 |
| Evidence Status | PRESENT / MISSING / PENDING / NOT_REQUIRED |
| Verification Status | PENDING / VERIFIED / REJECTED |
| Evidence File | 실제 증적 |
| Reviewer | 검토자 |
| Verified Date | 검토일 |
| Due Date | 제출 기한 |
| Remark | 비고 |

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

## Evidence Management UI

```text
Evidence Management
│
├── Evidence Dashboard
│   ├── Total
│   ├── Present
│   ├── Missing
│   ├── Pending
│   ├── Verification Rate
│   └── Evidence Completion Rate
│
├── Evidence Checklist
│   ├── Filter
│   ├── Search
│   ├── Status
│   ├── Due Date
│   └── Missing Only
│
├── Evidence Detail
│   ├── Requirement
│   ├── Related WBS
│   ├── Related Activity
│   ├── Evidence File
│   ├── Verification History
│   └── Comment
│
└── Evidence Registration
    ├── File Upload
    ├── Evidence Type
    ├── Description
    └── Submit Verification
```

## Evidence Verification Workflow

```text
Evidence Requirement
        ↓
Evidence Registration
        ↓
Submitted
        ↓
Reviewer Check
        ↓
 ┌──────┴──────┐
 │             │
Approved     Rejected
 │             │
 ▼             ▼
VERIFIED     Re-registration
```

## Evidence Data Model Direction

```text
EvidenceRequirement
Evidence
EvidenceVerification
EvidenceHistory
File
```

### Deliverables

Backend:
- Evidence Requirement API
- Evidence CRUD API
- Evidence File API
- Evidence Verification API
- Evidence History API
- Evidence Completion Statistics API

Frontend:
- Evidence Dashboard
- Evidence Checklist
- Missing Evidence Filter
- Evidence Detail
- Evidence Registration
- Evidence Verification UI

---

# Sprint 6

## CMDB

### Goal
프로젝트 인프라와 구성 정보를 관리하는 CMDB를 구현한다.

### Backend Feature Branch
```text
feature/cmdb-management
```

### Frontend Feature Branch
```text
feature/frontend-cmdb
```

### Backend Tasks
- Server CI
- Software CI
- Database CI
- Relationship
- Version History
- Change Tracking

### Frontend Tasks
- CI List
- CI Detail
- CI Registration
- Relationship View
- Configuration History
- Change History

### Deliverables
- CMDB API
- CMDB UI
- Configuration Management

---

# Sprint 7

## Dashboard & Report

### Goal
PMO 업무에 필요한 통합 Dashboard와 Report 기능을 구현한다.

### Backend Feature Branch
```text
feature/dashboard
feature/report-excel
feature/report-pdf
```

### Frontend Feature Branch
```text
feature/frontend-dashboard
feature/frontend-report
```

### Backend Tasks

Dashboard:
- Project Dashboard
- WBS Dashboard
- Schedule Dashboard
- Issue Dashboard
- Risk Dashboard
- Change Dashboard
- Evidence Dashboard
- CMDB Dashboard

Report:
- Weekly Report
- Monthly Report
- Executive Report
- Evidence Status Report
- Evidence Missing Report
- Excel Export
- PDF Export
- Statistics API

### Frontend Tasks

Dashboard:
- Executive Dashboard
- Project Dashboard
- Schedule Dashboard
- Issue Dashboard
- Risk Dashboard
- Change Dashboard
- Evidence Dashboard
- CMDB Dashboard
- Dashboard Widgets
- Dashboard API Integration

Report:
- Weekly Report UI
- Monthly Report UI
- Executive Report UI
- Evidence Status Report
- Excel Download
- PDF Download
- Statistics Visualization

> **Current Note:** Sprint 2에서 HMC Reference 기반 Dashboard UI Skeleton을 선행 구현했다. Sprint 7에서는 이를 실제 Project/WBS/Schedule/Issue/Risk/Change/Evidence/CMDB API 기반 통합 Dashboard로 확장한다.

---

# Sprint 8

## Spring AI

### Goal
PMIS 데이터를 활용하는 AI Assistant를 구현한다.

### Backend Feature Branch
```text
feature/spring-ai
feature/ai-risk-analysis
feature/ai-report-summary
```

### Frontend Feature Branch
```text
feature/frontend-ai
```

### Backend Tasks
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

### Frontend Tasks
- AI Assistant UI
- Chat Interface
- AI Project Summary
- AI Risk Analysis
- AI Evidence Gap Analysis
- AI Report Summary
- AI Schedule Analysis
- AI Meeting Summary
- AI Recommendation Display

### Deliverables
- AI Chat API
- AI Assistant UI
- PMIS AI Assistant

---

# Sprint 9

## Finalization

### Goal
Backend와 Frontend를 통합하여 포트폴리오 수준의 완성도를 확보한다.

### Backend
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

### Deliverables
- Production Ready PMIS
- Full Stack PMIS
- Portfolio Repository

---

# 8. Milestones

| Milestone | Description |
| --- | --- |
| M1 | Base Project Complete |
| M2 | Authentication Complete |
| M3 | Project Management Complete |
| M4 | Frontend Foundation Complete |
| M5 | WBS & Schedule Complete |
| M6 | Issue/Risk/Change Complete |
| M7 | Evidence & Inspection Complete |
| M8 | CMDB Complete |
| M9 | Dashboard & Report Complete |
| M10 | Spring AI Complete |
| M11 | Production Ready |
| M12 | Portfolio Release |

---

# 9. Estimated Schedule

| Sprint | Duration |
| --- | ---: |
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

**Estimated Total: 약 18~20주**

Frontend와 Backend는 동일 Sprint에서 병렬 개발할 수 있으며, API가 준비된 기능부터 Frontend Integration을 진행한다.

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
- Evidence Checklist UX

## Integration
- REST API Contract
- API Error Handling
- Authentication Integration
- Loading State
- Empty State
- Error State
- E2E Verification
- Evidence Workflow Verification

## Development Standards
- Git Flow
- Semantic Versioning
- Conventional Commit
- Pull Request
- Code Review
- Unit Test
- Integration Test
- E2E Test

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
- Project Detail UI
- WBS UI
- Schedule UI
- Issue UI
- Risk UI
- Change UI
- Evidence Dashboard UI
- Evidence Checklist UI
- Evidence Detail UI
- Evidence Verification UI
- CMDB UI
- Dashboard UI
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

프로젝트 완료 시 다음 목표를 만족한다.

- 실제 SI 프로젝트 수준의 PMIS 구현
- Spring Boot 기반 엔터프라이즈 Backend 구현
- React 기반 PMIS Frontend 구현
- Backend / Frontend REST API Integration
- JWT 기반 인증/인가 구현
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
- Project CRUD
- Project Search
- Project Dashboard API
- Project Dashboard KPI
- Project Detail API
- Project Controller Separation
- Project Dashboard Controller Separation
- Project Detail Controller Separation

### In Progress
- WBS
- Schedule

### Planned
- Issue
- Risk
- Change
- Evidence
- Inspection
- CMDB
- Report
- Spring AI

## Frontend

### Completed
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
- Vite Sample Removal
- TypeScript / Vite Alias Configuration
- HMC Reference 기반 Dashboard UI
- Dashboard Component 분리
- Dashboard Production Build Verification

### Dashboard Component Structure

```text
pmis-frontend/src/components/dashboard/
├── KpiCard.tsx
├── ProjectOverview.tsx
├── WbsProgress.tsx
├── ScheduleSummary.tsx
├── IssueSummary.tsx
└── RecentActivity.tsx
```

### Current Dashboard State

현재 Dashboard는 HMC(Hardware Management Console) Reference를 기반으로 PMIS 운영 Dashboard의 정보 구조와 화면 구성을 선행 구현한 상태이다.

현재 데이터는 정적 Reference Data이며 Backend API와의 연동은 아직 진행하지 않았다.

향후 다음 API와 연계한다.

```text
Project Dashboard API
        ↓
ProjectOverview / KPI

WBS API
        ↓
WbsProgress

Schedule API
        ↓
ScheduleSummary

Issue / Risk API
        ↓
IssueSummary

Activity / Project History API
        ↓
RecentActivity

Evidence API
        ↓
Evidence Status / Missing Evidence KPI
```

### Frontend Next
- Project Management UI
- Project Detail UI
- Project API Integration
- Authentication UI
- WBS UI
- Schedule UI
- Dashboard API Integration
- Loading / Empty / Error State
- API Error Handling

---

# 15. Current Git Development State

## Backend

```text
feature/project-crud
        ↓
develop
        ↓
feature/project-search
        ↓
develop
        ↓
feature/project-dashboard
        ↓
develop
        ↓
feature/project-detail
        ↓
develop
```

현재 Project Detail API 개발까지 완료되어 `feature/project-detail`의 변경 사항은 `develop`에 Merge된 상태를 기준으로 한다.

## Frontend

```text
feature/frontend-layout
        ↓
develop
        ↓
feature/frontend-components
        ↓
develop
        ↓
feature/frontend-dashboard-hmc-reference
        ↓
develop
```

현재 `feature/frontend-dashboard-hmc-reference`의 변경 사항은 `develop`에 Fast-forward Merge 및 Push가 완료된 상태이다.

---

# 16. Development Process

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
Pull Request / Merge Review
↓
Merge to develop
↓
Integration Test
↓
Release
```

Backend와 Frontend가 함께 필요한 기능:

```text
Backend API
↓
API Test
↓
Frontend UI
↓
API Integration
↓
Browser Test
↓
E2E Verification
```

Evidence 기능:

```text
Evidence Requirement
↓
Evidence Registration
↓
File Upload
↓
Evidence Status Check
↓
Reviewer Verification
↓
Approval / Rejection
↓
Evidence History
↓
Dashboard / Report
```

---

# 17. Git Branch Strategy

```text
main
    │
develop
    │
    ├── feature/*
    ├── release/*
    └── hotfix/*
```

Release는 반드시 `develop` 브랜치에서 충분히 검증한 후 `main`으로 병합한다.

Frontend와 Backend는 동일한 Git Repository에서 관리하되, 기능 단위 Feature Branch를 사용한다.

---

# 18. Documentation Policy

다음 문서는 항상 최신 상태를 유지한다.

- CHANGELOG.md
- DEVELOPMENT_ROADMAP.md
- PROJECT_OVERVIEW.md
- ARCHITECTURE.md

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
- 001_frontend_architecture.md
- 002_frontend_folder_structure.md
- 003_frontend_coding_convention.md
- 004_frontend_ui_design.md
- 005_frontend_component_architecture.md
- 006_frontend_development_history.md

소스코드 변경이 발생하면 관련 문서를 함께 현행화한다.

---

# 19. Maintenance Policy

- 모든 기능은 Feature Branch에서 개발한다.
- 모든 변경 사항은 CHANGELOG에 기록한다.
- Backend와 Frontend의 개발 상태를 Roadmap에 반영한다.
- 문서와 소스코드는 항상 동일한 상태를 유지한다.
- Pull Request 검토 후 Develop 브랜치에 병합한다.
- Main 브랜치에는 검증된 코드만 Release한다.
- Semantic Versioning을 준수한다.
- Release마다 Git Tag를 생성한다.
- 증적 관련 변경은 Evidence History에 기록한다.
- 필수 증적의 누락 상태를 프로젝트 Dashboard에서 추적한다.
- 검수 결과와 증적 상태를 Report에 반영한다.

---

# 20. Current Information

| Item | Value |
| --- | --- |
| Last Updated | 2026-08-11 |
| Roadmap Version | **v1.4** |
| Current Release | **v0.5.1** |
| Current Integration Branch | **develop** |
| Last Completed Backend Feature Branch | **feature/project-detail** |
| Last Completed Frontend Feature Branch | **feature/frontend-dashboard-hmc-reference** |
| Current Sprint | **Sprint 2 - Project Management & Frontend Foundation** |
| Development Stage | **Project Management Backend Complete / Frontend Dashboard Reference UI Complete / API Integration Preparation** |
| Next Domain | **WBS & Schedule** |
| Evidence Domain | **Planned - Sprint 5** |
| Maintainer | **Seo Seokhyeon** |

---

# 21. Next Milestone

## Sprint 3

### Backend
- WBS CRUD
- WBS Tree Structure
- Progress Calculation
- Schedule CRUD
- Calendar API
- Gantt Data
- Milestone

### Frontend
- Project List
- Project Search
- Project Detail
- Project Dashboard UI
- Project API Integration
- WBS UI
- Schedule UI
- Calendar UI
- Gantt UI

### Integration
- Project API Integration
- Project Dashboard API Integration
- Project Detail API Integration
- WBS API Integration
- Schedule API Integration
- Loading / Empty / Error State
- API Error Handling

### Recommended Development Order

```text
Project API Verification
        ↓
Project List / Search UI
        ↓
Project Detail UI
        ↓
Project Dashboard API Integration
        ↓
WBS API
        ↓
WBS UI
        ↓
Schedule API
        ↓
Schedule UI
        ↓
Dashboard Data Integration
```

Evidence 기능은 Sprint 5에서 별도 Domain으로 구현하되, Sprint 3~4에서 Project/WBS/Issue/Change와의 연계 관계를 고려하여 데이터 모델을 설계한다.

---

# 22. Release Summary

| Version | Description |
| --- | --- |
| v0.1.0 | Project Initialization |
| v0.2.0 | Common Infrastructure |
| v0.3.0 | Security Foundation |
| v0.3.1 | Authentication & Authorization |
| v0.3.2 | Swagger / OpenAPI |
| v0.4.0 | Project Management |
| v0.5.0 | Frontend Foundation |
| v0.5.1 | Frontend Layout & Common Components |
| v0.6.x | WBS & Schedule |
| v0.7.x | Issue / Risk / Change |
| v0.8.x | Evidence & Inspection |
| v0.9.x | CMDB |
| v0.10.x | Dashboard & Reporting |
| v1.0.0 | AI Powered PMIS |

> **Note:** HMC Reference 기반 Dashboard UI는 현재 `v0.5.1` 개발 계열에서 Frontend Foundation 확장 작업으로 선행 구현되었으며, 실제 Dashboard API 및 통합 Dashboard 기능은 향후 `v0.10.x`에서 완성한다.

---

# 23. Long-term Roadmap

| Version | Goal |
| --- | --- |
| 1.x | PMIS Core Platform |
| 2.x | Enterprise PMIS |
| 3.x | AI Native PMIS Platform |

---

# 24. Evidence Management Long-term Direction

증적 관리 기능은 단순 파일 관리에서 시작하여 프로젝트 품질 및 감사 추적 체계로 확장한다.

## Phase 1 - Evidence Checklist
- 증적 요구사항 정의
- 필수/선택 증적 구분
- 증적 존재 여부 확인
- 누락 증적 조회
- 증적 확보율 Dashboard

## Phase 2 - Evidence Verification
- 검수자 지정
- 검수 상태 관리
- 승인/반려
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

이를 통해 PMIS는 단순한 프로젝트 일정 관리 시스템이 아니라 **업무 수행 → 산출물 → 증적 → 검수 → 보고**의 전체 추적성을 제공하는 PMO 관리 시스템으로 확장한다.

## Phase 4 - AI Evidence Assistant

향후 Spring AI를 적용하여 다음 기능을 제공한다.

- 필수 증적 누락 탐지
- WBS 대비 증적 미등록 탐지
- 일정 대비 증적 지연 탐지
- 검수 반려 증적 분석
- 반복적인 증적 누락 패턴 분석
- 증적 제출 우선순위 추천
- 주간 증적 상태 요약
- 증적 관련 질의응답

---

# End of Document
