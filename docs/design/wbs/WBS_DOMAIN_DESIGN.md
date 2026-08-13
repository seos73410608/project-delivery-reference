# WBS Domain Design

> PMIS (Project Management Information System)  
> Version: **1.1**  
> Domain: **WBS (Work Breakdown Structure)**  
> Status: **Implementation Aligned / Design Confirmed**  
> Last Updated: **2026-08-13**

---

## 1. 문서 목적

본 문서는 PMIS의 WBS Domain에 대한 **구현 기준 문서**이다.

현재 Backend에 구현되어 있는 WBS API, Entity, DTO, Repository, Service 및 Swagger/OpenAPI 명세를 기준으로 작성한다.

Frontend 개발자는 본 문서와 Swagger를 함께 참고하여 WBS 화면 및 API 연동을 구현할 수 있어야 한다.

### 관련 API 문서

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## 2. WBS 개요

WBS(Work Breakdown Structure)는 프로젝트 전체 업무 범위를 계층적으로 분해하고 관리하기 위한 핵심 Domain이다.

PMIS에서는 WBS를 단순 목록이 아니라 Tree 구조로 관리한다.

```text
Project
 └── WBS
      ├── WBS
      │    ├── WBS
      │    └── WBS
      └── WBS
```

예:

```text
1 프로젝트 관리
├── 1.1 프로젝트 계획
│   ├── 1.1.1 상세 일정 계획
│   └── 1.1.2 품질 계획
└── 1.2 프로젝트 수행
```

WBS는 향후 Schedule, Progress, Issue, Risk, Change 및 Report Domain과 연결되는 프로젝트 업무 구조의 기준 데이터이다.

---

## 3. Domain 관계

```text
Project
   │
   │ 1:N
   ▼
  WBS
   │
   │ self reference
   ▼
  WBS
```

관계:

```text
Project 1 ───── N Wbs

Wbs 1 ───── N Wbs
           parent
```

최상위 WBS는 `parentId = null`이다.

---

## 4. 현재 Backend 구현 기준

현재 WBS Backend는 다음 API를 제공한다.

| Method | Endpoint | 목적 |
|---|---|---|
| GET | `/api/wbs/{id}` | WBS 단건 조회 |
| PUT | `/api/wbs/{id}` | WBS 수정 |
| DELETE | `/api/wbs/{id}` | WBS 삭제 |
| GET | `/api/projects/{projectId}/wbs` | 프로젝트 WBS 목록 조회 |
| POST | `/api/projects/{projectId}/wbs` | 프로젝트 WBS 생성 |
| PATCH | `/api/wbs/{id}/status` | WBS 상태 변경 |
| PATCH | `/api/wbs/{id}/sort-order` | WBS 정렬 순서 변경 |
| GET | `/api/wbs` | 조건 기반 WBS 검색 |
| GET | `/api/wbs/{parentId}/children` | 하위 WBS 조회 |
| GET | `/api/projects/{projectId}/wbs/tree` | WBS Tree 조회 |
| GET | `/api/projects/{projectId}/wbs/root` | Root WBS 조회 |

---

## 5. WBS Entity

| Field | Type | Nullable | Frontend 의미 |
|---|---|---:|---|
| `id` | Long | N | WBS PK |
| `projectId` | Long | N | 소속 Project ID |
| `parentId` | Long | Y | 상위 WBS ID |
| `wbsCode` | String | N | WBS 식별 코드 |
| `wbsName` | String | N | WBS 명 |
| `level` | Integer | N | 계층 Level |
| `sortOrder` | Integer | N | 동일 Parent 내 정렬 순서 |
| `status` | WbsStatus | N | WBS 상태 |
| `description` | String | Y | WBS 설명 |
| `createdAt` | DateTime | N | 생성일시 |
| `updatedAt` | DateTime | N | 수정일시 |

---

## 6. WBS Status

현재 Backend Enum:

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

Frontend는 임의의 Status 값을 생성하지 않는다.

---

## 7. WBS Tree 조회

### Endpoint

```http
GET /api/projects/{projectId}/wbs/tree
```

예:

```http
GET /api/projects/2/wbs/tree
```

### Response

```json
[
  {
    "id": 1,
    "projectId": 2,
    "parentId": null,
    "wbsCode": "1",
    "wbsName": "프로젝트 관리",
    "level": 1,
    "sortOrder": 1,
    "status": "PLANNED",
    "description": "프로젝트 전체 관리",
    "children": [
      {
        "id": 2,
        "projectId": 2,
        "parentId": 1,
        "wbsCode": "1.1",
        "wbsName": "프로젝트 계획",
        "level": 2,
        "sortOrder": 1,
        "status": "PLANNED",
        "description": "프로젝트 계획 수립",
        "children": [
          {
            "id": 3,
            "projectId": 2,
            "parentId": 2,
            "wbsCode": "1.1.1",
            "wbsName": "상세 일정 계획",
            "level": 3,
            "sortOrder": 2,
            "status": "IN_PROGRESS",
            "description": "프로젝트 상세 일정 수립",
            "children": []
          }
        ]
      }
    ]
  }
]
```

Frontend의 WBS Tree 화면은 이 API를 기본 데이터 소스로 사용할 수 있다.

---

## 8. WBS 목록 조회

```http
GET /api/projects/{projectId}/wbs
```

예:

```http
GET /api/projects/2/wbs
```

응답은 평면 `WbsResponse[]`이다.

Tree UI에는 `/tree` API 사용을 권장한다.

---

## 9. Root WBS 조회

```http
GET /api/projects/{projectId}/wbs/root
```

Root WBS 조건:

```text
projectId = 지정 프로젝트
parentId = null
```

---

## 10. Child WBS 조회

```http
GET /api/wbs/{parentId}/children
```

예:

```http
GET /api/wbs/2/children
```

응답 예:

```json
[
  {
    "id": 3,
    "projectId": 2,
    "parentId": 2,
    "wbsCode": "1.1.1",
    "wbsName": "상세 일정 계획",
    "level": 3,
    "sortOrder": 2,
    "status": "IN_PROGRESS",
    "description": "프로젝트 상세 일정 수립",
    "createdAt": "2026-08-11T16:02:33.745661",
    "updatedAt": "2026-08-11T16:18:10.170931"
  }
]
```

---

## 11. WBS 생성

### Endpoint

```http
POST /api/projects/{projectId}/wbs
```

### Request

```json
{
  "parentId": 2,
  "wbsCode": "1.1.1",
  "wbsName": "상세 일정 계획",
  "description": "프로젝트 상세 일정 수립",
  "status": "PLANNED",
  "sortOrder": 1
}
```

### Request Field

| Field | Type | Required | Description |
|---|---|---:|---|
| `parentId` | Long | N | 상위 WBS ID |
| `wbsCode` | String | N | WBS Code |
| `wbsName` | String | N | WBS 명 |
| `description` | String | N | 설명 |
| `status` | WbsStatus | N | 상태 |
| `sortOrder` | Integer | N | 정렬 순서 |

생성 결과의 `projectId`, `level`, `createdAt`, `updatedAt` 등은 Backend가 관리한다.

---

## 12. WBS 수정

### Endpoint

```http
PUT /api/wbs/{id}
```

예:

```http
PUT /api/wbs/3
```

### Request

```json
{
  "parentId": 2,
  "wbsCode": "1.1.1",
  "wbsName": "상세 일정 계획",
  "description": "프로젝트 상세 일정 수립",
  "status": "IN_PROGRESS",
  "sortOrder": 2
}
```

예를 들어 다음과 같이 수정할 수 있다.

```text
wbsName
일정 계획
→ 상세 일정 계획

description
상세 일정 계획
→ 프로젝트 상세 일정 수립

status
PLANNED
→ IN_PROGRESS

sortOrder
1
→ 2
```

수정 결과:

```json
{
  "id": 3,
  "projectId": 2,
  "parentId": 2,
  "wbsCode": "1.1.1",
  "wbsName": "상세 일정 계획",
  "level": 3,
  "sortOrder": 2,
  "status": "IN_PROGRESS",
  "description": "프로젝트 상세 일정 수립",
  "createdAt": "2026-08-11T16:02:33.745661",
  "updatedAt": "2026-08-11T16:18:10.170931"
}
```

---

## 13. 상태 변경

```http
PATCH /api/wbs/{id}/status
```

예:

```http
PATCH /api/wbs/3/status
```

상태 변경 UI에서 사용할 수 있다.

정확한 Request Body 형식은 실행 중인 Swagger의 `changeStatus` 명세를 기준으로 한다.

---

## 14. Sort Order 변경

```http
PATCH /api/wbs/{id}/sort-order
```

예:

```http
PATCH /api/wbs/3/sort-order
```

Drag & Drop 기반 정렬 UI에서 사용할 수 있다.

정확한 Request Body 형식은 실행 중인 Swagger의 `changeSortOrder` 명세를 기준으로 한다.

---

## 15. WBS 단건 조회

```http
GET /api/wbs/{id}
```

예:

```http
GET /api/wbs/3
```

응답:

```json
{
  "id": 3,
  "projectId": 2,
  "parentId": 2,
  "wbsCode": "1.1.1",
  "wbsName": "상세 일정 계획",
  "level": 3,
  "sortOrder": 2,
  "status": "IN_PROGRESS",
  "description": "프로젝트 상세 일정 수립",
  "createdAt": "2026-08-11T16:02:33.745661",
  "updatedAt": "2026-08-11T16:18:10.170931"
}
```

---

## 16. WBS 삭제

```http
DELETE /api/wbs/{id}
```

예:

```http
DELETE /api/wbs/3
```

하위 WBS가 존재하는 경우 삭제가 거부될 수 있다.

Frontend에서는 삭제 전 확인 Dialog를 권장한다.

---

## 17. WBS 검색

```http
GET /api/wbs
```

`WbsSearchRequest` Query Parameter:

| Parameter | Type | Description |
|---|---|---|
| `projectId` | Long | 프로젝트 ID |
| `keyword` | String | 검색어 |
| `status` | WbsStatus | 상태 |
| `parentId` | Long | Parent ID |
| `sortBy` | String | 정렬 필드 |
| `direction` | String | 정렬 방향 |
| `page` | Integer | 페이지 |
| `size` | Integer | 페이지 크기 |

예:

```http
GET /api/wbs?projectId=2&keyword=일정&status=IN_PROGRESS&page=0&size=20
```

---

## 18. Search Response

```json
{
  "totalPages": 1,
  "totalElements": 1,
  "size": 20,
  "content": [
    {
      "id": 3,
      "projectId": 2,
      "parentId": 2,
      "wbsCode": "1.1.1",
      "wbsName": "상세 일정 계획",
      "level": 3,
      "sortOrder": 2,
      "status": "IN_PROGRESS",
      "description": "프로젝트 상세 일정 수립",
      "createdAt": "2026-08-11T16:02:33.745661",
      "updatedAt": "2026-08-11T16:18:10.170931"
    }
  ],
  "number": 0,
  "first": true,
  "last": true,
  "numberOfElements": 1,
  "empty": false
}
```

---

## 19. DTO 구조

### WbsCreateRequest

```text
parentId
wbsCode
wbsName
description
status
sortOrder
```

### WbsUpdateRequest

```text
parentId
wbsCode
wbsName
description
status
sortOrder
```

### WbsResponse

```text
id
projectId
parentId
wbsCode
wbsName
level
sortOrder
status
description
createdAt
updatedAt
```

### WbsTreeResponse

```text
id
projectId
parentId
wbsCode
wbsName
level
sortOrder
status
description
children
```

---

## 20. Frontend TypeScript Model

```typescript
export type WbsStatus =
  | 'PLANNED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'ON_HOLD'
  | 'CANCELLED';

export interface WbsResponse {
  id: number;
  projectId: number;
  parentId: number | null;
  wbsCode: string;
  wbsName: string;
  level: number;
  sortOrder: number;
  status: WbsStatus;
  description: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface WbsTreeResponse extends WbsResponse {
  children: WbsTreeResponse[];
}
```

---

## 21. Frontend API Client 예시

```typescript
import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
});

export const getWbsTree = async (
  projectId: number
): Promise<WbsTreeResponse[]> => {
  const response = await api.get(
    `/projects/${projectId}/wbs/tree`
  );

  return response.data;
};
```

실제 PMIS Frontend에서는 공통 API Client 및 JWT 인증 구조를 사용한다.

---

## 22. Frontend 권장 Component 구조

```text
WbsPage
├── WbsToolbar
│   ├── Search
│   ├── StatusFilter
│   └── CreateButton
│
├── WbsTree
│   └── WbsTreeNode
│       ├── WbsCode
│       ├── WbsName
│       ├── StatusBadge
│       ├── SortOrder
│       └── ActionMenu
│
└── WbsDialog
    ├── Create
    └── Edit
```

---

## 23. WBS Tree UI

권장 UI:

```text
[+] 1 프로젝트 관리
    └─ [+] 1.1 프로젝트 계획
          └─ 1.1.1 상세 일정 계획
```

Node 선택:

```text
WBS 선택
   ↓
상세 정보 표시
   ↓
수정 / 상태 변경 / 삭제
```

Parent Node에는 Expand / Collapse 기능을 제공할 수 있다.

---

## 24. WBS Code

WBS Code는 업무 식별용 코드이다.

```text
1
1.1
1.1.1
1.1.2
1.2
2
2.1
```

```text
id
→ Database PK

wbsCode
→ Business Identifier
```

Frontend에서는 두 값을 동일한 개념으로 처리하지 않는다.

---

## 25. Level

```text
1       → Level 1
1.1     → Level 2
1.1.1   → Level 3
```

`level`은 Backend 관리 값이다.

Frontend는 Tree Component의 계층 구조를 우선 사용하고, 필요한 경우 `level`을 표시 또는 보조 데이터로 사용한다.

---

## 26. Sort Order

`sortOrder`는 동일 Parent 아래의 표시 순서이다.

```text
1.1  sortOrder=1
1.2  sortOrder=2
1.3  sortOrder=3
```

```text
wbsCode
→ 업무 식별

sortOrder
→ 화면 표시 순서
```

---

## 27. Project와 WBS

WBS는 반드시 Project에 소속된다.

```text
Project ID = 2

WBS
projectId = 2
```

Project 상세 화면에서 WBS를 조회할 경우:

```http
GET /api/projects/{projectId}/wbs/tree
```

를 사용한다.

---

## 28. 삭제 정책

예:

```text
1 프로젝트 관리
├── 1.1 프로젝트 계획
└── 1.2 프로젝트 수행
```

이 상태에서 `1`을 삭제하려 하면 Backend가 삭제를 거부할 수 있다.

Frontend는 삭제 실패 시 Backend 오류 메시지를 표시한다.

---

## 29. 순환 참조 방지

다음 구조는 허용하지 않는다.

```text
A
└── B
    └── C
```

C의 Parent가 다시 A가 되어 순환되는 구조:

```text
A
└── B
    └── C
        └── A
```

Frontend에서는 자기 자신 및 하위 Node를 Parent 선택 대상에서 제외하는 것을 권장한다.

최종 검증은 Backend에서 수행한다.

---

## 30. API 공통 응답 주의사항

PMIS Backend는 일부 API에서 다음 공통 응답 구조를 사용한다.

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "조회 성공",
  "data": {}
}
```

그러나 현재 WBS Controller의 API 중에는 `WbsResponse`, `WbsResponse[]` 또는 Page 형태를 직접 반환하는 Endpoint가 존재한다.

따라서 Frontend는 **Endpoint별 실제 Swagger Response Schema를 기준으로 처리**해야 한다.

모든 WBS API가 `ApiResponse<T>`라고 가정해서는 안 된다.

---

## 31. 오류 처리

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
WBS 생성 실패
→ 오류 메시지 표시

WBS 수정 실패
→ 기존 데이터 유지

WBS 삭제 실패
→ 삭제하지 않고 오류 표시
```

---

## 32. Frontend에서 임의 변경하면 안 되는 값

다음 값은 Backend가 관리한다.

```text
projectId
level
createdAt
updatedAt
```

Frontend에서 저장값을 임의로 변경하지 않는다.

---

## 33. WBS와 Schedule 책임 분리

WBS는 업무 구조를 정의하고 Schedule은 시간 정보를 관리한다.

```text
Project
   ↓
WBS
   ↓
Schedule
```

WBS에 다음 Schedule 필드를 직접 추가하지 않는다.

```text
startDate
endDate
duration
dependency
```

이는 Schedule Domain의 책임이다.

---

## 34. WBS와 Progress 책임 분리

WBS 자체에는 Progress Rate를 저장하지 않는다.

향후 구조:

```text
WBS
 ↓
Schedule / Task
 ↓
Progress
```

Dashboard에서는 해당 데이터를 집계하여 프로젝트 진행 상태를 표시한다.

---

## 35. PMIS 전체 Domain 관계

```text
Project
   │
   └── WBS
        │
        └── Schedule
              │
              └── Progress

Project
   ├── Issue
   ├── Risk
   ├── Change
   ├── CMDB
   └── Report
```

---

## 36. Swagger 기반 Frontend 개발 순서

```text
1. 로그인
   ↓
2. Access Token 확보
   ↓
3. Project 선택
   ↓
4. GET /api/projects/{projectId}/wbs/tree
   ↓
5. WBS Tree 표시
   ↓
6. WBS 생성
   ↓
7. WBS 수정
   ↓
8. 상태 변경
   ↓
9. Sort Order 변경
   ↓
10. 삭제
```

---

## 37. Frontend 구현 Checklist

### API

- [ ] WBS Tree 조회
- [ ] WBS 목록 조회
- [ ] WBS 단건 조회
- [ ] WBS 생성
- [ ] WBS 수정
- [ ] WBS 삭제
- [ ] WBS 상태 변경
- [ ] WBS Sort Order 변경
- [ ] WBS 검색
- [ ] Child 조회
- [ ] Root 조회

### UI

- [ ] WBS Tree
- [ ] Expand / Collapse
- [ ] Status Badge
- [ ] WBS 상세
- [ ] 생성 Dialog
- [ ] 수정 Dialog
- [ ] 삭제 Confirm
- [ ] 검색
- [ ] Status Filter
- [ ] Loading
- [ ] Empty State
- [ ] Error Message

---

## 38. V1 범위

- Project별 WBS 생성
- Project별 WBS 조회
- WBS 단건 조회
- WBS 수정
- WBS 삭제
- Parent / Child 계층
- WBS Level
- WBS Code
- Sort Order
- Status
- WBS 검색
- Tree 조회
- Root 조회
- Child 조회
- 기본 Validation
- 순환 참조 방지
- Child 존재 시 삭제 방지

---

## 39. V1 제외 범위

- Gantt Chart
- Calendar
- Schedule 상세 관리
- Progress 계산
- Baseline 관리
- Resource Allocation
- Cost Management
- Earned Value Management
- WBS Versioning
- WBS Approval Workflow
- AI 기반 WBS 생성

Drag & Drop UI는 Frontend에서 구현할 수 있으나 WBS Domain 자체의 핵심 기능은 아니다.

---

## 40. 최종 WBS 구조

```text
                     Project
                        │
                        │ 1:N
                        ▼
                       Wbs
                        │
              ┌─────────┴─────────┐
              │                   │
           project              parent
              │                   │
              │                   ▼
              │                  Wbs
              │
              ├── wbsCode
              ├── wbsName
              ├── description
              ├── level
              ├── sortOrder
              └── status
```

---

## 41. PMIS 업무 흐름

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

---

## 42. 설계 원칙

1. WBS는 Project의 하위 Domain이다.
2. WBS는 Project에 소속된다.
3. WBS는 Parent/Child 자기참조 구조를 가진다.
4. Root WBS의 `parentId`는 `null`이다.
5. `level`은 Backend가 관리한다.
6. `wbsCode`는 Business Identifier이다.
7. `id`는 Database PK이다.
8. `sortOrder`는 화면 정렬 목적이다.
9. WBS Status는 Backend Enum을 사용한다.
10. Child가 존재하는 WBS 삭제는 제한한다.
11. 순환 Parent 구조를 허용하지 않는다.
12. WBS와 Schedule의 책임을 분리한다.
13. WBS와 Progress의 책임을 분리한다.
14. Frontend는 실제 Swagger Response Schema를 기준으로 API를 연동한다.
15. Frontend에서 Backend 관리 필드를 임의로 변경하지 않는다.
16. WBS Tree 화면은 `/api/projects/{projectId}/wbs/tree`를 기본 데이터 소스로 사용할 수 있다.

---

## 43. Document History

| Version | Date | Author | Description |
|---|---|---|---|
| 1.0 | 2026-08-11 | Seo Seokhyeon | Initial WBS Domain Design |
| 1.1 | 2026-08-13 | Seo Seokhyeon | Actual Backend API 및 Swagger 기준으로 문서 정비, Frontend 개발 가이드 추가 |

---

# End of Document
