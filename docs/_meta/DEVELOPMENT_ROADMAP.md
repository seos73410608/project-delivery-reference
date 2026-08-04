# PMIS Development Roadmap

> Version: **1.1**
>
> Last Updated: **2026-08-04**
>
> Project: **Project Management Information System (PMIS)**
>
> Architecture:
> Spring Boot 3.5.x
> Spring Security
> JWT
> Spring AI
> MariaDB
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

실제 운영 가능한 수준의 아키텍처와 품질을 목표로 개발한다.

---

# 2. Development Strategy

## Development Flow

```
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

모든 기능은 Feature Branch에서 개발 후
Develop으로 Merge한다.

검증 완료 후 Main으로 Release 한다.

---

## Branch Strategy

```
feature/auth-jwt
feature/auth-login
feature/auth-role

feature/project-crud
feature/project-search

feature/wbs-management

feature/schedule-management

feature/issue-management

feature/risk-management

feature/change-management

feature/cmdb-management

feature/report-excel
feature/report-pdf

feature/dashboard

feature/spring-ai-assistant
feature/ai-risk-analysis
feature/ai-report-summary
```

모든 기능은 독립 Feature Branch에서 개발한다.

---

## Version Strategy (Semantic Versioning)

```
1.0.0
│ │ └─ Patch
│ └──── Minor
└────── Major
```

### Major

호환되지 않는 변경

### Minor

새로운 기능 추가

### Patch

버그 수정 및 개선

---

## Commit Convention

```
feat:
fix:
refactor:
style:
test:
docs:
build:
chore:
```

예시

```
feat: implement JWT authentication

fix: resolve login validation issue

refactor: extract common exception handler

docs: update architecture documentation
```

---

# 3. Sprint Plan

---

# Sprint 0

## Base Project

### Goal

프로젝트 기반 구축

### Tasks

- Spring Boot
- Gradle
- Git Flow
- MariaDB
- Package Structure
- Base Entity
- Global Response
- Exception
- JPA
- Build Configuration

### Deliverables

- Base Project
- Build Success
- Git Repository

---

# Sprint 1

## Authentication & Security

### Goal

JWT 기반 인증/인가 구축

### Feature Branch

```
feature/auth-jwt
feature/auth-login
feature/auth-role
```

### Tasks

- JWT Provider
- JWT Filter
- SecurityConfig
- UserPrincipal
- Login
- Logout
- Refresh Token
- Role Authorization
- Password Encoder

### Deliverables

- JWT Authentication
- Role Authorization
- Security Framework

---

# Sprint 2

## Project Management

### Feature Branch

```
feature/project-crud
feature/project-search
```

### Tasks

- Project CRUD
- Customer
- PM
- Status
- Project Search
- Pagination
- Specification

### Deliverables

- Project API
- Search API

---

# Sprint 3

## WBS & Schedule

### Feature Branch

```
feature/wbs-management

feature/schedule-management
```

### Tasks

- WBS CRUD
- Schedule CRUD
- Progress Calculation
- Calendar API
- Gantt Data

### Deliverables

- WBS API
- Schedule API

---

# Sprint 4

## Issue / Risk / Change

### Feature Branch

```
feature/issue-management

feature/risk-management

feature/change-management
```

### Tasks

Issue

- 등록
- 수정
- 종료

Risk

- 위험도 계산
- 대응

Change

- 변경 요청
- 승인
- 이력 관리

### Deliverables

- Issue Module
- Risk Module
- Change Module

---

# Sprint 5

## CMDB

### Feature Branch

```
feature/cmdb-management
```

### Tasks

- Server CI
- Software CI
- Database CI
- Relationship
- Version History
- Change Tracking

### Deliverables

- CMDB API

---

# Sprint 6

## Dashboard & Report

### Feature Branch

```
feature/dashboard

feature/report-excel

feature/report-pdf
```

### Tasks

Dashboard

- 프로젝트 현황
- 일정 현황
- 이슈 현황
- 리스크 현황

Report

- Excel Export
- PDF Export

### Deliverables

- Dashboard
- Excel Report
- PDF Report

---

# Sprint 7

## Spring AI

### Feature Branch

```
feature/spring-ai-assistant

feature/ai-risk-analysis

feature/ai-report-summary
```

### Tasks

Spring AI

- OpenAI 연동
- Prompt 관리
- AI Chat

AI 기능

- 일정 분석
- 리스크 분석
- 프로젝트 분석
- 보고서 요약
- 회의록 요약
- Action Item 생성
- 변경 영향도 분석

### Deliverables

- AI Chat API
- AI Assistant

---

# Sprint 8

## Finalization

### Goal

포트폴리오 완성

### Tasks

- Refactoring
- Unit Test
- Integration Test
- Swagger
- Docker
- Docker Compose
- GitHub Actions
- README
- Portfolio

### Deliverables

- Production Ready PMIS
- Portfolio Repository

---

# 4. Milestones

| Milestone | Description |
|------------|-------------|
| M1 | Base Project Complete |
| M2 | Authentication Complete |
| M3 | Project Management Complete |
| M4 | WBS & Schedule Complete |
| M5 | Issue/Risk/Change Complete |
| M6 | CMDB Complete |
| M7 | Dashboard & Report Complete |
| M8 | Spring AI Complete |
| M9 | Production Ready |
| M10 | Portfolio Release |

---

# 5. Estimated Schedule

| Sprint | Duration |
|---------|----------|
| Sprint 0 | 1 Week |
| Sprint 1 | 2 Weeks |
| Sprint 2 | 2 Weeks |
| Sprint 3 | 2 Weeks |
| Sprint 4 | 2 Weeks |
| Sprint 5 | 2 Weeks |
| Sprint 6 | 2 Weeks |
| Sprint 7 | 2 Weeks |
| Sprint 8 | 1 Week |

**Estimated Total**

약 **16~18주**

---

# 6. Quality Objectives

## Architecture

- Layered Architecture
- Clean Architecture
- SOLID Principles
- RESTful API
- DTO Pattern
- Mapper Pattern
- Specification Pattern

---

## Development Standards

- Git Flow
- Semantic Versioning
- Conventional Commit
- Pull Request
- Code Review
- Unit Test
- Integration Test

---

## Documentation

다음 문서를 지속적으로 관리한다.

- PROJECT_OVERVIEW.md
- DEVELOPMENT_ROADMAP.md
- ARCHITECTURE.md
- CHANGELOG.md
- PORTFOLIO.md

---

# 7. Final Deliverables

최종 결과물은 다음을 포함한다.

- PMIS Backend
- Spring Boot REST API
- JWT Authentication
- MariaDB Schema
- Project Management
- WBS Management
- Issue Management
- Risk Management
- Change Management
- CMDB
- Dashboard
- Report System
- Spring AI Assistant
- Swagger API
- Docker Deployment
- GitHub Repository
- Portfolio Documentation

---

# 8. Success Criteria

프로젝트 완료 시 다음 목표를 만족한다.

- 실제 SI 프로젝트 수준의 PMIS 구현
- Spring Boot 기반 엔터프라이즈 아키텍처 적용
- JWT 기반 인증/인가 구현
- Spring AI 기반 AI Assistant 제공
- Docker 기반 배포 환경 구축
- Git Flow 기반 협업 프로세스 적용
- 운영 가능한 백엔드 시스템 완성
- 포트폴리오 및 기술 시연 가능한 수준의 결과물 확보

---

# Document History

| Version | Date | Author | Description |
|----------|------|--------|-------------|
| 1.0 | 2026-08-03 | Seo Seokhyeon | Initial Development Roadmap |
| 1.1 | 2026-08-04 | Seo Seokhyeon | Updated roadmap, branch strategy, semantic versioning, sprint structure |