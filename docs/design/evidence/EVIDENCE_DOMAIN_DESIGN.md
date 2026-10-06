# EVIDENCE Domain Design

> Version: 1.0\
> Domain: Evidence & Inspection\
> Status: Design Confirmed / Ready for Implementation\
> Last Updated: 2026-10-02\
> Project: Project Management Information System (PMIS)

------------------------------------------------------------------------

## 1. 문서 목적

본 문서는 PMIS의 **Evidence & Inspection Domain**을 구현하기 위한 업무
및 기술 설계 기준을 정의한다.

Evidence Domain은 단순한 파일 첨부 기능이 아니라 다음의 PMO 업무 흐름을
시스템화하는 것을 목적으로 한다.

``` text
Project
  ↓
WBS / Task
  ↓
Evidence Requirement
  ↓
Evidence
  ↓
Verification
  ↓
Inspection
  ↓
Report / Dashboard
```

핵심 목적은 프로젝트에서 **무엇을 제출해야 하는지**, **실제로
제출되었는지**, **검증되었는지**, **검수 결과가 무엇인지**를 추적
가능하게 만드는 것이다.

------------------------------------------------------------------------

## 2. Domain 핵심 개념

Evidence Domain은 다음 4개의 개념을 구분한다.

  -----------------------------------------------------------------------
  개념                                의미
  ----------------------------------- -----------------------------------
  Evidence Requirement                프로젝트에서 반드시 확보해야 하는
                                      증적/산출물 요구사항

  Evidence                            실제로 등록된 증적

  Verification                        등록된 증적의 검증 결과

  Inspection                          프로젝트 또는 특정 단계에 대한 공식
                                      검수/점검 결과
  -----------------------------------------------------------------------

### 2.1 Evidence Requirement

Evidence Requirement는 "무엇을 확보해야 하는가?"를 정의한다.

예:

-   서버 설치 결과 증적
-   계정 생성 결과
-   OS 설정 결과
-   DB 설치 결과
-   WAS 설치 결과
-   네트워크 설정 결과
-   테스트 결과서
-   이관 결과 보고서
-   검수 확인서

Requirement와 실제 Evidence를 분리하는 이유는 **증적이 아직 존재하지
않는 상태 자체를 관리해야 하기 때문**이다.

------------------------------------------------------------------------

### 2.2 Evidence

Evidence는 Evidence Requirement에 대응하여 실제로 등록된 증적이다.

예:

``` text
Requirement:
  서버 계정 생성 결과

Evidence:
  filename = server-account-result.xlsx
  type = DOCUMENT
  submittedBy = 홍길동
  submittedAt = 2026-10-01 14:30
```

하나의 Requirement에 여러 개의 Evidence가 등록될 수 있다.

``` text
Evidence Requirement
 ├─ Evidence #1
 ├─ Evidence #2
 └─ Evidence #3
```

따라서 기본 관계는 다음과 같다.

``` text
EvidenceRequirement 1 : N Evidence
```

------------------------------------------------------------------------

### 2.3 Verification

Verification은 등록된 Evidence가 요구사항을 충족하는지 검증하는
과정이다.

``` text
Evidence
   ↓
Verification
   ├─ PENDING
   ├─ APPROVED
   └─ REJECTED
```

------------------------------------------------------------------------

### 2.4 Inspection

Inspection은 개별 Evidence 검증보다 상위 수준의 프로젝트 검수 개념이다.

예:

-   중간 검수
-   구축 완료 검수
-   서버 설치 검수
-   이관 검수
-   최종 검수

Inspection은 여러 Evidence Requirement를 묶어 하나의 공식적인 검수
단위로 관리할 수 있다.

``` text
Inspection
 ├─ Requirement A → Evidence → APPROVED
 ├─ Requirement B → Evidence → APPROVED
 ├─ Requirement C → Evidence → REJECTED
 └─ Requirement D → Evidence → PENDING
```

V1에서는 Inspection을 지나치게 복잡한 Workflow Engine으로 만들지 않고,
**검수 단위 + 검수 항목 + 결과** 중심으로 구현한다.

------------------------------------------------------------------------

## 3. Domain 관계

### 3.1 기본 관계

``` text
Project
  │
  ├── WBS
  │    │
  │    └── EvidenceRequirement
  │           │
  │           └── Evidence
  │                  │
  │                  └── Verification
  │
  └── Inspection
         │
         └── InspectionItem
                │
                └── EvidenceRequirement / Evidence
```

### 3.2 Entity 관계

``` text
Project 1:N EvidenceRequirement
Project 1:N Evidence
EvidenceRequirement 1:N Evidence
Project 1:N Inspection
Inspection 1:N InspectionItem
```

WBS와의 연결은 EvidenceRequirement에서 선택적으로 제공한다.

``` text
EvidenceRequirement
 ├─ projectId   required
 └─ wbsId       optional
```

------------------------------------------------------------------------

## 4. Requirement Status

Evidence Requirement의 존재 여부와 검증 여부를 혼합하지 않는다.

Requirement의 상태는 **증적 존재 상태**를 의미한다.

``` text
PENDING
PRESENT
MISSING
NOT_REQUIRED
```

  Status         의미
  -------------- ---------------------------------------------
  PENDING        아직 제출되지 않았거나 처리 중
  PRESENT        요구되는 Evidence가 등록됨
  MISSING        제출 기한이 지났거나 필요한 증적이 없음
  NOT_REQUIRED   해당 프로젝트/작업에서 증적이 필요하지 않음

상태 변경은 Service Layer에서 통제한다.

------------------------------------------------------------------------

## 5. Verification Status

Evidence의 검증 상태는 별도로 관리한다.

``` text
PENDING
APPROVED
REJECTED
```

  Status     의미
  ---------- --------------------
  PENDING    아직 검증되지 않음
  APPROVED   검증 완료 및 승인
  REJECTED   검증 결과 반려

Requirement의 `PRESENT`와 Evidence의 `APPROVED`는 서로 다른 의미다.

``` text
PRESENT
= 증적이 존재한다.

APPROVED
= 증적이 검증되어 승인되었다.
```

------------------------------------------------------------------------

## 6. Evidence Type

V1에서는 다음 Evidence Type을 제공한다.

``` text
DOCUMENT
IMAGE
SCREENSHOT
LOG
CONFIGURATION
REPORT
TEST_RESULT
OTHER
```

------------------------------------------------------------------------

## 7. Evidence Requirement Entity

  Field             Type              Required Description
  ----------------- --------------- ---------- -----------------------------
  id                Long                     Y PK
  projectId         Long                     Y Project FK
  wbsId             Long                     N WBS FK
  requirementKey    String                   Y 프로젝트 내 Requirement Key
  title             String                   Y 증적 요구사항 제목
  description       String                   N 상세 설명
  requirementType   Enum                     Y 요구사항 유형
  required          Boolean                  Y 필수 여부
  dueDate           LocalDate                N 제출 기한
  status            Enum                     Y Requirement Status
  sortOrder         Integer                  N 정렬 순서
  createdAt         LocalDateTime            Y 생성일시
  updatedAt         LocalDateTime            Y 수정일시

------------------------------------------------------------------------

## 8. Requirement Type

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

------------------------------------------------------------------------

## 9. Evidence Entity

  Field                Type              Required Description
  -------------------- --------------- ---------- --------------------------
  id                   Long                     Y PK
  projectId            Long                     Y Project FK
  requirementId        Long                     Y Evidence Requirement FK
  wbsId                Long                     N WBS FK
  evidenceKey          String                   Y 프로젝트 내 Evidence Key
  evidenceType         Enum                     Y Evidence 유형
  title                String                   Y 증적 제목
  description          String                   N 설명
  fileName             String                   N 파일명
  filePath             String                   N 파일 경로
  submittedBy          Long                     Y 제출자
  submittedAt          LocalDateTime            Y 제출일시
  verificationStatus   Enum                     Y 검증 상태
  verifiedBy           Long                     N 검증자
  verifiedAt           LocalDateTime            N 검증일시
  verificationRemark   String                   N 검증 의견
  createdAt            LocalDateTime            Y 생성일시
  updatedAt            LocalDateTime            Y 수정일시

------------------------------------------------------------------------

## 10. Evidence Key

Requirement:

``` text
EVR-001
EVR-002
EVR-003
```

Evidence:

``` text
EVD-001
EVD-002
EVD-003
```

DB에서는 다음 제약조건을 적용한다.

``` text
UNIQUE(project_id, requirement_key)
UNIQUE(project_id, evidence_key)
```

Key 생성은 Backend에서 담당한다.

------------------------------------------------------------------------

## 11. Backend Controlled Fields

### Requirement Create/Update 제외

``` text
id
projectId
requirementKey
status
createdAt
updatedAt
```

### Evidence Create/Update 제외

``` text
id
projectId
requirementId
evidenceKey
verificationStatus
verifiedBy
verifiedAt
createdAt
updatedAt
```

검증 관련 필드는 별도의 Verification API를 통해 변경한다.

------------------------------------------------------------------------

## 12. Requirement API

``` http
GET /api/projects/{projectId}/evidence-requirements
POST /api/projects/{projectId}/evidence-requirements
GET /api/evidence-requirements/{id}
PUT /api/evidence-requirements/{id}
DELETE /api/evidence-requirements/{id}
GET /api/evidence-requirements
```

------------------------------------------------------------------------

## 13. Requirement Search

기존 PMIS Domain과 동일한 Search + Pageable 계약을 사용한다.

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

기존 `SearchPageableFactory`를 재사용한다.

------------------------------------------------------------------------

## 14. Evidence API

``` http
GET /api/evidence-requirements/{requirementId}/evidence
POST /api/evidence-requirements/{requirementId}/evidence
GET /api/evidence/{id}
PUT /api/evidence/{id}
DELETE /api/evidence/{id}
GET /api/evidence
```

------------------------------------------------------------------------

## 15. Verification API

Evidence의 검증은 일반 수정 API와 분리한다.

``` http
PATCH /api/evidence/{id}/verification
```

예:

``` json
{
  "status": "APPROVED",
  "remark": "설치 결과 및 설정값 확인 완료"
}
```

Backend에서 다음을 자동 처리한다.

``` text
verificationStatus
verifiedBy
verifiedAt
verificationRemark
```

------------------------------------------------------------------------

## 16. Inspection

V1에서는 Inspection을 별도 대형 Workflow Engine으로 만들지 않고 기본
검수 단위로 구현한다.

### Inspection 주요 필드

  Field            Description
  ---------------- -------------
  id               PK
  projectId        Project FK
  title            검수명
  inspectionType   검수 유형
  inspectionDate   검수일
  inspectorId      검수 담당자
  status           검수 상태
  remark           검수 의견
  createdAt        생성일시
  updatedAt        수정일시

### Inspection Status

``` text
PLANNED
IN_PROGRESS
PASSED
FAILED
CLOSED
```

### Inspection Item

``` text
Inspection
  └── InspectionItem
        ├── evidenceRequirementId
        ├── evidenceId
        ├── result
        └── remark
```

Result:

``` text
PENDING
PASSED
FAILED
NOT_APPLICABLE
```

------------------------------------------------------------------------

## 17. Inspection API

``` http
GET /api/projects/{projectId}/inspections
POST /api/projects/{projectId}/inspections
GET /api/inspections/{id}
PUT /api/inspections/{id}
POST /api/inspections/{inspectionId}/items
PATCH /api/inspections/{id}/status
```

------------------------------------------------------------------------

## 18. Requirement → Evidence Workflow

``` text
1. Requirement 생성
       ↓
2. PENDING
       ↓
3. Evidence 등록
       ↓
4. PRESENT
       ↓
5. Evidence Verification
       ↓
6. APPROVED / REJECTED
       ↓
7. Inspection
       ↓
8. PASSED / FAILED
```

반려 시:

``` text
REJECTED
   ↓
Evidence 수정/재등록
   ↓
PENDING
   ↓
재검증
```

------------------------------------------------------------------------

## 19. Requirement 자동 상태 정책

Evidence 0건:

``` text
PENDING
```

Evidence 1건 이상:

``` text
PRESENT
```

필수 Requirement + DueDate 경과 + Evidence 없음:

``` text
MISSING
```

비필수 Requirement:

``` text
NOT_REQUIRED
```

실제 구현에서는 자동 상태 계산과 수동 상태 변경의 충돌을 방지하도록
Service Layer에서 책임을 명확히 정의한다.

------------------------------------------------------------------------

## 20. Validation

### Requirement

``` text
projectId 존재
title 필수
requirementType 필수
required 필수
dueDate 유효성
wbsId 존재 여부
```

### Evidence

``` text
projectId 존재
requirementId 존재
requirement이 해당 project에 속하는지 확인
evidenceType 필수
title 필수
submittedBy 존재
```

### Verification

``` text
Evidence 존재
status 유효
검증자 존재
APPROVED 시 검증일시 기록
REJECTED 시 remark 권장
```

------------------------------------------------------------------------

## 21. Domain Integrity

Evidence는 반드시 Requirement에 연결되어야 한다.

``` text
Evidence.projectId
=
EvidenceRequirement.projectId
```

WBS를 사용하는 경우에도 프로젝트/작업 관계가 일치하는지 Backend에서
검증한다.

------------------------------------------------------------------------

## 22. 삭제 정책

Requirement에 Evidence가 존재하는 경우 단순 Hard Delete는 지양한다.

``` text
Requirement
  └── Evidence #1
```

이 상태에서 Requirement 삭제 요청이 들어오면 BusinessException으로
제한한다.

V1에서는 데이터 추적성을 우선한다.

------------------------------------------------------------------------

## 23. Dashboard Metrics

``` text
Total Requirements
Required Requirements
Present
Missing
Pending
Not Required
Verification Pending
Approved
Rejected
Overdue
```

추가 KPI:

``` text
Evidence Completion Rate
Verification Approval Rate
Inspection Pass Rate
Overdue Requirement Rate
```

------------------------------------------------------------------------

## 24. Evidence Summary API

``` http
GET /api/projects/{projectId}/evidence-summary
```

예상 Response:

``` json
{
  "totalRequirements": 120,
  "requiredRequirements": 100,
  "present": 78,
  "missing": 12,
  "pending": 8,
  "notRequired": 22,
  "verificationPending": 15,
  "approved": 60,
  "rejected": 3
}
```

------------------------------------------------------------------------

## 25. Backend Package Structure

``` text
evidence/
├── controller/
│   ├── EvidenceRequirementController
│   ├── EvidenceController
│   └── InspectionController
├── service/
│   ├── EvidenceRequirementService
│   ├── EvidenceService
│   └── InspectionService
├── repository/
│   ├── EvidenceRequirementRepository
│   ├── EvidenceRepository
│   ├── InspectionRepository
│   └── InspectionItemRepository
├── mapper/
│   ├── EvidenceRequirementMapper
│   ├── EvidenceMapper
│   └── InspectionMapper
├── specification/
│   ├── EvidenceRequirementSpecification
│   └── EvidenceSpecification
├── entity/
│   ├── EvidenceRequirement
│   ├── Evidence
│   ├── Inspection
│   └── InspectionItem
├── dto/
│   ├── request/
│   └── response/
└── enums/
    ├── RequirementStatus
    ├── RequirementType
    ├── EvidenceType
    ├── VerificationStatus
    ├── InspectionStatus
    └── InspectionItemResult
```

------------------------------------------------------------------------

## 26. Frontend Structure

``` text
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

------------------------------------------------------------------------

## 27. Frontend Page Concept

Evidence 화면은 단순 파일 목록이 아니라 **요구사항 중심 화면**으로
설계한다.

``` text
┌──────────────────────────────────────────────┐
│ Evidence & Inspection                       │
├──────────────────────────────────────────────┤
│ Total 120 | Present 78 | Missing 12 | ...   │
├──────────────────────────────────────────────┤
│ Project [▼] WBS [▼] Status [▼] Type [▼]    │
│ Keyword [________________] [Search]          │
├──────────────────────────────────────────────┤
│ EVR-001 │ 서버 설치 결과 │ PRESENT           │
│ EVR-002 │ 계정 생성 결과 │ MISSING           │
│ EVR-003 │ OS 설정 결과   │ PRESENT           │
├──────────────────────────────────────────────┤
│ Requirement Detail                           │
│   Evidence List                              │
│   Verification                               │
│   Inspection                                 │
└──────────────────────────────────────────────┘
```

핵심은 "파일을 보여주는 화면"보다 **"무엇이 빠져 있는가?"를 보여주는
화면**이다.

------------------------------------------------------------------------

## 28. File Storage 전략

V1에서는 실제 Binary File Storage를 필수 구현 범위에서 제외한다.

우선 다음 metadata를 관리한다.

``` text
fileName
filePath
```

향후:

``` text
Local File Storage
        ↓
Object Storage
        ↓
S3 / MinIO / NAS
```

등으로 확장한다.

------------------------------------------------------------------------

## 29. Swagger Verification

Backend 구현 완료 후 최소 다음을 검증한다.

``` text
Requirement:
GET / POST / GET detail / PUT / DELETE

Evidence:
GET / POST / GET detail / PUT / DELETE

Verification:
PATCH verification

Inspection:
GET / POST / GET detail / PUT / POST item / PATCH status
```

------------------------------------------------------------------------

## 30. Browser Verification

### Requirement

``` text
Evidence 화면 진입
Project 선택
Requirement 목록 확인
검색
필터
Pagination
Requirement 생성
Requirement 수정
상세 조회
```

### Evidence

``` text
Requirement 선택
Evidence 등록
Evidence 목록 확인
Evidence 상세
Evidence 수정
Evidence 삭제
```

### Verification

``` text
Verification Dialog
APPROVED
REJECTED
Remark 확인
상태 반영 확인
```

### Inspection

``` text
Inspection 생성
Inspection Item 등록
검수 결과 입력
Inspection 종료
결과 확인
```

------------------------------------------------------------------------

## 31. E2E Test Scenario

### 정상 흐름

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
Evidence Verification = APPROVED
  ↓
Inspection Item = PASSED
  ↓
Inspection = PASSED
```

### 반려 흐름

``` text
Evidence 등록
  ↓
Verification = REJECTED
  ↓
수정/재등록
  ↓
Verification = APPROVED
```

### 누락 흐름

``` text
Required Requirement
  ↓
Evidence 없음
  ↓
DueDate 경과
  ↓
MISSING
```

------------------------------------------------------------------------

## 32. Test Data

현실적인 PMO 증적 데이터를 사용한다.

``` text
EVR-001 서버 OS 설치 결과
EVR-002 서버 계정 생성 결과
EVR-003 OS 보안 설정 결과
EVR-004 DB 설치 결과
EVR-005 WAS 설치 결과
EVR-006 네트워크 설정 결과
EVR-007 인터페이스 테스트 결과
EVR-008 데이터 이관 결과
EVR-009 이관 검증 결과
EVR-010 최종 검수 결과
```

각 Requirement에는 다음 상태가 섞여 있어야 한다.

``` text
PRESENT
MISSING
PENDING
NOT_REQUIRED
```

------------------------------------------------------------------------

## 33. V1 Scope

### 포함

``` text
Evidence Requirement CRUD
Evidence CRUD
Requirement ↔ Evidence linkage
Project linkage
WBS linkage
Requirement status
Evidence verification
Inspection
Inspection Item
Search
Filter
Pagination
Sorting
Summary
Swagger
Frontend UI
API Integration
Browser Verification
E2E Verification
Documentation
```

### 제외

``` text
실제 파일 Binary Storage
Object Storage
S3
MinIO
다단계 승인 Workflow
Notification
Email
SLA Engine
자동 증적 생성
AI 증적 분류
AI 증적 요약
AI 누락 증적 추천
자동 Risk 생성
자동 Issue 생성
자동 Change 생성
자동 WBS 생성
자동 Schedule 변경
복잡한 Audit History
전자결재 연동
외부 문서관리시스템 연동
```

------------------------------------------------------------------------

## 34. 향후 확장

### Object Storage

``` text
Evidence
  ↓
File Storage Service
  ↓
Object Storage
```

### Audit

``` text
Evidence
  ↓
EvidenceHistory
```

### Change 연계

``` text
Change
  ↓
Evidence Requirement
  ↓
Evidence
```

### Risk 연계

``` text
Risk
  ↓
Mitigation Action
  ↓
Evidence Requirement
```

### AI 연계

``` text
Evidence
  ↓
AI Classification
  ↓
Requirement Matching
  ↓
Missing Evidence Detection
  ↓
Summary
```

------------------------------------------------------------------------

## 35. Dashboard / Report 연계

Evidence Domain은 Dashboard에 다음 정보를 제공한다.

``` text
Evidence Completion
Verification Status
Missing Evidence
Overdue Evidence
Inspection Status
```

Report Domain에서는 다음 보고서를 생성할 수 있다.

``` text
Evidence Status Report
Missing Evidence Report
Verification Report
Inspection Result Report
WBS Evidence Traceability Report
Project Evidence Completion Report
```

핵심 Traceability:

``` text
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

------------------------------------------------------------------------

## 36. Traceability 핵심

Evidence Domain의 가장 중요한 기능은 단순 파일 관리가 아니라
**Traceability**다.

시스템은 다음 질문에 답할 수 있어야 한다.

``` text
1. 이 WBS 작업에서 필요한 증적은 무엇인가?
2. 그 증적은 실제로 제출되었는가?
3. 제출된 증적은 무엇인가?
4. 누가 제출했는가?
5. 언제 제출했는가?
6. 검증되었는가?
7. 누가 검증했는가?
8. 반려되었다면 이유는 무엇인가?
9. 검수에서 통과했는가?
10. 아직 빠진 증적은 무엇인가?
```

------------------------------------------------------------------------

## 37. 구현 순서

``` text
1. EVIDENCE_DOMAIN_DESIGN.md
        ↓
2. Entity / Enum
        ↓
3. Repository
        ↓
4. DTO
        ↓
5. Mapper
        ↓
6. Specification
        ↓
7. Service
        ↓
8. Controller
        ↓
9. Requirement API
        ↓
10. Evidence API
        ↓
11. Verification API
        ↓
12. Inspection API
        ↓
13. Swagger Verification
        ↓
14. Frontend Type
        ↓
15. Frontend API Client
        ↓
16. Requirement UI
        ↓
17. Evidence UI
        ↓
18. Verification UI
        ↓
19. Inspection UI
        ↓
20. API Integration
        ↓
21. Browser Verification
        ↓
22. E2E
        ↓
23. Documentation
        ↓
24. Commit
        ↓
25. Push
        ↓
26. PR / Review
        ↓
27. develop Integration Test
```

------------------------------------------------------------------------

## 38. Git Branch

``` text
feature/evidence-management
```

권장 Commit:

``` text
feat: implement evidence domain entities
feat: implement evidence requirement api
feat: implement evidence api
feat: implement evidence verification workflow
feat: implement inspection api
feat: implement evidence management ui
feat: implement evidence verification ui
feat: implement inspection ui
test: add evidence domain tests
test: verify evidence workflow e2e
docs: update evidence domain documentation
```

------------------------------------------------------------------------

## 39. 개발 완료 기준

``` text
[ ] Requirement CRUD
[ ] Evidence CRUD
[ ] Requirement ↔ Evidence 연결
[ ] Project/WBS 연결
[ ] Requirement Status
[ ] Verification Workflow
[ ] Inspection
[ ] Search / Filter
[ ] Pagination / Sorting
[ ] Summary
[ ] Swagger 검증
[ ] Frontend UI
[ ] API Integration
[ ] Browser Verification
[ ] E2E
[ ] Build Success
[ ] Documentation
[ ] Git Commit
[ ] Push
[ ] develop Integration
```

------------------------------------------------------------------------

## 40. Definition of Done

### Backend

``` text
./gradlew clean build
```

성공.

### Frontend

``` text
npm run build
```

성공.

### API

Swagger 기준 주요 API 전체 정상.

### UI

``` text
Requirement
→ Evidence
→ Verification
→ Inspection
```

전체 흐름 정상.

### E2E

``` text
Requirement 생성
→ Evidence 등록
→ PRESENT
→ APPROVED
→ Inspection PASSED
```

### Documentation

``` text
docs/design/EVIDENCE_DOMAIN_DESIGN.md
docs/_meta/CHANGELOG.md
docs/_meta/DEVELOPMENT_ROADMAP.md
docs/development/backend/
docs/development/frontend/
```

------------------------------------------------------------------------

## 41. 설계 원칙

### 원칙 1. Requirement와 Evidence를 분리한다.

``` text
"필요한 것"
≠
"실제로 제출된 것"
```

### 원칙 2. Presence와 Verification을 분리한다.

``` text
PRESENT
≠
APPROVED
```

### 원칙 3. Inspection은 Evidence의 상위 검수 개념으로 둔다.

``` text
Evidence Verification
→ 개별 증적 검증

Inspection
→ 프로젝트/단계 단위 공식 검수
```

### 원칙 4. V1에서는 파일 저장소보다 업무 흐름을 먼저 완성한다.

``` text
Domain
→ Workflow
→ Traceability
→ Storage
```

### 원칙 5. 기존 PMIS 공통 패턴을 재사용한다.

``` text
ApiResponse
BusinessException
GlobalExceptionHandler
ErrorCode
BaseEntity
SearchPageableFactory
Swagger
Project/WBS linkage
```

### 원칙 6. Evidence Domain은 PMO 업무의 증적 추적성을 중심으로 한다.

CRUD 자체가 목적이 아니다.

``` text
"What should exist?"
"What actually exists?"
"Was it verified?"
"Was it inspected?"
"What is missing?"
```

------------------------------------------------------------------------

## 42. 최종 Domain 구조

``` text
                        ┌───────────────┐
                        │    Project    │
                        └───────┬───────┘
                                │
                 ┌──────────────┴──────────────┐
                 │                             │
                 ▼                             ▼
          ┌─────────────┐              ┌─────────────┐
          │     WBS     │              │ Inspection  │
          └──────┬──────┘              └──────┬──────┘
                 │                             │
                 ▼                             ▼
      ┌─────────────────────┐        ┌─────────────────┐
      │ Evidence Requirement│◄───────│ Inspection Item │
      └──────────┬──────────┘        └─────────────────┘
                 │
                 ▼
          ┌─────────────┐
          │   Evidence  │
          └──────┬──────┘
                 │
                 ▼
          ┌─────────────┐
          │ Verification│
          └─────────────┘
```

------------------------------------------------------------------------

## 43. Document History

  Version   Date         Description
  --------- ------------ ---------------------------------------------
  1.0       2026-10-02   Initial Evidence & Inspection Domain Design
