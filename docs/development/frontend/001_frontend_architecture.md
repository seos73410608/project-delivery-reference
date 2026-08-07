# Frontend Architecture

## 1. 문서 정보

| 항목 | 내용 |
|------|------|
| 시스템 | PMIS (Project Management Information System) |
| 문서명 | Frontend Architecture |
| 문서번호 | FA-001 |
| 작성일 | 2026-08-07 |
| 버전 | 1.0 |

---

# 2. 목적

본 문서는 PMIS Frontend의 전체 아키텍처를 정의한다.

Frontend는 사용자와 시스템 간의 인터페이스(UI)를 제공하며, Backend(Spring Boot)와 REST API를 통해 통신한다.

본 문서는 Frontend 개발의 기준이 되는 아키텍처를 정의하고, 모든 화면 개발은 본 문서를 기준으로 수행한다.

---

# 3. 시스템 구성

PMIS는 Frontend와 Backend를 분리한 구조(Frontend-Backend Separation Architecture)를 사용한다.

```
+------------------------------------------------------+
|                    Web Browser                       |
+------------------------------------------------------+
                     │
                     ▼
+------------------------------------------------------+
|                 React Frontend                       |
|------------------------------------------------------|
| UI                                                   |
| Routing                                              |
| State Management                                     |
| API Client                                           |
+------------------------------------------------------+
                     │
                 REST API
                     │
                     ▼
+------------------------------------------------------+
|               Spring Boot Backend                    |
|------------------------------------------------------|
| Authentication                                       |
| Business Logic                                       |
| Validation                                           |
| Database Access                                      |
+------------------------------------------------------+
                     │
                     ▼
+------------------------------------------------------+
|                     MariaDB                          |
+------------------------------------------------------+
```

---

# 4. 아키텍처 원칙

PMIS Frontend는 다음 원칙을 따른다.

## 4.1 Frontend와 Backend 분리

Frontend와 Backend는 독립적으로 개발 및 배포 가능해야 한다.

Frontend는 REST API를 통해 Backend와 통신하며 Database에 직접 접근하지 않는다.

---

## 4.2 UI 중심 구조

Frontend는 사용자 인터페이스(UI)를 담당한다.

Business Logic은 Backend에서 수행한다.

Frontend의 역할은 다음과 같다.

- 화면 표시
- 사용자 입력 처리
- API 호출
- 인증 정보 관리
- 화면 상태 관리

---

## 4.3 API 중심 개발

모든 데이터는 REST API를 통해 처리한다.

예시

```
GET    /api/projects

POST   /api/projects

PUT    /api/projects/{projectId}

DELETE /api/projects/{projectId}
```

Backend API가 변경되지 않는 한 Frontend는 독립적으로 개발할 수 있다.

---

## 4.4 재사용 가능한 컴포넌트

공통 UI는 Component 단위로 분리한다.

예)

- Header
- Sidebar
- Button
- Table
- Dialog
- Pagination
- Search Form

동일한 기능을 여러 화면에서 재사용할 수 있도록 설계한다.

---

## 4.5 Feature 기반 구조

기능 단위(Feature)로 프로젝트를 구성한다.

예)

- Dashboard
- Project
- Task
- Schedule
- Issue
- Report
- CMDB
- Admin

기능 간의 의존성을 최소화한다.

---

# 5. 기술 스택

| 구분 | 기술 |
|------|------|
| Framework | React |
| Language | TypeScript |
| Build Tool | Vite |
| Package Manager | npm |
| Linter | ESLint |
| HTTP Client | Axios |
| Router | React Router |
| UI Library | Material UI (예정) |
| Authentication | JWT |
| Backend | Spring Boot |
| Database | MariaDB |

---

# 6. 화면 구성

모든 업무 화면은 공통 Layout을 사용한다.

```
+------------------------------------------------------+
| Header                                               |
+----------------------+-------------------------------+
| Sidebar              |                               |
|                      |                               |
|                      |            Content            |
|                      |                               |
|                      |                               |
+----------------------+-------------------------------+
```

Header와 Sidebar는 공통 컴포넌트이며, Content 영역만 기능에 따라 변경된다.

---

# 7. 데이터 흐름

Frontend는 Backend API를 호출하여 데이터를 처리한다.

```
사용자

↓

React Component

↓

Axios

↓

REST API

↓

Spring Boot

↓

MariaDB
```

Frontend는 API의 요청 및 응답(JSON)을 화면에 표현하는 역할을 수행한다.

---

# 8. 인증 구조

사용자 인증은 JWT(JSON Web Token)를 사용한다.

```
Login

↓

Access Token 발급

↓

브라우저 저장

↓

Authorization Header

↓

Backend 인증
```

향후 Refresh Token 기반의 토큰 재발급 기능을 적용한다.

---

# 9. 개발 목표

Frontend는 다음 목표를 가진다.

- 유지보수가 쉬운 구조
- 기능별 독립적인 개발
- 공통 컴포넌트 재사용
- Backend와 느슨한 결합(Loose Coupling)
- 확장 가능한 프로젝트 구조

---

# 10. 향후 확장

향후 다음 기능을 추가할 수 있도록 설계한다.

- Dashboard Widget
- Notification
- Dark Mode
- 다국어(Language)
- 모바일 반응형 UI
- 권한(Role) 기반 메뉴 제어
- 사용자 환경설정

---

# 11. 결론

PMIS Frontend는 React + TypeScript 기반의 SPA(Single Page Application)로 개발한다.

Frontend는 UI와 사용자 경험을 담당하며, Business Logic은 Backend에서 처리한다.

모든 기능은 공통 Layout과 Feature 기반 구조를 유지하여 개발하며, 프로젝트가 확장되더라도 동일한 아키텍처 원칙을 유지한다.