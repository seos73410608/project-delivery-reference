# PMIS Project Overview

> **Project:** Project Management Information System (PMIS)  
> **Version:** 1.1  
> **Last Updated:** 2026-08-08  
> **Repository:** `project-delivery-reference`  
> **Current Integration Branch:** `develop`

---

# 1. 프로젝트 개요

## 프로젝트명

**PMIS (Project Management Information System)**

## 프로젝트 목적

PMIS는 SI(System Integration) 및 SM 프로젝트 수행 과정에서 발생하는 프로젝트 관리 업무를 하나의 시스템으로 통합하기 위해 개발하는 Enterprise PMIS이다.

실제 프로젝트 현장에서 수행되는 PMO(Project Management Office) 업무를 기반으로 다음 영역을 통합 관리하는 것을 목표로 한다.

- 프로젝트 관리
- WBS 관리
- 일정 관리
- 진척 관리
- 이슈 관리
- 리스크 관리
- 변경 관리
- 구축 관리
- 검수 관리
- CMDB(Configuration Management Database)
- Dashboard
- Report
- 파일 및 산출물 관리
- 운영 인수인계

향후 Spring AI를 접목하여 반복적인 PMO 업무를 자동화하고 프로젝트 데이터를 기반으로 의사결정을 지원하는 AI PM Assistant로 확장한다.

---

# 2. 프로젝트 목표

## 2.1 Enterprise PMIS 구축

실제 SI 프로젝트에서 사용할 수 있는 수준의 프로젝트 관리 시스템을 구축한다.

주요 목표:

- Enterprise Backend Architecture 구축
- React 기반 Web Frontend 구축
- REST API 기반 Frontend / Backend 연계
- JWT 기반 인증/인가
- 프로젝트 및 WBS 관리
- 일정 및 진척 관리
- 이슈 / 리스크 / 변경 관리
- CMDB 관리
- Dashboard 및 Report
- 파일 및 산출물 관리

---

## 2.2 PMO 업무 시스템화

문서와 Excel 중심으로 수행되는 PMO 업무를 데이터 기반 시스템으로 전환한다.

예:

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
```

이를 통해 프로젝트 현황을 하나의 시스템에서 추적할 수 있도록 한다.

---

## 2.3 AI 기반 PMIS 확장

PMIS Core 기능이 안정화된 이후 Spring AI를 적용한다.

예정 기능:

- 프로젝트 상태 요약
- 일정 분석
- 일정 지연 분석
- 리스크 분석
- 이슈 요약
- 변경 영향도 분석
- 회의록 요약
- Action Item 추출
- 주간보고서 초안 생성
- 프로젝트 질의응답(Chat)

---

# 3. 주요 기능

## 3.1 프로젝트 관리

- 프로젝트 생성
- 프로젝트 조회
- 프로젝트 수정
- 프로젝트 삭제
- 프로젝트 검색
- 정렬
- Validation
- 프로젝트 Dashboard API

---

## 3.2 일정 관리

예정 기능:

- 일정 등록
- 일정 변경
- 일정 지연 관리
- 진척률 관리
- Calendar
- Gantt
- Milestone

---

## 3.3 WBS 관리

예정 기능:

- WBS CRUD
- 계층형 Tree Structure
- 담당자 관리
- 작업 상태
- 진척률 계산
- 일정 연계

---

## 3.4 이슈 관리

예정 기능:

- 이슈 등록
- 이슈 수정
- 이슈 종료
- 담당자 지정
- 우선순위 관리
- 상태 관리
- 처리 이력
- 첨부파일

---

## 3.5 리스크 관리

예정 기능:

- Risk 등록
- Probability / Impact
- Risk Score
- Risk Matrix
- 대응 전략
- 대응 결과
- Risk Monitoring

---

## 3.6 변경 관리

예정 기능:

- Change Request
- 변경 영향도 분석
- 승인 프로세스
- 변경 이력
- 변경 전/후 비교

---

## 3.7 CMDB 관리

예정 기능:

- Server CI
- Database CI
- Software CI
- Network CI
- CI Relationship
- Dependency
- Configuration History
- Audit Log

---

## 3.8 구축 관리

향후 PMO 업무 확장 영역:

- 설치 대상 관리
- HW/SW 설치 일정
- 환경 구성
- 구축 진행률
- 설치 결과
- 협력사 작업 관리
- 구축 이슈 관리

---

## 3.9 검수 관리

예정 기능:

- 검수 요청
- 검수 대상
- 검수 결과
- 증적 관리
- 검수 산출물
- 완료 보고

---

## 3.10 보고 관리

예정 기능:

- 주간보고
- 월간보고
- 프로젝트 현황 보고
- 회의록
- 의사결정 관리
- Excel Export
- PDF Export

---

## 3.11 운영 인수인계

예정 기능:

- 운영 문서
- 시스템 구성 정보
- 교육 자료
- 운영 담당자
- 인수인계 체크리스트
- 종료 보고서

---

# 4. AI 기능

Spring AI를 기반으로 다음 기능을 단계적으로 구현한다.

## Project Analysis

- 프로젝트 상태 요약
- 진행률 분석
- 일정 상태 분석

## Risk Analysis

- 위험도 분석
- 고위험 항목 탐지
- 대응 전략 추천

## Issue Analysis

- 이슈 요약
- 반복 이슈 분석
- Action Item 추출

## Change Analysis

- 변경 영향도 분석
- 일정 영향 분석
- 리스크 영향 분석

## Report Automation

- 주간보고 초안 생성
- 월간보고 초안 생성
- 회의록 요약
- 프로젝트 상태 보고

## AI Assistant

```text
User
 ↓
PMIS Frontend
 ↓
PMIS REST API
 ↓
Spring AI
 ↓
LLM / RAG
 ↓
Project Data
 ↓
AI Response
```

---

# 5. 기술 스택

## Backend

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
| Utility | Lombok |

---

## Frontend

| Category | Technology |
|---|---|
| Framework | React |
| Language | TypeScript |
| Build Tool | Vite |
| Package Manager | npm |
| Router | React Router DOM |
| Architecture | Component-based Architecture |

Frontend는 Backend와 별도 프로젝트로 관리하며 REST API 기반으로 연계한다.

---

## AI / Future

예정:

- Spring AI
- OpenAI
- Azure OpenAI
- Ollama
- RAG
- Vector Database
- MCP
- Multi-Agent Architecture

---

## Infrastructure / Deployment

향후 적용:

- Docker
- Docker Compose
- Nginx
- GitHub Actions
- AWS
- Object Storage

---

# 6. Repository 구조

현재 Repository 구조:

```text
project-delivery-reference
│
├── docs
│   │
│   ├── _meta
│   │   └── CHANGELOG.md
│   │
│   ├── design
│   │   ├── issue
│   │   ├── project
│   │   ├── report
│   │   ├── schedule
│   │   ├── server
│   │   └── wbs
│   │
│   └── development
│       └── frontend
│           ├── 001_frontend_architecture.md
│           ├── 002_frontend_folder_structure.md
│           ├── 003_frontend_coding_convention.md
│           ├── 004_frontend_ui_design.md
│           ├── 005_frontend_component_architecture.md
│           └── 006_frontend_development_history.md
│
├── pmis-backend
│
└── pmis-frontend
```

---

# 7. Backend 구조

Backend Root:

```text
pmis-backend
└── src
    └── main
        └── java
            └── com.seos.pmis
```

주요 Domain:

```text
com.seos.pmis
│
├── auth
├── user
├── project
├── wbs
├── schedule
├── issue
├── risk
├── change
├── cmdb
├── report
├── dashboard
├── file
├── batch
├── common
└── config
```

공통 영역:

```text
common
├── annotation
├── constant
├── dto
├── entity
├── enums
├── exception
├── response
├── util
└── validation
```

---

# 8. Frontend 구조

Frontend Root:

```text
pmis-frontend
└── src
    ├── api
    ├── assets
    ├── components
    │   ├── common
    │   ├── form
    │   └── layout
    ├── features
    │   ├── admin
    │   ├── cmdb
    │   ├── dashboard
    │   ├── issue
    │   ├── project
    │   ├── report
    │   ├── risk
    │   ├── schedule
    │   └── task
    ├── hooks
    ├── layouts
    ├── pages
    ├── router
    ├── services
    ├── store
    ├── styles
    ├── types
    └── utils
```

현재 구현된 Layout Component:

```text
components/layout
├── Header.tsx
├── Sidebar.tsx
└── Breadcrumb.tsx
```

현재 구현된 Common UI Component:

```text
components/common
├── Button.tsx
├── Card.tsx
├── Loading.tsx
└── EmptyState.tsx
```

---

# 9. 현재 Frontend 화면 구조

현재 Frontend의 기본 화면 구조:

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

현재 Dashboard는 Skeleton 단계이며 향후 Widget 기반 Dashboard로 확장한다.

---

# 10. 시스템 아키텍처

전체 시스템 구조:

```text
┌──────────────────────────────┐
│          Browser             │
│                              │
│     React + TypeScript       │
└──────────────┬───────────────┘
               │
               │ HTTPS / REST API
               ▼
┌──────────────────────────────┐
│      Spring Boot API         │
│                              │
│ Controller                   │
│      ↓                       │
│ Service                      │
│      ↓                       │
│ Repository                   │
└──────────────┬───────────────┘
               │
               ▼
        ┌─────────────┐
        │   MariaDB   │
        └─────────────┘

Future
               │
               ▼
        ┌─────────────┐
        │  Spring AI  │
        └──────┬──────┘
               ▼
          LLM / RAG
```

---

# 11. 인증/인가 구조

현재 Security / JWT 기반 인증 구조를 적용하였다.

```text
Client
  ↓
Login
  ↓
Authentication
  ↓
JWT Access Token
  ↓
Request
  ↓
JWT Authentication Filter
  ↓
Authorization
  ↓
Controller
```

주요 구성:

- Spring Security
- BCrypt Password Encoder
- Stateless Authentication
- CORS
- JWT Provider
- JWT Authentication Filter
- Authentication EntryPoint
- Access Denied Handler
- UserPrincipal
- Role Hierarchy

Role:

```text
USER
PM
ADMIN
```

---

# 12. 개발 원칙

본 프로젝트는 다음 원칙을 따른다.

## Architecture

- Layered Architecture
- Domain-oriented Design
- Clean Architecture Principles
- SOLID
- Low Coupling
- High Cohesion

## Backend

- RESTful API
- DTO Pattern
- Service Layer
- Repository Pattern
- Global Exception Handling
- Standard API Response

## Frontend

- Component-based Architecture
- Reusable Components
- Layout / Page 분리
- Feature-based Directory
- TypeScript 기반 타입 안정성

## Development

- Git Flow
- Semantic Versioning
- Conventional Commit
- Pull Request
- Code Review
- Testable Code
- Documentation First

---

# 13. 현재 프로젝트 상태

## Backend

| 영역 | 상태 |
|---|---|
| Spring Boot Foundation | ✅ 완료 |
| Java 21 | ✅ 완료 |
| Gradle Kotlin DSL | ✅ 완료 |
| MariaDB | ✅ 완료 |
| Common Infrastructure | ✅ 완료 |
| Security | ✅ 완료 |
| JWT | ✅ 완료 |
| Authentication | ✅ 완료 |
| Authorization / Role | ✅ 완료 |
| Swagger / OpenAPI | ✅ 완료 |
| Project CRUD | ✅ 완료 |
| Project Search | ✅ 완료 |
| Project Dashboard API | ✅ 완료 |
| WBS | ⏳ 예정 |
| Schedule | ⏳ 예정 |
| Issue | ⏳ 예정 |
| Risk | ⏳ 예정 |
| Change | ⏳ 예정 |
| CMDB | ⏳ 예정 |
| Report | ⏳ 예정 |
| Spring AI | ⏳ 예정 |

---

## Frontend

| 영역 | 상태 |
|---|---|
| React | ✅ 완료 |
| Vite | ✅ 완료 |
| TypeScript | ✅ 완료 |
| React Router | ✅ 완료 |
| Feature Directory | ✅ 완료 |
| MainLayout | ✅ 완료 |
| Header | ✅ 완료 |
| Sidebar | ✅ 완료 |
| Breadcrumb | ✅ 완료 |
| Button | ✅ 완료 |
| Card | ✅ 완료 |
| Loading | ✅ 완료 |
| EmptyState | ✅ 완료 |
| Dashboard Skeleton | 🚧 진행 중 |
| API Integration | ⏳ 예정 |
| Authentication UI | ⏳ 예정 |
| Project UI | ⏳ 예정 |
| WBS UI | ⏳ 예정 |
| Schedule UI | ⏳ 예정 |
| Issue UI | ⏳ 예정 |
| Risk UI | ⏳ 예정 |
| Report UI | ⏳ 예정 |

---

# 14. 개발 전략

전체 개발은 Backend와 Frontend를 병렬적으로 진행한다.

```text
Backend
  ↓
REST API
  ↓
Frontend
  ↓
UI Integration
  ↓
E2E Verification
```

기능 개발 단위:

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
```

---

# 15. Git 전략

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

Branch 예:

```text
feature/common
feature/security
feature/auth-jwt
feature/auth-login
feature/project-search
feature/project-dashboard
feature/frontend-layout
feature/frontend-components
feature/wbs
feature/schedule
feature/issue
feature/risk
feature/change
feature/cmdb
feature/dashboard
feature/report
feature/spring-ai
```

---

# 16. 문서 관리

주요 문서는 다음과 같이 관리한다.

```text
docs
│
├── _meta
│   └── CHANGELOG.md
│
├── PROJECT_OVERVIEW.md
├── DEVELOPMENT_ROADMAP.md
├── ARCHITECTURE.md
└── PORTFOLIO.md
```

Frontend 개발 문서:

```text
docs/development/frontend
├── 001_frontend_architecture.md
├── 002_frontend_folder_structure.md
├── 003_frontend_coding_convention.md
├── 004_frontend_ui_design.md
├── 005_frontend_component_architecture.md
└── 006_frontend_development_history.md
```

문서와 소스코드의 상태를 일치시키는 것을 원칙으로 한다.

---

# 17. 향후 개발 계획

## Phase 1 — Frontend Foundation

현재 진행 영역:

- Dashboard UI
- Dashboard Widgets
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
- Gantt
- Progress Visualization

---

## Phase 3 — Issue / Risk / Change

- Issue Management
- Risk Management
- Change Management
- Issue Board
- Risk Matrix
- Change Workflow

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
- LLM Integration
- Prompt Management
- RAG
- Project Summary
- Risk Analysis
- Schedule Analysis
- Report Generation
- Meeting Summary
- Action Item Generation

---

# 18. 기대 효과

PMIS 구축을 통해 다음 효과를 목표로 한다.

## 업무 측면

- PMO 업무 표준화
- 프로젝트 데이터 통합
- 프로젝트 현황 가시화
- 반복적인 보고 업무 감소
- 산출물 및 증적 관리 체계화

## 기술 측면

- Enterprise Spring Boot Architecture 경험
- React / TypeScript Frontend 경험
- REST API 설계 및 연계
- Security / JWT 구현
- Database Modeling
- Git Flow 기반 개발
- AI 서비스 통합

## 포트폴리오 측면

단순 CRUD 프로젝트가 아닌 **실제 PMO 업무 도메인과 Software Engineering을 결합한 Enterprise Web Application**을 목표로 한다.

---

# 19. Repository 역할

Repository의 각 영역은 다음 목적을 가진다.

| Directory | Role |
|---|---|
| `pmis-backend` | Spring Boot REST API |
| `pmis-frontend` | React Web Application |
| `docs` | 프로젝트 설계 / 개발 / 운영 문서 |

---

# 20. Document History

| Version | Date | Author | Description |
|---|---|---|---|
| 1.0 | 2026-08-03 | Seo Seokhyeon | Initial Project Overview |
| 1.1 | 2026-08-08 | Seo Seokhyeon | Updated Backend/Frontend architecture and current implementation status |

---

# End of Document
