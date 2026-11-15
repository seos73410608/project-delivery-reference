# REQUIREMENT DOMAIN DESIGN

> Project Management Information System (PMIS)  
> Version: **0.7**  
> Domain: **Requirement Management**  
> Status: **Existing Schema Reviewed / Domain Model Refined / Implementation Review Pending**  
> Last Updated: **2026-11-15**

---

## 1. 문서 목적

본 문서는 PMIS의 **Requirement Domain** 설계 기준을 정의한다.

Requirement Domain은 프로젝트 수행 과정에서 발생하는 요구사항뿐 아니라, 프로젝트 종료 이후에도 지속적으로 관리해야 하는 운영 환경의 요구사항을 포함한다.

요구사항은 특정 프로젝트의 일정이나 작업에 종속되는 데이터가 아니다.

하나의 요구사항은 독립적인 식별 체계를 가지며, 필요에 따라 여러 프로젝트 또는 운영 관리 단위에 적용될 수 있다.

본 설계의 핵심 목적은 다음과 같다.

1. 요구사항 정의와 적용을 분리한다.
2. 요구사항의 변경 이력을 버전 단위로 보존한다.
3. 프로젝트 또는 운영 대상에 적용된 요구사항의 기준 버전을 명확하게 관리한다.
4. 요구사항 이행 상태와 검증 상태를 구분한다.
5. 요구사항의 적용, 이행, 검증, 승인 이력을 추적할 수 있도록 한다.
6. 프로젝트 수행 단계와 운영 단계에서 동일한 요구사항 관리 체계를 사용한다.
7. 운영 서비스·시스템 단위와 개별 구성 항목(CI) 단위의 요구사항 적용을 지원한다.
8. 프로젝트에서 운영으로 이어지는 요구사항의 생명주기를 관리한다.
9. JPA Entity 및 Aggregate 설계 원칙을 활용하여 데이터 정합성과 도메인 책임을 유지한다.
10. Project, WBS, Task, Schedule, Evidence, Change, CMDB 등의 기존 Domain과 책임을 명확하게 구분한다.
11. 기존 PostgreSQL 스키마의 PK, FK, 제약조건 및 명명 규칙과 일관성을 유지한다.
12. 기존 프로젝트 중심 업무 데이터와 신규 운영 중심 업무 데이터가 서로의 책임을 침범하지 않도록 한다.

---

## 2. 기존 DB 스키마 검토 결과

### 2.1 검토 범위

2026-11-15 전달된 PostgreSQL DDL을 기준으로 다음 기존 테이블을 검토하였다.

| Domain | 기존 테이블 | 확인 내용 |
|---|---|---|
| Project | projects | 프로젝트 기본 PK 및 코드 |
| User | users | 사용자 PK 및 역할 |
| Change | changes | 프로젝트 필수 FK |
| Task | tasks | 프로젝트 필수 FK |
| Schedule | schedules | 프로젝트 및 WBS FK |
| WBS | wbs | 프로젝트 및 계층 관계 |
| Evidence | evidence | 프로젝트 및 EvidenceRequirement FK |
| Evidence | evidence_requirements | 프로젝트 증적 요구사항 |
| Evidence | evidence_version | 증적 버전 및 검증 이력 |
| Inspection | inspections | 프로젝트 필수 FK |
| Inspection | inspection_items | EvidenceRequirement 및 Evidence FK |
| CMDB | configuration_items | 실제 CI 기본정보 |
| CMDB | server_details | CI별 서버 상세정보 |
| CMDB | ci_types | CI 유형 |
| CMDB | ci_relationships | CI 간 관계 |
| CMDB | project_configuration_items | 프로젝트와 CI 연결 |
| User | users | 담당자 및 승인자 FK 대상 |

### 2.2 기존 DB에서 확정된 FK 대상

| 업무 개념 | 실제 테이블 | PK |
|---|---|---|
| Project | projects | id |
| User | users | id |
| Configuration Item | configuration_items | id |
| Evidence Requirement | evidence_requirements | id |
| Evidence | evidence | id |
| Task | tasks | id |
| WBS | wbs | id |
| Change | changes | id |
| Inspection | inspections | id |

신규 Requirement Domain은 위 테이블을 직접 참조하는 경우 기존 테이블의 PK를 사용한다.

기존 테이블의 구조를 복제하거나 동일한 업무 개념의 신규 테이블을 중복 생성하지 않는다.

### 2.3 기존 DB와 신규 Domain의 구분

현재 전달된 DDL에는 독립적인 Requirement, RequirementVersion, RequirementApplication, RequirementFulfillment, OperationalScope 테이블이 존재하지 않는다.

따라서 이 문서에서 정의하는 해당 Entity는 신규 Domain으로 도입하는 설계 대상이다.

단, 기존 `evidence_requirements`는 이미 Evidence Domain에서 사용 중인 업무 데이터이므로 신규 Requirement로 대체하지 않는다.

### 2.4 기존 프로젝트 중심 구조의 특성

현재 다음 테이블은 프로젝트 FK를 필수로 가진다.

- tasks
- changes
- inspections
- evidence_requirements
- evidence

따라서 현재 프로젝트 업무 구조는 프로젝트 수행 및 검수 중심으로 설계되어 있다.

신규 Requirement Domain은 이러한 구조를 변경하지 않고 프로젝트와 운영 환경을 공통으로 지원하는 독립적인 적용 모델을 제공한다.

운영 요구사항을 기존 프로젝트 종속 Entity에 억지로 연결하지 않는다.

---

## 3. 핵심 설계 원칙

### 3.1 Requirement는 Project 및 운영 대상에 종속되지 않는다

Requirement는 독립적인 요구사항 정의이다.

Project는 요구사항을 수행하기 위한 업무적·시간적 실행 단위이며, Requirement의 소유자가 아니다.

운영 서비스, 시스템, 서버 등의 운영 관리 단위 역시 Requirement의 소유자가 아니다.

따라서 다음 관계를 기본 원칙으로 한다.

- 하나의 Requirement는 여러 Project에 적용될 수 있다.
- 하나의 Project에는 여러 Requirement가 적용될 수 있다.
- 하나의 Requirement는 여러 운영 관리 단위에 적용될 수 있다.
- 하나의 운영 관리 단위에는 여러 Requirement가 적용될 수 있다.
- Requirement는 Project가 종료되어도 유지된다.
- Requirement는 적용 대상이 변경되거나 운영 대상이 교체되어도 유지된다.
- Project가 완료되어도 Requirement 정의와 과거 이력은 보존한다.

### 3.2 프로젝트와 운영 환경을 공통 적용 모델로 관리한다

RequirementApplication은 특정 RequirementVersion을 특정 적용 대상에 적용한 이력을 관리한다.

적용 대상은 다음 두 가지 유형으로 구분한다.

1. PROJECT
2. OPERATIONAL

두 적용 유형은 동일한 Requirement 정의와 버전 관리 체계를 사용한다.

프로젝트와 운영 환경은 서로 다른 Entity 및 생명주기를 가지므로 RequirementApplication에서 실제 대상 FK를 명시적으로 구분한다.

- PROJECT: project_id
- OPERATIONAL: operational_scope_id

적용 대상 유형과 실제 FK의 일관성은 DB CHECK 제약조건으로 보장한다.

### 3.3 요구사항 정의와 적용을 분리한다

요구사항 정의는 무엇을 만족해야 하는지를 규정한다.

요구사항 적용은 해당 요구사항을 어떤 프로젝트 또는 운영 관리 단위에 어떤 버전으로 적용하는지를 규정한다.

두 개념을 하나의 Entity에 혼합하지 않는다.

### 3.4 요구사항 정의는 버전으로 관리한다

요구사항의 제목, 설명, 유형, 우선순위, 수용 기준, 검증 방법 등은 변경될 수 있다.

따라서 변경 가능한 정의 내용은 RequirementVersion에서 관리한다.

승인된 RequirementVersion은 원칙적으로 수정하지 않는다.

정의 변경이 발생하면 새로운 버전을 생성한다.

### 3.5 적용된 요구사항은 특정 버전을 기준으로 한다

하나의 Requirement가 여러 프로젝트와 운영 대상에 적용될 때 각 적용 건은 서로 다른 RequirementVersion을 사용할 수 있다.

따라서 RequirementApplication은 실제 적용된 RequirementVersion을 명확하게 참조해야 한다.

### 3.6 이행과 검증은 별도 상태로 관리한다

요구사항의 이행 여부와 이행 결과에 대한 검증 여부는 서로 다른 개념이다.

다음 두 상태를 별도 컬럼으로 관리한다.

- fulfillment_status
- verification_status

두 상태를 하나의 상태 컬럼으로 통합하지 않는다.

### 3.7 이력 데이터는 보존한다

요구사항 정의, 프로젝트 적용, 운영 적용, 이행 평가, 검증 결과는 추적성이 중요한 데이터이다.

일반적인 수정·삭제로 과거 이력이 소실되지 않도록 설계한다.

### 3.8 운영 관리 단위와 CMDB CI를 구분한다

운영 요구사항은 서비스·시스템·환경·개별 서버·DB 등 다양한 수준에 적용될 수 있다.

이를 위해 Requirement Domain은 OperationalScope를 운영 요구사항의 적용 단위로 사용한다.

OperationalScope는 CMDB의 CI와 동일한 Entity가 아니다.

OperationalScope는 요구사항을 적용하고 관리하기 위한 운영 관리 단위이며, CI는 실제 구성 항목과 구성정보를 표현한다.

두 모델은 별도의 Entity로 유지하고 명시적인 연결 관계를 제공한다.

### 3.9 적용 대상의 정합성을 명시적으로 보장한다

RequirementApplication은 프로젝트 또는 운영 관리 단위 중 하나에만 적용되어야 한다.

이를 위해 하나의 RequirementApplication에 다음 컬럼을 둔다.

- application_scope_type
- project_id
- operational_scope_id

application_scope_type에 따라 실제 FK 중 하나만 존재하도록 CHECK 제약조건을 적용한다.

---

## 4. Requirement 정의 및 유형

### 4.1 Requirement 정의

Requirement는 프로젝트 또는 운영 환경에서 충족해야 하는 조건, 제약, 정책, 품질 기준 또는 기능적 요구사항을 의미한다.

Requirement는 특정 작업(Task)을 의미하지 않으며, 특정 증적(Evidence)을 의미하지도 않는다.

Requirement는 달성해야 할 조건을 정의한다.

### 4.2 Requirement 유형

| 유형 | 설명 | 예시 |
|---|---|---|
| FUNCTIONAL | 기능 요구사항 | 사용자 인증 기능 제공 |
| NON_FUNCTIONAL | 비기능 요구사항 | 응답 시간 기준 충족 |
| INFRASTRUCTURE | 인프라 요구사항 | 서버 이중화 구성 |
| SECURITY | 보안 요구사항 | 계정 및 접근 통제 |
| OPERATIONAL | 운영 요구사항 | 정기 백업 및 복구 절차 |
| COMPLIANCE | 규정 및 준수 요구사항 | 금융권 보안 기준 준수 |
| PERFORMANCE | 성능 요구사항 | 동시 사용자 처리 기준 |
| AVAILABILITY | 가용성 요구사항 | 서비스 가용성 기준 |

요구사항 유형과 적용 대상 유형은 서로 다른 개념이다.

예를 들어 OPERATIONAL은 요구사항의 내용 유형이며, 운영 대상에 적용되었다는 의미와 동일하지 않다.

### 4.3 요구사항 적용 가능 범위

요구사항의 내용 유형과 별도로 해당 요구사항이 어떤 적용 대상에 적용될 수 있는지를 관리한다.

이를 RequirementVersion의 `applicability_scope`로 정의한다.

| 값 | 설명 |
|---|---|
| PROJECT | 프로젝트 적용 가능 |
| OPERATIONAL | 운영 대상 적용 가능 |
| BOTH | 프로젝트 및 운영 대상 모두 적용 가능 |

RequirementApplication 생성 시 RequirementVersion의 적용 가능 범위와 실제 적용 대상 유형이 일치하는지 검증한다.

적용 가능 범위는 요구사항 정의의 속성이며 실제 적용 여부를 의미하지 않는다.

### 4.4 요구사항 예시

**프로젝트 요구사항**

- 서버 구축 시 지정된 OS 버전을 적용해야 한다.
- WEB/WAS 서버는 승인된 보안 설정을 적용해야 한다.
- 데이터베이스 이관 후 정합성 검증을 완료해야 한다.
- 서비스 오픈 전에 성능 검증을 완료해야 한다.

**운영 요구사항**

- 운영 서버의 계정 권한을 정기적으로 검토해야 한다.
- 데이터베이스 백업 결과를 정기적으로 확인해야 한다.
- 보안 취약점 조치 결과를 관리해야 한다.
- 장애 발생 시 복구 절차를 수행할 수 있어야 한다.

운영 요구사항은 프로젝트가 종료된 이후에도 유지될 수 있다.

---

## 5. Domain 책임 및 경계

| Domain | 기존 또는 신규 Entity | 관리 대상 | Requirement와의 관계 |
|---|---|---|---|
| Project | projects | 프로젝트 기본정보 및 수행 기간 | 프로젝트 적용 대상 |
| User | users | 사용자 및 역할 | 등록자, 담당자, 승인자 |
| WBS | wbs | 프로젝트 작업 분해 구조 | 요구사항 수행 작업과 연결 가능 |
| Task | tasks | 실제 수행 작업 | 요구사항 이행을 위한 실행 수단 |
| Schedule | schedules | 일정 및 기간 | 요구사항 관련 작업의 일정 관리 |
| Evidence Requirement | evidence_requirements | 프로젝트 증적 제출 요구사항 | 요구사항 이행을 입증할 증적 항목 |
| Evidence | evidence | 증적 및 제출 파일 | 요구사항 이행을 입증하는 자료 |
| Inspection | inspections, inspection_items | 프로젝트 검수 | 요구사항 검증을 위한 일부 수단 |
| Change | changes | 변경 요청 및 승인 | 요구사항 변경의 업무적 출발점이 될 수 있음 |
| CMDB | configuration_items | 실제 구성 항목 | 운영 요구사항의 실제 상태 비교에 활용 |
| CMDB | server_details | 서버 상세정보 | 실제 서버 구성정보 관리 |
| CMDB | ci_relationships | CI 간 관계 | 구성 항목 간 기술적 관계 |
| OperationalScope | 신규 | 운영 관리 단위 | 운영 요구사항의 적용 대상 |
| Requirement | 신규 | 요구사항 정의 | 요구조건 및 적용 이력 관리 |

각 Domain은 자신의 책임을 유지한다.

Requirement Domain은 적용 대상과 이행·검증 결과를 관리하지만, 실제 프로젝트 정보, 작업 정보, 증적 파일, 구성정보 자체를 소유하지 않는다.

OperationalScope는 운영 관리 단위를 식별하고 관리하지만 CMDB의 상세 구성정보를 대신 관리하지 않는다.

---

## 6. Core Entity 설계

Requirement Domain의 핵심 Entity는 다음 여섯 가지이다.

1. Requirement
2. RequirementVersion
3. RequirementApplication
4. RequirementFulfillment
5. OperationalScope
6. OperationalScopeCi

RequirementApplication은 적용 대상 유형에 따라 실제 FK를 직접 관리한다.

따라서 프로젝트와 운영 적용을 위한 별도의 유형별 연결 Entity는 사용하지 않는다.

### 6.1 Requirement

#### 6.1.1 정의

Requirement는 요구사항의 장기적인 식별자이자 독립적인 관리 단위이다.

Requirement 자체에는 변경 가능한 요구사항 정의 내용을 중복 저장하지 않는다.

제목, 설명, 유형, 우선순위, 수용 기준 등은 RequirementVersion에서 관리한다.

#### 6.1.2 주요 필드

| 필드 | 설명 |
|---|---|
| id | PK |
| requirement_code | 요구사항 고유 코드 |
| status | 요구사항 생명주기 상태 |
| created_by | 최초 등록자, users.id FK |
| created_at | 등록일시 |
| updated_at | 수정일시 |
| version | 낙관적 잠금용 버전 |

`requirement_code`는 전체 Requirement Domain에서 유일해야 한다.

#### 6.1.3 설계 원칙

- Requirement는 Project와 운영 대상에 독립적으로 존재한다.
- Requirement의 식별 정보와 생명주기를 관리한다.
- 변경 가능한 요구사항 정의 내용은 RequirementVersion에 둔다.
- Requirement의 삭제는 물리 삭제보다 비활성화 또는 폐기를 우선한다.
- 과거 RequirementVersion, RequirementApplication, RequirementFulfillment 이력을 보존한다.
- created_by는 기존 users 테이블을 참조한다.

Requirement의 status는 요구사항 정의 자체의 생명주기를 의미한다.

프로젝트 적용 상태, 운영 적용 상태 또는 이행 상태를 의미하지 않는다.

### 6.2 RequirementVersion

#### 6.2.1 정의

RequirementVersion은 특정 시점의 요구사항 정의 내용을 보존하는 Entity이다.

Requirement의 정의가 변경되면 기존 버전을 덮어쓰지 않고 새로운 버전을 생성한다.

#### 6.2.2 주요 필드

| 필드 | 설명 |
|---|---|
| id | PK |
| requirement_id | Requirement FK |
| version_no | 버전 번호 |
| title | 요구사항 제목 |
| description | 요구사항 상세 설명 |
| requirement_type | 요구사항 유형 |
| applicability_scope | 적용 가능 범위 |
| priority | 우선순위 |
| source | 요구사항 출처 |
| acceptance_criteria | 수용 기준 |
| verification_method | 검증 방법 |
| owner_user_id | 요구사항 책임자, users.id FK |
| owner_organization | 책임 조직 |
| status | 버전 상태 |
| change_reason | 변경 사유 |
| created_by | 등록자, users.id FK |
| approved_by | 승인자, users.id FK |
| approved_at | 승인일시 |
| effective_from | 적용 시작일 |
| effective_to | 적용 종료일 |
| created_at | 생성일시 |

수용 기준과 검증 방법은 초기에는 문자열 또는 JSON 등의 구조화 필드로 관리할 수 있다.

개별 수용 기준의 독립적인 생명주기, 승인, 검증이 필요해지면 별도 Entity로 분리한다.

#### 6.2.3 관계

- Requirement 1 : N RequirementVersion

각 RequirementVersion은 반드시 하나의 Requirement에 속한다.

#### 6.2.4 버전 관리 원칙

- Requirement별 version_no는 중복될 수 없다.
- 승인된 버전은 원칙적으로 수정하지 않는다.
- 정의 변경 시 신규 버전을 생성한다.
- 과거 버전은 삭제하지 않는다.
- 버전 승인과 적용 대상별 적용은 별도 절차이다.
- 신규 버전이 승인되어도 기존 프로젝트 및 운영 적용 버전이 자동 변경되지 않는다.

유일성 제약:

`UNIQUE(requirement_id, version_no)`

RequirementApplication에서 복합 FK를 사용하기 위해 다음 복합 유일성도 고려한다.

`UNIQUE(id, requirement_id)`

#### 6.2.5 현재 버전 관리

초기 설계에서는 Requirement에 `current_version_id`를 두지 않는다.

현재 유효한 버전은 RequirementVersion의 승인 상태와 적용 기간을 기준으로 조회한다.

개별 적용 건에서 사용 중인 버전은 RequirementApplication의 `requirement_version_id`를 기준으로 판단한다.

Requirement의 현재 버전과 개별 적용 건의 기준 버전은 서로 다른 개념이다.

### 6.3 RequirementApplication

#### 6.3.1 정의

RequirementApplication은 특정 Requirement를 특정 프로젝트 또는 운영 관리 단위에 적용한 공통 이력을 관리한다.

Requirement가 무엇을 만족해야 하는지를 정의한다면, RequirementApplication은 해당 요구사항이 어디에 어떤 버전으로 적용되었는지를 정의한다.

RequirementApplication은 프로젝트와 운영 환경에서 공통으로 사용하는 적용 이력의 Aggregate Root이다.

#### 6.3.2 주요 필드

| 필드 | 설명 |
|---|---|
| id | PK |
| requirement_id | Requirement FK |
| requirement_version_id | 적용할 RequirementVersion FK |
| application_scope_type | PROJECT / OPERATIONAL |
| project_id | Project FK, PROJECT 적용 시 필수 |
| operational_scope_id | OperationalScope FK, OPERATIONAL 적용 시 필수 |
| application_status | 적용 상태 |
| application_reason | 적용 사유 |
| applied_at | 적용일시 |
| closed_at | 종료일시 |
| source_application_id | 이전 또는 원천 적용 이력 FK, 선택 |
| transition_type | 적용 관계 유형, 선택 |
| created_by | 등록자, users.id FK |
| created_at | 생성일시 |
| updated_at | 수정일시 |
| version | 낙관적 잠금용 버전 |

`application_scope_type`은 적용 대상 유형을 의미한다.

`project_id`와 `operational_scope_id`는 실제 적용 대상에 대한 명시적인 FK이다.

#### 6.3.3 관계

- Requirement 1 : N RequirementApplication
- RequirementVersion 1 : N RequirementApplication
- Project 1 : N RequirementApplication
- OperationalScope 1 : N RequirementApplication

실제 FK 대상은 다음과 같이 확정한다.

- project_id → projects.id
- operational_scope_id → operational_scopes.id
- requirement_id → requirements.id
- requirement_version_id → requirement_versions.id
- created_by → users.id

RequirementApplication은 반드시 하나의 Requirement와 하나의 RequirementVersion을 참조한다.

참조하는 RequirementVersion은 반드시 해당 Requirement에 속한 버전이어야 한다.

#### 6.3.4 적용 대상 관리

RequirementApplication은 다음 두 가지 형태 중 하나만 허용한다.

**PROJECT 적용**

- application_scope_type = PROJECT
- project_id 필수
- operational_scope_id NULL

**OPERATIONAL 적용**

- application_scope_type = OPERATIONAL
- project_id NULL
- operational_scope_id 필수

위 조건은 서비스 계층에서 검증하고 DB CHECK 제약조건으로도 보장한다.

다형성 `target_type`, `target_id` 구조를 사용하지 않고 실제 Entity FK를 유지한다.

#### 6.3.5 적용 가능 범위 검증

| RequirementVersion.applicability_scope | PROJECT 적용 | OPERATIONAL 적용 |
|---|---|---|
| PROJECT | 허용 | 거부 |
| OPERATIONAL | 거부 | 허용 |
| BOTH | 허용 | 허용 |

적용 가능 범위가 실제 적용 대상 유형과 일치하지 않으면 Application 생성을 거부한다.

#### 6.3.6 적용 이력 관리

RequirementApplication은 단순히 현재 적용 여부를 나타내는 연결 테이블이 아니다.

특정 버전이 특정 대상에 적용된 하나의 적용 에피소드를 의미한다.

- 적용된 RequirementVersion은 해당 Application의 기준 버전이다.
- 기존 Application의 적용 버전을 임의로 변경하지 않는다.
- 새로운 버전을 적용할 때는 새로운 Application을 생성한다.
- 새로운 Application은 필요하면 source_application_id로 이전 적용 이력과 연결한다.
- 과거 Application은 CLOSED 등으로 종료하고 보존한다.
- 프로젝트 적용과 운영 적용은 동일한 RequirementApplication 생명주기를 사용한다.

#### 6.3.7 프로젝트-운영 전환 관계

프로젝트 적용에서 운영 적용으로 전환하는 경우 기존 Application을 운영 Application으로 변경하지 않는다.

새로운 RequirementApplication을 생성하고 이전 Application과 연결한다.

`transition_type`의 초기 후보는 다음과 같다.

| 값 | 설명 |
|---|---|
| OPERATIONAL_HANDOVER | 프로젝트에서 운영으로 인계 |
| REAPPLICATION | 동일 대상 또는 다른 대상에 재적용 |
| SCOPE_CHANGE | 적용 대상 변경 |
| DERIVED_FROM | 기존 적용 건을 기반으로 신규 적용 |

`source_application_id`는 동일한 Requirement에 속한 이전 Application을 참조하도록 검증한다.

하나의 기존 Application에서 여러 운영 Application이 파생될 수 있다.

여러 원천 Application을 하나의 운영 Application에 연결해야 하는 업무가 발생하면 별도 Application 관계 Entity를 검토한다.

#### 6.3.8 적용 중복 관리

동일한 Requirement와 동일한 적용 대상에 활성 Application이 중복 생성되지 않도록 관리한다.

다만 버전 변경, 재적용, 범위 변경 등은 새로운 Application을 생성할 수 있다.

활성 Application의 중복 기준은 다음을 중심으로 정의한다.

- requirement_id
- application_scope_type
- project_id 또는 operational_scope_id
- application_status

DB 부분 유일 인덱스 등 지원 기능을 활용하고 서비스 계층에서도 중복 여부를 검증한다.

정확한 중복 허용 정책은 구현 전 확정한다.

### 6.4 OperationalScope

#### 6.4.1 정의

OperationalScope는 요구사항을 적용하고 지속적으로 관리하기 위한 운영 관리 단위이다.

OperationalScope는 단일 서버에 한정되지 않는다.

서비스, 시스템, 환경, 플랫폼, 데이터베이스, 서버 그룹 등 요구사항의 적용 및 관리가 필요한 단위를 표현한다.

OperationalScope는 CMDB CI와 동일한 개념이 아니다.

#### 6.4.2 주요 필드

| 필드 | 설명 |
|---|---|
| id | PK |
| scope_code | 운영 관리 단위 코드 |
| scope_name | 운영 관리 단위 이름 |
| scope_type | 운영 관리 단위 유형 |
| status | 운영 관리 단위 상태 |
| parent_scope_id | 상위 OperationalScope FK, 선택 |
| owner_user_id | 운영 책임자, users.id FK, 선택 |
| owner_organization | 운영 책임 조직 |
| description | 설명 |
| created_at | 생성일시 |
| updated_at | 수정일시 |
| version | 낙관적 잠금용 버전 |

#### 6.4.3 운영 관리 단위 유형

| 유형 | 설명 | 예시 |
|---|---|---|
| SERVICE | 서비스 단위 | 금융거래 서비스 |
| SYSTEM | 시스템 단위 | 인터넷뱅킹 시스템 |
| ENVIRONMENT | 환경 단위 | 운영 환경 |
| PLATFORM | 플랫폼 단위 | WEB/WAS 플랫폼 |
| DATABASE | 데이터베이스 단위 | 운영 DB 서비스 |
| SERVER_GROUP | 서버 그룹 | 업무 WAS 서버 그룹 |
| CI_SCOPE | 개별 구성 항목 단위 | 특정 운영 서버 |

실제 유형과 분류 체계는 운영관리 및 CMDB 설계에 따라 확정한다.

#### 6.4.4 관계

- OperationalScope 1 : N RequirementApplication
- OperationalScope 1 : N OperationalScopeCi
- OperationalScope 0..1 : N OperationalScope

OperationalScope는 선택적으로 상위 OperationalScope를 참조하여 서비스·시스템·환경 등의 계층을 표현할 수 있다.

계층 관계는 순환 참조가 발생하지 않도록 서비스 계층에서 검증한다.

#### 6.4.5 기존 CMDB와의 관계

현재 CMDB의 실제 CI 기본 테이블은 `configuration_items`이다.

주요 필드는 다음과 같다.

- id
- ci_code
- ci_name
- ci_type_id
- status
- environment
- manager_id
- location
- description

서버 상세정보는 `server_details`에서 관리하며 `ci_id`를 통해 `configuration_items.id`와 연결된다.

OperationalScope는 위 CI 정보를 복제하지 않는다.

운영 관리 단위의 식별과 요구사항 적용 범위만 관리한다.

#### 6.4.6 설계 원칙

- OperationalScope는 Project와 독립적으로 존재한다.
- 운영 조직이 프로젝트와 무관하게 등록할 수 있다.
- 운영 관리 단위의 코드와 식별자는 안정적으로 유지한다.
- 운영 관리 단위가 폐기되더라도 과거 적용 이력은 보존한다.
- 실제 서버·DB·미들웨어 구성정보를 복제하지 않는다.
- 실제 구성정보는 CMDB 또는 해당 운영 Domain에서 관리한다.
- OperationalScope의 상태 변경이 연결된 RequirementApplication의 상태를 자동으로 변경하지 않는다.
- owner_user_id는 기존 users.id를 참조한다.

### 6.5 OperationalScopeCi

#### 6.5.1 정의

OperationalScopeCi는 OperationalScope와 CMDB의 Configuration Item을 연결하는 관계 Entity이다.

운영 서비스·시스템·환경 등의 관리 단위와 실제 구성 항목 간의 관계를 표현한다.

하나의 OperationalScope에는 여러 CI가 연결될 수 있다.

하나의 CI도 업무상 여러 OperationalScope에 포함될 수 있다.

따라서 OperationalScope와 CI는 N:M 관계이며, OperationalScopeCi를 통해 연결한다.

#### 6.5.2 주요 필드

| 필드 | 설명 |
|---|---|
| id | PK |
| operational_scope_id | OperationalScope FK |
| ci_id | CMDB CI FK |
| relation_type | 연결 관계 유형 |
| effective_from | 연결 시작일 |
| effective_to | 연결 종료일 |
| created_at | 생성일시 |

실제 FK는 다음과 같이 확정한다.

- operational_scope_id → operational_scopes.id
- ci_id → configuration_items.id

#### 6.5.3 관계 유형

초기 관계 유형 후보는 다음과 같다.

- CONTAINS
- SUPPORTS
- RUNS_ON
- DEPENDS_ON
- MEMBER_OF

실제 운영 모델에 맞춰 관계 유형을 확정한다.

#### 6.5.4 기존 CI 관계와의 구분

기존 `ci_relationships`는 다음 구조로 CI 간 관계를 관리한다.

- source_ci_id
- target_ci_id
- relationship_type_id
- status
- deactivated_at
- deactivated_by
- deactivation_reason

이는 실제 구성 항목 사이의 관계를 관리하는 CMDB 기능이다.

반면 `operational_scope_ci`는 운영 관리 단위와 실제 CI 사이의 관계를 관리한다.

따라서 두 관계를 하나의 테이블로 통합하지 않는다.

| 구분 | ci_relationships | operational_scope_ci |
|---|---|---|
| 관계 대상 | CI ↔ CI | OperationalScope ↔ CI |
| 목적 | 구성 항목 간 관계 | 운영 관리 범위와 CI 연결 |
| 소유 Domain | CMDB | Requirement / Operational Management |
| 예시 | WAS 서버가 DB 서버에 의존 | 업무 서비스가 WAS 서버를 포함 |

#### 6.5.5 설계 원칙

- OperationalScope와 CI는 별도 Entity로 유지한다.
- OperationalScopeCi는 단순 N:M 연결이 아니라 업무적 관계를 표현한다.
- CI가 교체되거나 운영 범위에서 제외되면 연결 종료일을 기록한다.
- 과거 연결 이력은 보존한다.
- CI의 이름, IP, OS, CPU 등의 구성정보를 OperationalScopeCi에 중복 저장하지 않는다.
- 동일한 관계의 유효기간이 중복되거나 충돌하지 않도록 서비스 계층에서 검증한다.
- CI FK는 기존 configuration_items.id를 사용한다.
- 기존 CMDB CI를 삭제하거나 수정하는 정책은 CMDB Domain의 책임으로 유지한다.

### 6.6 RequirementFulfillment

#### 6.6.1 정의

RequirementFulfillment은 특정 RequirementApplication에 대한 이행 및 검증 평가 기록이다.

요구사항이 실제로 충족되었는지, 검증이 완료되었는지를 관리한다.

RequirementFulfillment은 Requirement 자체의 상태가 아니다.

특정 프로젝트 또는 운영 관리 단위에 적용된 특정 요구사항에 대한 평가 결과이다.

#### 6.6.2 주요 필드

| 필드 | 설명 |
|---|---|
| id | PK |
| requirement_application_id | RequirementApplication FK |
| assessment_type | 평가 유형 |
| assessment_period_start | 평가 대상 기간 시작일, 선택 |
| assessment_period_end | 평가 대상 기간 종료일, 선택 |
| fulfillment_status | 이행 상태 |
| verification_status | 검증 상태 |
| assessed_at | 평가일시 |
| assessor_id | 평가자, users.id FK |
| fulfillment_comment | 이행 결과 및 평가 의견 |
| failure_reason | 미충족 또는 검증 실패 사유 |
| action_plan | 후속 조치 계획 |
| next_assessment_due_at | 다음 평가 예정일, 선택 |
| created_at | 생성일시 |

#### 6.6.3 평가 유형

| 유형 | 설명 |
|---|---|
| INITIAL | 최초 평가 |
| PERIODIC | 정기 평가 |
| REASSESSMENT | 재평가 |
| INCIDENT | 장애·사고 관련 평가 |
| CHANGE | 변경 이후 평가 |

평가 유형은 프로젝트 및 운영 환경 모두에서 사용할 수 있다.

#### 6.6.4 관계

- RequirementApplication 1 : N RequirementFulfillment

평가자는 기존 `users.id`를 참조한다.

기존 Project, Task 등의 담당자 FK와 동일한 사용자 식별 체계를 사용한다.

#### 6.6.5 평가 이력 관리

RequirementFulfillment은 평가 이력을 보존하는 데이터이다.

기존 평가 결과를 덮어쓰기보다는 새로운 평가 기록을 추가하는 방식을 기본으로 한다.

최신 평가 결과는 assessed_at 및 식별자를 기준으로 조회한다.

V1에서는 is_latest 같은 중복 상태 컬럼을 기본적으로 두지 않는다.

평가 기록의 수정은 오기 정정 등 제한적인 경우에만 허용하고, 승인된 평가 결과를 변경해야 하는 경우에는 수정 사유와 수정 이력을 보존한다.

#### 6.6.6 이행 상태와 검증 상태

두 상태는 반드시 별도 컬럼으로 관리한다.

**Fulfillment Status**

| 상태 | 설명 |
|---|---|
| NOT_STARTED | 이행 시작 전 |
| IN_PROGRESS | 이행 진행 중 |
| PARTIALLY_FULFILLED | 일부 이행 |
| FULFILLED | 이행 완료 |
| NOT_FULFILLED | 미이행 |
| ACCEPTED_EXCEPTION | 미이행 사항에 대한 예외 수용 |

**Verification Status**

| 상태 | 설명 |
|---|---|
| NOT_VERIFIED | 검증 전 |
| PENDING | 검증 대기 또는 진행 중 |
| PASSED | 검증 통과 |
| FAILED | 검증 실패 |
| EXCEPTION_APPROVED | 예외 승인 완료 |

ACCEPTED_EXCEPTION은 실제 요구사항이 충족되었다는 의미가 아니다.

요구사항을 충족하지 못했지만 승인된 예외로 처리했다는 의미이다.

예외 처리는 별도의 승인 근거와 이력을 남겨야 한다.

필요한 경우 RequirementExceptionApproval을 별도 Entity로 분리한다.

#### 6.6.7 평가 주기 관리

RequirementFulfillment은 반복 평가 이력을 관리한다.

다만 다음 기능은 별도의 운영관리 기능으로 분리한다.

- 평가 주기 정의
- 평가 일정 자동 생성
- 평가 담당자 지정
- 미수행 알림
- 정기 평가 결과 집계
- 외부 시스템 기반 자동 검증

V1에서는 평가 이력의 반복 기록을 지원하고, 정기 평가 계획은 후속 기능으로 확장한다.

---

## 7. 핵심 Entity 관계 요약

| 기준 Entity | 관계 | 대상 Entity | 의미 |
|---|---|---|---|
| Requirement | 1:N | RequirementVersion | 요구사항 정의 버전 |
| Requirement | 1:N | RequirementApplication | 요구사항 적용 이력 |
| RequirementVersion | 1:N | RequirementApplication | 특정 버전 적용 이력 |
| Project | 1:N | RequirementApplication | 프로젝트 적용 요구사항 |
| OperationalScope | 1:N | RequirementApplication | 운영 적용 요구사항 |
| OperationalScope | 1:N | OperationalScopeCi | 실제 CI 연결 |
| ConfigurationItem | 1:N | OperationalScopeCi | 운영 관리 단위 연결 |
| RequirementApplication | 1:N | RequirementFulfillment | 이행 및 검증 평가 이력 |
| RequirementApplication | N:1 | RequirementApplication | 이전 적용 이력 참조, 선택 |

### 7.1 논리 ERD

```text
Requirement
    |
    +---- RequirementVersion
    |
    +---- RequirementApplication
                |
                +---- RequirementVersion
                |
                +---- Project (선택)
                |       |
                |       +---- projects.id
                |
                +---- OperationalScope (선택)
                |             |
                |             +---- OperationalScopeCi
                |                        |
                |                        +---- configuration_items.id
                |
                +---- RequirementFulfillment
                |             |
                |             +---- users.id (assessor)
                |
                +---- RequirementApplication
                         (source_application_id)

기존 업무 Domain:

Project
    |
    +---- WBS
    +---- Task
    +---- Schedule
    +---- EvidenceRequirement
    +---- Evidence
    +---- Inspection
    +---- Change

CMDB:

configuration_items
    |
    +---- server_details
    |
    +---- ci_relationships
    |
    +---- OperationalScopeCi
```

---

## 8. JPA Entity 및 Aggregate 설계 원칙

### 8.1 Aggregate 경계

| Aggregate Root | 관리 대상 | 설명 |
|---|---|---|
| Requirement | RequirementVersion | 요구사항 정의와 버전 관리 |
| RequirementApplication | 적용 대상 FK 및 적용 상태 | 프로젝트·운영 적용 관리 |
| RequirementFulfillment | 개별 평가 기록 | 이행 및 검증 결과 관리 |
| OperationalScope | OperationalScopeCi | 운영 관리 단위 및 CI 연결 |

RequirementApplication은 Requirement와 RequirementVersion을 참조하지만 별도의 Aggregate Root로 취급한다.

RequirementFulfillment 역시 평가 이력 보존의 독립성이 중요하므로 별도의 Aggregate Root로 관리한다.

OperationalScope는 운영 관리 단위의 식별과 CI 연결을 담당하는 별도 Aggregate Root로 관리한다.

OperationalScopeCi는 OperationalScope의 구성 관계를 표현하는 Entity로 관리한다.

### 8.2 연관관계 방향

기본적으로 자식 Entity에서 부모 Entity를 참조하는 단방향 관계를 우선한다.

예시:

```java
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "requirement_id", nullable = false)
private Requirement requirement;
```

RequirementVersion에서 Requirement를 참조한다.

```java
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "requirement_version_id", nullable = false)
private RequirementVersion requirementVersion;
```

RequirementApplication에서 RequirementVersion을 참조한다.

RequirementApplication은 다음 관계를 명시적으로 가진다.

- Requirement
- RequirementVersion
- Project 또는 OperationalScope 중 하나

RequirementFulfillment은 RequirementApplication을 참조한다.

OperationalScopeCi는 OperationalScope와 실제 CMDB CI를 참조한다.

### 8.3 적용 대상 FK 관리

RequirementApplication은 다음과 같이 설계한다.

```java
private ApplicationScopeType applicationScopeType;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "project_id")
private Project project;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "operational_scope_id")
private OperationalScope operationalScope;
```

실제 Entity 구현 시 applicationScopeType과 두 FK의 일관성을 생성 및 변경 서비스에서 검증한다.

DB CHECK 제약조건도 함께 적용한다.

### 8.4 기존 Entity 참조 원칙

기존 Entity와의 연관관계는 실제 DB FK 구조를 기준으로 정의한다.

| 신규 Entity 필드 | 기존 또는 신규 참조 Entity | FK |
|---|---|---|
| Requirement.created_by | User | users.id |
| RequirementVersion.created_by | User | users.id |
| RequirementVersion.approved_by | User | users.id |
| RequirementVersion.owner_user_id | User | users.id |
| RequirementApplication.project_id | Project | projects.id |
| RequirementApplication.created_by | User | users.id |
| OperationalScope.owner_user_id | User | users.id |
| OperationalScopeCi.ci_id | ConfigurationItem | configuration_items.id |
| RequirementFulfillment.assessor_id | User | users.id |

실제 User Entity의 Java 클래스명 및 패키지는 인증 Domain 구현을 기준으로 한다.

기존 Project의 `project_manager`는 현재 DDL에서 문자열 필드이므로 신규 Requirement Domain의 사용자 FK와 혼동하지 않는다.

### 8.5 양방향 연관관계

양방향 연관관계는 반드시 필요한 경우에만 추가한다.

대량의 버전, 적용 이력, CI 연결 정보를 무조건 로딩하지 않는다.

목록 조회는 Repository Query 또는 DTO Projection을 활용한다.

### 8.6 Cascade 정책

Cascade는 Aggregate 경계 안에서만 신중하게 사용한다.

- Requirement와 RequirementVersion의 생성 관계에서는 필요한 경우 PERSIST를 사용할 수 있다.
- RequirementApplication과 Requirement, RequirementVersion 사이에는 일반적으로 Cascade를 적용하지 않는다.
- RequirementApplication과 Project 또는 OperationalScope 사이에는 Cascade를 적용하지 않는다.
- RequirementFulfillment과 RequirementApplication 사이에도 일반적으로 Cascade를 적용하지 않는다.
- OperationalScope와 CMDB CI 사이에는 Cascade를 적용하지 않는다.
- 이력 데이터에 REMOVE를 적용하지 않는다.
- 모든 연관관계에 CascadeType.ALL을 일괄 적용하지 않는다.

### 8.7 orphanRemoval 정책

다음 Entity에는 기본적으로 적용하지 않는다.

- RequirementVersion
- RequirementApplication
- RequirementFulfillment
- OperationalScopeCi

이들은 추적성과 이력 보존이 필요한 업무 데이터이다.

### 8.8 낙관적 잠금

동시 수정 가능성이 있는 Aggregate Root에는 @Version을 적용한다.

- Requirement
- RequirementApplication
- OperationalScope

승인 후 불변인 RequirementVersion이나 추가만 허용하는 RequirementFulfillment에는 기본적으로 낙관적 잠금이 필요하지 않다.

### 8.9 Enum 저장 방식

상태값은 문자열 기반으로 저장한다.

```java
@Enumerated(EnumType.STRING)
private RequirementStatus status;
```

ORDINAL 방식은 사용하지 않는다.

### 8.10 DB 제약조건

JPA Annotation만으로 데이터 정합성을 보장하지 않는다.

DB 수준에서도 다음 제약을 적용한다.

- PK
- FK
- NOT NULL
- UNIQUE
- CHECK
- 필요한 경우 복합 FK
- 필요한 경우 부분 유일 인덱스

특히 다음 조건을 보장해야 한다.

1. RequirementApplication이 참조하는 RequirementVersion은 동일한 Requirement에 속해야 한다.
2. PROJECT 적용이면 project_id가 필수이고 operational_scope_id는 NULL이어야 한다.
3. OPERATIONAL 적용이면 operational_scope_id가 필수이고 project_id는 NULL이어야 한다.
4. application_scope_type과 실제 FK의 조합은 CHECK 제약조건으로 검증해야 한다.
5. RequirementApplication의 대상 FK는 실제 Project 또는 OperationalScope를 참조해야 한다.
6. OperationalScopeCi는 실제 OperationalScope와 CMDB CI를 참조해야 한다.
7. RequirementApplication의 Requirement와 RequirementVersion 간 소속 관계는 복합 FK로 보장하는 방식을 우선한다.

적용 대상 정합성 CHECK 예시:

```sql
CHECK (
    (
        application_scope_type = 'PROJECT'
        AND project_id IS NOT NULL
        AND operational_scope_id IS NULL
    )
    OR
    (
        application_scope_type = 'OPERATIONAL'
        AND project_id IS NULL
        AND operational_scope_id IS NOT NULL
    )
)
```

Requirement와 RequirementVersion의 소속 정합성은 다음 방식으로 설계한다.

```sql
ALTER TABLE requirement_versions
ADD CONSTRAINT uk_requirement_versions_id_requirement
UNIQUE (id, requirement_id);
```

RequirementApplication에서는 다음 복합 FK를 적용한다.

```sql
FOREIGN KEY (requirement_version_id, requirement_id)
REFERENCES requirement_versions (id, requirement_id)
```

이 방식은 Application이 다른 Requirement에 속한 버전을 참조하는 오류를 DB 수준에서 방지한다.

### 8.11 Many-to-Many 사용 원칙

업무적 의미와 생명주기가 있는 N:M 관계에는 JPA @ManyToMany를 사용하지 않는다.

중간 관계를 별도 Entity로 만든다.

예를 들어 다음과 같다.

- RequirementApplicationTask
- RequirementApplicationEvidenceRequirement
- OperationalScopeCi

---

## 9. Requirement Baseline

### 9.1 정의

Requirement Baseline은 특정 시점에 특정 적용 범위에서 확정한 요구사항 집합을 의미한다.

프로젝트 수행 중 요구사항이 변경되더라도 최초 승인된 기준을 추적할 수 있도록 한다.

Baseline은 프로젝트와 운영 환경 모두에 활용할 수 있는 공통 개념으로 설계한다.

### 9.2 구성

향후 다음 Entity를 도입할 수 있다.

**RequirementBaseline**

| 필드 | 설명 |
|---|---|
| id | PK |
| baseline_name | 기준선 이름 |
| baseline_version | 기준선 버전 |
| scope_type | PROJECT / OPERATIONAL |
| project_id | Project FK, 선택 |
| operational_scope_id | OperationalScope FK, 선택 |
| status | 기준선 상태 |
| approved_by | 승인자, users.id FK |
| approved_at | 승인일시 |
| created_at | 생성일시 |

**RequirementBaselineItem**

| 필드 | 설명 |
|---|---|
| id | PK |
| baseline_id | Baseline FK |
| requirement_application_id | RequirementApplication FK |
| created_at | 생성일시 |

Baseline은 적용 대상 유형에 맞는 실제 FK 하나만 가져야 한다.

Baseline의 scope_type과 project_id, operational_scope_id 간 일관성은 RequirementApplication과 동일한 방식으로 DB CHECK 제약조건을 적용한다.

BaselineItem의 Application이 Baseline과 동일한 대상에 속하는지 검증해야 한다.

이 검증은 서비스 계층에서 우선 보장하고, 필요하면 복합 FK 등 DB 제약조건으로 강화한다.

### 9.3 기준선 원칙

- 승인된 Baseline은 원칙적으로 수정하지 않는다.
- 기준선 변경은 새로운 Baseline 버전 또는 변경 이력으로 관리한다.
- 기존 Baseline은 보존한다.
- Baseline은 Requirement의 소유자가 아니다.
- Baseline은 특정 적용 범위의 요구사항 기준을 고정하는 역할이다.

Baseline은 핵심 Entity와 별도로 관리하는 확장 기능이다.

---

## 10. 상태 모델

### 10.1 Requirement Status

| 상태 | 설명 |
|---|---|
| DRAFT | 작성 중 |
| ACTIVE | 사용 중 |
| DEPRECATED | 사용 중단 예정 |
| RETIRED | 폐기 |

### 10.2 RequirementVersion Status

| 상태 | 설명 |
|---|---|
| DRAFT | 작성 중 |
| IN_REVIEW | 검토 중 |
| APPROVED | 승인 완료 |
| REJECTED | 반려 |
| SUPERSEDED | 후속 버전으로 대체 |
| RETIRED | 사용 종료 |

SUPERSEDED는 후속 버전이 생성되었다는 사실과 신규 적용 제한을 표현한다.

기존 Application이 참조하는 과거 버전은 기존 적용 건의 기준으로 보존한다.

신규 적용 가능 여부와 기존 적용 건의 유효성은 구분한다.

### 10.3 RequirementApplication Status

| 상태 | 설명 |
|---|---|
| PLANNED | 적용 예정 |
| ACTIVE | 적용 중 |
| SUSPENDED | 적용 보류 |
| CLOSED | 적용 종료 |

CLOSED는 해당 대상에서의 적용 에피소드가 종료되었다는 의미이다.

Requirement 자체가 폐기되었다는 의미가 아니다.

### 10.4 RequirementFulfillment Status

| 상태 | 설명 |
|---|---|
| NOT_STARTED | 이행 전 |
| IN_PROGRESS | 이행 중 |
| PARTIALLY_FULFILLED | 일부 이행 |
| FULFILLED | 이행 완료 |
| NOT_FULFILLED | 미이행 |
| ACCEPTED_EXCEPTION | 예외 수용 |

### 10.5 Verification Status

| 상태 | 설명 |
|---|---|
| NOT_VERIFIED | 검증 전 |
| PENDING | 검증 대기 또는 진행 중 |
| PASSED | 검증 통과 |
| FAILED | 검증 실패 |
| EXCEPTION_APPROVED | 예외 승인 |

이행 상태와 검증 상태는 서로 독립적으로 관리한다.

단, 상태 조합에 대한 업무 규칙과 전이 조건은 별도 상태 전이 정책에서 확정한다.

### 10.6 OperationalScope Status

| 상태 | 설명 |
|---|---|
| ACTIVE | 운영 중 |
| INACTIVE | 비활성 |
| RETIRED | 운영 종료 |

OperationalScope의 상태는 운영 관리 단위의 생명주기를 의미한다.

해당 Scope에 적용된 RequirementApplication의 상태를 자동으로 변경하지 않는다.

운영 대상 종료에 따른 적용 종료는 별도 업무 절차로 처리한다.

### 10.7 상태 관리 원칙

Project.status, Task.status, Schedule.status, Evidence 상태, Inspection 상태, Requirement의 이행 및 검증 상태를 하나의 진행률 또는 상태로 통합하지 않는다.

각 Domain은 독립적인 상태를 유지한다.

---

## 11. Requirement와 WBS / Task 관계

### 11.1 WBS 관계

WBS는 프로젝트의 업무 분해 구조이다.

Requirement는 충족해야 할 조건이다.

두 Domain은 목적이 다르므로 Requirement가 WBS에 종속되어서는 안 된다.

필요한 경우 RequirementApplication과 WBS 사이에 선택적인 연결 관계를 둘 수 있다.

### 11.2 Task 관계

Task는 실제 수행할 작업을 관리한다.

Requirement는 작업을 통해 이행될 수 있지만 Task 자체가 Requirement의 이행 결과를 의미하지는 않는다.

RequirementApplication과 Task의 추적성이 필요한 경우 RequirementApplicationTask를 도입한다.

Task는 기존 구조상 반드시 Project에 속한다.

따라서 연결 시 다음 정합성을 검증한다.

- RequirementApplication은 프로젝트 적용 건이어야 한다.
- 연결된 Task는 해당 프로젝트에 속해야 한다.

Task의 실제 FK는 기존 `tasks.id`를 사용한다.

운영 적용 건에는 프로젝트 Task를 직접 연결하지 않는다.

운영 작업이 필요한 경우 별도 OperationalWorkItem 또는 WorkOrder Domain과 연계한다.

Task 완료만으로 RequirementFulfillment이 자동 완료되는 것은 아니다.

---

## 12. Requirement와 Evidence / Inspection 관계

### 12.1 Requirement와 EvidenceRequirement 구분

기존 `evidence_requirements`는 프로젝트 수행 과정에서 제출하거나 확보해야 할 증적 항목을 관리한다.

신규 Requirement는 프로젝트 및 운영 환경에서 충족해야 할 조건을 관리한다.

두 개념은 이름에 Requirement가 포함되어 있더라도 업무적 책임이 다르다.

| 구분 | Requirement | EvidenceRequirement |
|---|---|---|
| 목적 | 충족해야 할 조건 정의 | 제출해야 할 증적 항목 관리 |
| 적용 범위 | 프로젝트 및 운영 | 현재 프로젝트 중심 |
| 독립 버전 | RequirementVersion | 기존 구조 유지 |
| 이행 평가 | RequirementFulfillment | 자체 제출 상태 |
| 주요 연결 | RequirementApplication | Evidence |
| 대표 예시 | 운영 서버 계정 권한을 정기 점검해야 한다 | 계정 점검 결과 보고서를 제출해야 한다 |

기존 `evidence_requirements`는 다음 필드를 유지한다.

- project_id
- wbs_id
- requirement_key
- requirement_type
- status
- required
- due_date

기존 `evidence.requirement_id`는 계속 `evidence_requirements.id`를 참조한다.

해당 FK를 신규 Requirement로 변경하지 않는다.

### 12.2 기존 Evidence Domain 유지

기존 Evidence Domain의 책임과 구조는 유지한다.

현재 다음 관계가 존재한다.

- evidence.project_id → projects.id
- evidence.requirement_id → evidence_requirements.id
- evidence.submitted_by → users.id
- evidence.verified_by → users.id
- evidence_version.evidence_id → evidence.id
- inspection_items.requirement_id → evidence_requirements.id
- inspection_items.evidence_id → evidence.id

EvidenceRequirement 및 Evidence는 현재 프로젝트 중심으로 구성되어 있다.

운영 증적 관리 기능을 추가할 때 운영 대상과의 관계를 별도로 검토한다.

운영 증적을 프로젝트 EvidenceRequirement에 억지로 종속시키지 않는다.

### 12.3 RequirementApplication과 EvidenceRequirement 연결

요구사항과 증적 제출 항목 사이의 추적성이 필요한 경우 다음 관계 Entity를 추가한다.

**RequirementApplicationEvidenceRequirement**

| 필드 | 설명 |
|---|---|
| id | PK |
| requirement_application_id | RequirementApplication FK |
| evidence_requirement_id | EvidenceRequirement FK |
| relation_type | 관계 유형 |
| created_at | 생성일시 |

EvidenceRequirement가 프로젝트에 종속되는 경우 연결 대상 Application도 프로젝트 적용 건이어야 한다.

운영 증적 관리 체계가 마련되면 운영 증적과의 추적성 구조를 별도로 추가한다.

증적 제출만으로 RequirementFulfillment이 자동 완료되는 것은 아니다.

---

## 13. Requirement와 Change 관계

Change Domain은 변경 요청 및 승인 절차를 관리한다.

현재 `changes.project_id`는 NOT NULL이며 `projects.id`를 참조한다.

따라서 기존 Change Domain은 프로젝트 변경관리 범위를 중심으로 유지한다.

Requirement Domain은 요구사항 정의와 적용 및 이행을 관리한다.

Change 요청으로 요구사항 변경이 발생할 수 있지만 Change 승인이 곧바로 Requirement 정의를 변경하는 것은 아니다.

기본 흐름은 다음과 같다.

1. Change 요청 등록
2. 변경 영향 분석
3. Change 승인
4. Requirement 변경 내용 확정
5. 신규 RequirementVersion 생성
6. RequirementVersion 승인
7. 적용 대상별 신규 RequirementApplication 생성
8. 이행 및 검증 수행

기존 `changes.change_type`에는 REQUIREMENT 유형이 존재한다.

그러나 이 값은 변경 유형을 구분하는 코드일 뿐, Requirement Entity와의 직접적인 FK 관계를 의미하지 않는다.

따라서 기존 Change의 REQUIREMENT 유형만으로 Requirement 추적 관계가 형성되었다고 판단하지 않는다.

향후 Change와 RequirementApplication 또는 RequirementVersion 간 직접 추적이 필요해지면 별도 관계 Entity를 검토한다.

운영 변경관리까지 지원하려면 Change Domain의 적용 범위와 대상 구조를 별도로 확장해야 한다.

Requirement Domain이 운영 변경을 기존 Project 기반 Change에 억지로 종속시키지 않는다.

---

## 14. Requirement와 CMDB 관계

Requirement는 목표로 하는 구성 조건을 정의한다.

CMDB는 실제 구성 항목과 구성정보를 관리한다.

OperationalScope는 요구사항 적용 및 운영 관리를 위한 경계를 표현한다.

### 14.1 기존 CMDB 구조

현재 확인된 CMDB 주요 테이블은 다음과 같다.

| 테이블 | 역할 |
|---|---|
| configuration_items | CI 기본정보 |
| ci_types | CI 유형 |
| ci_relationship_types | CI 관계 유형 |
| ci_relationships | CI 간 관계 |
| server_details | 서버 상세정보 |
| project_configuration_items | 프로젝트와 CI 연결 |

실제 CI의 기본 PK는 `configuration_items.id`이다.

서버 상세정보는 `server_details.ci_id`를 통해 CI와 연결된다.

### 14.2 운영 요구사항 적용 예시

| 구분 | 관리 내용 |
|---|---|
| Requirement | 서버는 지정된 OS 버전을 사용해야 한다. |
| RequirementVersion | 적용할 OS 버전 및 검증 기준 |
| OperationalScope | 특정 운영 서버 또는 서버 그룹 관리 단위 |
| RequirementApplication | 해당 운영 관리 단위에 요구사항 적용 |
| OperationalScopeCi | OperationalScope와 실제 CI 연결 |
| CMDB | 실제 서버 OS 및 버전 정보 |
| RequirementFulfillment | 목표 조건과 실제 구성정보를 비교한 평가 결과 |

### 14.3 서비스 및 개별 CI 적용

운영 요구사항은 다음과 같이 다양한 범위에 적용할 수 있다.

| 요구사항 | 적용 관리 단위 |
|---|---|
| 서비스 가용성 기준 | SERVICE |
| 시스템 보안 기준 | SYSTEM |
| 운영 환경 설정 기준 | ENVIRONMENT |
| WEB/WAS 플랫폼 기준 | PLATFORM |
| DB 백업 정책 | DATABASE |
| 서버 계정 관리 | CI_SCOPE 또는 SERVER_GROUP |

OperationalScope와 CI 연결을 통해 서비스 단위의 통합 관리와 개별 구성 항목의 세부 관리를 모두 지원한다.

### 14.4 CMDB 연계 원칙

- CMDB의 실제 구성정보는 CMDB가 소유한다.
- OperationalScope는 구성정보를 복제하지 않는다.
- CI와 OperationalScope는 명시적인 FK 관계를 가진 연결 Entity로 관리한다.
- CI 교체 또는 운영 범위 변경 시 연결 이력을 보존한다.
- 실제 구성정보와 요구사항의 목표 조건을 비교하는 평가는 RequirementFulfillment에서 기록한다.
- 기존 `ci_relationships`는 CI 간 관계를 유지한다.
- `operational_scope_ci`는 운영 관리 단위와 CI 간 관계를 별도로 관리한다.

### 14.5 ProjectServerRequirement 구분

ProjectServerRequirement는 프로젝트에서 필요한 서버 역할, 수량, 구성 계획 등을 관리하는 프로젝트별 계획 데이터이다.

이는 일반적인 Requirement와 목적이 다르다.

- Requirement: 충족해야 할 조건
- ProjectServerRequirement: 프로젝트 수행에 필요한 서버 구성 계획
- OperationalScope: 운영 요구사항을 적용하는 관리 단위
- CMDB: 실제 구성된 서버 및 CI 정보

이들을 하나의 Entity로 통합하지 않는다.

---

## 15. 운영 환경 요구사항

Requirement Domain은 프로젝트가 종료된 이후에도 요구사항을 지속적으로 관리할 수 있어야 한다.

운영 조직은 프로젝트와 무관하게 Requirement를 등록하고 관리할 수 있어야 한다.

### 15.1 운영 요구사항 등록

운영 요구사항은 반드시 프로젝트를 통해 생성되어야 하는 것은 아니다.

다음 흐름을 지원한다.

1. 운영 조직에서 Requirement 등록
2. RequirementVersion 작성 및 승인
3. OperationalScope 등록 또는 선택
4. RequirementApplication 생성
5. application_scope_type을 OPERATIONAL로 지정하고 operational_scope_id 연결
6. 이행 및 검증 평가
7. 정기 또는 변경 시점에 재평가

### 15.2 운영 대상의 계층 및 CI 연결

운영 대상은 서비스·시스템·환경·플랫폼·서버 그룹·개별 구성 항목 등 다양한 수준으로 관리한다.

OperationalScope는 상위·하위 관리 단위를 표현할 수 있다.

실제 구성 항목은 OperationalScopeCi를 통해 CMDB CI와 연결한다.

예를 들어 다음과 같다.

```text
운영 서비스
    |
    +---- 운영 시스템
              |
              +---- 운영 WEB 그룹
              |         |
              |         +---- WEB 서버 CI
              |
              +---- 운영 WAS 그룹
              |         |
              |         +---- WAS 서버 CI
              |
              +---- 운영 DB 관리 단위
                        |
                        +---- DB CI
```

위 구조는 운영 관리 단위의 논리적 구성 예시이다.

실제 서비스·시스템·CI 간 관계와 계층은 CMDB 및 운영 모델 설계에 따라 확정한다.

### 15.3 프로젝트에서 운영으로의 전환

프로젝트에서 정의하고 적용한 요구사항이 프로젝트 종료 후 운영 환경에서도 유효한 경우 운영 대상에 별도로 적용한다.

이때 프로젝트 Application을 운영 Application으로 변경하지 않는다.

새로운 RequirementApplication을 생성하고 프로젝트 적용 이력과 운영 적용 이력을 연결한다.

- 프로젝트 Application: 프로젝트 수행 및 검수 이력
- 운영 Application: 운영 환경에서의 지속적인 적용 이력

두 Application은 동일한 Requirement 및 RequirementVersion을 참조할 수 있다.

프로젝트 Application의 CLOSED 상태가 운영 Application의 상태를 자동으로 결정하지 않는다.

프로젝트 종료와 운영 요구사항의 시작은 별도의 업무 절차로 관리한다.

### 15.4 정기 운영 평가

운영 환경의 요구사항은 일회성 검증으로 끝나지 않을 수 있다.

예를 들어 계정 권한 점검이나 백업 결과 확인은 정기적으로 반복해야 한다.

RequirementFulfillment은 동일한 RequirementApplication에 대해 여러 평가 기록을 누적할 수 있어야 한다.

평가 주기, 평가 일정 생성, 미수행 알림, 자동 검증 등은 후속 운영관리 기능으로 설계한다.

---

## 16. V1 구현 범위

V1에서는 프로젝트와 운영 환경을 모두 지원할 수 있는 공통 데이터 구조를 설계 기준으로 삼는다.

운영 서비스·시스템 단위와 개별 CI 단위를 모두 수용할 수 있도록 OperationalScope와 CMDB CI의 연결 구조를 정의한다.

### 16.1 핵심 포함 범위

| 기능 | Entity | 설명 |
|---|---|---|
| 요구사항 등록 | Requirement | 독립적인 요구사항 식별 및 관리 |
| 요구사항 정의 | RequirementVersion | 버전별 요구사항 내용 관리 |
| 적용 가능 범위 | RequirementVersion | PROJECT / OPERATIONAL / BOTH |
| 공통 적용 관리 | RequirementApplication | 프로젝트·운영 공통 적용 이력 |
| 프로젝트 적용 | RequirementApplication | projects.id FK 연결 |
| 운영 관리 단위 | OperationalScope | 운영 서비스·시스템·환경 등 관리 |
| CI 연결 | OperationalScopeCi | configuration_items.id FK 연결 |
| 운영 적용 | RequirementApplication | OperationalScope FK 연결 |
| 이행 평가 | RequirementFulfillment | 이행 상태 기록 |
| 검증 평가 | RequirementFulfillment | 검증 상태 기록 |
| 평가 이력 | RequirementFulfillment | 재평가 결과 보존 |
| 사용자 참조 | 각 신규 Entity | users.id FK 활용 |

### 16.2 단계적 구현 범위

| 기능 | 구현 방향 |
|---|---|
| Requirement Baseline | 프로젝트 및 운영 기준선 |
| RequirementApplicationTask | 요구사항과 Task 추적성 |
| RequirementApplicationEvidenceRequirement | 요구사항과 증적 항목 추적성 |
| RequirementFulfillmentEvidence | 평가 기록과 증적 직접 연결 |
| RequirementExceptionApproval | 예외 승인 및 재검토 |
| 자동 검증 | CMDB 등 외부 데이터와 연계 |
| 정기 운영 평가 | 반복 평가 및 평가 주기 관리 |
| 운영 전환 관리 | 프로젝트 적용에서 운영 적용으로의 전환 절차 |
| 운영 변경관리 | Change Domain의 운영 적용 범위 확장 |

운영 적용의 핵심 FK 구조와 OperationalScope는 운영 기능을 후속 구현하더라도 V1 설계에 반영한다.

### 16.3 기존 DB 변경 원칙

신규 Requirement Domain을 도입하기 위해 기존 프로젝트 및 Evidence 테이블의 FK 구조를 변경하지 않는다.

특히 다음 구조는 유지한다.

- evidence.requirement_id → evidence_requirements.id
- evidence.project_id → projects.id
- inspection_items.requirement_id → evidence_requirements.id
- changes.project_id → projects.id
- tasks.project_id → projects.id
- project_configuration_items.ci_id → configuration_items.id

신규 Requirement Domain은 별도 테이블로 추가하고 필요한 경우 기존 Domain과 관계 Entity를 통해 연결한다.

---

## 17. 미확정 설계 사항

| 항목 | 검토 내용 |
|---|---|
| RequirementCode 정책 | 코드 생성 규칙 및 변경 불가 정책 |
| RequirementVersion 승인 | 승인 권한과 승인 워크플로우 |
| 적용 가능 범위 | PROJECT / OPERATIONAL / BOTH 정책 |
| 버전 유효기간 | effective_from, effective_to 관리 기준 |
| Application 대상 정합성 | application_scope_type과 실제 FK의 CHECK 제약 |
| Application 중복 정책 | 동일 대상 내 활성 Application 제한 |
| Application 재적용 | 이전 Application 연결 및 종료 정책 |
| Application 관계 | 여러 원천 적용 건을 연결할 필요성 |
| OperationalScope 유형 | 서비스·시스템·환경·CI 관리 단위 분류 |
| OperationalScope 계층 | 상위·하위 관계와 순환 참조 방지 |
| OperationalScopeCi | 관계 유형 및 유효기간 정책 |
| CI 연계 | configuration_items.id FK 적용 확정 |
| 운영 서비스 모델 | 서비스 및 시스템을 CMDB CI로 관리할지 여부 |
| Fulfillment 수정 정책 | 평가 기록 수정 허용 범위 |
| 평가 상태 조합 | fulfillment_status와 verification_status 전이 규칙 |
| 평가 주기 | 정기 평가 계획과 일정 생성 정책 |
| 예외 승인 | 예외 승인 Entity 및 승인 절차 |
| Baseline | 기준선 생성 및 변경 승인 절차 |
| Task 추적성 | RequirementApplicationTask 도입 시점 |
| 증적 추적성 | RequirementApplicationEvidenceRequirement 도입 시점 |
| Change 연계 | Change와 RequirementApplication 연결 방식 |
| 자동 검증 | CMDB 및 외부 시스템 연계 방식 |
| 프로젝트 운영 전환 | 프로젝트 적용과 운영 적용의 전환 절차 |
| 삭제 정책 | 신규 Domain별 논리 삭제 및 참조 데이터 보존 정책 |

---

## 18. 최종 설계 원칙

Requirement Domain은 다음 원칙을 유지한다.

1. Requirement는 Project 및 운영 대상과 독립적인 요구사항 정의이다.
2. RequirementVersion은 요구사항 정의의 변경 이력을 관리한다.
3. RequirementVersion은 적용 가능 범위를 정의한다.
4. RequirementApplication은 특정 요구사항 버전을 특정 대상에 적용한 공통 이력을 관리한다.
5. RequirementApplication은 PROJECT 또는 OPERATIONAL 중 하나의 적용 대상만 가진다.
6. RequirementApplication의 실제 적용 대상은 Project 또는 OperationalScope의 명시적 FK로 관리한다.
7. 적용 대상 유형과 실제 FK의 정합성은 서비스 계층 및 DB CHECK 제약조건으로 보장한다.
8. OperationalScope는 서비스·시스템·환경·플랫폼·서버 그룹·개별 CI 수준의 운영 관리 단위를 표현한다.
9. OperationalScope와 CMDB CI는 별도 Entity로 유지하고 명시적인 연결 Entity로 관리한다.
10. OperationalScopeCi의 CI FK는 기존 configuration_items.id를 사용한다.
11. RequirementFulfillment은 특정 적용 건에 대한 이행 및 검증 평가 이력을 관리한다.
12. 요구사항 정의, 버전, 적용, 이행 및 검증은 서로 다른 책임이다.
13. 요구사항 버전이 변경되어도 기존 프로젝트 및 운영 적용 버전은 자동으로 변경하지 않는다.
14. 새로운 버전을 적용할 때는 새로운 적용 이력을 생성한다.
15. 이행 상태와 검증 상태는 별도로 관리한다.
16. 평가 이력은 덮어쓰기보다 추가 기록을 우선한다.
17. 증적 제출, 작업 완료, 프로젝트 진행률을 요구사항 이행 완료와 동일하게 취급하지 않는다.
18. Requirement와 EvidenceRequirement는 서로 다른 Domain 책임을 가진다.
19. 기존 evidence_requirements 및 evidence의 FK 구조를 변경하지 않는다.
20. Requirement와 CMDB는 목표 조건과 실제 구성정보라는 서로 다른 역할을 가진다.
21. 이력 데이터는 물리 삭제보다 보존을 우선한다.
22. JPA 연관관계와 Cascade는 Aggregate 경계를 기준으로 설계한다.
23. N:M 관계에 업무적 의미가 있으면 별도 관계 Entity를 사용한다.
24. 프로젝트 종료 이후에도 운영 요구사항을 지속적으로 관리할 수 있어야 한다.
25. 운영 조직은 프로젝트와 무관하게 Requirement를 등록하고 관리할 수 있어야 한다.
26. 프로젝트 적용 이력과 운영 적용 이력은 독립적으로 관리하되 동일한 Requirement 정의 및 버전을 공유할 수 있어야 한다.
27. 적용 대상은 실제 Entity FK로 연결하며 다형성 target_type, target_id 구조를 사용하지 않는다.
28. 운영 서비스·시스템 단위와 개별 CI 단위의 요구사항 적용을 모두 지원한다.
29. OperationalScope의 생명주기와 RequirementApplication의 생명주기는 서로 독립적으로 관리한다.
30. 프로젝트에서 운영으로 전환할 때 기존 Application을 변경하지 않고 신규 Application을 생성하여 이력을 연결한다.
31. 기존 Project, User, CMDB 및 Evidence Domain의 실제 PK와 FK 구조를 존중한다.
32. 신규 Requirement Domain은 기존 프로젝트 중심 데이터 구조를 무리하게 변경하지 않고 독립적으로 확장한다.

---

## 19. 문서 변경 이력

| Version | Date | Description |
|---|---|---|
| 0.1 | 2026-10-02 | Requirement Domain 초안 작성 |
| 0.2 | 2026-11-15 | Requirement, RequirementVersion, RequirementApplication, RequirementFulfillment 기본 구조 정리 |
| 0.3 | 2026-11-15 | 핵심 Entity 관계, 적용 이력, 평가 이력, JPA Aggregate 경계 및 DB 정합성 원칙 구체화 |
| 0.4 | 2026-11-15 | 프로젝트·운영 공통 적용 모델 도입, 유형별 적용 대상 Entity 분리, 운영 환경 영속성 및 프로젝트-운영 전환 구조 반영 |
| 0.5 | 2026-11-15 | OperationalScope 도입, CMDB CI 연결 모델 정의, 서비스·시스템·개별 CI 수준의 운영 요구사항 적용 지원, 적용 가능 범위 및 평가 유형 구체화 |
| 0.6 | 2026-11-15 | RequirementApplication의 적용 대상 FK 통합, application_scope_type과 CHECK 제약조건을 통한 대상 정합성 강화, OperationalScope 및 평가 이력 구조 정교화 |
| 0.7 | 2026-11-15 | 기존 PostgreSQL DDL 검토 결과 반영, configuration_items.id 및 users.id 등 실제 FK 대상 확정, 기존 EvidenceRequirement와 신규 Requirement 분리 명시, 기존 CMDB CI 관계와 OperationalScopeCi의 책임 경계 구체화 |