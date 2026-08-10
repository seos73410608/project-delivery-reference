# 서버 구성 관리 기능 설계

## 1. 문서 개요

### 1.1 목적

PMIS(Project Management Information System)에서 프로젝트별 서버 구성 정보를 체계적으로 관리하기 위한 기능을 정의한다.

프로젝트 수행 과정에서 WEB, WAS, AP, GW, DB 등의 서버가 프로젝트별 요구사항에 따라 선택적으로 구성될 수 있으며, 단일 서버 유형 또는 복합 서버 구성으로 운영될 수 있다.

본 기능은 프로젝트에 필요한 서버 구성 계획을 등록하고, 서버별 설치 대상 및 구성 정보를 관리할 수 있도록 하는 것을 목적으로 한다.

### 1.2 대상 서버 유형

기본 서버 유형은 다음과 같다.

| 서버 유형 | 설명 |
|---|---|
| WEB | Web Server |
| WAS | Web Application Server |
| AP | Application Server |
| GW | Gateway Server |
| DB | Database Server |

서버 유형은 고정된 하나의 구성으로 제한하지 않는다.

예:

- WEB + WAS + AP + GW + DB
- WEB + WAS + DB
- WEB + AP + DB
- WAS + DB
- AP + GW + DB
- DB 단독
- WEB + WAS + AP + GW + DB 복합 구성

즉, **프로젝트별 서버 구성은 선택 가능한 구조**로 설계한다.

---

# 2. 기능 범위

서버 구성 관리 기능은 다음 범위를 포함한다.

1. 프로젝트 서버 구성 조회
2. 서버 유형 추가
3. 서버 유형 삭제
4. 서버 구성 수정
5. 서버 유형별 서버 수량 관리
6. 서버별 상세 정보 관리
7. 서버 구성 상태 관리
8. 서버 설치 대상 관리
9. 서버 구성 Dashboard 연계
10. 프로젝트와 서버 구성 간 연계

---

# 3. 도메인 구조

서버 구성은 다음과 같은 계층으로 구성한다.

```text
Project
   │
   └── ServerConfiguration
           │
           ├── WEB
           │      ├── Server
           │      └── Server
           │
           ├── WAS
           │      ├── Server
           │      └── Server
           │
           ├── AP
           │
           ├── GW
           │
           └── DB
                  ├── Server
                  └── Server
```

중요한 점은 `Project`가 서버 유형을 직접 여러 개 관리하는 것이 아니라,

```text
Project
  ↓
ServerConfiguration
  ↓
Server
```

형태로 분리하는 것이다.

---

# 4. Entity 설계

## 4.1 ServerType

서버 유형은 Enum으로 관리한다.

```java
public enum ServerType {

    WEB,
    WAS,
    AP,
    GW,
    DB
}
```

향후 필요할 경우 서버 유형을 추가할 수 있도록 설계한다.

예:

```text
WEB
WAS
AP
GW
DB
CACHE
MQ
BATCH
```

단, 초기 구현에서는 WEB/WAS/AP/GW/DB만 지원한다.

---

# 5. ServerConfiguration

프로젝트에 어떤 서버 유형이 필요한지를 관리하는 구성 정보이다.

예:

```text
Project
PMIS 구축

ServerConfiguration

WEB  : 사용
WAS  : 사용
AP   : 사용
GW   : 사용
DB   : 사용
```

또는

```text
Project
단순 Web 시스템

WEB  : 사용
WAS  : 사용
AP   : 미사용
GW   : 미사용
DB   : 사용
```

## 주요 필드

```text
id
projectId
serverType
enabled
description
createdAt
updatedAt
```

### enabled

해당 서버 유형을 프로젝트에서 사용하는지 여부를 나타낸다.

```text
true  = 사용
false = 미사용
```

다만 실제 서버가 존재하는지를 의미하는 것이 아니라 **구성 계획상 필요한 서버 유형인지 여부**를 의미한다.

---

# 6. Server

실제 서버 단위의 정보를 관리한다.

예:

```text
WEB
 ├── WEB-01
 └── WEB-02

WAS
 ├── WAS-01
 └── WAS-02

DB
 └── DB-01
```

## 주요 필드

```text
id
projectId
serverConfigurationId
serverName
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

---

# 7. 서버 환경

서버가 어느 환경에 설치되는지를 관리한다.

```java
public enum ServerEnvironment {

    DEV,
    TEST,
    STAGING,
    PROD
}
```

필요할 경우 프로젝트의 실제 환경 정책에 따라 확장한다.

---

# 8. 설치 상태

서버 설치 진행 상태를 관리한다.

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

의미는 다음과 같다.

| 상태 | 의미 |
|---|---|
| PLANNED | 설치 예정 |
| REQUESTED | 설치 요청 |
| INSTALLING | 설치 진행 중 |
| INSTALLED | 설치 완료 |
| COMPLETED | 검증까지 완료 |
| FAILED | 설치 실패 |

---

# 9. 서버 구성 예시

## Case 1. 전체 구성

```text
Project
 └── PMIS 구축

      ├── WEB
      │    ├── WEB-01
      │    └── WEB-02
      │
      ├── WAS
      │    ├── WAS-01
      │    └── WAS-02
      │
      ├── AP
      │    └── AP-01
      │
      ├── GW
      │    └── GW-01
      │
      └── DB
           ├── DB-01
           └── DB-02
```

---

## Case 2. 일부 구성

```text
Project
 └── 업무 시스템

      ├── WEB
      │    └── WEB-01
      │
      ├── WAS
      │    └── WAS-01
      │
      └── DB
           └── DB-01
```

AP와 GW가 없는 구성이다.

---

## Case 3. WAS + DB

```text
Project
 └── 내부 업무 시스템

      ├── WAS
      │    └── WAS-01
      │
      └── DB
           └── DB-01
```

---

## Case 4. AP + GW + DB

```text
Project
 └── 연계 시스템

      ├── AP
      │    └── AP-01
      │
      ├── GW
      │    └── GW-01
      │
      └── DB
           └── DB-01
```

---

# 10. API 설계

Base URL:

```text
/api/projects/{projectId}/servers
```

## 10.1 프로젝트 서버 구성 조회

```http
GET /api/projects/{projectId}/servers
```

응답 예:

```json
{
  "success": true,
  "code": "SUCCESS",
  "data": {
    "projectId": 1,
    "serverConfigurations": [
      {
        "serverType": "WEB",
        "enabled": true,
        "serverCount": 2
      },
      {
        "serverType": "WAS",
        "enabled": true,
        "serverCount": 2
      },
      {
        "serverType": "AP",
        "enabled": true,
        "serverCount": 1
      },
      {
        "serverType": "GW",
        "enabled": true,
        "serverCount": 1
      },
      {
        "serverType": "DB",
        "enabled": true,
        "serverCount": 2
      }
    ]
  }
}
```

---

# 11. 서버 유형 구성 API

## 서버 유형 추가

```http
POST /api/projects/{projectId}/server-configurations
```

Request:

```json
{
  "serverType": "WAS",
  "enabled": true,
  "description": "WAS 서버 구성"
}
```

---

## 서버 유형 수정

```http
PUT /api/projects/{projectId}/server-configurations/{configurationId}
```

Request:

```json
{
  "enabled": true,
  "description": "WAS 이중화 구성"
}
```

---

## 서버 유형 삭제

```http
DELETE /api/projects/{projectId}/server-configurations/{configurationId}
```

단, 해당 구성에 실제 서버가 등록되어 있는 경우 삭제 정책을 별도로 정의한다.

권장 정책:

```text
서버 존재
    ↓
삭제 불가
    ↓
서버 먼저 삭제
    ↓
구성 삭제
```

---

# 12. 서버 API

## 서버 등록

```http
POST /api/projects/{projectId}/servers
```

Request:

```json
{
  "serverType": "WAS",
  "serverName": "WAS-01",
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

---

## 서버 조회

```http
GET /api/projects/{projectId}/servers/{serverId}
```

---

## 서버 검색

```http
GET /api/projects/{projectId}/servers
```

검색 조건 예:

```text
serverType
serverName
environment
installationStatus
```

---

## 서버 수정

```http
PUT /api/projects/{projectId}/servers/{serverId}
```

---

## 서버 삭제

```http
DELETE /api/projects/{projectId}/servers/{serverId}
```

---

# 13. DTO 구조

권장 패키지:

```text
project
 ├── controller
 ├── dto
 │   ├── request
 │   │   ├── CreateServerConfigurationRequest
 │   │   ├── UpdateServerConfigurationRequest
 │   │   ├── CreateServerRequest
 │   │   └── UpdateServerRequest
 │   │
 │   └── response
 │       ├── ServerConfigurationResponse
 │       ├── ServerResponse
 │       └── ProjectServerDashboardResponse
 │
 ├── entity
 │   ├── Server
 │   ├── ServerConfiguration
 │   ├── ServerType
 │   ├── ServerEnvironment
 │   └── InstallationStatus
 │
 ├── mapper
 ├── repository
 └── service
```

---

# 14. Repository

## ServerConfigurationRepository

```java
public interface ServerConfigurationRepository
        extends JpaRepository<ServerConfiguration, Long> {

    List<ServerConfiguration> findByProjectId(Long projectId);

    Optional<ServerConfiguration> findByProjectIdAndServerType(
            Long projectId,
            ServerType serverType
    );

    boolean existsByProjectIdAndServerType(
            Long projectId,
            ServerType serverType
    );
}
```

## ServerRepository

```java
public interface ServerRepository
        extends JpaRepository<Server, Long> {

    List<Server> findByProjectId(Long projectId);

    List<Server> findByProjectIdAndServerType(
            Long projectId,
            ServerType serverType
    );

    long countByProjectId(Long projectId);

    long countByProjectIdAndServerType(
            Long projectId,
            ServerType serverType
    );

    long countByProjectIdAndInstallationStatus(
            Long projectId,
            InstallationStatus status
    );
}
```

---

# 15. 서비스 책임

## ServerConfigurationService

책임:

- 서버 유형 구성 등록
- 서버 유형 구성 조회
- 서버 유형 구성 수정
- 서버 유형 구성 삭제
- 프로젝트 존재 여부 검증
- 동일 서버 유형 중복 등록 방지

---

## ServerService

책임:

- 서버 등록
- 서버 조회
- 서버 검색
- 서버 수정
- 서버 삭제
- 서버 설치 상태 변경
- 서버 유형 검증

---

## ProjectServerDashboardService

프로젝트 Dashboard에서 서버 구성 현황을 제공한다.

예:

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
    "INSTALLED": 3
  }
}
```

---

# 16. 권한

기존 PMIS 권한 체계를 따른다.

### 조회

```text
USER
PM
ADMIN
```

### 등록 / 수정

```text
PM
ADMIN
```

### 삭제

```text
ADMIN
```

예:

```java
@PreAuthorize("hasRole('PM')")
```

---

# 17. 검증 정책

## 프로젝트 존재 여부

서버 등록 전에 프로젝트가 존재하는지 검증한다.

```text
Project Not Found
→ RESOURCE_NOT_FOUND
```

## 서버 유형 중복

동일 프로젝트에 동일한 `ServerConfiguration`을 중복 생성하지 않는다.

```text
Project 1
WEB
WEB
```

위와 같은 중복 구성은 허용하지 않는다.

단, WEB 서버가 여러 대 존재하는 것은 허용한다.

```text
WEB Configuration
 ├── WEB-01
 ├── WEB-02
 └── WEB-03
```

즉,

```text
ServerConfiguration
    = 서버 유형 구성

Server
    = 실제 서버 인스턴스
```

로 구분한다.

---

# 18. 핵심 설계 원칙

이번 기능에서 가장 중요한 원칙은 다음과 같다.

### 18.1 서버 유형은 선택적이다

모든 프로젝트가

```text
WEB + WAS + AP + GW + DB
```

를 가져야 하는 것은 아니다.

프로젝트 요구사항에 따라 다음과 같이 자유롭게 구성할 수 있어야 한다.

```text
WEB + WAS + DB
```

또는

```text
WAS + DB
```

또는

```text
AP + GW + DB
```

등이 가능해야 한다.

---

### 18.2 서버 유형과 실제 서버를 분리한다

다음 두 개념을 반드시 분리한다.

```text
ServerConfiguration
```

프로젝트가 어떤 서버 유형을 사용하는가?

```text
Server
```

실제로 몇 대의 서버가 존재하는가?

예:

```text
WAS
 ├── WAS-01
 ├── WAS-02
 └── WAS-03
```

이 경우 WAS Configuration은 1개이고 실제 Server는 3개이다.

---

### 18.3 향후 설치/진척 관리와 연계한다

현재 PMIS가 프로젝트의 WBS, 일정, 설치 대상, 설치 결과 및 이슈를 관리하는 방향이므로 서버 정보 역시 향후 다음 기능과 연결할 수 있어야 한다.

```text
Project
   │
   ├── WBS
   │
   ├── Schedule
   │
   ├── Server Configuration
   │       │
   │       └── Server
   │
   ├── Issue
   │
   └── Report
```

향후에는

```text
Server
 ↓
설치 일정
 ↓
설치 요청
 ↓
설치 결과
 ↓
검증
 ↓
이슈
```

와 같은 PMO 업무 흐름으로 확장할 수 있다.

---

# 19. 1차 개발 범위

이번 개발에서는 우선 다음 기능까지만 구현한다.

```text
[1] ServerType
[2] ServerConfiguration
[3] Server
[4] 프로젝트별 서버 구성 등록
[5] 프로젝트별 서버 구성 조회
[6] 실제 서버 등록
[7] 서버 조회
[8] 서버 수정
[9] 서버 삭제
[10] 설치 상태 관리
```

Dashboard 연계는 기본적인 집계 API까지 구현한다.

---

# 20. 향후 확장 범위

2차 이후 다음 기능을 확장할 수 있다.

```text
서버 설치 일정
      ↓
서버 설치 요청
      ↓
서버 설치 결과
      ↓
서버 검증
      ↓
이슈 등록
      ↓
조치 결과
```

또한 다음 정보까지 확장할 수 있다.

- 서버 계정
- IP / VIP
- Port
- OS Version
- CPU / Memory / Disk
- WAS 제품 및 Version
- DB 제품 및 Version
- Middleware
- 설치 담당 업체
- 설치 담당자
- 설치 예정일
- 설치 완료일
- 검수 상태
- 관련 문서
- 첨부파일

---

# 21. 구현 우선순위

Spring Boot 개발 순서는 다음을 권장한다.

```text
1. ServerType Enum
       ↓
2. ServerEnvironment Enum
       ↓
3. InstallationStatus Enum
       ↓
4. ServerConfiguration Entity
       ↓
5. Server Entity
       ↓
6. Repository
       ↓
7. Request DTO
       ↓
8. Response DTO
       ↓
9. Mapper
       ↓
10. Service
       ↓
11. Controller
       ↓
12. Swagger API 검증
       ↓
13. Dashboard 연계
```

---

# 22. 최종 목표

최종적으로 PMIS에서는 프로젝트 상세 화면에서 다음과 같은 형태로 서버 구성을 확인할 수 있어야 한다.

```text
┌─────────────────────────────────────────────┐
│ 프로젝트 서버 구성                          │
├────────┬──────────────┬─────────────────────┤
│ 유형   │ 서버 수      │ 상태                │
├────────┼──────────────┼─────────────────────┤
│ WEB    │ 2            │ 설치완료            │
│ WAS    │ 2            │ 설치진행            │
│ AP     │ 1            │ 설치완료            │
│ GW     │ 1            │ 설치예정            │
│ DB     │ 2            │ 설치완료            │
└────────┴──────────────┴─────────────────────┘
```

그리고 서버 상세에서는 다음과 같이 조회할 수 있어야 한다.

```text
WAS-01
--------------------------------
Server Type : WAS
Hostname    : was01
IP Address  : 10.10.10.101
OS          : Linux
CPU         : 8 Core
Memory      : 32 GB
Disk        : 500 GB
Environment : PROD
Status      : INSTALLED
```

이를 기반으로 향후 PMIS의 **인프라 구성 → 설치 일정 → 설치 결과 → 이슈 → 검수**까지 연결하는 것을 목표로 한다.