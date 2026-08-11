# PMIS Portfolio

> **Version:** 1.3  
> **Last Updated:** 2026-08-11  
> **Project:** Project Management Information System (PMIS)  
> **Repository:** `project-delivery-reference`  
> **Development Stage:** Backend Core + Frontend Foundation + Project Management Core  
> **Current Integration Branch:** `develop`

---

# 1. 프로젝트 소개

PMIS(Project Management Information System)는 SI(System Integration) 및 SM(System Maintenance) 프로젝트 수행 과정에서 발생하는 프로젝트 관리 업무를 통합 지원하기 위해 개발하는 Enterprise Web Application이다.

단순한 일정 관리 시스템이나 CRUD 예제가 아니라 실제 프로젝트 현장에서 수행되는 PMO(Project Management Office) 업무를 데이터와 시스템으로 구조화하는 것을 목표로 한다.

주요 관리 영역은 다음과 같다.

* 프로젝트 관리
* WBS 관리
* 일정 관리
* 진척 관리
* 이슈 관리
* 리스크 관리
* 변경 관리
* 구축 관리
* 검수 관리
* CMDB(Configuration Management Database)
* Dashboard
* Report
* 파일 및 산출물 관리
* 운영 인수인계
* AI 기반 PM Assistant

Backend와 Frontend를 독립적인 프로젝트로 분리하고 REST API를 통해 연계하며, Git Flow 기반 Feature Branch 개발 전략을 적용한다.

---

# 2. 프로젝트 목표

## 2.1 1차 목표 — Enterprise PMIS Core

Spring Boot 기반 Enterprise Backend와 React 기반 Web Frontend를 구축한다.

주요 구현 대상:

* 사용자 및 권한 관리
* 인증 및 인가
* 프로젝트 관리
* WBS 관리
* 일정 관리
* 진척 관리
* 이슈 관리
* 리스크 관리
* 변경 관리
* CMDB
* Dashboard
* Report
* 파일 및 산출물 관리

현재 Backend는 Enterprise 공통 인프라, Security/JWT, 인증/인가 및 Project Management Core까지 구현되었다.

Frontend는 React + TypeScript + Vite 기반 Foundation과 Application Layout 및 Common UI Component를 구축한 상태이며, 이후 실제 Domain 화면과 API Integration을 단계적으로 확장한다.

---

## 2.2 2차 목표 — PMO 업무 시스템화

실제 PMO 업무에서 사용하는 데이터를 시스템으로 관리하고 프로젝트 전체 흐름을 하나의 데이터 모델로 연결한다.

주요 관리 데이터:

* 프로젝트 기본정보
* WBS
* 일정
* 진척률
* 구축 진행상황
* 이슈 및 조치사항
* 리스크
* 변경 요청
* 산출물
* 검수 자료
* 프로젝트 보고서
* 구성정보(CMDB)

목표 업무 흐름:

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
Dashboard
   ↓
Report
   ↓
Project Closure
```

이를 통해 Excel과 문서 중심으로 관리되는 PMO 업무를 데이터 기반 관리 방식으로 전환하는 것을 목표로 한다.

---

## 2.3 3차 목표 — AI Powered PMIS

PMIS Core 기능이 안정화된 이후 Spring AI를 기반으로 AI PM Assistant를 구축한다.

예정 기능:

* 프로젝트 상태 요약
* 프로젝트 진행률 분석
* 일정 상태 분석
* 일정 지연 분석
* 리스크 분석
* 고위험 항목 탐지
* 이슈 요약
* Action Item 추출
* 변경 영향도 분석
* 회의록 요약
* 주간보고서 초안 생성
* 월간보고서 초안 생성
* 프로젝트 질의응답(Chat)

AI는 단순 Chat 기능이 아니라 PMIS에 축적된 프로젝트 데이터를 기반으로 프로젝트 상태와 의사결정을 지원하는 방향으로 확장한다.

---

# 3. 프로젝트 특징

## 3.1 실무 중심 설계

PMIS는 실제 SI 프로젝트에서 수행되는 PMO 업무 프로세스를 기준으로 기능을 설계한다.

단순히 데이터를 등록하고 조회하는 것보다 프로젝트 관리 데이터가 서로 연결되는 구조를 중요하게 본다.

```text
Project
  │
  ├── WBS
  │    └── Schedule
  │
  ├── Progress
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

각 Domain의 데이터는 Dashboard와 Report에서 프로젝트 전체 상태를 표현하는 기반 데이터로 활용한다.

---

## 3.2 Domain 기반 Backend 설계

Backend 기능은 업무 Domain 단위로 분리한다.

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

각 Domain은 필요에 따라 다음 계층을 구성한다.

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Domain에 따라 Entity, DTO, Mapper, Validation 등을 추가하여 책임을 분리한다.

---

## 3.3 Frontend / Backend 분리

Repository는 다음과 같이 구성한다.

```text
project-delivery-reference
│
├── pmis-backend
├── pmis-frontend
└── docs
```

Backend:

```text
Spring Boot
    ↓
REST API
    ↓
MariaDB
```

Frontend:

```text
React
    ↓
React Router
    ↓
Feature / Page
    ↓
REST API
```

Frontend와 Backend의 독립적인 개발과 배포를 고려한 구조이다.

---

## 3.4 실제 PMO 업무와 Software Engineering의 결합

PMIS의 가장 중요한 포트폴리오 방향은 다음 두 가지를 결합하는 것이다.

```text
PMO Domain Knowledge
        +
Enterprise Software Engineering
        ↓
       PMIS
```

PMO 관점에서는:

* WBS
* Schedule
* Progress
* Issue
* Risk
* Change
* CMDB
* Report

를 다루고,

개발 관점에서는:

* Spring Boot
* Java
* JPA
* REST API
* Spring Security
* JWT
* React
* TypeScript
* Git Flow
* API Documentation
* AI Integration

을 적용한다.

---

# 4. 기술 스택

## 4.1 Backend

| Category | Technology |
| --- | --- |
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
| Utility | Lombok |
| Version Control | Git / GitHub |

Backend는 Enterprise Layered Architecture를 기반으로 구성하며 공통 응답, 예외 처리, Security, JWT 등을 공통 인프라로 관리한다.

---

## 4.2 Frontend

| Category | Technology |
| --- | --- |
| Framework | React |
| Language | TypeScript |
| Build Tool | Vite |
| Package Manager | npm |
| Router | React Router DOM |
| Runtime | Node.js |
| Architecture | Component-based / Feature-based |
| API | REST API |

현재 Frontend는 Foundation 단계에서 Application Layout과 Common UI Component까지 구축되었다.

구축 영역:

* App
* AppRouter
* MainLayout
* Header
* Sidebar
* Breadcrumb
* Content Area
* Outlet
* Dashboard Page Skeleton
* Common UI Components
* Feature Directory Structure
* TypeScript Alias

---

## 4.3 AI / Future Stack

예정 기술:

* Spring AI
* OpenAI
* Azure OpenAI
* Ollama
* RAG
* Vector Database
* MCP
* Multi-Agent Architecture

AI 기술은 PMIS Core가 충분히 안정화된 이후 단계적으로 적용한다.

---

# 5. 현재 Backend 구현 상태

현재 Backend는 **Enterprise Foundation + Security + Project Management Core**까지 구현된 상태이다.

## 5.1 Project Foundation

완료:

* Spring Boot 프로젝트
* Java 21
* Gradle Kotlin DSL
* MariaDB 연동
* Git Repository
* Domain-oriented Package Structure
* Common Package Structure

---

## 5.2 Common Infrastructure

완료:

* `BaseEntity`
* JPA Auditing
* `ApiResponse`
* `ErrorCode`
* Common Error Code
* Domain Error Code 구조
* `BusinessException`
* `GlobalExceptionHandler`
* `JpaConfig`

공통 응답 및 예외 처리 구조를 통해 Domain별 API가 동일한 Response 규격을 사용하도록 설계하였다.

---

## 5.3 Security

완료:

* Spring Security
* BCrypt Password Encoder
* Stateless Authentication
* CORS
* JWT Provider
* JWT Authentication Filter
* Authentication EntryPoint
* Access Denied Handler
* UserPrincipal
* JWT Properties
* Role Hierarchy

---

## 5.4 Authentication / Authorization

완료:

* Login API
* JWT Access Token
* Refresh Token
* Token Reissue
* Access Token Validation
* Refresh Token Validation
* USER Role
* PM Role
* ADMIN Role
* Role Hierarchy

인증과 인가를 Backend 공통 Security Layer에서 처리하도록 구성하였다.

---

## 5.5 API Documentation

완료:

* OpenAPI 3
* Swagger UI
* JWT Authorization
* API Schema Documentation

---

## 5.6 Project Management

현재 핵심 업무 Domain 중 첫 번째로 Project Management를 구현하였다.

완료:

* Project Entity
* Project Repository
* Project DTO
* Project Service
* Project Controller
* Project CRUD
* Project Search
* Sorting
* Validation
* Project Dashboard API

Project Domain은 향후 WBS, Schedule, Issue, Risk, Change 등의 상위 Root Domain으로 활용할 수 있도록 설계한다.

---

# 6. 현재 Frontend 구현 상태

Frontend는 현재 **Foundation + Layout + Common Component 단계**이다.

## 6.1 Frontend Foundation

완료:

* React
* Vite
* TypeScript
* npm
* React Router DOM
* Backend / Frontend Repository 분리
* Feature-based Directory Structure
* `@` Alias 기반 Import 구조

---

## 6.2 Application Layout

완료:

* App
* AppRouter
* MainLayout
* Header
* Sidebar
* Breadcrumb
* Content Area
* Outlet
* Dashboard Page Skeleton

현재 Application 구조:

```text
App
 ↓
AppRouter
 ↓
MainLayout
 ├── Header
 ├── Sidebar
 └── Main Content
      ├── Breadcrumb
      └── Outlet
           ↓
      DashboardPage
```

---

## 6.3 Common UI Components

완료:

```text
src/components/common
│
├── Button.tsx
├── Card.tsx
├── Loading.tsx
└── EmptyState.tsx
```

Layout:

```text
src/components/layout
│
├── Header.tsx
├── Sidebar.tsx
└── Breadcrumb.tsx
```

공통 Component는 Domain에 종속되지 않는 재사용 가능한 UI를 중심으로 구성한다.

향후 다음 Component를 추가한다.

* Table
* Form
* Modal
* Pagination
* Select
* Input
* Date Picker
* Confirm Dialog

---

# 7. Frontend Architecture

현재 Frontend 구조:

```text
pmis-frontend
│
├── src
│   ├── api
│   ├── assets
│   │
│   ├── components
│   │   ├── common
│   │   ├── form
│   │   └── layout
│   │
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
│   │
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
├── package-lock.json
├── tsconfig.app.json
└── vite.config.ts
```

Feature별 화면과 API Integration은 Foundation 구축 이후 Domain 개발 단계에서 확장한다.

---

# 8. 시스템 아키텍처

전체 시스템은 다음 구조를 가진다.

```text
┌───────────────────────────────┐
│            Browser            │
│                               │
│      React + TypeScript       │
│              ↓                │
│             Vite              │
└───────────────┬───────────────┘
                │
                │ HTTPS / REST API
                ▼
┌───────────────────────────────┐
│       Spring Boot API         │
│                               │
│      Security / JWT           │
│              ↓                │
│         Controller            │
│              ↓                │
│           Service             │
│              ↓                │
│         Repository            │
└───────────────┬───────────────┘
                │
                ▼
          ┌───────────┐
          │  MariaDB  │
          └───────────┘

Future
                │
                ▼
          ┌───────────┐
          │ Spring AI │
          └─────┬─────┘
                ▼
            LLM / RAG
```

---

# 9. PMIS Domain Model

PMIS의 주요 Domain은 Project를 중심으로 확장한다.

```text
User
 │
 ▼
Project
 │
 ├── WBS
 │    └── Schedule
 │
 ├── Progress
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

Dashboard는 각 Domain의 데이터를 집계하여 프로젝트 전체 상태를 표현한다.

향후 Project → WBS → Schedule → Progress 관계를 중심으로 실제 프로젝트 수행 흐름을 구현한다.

---

# 10. PMO 업무 시스템화

PMIS는 PMO 업무 데이터를 개별 기능으로 분리하는 것에 그치지 않고 데이터 흐름으로 연결한다.

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
Dashboard
   ↓
Weekly / Monthly Report
```

향후 AI가 추가되면 다음 구조로 발전한다.

```text
PMIS Data
    ↓
AI Analysis
    ↓
Project Insight
    ↓
Recommendation / Report
```

이 구조를 통해 반복적인 PMO 분석 및 보고 업무를 점진적으로 자동화한다.

---

# 11. 개발 방법론

## Git Flow

```text
main
 │
develop
 │
 ├── feature/*
 ├── bugfix/*
 ├── refactor/*
 └── release/*
```

주요 Branch 역할:

| Branch | Role |
| --- | --- |
| `main` | 안정적인 Release |
| `develop` | 통합 개발 |
| `feature/*` | 기능 개발 |
| `bugfix/*` | Bug Fix |
| `refactor/*` | 구조 개선 |
| `release/*` | Release Preparation |
| `hotfix/*` | Production Hot Fix |

---

## 개발 절차

```text
Planning
   ↓
Feature Branch
   ↓
Development
   ↓
Local Test
   ↓
Documentation
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

현재 통합 기준 Branch는 `develop`이다.

---

# 12. Commit Convention

| Prefix | Meaning |
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

예:

```text
feat(frontend): initialize frontend project
feat(frontend): add sidebar component
feat(frontend): add common UI components
feat(frontend): implement application layout
feat(project): implement project search
feat(project): add project dashboard API
docs: update frontend development history
```

---

# 13. Documentation

PMIS는 소스코드와 문서를 함께 관리하는 Documentation First 원칙을 적용한다.

현재 주요 문서:

```text
docs
│
├── _meta
│   └── CHANGELOG.md
│
├── PROJECT_OVERVIEW.md
├── ARCHITECTURE.md
├── PORTFOLIO.md
├── DEVELOPMENT_ROADMAP.md
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

문서와 소스코드의 상태가 가능한 한 동일하게 유지되도록 관리한다.

---

# 14. 개발 진행 현황

| Area | Status |
| --- | --- |
| Spring Boot Foundation | ✅ Completed |
| Java 21 | ✅ Completed |
| Gradle Kotlin DSL | ✅ Completed |
| MariaDB | ✅ Completed |
| Common Infrastructure | ✅ Completed |
| Security / JWT | ✅ Completed |
| Authentication | ✅ Completed |
| Authorization / Role | ✅ Completed |
| Swagger / OpenAPI | ✅ Completed |
| Project CRUD | ✅ Completed |
| Project Search | ✅ Completed |
| Project Dashboard API | ✅ Completed |
| React Foundation | ✅ Completed |
| Vite | ✅ Completed |
| TypeScript | ✅ Completed |
| React Router | ✅ Completed |
| Feature Directory | ✅ Completed |
| Main Layout | ✅ Completed |
| Header | ✅ Completed |
| Sidebar | ✅ Completed |
| Breadcrumb | ✅ Completed |
| Common UI Components | ✅ Completed |
| Dashboard UI | 🚧 Skeleton |
| Backend / Frontend API Integration | ⏳ Planned |
| Authentication UI | ⏳ Planned |
| Project UI | ⏳ Planned |
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

## Phase 1 — Frontend Foundation / Integration

현재 구축된 Frontend Foundation을 실제 Backend API와 연결할 수 있는 기반으로 확장한다.

예정:

* Dashboard Widget
* Table
* Form
* Modal
* Pagination
* API Client
* API Error Handling
* Loading / Empty / Error State
* Authentication UI
* Project API Integration
* Project List UI
* Project Detail UI

Phase 1의 핵심 목표는 단순 UI 제작이 아니라 **Backend Project Management API와 React Frontend를 실제로 연결하는 것**이다.

---

## Phase 2 — WBS & Schedule

Backend:

* WBS CRUD
* Hierarchical WBS
* Task Management
* Schedule CRUD
* Progress Calculation
* Calendar API
* Gantt Data
* Milestone

Frontend:

* WBS Tree
* Task View
* Schedule List
* Calendar
* Gantt View
* Progress Visualization

---

## Phase 3 — Issue / Risk / Change

Backend:

* Issue Management
* Risk Management
* Change Management
* Assignment
* Status
* Priority
* History
* Approval Workflow

Frontend:

* Issue Board
* Issue Detail
* Risk Matrix
* Risk Detail
* Change Request
* Change Workflow
* History View

---

## Phase 4 — CMDB

* Server CI
* Database CI
* Software CI
* Network CI
* CI Relationship
* Dependency
* Configuration History
* Audit Log

---

## Phase 5 — Dashboard & Report

* Executive Dashboard
* Project Dashboard
* WBS Dashboard
* Schedule Dashboard
* Issue Dashboard
* Risk Dashboard
* Change Dashboard
* CMDB Dashboard
* Weekly Report
* Monthly Report
* Executive Report
* Excel Export
* PDF Export

---

## Phase 6 — AI PM Assistant

* Spring AI
* Prompt Management
* LLM Integration
* Project Summary
* Schedule Analysis
* Risk Analysis
* Issue Summary
* Change Impact Analysis
* RAG
* Meeting Summary
* Action Item Generation
* Weekly Report Generation
* Project Q&A

---

# 16. Portfolio Demonstration Scenario

최종 포트폴리오에서는 단순 CRUD 화면이 아니라 실제 PMO 업무 흐름이 하나의 시스템에서 연결되는 모습을 시연한다.

```text
1. Login
      ↓
2. Project 선택
      ↓
3. Project Dashboard
      ↓
4. WBS 확인
      ↓
5. Schedule 확인
      ↓
6. Progress 입력
      ↓
7. Issue 등록
      ↓
8. Risk 등록
      ↓
9. Change Request 등록
      ↓
10. Dashboard 집계
      ↓
11. Weekly Report 생성
      ↓
12. AI Project Summary
```

이를 통해 다음 흐름을 하나의 E2E 시나리오로 보여주는 것을 목표로 한다.

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
Dashboard
   ↓
Report
   ↓
AI Analysis
```

---

# 17. 포트폴리오 가치

PMIS는 단순한 기술 Stack 나열형 프로젝트가 아니라 **PMO Domain Knowledge와 Software Engineering을 결합한 Enterprise Application**을 지향한다.

## 17.1 Backend 역량

* Java 21
* Spring Boot
* Enterprise Layered Architecture
* Domain-oriented Design
* REST API
* Spring Security
* JWT
* JPA
* MariaDB
* Global Exception Handling
* Standard API Response
* OpenAPI / Swagger

---

## 17.2 Frontend 역량

* React
* TypeScript
* Vite
* React Router
* Feature-based Architecture
* Component Architecture
* Layout Architecture
* Reusable UI Components
* REST API Integration
* Type-safe Development

---

## 17.3 PM / PMO Domain 역량

* Project Management
* WBS
* Schedule Management
* Progress Management
* Issue Management
* Risk Management
* Change Management
* CMDB
* Dashboard
* Reporting
* Project Closure

---

## 17.4 AI 역량

향후 구현:

* Spring AI
* LLM Integration
* Prompt Engineering
* RAG
* Vector Database
* Project Data Analysis
* AI Report Generation
* AI Project Assistant

---

# 18. 차별화 포인트

PMIS의 핵심 차별화 요소는 **개발 경험과 PMO 실무 경험을 하나의 시스템으로 연결하는 것**이다.

일반적인 포트폴리오:

```text
CRUD
 ↓
Database
 ↓
Web UI
```

PMIS:

```text
Real PMO Process
        ↓
Domain Modeling
        ↓
Enterprise Backend
        ↓
REST API
        ↓
React Frontend
        ↓
Dashboard / Report
        ↓
AI Analysis
```

따라서 PMIS는 단순한 개인 학습용 CRUD 프로젝트가 아니라 실제 SI 프로젝트의 관리 프로세스를 Software Engineering 관점에서 모델링한 프로젝트라는 점을 핵심 가치로 한다.

---

# 19. 향후 확장

PMIS Core가 안정화된 이후 실제 요구사항에 따라 다음 기술을 검토한다.

* Redis
* QueryDSL
* Elasticsearch
* Kafka
* RabbitMQ
* Spring Batch
* Prometheus
* Grafana
* Docker
* Docker Compose
* GitHub Actions
* Nginx
* AWS
* Object Storage
* S3
* Kubernetes
* Vector Database
* MCP
* Multi-Agent Architecture

모든 기술을 무조건 적용하지 않고 실제 문제와 요구사항을 기준으로 선택한다.

---

# 20. 최종 포트폴리오 목표

PMIS의 최종 목표는 다음 세 가지 역량을 하나의 프로젝트에서 보여주는 것이다.

```text
                    PMIS
                     │
        ┌────────────┼────────────┐
        ▼            ▼            ▼
   PMO Domain    Software        AI
   Knowledge     Engineering     Engineering
        │            │            │
        ▼            ▼            ▼
   Project/WBS    Spring Boot    Spring AI
   Schedule       React          LLM
   Issue/Risk     REST API       RAG
   Change/CMDB    Security       Analysis
   Report         Database       Assistant
```

최종적으로 다음과 같은 Enterprise PMIS를 목표로 한다.

```text
┌──────────────────────────────────────┐
│               PMIS                   │
│                                      │
│ Project Management                   │
│ WBS / Schedule / Progress            │
│ Issue / Risk / Change                │
│ CMDB / Dashboard / Report            │
│ Document / File / Closure             │
│                                      │
│                ↓                     │
│          PMO Data Platform           │
│                ↓                     │
│            Spring AI                 │
│                ↓                     │
│         AI PM Assistant              │
└──────────────────────────────────────┘
```

단순히 **"Java + React를 사용한 프로젝트"**가 아니라,

**"실제 PMO 업무를 이해하고 이를 Enterprise Web Application으로 설계·구현하며, 향후 AI 기반 PM Assistant까지 확장하는 프로젝트"**

라는 포지셔닝을 최종 목표로 한다.

---

# 21. Document History

| Version | Date | Author | Description |
| --- | --- | --- | --- |
| 1.0 | 2026-08-03 | Seo Seokhyeon | Initial Portfolio Document |
| 1.1 | 2026-08-08 | Seo Seokhyeon | Updated portfolio to reflect Backend + Frontend implementation status |
| 1.2 | 2026-08-10 | Seo Seokhyeon | Updated current implementation status, Frontend Foundation, Project Management Core, development roadmap, demonstration scenario and portfolio positioning |
| 1.3 | 2026-08-11 | Seo Seokhyeon | Updated current implementation stage, Frontend Foundation status, Project Management Core status, Frontend integration roadmap and portfolio positioning |

---

# End of Document
