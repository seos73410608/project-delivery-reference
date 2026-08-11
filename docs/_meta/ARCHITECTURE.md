# PMIS Architecture

> **Version:** 1.3  
> **Project:** **Project Management Information System (PMIS)**  
> **Backend Framework:** **Spring Boot 4.1.0**  
> **Backend Language:** **Java 21**  
> **Frontend Framework:** **React + TypeScript**  
> **Frontend Build Tool:** **Vite**  
> **Database:** **MariaDB**  
> **Authentication:** **Spring Security + JWT**  
> **AI:** **Spring AI (Planned)**  
> **Last Updated:** **2026-08-11**

---

# 1. Architecture Overview

PMIS는 **Backend와 Frontend를 분리한 Enterprise Web Application Architecture**를 기반으로 설계한다.

설계 목표는 다음과 같다.

* 유지보수가 쉬운 구조
* Backend / Frontend 책임 분리
* Domain 및 Feature 단위의 기능 분리
* 재사용 가능한 공통 Component 구성
* REST API 기반 Frontend / Backend 연계
* 확장 가능한 인증 및 권한 구조
* 프로젝트 관리 데이터의 중앙화
* 실제 SI 프로젝트 PMO 업무를 반영한 Domain Architecture
* 향후 AI 기능을 독립적으로 확장할 수 있는 구조
* 포트폴리오 및 향후 운영을 고려한 Enterprise 수준의 구조

전체 시스템은 다음과 같이 구성한다.

```text
                         PMIS Web Client
                               │
                               ▼
              ┌────────────────────────────┐
              │ React + TypeScript         │
              │ Vite                       │
              ├────────────────────────────┤
              │ App / Router               │
              │ Layout                     │
              │ Pages                      │
              │ Features                   │
              │ Components                 │
              │ API / Services             │
              │ Hooks / Store / Types      │
              └─────────────┬──────────────┘
                            │
                       HTTPS / REST
                            │
                            ▼
              ┌────────────────────────────┐
              │ Spring Boot 4.1.0 API      │
              ├────────────────────────────┤
              │ Security / JWT             │
              │ Controller                 │
              │ Service                    │
              │ Repository                 │
              │ Domain Modules              │
              │ Common Infrastructure      │
              │ Batch / File               │
              └─────────────┬──────────────┘
                            │
                            ▼
                       ┌─────────┐
                       │ MariaDB │
                       └─────────┘
                            │
                         Future
                            │
                            ▼
                     ┌─────────────┐
                     │ Spring AI   │
                     └──────┬──────┘
                            ▼
                        LLM / RAG
```

---

# 2. System Architecture

## 2.1 Logical Architecture

```text
┌──────────────────────────────────────────────────────────────┐
│                       PMIS Web Browser                       │
│                                                              │
│  ┌────────────────────────────────────────────────────────┐  │
│  │                 React + TypeScript                     │  │
│  │                                                        │  │
│  │  App                                                   │  │
│  │   ↓                                                    │  │
│  │  AppRouter                                              │  │
│  │   ↓                                                    │  │
│  │  MainLayout                                             │  │
│  │   ├── Header                                            │  │
│  │   ├── Sidebar                                           │  │
│  │   ├── Breadcrumb                                        │  │
│  │   └── Outlet                                            │  │
│  │        ↓                                                │  │
│  │      Page                                                │  │
│  │        ↓                                                │  │
│  │      Feature / Component                                │  │
│  │                                                        │  │
│  │  API / Services / Hooks / Store / Types                │  │
│  └────────────────────────┬───────────────────────────────┘  │
└───────────────────────────┼──────────────────────────────────┘
                            │
                       HTTPS / REST API
                            │
                            ▼
┌──────────────────────────────────────────────────────────────┐
│                    Spring Boot 4.1.0                         │
│                                                              │
│  Security Filter Chain                                      │
│          ↓                                                   │
│  Authentication / Authorization                              │
│          ↓                                                   │
│  Controller                                                  │
│          ↓                                                   │
│  Service                                                     │
│          ↓                                                   │
│  Repository                                                  │
│          ↓                                                   │
│       MariaDB                                                │
│                                                              │
│  Common Infrastructure                                       │
│  Exception / Response / Validation / Audit / Utility        │
│                                                              │
│  Future: Spring AI                                           │
└──────────────────────────────────────────────────────────────┘
```

---

# 3. Technology Stack

## 3.1 Backend

| Category | Technology |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 4.1.0 |
| Build | Gradle Kotlin DSL |
| ORM | Spring Data JPA |
| Database | MariaDB |
| Security | Spring Security |
| Authentication | JWT |
| Password | BCrypt |
| Validation | Jakarta Validation |
| API | RESTful API |
| Documentation | Swagger / OpenAPI |
| Utility | Lombok |
| Version Control | Git |
| CI/CD | Planned - GitHub Actions |
| Container | Planned - Docker |

---

## 3.2 Frontend

| Category | Technology |
| --- | --- |
| Framework | React |
| Language | TypeScript |
| Build Tool | Vite |
| Package Manager | npm |
| Router | React Router DOM |
| API Communication | REST API |
| Architecture | Component-based + Feature-based |
| Styling | CSS 기반 |
| State Management | 확장 예정 |
| Testing | 확장 예정 |

Frontend는 Backend와 별도 프로젝트로 관리하며 REST API를 통해 연계한다.

```text
project-delivery-reference
│
├── pmis-backend
├── pmis-frontend
└── docs
```

---

# 4. Repository Architecture

전체 Repository는 Backend, Frontend, Documentation을 하나의 Repository에서 관리한다.

```text
project-delivery-reference
│
├── pmis-backend
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── src
│
├── pmis-frontend
│   ├── package.json
│   ├── package-lock.json
│   ├── tsconfig.app.json
│   ├── vite.config.ts
│   └── src
│
├── docs
│   ├── _meta
│   ├── design
│   └── development
│
└── README.md
```

Repository 역할:

| Directory | Responsibility |
| --- | --- |
| `pmis-backend` | Spring Boot REST API |
| `pmis-frontend` | React Web Application |
| `docs` | Architecture / Design / Development Documentation |

Backend와 Frontend는 동일 Repository에서 관리하지만 **빌드, 실행, 배포 및 책임 영역은 독립적으로 관리**한다.

---

# 5. Backend Architecture

## 5.1 Backend Package Structure

Backend는 업무 Domain을 기준으로 Package를 분리한다.

```text
com.seos.pmis
│
├── auth
│   ├── controller
│   ├── dto
│   ├── security
│   │   ├── filter
│   │   ├── handler
│   │   ├── jwt
│   │   └── principal
│   └── service
│
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

향후 Domain이 구현되면 각 Domain은 기본적으로 다음 구조를 따른다.

```text
domain
├── controller
├── dto
├── entity
├── mapper
├── repository
└── service
```

단, Domain 특성에 따라 불필요한 계층은 생략할 수 있다.

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

* REST API 제공
* Request DTO 수신
* Request Validation
* Service 호출
* Response 반환

Controller는 HTTP 처리와 API orchestration에 집중한다.

핵심 Business Logic은 Controller에서 직접 처리하지 않는다.

---

## 6.2 Service

책임:

* Business Logic
* Transaction 처리
* Domain 간 업무 처리
* Repository 조합
* BusinessException 발생
* Domain 상태 변경

Business Rule은 가능한 Service Layer에서 관리한다.

---

## 6.3 Repository

책임:

* Entity Persistence
* Database Query
* Spring Data JPA 기반 데이터 접근
* 필요 시 Specification 활용

향후 Query 요구사항에 따라 QueryDSL 등의 기술을 검토한다.

---

## 6.4 Entity

Entity는 Domain의 영속 상태를 표현한다.

공통 Entity 기반 구조:

```text
BaseEntity
 ├── createdAt
 └── updatedAt
```

실제 `createdBy`, `updatedBy` 등의 Audit 정보는 인증 및 사용자 추적 요구사항에 따라 단계적으로 확장한다.

---

# 7. Frontend Architecture

Frontend는 **React + TypeScript + Component-based Architecture + Feature-based Architecture**를 적용한다.

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
            ▼
          Page
            │
            ▼
      Feature / Component
```

Frontend의 주요 책임은 다음과 같이 분리한다.

```text
Page
 │
 ├── Feature
 │    ├── Domain UI
 │    ├── Domain Logic
 │    └── Domain Types
 │
 └── Common Component
```

---

# 8. Frontend Directory Structure

현재 Frontend는 다음 구조를 기준으로 관리한다.

```text
src
│
├── api
├── assets
│
├── components
│   ├── common
│   ├── form
│   └── layout
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

현재 Dashboard Component:

```text
features/dashboard
├── KpiCard
├── ProjectOverview
├── WbsProgress
├── ScheduleSummary
├── IssueSummary
└── RecentActivity
```

Dashboard는 Power HMC(Hardware Management Console) 스타일을 참고한 운영 현황 중심의 Reference UI로 구성되어 있으며, 현재 정적 데이터를 기반으로 화면 구조를 구현한다.

---

# 9. Frontend Component Architecture

## 9.1 Layout Components

Layout Component는 Application 전체의 공통 화면 구조를 담당한다.

### Header

책임:

* PMIS 서비스 Header
* 사용자 영역
* 향후 Notification / User Menu 확장

### Sidebar

책임:

* 주요 기능 Navigation
* Dashboard
* Project
* WBS
* Schedule
* Issue
* Risk
* Change
* CMDB
* Report
* Admin

### Breadcrumb

책임:

* 현재 Route 기반 위치 표시
* 상위 / 하위 메뉴 관계 표현

---

## 9.2 Common Components

Common Component는 특정 Domain에 종속되지 않는 재사용 가능한 UI를 담당한다.

현재 구성:

```text
components/common
├── Button.tsx
├── Card.tsx
├── Loading.tsx
└── EmptyState.tsx
```

원칙:

* 특정 Domain에 종속되지 않는다.
* 여러 Feature에서 재사용할 수 있어야 한다.
* Domain 전용 Component는 해당 Feature 내부에 둔다.
* Common Component의 과도한 비대화를 방지한다.

---

## 9.3 Dashboard Components

Dashboard는 PMIS 운영 현황을 한 화면에서 파악할 수 있도록 구성한다.

```text
DashboardPage
│
├── ProjectOverview
├── KpiCard
│   ├── Project Status KPI
│   ├── Progress KPI
│   ├── Schedule KPI
│   └── Issues KPI
│
├── WbsProgress
├── ScheduleSummary
├── IssueSummary
└── RecentActivity
```

현재 Dashboard의 역할은 실제 API 연동 이전의 UI Reference 및 Component Architecture 검증이다.

향후 Project / WBS / Schedule / Issue / Risk API와 연계하여 실제 운영 데이터 기반 Dashboard로 확장한다.

---

# 10. Frontend Routing Architecture

React Router DOM을 사용한다.

현재 Application 구조:

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

향후 인증 UI 구현 이후 Protected Route를 적용한다.

목표 구조:

```text
App
 │
 ▼
AppRouter
 │
 ├── Public Routes
 │   └── Login
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

Frontend는 Backend REST API와 다음 구조로 통신한다.

```text
Page / Feature
      │
      ▼
Service
      │
      ▼
API Client
      │
      ▼
HTTP Request
      │
      ▼
Spring Boot REST API
```

역할:

| Area | Responsibility |
| --- | --- |
| `api` | HTTP API Client / Endpoint 호출 |
| `services` | Frontend Business-oriented Service |
| `types` | Request / Response Type |
| `hooks` | Reusable React Logic |
| `store` | Global State |
| `utils` | Common Utility |

Frontend에서 Backend의 Domain 구조를 직접 복제하기보다는 **화면 및 Feature 중심으로 API와 UI를 구성**한다.

현재 Backend의 Project API 및 Project Dashboard / Detail API는 구현되어 있으며, Frontend의 Project / Dashboard API Integration은 후속 단계에서 진행한다.

---

# 12. Frontend State Management

현재 초기 단계에서는 State Management 영역만 확보한다.

향후 필요에 따라 다음 상태를 관리한다.

* Authentication State
* Current User
* Access Token
* Project Context
* UI State
* Notification State
* Filter / Search State

State Management Library는 실제 요구사항과 상태 복잡도를 확인한 후 도입한다.

초기 단계에서는 React 기본 State 및 Context 등으로 충분한 영역은 별도 Library를 사용하지 않는다.

---

# 13. Security Architecture

PMIS Backend는 **Spring Security + JWT 기반 Stateless Authentication**을 적용한다.

기본 구조:

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

* Spring Security
* Security Configuration
* JWT Provider
* JWT Authentication Filter
* Authentication EntryPoint
* Access Denied Handler
* UserPrincipal
* BCrypt Password Encoder
* Stateless Session
* CORS
* Role-based Authorization

현재 Role 구조:

```text
USER
 │
 └── 일반 사용자 권한

PM
 │
 └── 프로젝트 관리 권한

ADMIN
 │
 └── 시스템 관리 권한
```

향후 프로젝트 단위 권한 및 세부 Permission이 필요한 경우 확장한다.

---

# 14. Authentication Flow

현재 인증 구조:

```text
Login Request
      │
      ▼
AuthenticationManager
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
JWT Authentication Filter
      │
      ▼
Authentication / Authorization
      │
      ▼
Protected API
```

현재 구현된 인증 기능:

* Login API
* Refresh Token API
* Token Reissue
* Access Token Validation
* Refresh Token Validation
* Role-based Authorization

향후 요구사항에 따라 다음 기능을 확장한다.

* Token Rotation
* Logout / Token Invalidation
* Session / Device Management
* Fine-grained Permission

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

현재 주요 구성:

* `BaseEntity`
* `ApiResponse`
* `ErrorCode`
* Domain별 ErrorCode
* `BusinessException`
* `GlobalExceptionHandler`
* `JpaConfig`

공통 모듈은 Domain의 Business Logic을 포함하지 않으며 여러 Domain에서 공통적으로 사용할 수 있는 기능만 관리한다.

---

# 16. Domain Modules

PMIS Backend는 실제 PMO 업무 영역을 기준으로 Domain Module을 구성한다.

## Authentication

* Login
* JWT
* Authentication
* Authorization
* Role

## User

* 사용자
* 사용자 정보
* Role
* 권한

## Project

* 프로젝트
* 고객사
* PM
* 프로젝트 상태
* 프로젝트 검색
* 프로젝트 Dashboard
* 프로젝트 상세 조회

현재 구현:

* Project CRUD
* Project Search
* Sorting
* Sorting Column Validation
* Project Dashboard API
* Project Detail API

## WBS

* WBS
* Task
* 작업 계층
* 담당자
* 진척률

## Schedule

* 일정
* Calendar
* Gantt
* Milestone
* 일정 지연

## Issue

* Issue 등록
* Assignment
* Status
* Priority
* 처리 이력
* Attachment

## Risk

* Risk 등록
* Probability
* Impact
* Risk Score
* Risk Matrix
* Response Strategy

## Change

* Change Request
* Approval
* Impact Analysis
* Change History

## CMDB

* Server CI
* Database CI
* Software CI
* Network CI
* CI Relationship
* Dependency
* Configuration History

## Build / Construction

향후 실제 SI 구축 프로젝트 PMO 업무를 지원하기 위해 확장한다.

* 설치 대상
* HW/SW 설치 일정
* 환경 구성
* 구축 진행률
* 설치 결과
* 협력사 작업
* 구축 이슈

## Inspection

* 검수 요청
* 검수 대상
* 검수 결과
* 증적
* 검수 산출물
* 완료 보고

## Dashboard

* Executive Dashboard
* Project Dashboard
* Schedule Dashboard
* Issue Dashboard
* Risk Dashboard
* Change Dashboard
* CMDB Dashboard

## Report

* Weekly Report
* Monthly Report
* Executive Report
* Meeting Report
* Excel Export
* PDF Export

## File

* Attachment
* Upload
* Download
* Storage
* Evidence / Deliverable Management

## Handover

향후 프로젝트 종료 및 운영 인수인계 영역으로 확장한다.

* 운영 문서
* 시스템 구성 정보
* 교육 자료
* 운영 담당자
* 인수인계 Checklist
* 종료 보고서

---

# 17. Database Architecture

PMIS Database는 MariaDB를 기본 RDBMS로 사용한다.

기본적인 Domain 관계는 다음과 같은 구조를 목표로 한다.

```text
User
 │
 ▼
Project
 │
 ├── WBS
 │    └── Task
 │         └── Schedule
 │
 ├── Issue
 │
 ├── Risk
 │
 ├── Change
 │
 ├── CMDB
 │
 ├── Report
 │
 └── File / Deliverable
```

향후 실제 Domain 구현 과정에서 Entity 관계를 구체화한다.

공통 Audit 정보는 `BaseEntity`를 중심으로 관리한다.

현재:

```text
createdAt
updatedAt
```

향후 사용자 추적이 필요한 Domain에 대해서는 다음 정보를 확장할 수 있다.

```text
createdBy
updatedBy
```

---

# 18. Exception Handling Architecture

Backend의 예외 처리 흐름:

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

예외 코드 구조는 Domain별 ErrorCode를 사용한다.

예:

```text
CommonErrorCode
AuthErrorCode
UserErrorCode
ProjectErrorCode
WbsErrorCode
ScheduleErrorCode
IssueErrorCode
RiskErrorCode
ChangeErrorCode
CmdbErrorCode
ReportErrorCode
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

향후 Pagination이 필요한 API는 다음과 같이 별도 Response 구조를 확장할 수 있다.

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "Success",
  "data": {
    "content": [],
    "page": 0,
    "size": 20,
    "totalElements": 0,
    "totalPages": 0
  }
}
```

---

# 20. AI Architecture

AI 기능은 **PMIS Core 기능 안정화 이후 단계적으로 도입**한다.

Spring AI를 Backend 내부의 AI Application Layer로 사용한다.

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
      ├── Chat Client
      ├── Prompt
      ├── Context
      ├── Tool / Function
      └── RAG
              │
              ▼
         LLM Provider
```

예정 Provider:

* OpenAI
* Azure OpenAI
* Ollama
* 기타 LLM Provider

예정 AI 기능:

* Project Summary
* Schedule Analysis
* Delay Analysis
* Risk Analysis
* Issue Summary
* Change Impact Analysis
* Meeting Summary
* Action Item Generation
* Weekly Report Generation
* Monthly Report Generation
* AI PM Assistant

AI는 PMIS Core Domain과 직접 결합하기보다 **독립적인 Application Layer로 확장 가능한 구조**를 목표로 한다.

---

# 21. AI Knowledge Architecture

향후 PMIS 프로젝트 데이터를 AI Knowledge Base로 확장한다.

```text
PMIS Documents / Data
          │
          ▼
Document Processing
          │
          ▼
Chunking
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
AI PM Assistant
```

대상 데이터:

* Project Documents
* WBS
* Schedule
* Issue
* Risk
* Change
* Reports
* Meeting Notes
* Deliverables
* Configuration Information

향후 필요에 따라 다음 기술을 검토한다.

* Vector Database
* RAG
* MCP
* Tool Calling
* Multi-Agent Architecture

---

# 22. API Documentation

REST API는 Swagger / OpenAPI를 통해 문서화한다.

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

* Authentication API
* User API
* Project API
* WBS API
* Schedule API
* Issue API
* Risk API
* Change API
* CMDB API
* Dashboard API
* Report API
* File API

현재 구현 상태와 API 문서가 일치하도록 관리한다.

---

# 23. Deployment Architecture

현재는 Local Development 환경을 중심으로 개발하며, 향후 Docker 기반 배포 구조를 적용한다.

목표 구조:

```text
GitHub
   │
   ▼
GitHub Actions
   │
   ▼
Build / Test
   │
   ▼
Docker Image
   │
   ├───────────────┐
   ▼               ▼
Frontend          Backend
Container         Container
                    │
                    ▼
                 MariaDB
   │
   └────── HTTPS / Reverse Proxy
```

향후 환경에 따라 다음 기술을 검토한다.

* Docker
* Docker Compose
* Nginx
* AWS
* Object Storage
* S3
* Cloud Load Balancer

---

# 24. CI/CD Architecture

CI/CD는 향후 GitHub Actions를 기반으로 구축한다.

목표 흐름:

```text
Developer
    │
    ▼
Feature Branch
    │
    ▼
Local Test
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

현재는 Git Flow 기반의 Branch 및 Commit 전략을 우선 적용한다.

---

# 25. Git Flow Architecture

현재 Integration Branch는 `develop`이다.

```text
main
 │
 └── release/*
         │
         ▼
      develop
         │
         ├── feature/*
         ├── bugfix/*
         └── refactor/*
```

Branch 역할:

| Branch | Responsibility |
| --- | --- |
| `main` | 검증된 Release |
| `develop` | Integration Branch |
| `feature/*` | Feature Development |
| `bugfix/*` | Bug Fix |
| `refactor/*` | Refactoring |
| `release/*` | Release Preparation |
| `hotfix/*` | Production Hot Fix |

개발 기본 흐름:

```text
develop
   │
   ▼
feature/*
   │
   ▼
Development
   │
   ▼
Local Test
   │
   ▼
Documentation
   │
   ▼
Commit
   │
   ▼
Push
   │
   ▼
Pull Request
   │
   ▼
Code Review
   │
   ▼
develop
```

---

# 26. Design Principles

## Backend

* Layered Architecture
* Domain-Oriented Design
* SOLID
* Clean Code
* RESTful API
* DTO Pattern
* Mapper Pattern
* Repository Pattern
* Specification Pattern
* Global Exception Handling
* Standard API Response
* Low Coupling
* High Cohesion

## Frontend

* Component-based Architecture
* Feature-based Organization
* Separation of Concerns
* Route-based Page Structure
* Common UI Component Reuse
* Type Safety
* API / Service Separation
* Layout / Page Separation
* Reusable Component Design

## Project Development

* Git Flow
* Semantic Versioning
* Conventional Commit
* Pull Request
* Code Review
* Testable Code
* Documentation First
* Implementation / Documentation Synchronization

---

# 27. Architecture Evolution

PMIS는 초기에는 단순하고 유지보수 가능한 Architecture를 유지하고 실제 요구사항이 발생할 경우 점진적으로 확장한다.

향후 검토 기술:

```text
Current
 │
 ├── Spring Boot
 ├── Spring Data JPA
 ├── MariaDB
 ├── React
 ├── TypeScript
 └── JWT
        │
        ▼
Expansion
 │
 ├── QueryDSL
 ├── Redis
 ├── Spring Batch
 ├── Kafka
 ├── Elasticsearch
 ├── Object Storage
 ├── Docker
 ├── GitHub Actions
 ├── Prometheus
 ├── Grafana
 ├── Spring AI
 ├── RAG
 ├── Vector Database
 ├── MCP
 └── Multi-Agent Architecture
```

기술 도입은 기술 자체를 목적화하지 않고 다음 기준에 따라 결정한다.

* 실제 Business Requirement
* 시스템 복잡도
* 성능 요구사항
* 운영 비용
* 유지보수 비용
* 장애 대응 필요성
* 확장성

---

# 28. Current Architecture Status

## 28.1 Backend

### Completed

* Spring Boot 4.1.0
* Java 21
* Gradle Kotlin DSL
* MariaDB
* Common Infrastructure
* BaseEntity
* JPA Auditing
* Standard API Response
* Global Exception Handling
* Domain ErrorCode Structure
* Spring Security
* JWT Authentication
* BCrypt Password Encoding
* Stateless Security
* CORS
* Authentication / Authorization
* Role Authorization
* Swagger / OpenAPI
* Project CRUD
* Project Search
* Project Search Sorting Validation
* Project Dashboard API
* Project Dashboard Controller
* Project Detail API
* Project Detail Controller
* Project Detail Service
* Project Detail Response DTO
* Project Controller / Dashboard Controller / Detail Controller 책임 분리

### In Progress

* WBS
* Schedule

### Planned

* Issue
* Risk
* Change
* CMDB
* Build / Construction Management
* Inspection Management
* File / Deliverable Management
* Report
* Handover Management
* Batch
* Spring AI
* RAG
* AI PM Assistant

---

## 28.2 Frontend

### Completed

* React
* Vite
* TypeScript
* npm
* React Router DOM
* Feature-based Directory Structure
* AppRouter
* MainLayout
* Header
* Sidebar
* Breadcrumb
* Common UI Components
  * Button
  * Card
  * Loading
  * EmptyState
* Frontend Path Alias
* Layout Component Separation
* Dashboard Reference Layout
* ProjectOverview Component
* KpiCard Component
* WbsProgress Component
* ScheduleSummary Component
* IssueSummary Component
* RecentActivity Component
* Dashboard Component Separation
* Dashboard Production Build Verification

### Current

* Dashboard UI Reference Implementation
* Project Module
* Theme
* API Integration
* Form Components

### In Progress

* Project List
* Project Search
* Project Detail UI
* Project Dashboard UI
* Project API Integration
* Project Dashboard API Integration

### Planned

* WBS UI
* Schedule UI
* Calendar UI
* Gantt UI
* Issue UI
* Risk UI
* Change UI
* CMDB UI
* Report UI
* Authentication UI
* Protected Route
* Table Components
* Modal Components
* Pagination
* State Management
* Theme / Styling System

---

# 29. Current Development Architecture

현재 개발은 Backend와 Frontend를 병렬적으로 진행한다.

```text
                    PMIS Development
                          │
            ┌─────────────┴─────────────┐
            ▼                           ▼
        Backend                     Frontend
            │                           │
      Spring Boot API              React + TS
            │                           │
            └─────────────┬─────────────┘
                          ▼
                    REST Integration
                          │
                          ▼
                       E2E Testing
```

기능 개발 단위는 다음을 기준으로 한다.

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

Frontend Reference UI와 같이 Backend API가 아직 준비되지 않은 기능은 다음과 같이 진행한다.

```text
Reference / Requirement
        ↓
UI Structure Design
        ↓
Component Design
        ↓
Static Data Implementation
        ↓
Browser Verification
        ↓
Production Build
        ↓
Documentation
        ↓
Commit
        ↓
Push
        ↓
Merge to develop
        ↓
Backend API Integration
        ↓
E2E Verification
```

---

# 30. Documentation Architecture

Architecture와 실제 구현 상태가 일치하도록 Documentation을 관리한다.

```text
docs
│
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

상위 Architecture 문서는 다음 구조를 기준으로 관리한다.

```text
docs
├── PROJECT_OVERVIEW.md
├── ARCHITECTURE.md
├── DEVELOPMENT_ROADMAP.md
└── PORTFOLIO.md
```

문서 관리 원칙:

1. 실제 구현과 문서의 상태를 일치시킨다.
2. 완료되지 않은 기능을 Completed로 기록하지 않는다.
3. Architecture와 구현이 달라진 경우 문서를 먼저 또는 동시에 갱신한다.
4. 주요 Architecture 변경은 Document History에 기록한다.
5. Frontend Reference UI와 실제 API Integration 단계를 구분하여 기록한다.

---

# 31. Architecture Decision Summary

현재 PMIS Architecture의 핵심 결정사항은 다음과 같다.

| Area | Decision |
| --- | --- |
| Backend | Spring Boot 4.1.0 |
| Backend Language | Java 21 |
| Backend Build | Gradle Kotlin DSL |
| Backend Architecture | Layered + Domain-Oriented |
| ORM | Spring Data JPA |
| Database | MariaDB |
| Security | Spring Security |
| Authentication | JWT |
| Frontend | React |
| Frontend Language | TypeScript |
| Frontend Build | Vite |
| Frontend Architecture | Component + Feature-based |
| Routing | React Router DOM |
| API | REST |
| Documentation | Swagger / OpenAPI |
| Version Control | Git |
| Integration Branch | develop |
| AI | Spring AI - Future |
| RAG | Future |
| Deployment | Docker - Future |
| CI/CD | GitHub Actions - Future |

---

# 32. Current Project Snapshot

| Item | Value |
| --- | --- |
| Current Version | v0.5.3 |
| Current Branch | develop |
| Current Sprint | Sprint 3 - Project Management & Frontend Integration |
| Backend Stage | Project Management Complete / WBS & Schedule In Progress |
| Frontend Stage | Dashboard Reference UI Complete / Project Module In Progress |
| Integration Stage | Project / Dashboard API Integration Next |
| Frontend Build | Production Build Success |
| Last Completed Feature Branch | `feature/frontend-dashboard-hmc-reference` |
| Integration Branch | `develop` |
| Maintainer | Seo Seokhyeon |

---

# 33. Document History

| Version | Date | Author | Description |
| --- | --- | --- | --- |
| 1.0 | 2026-08-03 | Seo Seokhyeon | Initial Backend Architecture Document |
| 1.1 | 2026-08-10 | Seo Seokhyeon | Added Frontend Architecture, Security, API, AI, Deployment and Repository Architecture |
| 1.2 | 2026-08-10 | Seo Seokhyeon | Updated Spring Boot to 4.1.0, synchronized current Backend/Frontend implementation status, refined Domain Architecture, Security, Repository, Git Flow and future AI/Deployment architecture |
| 1.3 | 2026-08-11 | Seo Seokhyeon | Synchronized Project Detail API implementation, Controller/Service responsibility separation, Frontend Dashboard HMC Reference UI, Dashboard Component Architecture, Production Build verification, and current Sprint 3 development status |

---

# End of Document
