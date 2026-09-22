# PMIS Search Filter Design

> Version: 1.0  
> Last Updated: 2026-09-22  
> Project: Project Management Information System (PMIS)  
> Scope: Backend Search API / Frontend Integration Contract

---

## 1. 목적

본 문서는 PMIS의 Domain별 목록 조회 및 검색 API에서 사용하는 검색 파라미터, 페이징, 정렬, 키워드 검색 및 Domain 전용 필터의 공통 규칙을 정의한다.

목적은 모든 Domain의 검색 구현을 하나의 DTO 또는 하나의 Specification으로 강제하는 것이 아니라, Frontend와 Backend가 동일한 검색 API 계약을 사용할 수 있도록 공통 규칙과 Domain별 차이를 명확히 정의하는 것이다.

### 핵심 원칙

1. Project 하위 관리 Domain은 공통 검색 파라미터를 사용한다.
2. Domain 고유 검색 조건은 Domain이 책임진다.
3. Specification은 Domain별 Entity 구조와 검색 의미를 고려하여 독립적으로 유지한다.
4. Pagination과 Sorting의 파라미터 명칭과 의미는 공통화하되, 기본 정렬값과 허용 정렬 필드는 Domain별로 관리한다.
5. Frontend는 공통 검색 모델을 재사용할 수 있지만 Domain별 필터 모델은 별도로 유지한다.
6. Calendar, Period, WBS 계층 검색 등 특수 검색은 일반 목록 검색 계약과 분리한다.

---

## 2. 적용 범위

### 2.1 Project-scoped Search 대상

- Issue
- Risk
- Change
- Schedule
- WBS

### 2.2 Project Search

Project는 최상위 관리 Domain이므로 Project-scoped Search와 별도의 검색 계약을 사용한다.

### 2.3 별도 계약으로 관리하는 검색

- Schedule Calendar Search
- Schedule Period / Overlap Search
- WBS Tree / Hierarchy 조회
- Dashboard 전용 집계 조회
- AI/RAG 전용 검색

---

## 3. Search Architecture

```text
                    PMIS Search
                        |
          +-------------+-------------+
          |                           |
   Project Search            Project-scoped Search
          |                           |
       Project          +------+------+------+------+
                        |      |      |      |
                      Issue   Risk   Change Schedule WBS
```

Project-scoped Search의 공통 파라미터:

```text
projectId
keyword
page
size
sortBy
direction
```

Domain별 추가 필터는 각 Domain의 SearchRequest와 Specification에서 관리한다.

---

## 4. Project-scoped Search Contract

| Parameter | Type | Required | Default | Description |
|---|---|---:|---:|---|
| projectId | Long | No | - | Project ID 기준 검색 범위 |
| keyword | String | No | - | Domain에서 정의한 문자열 필드 부분 검색 |
| page | Integer | No | 0 | 0-based 페이지 번호 |
| size | Integer | No | 20 | 페이지 크기 |
| sortBy | String | No | Domain별 | 정렬 기준 |
| direction | String | No | Domain별 | ASC / DESC |

예:

```http
GET /api/issues?projectId=2&keyword=server&page=0&size=20&sortBy=id&direction=DESC
GET /api/risks?projectId=2&status=OPEN&page=0&size=20
GET /api/changes?projectId=2&priority=CRITICAL&page=0&size=20
GET /api/wbs?projectId=2&status=IN_PROGRESS&page=0&size=20
```

---

## 5. Project Scope Contract

### 5.1 Global Search API

```http
GET /api/{domain}?projectId={projectId}
```

예:

```http
GET /api/issues?projectId=2
GET /api/risks?projectId=2
GET /api/changes?projectId=2
GET /api/schedules?projectId=2
GET /api/wbs?projectId=2
```

### 5.2 Project-scoped API

```http
GET /api/projects/{projectId}/issues
GET /api/projects/{projectId}/risks
GET /api/projects/{projectId}/changes
GET /api/projects/{projectId}/schedules
GET /api/projects/{projectId}/wbs
```

이 경우 URL의 `{projectId}`가 검색 범위를 결정한다.

Frontend는 Project-scoped API에서 `projectId` Query Parameter를 중복 전송하지 않는 것을 권장한다.

Path Variable과 Query Parameter가 동시에 존재하는 경우:

```text
Path Variable projectId > Request Query projectId
```

를 적용한다.

Backend에서는 Domain별 Specification이 실제 Entity 구조에 맞게 Project 범위를 적용한다.

---

## 6. Pagination Contract

### 6.1 기본 규칙

| 항목 | 규칙 |
|---|---|
| page | 0-based |
| 기본 page | 0 |
| size | 기본 20 |
| 최소 size | 1 |
| 최대 size | 100 |

예:

```http
GET /api/issues?page=0&size=20
GET /api/issues?page=1&size=20
```

### 6.2 Backend 구현

현재 Domain별 Java 구현 방식은 동일하게 강제하지 않는다.

- Issue: SearchRequest
- Risk: SearchRequest
- Change: SearchRequest
- Schedule: SearchRequest
- WBS: SearchRequest
- Project: Spring Pageable

API 수준에서 `page`, `size`의 의미만 표준화한다.

---

## 7. Sorting Contract

### 7.1 공통 Parameter

```text
sortBy
direction
```

`direction`은:

```text
ASC
DESC
```

만 사용한다.

### 7.2 기본 정렬

Domain별 업무 특성에 따라 기본 정렬을 다르게 유지한다.

| Domain | Default Sort |
|---|---|
| Issue | Domain 구현 기준 |
| Risk | `id DESC` |
| Change | `id DESC` |
| Schedule | `sortOrder ASC` |
| WBS | `sortOrder ASC` |
| Project | Pageable / Controller 구현 기준 |

### 7.3 허용 정렬 필드

Frontend는 Backend가 허용하는 `sortBy` 값만 전송한다.

예:

WBS:

```text
wbsCode
wbsName
sortOrder
createdAt
```

Schedule:

```text
id
scheduleName
startDate
endDate
sortOrder
createdAt
updatedAt
```

임의 Entity Property를 Frontend에서 전송하는 방식은 권장하지 않는다.

---

## 8. Keyword Search Contract

### 8.1 기본 방식

Project-scoped Domain의 `keyword`는 기본적으로:

```text
대소문자 무시
+
부분 일치
```

를 사용한다.

### 8.2 Domain별 검색 대상

| Domain | Keyword Fields |
|---|---|
| Issue | title, description |
| Risk | riskKey, title, description |
| Change | changeKey, title, description |
| Schedule | scheduleName, description |
| WBS | wbsCode, wbsName |

Project는 `keyword` 대신 개별 검색 필드를 사용한다.

### 8.3 AND / OR

하나의 keyword가 여러 필드에 적용되면 OR로 결합한다.

```text
field1 LIKE keyword
OR
field2 LIKE keyword
```

서로 다른 Filter Parameter는 AND로 결합한다.

예:

```text
projectId = 1
AND status = IN_PROGRESS
AND (
    wbsCode LIKE '%server%'
    OR wbsName LIKE '%server%'
)
```

### 8.4 문자열 정규화

1. null / blank 확인
2. trim
3. 대소문자 무시 검색을 위한 normalization

---

## 9. Specification Contract

Specification은 Domain별로 유지한다.

```text
IssueSpecification
RiskSpecification
ChangeSpecification
ScheduleSpecification
WbsSpecification
ProjectSpecification
```

공통 Search Parameter가 존재한다고 해서 공통 Specification을 강제하지 않는다.

예를 들어 `projectId`라는 동일한 API Parameter라도 JPA Entity 구조가 다르다.

```text
WBS      -> project.id
Risk     -> projectId
Change   -> projectId
Issue    -> project.id
```

따라서 Common Specification으로 통합하면 오히려 Domain 결합도가 높아질 수 있다.

---

## 10. Domain-specific Filter Contract

### 10.1 Issue

공통:

```text
projectId
keyword
page
size
sortBy
direction
```

Domain:

```text
status
priority
assigneeId
```

Keyword:

```text
title
description
```

### 10.2 Risk

공통:

```text
projectId
keyword
page
size
sortBy
direction
```

Domain:

```text
status
priority
probability
impact
assigneeId
```

Keyword:

```text
riskKey
title
description
```

### 10.3 Change

공통:

```text
projectId
keyword
page
size
sortBy
direction
```

Domain:

```text
status
priority
changeType
impactLevel
requesterId
assigneeId
```

Keyword:

```text
changeKey
title
description
```

### 10.4 Schedule

공통:

```text
projectId
keyword
page
size
sortBy
direction
```

Domain:

```text
wbsId
status
```

Keyword:

```text
scheduleName
description
```

특수 검색:

```text
Period Search
Calendar Search
Overlap Search
```

특수 검색은 일반 List Search와 별도 API/Request Contract로 관리한다.

### 10.5 WBS

공통:

```text
projectId
keyword
page
size
sortBy
direction
```

Domain:

```text
status
parentId
```

Keyword:

```text
wbsCode
wbsName
```

`parentId`는 WBS 계층 탐색과 관련된 Domain 전용 필터다.

---

## 11. Project Search Contract

Project는 Project-scoped Search와 별도로 관리한다.

### 11.1 Query Parameters

| Parameter | Type | Description |
|---|---|---|
| projectCode | String | 프로젝트 코드 |
| projectName | String | 프로젝트명 |
| customerName | String | 고객사 |
| projectManager | String | 프로젝트 관리자 |
| status | Enum | 프로젝트 상태 |
| priority | Enum | 프로젝트 우선순위 |
| startDateFrom | LocalDate | 시작일 From |
| startDateTo | LocalDate | 시작일 To |
| endDateFrom | LocalDate | 종료일 From |
| endDateTo | LocalDate | 종료일 To |

### 11.2 문자열 검색

다음 필드는 대소문자 무시 부분 일치 검색을 사용한다.

```text
projectCode
projectName
customerName
projectManager
```

### 11.3 날짜 검색

```text
startDate >= startDateFrom
startDate <= startDateTo

endDate >= endDateFrom
endDate <= endDateTo
```

### 11.4 Pagination / Sorting

현재 Project API는 `ProjectSearchRequest`와 Spring `Pageable`을 함께 사용한다.

일반적인 형태:

```http
GET /api/projects?page=0&size=20&sort=projectName,asc
```

실제 허용 sort property는 Controller/Service의 Pageable 설정을 기준으로 한다.

---

## 12. Controller Binding Contract

SearchRequest DTO를 사용하는 Controller는 Query Parameter binding을 명시적으로 유지한다.

```java
@GetMapping
public ApiResponse<?> search(
        @ModelAttribute SearchRequest request
) {
    ...
}
```

Project처럼 Pageable을 사용하는 경우:

```java
@GetMapping
public ApiResponse<?> search(
        @ModelAttribute ProjectSearchRequest request,
        Pageable pageable
) {
    ...
}
```

검색 조건은 Request Body가 아닌 Query Parameter로 전달한다.

---

## 13. API Response Contract

목록 검색 API는 Spring Data `Page` 기반 결과를 사용한다.

Frontend에서 주요하게 사용하는 정보:

```text
content
page number
page size
total elements
total pages
```

Response wrapper는 PMIS 공통 응답 구조인 `ApiResponse`를 따른다.

---

## 14. Frontend Integration Contract

Frontend에서는 Project-scoped Domain의 공통 검색 모델을 재사용할 수 있다.

```typescript
interface ProjectScopedSearchParams {
    projectId?: number;
    keyword?: string;
    page?: number;
    size?: number;
    sortBy?: string;
    direction?: 'ASC' | 'DESC';
}
```

Domain별 확장:

```typescript
interface IssueSearchParams extends ProjectScopedSearchParams {
    status?: IssueStatus;
    priority?: IssuePriority;
    assigneeId?: number;
}
```

```typescript
interface RiskSearchParams extends ProjectScopedSearchParams {
    status?: RiskStatus;
    priority?: RiskPriority;
    probability?: RiskProbability;
    impact?: RiskImpact;
    assigneeId?: number;
}
```

```typescript
interface ChangeSearchParams extends ProjectScopedSearchParams {
    status?: ChangeStatus;
    priority?: ChangePriority;
    changeType?: ChangeType;
    impactLevel?: ChangeImpactLevel;
    requesterId?: number;
    assigneeId?: number;
}
```

Frontend 공통화 대상:

```text
SearchParams
Query String 생성
Pagination
Sort 상태
검색어 상태
검색 요청 실행
```

Domain별 유지:

```text
Filter UI
Enum
Filter Label
Domain-specific Query Parameter
검색 대상 필드
특수 검색
```

---

## 15. Frontend Query String Rules

값이 없는 검색 조건은 불필요하게 전송하지 않는 것을 권장한다.

권장:

```http
GET /api/issues?page=0&size=20
```

조건 추가:

```http
GET /api/issues?page=0&size=20&status=OPEN&priority=HIGH
```

검색:

```http
GET /api/issues?page=0&size=20&keyword=server
```

권장하지 않음:

```http
GET /api/issues?keyword=&status=&assigneeId=&page=0&size=20
```

Enum은 Backend가 정의한 문자열 값을 그대로 사용한다.

---

## 16. Search Condition Composition

일반 Domain Search:

```text
Common Scope
AND
Domain Filters
AND
Keyword Group
```

예:

```text
projectId = 2
AND
status = OPEN
AND
priority = HIGH
AND
assigneeId = 10
AND
(
    title LIKE '%server%'
    OR description LIKE '%server%'
)
```

---

## 17. Validation Contract

### Pagination

```text
page >= 0
1 <= size <= 100
```

### Sorting

```text
direction = ASC | DESC
sortBy = Domain에서 허용한 필드
```

### ID

```text
projectId > 0
assigneeId > 0
requesterId > 0
wbsId > 0
parentId > 0
```

실제 적용 범위는 Domain별 Service validation 정책을 따른다.

### Date

From/To 조건을 사용하는 Domain은 일반적으로:

```text
From <= To
```

를 검증한다.

---

## 18. Current Backend Implementation

| Domain | Search DTO | Pagination | Specification |
|---|---|---|---|
| Issue | IssueSearchRequest | DTO | IssueSpecification |
| Risk | RiskSearchRequest | DTO | RiskSpecification |
| Change | ChangeSearchRequest | DTO | ChangeSpecification |
| Schedule | ScheduleSearchRequest | DTO | ScheduleSpecification |
| WBS | WbsSearchRequest | DTO | WbsSpecification |
| Project | ProjectSearchRequest | Pageable | ProjectSpecification |

이 차이는 API Contract의 불일치가 아니라 Backend 내부 구현 차이로 관리한다.

---

## 19. Non-Goals

현재 단계에서 다음은 공통 Infrastructure로 강제하지 않는다.

```text
CommonSpecification
CommonSearchRequest 상속
Common Domain Filter DTO
공통 default sort
공통 keyword 대상 필드
공통 JPA projectId Path
공통 Date Specification
Schedule overlap logic
WBS parent hierarchy logic
Project SearchRequest와 Project-scoped SearchRequest 통합
```

---

## 20. Backend Implementation Rules

### 공통화 대상

```text
1. Query Parameter naming
2. Pagination semantic
3. Sorting semantic
4. Keyword semantic
5. Controller binding convention
6. Project scope convention
```

### Domain 유지 대상

```text
1. SearchRequest class
2. Specification
3. Domain filter
4. Default sorting
5. Allowed sorting fields
6. Keyword target fields
7. Domain-specific validation
```

---

## 21. E2E Test Matrix

| Test | Issue | Risk | Change | Schedule | WBS | Project |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| 전체 조회 | O | O | O | O | O | O |
| projectId | O | O | O | O | O | - |
| keyword | O | O | O | O | O | 개별 필드 |
| status | O | O | O | O | O | O |
| priority | O | O | O | O | - | O |
| Domain Filter | O | O | O | O | O | O |
| pagination | O | O | O | O | O | O |
| sorting | O | O | O | O | O | O |
| composite filter | O | O | O | O | O | O |
| invalid page | O | O | O | O | O | O |
| invalid size | O | O | O | O | O | O |

추가 테스트:

```text
Schedule
- period search
- overlap search
- calendar search

WBS
- parentId
- top-level hierarchy behavior

Project
- date range
- projectCode
- projectName
- customerName
- projectManager
```

---

## 22. Known Issues / Backlog

### 22.1 Issue Keyword CLOB

Issue keyword 검색 과정에서 `description` 필드의 DB mapping과 `lower()` 함수 조합에 따른 오류가 확인되었다.

```text
InvalidDataAccessResourceUsageException
Parameter of function 'lower()' has type STRING,
but argument is mapped to CLOB
```

Common Search Contract 문제가 아니라 Issue Domain 검색 구현 문제로 관리한다.

### 22.2 WBS parentId null 의미

WBS SearchRequest의 주석에는 `parentId = null`을 최상위 WBS 검색에 사용할 수 있다고 정의되어 있으나, 현재 Specification은 `parentId != null`일 때만 조건을 추가한다.

다음 의미를 별도 확정한다.

```text
parentId 미전송
parentId = null
parentId = 특정 ID
```

### 22.3 Domain별 Sort Whitelist

Risk/Change 등 일부 Domain의 `sortBy` 입력값에 대해 Domain Service에서 허용 필드를 명시적으로 제한하는지 점검한다.

### 22.4 Project Pagination Contract

Project는 SearchRequest 내부 Pagination이 아니라 Spring Pageable을 사용하므로 Frontend 계약에서 실제 Controller/Service의 Pageable 설정과 허용 sort property를 최종 확인한다.

---

## 23. Contract Change Procedure

```text
1. Backend Search DTO / Specification 변경
        ↓
2. Swagger API 확인
        ↓
3. SEARCH_FILTER_DESIGN.md 변경
        ↓
4. Frontend API Type / Query Builder 변경
        ↓
5. Frontend Filter UI 변경
        ↓
6. Backend E2E
        ↓
7. Frontend E2E
        ↓
8. Git Commit / Push
```

검색 Parameter를 추가하거나 이름을 변경하는 경우 Backend와 Frontend를 임의로 독립 변경하지 않는다.

---

## 24. Implementation Status

### Backend

| Area | Status |
|---|---|
| SearchRequest binding standardization | Completed |
| `@ModelAttribute` convention | Completed |
| Setter-based query binding | Completed |
| Project-scoped search analysis | Completed |
| Domain search mapping analysis | Completed |
| Common search contract | Documented |
| Common DTO extraction | Not applied |
| Common Specification extraction | Not applied |

### Frontend

| Area | Status |
|---|---|
| Common SearchParams design | Planned |
| Common query-string builder | Planned |
| Domain filter mapping | Planned |
| Search UI standardization | Planned |
| E2E search validation | Planned |

---

## 25. Version History

| Version | Date | Description |
|---|---|---|
| 1.0 | 2026-09-22 | Initial Search Filter Contract and Domain Mapping |

---

## Appendix A. Domain Search Summary

```text
Issue
  Common:
    projectId, keyword, page, size, sortBy, direction
  Domain:
    status, priority, assigneeId

Risk
  Common:
    projectId, keyword, page, size, sortBy, direction
  Domain:
    status, priority, probability, impact, assigneeId

Change
  Common:
    projectId, keyword, page, size, sortBy, direction
  Domain:
    status, priority, changeType, impactLevel, requesterId, assigneeId

Schedule
  Common:
    projectId, keyword, page, size, sortBy, direction
  Domain:
    wbsId, status
  Special:
    period / overlap / calendar

WBS
  Common:
    projectId, keyword, page, size, sortBy, direction
  Domain:
    status, parentId

Project
  Search:
    projectCode, projectName, customerName, projectManager,
    status, priority,
    startDateFrom, startDateTo,
    endDateFrom, endDateTo
  Pagination:
    Pageable
```

## Appendix B. Frontend Integration Principle

Frontend는 Backend의 Domain 구현 방식을 알 필요가 없다.

Frontend가 알아야 하는 것은:

```text
1. API Endpoint
2. Query Parameter
3. Parameter Type
4. Enum Value
5. Pagination Contract
6. Sorting Contract
7. Response Structure
8. Error Contract
```

Backend는:

```text
Entity
Specification
Repository
Service
Validation
```

의 구현을 책임진다.

따라서 Search Filter 공통화의 최종 목표는 코드의 동일화가 아니라 **API Contract의 일관성**이다.
