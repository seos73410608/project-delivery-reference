# PMIS Portfolio

> **Version:** 1.1  
> **Last Updated:** 2026-08-08  
> **Project:** Project Management Information System (PMIS)  
> **Repository:** `project-delivery-reference`  
> **Development Stage:** Backend Core + Frontend Foundation  
> **Current Integration Branch:** `develop`

---

# 1. 프로젝트 소개

PMIS(Project Management Information System)는 SI 및 SM 프로젝트 수행 과정에서 발생하는 프로젝트 관리 업무를 통합 지원하는 웹 시스템이다.

단순한 일정 관리나 CRUD 예제가 아니라 실제 프로젝트 현장에서 수행되는 PMO(Project Management Office) 업무를 시스템화하는 것을 목표로 한다.

주요 관리 영역은 다음과 같다.

- 프로젝트 관리
- WBS 관리
- 일정 관리
- 이슈 관리
- 리스크 관리
- 변경 관리
- CMDB(Configuration Management Database)
- Dashboard
- Report
- 파일 및 산출물 관리
- 향후 AI 기반 PM Assistant

Backend와 Frontend를 분리하여 독립적으로 개발하고 있으며, Git Flow 기반 Feature Branch 전략을 적용한다.

---

# 2. 프로젝트 목표

## 2.1 1차 목표 — Enterprise PMIS Core

Spring Boot 기반의 엔터프라이즈 PMIS Backend를 구축한다.

구현 및 개발 대상:

- 사용자/권한 관리
- 인증/인가
- 프로젝트 관리
- WBS 관리
- 일정 관리
- 이슈 관리
- 리스크 관리
- 변경 관리
- CMDB
- Dashboard
- Report
- 파일 관리

---

## 2.2 2차 목표 — PMO 업무 시스템화

실제 PMO 업무에서 사용하는 데이터를 시스템으로 관리한다.

예:

- 프로젝트 기본정보
- WBS
- 일정 및 진척률
- 구축 진행상황
- 이슈 및 조치사항
- 리스크
- 변경 요청
- 산출물 및 검수자료
- 보고서
- 구성정보(CMDB)

이를 통해 문서 중심의 PMO 업무를 데이터 기반 관리 방식으로 전환하는 것을 목표로 한다.

---

## 2.3 3차 목표 — AI Powered PMIS

Spring AI를 기반으로 PMIS 데이터를 활용하는 AI PM Assistant를 구축한다.

예정 기능:

- 프로젝트 상태 요약
- 일정 분석
- 리스크 분석
- 이슈 요약
- 변경 영향도 분석
- 회의록 요약
- Action Item 생성
- 보고서 초안 생성

AI 기능은 PMIS Core가 안정화된 이후 단계적으로 추가한다.

---

# 3. 프로젝트 특징

## 3.1 실무 중심 설계

실제 SI 프로젝트의 PMO 수행 프로세스를 기준으로 기능을 설계한다.

단순 CRUD 구현보다 다음과 같은 프로젝트 관리 흐름을 시스템으로 표현하는 것을 중요하게 본다.

```text
Project
   ↓
WBS
   ↓
Schedule
   ↓
Progress
   ↓
Issue / Risk / Change
   ↓
Report / Dashboard
   ↓
Project Closure
```

---

## 3.2 Domain 기반 설계

Backend 기능은 Domain 단위로 분리한다.

주요 Domain:

```text
auth
user
project
wbs
schedule
issue
risk
change
cmdb
report
dashboard
file
batch
common
config
```

각 Domain은 필요에 따라 다음 구조를 사용한다.

```text
Controller
Service
Repository
Entity
DTO
Mapper
```

---

## 3.3 Frontend / Backend 분리

Repository 구조:

```text
project-delivery-reference
│
├── pmis-backend
├── pmis-frontend
└── docs
```

Frontend는 React 기반으로 별도 프로젝트로 구성하며 Backend REST API와 연동한다.

---

# 4. 현재 기술 스택

## 4.1 Backend

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.0 |
| Build | Gradle Kotlin DSL |
| ORM | Spring Data JPA |
| Database | MariaDB |
| Security | Spring Security |
| Authentication | JWT |
| Validation | Jakarta Validation |
| API | RESTful API |
| Documentation | Swagger / OpenAPI |
| Version Control | Git / GitHub |

> Spring Boot 버전 및 라이브러리 구성은 실제 구현 상태를 기준으로 문서화한다.

---

## 4.2 Frontend

| Category | Technology |
|---|---|
| Framework | React |
| Language | TypeScript |
| Build Tool | Vite |
| Package Manager | npm |
| Router | React Router DOM |
| Runtime | Node.js |
| UI Architecture | Component-based Architecture |

현재 Frontend는 Foundation 단계이며 공통 Layout 및 UI Component를 구축하고 있다.

---

## 4.3 AI / Future Stack

예정:

- Spring AI
- OpenAI
- Azure OpenAI
- Ollama
- RAG
- Vector Database
- MCP
- Multi-Agent Architecture

AI 기술은 PMIS Core 기능 안정화 이후 적용한다.

---

# 5. 현재 구현 상태

## 5.1 Backend — Completed

현재 Backend에는 다음 기반 기능이 구현되어 있다.

### Project Foundation

- Spring Boot 프로젝트
- Java 21
- Gradle Kotlin DSL
- MariaDB 연동
- Git Repository
- 공통 Package 구조

### Common Infrastructure

- BaseEntity
- JPA Auditing
- ApiResponse
- ErrorCode
- Common Error Code
- Domain Error Code 구조
- BusinessException
- GlobalExceptionHandler
- JpaConfig

### Security

- Spring Security
- BCrypt
- Stateless Authentication
- CORS
- JWT Provider
- JWT Authentication Filter
- Authentication EntryPoint
- Access Denied Handler
- UserPrincipal
- JWT Properties

### Authentication / Authorization

- Login API
- Refresh Token API
- Token Reissue
- Access Token Validation
- Refresh Token Validation
- USER / PM / ADMIN Role
- Role Hierarchy

### API Documentation

- OpenAPI 3
- Swagger UI
- JWT Authorization

### Project Management

- Project Entity
- Project Repository
- Project Service
- Project Controller
- Project CRUD API
- Project Search
- Sorting
- Validation
- Project Dashboard API

---

# 6. 현재 Frontend 구현 상태

Frontend는 현재 **Foundation 및 공통 Layout Component 단계**이다.

## 6.1 Frontend Foundation

완료:

- React
- Vite
- TypeScript
- npm
- React Router DOM
- Backend / Frontend 분리
- Feature 기반 Directory Structure
- Alias(`@`) 기반 Import 구조

---

## 6.2 Layout

완료:

- App
- AppRouter
- MainLayout
- Header
- Sidebar
- Breadcrumb
- Content Area
- Dashboard Page Skeleton

현재 구조:

```text
App
 ↓
AppRouter
 ↓
MainLayout
 ├── Header
 ├── Sidebar
 └── Content
      ├── Breadcrumb
      └── Outlet
```

---

## 6.3 Common UI Components

현재 공통 Component를 구축하였다.

```text
src/components/common
│
├── Button.tsx
├── Card.tsx
├── EmptyState.tsx
└── Loading.tsx
```

Layout Component:

```text
src/components/layout
│
├── Header.tsx
├── Sidebar.tsx
└── Breadcrumb.tsx
```

향후 Form, Modal, Table, Pagination 등의 공통 Component를 추가할 예정이다.

---

# 7. Frontend Architecture

현재 Frontend 구조:

```text
pmis-frontend
│
├── src
│   ├── api
│   ├── assets
│   ├── components
│   │   ├── common
│   │   ├── form
│   │   └── layout
│   ├── features
│   │   ├── admin
│   │   ├── cmdb
│   │   ├── dashboard
│   │   ├── issue
│   │   ├── project
│   │   ├── report
│   │   ├── risk
│   │   ├── schedule
│   │   └── task
│   ├── hooks
│   ├── layouts
│   ├── pages
│   ├── router
│   ├── services
│   ├── store
│   ├── styles
│   ├── types
│   └── utils
│
├── package.json
├── tsconfig.app.json
└── vite.config.ts
```

Feature별 실제 화면 및 API 연동은 Core UI Foundation 이후 단계적으로 구현한다.

---

# 8. 시스템 아키텍처

전체적인 구조는 다음과 같다.

```text
┌─────────────────────────────┐
│          Browser            │
│       React Frontend        │
└──────────────┬──────────────┘
               │
               │ HTTPS / REST API
               ▼
┌─────────────────────────────┐
│       Spring Boot API       │
│                             │
│ Controller                  │
│      ↓                      │
│ Service                     │
│      ↓                      │
│ Repository                  │
└──────────────┬──────────────┘
               │
               ▼
        ┌─────────────┐
        │   MariaDB   │
        └─────────────┘

               │
               │ Future
               ▼
        ┌─────────────┐
        │  Spring AI  │
        └──────┬──────┘
               ▼
          LLM / RAG
```

---

# 9. PMIS Domain Model

주요 Domain 관계는 다음 방향으로 확장한다.

```text
User
 │
 ▼
Project
 │
 ├── WBS
 │    └── Schedule
 │
 ├── Issue
 │
 ├── Risk
 │
 ├── Change
 │
 ├── CMDB
 │
 └── Report
```

Dashboard는 각 Domain의 데이터를 집계하여 프로젝트 상태를 표현한다.

---

# 10. PMO 업무 자동화 방향

PMIS의 핵심 차별점은 PMO 업무를 단순 저장하는 것이 아니라 반복 업무를 시스템화하는 것이다.

예:

```text
Project Data
     ↓
WBS / Schedule
     ↓
Progress Data
     ↓
Issue / Risk / Change
     ↓
Dashboard
     ↓
Weekly / Monthly Report
```

향후에는 다음과 같이 확장한다.

```text
PMIS Data
    ↓
AI Analysis
    ↓
Project Insight
    ↓
Report / Recommendation
```

---

# 11. 개발 방법론

## Git Flow

```text
main
  │
develop
  │
feature/*
  │
release/*
  │
hotfix/*
```

개발 절차:

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
Release
```

---

# 12. Commit Convention

| Prefix | Meaning |
|---|---|
| feat | Feature |
| fix | Bug Fix |
| refactor | Refactoring |
| docs | Documentation |
| style | Code Style |
| test | Test |
| build | Build |
| chore | Maintenance |
| perf | Performance |
| security | Security |

예:

```text
feat(frontend): add sidebar component
feat(frontend): add common UI components
feat(frontend): implement application layout
docs: update frontend development history
```

---

# 13. Documentation

현재 주요 문서:

```text
docs
├── _meta
│   └── CHANGELOG.md
│
├── design
│   ├── issue
│   ├── project
│   ├── report
│   ├── schedule
│   ├── server
│   └── wbs
│
└── development
    └── frontend
        ├── 001_frontend_architecture.md
        ├── 002_frontend_folder_structure.md
        ├── 003_frontend_coding_convention.md
        ├── 004_frontend_ui_design.md
        ├── 005_frontend_component_architecture.md
        └── 006_frontend_development_history.md
```

문서와 소스코드는 동일한 상태를 유지하는 것을 원칙으로 한다.

---

# 14. 개발 진행 현황

| Area | Status |
|---|---|
| Spring Boot Foundation | ✅ Completed |
| Common Infrastructure | ✅ Completed |
| Security / JWT | ✅ Completed |
| Authentication | ✅ Completed |
| Swagger / OpenAPI | ✅ Completed |
| Project Management API | ✅ Completed |
| React Foundation | ✅ Completed |
| React Router | ✅ Completed |
| Main Layout | ✅ Completed |
| Header | ✅ Completed |
| Sidebar | ✅ Completed |
| Breadcrumb | ✅ Completed |
| Common UI Components | ✅ Completed |
| Dashboard UI | 🚧 Skeleton |
| WBS | ⏳ Planned |
| Schedule | ⏳ Planned |
| Issue | ⏳ Planned |
| Risk | ⏳ Planned |
| Change | ⏳ Planned |
| CMDB | ⏳ Planned |
| Report | ⏳ Planned |
| AI Assistant | ⏳ Planned |

---

# 15. 향후 개발 계획

## Phase 1 — Frontend Foundation

완료:

- React Foundation
- Routing
- Main Layout
- Header
- Sidebar
- Breadcrumb
- Common UI Components

다음:

- Dashboard Widget
- Table
- Form
- Modal
- Pagination
- API Client
- Error Handling

---

## Phase 2 — WBS & Schedule

Backend:

- WBS CRUD
- Schedule CRUD
- Progress Calculation
- Calendar API
- Gantt Data

Frontend:

- WBS Tree
- Schedule List
- Calendar
- Gantt View
- Progress Visualization

---

## Phase 3 — Issue / Risk / Change

Backend:

- Issue Management
- Risk Management
- Change Management

Frontend:

- Issue Board
- Risk Matrix
- Change Workflow
- Detail / History View

---

## Phase 4 — CMDB

- Server CI
- Database CI
- Software CI
- CI Relationship
- Configuration History

---

## Phase 5 — Dashboard & Report

- Executive Dashboard
- Project Dashboard
- WBS Dashboard
- Schedule Dashboard
- Issue Dashboard
- Risk Dashboard
- Weekly Report
- Monthly Report
- Excel Export
- PDF Export

---

## Phase 6 — AI PM Assistant

- Spring AI
- Prompt Management
- LLM Integration
- RAG
- Project Summary
- Risk Analysis
- Schedule Analysis
- Report Generation
- Meeting Summary
- Action Item Generation

---

# 16. Portfolio Demonstration Scenario

최종 포트폴리오에서는 실제 PMO 업무 흐름을 하나의 시나리오로 시연한다.

```text
1. Login
   ↓
2. Project 선택
   ↓
3. Project Dashboard
   ↓
4. WBS / Schedule 확인
   ↓
5. Progress 입력
   ↓
6. Issue / Risk 등록
   ↓
7. Change Request 등록
   ↓
8. Dashboard 집계
   ↓
9. Weekly Report 생성
   ↓
10. AI Project Summary
```

이를 통해 단순 화면 구현이 아니라 **프로젝트 관리 업무 전체가 하나의 시스템으로 연결되는 구조**를 보여준다.

---

# 17. 포트폴리오 가치

본 프로젝트를 통해 다음 역량을 보여주는 것을 목표로 한다.

### Backend

- Java / Spring Boot
- Enterprise Layered Architecture
- REST API
- Spring Security
- JWT Authentication
- JPA
- MariaDB
- API Documentation

### Frontend

- React
- TypeScript
- Vite
- React Router
- Component-based Architecture
- Layout Architecture
- Reusable UI Components
- REST API Integration

### PM / PMO

- WBS
- Schedule Management
- Issue Management
- Risk Management
- Change Management
- Dashboard
- Reporting
- CMDB

### AI

- Spring AI
- LLM Integration
- RAG
- AI-based Project Analysis

핵심적으로 **개발자 관점의 구현 능력과 PMO 관점의 업무 도메인 이해를 동시에 보여주는 포트폴리오**를 지향한다.

---

# 18. 향후 확장

PMIS Core가 안정화되면 다음 기술을 검토한다.

- Redis
- QueryDSL
- Elasticsearch
- Kafka
- RabbitMQ
- Spring Batch
- Prometheus
- Grafana
- Docker
- GitHub Actions
- Kubernetes
- AWS
- Object Storage
- Vector Database
- MCP
- Multi-Agent Architecture

모든 기술을 무조건 적용하지 않고 실제 요구사항과 학습 목적에 따라 선택한다.

---

# 19. Document History

| Version | Date | Author | Description |
|---|---|---|---|
| 1.0 | 2026-08-03 | Seo Seokhyeon | Initial Portfolio Document |
| 1.1 | 2026-08-08 | Seo Seokhyeon | Updated portfolio to reflect current Backend + Frontend implementation status |

---

# End of Document
