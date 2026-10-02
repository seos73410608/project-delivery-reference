# PMIS Development Roadmap

> **Version:** 1.14  
> **Last Updated:** 2026-09-28  
> **Project:** Project Management Information System (PMIS)

---

## 1. Development Goal

실제 SI 프로젝트에서 사용하는 PMIS를 Full Stack으로 구현한다. 단순 CRUD를 넘어 사용자/권한, 프로젝트, WBS, 일정·진척, 이슈, 리스크, 변경, 증적·검수, CMDB, Dashboard, Report, 운영 인수인계 및 Spring AI Assistant를 포함한다.

핵심 차별점은 산출물·증적의 대상, 필수 여부, 실제 존재 여부, 검토/검수 상태 및 누락을 추적하는 체계다.

## 2. Development Strategy

### Full-stack flow

```text
Requirement → Domain/Data Model → Backend API Design → Backend Implementation
→ API Verification → Frontend Implementation → API Integration → UI Verification
→ E2E Verification → Documentation → Commit → Push → Review/Merge
→ develop Integration Test
```

### Evidence flow

```text
Project → WBS/Task → Activity/Deliverable → Evidence Requirement
→ Evidence Registration → Verification → Status → Inspection/Approval
→ Report/Dashboard
```

증적 상태: `PRESENT`, `MISSING`, `PENDING`, `NOT_REQUIRED`.

체크 항목에는 필수 여부, 유형, 파일/문서, 검증 상태, 검토자, 검토일, 비고, 제출기한을 포함한다.

## 3. Architecture

```text
React + TypeScript + Vite
          ↓ REST API
Spring Boot + Spring Security + JWT
Controller / Service / Repository / DTO / Mapper / Specification
          ↓
       MariaDB
          ↓
File/Object Storage (Planned)
          ↓
Spring AI (Assistant / Analysis / RAG)
```

- **Backend:** Spring Boot 4.1.0, Gradle Kotlin DSL, Java 21, Spring Security, JWT, MariaDB, Swagger/OpenAPI, 공통 응답·예외 처리
- **Frontend:** React, Vite, TypeScript, React Router DOM, feature-based 구조, 공통 UI, API/service 분리

## 4. Git Flow

```text
main ← release ← develop ← feature/*
```

기능은 Feature Branch에서 개발·검증 후 `develop`에 통합한다. Hotfix는 `hotfix/*`에서 수정·검증한다.

### Backend branches

`feature/common`, `feature/security`, `feature/auth-jwt`, `feature/auth-login`, `feature/auth-refresh`, `feature/auth-role`, `feature/swagger`, `feature/project-crud`, `feature/project-search`, `feature/project-dashboard`, `feature/project-detail`, `feature/wbs-management`, `feature/schedule-management`, `feature/issue-management`, `feature/risk-management`, `feature/change-management`, `feature/evidence-management`, `feature/cmdb-management`, `feature/dashboard`, `feature/report-excel`, `feature/report-pdf`, `feature/spring-ai`, `feature/ai-risk-analysis`, `feature/ai-report-summary`

### Frontend branches

`feature/frontend-layout`, `feature/frontend-components`, `feature/frontend-dashboard-hmc-reference`, `feature/frontend-api-integration`, `feature/frontend-project`, `feature/frontend-wbs`, `feature/frontend-schedule`, `feature/frontend-issue`, `feature/frontend-risk`, `feature/frontend-change`, `feature/frontend-evidence`, `feature/frontend-cmdb`, `feature/frontend-report`, `feature/frontend-auth`, `feature/frontend-ai`

## 5. Version Strategy

Semantic Versioning `MAJOR.MINOR.PATCH`를 따른다.

- Major: 호환성 파괴 변경
- Minor: 기능 추가
- Patch: 버그 수정/개선

마지막 정식 Release는 **v0.5.3**이며, 이후 통합 기능은 아직 별도 정식 Release로 태그하지 않았다.

## 6. Commit Convention

`feat:`, `fix:`, `refactor:`, `style:`, `test:`, `docs:`, `build:`, `chore:`, `perf:`, `security:`를 사용한다.

예:
- `feat: implement project CRUD`
- `feat(frontend): integrate schedule calendar API`
- `refactor(frontend): split schedule calendar into components`

## 7. Sprint Plan

### Sprint 0 — Base Project

Spring Boot/Gradle/MariaDB, 패키지 구조, Base Entity, 공통 응답·예외, JPA 및 React/Vite/TypeScript 기반 구축.

**상태: 완료**

### Sprint 1 — Authentication & Security

JWT Provider/Filter, SecurityConfig, 사용자 principal, 로그인, refresh token, 역할 기반 인가, password encoder.

Backend 보안 기반 및 인증/인가 구현 완료. Frontend 로그인 UI·토큰 처리·보호 라우트는 후속 검증 대상.

### Sprint 2 — Project Management & Frontend Foundation

Project CRUD, 검색/정렬, dashboard KPI API, detail API 및 Controller/Service/Mapper 분리 완료.

Frontend 레이아웃·공통 컴포넌트·대시보드 기반 및 Project CRUD/API 연계 완료.

### Sprint 3 — WBS / Schedule Management

- **WBS Backend:** CRUD, 트리, 진척 계산, task 관계, validation, 삭제 처리, Swagger 검증 완료.
- **WBS Frontend:** API client/integration, tree/detail/form/status form, progress, delete 완료.
- **WBS 남은 일:** Browser verification, integration test.
- **Schedule Backend:** CRUD, project 조회, 상태/순서 관리, 검색·필터, pagination/sorting, 기간 검색, validation, calendar API, Swagger 검증 완료.
- **Schedule Frontend:** 관리 UI, 목록/검색/상세/등록/수정/삭제/상태, API 연계, Calendar UI/API, 월 이동·오늘 이동·범위 조회, 컴포넌트/유틸 분리, production build 완료.
- **Schedule 남은 일:** Browser verification, integration test, Gantt, WBS 연계, timeline/milestone/progress 시각화.

### Sprint 4 — Issue / Risk / Change Management

#### Issue

- **Issue Backend 완료:** CRUD, 동적 검색/필터, pagination/sorting, 상태 workflow, 삭제, Swagger, project별 Issue Key 생성 및 `(project_id, issue_key)` unique constraint.
- **Issue Frontend 완료:** API client, 목록/검색/상세/등록/수정/상태/삭제, key 표시, API 연계 및 build.
- **Issue 남은 일:** Browser/E2E verification.

#### Risk

- **Risk Backend 완료:** CRUD, 검색/필터, 상태 workflow, score/level 계산, project별 key 생성, Swagger.
- **Risk Frontend 완료:** 목록/검색/상세/등록/수정/상태/삭제, summary/matrix, API 연계, loading/empty/error 및 production build.
- **Risk 남은 일:** Browser/E2E verification.

#### Change

Change Management의 Frontend workflow 및 API integration을 구현했다.

- Change API client
- 프로젝트별 Change 목록 조회
- 검색/상태/우선순위 필터
- Change Summary
- Change 상세 조회
- Change 등록/수정
- 상태 변경 workflow
- 승인/반려 workflow
- 승인 의견 입력
- 상태/승인 API 연계
- 상세 화면 수정 버튼
- 프로젝트 상세 → Change Management navigation
- `/project/:projectId/change` route
- loading/empty/error 처리
- production build 검증

현재 구현된 주요 workflow:

```text
REQUESTED
    ↓
ANALYZING
    ↓
PENDING_APPROVAL
    ↓
APPROVED / REJECTED
    ↓
IMPLEMENTING
    ↓
VERIFIED
    ↓
CLOSED
```

Change frontend workflow UI는 `feature/change-management-ui`에서 구현 후 `develop`에 통합했다.

- Feature commit: `f3ed60c`
- Commit message: `feat: complete change management workflow ui`
- Integration: fast-forward merge into `develop`
- Integration push: `origin/develop`

**Change 남은 일:**
- Browser verification
- 승인/반려 실제 API E2E 검증
- 상태 전이 규칙 검증
- Change 이력/감사 로그 연계
- 영향 분석 고도화
- Backend Change 도메인 전체 범위 점검 및 보완
- Evidence/Inspection과의 변경 영향 연계

## 8. Milestones

| ID | Milestone |
|---|---|
| M1 | Base Project Complete |
| M2 | Authentication Complete |
| M3 | Project Management Backend Complete |
| M4 | Frontend Foundation Complete |
| M5 | Project Frontend CRUD Complete |
| M6 | WBS & Schedule Backend Complete |
| M7 | WBS & Schedule Frontend Complete |
| M8 | Issue / Risk / Change Complete |
| M9 | Evidence & Inspection Complete |
| M10 | CMDB Complete |
| M11 | Dashboard & Report Complete |
| M12 | Spring AI Complete |
| M13 | Production Ready |
| M14 | Portfolio Release |

> M8은 Change의 Browser/E2E 검증 및 일부 workflow/이력 보완이 남아 있으므로 최종 완료 판정 전 단계로 관리한다.

## 9. Estimated Schedule

| Sprint | Duration |
|---|---:|
| Sprint 0 | 1 week |
| Sprint 1 | 2 weeks |
| Sprint 2 | 2 weeks |
| Sprint 3 | 2 weeks |
| Sprint 4 | 2 weeks |
| Sprint 5 | 2 weeks |
| Sprint 6 | 2 weeks |
| Sprint 7 | 2 weeks |
| Sprint 8 | 2 weeks |
| Sprint 9 | 1 week |

예상 총 기간: 약 18–20주.

## 10. Quality Objectives

### Backend
- Layered/Clean Architecture
- SOLID
- REST
- DTO/Mapper/Specification
- 도메인별 Controller/Service 분리
- Audit/History
- Evidence Traceability

### Frontend
- Component/Feature-based Architecture
- 재사용 컴포넌트
- API/service 분리
- 상태 관리
- TypeScript 안전성
- 반응형 UI
- 공통 유틸

### Integration
- API 계약
- 오류 처리
- 인증 연계
- Loading/Empty/Error
- Browser verification
- Production build
- E2E verification

## 11. Documentation

Project docs:
- `PROJECT_OVERVIEW.md`
- `DEVELOPMENT_ROADMAP.md`
- `ARCHITECTURE.md`
- `CHANGELOG.md`
- `PORTFOLIO.md`

Design docs:

`docs/design/` 아래 Issue, Project, Project Dashboard, Report, Schedule, Server Configuration, WBS, Evidence 설계 문서.

문서와 소스의 실제 상태를 일치시킨다.

## 12. Final Deliverables

### Backend

Spring Boot REST API, JWT, MariaDB, Project/WBS/Schedule/Issue/Risk/Change/Evidence/Inspection/CMDB, Dashboard API, Report, Spring AI, Swagger

### Frontend

React/TypeScript/Vite, Layout/Dashboard, Project/WBS/Schedule, Calendar/Gantt, Issue/Risk/Change, Evidence, CMDB, Report, AI Assistant

### Delivery

Docker/Compose, GitHub Actions, GitHub Repository, Portfolio Documentation

## 13. Success Criteria

SI 프로젝트 업무를 지원하는 운영 가능한 Full Stack PMIS를 구축한다.

- REST API 연계
- JWT 인증/인가
- 수행 데이터 통합
- 증적 추적·누락 탐지
- 검수/승인 이력
- Change workflow
- Spring AI Assistant
- Docker 배포
- Git Flow 적용
- 포트폴리오 시연 가능한 결과물

## 14. Current Project Status — 2026-09-28

### Backend completed

- Foundation
- Security/JWT
- login/refresh/role hierarchy
- Swagger/OpenAPI
- Project CRUD/search/dashboard/detail API
- WBS CRUD/validation/delete handling/tree
- Schedule CRUD/search/pagination/sorting/period search/validation/calendar API/Swagger
- Issue CRUD/search/workflow/key generation/unique constraint/Swagger
- Risk CRUD/search/workflow/score-level/key generation/Swagger
- Common `SearchPageableFactory` pagination refactor for Change/Issue/Risk/Schedule/WBS

### Frontend completed

- React/Vite/TypeScript/Router
- Layout
- Common UI
- Dashboard reference
- Project CRUD/API integration
- WBS UI/API integration
- Schedule management and calendar UI/API integration
- Schedule component/utility separation
- Issue UI/API integration and key display
- Risk UI/API integration, summary/matrix/forms/status/delete and production build
- Change Management UI/API integration
- Change list/search/filter/summary/detail
- Change create/edit
- Change status workflow
- Change approval/rejection UI
- Project-scoped Change route
- Change workflow API integration
- Change production build

### Verification remaining

- WBS/Schedule/Issue/Risk browser verification and integration/E2E tests
- Gantt UI and WBS/Schedule linkage
- Dashboard API integration and authentication UI
- Change browser verification
- Change approval/rejection E2E verification
- Change history/audit integration
- Evidence & Inspection implementation

## 15. WBS State

```text
Backend:
API / CRUD / validation / delete handling / tree ✓

Frontend:
API client / integration / tree / detail / form / status / delete ✓

Remaining:
browser verification → integration test → E2E verification
```

## 16. Schedule State

```text
Backend:
CRUD / search / pagination / sorting / period search / validation /
calendar API / Swagger ✓

Frontend:
management / list / detail / forms / status / API / calendar /
refactoring / build ✓

Remaining:
browser verification → integration test → Gantt and WBS linkage
→ timeline / milestone / progress visualization
```

## 17. Issue State

```text
Backend:
entity / DTO / specification / CRUD / search / workflow /
key generation / uniqueness / Swagger ✓

Frontend:
API client / list / search / detail / create / edit / status /
delete / integration ✓

Remaining:
browser verification → E2E verification
```

## 18. Risk State

```text
Backend:
CRUD / search / workflow / score-level / key generation / Swagger ✓

Frontend:
list / search / summary / matrix / detail / forms / status /
delete / integration / build ✓

Remaining:
browser verification → integration and E2E verification
```

## 19. Change State

```text
Backend:
Change API / CRUD-related endpoints / search / status workflow /
approval API integration ✓

Remaining:
domain completeness review / history-audit / impact-analysis refinement
```

```text
Frontend:
API client / project-scoped route / list / search / filter /
summary / detail / create / edit / status / approval / rejection /
workflow UI / API integration / build ✓

Remaining:
browser verification → approval/rejection E2E →
history/audit → Evidence linkage
```

### Change Git Integration Record

- Feature branch: `feature/change-management-ui`
- Latest feature commit: `f3ed60c`
- Commit: `feat: complete change management workflow ui`
- Integration branch: `develop`
- Integration method: fast-forward merge
- Integration result: `bfb2a33..f3ed60c`
- Push result: `origin/develop` updated to `f3ed60c`

Change Management implementation included:

```text
pmis-frontend/src/features/change/ChangePage.tsx
pmis-frontend/src/features/change/api/changeApi.ts
pmis-frontend/src/features/change/components/ChangeApprovalForm.tsx
pmis-frontend/src/features/change/components/ChangeDetail.tsx
pmis-frontend/src/features/change/components/ChangeDialog.tsx
pmis-frontend/src/features/change/components/ChangeForm.tsx
pmis-frontend/src/features/change/components/ChangeList.tsx
pmis-frontend/src/features/change/components/ChangeRow.tsx
pmis-frontend/src/features/change/components/ChangeStatusForm.tsx
pmis-frontend/src/features/change/components/ChangeSummary.tsx
pmis-frontend/src/features/change/components/ChangeToolbar.tsx
pmis-frontend/src/features/change/styles/Change.css
pmis-frontend/src/features/change/types/change.ts
```

Project navigation/routing integration:

```text
pmis-frontend/src/features/project/pages/ProjectDetailPage.tsx
pmis-frontend/src/router/AppRouter.tsx
pmis-frontend/src/components/layout/Sidebar.tsx
```

## 20. Git Integration Record

### Common search pagination standardization

- Feature branch: `feature/common-search-filter`
- Commit: `1f7f011` — `feat: standardize WBS search pagination`
- Scope: search contract documentation, shared `SearchPageableFactory`, and service pagination standardization for Change/Issue/Risk/Schedule/WBS.
- Merged fast-forward into `develop` and pushed to `origin/develop`.

### Change Management workflow UI

- Feature branch: `feature/change-management-ui`
- Commit: `f3ed60c` — `feat: complete change management workflow ui`
- Scope: Change detail/edit workflow, status workflow, approval/rejection workflow UI, API integration, project-scoped navigation/routing.
- Merged fast-forward into `develop`.
- `develop` pushed to `origin/develop`.
- Current integrated commit: `f3ed60c`.

## 21. Development Process & Maintenance

```text
Planning
  → Feature Branch
  → Development
  → Local Test
  → Production Build
  → Documentation
  → Commit
  → Push
  → Review/Merge
  → develop Integration Test
  → Release
```

주요 변경은 CHANGELOG에 기록한다.

Roadmap/Overview/Architecture와 실제 소스 상태를 동기화하고, 검증된 코드만 `main`에 Release하며 Release마다 Git Tag를 생성한다.

API 연계는 API 및 Browser Verification을 수행하고, Component Refactoring 후 production build를 실행한다.

## 22. Current Information

| Item | Value |
|---|---|
| Last Updated | 2026-09-28 |
| Roadmap Version | v1.14 |
| Current Release | v0.5.3 |
| Integration Branch | `develop` |
| Current Sprint | Sprint 4 — Issue / Risk / Change Management |
| Last completed backend domain | Risk Management |
| Last completed frontend domain | Change Management UI/API Workflow |
| Last integrated commit | `f3ed60c` |
| Latest integrated feature | Change Management workflow UI/API integration |
| Change workflow | Status + Approval/Rejection |
| Next stage | Change browser/E2E verification → Change history/audit → Evidence & Inspection |
| Evidence | Planned — Sprint 5 |
| Maintainer | Seo Seokhyeon |

## 23. Next Milestone

1. Change browser verification
2. Change approval/rejection API E2E verification
3. Change status transition validation
4. Change history/audit implementation
5. WBS/Schedule/Issue/Risk browser verification and E2E tests
6. Gantt UI and WBS/Schedule linkage
7. Evidence & Inspection
8. CMDB
9. Dashboard & Report
10. Spring AI
11. Finalization and portfolio release

---

# End of Document
