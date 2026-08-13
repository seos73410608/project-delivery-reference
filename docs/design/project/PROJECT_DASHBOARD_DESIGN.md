# Project Dashboard Design

> PMIS (Project Management Information System)  
> Version: 1.0  
> Domain: Project Dashboard  
> Status: Design Confirmed  
> Last Updated: 2026-08-13

---

# 1. 개요

Project Dashboard는 PMIS에 등록된 프로젝트의 전체 현황을 요약하여 제공하는 조회 전용 Dashboard Domain이다.

Dashboard는 프로젝트 자체의 상세 데이터를 직접 관리하지 않고, Project Domain에서 관리되는 데이터를 집계하여 프로젝트 포트폴리오 및 전체 프로젝트 현황을 빠르게 파악할 수 있도록 한다.

현재 Backend에는 다음 Dashboard API가 구현되어 있다.

```text
GET /api/projects/dashboard
```

Frontend는 본 문서를 기준으로 Dashboard 화면 및 API 연동을 구현한다.

---

# 2. 설계 목표

- 전체 프로젝트 수 표시
- 프로젝트 상태별 건수 표시
- 프로젝트 우선순위별 건수 표시
- 최근 프로젝트 건수 표시
- 종료 예정 프로젝트 건수 표시
- Project Domain과 Dashboard Domain의 책임 분리
- Dashboard API를 통한 단일 집계 데이터 제공
- Frontend에서 API 응답을 그대로 활용할 수 있는 구조 제공

---

# 3. Domain 책임

Dashboard는 프로젝트 데이터를 직접 생성하거나 수정하지 않는다.

```text
Project Domain
      │
      │ 프로젝트 원천 데이터
      ▼
Project Dashboard
      │
      │ 집계
      ▼
Frontend Dashboard
```

Dashboard의 책임은 다음과 같다.

```text
Project 데이터 조회
        ↓
조건별 집계
        ↓
Dashboard Response
        ↓
Frontend 표시
```

Dashboard에서 Project 생성, 수정, 삭제를 수행하지 않는다.

---

# 4. 현재 Backend API

## 4.1 Dashboard 조회

```http
GET /api/projects/dashboard
```

설명:

```text
프로젝트 현황 통계를 조회합니다.
```

Request Parameter:

```text
없음
```

Request Body:

```text
없음
```

Response:

```http
200 OK
```

---

# 5. Swagger

Backend Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI:

```text
http://localhost:8080/v3/api-docs
```

Dashboard API Swagger Tag:

```text
Project Dashboard
```

Operation:

```text
GET /api/projects/dashboard
```

Frontend 개발 시 Backend Swagger를 기준으로 실제 API 응답을 확인할 수 있다.

---

# 6. API Response 구조

현재 API Response는 공통 `ApiResponse<T>` 구조를 사용한다.

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "조회 성공",
  "data": {
    "totalProjects": 0,
    "projectStatusCount": {},
    "projectPriorityCount": {},
    "recentProjects": 0,
    "upcomingDeadlineProjects": 0
  }
}
```

공통 Response 구조:

```text
success
code
message
data
```

Dashboard 데이터는 `data` 내부에 존재한다.

---

# 7. ProjectDashboardResponse

현재 Backend Response DTO:

```text
ProjectDashboardResponse
```

구조:

| Field | Type | Description |
|---|---|---|
| totalProjects | Long | 전체 프로젝트 수 |
| projectStatusCount | Map<String, Long> | 프로젝트 상태별 건수 |
| projectPriorityCount | Map<String, Long> | 프로젝트 우선순위별 건수 |
| recentProjects | Long | 최근 프로젝트 건수 |
| upcomingDeadlineProjects | Long | 종료 예정 프로젝트 건수 |

---

# 8. TypeScript Interface

Frontend에서는 다음과 같은 TypeScript 타입으로 관리할 수 있다.

```typescript
export interface ProjectDashboardResponse {
  totalProjects: number;
  projectStatusCount: Record<string, number>;
  projectPriorityCount: Record<string, number>;
  recentProjects: number;
  upcomingDeadlineProjects: number;
}
```

API 전체 Response:

```typescript
export interface ApiResponse<T> {
  success: boolean;
  code: string;
  message: string;
  data: T;
}
```

Dashboard API:

```typescript
export type ProjectDashboardApiResponse =
  ApiResponse<ProjectDashboardResponse>;
```

---

# 9. Dashboard API Client

Frontend API Layer에서는 다음과 같이 분리한다.

```text
src/
└── api/
    └── projectDashboardApi.ts
```

예시:

```typescript
import apiClient from '@/api/apiClient';
import type {
  ApiResponse,
  ProjectDashboardResponse,
} from '@/types/projectDashboard';

export const getProjectDashboard =
  async (): Promise<ApiResponse<ProjectDashboardResponse>> => {
    const response = await apiClient.get(
      '/api/projects/dashboard'
    );

    return response.data;
  };
```

실제 프로젝트의 공통 Axios Client 구조가 존재한다면 해당 구조를 우선 사용한다.

---

# 10. 인증

현재 PMIS Backend는 JWT 기반 인증을 사용한다.

Dashboard API 호출 시 Frontend는 로그인 후 발급받은 Access Token을 사용한다.

```text
Login
  ↓
Access Token
  ↓
Authorization Header
  ↓
GET /api/projects/dashboard
```

Header:

```http
Authorization: Bearer {accessToken}
```

Frontend에서는 API Client의 Interceptor를 이용하여 Access Token을 자동으로 전달하는 방식을 권장한다.

---

# 11. Dashboard 화면 구성

Dashboard는 크게 다음 영역으로 구성한다.

```text
┌─────────────────────────────────────────────┐
│ Project Dashboard                           │
├───────────┬───────────┬───────────┬─────────┤
│ 전체      │ 최근      │ 종료예정  │ 기타    │
│ 프로젝트  │ 프로젝트  │ 프로젝트  │ KPI     │
├───────────────────────┬─────────────────────┤
│ 프로젝트 상태 현황    │ 프로젝트 우선순위  │
│                       │ 현황                │
├───────────────────────┴─────────────────────┤
│ 프로젝트 전체 현황 / Summary                │
└─────────────────────────────────────────────┘
```

현재 Backend가 제공하는 데이터 범위 내에서 Frontend Dashboard를 구성한다.

---

# 12. KPI 영역

현재 API 기준 핵심 KPI는 다음과 같다.

## 12.1 전체 프로젝트

Field:

```text
totalProjects
```

표시 예:

```text
전체 프로젝트
12
```

---

## 12.2 최근 프로젝트

Field:

```text
recentProjects
```

표시 예:

```text
최근 프로젝트
3
```

---

## 12.3 종료 예정 프로젝트

Field:

```text
upcomingDeadlineProjects
```

표시 예:

```text
종료 예정
2
```

---

# 13. 프로젝트 상태 현황

API Field:

```text
projectStatusCount
```

형태:

```json
{
  "PLANNING": 2,
  "IN_PROGRESS": 5,
  "ON_HOLD": 1,
  "COMPLETED": 3,
  "CANCELLED": 1
}
```

Project Status Enum:

```text
PLANNING
IN_PROGRESS
ON_HOLD
COMPLETED
CANCELLED
```

Frontend에서는 다음과 같은 형태로 표시할 수 있다.

```text
프로젝트 상태

PLANNING       2
IN_PROGRESS    5
ON_HOLD        1
COMPLETED      3
CANCELLED      1
```

또는 Pie / Donut / Bar Chart 형태로 표현할 수 있다.

Chart Library 사용 여부는 Frontend 구현 단계에서 결정한다.

---

# 14. 프로젝트 우선순위 현황

API Field:

```text
projectPriorityCount
```

형태:

```json
{
  "LOW": 3,
  "MEDIUM": 4,
  "HIGH": 4,
  "CRITICAL": 1
}
```

Priority Enum:

```text
LOW
MEDIUM
HIGH
CRITICAL
```

Frontend 표시 예:

```text
프로젝트 우선순위

LOW         3
MEDIUM      4
HIGH        4
CRITICAL    1
```

---

# 15. Dashboard Data Flow

전체 흐름:

```text
Backend
ProjectRepository
      ↓
ProjectDashboardService
      ↓
ProjectDashboardResponse
      ↓
ApiResponse<ProjectDashboardResponse>
      ↓
GET /api/projects/dashboard
      ↓
Frontend API Client
      ↓
Dashboard Hook / State
      ↓
Dashboard Components
```

---

# 16. Frontend Component 구조

권장 구조:

```text
src/
└── components/
    └── dashboard/
        ├── ProjectDashboard.tsx
        ├── ProjectDashboardKpi.tsx
        ├── ProjectStatusSummary.tsx
        └── ProjectPrioritySummary.tsx
```

Page:

```text
src/
└── pages/
    └── DashboardPage.tsx
```

API:

```text
src/
└── api/
    └── projectDashboardApi.ts
```

Types:

```text
src/
└── types/
    └── projectDashboard.ts
```

---

# 17. DashboardPage 책임

`DashboardPage`는 화면의 전체 레이아웃을 담당한다.

예:

```tsx
const DashboardPage = () => {
  return (
    <MainLayout>
      <ProjectDashboard />
    </MainLayout>
  );
};
```

DashboardPage에서 직접 API 세부 처리를 수행하기보다는 Hook 또는 Dashboard Container에서 처리하는 것을 권장한다.

---

# 18. Dashboard Container

권장 구조:

```text
DashboardPage
    ↓
ProjectDashboard
    ↓
useProjectDashboard()
    ↓
getProjectDashboard()
    ↓
GET /api/projects/dashboard
```

예:

```typescript
const {
  data,
  isLoading,
  error,
} = useProjectDashboard();
```

React Query 사용 여부는 Frontend 공통 Architecture에 따라 결정한다.

---

# 19. Loading 상태

API 요청 중에는 Dashboard Loading UI를 표시한다.

```text
Dashboard
   ↓
Loading
   ↓
API Response
   ↓
Dashboard Render
```

권장 UI:

```text
┌─────────────────────────┐
│ Dashboard               │
│                         │
│        Loading...       │
│                         │
└─────────────────────────┘
```

Skeleton UI를 사용하는 경우 KPI Card 및 Chart 영역에 Skeleton을 적용할 수 있다.

---

# 20. Empty 상태

Project가 하나도 없는 경우:

```json
{
  "totalProjects": 0,
  "projectStatusCount": {},
  "projectPriorityCount": {},
  "recentProjects": 0,
  "upcomingDeadlineProjects": 0
}
```

Frontend에서는 오류가 아니라 정상적인 Empty 상태로 처리한다.

예:

```text
등록된 프로젝트가 없습니다.
```

---

# 21. Error 상태

API 호출 실패 시 Dashboard 전체를 오류 상태로 표시한다.

예:

```text
프로젝트 Dashboard 정보를 불러오지 못했습니다.

[다시 시도]
```

HTTP 401:

```text
인증이 만료되었습니다.
로그인 화면으로 이동합니다.
```

HTTP 403:

```text
Dashboard 접근 권한이 없습니다.
```

HTTP 500:

```text
서버 오류가 발생했습니다.
잠시 후 다시 시도해주세요.
```

---

# 22. API 응답 성공 여부

Frontend에서는 HTTP Status만 확인하지 않고 `success` 필드도 확인한다.

```typescript
if (!response.success) {
  throw new Error(response.message);
}
```

정상적인 응답:

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "조회 성공",
  "data": {}
}
```

---

# 23. Dashboard와 Project Domain의 관계

Dashboard에서 프로젝트 상세 정보를 직접 조회하지 않는다.

프로젝트 상세 정보가 필요한 경우 Project API를 사용한다.

```text
Dashboard
GET /api/projects/dashboard

Project List
GET /api/projects

Project Detail
GET /api/projects/{projectId}/detail
```

책임:

```text
Dashboard API
→ 집계

Project API
→ 프로젝트 원천 데이터

Project Detail API
→ 특정 프로젝트 상세 정보
```

---

# 24. 현재 Backend Project API

Dashboard와 함께 Frontend 개발에 필요한 Project API는 다음과 같다.

## 프로젝트 생성

```http
POST /api/projects
```

## 프로젝트 검색

```http
GET /api/projects
```

## 프로젝트 상세

```http
GET /api/projects/{projectId}/detail
```

## 프로젝트 수정

```http
PUT /api/projects/{projectId}
```

## 프로젝트 삭제

```http
DELETE /api/projects/{projectId}
```

Dashboard는 이 API들을 직접 대체하지 않는다.

---

# 25. Dashboard에서 Project Detail로 이동

Dashboard에서 특정 프로젝트 목록을 표시하도록 Backend API가 확장되는 경우 다음 흐름을 사용한다.

```text
Dashboard
   ↓
Project 선택
   ↓
/projects/{projectId}
   ↓
GET /api/projects/{projectId}/detail
```

현재 Dashboard API의 `recentProjects` 및 `upcomingDeadlineProjects`는 건수만 제공한다.

따라서 현재 Backend 상태에서는 Dashboard에서 해당 프로젝트의 상세 목록을 API 하나로 직접 표시할 수 없다.

필요할 경우 Project 검색 API를 별도로 호출한다.

---

# 26. 현재 API의 중요한 제약

현재 `ProjectDashboardResponse`의 다음 필드는 Count 형태이다.

```text
recentProjects
upcomingDeadlineProjects
```

즉,

```text
recentProjects = 5
```

라고 응답되더라도 어떤 프로젝트가 최근 프로젝트인지에 대한 목록 정보는 제공하지 않는다.

따라서 Frontend에서 현재 구현 가능한 것은:

```text
최근 프로젝트 수: 5
```

까지이다.

다음과 같은 목록은 별도 API 또는 Dashboard API 확장이 필요하다.

```text
최근 프로젝트
├── PMIS 구축
├── 금융결제원 인프라
└── ...
```

---

# 27. Dashboard API 확장 후보

향후 Dashboard 요구사항에 따라 다음 데이터를 추가할 수 있다.

```text
recentProjectList
upcomingDeadlineProjectList
projectProgressSummary
scheduleDelayCount
openIssueCount
openRiskCount
pendingChangeCount
```

예:

```json
{
  "totalProjects": 12,
  "projectStatusCount": {},
  "projectPriorityCount": {},
  "recentProjects": 3,
  "upcomingDeadlineProjects": 2,
  "openIssueCount": 7,
  "openRiskCount": 3,
  "scheduleDelayCount": 2
}
```

단, 해당 기능은 현재 V1 Backend 범위에 포함하지 않는다.

---

# 28. Dashboard와 WBS

현재 Dashboard API는 WBS 데이터를 직접 제공하지 않는다.

WBS 현황을 Dashboard에 표시하려면 WBS API를 별도로 호출한다.

```text
Dashboard
   │
   ├── Project Dashboard API
   │
   └── WBS API
```

WBS Tree:

```http
GET /api/projects/{projectId}/wbs/tree
```

Project WBS:

```http
GET /api/projects/{projectId}/wbs
```

따라서 WBS Progress 등의 Widget은 WBS API 연동을 통해 별도로 구현한다.

---

# 29. Dashboard와 Schedule

현재 Dashboard API는 Schedule 데이터를 제공하지 않는다.

향후 Schedule Domain 구현 후 Dashboard에서 다음 데이터를 집계할 수 있다.

```text
전체 일정
지연 일정
완료 일정
진행 일정
예정 일정
```

현재 Frontend Dashboard에서는 Schedule 관련 KPI를 임의로 생성하지 않는다.

---

# 30. Dashboard와 Issue / Risk / Change

동일한 원칙을 적용한다.

현재 Dashboard API에서 제공하지 않는 데이터는 Frontend에서 임의 계산하지 않는다.

향후 API가 제공되면 다음과 같이 연계한다.

```text
Issue
 ↓
Issue Summary

Risk
 ↓
Risk Summary

Change
 ↓
Change Summary
```

---

# 31. 상태 Label Mapping

Backend Enum을 그대로 화면에 표시하지 않고 Frontend에서 Label을 관리한다.

예:

```typescript
export const PROJECT_STATUS_LABEL: Record<string, string> = {
  PLANNING: '계획',
  IN_PROGRESS: '진행 중',
  ON_HOLD: '보류',
  COMPLETED: '완료',
  CANCELLED: '취소',
};
```

Priority:

```typescript
export const PROJECT_PRIORITY_LABEL: Record<string, string> = {
  LOW: '낮음',
  MEDIUM: '보통',
  HIGH: '높음',
  CRITICAL: '긴급',
};
```

Backend 값은 변경하지 않는다.

---

# 32. Dashboard 데이터 변환 원칙

Backend:

```text
PLANNING
IN_PROGRESS
COMPLETED
```

Frontend:

```text
계획
진행 중
완료
```

즉,

```text
API Data
   ↓
Mapper
   ↓
View Model
   ↓
Component
```

구조를 권장한다.

---

# 33. 권장 View Model

```typescript
export interface DashboardKpi {
  key: string;
  label: string;
  value: number;
}
```

예:

```typescript
[
  {
    key: 'totalProjects',
    label: '전체 프로젝트',
    value: data.totalProjects,
  },
  {
    key: 'recentProjects',
    label: '최근 프로젝트',
    value: data.recentProjects,
  },
  {
    key: 'upcomingDeadlineProjects',
    label: '종료 예정',
    value: data.upcomingDeadlineProjects,
  },
]
```

---

# 34. Chart Data 변환

API:

```json
{
  "PLANNING": 2,
  "IN_PROGRESS": 5,
  "COMPLETED": 3
}
```

Chart용 데이터:

```typescript
[
  {
    key: 'PLANNING',
    label: '계획',
    value: 2,
  },
  {
    key: 'IN_PROGRESS',
    label: '진행 중',
    value: 5,
  },
  {
    key: 'COMPLETED',
    label: '완료',
    value: 3,
  },
]
```

Backend Response를 Chart Component가 직접 해석하지 않도록 Mapper를 두는 것을 권장한다.

---

# 35. API 호출 중복 방지

Dashboard 화면 하나에서 동일 API를 여러 Component가 각각 호출하지 않는다.

잘못된 구조:

```text
KpiCard
 └── GET /api/projects/dashboard

StatusChart
 └── GET /api/projects/dashboard

PriorityChart
 └── GET /api/projects/dashboard
```

권장 구조:

```text
ProjectDashboard
 └── GET /api/projects/dashboard
        │
        ├── KPI
        ├── Status Summary
        └── Priority Summary
```

Dashboard 데이터를 한 번 조회한 후 하위 Component에 Props로 전달한다.

---

# 36. 권장 Component Data Flow

```text
ProjectDashboard
        │
        │ dashboardData
        ├───────────────┐
        ▼               ▼
ProjectDashboardKpi   ProjectStatusSummary
        │
        ▼
ProjectPrioritySummary
```

예:

```tsx
<ProjectDashboardKpi
  data={dashboardData}
/>

<ProjectStatusSummary
  data={dashboardData.projectStatusCount}
/>

<ProjectPrioritySummary
  data={dashboardData.projectPriorityCount}
/>
```

---

# 37. Dashboard Route

Frontend Route 예:

```text
/dashboard
```

또는 현재 Frontend Router 정책에 따라:

```text
/
```

를 Dashboard로 사용할 수 있다.

현재 프로젝트의 Router 설계를 우선한다.

---

# 38. 권장 화면 URL 구조

```text
/login
/dashboard
/projects
/projects/:projectId
/projects/:projectId/wbs
```

Dashboard:

```text
/dashboard
```

Project:

```text
/projects
```

Project Detail:

```text
/projects/:projectId
```

---

# 39. Dashboard UI 원칙

Dashboard는 조회 중심 화면으로 설계한다.

원칙:

- 핵심 KPI를 상단에 배치
- 상태 정보를 빠르게 파악
- 우선순위를 빠르게 파악
- 숫자와 그래프를 함께 제공
- 상세 데이터는 Project 화면으로 이동
- API에서 제공하지 않는 값을 임의 계산하지 않음
- Backend Enum을 직접 노출하지 않음

---

# 40. 권장 Dashboard Layout

```text
┌─────────────────────────────────────────────────┐
│ Project Dashboard                               │
│ 프로젝트 전체 현황                               │
├─────────────────────────────────────────────────┤
│                                                 │
│ ┌────────────┐ ┌────────────┐ ┌────────────┐   │
│ │ 전체       │ │ 최근       │ │ 종료 예정  │   │
│ │ 프로젝트   │ │ 프로젝트   │ │ 프로젝트   │   │
│ │ 12         │ │ 3          │ │ 2          │   │
│ └────────────┘ └────────────┘ └────────────┘   │
│                                                 │
├────────────────────────┬────────────────────────┤
│ 프로젝트 상태          │ 프로젝트 우선순위     │
│                        │                        │
│ 계획        2          │ 낮음        3          │
│ 진행 중     5          │ 보통        4          │
│ 보류        1          │ 높음        4          │
│ 완료        3          │ 긴급        1          │
│ 취소        1          │                        │
└────────────────────────┴────────────────────────┘
```

---

# 41. 테스트 기준

Frontend Dashboard 구현 완료 기준:

### API

- [ ] `GET /api/projects/dashboard` 호출 성공
- [ ] JWT Access Token 전달
- [ ] HTTP 200 처리
- [ ] `success` 확인
- [ ] `data` 파싱

### KPI

- [ ] 전체 프로젝트 수 표시
- [ ] 최근 프로젝트 수 표시
- [ ] 종료 예정 프로젝트 수 표시

### Status

- [ ] 프로젝트 상태별 건수 표시
- [ ] Enum Label 변환

### Priority

- [ ] 프로젝트 우선순위별 건수 표시
- [ ] Enum Label 변환

### 상태 처리

- [ ] Loading
- [ ] Empty
- [ ] Error
- [ ] Unauthorized

---

# 42. Swagger 기반 Frontend 개발 순서

```text
Swagger 확인
      ↓
ProjectDashboardResponse 확인
      ↓
TypeScript Type 작성
      ↓
API Client 작성
      ↓
Dashboard Hook 작성
      ↓
Dashboard Page 작성
      ↓
KPI Component 작성
      ↓
Status Summary 작성
      ↓
Priority Summary 작성
      ↓
Loading / Empty / Error 처리
      ↓
JWT 인증 테스트
      ↓
Browser 테스트
```

---

# 43. Frontend 개발 시 주의사항

## 43.1 Backend 필드 임의 변경 금지

Backend:

```text
totalProjects
```

Frontend에서:

```text
totalProjectCount
```

로 API Response를 직접 변경하지 않는다.

Mapper 내부에서는 변경 가능하지만 API Client Response Type은 Backend와 동일하게 유지한다.

---

## 43.2 Count와 List 혼동 금지

```text
recentProjects
```

는 현재 프로젝트 목록이 아니라 **건수**이다.

```text
recentProjects: 3
```

를 다음과 같이 해석하지 않는다.

```text
recentProjects: [
  ...
]
```

---

## 43.3 Dashboard에서 임의 계산 금지

예를 들어:

```text
totalProjects = status별 건수 합
```

처럼 Frontend에서 별도 계산하지 않는다.

Backend가 제공한 값을 기준으로 화면을 구성한다.

---

# 44. 현재 Backend와 Frontend의 역할

```text
┌───────────────────────┐
│ Backend               │
│                       │
│ Project 데이터 관리   │
│ Dashboard 집계        │
│ JWT 인증              │
└──────────┬────────────┘
           │ REST API
           ▼
┌───────────────────────┐
│ Frontend              │
│                       │
│ API 호출              │
│ 데이터 변환           │
│ KPI 표시              │
│ Chart 표시            │
│ Loading/Error 처리     │
└───────────────────────┘
```

---

# 45. V1 범위

현재 Dashboard V1에서 구현한다.

- Dashboard 페이지
- Dashboard API 연동
- 전체 프로젝트 KPI
- 최근 프로젝트 KPI
- 종료 예정 프로젝트 KPI
- 프로젝트 상태 Summary
- 프로젝트 우선순위 Summary
- Loading 상태
- Empty 상태
- Error 상태
- JWT 인증 연동
- Responsive Layout

---

# 46. V1 제외 범위

현재 Backend API에 없는 기능은 V1에서 Dashboard API를 임의 확장하지 않는다.

- 최근 프로젝트 상세 목록
- 종료 예정 프로젝트 상세 목록
- WBS Progress
- Schedule Progress
- Issue Count
- Risk Count
- Change Count
- Schedule Delay
- Resource Utilization
- Cost
- EVM
- AI Dashboard 분석
- 실시간 WebSocket Dashboard

해당 기능은 각 Domain API 구현 이후 확장한다.

---

# 47. 향후 Dashboard 확장 구조

최종적으로 PMIS Dashboard는 다음 구조를 목표로 한다.

```text
Project Dashboard
        │
        ├── Project Summary
        │
        ├── WBS Summary
        │
        ├── Schedule Summary
        │
        ├── Progress Summary
        │
        ├── Issue Summary
        │
        ├── Risk Summary
        │
        ├── Change Summary
        │
        ├── CMDB Summary
        │
        └── Report Summary
```

전체 PMIS 데이터 흐름:

```text
Project
   │
   ├── WBS
   │    └── Schedule
   │         └── Progress
   │
   ├── Issue
   ├── Risk
   ├── Change
   ├── CMDB
   └── Report
            │
            ▼
       Dashboard
            │
            ▼
          PMO
```

---

# 48. 설계 원칙 요약

1. Dashboard는 조회 및 집계 전용 Domain이다.
2. Dashboard는 Project 원천 데이터를 직접 변경하지 않는다.
3. Dashboard V1은 Backend에서 제공하는 데이터만 표시한다.
4. `GET /api/projects/dashboard`를 Dashboard의 핵심 API로 사용한다.
5. 공통 `ApiResponse<T>` 구조를 사용한다.
6. `ProjectDashboardResponse`를 Dashboard의 핵심 데이터 모델로 사용한다.
7. `totalProjects`는 전체 프로젝트 수이다.
8. `projectStatusCount`는 상태별 프로젝트 건수이다.
9. `projectPriorityCount`는 우선순위별 프로젝트 건수이다.
10. `recentProjects`는 최근 프로젝트의 **건수**이다.
11. `upcomingDeadlineProjects`는 종료 예정 프로젝트의 **건수**이다.
12. 최근/종료 예정 프로젝트의 상세 목록은 현재 API에서 제공하지 않는다.
13. Dashboard에서 제공하지 않는 데이터를 Frontend에서 임의 생성하지 않는다.
14. WBS/Schedule/Issue/Risk/Change 데이터는 해당 Domain API와 연계한다.
15. Backend Enum은 Frontend Label Mapper를 통해 표시한다.
16. 동일 Dashboard API를 여러 Component가 중복 호출하지 않는다.
17. Loading/Empty/Error 상태를 반드시 처리한다.
18. JWT Access Token을 사용하여 인증된 API를 호출한다.
19. Dashboard는 상세 업무 관리 화면으로 이동하기 위한 진입점 역할을 한다.
20. V1은 Project Summary 중심으로 구현하고 Domain 확장은 이후 단계에서 진행한다.

---

# 49. Document History

| Version | Date | Author | Description |
|---|---|---|---|
| 1.0 | 2026-08-13 | Seo Seokhyeon | Initial Project Dashboard Design based on implemented Backend API and Swagger |

---

# End of Document
