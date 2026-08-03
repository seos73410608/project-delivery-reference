# PMIS Portfolio

> Enterprise Project Management Information System  
> Spring Boot 4 + Java 21 + MariaDB + Spring Security + JWT + Spring AI

---

# 1. 프로젝트 소개

PMIS(Project Management Information System)는 SI 및 SM 프로젝트 수행 과정에서 발생하는 프로젝트 관리 업무를 통합 지원하는 웹 시스템이다.

일반적인 일정 관리 시스템이 아니라 실제 프로젝트 현장에서 사용하는 PMO(Project Management Office) 업무를 기반으로 설계하였다.

본 프로젝트는 단순 CRUD 구현이 목적이 아니다.

실무 프로젝트에서 발생하는

- 일정관리
- WBS 관리
- 이슈관리
- 리스크관리
- 변경관리
- 검수관리
- 구축관리
- CMDB
- 보고관리
- 운영 인수인계

전체 프로세스를 하나의 플랫폼으로 구현하는 것을 목표로 한다.

---

# 2. 프로젝트 목표

## 1차 목표

Spring Boot 기반의 Enterprise PMIS 구축

구현 대상

- 프로젝트 관리
- 일정 관리
- WBS
- 이슈
- 리스크
- 변경관리
- 보고관리
- CMDB
- Dashboard

---

## 2차 목표

현업에서 사용하는 PMO 문서를 자동 생성할 수 있는 시스템 구축

예)

- 주간보고서
- 회의록
- 변경요청서
- 검수보고서
- 완료보고서

---

## 3차 목표

Spring AI 기반 AI PM Assistant 구축

AI가

- 프로젝트 진행률 분석
- 일정 지연 예측
- 리스크 예측
- 변경 영향도 분석
- 회의록 요약
- 보고서 자동 작성

을 수행한다.

---

# 3. 프로젝트 특징

## 실무 중심 설계

실제 프로젝트 수행 프로세스를 기준으로 설계하였다.

단순 CRUD 프로젝트가 아니라

프로젝트 수행 프로세스 자체를 시스템으로 구현한다.

---

## Domain Driven Design

모든 기능을 Domain 단위로 분리하였다.

예)

```
project
issue
risk
change
schedule
wbs
cmdb
report
dashboard
auth
user
```

각 Domain은

- Controller
- Service
- Repository
- Entity
- DTO
- Mapper

구조를 동일하게 유지한다.

---

## 계층 분리

```
Presentation

↓

Application(Service)

↓

Domain(Entity)

↓

Persistence(Repository)

↓

Database
```

구조를 명확하게 유지한다.

---

## 공통 모듈화

공통 기능은 Common Module로 관리한다.

예)

- ApiResponse
- Exception
- ErrorCode
- BaseEntity
- Validation
- Utility

---

# 4. 기술 스택

## Backend

- Java 21
- Spring Boot 4
- Spring Security
- Spring Data JPA
- Hibernate
- JWT

---

## Database

- MariaDB

---

## Build

- Gradle Kotlin DSL

---

## Documentation

- Markdown

---

## Version Control

- Git
- GitHub Flow

---

## AI

예정

- Spring AI
- OpenAI
- MCP
- RAG

---

# 5. 아키텍처

```
Browser

↓

Spring Boot

↓

Controller

↓

Service

↓

Repository

↓

MariaDB
```

향후

```
Spring AI

↓

LLM

↓

OpenAI

↓

PMIS Database

↓

RAG

↓

AI Assistant
```

구조를 추가할 예정이다.

---

# 6. 구현 예정 기능

## 프로젝트

- 프로젝트 생성
- 프로젝트 종료
- 프로젝트 상태관리

---

## 일정

- 일정 등록
- 일정 변경
- 일정 진척률

---

## WBS

- 작업 등록
- 담당자 지정
- 진행률 계산

---

## 이슈

- 이슈 등록
- 조치 관리
- 완료 처리

---

## 리스크

- Risk 등록
- Impact 분석
- 대응 전략

---

## 변경관리

- 변경 요청
- 영향도 분석
- 승인 프로세스

---

## 보고관리

- 주간보고
- 월간보고
- Dashboard

---

## CMDB

- CI 관리
- Server
- Database
- Software
- 관계관리

---

## Dashboard

- 프로젝트 현황
- 일정
- 이슈
- 리스크
- KPI

---

## AI Assistant

예정 기능

- 일정 예측

- 프로젝트 위험도 분석

- 이슈 요약

- 회의록 요약

- 변경 영향도 분석

- 프로젝트 상태 자동 분석

- 보고서 자동 생성

---

# 7. 개발 원칙

본 프로젝트는 아래 원칙을 따른다.

- Clean Architecture
- SOLID
- DDD
- REST API
- Git Flow
- Convention First
- Testable Code
- Reusable Component

---

# 8. 프로젝트 진행 현황

| Sprint | 내용 | 상태 |
|---------|------|------|
| Sprint 0 | 프로젝트 생성 | 완료 |
| Sprint 1 | 패키지 구조 | 완료 |
| Sprint 2 | 공통 모듈 | 진행중 |
| Sprint 3 | Security/JWT | 예정 |
| Sprint 4 | 사용자 관리 | 예정 |
| Sprint 5 | 프로젝트 관리 | 예정 |
| Sprint 6 | 일정 관리 | 예정 |
| Sprint 7 | WBS | 예정 |
| Sprint 8 | 이슈 관리 | 예정 |
| Sprint 9 | 리스크 관리 | 예정 |
| Sprint 10 | 변경 관리 | 예정 |
| Sprint 11 | Dashboard | 예정 |
| Sprint 12 | CMDB | 예정 |
| Sprint 13 | Report | 예정 |
| Sprint 14 | Spring AI | 예정 |
| Sprint 15 | Portfolio 정리 | 예정 |

---

# 9. 기대 효과

본 프로젝트를 통해 다음 역량을 입증할 수 있다.

- Enterprise Backend Architecture
- Spring Boot 실무 프로젝트
- Spring Security
- JWT Authentication
- JPA 설계
- Database Modeling
- REST API 설계
- Git Flow 기반 협업
- 프로젝트 관리 프로세스 이해
- PMO 업무 자동화
- AI 기반 프로젝트 관리 시스템 구축

---

# 10. 향후 확장 계획

향후 아래 기능을 추가할 예정이다.

- AI PM Assistant
- Chat 기반 PMIS
- AI 보고서 생성
- AI 일정 분석
- AI Risk Prediction
- AI Change Impact Analysis
- AI Dashboard
- Vector Database 기반 RAG
- MCP 연동
- Multi Agent Architecture

---

# 11. 포트폴리오 가치

이 프로젝트는 단순한 CRUD 예제가 아니라 실제 기업에서 사용할 수 있는 Enterprise PMIS를 목표로 한다.

프로젝트 관리, 구축 관리, 검수 관리, CMDB, 보고 관리와 같은 실무 업무를 시스템으로 구현하고, Spring AI를 활용한 AI 기반 프로젝트 관리 기능까지 포함하여 현대적인 엔터프라이즈 애플리케이션 아키텍처를 제시한다.

이를 통해 백엔드 설계 능력, 프로젝트 관리 도메인 이해, AI 서비스 통합 역량을 함께 보여줄 수 있는 종합 포트폴리오를 완성하는 것을 목표로 한다.