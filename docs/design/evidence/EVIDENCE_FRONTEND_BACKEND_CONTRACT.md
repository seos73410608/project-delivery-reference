# Evidence & Inspection Frontend ↔ Backend API Contract

> Version: **1.0**  
> Domain: **Evidence & Inspection**  
> Status: **Ready for Implementation**  
> Last Updated: **2026-10-06**  
> Git Branch: `feature/evidence-management`

## 1. Purpose

PMIS Evidence & Inspection Domain의 Frontend ↔ Backend API 계약을 정의한다.

핵심 흐름:

```text
Project → WBS / Task → Evidence Requirement → Evidence
→ Verification → Inspection → Report / Dashboard
```

Frontend와 Backend는 본 계약을 기준으로 API, TypeScript Type, 화면, 검증 및 E2E를 구현한다.

## 2. API Base Path

```text
/api
```

## 3. Domain Relationship

```text
Project
 │
 ├── WBS
 │   └── EvidenceRequirement
 │        └── Evidence
 │             └── Verification
 │
 └── Inspection
      └── InspectionItem
           └── EvidenceRequirement / Evidence
```

- Project 1:N EvidenceRequirement
- Project 1:N Evidence
- EvidenceRequirement 1:N Evidence
- Project 1:N Inspection
- Inspection 1:N InspectionItem
- EvidenceRequirement의 WBS 연결은 선택적
- Evidence는 반드시 EvidenceRequirement에 연결
- Evidence와 Requirement의 Project는 동일해야 한다.

## 4. API Inventory

### Evidence Requirement

| Method | URI | Description |
|---|---|---|
| GET | `/api/projects/{projectId}/evidence-requirements` | 프로젝트별 조회 |
| POST | `/api/projects/{projectId}/evidence-requirements` | 생성 |
| GET | `/api/evidence-requirements/{id}` | 상세 |
| PUT | `/api/evidence-requirements/{id}` | 수정 |
| DELETE | `/api/evidence-requirements/{id}` | 삭제 |
| GET | `/api/evidence-requirements` | 검색 |

### Evidence

| Method | URI | Description |
|---|---|---|
| GET | `/api/evidence-requirements/{requirementId}/evidence` | Requirement별 조회 |
| POST | `/api/evidence-requirements/{requirementId}/evidence` | 등록 |
| GET | `/api/evidence/{id}` | 상세 |
| PUT | `/api/evidence/{id}` | 수정 |
| DELETE | `/api/evidence/{id}` | 삭제 |
| GET | `/api/evidence` | 검색 |
| PATCH | `/api/evidence/{id}/verification` | Verification |

### Inspection

| Method | URI | Description |
|---|---|---|
| GET | `/api/projects/{projectId}/inspections` | 목록 |
| POST | `/api/projects/{projectId}/inspections` | 생성 |
| GET | `/api/inspections/{id}` | 상세 |
| PUT | `/api/inspections/{id}` | 수정 |
| POST | `/api/inspections/{inspectionId}/items` | Item 등록 |
| PATCH | `/api/inspections/{id}/status` | 상태 변경 |

### Summary

```http
GET /api/projects/{projectId}/evidence-summary
```

## 5. Enum Contract

### RequirementStatus

```text
PENDING
PRESENT
MISSING
NOT_REQUIRED
```

### RequirementType

```text
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

### EvidenceType

```text
DOCUMENT
IMAGE
SCREENSHOT
LOG
CONFIGURATION
REPORT
TEST_RESULT
OTHER
```

### VerificationStatus

```text
PENDING
APPROVED
REJECTED
```

### InspectionStatus

```text
PLANNED
IN_PROGRESS
PASSED
FAILED
CLOSED
```

### InspectionItemResult

```text
PENDING
PASSED
FAILED
NOT_APPLICABLE
```

## 6. Evidence Requirement Contract

주요 필드:

```text
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

```text
EVR-001
EVR-002
EVR-003
...
```

프로젝트별 `(projectId, requirementKey)`는 unique해야 한다.

## 7. Requirement Create API

```http
POST /api/projects/{projectId}/evidence-requirements
Content-Type: application/json
```

```json
{
  "wbsId": 101,
  "title": "서버 OS 설치 결과",
  "description": "대상 서버의 OS 설치 결과 증적",
  "requirementType": "INSTALLATION",
  "required": true,
  "dueDate": "2026-10-15",
  "sortOrder": 1
}
```

Backend Managed Fields:

```text
id
projectId
requirementKey
status
createdAt
updatedAt
```

생성 시 `requirementKey = EVR-XXX`, `status = PENDING`.

## 8. Requirement Update API

```http
PUT /api/evidence-requirements/{id}
```

수정 가능한 업무 필드만 전달한다.

수정 불가:

```text
id
projectId
requirementKey
status
createdAt
updatedAt
```

Evidence가 존재하는 Requirement는 V1에서 hard delete하지 않는다.

## 9. Requirement Detail API

```http
GET /api/evidence-requirements/{id}
```

Response 예시:

```json
{
  "id": 1,
  "projectId": 10,
  "wbsId": 101,
  "requirementKey": "EVR-001",
  "title": "서버 OS 설치 결과",
  "description": "대상 서버의 OS 설치 결과 증적",
  "requirementType": "INSTALLATION",
  "required": true,
  "dueDate": "2026-10-15",
  "status": "PRESENT",
  "sortOrder": 1,
  "createdAt": "2026-10-02T10:00:00",
  "updatedAt": "2026-10-02T11:00:00"
}
```

## 10. Requirement Search API

```http
GET /api/evidence-requirements
```

Query:

```text
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

```http
GET /api/evidence-requirements?projectId=10&status=MISSING&page=0&size=20&sortBy=dueDate&direction=ASC
```

기존 PMIS `SearchPageableFactory` 정책을 따른다.

## 11. Evidence Key Contract

Evidence Key는 Backend가 생성한다.

```text
EVD-001
EVD-002
EVD-003
...
```

프로젝트별 `(projectId, evidenceKey)`는 unique해야 한다.

## 12. Evidence Fields

```text
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
submittedBy
submittedAt
verificationStatus
verifiedBy
verifiedAt
verificationRemark
createdAt
updatedAt
```

## 13. Evidence Create API

```http
POST /api/evidence-requirements/{requirementId}/evidence
```

```json
{
  "evidenceType": "DOCUMENT",
  "title": "서버 OS 설치 결과서",
  "description": "OS 설치 완료 결과 문서",
  "fileName": "server-os-install.pdf",
  "filePath": "/evidence/2026/10/server-os-install.pdf"
}
```

Backend Managed Fields:

```text
id
projectId
requirementId
evidenceKey
wbsId
submittedBy
submittedAt
verificationStatus
verifiedBy
verifiedAt
verificationRemark
createdAt
updatedAt
```

생성 시 `verificationStatus = PENDING`.

Evidence가 등록되면 Requirement 상태는 원칙적으로 `PRESENT`.

## 14. Evidence Update / Detail API

```http
PUT /api/evidence/{id}
GET /api/evidence/{id}
```

수정 불가:

```text
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

## 15. Requirement별 Evidence API

```http
GET  /api/evidence-requirements/{requirementId}/evidence
POST /api/evidence-requirements/{requirementId}/evidence
```

Requirement ID는 URL Path로 전달하고 Body에 중복 전달하지 않는다.

## 16. Evidence Search API

```http
GET /api/evidence
```

Query:

```text
projectId
requirementId
wbsId
keyword
evidenceType
verificationStatus
submittedBy
page
size
sortBy
direction
```

## 17. Verification API

```http
PATCH /api/evidence/{id}/verification
Content-Type: application/json
```

승인:

```json
{
  "status": "APPROVED",
  "remark": "설치 결과 및 설정값 확인 완료"
}
```

반려:

```json
{
  "status": "REJECTED",
  "remark": "설치 로그 일부가 누락되어 재제출 필요"
}
```

Backend가 관리:

```text
verificationStatus
verifiedBy
verifiedAt
verificationRemark
```

규칙:

- APPROVED → `verifiedAt = now()`
- REJECTED → `verifiedAt = now()`
- REJECTED는 remark 필수
- verifiedBy는 현재 인증 사용자
- Frontend가 verifier ID를 임의 지정하지 않는다.

핵심 원칙:

```text
PRESENT != APPROVED
```

## 18. Requirement Status Policy

| 조건 | Status |
|---|---|
| required = false | NOT_REQUIRED |
| evidence = 0, dueDate 미경과 | PENDING |
| required = true, evidence = 0, dueDate 경과 | MISSING |
| evidence >= 1 | PRESENT |

```text
Evidence 0
 ├─ required=false → NOT_REQUIRED
 ├─ required=true + overdue → MISSING
 └─ otherwise → PENDING

Evidence >= 1 → PRESENT
```

Service Layer가 관리하며 Frontend는 자체 계산하지 않는다.

## 19. Inspection Contract

Inspection은 프로젝트 또는 단계 수준의 공식 점검을 관리한다.

```text
Inspection
 └── InspectionItem
      └── EvidenceRequirement / Evidence
```

상태:

```text
PLANNED
IN_PROGRESS
PASSED
FAILED
CLOSED
```

## 20. Inspection Create / Detail / Update

생성:

```http
POST /api/projects/{projectId}/inspections
```

```json
{
  "title": "서버 인프라 설치 검수",
  "description": "서버 OS 및 WAS 설치 결과 검수",
  "inspectionDate": "2026-10-20"
}
```

초기 상태는 `PLANNED`.

상세:

```http
GET /api/inspections/{id}
```

수정:

```http
PUT /api/inspections/{id}
```

상태와 식별자는 Backend 관리 영역이다.

## 21. Inspection Item Contract

주요 필드:

```text
id
inspectionId
requirementId
evidenceId
result
remark
createdAt
updatedAt
```

Result:

```text
PENDING
PASSED
FAILED
NOT_APPLICABLE
```

## 22. Inspection Item Create API

```http
POST /api/inspections/{inspectionId}/items
```

```json
{
  "requirementId": 1,
  "evidenceId": 1001
}
```

초기 결과는 `PENDING`.

## 23. Inspection Status API

```http
PATCH /api/inspections/{id}/status
```

```json
{
  "status": "PASSED"
}
```

Backend는 Inspection Item 결과를 검증한 후 상태 변경을 허용한다.

## 24. Evidence Summary API

```http
GET /api/projects/{projectId}/evidence-summary
```

Response:

```json
{
  "totalRequirements": 10,
  "requiredRequirements": 9,
  "presentRequirements": 7,
  "missingRequirements": 1,
  "pendingRequirements": 1,
  "notRequiredRequirements": 1,
  "verificationPending": 2,
  "approvedEvidence": 5,
  "rejectedEvidence": 1,
  "overdueRequirements": 1,
  "totalInspections": 2,
  "plannedInspections": 0,
  "inProgressInspections": 1,
  "passedInspections": 0,
  "failedInspections": 1,
  "closedInspections": 1
}
```

Frontend는 Summary API를 기준으로 KPI를 표시한다.

## 25. Integrity Contract

Backend가 다음을 검증한다.

- Project 존재
- WBS 존재
- WBS와 Project 일치
- Requirement 존재
- Requirement와 Project 일치
- Evidence와 Requirement Project 일치
- Inspection과 연결 대상 Project 일치

Frontend는 최종 무결성을 판단하지 않는다.

## 26. Error Contract

| Code | Description |
|---|---|
| EVIDENCE_001 | Requirement not found |
| EVIDENCE_002 | Evidence not found |
| EVIDENCE_003 | Inspection not found |
| EVIDENCE_004 | Project not found |
| EVIDENCE_005 | WBS not found |
| EVIDENCE_006 | Invalid requirement status |
| EVIDENCE_007 | Invalid verification request |
| EVIDENCE_008 | Invalid inspection transition |
| EVIDENCE_009 | Project/requirement/evidence mismatch |
| EVIDENCE_010 | Cannot delete requirement with evidence |
| EVIDENCE_011 | Rejected verification remark required |
| EVIDENCE_012 | Duplicate requirement key |
| EVIDENCE_013 | Duplicate evidence key |
| EVIDENCE_014 | Invalid inspection item |

HTTP:

```text
400 Bad Request
404 Not Found
409 Conflict
```

기존 PMIS `ApiResponse` 및 `GlobalExceptionHandler` 정책을 따른다.

## 27. Backend Swagger Test Contract

최소 검증:

- Requirement 목록/생성/상세/수정/삭제/검색
- Pagination/Sorting
- Evidence 목록/생성/상세/수정/삭제/검색
- Requirement/Project mismatch
- Verification APPROVED
- Verification REJECTED
- REJECTED without remark
- Inspection 생성/상세/수정
- Inspection Item 등록/결과
- Inspection 상태 변경
- 잘못된 상태 전이
- Summary 집계

## 28. Frontend Integration Contract

```text
Page
 ↓
Component
 ↓
API Client
 ↓
Backend REST API
```

권장 구조:

```text
evidence/
├── pages/
│   └── EvidencePage
├── components/
│   ├── EvidenceRequirementSummary
│   ├── EvidenceRequirementToolbar
│   ├── EvidenceRequirementList
│   ├── EvidenceRequirementRow
│   ├── EvidenceList
│   ├── EvidenceDetail
│   ├── RequirementDialog
│   ├── EvidenceDialog
│   ├── VerificationDialog
│   ├── InspectionList
│   └── InspectionDetail
├── api/
│   ├── evidenceRequirementApi.ts
│   ├── evidenceApi.ts
│   └── inspectionApi.ts
├── types/
│   ├── evidenceRequirement.ts
│   ├── evidence.ts
│   └── inspection.ts
└── utils/
    └── evidenceStatus.ts
```

## 29. Frontend API Client Contract

```text
evidenceRequirementApi.ts
evidenceApi.ts
inspectionApi.ts
evidenceSummaryApi.ts
```

```ts
export const evidenceRequirementApi = {
  getList: async (params) => {},
  getById: async (id) => {},
  create: async (projectId, request) => {},
  update: async (id, request) => {},
  delete: async (id) => {}
};
```

```ts
export const evidenceApi = {
  getList: async (params) => {},
  getById: async (id) => {},
  getByRequirement: async (requirementId, params) => {},
  create: async (requirementId, request) => {},
  update: async (id, request) => {},
  delete: async (id) => {},
  verify: async (id, request) => {}
};
```

```ts
export const inspectionApi = {
  getList: async (projectId, params) => {},
  getById: async (id) => {},
  create: async (projectId, request) => {},
  update: async (id, request) => {},
  addItem: async (inspectionId, request) => {},
  updateStatus: async (id, request) => {}
};
```

## 30. Frontend TypeScript Contract

```ts
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
```

```ts
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

## 31. Frontend Responsibility

Frontend 책임:

- 화면 렌더링
- 검색 조건 입력
- Pagination/Sorting UI
- Form Validation
- API 호출
- Loading/Error 상태
- Dialog/Modal
- Verification UI
- Inspection UI
- 상태 표시

Frontend가 하지 않는 것:

- EVR/EVD Key 생성
- Requirement Status 임의 계산
- Verification 승인 여부 임의 결정
- Verified By 임의 지정
- Inspection 상태 전이 임의 결정
- Project/Requirement 최종 무결성 판단
- Backend 관리 날짜 임의 생성

## 32. Backend Responsibility

Backend 책임:

- Key 생성
- 상태 관리 및 상태 전이 검증
- 인증 사용자 식별
- 날짜/시간 기록
- Project/WBS/Requirement 무결성
- Duplicate Key 검증
- Requirement Status 계산
- Verification 처리
- Inspection 결과 검증
- Summary 집계
- 권한/보안 검증
- 공통 Error Response

## 33. File Storage Contract

V1에서는 실제 Binary Storage를 구현하지 않는다.

관리:

```text
fileName
filePath
```

제외:

```text
S3
MinIO
NAS
Object Storage
파일 업로드 서버
파일 다운로드 인증
```

향후:

```text
Local Storage → Object Storage → S3 / MinIO / NAS
```

## 34. Frontend E2E Contract

### Normal

```text
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

### Reject

```text
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

### Missing

```text
Required Requirement
 ↓
Evidence 없음
 ↓
Due Date 경과
 ↓
Requirement = MISSING
```

### Inspection Failed

```text
Inspection
 ↓
Item FAILED
 ↓
Inspection FAILED
```

## 35. Traceability Contract

```text
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

사용자는 다음 질문에 답할 수 있어야 한다.

1. Project에 필요한 증적은 무엇인가?
2. 어떤 WBS/Task에 연결되어 있는가?
3. 아직 제출되지 않은 증적은 무엇인가?
4. 제출된 Evidence는 무엇인가?
5. 어떤 Evidence가 승인되었는가?
6. 어떤 Evidence가 반려되었는가?
7. 반려 사유는 무엇인가?
8. 어떤 증적이 검수 대상인가?
9. 검수 결과는 무엇인가?
10. Project의 최종 Evidence 상태는 어떠한가?

## 36. Contract Change Procedure

```text
Requirement 변경
 ↓
Domain Design 변경
 ↓
API Contract 변경
 ↓
Backend 구현
 ↓
Swagger 검증
 ↓
Frontend Type 변경
 ↓
Frontend API Client 변경
 ↓
Frontend UI 변경
 ↓
E2E 검증
 ↓
Documentation 업데이트
```

Frontend만 임의로 API Contract를 변경하지 않는다.

## 37. Implementation Order

```text
1. Entity / Enum
2. Repository
3. Request DTO
4. Response DTO
5. Mapper
6. Specification
7. Service
8. Controller
9. Requirement API
10. Evidence API
11. Verification API
12. Inspection API
13. Summary API
14. Swagger
15. Frontend TypeScript Types
16. Frontend API Client
17. Requirement UI
18. Evidence UI
19. Verification UI
20. Inspection UI
21. API Integration
22. Browser Verification
23. E2E
24. Documentation
25. Commit
26. Push
27. PR / Review
28. develop Integration Test
```

핵심 순서:

```text
Requirement → Evidence → Verification → Inspection
```

## 38. Recommended Git Commit Contract

Branch:

```text
feature/evidence-management
```

```text
feat: implement evidence domain entities
feat: implement evidence requirement api
feat: implement evidence api
feat: implement evidence verification workflow
feat: implement inspection api
feat: implement evidence summary api
feat: implement evidence management ui
feat: implement evidence verification ui
feat: implement inspection ui
test: add evidence domain tests
test: verify evidence workflow e2e
docs: update evidence domain documentation
```

작업 단위:

```text
Develop → Build / Test → Verify → Commit
```

## 39. Definition of Done

### Backend

- Entity / Enum / Repository / DTO / Mapper / Specification / Service / Controller 완료
- Requirement CRUD/Search 완료
- Evidence CRUD/Search 완료
- Verification 완료
- Inspection 완료
- Summary 완료
- Validation 완료
- Swagger 검증 완료
- Build 성공

### Frontend

- TypeScript Type 완료
- API Client 완료
- Requirement List/Search/Filter 완료
- Requirement Create/Update/Detail 완료
- Evidence List/Create/Update/Detail 완료
- Verification UI 완료
- Inspection UI 완료
- Summary UI 완료
- API Integration 완료
- Browser Verification 완료
- E2E 완료
- Build 성공

### Documentation

- Domain Design
- API Contract
- Development History
- Changelog
- Roadmap

## 40. Implementation Status

| Area | Status |
|---|---|
| Evidence Domain Design | DONE |
| Entity / Enum | TODO |
| Repository | TODO |
| Request DTO | TODO |
| Response DTO | TODO |
| Mapper | TODO |
| Specification | TODO |
| Service | TODO |
| Controller | TODO |
| Requirement API | TODO |
| Evidence API | TODO |
| Verification API | TODO |
| Inspection API | TODO |
| Summary API | TODO |
| Swagger Test | TODO |
| Frontend Type | TODO |
| Frontend API Client | TODO |
| Requirement UI | TODO |
| Evidence UI | TODO |
| Verification UI | TODO |
| Inspection UI | TODO |
| Browser Verification | TODO |
| E2E | TODO |
| Documentation | TODO |

## 41. V1 Scope

### Included

- Evidence Requirement CRUD
- Evidence Requirement Search/Filter/Pagination/Sorting
- Evidence CRUD
- Evidence Search/Filter/Pagination/Sorting
- Requirement ↔ Evidence linkage
- Project/WBS linkage
- Requirement Status
- Evidence Verification
- Inspection
- Inspection Item
- Evidence Summary
- Swagger
- Frontend UI
- API Integration
- Browser Verification
- E2E
- Documentation

### Excluded

- Binary file storage
- Object Storage
- S3
- MinIO
- Multi-level approval
- Email/Notification
- SLA
- Automatic Evidence generation
- AI classification
- AI summary/recommendation
- Automatic Risk/Issue/Change generation
- Automatic Schedule change
- Complex audit history
- E-approval
- External DMS

## 42. Core Contract Principles

### Requirement and Evidence are separate

```text
Requirement = What must exist?
Evidence    = What was actually submitted?
```

### PRESENT does not mean APPROVED

```text
PRESENT ≠ APPROVED
```

### Backend owns business state

```text
Frontend = Request / Display
Backend  = Validation / State / Business Rule
```

### Missing Evidence is a managed state

```text
Evidence = 0
+
Required
+
Due Date Passed
=
MISSING
```

### Inspection is downstream

```text
Requirement → Evidence → Verification → Inspection
```

### API Contract is the integration boundary

Frontend와 Backend는 Contract를 기준으로 독립 개발하되 동일한 Enum, Request, Response, Error 및 상태 전이를 사용한다.

## 43. Version History

| Version | Date | Description |
|---|---|---|
| 1.0 | 2026-10-02 | Initial Evidence & Inspection Frontend ↔ Backend API Contract |

## Appendix. Recommended Development Sequence

현재 `feature/evidence-management`에서:

```text
Phase 1: Evidence Domain Foundation
 ├─ RequirementStatus
 ├─ RequirementType
 ├─ EvidenceType
 ├─ VerificationStatus
 ├─ InspectionStatus
 ├─ InspectionItemResult
 ├─ EvidenceRequirement
 └─ Evidence

Phase 2: Evidence Requirement Backend
 ├─ Repository
 ├─ DTO
 ├─ Mapper
 ├─ Specification
 ├─ Service
 ├─ Controller
 └─ Swagger Verification

Phase 3: Evidence Backend
 ├─ Repository
 ├─ DTO
 ├─ Mapper
 ├─ Specification
 ├─ Service
 ├─ Controller
 └─ Swagger Verification

Phase 4: Verification Workflow
 └─ PATCH /api/evidence/{id}/verification

Phase 5: Inspection Backend
 ├─ Inspection
 ├─ InspectionItem
 ├─ Status Transition
 └─ Swagger Verification

Phase 6: Summary
 └─ GET /api/projects/{projectId}/evidence-summary

Phase 7: Frontend
 ├─ Requirement
 ├─ Evidence
 ├─ Verification
 └─ Inspection

Phase 8: Integration
 ├─ Browser Verification
 ├─ E2E
 ├─ Documentation
 └─ develop Integration Test
```

**Immediate implementation target:**

```text
Evidence Domain Foundation
→ Entity / Enum
→ Build
→ Commit
→ Requirement Backend
```
