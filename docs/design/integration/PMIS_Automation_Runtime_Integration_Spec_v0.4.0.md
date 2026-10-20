# PMIS–Automation Runtime 연동 구현 규격서

- **문서 버전:** v0.4.0
- **문서 상태:** PMIS 구현 기준 제안 / Runtime 확인 및 구현 요청
- **작성 주체:** PMIS 팀
- **목적:** 서비스 인증, API 규격, 실행 식별자 연결 및 중복 실행 방지 기준을 정하고 연동 개발을 진행한다.

> 본 문서는 PMIS 측 제안 규격이다. Runtime 기존 구현과 다른 부분은 차이점만 공유하고, 기존 구현으로 요구사항을 충족할 수 있으면 재사용한다. 아래 예시 경로와 JSON은 제안이며, Runtime에서 확인되지 않은 API가 이미 구현되었다는 의미는 아니다.

## 1. 구현 기준

1. Frontend는 Runtime을 직접 호출하지 않는다. PMIS Backend가 사용자·업무 권한을 검증한 후 Runtime을 호출한다.
2. 서비스 인증은 API Key를 기본 제안으로 한다. Runtime에 기존 인증 체계가 있으면 먼저 확인하고 차이점만 협의한다.
3. PMIS 요청 ID와 Runtime 실행 ID를 연결해 요청을 추적한다.
4. 동일 PMIS 요청 ID가 재전송되면 기존 실행 정보를 반환하고 중복 실행하지 않는다.
5. 기존 업로드·실행·상태 조회 흐름을 우선 재사용하고, 부족한 API만 구현한다.

## 2. 서비스 인증

### 요청 헤더 제안

```http
X-API-Key: <service-api-key>
Content-Type: application/json
```

- 키는 PMIS Backend의 서버 측 설정/비밀 저장소에 보관한다.
- Frontend, URL query string, 응답 본문 및 로그에 키를 노출하지 않는다.
- 개발·검증·운영 환경별 키를 분리한다.
- `401 Unauthorized`는 인증 실패, `403 Forbidden`은 권한 부족을 나타내는 것을 제안한다.
- 인증 적용 위치(Runtime 앱 또는 기존 Gateway/프록시)는 현재 배포 구성을 확인해 확정한다.

## 3. API 규격 제안

경로와 필드는 Runtime의 기존 구현 확인 후 최종 확정한다. 아래 경로가 이미 구현되어 있다고 가정하지 않는다.

### 3.1 입력 파일 업로드

**제안:** `POST /api/v1/uploads`

요청 형식: `multipart/form-data`

필드:
- `file`: 입력 파일
- `pmis_request_id`: PMIS 요청 추적 ID
- `work_type`: PMIS 업무 유형 코드

응답 예시 (`202 Accepted`):

```json
{
  "upload_id": "upl_01J9EXAMPLE",
  "pmis_request_id": "pmis-req-20261020-000123",
  "file_name": "repair_pending.xlsx",
  "status": "UPLOADED"
}
```

Runtime은 업로드 성공 여부와 실행 요청에서 참조할 식별자를 반환한다. 파일 크기·확장자 제한 및 보존 정책은 기존 설정을 확인해 확정한다. `upload_id`가 기존 구현에 없다면 기존 식별자를 재사용할 수 있다.

### 3.2 Job 실행 요청

**제안:** `POST /api/v1/executions`

요청 예시:

```json
{
  "pmis_request_id": "pmis-req-20261020-000123",
  "job_id": "repair_pending",
  "input_file_ids": ["upl_01J9EXAMPLE"],
  "parameters": {}
}
```

수락 응답 예시 (`202 Accepted`):

```json
{
  "pmis_request_id": "pmis-req-20261020-000123",
  "job_id": "repair_pending",
  "execution_uuid": "a39aad5c-db14-4843-a043-ad75ca0d8024",
  "status": "PENDING",
  "accepted": true
}
```

요구사항:
- `pmis_request_id`는 PMIS가 생성하고 Runtime은 이를 실행과 연결한다.
- `execution_uuid`는 Runtime 실행 식별자다.
- `202 Accepted`는 실행 요청 수락이며 실행 완료가 아니다.
- 동일 `pmis_request_id`와 동일 요청 내용이 재전송되면 기존 실행 정보를 반환하고 새 실행을 생성하지 않는다.
- 동일 ID에 다른 Job 또는 입력 파일이 전달되면 `409 Conflict` 반환을 제안한다.

### 3.3 실행 상태 및 오류 조회

**제안:** `GET /api/v1/executions/{execution_uuid}`

응답 예시:

```json
{
  "pmis_request_id": "pmis-req-20261020-000123",
  "job_id": "repair_pending",
  "execution_uuid": "a39aad5c-db14-4843-a043-ad75ca0d8024",
  "status": "RUNNING",
  "started_at": "2026-10-20T03:10:00Z",
  "finished_at": null,
  "error": null
}
```

실패 응답 데이터 예시:

```json
{
  "pmis_request_id": "pmis-req-20261020-000123",
  "job_id": "repair_pending",
  "execution_uuid": "a39aad5c-db14-4843-a043-ad75ca0d8024",
  "status": "FAILED",
  "started_at": "2026-10-20T03:10:00Z",
  "finished_at": "2026-10-20T03:10:12Z",
  "error": {
    "code": "JOB_EXECUTION_FAILED",
    "message": "Job execution failed"
  }
}
```

`PENDING`, `RUNNING`, `SUCCEEDED`, `FAILED`는 제안 상태값이다. Runtime 기존 상태 모델을 확인한 뒤 매핑을 확정한다. 내부 경로, 비밀정보, 스택 트레이스 등 민감한 정보를 응답에 포함하지 않는다.

### 3.4 결과 파일 목록 조회 — 기존 API

`GET /api/v1/executions/{execution_uuid}/files`

Runtime 회신에서 구현 및 로컬 기본 검증 완료가 확인된 API다. PMIS 통합 테스트를 수행한다.

```json
[
  {
    "file_id": "0e240c6d-1ab5-44fd-ab87-6178eb996c52",
    "job_id": "repair_pending",
    "execution_uuid": "a39aad5c-db14-4843-a043-ad75ca0d8024",
    "vendor_name": "KIDO",
    "file_type": "xlsx",
    "created_at": "2026-10-20T03:10:15Z"
  }
]
```

### 3.5 파일 메타데이터 조회 — 기존 API

`GET /api/v1/files/{file_id}`

```json
{
  "file_id": "0e240c6d-1ab5-44fd-ab87-6178eb996c52",
  "job_id": "repair_pending",
  "execution_uuid": "a39aad5c-db14-4843-a043-ad75ca0d8024",
  "vendor_name": "KIDO",
  "file_type": "xlsx",
  "created_at": "2026-10-20T03:10:15Z"
}
```

### 3.6 결과 파일 다운로드 — 기존 API

`GET /api/v1/files/{file_id}/content`

- 성공 시 파일 바이너리 반환
- `Content-Type` 및 `Content-Disposition` 헤더 제공
- 현재 Runtime 회신 기준 `application/octet-stream`
- PMIS Backend는 파일 접근 권한 확인 후 다운로드를 중계

## 4. 식별자 및 중복 실행 처리

| 식별자 | 생성 주체 | 용도 |
|---|---|---|
| `pmis_request_id` | PMIS | 요청 추적 및 중복 방지 키 |
| `execution_uuid` | Runtime | 실행 건 식별 |
| `job_id` | Runtime 설정/계약 | 실행 Job 식별 |
| `upload_id` 또는 기존 식별자 | Runtime | 입력 파일을 실행 요청에 연결 |
| `file_id` | Runtime | 결과 파일 조회·다운로드 |

규칙:
1. PMIS는 논리적 실행 요청마다 고유한 `pmis_request_id`를 생성한다.
2. Runtime은 요청 ID와 실행 요청의 연결 정보를 보존한다.
3. 동일 ID와 동일 요청 내용이 재전송되면 기존 `execution_uuid` 및 상태를 반환한다.
4. 동일 ID에 다른 Job 또는 입력 파일이 연결되면 `409 Conflict`를 반환하는 방안을 제안한다.
5. PMIS가 응답을 받지 못한 경우 새 ID로 즉시 재요청하지 않는다. 먼저 기존 요청의 수락 여부를 확인한다.
6. 중복 방지 기록의 보존 기간과 동시 요청 처리 방식은 Runtime 저장 구조를 확인해 확정한다.

## 5. 공통 오류 응답 제안

```json
{
  "success": false,
  "code": "INVALID_REQUEST",
  "message": "Request validation failed",
  "data": null,
  "request_id": "pmis-req-20261020-000123"
}
```

| 상황 | HTTP 상태 제안 | 처리 |
|---|---:|---|
| 요청 형식/필수값 오류 | `400 Bad Request` | 입력 수정 |
| 서비스 인증 실패 | `401 Unauthorized` | 인증정보 확인 |
| 권한 부족 | `403 Forbidden` | 접근 차단 |
| 실행·파일 식별자 없음 | `404 Not Found` | 식별자 확인 |
| 동일 요청 ID의 내용 불일치 | `409 Conflict` | 요청 ID/내용 확인 |
| 파일 크기 제한 초과 | `413 Payload Too Large` | 파일 정책 확인 |
| 일시적 Runtime 장애 | `5xx` | 합의된 재시도 정책 적용 |

Runtime에 기존 공통 오류 규격이 있으면 이를 우선 확인하고 최종 확정한다. 외부 응답에는 안전한 오류 정보만 포함한다.

## 6. 완료 및 검증 기준

- [ ] 기존 업로드·실행·상태 조회 기능과 API 존재 여부 확인
- [ ] API 경로, 요청·응답 필드 및 상태값 확정
- [ ] 서비스 인증 방식과 적용 위치 확정
- [ ] PMIS 요청 ID와 Runtime 실행 ID 연결
- [ ] 동일 요청 재전송 시 중복 실행 방지
- [ ] PMIS Backend에서 업로드·실행·상태 조회 연동 성공
- [ ] 결과 파일 목록·메타데이터·다운로드 통합 테스트 성공
- [ ] 인증 실패, 권한 부족, 미등록 ID, 실행 실패 테스트
- [ ] 타 사용자/테넌트 파일 접근 차단 확인

## 7. Runtime 팀 회신 요청

1. 업로드·실행·상태 조회의 기존 API 및 실행 흐름
2. 본 제안 규격과 기존 구현의 차이
3. 재사용할 수 있는 기존 식별자 및 중복 실행 방지 방식
4. 서비스 인증 적용 위치 및 기존 인증 체계
5. 구현이 필요한 항목과 예상 완료 시점

기존 구현으로 요구사항을 충족할 수 있는 부분은 재사용하고, 차이가 있는 부분만 협의하여 구현한다. 본 문서는 연동 개발을 위한 PMIS 측 제안 규격이며, 실제 구현과 통합 테스트 결과를 기준으로 최종 확정한다.
