# PMIS–Automation Runtime OCR 연동 설계 및 테스트 결과

- 문서 목적: PMIS Frontend 개발팀에 OCR 연동 구조, API 호출 순서, 화면 구현 시 참고사항 및 현재 검증 결과를 전달한다.
- 기준일: 2026-10-23
- 상태: **핵심 E2E 흐름 검증 완료 / 예외·운영 보완 테스트 진행 필요**
- 대상 시스템:
  - PMIS Backend: `http://localhost:8080`
  - Automation Runtime: `http://127.0.0.1:8000` (Runtime Swagger: `http://127.0.0.1:8000/docs`)
  - PMIS Swagger: `http://localhost:8080/swagger-ui/index.html`

> 보안 주의: 테스트 로그에 포함된 JWT Bearer Token은 문서에 복사하지 않는다. 대화에 공유된 토큰은 유출된 것으로 간주해 폐기/재발급하고, 문서·이슈·소스 코드에는 토큰을 저장하지 않는다.

---

## 1. 요약

PMIS Backend는 Automation Runtime의 OCR API를 호출하는 기능에 더해, 기존 PMIS 증적(Evidence)에 연결된 OCR 작업 이력을 저장하고 조회하는 API를 제공한다.

현재 확인된 정상 시나리오는 다음과 같다.

1. OCR 입력 이미지 업로드
2. Runtime OCR 작업 생성
3. Runtime 작업 상태 및 결과 조회
4. PMIS Evidence에 연결된 OCR 작업 이력 생성
5. PMIS에서 Runtime 상태/결과 동기화
6. PMIS OCR 작업 상세 조회
7. Evidence별 OCR 작업 이력 목록 조회

**검증 결과:** Evidence 연계 OCR 이력 생성 → Runtime 결과 동기화 → PMIS 상세/목록 재조회까지 정상 동작했다. 다만, 타임스탬프 시간대 표현이 일치하지 않는 문제가 관찰되었고 예외 케이스 및 프런트엔드 통합 테스트는 별도로 진행해야 한다.

## 2. 구성 및 책임

### 2.1 시스템 간 책임

| 구성요소 | 책임 |
|---|---|
| PMIS Frontend | 사용자가 Evidence를 선택하고 OCR 실행·상태 갱신·결과 확인을 할 수 있는 UI 제공 |
| PMIS Backend | 인증/인가, Evidence와 OCR 이력 연결, PMIS 요청 ID 생성, Runtime 호출, 결과/상태 저장 및 조회 |
| Automation Runtime | 업로드된 이미지 관리, OCR 작업 실행, 상태 및 OCR 결과 제공 |
| DB | Evidence와 OCR 이력 및 동기화된 OCR 결과의 영속 저장 |

### 2.2 식별자 구분

식별자는 용도가 다르므로 서로 대체해서 사용하지 않는다.

| 필드 | 의미 | 생성/관리 주체 |
|---|---|---|
| `evidenceId` | PMIS Evidence 식별자 | PMIS |
| `inputFileId` | OCR 입력 이미지 업로드 후 Runtime이 반환한 파일 ID | Runtime |
| `pmisRequestId` | PMIS가 생성하는 상관관계/중복 요청 추적 ID | PMIS Backend |
| `ocrRequestId` | Runtime OCR 작업 ID | Runtime |
| `Evidence.sourceFileId` | 기존 Evidence가 참조하는 Runtime 실행 결과 파일 ID | 기존 Evidence/Runtime 연계 |

**중요:** `Evidence.sourceFileId`와 OCR 입력 파일의 `inputFileId`는 서로 다른 개념이다. OCR 입력 파일 ID를 기존 Evidence의 `sourceFileId`에 덮어쓰지 않는다.

## 3. API 계약

모든 PMIS API 호출은 PMIS 인증 정책에 맞는 `Authorization: Bearer <JWT>` 헤더가 필요하다. 실제 권한은 PMIS Security 설정에 따른다. 아래 응답은 핵심 필드 위주로 정리했으며 실제 응답에는 공통 응답 래퍼가 있거나 API별 DTO 형식이 다를 수 있다.

### 3.1 OCR 이미지 업로드

- **Method/Path:** `POST /api/runtime/ocr/uploads`
- **Content-Type:** `multipart/form-data`
- **Form field:** `file`
- **성공 응답 예시(요약):**

```json
{
  "success": true,
  "code": "SUCCESS",
  "data": {
    "status": "UPLOADED",
    "file_id": "b17667a1-7314-4133-b4e5-32a364905b02",
    "file_name": "ocr_test.png",
    "media_type": "image/png",
    "size_bytes": 7064,
    "created_at": "2026-10-22T08:11:58.014046Z"
  }
}
```

Frontend 동작:
1. 이미지 파일을 선택한다.
2. `FormData`의 `file` 필드로 업로드한다.
3. 응답의 `data.file_id`를 보관한다.
4. 이후 Evidence OCR 작업 생성 요청의 `inputFileId`로 전달한다.

`file_id`는 Runtime에서 발급한 ID이며 프런트에서 직접 생성하지 않는다.

### 3.2 Runtime OCR 작업 생성 API (직접 호출/기능 확인용)

- **Method/Path:** `POST /api/runtime/ocr/jobs`
- **Content-Type:** `application/json`
- **요청 예시:**

```json
{
  "pmis_request_id": "test-ocr-00000002",
  "file_id": "b17667a1-7314-4133-b4e5-32a364905b02"
}
```

- **응답 예시(요약):**

```json
{
  "success": true,
  "code": "SUCCESS",
  "data": {
    "status": "PENDING",
    "accepted": true,
    "ocr_request_id": "2a2b4888-ca74-42b7-a950-b8680ac0e200",
    "pmis_request_id": "test-ocr-00000002",
    "file_id": "b17667a1-7314-4133-b4e5-32a364905b02",
    "created_at": "2026-10-22T08:14:07.694913Z",
    "status_url": "/api/v1/pmis/ocr/jobs/2a2b4888-ca74-42b7-a950-b8680ac0e200",
    "result_url": "/api/v1/pmis/ocr/jobs/2a2b4888-ca74-42b7-a950-b8680ac0e200/result"
  }
}
```

이 API는 Runtime 연동 자체를 확인하기 위한 API다. Evidence와 OCR 이력을 연결하는 화면에서는 **3.4의 PMIS Evidence OCR 작업 생성 API**를 우선 사용한다. 동일 작업을 두 API로 중복 생성하지 않도록 한다.

### 3.3 Runtime OCR 상태/결과 조회 API (직접 호출/기능 확인용)

- 상태: `GET /api/runtime/ocr/jobs/{ocrRequestId}`
- 결과: `GET /api/runtime/ocr/jobs/{ocrRequestId}/result`

상태 응답 예시(요약):

```json
{
  "success": true,
  "code": "SUCCESS",
  "data": {
    "status": "SUCCEEDED",
    "error": null,
    "ocr_request_id": "ec0a54c9-2fff-418f-95ae-46df18b433bc",
    "pmis_request_id": "test-ocr-00000001",
    "file_id": "31b619b7-bb43-46c5-9e04-7d49bf48fc03",
    "created_at": "2026-10-22T03:20:15.502852Z",
    "started_at": "2026-10-22T03:20:15.640083Z",
    "finished_at": "2026-10-22T03:20:17.837319Z"
  }
}
```

결과 응답 예시(요약):

```json
{
  "success": true,
  "code": "SUCCESS",
  "data": {
    "status": "SUCCEEDED",
    "text": "OCR TEST 12345\n",
    "engine": {
      "name": "Tesseract",
      "version": "tesseract v5.5.3.20260724"
    },
    "warnings": [],
    "ocr_request_id": "ec0a54c9-2fff-418f-95ae-46df18b433bc",
    "pmis_request_id": "test-ocr-00000001",
    "file_id": "31b619b7-bb43-46c5-9e04-7d49bf48fc03",
    "created_at": "2026-10-22T03:20:17.837319Z"
  }
}
```

주의: 이 직접 조회 API 결과는 Runtime 연동 확인용이다. Frontend의 Evidence OCR 이력 화면에서는 PMIS에 저장된 최신 상태/결과를 조회하고, 필요 시 3.5의 refresh API를 호출하는 방식으로 구성한다.

### 3.4 Evidence에 연결된 OCR 작업 생성

- **Method/Path:** `POST /api/evidences/{evidenceId}/ocr-jobs`
- **Content-Type:** `application/json`
- **요청 예시:**

```json
{
  "inputFileId": "b17667a1-7314-4133-b4e5-32a364905b02"
}
```

- **성공 응답 예시:**

```json
{
  "id": 1,
  "evidenceId": 2,
  "pmisRequestId": "7a5463e2-ae14-4c5b-9576-8dc9550ffd2b",
  "ocrRequestId": "573403c4-e40c-4620-a674-21e5fb44d296",
  "inputFileId": "b17667a1-7314-4133-b4e5-32a364905b02",
  "status": "PENDING",
  "recognizedText": null,
  "engineName": null,
  "engineVersion": null,
  "warnings": null,
  "errorDetail": null,
  "requestedAt": "2026-10-23T11:44:36.645792400",
  "startedAt": null,
  "finishedAt": null
}
```

Frontend는 `evidenceId`와 업로드 응답의 `inputFileId`를 전달한다. `pmisRequestId`와 `ocrRequestId`는 백엔드에서 발급/수신하므로 프런트에서 만들지 않는다.

### 3.5 OCR 상태 갱신 및 결과 동기화

- **Method/Path:** `POST /api/evidence-ocr-jobs/{ocrJobId}/refresh`
- **Request body:** 없음
- **역할:** Runtime의 현재 상태를 확인하고, 완료 상태라면 OCR 결과를 PMIS 이력에 저장한다.
- **성공 응답 예시:**

```json
{
  "id": 1,
  "evidenceId": 2,
  "pmisRequestId": "7a5463e2-ae14-4c5b-9576-8dc9550ffd2b",
  "ocrRequestId": "573403c4-e40c-4620-a674-21e5fb44d296",
  "inputFileId": "b17667a1-7314-4133-b4e5-32a364905b02",
  "status": "SUCCEEDED",
  "recognizedText": "OCR TEST 12345\n",
  "engineName": "Tesseract",
  "engineVersion": "tesseract v5.5.3.20260724",
  "warnings": null,
  "errorDetail": null,
  "requestedAt": "2026-10-23T11:44:36.645792",
  "startedAt": "2026-10-23T02:44:40.541938",
  "finishedAt": "2026-10-23T02:44:49.195418"
}
```

Frontend 권장 UX:
- `PENDING` / `RUNNING`: 진행 중 표시, 사용자가 다시 확인할 수 있는 새로고침 버튼 제공.
- `SUCCEEDED`: 인식 텍스트와 엔진 정보를 표시.
- `FAILED`: 오류 내용을 표시하고 재시도 정책을 안내한다. 재시도는 새 OCR 작업 생성인지 기존 작업 refresh인지 구분한다.
- refresh는 네트워크 요청이므로 로딩 상태/중복 클릭 방지를 구현한다.
- 현재 API는 polling 주기/자동 재시도 정책을 이 문서에서 확정하지 않는다. 초기 UI는 수동 refresh로 구현하는 편이 안전하다.

### 3.6 OCR 작업 상세 조회

- **Method/Path:** `GET /api/evidence-ocr-jobs/{ocrJobId}`
- **성공 시:** PMIS에 저장된 OCR 작업 이력 1건을 반환한다.
- 상세 화면 진입/새로고침 후에는 이 API로 저장된 상태를 표시한다.

### 3.7 Evidence별 OCR 작업 목록 조회

- **Method/Path:** `GET /api/evidences/{evidenceId}/ocr-jobs`
- **성공 시:** 해당 Evidence에 연결된 OCR 작업 이력 배열을 반환한다.
- 화면에서는 요청 시각, 상태, 엔진, 인식 텍스트 요약 등을 목록에 활용할 수 있다.
- 정렬은 백엔드 메서드 기준 최신 요청 우선(`requestedAt` 내림차순)이다.

## 4. 권장 Frontend 사용자 흐름

1. Evidence 상세 화면을 연다.
2. 해당 Evidence에 대한 OCR 실행 영역을 표시한다.
3. 사용자가 PNG/JPEG 등 지원되는 이미지를 선택한다. 지원 형식/용량 제한은 Runtime 계약을 확인하고 프런트에서 임의로 단정하지 않는다.
4. `POST /api/runtime/ocr/uploads`로 업로드한다.
5. 업로드 성공 후 `data.file_id`를 임시 보관한다.
6. `POST /api/evidences/{evidenceId}/ocr-jobs`로 `{ "inputFileId": "<file_id>" }`를 전달한다.
7. 생성 응답의 `id`를 OCR 이력 ID로 사용하고 `status`를 표시한다.
8. `PENDING`/`RUNNING`이면 refresh 버튼을 통해 `POST /api/evidence-ocr-jobs/{ocrJobId}/refresh`를 호출한다.
9. refresh 응답의 상태가 `SUCCEEDED`면 `recognizedText`, `engineName`, `engineVersion`을 표시한다.
10. 페이지 재진입 또는 재조회 시 `GET /api/evidences/{evidenceId}/ocr-jobs`로 이력을 복원한다.
11. 특정 이력 상세가 필요하면 `GET /api/evidence-ocr-jobs/{ocrJobId}`를 호출한다.

업로드만 성공하고 OCR 작업 생성이 실패한 경우에는 업로드된 파일이 Runtime에 남을 수 있다. 현재 제공된 테스트 결과만으로는 해당 파일의 정리 정책이 확인되지 않으므로 UI 오류를 명확히 표시하고, 파일 정리 여부는 Backend/Runtime 팀과 별도 협의한다.

## 5. 테스트 결과

### 5.1 Runtime API 직접 연동 테스트

| TC | 시나리오 | 결과 | 확인 내용 |
|---|---|---|---|
| RT-01 | OCR 이미지 업로드 | PASS | `ocr_test.png` 업로드, `file_id` 반환, 크기 7,064 bytes |
| RT-02 | Runtime OCR 작업 생성 | PASS | `test-ocr-00000002` 요청이 접수되고 `PENDING`, `accepted=true` 반환 |
| RT-03 | Runtime 작업 상태 조회 | PASS | 기존 작업 `ec0a54c9-2fff-418f-95ae-46df18b433bc`가 `SUCCEEDED` |
| RT-04 | Runtime OCR 결과 조회 | PASS | 텍스트 `OCR TEST 12345\n`, Tesseract 엔진/버전 반환 |

참고: RT-02에서 생성한 작업 ID와 RT-03/RT-04에서 조회한 작업 ID는 다르다. RT-03/RT-04는 기존 작업(`test-ocr-00000001`) 결과를 확인한 것이므로 동일 요청의 생성-조회 연속 테스트로 해석하지 않는다.

### 5.2 PMIS Evidence OCR 이력 E2E 테스트

| TC | 시나리오 | 결과 | 확인 내용 |
|---|---|---|---|
| PMIS-OCR-01 | Evidence ID 2에 OCR 작업 생성 | PASS | 이력 ID `1`, 상태 `PENDING`, Runtime `ocrRequestId` 연결 |
| PMIS-OCR-02 | OCR 작업 refresh 및 결과 동기화 | PASS | 상태 `SUCCEEDED`, 인식 텍스트 및 엔진 정보 저장 |
| PMIS-OCR-03 | OCR 작업 상세 조회 | PASS | `GET /api/evidence-ocr-jobs/1`에서 저장된 결과 재조회 |
| PMIS-OCR-04 | Evidence별 OCR 이력 목록 조회 | PASS | `GET /api/evidences/2/ocr-jobs`에서 동일 이력 반환 |

확인된 결과 데이터:
- 인식 텍스트: `OCR TEST 12345\n`
- 엔진: `Tesseract`
- 버전: `tesseract v5.5.3.20260724`
- 최종 상태: `SUCCEEDED`

### 5.3 아직 수행이 필요한 테스트

| TC | 시나리오 | 기대 결과/확인 사항 |
|---|---|---|
| NEG-01 | 존재하지 않는 `evidenceId`로 OCR 작업 생성 | 정의된 4xx 오류 코드/응답 확인 |
| NEG-02 | `inputFileId` 누락 또는 공백 | Validation 오류(일반적으로 400) 확인 |
| NEG-03 | 존재하지 않는 `ocrJobId` 상세 조회 | 일관된 404/비즈니스 오류 응답 확인. 현재 서비스 예외 매핑 확인 필요 |
| NEG-04 | Runtime 중단/접속 불가 상태에서 refresh | 오류 응답, 저장 상태 및 재시도 가능성 확인 |
| NEG-05 | 이미 `SUCCEEDED`인 작업 refresh | Runtime 재호출 여부와 기존 결과 반환 동작 확인 |
| NEG-06 | 이미 `FAILED`인 작업 refresh | terminal 상태 처리 및 재실행 정책 확인 |
| NEG-07 | 업로드 성공 후 OCR 작업 생성 실패 | 사용자 메시지 및 업로드 파일 정리 정책 확인 |
| NEG-08 | 비인증/권한 부족으로 호출 | 401/403 및 Frontend 오류 UX 확인 |

---

## 6. 확인된 이슈 및 권고

### 6.1 타임스탬프 시간대 불일치 — 수정/정책 확정 필요

실제 PMIS 응답에서:
- `requestedAt`: `2026-10-23T11:44:36...`
- `startedAt`: `2026-10-23T02:44:40...`
- `finishedAt`: `2026-10-23T02:44:49...`

Runtime 응답의 시간은 `Z`(UTC) 기준이며, PMIS가 `OffsetDateTime`에서 `toLocalDateTime()`을 호출해 오프셋을 버리면 시간대 정보가 사라진다. 현재 결과는 동일 시간대 표현으로 볼 수 없다. **Backend에서 저장·반환 시간 정책(UTC 또는 Asia/Seoul)을 통일하고 변환 로직을 검증해야 한다.** Frontend에서 임의로 9시간을 더하거나 빼서 보정하지 않는다.

### 6.2 오류 응답 계약 확인 필요

현재 제공된 정상 응답만으로는 다음 사항을 확정할 수 없다.
- 존재하지 않는 OCR 이력 ID의 최종 HTTP 상태/오류 코드
- Runtime 장애 시 생성 API가 어떤 HTTP 상태를 반환하는지
- OCR 실패 시 기존 이력을 refresh하는 정책과 새 작업 생성 정책

Frontend는 우선 공통 오류 응답을 처리하되, 위 예외 테스트 후 확정된 API 계약에 맞춰 분기한다.

### 6.3 직접 Runtime API와 Evidence OCR API의 사용 구분

- 직접 Runtime API(`/api/runtime/ocr/jobs`, `/api/runtime/ocr/jobs/{id}` 등): Runtime 기능 확인/저수준 연동.
- Evidence OCR API(`/api/evidences/{evidenceId}/ocr-jobs` 등): PMIS 업무 화면에서 이력과 Evidence 관계를 관리하기 위한 API.

Evidence 상세 화면에서는 이력 추적을 위해 Evidence OCR API를 주 경로로 사용한다. 같은 이미지에 대해 두 생성 API를 모두 호출하면 별도 OCR 작업이 생성될 수 있으므로 중복 호출하지 않는다.

## 7. Frontend 구현 완료 기준

- [ ] Evidence 상세 화면에서 OCR 입력 이미지를 선택하고 업로드할 수 있다.
- [ ] 업로드된 `file_id`로 Evidence OCR 작업을 생성한다.
- [ ] `PENDING`, `RUNNING`, `SUCCEEDED`, `FAILED` 상태를 구분해 표시한다.
- [ ] refresh 중 로딩 표시 및 중복 클릭 방지를 구현한다.
- [ ] 성공 결과에 인식 텍스트, 엔진명, 엔진 버전을 표시한다.
- [ ] Evidence별 OCR 이력 목록을 조회하고 상세 이력을 다시 열 수 있다.
- [ ] 인증 만료/권한 오류/Runtime 오류를 사용자에게 알기 쉽게 표시한다.
- [ ] 토큰, 민감한 응답 헤더, 불필요한 이미지 내용을 콘솔 로그에 남기지 않는다.
- [ ] 시간대 정책이 확정되기 전에는 날짜/시간을 임의 보정하지 않는다.
- [ ] Backend와 합의된 오류 응답 계약에 따라 예외 화면을 처리한다.

## 8. Frontend 개발팀에 전달할 핵심 메시지

1. **정상 경로는 Backend Swagger에서 E2E 검증되었다.** 업로드 → Evidence OCR 작업 생성 → refresh → 상세/목록 조회가 동작한다.
2. Evidence 상세 화면의 주 API는 `POST /api/evidences/{evidenceId}/ocr-jobs`, `POST /api/evidence-ocr-jobs/{ocrJobId}/refresh`, `GET /api/evidence-ocr-jobs/{ocrJobId}`, `GET /api/evidences/{evidenceId}/ocr-jobs`다.
3. 업로드 API는 기존 `/api/runtime/ocr/uploads`를 재사용한다.
4. 프런트에서 `pmisRequestId`/`ocrRequestId`를 만들지 않는다. 업로드 결과의 `file_id`만 `inputFileId`로 전달한다.
5. 현재 주요 후속 확인 사항은 **시간대 처리**, **오류 응답**, **예외/권한 테스트**다. 정상 경로 개발을 막을 필요는 없고, 시간 표시와 오류 분기는 계약 확정 후 마무리한다.

---

## 부록 A. Swagger 수동 테스트 순서

1. PMIS Swagger에서 인증 후 `/api/runtime/ocr/uploads`에 `ocr_test.png` 업로드.
2. 응답의 `data.file_id`를 복사한다.
3. 기존 Evidence ID를 확인하고 `/api/evidences/{evidenceId}/ocr-jobs`에 `inputFileId`를 넣어 호출한다.
4. 응답의 `id`를 기록한다.
5. `POST /api/evidence-ocr-jobs/{id}/refresh`를 호출한다. 아직 `PENDING`/`RUNNING`이면 잠시 후 다시 호출한다.
6. `GET /api/evidence-ocr-jobs/{id}`에서 결과가 저장되었는지 확인한다.
7. `GET /api/evidences/{evidenceId}/ocr-jobs`에서 해당 이력이 목록에 포함되는지 확인한다.
8. 예외 테스트는 별도 테스트 데이터/환경에서 실행한다.

## 부록 B. 실제 테스트 증적에서 보안상 제외한 항목

- JWT Bearer Token
- 인증 헤더 전체
- 테스트 실행에 불필요한 비밀 설정값

문서에는 재사용 가능한 API 경로, 요청 구조, 결과 필드 및 테스트 상태만 남긴다.
