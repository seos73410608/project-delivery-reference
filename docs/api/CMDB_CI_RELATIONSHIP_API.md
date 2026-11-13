
# CMDB CI Relationship API 명세

- **문서 유형:** Backend API Contract
- **기능:** CMDB Configuration Item Relationship 관리
- **구현 브랜치:** `feature/cmdb-relationship`
- **구현 커밋:** `919f873`
- **기준일:** 2026-11-13
- **Base Path:** `/api`

---

## 1. 개요

본 문서는 CMDB Configuration Item(CI) 간 관계를 관리하는 API의 요청·응답 규격과 예외 처리 정책을 정의한다.

프론트엔드와 백엔드 간 연동 기준으로 사용하며, 실제 구현된 Controller, DTO, Service 및 공통 예외 처리 규격을 기준으로 작성한다.

### 1.1 주요 기능

- CI 간 관계 생성
- CI 기준 관계 목록 조회
- 관계 상세 조회
- 관계 설명 수정
- 관계 비활성화
- Relationship Type 조회

### 1.2 관계 구조

관계는 방향성을 가진다.

```text
Source CI ── Relationship Type ──> Target CI

예:
Application CI ── RUNS_ON ──> Server CI
Application CI ── DEPENDS_ON ──> Database CI
```

관계 식별자는 다음 조합으로 관리한다.

```text
(source_ci_id, target_ci_id, relationship_type_id)
```

동일한 Source CI, Target CI, Relationship Type 조합은 하나의 관계만 허용한다.

### 1.3 Relationship Type

초기 Migration에서 등록하는 관계 유형은 다음과 같다.

| Code | 설명 |
|---|---|
| RUNS_ON | 실행 환경 관계 |
| DEPENDS_ON | 의존 관계 |
| HOSTED_ON | 호스팅 관계 |
| CONNECTS_TO | 연결 관계 |
| CONTAINS | 포함 관계 |

관계 유형은 별도 코드 테이블에서 관리하며 활성화 여부를 가진다.

---

## 2. 인증 및 권한

### 2.1 인증 방식

JWT 기반 인증을 사용한다.

인증이 필요한 API 요청에는 다음 Authorization Header를 전달한다.

```http
Authorization: Bearer {accessToken}
```

### 2.2 역할별 권한

| 역할 | 권한 |
|---|---|
| USER | 관계 조회, 활성 Relationship Type 조회 |
| PM | USER 권한 + 관계 생성, 수정, 비활성화 |
| ADMIN | PM 권한 + 전체 Relationship Type 조회 |

역할 계층:

```text
ROLE_ADMIN
    >
ROLE_PM
    >
ROLE_USER
```

### 2.3 API별 권한

| Method | Endpoint | 권한 |
|---|---|---|
| POST | `/api/configuration-items/{ciId}/relationships` | PM |
| GET | `/api/configuration-items/{ciId}/relationships` | USER |
| GET | `/api/ci-relationships/{id}` | USER |
| PUT | `/api/ci-relationships/{id}` | PM |
| PATCH | `/api/ci-relationships/{id}/deactivate` | PM |
| GET | `/api/ci-relationship-types` | USER |
| GET | `/api/ci-relationship-types/all` | ADMIN |

---

## 3. 공통 응답 규격

모든 Relationship API는 `ApiResponse<T>` 구조를 사용한다.

### 3.1 성공 응답

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "Configuration Item 관계가 생성되었습니다.",
  "data": {
    "id": 1
  }
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| success | Boolean | 요청 성공 여부 |
| code | String | 성공 코드 `SUCCESS` |
| message | String | 선택적 성공 메시지 |
| data | Object / Array | API 반환 데이터 |

데이터만 반환하는 성공 응답에서는 `message`가 생략된다.

### 3.2 실패 응답

```json
{
  "success": false,
  "code": "CMDB_CI_RELATIONSHIP_001",
  "message": "CI Relationship을 찾을 수 없습니다."
}
```

실패 응답에서는 `data`가 null이므로 JSON에서 생략된다.

### 3.3 프론트엔드 처리 기준

- HTTP 상태 코드와 `success`를 함께 확인한다.
- 업무 오류 처리는 `code`를 기준으로 분기한다.
- 사용자에게 보여주는 오류 문구는 `message`를 활용한다.
- 성공 응답의 `message`는 없을 수 있으므로 필수값으로 취급하지 않는다.
- 목록 데이터는 `data` 배열로 처리한다.
- 실패 응답에는 `data`가 포함되지 않는다.

---

## 4. API 상세 명세

### 4.1 CI 관계 생성

**POST** `/api/configuration-items/{ciId}/relationships`

- 권한: PM
- 성공 HTTP 상태: `201 Created`
- 목적: Source CI와 Target CI 사이의 관계 생성

#### Path Parameter

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| ciId | Long | Y | Source CI ID |

#### Request Body

```json
{
  "targetCiId": 20,
  "relationshipTypeId": 1,
  "description": "애플리케이션 실행 서버 관계"
}
```

| 필드 | 타입 | 필수 | 검증 |
|---|---|---|---|
| targetCiId | Long | Y | 양수 |
| relationshipTypeId | Long | Y | 양수 |
| description | String | N | 최대 1000자 |

#### 성공 응답

HTTP `201 Created`

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "Configuration Item 관계가 생성되었습니다.",
  "data": {
    "id": 100,
    "sourceCiId": 10,
    "sourceCiCode": "APP-001",
    "sourceCiName": "Payment Application",
    "targetCiId": 20,
    "targetCiCode": "SRV-001",
    "targetCiName": "Payment Server",
    "relationshipTypeId": 1,
    "relationshipTypeCode": "RUNS_ON",
    "relationshipTypeName": "Runs On",
    "description": "애플리케이션 실행 서버 관계",
    "status": "ACTIVE",
    "deactivatedAt": null,
    "deactivatedBy": null,
    "deactivationReason": null
  }
}
```

위 JSON은 응답 필드 구조를 설명하기 위한 예시이며 ID와 CI 정보는 실제 데이터에 따라 달라진다.

#### 생성 및 재등록 정책

| 기존 관계 | 처리 |
|---|---|
| 존재하지 않음 | 신규 관계 생성 |
| ACTIVE | 기존 관계 그대로 반환 |
| INACTIVE | 기존 ID를 유지하고 재활성화 |

추가 규칙:

- Source CI와 Target CI는 서로 달라야 한다.
- 비활성 Relationship Type은 신규 생성에 사용할 수 없다.
- 이미 ACTIVE인 관계는 Relationship Type이 비활성화되어 있어도 기존 관계를 반환한다.
- ACTIVE 관계의 재등록 요청은 기존 설명을 변경하지 않는다.
- INACTIVE 관계 재등록 시 요청한 설명으로 갱신한다.
- 재활성화 시 기존 비활성화 정보는 초기화된다.
- 재등록 시 새로운 Relationship ID를 발급하지 않는다.

### 4.2 CI 관계 목록 조회

**GET** `/api/configuration-items/{ciId}/relationships`

- 권한: USER
- 성공 HTTP 상태: `200 OK`
- 목적: 특정 CI와 연결된 관계 목록 조회

#### Path Parameter

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| ciId | Long | Y | 기준 CI ID |

#### Query Parameter

| 이름 | 타입 | 필수 | 기본값 | 설명 |
|---|---|---|---|---|
| direction | RelationshipDirection | N | ALL | 관계 방향 |
| status | RelationshipStatus | N | 전체 | 관계 상태 |
| relationshipTypeId | Long | N | 전체 | 관계 유형 ID |

`direction` 허용값:

| 값 | 설명 |
|---|---|
| OUTGOING | 기준 CI가 Source인 관계 |
| INCOMING | 기준 CI가 Target인 관계 |
| ALL | 양방향 관계 전체 |

`status` 허용값:

| 값 | 설명 |
|---|---|
| ACTIVE | 활성 관계 |
| INACTIVE | 비활성 관계 |

#### 호출 예시

OUTGOING 조회:

```http
GET /api/configuration-items/10/relationships?direction=OUTGOING
```

ACTIVE 관계만 조회:

```http
GET /api/configuration-items/10/relationships?status=ACTIVE
```

관계 유형 필터:

```http
GET /api/configuration-items/10/relationships?relationshipTypeId=1
```

복합 필터:

```http
GET /api/configuration-items/10/relationships?direction=ALL&status=ACTIVE&relationshipTypeId=1
```

#### 성공 응답

```json
{
  "success": true,
  "code": "SUCCESS",
  "data": [
    {
      "id": 100,
      "sourceCiId": 10,
      "sourceCiCode": "APP-001",
      "sourceCiName": "Payment Application",
      "targetCiId": 20,
      "targetCiCode": "SRV-001",
      "targetCiName": "Payment Server",
      "relationshipTypeId": 1,
      "relationshipTypeCode": "RUNS_ON",
      "relationshipTypeName": "Runs On",
      "description": "애플리케이션 실행 서버 관계",
      "status": "ACTIVE",
      "deactivatedAt": null,
      "deactivatedBy": null,
      "deactivationReason": null
    }
  ]
}
```

조회 결과가 없는 경우 빈 배열을 반환한다.

```json
{
  "success": true,
  "code": "SUCCESS",
  "data": []
}
```

### 4.3 CI 관계 상세 조회

**GET** `/api/ci-relationships/{id}`

- 권한: USER
- 성공 HTTP 상태: `200 OK`

#### Path Parameter

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| id | Long | Y | Relationship ID |

#### 호출 예시

```http
GET /api/ci-relationships/100
```

성공 응답의 `data`는 4.1의 CI 관계 응답 구조와 동일하다.

### 4.4 CI 관계 설명 수정

**PUT** `/api/ci-relationships/{id}`

- 권한: PM
- 성공 HTTP 상태: `200 OK`

#### Path Parameter

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| id | Long | Y | Relationship ID |

#### Request Body

```json
{
  "description": "변경된 관계 설명"
}
```

| 필드 | 타입 | 필수 | 검증 |
|---|---|---|---|
| description | String | N | 최대 1000자 |

#### 성공 응답

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "Configuration Item 관계가 수정되었습니다.",
  "data": {
    "id": 100,
    "sourceCiId": 10,
    "sourceCiCode": "APP-001",
    "sourceCiName": "Payment Application",
    "targetCiId": 20,
    "targetCiCode": "SRV-001",
    "targetCiName": "Payment Server",
    "relationshipTypeId": 1,
    "relationshipTypeCode": "RUNS_ON",
    "relationshipTypeName": "Runs On",
    "description": "변경된 관계 설명",
    "status": "ACTIVE",
    "deactivatedAt": null,
    "deactivatedBy": null,
    "deactivationReason": null
  }
}
```

#### 수정 정책

- 관계 설명만 수정할 수 있다.
- Source CI, Target CI, Relationship Type은 변경할 수 없다.
- 관계 식별자는 생성 이후 변경하지 않는다.

### 4.5 CI 관계 비활성화

**PATCH** `/api/ci-relationships/{id}/deactivate`

- 권한: PM
- 성공 HTTP 상태: `200 OK`

#### Path Parameter

| 이름 | 타입 | 필수 | 설명 |
|---|---|---|---|
| id | Long | Y | Relationship ID |

#### Request Body

```json
{
  "reason": "시스템 구성 변경으로 인한 관계 종료"
}
```

| 필드 | 타입 | 필수 | 검증 |
|---|---|---|---|
| reason | String | Y | 공백 불가, 최대 500자 |

#### 성공 응답

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "Configuration Item 관계가 비활성화되었습니다.",
  "data": {
    "id": 100,
    "sourceCiId": 10,
    "sourceCiCode": "APP-001",
    "sourceCiName": "Payment Application",
    "targetCiId": 20,
    "targetCiCode": "SRV-001",
    "targetCiName": "Payment Server",
    "relationshipTypeId": 1,
    "relationshipTypeCode": "RUNS_ON",
    "relationshipTypeName": "Runs On",
    "description": "애플리케이션 실행 서버 관계",
    "status": "INACTIVE",
    "deactivatedAt": "2026-11-13T12:00:00",
    "deactivatedBy": 1,
    "deactivationReason": "시스템 구성 변경으로 인한 관계 종료"
  }
}
```

#### 비활성화 처리

- 상태를 `INACTIVE`로 변경한다.
- 비활성화 일시를 기록한다.
- 현재 인증된 사용자를 비활성화 사용자로 기록한다.
- 비활성화 사유를 기록한다.
- 관계 데이터는 물리 삭제하지 않는다.
- 이미 비활성화된 관계에 대한 호출은 상태를 다시 변경하지 않는 멱등적 동작이다.

### 4.6 활성 Relationship Type 조회

**GET** `/api/ci-relationship-types`

- 권한: USER
- 성공 HTTP 상태: `200 OK`

#### 호출 예시

```http
GET /api/ci-relationship-types
```

#### 성공 응답

```json
{
  "success": true,
  "code": "SUCCESS",
  "data": [
    {
      "id": 1,
      "code": "RUNS_ON",
      "name": "Runs On",
      "description": "실행 환경 관계",
      "enabled": true,
      "sortOrder": 1
    }
  ]
}
```

활성화된 유형만 반환한다.

정렬 기준:

1. sortOrder 오름차순
2. name 오름차순

### 4.7 전체 Relationship Type 조회

**GET** `/api/ci-relationship-types/all`

- 권한: ADMIN
- 성공 HTTP 상태: `200 OK`

#### 호출 예시

```http
GET /api/ci-relationship-types/all
```

성공 응답의 `data`는 4.6의 Relationship Type 구조와 동일하다.

활성화 여부와 관계없이 전체 유형을 반환한다.

정렬 기준:

1. sortOrder 오름차순
2. name 오름차순

---

## 5. 응답 DTO 정의

### 5.1 CiRelationshipResponse

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long | 관계 ID |
| sourceCiId | Long | Source CI ID |
| sourceCiCode | String | Source CI 코드 |
| sourceCiName | String | Source CI 이름 |
| targetCiId | Long | Target CI ID |
| targetCiCode | String | Target CI 코드 |
| targetCiName | String | Target CI 이름 |
| relationshipTypeId | Long | 관계 유형 ID |
| relationshipTypeCode | String | 관계 유형 코드 |
| relationshipTypeName | String | 관계 유형 이름 |
| description | String | 관계 설명 |
| status | String | ACTIVE / INACTIVE |
| deactivatedAt | LocalDateTime | 비활성화 일시 |
| deactivatedBy | Long | 비활성화 수행 사용자 ID |
| deactivationReason | String | 비활성화 사유 |

활성 상태에서는 비활성화 관련 필드가 null이다.

### 5.2 CiRelationshipTypeResponse

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long | 관계 유형 ID |
| code | String | 관계 유형 코드 |
| name | String | 관계 유형 이름 |
| description | String | 설명 |
| enabled | Boolean | 활성화 여부 |
| sortOrder | Integer | 정렬 순서 |

---

## 6. 오류 및 예외 응답

### 6.1 Relationship 업무 오류

| HTTP | 오류 코드 | 설명 | 발생 조건 |
|---|---|---|---|
| 404 | CMDB_CI_RELATIONSHIP_001 | CI Relationship을 찾을 수 없습니다. | 관계 ID가 존재하지 않음 |
| 404 | CMDB_CI_RELATIONSHIP_002 | CI Relationship Type을 찾을 수 없습니다. | 관계 유형 ID가 존재하지 않음 |
| 409 | CMDB_CI_RELATIONSHIP_003 | 비활성화된 Relationship Type은 사용할 수 없습니다. | 비활성 유형으로 신규 생성 또는 재활성화 |
| 404 | CMDB_CI_RELATIONSHIP_004 | Configuration Item을 찾을 수 없습니다. | Source 또는 Target CI가 존재하지 않음 |
| 400 | CMDB_CI_RELATIONSHIP_005 | CI는 자기 자신과 관계를 맺을 수 없습니다. | Source와 Target이 동일 |
| 400 | CMDB_CI_RELATIONSHIP_006 | 유효하지 않은 Relationship 조회 방향입니다. | 서비스 계층에서 잘못된 방향 값 전달 |

### 6.2 공통 오류

| HTTP | 오류 코드 | 설명 |
|---|---|---|
| 400 | COMMON_001 | 잘못된 요청입니다. |
| 400 | COMMON_004 | 유효하지 않은 요청입니다. |
| 401 | AUTH_001 | 인증이 필요합니다. |
| 403 | AUTH_002 | 접근 권한이 없습니다. |
| 404 | AUTH_007 | 사용자를 찾을 수 없습니다. |
| 500 | COMMON_003 | 서버 내부 오류가 발생했습니다. |

JWT 인증 과정에서는 토큰의 상태에 따라 다음 오류 코드도 사용한다.

| HTTP | 오류 코드 | 설명 |
|---|---|---|
| 401 | AUTH_003 | 유효하지 않은 JWT 토큰 |
| 401 | AUTH_004 | 만료된 JWT 토큰 |
| 401 | AUTH_005 | 지원하지 않는 JWT 토큰 |
| 401 | AUTH_006 | 잘못된 형식의 JWT 토큰 |

### 6.3 요청 검증 오류

다음 조건을 위반하면 HTTP 400과 `COMMON_001`을 반환한다.

| 대상 | 검증 조건 |
|---|---|
| targetCiId | 필수, 양수 |
| relationshipTypeId | 필수, 양수 |
| description (생성) | 최대 1000자 |
| description (수정) | 최대 1000자 |
| reason | 필수, 공백 불가, 최대 500자 |
| direction | OUTGOING, INCOMING, ALL 중 하나 |
| status | ACTIVE, INACTIVE 중 하나 |

잘못된 숫자·enum 파라미터 형식도 공통 파라미터 검증 오류로 처리한다.

### 6.4 인증 및 인가 실패

| 상황 | HTTP | 오류 코드 |
|---|---|---|
| 인증되지 않은 요청 | 401 | AUTH_001 |
| 권한이 부족한 요청 | 403 | AUTH_002 |

인증 실패는 `JwtAuthenticationEntryPoint`, 권한 부족은 `JwtAccessDeniedHandler`에서 `ApiResponse` 형식으로 반환한다.

### 6.5 오류 응답 예시

```json
{
  "success": false,
  "code": "COMMON_001",
  "message": "잘못된 요청입니다."
}
```

---

## 7. 프론트엔드 연동 기준

### 7.1 관계 목록 화면

권장 처리 순서:

1. 기준 CI ID 확보
2. 활성 Relationship Type 조회
3. CI 관계 목록 조회
4. 방향·상태·유형 필터 적용
5. 관계 상세 화면 또는 수정 화면 연결

### 7.2 관계 생성 화면

- Source CI는 URL 경로의 `ciId`로 전달한다.
- Target CI 선택 결과는 `targetCiId`로 전달한다.
- Relationship Type 선택 결과는 `relationshipTypeId`로 전달한다.
- 비활성 Relationship Type은 신규 선택 대상에서 제외한다.
- 설명은 최대 1000자까지 입력한다.

### 7.3 관계 수정 화면

- 수정 가능한 필드는 description뿐이다.
- Source CI, Target CI, Relationship Type은 읽기 전용으로 표시한다.
- 저장 요청은 PUT을 사용한다.

### 7.4 관계 비활성화 화면

- 비활성화 사유를 필수 입력으로 처리한다.
- 최대 500자 제한을 적용한다.
- 비활성화 완료 후 서버 응답의 status를 기준으로 화면을 갱신한다.
- 비활성화 수행자 및 일시는 서버 응답을 사용한다.

### 7.5 오류 처리

프론트엔드에서는 오류 메시지 문자열보다 오류 코드를 기준으로 처리하는 것을 권장한다.

| 오류 코드 | 프론트엔드 처리 |
|---|---|
| CMDB_CI_RELATIONSHIP_001 | 관계를 찾을 수 없음 |
| CMDB_CI_RELATIONSHIP_003 | 비활성 관계 유형 사용 불가 |
| CMDB_CI_RELATIONSHIP_005 | 자기 자신과 관계 설정 불가 |
| AUTH_001 | 로그인 필요 |
| AUTH_002 | 접근 권한 부족 |
| COMMON_001 | 입력값 확인 필요 |
| COMMON_003 | 서버 오류 안내 |

---

## 8. 구현 및 테스트 기준

### 8.1 백엔드 구현 범위

- Controller: `CiRelationshipController`
- Service: `CiRelationshipService`
- Entity: `CiRelationship`, `CiRelationshipType`
- Request DTO: 생성, 수정, 비활성화
- Response DTO: 관계, 관계 유형
- Repository: 관계 및 관계 유형
- Migration: `V3__create_cmdb_relationship_tables.sql`

### 8.2 테스트 범위

서비스 테스트:

- 관계 생성
- ACTIVE 관계 재등록
- INACTIVE 관계 재활성화
- CI 및 관계 유형 검증
- 방향별 목록 조회
- 관계 설명 수정
- 인증 사용자 기준 비활성화
- Relationship Type 조회

Controller 테스트:

- 역할별 API 접근 권한
- 정상 요청 및 응답
- 목록 필터 파라미터
- 요청 필드 검증
- 인증 및 인가 실패

전체 백엔드 테스트 명령:

```powershell
.\gradlew test
```

### 8.3 연동 완료 확인 기준

- [ ] JWT 인증을 포함한 API 호출 성공
- [ ] USER / PM / ADMIN 권한별 접근 확인
- [ ] 관계 생성 및 목록 조회 확인
- [ ] OUTGOING / INCOMING / ALL 필터 확인
- [ ] 관계 상세 조회 확인
- [ ] 설명 수정 확인
- [ ] 비활성화 사유 입력 및 처리 확인
- [ ] 비활성 관계 재등록 및 재활성화 확인
- [ ] Relationship Type 활성/전체 조회 확인
- [ ] 오류 코드별 프론트엔드 처리 확인

---

## 9. 관련 문서

- [CMDB 개요](../08_CMDB/CMDB_개요.md)
- [CI 관계 모델](../08_CMDB/관계_모델.md)
- [프로젝트 API 테스트](./PROJECT_API_TEST.md)

---

## 10. 변경 이력

| 버전 | 일자 | 변경 내용 |
|---|---|---|
| 1.0 | 2026-11-13 | CMDB CI Relationship API 최초 명세 작성 |
