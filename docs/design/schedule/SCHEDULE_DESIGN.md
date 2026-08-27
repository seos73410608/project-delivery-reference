# SCHEDULE Domain Design

> PMIS (Project Management Information System)  
> Version: **1.0**  
> Domain: **Schedule**  
> Status: **Implementation Aligned / Design Confirmed**  
> Last Updated: **2026-08-27**

---

## 1. 문서 목적

본 문서는 PMIS의 **Schedule Domain에 대한 구현 기준 문서**이다.

현재 Backend에 구현되어 있는 Schedule API, Entity, DTO, Repository, Service 및 Swagger/OpenAPI 명세를 기준으로 작성한다.

Frontend 개발자는 본 문서와 Swagger를 함께 참고하여 Schedule 화면 및 API 연동을 구현할 수 있어야 한다.

### 관련 API 문서

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## 2. Schedule 개요

Schedule은 프로젝트 업무의 **시간적 계획과 실행 상태를 관리**하기 위한 핵심 Domain이다.

WBS가 프로젝트 업무를 계층적으로 정의한다면, Schedule은 해당 업무의 시작일, 종료일, 기간, 선후행 관계 및 일정 상태 등 **시간 정보**를 관리한다.

```text
Project
   │
   ▼
  WBS
   │
   ▼
Schedule
```

WBS와 Schedule의 핵심 책임은 다음과 같이 분리한다.

```text
WBS
→ 무엇을 수행하는가

Schedule
→ 언제 수행하는가
```

---

## 3. Domain 관계

```text
Project
   │
   │ 1:N
   ▼
  WBS
   │
   │ 1:N
   ▼
Schedule
```

기본 관계:

```text
Project 1 ───── N WBS
WBS 1 ───── N Schedule
```

Schedule은 WBS를 기준으로 프로젝트 업무의 시간 정보를 관리한다.

---

## 4. Schedule 핵심 책임

Schedule Domain은 다음 책임을 가진다.

- 일정 생성
- 일정 조회
- 일정 단건 조회
- 일정 수정
- 일정 삭제
- 시작일/종료일 관리
- 일정 기간 관리
- 일정 상태 관리
- WBS와 일정 연결
- 일정 검색
- 일정 정렬
- 일정 순서 관리
- 일정 Validation
- 향후 일정 기반 Progress 연계

---

## 5. Schedule Entity

현재 Backend Entity의 실제 필드를 기준으로 Frontend 모델을 확정한다.

권장 기본 구조:

| Field | Type | Nullable | Frontend 의미 |
|---|---|---:|---|
| `id` | Long | N | Schedule PK |
| `projectId` | Long | N | 소속 Project ID |
| `wbsId` | Long | N | 연결된 WBS ID |
| `scheduleName` | String | N | 일정 명 |
| `startDate` | LocalDate | N | 시작일 |
| `endDate` | LocalDate | N | 종료일 |
| `status` | ScheduleStatus | N | 일정 상태 |
| `description` | String | Y | 일정 설명 |
| `sortOrder` | Integer | N | 표시 순서 |
| `createdAt` | DateTime | N | 생성일시 |
| `updatedAt` | DateTime | N | 수정일시 |

> 실제 Backend Entity의 필드명이 위 구조와 다른 경우에는 **실행 중인 Swagger/OpenAPI 및 실제 Entity를 최종 기준**으로 한다.

---

## 6. Schedule Status

Schedule Status는 Backend Enum을 기준으로 사용한다.

권장 상태 모델:

```text
PLANNED
IN_PROGRESS
COMPLETED
ON_HOLD
CANCELLED
```

| Status | 의미 |
|---|---|
| `PLANNED` | 계획 |
| `IN_PROGRESS` | 진행 중 |
| `COMPLETED` | 완료 |
| `ON_HOLD` | 보류 |
| `CANCELLED` | 취소 |

Frontend는 Backend Enum에 없는 Status 값을 임의로 생성하지 않는다.

---

## 7. Schedule 날짜 관리

Schedule은 시작일과 종료일을 기준으로 일정 기간을 관리한다.

```text
startDate
    │
    ├──────── duration ────────┤
    │                          │
    ▼                          ▼
시작일                       종료일
```

기본 Validation:

```text
startDate <= endDate
```

다음 상태는 허용하지 않는다.

```text
startDate > endDate
```

Frontend에서도 입력 단계에서 기본 Validation을 수행할 수 있으나 최종 검증은 Backend에서 수행한다.

---

## 8. Schedule 기간

Schedule의 기간은 시작일과 종료일을 기반으로 계산할 수 있다.

```text
startDate
+
endDate
↓
Schedule Duration
```

예:

```text
startDate = 2026-08-01
endDate   = 2026-08-10

→ 10일 일정
```

기간을 별도의 영구 저장 필드로 관리할지는 Backend 구현 정책을 따른다.

Frontend는 Backend가 계산하여 제공하는 값을 우선 사용하고, 제공되지 않는 경우 화면 표시 목적에 한해 계산할 수 있다.

---

## 9. Schedule API

Schedule Backend의 기본 API 구조는 다음과 같다.

| Method | Endpoint | 목적 |
|---|---|---|
| GET | `/api/schedules/{id}` | Schedule 단건 조회 |
| PUT | `/api/schedules/{id}` | Schedule 수정 |
| DELETE | `/api/schedules/{id}` | Schedule 삭제 |
| GET | `/api/projects/{projectId}/schedules` | 프로젝트 Schedule 목록 조회 |
| POST | `/api/projects/{projectId}/schedules` | 프로젝트 Schedule 생성 |
| GET | `/api/schedules` | 조건 기반 Schedule 검색 |

> 실제 구현에 상태 변경, 정렬 순서 변경, WBS별 조회 등의 API가 추가되어 있다면 Swagger를 기준으로 본 문서를 갱신한다.

---

## 10. 프로젝트 Schedule 조회

### Endpoint

```http
GET /api/projects/{projectId}/schedules
```

예:

```http
GET /api/projects/2/schedules
```

특정 프로젝트의 Schedule 목록을 조회한다.

Frontend Schedule 화면의 기본 데이터 소스로 사용할 수 있다.

---

## 11. Schedule 생성

### Endpoint

```http
POST /api/projects/{projectId}/schedules
```

예:

```http
POST /api/projects/2/schedules
```

### Request 예시

```json
{
  "wbsId": 2,
  "scheduleName": "상세 일정 계획",
  "startDate": "2026-08-01",
  "endDate": "2026-08-10",
  "status": "PLANNED",
  "description": "프로젝트 상세 일정 수립",
  "sortOrder": 1
}
```

### Request Field

| Field | Type | Required | Description |
|---|---|---:|---|
| `wbsId` | Long | N | 연결 WBS ID |
| `scheduleName` | String | N | 일정 명 |
| `startDate` | LocalDate | N | 시작일 |
| `endDate` | LocalDate | N | 종료일 |
| `status` | ScheduleStatus | N | 일정 상태 |
| `description` | String | N | 설명 |
| `sortOrder` | Integer | N | 정렬 순서 |

생성 결과의 `id`, `projectId`, `createdAt`, `updatedAt` 등 Backend 관리 값은 Backend가 관리한다.

---

## 12. Schedule 수정

### Endpoint

```http
PUT /api/schedules/{id}
```

예:

```http
PUT /api/schedules/3
```

### Request 예시

```json
{
  "wbsId": 2,
  "scheduleName": "상세 일정 계획",
  "startDate": "2026-08-03",
  "endDate": "2026-08-12",
  "status": "IN_PROGRESS",
  "description": "프로젝트 상세 일정 수립",
  "sortOrder": 2
}
```

주요 수정 대상:

```text
scheduleName
startDate
endDate
status
description
sortOrder
```

WBS 연결 변경을 허용하는지는 실제 Backend Service의 Validation 정책을 따른다.

---

## 13. Schedule 단건 조회

### Endpoint

```http
GET /api/schedules/{id}
```

예:

```http
GET /api/schedules/3
```

### Response 예시

```json
{
  "id": 3,
  "projectId": 2,
  "wbsId": 2,
  "scheduleName": "상세 일정 계획",
  "startDate": "2026-08-03",
  "endDate": "2026-08-12",
  "status": "IN_PROGRESS",
  "description": "프로젝트 상세 일정 수립",
  "sortOrder": 2,
  "createdAt": "2026-08-11T16:02:33.745661",
  "updatedAt": "2026-08-13T16:18:10.170931"
}
```

---

## 14. Schedule 삭제

### Endpoint

```http
DELETE /api/schedules/{id}
```

예:

```http
DELETE /api/schedules/3
```

Frontend에서는 삭제 전 Confirm Dialog를 권장한다.

삭제 실패 시 Backend에서 반환하는 오류 메시지를 사용자에게 표시한다.

향후 Progress, Issue 또는 기타 Domain과 참조 관계가 추가될 경우 삭제 정책이 강화될 수 있다.

---

## 15. Schedule 검색

### Endpoint

```http
GET /api/schedules
```

조건 기반 Schedule 검색을 지원한다.

권장 Query Parameter:

| Parameter | Type | Description |
|---|---|---|
| `projectId` | Long | 프로젝트 ID |
| `wbsId` | Long | WBS ID |
| `keyword` | String | 일정명/검색어 |
| `status` | ScheduleStatus | 일정 상태 |
| `sortBy` | String | 정렬 필드 |
| `direction` | String | 정렬 방향 |
| `page` | Integer | 페이지 |
| `size` | Integer | 페이지 크기 |

예:

```http
GET /api/schedules?projectId=2&status=IN_PROGRESS&page=0&size=20
```

> 실제 Query Parameter는 실행 중인 Swagger의 `ScheduleSearchRequest` 명세를 최종 기준으로 한다.

---

## 16. Search Response

Page 기반 검색을 사용하는 경우 다음과 같은 구조를 가질 수 있다.

```json
{
  "totalPages": 1,
  "totalElements": 1,
  "size": 20,
  "content": [
    {
      "id": 3,
      "projectId": 2,
      "wbsId": 2,
      "scheduleName": "상세 일정 계획",
      "startDate": "2026-08-03",
      "endDate": "2026-08-12",
      "status": "IN_PROGRESS",
      "description": "프로젝트 상세 일정 수립",
      "sortOrder": 2,
      "createdAt": "2026-08-11T16:02:33.745661",
      "updatedAt": "2026-08-13T16:18:10.170931"
    }
  ],
  "number": 0,
  "first": true,
  "last": true,
  "numberOfElements": 1,
  "empty": false
}
```

Frontend에서는 실제 Swagger Response Schema를 기준으로 타입을 정의한다.

---

## 17. WBS와 Schedule 연결

Schedule은 WBS를 참조한다.

```text
WBS
│
├── WBS ID: 2
│   "프로젝트 계획"
│
└── Schedule
    ├── 상세 일정 계획
    └── 품질 계획
```

Schedule 생성 시 연결할 WBS를 지정한다.

Frontend에서는 Schedule 생성/수정 화면에서 WBS를 선택할 수 있도록 구성하는 것을 권장한다.

---

## 18. Frontend TypeScript Model

```typescript
export type ScheduleStatus =
  | 'PLANNED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'ON_HOLD'
  | 'CANCELLED';

export interface ScheduleResponse {
  id: number;
  projectId: number;
  wbsId: number;
  scheduleName: string;
  startDate: string;
  endDate: string;
  status: ScheduleStatus;
  description: string | null;
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
}
```

Request 모델:

```typescript
export interface ScheduleCreateRequest {
  wbsId: number;
  scheduleName: string;
  startDate: string;
  endDate: string;
  status: ScheduleStatus;
  description?: string;
  sortOrder?: number;
}

export interface ScheduleUpdateRequest {
  wbsId: number;
  scheduleName: string;
  startDate: string;
  endDate: string;
  status: ScheduleStatus;
  description?: string;
  sortOrder?: number;
}
```

실제 Backend DTO가 위 구조와 다를 경우 Backend DTO 및 Swagger를 최종 기준으로 한다.

---

## 19. Frontend API Client 예시

```typescript
import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
});

export const getSchedules = async (
  projectId: number
): Promise<ScheduleResponse[]> => {
  const response = await api.get(
    `/projects/${projectId}/schedules`
  );

  return response.data;
};
```

실제 PMIS Frontend에서는 공통 API Client 및 JWT 인증 구조를 사용한다.

---

## 20. Frontend 권장 Component 구조

```text
SchedulePage
├── ScheduleToolbar
│   ├── Search
│   ├── StatusFilter
│   ├── WbsFilter
│   └── CreateButton
│
├── ScheduleList
│   └── ScheduleRow
│       ├── WbsCode
│       ├── ScheduleName
│       ├── StartDate
│       ├── EndDate
│       ├── Duration
│       ├── StatusBadge
│       └── ActionMenu
│
├── ScheduleDetail
│   ├── Wbs
│   ├── DateRange
│   ├── Status
│   └── Description
│
└── ScheduleDialog
    ├── Create
    └── Edit
```

---

## 21. Schedule UI

기본 List UI:

```text
┌──────┬──────────────────┬────────────┬────────────┬──────────────┐
│ WBS  │ Schedule         │ Start      │ End        │ Status       │
├──────┼──────────────────┼────────────┼────────────┼──────────────┤
│ 1.1  │ 프로젝트 계획    │ 2026-08-01 │ 2026-08-10 │ PLANNED      │
│ 1.2  │ 프로젝트 수행    │ 2026-08-11 │ 2026-08-30 │ IN_PROGRESS  │
└──────┴──────────────────┴────────────┴────────────┴──────────────┘
```

Schedule 선택:

```text
Schedule 선택
   ↓
상세 정보 표시
   ↓
수정 / 삭제
```

---

## 22. Schedule Calendar / Timeline

Schedule은 향후 Timeline 또는 Gantt UI로 확장할 수 있다.

```text
              Aug
WBS          01 05 10 15 20 25 30
------------------------------------
1.1 계획     █████████
1.2 수행              █████████████
1.3 검수                         ██████
```

다만 다음 기능은 V1 Schedule Domain의 필수 범위와 분리한다.

```text
Gantt Chart
Calendar View
Dependency Visualization
Baseline
Critical Path
```

---

## 23. Schedule Sort Order

`sortOrder`는 동일한 기준 내에서 Schedule의 표시 순서를 관리한다.

```text
Schedule A sortOrder = 1
Schedule B sortOrder = 2
Schedule C sortOrder = 3
```

`sortOrder`와 날짜 순서는 서로 다른 개념이다.

```text
sortOrder
→ 화면/업무 표시 순서

startDate / endDate
→ 시간적 일정
```

---

## 24. Schedule Validation

Backend는 Schedule 생성/수정 시 기본 Validation을 수행한다.

예:

```text
projectId 존재 여부
wbsId 존재 여부
scheduleName 필수 여부
startDate 필수 여부
endDate 필수 여부
startDate <= endDate
status 유효성
sortOrder 유효성
```

Frontend도 사용자 경험을 위해 동일한 기본 Validation을 수행할 수 있다.

그러나 최종 Validation 책임은 Backend에 있다.

---

## 25. Project와 Schedule

Schedule은 Project와 WBS를 통해 프로젝트 업무와 연결된다.

```text
Project
   │
   └── WBS
        │
        └── Schedule
```

Project 상세 화면에서 Schedule을 조회하는 경우:

```http
GET /api/projects/{projectId}/schedules
```

를 기본 API로 사용할 수 있다.

---

## 26. Schedule과 Progress 책임 분리

Schedule은 일정 정보를 관리하고 Progress는 실제 업무 진행률을 관리한다.

```text
Schedule
├── startDate
├── endDate
└── status

Progress
└── progressRate
```

Schedule에 다음과 같은 Progress 정보를 직접 저장하지 않는 것을 기본 원칙으로 한다.

```text
progressRate
actualStartDate
actualEndDate
```

향후 실제 수행 결과를 별도 Progress/Task Domain에서 관리할 수 있다.

---

## 27. Schedule과 Issue / Risk / Change

Schedule은 Issue, Risk, Change Domain과 직접적인 책임을 분리한다.

```text
Project
├── WBS
│   └── Schedule
│
├── Issue
├── Risk
└── Change
```

필요한 경우 Issue/Risk/Change가 Schedule을 참조하여 일정 영향을 분석할 수 있다.

예:

```text
Issue
"서버 설치 지연"
   ↓
Schedule
"서버 설치 일정"
   ↓
End Date 변경 영향
```

영향 분석 로직은 각 Domain의 책임 범위와 API 설계에 따라 별도로 구현한다.

---

## 28. Schedule과 Dashboard

Dashboard에서는 Schedule 데이터를 집계하여 프로젝트 일정 상태를 표시할 수 있다.

예:

```text
Project Dashboard

Project Progress       72%
Schedule Progress      68%
Delayed Schedule        3
Upcoming Schedule       5
Completed Schedule     21
```

Dashboard는 Schedule 데이터를 직접 변경하지 않고 조회/집계한다.

---

## 29. API 공통 응답 주의사항

PMIS Backend는 일부 API에서 다음 공통 응답 구조를 사용한다.

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "조회 성공",
  "data": {}
}
```

그러나 Schedule Controller의 API 중에는 `ScheduleResponse`, `ScheduleResponse[]` 또는 Page 형태를 직접 반환하는 Endpoint가 존재할 수 있다.

따라서 Frontend는 **Endpoint별 실제 Swagger Response Schema를 기준으로 처리**해야 한다.

모든 Schedule API가 `ApiResponse<T>`라고 가정해서는 안 된다.

---

## 30. 오류 처리

Frontend는 최소한 다음 HTTP 상태를 처리한다.

```text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
500 Internal Server Error
```

예:

```text
Schedule 생성 실패
→ 오류 메시지 표시

Schedule 수정 실패
→ 기존 데이터 유지

Schedule 삭제 실패
→ 삭제하지 않고 오류 표시
```

---

## 31. Frontend에서 임의 변경하면 안 되는 값

다음 값은 Backend 관리 값이다.

```text
id
projectId
createdAt
updatedAt
```

또한 Backend가 계산/관리하는 값이 있다면 Frontend에서 임의로 저장하지 않는다.

예:

```text
duration
기타 서버 계산 필드
```

---

## 32. Schedule Dependency

Schedule 간 선후행 관계는 향후 PMIS의 일정 관리 고도화에 활용할 수 있다.

```text
Schedule A
    ↓
Schedule B
    ↓
Schedule C
```

예:

```text
서버 반입
   ↓
서버 설치
   ↓
OS 설치
   ↓
Middleware 설치
```

Dependency를 별도 Entity로 관리하는 경우에는 Schedule Domain의 추가 설계가 필요하다.

V1에서는 기본 일정 관리에 집중하고 Dependency 모델은 별도 설계 대상으로 분리한다.

---

## 33. Schedule Baseline

Baseline은 계획 일정과 실제 일정을 비교하기 위한 기준선이다.

예:

```text
Baseline
2026-08-01 ~ 2026-08-10

Actual
2026-08-03 ~ 2026-08-12
```

이를 통해 일정 지연을 분석할 수 있다.

Baseline 관리는 V1 Schedule Domain의 제외 범위로 둔다.

---

## 34. Schedule과 Gantt Chart 책임

Gantt Chart는 Schedule 데이터를 시각화하는 UI 기능이다.

```text
Schedule Domain
       ↓
Schedule Data
       ↓
Gantt Chart
```

따라서 Gantt Chart 자체를 별도 핵심 Domain으로 보지 않는다.

Frontend에서 Schedule 데이터를 이용하여 구현할 수 있다.

---

## 35. Swagger 기반 Frontend 개발 순서

```text
1. 로그인
   ↓
2. Access Token 확보
   ↓
3. Project 선택
   ↓
4. GET /api/projects/{projectId}/schedules
   ↓
5. Schedule List 표시
   ↓
6. WBS 선택
   ↓
7. Schedule 생성
   ↓
8. Schedule 수정
   ↓
9. 상태 변경/수정
   ↓
10. 삭제
   ↓
11. 검색 / 필터
   ↓
12. UI Validation
```

실제 API가 별도 상태 변경 Endpoint를 제공하는 경우 Swagger 기준으로 해당 API를 추가한다.

---

## 36. Frontend 구현 Checklist

### API

- [ ] Schedule 목록 조회
- [ ] Schedule 단건 조회
- [ ] Schedule 생성
- [ ] Schedule 수정
- [ ] Schedule 삭제
- [ ] Schedule 검색
- [ ] WBS 기반 Schedule 조회
- [ ] Schedule 상태 변경
- [ ] Schedule 정렬

### UI

- [ ] Schedule List
- [ ] WBS Filter
- [ ] Status Filter
- [ ] Date Range
- [ ] Duration 표시
- [ ] Status Badge
- [ ] Schedule 상세
- [ ] 생성 Dialog
- [ ] 수정 Dialog
- [ ] 삭제 Confirm
- [ ] 검색
- [ ] Loading
- [ ] Empty State
- [ ] Error Message
- [ ] Date Validation

### 향후 확장

- [ ] Calendar View
- [ ] Gantt Chart
- [ ] Drag & Drop
- [ ] Dependency
- [ ] Baseline
- [ ] Critical Path

---

## 37. V1 범위

Schedule V1의 기본 범위:

- Project별 Schedule 생성
- Project별 Schedule 조회
- Schedule 단건 조회
- Schedule 수정
- Schedule 삭제
- WBS와 Schedule 연결
- Start Date
- End Date
- Schedule Status
- Sort Order
- Schedule 검색
- 기본 Validation
- 날짜 Validation
- WBS 존재 여부 Validation

실제 구현된 API가 추가 기능을 포함한다면 해당 API는 V1 범위에 반영한다.

---

## 38. V1 제외 범위

다음 기능은 Schedule V1의 기본 범위에서 제외한다.

- Gantt Chart 고도화
- Calendar 고도화
- Schedule Dependency 고도화
- Critical Path
- Baseline Management
- Resource Allocation
- Cost Management
- Earned Value Management
- Advanced Progress Calculation
- AI 기반 일정 생성
- AI 기반 일정 지연 예측

해당 기능은 향후 별도 설계 및 구현 대상으로 관리한다.

---

## 39. Schedule Domain 최종 구조

```text
                    Project
                       │
                       │ 1:N
                       ▼
                      WBS
                       │
                       │ 1:N
                       ▼
                   Schedule
                       │
             ┌─────────┼─────────┐
             │         │         │
        scheduleName startDate endDate
             │
             ├── status
             ├── description
             └── sortOrder
```

핵심 책임:

```text
WBS
→ 업무 구조

Schedule
→ 시간 계획

Progress
→ 실제 진행률
```

---

## 40. PMIS 업무 흐름

```text
Project
   ↓
WBS
   ↓
Schedule
   ↓
Progress
   ↓
Issue / Risk / Change
   ↓
Dashboard
   ↓
Report
   ↓
Project Closure
```

Schedule은 Project 실행의 시간 기준 데이터로 활용된다.

---

## 41. 설계 원칙

1. Schedule은 Project 업무의 시간 정보를 관리하는 Domain이다.
2. Schedule은 WBS와 연결된다.
3. WBS는 업무 구조를 정의하고 Schedule은 시간 정보를 정의한다.
4. Schedule은 Start Date와 End Date를 기준으로 관리한다.
5. `startDate <= endDate`를 기본 Validation 원칙으로 한다.
6. Schedule Status는 Backend Enum을 사용한다.
7. `sortOrder`는 시간 순서가 아니라 표시/업무 순서의 의미를 가진다.
8. Project 및 WBS 연결 정보는 Backend Validation을 거친다.
9. Frontend는 실제 Swagger Response Schema를 기준으로 API를 연동한다.
10. Frontend는 Backend 관리 필드를 임의로 변경하지 않는다.
11. Schedule과 Progress의 책임을 분리한다.
12. Schedule과 Issue/Risk/Change의 책임을 분리한다.
13. Gantt/Calendar는 Schedule 데이터를 기반으로 하는 시각화 기능으로 본다.
14. Dependency와 Baseline은 별도의 고도화 설계 대상으로 관리한다.
15. Schedule Domain의 최종 API 계약은 실행 중인 Swagger/OpenAPI를 기준으로 한다.

---

## 42. Document History

| Version | Date | Author | Description |
|---|---|---|---|
| 1.0 | 2026-08-27 | Seo Seokhyeon | Initial Schedule Domain Design |

---

# End of Document
