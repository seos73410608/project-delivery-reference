# Frontend Component Architecture

## 1. 문서 정보

| 항목 | 내용 |
|------|------|
| 시스템 | PMIS (Project Management Information System) |
| 문서명 | Frontend Component Architecture |
| 문서번호 | FA-005 |
| 작성일 | 2026-08-07 |
| 버전 | 1.0 |

---

# 2. 목적

본 문서는 PMIS Frontend의 Component Architecture를 정의한다.

Component는 역할별로 분리하며, 공통 컴포넌트와 업무 컴포넌트를 명확하게 구분한다.

모든 화면은 본 문서의 Component 구조를 기준으로 개발한다.

---

# 3. Component 설계 원칙

PMIS Frontend는 다음 원칙을 따른다.

- Component 재사용
- 단일 책임 원칙(SRP)
- Layout과 Business 기능 분리
- 공통 Component 우선 개발
- Feature 기반 구성

---

# 4. 전체 Component 구조

```

Application

│

▼

App

│

▼

Router

│

▼

Layout

│

├──────────────┬──────────────┐

▼ ▼ ▼

Header Sidebar Content

│

▼

Page

│

▼

Feature

│

▼

Component

```

---

# 5. Component 계층

```

Application

↓

App

↓

Router

↓

MainLayout

↓

DashboardPage

↓

DashboardWidget

↓

Chart

```

예)

```

Application

↓

App

↓

Router

↓

MainLayout

↓

ProjectPage

↓

ProjectTable

↓

ProjectRow

```

---

# 6. Layout Component

Layout은 모든 업무 화면에서 공통으로 사용한다.

구성

```

layouts

│

├── MainLayout

│

└── LoginLayout

```

### MainLayout

구성

```

Header

↓

Sidebar

↓

Breadcrumb

↓

Content

```

### LoginLayout

구성

```

Login Form

```

---

# 7. Header Component

Header는 시스템 전체에서 하나만 존재한다.

구성

```

Header

├── Logo

├── System Title

├── Notification

├── User Profile

└── Logout

```

Header는 업무 기능을 포함하지 않는다.

---

# 8. Sidebar Component

Sidebar는 메뉴를 표시한다.

예)

```

Dashboard

Project

Task

Issue

Schedule

Risk

CMDB

Report

Admin

```

권한(Role)에 따라 메뉴를 제어한다.

---

# 9. Breadcrumb Component

현재 화면 위치를 표시한다.

예)

```

Dashboard

Project > List

Project > Detail

Issue > Detail

```

---

# 10. Page Component

Page는 화면을 조립하는 역할만 수행한다.

예)

```

ProjectPage

↓

ProjectSearch

↓

ProjectTable

↓

Pagination

```

Page에는 Business Logic을 작성하지 않는다.

---

# 11. Feature Component

Feature는 업무 기능을 담당한다.

예)

```

Project

│

├── ProjectSearch

├── ProjectTable

├── ProjectDialog

├── ProjectForm

└── ProjectCard

```

Feature는 다른 Feature를 직접 참조하지 않는다.

---

# 12. Common Component

공통 UI는 components에서 관리한다.

예)

```

components

│

├── Button

├── Table

├── Dialog

├── Pagination

├── Loading

├── ConfirmDialog

├── EmptyState

└── SearchBox

```

재사용 가능한 UI만 포함한다.

---

# 13. Form Component

입력 화면은 Form Component를 사용한다.

예)

```

ProjectForm

↓

TextField

↓

Select

↓

DatePicker

↓

Button

```

---

# 14. Table Component

목록 화면은 Table Component를 사용한다.

예)

```

ProjectTable

↓

ProjectRow

↓

ProjectCell

```

Table 내부에서 API를 호출하지 않는다.

---

# 15. Dialog Component

Dialog는 독립 Component로 작성한다.

예)

```

ProjectDialog

IssueDialog

ReportDialog

```

---

# 16. Dashboard Component

Dashboard는 Widget 단위로 분리한다.

예)

```

Dashboard

│

├── ProjectStatusWidget

├── ScheduleWidget

├── IssueWidget

├── RiskWidget

└── ReportWidget

```

Widget은 서로 독립적으로 개발한다.

---

# 17. API Component 관계

```

Component

↓

Hook

↓

Service

↓

API

↓

Backend

```

Component가 직접 API를 여러 곳에서 호출하지 않는다.

---

# 18. Component 의존성

허용

```

Page

↓

Feature

↓

Common Component

```

금지

```

Feature A

↓

Feature B

```

Feature 간 직접 의존은 허용하지 않는다.

---

# 19. Component 생성 규칙

새로운 화면을 추가할 경우

예)

```

Project

↓

ProjectPage

↓

ProjectSearch

↓

ProjectTable

↓

ProjectDialog

↓

projectApi

```

동일한 패턴을 유지한다.

---

# 20. Component 책임

| Component | 역할 |
|------------|------|
| Layout | 화면 구조 |
| Header | 상단 메뉴 |
| Sidebar | 좌측 메뉴 |
| Breadcrumb | 현재 위치 |
| Page | 화면 구성 |
| Feature | 업무 기능 |
| Component | UI |
| Hook | 공통 로직 |
| Service | Frontend 서비스 |
| API | Backend 통신 |

---

# 21. 개발 순서

Frontend Component는 다음 순서로 개발한다.

1. MainLayout
2. Header
3. Sidebar
4. Breadcrumb
5. Router
6. Dashboard
7. Project
8. Task
9. Schedule
10. Issue
11. Report
12. Admin

---

# 22. Component 설계 목표

PMIS Frontend는 다음 목표를 가진다.

- 높은 재사용성
- 낮은 결합도
- 높은 응집도
- 유지보수 용이성
- 기능별 독립 개발
- 일관된 화면 구조

---

# 23. 결론

PMIS Frontend는 Component 기반 아키텍처를 적용한다.

모든 화면은 Layout → Page → Feature → Component 구조를 유지하며, 공통 UI는 재사용 가능한 Component로 관리한다.

Feature 간 직접 의존성을 제거하고 역할을 명확하게 분리하여 확장성과 유지보수성을 확보한다.