# PMIS Project Overview

> **Project:** Project Management Information System (PMIS)  
> **Version:** 1.3  
> **Last Updated:** 2026-08-11  
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
- 증적(Evidence) 관리
- CMDB(Configuration Management Database)
- Dashboard
- Report
- 파일 및 산출물 관리
- 운영 인수인계

PMIS의 핵심 방향은 단순한 일정관리 또는 CRUD 시스템을 넘어, **프로젝트 실행 데이터 → 관리 활동 → 산출물/증적 → 검수 → 보고**가 하나의 흐름으로 연결되도록 하는 것이다.

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
- 구축 및 검수 관리
- 증적 및 산출물 관리
- CMDB 관리
- Dashboard 및 Report
- 파일 관리

## 2.2 PMO 업무 시스템화

문서와 Excel 중심으로 수행되는 PMO 업무를 데이터 기반 시스템으로 전환한다.

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
Evidence / Deliverable
   ↓
Inspection
   ↓
Dashboard / Report
   ↓
Project Closure
```

이를 통해 프로젝트 현황뿐 아니라 **업무 수행 여부와 완료 사실을 증명할 수 있는 증적의 존재 여부까지 추적**할 수 있도록 한다.

## 2.3 증적 기반 PMO 관리

증적(Evidence)은 특정 업무, 작업, 산출물 또는 검수 항목이 실제로 수행되었거나 완료되었음을 확인할 수 있는 자료이다.

예:

- 작업 결과 화면 캡처
- 설치 결과서
- 테스트 결과서
- 검수 결과서
- 로그 파일
- 시스템 화면
- 회의록
- 승인 문서
- 이메일/공문
- Excel 결과자료
- PDF 보고서
- 산출물
- 현장 작업자료

증적 관리 프로세스:

```text
업무 생성
   ↓
증적 필요 여부 판단
   ↓
증적 요구사항 등록
   ↓
업무 수행
   ↓
완료 처리
   ↓
증적 등록
   ↓
증적 검토
   ├── 유효 ─────────→ 완료
   ├── 보완 필요 ────→ 증적 재등록
   └── 반려 ─────────→ 재검토
```

증적 상태:

```text
NOT_REQUIRED  증적 불필요
REQUIRED      증적 필요
MISSING       증적 누락
REGISTERED    증적 등록
UNDER_REVIEW  검토 중
VALID         유효
REVISION_REQUIRED 보완 필요
REJECTED      반려
```

PMIS는 업무 상태와 증적 상태를 분리하여 관리한다.

```text
Task Status
  예정 / 진행 / 완료 / 보류

Evidence Status
  불필요 / 필요 / 누락 / 등록 / 검토중 / 유효 / 보완 / 반려
```

## 2.4 AI 기반 PMIS 확장

PMIS Core 기능이 안정화된 이후 Spring AI를 적용한다.

예정 기능:

- 프로젝트 상태 요약
- 일정 분석
- 일정 지연 분석
- 리스크 분석
- 이슈 요약
- 변경 영향도 분석
- 증적 누락 탐지
- 증적 및 산출물 요약
- 회의록 요약
- Action Item 추출
- 주간보고서 초안 생성
- 프로젝트 질의응답(Chat)

---

# 3. 주요 기능

## 3.1 프로젝트 관리

### 현재 구현

- 프로젝트 생성 / 조회 / 상세 조회 / 수정 / 삭제
- 프로젝트 검색 및 정렬
- Validation
- Project Dashboard API
- Project Service / Repository
- Specification 기반 검색
- API Response 표준화
- Project Detail API
- Project Controller / Detail Controller 분리

### Project API

```text
POST   /api/projects
GET    /api/projects
GET    /api/projects/{projectId}
PUT    /api/projects/{projectId}
DELETE /api/projects/{projectId}
GET    /api/projects/{projectId}/detail
```

### Controller 구조

```text
project
└── controller
    ├── ProjectController
    │   ├── create
    │   ├── get
    │   ├── search
    │   ├── update
    │   └── delete
    │
    └── ProjectDetailController
        └── getProjectDetail
```

## 3.2 Dashboard

현재 Frontend Dashboard는 Foundation에서 실제 PMO Dashboard로 확장하는 단계이다.

주요 방향:

- KPI Card
- Project Overview
- Progress Summary
- Issue Summary
- Risk Summary
- Schedule Summary
- Evidence Status
- Milestone Status
- PMO 현황
- 구축/인프라 진행 현황

```text
Project Dashboard
 ├── Overall Progress
 ├── Schedule Status
 ├── Issue Status
 ├── Risk Status
 ├── Change Status
 ├── Evidence Status
 ├── Milestone
 └── Recent Activities
```

## 3.3 WBS 관리

예정 기능:

- WBS CRUD
- 계층형 Tree Structure
- 담당자 관리
- 작업 상태
- 진척률 계산
- 일정 연계
- 산출물 연계
- 증적 요구사항 연계

## 3.4 일정 관리

예정 기능:

- 일정 등록 / 변경
- 일정 지연 관리
- 진척률 관리
- Calendar
- Gantt
- Milestone
- Baseline
- 계획 / 실제 일정 비교

## 3.5 이슈 관리

예정 기능:

- 이슈 등록 / 수정 / 종료
- 담당자 / 우선순위 / 상태 관리
- 처리 이력
- 첨부파일
- WBS / 일정 연계
- 증적 연계

## 3.6 리스크 관리

예정 기능:

- Risk 등록
- Probability / Impact
- Risk Score
- Risk Matrix
- 대응 전략 / 결과
- Risk Monitoring
- Issue / 증적 연계

## 3.7 변경 관리

예정 기능:

- Change Request
- 변경 영향도 분석
- 승인 프로세스
- 변경 이력
- 변경 전/후 비교
- 일정 / WBS / 산출물 영향
- 증적 연계

## 3.8 구축 관리

PMO 실무를 반영한 구축 관리 영역이다.

- 설치 대상 관리
- HW/SW 설치 일정
- 서버 계정 요청
- 설치 대상 서버 관리
- 환경 구성
- 구축 진행률
- 설치 결과
- 협력사 작업 관리
- 구축 이슈 관리
- 작업 결과 증적 관리

```text
Installation Target
        ↓
Installation Schedule
        ↓
Vendor / Engineer Assignment
        ↓
Installation
        ↓
Installation Result
        ↓
Evidence Registration
        ↓
Inspection
```

## 3.9 검수 관리

- 검수 요청
- 검수 대상 / 항목
- 검수 결과
- 검수 산출물
- 증적 관리
- 보완 요청
- 재검수
- 완료 보고

## 3.10 증적(Evidence) 관리

증적은 다음 Domain과 연계한다.

```text
WBS / Task
Issue
Risk
Change
Construction
Inspection
Deliverable
       │
       ▼
    Evidence
       │
       ▼
      File
```

### Evidence Completeness Check

프로젝트 또는 WBS 단위로 증적 완결성을 집계한다.

```text
Evidence Completeness

전체 대상       120
증적 필요       100
증적 등록        92
증적 검토 완료   85
증적 누락         8
보완 필요         7

Completeness = 92%
Validated     = 85%
Missing       = 8
Revision      = 7
```

### 증적 관리 화면

```text
┌─────────────────────────────────────────────────────┐
│ Evidence Management                                 │
├─────────────────────────────────────────────────────┤
│ Project: 차세대 인프라 구축                           │
│                                                     │
│ [전체] [증적필요] [누락] [검토중] [유효] [보완필요]   │
│                                                     │
│ WBS       Task              Status     Evidence      │
│ ─────────────────────────────────────────────────── │
│ 1.1       서버 설치           완료       ✓ 유효       │
│ 1.2       DB 설치             완료       ⚠ 누락       │
│ 1.3       Middleware 설치     진행       -            │
│ 1.4       테스트              완료       ◐ 검토중     │
│                                                     │
│                         [증적 등록] [검토] [상세]     │
└─────────────────────────────────────────────────────┘
```

핵심 목적은 **완료된 업무 중 증적이 없는 항목을 즉시 식별**하고, 프로젝트 종료 또는 검수 전에 증적 누락을 제거하는 것이다.

## 3.11 CMDB 관리

예정 기능:

- Server CI
- Database CI
- Software CI
- Network CI
- CI Relationship
- Dependency
- Configuration History
- Audit Log

## 3.12 보고 관리

예정 기능:

- 주간 / 월간 보고
- 프로젝트 현황 보고
- 회의록
- 의사결정 관리
- 증적 현황 보고
- 검수 현황 보고
- Excel / PDF Export

## 3.13 운영 인수인계

예정 기능:

- 운영 문서
- 시스템 구성 정보
- 교육 자료
- 운영 담당자
- 인수인계 체크리스트
- 미해결 이슈 / 리스크
- 증적 및 산출물 확인
- 종료 보고서

---

# 4. AI 기능

Spring AI를 기반으로 다음 기능을 단계적으로 구현한다.

## Project Analysis

- 프로젝트 상태 요약
- 진행률 분석
- 일정 상태 분석
- 지연 가능성 분석

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

## Evidence Analysis

- 증적 누락 탐지
- 증적 메타데이터 분석
- 증적 내용 요약
- 검수자료 적합성 보조 판단
- 산출물 간 내용 비교

AI 판단은 PMO 담당자의 검토를 전제로 하며, 시스템이 최종 검수 결정을 자동으로 내리는 구조는 지양한다.

## Report Automation

- 주간보고 초안 생성
- 월간보고 초안 생성
- 회의록 요약
- 프로젝트 상태 보고
- Evidence Status Report 생성

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
Project / WBS / Issue / Risk / Evidence Data
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

## AI / Future

- Spring AI
- OpenAI
- Azure OpenAI
- Ollama
- RAG
- Vector Database
- MCP
- Multi-Agent Architecture

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

```text
project-delivery-reference
│
├── docs
│   ├── _meta
│   │   ├── CHANGELOG.md
│   │   ├── PROJECT_OVERVIEW.md
│   │   ├── DEVELOPMENT_ROADMAP.md
│   │   ├── ARCHITECTURE.md
│   │   └── PORTFOLIO.md
│   ├── design
│   │   ├── issue
│   │   ├── project
│   │   ├── report
│   │   ├── schedule
│   │   ├── server
│   │   └── wbs
│   └── development
│       └── frontend
│
├── pmis-backend
└── pmis-frontend
```

---

# 7. Backend 구조

```text
com.seos.pmis
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

향후 증적 관리가 독립 Domain으로 구현될 경우:

```text
evidence
├── controller
├── dto
├── entity
├── mapper
├── repository
└── service
```

Evidence Domain은 증적 파일 자체보다 **증적 요구사항, 대상 업무, 검토 상태, 검수 관계, 이력** 등의 업무 메타데이터를 관리하고 실제 파일은 File 영역과 연계한다.

---

# 8. Frontend 구조

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
    │   ├── evidence
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

현재 구현된 Layout:

```text
components/layout
├── Header.tsx
├── Sidebar.tsx
└── Breadcrumb.tsx
```

현재 구현된 Common UI:

```text
components/common
├── Button.tsx
├── Card.tsx
├── Loading.tsx
└── EmptyState.tsx
```

---

# 9. 현재 Frontend 화면 구조

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

Dashboard는 Skeleton에서 PMO Dashboard Component를 확장하는 단계이다.

Project 화면:

```text
Project
 ├── Project List
 ├── Project Search
 ├── Project Detail
 └── Project Dashboard
```

Evidence 화면:

```text
Evidence
 ├── Evidence Dashboard
 ├── Evidence List
 ├── Missing Evidence
 ├── Evidence Review
 └── Evidence Detail
```

Evidence Detail:

```text
Evidence Detail
 ├── 대상 프로젝트
 ├── WBS / Task
 ├── 증적 요구 여부
 ├── 증적 상태
 ├── 등록자 / 등록일
 ├── 파일
 ├── 설명
 ├── 검토자 / 검토일
 ├── 검토 의견
 └── 관련 검수 항목
```

---

# 10. 시스템 아키텍처

```text
┌──────────────────────────────┐
│          Browser             │
│      React + TypeScript      │
└──────────────┬───────────────┘
               │
               │ HTTPS / REST API
               ▼
┌──────────────────────────────┐
│       Spring Boot API        │
│                              │
│ Controller                   │
│      ↓                       │
│ Service                      │
│      ↓                       │
│ Repository                   │
└──────────────┬───────────────┘
               │
               ├───────────────┐
               ▼               ▼
        ┌─────────────┐   ┌──────────────┐
        │   MariaDB   │   │ File Storage │
        └─────────────┘   └──────────────┘
               │               │
               └───────┬───────┘
                       │
                    PMIS Data
                       │
                     Future
                       ▼
                ┌─────────────┐
                │  Spring AI  │
                └──────┬──────┘
                       ▼
                   LLM / RAG
```

File과 Evidence는 역할을 구분한다.

- **File:** 실제 파일 저장 및 파일 메타데이터
- **Evidence:** 특정 업무 또는 검수 대상에 연결된 증적 업무 정보

```text
File
 ↓
Evidence
 ↓
Task / Issue / Risk / Change / Inspection
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

향후 Evidence 권한을 별도 Permission으로 세분화할 수 있다.

```text
EVIDENCE_REGISTER
EVIDENCE_REVIEW
EVIDENCE_APPROVE
EVIDENCE_ADMIN
```

---

# 12. 개발 원칙

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
- Specification Pattern
- Mapper Pattern
- Global Exception Handling
- Standard API Response
- API 책임에 따른 Controller 분리

## Frontend

- Component-based Architecture
- Reusable Components
- Layout / Page 분리
- Feature-based Directory
- TypeScript 기반 타입 안정성

## PMO Domain

- 업무와 증적의 분리
- 업무 상태와 증적 상태의 분리
- 검수와 증적 검토의 분리
- 이력 추적 가능성 확보
- 프로젝트 종료 시 데이터 완결성 확보

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
| Project Detail API | ✅ 완료 |
| Project Controller 분리 | ✅ 완료 |
| WBS | ⏳ 예정 |
| Schedule | ⏳ 예정 |
| Issue | ⏳ 예정 |
| Risk | ⏳ 예정 |
| Change | ⏳ 예정 |
| Evidence | ⏳ 설계 예정 |
| Inspection | ⏳ 예정 |
| CMDB | ⏳ 예정 |
| Report | ⏳ 예정 |
| Spring AI | ⏳ 예정 |

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
| Dashboard KPI / PMO Widgets | 🚧 진행 중 |
| Project UI | ⏳ 예정 |
| API Integration | ⏳ 예정 |
| Authentication UI | ⏳ 예정 |
| Evidence UI | ⏳ 예정 |
| Evidence Dashboard | ⏳ 예정 |
| Evidence Review UI | ⏳ 예정 |
| WBS UI | ⏳ 예정 |
| Schedule UI | ⏳ 예정 |
| Issue UI | ⏳ 예정 |
| Risk UI | ⏳ 예정 |
| Report UI | ⏳ 예정 |

---

# 14. 개발 전략

```text
Backend
   ↓
REST API
   ↓
API Verification
   ↓
Frontend
   ↓
UI Integration
   ↓
Browser Test
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
   ↓
Integration Test
```

Evidence 기능은 다음 Vertical Slice를 목표로 한다.

```text
Evidence Requirement
        ↓
Evidence Registration
        ↓
Evidence File Upload
        ↓
Evidence Review
        ↓
Missing Evidence Check
        ↓
Evidence Dashboard
        ↓
Inspection
```

---

# 15. Git 전략

```text
main
 │
develop
 │
 ├── feature/*
 ├── release/*
 └── hotfix/*
```

주요 개발 흐름:

```text
feature/project-crud
        ↓
develop
        ↓
feature/project-search
        ↓
develop
        ↓
feature/project-detail
        ↓
develop
        ↓
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

현재 통합 Branch는 `develop`이다.

주요 Branch 예:

```text
feature/common
feature/security
feature/auth-jwt
feature/auth-login
feature/auth-refresh
feature/auth-role
feature/swagger
feature/project-search
feature/project-dashboard
feature/project-detail
feature/frontend-layout
feature/frontend-components
feature/frontend-dashboard-hmc-reference
feature/wbs
feature/schedule
feature/issue
feature/risk
feature/change
feature/cmdb
feature/evidence
feature/inspection
feature/report
feature/spring-ai
```

---

# 16. 문서 관리

```text
docs
│
├── _meta
│   ├── CHANGELOG.md
│   ├── PROJECT_OVERVIEW.md
│   ├── DEVELOPMENT_ROADMAP.md
│   ├── ARCHITECTURE.md
│   └── PORTFOLIO.md
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

문서와 소스코드의 상태를 일치시키는 것을 원칙으로 한다.

---

# 17. 향후 개발 계획

## Phase 1 — Frontend Foundation & Project Integration

- Dashboard UI
- Dashboard Widgets
- KPI Card
- Project Overview
- Issue Summary
- PMO Dashboard
- Table
- Form
- Modal
- Pagination
- API Client
- Error Handling
- Project List / Search / Detail / Dashboard
- Backend API Integration

## Phase 2 — WBS & Schedule

### Backend

- WBS CRUD
- WBS Tree Structure
- Schedule CRUD
- Progress Calculation
- Calendar API
- Gantt Data
- Milestone
- Baseline

### Frontend

- WBS Tree
- Schedule List
- Calendar
- Gantt
- Progress Visualization

## Phase 3 — Issue / Risk / Change

- Issue Management
- Risk Management
- Change Management
- Issue Board
- Risk Matrix
- Change Workflow
- History
- 증적 연계

## Phase 4 — 구축 / 검수 / 증적

### Construction

- Installation Target
- Installation Schedule
- HW/SW Installation
- Vendor Task
- Installation Result
- Construction Progress

### Inspection

- Inspection Checklist
- Inspection Target
- Inspection Result
- Review Workflow
- Completion Report

### Evidence

- Evidence Requirement
- Evidence Registration
- Evidence File Upload
- Evidence Review
- Missing Evidence Detection
- Evidence Completeness
- Evidence Dashboard
- Evidence History
- Evidence / Inspection Relationship

핵심 흐름:

```text
WBS / Task
   ↓
Work Execution
   ↓
Completion
   ↓
Evidence Required?
   ├── No → Complete
   └── Yes
        ↓
   Evidence Registered?
        ├── No → Missing
        └── Yes
             ↓
         Review
          ├── Valid
          ├── Revision Required
          └── Rejected
```

## Phase 5 — CMDB

- Server CI
- Database CI
- Software CI
- Network CI
- CI Relationship
- Configuration History
- Audit Log

## Phase 6 — Dashboard & Report

- Executive Dashboard
- Project Dashboard
- Schedule Dashboard
- Issue Dashboard
- Risk Dashboard
- Evidence Dashboard
- Inspection Dashboard
- Weekly / Monthly Report
- Evidence Status Report
- Excel / PDF Export

## Phase 7 — AI PM Assistant

- Spring AI
- LLM Integration
- Prompt Management
- RAG
- Project Summary
- Risk Analysis
- Schedule Analysis
- Evidence Analysis
- Report Generation
- Meeting Summary
- Action Item Generation
- Project Chat

---

# 18. Portfolio Demonstration Scenario

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
8. 업무 완료 처리
   ↓
9. 증적 필요 여부 확인
   ↓
10. 증적 등록
   ↓
11. 증적 검토
   ↓
12. 증적 누락 확인
   ↓
13. Inspection
   ↓
14. Dashboard 집계
   ↓
15. Weekly Report 생성
   ↓
16. AI Project Summary
```

핵심 연결 구조:

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
Evidence / Deliverable
  ↓
Inspection
  ↓
Dashboard / Report
  ↓
AI Analysis
```

---

# 19. 기대 효과

## 업무 측면

- PMO 업무 표준화
- 프로젝트 데이터 통합
- 프로젝트 현황 가시화
- 반복적인 보고 업무 감소
- 산출물 및 증적 관리 체계화
- 증적 누락 조기 탐지
- 검수 준비 업무 효율화
- 프로젝트 종료 시 데이터 완결성 확보

## 기술 측면

- Enterprise Spring Boot Architecture 경험
- React / TypeScript Frontend 경험
- REST API 설계 및 연계
- Security / JWT 구현
- Database Modeling
- Git Flow 기반 개발
- File / Evidence Management
- AI 서비스 통합

## 포트폴리오 측면

단순 CRUD 프로젝트가 아닌 **실제 PMO 업무 도메인과 Software Engineering을 결합한 Enterprise Web Application**을 목표로 한다.

특히 Backend API 설계부터 Frontend Integration, 인증/인가, Dashboard, 증적 관리, 문서화, Git Flow까지 실제 SI 프로젝트의 개발 프로세스를 적용하는 것을 중요한 목표로 한다.

---

# 20. Repository 역할

| Directory | Role |
|---|---|
| `pmis-backend` | Spring Boot REST API |
| `pmis-frontend` | React Web Application |
| `docs` | 프로젝트 설계 / 개발 / 운영 문서 |

---

# 21. Document History

| Version | Date | Author | Description |
|---|---|---|---|
| 1.0 | 2026-08-03 | Seo Seokhyeon | Initial Project Overview |
| 1.1 | 2026-08-08 | Seo Seokhyeon | Updated Backend/Frontend architecture and implementation status |
| 1.2 | 2026-08-10 | Seo Seokhyeon | Updated Project Management implementation, separated Project Detail API, and synchronized develop integration status |
| 1.3 | 2026-08-11 | Seo Seokhyeon | Added PMO evidence management, evidence completeness workflow, inspection linkage, and current Dashboard/Frontend development status |

---

# End of Document
