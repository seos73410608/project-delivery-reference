# Evidence & Inspection Frontend ↔ Backend API Contract

> Version: **1.1**\
> Domain: **Evidence & Inspection**\
> Status: **Backend Swagger Verification Completed / Frontend Ready**\
> Last Updated: **2026-10-08**\
> Git Branch: `feature/evidence-management`

## 1. Purpose

PMIS Evidence & Inspection Domain의 Frontend ↔ Backend API 계약을
정의한다.

핵심 흐름:

``` text
Project → WBS / Task → Evidence Requirement → Evidence
→ Verification → Inspection → Report / Dashboard
```

본 문서는 초기 계약에 실제 Backend 구현 및 2026-10-08 Swagger 검증
결과를 반영한 갱신본이다.

------------------------------------------------------------------------

## 2. API Base Path

``` text
/api
```

## 3. Domain Relationship

``` text
Project
 │
 ├── WBS
 │    └── EvidenceRequirement
 │         └── Evidence
 │              └── Verification
 │
 └── Inspection
      └── InspectionItem
           ├── EvidenceRequirement
           └── Evidence
```

-   Project 1:N EvidenceRequirement
-   Project 1:N Evidence
-   EvidenceRequirement 1:N Evidence
-   Project 1:N Inspection
-   Inspection 1:N InspectionItem
-   EvidenceRequirement의 WBS 연결은 선택적
-   Evidence는 반드시 EvidenceRequirement에 연결
-   Evidence와 Requirement의 Project는 동일해야 한다.
-   InspectionItem의 Evidence는 선택적이다.
-   Inspection에서 사용 중인 Evidence는 삭제할 수 없다.

------------------------------------------------------------------------

# 4. API Inventory

## 4.1 Evidence Requirement

  ---------------------------------------------------------------------------------------------------
  Method                  URI                                                 Description
  ----------------------- --------------------------------------------------- -----------------------
  GET                     `/api/projects/{projectId}/evidence-requirements`   프로젝트별 조회

  POST                    `/api/projects/{projectId}/evidence-requirements`   생성

  GET                     `/api/evidence-requirements/{id}`                   상세

  PUT                     `/api/evidence-requirements/{id}`                   수정

  DELETE                  `/api/evidence-requirements/{id}`                   삭제

  GET                     `/api/evidence-requirements`                        검색
  ---------------------------------------------------------------------------------------------------

## 4.2 Evidence

  -------------------------------------------------------------------------------------------------------
  Method                  URI                                                     Description
  ----------------------- ------------------------------------------------------- -----------------------
  GET                     `/api/projects/{projectId}/evidence`                    프로젝트별 조회

  GET                     `/api/evidence-requirements/{requirementId}/evidence`   Requirement별 조회

  POST                    `/api/evidence-requirements/{requirementId}/evidence`   등록

  GET                     `/api/evidence/{id}`                                    상세

  PUT                     `/api/evidence/{id}`                                    수정

  DELETE                  `/api/evidence/{id}`                                    삭제

  GET                     `/api/evidence`                                         검색

  GET                     `/api/projects/{projectId}/wbs/{wbsId}/evidence`        Project + WBS별 조회

  PATCH                   `/api/evidence/{id}/verification`                       Verification
  -------------------------------------------------------------------------------------------------------

## 4.3 Inspection

  ---------------------------------------------------------------------------------------------------------
  Method                  URI                                                       Description
  ----------------------- --------------------------------------------------------- -----------------------
  GET                     `/api/projects/{projectId}/inspections`                   프로젝트별 목록

  GET                     `/api/projects/{projectId}/inspections/status/{status}`   상태별 목록

  POST                    `/api/projects/{projectId}/inspections`                   생성

  GET                     `/api/inspections/{id}`                                   상세

  PUT                     `/api/inspections/{id}`                                   수정

  PATCH                   `/api/inspections/{id}/status`                            상태 변경

  GET                     `/api/inspections/{inspectionId}/items`                   Item 목록

  POST                    `/api/inspections/{inspectionId}/items`                   Item 등록

  GET                     `/api/inspection-items/{itemId}`                          Item 상세

  PUT                     `/api/inspection-items/{itemId}`                          Item 수정

  PATCH                   `/api/inspection-items/{itemId}/result`                   Item 결과 변경
  ---------------------------------------------------------------------------------------------------------

## 4.4 Summary

설계 Endpoint:

``` http
GET /api/projects/{projectId}/evidence-summary
```

> Summary DTO는 존재하지만 2026-10-08 현재 실제 Controller/Swagger 응답
> 검증은 별도 완료 대상이다. Frontend Summary 구현 전에 실제 Response를
> 확정한다.

------------------------------------------------------------------------

# 5. Enum Contract

## RequirementStatus

``` text
PENDING
PRESENT
MISSING
NOT_REQUIRED
```

## RequirementType

``` text
INSTALLATION
CONFIGURATION
ACCOUNT
TEST
MIGRATION
INSPECTION
REPORT
OPERATION
SECURITY
OTHER
```

## EvidenceType --- 실제 Backend Enum

``` text
OTHER
LOG
SCREENSHOT
TEST_RESULT
IMAGE
REPORT
CONFIGURATION
DOCUMENT
```

## VerificationStatus

``` text
PENDING
APPROVED
REJECTED
```

## InspectionStatus

``` text
PLANNED
IN_PROGRESS
PASSED
FAILED
CLOSED
```

## InspectionItemResult

``` text
PENDING
PASSED
FAILED
NOT_APPLICABLE
```

------------------------------------------------------------------------

# 6. Evidence Requirement Contract

주요 필드:

``` text
id
projectId
wbsId
requirementKey
title
description
requirementType
required
dueDate
status
sortOrder
createdAt
updatedAt
```

Requirement Key는 Backend가 생성한다.

``` text
EVR-001
EVR-002
EVR-003
...
```

DB Unique:

``` text
(projectId, requirementKey)
```

------------------------------------------------------------------------

# 7. Requirement Create API

``` http
POST /api/projects/{projectId}/evidence-requirements
Content-Type: application/json
```

Request:

``` json
{
  "wbsId": 1,
  "title": "서버 설치 결과 증적",
  "description": "서버 설치 및 기본 설정 결과를 확인할 수 있는 증적",
  "requirementType": "INSTALLATION",
  "required": true,
  "dueDate": "2026-10-20",
  "sortOrder": 1
}
```

Backend Managed:

``` text
id
projectId
requirementKey
status
createdAt
updatedAt
```

생성 시:

``` text
requirementKey = EVR-001
status = PENDING
```

실제 Swagger:

``` text
Project ID = 2
WBS ID = 1
Requirement ID = 1
Requirement Key = EVR-001
Initial Status = PENDING
```

------------------------------------------------------------------------

# 8. Requirement Status Policy

Backend Service가 관리한다.

  조건                                                Status
  --------------------------------------------------- ----------------
  `required = false`                                  `NOT_REQUIRED`
  Evidence \>= 1                                      `PRESENT`
  `required = true` + Evidence = 0 + dueDate 경과     `MISSING`
  `required = true` + Evidence = 0 + dueDate 미경과   `PENDING`

``` text
required=false
    → NOT_REQUIRED

Evidence >= 1
    → PRESENT

Evidence = 0 + overdue
    → MISSING

Evidence = 0 + not overdue
    → PENDING
```

Frontend는 자체 계산하지 않는다.

### Evidence 생성/삭제 연동

Evidence 생성 후:

``` java
evidenceRequirementService.refreshStatus(requirementId);
```

Evidence 삭제 후에도 동일하게 Requirement Status를 재계산한다.

실제 검증:

``` text
EVD-001 삭제
→ Evidence 재등록
→ EVR-001 = PRESENT
```

------------------------------------------------------------------------

# 9. Requirement Update / Detail / Search

## Update

``` http
PUT /api/evidence-requirements/{id}
```

수정 가능:

``` text
wbsId
title
description
requirementType
required
dueDate
sortOrder
```

수정 불가:

``` text
id
projectId
requirementKey
status
createdAt
updatedAt
```

Evidence가 존재하는 Requirement 삭제는 Backend 무결성 정책에 의해
제한된다.

## Detail

``` http
GET /api/evidence-requirements/{id}
```

## Search

``` http
GET /api/evidence-requirements
```

Query:

``` text
projectId
wbsId
keyword
requirementType
required
status
page
size
sortBy
direction
```

예:

``` http
GET /api/evidence-requirements?projectId=2&status=MISSING&page=0&size=20&sortBy=dueDate&direction=ASC
```

Paging은 기존 `SearchPageableFactory` 정책을 따른다.

------------------------------------------------------------------------

# 10. Evidence Contract

## Key

``` text
EVD-001
EVD-002
EVD-003
...
```

DB Unique:

``` text
(projectId, evidenceKey)
```

## Fields

``` text
id
projectId
requirementId
wbsId
evidenceKey
evidenceType
title
description
fileName
filePath
submittedById
submittedByName
submittedAt
verificationStatus
verifiedById
verifiedByName
verifiedAt
verificationRemark
createdAt
updatedAt
```

------------------------------------------------------------------------

# 11. Evidence Create / Update / Detail

## Create

``` http
POST /api/evidence-requirements/{requirementId}/evidence
```

Request:

``` json
{
  "wbsId": 1,
  "evidenceType": "DOCUMENT",
  "title": "서버 설치 결과 확인서",
  "description": "서버 설치 및 기본 설정 완료 결과",
  "fileName": "server-install-result.pdf",
  "filePath": "/evidence/2026/10/server-install-result.pdf"
}
```

Backend Managed:

``` text
id
projectId
requirementId
evidenceKey
submittedBy
submittedAt
verificationStatus
verifiedBy
verifiedAt
verificationRemark
createdAt
updatedAt
```

생성 시:

``` text
verificationStatus = PENDING
```

실제 Swagger:

``` text
Evidence ID = 2
Evidence Key = EVD-001
Evidence Type = DOCUMENT
Verification Status = PENDING
Submitted By = 관리자
```

Evidence 생성 후 Requirement는 자동으로 `PRESENT`가 된다.

## Update

``` http
PUT /api/evidence/{id}
```

수정 가능:

``` text
wbsId
evidenceType
title
description
fileName
filePath
```

## Detail

``` http
GET /api/evidence/{id}
```

수정 불가:

``` text
id
projectId
requirementId
evidenceKey
verificationStatus
verifiedBy
verifiedAt
verificationRemark
createdAt
updatedAt
```

------------------------------------------------------------------------

# 12. Evidence Query APIs

``` http
GET /api/projects/{projectId}/evidence

GET /api/evidence-requirements/{requirementId}/evidence

GET /api/projects/{projectId}/wbs/{wbsId}/evidence
```

Search:

``` http
GET /api/evidence
```

Query:

``` text
projectId
requirementId
wbsId
keyword
evidenceType
verificationStatus
page
size
sortBy
direction
```

> 현재 실제 `EvidenceSearchRequest`에는 `submittedBy` 필드가 없으므로
> Frontend Search Contract에서는 제외한다.

------------------------------------------------------------------------

# 13. Verification API

``` http
PATCH /api/evidence/{id}/verification
Content-Type: application/json
```

승인:

``` json
{
  "status": "APPROVED",
  "remark": "서버 설치 결과 및 기본 설정 확인 완료"
}
```

반려:

``` json
{
  "status": "REJECTED",
  "remark": "설치 로그 일부가 누락되어 재제출 필요"
}
```

Backend Managed:

``` text
verificationStatus
verifiedBy
verifiedAt
verificationRemark
```

Rules:

``` text
APPROVED → verifiedAt = now()
REJECTED → verifiedAt = now()
REJECTED → remark 필수
verifiedBy → 현재 인증 사용자
verifiedAt → Backend 현재 시각
```

핵심:

``` text
PRESENT != APPROVED
```

실제 Swagger:

``` text
EVD-001
PENDING
  ↓
PATCH /api/evidence/2/verification
  ↓
APPROVED
```

결과:

``` text
verificationStatus = APPROVED
verifiedById = 1
verifiedByName = 관리자
verifiedAt = Backend current time
verificationRemark = 서버 설치 결과 및 기본 설정 확인 완료
```

------------------------------------------------------------------------

# 14. Inspection Contract

``` text
Inspection
  └── InspectionItem
       ├── EvidenceRequirement
       └── Evidence
```

Status:

``` text
PLANNED
IN_PROGRESS
PASSED
FAILED
CLOSED
```

------------------------------------------------------------------------

# 15. Inspection Create / Detail / Update

Create:

``` http
POST /api/projects/{projectId}/inspections
```

Request:

``` json
{
  "title": "서버 설치 결과 검수",
  "description": "서버 설치 및 기본 설정 결과 검수",
  "inspectionDate": "2026-10-08",
  "inspectorName": "PMO 담당자",
  "resultRemark": ""
}
```

Initial:

``` text
PLANNED
```

실제 Swagger:

``` text
Inspection ID = 1
Project ID = 2
Status = PLANNED
Inspector = PMO 담당자
```

Detail:

``` http
GET /api/inspections/{id}
```

Update:

``` http
PUT /api/inspections/{id}
```

수정 가능:

``` text
title
description
inspectionDate
inspectorName
resultRemark
```

Backend Managed:

``` text
id
projectId
status
startedAt
completedAt
createdAt
updatedAt
```

------------------------------------------------------------------------

# 16. Inspection Item Contract

주요 필드:

``` text
id
inspectionId
requirementId
requirementKey
requirementTitle
evidenceId
evidenceKey
evidenceTitle
result
remark
sortOrder
createdAt
updatedAt
```

Result:

``` text
PENDING
PASSED
FAILED
NOT_APPLICABLE
```

Create:

``` http
POST /api/inspections/{inspectionId}/items
```

Request:

``` json
{
  "requirementId": 1,
  "evidenceId": 2,
  "remark": "",
  "sortOrder": 1
}
```

Evidence를 전달하는 경우 Backend는 다음을 검증한다.

``` text
Inspection Project = Evidence Project
Evidence Requirement = Request Requirement
```

초기:

``` text
PENDING
```

실제 Swagger:

``` text
Inspection Item ID = 1
Inspection ID = 1
Requirement = EVR-001
Evidence = EVD-001
Result = PENDING
```

------------------------------------------------------------------------

# 17. Inspection Item APIs

``` http
GET /api/inspections/{inspectionId}/items

GET /api/inspection-items/{itemId}

PUT /api/inspection-items/{itemId}

PATCH /api/inspection-items/{itemId}/result
```

결과 변경 Request:

``` json
{
  "result": "PASSED",
  "remark": "증적 및 서버 설치 결과 확인 완료"
}
```

실제:

``` text
PENDING → PASSED
```

------------------------------------------------------------------------

# 18. Inspection Status Transition

허용:

``` text
PLANNED
  ↓
IN_PROGRESS

IN_PROGRESS
  ↓
PASSED / FAILED

PASSED
  ↓
CLOSED

FAILED
  ↓
CLOSED
```

`CLOSED`는 Terminal State다.

실제 Swagger:

``` text
PLANNED
  ↓
IN_PROGRESS
  ↓
PASSED
  ↓
CLOSED
```

------------------------------------------------------------------------

# 19. Inspection Status API

``` http
PATCH /api/inspections/{id}/status
```

예:

``` json
{
  "status": "IN_PROGRESS",
  "resultRemark": ""
}
```

``` json
{
  "status": "PASSED",
  "resultRemark": "서버 설치 결과 검수 완료"
}
```

``` json
{
  "status": "CLOSED"
}
```

Backend가 상태 전이를 검증한다.

------------------------------------------------------------------------

# 20. Inspection Item Result Transition

허용:

``` text
PENDING
  ├─ PASSED
  ├─ FAILED
  └─ NOT_APPLICABLE

PASSED
  └─ PENDING

FAILED
  └─ PENDING

NOT_APPLICABLE
  └─ PENDING
```

동일 결과 변경은 허용된다.

------------------------------------------------------------------------

# 21. Evidence Summary Contract

Endpoint:

``` http
GET /api/projects/{projectId}/evidence-summary
```

현재 확인된 `EvidenceSummaryResponse`:

``` text
totalRequirements
requiredRequirements
presentRequirements
missingRequirements
pendingRequirements
notRequiredRequirements
verificationPending
verificationApproved
verificationRejected
overdueRequirements
evidenceCompletionRate
verificationApprovalRate
```

예:

``` json
{
  "totalRequirements": 10,
  "requiredRequirements": 9,
  "presentRequirements": 7,
  "missingRequirements": 1,
  "pendingRequirements": 1,
  "notRequiredRequirements": 1,
  "verificationPending": 2,
  "verificationApproved": 5,
  "verificationRejected": 1,
  "overdueRequirements": 1,
  "evidenceCompletionRate": 77.78,
  "verificationApprovalRate": 62.5
}
```

> Summary Controller/Swagger 응답은 아직 별도 검증 대상이다. Frontend
> Summary UI 전에 실제 API를 확정한다.

------------------------------------------------------------------------

# 22. Integrity Contract

Backend가 검증:

``` text
Project 존재
WBS 존재
WBS ↔ Project 일치
Requirement 존재
Requirement ↔ Project 일치
Evidence ↔ Requirement Project 일치
Evidence ↔ Requirement 일치
Inspection ↔ 연결 대상 Project 일치
```

Frontend는 최종 무결성을 판단하지 않는다.

------------------------------------------------------------------------

# 23. Error Contract

실제 Backend `EvidenceErrorCode` 기준:

  ---------------------------------------------------------------------------------------------------
  Code                                                                  HTTP Description
  --------------------------------------------- ---------------------------- ------------------------
  `EVIDENCE_REQUIREMENT_NOT_FOUND`                                       404 Requirement not found

  `EVIDENCE_REQUIREMENT_DELETE_NOT_ALLOWED`                              409 Evidence가 등록된
                                                                             Requirement 삭제 제한

  `EVIDENCE_NOT_FOUND`                                                   404 Evidence not found

  `EVIDENCE_DELETE_NOT_ALLOWED`                                          409 Inspection에서 사용 중인
                                                                             Evidence 삭제 제한

  `INSPECTION_NOT_FOUND`                                                 404 Inspection not found

  `INSPECTION_ITEM_NOT_FOUND`                                            404 Inspection Item not
                                                                             found

  `PROJECT_NOT_FOUND`                                                    404 Project not found

  `WBS_NOT_FOUND`                                                        404 WBS not found

  `WBS_PROJECT_MISMATCH`                                                 400 WBS/Project mismatch

  `EVIDENCE_WBS_PROJECT_MISMATCH`                                        400 Evidence WBS/Project
                                                                             mismatch

  `EVIDENCE_VERIFICATION_NOT_ALLOWED`                                    400 Invalid verification
                                                                             request

  `EVIDENCE_VERIFICATION_REMARK_REQUIRED`                                400 REJECTED remark required

  `INSPECTION_REQUIREMENT_PROJECT_MISMATCH`                              400 Inspection/Requirement
                                                                             mismatch

  `INSPECTION_EVIDENCE_PROJECT_MISMATCH`                                 400 Inspection/Evidence
                                                                             mismatch

  `INSPECTION_EVIDENCE_REQUIREMENT_MISMATCH`                             400 Evidence/Requirement
                                                                             mismatch

  `INSPECTION_INVALID_STATUS_TRANSITION`                                 400 Invalid inspection
                                                                             transition

  `INSPECTION_ITEM_INVALID_RESULT_TRANSITION`                            400 Invalid item result
                                                                             transition
  ---------------------------------------------------------------------------------------------------

HTTP:

``` text
400 Bad Request
404 Not Found
409 Conflict
```

------------------------------------------------------------------------

# 24. ApiResponse Contract

Success:

``` json
{
  "success": true,
  "code": "SUCCESS",
  "data": {}
}
```

Failure:

``` json
{
  "success": false,
  "code": "ERROR_CODE",
  "message": "오류 메시지"
}
```

기존 PMIS `ApiResponse` / `GlobalExceptionHandler` 정책을 따른다.

------------------------------------------------------------------------

# 25. Frontend Integration Architecture

``` text
Page
  ↓
Component
  ↓
API Client
  ↓
Backend REST API
```

권장:

``` text
evidence/
├── pages/
│   └── EvidencePage.tsx
├── components/
│   ├── EvidenceRequirementSummary.tsx
│   ├── EvidenceRequirementToolbar.tsx
│   ├── EvidenceRequirementList.tsx
│   ├── EvidenceRequirementRow.tsx
│   ├── EvidenceList.tsx
│   ├── EvidenceDetail.tsx
│   ├── RequirementDialog.tsx
│   ├── EvidenceDialog.tsx
│   ├── VerificationDialog.tsx
│   ├── InspectionList.tsx
│   └── InspectionDetail.tsx
├── api/
│   ├── evidenceRequirementApi.ts
│   ├── evidenceApi.ts
│   ├── inspectionApi.ts
│   └── evidenceSummaryApi.ts
├── types/
│   ├── evidenceRequirement.ts
│   ├── evidence.ts
│   └── inspection.ts
└── utils/
    └── evidenceStatus.ts
```

------------------------------------------------------------------------

# 26. Frontend API Client Contract

``` ts
export const evidenceRequirementApi = {
  getList: async (params) => {},
  getById: async (id) => {},
  create: async (projectId, request) => {},
  update: async (id, request) => {},
  delete: async (id) => {},
};

export const evidenceApi = {
  getList: async (params) => {},
  getById: async (id) => {},
  getByRequirement: async (requirementId) => {},
  getByProject: async (projectId) => {},
  getByProjectAndWbs: async (projectId, wbsId) => {},
  create: async (requirementId, request) => {},
  update: async (id, request) => {},
  delete: async (id) => {},
  verify: async (id, request) => {},
};

export const inspectionApi = {
  getList: async (projectId) => {},
  getListByStatus: async (projectId, status) => {},
  getById: async (id) => {},
  create: async (projectId, request) => {},
  update: async (id, request) => {},
  getItems: async (inspectionId) => {},
  getItemById: async (itemId) => {},
  addItem: async (inspectionId, request) => {},
  updateItem: async (itemId, request) => {},
  updateItemResult: async (itemId, request) => {},
  updateStatus: async (id, request) => {},
};

export const evidenceSummaryApi = {
  getByProject: async (projectId) => {},
};
```

------------------------------------------------------------------------

# 27. Frontend TypeScript Contract

``` ts
export type RequirementStatus =
  | 'PENDING'
  | 'PRESENT'
  | 'MISSING'
  | 'NOT_REQUIRED';

export type VerificationStatus =
  | 'PENDING'
  | 'APPROVED'
  | 'REJECTED';

export type InspectionStatus =
  | 'PLANNED'
  | 'IN_PROGRESS'
  | 'PASSED'
  | 'FAILED'
  | 'CLOSED';

export type InspectionItemResult =
  | 'PENDING'
  | 'PASSED'
  | 'FAILED'
  | 'NOT_APPLICABLE';

export type RequirementType =
  | 'INSTALLATION'
  | 'CONFIGURATION'
  | 'ACCOUNT'
  | 'TEST'
  | 'MIGRATION'
  | 'INSPECTION'
  | 'REPORT'
  | 'OPERATION'
  | 'SECURITY'
  | 'OTHER';

export type EvidenceType =
  | 'DOCUMENT'
  | 'IMAGE'
  | 'SCREENSHOT'
  | 'LOG'
  | 'CONFIGURATION'
  | 'REPORT'
  | 'TEST_RESULT'
  | 'OTHER';
```

------------------------------------------------------------------------

# 28. Frontend / Backend Responsibility

## Frontend

-   화면 렌더링
-   검색/필터
-   Pagination/Sorting UI
-   Form Validation
-   API 호출
-   Loading/Error
-   Dialog/Modal
-   Verification UI
-   Inspection UI
-   상태 표시

Frontend가 하지 않는 것:

-   EVR/EVD Key 생성
-   Requirement Status 계산
-   Verification 승인 여부 결정
-   Verified By 지정
-   Inspection 상태 전이 결정
-   최종 무결성 판단
-   Backend 관리 날짜 생성

## Backend

-   Key 생성
-   상태 관리
-   상태 전이 검증
-   인증 사용자 식별
-   날짜/시간 기록
-   Project/WBS/Requirement 무결성
-   Duplicate Key 검증
-   Requirement Status 계산
-   Verification 처리
-   Inspection 처리
-   Summary 집계
-   권한/보안
-   Error Response

------------------------------------------------------------------------

# 29. File Storage Contract

V1에서는 실제 Binary Storage를 구현하지 않는다.

관리:

``` text
fileName
filePath
```

제외:

``` text
S3
MinIO
NAS
Object Storage
파일 업로드 서버
파일 다운로드 인증
```

향후:

``` text
Local Storage
→ Object Storage
→ S3 / MinIO / NAS
```

------------------------------------------------------------------------

# 30. Backend Swagger Verification Result

2026-10-08 기준 실제 검증 완료:

## Requirement

-   생성 ✅
-   목록 조회 ✅
-   Status 확인 ✅
-   Project/WBS 연결 확인 ✅

## Evidence

-   생성 ✅
-   Project별 조회 ✅
-   Requirement별 조회 ✅
-   Key 생성 확인 ✅
-   Enum 검증 ✅
-   삭제 후 재등록 ✅
-   Requirement 자동 Status Refresh ✅

## Verification

-   PENDING 확인 ✅
-   APPROVED 처리 ✅
-   verifiedBy 자동 기록 ✅
-   verifiedAt 자동 기록 ✅
-   remark 기록 ✅

## Inspection

-   생성 ✅
-   PLANNED 확인 ✅
-   Item 생성 ✅
-   Requirement 연결 ✅
-   Evidence 연결 ✅
-   PLANNED → IN_PROGRESS ✅
-   Item PENDING → PASSED ✅
-   IN_PROGRESS → PASSED ✅
-   PASSED → CLOSED ✅

------------------------------------------------------------------------

# 31. Verified E2E Data

``` text
Project
  ID = 2

WBS
  ID = 1

Evidence Requirement
  ID = 1
  Key = EVR-001
  Status = PRESENT

Evidence
  ID = 2
  Key = EVD-001
  Verification = APPROVED

Inspection
  ID = 1
  Status = CLOSED

Inspection Item
  ID = 1
  Result = PASSED
```

전체 흐름:

``` text
EVR-001
  ↓
PRESENT
  ↓
EVD-001
  ↓
APPROVED
  ↓
Inspection #1
  ↓
Item #1 = PASSED
  ↓
Inspection #1 = CLOSED
```

------------------------------------------------------------------------

# 32. Frontend E2E Contract

## Normal

``` text
Project
  ↓
WBS Task
  ↓
Requirement 생성
  ↓
Evidence 등록
  ↓
Requirement = PRESENT
  ↓
Evidence = PENDING
  ↓
Verification APPROVED
  ↓
Inspection 생성
  ↓
Inspection Item 등록
  ↓
Inspection Item PASSED
  ↓
Inspection PASSED
  ↓
Inspection CLOSED
```

## Reject

``` text
Evidence 등록
  ↓
Verification REJECTED
  ↓
Remark 확인
  ↓
Evidence 수정 / 재등록
  ↓
Verification APPROVED
```

## Missing

``` text
Required Requirement
  ↓
Evidence 없음
  ↓
Due Date 경과
  ↓
Requirement = MISSING
```

## Inspection Failed

``` text
Inspection
  ↓
Item FAILED
  ↓
Inspection FAILED
  ↓
CLOSED
```

------------------------------------------------------------------------

# 33. Traceability

``` text
Project
  ↓
WBS
  ↓
Task
  ↓
Deliverable
  ↓
Evidence Requirement
  ↓
Evidence
  ↓
Verification
  ↓
Inspection
```

사용자가 확인할 수 있어야 하는 질문:

1.  Project에 필요한 증적은 무엇인가?
2.  어떤 WBS/Task에 연결되어 있는가?
3.  아직 제출되지 않은 증적은 무엇인가?
4.  제출된 Evidence는 무엇인가?
5.  어떤 Evidence가 승인되었는가?
6.  어떤 Evidence가 반려되었는가?
7.  반려 사유는 무엇인가?
8.  어떤 증적이 검수 대상인가?
9.  검수 결과는 무엇인가?
10. Project의 최종 Evidence 상태는 어떠한가?

------------------------------------------------------------------------

# 34. Implementation Order

Backend 핵심 Workflow가 검증 완료되었으므로 Frontend 단계로 전환한다.

``` text
1. TypeScript Types
2. API Client
3. Requirement UI
4. Evidence UI
5. Verification UI
6. Inspection UI
7. Summary UI
8. API Integration
9. Browser Verification
10. Frontend E2E
11. Documentation
12. Commit
13. Push
14. PR / Review
15. develop Integration Test
```

핵심:

``` text
Requirement
  → Evidence
  → Verification
  → Inspection
```

------------------------------------------------------------------------

# 35. Recommended Git Commit Contract

Branch:

``` text
feature/evidence-management
```

권장:

``` text
feat: implement evidence management ui
feat: implement evidence verification ui
feat: implement inspection ui
feat: implement evidence summary ui
test: verify evidence frontend workflow
test: verify evidence workflow e2e
docs: update evidence api contract
```

작업 단위:

``` text
Develop
  → Build
  → Browser Verify
  → Commit
```

------------------------------------------------------------------------

# 36. Definition of Done

## Backend

-   [x] Entity / Enum
-   [x] Repository
-   [x] Request DTO
-   [x] Response DTO
-   [x] Mapper
-   [x] Specification
-   [x] Service
-   [x] Controller
-   [x] Requirement API
-   [x] Evidence API
-   [x] Verification API
-   [x] Inspection API
-   [ ] Summary API 실제 Swagger 검증
-   [x] Validation
-   [x] 핵심 Swagger Workflow 검증
-   [x] Build 성공

## Frontend

-   [ ] TypeScript Type
-   [ ] API Client
-   [ ] Requirement List/Search/Filter
-   [ ] Requirement Create/Update/Detail
-   [ ] Evidence List/Create/Update/Detail
-   [ ] Verification UI
-   [ ] Inspection UI
-   [ ] Summary UI
-   [ ] API Integration
-   [ ] Browser Verification
-   [ ] E2E
-   [ ] Build 성공

## Documentation

-   [x] Domain Design
-   [x] API Contract
-   [ ] Development History
-   [ ] Changelog
-   [ ] Roadmap

------------------------------------------------------------------------

# 37. V1 Scope

## Included

-   Evidence Requirement CRUD
-   Requirement Search/Filter/Pagination/Sorting
-   Evidence CRUD
-   Evidence Search/Filter/Pagination/Sorting
-   Requirement ↔ Evidence linkage
-   Project/WBS linkage
-   Requirement Status
-   Evidence Verification
-   Inspection
-   Inspection Item
-   Evidence Summary
-   Swagger
-   Frontend UI
-   API Integration
-   Browser Verification
-   E2E
-   Documentation

## Excluded

-   Binary file storage
-   Object Storage
-   S3
-   MinIO
-   Multi-level approval
-   Email/Notification
-   SLA
-   Automatic Evidence generation
-   AI classification
-   AI summary/recommendation
-   Automatic Risk/Issue/Change generation
-   Automatic Schedule change
-   Complex audit history
-   E-approval
-   External DMS

------------------------------------------------------------------------

# 38. Core Contract Principles

``` text
Requirement = What must exist?
Evidence    = What was actually submitted?

PRESENT != APPROVED

Frontend = Request / Display
Backend  = Validation / State / Business Rule

Evidence = 0
+ Required
+ Due Date Passed
= MISSING

Requirement
  → Evidence
  → Verification
  → Inspection
```

API Contract는 Frontend와 Backend의 통합 경계다.

Frontend와 Backend는 동일한:

``` text
Enum
Request
Response
Error
State Transition
```

을 사용한다.

------------------------------------------------------------------------

# 39. Known Contract Notes

## 39.1 Evidence submittedAt

현재 Create DTO에서 `submittedAt`을 전달할 수 있으나 업무 의미상 Backend
Managed Field로 정의되어 있다.

향후 권장:

``` text
Client submittedAt 제거
Backend LocalDateTime.now() 사용
```

## 39.2 Evidence Update submittedAt

현재 Update DTO에 존재하지만 실제 Entity Update에는 반영되지 않는다.

Frontend Update Type에서는 제외한다.

## 39.3 Evidence Delete

현재 실제 삭제 제한은 InspectionItem에서 Evidence가 사용 중인지 여부를
검사한다.

Frontend 메시지:

``` text
Inspection에서 사용 중인 Evidence는 삭제할 수 없습니다.
```

## 39.4 Summary

Summary DTO는 존재하지만 실제 Controller/Swagger 응답 검증이 필요하다.

## 39.5 Inspection Item Delete

현재 Backend에 Delete API가 없으므로 Frontend에서도 Delete 기능을
제공하지 않는다.

------------------------------------------------------------------------

# 40. Final Implementation Status

``` text
Backend Evidence & Inspection
        │
        ├─ Requirement        DONE
        ├─ Evidence           DONE
        ├─ Verification       DONE
        ├─ Inspection         DONE
        ├─ Inspection Item    DONE
        ├─ Core E2E Workflow  DONE
        ├─ Build              DONE
        └─ Summary Swagger    TODO
                 │
                 ▼
        Frontend Implementation
                 │
                 ├─ TypeScript Types
                 ├─ API Client
                 ├─ Requirement UI
                 ├─ Evidence UI
                 ├─ Verification UI
                 ├─ Inspection UI
                 └─ Summary UI
```

------------------------------------------------------------------------

# 41. Version History

  -----------------------------------------------------------------------
  Version                 Date                    Description
  ----------------------- ----------------------- -----------------------
  1.0                     2026-10-02              Initial Evidence &
                                                  Inspection API Contract

  1.1                     2026-10-08              Actual Backend
                                                  implementation 및
                                                  Swagger E2E
                                                  verification 결과 반영
  -----------------------------------------------------------------------

------------------------------------------------------------------------

# Appendix. Verified E2E Scenario

## 1. Requirement

``` http
POST /api/projects/2/evidence-requirements
```

``` text
EVR-001
status = PENDING
```

## 2. Evidence

``` http
POST /api/evidence-requirements/1/evidence
```

``` text
EVD-001
verificationStatus = PENDING
```

Requirement 자동 갱신:

``` text
EVR-001
PENDING → PRESENT
```

## 3. Verification

``` http
PATCH /api/evidence/2/verification
```

``` json
{
  "status": "APPROVED",
  "remark": "서버 설치 결과 및 기본 설정 확인 완료"
}
```

결과:

``` text
EVD-001
PENDING → APPROVED
```

## 4. Inspection

``` http
POST /api/projects/2/inspections
```

``` text
Inspection #1 = PLANNED
```

## 5. Inspection Item

``` http
POST /api/inspections/1/items
```

``` text
InspectionItem #1 = PENDING
```

## 6. Inspection Start

``` http
PATCH /api/inspections/1/status
```

``` json
{
  "status": "IN_PROGRESS"
}
```

## 7. Item Pass

``` http
PATCH /api/inspection-items/1/result
```

``` json
{
  "result": "PASSED",
  "remark": "증적 및 서버 설치 결과 확인 완료"
}
```

## 8. Inspection Pass

``` http
PATCH /api/inspections/1/status
```

``` json
{
  "status": "PASSED",
  "resultRemark": "서버 설치 결과 검수 완료"
}
```

## 9. Inspection Close

``` http
PATCH /api/inspections/1/status
```

``` json
{
  "status": "CLOSED"
}
```

최종:

``` text
EVR-001 = PRESENT
EVD-001 = APPROVED
Inspection #1 = CLOSED
InspectionItem #1 = PASSED
```

**Evidence & Inspection Backend V1 핵심 E2E Workflow 검증 완료.**
