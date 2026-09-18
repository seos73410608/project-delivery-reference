# CHANGE Domain Design

> PMIS (Project Management Information System)
> Version: **1.0**
> Domain: **Change Management**
> Status: **Design Confirmed / Ready for Implementation**
> Last Updated: **2026-09-18**

---

## 1. 문서 목적

본 문서는 PMIS의 **Change Management Domain에 대한 설계 기준 문서**이다.

Change Management는 프로젝트 수행 중 발생하는 요구사항, 일정, 범위, 비용, 품질, 기술 및 업무 변경 요청을 등록하고 영향도를 분석하며 승인 및 반영 상태를 추적하는 핵심 Domain이다.

본 문서는 다음 구현의 기준으로 사용한다.

* Backend Domain 설계
* Entity / DTO / Repository / Service 설계
* REST API 설계
* Frontend TypeScript Model
* Frontend 화면 설계
* Swagger/OpenAPI 명세
* Change Approval 관리
* Change Impact Analysis 관리
* Change History 관리

---

## 2. Change Management 개요

프로젝트 수행 중 다음과 같은 변경이 발생할 수 있다.

* 고객 요구사항 변경
* 개발 범위 변경
* 시스템 기능 추가
* 시스템 기능 삭제
* 서버 사양 변경
* Middleware 변경
* 데이터 이관 범위 변경
* 인터페이스 변경
* 일정 변경
* 납품 일정 변경
* 테스트 범위 변경
* 운영 환경 변경
* 보안 정책 변경
* 업무 프로세스 변경
* 구축 방법 변경
* 외부 업체 작업 범위 변경

Change Management의 기본 흐름:

```text
변경 필요 발생
    ↓
Change Request 등록
    ↓
변경 내용 검토
    ↓
Impact Analysis
    ↓
일정 / 비용 / 범위 / 품질 영향 분석
    ↓
승인 요청
    ↓
Approval
    ↓
승인 / 반려
    ↓
변경 반영
    ↓
검증
    ↓
Change 종료
```

---

## 3. Domain 관계

Change는 기본적으로 Project에 소속된다.

```text
Project
   │
   │ 1:N
   ▼
Change
```

```text
Project 1 ───── N Change
```

향후 WBS, Schedule, Issue, Risk, Evidence와 선택적으로 연결할 수 있으나 V1에서는 Project와 Change 관계를 핵심으로 한다.

---

## 4. Change 핵심 책임

Change Management의 핵심 책임은 다음과 같다.

* Change Request 등록
* Change 목록 조회
* Change 단건 조회
* Change 수정
* Change 삭제
* Change 상태 관리
* Change Priority 관리
* Change Type 관리
* Change Requester 관리
* Change Assignee 관리
* Change 요청일 관리
* Change 목표일 관리
* Change 영향 분석
* Change 승인 요청
* Change 승인 상태 관리
* Change 승인 의견 관리
* Change 반영 상태 관리
* Change 검색 및 필터링
* Change 정렬
* Pagination
* Validation
* Change History 관리
* 향후 Risk / Issue / WBS / Schedule 연계

---

## 5. Change Lifecycle

권장 Lifecycle:

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

반려 또는 취소가 필요한 경우:

```text
PENDING_APPROVAL
    ↓
REJECTED
```

또는:

```text
REQUESTED
    ↓
CANCELLED
```

V1에서는 다음 Backend Enum을 기준으로 관리하는 것을 권장한다.

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

Frontend는 Backend에 정의되지 않은 Status를 임의로 생성하지 않는다.

---

## 6. Change Status

권장 Backend Enum 기준:

| Status             | 의미                 |
| ------------------ | ------------------ |
| `REQUESTED`        | 변경 요청이 등록된 상태      |
| `ANALYZING`        | 변경 영향도를 분석하는 상태    |
| `PENDING_APPROVAL` | 승인 대기 상태           |
| `APPROVED`         | 변경 요청이 승인된 상태      |
| `REJECTED`         | 변경 요청이 반려된 상태      |
| `IMPLEMENTING`     | 승인된 변경을 실제 반영하는 상태 |
| `VERIFIED`         | 변경 반영 결과를 검증한 상태   |
| `CLOSED`           | 변경 관리가 종료된 상태      |
| `CANCELLED`        | 변경 요청이 취소된 상태      |

권장 기본 흐름:

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

반려:

```text
PENDING_APPROVAL → REJECTED
```

취소:

```text
REQUESTED / ANALYZING / PENDING_APPROVAL → CANCELLED
```

실제 상태 전이 규칙은 Backend Service에서 관리한다.

---

## 7. Change Priority

Change의 관리 우선순위를 다음과 같이 정의한다.

```text
LOW
MEDIUM
HIGH
CRITICAL
```

| Priority   | 의미                                      |
| ---------- | --------------------------------------- |
| `LOW`      | 프로젝트 영향이 낮은 변경                          |
| `MEDIUM`   | 일반적인 관리가 필요한 변경                         |
| `HIGH`     | 일정 / 품질 / 범위 등에 상당한 영향을 줄 수 있는 변경       |
| `CRITICAL` | 프로젝트 핵심 일정 / 목표 / 운영에 중대한 영향을 줄 수 있는 변경 |

Priority는 Change의 관리 우선순위이며 Impact와 별도로 관리한다.

---

## 8. Change Type

변경 유형을 다음과 같이 관리한다.

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

| Type             | 의미         |
| ---------------- | ---------- |
| `SCOPE`          | 프로젝트 범위 변경 |
| `SCHEDULE`       | 일정 변경      |
| `COST`           | 비용 변경      |
| `QUALITY`        | 품질 기준 변경   |
| `TECHNICAL`      | 기술 변경      |
| `INFRASTRUCTURE` | 인프라 변경     |
| `SECURITY`       | 보안 관련 변경   |
| `DATA`           | 데이터 관련 변경  |
| `INTERFACE`      | 인터페이스 변경   |
| `OPERATION`      | 운영 관련 변경   |
| `REQUIREMENT`    | 요구사항 변경    |
| `OTHER`          | 기타 변경      |

V1에서는 하나의 Change에 하나의 Change Type을 지정하는 것을 기본으로 한다.

향후 다중 유형이 필요한 경우 별도 관계 구조로 확장할 수 있다.

---

## 9. Change Impact

Change가 프로젝트에 미치는 영향도를 관리한다.

```text
LOW
MEDIUM
HIGH
CRITICAL
```

영향 분석 대상:

```text
Scope
Schedule
Cost
Quality
Technical
Infrastructure
Operation
```

예:

```text
Change

서버 사양 변경

    ↓

Scope       LOW
Schedule    MEDIUM
Cost        HIGH
Quality     LOW
Technical   HIGH
```

V1에서는 영향 분석 결과를 별도 정량 점수로 저장하지 않고 Change의 Impact Analysis 정보로 관리한다.

---

## 10. Change Approval

Change는 프로젝트 통제 절차에 따라 승인 여부를 관리할 수 있다.

기본 흐름:

```text
Change Request
    ↓
Impact Analysis
    ↓
Approval Request
    ↓
Reviewer
    ↓
APPROVED / REJECTED
```

승인 정보:

* Approval Status
* Approver
* Approval Date
* Approval Comment

V1에서는 단일 승인자를 기준으로 구현할 수 있다.

향후 다단계 승인으로 확장할 수 있다.

```text
Requester
    ↓
PM
    ↓
Project Manager
    ↓
Customer
```

---

## 11. Change History

Change의 주요 상태 및 정보 변경 이력을 관리할 수 있다.

예:

```text
Change 생성
    ↓
Status 변경
    ↓
Priority 변경
    ↓
Impact 변경
    ↓
Assignee 변경
    ↓
Impact Analysis 수정
    ↓
Approval 요청
    ↓
Approval
    ↓
Implementation
    ↓
Verification
    ↓
Close
```

V1에서는 기본 Change Entity의 `createdAt`, `updatedAt`을 사용하고 상세 History Domain은 별도 확장 대상으로 분리할 수 있다.

향후:

```text
ChangeHistory
```

Domain을 추가한다.

---

## 12. Change Entity

권장 구조:

| Field                | Type              | Nullable | Description   |
| -------------------- | ----------------- | -------: | ------------- |
| `id`                 | Long              |        N | Change PK     |
| `projectId`          | Long              |        N | 소속 Project ID |
| `changeKey`          | String            |        Y | Change 식별 Key |
| `title`              | String            |        N | 변경 요청 제목      |
| `description`        | String            |        Y | 변경 요청 상세 내용   |
| `status`             | ChangeStatus      |        N | Change 상태     |
| `priority`           | ChangePriority    |        N | 관리 우선순위       |
| `changeType`         | ChangeType        |        N | 변경 유형         |
| `impactLevel`        | ChangeImpactLevel |        N | 종합 영향도        |
| `requesterId`        | Long              |        Y | 요청자           |
| `assigneeId`         | Long              |        Y | 담당자           |
| `identifiedDate`     | LocalDate         |        Y | 변경 필요 식별일     |
| `requestedDate`      | LocalDate         |        Y | 변경 요청일        |
| `dueDate`            | LocalDate         |        Y | 목표 완료일        |
| `approvedDate`       | LocalDate         |        Y | 승인일           |
| `implementedDate`    | LocalDate         |        Y | 반영일           |
| `verifiedDate`       | LocalDate         |        Y | 검증일           |
| `closedDate`         | LocalDate         |        Y | 종료일           |
| `impactAnalysis`     | String            |        Y | 영향 분석         |
| `implementationPlan` | String            |        Y | 반영 계획         |
| `verificationResult` | String            |        Y | 검증 결과         |
| `approvalComment`    | String            |        Y | 승인 의견         |
| `sortOrder`          | Integer           |        N | 표시 순서         |
| `createdAt`          | LocalDateTime     |        N | 생성일시          |
| `updatedAt`          | LocalDateTime     |        N | 수정일시          |

실제 Backend Entity가 구현되면 실제 Entity와 Swagger/OpenAPI를 최종 기준으로 한다.

---

## 13. Change 식별 Key

Change를 사람이 쉽게 식별할 수 있도록 Key를 사용한다.

예:

```text
CHG-001
CHG-002
CHG-003
```

프로젝트별 식별 방식:

```text
Project A
├── CHG-001
├── CHG-002
└── CHG-003

Project B
├── CHG-001
├── CHG-002
└── CHG-003
```

Issue / Risk Domain과 동일하게 Project-scoped Key 정책을 적용한다.

Backend가 자동 생성하며 Frontend에서는 Change Key를 입력하지 않는다.

권장 형식:

```text
CHG-%03d
```

예:

```text
CHG-001
CHG-002
CHG-003
```

권장 DB 제약:

```text
UNIQUE(project_id, change_key)
```

---

## 14. Change 식별일과 요청일

두 날짜는 다른 개념이다.

```text
identifiedDate

→ 변경 필요성이 처음 식별된 날짜
```

```text
requestedDate

→ 실제 Change Request가 등록된 날짜
```

변경 필요성을 먼저 발견하고 실제 Change Request를 나중에 등록할 수 있으므로 별도로 관리한다.

---

## 15. Change 대응 기한

`dueDate`는 변경 작업을 완료해야 하는 목표일이다.

```text
Change Request
    ↓
Impact Analysis
    ↓
Approval
    ↓
Implementation
    ↓
dueDate
    ↓
Verification
```

V1에서는 `dueDate`를 기반으로 지연 Change를 계산할 수 있다.

---

## 16. Change 승인일

Change가 승인된 경우 `approvedDate`를 관리한다.

```text
PENDING_APPROVAL
    ↓
APPROVED
    ↓
approvedDate 기록
```

승인되지 않은 Change에서는 `approvedDate`를 기록하지 않는다.

---

## 17. Change 반영일

승인된 변경을 실제 프로젝트에 반영한 날짜를 관리한다.

```text
APPROVED
    ↓
IMPLEMENTING
    ↓
implementedDate
```

반영일은 승인일과 다른 개념이다.

---

## 18. Change 검증일

변경 반영 결과가 정상인지 확인한 날짜를 관리한다.

```text
IMPLEMENTING
    ↓
VERIFIED
    ↓
verifiedDate
```

검증 결과는 `verificationResult`에 기록할 수 있다.

---

## 19. Change 종료일

Change 관리가 완료된 경우 `closedDate`를 관리한다.

```text
VERIFIED
    ↓
CLOSED
    ↓
closedDate 기록
```

---

## 20. Change 영향 분석

Change는 승인 전에 영향 분석을 수행하는 것을 기본으로 한다.

분석 대상:

```text
Scope
Schedule
Cost
Quality
Technical
Infrastructure
Operation
```

예:

```text
Change

서버 사양 변경

    ↓

Scope
→ 기존 구축 범위 변경 없음

Schedule
→ 설치 일정 2일 증가 가능

Cost
→ 서버 비용 증가 가능

Quality
→ 성능 개선 가능

Technical
→ OS / Middleware 호환성 검토 필요

Infrastructure
→ Rack / Power / Network 확인 필요
```

V1에서는 하나의 `impactAnalysis` 문자열 필드로 관리할 수 있다.

향후 세부 영향 분석 Entity로 확장한다.

---

## 21. Change 반영 계획

승인된 Change는 실제 반영 계획을 관리한다.

예:

```text
Change:

Middleware Version 변경

Implementation Plan:

1. 기존 설정 백업
2. 신규 Middleware 설치
3. 환경 설정 변경
4. 애플리케이션 배포
5. 기능 테스트
6. 서비스 정상 여부 확인
```

V1에서는 `implementationPlan` 문자열 필드로 관리한다.

---

## 22. Change 검증 결과

Change 반영 후 검증 결과를 기록한다.

예:

```text
Verification Result:

- Middleware 정상 기동
- Application 정상 배포
- 주요 API 정상 응답
- 기존 기능 Regression Test 완료
- 장애 로그 미발생
```

V1에서는 `verificationResult` 문자열 필드로 관리한다.

---

## 23. Change 승인 의견

승인자는 Change에 대한 의견을 기록할 수 있다.

예:

```text
Approval Comment:

일정 영향이 프로젝트 전체 일정에 미치는 영향이
허용 범위 내이므로 변경을 승인합니다.
```

V1에서는 `approvalComment` 문자열 필드로 관리할 수 있다.

---

## 24. Change API

권장 기본 API:

| Method | Endpoint                            | 목적             |
| ------ | ----------------------------------- | -------------- |
| GET    | `/api/changes/{id}`                 | Change 단건 조회   |
| PUT    | `/api/changes/{id}`                 | Change 수정      |
| DELETE | `/api/changes/{id}`                 | Change 삭제      |
| GET    | `/api/projects/{projectId}/changes` | 프로젝트 Change 목록 |
| POST   | `/api/projects/{projectId}/changes` | Change 생성      |
| GET    | `/api/changes`                      | Change 검색      |
| PATCH  | `/api/changes/{id}/status`          | 상태 변경          |
| PATCH  | `/api/changes/{id}/approval`        | 승인 처리          |

실제 구현 API는 Swagger/OpenAPI를 최종 기준으로 한다.

---

## 25. 프로젝트 Change 조회

### Endpoint

```http
GET /api/projects/{projectId}/changes
```

예:

```http
GET /api/projects/2/changes
```

특정 프로젝트에 소속된 Change 목록을 조회한다.

---

## 26. Change 생성

### Endpoint

```http
POST /api/projects/{projectId}/changes
```

### Request 예시

```json
{
  "title": "Middleware 버전 변경 요청",
  "description": "외부 업체 정책 변경으로 Middleware 버전을 변경해야 합니다.",
  "priority": "HIGH",
  "changeType": "TECHNICAL",
  "impactLevel": "HIGH",
  "requesterId": 10,
  "assigneeId": 20,
  "identifiedDate": "2026-09-18",
  "requestedDate": "2026-09-18",
  "dueDate": "2026-09-25",
  "impactAnalysis": "설치 일정 및 Middleware 호환성 검토가 필요합니다.",
  "implementationPlan": "기존 설정 백업 후 신규 Middleware 설치 및 기능 테스트를 수행합니다.",
  "sortOrder": 1
}
```

### Request Field

| Field                | Type              | Required | Description |
| -------------------- | ----------------- | -------: | ----------- |
| `title`              | String            |        Y | Change 제목   |
| `description`        | String            |        N | 상세 내용       |
| `priority`           | ChangePriority    |        Y | 우선순위        |
| `changeType`         | ChangeType        |        Y | 변경 유형       |
| `impactLevel`        | ChangeImpactLevel |        Y | 영향도         |
| `requesterId`        | Long              |        N | 요청자         |
| `assigneeId`         | Long              |        N | 담당자         |
| `identifiedDate`     | LocalDate         |        N | 식별일         |
| `requestedDate`      | LocalDate         |        N | 요청일         |
| `dueDate`            | LocalDate         |        N | 목표일         |
| `impactAnalysis`     | String            |        N | 영향 분석       |
| `implementationPlan` | String            |        N | 반영 계획       |
| `sortOrder`          | Integer           |        N | 표시 순서       |

Backend 관리 값은 Request에 포함하지 않는다.

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
```

승인 정보는 별도 Approval API를 사용하는 것을 권장한다.

---

## 27. Change 수정

### Endpoint

```http
PUT /api/changes/{id}
```

### Request 예시

```json
{
  "title": "Middleware 버전 변경 요청",
  "description": "외부 업체 정책 변경에 따라 Middleware 버전 변경이 필요합니다.",
  "priority": "CRITICAL",
  "changeType": "TECHNICAL",
  "impactLevel": "HIGH",
  "requesterId": 10,
  "assigneeId": 20,
  "identifiedDate": "2026-09-18",
  "requestedDate": "2026-09-18",
  "dueDate": "2026-09-27",
  "impactAnalysis": "일정 2일 증가 및 Middleware 호환성 검토가 필요합니다.",
  "implementationPlan": "설정 백업 후 신규 버전을 설치하고 Regression Test를 수행합니다.",
  "sortOrder": 1
}
```

Change Key는 수정 대상에 포함하지 않는다.

상태 및 승인 정보 역시 별도 상태/승인 API에서 관리하는 것을 권장한다.

---

## 28. Change 상태 변경

### Endpoint

```http
PATCH /api/changes/{id}/status
```

### Request

```json
{
  "status": "ANALYZING"
}
```

권장 흐름:

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

반려:

```text
PENDING_APPROVAL
    ↓
REJECTED
```

취소:

```text
REQUESTED
    ↓
CANCELLED
```

실제 상태 전이 규칙은 Backend Service에서 관리한다.

---

## 29. Change Approval

### Endpoint

```http
PATCH /api/changes/{id}/approval
```

### Request 예시

```json
{
  "approved": true,
  "approverId": 1,
  "comment": "일정 및 기술 영향 검토 결과 변경을 승인합니다."
}
```

승인:

```text
PENDING_APPROVAL
    ↓
APPROVED
    ↓
approvedDate 기록
```

반려:

```text
PENDING_APPROVAL
    ↓
REJECTED
```

실제 Approval Request DTO 구조는 Backend 구현 시 확정한다.

---

## 30. Change 단건 조회

### Endpoint

```http
GET /api/changes/{id}
```

### Response 예시

```json
{
  "id": 3,
  "projectId": 2,
  "changeKey": "CHG-003",
  "title": "Middleware 버전 변경 요청",
  "description": "외부 업체 정책 변경으로 Middleware 버전을 변경해야 합니다.",
  "status": "PENDING_APPROVAL",
  "priority": "HIGH",
  "changeType": "TECHNICAL",
  "impactLevel": "HIGH",
  "requesterId": 10,
  "assigneeId": 20,
  "identifiedDate": "2026-09-18",
  "requestedDate": "2026-09-18",
  "dueDate": "2026-09-25",
  "approvedDate": null,
  "implementedDate": null,
  "verifiedDate": null,
  "closedDate": null,
  "impactAnalysis": "설치 일정 및 Middleware 호환성 검토가 필요합니다.",
  "implementationPlan": "기존 설정 백업 후 신규 Middleware 설치 및 기능 테스트를 수행합니다.",
  "verificationResult": null,
  "approvalComment": null,
  "sortOrder": 1,
  "createdAt": "2026-09-18T10:00:00",
  "updatedAt": "2026-09-18T14:30:00"
}
```

---

## 31. Change 삭제

### Endpoint

```http
DELETE /api/changes/{id}
```

Frontend에서는 Confirm Dialog를 권장한다.

```text
변경 요청을 삭제하시겠습니까?

[취소] [삭제]
```

승인된 Change 또는 구현 중인 Change는 삭제를 제한하는 정책을 향후 적용할 수 있다.

---

## 32. Change 검색

### Endpoint

```http
GET /api/changes
```

권장 Query Parameter:

| Parameter     | Type              | Description             |
| ------------- | ----------------- | ----------------------- |
| `projectId`   | Long              | 프로젝트 ID                 |
| `keyword`     | String            | 제목 / 내용 / Change Key 검색 |
| `status`      | ChangeStatus      | 상태                      |
| `priority`    | ChangePriority    | 우선순위                    |
| `changeType`  | ChangeType        | 변경 유형                   |
| `impactLevel` | ChangeImpactLevel | 영향도                     |
| `requesterId` | Long              | 요청자                     |
| `assigneeId`  | Long              | 담당자                     |
| `page`        | Integer           | 페이지                     |
| `size`        | Integer           | 페이지 크기                  |
| `sortBy`      | String            | 정렬 기준                   |
| `direction`   | String            | 정렬 방향                   |

예:

```http
GET /api/changes?projectId=2&status=PENDING_APPROVAL&priority=HIGH&page=0&size=20
```

---

## 33. Search Response

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

## 34. Change 담당자

Change에는 담당자를 지정할 수 있다.

```text
Change
 ├── Requester
 └── Assignee
```

Requester:

```text
변경을 요청한 사용자
```

Assignee:

```text
변경 작업을 담당하는 사용자
```

담당자 변경은 Change 수정 또는 별도 API로 구현할 수 있다.

---

## 35. Change Validation

Backend 기본 Validation:

```text
projectId 존재 여부

title 필수 여부

title 길이

status 유효성

priority 유효성

changeType 유효성

impactLevel 유효성

requesterId 유효성

assigneeId 유효성

identifiedDate 유효성

requestedDate 유효성

dueDate 유효성

sortOrder 유효성
```

날짜 Validation 예:

```text
identifiedDate <= requestedDate
requestedDate <= dueDate
```

상태 Validation 예:

```text
APPROVED 상태에서는 승인 정보 존재 여부 검증

IMPLEMENTING 상태에서는 승인 여부 검증

VERIFIED 상태에서는 implementedDate 존재 여부 검증

CLOSED 상태에서는 verifiedDate 존재 여부 검증
```

최종 Validation 책임은 Backend에 있다.

---

## 36. Change Approval Validation

Approval 처리 시 다음 조건을 검증한다.

```text
현재 상태 == PENDING_APPROVAL
```

승인:

```text
PENDING_APPROVAL
    ↓
APPROVED
```

반려:

```text
PENDING_APPROVAL
    ↓
REJECTED
```

이미 승인되었거나 반려된 Change에 다시 Approval을 수행하는 것은 제한한다.

---

## 37. Change Status Workflow

권장 Backend Workflow:

```text
REQUESTED
    │
    ▼
ANALYZING
    │
    ▼
PENDING_APPROVAL
    │
    ├───────────────┐
    ▼               ▼
APPROVED          REJECTED
    │
    ▼
IMPLEMENTING
    │
    ▼
VERIFIED
    │
    ▼
CLOSED
```

취소:

```text
REQUESTED
ANALYZING
PENDING_APPROVAL
      │
      ▼
CANCELLED
```

Frontend에서는 상태 변경 버튼을 현재 상태에 따라 제한할 수 있다.

---

## 38. Change와 Risk

Risk와 Change는 서로 다른 책임을 가진다.

```text
Risk

→ 잠재적으로 발생할 수 있는 문제
```

```text
Change

→ 기존 계획 / 범위 / 일정 / 기술 등을 변경하기 위한 요청
```

예:

```text
Risk

Middleware 설치 일정 지연 가능성
        ↓
대응 과정에서 Middleware 버전 변경 필요
        ↓
Change Request
```

또는:

```text
Risk
 ↓
실제 발생
 ↓
Issue
 ↓
분석
 ↓
Change Request
```

V1에서는 Risk → Change 자동 생성 기능을 구현하지 않는다.

향후 연계할 수 있다.

---

## 39. Change와 Issue

Issue와 Change도 책임을 분리한다.

```text
Issue

→ 이미 발생한 실제 문제
```

```text
Change

→ 문제 해결 또는 프로젝트 계획 변경을 위한 요청
```

예:

```text
Issue

Middleware 설치 실패
        ↓
원인 분석
        ↓
기존 Middleware 변경 필요
        ↓
Change Request
```

향후:

```text
Issue → Change
```

연계를 지원할 수 있다.

V1에서는 자동 전환하지 않는다.

---

## 40. Change와 WBS

Change가 승인되면 특정 WBS Task의 변경이 필요할 수 있다.

예:

```text
Change

서버 설치 방식 변경
        ↓
WBS
1.2.3 서버 구축
        ↓
Task 변경
```

V1에서는 Change와 WBS를 직접 강제 연결하지 않는다.

향후:

```text
Change
   │
   └── WBS Task
```

관계를 지원할 수 있다.

---

## 41. Change와 Schedule

Change는 Schedule에 영향을 줄 수 있다.

예:

```text
Change

Middleware Version 변경
        ↓
Impact Analysis
        ↓
Schedule +2일
        ↓
Schedule 변경 필요
```

Change가 Schedule 자체를 직접 수정하는 것은 기본 책임으로 두지 않는다.

```text
Change
→ 변경 요청 및 영향 분석

Schedule
→ 실제 일정 계획 관리
```

향후 승인된 Change와 Schedule의 변경 이력을 연계할 수 있다.

---

## 42. Change와 Cost

Change는 프로젝트 비용에 영향을 줄 수 있다.

예:

```text
Change

서버 사양 변경
        ↓
장비 비용 증가
        ↓
Cost Impact
```

V1에서는 비용을 별도 Cost Domain으로 관리하지 않는 경우 `impactAnalysis`에 기록할 수 있다.

향후:

```text
Change
   ↓
Cost Impact
```

구조로 확장할 수 있다.

---

## 43. Change와 Evidence

Change 수행 결과는 향후 증적과 연결할 수 있다.

예:

```text
Change

Middleware Version 변경
        ↓
Implementation
        ↓
Verification
        ↓
Evidence

- 설치 결과
- 설정 파일
- 테스트 결과
- 검수 결과
```

V1에서는 Attachment / Evidence 연계를 필수로 구현하지 않는다.

Sprint 5 Evidence Domain과 연계하는 것을 권장한다.

---

## 44. Change와 Dashboard

Dashboard 집계 예:

```text
Total Changes          15

Requested               3

Analyzing               2

Pending Approval        3

Approved                2

Implementing            2

Verified                1

Closed                  2

Rejected                1

Critical Changes        2

Overdue Changes         3
```

추가 집계:

```text
Change Type별

Priority별

Impact별

Status별

Approval별
```

Dashboard는 Change 데이터를 직접 수정하지 않는다.

---

## 45. Overdue Change

`dueDate`를 사용하는 경우 지연 Change를 계산할 수 있다.

기본 조건:

```text
dueDate < today

AND

status != CLOSED

AND

status != CANCELLED

AND

status != REJECTED
```

V1 Entity에 `overdue`를 저장하지 않고 조회/집계 시 계산하는 것을 권장한다.

---

## 46. Change Key 정책

Change Key는 Backend에서 자동 생성한다.

```text
Project A

CHG-001
CHG-002
CHG-003
```

```text
Project B

CHG-001
CHG-002
CHG-003
```

정책:

* Change Key는 Backend에서 생성한다.
* Create Request에서는 Change Key를 입력하지 않는다.
* Update 시 Change Key는 변경하지 않는다.
* Frontend는 Change Key를 표시만 한다.
* Change Key는 Project 단위로 관리한다.
* Project별 sequence를 사용한다.
* 동일 Project 내에서는 Change Key가 중복되지 않는다.

DB:

```text
UNIQUE(project_id, change_key)
```

권장 형식:

```text
CHG-%03d
```

---

## 47. Backend 관리 필드

Frontend에서 임의 변경하지 않는 값:

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
```

사용자 식별 정보 역시 JWT 기반 사용자 인증 구조가 적용되면 Backend에서 자동 설정하는 방식을 권장한다.

---

## 48. Change History

향후 Audit 기능:

```text
Change 생성

Change 수정

Priority 변경

Change Type 변경

Impact 변경

Assignee 변경

Impact Analysis 변경

Status 변경

Approval 요청

Approval 승인

Approval 반려

Implementation 시작

Verification 완료

Change 종료
```

V1에서는 기본 CRUD 및 상태 / 승인 관리에 집중한다.

향후:

```text
ChangeHistory
```

Domain을 별도로 구현한다.

---

## 49. Change Comment

향후 Change Comment 기능을 추가할 수 있다.

```text
Change

 ├── Comment #1
 ├── Comment #2
 └── Comment #3
```

Comment 예:

```text
PM:

고객 승인 완료 후 작업 진행 바랍니다.

개발 담당자:

개발 환경 반영 완료했습니다.

인프라 담당자:

운영 서버 반영 예정입니다.
```

V1에서는 별도 Comment Domain을 제외한다.

---

## 50. Change Attachment

향후 Change 관련 파일을 연결할 수 있다.

예:

```text
Change

├── change-request.xlsx
├── impact-analysis.xlsx
├── implementation-plan.pdf
└── verification-result.pdf
```

Attachment는 공통 File Management / Evidence Domain과 연결하는 것을 권장한다.

V1에서는 제외한다.

---

## 51. Change Approval 확장

향후 다단계 Approval을 지원할 수 있다.

```text
Change Request
      ↓
Impact Analysis
      ↓
PM Review
      ↓
Project Manager Approval
      ↓
Customer Approval
      ↓
Implementation
```

향후 Entity:

```text
ChangeApproval
```

예상 필드:

```text
id
changeId
approverId
approvalOrder
status
comment
approvedAt
createdAt
updatedAt
```

V1에서는 단일 Approval 구조를 우선한다.

---

## 52. Change Frontend TypeScript Model

```typescript
export type ChangeStatus =
  | 'REQUESTED'
  | 'ANALYZING'
  | 'PENDING_APPROVAL'
  | 'APPROVED'
  | 'REJECTED'
  | 'IMPLEMENTING'
  | 'VERIFIED'
  | 'CLOSED'
  | 'CANCELLED';

export type ChangePriority =
  | 'LOW'
  | 'MEDIUM'
  | 'HIGH'
  | 'CRITICAL';

export type ChangeType =
  | 'SCOPE'
  | 'SCHEDULE'
  | 'COST'
  | 'QUALITY'
  | 'TECHNICAL'
  | 'INFRASTRUCTURE'
  | 'SECURITY'
  | 'DATA'
  | 'INTERFACE'
  | 'OPERATION'
  | 'REQUIREMENT'
  | 'OTHER';

export type ChangeImpactLevel =
  | 'LOW'
  | 'MEDIUM'
  | 'HIGH'
  | 'CRITICAL';

export interface ChangeResponse {
  id: number;
  projectId: number;
  changeKey?: string | null;
  title: string;
  description: string | null;
  status: ChangeStatus;
  priority: ChangePriority;
  changeType: ChangeType;
  impactLevel: ChangeImpactLevel;
  requesterId: number | null;
  assigneeId: number | null;
  identifiedDate: string | null;
  requestedDate: string | null;
  dueDate: string | null;
  approvedDate: string | null;
  implementedDate: string | null;
  verifiedDate: string | null;
  closedDate: string | null;
  impactAnalysis: string | null;
  implementationPlan: string | null;
  verificationResult: string | null;
  approvalComment: string | null;
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
}
```

---

## 53. Create / Update Request

```typescript
export interface ChangeCreateRequest {
  title: string;
  description?: string;
  priority: ChangePriority;
  changeType: ChangeType;
  impactLevel: ChangeImpactLevel;
  requesterId?: number;
  assigneeId?: number;
  identifiedDate?: string;
  requestedDate?: string;
  dueDate?: string;
  impactAnalysis?: string;
  implementationPlan?: string;
  sortOrder?: number;
}

export interface ChangeUpdateRequest {
  title: string;
  description?: string;
  priority: ChangePriority;
  changeType: ChangeType;
  impactLevel: ChangeImpactLevel;
  requesterId?: number;
  assigneeId?: number;
  identifiedDate?: string;
  requestedDate?: string;
  dueDate?: string;
  impactAnalysis?: string;
  implementationPlan?: string;
  sortOrder?: number;
}

export interface ChangeStatusUpdateRequest {
  status: ChangeStatus;
}

export interface ChangeApprovalRequest {
  approved: boolean;
  approverId?: number;
  comment?: string;
}
```

Change Key 및 Backend 관리 상태 정보는 Create / Update Request에서 제외한다.

---

## 54. Frontend Component 구조

```text
ChangePage

├── ChangeToolbar
│   ├── Search
│   ├── StatusFilter
│   ├── PriorityFilter
│   ├── ChangeTypeFilter
│   ├── ImpactFilter
│   └── CreateButton
│
├── ChangeSummary
│
├── ChangeList
│   └── ChangeRow
│       ├── ChangeKey
│       ├── PriorityBadge
│       ├── ChangeTypeBadge
│       ├── ImpactBadge
│       ├── Title
│       ├── Assignee
│       ├── StatusBadge
│       └── ActionMenu
│
├── ChangeDetail
│
└── ChangeDialog
    ├── Create
    └── Edit
```

향후:

```text
ChangeApprovalDialog
ChangeImpactAnalysis
ChangeHistory
```

컴포넌트를 추가할 수 있다.

---

## 55. Change UI

```text
┌──────────┬──────────┬──────────────────────────┬────────────┬──────────┬────────────┐
│ Key      │ Priority │ Change                   │ Type       │ Impact   │ Status     │
├──────────┼──────────┼──────────────────────────┼────────────┼──────────┼────────────┤
│ CHG-001  │ HIGH     │ Middleware 버전 변경     │ TECHNICAL  │ HIGH     │ ANALYZING  │
│ CHG-002  │ CRITICAL │ 서버 사양 변경           │ INFRA      │ CRITICAL │ APPROVED   │
└──────────┴──────────┴──────────────────────────┴────────────┴──────────┴────────────┘
```

Change 목록에서는 다음 정보를 핵심으로 표시한다.

* Change Key
* Priority
* Change Type
* Impact
* Title
* Assignee
* Status

---

## 56. Change Detail

```text
Change Detail
────────────────────

Change Key

CHG-003

Title

Middleware 버전 변경 요청

Priority

HIGH

Change Type

TECHNICAL

Impact

HIGH

Status

PENDING_APPROVAL

Requester

프로젝트 담당자

Assignee

인프라 담당자

Identified Date

2026-09-18

Requested Date

2026-09-18

Due Date

2026-09-25

Impact Analysis

설치 일정 및 Middleware 호환성 검토가 필요합니다.

Implementation Plan

기존 설정 백업 후 신규 Middleware 설치 및 기능 테스트를 수행합니다.

Approval

Pending
```

승인 이후:

```text
Approved Date

2026-09-19
```

반영 이후:

```text
Implemented Date

2026-09-21
```

검증 이후:

```text
Verified Date

2026-09-22

Verification Result

주요 기능 및 서비스 정상 여부 확인 완료
```

---

## 57. Change 생성 흐름

```text
Project 선택
    ↓
Change 메뉴
    ↓
Create
    ↓
입력 및 Validation
    ↓
POST /api/projects/{projectId}/changes
    ↓
Change Key Backend 자동 생성
    ↓
REQUESTED
    ↓
목록 Refresh
```

Frontend는 Change Key를 입력하지 않는다.

---

## 58. Change 수정 흐름

```text
Change 선택
    ↓
상세 조회
    ↓
Edit
    ↓
Validation
    ↓
PUT /api/changes/{id}
    ↓
화면 Refresh
```

Change Key는 수정하지 않는다.

---

## 59. Change 승인 흐름

```text
Change 선택
    ↓
Impact Analysis
    ↓
PENDING_APPROVAL
    ↓
Approval
    ├── Approve
    │     ↓
    │   APPROVED
    │
    └── Reject
          ↓
        REJECTED
```

승인된 Change만 Implementation 단계로 이동할 수 있다.

---

## 60. Change 반영 흐름

```text
APPROVED
    ↓
IMPLEMENTING
    ↓
Implementation Plan 수행
    ↓
변경 반영
    ↓
Verification
    ↓
VERIFIED
    ↓
CLOSED
```

---

## 61. Change 삭제 흐름

```text
Change 선택
    ↓
Delete
    ↓
Confirm
    ↓
DELETE /api/changes/{id}
    ↓
Change List Refresh
```

운영 중이거나 승인된 Change에 대한 삭제 제한은 향후 정책으로 확장할 수 있다.

---

## 62. Swagger 기반 Frontend 개발 순서

```text
1. 로그인
    ↓
2. Access Token 확보
    ↓
3. Project 선택
    ↓
4. GET /api/projects/{projectId}/changes
    ↓
5. Change List 표시
    ↓
6. Filter 적용
    ↓
7. Change 생성
    ↓
8. Change 상세 조회
    ↓
9. Impact Analysis 확인
    ↓
10. Change 수정
    ↓
11. Status 변경
    ↓
12. Approval
    ↓
13. Implementation 상태 변경
    ↓
14. Verification
    ↓
15. 삭제
    ↓
16. 검색
    ↓
17. Error 처리
```

---

## 63. Frontend 구현 Checklist

### API

* [ ] Project Change 목록 조회
* [ ] Change 단건 조회
* [ ] Change 생성
* [ ] Change 수정
* [ ] Change 삭제
* [ ] Change 검색
* [ ] Change 상태 변경
* [ ] Change Approval
* [ ] Status Filter
* [ ] Priority Filter
* [ ] Change Type Filter
* [ ] Impact Filter

### UI

* [ ] Change List
* [ ] Change Summary
* [ ] Keyword Search
* [ ] Status Filter
* [ ] Priority Filter
* [ ] Change Type Filter
* [ ] Impact Filter
* [ ] Change Detail
* [ ] Create Dialog
* [ ] Edit Dialog
* [ ] Delete Confirm
* [ ] Status Badge
* [ ] Priority Badge
* [ ] Change Type Badge
* [ ] Impact Badge
* [ ] Approval UI
* [ ] Loading
* [ ] Empty State
* [ ] Error Message
* [ ] Overdue 표시

### 향후 확장

* [ ] Change History
* [ ] Change Comment
* [ ] Change Attachment
* [ ] Multi-level Approval
* [ ] Change Impact 상세 관리
* [ ] Risk → Change 연계
* [ ] Issue → Change 연계
* [ ] Change → WBS 연계
* [ ] Change → Schedule 연계
* [ ] Change → Evidence 연계
* [ ] Notification
* [ ] Email Notification
* [ ] Approval Notification
* [ ] Overdue Alert
* [ ] AI 기반 Impact Analysis
* [ ] AI 기반 Change Summary

---

## 64. Backend 권장 구조

```text
change
├── controller
│   └── ChangeController
│
├── service
│   └── ChangeService
│
├── repository
│   └── ChangeRepository
│
├── mapper
│   └── ChangeMapper
│
├── specification
│   └── ChangeSpecification
│
├── entity
│   └── Change
│
├── dto
│   ├── request
│   │   ├── ChangeCreateRequest
│   │   ├── ChangeUpdateRequest
│   │   ├── ChangeSearchRequest
│   │   ├── ChangeStatusUpdateRequest
│   │   └── ChangeApprovalRequest
│   │
│   └── response
│       └── ChangeResponse
│
└── enums
    ├── ChangeStatus
    ├── ChangePriority
    ├── ChangeType
    └── ChangeImpactLevel
```

---

## 65. Service 핵심 책임

```text
createChange()

getChange()

getProjectChanges()

searchChanges()

updateChange()

updateChangeStatus()

approveChange()

deleteChange()
```

추가 책임:

```text
generateChangeKey()

validateChange()

validateStatusTransition()

validateApproval()

calculateOverdue()
```

향후:

```text
createChangeHistory()

analyzeImpact()

calculateScheduleImpact()

calculateCostImpact()
```

등으로 확장할 수 있다.

---

## 66. Repository 역할

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

changeType

impactLevel

requesterId

assigneeId

keyword
```

Change Key 생성 지원:

```text
findTopByProjectIdOrderByIdDesc()
```

또는 기존 Issue / Risk Domain의 Project-scoped Key 생성 패턴을 따른다.

복잡한 검색은 기존 Domain의 Specification 구현 패턴을 따른다.

---

## 67. Change Error Code

권장 Error Code:

```text
CHANGE_001

Change를 찾을 수 없습니다.
```

```text
CHANGE_002

Change가 속한 프로젝트를 찾을 수 없습니다.
```

```text
CHANGE_003

유효하지 않은 Change 상태입니다.
```

```text
CHANGE_004

Change 날짜 범위가 유효하지 않습니다.
```

```text
CHANGE_005

이미 존재하는 Change Key입니다.
```

```text
CHANGE_006

승인할 수 없는 Change 상태입니다.
```

예상 구조:

```java
public enum ChangeErrorCode implements ErrorCode {

    CHANGE_NOT_FOUND,
    CHANGE_PROJECT_NOT_FOUND,
    CHANGE_INVALID_STATUS,
    CHANGE_INVALID_DATE_RANGE,
    CHANGE_DUPLICATE_KEY,
    CHANGE_INVALID_APPROVAL
}
```

실제 HTTP Status와 Error Message는 Backend 구현 시 확정한다.

---

## 68. API 공통 응답 주의사항

PMIS Backend는 다음과 같은 공통 응답을 사용할 수 있다.

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "조회 성공",
  "data": {}
}
```

실제 Controller 구현에 따라 DTO 또는 Page 객체를 직접 반환할 수 있다.

Frontend는 반드시 다음을 기준으로 구현한다.

```text
Swagger/OpenAPI

+

실제 Backend DTO
```

---

## 69. 오류 처리

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
생성 실패
→ 오류 메시지 표시

수정 실패
→ 기존 데이터 유지

승인 실패
→ 상태 변경하지 않고 오류 표시

삭제 실패
→ 삭제하지 않고 오류 표시

잘못된 Status Transition
→ 400 Bad Request

Change Key 중복
→ 409 Conflict
```

---

## 70. Change Management 업무 흐름

PMIS 전체 업무 흐름에서 Change는 다음과 같이 위치한다.

```text
Project
    ↓
WBS
    ↓
Schedule
    ↓
Progress
    ↓
Risk
    ↓
Issue
    ↓
Change
    ↓
Evidence
    ↓
Dashboard
    ↓
Report
```

실제 변경이 필요한 경우:

```text
Risk
  ↓
Issue
  ↓
Change Request
  ↓
Impact Analysis
  ↓
Approval
  ↓
Implementation
  ↓
Verification
```

---

## 71. Change와 PMO 업무

Change Management는 실제 PMO 업무에서 다음과 같은 관리에 활용할 수 있다.

```text
변경 요청 접수

↓

변경 사유 확인

↓

영향도 분석

↓

일정 영향 확인

↓

범위 영향 확인

↓

비용 영향 확인

↓

품질 영향 확인

↓

승인

↓

변경 반영

↓

검증

↓

변경 이력 관리
```

PMO는 Change의 현재 상태와 승인 여부 및 프로젝트 영향도를 확인할 수 있어야 한다.

---

## 72. Change Dashboard

Change Dashboard에서는 다음 지표를 제공할 수 있다.

```text
Total Changes

Open Changes

Pending Approval

Approved Changes

Implementing Changes

Verified Changes

Closed Changes

Rejected Changes

Critical Changes

Overdue Changes
```

추가:

```text
Change Type Distribution

Priority Distribution

Impact Distribution

Approval Status

Monthly Change Trend
```

향후 Dashboard Domain과 통합한다.

---

## 73. Change Report

Change 데이터는 향후 Report Domain에서 다음 형태로 활용할 수 있다.

```text
Weekly Change Report

Monthly Change Report

Change Approval Report

Change Impact Report

Change History Report

Open Change Report

Overdue Change Report
```

Export:

```text
Excel
PDF
```

기능은 Report Domain에서 담당한다.

---

## 74. Change와 Evidence Traceability

향후 Change와 Evidence를 연결하면 다음과 같은 추적 구조를 만들 수 있다.

```text
Project
    ↓
Change
    ↓
Impact Analysis
    ↓
Approval
    ↓
Implementation
    ↓
Evidence
    ↓
Verification
    ↓
Inspection
```

예:

```text
CHG-003
Middleware Version 변경

        ↓

Approval Evidence

        ↓

설치 결과

        ↓

테스트 결과

        ↓

검수 결과
```

이 구조는 PMIS의 Evidence Traceability 목표와 연결된다.

---

## 75. Change와 Audit

Change는 프로젝트 통제 관점에서 중요한 관리 대상이므로 향후 Audit Log와 연결한다.

예:

```text
2026-09-18 10:00
CHG-003 생성

2026-09-18 11:30
Status REQUESTED → ANALYZING

2026-09-18 14:00
Impact Analysis 등록

2026-09-19 09:00
Status ANALYZING → PENDING_APPROVAL

2026-09-19 13:00
Approval APPROVED

2026-09-20 09:00
Status APPROVED → IMPLEMENTING

2026-09-22 16:00
Status IMPLEMENTING → VERIFIED

2026-09-23 10:00
Status VERIFIED → CLOSED
```

V1에서는 기본 Entity의 생성 / 수정 정보를 활용하고 상세 Audit은 Finalization 단계에서 구현한다.

---

## 76. Change V1 범위

V1에서는 다음 기능을 구현한다.

* Project별 Change 생성
* Project별 Change 조회
* Change 단건 조회
* Change 수정
* Change 삭제
* Change Status
* Change Priority
* Change Type
* Change Impact Level
* Change Requester
* Change Assignee
* Change 식별일
* Change 요청일
* Change 목표일
* Change 승인일
* Change 반영일
* Change 검증일
* Change 종료일
* Change 영향 분석
* Change 반영 계획
* Change 검증 결과
* Change 승인 의견
* Change 검색
* Change Filter
* Pagination
* Dynamic Sorting
* 기본 Validation
* Status Workflow
* Approval
* Project별 Change Key 자동 생성

---

## 77. V1 제외 범위

다음 기능은 V1에서 제외한다.

* Change Comment
* Change Attachment
* Change History Entity
* Multi-level Approval
* Workflow Engine
* SLA Management
* Escalation
* Notification
* Email Notification
* Schedule 자동 변경
* Cost 자동 변경
* WBS 자동 변경
* Risk 자동 Change 생성
* Issue 자동 Change 생성
* Evidence 자동 생성
* Advanced Impact Analysis Engine
* AI 기반 Impact Analysis
* AI 기반 Change 분류
* AI 기반 Change Summary

---

## 78. Change Domain 최종 구조

```text
                         Project
                            │
                            │ 1:N
                            ▼
                         Change
                            │
       ┌────────────────────┼─────────────────────┐
       │                    │                     │
    Status               Priority             Type
       │                    │                     │
       │                    │                     │
       ├── Requested        ├── Low              ├── Scope
       ├── Analyzing        ├── Medium           ├── Schedule
       ├── Pending Approval ├── High             ├── Cost
       ├── Approved         └── Critical         ├── Quality
       ├── Rejected                              ├── Technical
       ├── Implementing                           ├── Infrastructure
       ├── Verified                               ├── Security
       ├── Closed                                 ├── Data
       └── Cancelled                              ├── Interface
                                                   ├── Operation
                                                   ├── Requirement
                                                   └── Other

                         Change
                            │
          ┌─────────────────┼───────────────────┐
          │                 │                   │
       Requester         Assignee          Impact Level
          │                                     │
          │                              ┌──────┼──────┐
          │                              │      │      │
       Request                     Scope Schedule Cost
       Information                 Quality Technical
                                   Infrastructure
                                   Operation
```

Change의 핵심 관리 구조:

```text
Change Request
       ↓
Impact Analysis
       ↓
Approval
       ↓
Implementation
       ↓
Verification
       ↓
Closure
```

---

## 79. PMIS Change Traceability

Change는 다른 PMIS Domain과 다음과 같은 관계를 가진다.

```text
Risk
  ↓
Issue
  ↓
Change
  ↓
WBS / Schedule
  ↓
Implementation
  ↓
Evidence
  ↓
Inspection
  ↓
Report
```

Change는 프로젝트 계획 변경의 통제 지점 역할을 한다.

---

## 80. Change Design Principles

1. Change는 Project에 소속된다.
2. Change는 프로젝트 수행 중 발생하는 변경 요청을 관리한다.
3. Risk와 Change의 책임을 분리한다.
4. Issue와 Change의 책임을 분리한다.
5. Change는 기존 계획 / 범위 / 일정 / 기술 등의 변경을 통제한다.
6. Change Status는 Backend Enum으로 관리한다.
7. Change Priority는 Backend Enum으로 관리한다.
8. Change Type은 Backend Enum으로 관리한다.
9. Change Impact Level은 Backend Enum으로 관리한다.
10. Frontend는 Backend Enum에 없는 값을 임의로 생성하지 않는다.
11. 최종 Validation 책임은 Backend에 있다.
12. Frontend는 UX를 위한 기본 Validation을 수행할 수 있다.
13. Backend 관리 필드는 Frontend에서 임의로 변경하지 않는다.
14. Change Key는 Backend에서 자동 생성한다.
15. Change Key는 Project-scoped 방식으로 관리한다.
16. 동일 Project 내에서는 Change Key가 중복되지 않아야 한다.
17. Change Approval은 Backend에서 상태와 승인 결과를 관리한다.
18. 승인되지 않은 Change는 Implementation 단계로 이동할 수 없다.
19. Change 영향 분석은 승인 전에 수행하는 것을 기본으로 한다.
20. Change 반영 결과는 Verification 단계에서 확인한다.
21. Change 완료 후 CLOSED 상태로 관리한다.
22. Overdue는 저장 필드보다 계산 기반 관리를 우선한다.
23. Comment, Attachment, History는 V1 이후 확장 기능으로 분리한다.
24. Multi-level Approval은 V1 이후 확장한다.
25. Risk → Change 및 Issue → Change 연계는 향후 지원한다.
26. Change → WBS / Schedule 연계는 향후 지원한다.
27. Change → Evidence 연계는 Evidence Domain 구현 이후 지원한다.
28. Dashboard는 Change 데이터를 조회 및 집계하며 직접 수정하지 않는다.
29. Report는 Change 데이터를 주간 / 월간 / 승인 / 영향 분석 등에 활용한다.
30. API 계약은 실제 Backend DTO와 Swagger/OpenAPI를 기준으로 한다.
31. Change Workflow는 Backend Service에서 최종 통제한다.
32. Change 이력 및 Audit은 Finalization 단계에서 확장한다.

---

## 81. Change Domain 구현 순서

Backend 구현 순서:

```text
CHANGE_DESIGN
    ↓
Change Entity / Enum
    ↓
Repository
    ↓
DTO
    ↓
Mapper
    ↓
Specification
    ↓
Service
    ↓
Controller
    ↓
Status Workflow
    ↓
Approval
    ↓
Change Key Generation
    ↓
Swagger API Verification
    ↓
Commit / Push
    ↓
develop Merge
```

Frontend 구현 순서:

```text
Change Design
    ↓
Change TypeScript Model
    ↓
Change API Client
    ↓
Change List
    ↓
Search / Filter
    ↓
Change Detail
    ↓
Create
    ↓
Edit
    ↓
Status UI
    ↓
Approval UI
    ↓
Delete
    ↓
API Integration
    ↓
Browser Verification
    ↓
Production Build
    ↓
Integration Test
```

---

## 82. Change Development Flow

전체 Change 개발 흐름:

```text
Change Design
    ↓
Entity / Enum
    ↓
Repository
    ↓
DTO
    ↓
Specification
    ↓
Service
    ↓
Controller
    ↓
Status Workflow
    ↓
Approval
    ↓
Change Key Generation
    ↓
Swagger API Verification
    ↓
Commit
    ↓
Push
    ↓
develop Merge
    ↓
Frontend Change UI
    ↓
API Integration
    ↓
Production Build
    ↓
Browser Verification
    ↓
E2E Verification
```

---

## 83. Change Swagger Test Scope

Change Backend 구현 완료 후 다음 항목을 검증한다.

```text
1. Change 생성
2. Change Key 생성
3. Change 단건 조회
4. Project Change 조회
5. Change 전체 검색
6. Keyword Search
7. Status Filter
8. Priority Filter
9. Change Type Filter
10. Impact Filter
11. Pagination
12. Dynamic Sorting
13. Change 수정
14. Status Workflow
15. Approval
16. Rejection
17. Change 삭제
18. Validation
19. Date Validation
20. Duplicate Key
21. Not Found
```

특히 다음 Workflow를 필수 검증한다.

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

---

## 84. Change Browser Verification Scope

Frontend 구현 완료 후 다음 항목을 검증한다.

```text
Change Menu
    ↓
Project 선택
    ↓
Change List
    ↓
Search
    ↓
Filter
    ↓
Create
    ↓
Detail
    ↓
Edit
    ↓
Status
    ↓
Approval
    ↓
Implementation
    ↓
Verification
    ↓
Delete
```

UI 상태:

```text
Loading
Empty
Error
Success
```

도 함께 검증한다.

---

## 85. Change Integration Test

Backend / Frontend 통합 테스트:

```text
Project
    ↓
Change 생성
    ↓
Change Key 확인
    ↓
Change List
    ↓
Detail
    ↓
Edit
    ↓
Impact Analysis
    ↓
Approval
    ↓
Status 변경
    ↓
Implementation
    ↓
Verification
    ↓
Closed
```

API와 UI의 상태가 동일하게 유지되는지 검증한다.

---

## 86. Change Domain Final Structure

최종적으로 Change Domain은 다음 구조를 목표로 한다.

```text
                         Project
                            │
                            ▼
                         Change
                            │
          ┌─────────────────┼──────────────────┐
          │                 │                  │
       Request           Analysis           Approval
          │                 │                  │
          │                 │                  │
       Requester        Impact Level        Approver
       Assignee         Impact Analysis     Approval Date
       Description      Schedule Impact     Comment
                       Cost Impact
                       Scope Impact
                            │
                            ▼
                      Implementation
                            │
                            ▼
                       Verification
                            │
                            ▼
                          Closed
```

전체 PMIS 관점에서는:

```text
Project
   ↓
WBS
   ↓
Schedule
   ↓
Progress
   ↓
Risk
   ↓
Issue
   ↓
Change
   ↓
Evidence
   ↓
Inspection
   ↓
Dashboard
   ↓
Report
```

Change는 **프로젝트 변경 요청을 통제하고, 영향 분석과 승인 및 반영 결과를 추적하는 Domain**으로 정의한다.

---

## 87. Document History

| Version | Date       | Author        | Description                             |
| ------- | ---------- | ------------- | --------------------------------------- |
| 1.0     | 2026-09-18 | Seo Seokhyeon | Initial Change Management Domain Design |

---

# End of Document
