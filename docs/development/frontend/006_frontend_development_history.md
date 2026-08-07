# Frontend 개발 이력

## 문서 정보

| 항목 | 내용 |
|------|------|
| 문서명 | Frontend 개발 이력 |
| 작성일 | 2026-08-07 |
| 작성자 | SeoS |
| 버전 | v1.0 |

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
- Node.js LTS 설치
- npm 환경 구성
- React Router 적용 준비

#### 기술 스택

| 구분 | 내용 |
|------|------|
| Framework | React |
| Build Tool | Vite |
| Language | TypeScript |
| Package Manager | npm |
| Router | React Router DOM |

---

### 프로젝트 구조 구성

Frontend 프로젝트를 Backend와 분리하였다.

```
project-delivery-reference
│
├── pmis-backend
│
├── pmis-frontend
│
└── docs
```

---

### Feature 기반 디렉터리 구성

초기 디렉터리 구조를 생성하였다.

```
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

모든 디렉터리는 `.gitkeep` 정책을 적용하였다.

---

### Router 구조 적용

React Router를 적용하였다.

```
App

↓

AppRouter

↓

MainLayout

↓

DashboardPage
```

향후 모든 화면은 Router를 통해 관리한다.

예)

- Dashboard
- Project
- Task
- Schedule
- Issue
- Report
- CMDB
- Admin

---

### Main Layout 구현

초기 Main Layout을 구현하였다.

구성 요소

- Header (Placeholder)
- Sidebar (Placeholder)
- Breadcrumb (Placeholder)
- Content Area

현재는 구조만 구현하였으며,
추후 각각 독립 Component로 분리한다.

---

### Dashboard Page 구현

Dashboard Page Skeleton을 구현하였다.

현재는

- PMIS Dashboard
- Development Status

를 표시하도록 구현하였다.

향후 Dashboard Widget 형태로 변경 예정이다.

---

### App 구조 변경

기존 Vite Sample Application을 제거하였다.

기존

```
App

↓

Vite Sample
```

변경

```
App

↓

AppRouter

↓

MainLayout

↓

Dashboard
```

---

### 제거한 항목

초기 Vite 예제 제거

- hero.png
- react.svg
- vite.svg
- Counter Example
- Documentation Sample
- Social Link Example

PMIS 전용 화면으로 변경하였다.

---

# Git 이력

### Branch

```
feature/frontend-layout
```

### 주요 Commit

```
feat(frontend): initialize React project directory structure

feat(frontend): implement application routing and main layout
```

---

# 현재 진행 상태

| 기능 | 상태 |
|------|------|
| React Project | ✅ |
| Vite | ✅ |
| TypeScript | ✅ |
| React Router | ✅ |
| Main Layout | ✅ |
| Dashboard | ✅ |
| Header | 🚧 |
| Sidebar | 🚧 |
| Breadcrumb | 🚧 |
| Login | ⏳ |
| Authentication | ⏳ |
| Project Module | ⏳ |
| Task Module | ⏳ |
| Issue Module | ⏳ |
| Report Module | ⏳ |
| Admin Module | ⏳ |

---

# 다음 개발 계획

다음 Feature Branch에서 구현 예정

- Header Component
- Sidebar Component
- Breadcrumb Component
- Layout Refactoring
- Dashboard UI 개선

---

# 비고

Frontend는 Backend와 동일하게 Feature Branch 전략을 적용한다.

개발 절차

```
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
```

문서와 소스코드는 항상 동일한 상태를 유지하는 것을 원칙으로 한다.