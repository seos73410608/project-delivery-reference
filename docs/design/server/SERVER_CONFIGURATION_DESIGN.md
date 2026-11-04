# 서버 구성 및 인프라 CI 관리 기능 설계

> 문서 버전: v2.0-draft  
> 대상 시스템: PMIS (Project Management Information System)  
> 대상 도메인: Infrastructure / CMDB  
> 1차 개발 범위: Server Configuration Management  
> 설계 방향: Project 독립형 Infrastructure 관리

---

# 1. 문서 개요

## 1.1 목적

PMIS에서 프로젝트 및 조직과 독립적으로 인프라 구성요소를 관리하고, 프로젝트와 인프라 간의 관계를 체계적으로 관리하기 위한 기능을 정의한다.

기존 프로젝트 중심의 서버 구성 관리 방식에서 확장하여, 서버를 독립적인 인프라 구성항목(Configuration Item, CI)으로 관리한다.

프로젝트는 서버를 직접 소유하거나 종속시키는 것이 아니라, 인프라 자산을 구축·사용·변경·이관·참조하는 관계를 갖는다.

이를 통해 특정 수행사 또는 고객사에 종속되지 않는 범용적인 Infrastructure Configuration Management 구조를 마련한다.

## 1.2 핵심 설계 방향

기존 구조:

```text
Project
 └── ServerConfiguration
       └── Server
```

변경 구조:

```text
Organization
     │
     ├── Project
     │     │
     │     └── ProjectInfrastructure
     │                  │
     │                  └── Infrastructure CI
     │
     └── Infrastructure
                │
                └── CI
```

핵심 원칙은 다음과 같다.

1. Project와 Infrastructure는 독립적인 도메인으로 관리한다.
2. Server는 독립적인 CI로 관리한다.
3. Project와 Server는 매핑 관계로 연결한다.
4. 하나의 Server는 정책에 따라 여러 Project에서 참조될 수 있다.
5. 프로젝트 종료 후에도 인프라 CI의 기준정보와 이력을 유지한다.
6. 향후 OS, DBMS, WAS, Network, Storage, Application 등의 CI 유형을 확장할 수 있도록 설계한다.

## 1.3 용어 정의

| 용어 | 설명 |
|---|---|
| CI | Configuration Item. 관리 대상 구성항목 |
| Infrastructure | HW 및 Software를 포함하는 인프라 영역 |
| Server | 독립적으로 식별하고 관리하는 서버 CI |
| Project Server Requirement | 프로젝트에서 필요로 하는 서버 구성 계획 |
| ProjectInfrastructure | 프로젝트와 인프라 CI 간의 매핑 |
| CI Relationship | CI와 다른 CI 간의 구성 및 의존 관계 |

---

# 2. 기능 범위

서버 구성 및 인프라 CI 관리 기능은 다음 범위를 포함한다.

1. CI 공통정보 관리
2. 서버 CI 등록 및 조회
3. 서버 상세정보 관리
4. 프로젝트별 서버 구성 계획 관리
5. 프로젝트와 서버 CI 매핑
6. 서버 유형별 구성 현황 조회
7. 서버 환경 및 설치 상태 관리
8. 서버 생명주기 관리
9. 프로젝트별 인프라 현황 조회
10. 인프라 Dashboard 연계
11. CI 변경 이력 관리
12. 향후 Software 및 기타 Infrastructure CI 확장

---

# 3. 전체 도메인 구조

## 3.1 도메인 구성

```text
Organization
 │
 ├── Project
 │     │
 │     ├── WBS
 │     ├── Schedule
 │     ├── Issue
 │     ├── Risk
 │     ├── Change
 │     ├── Evidence
 │     │
 │     └── ProjectInfrastructure
 │                │
 │                └── Infrastructure CI
 │
 └── Infrastructure
       │
       ├── CI
       │    │
       │    ├── Hardware CI
       │    ├── Software CI
       │    ├── Database CI
       │    ├── Network CI
       │    └── Application CI
       │
       ├── CI Relationship
       └── CI History
```

## 3.2 Project와 Infrastructure의 관계

Project는 Infrastructure를 직접 소유하지 않는다.

ProjectInfrastructure 매핑을 통해 특정 프로젝트가 어떤 CI를 어떤 목적으로 사용하는지 관리한다.

예:

```text
Infrastructure CI
 └── SRV-001 (WEB Server)
       │
       ├── Project A
       │     └── BUILD
       │
       └── Project B
             └── USE
```

동일한 CI를 여러 프로젝트에서 참조할 수 있도록 설계한다.

다만 실제 자산의 동시 사용 가능 여부와 변경 권한은 자산 및 운영 정책에 따라 통제한다.

---

# 4. CI 공통 모델

## 4.1 CI (Configuration Item)

모든 인프라 구성요소를 식별하고 관리하기 위한 공통 엔티티이다.

### 주요 필드

```text
id
ciCode
ciName
ciType
status
environment
ownerOrganizationId
operatingOrganizationId
managerId
location
description
createdAt
updatedAt
```

### CI 유형

초기에는 다음 유형을 고려한다.

| CI 유형 | 설명 | 구현 단계 |
|---|---|---|
| SERVER | 서버 | 1차 |
| NETWORK | 네트워크 장비 | 후속 |
| STORAGE | 스토리지 | 후속 |
| BACKUP | 백업 장비 | 후속 |
| OS | 운영체제 | 후속 |
| DBMS | 데이터베이스 제품 및 인스턴스 | 후속 |
| MIDDLEWARE | Web/WAS/MQ 등 | 후속 |
| DATABASE | 데이터베이스 | 후속 |
| APPLICATION | 업무 애플리케이션 | 후속 |
| SERVICE | IT 서비스 | 후속 |
| CLOUD | 클라우드 자원 | 후속 |

CI 유형은 확장 가능하도록 설계한다.

초기 구현에서는 SERVER만 실제 등록 및 관리 기능을 제공한다.

## 4.2 CI 상태

CI의 현재 관리 상태와 생명주기를 구분한다.

예:

```text
PLANNED
ACTIVE
INACTIVE
MAINTENANCE
RETIRED
DISPOSED
```

상태의 의미:

| 상태 | 의미 |
|---|---|
| PLANNED | 도입 또는 구축 예정 |
| ACTIVE | 사용 중 |
| INACTIVE | 비활성 |
| MAINTENANCE | 유지보수 중 |
| RETIRED | 운영 종료 |
| DISPOSED | 폐기 |

CI 상태와 설치 진행 상태는 서로 다른 개념으로 관리한다.

---

# 5. Server CI

## 5.1 개념

Server는 Project에 종속되지 않는 독립적인 Infrastructure CI이다.

실제 서버의 식별정보와 하드웨어 및 운영체제 관련 정보를 관리한다.

프로젝트에서 신규 구축하거나 기존 서버를 활용하는 경우 ProjectInfrastructure 매핑을 통해 연결한다.

## 5.2 ServerType

초기 서버 역할 유형은 다음과 같다.

```java
public enum ServerType {
    WEB,
    WAS,
    AP,
    GW,
    DB
}
```

향후 다음 유형을 추가할 수 있다.

```text
CACHE
MQ
BATCH
FILE
ETC
```

ServerType은 서버의 물리적 종류가 아니라 업무 및 시스템 구성상 역할을 나타낸다.

## 5.3 Server 주요 필드

```text
id
ciId
serverType
hostname
ipAddress
os
cpu
memory
disk
environment
installationStatus
description
createdAt
updatedAt
```

`ciId`는 공통 CI와 Server 상세정보를 연결한다.

`projectId`는 Server의 필수 소유 필드로 사용하지 않는다.

서버가 어느 프로젝트와 관련되어 있는지는 ProjectInfrastructure 매핑을 통해 조회한다.

## 5.4 서버 환경

```java
public enum ServerEnvironment {
    DEV,
    TEST,
    STAGING,
    PROD
}
```

환경은 프로젝트 정책에 따라 확장 가능하도록 한다.

## 5.5 설치 상태

```java
public enum InstallationStatus {
    PLANNED,
    REQUESTED,
    INSTALLING,
    INSTALLED,
    COMPLETED,
    FAILED
}
```

| 상태 | 의미 |
|---|---|
| PLANNED | 설치 예정 |
| REQUESTED | 설치 요청 |
| INSTALLING | 설치 진행 중 |
| INSTALLED | 설치 완료 |
| COMPLETED | 설치 후 검증까지 완료 |
| FAILED | 설치 실패 |

설치 상태는 CI의 운영 상태와 구분하여 관리한다.

---

# 6. Project Server Requirement

## 6.1 개념

ProjectServerRequirement는 특정 프로젝트에서 필요로 하는 서버 역할 및 구성 계획을 관리한다.

이는 실제 서버 자산을 의미하지 않는다.

예를 들어 프로젝트에서 WEB 2대, WAS 2대, DB 1대가 필요하다는 계획을 등록할 수 있다.

## 6.2 주요 필드

```text
id
projectId
serverType
enabled
plannedCount
description
createdAt
updatedAt
```

## 6.3 서버 유형별 계획

예:

```text
Project A

WEB : 사용 / 2대
WAS : 사용 / 2대
AP  : 미사용
GW  : 미사용
DB  : 사용 / 1대
```

프로젝트마다 필요한 서버 유형과 수량은 다를 수 있다.

동일 프로젝트에서 동일 ServerType의 계획은 중복 생성하지 않는다.

DB 제약조건:

```text
UNIQUE(project_id, server_type)
```

## 6.4 계획과 실제 서버의 구분

다음 두 정보는 별도로 관리한다.

```text
ProjectServerRequirement
    = 프로젝트가 필요로 하는 서버 구성 계획

Server CI
    = 실제 등록된 서버 구성항목
```

계획 수량과 실제 등록된 서버 수량은 다를 수 있다.

예:

```text
WAS

계획 수량 : 3
실제 서버 : 2
차이       : 1
```

Dashboard에서는 계획 수량과 실제 서버 수량을 비교할 수 있도록 한다.

---

# 7. ProjectInfrastructure Mapping

## 7.1 개념

Project와 Infrastructure CI를 연결하는 매핑 엔티티이다.

프로젝트가 특정 서버를 어떤 목적으로 사용하는지 관리한다.

## 7.2 주요 필드

```text
id
projectId
ciId
relationshipType
validFrom
validTo
description
createdAt
updatedAt
```

## 7.3 관계 유형

```java
public enum ProjectInfrastructureType {
    BUILD,
    USE,
    CHANGE,
    MIGRATE,
    REFERENCE
}
```

| 관계 | 의미 |
|---|---|
| BUILD | 프로젝트에서 신규 구축 |
| USE | 기존 인프라 사용 |
| CHANGE | 기존 인프라 변경 |
| MIGRATE | 인프라 이관 |
| REFERENCE | 참조 목적으로 연결 |

동일 프로젝트와 동일 CI의 중복 활성 매핑을 방지한다.

프로젝트 종료 또는 이관 시 매핑 종료일을 관리하여 과거 관계를 유지한다.

CI 자체를 삭제하지 않고 관계의 종료로 관리하는 것을 기본 정책으로 한다.

---

# 8. CI Relationship

## 8.1 개념

CI와 다른 CI 사이의 구성 및 의존 관계를 관리한다.

향후 HW, OS, DBMS, WAS, Application 등을 연결하기 위한 공통 구조이다.

## 8.2 주요 필드

```text
id
sourceCiId
targetCiId
relationshipType
validFrom
validTo
description
createdAt
updatedAt
```

## 8.3 관계 유형 예시

```text
CONTAINS
RUNS_ON
HOSTED_ON
DEPENDS_ON
CONNECTS_TO
USES
BACKED_UP_BY
REPLICATES_TO
```

예:

```text
Application
   │
   ├── RUNS_ON
   │      └── WAS
   │
   └── DEPENDS_ON
          └── DBMS
```

초기 개발에서는 CI Relationship의 기본 구조만 설계하고, 실제 관계 편집 기능은 후속 개발 범위로 둘 수 있다.

---

# 9. API 설계

Base URL은 기존 PMIS API 규칙에 맞춰 적용한다.

## 9.1 프로젝트별 서버 구성 계획 조회

```http
GET /api/projects/{projectId}/server-requirements
```

응답 개념:

```json
{
  "projectId": 1,
  "serverRequirements": [
    {
      "serverType": "WEB",
      "enabled": true,
      "plannedCount": 2,
      "actualCount": 2
    },
    {
      "serverType": "WAS",
      "enabled": true,
      "plannedCount": 3,
      "actualCount": 2
    },
    {
      "serverType": "DB",
      "enabled": true,
      "plannedCount": 1,
      "actualCount": 1
    }
  ]
}
```

`actualCount`는 해당 프로젝트와 활성 매핑된 Server CI를 기준으로 집계한다.

## 9.2 서버 구성 계획 등록

```http
POST /api/projects/{projectId}/server-requirements
```

Request:

```json
{
  "serverType": "WAS",
  "enabled": true,
  "plannedCount": 3,
  "description": "운영 WAS 서버 구성 계획"
}
```

## 9.3 서버 구성 계획 수정

```http
PUT /api/projects/{projectId}/server-requirements/{requirementId}
```

## 9.4 서버 구성 계획 삭제

```http
DELETE /api/projects/{projectId}/server-requirements/{requirementId}
```

실제 서버 CI가 존재하더라도 계획 삭제가 서버 자산 삭제로 이어져서는 안 된다.

관련 매핑이 존재하는 경우 계획 삭제 정책을 별도로 적용한다.

## 9.5 서버 CI 등록

```http
POST /api/infrastructure/servers
```

Request:

```json
{
  "ciCode": "SRV-001",
  "ciName": "WAS-01",
  "serverType": "WAS",
  "hostname": "was01",
  "ipAddress": "10.10.10.101",
  "os": "Linux",
  "cpu": 8,
  "memory": 32,
  "disk": 500,
  "environment": "PROD",
  "installationStatus": "PLANNED",
  "description": "운영 WAS 서버"
}
```

실제 API Request는 기존 CI 공통정보 DTO와 Server 상세 DTO의 구성 방식에 맞춰 확정한다.

## 9.6 서버 CI 조회

```http
GET /api/infrastructure/servers/{serverId}
```

## 9.7 서버 CI 검색

```http
GET /api/infrastructure/servers
```

검색 조건:

```text
serverType
serverName
hostname
ipAddress
environment
installationStatus
status
```

## 9.8 서버 CI 수정

```http
PUT /api/infrastructure/servers/{serverId}
```

## 9.9 서버 CI 삭제

```http
DELETE /api/infrastructure/servers/{serverId}
```

운영 중이거나 프로젝트 매핑 및 CI 관계가 존재하는 경우 물리 삭제를 제한한다.

기본적으로 폐기 또는 비활성 상태로 변경하는 정책을 우선 고려한다.

## 9.10 프로젝트와 서버 CI 매핑

```http
POST /api/projects/{projectId}/infrastructure
```

Request:

```json
{
  "ciId": 101,
  "relationshipType": "BUILD",
  "description": "프로젝트 신규 구축 대상"
}
```

## 9.11 프로젝트별 서버 조회

```http
GET /api/projects/{projectId}/servers
```

프로젝트와 매핑된 서버 CI를 조회한다.

검색 조건:

```text
serverType
serverName
environment
installationStatus
relationshipType
```

## 9.12 프로젝트 인프라 Dashboard

```http
GET /api/projects/{projectId}/infrastructure/dashboard
```

계획 수량, 실제 매핑 서버 수, 설치 상태를 집계한다.

---

# 10. Dashboard

## 10.1 프로젝트별 서버 현황

```json
{
  "projectId": 1,
  "totalServers": 8,
  "serverTypeCount": {
    "WEB": 2,
    "WAS": 2,
    "AP": 1,
    "GW": 1,
    "DB": 2
  },
  "installationStatusCount": {
    "PLANNED": 2,
    "REQUESTED": 1,
    "INSTALLING": 2,
    "INSTALLED": 2,
    "COMPLETED": 1
  }
}
```

## 10.2 프로젝트별 계획 대비 실제 현황

```text
Server Type | Planned | Actual | Difference
--------------------------------------------
WEB         | 2       | 2      | 0
WAS         | 3       | 2      | 1
AP          | 1       | 1      | 0
GW          | 0       | 0      | 0
DB          | 1       | 1      | 0
```

Dashboard 집계는 ProjectInfrastructure 매핑을 기준으로 한다.

전사 인프라 Dashboard와 프로젝트별 Dashboard는 서로 다른 조회 범위로 제공할 수 있도록 설계한다.

---

# 11. 데이터 무결성 및 검증 정책

## 11.1 CI 식별

- CI Code는 시스템 내에서 중복되지 않도록 관리한다.
- CI 유형과 상세정보의 유형은 일치해야 한다.
- Server 상세정보는 SERVER 유형 CI에만 연결한다.

## 11.2 Project 검증

- Project가 존재하는지 확인한다.
- Project가 존재하지 않으면 RESOURCE_NOT_FOUND를 반환한다.

## 11.3 Server Requirement 검증

- 동일 프로젝트에 동일 ServerType의 중복 계획을 허용하지 않는다.
- plannedCount는 0 이상의 정수로 관리한다.
- enabled=false인 경우 계획 수량과 실제 매핑 관계의 처리 정책을 적용한다.

## 11.4 ProjectInfrastructure 검증

- Project와 CI가 모두 존재해야 한다.
- 동일한 활성 매핑의 중복 등록을 방지한다.
- 관계 종료 후에도 과거 이력을 유지한다.

## 11.5 CI 삭제 정책

- 프로젝트 매핑이 존재하는 CI는 물리 삭제하지 않는다.
- 다른 CI와 관계가 존재하는 경우 삭제를 제한한다.
- 폐기 또는 비활성 상태 변경을 우선 적용한다.

---

# 12. 권한

기존 PMIS의 Security 및 Role 정책을 따른다.

| 기능 | USER | PM | ADMIN |
|---|---|---|---|
| CI 조회 | 허용 | 허용 | 허용 |
| CI 등록 | 제한 | 허용 | 허용 |
| CI 수정 | 제한 | 허용 | 허용 |
| CI 삭제/폐기 | 제한 | 제한 | 허용 |
| Project 매핑 조회 | 허용 | 허용 | 허용 |
| Project 매핑 등록/수정 | 제한 | 허용 | 허용 |
| Project 매핑 삭제 | 제한 | 제한 | 허용 |

실제 권한 표현은 기존 PMIS의 Role Hierarchy와 Method Security 구현에 맞춘다.

프로젝트별 접근 권한 및 조직별 자산 관리 권한은 후속 확장 범위로 고려한다.

---

# 13. DTO 및 패키지 구조

권장 도메인 구조:

```text
com.seos.pmis
 ├── project
 │    ├── ...
 │    └── infrastructure
 │         ├── requirement
 │         └── mapping
 │
 └── infrastructure
      ├── ci
      │    ├── controller
      │    ├── dto
      │    ├── entity
      │    ├── repository
      │    ├── service
      │    └── mapper
      │
      ├── server
      │    ├── controller
      │    ├── dto
      │    ├── entity
      │    ├── repository
      │    ├── service
      │    └── mapper
      │
      ├── relationship
      ├── history
      └── dashboard
```

위 구조는 논리적인 패키지 제안이며, 실제 적용 전 기존 Backend의 패키지 규칙과 공통 모듈 구조를 확인한다.

---

# 14. 데이터 모델 확장 방향

향후 다음 구조로 확장할 수 있도록 한다.

```text
Infrastructure CI
 │
 ├── Hardware
 │    ├── Server
 │    ├── Network
 │    ├── Storage
 │    └── Backup
 │
 ├── Software
 │    ├── OS
 │    ├── DBMS
 │    ├── Middleware
 │    └── Application
 │
 ├── Database
 │    ├── DB Instance
 │    ├── SID
 │    ├── Database
 │    ├── Schema
 │    └── Account
 │
 ├── Cloud
 │    ├── Virtual Machine
 │    ├── Network
 │    └── Storage
 │
 └── Service
      ├── Business Service
      └── Technical Service
```

DBMS 상세 모델에서는 제품별 개념 차이를 반영한다.

예를 들어 Oracle의 SID, PostgreSQL의 Cluster, MariaDB의 Database 및 User를 동일한 개념으로 취급하지 않는다.

계정도 OS Account, DBMS Account, Application Account를 구분한다.

계정의 인증정보 및 비밀번호는 일반 CI 데이터에 저장하지 않고 별도의 Secret 관리 체계와 연계하는 것을 원칙으로 한다.

---

# 15. CI 변경 이력

CI의 주요 변경 사항을 추적할 수 있도록 설계한다.

예:

```text
CI History

SRV-001
 ├── 2026-11-01 : 서버 등록
 ├── 2026-11-02 : IP 변경
 ├── 2026-11-03 : Memory 변경
 └── 2026-11-04 : 운영 상태 변경
```

주요 관리 항목:

```text
ciId
changeType
changedField
beforeValue
afterValue
changedBy
changedAt
reason
```

초기 구현에서는 기존 PMIS의 공통 감사 및 이력 관리 정책을 우선 활용한다.

별도의 CI History 기능은 이력 요구사항을 확인한 후 구현한다.

---

# 16. 향후 설치 및 검증 업무 연계

서버 CI는 향후 PMO 업무 흐름과 연결한다.

```text
Project
   │
   ├── WBS
   ├── Schedule
   │
   └── ProjectInfrastructure
            │
            └── Server CI
                  │
                  ├── Installation
                  │      ├── 설치 일정
                  │      ├── 설치 요청
                  │      └── 설치 결과
                  │
                  ├── Verification
                  │      ├── 검증 항목
                  │      ├── 검증 결과
                  │      └── Evidence
                  │
                  └── Issue
                         └── 조치 결과
```

설치 및 검증은 Project의 업무 활동으로 관리하면서, 그 결과를 실제 CI에 연결한다.

이를 통해 프로젝트 종료 후에도 해당 서버의 구축 및 변경 이력을 추적할 수 있다.

---

# 17. 1차 개발 범위

이번 개발에서는 범용 CMDB의 모든 기능을 구현하지 않는다.

다만 향후 확장에 필요한 공통 구조를 우선 확보한다.

## 17.1 필수 구현

```text
[1] CI 공통 식별체계
[2] CI Type
[3] Server CI
[4] Server 상세정보
[5] Project Server Requirement
[6] ProjectInfrastructure Mapping
[7] 프로젝트별 서버 구성 조회
[8] 서버 CI 등록
[9] 서버 CI 조회
[10] 서버 CI 수정
[11] 서버 CI 삭제 또는 폐기 정책
[12] 설치 상태 관리
[13] 프로젝트별 서버 Dashboard
```

## 17.2 기본 설계 반영, 후속 구현

```text
[1] CI Relationship
[2] CI History
[3] Network CI
[4] Storage CI
[5] Backup CI
[6] OS CI
[7] DBMS CI
[8] Middleware CI
[9] Application CI
[10] Service CI
[11] Account Management
[12] Discovery / Import
```

---

# 18. 개발 순서

```text
1. 기존 Project Entity 및 관계 확인
       ↓
2. CI 공통 모델 설계
       ↓
3. CI Type 및 상태 정의
       ↓
4. Server CI Entity
       ↓
5. Server 상세 Entity
       ↓
6. Project Server Requirement
       ↓
7. ProjectInfrastructure Mapping
       ↓
8. Repository
       ↓
9. Request / Response DTO
       ↓
10. Mapper
       ↓
11. Service
       ↓
12. Controller
       ↓
13. Security 및 Validation
       ↓
14. Swagger API 검증
       ↓
15. Dashboard 연계
```

실제 구현 시 기존 PMIS의 공통 응답, 예외 처리, Security, Entity Base 클래스 및 ID 정책을 그대로 따른다.

---

# 19. 최종 목표

최종적으로 PMIS는 다음과 같은 구조를 갖는다.

```text
PMIS
 │
 ├── Project Management
 │     ├── Project
 │     ├── WBS
 │     ├── Schedule
 │     ├── Issue
 │     ├── Risk
 │     ├── Change
 │     └── Evidence
 │
 ├── Organization Management
 │     ├── Organization
 │     ├── Customer
 │     ├── Vendor
 │     └── Operating Organization
 │
 ├── Configuration Management (CMDB)
 │     ├── CI Core
 │     ├── Hardware
 │     ├── Software
 │     ├── Database
 │     ├── Network
 │     ├── Application
 │     ├── CI Relationship
 │     └── CI History
 │
 └── Infrastructure Lifecycle
       ├── Installation
       ├── Deployment
       ├── Verification
       ├── Evidence
       └── Migration
```

**최종 설계 원칙**

1. Project와 Infrastructure는 독립적으로 관리한다.
2. CI는 프로젝트와 관계없이 고유하게 식별한다.
3. Project와 CI는 매핑 관계로 연결한다.
4. 서버 유형별 계획과 실제 서버 자산을 구분한다.
5. HW와 Software를 동일한 CI 관리 체계에서 확장할 수 있도록 한다.
6. CI 간 관계와 변경 이력을 관리할 수 있도록 한다.
7. 프로젝트 종료 후에도 인프라 자산과 이력을 유지한다.
8. 초기 개발은 Server CI부터 시작하고 단계적으로 CMDB 범위를 확장한다.

**개발 방향: 기존 PMIS 기능 유지 + Infrastructure CI 기반 CMDB 확장**