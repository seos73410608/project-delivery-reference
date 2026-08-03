# Project Overview

> Enterprise Project Management Information System (PMIS)

---

# 1. 프로젝트 개요

## 프로젝트명

PMIS (Project Management Information System)

## 프로젝트 목적

본 프로젝트는 SI(System Integration) 프로젝트 수행 시 필요한
프로젝트 관리 업무를 하나의 시스템으로 통합하기 위해 개발하는
Enterprise PMIS입니다.

실제 프로젝트에서 사용하는 프로젝트 관리 프로세스를 기반으로

- 프로젝트 관리
- 일정 관리
- WBS 관리
- 이슈 관리
- 리스크 관리
- 변경 관리
- CMDB 관리
- 구축 관리
- 검수 관리
- 보고 관리
- 운영 인수인계

등을 통합 관리하는 시스템을 목표로 합니다.

또한 Spring AI를 접목하여 프로젝트 관리 업무를 자동화하는 차세대 PMIS를 구축합니다.

---

# 2. 프로젝트 목표

본 프로젝트의 목표는 다음과 같습니다.

- Enterprise 수준의 PMIS 구현
- 실제 SI 프로젝트 업무 프로세스 반영
- 대규모 Spring Boot 아키텍처 설계
- JWT 기반 인증 시스템 구축
- Spring Security 적용
- Spring AI 기반 업무 자동화
- CI/CD 자동화
- Docker 기반 배포
- AWS 클라우드 배포

---

# 3. 주요 기능

## 프로젝트 관리

- 프로젝트 생성
- 프로젝트 종료
- 프로젝트 상태 관리
- 프로젝트 참여자 관리

---

## 일정 관리

- 일정 등록
- 일정 변경
- 일정 지연 관리
- 진척률 관리

---

## WBS 관리

- 작업 분류
- 작업 진행률
- 담당자 관리
- 일정 연계

---

## 이슈 관리

- 이슈 등록
- 우선순위 관리
- 담당자 지정
- 처리 이력 관리

---

## 리스크 관리

- 리스크 등록
- 영향도 분석
- 대응 계획
- 대응 결과 관리

---

## 변경 관리

- 변경 요청
- 변경 승인
- 영향도 분석
- 변경 이력 관리

---

## CMDB 관리

- 서버 관리

- Software 관리

- Database 관리

- CI 관계 관리

---

## 구축 관리

- 설치 관리
- 환경 관리
- 구축 진행률

---

## 검수 관리

- 검수 요청
- 검수 결과
- 증적 관리

---

## 보고 관리

- 주간 보고
- 회의록
- 의사결정 관리

---

## 운영 인수인계

- 운영 문서
- 교육
- 구성 정보
- 종료 보고서

---

# 4. AI 기능

Spring AI를 활용하여 다음 기능을 제공합니다.

- 일정 지연 예측
- 리스크 분석
- 변경 영향도 분석
- 회의록 요약
- Action Item 추출
- 주간보고 자동 생성
- 프로젝트 상태 요약
- 프로젝트 질의응답(Chat)

---

# 5. 기술 스택

## Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Spring AI
- JWT
- Lombok

## Database

- MariaDB

## Build

- Gradle Kotlin DSL

## Infrastructure

- Docker
- Docker Compose
- Nginx

## Cloud

- AWS

---

# 6. 프로젝트 구조

```
project-delivery-reference
│
├── docs
│
│   ├── _meta
│   │
│   ├── PROJECT_OVERVIEW.md
│   ├── DEVELOPMENT_ROADMAP.md
│   ├── ARCHITECTURE.md
│   ├── CHANGELOG.md
│   └── PORTFOLIO.md
│
│   ├── 01_프로젝트관리
│   ├── 02_구축관리
│   ├── 03_검수관리
│   ├── 04_구성관리
│   ├── 05_협력사관리
│   ├── 06_보고관리
│   ├── 07_변경관리
│   ├── 08_CMDB
│   ├── 09_대시보드관리
│   ├── 10_운영인수인계
│   └── 11_PMIS_모델
│
└── pmis
```

---

# 7. 개발 원칙

본 프로젝트는 다음 원칙을 준수합니다.

- Domain 기반 패키지 구조
- RESTful API
- Layered Architecture
- Clean Code
- SOLID Principle
- Convention 기반 개발
- Git Flow 전략
- Code Review
- 테스트 가능한 구조

---

# 8. 향후 계획

- 사용자 관리
- 인증/인가
- 프로젝트 관리
- 일정 관리
- WBS 관리
- 이슈 관리
- 리스크 관리
- 변경 관리
- CMDB
- Dashboard
- Report
- Spring AI
- Docker
- AWS
- CI/CD

---

# 9. 프로젝트 상태

| 구분 | 상태 |
|------|------|
| 프로젝트 생성 | ✅ 완료 |
| 패키지 구조 | ✅ 완료 |
| 공통 응답 | ✅ 완료 |
| 공통 예외 처리 | ✅ 완료 |
| Base Entity | ✅ 완료 |
| Security | 🚧 진행 중 |
| JWT | 🚧 진행 중 |
| User | ⏳ 예정 |
| Project | ⏳ 예정 |
| WBS | ⏳ 예정 |
| AI 기능 | ⏳ 예정 |

---

# 10. Repository

```
Main Branch

main

↓

develop

↓

feature/*
```

Git Flow 전략을 기반으로 개발을 진행합니다.