# Project Domain Design

> PMIS (Project Management Information System)  
> Version: 1.1  
> Domain: Project  
> Status: Backend Implemented / Frontend Development Reference  
> Last Updated: 2026-08-13

---

# 1. 문서 목적

본 문서는 현재 구현 완료된 PMIS Backend의 **Project Domain**을 기준으로 작성한 Frontend 개발 참조 문서이다.

Frontend 개발자는 본 문서와 Swagger API 명세를 기준으로 Project 화면 및 API 연동을 구현할 수 있다.

> **Backend 구현을 Source of Truth로 한다.**
>
> 본 문서의 API, Request/Response, Enum, Pagination 구조는 현재 Backend Swagger 명세를 기준으로 한다.

---

# 2. Project Domain 개요

Project는 PMIS에서 프로젝트 자체를 관리하는 최상위 Domain이다.

Project는 다음 Domain의 상위 기준 데이터로 활용된다.

```text
Project
 ├── WBS
 ├── Schedule
 ├── Issue
 ├── Risk
 ├── Change
 ├── CMDB
 └── Report
```

Project Domain은 다음 기능을 제공한다.

- 프로젝트 생성
- 프로젝트 검색
- 프로젝트 수정
- 프로젝트 삭제
- 프로젝트 상세 조회
- 프로젝트 상태 관리
- 프로젝트 우선순위 관리
- 프로젝트 기간 관리
- 프로젝트 Dashboard 집계

단, Dashboard 집계는 별도의 `PROJECT_DASHBOARD_DESIGN.md`에서 관리한다.

---

# 3. Project Entity

현재 Backend Project Response 기준 Project의 주요 필드는 다음과 같다.

| Field | Type | Required | Description |
|---|---|---:|---|
| id | Long | Y | Project PK |
| projectCode | String | Y | 프로젝트 코드 |
| projectName | String | Y | 프로젝트명 |
| customerName | String | Y | 고객사 |
| projectManager | String | Y | 프로젝트 관리자 |
| description | String | N | 프로젝트 설명 |
| startDate | LocalDate | Y | 프로젝트 시작일 |
| endDate | LocalDate | Y | 프로젝트 종료일 |
| status | ProjectStatus | Y | 프로젝트 상태 |
| priority | ProjectPriority | Y | 프로젝트 우선순위 |
| createdAt | LocalDateTime | Y | 생성일시 |
| updatedAt | LocalDateTime | Y | 수정일시 |

---

# 4. Project 상태

현재 Backend에서 사용하는 Project Status는 다음과 같다.

```text
PLANNING
IN_PROGRESS
ON_HOLD
COMPLETED
CANCELLED
```

| Status | 의미 |
|---|---|
| PLANNING | 계획 |
| IN_PROGRESS | 진행 |
| ON_HOLD | 보류 |
| COMPLETED | 완료 |
| CANCELLED | 취소 |

Frontend에서는 임의의 상태값을 추가하지 않는다.

TypeScript 예시:

```ts
export type ProjectStatus =
  | 'PLANNING'
  | 'IN_PROGRESS'
  | 'ON_HOLD'
  | 'COMPLETED'
  | 'CANCELLED';
```

---

# 5. Project Priority

현재 Backend에서 사용하는 Priority는 다음과 같다.

```text
LOW
MEDIUM
HIGH
CRITICAL
```

| Priority | 의미 |
|---|---|
| LOW | 낮음 |
| MEDIUM | 보통 |
| HIGH | 높음 |
| CRITICAL | 긴급 |

TypeScript:

```ts
export type ProjectPriority =
  | 'LOW'
  | 'MEDIUM'
  | 'HIGH'
  | 'CRITICAL';
```

---

# 6. Project Response

일반적인 Project Response 구조:

```json
{
  "id": 2,
  "projectCode": "PMIS-2026-001",
  "projectName": "PMIS 구축",
  "customerName": "금융결제원",
  "projectManager": "홍길동",
  "description": "PMIS 구축 프로젝트",
  "startDate": "2026-01-01",
  "endDate": "2026-12-31",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "createdAt": "2026-08-13T00:29:33.460Z",
  "updatedAt": "2026-08-13T00:29:33.460Z"
}
```

TypeScript:

```ts
export interface ProjectResponse {
  id: number;
  projectCode: string;
  projectName: string;
  customerName: string;
  projectManager: string;
  description: string;
  startDate: string;
  endDate: string;
  status: ProjectStatus;
  priority: ProjectPriority;
  createdAt: string;
  updatedAt: string;
}
```

---

# 7. 공통 API Response

PMIS Backend는 공통적으로 `ApiResponse<T>` 구조를 사용한다.

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "조회 성공",
  "data": {}
}
```

구조:

```text
success
code
message
data
```

Frontend 공통 타입:

```ts
export interface ApiResponse<T> {
  success: boolean;
  code: string;
  message: string;
  data: T;
}
```

Project API에서는 다음 형태로 사용된다.

```ts
ApiResponse<ProjectResponse>
ApiResponse<ProjectDetailResponse>
ApiResponse<PageProjectResponse>
ApiResponse<ProjectDashboardResponse>
ApiResponse<string>
```

---

# 8. API Base URL

현재 Backend 개발 환경:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI:

```text
http://localhost:8080/v3/api-docs
```

Frontend에서는 환경변수로 관리하는 것을 권장한다.

예:

```env
VITE_API_BASE_URL=http://localhost:8080
```

---

# 9. Project API 목록

현재 구현된 Project 관련 API:

| Method | URI | 기능 |
|---|---|---|
| POST | `/api/projects` | 프로젝트 생성 |
| GET | `/api/projects` | 프로젝트 검색 |
| PUT | `/api/projects/{projectId}` | 프로젝트 수정 |
| DELETE | `/api/projects/{projectId}` | 프로젝트 삭제 |
| GET | `/api/projects/{projectId}/detail` | 프로젝트 상세 조회 |
| GET | `/api/projects/dashboard` | 프로젝트 Dashboard |

---

# 10. 프로젝트 생성

## Endpoint

```http
POST /api/projects
```

## 설명

신규 프로젝트를 생성한다.

## Request

```json
{
  "projectCode": "PMIS-2026-001",
  "projectName": "PMIS 구축",
  "customerName": "금융결제원",
  "projectManager": "홍길동",
  "description": "Project Management Information System 구축",
  "startDate": "2026-01-01",
  "endDate": "2026-12-31",
  "priority": "HIGH"
}
```

## Request Schema

```ts
export interface CreateProjectRequest {
  projectCode: string;
  projectName: string;
  customerName: string;
  projectManager: string;
  description: string;
  startDate: string;
  endDate: string;
  priority: ProjectPriority;
}
```

## Field 제한

| Field | Type | 제한 |
|---|---|---|
| projectCode | string | 0~30 |
| projectName | string | 0~200 |
| customerName | string | 0~100 |
| projectManager | string | 0~100 |
| description | string | 0~5000 |
| startDate | date | `YYYY-MM-DD` |
| endDate | date | `YYYY-MM-DD` |
| priority | enum | LOW/MEDIUM/HIGH/CRITICAL |

`status`는 생성 Request에 포함하지 않는다.

현재 생성 시 Backend 기본 상태가 적용된다.

## Response

HTTP Status:

```text
201 Created
```

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "생성되었습니다.",
  "data": {
    "id": 2,
    "projectCode": "PMIS-2026-001",
    "projectName": "PMIS 구축",
    "customerName": "금융결제원",
    "projectManager": "홍길동",
    "description": "Project Management Information System 구축",
    "startDate": "2026-01-01",
    "endDate": "2026-12-31",
    "status": "PLANNING",
    "priority": "HIGH",
    "createdAt": "2026-08-13T00:29:33.488Z",
    "updatedAt": "2026-08-13T00:29:33.488Z"
  }
}
```

---

# 11. 프로젝트 검색

## Endpoint

```http
GET /api/projects
```

## 설명

검색 조건과 Pagination을 이용하여 프로젝트 목록을 조회한다.

현재 Backend는 `ProjectSearchRequest`와 Spring `Pageable`을 사용한다.

## Search Request

| Field | Type | Description |
|---|---|---|
| projectCode | string | 프로젝트 코드 |
| projectName | string | 프로젝트명 |
| customerName | string | 고객사 |
| projectManager | string | 프로젝트 관리자 |
| status | ProjectStatus | 프로젝트 상태 |
| priority | ProjectPriority | 프로젝트 우선순위 |
| startDateFrom | date | 시작일 From |
| startDateTo | date | 시작일 To |
| endDateFrom | date | 종료일 From |
| endDateTo | date | 종료일 To |

예:

```text
GET /api/projects?projectName=PMIS&status=IN_PROGRESS
```

Pagination 예:

```text
GET /api/projects?page=0&size=20
```

정렬 예:

```text
GET /api/projects?page=0&size=20&sort=projectName,asc
```

복합 조건:

```text
GET /api/projects?projectName=PMIS&status=IN_PROGRESS&page=0&size=20&sort=projectName,asc
```

> 실제 Query Parameter 직렬화는 현재 Swagger의 `ProjectSearchRequest`와 `Pageable`을 기준으로 한다.

## TypeScript

```ts
export interface ProjectSearchRequest {
  projectCode?: string;
  projectName?: string;
  customerName?: string;
  projectManager?: string;
  status?: ProjectStatus;
  priority?: ProjectPriority;
  startDateFrom?: string;
  startDateTo?: string;
  endDateFrom?: string;
  endDateTo?: string;
}

export interface Pageable {
  page?: number;
  size?: number;
  sort?: string[];
}
```

## Response

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "조회 성공",
  "data": {
    "totalPages": 1,
    "totalElements": 1,
    "size": 20,
    "content": [
      {
        "id": 2,
        "projectCode": "PMIS-2026-001",
        "projectName": "PMIS 구축",
        "customerName": "금융결제원",
        "projectManager": "홍길동",
        "description": "PMIS 구축 프로젝트",
        "startDate": "2026-01-01",
        "endDate": "2026-12-31",
        "status": "IN_PROGRESS",
        "priority": "HIGH",
        "createdAt": "2026-08-13T00:29:33.475Z",
        "updatedAt": "2026-08-13T00:29:33.475Z"
      }
    ],
    "number": 0,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "first": true,
    "last": true,
    "numberOfElements": 1,
    "pageable": {
      "offset": 0,
      "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
      },
      "unpaged": false,
      "paged": true,
      "pageSize": 20,
      "pageNumber": 0
    },
    "empty": false
  }
}
```

Frontend Page 타입:

```ts
export interface PageProjectResponse {
  totalPages: number;
  totalElements: number;
  size: number;
  content: ProjectResponse[];
  number: number;
  sort: SortObject;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  pageable: PageableObject;
  empty: boolean;
}
```

---

# 12. 프로젝트 수정

## Endpoint

```http
PUT /api/projects/{projectId}
```

예:

```http
PUT /api/projects/2
```

## Request

```json
{
  "projectName": "PMIS 고도화 구축",
  "customerName": "금융결제원",
  "projectManager": "홍길동",
  "description": "PMIS 프로젝트 고도화",
  "startDate": "2026-01-01",
  "endDate": "2026-12-31",
  "status": "IN_PROGRESS",
  "priority": "HIGH"
}
```

## TypeScript

```ts
export interface UpdateProjectRequest {
  projectName: string;
  customerName: string;
  projectManager: string;
  description: string;
  startDate: string;
  endDate: string;
  status: ProjectStatus;
  priority: ProjectPriority;
}
```

## Field 제한

| Field | 제한 |
|---|---|
| projectName | 0~200 |
| customerName | 0~100 |
| projectManager | 0~100 |
| description | 0~5000 |
| startDate | date |
| endDate | date |
| status | ProjectStatus |
| priority | ProjectPriority |

`projectCode`는 수정 Request에 포함하지 않는다.

## Response

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "수정되었습니다.",
  "data": {
    "id": 2,
    "projectCode": "PMIS-2026-001",
    "projectName": "PMIS 고도화 구축",
    "customerName": "금융결제원",
    "projectManager": "홍길동",
    "description": "PMIS 프로젝트 고도화",
    "startDate": "2026-01-01",
    "endDate": "2026-12-31",
    "status": "IN_PROGRESS",
    "priority": "HIGH",
    "createdAt": "2026-08-13T00:29:33.460Z",
    "updatedAt": "2026-08-13T00:40:00.000Z"
  }
}
```

---

# 13. 프로젝트 삭제

## Endpoint

```http
DELETE /api/projects/{projectId}
```

예:

```http
DELETE /api/projects/2
```

## Response

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "삭제되었습니다.",
  "data": null
}
```

Frontend에서는 성공 여부를 `success`로 확인하고 목록을 다시 조회하거나 해당 프로젝트 화면에서 이동한다.

> Project에 WBS 등 하위 데이터가 존재하는 경우 삭제 정책은 Backend 구현 및 Exception Code를 따른다. Frontend에서 임의로 Cascade 삭제하지 않는다.

---

# 14. 프로젝트 상세 조회

## Endpoint

```http
GET /api/projects/{projectId}/detail
```

예:

```http
GET /api/projects/2/detail
```

## 목적

Project의 기본 정보와 현재 Progress 정보를 포함한 상세 조회 API이다.

## Response

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "조회 성공",
  "data": {
    "id": 2,
    "projectCode": "PMIS-2026-001",
    "projectName": "PMIS 구축",
    "customerName": "금융결제원",
    "projectManager": "홍길동",
    "description": "PMIS 구축 프로젝트",
    "startDate": "2026-01-01",
    "endDate": "2026-12-31",
    "status": "IN_PROGRESS",
    "priority": "HIGH",
    "progressRate": 72,
    "createdAt": "2026-08-13T00:29:33.393Z",
    "updatedAt": "2026-08-13T00:29:33.393Z"
  }
}
```

## TypeScript

```ts
export interface ProjectDetailResponse extends ProjectResponse {
  progressRate: number;
}
```

> `progressRate`는 현재 Project Detail API에서 제공되는 조회값이다. Frontend에서 Project 기본 Entity에 임의로 추가하여 저장하지 않는다.

---

# 15. Frontend 화면 매핑

Project Domain의 권장 화면 구성:

```text
Project
├── Project List
│   ├── 검색
│   ├── Pagination
│   └── Project Table
│
├── Project Create
│   └── 생성 Form
│
├── Project Detail
│   ├── 기본 정보
│   ├── 기간
│   ├── 상태
│   ├── 우선순위
│   └── Progress
│
└── Project Edit
    └── 수정 Form
```

추천 Route:

```text
/projects
/projects/new
/projects/:projectId
/projects/:projectId/edit
```

---

# 16. Frontend API Service 예시

권장 구조:

```text
src/
├── api/
│   └── projectApi.ts
├── types/
│   └── project.ts
├── pages/
│   └── project/
│       ├── ProjectListPage.tsx
│       ├── ProjectCreatePage.tsx
│       ├── ProjectDetailPage.tsx
│       └── ProjectEditPage.tsx
└── components/
    └── project/
        ├── ProjectSearchForm.tsx
        ├── ProjectTable.tsx
        ├── ProjectForm.tsx
        └── ProjectInfo.tsx
```

API Service 예:

```ts
import api from './client';

export const getProjects = (
  params: ProjectSearchRequest & {
    page?: number;
    size?: number;
    sort?: string[];
  }
) => {
  return api.get<ApiResponse<PageProjectResponse>>(
    '/api/projects',
    { params }
  );
};

export const getProjectDetail = (projectId: number) => {
  return api.get<ApiResponse<ProjectDetailResponse>>(
    `/api/projects/${projectId}/detail`
  );
};

export const createProject = (request: CreateProjectRequest) => {
  return api.post<ApiResponse<ProjectResponse>>(
    '/api/projects',
    request
  );
};

export const updateProject = (
  projectId: number,
  request: UpdateProjectRequest
) => {
  return api.put<ApiResponse<ProjectResponse>>(
    `/api/projects/${projectId}`,
    request
  );
};

export const deleteProject = (projectId: number) => {
  return api.delete<ApiResponse<null>>(
    `/api/projects/${projectId}`
  );
};
```

---

# 17. Axios Client 예시

Frontend의 공통 Axios Client에서 Backend Base URL을 관리한다.

```ts
import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export default api;
```

JWT 인증이 필요한 API는 공통 Authorization 처리 구조를 사용한다.

```http
Authorization: Bearer {accessToken}
```

---

# 18. Project List UI 권장 구성

```text
┌──────────────────────────────────────────────────────┐
│ Project Management                                   │
├──────────────────────────────────────────────────────┤
│ [프로젝트 코드] [프로젝트명] [고객사] [상태] [검색] │
├──────────────────────────────────────────────────────┤
│ Code       Project       Customer    Status  Priority │
│ PMIS-001   PMIS 구축     금융결제원  진행    HIGH     │
│ PMIS-002   차세대 구축   ○○기관      계획    MEDIUM   │
├──────────────────────────────────────────────────────┤
│                 < 1 2 3 4 5 >                        │
└──────────────────────────────────────────────────────┘
```

---

# 19. Project Detail UI 권장 구성

```text
┌──────────────────────────────────────────────────────┐
│ PMIS 구축                                             │
│ PMIS-2026-001                                        │
├──────────────────────────────────────────────────────┤
│ 고객사       금융결제원                               │
│ PM            홍길동                                 │
│ 기간         2026-01-01 ~ 2026-12-31                │
│ 상태         IN_PROGRESS                             │
│ 우선순위     HIGH                                    │
├──────────────────────────────────────────────────────┤
│ Progress                                             │
│ ████████████████████████████████░░░░ 72%            │
├──────────────────────────────────────────────────────┤
│ [수정] [삭제]                                        │
└──────────────────────────────────────────────────────┘
```

---

# 20. 날짜 처리

Backend의 날짜 타입은 다음과 같다.

Project 기간:

```text
LocalDate
```

JSON:

```text
YYYY-MM-DD
```

예:

```text
2026-08-13
```

생성/수정 시간:

```text
LocalDateTime
```

JSON:

```text
2026-08-13T00:29:33.393Z
```

Frontend에서는 날짜와 시간을 분리하여 표시할 수 있다.

```ts
const formatDate = (value: string) => {
  return value.slice(0, 10);
};
```

---

# 21. Pagination 처리

Project Search는 Spring Data Page 형태를 사용한다.

Frontend에서 가장 중요한 값:

```text
data.content
data.totalPages
data.totalElements
data.number
data.size
data.first
data.last
```

예:

```ts
const response = await getProjects({
  page: 0,
  size: 20,
});

const page = response.data.data;

console.log(page.content);
console.log(page.totalPages);
console.log(page.totalElements);
```

Pagination UI는 `number`, `totalPages`를 기준으로 구성한다.

---

# 22. Error 처리

Frontend는 API Response의 다음 값을 기준으로 오류를 처리한다.

```json
{
  "success": false,
  "code": "ERROR_CODE",
  "message": "오류 메시지",
  "data": null
}
```

Frontend에서는:

```ts
if (!response.data.success) {
  // 공통 오류 처리
}
```

와 같은 구조로 처리한다.

HTTP Status와 Backend Error Code를 함께 고려한다.

---

# 23. Project API와 WBS의 관계

Project는 WBS의 상위 Domain이다.

```text
Project
   │
   └── WBS
```

Project 화면에서 WBS로 이동하는 경우:

```text
/projects/{projectId}
        ↓
/projects/{projectId}/wbs
```

WBS API:

```text
GET  /api/projects/{projectId}/wbs
POST /api/projects/{projectId}/wbs
GET  /api/projects/{projectId}/wbs/tree
GET  /api/projects/{projectId}/wbs/root
```

Project ID를 WBS API의 기준 식별자로 사용한다.

---

# 24. Project와 Dashboard의 책임 분리

Project CRUD와 Dashboard 집계는 분리한다.

```text
Project Domain
├── 생성
├── 검색
├── 수정
├── 삭제
└── 상세 조회

Project Dashboard
└── 전체 프로젝트 현황 집계
```

Dashboard API:

```http
GET /api/projects/dashboard
```

상세 설계는:

```text
PROJECT_DASHBOARD_DESIGN.md
```

에서 관리한다.

---

# 25. Swagger 기준 API 요약

| 기능 | Method | Endpoint | Response |
|---|---|---|---|
| 프로젝트 생성 | POST | `/api/projects` | `ApiResponse<ProjectResponse>` |
| 프로젝트 검색 | GET | `/api/projects` | `ApiResponse<PageProjectResponse>` |
| 프로젝트 수정 | PUT | `/api/projects/{projectId}` | `ApiResponse<ProjectResponse>` |
| 프로젝트 삭제 | DELETE | `/api/projects/{projectId}` | `ApiResponse<String/void>` |
| 프로젝트 상세 | GET | `/api/projects/{projectId}/detail` | `ApiResponse<ProjectDetailResponse>` |
| Dashboard | GET | `/api/projects/dashboard` | `ApiResponse<ProjectDashboardResponse>` |

---

# 26. Swagger 개발 순서

Frontend 개발자는 다음 순서로 API를 확인한다.

```text
1. GET /api/projects
        ↓
2. POST /api/projects
        ↓
3. GET /api/projects/{projectId}/detail
        ↓
4. PUT /api/projects/{projectId}
        ↓
5. DELETE /api/projects/{projectId}
        ↓
6. GET /api/projects/dashboard
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

---

# 27. Frontend 구현 체크리스트

## Types

- [ ] `ProjectStatus`
- [ ] `ProjectPriority`
- [ ] `ProjectResponse`
- [ ] `ProjectDetailResponse`
- [ ] `CreateProjectRequest`
- [ ] `UpdateProjectRequest`
- [ ] `ProjectSearchRequest`
- [ ] `PageProjectResponse`

## API

- [ ] 프로젝트 목록 조회
- [ ] 프로젝트 검색
- [ ] 프로젝트 생성
- [ ] 프로젝트 상세 조회
- [ ] 프로젝트 수정
- [ ] 프로젝트 삭제

## UI

- [ ] Project List
- [ ] Search Form
- [ ] Pagination
- [ ] Create Form
- [ ] Detail View
- [ ] Edit Form
- [ ] Delete Confirmation
- [ ] Status Badge
- [ ] Priority Badge
- [ ] Progress 표시

## 연계

- [ ] Project → WBS 이동
- [ ] Project → Dashboard 연계
- [ ] JWT Authorization
- [ ] 공통 Error 처리

---

# 28. V1 Frontend 범위

Project Frontend V1은 다음 범위에 집중한다.

```text
Project List
    ↓
Project Search
    ↓
Project Create
    ↓
Project Detail
    ↓
Project Edit
    ↓
Project Delete
```

다음 기능은 Project Domain의 직접 구현 범위에서 제외한다.

- WBS 상세 관리
- Schedule 상세 관리
- Issue 관리
- Risk 관리
- Change 관리
- CMDB 관리
- Report 관리
- AI 기능

각 Domain은 별도의 Design Document를 기준으로 구현한다.

---

# 29. 현재 Backend 기준 핵심 규칙

1. Project의 PK는 `id`이다.
2. Project Code는 `projectCode`로 관리한다.
3. Project 생성 Request에는 `status`가 없다.
4. Project 생성 시 Backend가 기본 상태를 적용한다.
5. Project 수정에서는 `projectCode`를 변경하지 않는다.
6. Project Status는 5개 Enum만 사용한다.
7. Project Priority는 4개 Enum만 사용한다.
8. Project 검색은 Pagination을 지원한다.
9. Project 상세 API는 `progressRate`를 제공한다.
10. Dashboard API는 별도 Endpoint로 제공한다.
11. Frontend는 Backend API Response 구조를 임의로 변경하지 않는다.
12. 날짜는 `YYYY-MM-DD` 형식을 사용한다.
13. 생성/수정일시는 ISO Date-Time 형식을 사용한다.
14. 인증이 필요한 API는 JWT Bearer Token을 사용한다.
15. Project와 WBS는 `projectId`를 기준으로 연결한다.

---

# 30. Document History

| Version | Date | Author | Description |
|---|---|---|---|
| 1.0 | 2026-08-11 | Seo Seokhyeon | Initial Project Domain Design |
| 1.1 | 2026-08-13 | Seo Seokhyeon | Current Backend Swagger 기준으로 API/Frontend 개발 명세 보강 |

---

# End of Document
