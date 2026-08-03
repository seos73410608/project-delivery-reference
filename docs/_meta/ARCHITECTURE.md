# PMIS Architecture

> Version: 1.0  
> Project: Project Management Information System (PMIS)  
> Framework: Spring Boot 4.x  
> Java: 21  
> Database: MariaDB  
> Last Updated: 2026-08-03

---

# 1. Architecture Overview

PMIS는 **Enterprise Layered Architecture**를 기반으로 설계한다.

설계 목표는 다음과 같다.

- 유지보수가 쉬운 구조
- 기능별 모듈 분리
- 확장 가능한 아키텍처
- AI 기능의 손쉬운 추가
- 실제 SI 프로젝트 수준의 설계

---

# 2. System Architecture

```
                +----------------------+
                |     Client (Web)     |
                +----------+-----------+
                           |
                           |
                    REST API (HTTPS)
                           |
                           ▼
+--------------------------------------------------+
|               Spring Boot Application            |
|--------------------------------------------------|
|                                                  |
|  Controller                                      |
|      │                                           |
|      ▼                                           |
|  Service                                         |
|      │                                           |
|      ▼                                           |
| Repository (JPA)                                 |
|      │                                           |
|      ▼                                           |
| MariaDB                                          |
|                                                  |
+--------------------------------------------------+

                │

                ▼

        Spring Security

                │

                ▼

            JWT Token

                │

                ▼

            Spring AI

                │

                ▼

          OpenAI / LLM
```

---

# 3. Technology Stack

| Category | Technology |
|-----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 4.x |
| Build | Gradle Kotlin DSL |
| Database | MariaDB |
| ORM | Spring Data JPA |
| Security | Spring Security |
| Authentication | JWT |
| AI | Spring AI |
| API | RESTful API |
| Validation | Jakarta Validation |
| Documentation | Swagger/OpenAPI |
| Build Tool | Gradle |
| Version Control | Git |
| CI/CD | GitHub Actions |
| Container | Docker |

---

# 4. Package Structure

```
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

# 5. Layer Architecture

```
Controller

↓

Service

↓

Repository

↓

Database
```

각 계층의 역할은 다음과 같다.

## Controller

- REST API 제공
- Request Validation
- Response 반환

---

## Service

- Business Logic
- Transaction 처리
- Domain Service

---

## Repository

- JPA Repository
- QueryDSL (확장 예정)
- Specification

---

## Database

- MariaDB
- JPA
- Hibernate

---

# 6. Security Architecture

```
Request

↓

JWT Filter

↓

Authentication

↓

Authorization

↓

Controller
```

구성 요소

- JWT Authentication
- JWT Authorization
- Password Encoder (BCrypt)
- Security Filter Chain
- UserDetailsService
- AuthenticationEntryPoint
- AccessDeniedHandler

---

# 7. Common Module

공통 모듈은 모든 도메인에서 사용한다.

```
common

├── response
├── exception
├── entity
├── util
├── dto
├── enums
├── validation
```

주요 구성

- ApiResponse
- BaseEntity
- BusinessException
- GlobalExceptionHandler
- ErrorCode

---

# 8. Domain Modules

## Authentication

- Login
- Logout
- JWT
- Refresh Token

---

## User

- 사용자
- 권한
- Role

---

## Project

- 프로젝트
- 고객사
- PM
- 상태관리

---

## WBS

- 작업
- 일정
- 진척률

---

## Schedule

- 일정관리
- Calendar
- Gantt

---

## Issue

- 이슈 등록
- 처리
- 종료

---

## Risk

- 위험관리
- 대응방안
- 우선순위

---

## Change

- 변경요청
- 승인
- 변경이력

---

## CMDB

- Server CI
- Software CI
- Database CI
- Relationship

---

## Dashboard

- 프로젝트 현황
- 일정 현황
- 이슈 현황
- 리스크 현황

---

## Report

- PDF
- Excel
- 통계보고서

---

## File

- 첨부파일
- 다운로드
- Storage

---

# 9. Database Architecture

```
User

↓

Project

↓

WBS

↓

Issue

↓

Risk

↓

Change

↓

CMDB

↓

Report
```

모든 Entity는 BaseEntity를 상속한다.

공통 컬럼

- createdAt
- updatedAt
- createdBy
- updatedBy

---

# 10. Exception Handling

```
Exception

↓

BusinessException

↓

GlobalExceptionHandler

↓

ApiResponse
```

모든 오류는 동일한 형식으로 반환한다.

예시

```json
{
  "success": false,
  "code": "PROJECT_NOT_FOUND",
  "message": "Project not found."
}
```

---

# 11. API Response Standard

성공 응답

```json
{
  "success": true,
  "message": "Success",
  "data": {}
}
```

실패 응답

```json
{
  "success": false,
  "code": "ERROR_CODE",
  "message": "Error Message"
}
```

---

# 12. AI Architecture

```
User

↓

REST API

↓

Spring AI

↓

Prompt

↓

OpenAI

↓

Response

↓

Dashboard
```

AI 기능

- 프로젝트 분석
- 일정 분석
- 리스크 분석
- 변경 영향도 분석
- 보고서 자동 생성
- 회의록 요약
- Action Item 생성
- 프로젝트 상태 요약

---

# 13. Deployment Architecture

```
GitHub

↓

GitHub Actions

↓

Docker Build

↓

Docker Image

↓

Docker Compose

↓

MariaDB

↓

Spring Boot

↓

Nginx (Optional)
```

---

# 14. Future Architecture

향후 다음 기술을 추가 적용할 수 있다.

- Redis Cache
- QueryDSL
- Elasticsearch
- RabbitMQ
- Kafka
- Spring Batch
- Prometheus
- Grafana
- Kubernetes
- AWS ECS/EKS
- Object Storage(S3)

---

# 15. Design Principles

본 프로젝트는 다음 원칙을 따른다.

- SOLID Principle
- Clean Code
- Clean Architecture
- Layered Architecture
- Domain-Oriented Design
- RESTful API Design
- Convention over Configuration
- Reusable Components
- Low Coupling
- High Cohesion

---

# 16. Document History

| Version | Date | Author | Description |
|----------|------|--------|-------------|
| 1.0 | 2026-08-03 | Seo Seokhyeon | Initial Architecture Document |