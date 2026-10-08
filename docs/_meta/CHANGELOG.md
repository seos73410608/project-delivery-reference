# CHANGELOG

> PMIS (Project Management Information System) 변경 이력 관리 문서
> 프로젝트 진행 과정에서 실제 개발이 완료되고 `develop` 브랜치에 통합된 주요 변경 사항과 Release 이력을 기록한다.

---

# 변경 이력 관리 원칙

본 문서는 PMIS 프로젝트의 주요 변경 사항과 Release 이력을 관리한다.

## 기록 대상

* Feature
* Improvement
* Bug Fix
* Refactoring
* Security
* Performance
* Documentation
* Build

CHANGELOG에는 **실제 개발이 완료되고 `develop` 브랜치에 통합된 주요 변경 사항**을 기록한다.

개발 예정 사항과 향후 구현 계획은 `DEVELOPMENT_ROADMAP.md`에서 관리한다.

`Unreleased`는 현재 `develop` 브랜치에 통합되었지만 아직 정식 Release Tag가 생성되지 않은 변경 사항을 관리한다.

작업 중이며 아직 `develop`에 통합되지 않은 항목은 Unreleased의 완료 항목으로 기록하지 않는다.

---

# Versioning

PMIS는 Semantic Versioning(SemVer)을 따른다.

```text
MAJOR.MINOR.PATCH
```

* **Major**: 기존 API 또는 시스템과 호환되지 않는 변경
* **Minor**: 기존 기능과 호환되는 기능 추가
* **Patch**: 기존 기능과 호환되는 버그 수정

초기 개발 단계에서는 `0.x.x` 버전을 사용한다.

현재 마지막 정식 Release Tag는 `v0.5.3`이며, 이후 기능은 `Unreleased` 상태로 관리한다.

---

# Release History

## v0.1.0 (2026-08-03)

### Project Initialization

#### Added

* Spring Boot 프로젝트 생성
* Java 21 적용
* Gradle Kotlin DSL 적용
* MariaDB 연동
* Git Repository 구성
* Git Flow 브랜치 전략 적용
* 기본 프로젝트 구조 생성

---

## v0.2.0 (2026-08-03)

### Common Infrastructure

#### Added

* Domain 기반 Package 구조
* `BaseEntity`
* `ApiResponse`
* `ErrorCode`
* `CommonErrorCode`
* `BusinessException`
* `GlobalExceptionHandler`
* `JpaConfig`

#### Changed

* API Response 표준화
* Global Exception 구조 적용
* JPA Auditing 기반 생성/수정 시간 관리 적용

---

## v0.3.0 (2026-08-04)

### Security Foundation

#### Added

* Spring Security `SecurityConfig`
* BCrypt PasswordEncoder
* Stateless Authentication
* CORS Configuration
* JWT `JwtProvider`
* `JwtAuthenticationFilter`
* `JwtAuthenticationEntryPoint`
* `JwtAccessDeniedHandler`
* JWT Properties
* Test API

  * `GET /api/test/public`
  * `GET /api/test/private`
  * `GET /api/test/token`

#### Changed

* Session 기반 인증 제거
* JWT 기반 Stateless Authentication 적용

#### Fixed

* Spring Boot / JWT 라이브러리 호환성 문제 수정
* Security Handler 개선
* Security Configuration 오류 수정

---

## v0.3.1 (2026-08-05)

### Authentication & Authorization

#### Added

* Login API 및 Authentication Service
* Refresh Token API 및 Token Reissue
* Access Token / Refresh Token 검증
* Role Hierarchy
* `USER`, `PM`, `ADMIN` Role
* Role Test API

  * `GET /api/role/authenticated`
  * `GET /api/role/user`
  * `GET /api/role/pm`
  * `GET /api/role/admin`
* Authentication API

  * `POST /api/auth/login`
  * `POST /api/auth/refresh`

#### Changed

* Authentication Flow 및 Authorization 처리 개선
* Role 기반 접근 제어 적용

#### Fixed

* JWT Validation 문제 수정
* Security Exception 처리 개선

---

## v0.3.2 (2026-08-06)

### API Documentation

#### Added

* OpenAPI 3
* Swagger UI
* JWT Authorization
* API Documentation

#### Changed

* API 문서 자동화
* JWT 인증 API 테스트 환경 개선

---

## v0.4.0 (2026-08-06)

### Project Management Backend

#### Added

* Project Entity / Repository / Service / Controller
* Project CRUD API

  * `POST /api/projects`
  * `GET /api/projects`
  * `GET /api/projects/{projectId}`
  * `PUT /api/projects/{projectId}`
  * `DELETE /api/projects/{projectId}`
* Project Search 및 Sorting
* Sorting Column Validation
* Project Dashboard API
* Status / Priority / Recent Project / Upcoming Deadline KPI

#### Changed

* Project Domain 구조 및 Service Layer 개선
* Validation 구조 개선
* 검색 API 정렬 컬럼 검증 적용

#### Fixed

* Project Validation 오류 수정
* Search 조건 처리 개선

---

## v0.5.0 (2026-08-07)

### Frontend Foundation

#### Added

* React
* Vite
* TypeScript
* npm
* React Router DOM
* Backend / Frontend 프로젝트 분리
* Feature 기반 구조
* Layout 기반 화면 구성
* Frontend Path Alias
* `AppRouter`
* `MainLayout`
* `DashboardPage`
* PMIS Dashboard Skeleton
* Development Status 화면

#### Changed

* 기존 Vite / React 예제 및 샘플 자산 제거
* Vite / TypeScript Path Resolution 구성
* Frontend Path Alias 구성

#### Removed

* Vite Example Application
* Sample Assets
* Counter Example

---

## v0.5.1 (2026-08-08)

### Frontend Layout & Common UI Components

#### Added

* Header
* Sidebar
* Breadcrumb
* Button
* Card
* Loading
* EmptyState
* MainLayout 내 Layout / Common UI Component 분리
* Router Path 기반 Breadcrumb

#### Changed

* MainLayout 직접 구현 영역을 독립 Component로 분리
* Layout Component와 Common UI Component 분리
* `@/components/...` Alias 기반 Import 구조 적용
* Vite Path Resolution 설정 정리

#### Documentation

* `006_frontend_development_history.md` 현행화
* Frontend Layout 및 Common UI Component 개발 이력 추가

---

## v0.5.2 (2026-08-10)

### Project Detail API & Controller Separation

#### Added

* `ProjectDetailController`
* `ProjectDetailService`
* `ProjectDetailResponse`
* `GET /api/projects/{projectId}/detail`

#### Changed

* Project CRUD / Dashboard / Detail Controller 책임 분리
* Project CRUD / Dashboard / Detail Service 책임 분리
* 단건 조회와 상세 조회 책임 분리
* 상세 조회 전용 Response DTO 구성

#### Refactoring

* Controller 및 Service 단일 책임 구조 개선
* Project Domain API 구조 정리
* WBS / Schedule / Issue / Risk / Change 연계 기반 마련

#### Documentation

* Project Detail API Swagger 문서화
* Project Domain API 구조 문서화

---

## v0.5.3 (2026-08-11)

### Frontend Dashboard HMC Reference

#### Added

* Power HMC (Hardware Management Console) 스타일을 참고한 PMIS Dashboard
* Project Overview
* KPI
* WBS Progress
* Schedule Summary
* Issue Summary
* Recent Activity
* `KpiCard`
* `ProjectOverview`
* `WbsProgress`
* `ScheduleSummary`
* `IssueSummary`
* `RecentActivity`

#### Changed

* Dashboard Skeleton을 운영 현황 중심의 Dashboard UI로 확장
* DashboardPage를 Component 단위로 분리
* Dashboard Component Import 구조 정리

#### Build

* TypeScript Build 오류 수정
* TypeScript 6.x `baseUrl` deprecated 대응
* `ignoreDeprecations` 설정
* `npm run build` 성공
* Vite Production Build 성공

#### Git

* Feature Branch: `feature/frontend-dashboard-hmc-reference`
* Feature Branch Push
* `develop` Merge 및 Push 완료

#### Note

* Dashboard 데이터는 Reference UI 구현을 위한 정적 데이터
* 실제 Project / WBS / Schedule / Issue API 연동은 후속 작업으로 분리

---

# Unreleased

> `develop` 브랜치에 통합되었으며 아직 정식 Release Tag가 생성되지 않은 변경 사항.

---

## Project Frontend CRUD

### Added

* Project API Client
* Project List
* Project Search
* Project Detail
* Project Registration
* Project Edit
* Project Delete UI
* Project CRUD API Integration
* Loading / Empty / Error State
* Delete Confirmation
* API Error Handling
* CRUD 결과 UI 반영

### Completed

* Project List / Search / Detail
* Create / Edit / Delete
* Project API Integration
* Loading / Empty / Error State
* Browser Verification
* Production Build

### Git

* Commit: `8d7df6d` — `feat(frontend): complete project CRUD`
* Project Frontend Feature → `develop` 통합 완료

---

## WBS Management Backend & Frontend Integration

### Added

* WBS CRUD
* WBS Tree Structure
* WBS 단건 조회
* WBS Validation
* Parent / Child 관계 처리
* WBS Tree / Search / Detail / Create / Update / Delete API
* Frontend API Client
* `apiClient`
* `getWbsTree`
* `searchWbs`
* `getWbs`
* `createWbs`
* `updateWbs`
* `deleteWbs`
* `WbsTree`
* `WbsTreeNode`
* `WbsDetail`
* `WbsForm`
* `WbsStatusForm`
* WBS Tree / Search / Detail / Create / Edit / Status Change / Delete UI

### Changed

* WBS UI를 실제 Backend API 기반으로 전환
* 생성 / 수정 / 상태 변경 / 삭제 후 최신 Tree 데이터 반영
* 삭제 성공 후 선택 상태 초기화
* Parent WBS 관계를 Tree 기반으로 처리

### Validation & UX

* 동일 상태 변경 방지
* 하위 WBS 존재 시 삭제 제한
* 삭제 조건 확인
* Loading / Empty / Error State
* Delete Confirmation
* Tree Refresh

### Verification

* WBS CRUD API Integration 검증
* Status Change 검증
* Delete 검증
* Browser 기반 기능 검증

### Git

* `75e9d5b` — `feat(frontend): implement WBS status change`
* `d468f70` — `feat(frontend): implement WBS delete`
* Feature: `feature/frontend-wbs` → `develop`

---

## Schedule Management Backend & Frontend Integration

### Added

* Schedule Entity
* Repository
* Service
* Controller
* DTO
* Validation
* Schedule CRUD
* Status / Sort Order 관리
* Project / WBS / Keyword / Status / 기간 검색
* Pagination
* Dynamic Sorting
* Schedule Frontend API Client
* Type Definition
* Schedule List / Search / Detail / Create / Edit / Delete / Status UI
* Loading / Empty / Error State

### Changed

* Schedule UI를 실제 Backend API와 연계
* Create / Update 이후 최신 데이터 반영
* Delete 이후 목록 재조회
* Frontend Search 조건과 Backend Query Parameter 연동

### Verification

* Swagger / OpenAPI CRUD 검증
* Search 검증
* Pagination 검증
* Sorting 검증
* Period Search 검증
* Validation 검증
* Not Found 검증
* Frontend CRUD / Search / Detail 검증
* Browser UI 검증
* Production Build 검증
* Backend / Frontend Integration `develop` 통합 완료

### Git

* Backend Feature: `feature/schedule-management`
* Frontend Feature: `feature/frontend-schedule`

---

## Common Search Filter & Pagination Standardization

### Added

* 공통 검색 조건 및 Pageable 생성 규약 문서화
* `docs/_meta/SEARCH_FILTER_DESIGN.md`
* `SearchPageableFactory` 공통 Factory 구현
* Issue / Risk / Change / Schedule / WBS 검색 API 공통 Pagination / Sorting 처리

### Changed

* 각 Domain에 분산되어 있던 Pagination 및 Sorting 처리 로직을 공통 Factory 기반으로 정리
* WBS 검색 Service의 로컬 페이지 파싱 및 정렬 처리 제거
* 허용 정렬 필드와 검색 정렬 조건 검증 구조 통일

### Documentation

* 공통 Search Filter 계약 문서 추가

### Git

* `d744bfb` — `docs: define common search filter contract`
* `b3450e7` — `feat: implement common search pageable factory`
* `b878017` — `feat: standardize issue search pagination`
* `64c76ca` — `feat: standardize risk search pagination`
* `04cf172` — `feat: standardize schedule search pagination`
* `7c5328f` — `feat: standardize change search pagination`
* `1f7f011` — `feat: standardize WBS search pagination`
* Feature: `feature/common-search-filter` → `develop` 병합 및 Push 완료

---

## Issue Management Backend & Frontend Integration

### Added

* Issue Domain Backend
* Issue CRUD
* Issue Search / Filter
* Issue Status Workflow
* Project-scoped Issue Key
* `(project_id, issue_key)` Unique Constraint
* Issue Frontend API Client
* Issue List / Search / Detail
* Issue Create / Edit
* Issue Status Change
* Issue Delete
* Issue Key Display

### Changed

* Issue UI를 실제 Backend API와 연계
* Project-scoped Issue 관리 구조 적용
* Backend 생성 Issue Key를 Frontend에서 표시

### Verification

* Swagger API 검증
* Frontend API Integration
* Production Build

### Remaining

* Browser Verification
* E2E Verification

---

## Risk Management Backend & Frontend Integration

### Added

* Risk CRUD
* Risk Search / Filter
* Risk Status Workflow
* Probability / Impact 기반 Score
* Risk Level 계산
* Project-scoped Risk Key
* Risk Summary
* Risk Matrix
* Risk Frontend List / Search / Detail
* Risk Create / Edit
* Risk Status Change
* Risk Delete
* Loading / Empty / Error State

### Changed

* Risk Backend / Frontend API Integration
* Risk Score / Level 기반 UI 구성
* Risk Matrix 기반 시각화

### Verification

* Backend API 검증
* Frontend API Integration
* Production Build

### Remaining

* Browser Verification
* E2E Verification

---

## Change Management Workflow UI & API Integration

### Added

* Change API Client
* 프로젝트별 Change 목록 조회
* Change Search / Status / Priority Filter
* Change Summary
* Change Detail UI
* Change Create / Edit UI
* Change Status Workflow UI
* Change Approval / Rejection UI
* Approval Comment 입력
* Status / Approval API Integration
* Project Detail → Change Management Navigation
* `/project/:projectId/change` Route
* Loading / Empty / Error State
* Change Detail 수정 버튼

### Workflow

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

### Verification

* Change Frontend Production Build 성공
* Change API Client 및 Workflow API 호출 구조 확인
* `develop` 통합 완료

### Remaining

* Browser Verification
* Approval / Rejection E2E
* Status Transition Verification
* History / Audit
* Impact Analysis Refinement
* Evidence 연계

### Git

* Feature Branch: `feature/change-management-ui`
* Commit: `f3ed60c` — `feat: complete change management workflow ui`
* Integration: `develop` fast-forward
* Integration range: `bfb2a33..f3ed60c`
* `origin/develop` Push 완료

---

## Project Navigation / Sidebar Change

### Changed

* Change Management를 Global Sidebar에서 제거
* Project-scoped Change Management 진입 구조로 변경
* Project Detail에서 Change Management로 이동하는 구조 적용

### Git

* Commit: `41a203a` — `fix: remove change management from sidebar`

---

# Evidence & Inspection Management

> 2026-10-08 기준 `develop`에 통합된 Evidence & Inspection Backend Workflow 구현 및 Swagger E2E 검증.

## Evidence Requirement

### Added

* Evidence Requirement Domain
* Requirement CRUD
* Project-scoped Requirement 조회
* WBS 연계
* Requirement Key 생성
* Requirement Type 관리
* Required 여부 관리
* Due Date 관리
* Requirement Status 관리
* Project별 `requirementKey` Unique Constraint

### Requirement Status

```text
required = false
    ↓
NOT_REQUIRED

required = true
    ↓
Evidence 존재 ─────────→ PRESENT
    │
    └─ Evidence 없음
          │
          ├─ dueDate 경과 → MISSING
          │
          └─ 그 외 → PENDING
```

### Status Refresh

Evidence Requirement 상태는 Evidence 등록/삭제 시 자동 갱신된다.

```text
PENDING
   ↓ Evidence 등록
PRESENT

PRESENT
   ↓ Evidence 삭제
PENDING / MISSING
```

`dueDate`가 오늘인 경우에는 `PENDING`으로 유지하고, `dueDate`가 오늘보다 이전인 경우 `MISSING`으로 처리한다.

---

## Evidence Management

### Added

* Evidence Domain
* Evidence CRUD
* Requirement별 Evidence 조회
* Project별 Evidence 조회
* WBS별 Evidence 조회
* Evidence Key 생성
* Evidence Type
* 제출자 / 제출일
* Verification Status
* 검증자 / 검증일
* Verification Remark
* Project별 `evidenceKey` Unique Constraint

### Evidence Type

현재 Backend Enum:

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

### Verification Workflow

```text
PENDING
   ↓
APPROVED
   또는
REJECTED
```

### Verification Rules

* `APPROVED` / `REJECTED`만 최종 검증 상태로 허용
* `PENDING`으로의 Verification API 변경은 허용하지 않음
* `REJECTED`인 경우 Remark 필수
* 검증자는 현재 로그인 사용자
* 검증일시는 Backend에서 생성
* Frontend에서 검증자 및 검증일을 지정하지 않음

### Requirement Integration

Evidence 등록 이후 해당 Requirement의 상태를 자동 갱신한다.

Evidence 삭제 이후에도 해당 Requirement 상태를 다시 계산한다.

---

## Inspection Management

### Added

* Inspection Domain
* Project별 Inspection 조회
* Inspection 생성 / 수정
* Inspection Status Workflow
* Inspection Item 생성 / 수정
* Requirement ↔ Evidence ↔ Inspection Item 연계
* Inspection Result 관리
* Inspection Result Transition

### Inspection Status

```text
PLANNED
   ↓
IN_PROGRESS
   ↓
PASSED / FAILED
   ↓
CLOSED
```

허용된 주요 상태 전이:

```text
PLANNED → IN_PROGRESS

IN_PROGRESS → PASSED
IN_PROGRESS → FAILED

PASSED → CLOSED
FAILED → CLOSED
```

`CLOSED`는 Terminal State로 관리한다.

### Inspection Item Result

```text
PENDING
   ↓
PASSED
FAILED
NOT_APPLICABLE
```

Terminal Result 이후 `PENDING`으로 Reset할 수 있도록 구현되어 있다.

### Integrity Validation

Inspection Item 생성/수정 시 다음 관계를 검증한다.

* Inspection과 Requirement가 동일 Project에 속해야 함
* Evidence가 존재하는 경우 동일 Project여야 함
* Evidence가 해당 Requirement에 속해야 함

---

## Evidence & Inspection API Verification

### Verified Test Scenario

```text
Project
  └─ Project ID: 2

WBS
  └─ WBS ID: 1

Evidence Requirement
  └─ EVR-001

Evidence
  └─ EVD-001

Inspection
  └─ Inspection ID: 1
```

### E2E Workflow

```text
Evidence Requirement
        ↓
Evidence Registration
        ↓
Requirement Status = PRESENT
        ↓
Evidence Verification = APPROVED
        ↓
Inspection Creation
        ↓
Inspection Item Registration
        ↓
Inspection = IN_PROGRESS
        ↓
Inspection Item = PASSED
        ↓
Inspection = PASSED
        ↓
Inspection = CLOSED
```

### Swagger Verification Result

#### 1. Evidence 생성

* `POST /api/evidence-requirements/{requirementId}/evidence`
* Evidence Key `EVD-001` 생성
* Submitted By 자동 지정
* Verification Status `PENDING`

#### 2. Requirement 상태 자동 갱신

Evidence 생성 이후:

```text
EVR-001
PENDING → PRESENT
```

#### 3. Evidence Verification

* `PATCH /api/evidence/{id}/verification`
* `PENDING → APPROVED`
* `verifiedBy` 자동 지정
* `verifiedAt` Backend 생성
* Verification Remark 저장

#### 4. Inspection 생성

* `POST /api/projects/{projectId}/inspections`
* Inspection `PLANNED` 생성

#### 5. Inspection Item 생성

* `POST /api/inspections/{inspectionId}/items`
* Requirement와 Evidence 연계
* 초기 Result `PENDING`

#### 6. Inspection 시작

* `PATCH /api/inspections/{id}/status`
* `PLANNED → IN_PROGRESS`

#### 7. Inspection Item 결과

* `PATCH /api/inspection-items/{itemId}/result`
* `PENDING → PASSED`

#### 8. Inspection 완료

* `PATCH /api/inspections/{id}/status`
* `IN_PROGRESS → PASSED`

#### 9. Inspection 종료

* `PATCH /api/inspections/{id}/status`
* `PASSED → CLOSED`

### Verification Result

```text
Evidence Registration       ✓
Requirement Status Refresh  ✓
Evidence Verification       ✓
Inspection Creation         ✓
Inspection Item Creation    ✓
Inspection Start            ✓
Inspection Item PASS        ✓
Inspection PASS             ✓
Inspection CLOSE            ✓
```

---

## Evidence & Inspection Backend Git Integration

### Git

* Branch: `feature/evidence-management`
* Commit: `327ee15`
* Commit Message: `feat: complete evidence inspection workflow`
* Changed Files: 2
* Commit Statistics:

  * 1,352 insertions
  * 682 deletions

### Integration

* `develop`에 직접 통합
* `origin/develop` Push 완료
* Previous integrated commit: `667bd3c`
* Current integrated commit: `327ee15`

### Build

Backend Build:

```text
BUILD SUCCESSFUL
```

### Documentation

Evidence Frontend ↔ Backend API Contract 문서가 Swagger 실제 검증 결과를 기준으로 현행화되었다.

* `docs/design/evidence/EVIDENCE_FRONTEND_BACKEND_CONTRACT.md`

---

# Current Development Status

## Backend

### Completed

* Foundation
* Common Infrastructure
* JPA Auditing
* Security
* JWT Authentication
* Login
* Refresh Token
* Role Hierarchy / Authorization
* Swagger / OpenAPI
* Project CRUD
* Project Search / Sorting
* Project Dashboard API
* Project Detail API
* WBS CRUD / Tree / Validation / Delete Handling
* Schedule CRUD / Search / Pagination / Sorting / Period Search / Calendar API
* Issue CRUD / Search / Workflow / Project-scoped Key / Unique Constraint
* Risk CRUD / Search / Workflow / Score / Level / Project-scoped Key
* Common `SearchPageableFactory`
* Change API / Search / Status Workflow / Approval Foundation
* Evidence Requirement
* Evidence CRUD / Verification
* Evidence Requirement Status Refresh
* Inspection
* Inspection Item
* Evidence / Inspection Workflow
* Swagger E2E Verification

## Frontend

### Completed

* React / Vite / TypeScript / Router
* Feature-based Structure
* Path Alias
* MainLayout
* Header / Sidebar / Breadcrumb
* Common UI
* Dashboard Reference UI
* Project CRUD / API Integration
* WBS UI / API Integration
* Schedule UI / API Integration
* Issue UI / API Integration
* Risk UI / API Integration
* Change UI / API / Workflow Integration

### Remaining

* WBS / Schedule / Issue / Risk Browser / E2E Verification
* Change Browser / Approval-Rejection E2E Verification
* Change History / Audit
* Calendar / Timeline / Milestone / Gantt
* WBS / Schedule Relationship Visualization
* Dashboard 실제 API / Data Integration
* Authentication UI
* Evidence / Inspection Frontend
* CMDB Frontend
* Report Frontend
* AI Assistant UI

---

# Integration Status

```text
Project
├─ Backend                         ✓
└─ Frontend / API Integration     ✓

WBS
├─ Backend                         ✓
└─ Frontend / API Integration     ✓

Schedule
├─ Backend                         ✓
└─ Frontend / API Integration     ✓

Issue
├─ Backend                         ✓
└─ Frontend / API Integration     ✓

Risk
├─ Backend                         ✓
└─ Frontend / API Integration     ✓

Change
├─ Backend                         ✓
└─ Frontend / API Workflow         ✓
   └─ Browser / E2E                →

Evidence
├─ Backend                         ✓
├─ Verification                    ✓
└─ Inspection                      ✓

Evidence Frontend
└─ UI / API Integration             →

CMDB
└─ →

Dashboard / Report
└─ →

Spring AI
└─ →
```

---

# Current Development Snapshot

```text
Backend

├─ Common Infrastructure / SearchPageableFactory  ✓
├─ Security / JWT / Auth                           ✓
├─ Project CRUD / Search / Dashboard / Detail      ✓
├─ WBS Management                                  ✓
├─ Schedule Management                             ✓
├─ Issue Management                                ✓
├─ Risk Management                                 ✓
├─ Change API / Workflow / Approval                ✓
├─ Evidence Requirement                            ✓
├─ Evidence Management / Verification              ✓
├─ Inspection / Inspection Item                    ✓
├─ Evidence E2E Workflow                            ✓
├─ Change History / Audit                           →
├─ Change Impact Analysis                           →
├─ CMDB                                             →
├─ Dashboard / Report                               →
└─ Spring AI                                        →

Frontend

├─ React / Vite / TypeScript / Layout / Common UI  ✓
├─ Dashboard Reference UI                           ✓
├─ Project CRUD / API Integration                   ✓
├─ WBS UI / API Integration                         ✓
├─ Schedule UI / API Integration                    ✓
├─ Issue UI / API Integration                       ✓
├─ Risk UI / API Integration                        ✓
├─ Change UI / API / Workflow                       ✓
├─ Change Browser / E2E                              →
├─ Evidence UI / API Integration                    →
├─ CMDB UI                                          →
├─ Report UI                                        →
└─ AI Assistant UI                                  →

Integration

├─ Project / WBS / Schedule                         ✓
├─ Common Search Pagination Standardization         ✓
├─ Issue / Risk API Integration                     ✓
├─ Change Workflow Integration                      ✓
├─ Evidence / Inspection Backend E2E               ✓
├─ Change Browser / E2E                              →
├─ Evidence Frontend Integration                    →
└─ Full E2E                                         →
```

`✓` 완료, `→` 진행 예정 또는 진행 중.

세부 완료 여부는 실제 Feature 검증 결과와 `DEVELOPMENT_ROADMAP.md`를 기준으로 관리한다.

---

# Current Sprint & Priority

## Sprint 4 — Issue / Risk / Change Management

Issue와 Risk의 Backend/Frontend 구현이 완료되었으며 Change Management의 Backend/Frontend Workflow가 `develop`에 통합되었다.

Change는 Browser Verification, Approval/Rejection E2E, History/Audit 보완 단계다.

Evidence & Inspection Backend Workflow도 구현 및 Swagger E2E 검증이 완료되어 다음 Frontend 단계로 이동할 수 있는 상태다.

## Current Priority

1. Change Browser Verification
2. Change Approval / Rejection E2E Verification
3. Change Status Transition Verification
4. Change History / Audit
5. WBS / Schedule / Issue / Risk Browser / E2E Verification
6. Calendar / Timeline / Milestone / Gantt
7. WBS / Schedule Relationship Visualization
8. Project Dashboard API / Data Integration
9. Evidence & Inspection Frontend
10. CMDB
11. Dashboard / Report
12. Spring AI

---

# Planned Releases

## v0.6.x — WBS & Schedule Management Integration

### Completed

* WBS CRUD
* WBS Tree
* WBS Frontend Integration
* WBS Status Change
* WBS Delete
* WBS Browser Verification
* Schedule CRUD
* Schedule Search
* Pagination
* Dynamic Sorting
* Period Search
* Validation
* Swagger Verification
* Schedule Frontend API Client
* Schedule Frontend Integration
* Schedule UI
* Schedule Browser Verification

### Planned / Remaining

* Calendar
* Timeline
* Milestone
* Gantt
* WBS / Schedule Relationship
* Progress Visualization

---

## v0.7.x — Issue / Risk / Change Management

### Completed

* Issue CRUD
* Issue Search
* Project-scoped Issue Key
* Issue Status Workflow
* Risk CRUD
* Risk Search
* Risk Assessment
* Probability / Impact
* Risk Matrix
* Change Request
* Change Status Workflow
* Change Approval / Rejection
* Change Frontend Workflow
* Change API Integration

### Remaining

* Issue Browser / E2E
* Risk Browser / E2E
* Change Browser / E2E
* Change History / Audit
* Change Impact Analysis
* Evidence linkage

---

## v0.8.x — Evidence & Inspection Management

### Completed

* Evidence Requirement
* Evidence Checklist
* Requirement Registration
* Evidence Registration
* Evidence Status Calculation
* Missing Evidence Detection
* Evidence Verification
* Approval / Rejection
* Inspection
* Inspection Item
* Inspection Result
* Evidence / Inspection Workflow
* Swagger E2E Verification
* Requirement Status Refresh

### Remaining

* Evidence Frontend
* Inspection Frontend
* Evidence Dashboard
* Browser Verification
* Frontend E2E
* Evidence / Change linkage
* Report integration

---

## v0.9.x — CMDB

### Planned

* Configuration Items

  * Server CI
  * Database CI
  * Software CI
* CI / Dependency Relationships
* Version Management
* Configuration History
* Change Tracking

---

## v0.10.x — Dashboard & Reporting

### Planned

* Executive Dashboard
* Project Dashboard
* WBS Dashboard
* Schedule Dashboard
* Issue Dashboard
* Risk Dashboard
* Change Dashboard
* Evidence Dashboard
* CMDB Dashboard
* Weekly Report
* Monthly Report
* Executive Report
* Evidence Status Report
* Excel Export
* PDF Export
* Statistics API

> Dashboard Reference UI는 v0.5.3에서 선행 구현되었다. v0.10.x에서는 실제 Backend API와 각 Domain 데이터를 통합한다.

---

## v1.0.0 — AI Powered PMIS

### Planned

* Spring AI 기반 PM Assistant
* Project Summary
* Schedule Analysis
* Issue Summary
* Risk Analysis
* Change Impact Analysis
* Evidence Gap Analysis
* Meeting Summary
* Report Generation
* OpenAI / Azure OpenAI / Ollama 연동 검토
* PMIS Document Search
* Semantic Search
* RAG 기반 질의응답

---

# Git Flow

```text
main
│
├─ release/*
│
└─ develop
   ├─ feature/*
   └─ hotfix/*
```

* `main`: Production Release
* `develop`: Integration Branch
* `feature/*`: Feature Development
* `release/*`: Release Preparation
* `hotfix/*`: 긴급 수정

기본 흐름:

```text
Feature Development
        ↓
Local Test
        ↓
Build / Documentation
        ↓
Commit / Push
        ↓
Pull Request
        ↓
Merge to develop
        ↓
Integration Test
        ↓
Release
```

Hotfix는 별도 브랜치에서 수정·검증하며 실제 `develop` 통합 전까지 Release History에 완료 사항으로 기록하지 않는다.

---

# Commit Convention

| Prefix     | Description   |
| ---------- | ------------- |
| `feat`     | Feature       |
| `fix`      | Bug Fix       |
| `refactor` | Refactoring   |
| `docs`     | Documentation |
| `style`    | Code Style    |
| `test`     | Test          |
| `build`    | Build         |
| `chore`    | Maintenance   |
| `perf`     | Performance   |
| `security` | Security      |

Examples:

```text
feat: implement JWT authentication
feat: implement project CRUD
feat: implement WBS CRUD
feat: implement schedule management API
feat(frontend): complete project CRUD
feat(frontend): implement WBS API integration
feat(frontend): complete change management workflow ui
feat: complete evidence inspection workflow

fix: resolve JWT validation issue
docs: update development roadmap
refactor: improve exception handling
```

---

# Development Process

```text
Planning
   ↓
Requirement
   ↓
Domain / Data Model Design
   ↓
Feature Branch
   ↓
Backend API Design & Implementation
   ↓
API Verification
   ↓
Frontend API Client / Type Definition
   ↓
Frontend Implementation
   ↓
API Integration
   ↓
Browser / E2E Verification
   ↓
Production Build
   ↓
Documentation Update
   ↓
Commit / Push / Pull Request
   ↓
Merge to develop
   ↓
Integration Test
   ↓
Release
```

## Frontend API Integration Process

```text
Backend API
   ↓
Swagger / API Verification
   ↓
Frontend API Client
   ↓
Type Definition
   ↓
Feature Component / Page UI
   ↓
API Integration
   ↓
Loading / Empty / Error State
   ↓
Browser Verification
   ↓
Production Build
   ↓
Documentation
   ↓
Commit / Push / Merge
   ↓
Integration Test / E2E Verification
```

---

# Evidence Development Flow

```text
Evidence Requirement Backend
├─ Requirement CRUD                ✓
├─ Requirement Key                 ✓
├─ Requirement Status              ✓
├─ Due Date / Required             ✓
├─ WBS Relationship                ✓
└─ Status Refresh                  ✓
        ↓
Evidence Backend
├─ Evidence CRUD                   ✓
├─ Evidence Key                    ✓
├─ Requirement Relationship        ✓
├─ Verification                    ✓
└─ Project / WBS Search            ✓
        ↓
Inspection Backend
├─ Inspection CRUD                 ✓
├─ Inspection Status Workflow      ✓
├─ Inspection Item                 ✓
├─ Evidence Relationship           ✓
└─ Result Workflow                 ✓
        ↓
Integration
├─ Swagger Verification            ✓
├─ Evidence E2E                    ✓
├─ Inspection E2E                  ✓
├─ Browser Verification            →
└─ Frontend E2E                    →
        ↓
Evidence Frontend
├─ API Client                      →
├─ Requirement List                →
├─ Evidence List                   →
├─ Evidence Registration           →
├─ Verification UI                 →
├─ Inspection UI                   →
└─ Evidence Dashboard              →
```

---

# Change Development Flow

```text
Change Backend
├─ CRUD / Search / API             ✓
├─ Status Workflow API             ✓
├─ Approval API                    ✓
├─ Project-scoped Change           ✓
└─ History / Impact Analysis       →

        ↓

Change Frontend
├─ API Client                      ✓
├─ List / Search / Filter          ✓
├─ Summary / Detail                ✓
├─ Create / Edit                   ✓
├─ Status Workflow                 ✓
├─ Approval / Rejection            ✓
└─ Project Navigation / Route      ✓

        ↓

Integration
├─ API Integration                 ✓
├─ Production Build                ✓
├─ Browser Verification            →
└─ Approval / Rejection E2E        →
```

---

# Schedule Development Flow

```text
Schedule Backend
├─ CRUD                            ✓
├─ Search                          ✓
├─ Pagination / Sorting            ✓
├─ Period Search                   ✓
└─ Swagger Verification            ✓

        ↓

Schedule Frontend
├─ API Client                      ✓
├─ List / Search / Detail          ✓
├─ Create / Edit / Delete          ✓
├─ Status                          ✓
└─ API Integration                 ✓

        ↓

Visualization
├─ Calendar                        →
├─ Timeline / Milestone            →
├─ Gantt                           →
└─ WBS / Schedule Relationship     →
```

---

# Documentation Policy

다음 문서는 소스코드 변경 시 관련 내용을 함께 검토하고 최신 상태로 유지한다.

* `CHANGELOG.md`
* `DEVELOPMENT_ROADMAP.md`
* `PROJECT_OVERVIEW.md`
* `ARCHITECTURE.md`
* `PORTFOLIO.md`

## 주요 Design 문서

* `docs/design/project/PROJECT_DESIGN.md`
* `docs/design/project/PROJECT_DASHBOARD_DESIGN.md`
* `docs/design/wbs/WBS_DESIGN.md`
* `docs/design/schedule/SCHEDULE_DESIGN.md`
* `docs/design/issue/ISSUE_DESIGN.md`
* `docs/design/report/REPORT_DESIGN.md`
* `docs/design/server/SERVER_CONFIGURATION_DESIGN.md`
* `docs/design/evidence/EVIDENCE_DESIGN.md`
* `docs/design/evidence/EVIDENCE_FRONTEND_BACKEND_CONTRACT.md`

## Frontend 문서

* `001_frontend_architecture.md`
* `002_frontend_folder_structure.md`
* `003_frontend_coding_convention.md`
* `004_frontend_ui_design.md`
* `005_frontend_component_architecture.md`
* `006_frontend_development_history.md`

API Integration 변경 시 Source Code, API Contract, Design Document, Frontend Architecture / Component Architecture, Development History, CHANGELOG 및 Roadmap을 함께 검토한다.

---

# Maintenance Policy

* 모든 기능은 Feature Branch에서 개발한다.
* 주요 변경 사항은 `develop` 통합 후 CHANGELOG에 기록한다.
* Backend / Frontend / Integration / Verification 상태를 독립적으로 관리한다.
* Domain 완료는 Backend 구현만으로 판단하지 않고 Frontend, Integration, Verification까지 고려한다.
* API Integration 기능은 API Verification과 Browser Verification을 수행한다.
* Feature Merge 후 `develop` Integration Test를 수행한다.
* UI Reference 구현과 실제 API Integration 단계를 구분한다.
* Release마다 Git Tag를 생성하고 Semantic Versioning을 준수한다.
* `main`에는 검증된 코드만 Release한다.
* Evidence Domain은 Backend Workflow와 Frontend Integration을 독립적으로 관리한다.
* 실제 Swagger / E2E 검증 결과를 API Contract와 CHANGELOG에 반영한다.

---

# Current Information

| Item                                | Value                                                                      |
| ----------------------------------- | -------------------------------------------------------------------------- |
| Last Updated                        | 2026-10-08                                                                 |
| Current Branch                      | `develop`                                                                  |
| Integration Branch                  | `develop`                                                                  |
| Current Release                     | `v0.5.3`                                                                   |
| Roadmap Version                     | `v1.14`                                                                    |
| Last integrated commit              | `327ee15`                                                                  |
| Last integrated feature             | Evidence & Inspection workflow                                             |
| Project Frontend                    | CRUD / API Integration Complete                                            |
| WBS Backend / Frontend              | CRUD / Tree / Integration Complete                                         |
| Schedule Backend / Frontend         | CRUD / Search / UI / API Integration Complete                              |
| Issue Backend                       | CRUD / Search / Workflow / Key Generation Complete                         |
| Issue Frontend                      | UI / API Integration Complete; Browser/E2E Verification Remaining          |
| Risk Backend / Frontend             | Management UI/API Integration Complete; Browser/E2E Verification Remaining |
| Change Backend                      | API / Search / Status / Approval Workflow Foundation Complete              |
| Change Frontend                     | UI / API / Status / Approval / Rejection Workflow Complete                 |
| Change Verification                 | Browser / Approval-Rejection E2E Remaining                                 |
| Evidence Requirement                | Backend Complete                                                           |
| Evidence Management                 | Backend CRUD / Verification Complete                                       |
| Evidence Requirement Status Refresh | Complete                                                                   |
| Inspection                          | Backend / Workflow Complete                                                |
| Evidence / Inspection E2E           | Swagger Verification Complete                                              |
| Evidence Frontend                   | Not Started                                                                |
| Common Search Pagination            | Issue / Risk / Change / Schedule / WBS 공통화 완료                              |
| Next Development Stage              | Evidence Frontend → Change Verification → CMDB                             |
| Maintainer                          | Seo Seokhyeon                                                              |

---

# Next Milestone

## Evidence & Inspection Frontend

```text
Evidence API Contract
        ↓
Frontend Type Definition
        ↓
Evidence API Client
        ↓
Evidence Requirement List
        ↓
Evidence List / Detail
        ↓
Evidence Registration
        ↓
Verification UI
        ↓
Inspection List / Detail
        ↓
Inspection Item UI
        ↓
API Integration
        ↓
Browser Verification
        ↓
Production Build
        ↓
Frontend E2E
```

## Parallel Verification

```text
Change Browser Verification
        ↓
Approval / Rejection E2E
        ↓
Status Transition Verification
        ↓
History / Audit
```

## Subsequent Domains

```text
Evidence Frontend
      ↓
CMDB
      ↓
Dashboard / Report
      ↓
Spring AI
      ↓
Full E2E
      ↓
Production Release
      ↓
Portfolio Release
```

---

# Release Summary

| Version      | Description                                                                  |
| ------------ | ---------------------------------------------------------------------------- |
| v0.1.0       | Project Initialization                                                       |
| v0.2.0       | Common Infrastructure                                                        |
| v0.3.0       | Security Foundation                                                          |
| v0.3.1       | Authentication & Authorization                                               |
| v0.3.2       | Swagger / OpenAPI                                                            |
| v0.4.0       | Project Management Backend                                                   |
| v0.5.0       | Frontend Foundation                                                          |
| v0.5.1       | Frontend Layout & Common Components                                          |
| v0.5.2       | Project Detail API & Controller Separation                                   |
| v0.5.3       | Frontend Dashboard HMC Reference                                             |
| Unreleased   | Project Frontend CRUD                                                        |
| Unreleased   | WBS Management & Frontend Integration                                        |
| Unreleased   | Schedule Backend & Frontend Integration                                      |
| Unreleased   | Common Search Filter / Pagination Standardization                            |
| Unreleased   | Issue / Risk Frontend Integration                                            |
| Unreleased   | Change Management Workflow UI / API Integration                              |
| Unreleased   | Evidence Requirement / Evidence / Verification                               |
| Unreleased   | Inspection / Inspection Item / Evidence E2E Workflow                         |
| Next Release | WBS / Schedule / Issue / Risk / Change / Evidence Integration & Verification |
| v0.9.x       | CMDB                                                                         |
| v0.10.x      | Dashboard & Reporting                                                        |
| v1.0.0       | AI Powered PMIS                                                              |

---

# End of Document
