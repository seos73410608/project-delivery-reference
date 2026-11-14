# CMDB Server API 명세

## 1. 목적

서버 CI 등록 시 공통 `ConfigurationItem` 정보와 서버 전용 `ServerDetail` 정보를 하나의 API 요청으로 생성하고, CI ID를 기준으로 서버 상세정보를 조회한다.

- 공통 CI 정보는 `ConfigurationItem`에 저장한다.
- 서버 전용 정보는 `ServerDetail`에 저장한다.
- 등록 시 두 데이터는 하나의 트랜잭션으로 처리한다.
- 기존 `POST /api/configuration-items` API는 변경하지 않는다.
- 서버 상세정보 조회는 기존 CI와 `ServerDetail`의 1:1 관계를 기준으로 처리한다.

## 2. 데이터 구조

### 2.1 ConfigurationItem

기존 CI 공통 속성을 그대로 사용한다.

| 필드 | 설명 | 필수 |
|---|---|---|
| ciName | CI 이름 | Y |
| ciTypeId | CI 유형 ID | Y |
| environment | 운영 환경 | N |
| managerId | 담당자 ID | N |
| location | 설치 위치 | N |
| description | 설명 | N |

### 2.2 ServerDetail

`ConfigurationItem`과 1:1 관계로 관리한다.

| 필드 | 설명 | 필수 |
|---|---|---|
| hostname | 서버 호스트명 | Y |
| managementIp | 관리 IP | N |
| serverCategory | 서버 구분(PHYSICAL / VIRTUAL) | Y |
| osName | 운영체제 이름 | N |
| osVersion | 운영체제 버전 | N |
| cpuCores | CPU 코어 수 | N |
| memorySizeMb | 메모리 크기(MB) | N |
| virtualizationPlatform | 가상화 플랫폼 | N |

- ServerDetail은 ConfigurationItem ID를 참조한다.
- ConfigurationItem ID에는 UNIQUE 제약조건을 적용한다.
- 디스크와 네트워크 인터페이스는 다중 데이터이므로 별도 하위 엔티티로 확장한다.
- hostname과 managementIp에는 초기 단계에서 UNIQUE 제약조건을 적용하지 않는다.

## 3. 서버 등록 API

### 3.1 기본 정보

| 항목 | 내용 |
|---|---|
| Method | POST |
| URL | /api/servers |
| 권한 | PM 이상 |
| Content-Type | application/json |
| 성공 HTTP Status | 201 Created |

### 3.2 Request

```json
{
  "ciName": "운영 WEB 서버 01",
  "ciTypeId": 1,
  "environment": "PROD",
  "managerId": 1,
  "location": "IDC-A",
  "description": "운영 WEB 서버",
  "hostname": "web-prod-01",
  "managementIp": "192.168.10.11",
  "serverCategory": "PHYSICAL",
  "osName": "AIX",
  "osVersion": "7.2",
  "cpuCores": 8,
  "memorySizeMb": 32768,
  "virtualizationPlatform": null
}
```

### 3.3 Request 필드 규칙

- ciName, ciTypeId, hostname, serverCategory는 필수다.
- ciTypeId는 활성화 및 할당 가능한 SERVER 유형이어야 한다.
- serverCategory는 PHYSICAL 또는 VIRTUAL만 허용한다.
- virtualizationPlatform은 선택 항목이다.
- cpuCores와 memorySizeMb는 입력 시 양수여야 한다.
- 문자열 길이와 공통 CI 필드의 유효성은 기존 CI 등록 API 정책을 따른다.
- environment는 기존 CiEnvironment 정책을 따른다.

### 3.4 Response

HTTP 201 Created

```json
{
  "id": 100,
  "ciCode": "CI-000101",
  "ciName": "운영 WEB 서버 01",
  "ciTypeId": 1,
  "ciTypeCode": "SERVER",
  "ciTypeName": "서버",
  "status": "PLANNED",
  "environment": "PROD",
  "managerId": 1,
  "managerName": "담당자",
  "location": "IDC-A",
  "description": "운영 WEB 서버",
  "serverDetail": {
    "hostname": "web-prod-01",
    "managementIp": "192.168.10.11",
    "serverCategory": "PHYSICAL",
    "osName": "AIX",
    "osVersion": "7.2",
    "cpuCores": 8,
    "memorySizeMb": 32768,
    "virtualizationPlatform": null
  },
  "createdAt": "2026-11-13T10:00:00",
  "updatedAt": "2026-11-13T10:00:00"
}
```

- 공통 CI 응답 필드는 기존 ConfigurationItemResponse를 기준으로 한다.
- serverDetail은 서버 전용 응답 객체로 제공한다.
- status와 시간 값은 실제 공통 CI 생성 결과를 반환한다.

## 4. 서버 유형 검증

서버 등록 요청의 `ciTypeId`는 기존 `CiTypeRepository`를 통해 유형을 확인한다.

검증 순서:

1. 요청한 CI 유형이 존재하는지 확인한다.
2. 유형이 활성화되어 있는지 확인한다.
3. 유형이 할당 가능한지 확인한다.
4. 유형 코드가 정확히 `SERVER`인지 확인한다.

SERVER 하위 유형을 서버 구분 용도로 사용하지 않는다. 물리/가상 구분은 `serverCategory`에서 관리한다.

유형 미존재, 비활성화, 할당 불가 오류는 기존 CI 유형 오류 코드를 재사용한다. 위 검증을 통과한 유형이 `SERVER`가 아닌 경우 `CMDB_SERVER_001`을 반환한다.

## 5. 트랜잭션 정책

`ServerRegistrationService`가 서버 등록 트랜잭션을 소유한다.

처리 순서:

1. 요청 필드 검증
2. SERVER CI 유형 검증
3. 담당자 검증
4. CI 코드 생성
5. ConfigurationItem 생성 및 저장
6. ServerDetail 생성 및 저장
7. 통합 응답 반환

ConfigurationItem 또는 ServerDetail 저장 중 오류가 발생하면 전체 트랜잭션을 롤백한다.

부분적으로 CI만 생성되거나 ServerDetail만 생성되는 상태는 허용하지 않는다.

기존 ConfigurationItemService의 공통 생성 로직은 재사용할 수 있도록 공통 생성 책임을 분리한다. 기존 CI 등록 API의 동작과 계약은 유지한다.

## 6. 오류 코드

| 오류 코드 | HTTP | 설명 |
|---|---:|---|
| CMDB_SERVER_001 | 400 | 서버 CI 유형이 아님 |
| CMDB_SERVER_002 | 404 | 서버 상세정보를 찾을 수 없음 |
| CMDB_CI_001 | 404 | 기존 CI 조회 오류 |
| CMDB_CI_002 | 400 | CI 유형이 할당 불가능함 |
| CMDB_CI_003 | 404 | 담당자 없음 |
| CMDB_CI_004 | 400 | 담당자 비활성화 |
| 기존 공통 검증 오류 | 400 | 필수값 또는 입력 형식 오류 |

- 유형 미존재, 비활성화, 할당 불가 오류는 기존 CI/CI 유형 오류 코드를 재사용한다.
- SERVER가 아닌 유형을 요청한 경우 CMDB_SERVER_001을 반환한다.
- 존재하는 SERVER CI에 서버 상세정보가 없는 경우 CMDB_SERVER_002를 반환한다.
- DB 제약조건 위반 등 예상하지 못한 오류는 공통 예외 처리 정책을 따른다.

## 7. 초기 범위에서 제외할 항목

- 디스크 상세 정보 관리
- 네트워크 인터페이스 다중 관리
- CPU 모델 및 소켓 상세 정보
- 서버별 소프트웨어 설치 목록
- hostname 및 managementIp 중복 차단
- 서버 등록과 별개인 ServerDetail 단독 생성 API

## 8. 서버 상세정보 조회 API

### 8.1 기본 정보

| 항목 | 내용 |
|---|---|
| Method | GET |
| URL | /api/configuration-items/{ciId}/server-detail |
| 권한 | USER 이상 |
| Content-Type | application/json |
| 성공 HTTP Status | 200 OK |

### 8.2 Request

별도 Request Body는 사용하지 않는다.

**Path Parameter**

| 필드 | 설명 | 필수 |
|---|---|---|
| ciId | 조회할 ConfigurationItem ID | Y |

호출 예시:

```http
GET /api/configuration-items/100/server-detail
```

### 8.3 조회 처리 규칙

서버 상세정보 조회는 `ServerDetailQueryService`에서 처리한다.

조회 순서:

1. ciId에 해당하는 ConfigurationItem의 존재 여부를 확인한다.
2. CI 유형 코드가 정확히 `SERVER`인지 확인한다.
3. 해당 CI ID에 연결된 ServerDetail을 조회한다.
4. 공통 CI 정보와 서버 상세정보를 통합 응답으로 반환한다.

검증 및 조회 결과에 따른 처리:

- CI가 존재하지 않으면 `CMDB_CI_001`을 반환한다.
- CI 유형이 SERVER가 아니면 `CMDB_SERVER_001`을 반환한다.
- SERVER CI에 연결된 ServerDetail이 없으면 `CMDB_SERVER_002`를 반환한다.

### 8.4 Response

HTTP 200 OK

등록 API와 동일한 `ServerRegistrationResponse` 응답 구조를 사용한다.

```json
{
  "success": true,
  "message": "서버 상세정보를 조회했습니다.",
  "data": {
    "id": 100,
    "ciCode": "CI-000101",
    "ciName": "운영 WEB 서버 01",
    "ciTypeId": 1,
    "ciTypeCode": "SERVER",
    "ciTypeName": "서버",
    "status": "PLANNED",
    "environment": "PROD",
    "managerId": 1,
    "managerName": "담당자",
    "location": "IDC-A",
    "description": "운영 WEB 서버",
    "serverDetail": {
      "hostname": "web-prod-01",
      "managementIp": "192.168.10.11",
      "serverCategory": "PHYSICAL",
      "osName": "AIX",
      "osVersion": "7.2",
      "cpuCores": 8,
      "memorySizeMb": 32768,
      "virtualizationPlatform": null
    },
    "createdAt": "2026-11-13T10:00:00",
    "updatedAt": "2026-11-13T10:00:00"
  }
}
```

- 공통 CI 응답 필드는 ConfigurationItemResponse를 기준으로 한다.
- serverDetail은 ServerDetailResponse 객체로 제공한다.
- 등록 API와 조회 API는 동일한 ServerRegistrationResponse 응답 구조를 사용한다.
- 조회 API는 데이터를 변경하지 않는 읽기 전용 기능이다.

### 8.5 오류 처리

| 상황 | HTTP | 오류 코드 |
|---|---:|---|
| CI가 존재하지 않음 | 404 | CMDB_CI_001 |
| CI 유형이 SERVER가 아님 | 400 | CMDB_SERVER_001 |
| SERVER CI의 상세정보가 존재하지 않음 | 404 | CMDB_SERVER_002 |
| 인증되지 않은 요청 | 401 | 공통 인증 오류 |
| 권한이 없는 요청 | 403 | 공통 권한 오류 |

## 9. 후속 API 후보

서버 상세정보 수정 API는 조회 API 구현 이후 별도 계약으로 확장한다.

- `PUT /api/configuration-items/{ciId}/server-detail`

수정 API는 기존 CI의 존재 여부와 서버 상세정보의 1:1 관계를 기준으로 설계한다.
