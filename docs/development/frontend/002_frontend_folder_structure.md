# Frontend Folder Structure

## 1. 문서 정보

| 항목 | 내용 |
|------|------|
| 시스템 | PMIS (Project Management Information System) |
| 문서명 | Frontend Folder Structure |
| 문서번호 | FA-002 |
| 작성일 | 2026-08-07 |
| 버전 | 1.0 |

---

# 2. 목적

본 문서는 PMIS Frontend 프로젝트의 디렉터리 구조를 정의한다.

Frontend 프로젝트는 기능(Feature) 중심으로 구성하며, 공통 컴포넌트와 업무 기능을 명확하게 분리하여 유지보수성과 확장성을 확보한다.

---

# 3. 전체 구조

```
pmis-frontend
│
├── public
│
├── src
│   │
│   ├── api
│   │
│   ├── assets
│   │
│   ├── components
│   │
│   ├── features
│   │
│   ├── hooks
│   │
│   ├── layouts
│   │
│   ├── pages
│   │
│   ├── router
│   │
│   ├── services
│   │
│   ├── store
│   │
│   ├── styles
│   │
│   ├── types
│   │
│   ├── utils
│   │
│   ├── App.tsx
│   │
│   └── main.tsx
│
├── package.json
│
├── tsconfig.json
│
└── vite.config.ts
```

---

# 4. 디렉터리 설명

## public

정적 리소스를 저장한다.

예)

- favicon
- logo
- robots.txt

React Build 시 그대로 배포된다.

---

## src

애플리케이션의 모든 소스코드를 저장한다.

모든 개발은 src 디렉터리 내부에서 진행한다.

---

## api

Backend REST API 호출을 담당한다.

예)

```
api

├── authApi.ts

├── projectApi.ts

├── scheduleApi.ts

├── issueApi.ts
```

Backend API 변경 시 api 디렉터리만 수정한다.

---

## assets

이미지 및 아이콘을 저장한다.

예)

```
assets

├── images

├── icons

├── logo
```

---

## components

공통 UI Component를 저장한다.

업무와 관계없이 재사용 가능한 컴포넌트를 관리한다.

예)

```
components

├── common

│     Button

│     Table

│     Modal

│     Loading

│     Pagination

│

├── layout

│     Header

│     Sidebar

│     Footer

│

└── form

      SearchForm

      DatePicker

      SelectBox
```

동일한 UI를 여러 화면에서 재사용한다.

---

## features

업무 기능별 컴포넌트를 저장한다.

PMIS의 핵심 업무 기능이 위치한다.

```
features

├── dashboard

├── project

├── task

├── schedule

├── issue

├── risk

├── report

├── cmdb

└── admin
```

Feature 간의 의존성은 최소화한다.

---

## hooks

Custom React Hook을 저장한다.

예)

```
hooks

useAuth

usePagination

useApi
```

공통 로직을 재사용하기 위해 사용한다.

---

## layouts

공통 Layout을 정의한다.

예)

```
layouts

MainLayout

LoginLayout
```

모든 업무 화면은 MainLayout을 사용한다.

---

## pages

Router와 연결되는 화면(Page)을 저장한다.

예)

```
pages

LoginPage

DashboardPage

ProjectPage

TaskPage

IssuePage
```

Page는 Layout과 Feature를 조합하는 역할만 수행한다.

Business Logic은 최소화한다.

---

## router

React Router를 관리한다.

예)

```
router

index.tsx
```

화면 이동을 정의한다.

---

## services

Frontend 공통 서비스를 저장한다.

예)

```
services

authService

tokenService

storageService
```

API와는 별도로 Frontend에서 사용하는 서비스 계층이다.

---

## store

전역 상태(State)를 관리한다.

예)

```
store

auth

user

menu
```

초기에는 최소한으로 사용한다.

프로젝트가 커질 경우 확장한다.

---

## styles

공통 CSS 및 Theme를 관리한다.

예)

```
styles

theme

common.css

variables.css
```

---

## types

TypeScript Interface 및 Type을 정의한다.

예)

```
types

Project.ts

User.ts

ApiResponse.ts
```

Backend DTO와 유사한 구조를 유지한다.

---

## utils

공통 Utility를 저장한다.

예)

```
utils

date.ts

number.ts

validation.ts
```

Business Logic은 포함하지 않는다.

---

# 5. Feature 구조

각 Feature는 동일한 구조를 유지한다.

예)

```
project

├── components

├── pages

├── hooks

├── services

├── types

└── index.ts
```

Feature 내부의 코드는 다른 Feature에 직접 의존하지 않는다.

---

# 6. 컴포넌트 구성 원칙

Component는 하나의 책임만 가진다.

예)

```
ProjectTable

ProjectSearchForm

ProjectDetailDialog
```

하나의 Component에서 여러 역할을 수행하지 않는다.

---

# 7. 페이지 구성 원칙

Page는 화면을 구성하는 역할만 수행한다.

```
ProjectPage

↓

ProjectSearch

↓

ProjectTable

↓

ProjectDialog
```

Page는 Component를 조합하는 역할만 담당한다.

---

# 8. API 구성 원칙

API는 업무별 파일로 분리한다.

예)

```
authApi

projectApi

scheduleApi

issueApi
```

Backend Controller와 유사한 구조를 유지한다.

---

# 9. 확장 원칙

새로운 업무가 추가되면 Feature만 추가한다.

예)

```
features

├── project

├── issue

├── schedule

├── report

└── vendor
```

기존 구조를 변경하지 않는다.

---

# 10. 개발 원칙

Frontend는 다음 원칙을 따른다.

- Feature 중심 개발
- Component 재사용
- 단일 책임 원칙(SRP)
- 공통 Layout 사용
- Business Logic 최소화
- Backend API 중심 개발

---

# 11. 결론

PMIS Frontend는 Feature 기반 디렉터리 구조를 사용한다.

공통 기능은 components에서 관리하며, 업무 기능은 features에서 관리한다.

Layout과 Router를 중심으로 화면을 구성하고, Backend API와 느슨하게 결합된 구조를 유지하여 프로젝트 확장 시에도 안정적인 개발이 가능하도록 설계한다.