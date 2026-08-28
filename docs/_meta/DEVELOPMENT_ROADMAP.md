# PMIS Development Roadmap

> **Version:** **1.7**
> **Last Updated:** **2026-08-28**
> **Project:** **Project Management Information System (PMIS)**

**Architecture:**

* Backend: Spring Boot 4.1.0, Spring Security, JWT, MariaDB
* Frontend: React, Vite, TypeScript, React Router DOM
* AI: Spring AI (planned)

**Development Methodology:** Agile Scrum + Git Flow

---

# 1. Development Goal

본 프로젝트는 단순 CRUD 프로젝트가 아니라 실제 SI 프로젝트에서 사용하는 **Project Management Information System(PMIS)**을 구현하는 것을 목표로 한다.

구현 대상:

* 사용자/권한 관리
* 프로젝트 관리
* WBS 관리
* 일정 관리
* 진척 관리
* 이슈 관리
* 리스크 관리
* 변경 관리
* 구축 관리
* 검수 관리
* 증적 관리
* CMDB
* Dashboard
* Report
* 운영 인수인계
* Spring AI Assistant

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

| 항목                  | 설명            |
| ------------------- | ------------- |
| Evidence Required   | 증적 필수 여부      |
| Evidence Type       | 증적 유형         |
| Evidence            | 실제 증적 파일/문서   |
| Evidence Status     | 존재/누락/검토중/불필요 |
| Verification Status | 검토 여부         |
| Reviewer            | 검토자           |
| Verified Date       | 검토일           |
| Remark              | 비고            |
| Due Date            | 증적 제출 기한      |

---

# 3. Architecture

```text
┌─────────────────────────────────────────────┐
│              PMIS Frontend                  │
│ React + TypeScript + Vite                   │
│ React Router DOM                             │
│ Layout / Components / Pages / Features      │
│ Project / WBS / Schedule / Issue / Risk     │
│ Change / Evidence / CMDB / Report           │
└──────────────────────┬──────────────────────┘
                       │ REST API
                       ▼
┌─────────────────────────────────────────────┐
│              PMIS Backend                   │
│ Spring Boot + Spring Security + JWT         │
│ Controller / Service / Repository            │
│ DTO / Mapper / Specification                 │
│ Evidence / File / Verification               │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────┐
│                 MariaDB                     │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
               File / Object Storage
                    Planned
                       │
                       ▼
┌─────────────────────────────────────────────┐
│                Spring AI                    │
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

## Current Frontend Development Flow

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
        ↓
feature/frontend-api-integration
        ↓
develop
        ↓
feature/frontend-project
        ↓
develop
        ↓
feature/frontend-wbs
        ↓
develop
        ↓
feature/frontend-schedule
        ↓
Next
```

Project Frontend CRUD는 완료되었으며 WBS Frontend API Integration 및 UI 구현도 완료되었다.

현재 Schedule Backend API 검증이 완료되어 다음 단계는 Schedule Frontend Integration이다.

---

# 5. Version Strategy

PMIS는 Semantic Versioning(SemVer)을 따른다.

```text
MAJOR.MINOR.PATCH
```

* **Major**: 호환되지 않는 변경
* **Minor**: 새로운 기능 추가
* **Patch**: 버그 수정 및 개선

Feature Branch 단위의 개발 완료와 Release Version은 별도로 관리한다.

현재 정식 Release는 **v0.5.3**을 기준으로 한다.

`develop`에는 Project Frontend CRUD, WBS Frontend Integration 및 Schedule Backend 개발 변경 사항이 추가되어 있으며 다음 Release 후보로 관리한다.

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

feat: implement WBS CRUD

feat: implement Schedule CRUD

test: verify Schedule Swagger API

feat(frontend): implement React router

feat(frontend): add common UI components

feat(frontend): add HMC reference dashboard layout

feat(frontend): implement WBS API integration

feat(frontend): complete project CRUD

fix: resolve JWT validation issue

docs: update architecture documentation
```

## Recent Frontend Commit

```text
8d7df6d feat(frontend): complete project CRUD
```

---

# 7. Sprint Plan

# Sprint 0

## Base Project

### Goal

프로젝트 기반 구축

### Backend

* Spring Boot
* Gradle Kotlin DSL
* MariaDB
* Package Structure
* Base Entity
* Global Response
* Exception
* JPA
* Build Configuration

### Frontend

* Frontend Project Planning
* React / Vite / TypeScript 선정
* Backend / Frontend 프로젝트 분리
* Frontend Directory Architecture 설계

### Deliverables

* Base Project
* Backend Build Success
* Frontend Project Structure
* Git Repository

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

* JWT Provider
* JWT Filter
* SecurityConfig
* UserPrincipal
* Login
* Refresh Token
* Role Authorization
* Password Encoder

### Frontend Planned Tasks

* Login Page
* Login Form
* Authentication State
* Token Handling
* Protected Route
* Logout

### Deliverables

Backend:

* JWT Authentication
* Role Authorization
* Security Framework

Frontend:

* Authentication UI
* Protected Route

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

### Backend Completed Tasks

* Project CRUD
* Customer / PM / Status / Priority
* Validation
* Project Search
* Sorting
* Project Dashboard API
* Total Project Count
* Status Count
* Priority Count
* Recent Project KPI
* Upcoming Deadline KPI
* Project Detail API
* Project Detail Response
* Project Detail Service
* Project Detail Controller
* Project Detail Mapper
* Project Detail API 분리

### Controller Architecture

```text
project/controller
│
├── ProjectController
├── ProjectDashboardController
└── ProjectDetailController
```

### Frontend Foundation

* React
* Vite
* TypeScript
* npm
* React Router DOM
* Feature-based Directory Structure
* API / Service / Store 구조 준비
* AppRouter
* MainLayout
* Header
* Sidebar
* Breadcrumb
* Dashboard Page
* Button
* Card
* Loading
* EmptyState

### Project Frontend

* Project List
* Project Search
* Project Detail
* Project Registration
* Project Edit
* Project Delete
* API Integration
* Loading / Empty / Error State
* Delete Confirmation

---

# Sprint 3

## WBS / Schedule / Frontend API Integration

### Goal

프로젝트 작업 구조와 일정을 관리하는 핵심 기능을 Backend와 Frontend에서 구현하고 실제 REST API Integration 기반의 업무 화면을 구축한다.

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
feature/frontend-api-integration
```

---

## WBS Backend

### Completed / Verification

* WBS CRUD
* Tree Structure
* Progress Calculation
* Task Relationship
* Validation
* Delete Handling
* Swagger API Verification

### WBS Frontend

* WBS Tree
* WBS Detail
* WBS Form
* WBS Status Form
* Progress Display
* WBS Delete
* Loading / Empty / Error State
* WBS API Client
* WBS API Integration

---

## Schedule Backend

### Completed

* Schedule CRUD
* Project별 Schedule 조회
* Schedule 단건 조회
* Schedule 생성
* Schedule 수정
* Schedule 삭제
* Schedule Status 변경
* Sort Order 관리
* Schedule Search
* Project 조건 검색
* WBS 조건 검색
* Keyword 검색
* Status 검색
* Dynamic Sorting
* Pagination
* Period Search
* Validation
* Swagger / OpenAPI 검증

### Schedule API Verification

Schedule API는 Swagger를 이용하여 주요 CRUD 및 검색 시나리오를 검증하였다.

주요 검증 범위:

```text
Schedule List
    ↓
Schedule Create
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

Schedule이 존재하지 않는 경우의 Not Found 처리 및 기본 Validation 시나리오도 검증 대상으로 관리한다.

---

## Schedule Frontend

### Current

* Schedule UI Preparation
* Schedule API Integration Preparation

### Next

* Schedule API Client
* Schedule List UI
* Schedule Detail UI
* Schedule Registration
* Schedule Edit
* Schedule Delete
* Schedule Status
* Schedule Search
* Schedule Loading / Empty / Error State
* Schedule API Integration
* Browser Verification

### Calendar

* Calendar UI
* Calendar API Integration
* Schedule Timeline
* Milestone Display

### Gantt

* Gantt UI
* WBS / Schedule 연계
* Start / End Date Visualization
* Progress Visualization
* Milestone Visualization

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

* Issue CRUD
* Assignment
* Status Workflow
* Priority
* Attachment

Risk:

* Risk CRUD
* Risk Assessment
* Probability / Impact
* Response Strategy
* Risk Monitoring

Change:

* Change Request
* Approval
* History
* Impact Analysis

### Frontend Tasks

Issue:

* Issue List
* Issue Detail
* Issue Registration
* Assignment
* Status Management

Risk:

* Risk List
* Risk Detail
* Risk Matrix
* Response Strategy

Change:

* Change Request
* Approval UI
* Change History
* Impact Analysis

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

* 전체 증적 대상 수
* 증적 확보 수
* 증적 누락 수
* 검토 대기 수
* 검토 완료 수
* 증적 확보율
* 증적 검수 완료율
* 기한 초과 증적
* Critical / Mandatory 증적 누락

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

* Server CI
* Software CI
* Database CI
* Relationship
* Version History
* Change Tracking

### Frontend Tasks

* CI List
* CI Detail
* CI Registration
* Relationship View
* Configuration History
* Change History

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

* Project Dashboard
* WBS Dashboard
* Schedule Dashboard
* Issue Dashboard
* Risk Dashboard
* Change Dashboard
* Evidence Dashboard
* CMDB Dashboard

Report:

* Weekly Report
* Monthly Report
* Executive Report
* Evidence Status Report
* Evidence Missing Report
* Excel Export
* PDF Export
* Statistics API

### Frontend Tasks

Dashboard:

* Executive Dashboard
* Project Dashboard
* Schedule Dashboard
* Issue Dashboard
* Risk Dashboard
* Change Dashboard
* Evidence Dashboard
* CMDB Dashboard
* Dashboard Widgets
* Dashboard API Integration

Report:

* Weekly Report UI
* Monthly Report UI
* Executive Report UI
* Evidence Status Report
* Excel Download
* PDF Download
* Statistics Visualization

> **Current Note:** HMC Reference 기반 Dashboard UI는 Sprint 2에서 선행 구현되었다. Sprint 7에서는 실제 Project/WBS/Schedule/Issue/Risk/Change/Evidence/CMDB API 기반 통합 Dashboard로 확장한다.

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

* OpenAI Integration
* Prompt Management
* AI Chat API
* Schedule Analysis
* Risk Analysis
* Project Analysis
* Evidence Gap Analysis
* Report Summary
* Meeting Summary
* Action Item Generation
* Change Impact Analysis

### Frontend Tasks

* AI Assistant UI
* Chat Interface
* AI Project Summary
* AI Risk Analysis
* AI Evidence Gap Analysis
* AI Report Summary
* AI Schedule Analysis
* AI Meeting Summary
* AI Recommendation Display

---

# Sprint 9

## Finalization

### Goal

Backend와 Frontend를 통합하여 포트폴리오 수준의 완성도를 확보한다.

### Backend

* Refactoring
* Unit Test
* Integration Test
* Swagger
* Docker
* Docker Compose
* GitHub Actions
* Security Hardening
* Audit Log
* Evidence Integrity Validation

### Frontend

* Component Refactoring
* Responsive UI
* Frontend Test
* Error Handling
* Loading / Empty / Error State
* Accessibility
* Build Optimization

### Common

* API Integration Test
* E2E Test
* Evidence E2E Test
* README
* Architecture Documentation
* Portfolio Documentation

---

# 8. Milestones

| Milestone | Description                                  |
| --------- | -------------------------------------------- |
| M1        | Base Project Complete                        |
| M2        | Authentication Complete                      |
| M3        | Project Management Backend Complete          |
| M4        | Frontend Foundation Complete                 |
| M5        | Project Frontend CRUD Complete               |
| M6        | WBS & Schedule Backend Complete              |
| M7        | WBS & Schedule Frontend Integration Complete |
| M8        | Issue/Risk/Change Complete                   |
| M9        | Evidence & Inspection Complete               |
| M10       | CMDB Complete                                |
| M11       | Dashboard & Report Complete                  |
| M12       | Spring AI Complete                           |
| M13       | Production Ready                             |
| M14       | Portfolio Release                            |

---

# 9. Estimated Schedule

| Sprint   | Duration |
| -------- | -------: |
| Sprint 0 |   1 Week |
| Sprint 1 |  2 Weeks |
| Sprint 2 |  2 Weeks |
| Sprint 3 |  2 Weeks |
| Sprint 4 |  2 Weeks |
| Sprint 5 |  2 Weeks |
| Sprint 6 |  2 Weeks |
| Sprint 7 |  2 Weeks |
| Sprint 8 |  2 Weeks |
| Sprint 9 |   1 Week |

**Estimated Total: 약 18~20주**

Frontend와 Backend는 동일 Sprint에서 병렬 개발할 수 있으며, API가 준비된 기능부터 Frontend Integration을 진행한다.

---

# 10. Quality Objectives

## Backend Architecture

* Layered Architecture
* Clean Architecture
* SOLID Principles
* RESTful API
* DTO Pattern
* Mapper Pattern
* Specification Pattern
* Feature-based Controller Separation
* Service Layer Separation
* Audit / History Management
* Evidence Traceability

## Frontend Architecture

* Component-based Architecture
* Feature-based Directory Structure
* Layout Architecture
* Reusable Common Components
* API / Service Separation
* State Management
* TypeScript Type Safety
* Responsive UI
* Evidence Checklist UX

## Integration

* REST API Contract
* API Error Handling
* Authentication Integration
* Loading State
* Empty State
* Error State
* E2E Verification
* Evidence Workflow Verification

## Development Standards

* Git Flow
* Semantic Versioning
* Conventional Commit
* Pull Request
* Code Review
* Unit Test
* Integration Test
* E2E Test

---

# 11. Documentation

## Project Documentation

* PROJECT_OVERVIEW.md
* DEVELOPMENT_ROADMAP.md
* ARCHITECTURE.md
* CHANGELOG.md
* PORTFOLIO.md

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

* 001_frontend_architecture.md
* 002_frontend_folder_structure.md
* 003_frontend_coding_convention.md
* 004_frontend_ui_design.md
* 005_frontend_component_architecture.md
* 006_frontend_development_history.md

문서와 소스코드는 항상 동일한 상태를 유지한다.

특히 API Integration 변경 시 다음 문서를 함께 검토한다.

* Frontend Architecture
* Frontend Component Architecture
* Frontend Development History
* CHANGELOG
* DEVELOPMENT_ROADMAP

---

# 12. Final Deliverables

## Backend

* PMIS Backend
* Spring Boot REST API
* JWT Authentication
* MariaDB Schema
* Project Management
* Project Search
* Project Dashboard
* Project Detail
* WBS Management
* Schedule Management
* Issue Management
* Risk Management
* Change Management
* Evidence Management
* Evidence Verification
* Inspection Management
* CMDB
* Dashboard API
* Report System
* Spring AI Assistant
* Swagger API

## Frontend

* PMIS Frontend
* React
* TypeScript
* Vite
* React Router
* Application Layout
* Dashboard
* Project Management UI
* WBS UI
* Schedule UI
* Calendar UI
* Gantt UI
* Issue UI
* Risk UI
* Change UI
* Evidence Dashboard UI
* Evidence Checklist UI
* Evidence Detail UI
* Evidence Verification UI
* CMDB UI
* Dashboard UI
* Report UI
* AI Assistant UI

## Infrastructure / Delivery

* Docker
* Docker Compose
* GitHub Actions
* GitHub Repository
* Portfolio Documentation

---

# 13. Success Criteria

프로젝트 완료 시 다음 목표를 만족한다.

* 실제 SI 프로젝트 수준의 PMIS 구현
* Spring Boot 기반 엔터프라이즈 Backend 구현
* React 기반 PMIS Frontend 구현
* Backend / Frontend REST API Integration
* JWT 기반 인증/인가 구현
* 프로젝트 수행 데이터 통합
* 산출물 및 증적 추적 관리
* 증적 누락 자동 식별
* 증적 검수 및 승인 이력 관리
* Spring AI 기반 AI Assistant 제공
* Docker 기반 배포 환경 구축
* Git Flow 기반 개발 프로세스 적용
* 운영 가능한 Full Stack PMIS 완성
* 포트폴리오 및 기술 시연 가능한 수준의 결과물 확보

---

# 14. Current Project Status

## Backend

### Completed

* Spring Boot
* Gradle Kotlin DSL
* Common Infrastructure
* Security
* JWT Authentication
* Login
* Refresh Token
* Role Hierarchy
* Swagger / OpenAPI
* Project CRUD
* Project Search
* Project Dashboard API
* Project Dashboard KPI
* Project Detail API
* Project Controller Separation
* Project Dashboard Controller Separation
* Project Detail Controller Separation
* WBS CRUD
* WBS Validation
* WBS Delete Handling
* Schedule CRUD
* Schedule Search
* Schedule Pagination
* Schedule Sorting
* Schedule Period Search
* Schedule Validation
* Schedule Swagger API Verification

### Current

* WBS Integration Verification
* Schedule Frontend Integration Preparation

### Planned

* Schedule Frontend
* Calendar
* Gantt
* Issue
* Risk
* Change
* Evidence
* Inspection
* CMDB
* Dashboard Integration
* Report
* Spring AI

---

## Frontend

### Completed

* React
* Vite
* TypeScript
* React Router DOM
* Feature-based Directory Structure
* Main Layout
* Header
* Sidebar
* Breadcrumb
* Dashboard Skeleton
* Button
* Card
* Loading
* EmptyState
* Vite Sample Removal
* TypeScript / Vite Alias Configuration
* HMC Reference 기반 Dashboard UI
* Dashboard Component 분리
* Dashboard Production Build Verification
* Frontend API Client
* Project API Client
* Project List UI
* Project Search UI
* Project Detail UI
* Project Registration UI
* Project Edit UI
* Project Delete UI
* Project CRUD API Integration
* Project Loading / Empty / Error State
* WBS API Client
* WBS API Integration
* WBS Tree UI
* WBS Detail UI
* WBS Form UI
* WBS Status Form UI
* WBS Delete

### Current

* WBS Integration Test
* WBS Browser Verification
* Schedule UI Preparation
* Schedule API Integration Preparation
* Dashboard API Integration Preparation
* Frontend API Error Handling 확장
* Loading / Empty / Error State 확장

### Next

* WBS Integration Test 완료
* WBS Browser Verification 완료
* Schedule API Integration
* Schedule UI
* Calendar UI
* Gantt UI
* Project Dashboard API Integration
* Dashboard API Integration
* Authentication UI

---

# 15. Current WBS State

```text
WBS Backend
    │
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
                  │
                  ▼
               Current
```

WBS Backend API와 Frontend Integration 구현이 완료되었으며, 현재 API 및 Browser 기반 Integration Verification을 진행한다.

---

# 16. Current Schedule State

```text
Schedule Backend
        │
        ├── CRUD                    ✓
        ├── List                    ✓
        ├── Detail                  ✓
        ├── Create                  ✓
        ├── Update                  ✓
        ├── Delete                  ✓
        ├── Status                  ✓
        ├── Sort Order              ✓
        ├── Search                  ✓
        ├── Pagination              ✓
        ├── Dynamic Sorting         ✓
        ├── Period Search           ✓
        ├── Validation              ✓
        └── Swagger Verification    ✓
                    │
                    ▼
          Schedule Frontend
                    │
                    ├── UI Preparation       🚧
                    ├── API Client            →
                    ├── API Integration       →
                    ├── Browser Verification  →
                    ├── Calendar              →
                    └── Gantt                 →
```

Schedule Backend은 기본 CRUD 및 주요 검색 기능의 Swagger 검증을 완료하였다.

따라서 Schedule Domain은 Backend API 개발 단계에서 **Frontend Integration 단계로 전환**한다.

---

# 17. Current Project Frontend State

Project Management Frontend는 CRUD 전체 기능이 구현되어 `develop`에 통합된 상태이다.

```text
Project Backend
      │
      ▼
Project API Client
      │
      ▼
Project List / Search
      │
      ├── Detail
      ├── Create
      ├── Edit
      └── Delete
      │
      ▼
Browser Verification
      │
      ▼
Production Build
      │
      ▼
Commit / Push
      │
      ▼
Merge to develop
      │
      ▼
Integration Test
```

### Project Frontend Completion

```text
Project List                 ✓
Project Search              ✓
Project Detail              ✓
Project Registration        ✓
Project Edit                ✓
Project Delete              ✓
Project API Integration     ✓
Loading State               ✓
Empty State                 ✓
Error State                 ✓
Router Integration          ✓
```

Project Frontend CRUD는 Feature Branch 개발 및 Develop Integration까지 완료되었다.

---

# 18. Current Git Development State

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
        ↓
feature/wbs-management
        ↓
develop
        ↓
feature/schedule-management
        ↓
Current Feature
```

Project Backend 개발은 CRUD, Search, Dashboard, Detail API까지 완료되어 `develop`에 통합되었다.

WBS Backend 개발을 거쳐 현재 Schedule Backend 개발 및 API Verification까지 진행되었다.

---

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
        ↓
feature/frontend-api-integration
        ↓
develop
        ↓
feature/frontend-project
        ↓
develop
        ↓
feature/frontend-wbs
        ↓
develop
        ↓
feature/frontend-schedule
        ↓
Current / Next
```

### Last Completed Frontend Feature

```text
feature/frontend-wbs
```

### Previously Integrated Project Commit

```text
8d7df6d feat(frontend): complete project CRUD
```

현재 통합 기준 브랜치는 `develop`이다.

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

Backend와 Frontend가 함께 필요한 기능:

```text
Backend API
    ↓
Swagger / API Test
    ↓
Frontend API Client
    ↓
Frontend UI
    ↓
API Integration
    ↓
Browser Test
    ↓
E2E Verification
```

## Project CRUD Integration

```text
Project API
    ↓
Project API Client
    ↓
Project List / Search
    ↓
Project Detail
    ↓
Project Create / Edit
    ↓
Project Delete
    ↓
Browser Verification
    ↓
Production Build
    ↓
Commit / Push
    ↓
Merge to develop
    ↓
Integration Test
```

## WBS Integration

```text
WBS API
    ↓
Frontend API Client
    ↓
WBS Tree / Detail / Form
    ↓
WBS Delete
    ↓
Browser Verification
    ↓
Integration Test
```

## Schedule Integration

```text
Schedule API
    ↓
Swagger Verification ✓
    ↓
Frontend API Client
    ↓
Schedule List / Detail
    ↓
Create / Edit / Delete
    ↓
Status / Search
    ↓
Browser Verification
    ↓
Calendar UI
    ↓
Gantt UI
    ↓
E2E Verification
```

## Evidence 기능

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

# 20. Git Branch Strategy

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

# 21. Documentation Policy

다음 문서는 항상 최신 상태를 유지한다.

* CHANGELOG.md
* DEVELOPMENT_ROADMAP.md
* PROJECT_OVERVIEW.md
* ARCHITECTURE.md
* PORTFOLIO.md

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

* 001_frontend_architecture.md
* 002_frontend_folder_structure.md
* 003_frontend_coding_convention.md
* 004_frontend_ui_design.md
* 005_frontend_component_architecture.md
* 006_frontend_development_history.md

소스코드 변경이 발생하면 관련 문서를 함께 현행화한다.

특히 Schedule Backend 또는 Frontend Integration 변경 시 다음 문서를 함께 검토한다.

* Schedule Design
* Frontend Architecture
* Frontend Component Architecture
* Frontend Development History
* CHANGELOG
* DEVELOPMENT_ROADMAP

---

# 22. Maintenance Policy

* 모든 기능은 Feature Branch에서 개발한다.
* 모든 주요 변경 사항은 CHANGELOG에 기록한다.
* Backend와 Frontend의 개발 상태를 Roadmap에 반영한다.
* 문서와 소스코드는 항상 동일한 상태를 유지한다.
* Pull Request 검토 후 Develop 브랜치에 병합한다.
* Main 브랜치에는 검증된 코드만 Release한다.
* Semantic Versioning을 준수한다.
* Release마다 Git Tag를 생성한다.
* 증적 관련 변경은 Evidence History에 기록한다.
* 필수 증적의 누락 상태를 프로젝트 Dashboard에서 추적한다.
* 검수 결과와 증적 상태를 Report에 반영한다.
* API Integration 기능은 API Verification과 Browser Verification을 모두 수행한다.
* Feature Branch Merge 후 `develop` Integration Test를 수행한다.
* Project CRUD Integration 완료 상태를 Roadmap과 Development History에 반영한다.
* Schedule Backend API Verification 완료 상태를 Roadmap과 Development History에 반영한다.
* Schedule Frontend Integration 완료 후 Calendar / Gantt 연계 상태를 반영한다.

---

# 23. Current Information

| Item                                  | Value                                                    |
| ------------------------------------- | -------------------------------------------------------- |
| Last Updated                          | **2026-08-28**                                           |
| Roadmap Version                       | **v1.7**                                                 |
| Current Release                       | **v0.5.3**                                               |
| Current Integration Branch            | **develop**                                              |
| Current Sprint                        | **Sprint 3 - WBS / Schedule / Frontend API Integration** |
| Last Completed Backend Domain         | **Schedule API Verification**                            |
| Last Completed Backend Feature Branch | **feature/schedule-management**                          |
| Last Completed Frontend Domain        | **WBS Integration**                                      |
| Last Integrated Frontend Feature      | **feature/frontend-wbs**                                 |
| Development Stage                     | **Schedule Backend CRUD / Swagger Verification 완료**      |
| Current Next Stage                    | **Schedule Frontend API Integration**                    |
| Evidence Domain                       | **Planned - Sprint 5**                                   |
| Maintainer                            | **Seo Seokhyeon**                                        |

> **Version Note:** `v0.5.3`은 마지막 정식 Release 기준이다. 현재 `develop`에는 Project Frontend CRUD, WBS Integration 및 Schedule Backend API 개발/검증 변경 사항이 추가되어 있으며 다음 Release 후보로 관리한다.

---

# 24. Next Milestone

# Sprint 3 Completion

## Backend

* WBS CRUD ✓
* WBS Tree Structure ✓
* WBS Validation ✓
* WBS Delete ✓
* Schedule CRUD ✓
* Schedule Search ✓
* Schedule Pagination ✓
* Schedule Sorting ✓
* Schedule Period Search ✓
* Schedule Validation ✓
* Schedule Swagger Verification ✓

## Frontend

* Project CRUD ✓
* WBS API Integration ✓
* WBS UI ✓
* WBS Delete ✓
* WBS Browser Verification
* Schedule API Client
* Schedule API Integration
* Schedule UI
* Schedule Browser Verification
* Calendar UI
* Gantt UI

## Integration

* Project API Integration ✓
* Project CRUD Browser Verification ✓
* Project Detail Browser Verification ✓
* Project Delete Verification ✓
* WBS API Integration ✓
* WBS Backend Verification ✓
* Schedule Backend API Verification ✓
* Schedule Frontend Integration →
* Schedule Browser Verification →
* Calendar Integration →
* Gantt Integration →
* Dashboard API Integration →
* E2E Verification →

## Recommended Development Order

```text
Project Frontend CRUD
        ✓
        ↓
WBS API Verification
        ✓
        ↓
WBS Browser Verification
        ↓
WBS Integration Test
        ↓
Schedule Backend API
        ✓
        ↓
Schedule Swagger Verification
        ✓
        ↓
Schedule API Client
        ↓
Schedule API Integration
        ↓
Schedule UI
        ↓
Schedule Browser Verification
        ↓
Calendar UI
        ↓
Gantt UI
        ↓
Project Dashboard API Integration
        ↓
Dashboard Data Integration
        ↓
E2E Verification
        ↓
Issue / Risk / Change
```

Evidence 기능은 Sprint 5에서 별도 Domain으로 구현하되, Sprint 3~4에서 Project/WBS/Issue/Change와의 연계 관계를 고려하여 데이터 모델을 설계한다.

---

# 25. Release Summary

| Version      | Description                                        |
| ------------ | -------------------------------------------------- |
| v0.1.0       | Project Initialization                             |
| v0.2.0       | Common Infrastructure                              |
| v0.3.0       | Security Foundation                                |
| v0.3.1       | Authentication & Authorization                     |
| v0.3.2       | Swagger / OpenAPI                                  |
| v0.4.0       | Project Management                                 |
| v0.5.0       | Frontend Foundation                                |
| v0.5.1       | Frontend Layout & Common Components                |
| v0.5.2       | Project Detail API & Controller Separation         |
| v0.5.3       | Frontend Dashboard HMC Reference                   |
| Next Release | Project Frontend CRUD / WBS / Schedule Integration |
| v0.7.x       | Issue / Risk / Change                              |
| v0.8.x       | Evidence & Inspection                              |
| v0.9.x       | CMDB                                               |
| v0.10.x      | Dashboard & Reporting                              |
| v1.0.0       | AI Powered PMIS                                    |

> **Note:** HMC Reference 기반 Dashboard UI는 `v0.5.3`에서 선행 구현되었으며, 이후 `develop` 개발 계열에서 Project Frontend CRUD, WBS Integration 및 Schedule Backend 개발이 진행되고 있다.

---

# 26. Long-term Roadmap

| Version | Goal                    |
| ------- | ----------------------- |
| 1.x     | PMIS Core Platform      |
| 2.x     | Enterprise PMIS         |
| 3.x     | AI Native PMIS Platform |

---

# 27. Evidence Management Long-term Direction

증적 관리 기능은 단순 파일 관리에서 시작하여 프로젝트 품질 및 감사 추적 체계로 확장한다.

## Phase 1 - Evidence Checklist

* 증적 요구사항 정의
* 필수/선택 증적 구분
* 증적 존재 여부 확인
* 누락 증적 조회
* 증적 확보율 Dashboard

## Phase 2 - Evidence Verification

* 검수자 지정
* 검수 상태 관리
* 승인/반려
* 검수 의견
* 검수 이력

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

* 필수 증적 누락 탐지
* WBS 대비 증적 미등록 탐지
* 일정 대비 증적 지연 탐지
* 검수 반려 증적 분석
* 반복적인 증적 누락 패턴 분석
* 증적 제출 우선순위 추천
* 주간 증적 상태 요약
* 증적 관련 질의응답

---

# 28. Current Development Snapshot

```text
Backend

 ├─ Common Infrastructure       ✓
 ├─ Security / JWT              ✓
 ├─ Project CRUD                ✓
 ├─ Project Search              ✓
 ├─ Project Dashboard API       ✓
 ├─ Project Detail API          ✓
 ├─ WBS                         ✓
 ├─ Schedule CRUD               ✓
 ├─ Schedule Search             ✓
 ├─ Schedule Validation         ✓
 ├─ Schedule Swagger Test       ✓
 ├─ Issue                       →
 ├─ Risk                        →
 ├─ Change                      →
 ├─ Evidence                    →
 ├─ CMDB                        →
 ├─ Dashboard                   →
 ├─ Report                      →
 └─ Spring AI                   →


Frontend

 ├─ React / Vite / TypeScript   ✓
 ├─ Router / Layout             ✓
 ├─ Common UI Components        ✓
 ├─ Dashboard Reference UI     ✓
 ├─ Dashboard Components       ✓
 ├─ Project List                ✓
 ├─ Project Search              ✓
 ├─ Project Detail              ✓
 ├─ Project Create              ✓
 ├─ Project Edit                ✓
 ├─ Project Delete              ✓
 ├─ Project CRUD Integration    ✓
 ├─ WBS API Integration         ✓
 ├─ WBS UI                      ✓
 ├─ WBS Delete                  ✓
 ├─ Schedule API Client         →
 ├─ Schedule Integration        →
 ├─ Schedule UI                 →
 ├─ Calendar UI                 →
 └─ Gantt UI                    →


Integration

 ├─ Project API Integration     ✓
 ├─ Project CRUD Verification    ✓
 ├─ WBS API Integration          ✓
 ├─ WBS Browser Verification     →
 ├─ WBS Integration Test         →
 ├─ Schedule API Verification    ✓
 ├─ Schedule API Integration     →
 ├─ Schedule Browser Verification→
 ├─ Calendar Integration         →
 ├─ Gantt Integration            →
 ├─ Dashboard API Integration    →
 └─ E2E Verification             →
```

---

# 29. Current Development Priority

현재 PMIS 개발의 우선순위는 다음과 같다.

```text
1. WBS Integration Test
        ↓
2. WBS Browser Verification
        ↓
3. Schedule API Client
        ↓
4. Schedule API Integration
        ↓
5. Schedule UI
        ↓
6. Schedule Browser Verification
        ↓
7. Calendar UI
        ↓
8. Gantt UI
        ↓
9. Dashboard API Integration
        ↓
10. Dashboard Data Integration
        ↓
11. E2E Verification
        ↓
12. Issue / Risk / Change
        ↓
13. Evidence Management
```

현재 Project Management Frontend는 CRUD 전체가 완료되었으므로 Project 영역은 신규 기능 개발보다는 Dashboard 및 다른 Domain과의 연계 단계에서 활용한다.

Schedule Backend의 기본 CRUD와 Swagger API 검증이 완료되었으므로, 현재 개발의 중심은 **Backend API 구현에서 Frontend API Integration으로 이동한다.**

---

# 30. Schedule Development Completion Criteria

Schedule Domain의 완료 기준은 다음과 같이 정의한다.

## Backend

```text
Schedule Entity
    ✓
Schedule Repository
    ✓
Schedule Service
    ✓
Schedule Controller
    ✓
Schedule CRUD
    ✓
Schedule Search
    ✓
Pagination
    ✓
Sorting
    ✓
Period Search
    ✓
Validation
    ✓
Swagger Verification
    ✓
```

## Frontend

```text
Schedule API Client
    ↓
Schedule List
    ↓
Schedule Search
    ↓
Schedule Detail
    ↓
Schedule Create
    ↓
Schedule Edit
    ↓
Schedule Delete
    ↓
Schedule Status
    ↓
Loading / Empty / Error
    ↓
Browser Verification
    ↓
Production Build
```

## Calendar / Gantt

```text
Schedule Data
    ↓
Calendar
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

Schedule Domain은 Backend API 구현만으로 완료되는 것이 아니라 **Backend → Frontend → Browser → Calendar → Gantt**까지 연결되는 것을 완료 기준으로 한다.

---

# End of Document
