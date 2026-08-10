# PMIS Architecture

> Version: **1.1**  
> Project: **Project Management Information System (PMIS)**  
> Backend Framework: **Spring Boot 3.5.x**  
> Backend Language: **Java 21**  
> Frontend Framework: **React + TypeScript**  
> Frontend Build Tool: **Vite**  
> Database: **MariaDB**  
> Authentication: **Spring Security + JWT**  
> AI: **Spring AI**  
> Last Updated: **2026-08-10**

---

# 1. Architecture Overview

PMIS는 **Backend와 Frontend를 분리한 Enterprise Web Application Architecture**를 기반으로 설계한다.

설계 목표는 다음과 같다.

- 유지보수가 쉬운 구조
- Backend / Frontend 책임 분리
- 기능별 Domain 및 Feature 분리
- 재사용 가능한 공통 Component 구성
- REST API 기반 Frontend / Backend 연계
- 확장 가능한 인증 및 권한 구조
- AI 기능의 독립적인 확장
- 실제 SI 프로젝트 수준의 설계
- 포트폴리오 및 향후 운영을 고려한 구조

전체 시스템은 다음과 같이 구성한다.

```text
                        PMIS Web Client
                              │
                              ▼
                 ┌────────────────────────┐
                 │ React + TypeScript     │
                 │ Vite                   │
                 ├────────────────────────┤
                 │ Router                 │
                 │ Layout                 │
                 │ Pages                  │
                 │ Features               │
                 │ Components             │
                 │ API / Services         │
                 │ State / Hooks           │
                 └────────────┬───────────┘
                              │
                         HTTPS / REST
                              │
                              ▼
                 ┌────────────────────────┐
                 │ Spring Boot 3.5.x API  │
                 ├────────────────────────┤
                 │ Controller             │
                 │ Service                │
                 │ Repository             │
                 │ Security / JWT         │
                 │ Domain Modules         │
                 │ Common Infrastructure  │
                 │ Spring AI              │
                 └────────────┬───────────┘
                              │
                              ▼
                           MariaDB
```

---

# 2. System Architecture

## 2.1 Logical Architecture

```text
┌──────────────────────────────────────────────────────────┐
│                    PMIS Web Browser                      │
│                                                          │
│  ┌────────────────────────────────────────────────────┐  │
│  │              React Frontend                        │  │
│  │                                                    │  │
│  │  App                                               │  │
│  │   ↓                                                │  │
│  │  AppRouter                                          │  │
│  │   ↓                                                │  │
│  │  MainLayout                                         │  │
│  │   ├── Header                                        │  │
│  │   ├── Sidebar                                       │  │
│  │   ├── Breadcrumb                                    │  │
│  │   └── Outlet / Page                                 │  │
│  │                                                    │  │
│  │  Features / Components / API / Services            │  │
│  └──────────────────────┬─────────────────────────────┘  │
└─────────────────────────┼────────────────────────────────┘
                          │
                    HTTPS / REST API
                          │
                          ▼
┌──────────────────────────────────────────────────────────┐
│                 Spring Boot Backend                      │
│                                                          │
│  Security Filter Chain                                   │
│          ↓                                               │
│  Authentication / Authorization                          │
│          ↓                                               │
│  Controller                                               │
│          ↓                                               │
│  Service                                                  │
│          ↓                                               │
│  Repository                                               │
│          ↓                                               │
│  MariaDB                                                  │
│                                                          │
│  Spring AI                                                │
│      ↓                                                    │
│  LLM / AI Provider                                        │
└──────────────────────────────────────────────────────────┘
```

---

# 3. Technology Stack

## 3.1 Backend

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.x |
| Build | Gradle Kotlin DSL |
| ORM | Spring Data JPA |
| Database | MariaDB |
| Security | Spring Security |
| Authentication | JWT |
| Validation | Jakarta Validation |
| API | RESTful API |
| Documentation | Swagger / OpenAPI |
| AI | Spring AI |
| Version Control | Git |
| CI/CD | GitHub Actions |
| Container | Docker |

## 3.2 Frontend

| Category | Technology |
|---|---|
| Framework | React |
| Language | TypeScript |
| Build Tool | Vite |
| Package Manager | npm |
| Router | React Router DOM |
| API Communication | REST API |
| Styling | Component / CSS 기반 확장 예정 |
| State Management | 확장 예정 |
| Testing | 확장 예정 |

Frontend는 Backend와 독립적인 프로젝트로 관리한다.

```text
project-delivery-reference
│
├── pmis-backend
├── pmis-frontend
└── docs
```

---

# 4. Repository Architecture

전체 Repository 구조는 다음을 기준으로 한다.

```text
project-delivery-reference
│
├── pmis-backend
│   └── src
│
├── pmis-frontend
│   ├── src
│   ├── package.json
│   ├── package-lock.json
│   ├── tsconfig.app.json
│   └── vite.config.ts
│
├── docs
│   ├── _meta
│   ├── architecture
│   ├── design
│   └── development
│
└── README.md
```

Backend와 Frontend는 하나의 Repository 안에서 관리하지만 실행 및 책임 영역은 분리한다.

---

# 5. Backend Architecture

## 5.1 Backend Package Structure

```text
com.seos.pmis

├── auth
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── mapper
│   ├── repository
│   ├── security
│   │   ├── filter
│   │   ├── handler
│   │   ├── jwt
│   │   └── principal
│   └── service
│
├── project
├── schedule
├── wbs
├── issue
├── risk
├── change
├── cmdb
├── report
├── dashboard
├── file
├── user
│
├── batch
│
├── common
│   ├── annotation
│   ├── constant
│   ├── dto
│   ├── entity
│   ├── enums
│   ├── exception
│   ├── response
│   ├── util
│   └── validation
│
└── config
```

---

# 6. Backend Layer Architecture

기본 Backend 처리 흐름은 다음과 같다.

```text
HTTP Request
      │
      ▼
Security Filter
      │
      ▼
Controller
      │
      ▼
Service
      │
      ▼
Repository
      │
      ▼
MariaDB
```

## 6.1 Controller

책임:

- REST API 제공
- Request DTO 수신
- Validation
- Service 호출
- Response 반환

Controller에서는 핵심 Business Logic을 직접 처리하지 않는다.

---

## 6.2 Service

책임:

- Business Logic
- Transaction 처리
- Domain 간 처리
- Repository 조합
- BusinessException 발생

---

## 6.3 Repository

책임:

- Entity Persistence
- JPA Query
- Specification
- 향후 QueryDSL 확장

---

## 6.4 Database

MariaDB를 기본 관계형 데이터베이스로 사용한다.

JPA/Hibernate를 통해 Domain Entity와 Database를 연결한다.

---

# 7. Frontend Architecture

Frontend는 React 기반의 Component / Feature Architecture를 적용한다.

핵심 구조는 다음과 같다.

```text
React Application
      │
      ▼
App
      │
      ▼
AppRouter
      │
      ▼
MainLayout
      │
      ├── Header
      ├── Sidebar
      ├── Breadcrumb
      │
      └── Outlet
             │
             └── Page
                    │
                    └── Feature / Component
```

---

# 8. Frontend Directory Structure

현재 Frontend 구조는 다음을 기준으로 한다.

```text
src
│
├── api
├── assets
│
├── components
│   ├── common
│   │   ├── Button.tsx
│   │   ├── Card.tsx
│   │   ├── EmptyState.tsx
│   │   └── Loading.tsx
│   │
│   ├── form
│   │
│   └── layout
│       ├── Header.tsx
│       ├── Sidebar.tsx
│       └── Breadcrumb.tsx
│
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
│
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

---

# 9. Frontend Component Architecture

## 9.1 Layout Components

Layout Component는 전체 Application의 공통 화면 구조를 담당한다.

현재 구성:

```text
components/layout
│
├── Header.tsx
├── Sidebar.tsx
└── Breadcrumb.tsx
```

### Header

책임:

- PMIS 서비스 Header
- 사용자 영역
- 향후 알림 / 사용자 메뉴 확장

### Sidebar

책임:

- 주요 기능 Navigation
- Dashboard
- Project
- WBS
- Schedule
- Issue
- Risk
- Change
- CMDB
- Report
- Admin

### Breadcrumb

책임:

- 현재 Route 기반 화면 위치 표시
- 상위 / 하위 메뉴 관계 표현

---

## 9.2 Common Components

공통 UI Component는 여러 Feature에서 재사용한다.

현재 구성:

```text
components/common
│
├── Button.tsx
├── Card.tsx
├── EmptyState.tsx
└── Loading.tsx
```

원칙:

- 특정 Domain에 종속되지 않는다.
- 재사용 가능한 UI만 Common에 둔다.
- Domain 전용 UI는 해당 Feature에 둔다.

---

# 10. Frontend Routing Architecture

React Router DOM을 사용한다.

현재 구조:

```text
App
 │
 ▼
AppRouter
 │
 ▼
MainLayout
 │
 ├── Dashboard
 ├── Project
 ├── Task
 ├── Schedule
 ├── Issue
 ├── Report
 ├── CMDB
 └── Admin
```

향후 인증 상태에 따라 Protected Route를 적용한다.

예상 구조:

```text
App
 │
 ▼
AppRouter
 │
 ├── Public Routes
 │    └── Login
 │
 └── Protected Routes
      │
      └── MainLayout
           ├── Dashboard
           ├── Project
           ├── WBS
           ├── Schedule
           ├── Issue
           ├── Risk
           ├── Change
           ├── CMDB
           ├── Report
           └── Admin
```

---

# 11. Frontend API Architecture

Frontend는 Backend REST API와 통신한다.

```text
Page / Feature
      │
      ▼
Service
      │
      ▼
API
      │
      ▼
HTTP Request
      │
      ▼
Spring Boot REST API
```

역할 분리:

- `api`: HTTP API 호출 및 API Client
- `services`: Business-oriented Frontend Service
- `types`: Request / Response Type
- `hooks`: 재사용 가능한 React Logic
- `store`: 전역 상태 관리
- `utils`: 공통 Utility

---

# 12. Frontend State Management

현재 초기 구조에서는 전역 상태 관리 영역만 확보하고 실제 구현은 기능 요구에 따라 확장한다.

향후 관리 대상:

- Authentication State
- Current User
- Access Token
- Project Context
- UI State
- Notification State

상태 관리 Library는 실제 요구사항을 확인한 후 결정한다.

---

# 13. Security Architecture

PMIS는 Spring Security + JWT 기반 Stateless Authentication을 사용한다.

```text
Client
  │
  │ Authorization: Bearer <JWT>
  ▼
JWT Authentication Filter
  │
  ▼
Authentication
  │
  ▼
Authorization
  │
  ▼
Controller
```

주요 구성:

- SecurityConfig
- JwtProvider
- JwtAuthenticationFilter
- JwtAuthenticationEntryPoint
- JwtAccessDeniedHandler
- UserPrincipal
- BCrypt PasswordEncoder
- Stateless Session
- CORS

---

# 14. Authentication Flow

```text
Login Request
      │
      ▼
AuthenticationManager
      │
      ▼
UserDetailsService
      │
      ▼
User Authentication
      │
      ▼
JWT Access Token
      │
      ▼
Frontend
      │
      ▼
Authorization Header
      │
      ▼
JWT Filter
      │
      ▼
Protected API
```

Refresh Token을 이용한 Access Token 재발급 구조를 적용한다.

---

# 15. Common Module Architecture

공통 Backend 기능은 `common` 영역에서 관리한다.

```text
common
│
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

주요 구성:

- `BaseEntity`
- `ApiResponse`
- `ErrorCode`
- `BusinessException`
- `GlobalExceptionHandler`
- `JpaConfig`

---

# 16. Domain Modules

PMIS Backend는 업무 영역별 Domain Module로 구성한다.

## Authentication

- Login
- Logout
- JWT
- Refresh Token
- Role Authorization

## User

- 사용자
- 권한
- Role

## Project

- 프로젝트
- 고객사
- PM
- 프로젝트 상태
- 프로젝트 검색

## WBS

- WBS
- Task
- 진척률
- 작업 계층

## Schedule

- 일정
- Calendar
- Gantt
- Milestone

## Issue

- Issue 등록
- Assignment
- Status
- Priority
- Attachment

## Risk

- Risk 등록
- Risk Assessment
- Probability
- Impact
- Response Strategy

## Change

- Change Request
- Approval
- Change History
- Impact Analysis

## CMDB

- Server CI
- Database CI
- Software CI
- Network CI
- Relationship
- Version History

## Dashboard

- Project Dashboard
- Schedule Dashboard
- Issue Dashboard
- Risk Dashboard
- Change Dashboard
- CMDB Dashboard

## Report

- Weekly Report
- Monthly Report
- Executive Report
- Excel
- PDF
- Statistics

## File

- Attachment
- Upload
- Download
- Storage

---

# 17. Database Architecture

Domain 간 관계는 업무 요구사항에 따라 설계한다.

기본적인 업무 흐름은 다음과 같다.

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
 └── CMDB
        │
        ▼
      Report
```

모든 주요 Entity는 공통 Audit 정보를 관리한다.

기본 Audit 컬럼:

- createdAt
- updatedAt
- createdBy
- updatedBy

단, 실제 Entity별 요구사항에 따라 컬럼 구성은 조정한다.

---

# 18. Exception Handling Architecture

Backend의 예외 처리 흐름은 다음과 같다.

```text
Exception
    │
    ▼
BusinessException
    │
    ▼
GlobalExceptionHandler
    │
    ▼
ApiResponse
```

실패 응답 예시:

```json
{
  "success": false,
  "code": "PROJECT_NOT_FOUND",
  "message": "Project not found."
}
```

모든 API 오류는 일관된 Response 구조를 유지한다.

---

# 19. API Response Standard

## Success

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "Success",
  "data": {}
}
```

## Failure

```json
{
  "success": false,
  "code": "ERROR_CODE",
  "message": "Error Message"
}
```

Frontend는 해당 Response 구조를 기준으로 API 처리 로직을 구성한다.

---

# 20. AI Architecture

Spring AI는 Backend 내부에서 AI 기능을 제공하는 Application Layer로 사용한다.

```text
PMIS Frontend
      │
      ▼
REST API
      │
      ▼
Spring Boot
      │
      ▼
Spring AI
      │
      ├── Prompt
      ├── Chat Client
      ├── Context
      └── RAG
             │
             ▼
       LLM Provider
```

지원 예정 Provider:

- OpenAI
- Azure OpenAI
- Ollama
- 기타 LLM Provider

AI 기능:

- AI PM Assistant
- Project Summary
- Issue Summary
- Risk Analysis
- Schedule Analysis
- Change Impact Analysis
- Meeting Summary
- Action Item Generation
- Report Generation

---

# 21. AI Knowledge Architecture

향후 PMIS 문서를 AI Knowledge Base로 확장한다.

```text
PMIS Documents
      │
      ▼
Document Processing
      │
      ▼
Embedding
      │
      ▼
Vector Store
      │
      ▼
RAG
      │
      ▼
Spring AI
      │
      ▼
AI Assistant
```

대상 문서:

- Project Documents
- WBS
- Schedule
- Issue
- Risk
- Change
- Reports
- Meeting Notes

---

# 22. API Documentation

Swagger / OpenAPI를 통해 REST API를 문서화한다.

```text
Spring Boot
     │
     ▼
OpenAPI
     │
     ▼
Swagger UI
```

문서화 대상:

- Authentication API
- Project API
- WBS API
- Schedule API
- Issue API
- Risk API
- Change API
- CMDB API
- Report API
- Dashboard API

---

# 23. Deployment Architecture

향후 Docker 기반 배포 구조를 적용한다.

```text
GitHub
   │
   ▼
GitHub Actions
   │
   ▼
Docker Build
   │
   ├──────────────┐
   ▼              ▼
Frontend       Backend
Container      Container
   │              │
   │              ▼
   │           MariaDB
   │
   └─────── HTTPS / Reverse Proxy
```

향후 필요에 따라 Nginx 또는 Cloud Load Balancer를 적용할 수 있다.

---

# 24. CI/CD Architecture

```text
Developer
    │
    ▼
Feature Branch
    │
    ▼
Pull Request
    │
    ▼
Code Review
    │
    ▼
develop
    │
    ▼
CI
 ├── Build
 ├── Test
 ├── Lint
 └── Validation
    │
    ▼
release
    │
    ▼
main
    │
    ▼
Production
```

GitHub Actions를 기반으로 CI/CD를 구축한다.

---

# 25. Git Flow Architecture

```text
main
 │
 └── release/*
       │
       ▼
    develop
       │
       ├── feature/*
       │
       ├── bugfix/*
       │
       └── refactor/*
```

Branch 역할:

- `main`: 검증된 Production Release
- `develop`: Integration
- `feature/*`: Feature Development
- `release/*`: Release Preparation
- `hotfix/*`: Production Hot Fix

---

# 26. Design Principles

본 프로젝트는 다음 원칙을 따른다.

## Backend

- Layered Architecture
- Domain-Oriented Design
- SOLID
- Clean Code
- RESTful API
- DTO Pattern
- Mapper Pattern
- Specification Pattern
- Low Coupling
- High Cohesion

## Frontend

- Component Reusability
- Feature-based Organization
- Separation of Concerns
- Route-based Page Structure
- Common UI Component Reuse
- Type Safety
- API / Service Separation
- Layout / Page Separation

---

# 27. Architecture Evolution

현재 Architecture는 초기 Enterprise PMIS 구조를 기준으로 한다.

향후 시스템 규모와 요구사항에 따라 다음 기술을 검토한다.

- Redis
- QueryDSL
- Elasticsearch
- Kafka
- RabbitMQ
- Spring Batch
- Prometheus
- Grafana
- Kubernetes
- AWS ECS / EKS
- Object Storage
- S3
- Vector Database

단, 기술 도입은 실제 요구사항과 운영 비용을 고려하여 결정한다.

---

# 28. Current Architecture Status

## Backend

### Completed

- Spring Boot 3.5.x
- Java 21
- Gradle Kotlin DSL
- MariaDB
- Common Infrastructure
- Spring Security
- JWT Authentication
- Login
- Refresh Token
- Role Authorization
- Swagger / OpenAPI
- Project CRUD
- Project Search
- Project Dashboard API

### In Progress / Planned

- WBS
- Schedule
- Issue
- Risk
- Change
- CMDB
- Report
- AI

---

## Frontend

### Completed

- React
- Vite
- TypeScript
- npm
- React Router DOM
- AppRouter
- MainLayout
- Dashboard Skeleton
- Header
- Sidebar
- Breadcrumb
- Feature-based Directory Structure
- Common UI Components
  - Button
  - Card
  - Loading
  - EmptyState

### In Progress / Planned

- Authentication UI
- Login
- Protected Route
- API Client
- Project UI
- WBS UI
- Schedule UI
- Dashboard Widgets
- Theme
- State Management
- Form Components

---

# 29. Documentation Architecture

Architecture와 실제 구현 상태가 일치하도록 다음 문서를 지속적으로 관리한다.

```text
docs
│
├── _meta
│   └── CHANGELOG.md
│
├── architecture
│   └── ARCHITECTURE.md
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
    ├── frontend
    │   ├── 001_frontend_architecture.md
    │   ├── 002_frontend_folder_structure.md
    │   ├── 003_frontend_coding_convention.md
    │   ├── 004_frontend_ui_design.md
    │   ├── 005_frontend_component_architecture.md
    │   └── 006_frontend_development_history.md
    │
    └── DEVELOPMENT_ROADMAP.md
```

문서와 소스코드는 가능한 한 동일한 상태를 유지한다.

---

# 30. Document History

| Version | Date | Author | Description |
|---|---|---|---|
| 1.0 | 2026-08-03 | Seo Seokhyeon | Initial Backend Architecture Document |
| 1.1 | 2026-08-10 | Seo Seokhyeon | Added Frontend Architecture, updated Backend version, repository structure, security, API, AI, deployment and current implementation status |

---

**End of Document**
