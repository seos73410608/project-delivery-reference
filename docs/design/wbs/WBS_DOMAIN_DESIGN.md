# WBS Domain Design

> PMIS (Project Management Information System)  
> Version: 1.0  
> Domain: WBS (Work Breakdown Structure)  
> Status: Design Confirmed  
> Last Updated: 2026-08-11

---

# 1. 개요

WBS(Work Breakdown Structure)는 프로젝트의 전체 업무 범위를 계층적으로 분해하고 관리하기 위한 핵심 Domain이다.

PMIS에서는 WBS를 단순한 업무 목록으로 관리하지 않고, Project를 Root로 하여 하위 업무를 계층 구조로 표현하는 핵심 프로젝트 관리 데이터로 설계한다.

WBS는 향후 Schedule, Progress, Issue, Risk, Change 및 Report Domain에서 프로젝트 수행 데이터를 연결하기 위한 기준 데이터로 활용한다.

---

# 2. 설계 목표

- 프로젝트 업무를 계층적으로 관리
- Project와 WBS의 명확한 관계 정의
- Parent/Child 기반 WBS Tree 구성
- WBS 식별 코드 관리
- WBS 순서 관리
- WBS 상태 관리
- 향후 Schedule 및 Progress Domain과의 연계 기반 확보
- PMO 업무에서 사용하는 실제 WBS 관리 방식 반영

---

# 3. Domain 관계

```text
Project
   │
   │ 1:N
   ▼
 WBS
```

WBS는 자기참조 관계를 통해 계층 구조를 구성한다.

```text
Project
   │
   ├── WBS-1
   │    ├── WBS-1.1
   │    │    ├── WBS-1.1.1
   │    │    └── WBS-1.1.2
   │    └── WBS-1.2
   │
   └── WBS-2
```

관계는 다음과 같이 정의한다.

```text
Project 1 ───── N Wbs

Wbs 1 ───── N Wbs
           parent
```

---

# 4. WBS Entity

| Field | Type | Required | Description |
|---|---|---:|---|
| id | Long | Y | WBS PK |
| project | Project | Y | 소속 프로젝트 |
| parent | Wbs | N | 상위 WBS |
| wbsCode | String | Y | WBS 업무 식별 코드 |
| wbsName | String | Y | WBS 명 |
| description | String | N | WBS 설명 |
| level | Integer | Y | WBS 계층 Level |
| sortOrder | Integer | Y | 동일 Parent 내 정렬 순서 |
| status | WbsStatus | Y | WBS 상태 |

Entity 구조:

```text
Wbs
├── id
├── project
├── parent
├── wbsCode
├── wbsName
├── description
├── level
├── sortOrder
└── status
```

---

# 5. Project 관계

WBS는 Project를 참조하는 단방향 Many-to-One 관계로 설계한다.

```java
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "project_id", nullable = false)
private Project project;
```

Project Entity에는 WBS Collection을 직접 보유하지 않는다.

```java
@OneToMany(...)
private List<Wbs> wbsList;
```

와 같은 양방향 관계는 사용하지 않는다.

WBS 조회가 필요한 경우 Repository에서 Project ID를 기준으로 조회한다.

```text
findByProjectId(projectId)
```

---

# 6. WBS Parent 관계

WBS는 자기참조 Many-to-One 관계를 사용한다.

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "parent_id")
private Wbs parent;
```

최상위 WBS는 Parent가 없다.

```text
WBS 1
parent = null
level = 1
```

하위 WBS는 상위 WBS를 참조한다.

```text
WBS 1.1
parent = WBS 1
level = 2
```

---

# 7. WBS Level

`level`은 데이터베이스에 저장한다.

```text
1     → Level 1
1.1   → Level 2
1.1.1 → Level 3
```

현재 버전에서는 최대 Level을 별도로 제한하지 않는다.

단, Parent 변경 시 자기 자신 또는 자신의 하위 WBS를 Parent로 지정하는 순환 참조는 허용하지 않는다.

순환 참조 검증은 Service Layer에서 수행한다.

---

# 8. WBS Code

WBS Code는 시스템에서 자동 생성한다.

```text
1
1.1
1.2
1.2.1
1.2.2
2
2.1
```

WBS Code는 업무 식별을 위한 값이며 Database PK와는 별개의 개념이다.

```text
id      = Database PK
wbsCode = Business Identifier
```

---

# 9. WBS Code 중복 정책

WBS Code는 전체 시스템에서 Unique하지 않고 동일 Project 내부에서 Unique해야 한다.

```text
Project A
└── 1

Project B
└── 1
```

위 구조는 허용한다.

반면 동일 Project에서 동일 Code가 중복되는 것은 금지한다.

Database에서는 다음 Unique Constraint를 적용한다.

```text
UNIQUE(project_id, wbs_code)
```

Constraint 이름:

```text
uk_wbs_project_code
```

---

# 10. WBS Code 재번호 정책

기존 WBS Code는 삭제 또는 순서 변경을 이유로 자동 재번호화하지 않는다.

예:

```text
1
2
3
```

에서 `2`가 삭제되어도 `1`, `3`으로 유지한다.

이는 기존 WBS를 참조하는 일정, 이슈, 보고서 등의 데이터가 향후 존재할 수 있기 때문이다.

---

# 11. Sort Order

`wbsCode`와 화면 정렬 순서는 별도로 관리한다.

```text
wbsCode    sortOrder
--------------------
1          10
2          20
3          30
```

`wbsCode`는 업무 식별을 담당하고 `sortOrder`는 UI 및 Tree 표시 순서를 담당한다.

초기 Sort Order는 10 단위 증가 방식으로 생성한다.

---

# 12. WBS Status

```java
public enum WbsStatus {

    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    ON_HOLD,
    CANCELLED
}
```

| Status | Description |
|---|---|
| PLANNED | 계획 |
| IN_PROGRESS | 진행 |
| COMPLETED | 완료 |
| ON_HOLD | 보류 |
| CANCELLED | 취소 |

---

# 13. WBS 삭제 정책

하위 WBS가 존재하는 경우 상위 WBS를 삭제할 수 없다.

```text
1
├── 1.1
└── 1.2
```

이 상태에서 `1` 삭제를 요청하면 삭제를 거부한다.

Database Cascade Remove는 사용하지 않는다.

---

# 14. Project 삭제와 WBS

Project와 WBS는 Cascade Remove로 연결하지 않는다.

Project 삭제 요청 시 WBS가 존재하면 삭제를 거부한다.

```text
Project 삭제
    │
    ▼
WBS 존재?
 ┌──┴──┐
Yes    No
 │      │
 ▼      ▼
거부   삭제
```

---

# 15. Schedule과의 책임 분리

WBS는 업무 구조를 정의하고 Schedule은 시간 정보를 관리한다.

WBS Entity에 일정 관련 필드를 직접 포함하지 않는다.

```text
Project
   ↓
WBS
   ↓
Schedule
```

---

# 16. Progress와의 책임 분리

WBS에 직접 Progress Rate를 저장하지 않는다.

Progress는 향후 Schedule/Task 실행 데이터와 연결하여 관리한다.

```text
WBS
 ↓
Schedule / Task
 ↓
Progress
```

---

# 17. PMIS 전체 Domain 관계

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

Dashboard에서는 WBS, Schedule, Progress, Issue, Risk, Change 등의 데이터를 집계하여 프로젝트 전체 상태를 표현한다.

---

# 18. Entity 책임

WBS Entity는 자신의 상태 변경 행위를 가진다.

예상 행위:

```java
update(...)
changeStatus(...)
changeSortOrder(...)
```

무분별한 Setter 기반 상태 변경은 사용하지 않는다.

---

# 19. Service Layer 책임

WBS Service는 다음 책임을 가진다.

### 생성

- Project 존재 여부 검증
- Parent 존재 여부 검증
- Parent가 동일 Project에 속하는지 검증
- WBS Code 생성
- Level 계산
- Sort Order 생성
- 기본 Status 설정

### 수정

- WBS 존재 여부 검증
- WBS 명/설명 변경
- Parent 변경 시 계층 검증
- 순환 참조 검증
- 필요 시 Level 재계산

### 삭제

- WBS 존재 여부 검증
- 하위 WBS 존재 여부 확인
- 하위 WBS가 존재하면 삭제 거부

### 조회

- Project별 WBS 조회
- 단일 WBS 조회
- Root WBS 조회
- Tree 구조 조회

---

# 20. Repository 예상 API

초기 Repository는 필요한 조회 중심으로 구성한다.

```text
findByProjectId(projectId)
findByProjectIdAndId(projectId, id)
findByProjectIdAndParentIsNull(projectId)
existsByProjectIdAndWbsCode(projectId, wbsCode)
existsByParentId(parentId)
findByParentId(parentId)
```

---

# 21. API 설계 방향

WBS API는 Project 하위 Resource 형태로 설계한다.

```text
GET    /api/projects/{projectId}/wbs
POST   /api/projects/{projectId}/wbs
GET    /api/projects/{projectId}/wbs/{wbsId}
PUT    /api/projects/{projectId}/wbs/{wbsId}
DELETE /api/projects/{projectId}/wbs/{wbsId}
```

향후 필요에 따라 Tree 조회 및 순서 변경 API를 추가한다.

---

# 22. 개발 순서

```text
WBS Design Document
        ↓
WbsStatus
        ↓
Wbs Entity
        ↓
WbsRepository
        ↓
Wbs DTO
        ↓
Wbs Service
        ↓
WbsController
        ↓
Validation
        ↓
Exception Code
        ↓
Test
        ↓
Swagger
        ↓
Development History
        ↓
CHANGELOG
        ↓
Commit
```

문서를 먼저 작성하고 구현 결과가 설계와 일치하는지 검증한다.

---

# 23. V1 범위

- Project별 WBS 생성
- Project별 WBS 조회
- 단일 WBS 조회
- WBS 수정
- WBS 삭제
- Parent/Child 계층 구조
- WBS Level
- WBS Code 자동 생성
- Sort Order
- WBS Status
- 기본 Validation
- 순환 참조 방지
- 하위 WBS 존재 시 삭제 방지
- Project별 WBS Code Unique 보장

---

# 24. V1 제외 범위

- Gantt Chart
- Calendar
- WBS Drag & Drop UI
- Schedule 상세 관리
- Progress 계산
- Baseline 관리
- Resource Allocation
- Cost Management
- Earned Value Management
- WBS Versioning
- WBS Approval Workflow
- AI 기반 WBS 생성

---

# 25. 최종 설계

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

PMIS 전체 업무 흐름:

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

# 26. 설계 원칙 요약

1. WBS는 Project의 하위 Domain이다.
2. Project → WBS는 단방향 Many-to-One 관계를 사용한다.
3. WBS는 자기참조 Parent 구조를 사용한다.
4. `level`은 DB에 저장한다.
5. `wbsCode`는 시스템에서 자동 생성한다.
6. WBS Code는 Project 내부에서만 Unique하다.
7. 기존 WBS Code는 자동 재번호화하지 않는다.
8. `sortOrder`는 WBS Code와 별도로 관리한다.
9. 하위 WBS가 존재하면 상위 WBS를 삭제하지 않는다.
10. Project 삭제 시 WBS가 존재하면 삭제하지 않는다.
11. WBS에는 Schedule 정보를 직접 저장하지 않는다.
12. WBS에는 Progress Rate를 직접 저장하지 않는다.
13. 순환 참조를 허용하지 않는다.
14. Domain 상태 변경은 Entity Method를 통해 수행한다.
15. WBS V1은 핵심 Tree 관리 기능에 집중한다.

---

# 27. Document History

| Version | Date | Author | Description |
|---|---|---|---|
| 1.0 | 2026-08-11 | Seo Seokhyeon | Initial WBS Domain Design |

---

# End of Document
