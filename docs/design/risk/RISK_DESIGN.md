# RISK Domain Design

> PMIS (Project Management Information System)\
> Version: **1.0**\
> Domain: **Risk Management**\
> Status: **Design Confirmed / Ready for Implementation**\
> Last Updated: **2026-09-15**

------------------------------------------------------------------------

## 1. 문서 목적

본 문서는 PMIS의 **Risk Management Domain에 대한 설계 기준 문서**이다.

Risk Management는 프로젝트 수행 중 발생할 가능성이 있는 잠재적인 문제를
식별하고, 발생 가능성과 영향도를 평가하며, 대응 계획과 상태를 추적하는
핵심 Domain이다.

본 문서는 다음 구현의 기준으로 사용한다.

-   Backend Domain 설계
-   Entity / DTO / Repository / Service 설계
-   REST API 설계
-   Frontend TypeScript Model
-   Frontend 화면 설계
-   Swagger/OpenAPI 명세

------------------------------------------------------------------------

## 2. Risk Management 개요

프로젝트 수행 중 발생할 수 있는 Risk의 예시는 다음과 같다.

-   서버 반입 일정 지연 가능성
-   계정 발급 지연 가능성
-   Middleware 설치 실패 가능성
-   네트워크 구성 지연 가능성
-   인터페이스 개발 지연 가능성
-   데이터 이관 오류 가능성
-   외부 업체 대응 지연 가능성
-   테스트 일정 지연 가능성
-   주요 담당자 부재 가능성
-   인프라 자원 부족 가능성

Risk Management의 기본 흐름:

``` text
Risk 식별
   ↓
Risk 등록
   ↓
발생 가능성 / 영향도 평가
   ↓
대응 계획 수립
   ↓
담당자 지정
   ↓
Risk 모니터링
   ↓
대응 / 완화
   ↓
종료 또는 Issue 전환
```

------------------------------------------------------------------------

## 3. Domain 관계

Risk는 기본적으로 Project에 소속된다.

``` text
Project

   │

   │ 1:N

   ▼

 Risk
```

``` text
Project 1 ───── N Risk
```

향후 WBS, Schedule, Issue, Change와 선택적으로 연결할 수 있으나 V1에서는
Project와 Risk 관계를 핵심으로 한다.

------------------------------------------------------------------------

## 4. Risk 핵심 책임

-   Risk 등록
-   Risk 목록 조회
-   Risk 단건 조회
-   Risk 수정
-   Risk 삭제
-   Risk 상태 관리
-   Risk Priority 관리
-   Risk 발생 가능성 관리
-   Risk 영향도 관리
-   Risk 담당자 관리
-   Risk 식별일 관리
-   Risk 대응 기한 관리
-   Risk 대응 계획 관리
-   Risk 검색 및 필터링
-   Risk 정렬
-   Validation
-   Risk 모니터링
-   향후 Issue 전환

------------------------------------------------------------------------

## 5. Risk Lifecycle

권장 Lifecycle:

``` text
IDENTIFIED
    ↓
ASSESSED
    ↓
MITIGATING
    ↓
MONITORED
    ↓
CLOSED
```

실제 구현에서는 상태를 단순화하여 다음과 같이 관리할 수 있다.

``` text
OPEN
IN_PROGRESS
MITIGATED
CLOSED
```

추가 상태:

``` text
OPEN
IN_PROGRESS
MITIGATED
CLOSED
ACCEPTED
CANCELLED
```

V1에서는 실제 Backend Enum 설계를 기준으로 최종 확정한다.

------------------------------------------------------------------------

## 6. Risk Status

권장 Backend Enum 기준:

  Status          의미
  --------------- -----------------------------------
  `OPEN`          식별된 Risk
  `IN_PROGRESS`   대응 계획 실행 중
  `MITIGATED`     Risk가 완화되었거나 영향이 감소함
  `CLOSED`        Risk 관리 종료
  `ACCEPTED`      Risk를 수용하기로 결정
  `CANCELLED`     더 이상 관리하지 않음

Frontend는 Backend에 없는 Status를 임의로 생성하지 않는다.

------------------------------------------------------------------------

## 7. Risk Priority

Risk의 관리 우선순위를 다음과 같이 정의한다.

``` text
LOW
MEDIUM
HIGH
CRITICAL
```

  Priority     의미
  ------------ -------------------------------------------------
  `LOW`        프로젝트 영향이 낮음
  `MEDIUM`     일반적인 관리 필요
  `HIGH`       프로젝트 일정/품질/비용 등에 상당한 영향 가능
  `CRITICAL`   프로젝트 핵심 일정 또는 목표에 중대한 영향 가능

Risk Priority는 Risk Score와 별도로 관리한다.

------------------------------------------------------------------------

## 8. Risk Probability

Risk 발생 가능성:

``` text
LOW
MEDIUM
HIGH
```

  Probability   의미
  ------------- ----------------------
  `LOW`         발생 가능성이 낮음
  `MEDIUM`      발생 가능성이 보통임
  `HIGH`        발생 가능성이 높음

향후 1\~5점 정량 평가로 확장할 수 있으나 V1에서는 Enum 기반 관리도
가능하다.

------------------------------------------------------------------------

## 9. Risk Impact

Risk가 실제 발생했을 경우의 영향도:

``` text
LOW
MEDIUM
HIGH
CRITICAL
```

  Impact       의미
  ------------ --------------------------------
  `LOW`        프로젝트 영향이 낮음
  `MEDIUM`     일정/품질/업무에 제한적인 영향
  `HIGH`       프로젝트 수행에 상당한 영향
  `CRITICAL`   프로젝트 목표에 중대한 영향

------------------------------------------------------------------------

## 10. Risk Score

Risk Score는 발생 가능성과 영향도를 기반으로 관리할 수 있다.

기본 개념:

``` text
Risk Score = Probability × Impact
```

예:

``` text
Probability = HIGH
Impact      = HIGH

→ 높은 Risk Score
```

V1에서는 Score를 별도 저장 필드로 두기보다 Probability와 Impact를
기반으로 조회/집계 시 계산하는 것을 권장한다.

정량 점수 체계가 필요한 경우 향후 다음과 같이 확장할 수 있다.

``` text
Probability : 1 ~ 5
Impact      : 1 ~ 5

Risk Score = Probability × Impact
```

------------------------------------------------------------------------

## 11. Risk Entity

권장 구조:

  Field              Type                Nullable Description
  ------------------ ----------------- ---------- -----------------
  `id`               Long                       N Risk PK
  `projectId`        Long                       N 소속 Project ID
  `riskKey`          String                     Y Risk 식별 Key
  `title`            String                     N Risk 제목
  `description`      String                     Y Risk 상세 내용
  `status`           RiskStatus                 N Risk 상태
  `priority`         RiskPriority               N 관리 우선순위
  `probability`      RiskProbability            N 발생 가능성
  `impact`           RiskImpact                 N 영향도
  `assigneeId`       Long                       Y 담당자 ID
  `reporterId`       Long                       Y 등록자 ID
  `identifiedDate`   LocalDate                  Y Risk 식별일
  `dueDate`          LocalDate                  Y 대응 목표일
  `mitigatedDate`    LocalDate                  Y 완화일
  `responsePlan`     String                     Y 대응 계획
  `sortOrder`        Integer                    N 표시 순서
  `createdAt`        LocalDateTime              N 생성일시
  `updatedAt`        LocalDateTime              N 수정일시

실제 Backend Entity가 구현되면 실제 Entity와 Swagger/OpenAPI를 최종
기준으로 한다.

------------------------------------------------------------------------

## 12. Risk 식별 Key

Risk를 사람이 쉽게 식별할 수 있도록 Key를 사용할 수 있다.

예:

``` text
RISK-001
RISK-002
RISK-003
```

프로젝트별 식별 방식:

``` text
Project A → RISK-001, RISK-002
Project B → RISK-001, RISK-002
```

Issue Domain과 동일하게 프로젝트별 Key 정책을 적용하는 것을 권장한다.

Backend가 자동 생성하며 Frontend에서는 Risk Key를 입력하지 않는다.

권장 형식:

``` text
RISK-%03d
```

예:

``` text
RISK-001
RISK-002
RISK-003
```

프로젝트별 중복을 허용하되 동일 Project 내에서는 중복되지 않도록 한다.

권장 DB 제약:

``` text
UNIQUE(project_id, risk_key)
```

------------------------------------------------------------------------

## 13. Risk 식별일과 등록일

두 날짜는 다른 개념이다.

``` text
identifiedDate

→ Risk를 처음 식별한 날짜
```

``` text
createdAt

→ PMIS에 Risk를 등록한 날짜와 시간
```

Risk를 식별한 시점과 실제 PMIS 등록 시점이 다를 수 있으므로 별도
관리한다.

------------------------------------------------------------------------

## 14. Risk 대응 기한

`dueDate`는 Risk 대응 계획을 완료하거나 재평가해야 하는 목표일이다.

``` text
Risk 식별

   ↓

대응 계획 수립

   ↓

dueDate

   ↓

Risk 재평가
```

V1에서는 `dueDate`를 기반으로 지연 Risk를 계산할 수 있다.

------------------------------------------------------------------------

## 15. Risk 완화일

Risk가 완화되거나 대응이 완료된 경우 `mitigatedDate`를 관리할 수 있다.

``` text
Risk 발생 가능성 확인

   ↓

대응

   ↓

MITIGATED

   ↓

mitigatedDate 기록
```

자동 설정 여부는 Backend Service 정책에 따른다.

------------------------------------------------------------------------

## 16. Risk 대응 계획

Risk에는 대응 계획을 기록한다.

예:

``` text
Risk:
외부 업체의 Middleware 설치 지원 일정 지연 가능성

Response Plan:
설치 예정일 3일 전 업체 작업 일정을 재확인하고,
지연 발생 시 대체 작업 일정을 확보한다.
```

V1에서는 하나의 `responsePlan` 문자열 필드로 관리한다.

향후 대응 전략을 세분화할 수 있다.

``` text
Avoid
Mitigate
Transfer
Accept
```

------------------------------------------------------------------------

## 17. Risk 대응 전략

향후 Risk Response Strategy를 다음과 같이 확장할 수 있다.

  Strategy     의미
  ------------ ------------------------------
  `AVOID`      Risk 발생 자체를 회피
  `MITIGATE`   발생 가능성 또는 영향을 감소
  `TRANSFER`   외부 업체 등으로 Risk를 이전
  `ACCEPT`     Risk를 수용

V1에서는 별도 Enum을 필수로 두지 않고 `responsePlan` 중심으로 관리할 수
있다.

------------------------------------------------------------------------

## 18. Risk API

권장 기본 API:

  Method   Endpoint                            목적
  -------- ----------------------------------- --------------------
  GET      `/api/risks/{id}`                   Risk 단건 조회
  PUT      `/api/risks/{id}`                   Risk 수정
  DELETE   `/api/risks/{id}`                   Risk 삭제
  GET      `/api/projects/{projectId}/risks`   프로젝트 Risk 목록
  POST     `/api/projects/{projectId}/risks`   Risk 생성
  GET      `/api/risks`                        Risk 검색
  PATCH    `/api/risks/{id}/status`            상태 변경

실제 구현 API는 Swagger/OpenAPI를 최종 기준으로 한다.

------------------------------------------------------------------------

## 19. 프로젝트 Risk 조회

### Endpoint

``` http
GET /api/projects/{projectId}/risks
```

예:

``` http
GET /api/projects/2/risks
```

특정 프로젝트에 소속된 Risk 목록을 조회한다.

------------------------------------------------------------------------

## 20. Risk 생성

### Endpoint

``` http
POST /api/projects/{projectId}/risks
```

### Request 예시

``` json
{
  "title": "Middleware 설치 일정 지연 가능성",
  "description": "외부 업체 작업 일정에 따라 Middleware 설치 일정이 지연될 가능성이 있습니다.",
  "priority": "HIGH",
  "status": "OPEN",
  "probability": "HIGH",
  "impact": "HIGH",
  "assigneeId": 10,
  "identifiedDate": "2026-09-10",
  "dueDate": "2026-09-15",
  "responsePlan": "업체 작업 일정을 사전 확인하고 지연 시 대체 작업 일정을 확보합니다.",
  "sortOrder": 1
}
```

### Request Field

  Field              Type                Required Description
  ------------------ ----------------- ---------- ---------------
  `title`            String                     Y Risk 제목
  `description`      String                     N 상세 내용
  `priority`         RiskPriority               Y 관리 우선순위
  `status`           RiskStatus                 Y 상태
  `probability`      RiskProbability            Y 발생 가능성
  `impact`           RiskImpact                 Y 영향도
  `assigneeId`       Long                       N 담당자
  `identifiedDate`   LocalDate                  N 식별일
  `dueDate`          LocalDate                  N 대응 목표일
  `responsePlan`     String                     N 대응 계획
  `sortOrder`        Integer                    N 표시 순서

Backend 관리 값은 Request에 포함하지 않는다.

``` text
id
projectId
riskKey
reporterId
createdAt
updatedAt
```

------------------------------------------------------------------------

## 21. Risk 수정

### Endpoint

``` http
PUT /api/risks/{id}
```

### Request 예시

``` json
{
  "title": "Middleware 설치 일정 지연 가능성",
  "description": "외부 업체 작업 일정이 변경되어 설치 일정 재조정 가능성이 있습니다.",
  "priority": "HIGH",
  "status": "IN_PROGRESS",
  "probability": "MEDIUM",
  "impact": "HIGH",
  "assigneeId": 10,
  "identifiedDate": "2026-09-10",
  "dueDate": "2026-09-17",
  "responsePlan": "업체 작업 일정을 매일 확인하고 대체 작업 일정을 확보합니다.",
  "sortOrder": 1
}
```

Risk Key는 수정 대상에 포함하지 않는다.

------------------------------------------------------------------------

## 22. Risk 상태 변경

### Endpoint

``` http
PATCH /api/risks/{id}/status
```

### Request

``` json
{
  "status": "MITIGATED"
}
```

권장 흐름:

``` text
OPEN → IN_PROGRESS → MITIGATED → CLOSED
```

또는:

``` text
OPEN → ACCEPTED → CLOSED
```

실제 상태 전이 규칙은 Backend Service에서 관리한다.

------------------------------------------------------------------------

## 23. Risk 단건 조회

### Endpoint

``` http
GET /api/risks/{id}
```

### Response 예시

``` json
{
  "id": 3,
  "projectId": 2,
  "riskKey": "RISK-003",
  "title": "Middleware 설치 일정 지연 가능성",
  "description": "외부 업체 작업 일정에 따라 Middleware 설치 일정이 지연될 가능성이 있습니다.",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "probability": "HIGH",
  "impact": "HIGH",
  "assigneeId": 10,
  "reporterId": 5,
  "identifiedDate": "2026-09-10",
  "dueDate": "2026-09-17",
  "mitigatedDate": null,
  "responsePlan": "업체 작업 일정을 사전 확인하고 지연 시 대체 작업 일정을 확보합니다.",
  "sortOrder": 1,
  "createdAt": "2026-09-11T10:00:00",
  "updatedAt": "2026-09-11T14:30:00"
}
```

------------------------------------------------------------------------

## 24. Risk 삭제

### Endpoint

``` http
DELETE /api/risks/{id}
```

Frontend에서는 Confirm Dialog를 권장한다.

``` text
삭제하시겠습니까?

[취소] [삭제]
```

------------------------------------------------------------------------

## 25. Risk 검색

### Endpoint

``` http
GET /api/risks
```

권장 Query Parameter:

  Parameter       Type              Description
  --------------- ----------------- ----------------
  `projectId`     Long              프로젝트 ID
  `keyword`       String            제목/내용 검색
  `status`        RiskStatus        상태
  `priority`      RiskPriority      우선순위
  `probability`   RiskProbability   발생 가능성
  `impact`        RiskImpact        영향도
  `assigneeId`    Long              담당자
  `page`          Integer           페이지
  `size`          Integer           페이지 크기
  `sortBy`        String            정렬 기준
  `direction`     String            정렬 방향

예:

``` http
GET /api/risks?projectId=2&priority=HIGH&status=OPEN&page=0&size=20
```

------------------------------------------------------------------------

## 26. Search Response

Page 기반 검색 예시:

``` json
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

------------------------------------------------------------------------

## 27. Risk 담당자

Risk에는 담당자를 지정할 수 있다.

``` text
Risk

 ├── Title
 ├── Status
 ├── Priority
 ├── Probability
 ├── Impact
 └── Assignee
```

담당자 변경은 Risk 수정 또는 별도 API로 구현할 수 있다.

------------------------------------------------------------------------

## 28. Risk Validation

Backend 기본 Validation:

``` text
projectId 존재 여부

title 필수 여부

title 길이

status 유효성

priority 유효성

probability 유효성

impact 유효성

assigneeId 유효성

날짜 유효성

sortOrder 유효성
```

날짜 Validation 예:

``` text
identifiedDate <= dueDate
```

상태 Validation 예:

``` text
MITIGATED 상태에서는 mitigatedDate 관리
CLOSED 상태에서는 필요한 경우 mitigatedDate 존재 여부 검증
```

최종 Validation 책임은 Backend에 있다.

------------------------------------------------------------------------

## 29. Risk Score / 우선순위

Risk는 Priority와 Probability/Impact를 구분하여 관리한다.

``` text
Priority
→ 관리 우선순위

Probability
→ 발생 가능성

Impact
→ 발생 시 영향도
```

예:

``` text
Priority   = HIGH
Probability = HIGH
Impact      = CRITICAL
```

이 경우 Dashboard에서는 높은 Risk로 집계할 수 있다.

V1에서는 별도의 `riskScore` 저장 필드를 두지 않고 필요 시 계산한다.

------------------------------------------------------------------------

## 30. Overdue Risk

`dueDate`를 사용하는 경우 지연 Risk를 계산할 수 있다.

``` text
dueDate < today

AND

status != MITIGATED

AND

status != CLOSED

AND

status != CANCELLED
```

V1 Entity에 `overdue`를 저장하지 않고 조회/집계 시 계산하는 것을
권장한다.

------------------------------------------------------------------------

## 31. Risk와 WBS

향후 특정 WBS와 연결할 수 있다.

``` text
WBS

1.2 인프라 구축

   ↓

Risk

Middleware 설치 일정 지연 가능성
```

V1에서는 WBS 연결을 필수로 하지 않는다.

------------------------------------------------------------------------

## 32. Risk와 Schedule

예:

``` text
Risk

서버 반입 지연 가능성

   ↓

Schedule

서버 설치 일정

   ↓

일정 지연 가능성 증가
```

Risk는 Schedule 자체를 수정하지 않는다.

``` text
Risk
→ 잠재적인 문제와 대응 관리

Schedule
→ 실제 일정 계획과 진행 관리
```

------------------------------------------------------------------------

## 33. Risk와 Issue

Risk와 Issue는 책임을 분리한다.

``` text
Risk

→ 아직 발생하지 않은 잠재 문제
```

``` text
Issue

→ 이미 발생한 실제 문제
```

예:

``` text
Risk:
서버 반입 지연 가능성

       ↓ 실제 발생

Issue:
서버 반입 지연
```

향후 다음 전환 기능을 추가할 수 있다.

``` text
Risk → Issue
```

단, V1에서는 자동 전환하지 않는다.

------------------------------------------------------------------------

## 34. Risk와 Change

Risk가 실제 발생하여 기존 계획 변경이 필요한 경우:

``` text
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

또는 Risk 대응 과정에서 변경이 필요한 경우:

``` text
Risk
  ↓
대응 계획
  ↓
변경 필요
  ↓
Change Request
```

V1에서는 Risk와 Change의 책임을 분리한다.

------------------------------------------------------------------------

## 35. Risk와 Dashboard

Dashboard 집계 예:

``` text
Total Risks          15

Open Risks            6

In Progress           4

High Risks            3

Critical Risks        1

Overdue Risks         2

Mitigated Risks       5
```

추가 집계:

``` text
Probability별
Impact별
Priority별
Status별
```

Dashboard는 Risk 데이터를 직접 수정하지 않는다.

------------------------------------------------------------------------

## 36. Risk Matrix

Risk Matrix는 Probability와 Impact를 조합하여 Risk 수준을 시각적으로
표현할 수 있다.

``` text
                Impact
             LOW  MED  HIGH CRIT
Probability
LOW           L    L    M    H
MED           L    M    H    H
HIGH          M    H    H    C
```

예:

``` text
Probability = HIGH
Impact      = CRITICAL

→ Critical Risk
```

V1에서는 Matrix 결과를 별도 DB 필드로 저장하지 않고 조회/화면에서
계산하는 것을 권장한다.

------------------------------------------------------------------------

## 37. Frontend TypeScript Model

``` typescript
export type RiskStatus =
  | 'OPEN'
  | 'IN_PROGRESS'
  | 'MITIGATED'
  | 'CLOSED'
  | 'ACCEPTED'
  | 'CANCELLED';

export type RiskPriority =
  | 'LOW'
  | 'MEDIUM'
  | 'HIGH'
  | 'CRITICAL';

export type RiskProbability =
  | 'LOW'
  | 'MEDIUM'
  | 'HIGH';

export type RiskImpact =
  | 'LOW'
  | 'MEDIUM'
  | 'HIGH'
  | 'CRITICAL';

export interface RiskResponse {
  id: number;
  projectId: number;
  riskKey?: string | null;
  title: string;
  description: string | null;
  status: RiskStatus;
  priority: RiskPriority;
  probability: RiskProbability;
  impact: RiskImpact;
  assigneeId: number | null;
  reporterId: number | null;
  identifiedDate: string | null;
  dueDate: string | null;
  mitigatedDate: string | null;
  responsePlan: string | null;
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
}
```

------------------------------------------------------------------------

## 38. Create / Update Request

``` typescript
export interface RiskCreateRequest {
  title: string;
  description?: string;
  status: RiskStatus;
  priority: RiskPriority;
  probability: RiskProbability;
  impact: RiskImpact;
  assigneeId?: number;
  identifiedDate?: string;
  dueDate?: string;
  responsePlan?: string;
  sortOrder?: number;
}

export interface RiskUpdateRequest {
  title: string;
  description?: string;
  status: RiskStatus;
  priority: RiskPriority;
  probability: RiskProbability;
  impact: RiskImpact;
  assigneeId?: number;
  identifiedDate?: string;
  dueDate?: string;
  responsePlan?: string;
  sortOrder?: number;
}

export interface RiskStatusUpdateRequest {
  status: RiskStatus;
}
```

Risk Key는 Create / Update Request에서 제외한다.

------------------------------------------------------------------------

## 39. Frontend Component 구조

``` text
RiskPage

├── RiskToolbar
│   ├── Search
│   ├── StatusFilter
│   ├── PriorityFilter
│   ├── ProbabilityFilter
│   ├── ImpactFilter
│   └── CreateButton
│
├── RiskSummary
│
├── RiskMatrix
│
├── RiskList
│   └── RiskRow
│       ├── RiskKey
│       ├── PriorityBadge
│       ├── Title
│       ├── ProbabilityBadge
│       ├── ImpactBadge
│       ├── Assignee
│       ├── StatusBadge
│       └── ActionMenu
│
├── RiskDetail
│
└── RiskDialog
    ├── Create
    └── Edit
```

------------------------------------------------------------------------

## 40. Risk UI

``` text
┌───────────┬──────────┬──────────────────────────┬────────────┬──────────┬────────────┐
│ Key       │ Priority │ Risk                     │ Probability│ Impact   │ Status     │
├───────────┼──────────┼──────────────────────────┼────────────┼──────────┼────────────┤
│ RISK-001  │ HIGH     │ 서버 반입 지연 가능성      │ HIGH       │ HIGH     │ OPEN       │
│ RISK-002  │ CRITICAL │ Middleware 설치 지연      │ HIGH       │ CRITICAL │ IN_PROGRESS│
└───────────┴──────────┴──────────────────────────┴────────────┴──────────┴────────────┘
```

Risk 목록에서는 Risk Key, Priority, Probability, Impact, Title,
Assignee, Status 등을 핵심 정보로 표시한다.

------------------------------------------------------------------------

## 41. Risk Detail

``` text
Risk Detail
────────────────────

Risk Key

RISK-003

Title

Middleware 설치 일정 지연 가능성

Priority

HIGH

Probability

HIGH

Impact

HIGH

Status

IN_PROGRESS

Assignee

인프라 담당자

Identified Date

2026-09-10

Due Date

2026-09-17

Response Plan

업체 작업 일정을 사전 확인하고
지연 시 대체 작업 일정을 확보합니다.

Description

외부 업체 작업 일정에 따라
Middleware 설치 일정이 지연될 가능성이 있습니다.
```

------------------------------------------------------------------------

## 42. 생성 흐름

``` text
Project 선택

   ↓

Risk 메뉴

   ↓

Create

   ↓

입력 및 Validation

   ↓

POST /api/projects/{projectId}/risks

   ↓

Risk Key Backend 자동 생성

   ↓

목록 Refresh
```

Frontend는 Risk Key를 입력하지 않는다.

------------------------------------------------------------------------

## 43. 수정 흐름

``` text
Risk 선택

   ↓

상세 조회

   ↓

Edit

   ↓

Validation

   ↓

PUT /api/risks/{id}

   ↓

화면 Refresh
```

Risk Key는 수정하지 않는다.

------------------------------------------------------------------------

## 44. 삭제 흐름

``` text
Risk 선택

   ↓

Delete

   ↓

Confirm

   ↓

DELETE /api/risks/{id}

   ↓

Risk List Refresh
```

------------------------------------------------------------------------

## 45. API 공통 응답 주의사항

PMIS Backend는 다음과 같은 공통 응답을 사용할 수 있다.

``` json
{
  "success": true,
  "code": "SUCCESS",
  "message": "조회 성공",
  "data": {}
}
```

그러나 실제 Controller 구현에 따라 DTO 또는 Page 객체를 직접 반환할 수
있다.

Frontend는 반드시 다음을 기준으로 구현한다.

``` text
Swagger/OpenAPI

+

실제 Backend DTO
```

------------------------------------------------------------------------

## 46. 오류 처리

최소 처리 대상:

``` text
400 Bad Request

401 Unauthorized

403 Forbidden

404 Not Found

409 Conflict

500 Internal Server Error
```

예:

``` text
생성 실패 → 오류 메시지 표시

수정 실패 → 기존 데이터 유지

삭제 실패 → 삭제하지 않고 오류 표시
```

Risk Key 중복 등 DB 제약 위반은 `409 Conflict`로 처리할 수 있다.

------------------------------------------------------------------------

## 47. Backend 관리 필드

Frontend에서 임의 변경하지 않는 값:

``` text
id

projectId

riskKey

reporterId

createdAt

updatedAt
```

`reporterId`는 JWT 로그인 사용자 기준으로 Backend에서 자동 설정하는
방식을 권장한다.

------------------------------------------------------------------------

## 48. Risk History

향후 Audit 기능:

``` text
Risk 생성

Risk 수정

Priority 변경

Probability 변경

Impact 변경

Status 변경

담당자 변경

대응 계획 변경

Mitigation 처리

종료 처리
```

V1에서는 기본 CRUD와 상태 관리에 집중한다.

------------------------------------------------------------------------

## 49. Risk Comment

향후 확장:

``` text
Risk

 ├── Comment #1
 ├── Comment #2
 └── Comment #3
```

Comment Domain은 V1에서 제외한다.

------------------------------------------------------------------------

## 50. Risk Attachment

향후 증적 파일 연결:

``` text
Risk

 ├── vendor-schedule.xlsx
 ├── risk-assessment.xlsx
 └── response-plan.pdf
```

Attachment는 공통 File Management Domain과 연결하는 것을 권장한다.

V1에서는 제외한다.

------------------------------------------------------------------------

## 51. Swagger 기반 Frontend 개발 순서

``` text
1. 로그인

   ↓

2. Access Token 확보

   ↓

3. Project 선택

   ↓

4. GET /api/projects/{projectId}/risks

   ↓

5. Risk List 표시

   ↓

6. Filter 적용

   ↓

7. Risk 생성

   ↓

8. Risk 상세 조회

   ↓

9. Risk 수정

   ↓

10. Status 변경

   ↓

11. 삭제

   ↓

12. 검색

   ↓

13. Risk Matrix 표시

   ↓

14. Error 처리
```

------------------------------------------------------------------------

## 52. Frontend 구현 Checklist

### API

-   [ ] Project Risk 목록 조회
-   [ ] Risk 단건 조회
-   [ ] Risk 생성
-   [ ] Risk 수정
-   [ ] Risk 삭제
-   [ ] Risk 검색
-   [ ] Risk 상태 변경
-   [ ] Priority Filter
-   [ ] Status Filter
-   [ ] Probability Filter
-   [ ] Impact Filter

### UI

-   [ ] Risk List
-   [ ] Risk Summary
-   [ ] Risk Matrix
-   [ ] Keyword Search
-   [ ] Status Filter
-   [ ] Priority Filter
-   [ ] Probability Filter
-   [ ] Impact Filter
-   [ ] Risk Detail
-   [ ] Create Dialog
-   [ ] Edit Dialog
-   [ ] Delete Confirm
-   [ ] Status Badge
-   [ ] Priority Badge
-   [ ] Probability Badge
-   [ ] Impact Badge
-   [ ] Loading
-   [ ] Empty State
-   [ ] Error Message
-   [ ] Overdue 표시

### 향후 확장

-   [ ] Risk Comment
-   [ ] Risk Attachment
-   [ ] Risk History
-   [ ] Risk Matrix 고도화
-   [ ] Risk → Issue 전환
-   [ ] Issue → Change Request 연계
-   [ ] Notification
-   [ ] Email Notification
-   [ ] Overdue Alert
-   [ ] AI 기반 Risk 분석
-   [ ] AI 기반 대응 방안 추천

------------------------------------------------------------------------

## 53. Backend 권장 구조

``` text
risk
├── controller
│   └── RiskController
│
├── service
│   └── RiskService
│
├── repository
│   └── RiskRepository
│
├── entity
│   └── Risk
│
├── dto
│   ├── RiskCreateRequest
│   ├── RiskUpdateRequest
│   ├── RiskSearchRequest
│   ├── RiskStatusUpdateRequest
│   └── RiskResponse
│
└── enums
    ├── RiskStatus
    ├── RiskPriority
    ├── RiskProbability
    └── RiskImpact
```

------------------------------------------------------------------------

## 54. Service 핵심 책임

``` text
createRisk()

getRisk()

getProjectRisks()

searchRisks()

updateRisk()

updateRiskStatus()

deleteRisk()
```

추가 책임:

``` text
generateRiskKey()

validateRisk()

validateStatusTransition()

calculateOverdue()

calculateRiskLevel()
```

Risk Score 및 Risk Level은 필요 시 조회/집계 시 계산한다.

------------------------------------------------------------------------

## 55. Repository 역할

기본 조회:

``` text
findById()

findByProjectId()
```

검색 확장:

``` text
projectId

status

priority

probability

impact

assigneeId

keyword
```

Risk Key 생성 지원:

``` text
findTopByProject_IdOrderByIdDesc()
```

또는 기존 Issue Domain의 Project-scoped Key 생성 패턴을 따른다.

복잡한 검색은 프로젝트의 기존 구현 패턴을 따른다.

------------------------------------------------------------------------

## 56. V1 범위

-   Project별 Risk 생성
-   Project별 Risk 조회
-   Risk 단건 조회
-   Risk 수정
-   Risk 삭제
-   Risk Status
-   Risk Priority
-   Risk Probability
-   Risk Impact
-   Risk 담당자
-   Risk 식별일
-   Risk 대응 목표일
-   Risk 완화일
-   Risk 대응 계획
-   Risk 검색
-   Risk Filter
-   Risk Matrix 기본 표시
-   기본 Validation
-   Status 변경
-   Project별 Risk Key 자동 생성

------------------------------------------------------------------------

## 57. V1 제외 범위

-   Risk Comment
-   Risk Attachment
-   Risk History
-   Notification
-   Email Notification
-   Workflow Engine
-   SLA Management
-   Escalation
-   Risk Schedule Impact 자동 연계
-   Risk Cost Impact
-   Risk 자동 Issue 전환
-   Risk 자동 Change Request 생성
-   고급 Risk Scoring Engine
-   AI 기반 Risk 분류
-   AI 기반 Risk 분석
-   AI 기반 대응 방안 추천

------------------------------------------------------------------------

## 58. Risk Domain 최종 구조

``` text
                         Project
                            │
                            │ 1:N
                            ▼
                          Risk
                            │
          ┌─────────────────┼──────────────────┐
          │                 │                  │
       Status            Priority          Assignee
          │
          ├── Probability
          ├── Impact
          ├── Risk Key
          ├── Title
          ├── Description
          ├── Identified Date
          ├── Due Date
          ├── Mitigated Date
          ├── Response Plan
          └── Sort Order
```

Risk의 핵심 판단 구조:

``` text
Probability × Impact
        ↓
Risk Level
        ↓
Response Plan
        ↓
Monitoring
```

------------------------------------------------------------------------

## 59. PMIS 업무 흐름

``` text
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

Dashboard

   ↓

Report

   ↓

Project Closure
```

Risk는 프로젝트 수행 중 **발생할 가능성이 있는 잠재적인 문제를 관리하는
Domain**이다.

실제 문제가 발생하면 Issue Domain으로 관리 책임이 이동할 수 있다.

------------------------------------------------------------------------

## 60. 설계 원칙

1.  Risk는 프로젝트 수행 중 발생할 가능성이 있는 잠재적인 문제를
    관리한다.
2.  Risk는 Project에 소속된다.
3.  Risk와 Issue의 책임을 분리한다.
4.  Risk는 잠재 문제이고 Issue는 이미 발생한 실제 문제이다.
5.  Risk와 Schedule의 책임을 분리한다.
6.  Schedule은 시간 계획을 관리하고 Risk는 잠재적인 일정/품질/비용
    영향을 관리한다.
7.  Risk Status는 Backend Enum으로 관리한다.
8.  Risk Priority는 Backend Enum으로 관리한다.
9.  Risk Probability는 Backend Enum으로 관리한다.
10. Risk Impact는 Backend Enum으로 관리한다.
11. Frontend는 Backend Enum에 없는 값을 임의로 생성하지 않는다.
12. 최종 Validation 책임은 Backend에 있다.
13. Frontend는 UX를 위한 기본 Validation을 수행할 수 있다.
14. Backend 관리 필드는 Frontend에서 임의로 변경하지 않는다.
15. Risk Key는 Backend에서 자동 생성한다.
16. Risk Key는 Project-scoped 방식으로 관리한다.
17. 동일 Project 내에서는 Risk Key가 중복되지 않아야 한다.
18. Risk Score 또는 Risk Level은 V1에서 저장 필드보다 계산 기반 관리를
    우선한다.
19. 검색 조건은 Swagger/OpenAPI를 최종 기준으로 한다.
20. API 계약은 실제 Backend DTO와 Swagger/OpenAPI를 기준으로 한다.
21. Risk Matrix는 Probability와 Impact를 기반으로 계산한다.
22. Comment, Attachment, History는 V1 이후 확장 기능으로 분리한다.
23. Risk가 실제 문제로 전환된 경우 향후 Risk → Issue 연계를 지원할 수
    있다.
24. Dashboard는 Risk 데이터를 조회 및 집계하며 직접 수정하지 않는다.
25. Risk는 대응 계획과 담당자를 통해 지속적으로 모니터링한다.

------------------------------------------------------------------------

## 61. Document History

  -----------------------------------------------------------------------
  Version           Date              Author            Description
  ----------------- ----------------- ----------------- -----------------
  1.0               2026-09-15        Seo Seokhyeon     Initial Risk
                                                        Management Domain
                                                        Design

  -----------------------------------------------------------------------

------------------------------------------------------------------------

# End of Document
