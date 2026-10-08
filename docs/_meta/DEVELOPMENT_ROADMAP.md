# PMIS Development Roadmap

> **Version:** 1.15  
> **Last Updated:** 2026-10-08  
> **Project:** Project Management Information System (PMIS)

---

## 1. Development Goal

실제 SI 프로젝트에서 사용하는 PMIS를 Full Stack으로 구현한다. 단순 CRUD를 넘어 사용자/권한, 프로젝트, WBS, 일정·진척, 이슈, 리스크, 변경, 증적·검수, CMDB, Dashboard, Report, 운영 인수인계 및 Spring AI Assistant를 포함한다.

핵심 차별점은 산출물·증적의 대상, 필수 여부, 실제 존재 여부, 검토/검수 상태 및 누락을 추적하는 체계다.

---

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

증적 상태:

- `PRESENT`
- `MISSING`
- `PENDING`
- `NOT_REQUIRED`

검증 상태:

- `PENDING`
- `APPROVED`
- `REJECTED`

검수 상태:

- `PLANNED`
- `IN_PROGRESS`
- `PASSED`
- `FAILED`
- `CLOSED`

체크 항목에는 필수 여부, 유형, 파일/문서, 검증 상태, 검토자, 검토일, 비고, 제출기한을 포함한다.

---

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
- **Evidence:** Evidence Requirement → Evidence → Verification → Inspection의 추적 구조 구현
- **Storage:** V1에서는 실제 파일/Object Storage 연동 없이 `fileName`/`filePath` 기반으로 관리

---

## 4. Git Flow

```text
main ← release ← develop ← feature/*
```

기능은 Feature Branch에서 개발·검증 후 `develop`에 통합한다. Hotfix는 `hotfix/*`에서 수정·검증한다.

### Backend branches

`feature/common`, `feature/security`, `feature/auth-jwt`, `feature/auth-login`, `feature/auth-refresh`, `feature/auth-role`, `feature/swagger`, `feature/project-crud`, `feature/project-search`, `feature/project-dashboard`, `feature/project-detail`, `feature/wbs-management`, `feature/schedule-management`, `feature/issue-management`, `feature/risk-management`, `feature/change-management`, `feature/evidence-management`, `feature/cmdb-management`, `feature/dashboard`, `feature/report-excel`, `feature/report-pdf`, `feature/spring-ai`, `feature/ai-risk-analysis`, `feature/ai-report-summary`

### Frontend branches

`feature/frontend-layout`, `feature/frontend-components`, `feature/frontend-dashboard-hmc-reference`, `feature/frontend-api-integration`, `feature/frontend-project`, `feature/frontend-wbs`, `feature/frontend-schedule`, `feature/frontend-issue`, `feature/frontend-risk`, `feature/frontend-change`, `feature/frontend-evidence`, `feature/frontend-cmdb`, `feature/frontend-report`, `feature/frontend-auth`, `feature/frontend-ai`

---

## 5. Version Strategy

Semantic Versioning `MAJOR.MINOR.PATCH`를 따른다.

- Major: 호환성 파괴 변경
- Minor: 기능 추가
- Patch: 버그 수정/개선

마지막 정식 Release는 **v0.5.3**이며, 이후 통합 기능은 아직 별도 정식 Release로 태그하지 않았다.

---

## 6. Commit Convention

`feat:`, `fix:`, `refactor:`, `style:`, `test:`, `docs:`, `build:`, `chore:`, `perf:`, `security:`를 사용한다.

예:

- `feat: implement project CRUD`
- `feat(frontend): integrate schedule calendar API`
- `refactor(frontend): split schedule calendar into components`

---

# 7. Sprint Plan

## Sprint 0 — Base Project

Spring Boot/Gradle/MariaDB, 패키지 구조, Base Entity, 공통 응답·예외, JPA 및 React/Vite/TypeScript 기반 구축.

**상태: 완료**

---

## Sprint 1 — Authentication & Security

JWT Provider/Filter, SecurityConfig, 사용자 principal, 로그인, refresh token, 역할 기반 인가, password encoder.

Backend 보안 기반 및 인증/인가 구현 완료. Frontend 로그인 UI·토큰 처리·보호 라우트는 후속 검증 대상.

**상태: Backend 완료 / Frontend 후속 검증**

---

## Sprint 2 — Project Management & Frontend Foundation

Project CRUD, 검색/정렬, dashboard KPI API, detail API 및 Controller/Service/Mapper 분리 완료.

Frontend 레이아웃·공통 컴포넌트·대시보드 기반 및 Project CRUD/API 연계 완료.

**상태: 완료**

---

## Sprint 3 — WBS / Schedule Management

### WBS Backend

- CRUD
- tree
- progress calculation
- task relationship
- validation
- delete handling
- Swagger verification

**상태: 완료**

### WBS Frontend

- API client/integration
- tree
- detail
- form
- status
- progress
- delete

**상태: 구현 완료 / Browser·E2E 검증 잔여**

### Schedule Backend

- CRUD
- project 조회
- status/순서 관리
- search/filter
- pagination/sorting
- 기간 검색
- validation
- calendar API
- Swagger verification

**상태: 완료**

### Schedule Frontend

- management UI
- list/search/detail
- create/edit/delete
- status
- API integration
- Calendar UI/API
- month navigation
- today navigation
- range query
- component/utility separation
- production build

**상태: 구현 완료 / Browser·E2E 검증 및 Gantt 연계 잔여**

### WBS / Schedule Remaining

- Browser verification
- integration test
- E2E verification
- Gantt UI
- WBS/Schedule linkage
- timeline/milestone/progress visualization

---

# 8. Sprint 4 — Issue / Risk / Change Management

## Issue

### Backend

- CRUD
- dynamic search/filter
- pagination/sorting
- status workflow
- delete
- Swagger
- project별 Issue Key 생성
- `(project_id, issue_key)` unique constraint

**상태: 완료**

### Frontend

- API client
- list/search
- detail
- create/edit
- status
- delete
- key display
- API integration
- production build

**상태: 구현 완료 / Browser·E2E 검증 잔여**

---

## Risk

### Backend

- CRUD
- search/filter
- status workflow
- score/level calculation
- project별 key 생성
- Swagger

**상태: 완료**

### Frontend

- list/search
- summary/matrix
- detail
- create/edit
- status
- delete
- API integration
- loading/empty/error
- production build

**상태: 구현 완료 / Browser·E2E 검증 잔여**

---

## Change

### Backend

- Change API
- CRUD-related endpoints
- search/filter
- status workflow
- approval API
- validation

**상태: 구현 완료**

### Frontend

- API client
- project-scoped Change list
- search/status/priority filter
- Change Summary
- Change detail
- create/edit
- status workflow
- approval/rejection
- approval opinion
- status/approval API integration
- detail edit
- Project Detail → Change Management navigation
- `/project/:projectId/change` route
- loading/empty/error
- production build

Workflow:

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

**상태: 구현 완료**

### Change Remaining

- Browser verification
- approval/rejection actual API E2E verification
- status transition validation
- history/audit integration
- impact analysis refinement
- backend domain completeness review
- Evidence/Inspection change-impact linkage

---

# 9. Sprint 5 — Evidence & Inspection Management

## Evidence Requirement Backend

- Project별 Evidence Requirement 관리
- WBS 연계
- Requirement Key 생성 (`EVR-001` 등)
- `(project_id, requirement_key)` unique constraint
- CRUD
- Requirement status calculation
- due date 기반 `MISSING` 판정
- required=false → `NOT_REQUIRED`
- evidence 존재 → `PRESENT`
- evidence 미존재 + due date 미경과 → `PENDING`
- evidence 미존재 + due date 경과 → `MISSING`
- Swagger verification

**상태: 완료**

---

## Evidence Backend

- Requirement별 Evidence 조회/등록
- Evidence 상세 조회
- Evidence 수정/삭제
- Project별 Evidence 조회
- WBS별 Evidence 조회
- Evidence Key 생성 (`EVD-001` 등)
- `(project_id, evidence_key)` unique constraint
- Verification API
- current user 기반 verifiedBy
- backend time 기반 verifiedAt
- `APPROVED` / `REJECTED`
- REJECTED remark validation
- Evidence 생성 시 Requirement status refresh
- Evidence 삭제 시 Requirement status refresh
- Inspection Item에서 사용 중인 Evidence 삭제 방지
- Swagger verification

### EvidenceType

```text
OTHER
LOG
SCREENSHOT
TEST_RESULT
IMAGE
REPORT
CONFIGURATION
DOCUMENT
```

**상태: 완료**

---

## Inspection Backend

- Project별 Inspection 조회
- status별 Inspection 조회
- Inspection 생성/수정
- Inspection status workflow
- Inspection Item 생성/수정
- Requirement 연계
- Evidence 연계
- Project consistency validation
- Requirement/Evidence consistency validation
- Inspection Item result workflow
- Swagger verification

Inspection workflow:

```text
PLANNED
    ↓
IN_PROGRESS
    ↓
PASSED / FAILED
    ↓
CLOSED
```

Inspection Item result:

```text
PENDING
    ↓
PASSED / FAILED / NOT_APPLICABLE
```

**상태: 완료**

---

## Evidence & Inspection API Verification

실제 Swagger 기준 E2E 검증을 완료했다.

Test context:

```text
Project ID      : 2
WBS ID          : 1
Requirement     : EVR-001
Evidence        : EVD-001
Inspection      : 1
Inspection Item : 1
```

검증 시나리오:

1. 기존 Evidence 삭제
2. Evidence 신규 등록
3. Requirement 상태 자동 갱신 확인
4. Project Evidence 조회
5. Requirement 조회
6. Evidence Verification → `APPROVED`
7. Inspection 생성 → `PLANNED`
8. Inspection Item 생성
9. Inspection → `IN_PROGRESS`
10. Inspection Item → `PASSED`
11. Inspection → `PASSED`
12. Inspection → `CLOSED`

핵심 검증 결과:

```text
Evidence 생성
    ↓
Requirement = PRESENT
    ↓
Verification = APPROVED
    ↓
Inspection = PLANNED
    ↓
Inspection = IN_PROGRESS
    ↓
Inspection Item = PASSED
    ↓
Inspection = PASSED
    ↓
Inspection = CLOSED
```

**상태: Backend + API Verification 완료**

---

## Evidence Summary

현재 `EvidenceSummaryResponse` 기준:

- totalRequirements
- requiredRequirements
- presentRequirements
- missingRequirements
- pendingRequirements
- notRequiredRequirements
- verificationPending
- verificationApproved
- verificationRejected
- overdueRequirements
- evidenceCompletionRate
- verificationApprovalRate

**상태: Backend contract 기준 정리 완료**

---

## Evidence Documentation

Evidence & Inspection Frontend ↔ Backend API Contract 문서를 실제 Swagger 검증 결과와 동기화했다.

주요 반영 내용:

- 실제 EvidenceType enum
- 실제 Requirement/Verification/Inspection 상태값
- 실제 API endpoint
- 실제 Evidence/Inspection workflow
- Requirement status refresh 정책
- Verification 처리 규칙
- Inspection Item 연계
- 실제 ErrorCode
- Evidence Summary DTO
- Frontend 구현 순서
- Backend/Frontend 책임 분리

문서:

```text
docs/design/evidence/EVIDENCE_FRONTEND_BACKEND_CONTRACT.md
```

**상태: 완료**

---

## Evidence Backend Git Integration

- Integration branch: `develop`
- Commit: `327ee15`
- Commit message: `feat: complete evidence inspection workflow`
- Push: `origin/develop`
- Push result: `667bd3c..327ee15 develop -> develop`
- Changed files: 2
- Commit delta: `1352 insertions(+), 682 deletions(-)`

**상태: develop 반영 완료**

> 현재 Evidence 구현은 별도 feature branch의 merge 기록이 아니라 `develop`에서 직접 커밋되어 `origin/develop`에 반영된 상태다.

---

## Evidence Frontend — Next Development

Backend 및 API verification이 완료되었으므로 다음 개발 대상은 Frontend다.

예정 branch:

```text
feature/frontend-evidence
```

권장 구현 순서:

```text
1. TypeScript types
2. API client
3. Evidence Page
4. Requirement Summary
5. Requirement Toolbar
6. Requirement List/Row
7. Requirement Detail
8. Requirement Dialog
9. Evidence List
10. Evidence Detail
11. Evidence Dialog
12. Verification Dialog
13. Inspection List
14. Inspection Detail
15. Status / workflow UI
16. Loading / Empty / Error
17. Project Detail navigation
18. Route integration
19. Production build
20. Browser verification
21. API E2E verification
```

Frontend 주요 구조:

```text
pmis-frontend/src/features/evidence/

├── pages/
│   └── EvidencePage.tsx
│
├── components/
│   ├── EvidenceRequirementSummary.tsx
│   ├── EvidenceRequirementToolbar.tsx
│   ├── EvidenceRequirementList.tsx
│   ├── EvidenceRequirementRow.tsx
│   ├── EvidenceRequirementDetail.tsx
│   ├── RequirementDialog.tsx
│   ├── EvidenceList.tsx
│   ├── EvidenceDetail.tsx
│   ├── EvidenceDialog.tsx
│   ├── VerificationDialog.tsx
│   ├── InspectionList.tsx
│   └── InspectionDetail.tsx
│
├── api/
│   ├── evidenceRequirementApi.ts
│   ├── evidenceApi.ts
│   └── inspectionApi.ts
│
├── types/
│   ├── evidenceRequirement.ts
│   ├── evidence.ts
│   └── inspection.ts
│
└── utils/
    └── evidenceStatus.ts
```

---

# 10. Milestones

| ID | Milestone | Status |
|---|---|---|
| M1 | Base Project Complete | 완료 |
| M2 | Authentication Complete | 완료 |
| M3 | Project Management Backend Complete | 완료 |
| M4 | Frontend Foundation Complete | 완료 |
| M5 | Project Frontend CRUD Complete | 완료 |
| M6 | WBS & Schedule Backend Complete | 완료 |
| M7 | WBS & Schedule Frontend Complete | 구현 완료 / 검증 잔여 |
| M8 | Issue / Risk / Change Complete | 구현 완료 / 검증 잔여 |
| M9 | Evidence & Inspection Complete | **Backend/API 완료, Frontend 진행 예정** |
| M10 | CMDB Complete | 예정 |
| M11 | Dashboard & Report Complete | 예정 |
| M12 | Spring AI Complete | 예정 |
| M13 | Production Ready | 예정 |
| M14 | Portfolio Release | 예정 |

---

# 11. Estimated Schedule

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

현재 개발은 Sprint 5의 Evidence Frontend 단계로 진입한다.

---

# 12. Quality Objectives

## Backend

- Layered/Clean Architecture
- SOLID
- REST
- DTO/Mapper/Specification
- 도메인별 Controller/Service 분리
- Audit/History
- Evidence Traceability
- 상태 전이 검증
- Project/WBS 도메인 무결성 검증

## Frontend

- Component/Feature-based Architecture
- 재사용 컴포넌트
- API/service 분리
- 상태 관리
- TypeScript 안전성
- 반응형 UI
- 공통 유틸
- Loading/Empty/Error 처리
- Browser verification
- Production build

## Integration

- API 계약
- 오류 처리
- 인증 연계
- Loading/Empty/Error
- Browser verification
- Production build
- E2E verification
- Documentation synchronization

---

# 13. Documentation

Project docs:

- `PROJECT_OVERVIEW.md`
- `DEVELOPMENT_ROADMAP.md`
- `ARCHITECTURE.md`
- `CHANGELOG.md`
- `PORTFOLIO.md`

Design docs:

```text
docs/design/
├── issue/
├── project/
├── project-dashboard/
├── report/
├── schedule/
├── server-configuration/
├── wbs/
└── evidence/
```

Evidence 주요 문서:

```text
docs/design/evidence/EVIDENCE_FRONTEND_BACKEND_CONTRACT.md
```

문서와 소스의 실제 상태를 일치시킨다.

---

# 14. Final Deliverables

## Backend

Spring Boot REST API, JWT, MariaDB, Project/WBS/Schedule/Issue/Risk/Change/Evidence/Inspection/CMDB, Dashboard API, Report, Spring AI, Swagger

## Frontend

React/TypeScript/Vite, Layout/Dashboard, Project/WBS/Schedule, Calendar/Gantt, Issue/Risk/Change, Evidence, CMDB, Report, AI Assistant

## Delivery

Docker/Compose, GitHub Actions, GitHub Repository, Portfolio Documentation

---

# 15. Success Criteria

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

---

# 16. Current Project Status — 2026-10-08

## Backend completed

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
- Evidence Requirement
- Evidence
- Evidence Verification
- Inspection
- Inspection Item
- Evidence Summary contract
- Evidence/Inspection Swagger E2E verification

## Frontend completed

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

## Evidence / Inspection

### Backend

- Evidence Requirement ✓
- Evidence ✓
- Verification ✓
- Inspection ✓
- Inspection Item ✓
- Summary contract ✓
- Swagger E2E ✓
- develop integration ✓

### Frontend

- TypeScript types — 예정
- API client — 예정
- Evidence Page — 예정
- Requirement UI — 예정
- Evidence UI — 예정
- Verification UI — 예정
- Inspection UI — 예정
- Project navigation/route — 예정
- Production build — 예정
- Browser verification — 예정
- E2E verification — 예정

---

# 17. WBS State

```text
Backend:

API / CRUD / validation / delete handling / tree ✓

Frontend:

API client / integration / tree / detail / form / status / delete ✓

Remaining:

browser verification → integration test → E2E verification
```

---

# 18. Schedule State

```text
Backend:

CRUD / search / pagination / sorting / period search / validation /
calendar API / Swagger ✓

Frontend:

management / list / detail / forms / status / API / calendar /
refactoring / build ✓

Remaining:

browser verification → integration test
→ Gantt and WBS linkage
→ timeline / milestone / progress visualization
```

---

# 19. Issue State

```text
Backend:

entity / DTO / specification / CRUD / workflow /
key generation / uniqueness / Swagger ✓

Frontend:

API client / list / search / detail / create / edit / status /
delete / integration ✓

Remaining:

browser verification → E2E verification
```

---

# 20. Risk State

```text
Backend:

CRUD / search / workflow / score-level / key generation / Swagger ✓

Frontend:

list / search / summary / matrix / detail / forms / status /
delete / integration / build ✓

Remaining:

browser verification → integration and E2E verification
```

---

# 21. Change State

```text
Backend:

Change API / CRUD-related endpoints / search / status workflow /
approval API integration ✓

Frontend:

API client / project-scoped route / list / search / filter /
summary / detail / create / edit / status / approval / rejection /
workflow UI / API integration / build ✓

Remaining:

browser verification
→ approval/rejection E2E
→ history/audit
→ impact analysis refinement
→ Evidence linkage
```

---

# 22. Evidence State

```text
Backend:

Evidence Requirement / CRUD / key generation / unique constraint /
status calculation / Evidence CRUD / Verification /
Inspection / Inspection Item / validation / Summary contract ✓

API Verification:

Swagger E2E ✓

Documentation:

Frontend ↔ Backend API Contract ✓

Git:

develop commit 327ee15 ✓
origin/develop push ✓

Frontend:

API types → API client → Page → Requirement UI → Evidence UI
→ Verification → Inspection → Route → Build
→ Browser verification → E2E

Current:

Frontend implementation pending / next development target
```

---

# 23. Git Integration Record

## Common Search Pagination Standardization

- Feature branch: `feature/common-search-filter`
- Commit: `1f7f011` — `feat: standardize WBS search pagination`
- Scope: search contract documentation, shared `SearchPageableFactory`, and service pagination standardization for Change/Issue/Risk/Schedule/WBS
- Merged fast-forward into `develop`
- Pushed to `origin/develop`

## Change Management Workflow UI

- Feature branch: `feature/change-management-ui`
- Commit: `f3ed60c` — `feat: complete change management workflow ui`
- Scope: Change detail/edit workflow, status workflow, approval/rejection workflow UI, API integration, project-scoped navigation/routing
- Merged fast-forward into `develop`
- `develop` pushed to `origin/develop`

## Evidence & Inspection Workflow

- Working branch at commit time: `develop`
- Commit: `327ee15`
- Commit message: `feat: complete evidence inspection workflow`
- Scope: Evidence Requirement, Evidence, Verification, Inspection, Inspection Item workflow and contract/documentation synchronization
- Integration branch: `develop`
- Push result: `667bd3c..327ee15 develop -> develop`
- Changed files: 2
- Commit delta: `1352 insertions(+), 682 deletions(-)`

**Current integrated commit: `327ee15`**

---

# 24. Development Process & Maintenance

```text
Planning
  → Feature Branch
  → Development
  → Local Test
  → Production Build
  → API Verification
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

Evidence처럼 Backend/API 검증이 완료된 도메인은 Frontend 구현 → Browser verification → E2E verification 순으로 진행한다.

---

# 25. Current Information

| Item | Value |
|---|---|
| Last Updated | 2026-10-08 |
| Roadmap Version | v1.15 |
| Current Release | v0.5.3 |
| Integration Branch | `develop` |
| Current Sprint | Sprint 5 — Evidence & Inspection Management |
| Last completed backend domain | Evidence & Inspection |
| Last completed frontend domain | Change Management UI/API Workflow |
| Last integrated commit | `327ee15` |
| Latest integrated feature | Evidence & Inspection workflow |
| Evidence Backend | 완료 |
| Evidence API Verification | 완료 |
| Evidence Documentation | 완료 |
| Evidence Frontend | 다음 개발 대상 |
| Next stage | Evidence Frontend → Browser/E2E → CMDB |
| Maintainer | Seo Seokhyeon |

---

# 26. Next Milestone

1. Evidence Frontend TypeScript types
2. Evidence API client
3. Evidence Requirement UI
4. Evidence UI
5. Verification UI
6. Inspection UI
7. Project navigation / routing
8. Production build
9. Evidence Browser verification
10. Evidence API E2E verification
11. WBS/Schedule/Issue/Risk Browser/E2E verification
12. Change Browser/E2E verification
13. Change history/audit
14. Gantt UI and WBS/Schedule linkage
15. CMDB
16. Dashboard & Report
17. Spring AI
18. Finalization and portfolio release

---

# End of Document
