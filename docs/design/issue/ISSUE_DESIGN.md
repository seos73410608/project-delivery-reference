# ISSUE Domain Design

> PMIS (Project Management Information System)  
> Version: **1.0**  
> Domain: **Issue Management**  
> Status: **Design Confirmed / Ready for Implementation**  
> Last Updated: **2026-09-11**

---

## 1. 문서 목적

본 문서는 PMIS의 **Issue Management Domain에 대한 설계 기준 문서**이다.

Issue Management는 프로젝트 수행 중 발생하는 문제, 장애 및 조치 사항을 등록하고 해결 상태까지 추적하는 핵심 Domain이다.

본 문서는 다음 구현의 기준으로 사용한다.

- Backend Domain 설계
- Entity / DTO / Repository / Service 설계
- REST API 설계
- Frontend TypeScript Model
- Frontend 화면 설계
- Swagger/OpenAPI 명세

---

## 2. Issue Management 개요

프로젝트 수행 중 발생하는 문제의 예시는 다음과 같다.

- 서버 설치 지연
- 계정 발급 지연
- Middleware 설치 실패
- 네트워크 연결 문제
- 인터페이스 오류
- 데이터 이관 오류
- 테스트 결함
- 외부 업체 대응 지연

Issue Management의 기본 흐름:

```text
Issue 발생
   ↓
Issue 등록
   ↓
담당자 지정
   ↓
분석 및 조치
   ↓
해결
   ↓
종료
```

---

## 3. Domain 관계

Issue는 기본적으로 Project에 소속된다.

```text
Project
   │
   │ 1:N
   ▼
 Issue
```

```text
Project 1 ───── N Issue
```

향후 WBS, Schedule, Risk, Change와 선택적으로 연결할 수 있으나 V1에서는 Project와 Issue 관계를 핵심으로 한다.

---

## 4. Issue 핵심 책임

- Issue 등록
- Issue 목록 조회
- Issue 단건 조회
- Issue 수정
- Issue 삭제
- Issue 상태 관리
- Issue Priority 관리
- Issue 담당자 관리
- Issue 발생일 관리
- Issue 해결일 관리
- Issue 검색 및 필터링
- Issue 정렬
- Validation
- 향후 일정 영향 관리

---

## 5. Issue Lifecycle

권장 Lifecycle:

```text
OPEN
 ↓
IN_PROGRESS
 ↓
RESOLVED
 ↓
CLOSED
```

추가 상태:

```text
OPEN
IN_PROGRESS
RESOLVED
CLOSED
ON_HOLD
CANCELLED
```

---

## 6. Issue Status

Backend Enum 기준:

| Status | 의미 |
|---|---|
| `OPEN` | 신규 Issue |
| `IN_PROGRESS` | 조치 진행 중 |
| `RESOLVED` | 해결 완료 |
| `CLOSED` | 최종 종료 |
| `ON_HOLD` | 보류 |
| `CANCELLED` | 취소 |

Frontend는 Backend에 없는 Status를 임의로 생성하지 않는다.

---

## 7. Issue Priority

권장 Priority:

```text
LOW
MEDIUM
HIGH
CRITICAL
```

| Priority | 의미 |
|---|---|
| `LOW` | 낮음 |
| `MEDIUM` | 보통 |
| `HIGH` | 높음 |
| `CRITICAL` | 긴급 / 프로젝트 영향 큼 |

---

## 8. Issue Entity

권장 구조:

| Field | Type | Nullable | Description |
|---|---|---:|---|
| `id` | Long | N | Issue PK |
| `projectId` | Long | N | 소속 Project ID |
| `issueKey` | String | Y | Issue 식별 Key |
| `title` | String | N | Issue 제목 |
| `description` | String | Y | 상세 내용 |
| `status` | IssueStatus | N | Issue 상태 |
| `priority` | IssuePriority | N | 중요도 |
| `assigneeId` | Long | Y | 담당자 ID |
| `reporterId` | Long | Y | 등록자 ID |
| `occurredDate` | LocalDate | Y | 실제 발생일 |
| `dueDate` | LocalDate | Y | 조치 목표일 |
| `resolvedDate` | LocalDate | Y | 해결일 |
| `sortOrder` | Integer | N | 표시 순서 |
| `createdAt` | LocalDateTime | N | 생성일시 |
| `updatedAt` | LocalDateTime | N | 수정일시 |

실제 Backend Entity가 구현되면 실제 Entity와 Swagger/OpenAPI를 최종 기준으로 한다.

---

## 9. Issue 식별 Key

예:

```text
ISSUE-001
ISSUE-002
ISSUE-003
```

프로젝트별 식별 방식도 가능하다.

```text
PMIS-ISSUE-001
```

자동 생성 여부는 Backend 정책에 따른다.

---

## 10. Issue 발생일과 등록일

두 날짜는 다른 개념이다.

```text
occurredDate
→ 실제 문제 발생일

createdAt
→ PMIS 등록일시
```

---

## 11. Issue 해결일

Issue가 해결되면 `resolvedDate`를 관리할 수 있다.

```text
Issue 발생
   ↓
조치
   ↓
RESOLVED
   ↓
resolvedDate 기록
```

자동 설정 여부는 Backend Service 정책에 따른다.

---

## 12. Issue API

권장 기본 API:

| Method | Endpoint | 목적 |
|---|---|---|
| GET | `/api/issues/{id}` | Issue 단건 조회 |
| PUT | `/api/issues/{id}` | Issue 수정 |
| DELETE | `/api/issues/{id}` | Issue 삭제 |
| GET | `/api/projects/{projectId}/issues` | 프로젝트 Issue 목록 |
| POST | `/api/projects/{projectId}/issues` | Issue 생성 |
| GET | `/api/issues` | Issue 검색 |
| PATCH | `/api/issues/{id}/status` | 상태 변경 |

실제 구현 API는 Swagger/OpenAPI를 최종 기준으로 한다.

---

## 13. 프로젝트 Issue 조회

### Endpoint

```http
GET /api/projects/{projectId}/issues
```

예:

```http
GET /api/projects/2/issues
```

특정 프로젝트의 Issue 목록을 조회한다.

---

## 14. Issue 생성

### Endpoint

```http
POST /api/projects/{projectId}/issues
```

### Request 예시

```json
{
  "title": "서버 설치 일정 지연",
  "description": "서버 반입 일정 변경으로 설치 일정이 지연되었습니다.",
  "priority": "HIGH",
  "status": "OPEN",
  "assigneeId": 10,
  "occurredDate": "2026-09-10",
  "dueDate": "2026-09-15",
  "sortOrder": 1
}
```

### Request Field

| Field | Type | Required | Description |
|---|---|---:|---|
| `title` | String | Y | Issue 제목 |
| `description` | String | N | 상세 내용 |
| `priority` | IssuePriority | Y | 중요도 |
| `status` | IssueStatus | Y | 상태 |
| `assigneeId` | Long | N | 담당자 |
| `occurredDate` | LocalDate | N | 발생일 |
| `dueDate` | LocalDate | N | 목표일 |
| `sortOrder` | Integer | N | 표시 순서 |

Backend 관리 값은 Request에 포함하지 않는다.

```text
id
projectId
createdAt
updatedAt
```

---

## 15. Issue 수정

### Endpoint

```http
PUT /api/issues/{id}
```

### Request 예시

```json
{
  "title": "서버 설치 일정 지연",
  "description": "서버 반입 완료 후 설치 일정 재조정 중입니다.",
  "priority": "HIGH",
  "status": "IN_PROGRESS",
  "assigneeId": 10,
  "occurredDate": "2026-09-10",
  "dueDate": "2026-09-17",
  "sortOrder": 1
}
```

---

## 16. Issue 상태 변경

### Endpoint

```http
PATCH /api/issues/{id}/status
```

### Request

```json
{
  "status": "RESOLVED"
}
```

권장 흐름:

```text
OPEN → IN_PROGRESS → RESOLVED → CLOSED
```

실제 상태 전이 규칙은 Backend Service에서 관리한다.

---

## 17. Issue 단건 조회

### Endpoint

```http
GET /api/issues/{id}
```

### Response 예시

```json
{
  "id": 3,
  "projectId": 2,
  "issueKey": "ISSUE-003",
  "title": "서버 설치 일정 지연",
  "description": "서버 반입 일정 변경으로 설치 일정이 지연되었습니다.",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "assigneeId": 10,
  "reporterId": 5,
  "occurredDate": "2026-09-10",
  "dueDate": "2026-09-17",
  "resolvedDate": null,
  "sortOrder": 1,
  "createdAt": "2026-09-11T10:00:00",
  "updatedAt": "2026-09-11T14:30:00"
}
```

---

## 18. Issue 삭제

### Endpoint

```http
DELETE /api/issues/{id}
```

Frontend에서는 Confirm Dialog를 권장한다.

```text
삭제하시겠습니까?

[취소] [삭제]
```

---

## 19. Issue 검색

### Endpoint

```http
GET /api/issues
```

권장 Query Parameter:

| Parameter | Type | Description |
|---|---|---|
| `projectId` | Long | 프로젝트 ID |
| `keyword` | String | 제목/내용 검색 |
| `status` | IssueStatus | 상태 |
| `priority` | IssuePriority | 중요도 |
| `assigneeId` | Long | 담당자 |
| `page` | Integer | 페이지 |
| `size` | Integer | 페이지 크기 |
| `sortBy` | String | 정렬 기준 |
| `direction` | String | 정렬 방향 |

예:

```http
GET /api/issues?projectId=2&priority=HIGH&status=OPEN&page=0&size=20
```

---

## 20. Search Response

Page 기반 검색 예시:

```json
{
  "totalPages": 1,
  "totalElements": 2,
  "size": 20,
  "content": [],
  "number": 0,
  "first": true,
  "last": true,
  "numberOfElements": 0,
  "empty": true
}
```

실제 Response Schema는 Swagger/OpenAPI를 기준으로 한다.

---

## 21. Issue 담당자

Issue에는 담당자를 지정할 수 있다.

```text
Issue
 ├── Title
 ├── Status
 ├── Priority
 └── Assignee
```

담당자 변경은 Issue 수정 또는 별도 API로 구현할 수 있다.

---

## 22. Issue Validation

Backend 기본 Validation:

```text
projectId 존재 여부
title 필수 여부
title 길이
status 유효성
priority 유효성
assigneeId 유효성
날짜 유효성
sortOrder 유효성
```

날짜 Validation 예:

```text
occurredDate <= dueDate
```

최종 Validation 책임은 Backend에 있다.

---

## 23. Issue와 WBS

향후 특정 WBS와 연결할 수 있다.

```text
WBS
1.2 인프라 구축
   ↓
Issue
서버 설치 실패
```

V1에서는 WBS 연결을 필수로 하지 않는다.

---

## 24. Issue와 Schedule

예:

```text
Issue
서버 반입 지연
   ↓
Schedule
서버 설치 일정
   ↓
일정 지연
```

하지만 V1에서 Issue가 Schedule을 직접 수정하지 않는다.

```text
Issue → 문제 관리
Schedule → 일정 관리
```

---

## 25. Issue와 Risk

```text
Risk
→ 아직 발생하지 않은 잠재 문제

Issue
→ 이미 발생한 실제 문제
```

예:

```text
Risk: 서버 반입 지연 가능성
   ↓ 실제 발생
Issue: 서버 반입 지연
```

향후 Risk → Issue 전환 기능을 추가할 수 있다.

---

## 26. Issue와 Change

```text
Issue 발생
   ↓
분석
   ↓
기존 계획 변경 필요
   ↓
Change Request
```

V1에서는 두 Domain의 책임을 분리한다.

---

## 27. Issue와 Dashboard

Dashboard 집계 예:

```text
Total Issues       12
Open Issues         4
In Progress         3
Critical Issues     1
Resolved Issues     8
```

Priority별 집계도 가능하다.

Dashboard는 Issue 데이터를 직접 수정하지 않는다.

---

## 28. Overdue Issue

`dueDate`를 사용하는 경우 지연 Issue를 계산할 수 있다.

```text
dueDate < today

AND

status != RESOLVED

AND

status != CLOSED
```

V1 Entity에 `overdue`를 저장하지 않고 조회/집계 시 계산하는 것을 권장한다.

---

## 29. Frontend TypeScript Model

```typescript
export type IssueStatus =
  | 'OPEN'
  | 'IN_PROGRESS'
  | 'RESOLVED'
  | 'CLOSED'
  | 'ON_HOLD'
  | 'CANCELLED';

export type IssuePriority =
  | 'LOW'
  | 'MEDIUM'
  | 'HIGH'
  | 'CRITICAL';

export interface IssueResponse {
  id: number;
  projectId: number;
  issueKey?: string | null;
  title: string;
  description: string | null;
  status: IssueStatus;
  priority: IssuePriority;
  assigneeId: number | null;
  reporterId: number | null;
  occurredDate: string | null;
  dueDate: string | null;
  resolvedDate: string | null;
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
}
```

---

## 30. Create / Update Request

```typescript
export interface IssueCreateRequest {
  title: string;
  description?: string;
  status: IssueStatus;
  priority: IssuePriority;
  assigneeId?: number;
  occurredDate?: string;
  dueDate?: string;
  sortOrder?: number;
}

export interface IssueUpdateRequest {
  title: string;
  description?: string;
  status: IssueStatus;
  priority: IssuePriority;
  assigneeId?: number;
  occurredDate?: string;
  dueDate?: string;
  sortOrder?: number;
}

export interface IssueStatusUpdateRequest {
  status: IssueStatus;
}
```

---

## 31. Frontend Component 구조

```text
IssuePage

├── IssueToolbar
│   ├── Search
│   ├── StatusFilter
│   ├── PriorityFilter
│   └── CreateButton
│
├── IssueSummary
│
├── IssueList
│   └── IssueRow
│       ├── IssueKey
│       ├── PriorityBadge
│       ├── Title
│       ├── Assignee
│       ├── StatusBadge
│       └── ActionMenu
│
├── IssueDetail
│
└── IssueDialog
    ├── Create
    └── Edit
```

---

## 32. Issue UI

```text
┌───────────┬──────────┬──────────────────────────┬─────────────┬──────────────┐
│ Key       │ Priority │ Issue                    │ Assignee    │ Status       │
├───────────┼──────────┼──────────────────────────┼─────────────┼──────────────┤
│ ISSUE-001 │ HIGH     │ 서버 설치 일정 지연       │ 담당자 A    │ OPEN         │
│ ISSUE-002 │ CRITICAL │ Middleware 설치 실패      │ 담당자 B    │ IN_PROGRESS  │
└───────────┴──────────┴──────────────────────────┴─────────────┴──────────────┘
```

---

## 33. Issue Detail

```text
Issue Detail

────────────────────

Issue Key
ISSUE-003

Title
서버 설치 일정 지연

Priority
HIGH

Status
IN_PROGRESS

Assignee
인프라 담당자

Occurred Date
2026-09-10

Due Date
2026-09-17

Description
서버 반입 일정 변경으로
설치 일정이 지연되었습니다.
```

---

## 34. 생성 흐름

```text
Project 선택
   ↓
Issue 메뉴
   ↓
Create
   ↓
입력 및 Validation
   ↓
POST /api/projects/{projectId}/issues
   ↓
목록 Refresh
```

---

## 35. 수정 흐름

```text
Issue 선택
   ↓
상세 조회
   ↓
Edit
   ↓
Validation
   ↓
PUT /api/issues/{id}
   ↓
화면 Refresh
```

---

## 36. 삭제 흐름

```text
Issue 선택
   ↓
Delete
   ↓
Confirm
   ↓
DELETE /api/issues/{id}
   ↓
Issue List Refresh
```

---

## 37. API 공통 응답 주의사항

PMIS Backend는 다음과 같은 공통 응답을 사용할 수 있다.

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "조회 성공",
  "data": {}
}
```

그러나 실제 Controller 구현에 따라 DTO 또는 Page 객체를 직접 반환할 수 있다.

Frontend는 반드시 다음을 기준으로 구현한다.

```text
Swagger/OpenAPI

+

실제 Backend DTO
```

---

## 38. 오류 처리

최소 처리 대상:

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
생성 실패 → 오류 메시지 표시
수정 실패 → 기존 데이터 유지
삭제 실패 → 삭제하지 않고 오류 표시
```

---

## 39. Backend 관리 필드

Frontend에서 임의 변경하지 않는 값:

```text
id
projectId
issueKey
reporterId
createdAt
updatedAt
```

`reporterId`는 JWT 로그인 사용자 기준으로 Backend에서 자동 설정하는 방식을 권장한다.

---

## 40. Issue History

향후 Audit 기능:

```text
Issue 생성
Issue 수정
Priority 변경
Status 변경
담당자 변경
해결 처리
종료 처리
```

V1에서는 기본 CRUD에 집중한다.

---

## 41. Issue Comment

향후 확장:

```text
Issue
 ├── Comment #1
 ├── Comment #2
 └── Comment #3
```

Comment Domain은 V1에서 제외한다.

---

## 42. Issue Attachment

향후 증적 파일 연결:

```text
Issue
 ├── error-log.txt
 ├── install-result.xlsx
 └── screenshot.png
```

Attachment는 공통 File Management Domain과 연결하는 것을 권장한다.

V1에서는 제외한다.

---

## 43. Swagger 기반 Frontend 개발 순서

```text
1. 로그인
   ↓
2. Access Token 확보
   ↓
3. Project 선택
   ↓
4. GET /api/projects/{projectId}/issues
   ↓
5. Issue List 표시
   ↓
6. Filter 적용
   ↓
7. Issue 생성
   ↓
8. Issue 상세 조회
   ↓
9. Issue 수정
   ↓
10. Status 변경
   ↓
11. 삭제
   ↓
12. 검색
   ↓
13. Error 처리
```

---

## 44. Frontend 구현 Checklist

### API

- [ ] Project Issue 목록 조회
- [ ] Issue 단건 조회
- [ ] Issue 생성
- [ ] Issue 수정
- [ ] Issue 삭제
- [ ] Issue 검색
- [ ] Issue 상태 변경
- [ ] Priority Filter
- [ ] Status Filter

### UI

- [ ] Issue List
- [ ] Issue Summary
- [ ] Keyword Search
- [ ] Status Filter
- [ ] Priority Filter
- [ ] Issue Detail
- [ ] Create Dialog
- [ ] Edit Dialog
- [ ] Delete Confirm
- [ ] Status Badge
- [ ] Priority Badge
- [ ] Loading
- [ ] Empty State
- [ ] Error Message

### 향후 확장

- [ ] Issue Comment
- [ ] Issue Attachment
- [ ] Issue History
- [ ] Issue Schedule Impact
- [ ] Risk → Issue 전환
- [ ] Issue → Change Request
- [ ] Notification
- [ ] Overdue Alert

---

## 45. Backend 권장 구조

```text
issue

├── controller
│   └── IssueController
│
├── service
│   └── IssueService
│
├── repository
│   └── IssueRepository
│
├── entity
│   └── Issue
│
├── dto
│   ├── IssueCreateRequest
│   ├── IssueUpdateRequest
│   ├── IssueSearchRequest
│   ├── IssueStatusUpdateRequest
│   └── IssueResponse
│
└── enums
    ├── IssueStatus
    └── IssuePriority
```

---

## 46. Service 핵심 책임

```text
createIssue()

getIssue()

getProjectIssues()

searchIssues()

updateIssue()

updateIssueStatus()

deleteIssue()
```

---

## 47. Repository 역할

기본 조회:

```text
findById()

findByProjectId()
```

검색 확장:

```text
projectId
status
priority
assigneeId
keyword
```

복잡한 검색은 프로젝트의 기존 구현 패턴을 따른다.

---

## 48. V1 범위

- Project별 Issue 생성
- Project별 Issue 조회
- Issue 단건 조회
- Issue 수정
- Issue 삭제
- Issue Status
- Issue Priority
- Issue 담당자
- Issue 발생일
- Issue 목표일
- Issue 해결일
- Issue 검색
- Issue Filter
- 기본 Validation
- Status 변경

---

## 49. V1 제외 범위

- Issue Comment
- Issue Attachment
- Issue History
- Notification
- Email Notification
- Workflow Engine
- SLA Management
- Escalation
- Issue Schedule Impact
- Issue Cost Impact
- Risk 자동 전환
- Change Request 자동 생성
- AI 기반 Issue 분류
- AI 기반 해결 방안 추천

---

## 50. Issue Domain 최종 구조

```text
                    Project
                       │
                       │ 1:N
                       ▼
                     Issue
                       │
       ┌───────────────┼────────────────┐
       │               │                │
     Status         Priority          Assignee
       │
       ├── Title
       ├── Description
       ├── Occurred Date
       ├── Due Date
       ├── Resolved Date
       └── Sort Order
```

---

## 51. PMIS 업무 흐름

```text
Project
   ↓
WBS
   ↓
Schedule
   ↓
Progress
   ↓
Issue
   ↓
Risk
   ↓
Change
   ↓
Dashboard
   ↓
Report
   ↓
Project Closure
```

Issue는 프로젝트 수행 중 발생한 **실제 문제를 관리하는 Domain**이다.

---

## 52. 설계 원칙

1. Issue는 프로젝트 수행 중 발생한 실제 문제를 관리한다.
2. Issue는 Project에 소속된다.
3. Issue와 Risk의 책임을 분리한다.
4. Risk는 잠재 문제이고 Issue는 이미 발생한 문제이다.
5. Issue와 Schedule의 책임을 분리한다.
6. Schedule은 시간 계획을 관리하고 Issue는 문제를 관리한다.
7. Issue Status는 Backend Enum으로 관리한다.
8. Issue Priority는 Backend Enum으로 관리한다.
9. Frontend는 Backend Enum에 없는 값을 임의로 생성하지 않는다.
10. 최종 Validation 책임은 Backend에 있다.
11. Frontend는 UX를 위한 기본 Validation을 수행할 수 있다.
12. Backend 관리 필드는 Frontend에서 임의로 변경하지 않는다.
13. 검색 조건은 Swagger/OpenAPI를 최종 기준으로 한다.
14. API 계약은 실제 Backend DTO와 Swagger/OpenAPI를 기준으로 한다.
15. Comment, Attachment, History는 V1 이후 확장 기능으로 분리한다.
16. Issue의 일정 영향은 V1에서 직접 관리하지 않는다.
17. Dashboard는 Issue 데이터를 조회 및 집계하며 직접 수정하지 않는다.

---

## 53. Document History

| Version | Date | Author | Description |
|---|---|---|---|
| 1.0 | 2026-09-11 | Seo Seokhyeon | Initial Issue Management Domain Design |

---

# End of Document
