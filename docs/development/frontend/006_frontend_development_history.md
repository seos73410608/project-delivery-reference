# Frontend 개발 이력

## 문서 정보

| 항목 | 내용 |
| --- | --- |
| 문서명 | Frontend 개발 이력 |
| 작성일 | 2026-08-07 |
| 최종 수정일 | 2026-08-10 |
| 작성자 | SeoS |
| 버전 | v1.1 |

---

# 목적

본 문서는 PMIS(Project Management Information System) Frontend의 개발 이력을 관리한다.

프로젝트 진행 과정에서 구현된 기능, 구조 변경, 기술 스택 변경 사항 등을 기록하여 프로젝트의 추적성과 유지보수성을 확보한다.

---

# 개발 이력

---

## 2026-08-07

### Frontend 프로젝트 생성

#### 작업 내용

- React 기반 Frontend 프로젝트 생성
- Vite + React + TypeScript 환경 구성
- Node.js 환경 구성
- npm 환경 구성
- React Router 적용 준비

#### 기술 스택

| 구분 | 내용 |
| --- | --- |
| Framework | React |
| Build Tool | Vite |
| Language | TypeScript |
| Package Manager | npm |
| Router | React Router DOM |

---

### 프로젝트 구조 구성

Frontend 프로젝트를 Backend와 분리하였다.

```text
project-delivery-reference
│
├── pmis-backend
├── pmis-frontend
└── docs
```

---

### Feature 기반 디렉터리 구성

초기 Frontend 디렉터리 구조를 생성하였다.

```text
src
│
├── api
├── assets
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

모든 초기 디렉터리는 `.gitkeep` 정책을 적용하였다.

---

### Vite 기본 예제 제거

Vite에서 기본으로 생성된 예제 화면과 리소스를 제거하였다.

#### 제거 항목

- `hero.png`
- `react.svg`
- `vite.svg`
- Counter Example
- Documentation Sample
- Social Link Example

기존 Vite Sample Application을 제거하고 PMIS 전용 Frontend 구조로 변경하였다.

---

### Router 구조 적용

React Router DOM을 적용하였다.

Application의 기본 화면 흐름을 다음과 같이 구성하였다.

```text
App
 ↓
AppRouter
 ↓
MainLayout
 ↓
DashboardPage
```

향후 모든 주요 화면은 Router를 통해 관리한다.

예정 화면:

- Dashboard
- Project
- Task
- Schedule
- Issue
- Risk
- Change
- Report
- CMDB
- Admin

---

### Main Layout 구현

PMIS Frontend의 기본 Layout을 구현하였다.

초기 Layout은 다음 영역으로 구성하였다.

```text
MainLayout
│
├── Header
├── Sidebar
└── Content
    ├── Breadcrumb
    └── Page
```

초기에는 Header, Sidebar, Breadcrumb을 Placeholder 형태로 구현하였다.

이후 각 영역을 독립적인 Component로 분리하였다.

---

### Dashboard Page 구현

Dashboard Page Skeleton을 구현하였다.

현재 초기 Dashboard에서는 다음 내용을 표시한다.

- PMIS Dashboard
- Development Status

향후 실제 프로젝트 데이터를 기반으로 Dashboard Widget을 구성할 예정이다.

---

### App 구조 변경

기존 Vite Sample Application 구조를 PMIS Application 구조로 변경하였다.

기존:

```text
App
 ↓
Vite Sample
```

변경:

```text
App
 ↓
AppRouter
 ↓
MainLayout
 ↓
DashboardPage
```

---

## 2026-08-08

### Frontend Layout Component 구현

Frontend Layout의 주요 영역을 독립적인 React Component로 분리하였다.

#### Header Component

`Header.tsx`를 구현하였다.

```text
src/components/layout/Header.tsx
```

Header는 PMIS Application의 상단 영역을 담당한다.

---

#### Sidebar Component

`Sidebar.tsx`를 구현하였다.

```text
src/components/layout/Sidebar.tsx
```

Sidebar는 PMIS 주요 기능으로 이동하기 위한 좌측 Navigation 영역을 담당한다.

향후 다음 기능을 연결할 예정이다.

- Dashboard
- Project
- Task
- Schedule
- Issue
- Risk
- Change
- CMDB
- Report
- Admin

---

#### Breadcrumb Component

`Breadcrumb.tsx`를 구현하였다.

```text
src/components/layout/Breadcrumb.tsx
```

React Router의 현재 URL을 기반으로 Breadcrumb을 동적으로 구성하도록 구현하였다.

예:

```text
Dashboard
```

```text
Dashboard / Project
```

```text
Dashboard / Project / Detail
```

Router 경로가 추가되면 Breadcrumb도 확장할 수 있도록 구성하였다.

---

### MainLayout Refactoring

기존 `MainLayout.tsx`에 직접 작성되어 있던 Header, Sidebar, Breadcrumb 영역을 독립 Component로 분리하였다.

변경 전:

```text
MainLayout
├── Header 구현 코드
├── Sidebar 구현 코드
├── Breadcrumb 구현 코드
└── Outlet
```

변경 후:

```text
MainLayout
├── Header
├── Sidebar
├── Breadcrumb
└── Outlet
```

현재 `MainLayout`은 Layout Component들을 조합하고 Page를 렌더링하는 역할을 담당한다.

---

### Frontend Path Alias 구성

Frontend에서 `@` Alias 기반 Import 구조를 적용하였다.

예:

```typescript
import Header from "@/components/layout/Header";
import Sidebar from "@/components/layout/Sidebar";
import Breadcrumb from "@/components/layout/Breadcrumb";
```

TypeScript 및 Vite 환경의 경로 설정을 정리하여 Frontend Component 간 Import 경로를 일관되게 관리하도록 구성하였다.

---

### Vite Path Configuration 정리

Vite 최신 환경에서 TypeScript Path Alias를 사용할 수 있도록 Vite 설정을 정리하였다.

기존 별도 Path Plugin 의존성을 검토하고 Vite의 기본 Path Resolution 방식을 사용하는 방향으로 정리하였다.

---

## 2026-08-10

### Common UI Component 구현

PMIS Frontend 전체에서 재사용할 수 있는 공통 UI Component를 구현하였다.

구성:

```text
src/components/common
│
├── Button.tsx
├── Card.tsx
├── EmptyState.tsx
└── Loading.tsx
```

---

### Button Component

`Button.tsx`를 구현하였다.

```text
src/components/common/Button.tsx
```

주요 기능:

- Primary Button
- Secondary Button
- Danger Button
- Text Button
- Small / Medium / Large Size
- Disabled 상태
- HTML Button Attribute 지원

---

### Card Component

`Card.tsx`를 구현하였다.

```text
src/components/common/Card.tsx
```

주요 기능:

- 공통 Card UI
- Title
- Description
- Content 영역
- Small / Medium / Large Padding

Dashboard 및 각 업무 화면에서 데이터 영역을 표현하기 위한 기본 Container로 사용한다.

---

### Loading Component

`Loading.tsx`를 구현하였다.

```text
src/components/common/Loading.tsx
```

주요 기능:

- Loading Spinner
- Loading Message
- Small / Medium / Large Size
- 접근성을 고려한 `role="status"` 적용

향후 API 호출 상태 및 비동기 데이터 처리 시 활용한다.

---

### EmptyState Component

`EmptyState.tsx`를 구현하였다.

```text
src/components/common/EmptyState.tsx
```

주요 기능:

- 데이터 없음 상태 표시
- 사용자 정의 Title
- 사용자 정의 Message
- Action Component 지원

Project, Issue, Risk, Schedule 등의 목록 화면에서 데이터가 없는 경우 공통으로 활용할 예정이다.

---

# Frontend Component 구조

현재 Frontend Component 구조는 다음과 같다.

```text
src
│
└── components
    │
    ├── common
    │   ├── Button.tsx
    │   ├── Card.tsx
    │   ├── EmptyState.tsx
    │   └── Loading.tsx
    │
    ├── form
    │
    └── layout
        ├── Header.tsx
        ├── Sidebar.tsx
        └── Breadcrumb.tsx
```

---

# 현재 Application 구조

현재 PMIS Frontend의 기본 Application 구조는 다음과 같다.

```text
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
 │
 └── Content
      │
      ├── Breadcrumb
      │
      └── Outlet
             │
             ▼
        Page Component
```

---

# Git 이력

## Frontend Layout Branch

```text
feature/frontend-layout
```

주요 작업:

```text
feat(frontend): initialize React project directory structure
feat(frontend): implement application routing and main layout
```

Frontend 초기 디렉터리 구조와 Application Layout을 구현하였다.

---

## Frontend Component Branch

```text
feature/frontend-components
```

주요 Commit:

```text
feat(frontend): add sidebar component
feat(frontend): add breadcrumb component
feat(frontend): add common UI components
```

현재 Feature Branch에서 다음 Component를 구현하였다.

- Header
- Sidebar
- Breadcrumb
- Button
- Card
- Loading
- EmptyState

---

# 현재 진행 상태

| 기능 | 상태 |
| --- | --- |
| React Project | ✅ |
| Vite | ✅ |
| TypeScript | ✅ |
| React Router | ✅ |
| Main Layout | ✅ |
| Dashboard | ✅ |
| Header | ✅ |
| Sidebar | ✅ |
| Breadcrumb | ✅ |
| Button | ✅ |
| Card | ✅ |
| Loading | ✅ |
| EmptyState | ✅ |
| Login | ⏳ |
| Authentication | ⏳ |
| Project Module | ⏳ |
| WBS Module | ⏳ |
| Schedule Module | ⏳ |
| Issue Module | ⏳ |
| Risk Module | ⏳ |
| Change Module | ⏳ |
| CMDB Module | ⏳ |
| Report Module | ⏳ |
| Admin Module | ⏳ |

---

# 다음 개발 계획

다음 Feature에서는 실제 PMIS 업무 화면 구현을 진행한다.

우선순위:

1. Dashboard UI 개선
2. Project 화면 구현
3. Project API 연동
4. Project 목록 / 검색
5. Project 상세 화면
6. WBS / Schedule 화면
7. Issue / Risk / Change 화면
8. Report 화면
9. CMDB 화면
10. Admin 화면

공통 Component는 실제 업무 화면 구현 과정에서 필요한 기능을 기준으로 지속적으로 확장한다.

---

# Frontend 개발 절차

Frontend는 Backend와 동일하게 Feature Branch 전략을 적용한다.

개발 절차:

```text
Feature 생성
    ↓
개발
    ↓
실행 확인
    ↓
문서 현행화
    ↓
Commit
    ↓
Push
    ↓
Merge
    ↓
Develop 반영
```

---

# Branch 정책

```text
main
 │
 └── develop
       │
       ├── feature/frontend-layout
       │
       ├── feature/frontend-components
       │
       ├── feature/frontend-dashboard
       │
       ├── feature/frontend-project
       │
       ├── feature/frontend-wbs
       │
       └── feature/frontend-report
```

Branch 역할:

| Branch | 역할 |
| --- | --- |
| `main` | Release |
| `develop` | Integration |
| `feature/*` | Feature Development |
| `release/*` | Release Preparation |
| `hotfix/*` | Production Fix |

---

# 문서 및 소스코드 관리 원칙

Frontend 개발 과정에서 구현된 기능과 문서는 동일한 상태를 유지한다.

주요 변경 사항이 발생하면 다음 순서로 관리한다.

```text
소스 변경
    ↓
실행 및 검증
    ↓
문서 현행화
    ↓
Commit
    ↓
Push
    ↓
Merge
```

문서에는 다음 내용을 지속적으로 기록한다.

- Frontend 구조 변경
- Component 추가 및 변경
- Router 변경
- 기술 스택 변경
- Build 설정 변경
- 주요 Bug Fix
- Feature 개발 이력
- Git Branch 및 주요 Commit

---

# 향후 Component 확장 계획

현재 공통 Component:

```text
components/common
├── Button
├── Card
├── EmptyState
└── Loading
```

향후 필요에 따라 다음 Component를 추가한다.

```text
components/common
├── Button
├── Card
├── Loading
├── EmptyState
├── Modal
├── Table
├── Pagination
├── Badge
├── Input
├── Select
├── DatePicker
└── ConfirmDialog
```

단, 실제 화면 개발 과정에서 반복적으로 사용되는 UI를 우선적으로 Component화한다.

---

# 비고

Frontend는 Backend와 동일하게 Feature Branch 전략을 적용한다.

공통 UI Component는 특정 업무 화면에 종속되지 않도록 구성하며, Project / Schedule / Issue / Risk / CMDB / Report 등의 여러 업무 화면에서 재사용하는 것을 원칙으로 한다.

현재 Frontend는 **Application Layout 및 공통 UI 기반 구축 단계**를 완료하고, 다음 단계로 **실제 PMIS 업무 화면 구현 단계**로 진입한다.

---

**Last Updated** : 2026-08-10

**Current Branch** : feature/frontend-components

**Current Phase** : Frontend Layout & Common UI Component

**Maintainer** : Seo Seokhyeon
