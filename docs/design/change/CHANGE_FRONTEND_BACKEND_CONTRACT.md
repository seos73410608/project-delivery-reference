# Change Management Frontend ↔ Backend API Contract

> Version: 1.0  
> Status: Draft / Implementation Contract  
> Domain: Change Management  
> Backend: Spring Boot 4.1.0  
> Frontend: React + TypeScript + Vite  
> Last Updated: 2026-09-18

---

## 1. Purpose

본 문서는 PMIS Change Management Domain의 **Frontend와 Backend 간 API 계약(Contract)** 을 정의한다.

본 문서에서 정의한 API, Request/Response 구조, 상태 전이, Validation 및 테스트 시나리오를 기준으로 Backend와 Frontend를 각각 구현하고 통합한다.

### 기본 원칙

- Backend는 본 계약에 정의된 API와 업무 규칙을 제공한다.
- Frontend는 본 계약의 Request/Response 구조를 기준으로 API Client와 UI를 구현한다.
- Frontend는 Backend의 업무 규칙을 임의로 재정의하지 않는다.
- Backend에서 처리해야 하는 Validation과 상태 전이는 Backend가 최종 책임을 가진다.
- Swagger API 테스트 결과를 Backend 계약 검증의 기준으로 사용한다.
- Frontend E2E 테스트는 동일한 계약을 기준으로 수행한다.

---

## 2. API Base Path

```text
/api
```

---

## 3. API Inventory

| No | Method | Endpoint | Purpose |
|---|---|---|---|
| 01 | GET | `/api/changes/{id}` | 변경 요청 단건 조회 |
| 02 | GET | `/api/changes` | 변경 요청 전체 검색 |
| 03 | GET | `/api/projects/{projectId}/changes` | 프로젝트별 변경 요청 조회 |
| 04 | POST | `/api/projects/{projectId}/changes` | 변경 요청 생성 |
| 05 | PUT | `/api/changes/{id}` | 변경 요청 수정 |
| 06 | DELETE | `/api/changes/{id}` | 변경 요청 삭제 |
| 07 | PATCH | `/api/changes/{id}/status` | 변경 상태 변경 |
| 08 | PATCH | `/api/changes/{id}/approval` | 변경 승인/반려 |

---

## 4. Change Lifecycle

```text
REQUESTED
    ↓
ANALYZING
    ↓
PENDING_APPROVAL
    ↓
APPROVED
    ↓
IMPLEMENTING
    ↓
VERIFIED
    ↓
CLOSED
```

### 허용 상태 전이

| Current | Next |
|---|---|
| REQUESTED | ANALYZING |
| REQUESTED | CANCELLED |
| ANALYZING | PENDING_APPROVAL |
| ANALYZING | CANCELLED |
| PENDING_APPROVAL | APPROVED / REJECTED (Approval API 사용) |
| APPROVED | IMPLEMENTING |
| APPROVED | CANCELLED |
| IMPLEMENTING | VERIFIED |
| VERIFIED | CLOSED |

### 종료 상태

```text
CLOSED
REJECTED
CANCELLED
```

종료 상태에서는 추가적인 상태 전이를 허용하지 않는다.

---

## 5. Enum Contract

### ChangeStatus

```text
REQUESTED
ANALYZING
PENDING_APPROVAL
APPROVED
REJECTED
IMPLEMENTING
VERIFIED
CLOSED
CANCELLED
```

### ChangePriority

```text
LOW
MEDIUM
HIGH
CRITICAL
```

### ChangeType

```text
SCOPE
SCHEDULE
COST
QUALITY
TECHNICAL
INFRASTRUCTURE
SECURITY
DATA
INTERFACE
OPERATION
REQUIREMENT
OTHER
```

### ChangeImpactLevel

```text
LOW
MEDIUM
HIGH
CRITICAL
```

---

## 6. Change Key Contract

Change Key는 Backend가 자동 생성한다.

```text
CHG-001
CHG-002
CHG-003
...
```

### Rules

- 프로젝트별 순번을 관리한다.
- 최초 Change는 `CHG-001`이다.
- Frontend는 Change Key를 생성하지 않는다.
- Update API에서 Change Key를 변경할 수 없다.
- `(project_id, change_key)`는 Unique하다.

---

## 7. Create API

### Endpoint

```http
POST /api/projects/{projectId}/changes
```

### Request Body

```json
{
  "title": "금융결제원 서버 교체에 따른 운영환경 변경",
  "description": "신규 서버 교체에 따라 운영환경 설정 및 배포 절차를 변경한다.",
  "priority": "HIGH",
  "changeType": "INFRASTRUCTURE",
  "impactLevel": "HIGH",
  "requesterId": 1,
  "assigneeId": 2,
  "identifiedDate": "2026-09-18",
  "requestedDate": "2026-09-18",
  "dueDate": "2026-09-30",
  "impactAnalysis": "서버 교체에 따라 운영환경 설정 및 배포 일정에 영향이 발생할 수 있다.",
  "implementationPlan": "신규 서버 설치 후 설정 반영 및 검증을 수행한다."
}
```

### Backend Managed Fields

Frontend는 다음 필드를 Create Request에 전달하지 않는다.

```text
id
projectId
changeKey
status
approvedDate
implementedDate
verifiedDate
closedDate
createdAt
updatedAt
verificationResult
approvalComment
```

### Backend Processing

```text
status = REQUESTED
changeKey = CHG-xxx
```

---

## 8. Update API

### Endpoint

```http
PUT /api/changes/{id}
```

### Request Body

```json
{
  "title": "금융결제원 운영서버 교체에 따른 환경 변경",
  "description": "운영서버 교체에 따른 설정 및 배포 절차 변경",
  "priority": "CRITICAL",
  "changeType": "INFRASTRUCTURE",
  "impactLevel": "CRITICAL",
  "requesterId": 1,
  "assigneeId": 2,
  "identifiedDate": "2026-09-18",
  "requestedDate": "2026-09-18",
  "dueDate": "2026-10-05",
  "impactAnalysis": "운영환경 변경으로 서비스 전환 일정에 영향을 줄 수 있다.",
  "implementationPlan": "서버 설치 → 환경 설정 → 애플리케이션 배포 → 통합 테스트를 수행한다.",
  "sortOrder": 1
}
```

### Update 불가 필드

```text
id
projectId
changeKey
status
approvedDate
implementedDate
verifiedDate
closedDate
verificationResult
approvalComment
createdAt
updatedAt
```

### Update 제한 상태

```text
CLOSED
REJECTED
CANCELLED
```

---

## 9. Detail API

```http
GET /api/changes/{id}
```

### Response Example

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "정상적으로 처리되었습니다.",
  "data": {
    "id": 1,
    "projectId": 1,
    "changeKey": "CHG-001",
    "title": "금융결제원 서버 교체에 따른 운영환경 변경",
    "description": "신규 서버 교체에 따라 운영환경 설정 및 배포 절차를 변경한다.",
    "status": "REQUESTED",
    "priority": "HIGH",
    "changeType": "INFRASTRUCTURE",
    "impactLevel": "HIGH",
    "requesterId": 1,
    "assigneeId": 2,
    "identifiedDate": "2026-09-18",
    "requestedDate": "2026-09-18",
    "dueDate": "2026-09-30",
    "approvedDate": null,
    "implementedDate": null,
    "verifiedDate": null,
    "closedDate": null,
    "impactAnalysis": "서버 교체에 따라 운영환경 설정 및 배포 일정에 영향이 발생할 수 있다.",
    "implementationPlan": "신규 서버 설치 후 설정 반영 및 검증을 수행한다.",
    "verificationResult": null,
    "approvalComment": null,
    "sortOrder": null,
    "overdue": false
  }
}
```

---

## 10. Search API

### Endpoint

```http
GET /api/changes
```

### Query Parameters

| Parameter | Type | Description |
|---|---|---|
| projectId | Long | 프로젝트 ID |
| keyword | String | Change Key / 제목 / 설명 검색 |
| status | Enum | 상태 |
| priority | Enum | 우선순위 |
| changeType | Enum | 변경 유형 |
| impactLevel | Enum | 영향도 |
| requesterId | Long | 요청자 |
| assigneeId | Long | 담당자 |
| page | int | 페이지 번호 |
| size | int | 페이지 크기 |
| sortBy | String | 정렬 필드 |
| direction | ASC/DESC | 정렬 방향 |

### Example

```http
GET /api/changes?projectId=1&status=REQUESTED&priority=HIGH
```

---

## 11. Project Change API

```http
GET /api/projects/{projectId}/changes
```

예:

```http
GET /api/projects/1/changes
```

Project Detail의 Change 목록 조회에 사용한다.

---

## 12. Status API

```http
PATCH /api/changes/{id}/status
```

### Request

```json
{
  "status": "ANALYZING"
}
```

실제 상태 전이 가능 여부는 Backend가 판단한다.

---

## 13. Approval API

```http
PATCH /api/changes/{id}/approval
```

### Approve

```json
{
  "approved": true,
  "approvalComment": "변경 영향도 검토 완료. 변경을 승인한다."
}
```

결과:

```text
PENDING_APPROVAL → APPROVED
```

### Reject

```json
{
  "approved": false,
  "approvalComment": "운영 영향도가 높아 변경 요청을 반려한다."
}
```

결과:

```text
PENDING_APPROVAL → REJECTED
```

Approval API는 `PENDING_APPROVAL` 상태에서만 호출할 수 있다.

---

## 14. Delete API

```http
DELETE /api/changes/{id}
```

삭제 후 동일 Change를 조회하면:

```text
HTTP 404
CHANGE_001
```

을 반환한다.

---

## 15. Date Contract

다음 조건을 만족해야 한다.

```text
identifiedDate <= requestedDate <= dueDate
```

| Field | Meaning |
|---|---|
| identifiedDate | 변경 필요성이 최초 식별된 날짜 |
| requestedDate | 변경 요청이 실제 등록된 날짜 |
| dueDate | 변경 완료 목표 날짜 |
| approvedDate | 승인된 날짜 |
| implementedDate | 실제 구현 완료 날짜 |
| verifiedDate | 검증 완료 날짜 |
| closedDate | 종료 날짜 |

날짜 범위를 위반하면:

```text
HTTP 400
CHANGE_004
```

---

## 16. Overdue Contract

`overdue`는 Backend가 계산하여 Response에 제공한다.

조건:

```text
dueDate < today
AND
status NOT IN (CLOSED, CANCELLED, REJECTED)
```

Frontend는 자체 계산하지 않고 Backend Response의 `overdue` 값을 사용한다.

---

## 17. Error Contract

| Code | Meaning |
|---|---|
| CHANGE_001 | 변경 요청을 찾을 수 없음 |
| CHANGE_002 | 프로젝트를 찾을 수 없음 |
| CHANGE_003 | 유효하지 않은 상태 전이 |
| CHANGE_004 | 유효하지 않은 날짜 범위 |
| CHANGE_005 | 중복 Change Key |
| CHANGE_006 | 유효하지 않은 승인 요청 |

---

# 18. Backend Swagger Test Contract

## Scenario A. 정상 Lifecycle

```text
01. POST /api/projects/{projectId}/changes
        ↓
    REQUESTED
        ↓
02. PATCH /api/changes/{id}/status
    ANALYZING
        ↓
03. PATCH /api/changes/{id}/status
    PENDING_APPROVAL
        ↓
04. PATCH /api/changes/{id}/approval
    APPROVED
        ↓
05. PATCH /api/changes/{id}/status
    IMPLEMENTING
        ↓
06. PATCH /api/changes/{id}/status
    VERIFIED
        ↓
07. PATCH /api/changes/{id}/status
    CLOSED
```

## Scenario B. Reject

```text
REQUESTED
    ↓
ANALYZING
    ↓
PENDING_APPROVAL
    ↓
approval(false)
    ↓
REJECTED
```

## Scenario C. Search

```text
전체 검색
프로젝트 필터
Keyword 검색
Status 검색
Priority 검색
Change Type 검색
Impact Level 검색
복합 검색
Pagination
Sorting
```

## Scenario D. Validation

```text
필수값 누락
잘못된 날짜 범위
존재하지 않는 Change
잘못된 상태 전이
잘못된 Approval
종료 상태 Update
```

## Scenario E. Delete

```text
Create
 ↓
Delete
 ↓
Get
 ↓
404 / CHANGE_001
```

---

# 19. Frontend Integration Contract

권장 Frontend 구조:

```text
ChangePage
 ├── ChangeToolbar
 │    ├── Project Filter
 │    ├── Status Filter
 │    ├── Priority Filter
 │    ├── Change Type Filter
 │    ├── Impact Level Filter
 │    └── Keyword Search
 │
 ├── ChangeSummary
 │
 ├── ChangeList
 │    └── ChangeRow
 │
 └── ChangeDetail / ChangeDialog
```

API Client:

```text
changeApi
 ├── getChange()
 ├── searchChanges()
 ├── getProjectChanges()
 ├── createChange()
 ├── updateChange()
 ├── deleteChange()
 ├── updateChangeStatus()
 └── updateChangeApproval()
```

---

# 20. Frontend E2E Contract

```text
Change 목록 진입
    ↓
프로젝트 Change 조회
    ↓
Change 생성
    ↓
CHG-xxx 확인
    ↓
Detail 조회
    ↓
Change 수정
    ↓
REQUESTED
    ↓
ANALYZING
    ↓
PENDING_APPROVAL
    ↓
승인
    ↓
APPROVED
    ↓
IMPLEMENTING
    ↓
VERIFIED
    ↓
CLOSED
```

Reject E2E:

```text
Change 생성
    ↓
ANALYZING
    ↓
PENDING_APPROVAL
    ↓
반려
    ↓
REJECTED
```

---

# 21. Frontend / Backend Responsibility

## Frontend

```text
- Request DTO 구조 준수
- Enum 값 준수
- Backend Response 구조에 맞춘 TypeScript Type 정의
- API 호출
- Validation Error 표시
- 상태에 따른 UI 제공
- overdue Response 표시
```

## Backend

```text
- API 제공
- Request Validation
- 상태 전이 Validation
- 날짜 Validation
- Change Key 생성
- Approval 처리
- overdue 계산
- Error Code 제공
- 데이터 영속화
```

## Common

```text
Frontend와 Backend는 본 문서의 계약을 기준으로 통합한다.
계약 변경이 필요한 경우 API 구현 전에 문서를 먼저 변경한다.
```

---

# 22. Contract Change Procedure

```text
1. Contract MD 수정
        ↓
2. Backend 영향도 확인
        ↓
3. Frontend 영향도 확인
        ↓
4. Backend 구현
        ↓
5. Swagger Test
        ↓
6. Frontend 구현/수정
        ↓
7. Frontend E2E
        ↓
8. Contract Version Update
```

---

# 23. Implementation Status

| Area | Status |
|---|---|
| Change Design | DONE |
| Entity / Enum | DONE |
| Repository | DONE |
| Request DTO | DONE |
| Response DTO | DONE |
| Mapper | DONE |
| Specification | DONE |
| Service | DONE |
| Controller | DONE |
| Swagger Test | NEXT |
| Frontend API Client | TODO |
| Frontend UI | TODO |
| Frontend E2E | TODO |

---

# 24. Version History

| Version | Date | Description |
|---|---|---|
| 1.0 | 2026-09-18 | Initial Frontend ↔ Backend Change Management API Contract |
