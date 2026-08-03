# PMIS Development Roadmap

> Version: 1.0  
> Project: Project Management Information System (PMIS)  
> Architecture: Spring Boot + MariaDB + Spring Security + JWT + Spring AI  
> Development Methodology: Agile Scrum (2-Week Sprint)

---

# 1. Development Goal

본 프로젝트는 단순한 CRUD 프로젝트가 아니라 실제 SI 프로젝트에서 사용하는 PMIS(Project Management Information System)를 구현하는 것을 목표로 한다.

구현 대상은 다음과 같다.

- 프로젝트 관리
- 일정(WBS) 관리
- 이슈 관리
- 리스크 관리
- 변경관리
- CMDB
- Dashboard
- Report
- 사용자/권한관리
- AI Assistant(Spring AI)

실제 운영 가능한 수준의 구조와 품질을 목표로 개발한다.

---

# 2. Development Strategy

## Branch Strategy

```
main
 ├── develop
 │
 ├── feature/project
 ├── feature/security
 ├── feature/user
 ├── feature/wbs
 ├── feature/issue
 ├── feature/risk
 ├── feature/change
 ├── feature/cmdb
 ├── feature/dashboard
 ├── feature/report
 └── feature/ai
```

모든 기능은 Feature Branch에서 개발 후 Develop에 Merge한다.

완료 후 Main으로 Release한다.

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
feat: add JWT authentication

fix: resolve login validation issue

refactor: extract common response class

docs: update architecture document
```

---

# 3. Sprint Plan

프로젝트는 약 8개의 Sprint로 구성한다.

---

# Sprint 0

## Development Environment

### Goal

프로젝트 기반 구축

### Tasks

- Spring Boot
- Gradle
- MariaDB
- Git Flow
- Package Structure
- Global Response
- Exception Handler
- BaseEntity
- JPA Config
- Build Configuration

### Deliverables

- Build Success
- Git Repository
- Base Project

### Status

Completed

---

# Sprint 1

## Authentication & Security

### Goal

사용자 인증 시스템 구축

### Tasks

- User Entity
- Role Entity
- JWT
- Login
- Logout
- Refresh Token
- Spring Security
- Authentication Filter
- Authorization Filter
- Password Encryption

### Deliverables

- JWT Login
- Role Based Authorization
- Security Config

---

# Sprint 2

## Project Management

### Goal

프로젝트 관리 기능 구현

### Tasks

- Project CRUD
- Customer
- PM
- 기간 관리
- 상태 관리
- 프로젝트 검색
- Pagination
- Specification

### Deliverables

- Project API
- Project Search

---

# Sprint 3

## WBS & Schedule

### Goal

일정 관리 기능 구축

### Tasks

- WBS CRUD
- 일정 등록
- 일정 수정
- 일정 삭제
- 진척률 계산
- Gantt Data
- Calendar API

### Deliverables

- WBS API
- Schedule API
- Progress Calculation

---

# Sprint 4

## Issue / Risk / Change

### Goal

프로젝트 운영 관리

### Tasks

Issue

- 등록
- 수정
- 종료

Risk

- 위험도 계산
- 대응방안

Change

- 변경 요청
- 승인
- 변경 이력

### Deliverables

- Issue Module
- Risk Module
- Change Module

---

# Sprint 5

## CMDB

### Goal

구성관리 시스템 구축

### Tasks

- Server CI
- Software CI
- Database CI
- CI Relationship
- Version History
- Change Tracking

### Deliverables

- CMDB API
- Relationship Model

---

# Sprint 6

## Dashboard & Reporting

### Goal

관리자 Dashboard 구축

### Tasks

Dashboard

- 프로젝트 현황
- 일정 현황
- 이슈 현황
- 리스크 현황

Report

- Excel Export
- PDF Export
- 통계 Report

### Deliverables

- Dashboard
- Excel Report
- PDF Report

---

# Sprint 7

## Spring AI Integration

### Goal

AI 기반 PM Assistant 구축

### Tasks

Spring AI

- OpenAI 연동
- Prompt 관리
- Chat Service

AI 기능

- 일정 분석
- 리스크 분석
- 프로젝트 상태 분석
- 보고서 자동 생성
- 회의록 요약
- Action Item 생성
- 변경 영향도 분석
- 이슈 우선순위 추천

### Deliverables

- AI Chat API
- AI Report Generator
- AI Dashboard Assistant

---

# Sprint 8

## Finalization

### Goal

포트폴리오 완성

### Tasks

- Test
- Refactoring
- API Documentation
- Swagger
- README
- Docker
- Docker Compose
- CI/CD
- GitHub Actions
- Portfolio 정리

### Deliverables

- 운영 가능한 PMIS
- Portfolio Repository
- Deployment

---

# 4. Milestone

| Milestone | Description |
|-----------|-------------|
| M1 | Base Project Complete |
| M2 | Authentication Complete |
| M3 | Project Management Complete |
| M4 | WBS Complete |
| M5 | Issue/Risk/Change Complete |
| M6 | CMDB Complete |
| M7 | Dashboard Complete |
| M8 | Spring AI Complete |
| M9 | Portfolio Release |

---

# 5. Estimated Schedule

| Sprint | Duration |
|----------|---------|
| Sprint 0 | 1 Week |
| Sprint 1 | 2 Weeks |
| Sprint 2 | 2 Weeks |
| Sprint 3 | 2 Weeks |
| Sprint 4 | 2 Weeks |
| Sprint 5 | 2 Weeks |
| Sprint 6 | 2 Weeks |
| Sprint 7 | 2 Weeks |
| Sprint 8 | 1 Week |

**Total Duration**

약 **16~18주**

---

# 6. Quality Objectives

## Code Quality

- Clean Architecture
- SOLID
- Layered Architecture
- DTO Pattern
- Mapper Pattern
- Specification Pattern

---

## Development Standards

- Git Flow
- Conventional Commit
- Pull Request
- Code Review
- Unit Test
- Integration Test

---

## Documentation

프로젝트와 함께 아래 문서를 지속적으로 관리한다.

- Project Overview
- Development Roadmap
- Architecture
- Changelog
- Portfolio

---

# 7. Final Deliverables

최종 결과물은 다음을 포함한다.

- PMIS Backend
- Spring Boot REST API
- MariaDB Schema
- JWT Authentication
- Dashboard
- CMDB
- WBS
- Report System
- Spring AI Assistant
- Docker Deployment
- Swagger API
- GitHub Repository
- Portfolio Documentation

---

# 8. Success Criteria

프로젝트 완료 시 다음 목표를 만족한다.

- 실제 SI 프로젝트 수준의 PMIS 구현
- Spring Boot 기반 엔터프라이즈 아키텍처 적용
- JWT 기반 인증/인가 구현
- Spring AI를 활용한 AI Assistant 제공
- Docker 기반 배포 환경 구축
- Git Flow 기반 협업 프로세스 적용
- 포트폴리오 및 기술 시연 가능한 수준의 결과물 확보

---

# Document History

| Version | Date | Author | Description |
|----------|------|--------|-------------|
| 1.0 | 2026-08-03 | Seo Seokhyeon | Initial Development Roadmap |