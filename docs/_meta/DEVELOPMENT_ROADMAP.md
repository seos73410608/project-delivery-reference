# PMIS Development Roadmap

> Version: **1.2**
>
> Last Updated: **2026-08-10**
>
> Project: **Project Management Information System (PMIS)**
>
> Architecture:
> - Backend: Spring Boot 4.1.0, Spring Security, JWT, MariaDB
> - Frontend: React, Vite, TypeScript, React Router DOM
> - AI: Spring AI (planned)
>
> Development Methodology:
> Agile Scrum + Git Flow

---

# 1. Development Goal

본 프로젝트는 단순 CRUD 프로젝트가 아니라 실제 SI 프로젝트에서 사용하는
Project Management Information System(PMIS)를 구현하는 것을 목표로 한다.

구현 대상은 다음과 같다.

- 사용자/권한 관리
- 프로젝트 관리
- WBS 관리
- 일정 관리
- 이슈 관리
- 리스크 관리
- 변경 관리
- CMDB
- Dashboard
- Report
- Spring AI Assistant

Backend API와 Frontend UI를 연계하여 실제 프로젝트 관리 업무를 지원할 수 있는
Full Stack PMIS를 구현하는 것을 목표로 한다.

실제 운영 가능한 수준의 아키텍처와 품질을 목표로 개발한다.

---

# 2. System Development Strategy

## Full Stack Development Flow

기능 개발은 Backend와 Frontend를 독립적으로 개발하되,
최종적으로 API와 UI를 통합하여 하나의 기능 단위로 완성한다.

```text
Requirement
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

## Architecture

```text
┌─────────────────────────────────────────────┐
│                PMIS Frontend                │
│ React + TypeScript + Vite                   │
│ React Router DOM                            │
│ Layout / Components / Pages / Features      │
└──────────────────────┬──────────────────────┘
                       │ REST API
                       ▼
┌─────────────────────────────────────────────┐
│                PMIS Backend                 │
│ Spring Boot + Spring Security + JWT         │
│ REST API / Service / Repository              │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────┐
│                  MariaDB                    │
└─────────────────────────────────────────────┘

                    Planned
                       │
                       ▼
┌─────────────────────────────────────────────┐
│                 Spring AI                  │
│ AI Assistant / Analysis / RAG               │
└─────────────────────────────────────────────┘
```

---

# 3. Git Flow

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

모든 기능은 Feature Branch에서 개발 후 Develop으로 Merge한다.

검증 완료 후 Main으로 Release 한다.

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
feature/wbs-management
feature/schedule-management
feature/issue-management
feature/risk-management
feature/change-management
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
feature/frontend-cmdb
feature/frontend-report
feature/frontend-auth
```

---

# 4. Version Strategy

PMIS는 Semantic Versioning(SemVer)을 따른다.

```text
MAJOR.MINOR.PATCH
```

예:

```text
1.0.0
│ │ └─ Patch
│ └──── Minor
└────── Major
```

- **Major**: 호환되지 않는 변경
- **Minor**: 새로운 기능 추가
- **Patch**: 버그 수정 및 개선

---

# 5. Commit Convention

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
feat(frontend): implement React router
feat(frontend): add common UI components
fix: resolve JWT validation issue
docs: update architecture documentation
refactor: improve exception handling
build: upgrade Spring Boot
```

---

# 6. Sprint Plan

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
```

### Backend Tasks

- Project CRUD
- Customer
- PM
- Status
- Project Search
- Sorting
- Validation
- Project Dashboard API

### Frontend Feature Branch

```text
feature/frontend-layout
feature/frontend-components
```

### Frontend Foundation

- React
- Vite
- TypeScript
- npm
- React Router DOM
- Feature-based Directory Structure
- API / Service / Store 구조 준비

### Frontend Layout

- AppRouter
- MainLayout
- Header
- Sidebar
- Breadcrumb
- Dashboard Page

### Common UI Components

- Button
- Card
- Loading
- EmptyState

### Deliverables

Backend:

- Project API
- Project Search API
- Project Dashboard API

Frontend:

- PMIS Application Layout
- Dashboard Skeleton
- Header
- Sidebar
- Breadcrumb
- Common UI Components

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

WBS:

- WBS Tree
- WBS Detail
- Progress Display

Schedule:

- Calendar UI
- Gantt UI
- Milestone UI
- Schedule Detail

### Deliverables

- WBS API
- Schedule API
- Project Management UI
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

# Sprint 6

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
- CMDB Dashboard

Report:

- Weekly Report
- Monthly Report
- Executive Report
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
- CMDB Dashboard
- Dashboard Widgets

Report:

- Weekly Report UI
- Monthly Report UI
- Executive Report UI
- Excel Download
- PDF Download
- Statistics Visualization

### Deliverables

- Integrated Dashboard
- Report System
- Excel Export
- PDF Export

---

# Sprint 7

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
- Report Summary
- Meeting Summary
- Action Item Generation
- Change Impact Analysis

### Frontend Tasks

- AI Assistant UI
- Chat Interface
- AI Project Summary
- AI Risk Analysis
- AI Report Summary
- AI Schedule Analysis
- AI Meeting Summary
- AI Recommendation Display

### Deliverables

- AI Chat API
- AI Assistant UI
- PMIS AI Assistant

---

# Sprint 8

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
- README
- Architecture Documentation
- Portfolio Documentation

### Deliverables

- Production Ready PMIS
- Full Stack PMIS
- Portfolio Repository

---

# 7. Milestones

| Milestone | Description |
|---|---|
| M1 | Base Project Complete |
| M2 | Authentication Complete |
| M3 | Project Management Complete |
| M4 | Frontend Foundation Complete |
| M5 | WBS & Schedule Complete |
| M6 | Issue/Risk/Change Complete |
| M7 | CMDB Complete |
| M8 | Dashboard & Report Complete |
| M9 | Spring AI Complete |
| M10 | Production Ready |
| M11 | Portfolio Release |

---

# 8. Estimated Schedule

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
| Sprint 8 | 1 Week |

**Estimated Total: 약 16~18주**

Frontend와 Backend는 동일 Sprint에서 병렬 개발할 수 있으며,
API가 준비된 기능부터 Frontend Integration을 진행한다.

---

# 9. Quality Objectives

## Backend Architecture

- Layered Architecture
- Clean Architecture
- SOLID Principles
- RESTful API
- DTO Pattern
- Mapper Pattern
- Specification Pattern

## Frontend Architecture

- Component-based Architecture
- Feature-based Directory Structure
- Layout Architecture
- Reusable Common Components
- API / Service Separation
- State Management
- TypeScript Type Safety
- Responsive UI

## Integration

- REST API Contract
- API Error Handling
- Authentication Integration
- Loading State
- Empty State
- Error State
- E2E Verification

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

# 10. Documentation

## Project Documentation

- PROJECT_OVERVIEW.md
- DEVELOPMENT_ROADMAP.md
- ARCHITECTURE.md
- CHANGELOG.md
- PORTFOLIO.md

## Frontend Documentation

- 001_frontend_architecture.md
- 002_frontend_folder_structure.md
- 003_frontend_coding_convention.md
- 004_frontend_ui_design.md
- 005_frontend_component_architecture.md
- 006_frontend_development_history.md

문서와 소스코드는 항상 동일한 상태를 유지한다.

---

# 11. Final Deliverables

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
- Issue UI
- Risk UI
- Change UI
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

# 12. Success Criteria

프로젝트 완료 시 다음 목표를 만족한다.

- 실제 SI 프로젝트 수준의 PMIS 구현
- Spring Boot 기반 엔터프라이즈 Backend 구현
- React 기반 PMIS Frontend 구현
- Backend / Frontend REST API Integration
- JWT 기반 인증/인가 구현
- Spring AI 기반 AI Assistant 제공
- Docker 기반 배포 환경 구축
- Git Flow 기반 개발 프로세스 적용
- 운영 가능한 Full Stack PMIS 완성
- 포트폴리오 및 기술 시연 가능한 수준의 결과물 확보

---

# 13. Current Project Status

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

### In Progress

- WBS
- Schedule

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

### Next

- Dashboard UI Enhancement
- Project Management UI
- API Integration
- Authentication UI
- WBS UI
- Schedule UI

---

# 14. Current Git Development State

현재 Frontend Components 개발은 다음 흐름으로 완료되었다.

```text
feature/frontend-layout
        ↓
develop
        ↓
feature/frontend-components
        ↓
develop
```

주요 Frontend Components Commit:

```text
feat(frontend): implement application layout, routing and project documentation
feat(frontend): add sidebar component
feat(frontend): add common UI components
doc: updated.
```

현재 `feature/frontend-components`의 변경 사항은 `develop`에 Fast-forward Merge되어
원격 저장소에 반영된 상태를 기준으로 한다.

---

# 15. Development Process

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
Pull Request / Merge Review
↓
Merge to develop
↓
Integration Test
↓
Release
```

Backend와 Frontend가 함께 필요한 기능은 다음 순서로 검증한다.

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

---

# 16. Git Branch Strategy

```text
main
    │
develop
    │
    ├── feature/*
    ├── release/*
    └── hotfix/*
```

Release는 반드시 `develop` 브랜치에서 충분히 검증한 후
`main`으로 병합한다.

Frontend와 Backend는 동일한 Git Repository에서 관리하되,
기능 단위 Feature Branch를 사용한다.

---

# 17. Documentation Policy

다음 문서는 항상 최신 상태를 유지한다.

- CHANGELOG.md
- DEVELOPMENT_ROADMAP.md
- PROJECT_OVERVIEW.md
- ARCHITECTURE.md

Frontend 문서:

- 001_frontend_architecture.md
- 002_frontend_folder_structure.md
- 003_frontend_coding_convention.md
- 004_frontend_ui_design.md
- 005_frontend_component_architecture.md
- 006_frontend_development_history.md

소스코드 변경이 발생하면 관련 문서를 함께 현행화한다.

---

# 18. Maintenance Policy

- 모든 기능은 Feature Branch에서 개발한다.
- 모든 변경 사항은 CHANGELOG에 기록한다.
- Backend와 Frontend의 개발 상태를 Roadmap에 반영한다.
- 문서와 소스코드는 항상 동일한 상태를 유지한다.
- Pull Request 검토 후 Develop 브랜치에 병합한다.
- Main 브랜치에는 검증된 코드만 Release한다.
- Semantic Versioning을 준수한다.
- Release마다 Git Tag를 생성한다.

---

# 19. Current Information

| Item | Value |
|---|---|
| Last Updated | 2026-08-10 |
| Roadmap Version | **v1.2** |
| Current Release | **v0.5.1** |
| Current Integration Branch | **develop** |
| Last Completed Feature Branch | **feature/frontend-components** |
| Current Sprint | **Sprint 2 - Project Management & Frontend Foundation** |
| Development Stage | **Full Stack Foundation & Project Management** |
| Maintainer | **Seo Seokhyeon** |

---

# 20. Next Milestone

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
- WBS UI
- Schedule UI
- Calendar UI
- Gantt UI

### Integration

- Project API Integration
- WBS API Integration
- Schedule API Integration
- Loading / Empty / Error State
- API Error Handling

---

# 21. Release Summary

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
| v0.6.x | WBS & Schedule |
| v0.7.x | Issue / Risk / Change |
| v0.8.x | CMDB |
| v0.9.x | Dashboard & Reporting |
| v1.0.0 | AI Powered PMIS |

---

# 22. Long-term Roadmap

| Version | Goal |
|---|---|
| 1.x | PMIS Core Platform |
| 2.x | Enterprise PMIS |
| 3.x | AI Native PMIS Platform |

---

**End of Document**
