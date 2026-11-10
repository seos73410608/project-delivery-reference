# Evidence Version Restore 설계

- **문서 상태:** Proposed
- **버전:** 1.0
- **작성일:** 2026-11-09
- **대상 Feature:** Evidence Context Intelligence
- **구현 단계:** Phase 1 — Evidence Version Restore

---

## 1. 목적

현재 PMIS의 Evidence Version은 Soft Delete를 지원한다.

삭제된 버전은 데이터베이스에서 물리적으로 제거되지 않으며, 삭제자·삭제일시·삭제 사유가 기록된다. 또한 검증 이력과 Runtime 파일 참조도 보존된다.

그러나 현재는 삭제된 버전을 다시 활성화하는 기능이 없다.

이번 설계의 목적은 삭제된 Evidence Version을 복원할 수 있도록 하고, 복원 이후에도 기존 버전 이력과 Evidence의 대표 정보가 일관되게 유지되도록 하는 것이다.

이번 기능은 단순히 `deleted = false`로 변경하는 작업이 아니다.

다음 조건을 함께 보장해야 한다.

1. 삭제된 버전만 복원할 수 있어야 한다.
2. 복원 권한을 검증해야 한다.
3. 기존 버전 번호를 유지해야 한다.
4. 검증 이력과 Runtime 파일 참조를 보존해야 한다.
5. 복원한 버전과 현재 대표 Evidence 정보의 관계를 일관되게 유지해야 한다.
6. 복원 행위를 향후 Business Event로 추적할 수 있어야 한다.

## 2. 현재 구현 상태

### 2.1 Evidence Version 삭제 정책

현재 삭제 API는 다음과 같다.

```http
DELETE /api/evidence/{evidenceId}/versions/{versionNumber}
```

요청 본문:

```json
{
  "reason": "잘못 제출된 증적 파일"
}
```

응답은 `204 No Content`이다.

현재 삭제 권한은 다음과 같다.

| 버전 상태 | 삭제 가능 사용자 |
|---|---|
| PENDING | 제출자, PM, ADMIN |
| REJECTED | 제출자, PM, ADMIN |
| APPROVED | ADMIN |
| 그 외 상태 | 삭제 불가 |

삭제 시 다음 정보가 기록된다.

| 필드 | 설명 |
|---|---|
| deleted | 삭제 여부 |
| deletedBy | 삭제한 사용자 |
| deletedAt | 삭제 일시 |
| deletionReason | 삭제 사유 |

삭제는 Soft Delete로 처리하며 Runtime 파일을 물리적으로 삭제하지 않는다.

검증 이력과 Runtime 파일 참조도 유지한다.

### 2.2 버전 번호 정책

Evidence Version은 다음 제약을 가진다.

```text
UNIQUE (evidence_id, version_number)
```

새 버전 번호는 삭제된 버전을 포함한 전체 버전 중 최대 번호를 기준으로 생성한다.

따라서 삭제된 버전을 복원하더라도 기존 버전 번호를 그대로 사용한다.

**삭제된 버전 번호를 재사용하거나 복원 과정에서 새로운 버전을 생성하지 않는다.**

### 2.3 현재 Evidence 대표 정보

`Evidence`는 최신 활성 `EvidenceVersion`의 대표 정보를 보유한다.

현재 구현에는 다음 메서드가 있다.

```java
synchronizeLatestVersion(EvidenceVersion version)
restoreFromVersion(EvidenceVersion version)
```

두 메서드는 목적이 다르다.

| 메서드 | 목적 |
|---|---|
| synchronizeLatestVersion | 새로운 최신 버전의 내용을 Evidence에 반영 |
| restoreFromVersion | 기존 활성 버전의 내용을 Evidence 대표 정보에 복원 |

현재 `restoreFromVersion()`은 최신 버전 삭제 후 이전 활성 버전을 Evidence 대표 정보로 복원하는 데 사용된다.

이번 기능에서는 삭제된 버전을 다시 활성화하는 기능과 기존 메서드의 목적을 혼동하지 않아야 한다.

## 3. 복원 기능의 설계 방향

### 3.1 기본 원칙

복원은 삭제된 기존 버전을 다시 활성 상태로 전환하는 작업이다.

```text
삭제 전
  Evidence
    ├── Version 1 (ACTIVE)
    ├── Version 2 (DELETED)
    └── Version 3 (ACTIVE)

복원 후
  Evidence
    ├── Version 1 (ACTIVE)
    ├── Version 2 (ACTIVE)
    └── Version 3 (ACTIVE)
```

복원은 기존 데이터를 재사용한다.

다음 작업은 수행하지 않는다.

- 새로운 EvidenceVersion 생성
- 버전 번호 변경
- 기존 파일 재업로드
- Runtime 파일 복사 또는 이동
- 검증 이력 삭제 또는 재생성
- 기존 제출자 및 제출일시 변경

### 3.2 복원 대상

다음 조건을 모두 만족해야 한다.

- 해당 Evidence에 속한 버전이어야 한다.
- 현재 Soft Delete 상태여야 한다.
- 해당 사용자가 복원 권한을 가지고 있어야 한다.
- 복원 대상 Evidence가 정상적으로 존재해야 한다.

이미 활성 상태인 버전을 다시 복원하려는 요청은 허용하지 않는다.

### 3.3 복원 API

제안 API:

```http
POST /api/evidence/{evidenceId}/versions/{versionNumber}/restore
```

요청 본문:

```json
{
  "reason": "삭제된 정상 증적 파일의 복구"
}
```

복원 사유는 필수 입력으로 제안한다.

응답:

```http
204 No Content
```

기존 삭제 API와 동일하게 별도의 응답 본문 없이 처리한다.

단, 최종 API 계약은 기존 Controller 및 DTO의 예외 응답 규칙을 확인한 후 확정한다.

## 4. 복원 권한 정책

### 4.1 기본 권한

초기 구현에서는 삭제 정책과 동일한 권한 기준을 적용하는 것을 제안한다.

| 버전 상태 | 복원 가능 사용자 |
|---|---|
| PENDING | 제출자, PM, ADMIN |
| REJECTED | 제출자, PM, ADMIN |
| APPROVED | ADMIN |
| 그 외 상태 | 복원 불가 |

복원 대상 버전은 삭제 당시의 검증 상태를 유지하므로 해당 상태를 기준으로 권한을 판단한다.

### 4.2 권한 검증 원칙

복원 권한은 삭제 권한 메서드를 그대로 재사용하지 않는다.

예를 들어 다음과 같이 별도 메서드로 분리한다.

```java
validateVersionRestorePermission(
    EvidenceVersion version,
    User currentUser
);
```

삭제와 복원은 현재 같은 권한 정책을 사용하더라도 서로 다른 업무 행위다.

향후 정책이 달라질 가능성을 고려하여 권한 검증 책임을 분리한다.

### 4.3 프로젝트 접근 권한

현재 PMIS에는 역할 기반 API 접근 제어가 존재하지만, 프로젝트 구성원 기준의 일관된 프로젝트 접근 범위는 구현되어 있지 않다.

따라서 이번 복원 기능에서 프로젝트 접근 권한이 이미 완성되어 있다고 가정해서는 안 된다.

이번 단계에서는 기존 Evidence API의 인증 및 권한 정책을 따르되, 프로젝트 단위 접근 통제는 별도 보안 개선 과제로 관리한다.

## 5. Evidence 대표 정보 동기화

복원 기능에서 가장 중요한 업무 규칙이다.

Evidence는 최신 활성 EvidenceVersion의 대표 정보를 유지해야 한다.

### 5.1 복원 대상이 최신 활성 버전이 되는 경우

예시:

```text
복원 전
  Version 1 (ACTIVE)
  Version 2 (DELETED)
  Version 3 (DELETED)

Version 3 복원 후
  Version 1 (ACTIVE)
  Version 2 (DELETED)
  Version 3 (ACTIVE)
```

Version 3이 가장 높은 활성 버전이므로 Evidence 대표 정보를 Version 3으로 동기화한다.

### 5.2 복원 대상보다 높은 활성 버전이 존재하는 경우

예시:

```text
복원 전
  Version 1 (ACTIVE)
  Version 2 (DELETED)
  Version 3 (ACTIVE)

Version 2 복원 후
  Version 1 (ACTIVE)
  Version 2 (ACTIVE)
  Version 3 (ACTIVE)
```

Version 3이 여전히 최신 활성 버전이다.

따라서 Evidence 대표 정보는 변경하지 않는다.

### 5.3 동기화 판단 기준

복원 완료 후 다음 기준을 적용한다.

```text
복원 대상 버전 번호
    ==
복원 후 최신 활성 버전 번호
```

위 조건을 만족할 때만 Evidence 대표 정보를 갱신한다.

개념적인 서비스 흐름:

```java
restoreVersion(version);

EvidenceVersion latestActiveVersion =
    evidenceVersionRepository
        .findTopByEvidenceIdAndDeletedFalseOrderByVersionNumberDesc(
            evidence.getId()
        )
        .orElseThrow(...);

if (latestActiveVersion.getVersionNumber()
        .equals(version.getVersionNumber())) {

    evidence.restoreFromVersion(version);
}
```

위 코드는 설계 흐름을 설명하기 위한 예시이며 실제 구현 시 기존 프로젝트의 예외 처리와 Entity 메서드 규칙에 맞춰 조정한다.

## 6. Entity 변경 설계

### 6.1 EvidenceVersion

현재 `EvidenceVersion`에는 삭제 관련 필드가 존재한다.

```java
private boolean deleted;

private User deletedBy;

private LocalDateTime deletedAt;

private String deletionReason;
```

복원을 위해 다음과 같은 도메인 메서드를 추가하는 방향을 제안한다.

```java
public void restore() {
    if (!this.deleted) {
        throw new IllegalStateException(
            "삭제된 버전만 복원할 수 있습니다."
        );
    }

    this.deleted = false;
    this.deletedBy = null;
    this.deletedAt = null;
    this.deletionReason = null;
}
```

단, 위 예시는 최종 구현 계약이 아니다.

삭제 정보를 초기화하면 마지막 삭제 사유와 삭제자를 Entity에서 직접 확인할 수 없게 된다.

따라서 삭제 및 복원 이력은 별도의 변경 불가능한 이력으로 보존해야 한다.

### 6.2 삭제 정보와 이력의 분리

현재 삭제 필드는 활성 여부를 판단하는 상태 정보다.

```text
deleted = true
    → 현재 삭제 상태

deleted = false
    → 현재 활성 상태
```

반면 삭제와 복원은 반복될 수 있는 업무 행위다.

따라서 다음 두 개념을 구분한다.

| 구분 | 역할 |
|---|---|
| EvidenceVersion 삭제 필드 | 현재 버전의 활성/삭제 상태 |
| Business Event | 삭제·복원 행위의 발생 이력 |

삭제 이력을 단순히 `restoredBy`, `restoredAt` 필드 두 개로 추가하는 방식은 반복적인 삭제·복원 이력을 충분히 보존하지 못하므로 채택하지 않는다.

Business Event 도입은 다음 Phase에서 수행한다.

**따라서 Phase 1의 복원 기능 자체와 Phase 2의 완전한 업무 이벤트 감사 이력 구축은 완료 기준을 구분한다. Phase 2가 완료되기 전에는 복원 이력의 완전한 추적성이 확보되었다고 판단하지 않는다.**

## 7. 트랜잭션 및 동시성

### 7.1 트랜잭션 범위

복원 작업은 하나의 트랜잭션으로 처리한다.

```text
BEGIN TRANSACTION

1. Evidence 조회 및 잠금
2. EvidenceVersion 조회
3. 복원 대상 상태 확인
4. 복원 권한 검증
5. EvidenceVersion 복원
6. 최신 활성 버전 조회
7. 필요한 경우 Evidence 대표 정보 갱신
8. 변경 내용 저장

COMMIT
```

중간 단계에서 오류가 발생하면 전체 작업을 롤백한다.

### 7.2 Evidence 잠금

현재 버전 생성 및 삭제 과정에서는 부모 Evidence 행 잠금을 사용한다.

복원도 동일한 잠금 패턴을 사용한다.

이를 통해 다음 작업 간 충돌 가능성을 줄인다.

- 새 EvidenceVersion 생성
- 최신 버전 삭제
- 삭제된 버전 복원

### 7.3 동시성 고려 사항

동일 버전에 대한 동시 복원 요청은 하나만 성공해야 한다.

먼저 복원된 요청이 커밋되면 이후 요청은 해당 버전이 이미 활성 상태임을 확인하고 실패 처리한다.

복원과 새 버전 생성이 동시에 발생하는 경우에도 Evidence 잠금 및 트랜잭션을 통해 최신 활성 버전 판단이 일관되게 유지되어야 한다.

## 8. 오류 및 예외 처리

다음 상황을 명확하게 구분한다.

| 상황 | 처리 방향 |
|---|---|
| Evidence가 존재하지 않음 | 기존 Evidence 조회 예외 |
| 버전이 존재하지 않음 | 기존 Version 조회 예외 |
| 다른 Evidence 소속 버전 | 복원 불가 |
| 이미 활성 상태 | 복원 불가 |
| 복원 권한 없음 | 접근 거부 |
| 인증되지 않은 사용자 | 인증 오류 |
| 지원하지 않는 검증 상태 | 복원 불가 |
| 동시 복원 요청 | 이미 복원된 상태로 처리 |
| DB 오류 | 트랜잭션 롤백 |

실제 예외 코드와 HTTP 상태는 기존 `CommonErrorCode` 및 Evidence 예외 처리 규칙을 확인한 후 연결한다.

새로운 오류 코드를 불필요하게 중복 생성하지 않는다.

## 9. Business Event 연계

복원은 단순한 데이터 상태 변경이 아니라 업무 행위다.

따라서 향후 Business Event로 다음 내용을 기록할 수 있어야 한다.

| 항목 | 예시 |
|---|---|
| eventType | EVIDENCE_VERSION_RESTORED |
| aggregateType | EVIDENCE_VERSION |
| aggregateId | EvidenceVersion ID |
| projectId | 소속 Project ID |
| actorId | 복원 수행 사용자 ID |
| occurredAt | 실제 복원 행위 발생 시각 |
| reason | 복원 사유 |
| sourceReference | 원본 Evidence 및 Version 참조 |

이 정보는 향후 Context Builder가 삭제·복원·검증·승인 등의 행위를 연결하는 기초 자료가 된다.

`BaseEntity.createdAt`은 DB 레코드 생성 시각이며, 업무 행위가 발생한 시각인 `occurredAt`과 동일한 개념으로 취급하지 않는다.

Business Event의 실제 Entity 및 저장 구조는 Phase 2에서 별도 설계한다.

## 10. 테스트 설계

### 10.1 정상 복원 테스트

| ID | 시나리오 | 기대 결과 |
|---|---|---|
| RST-001 | 삭제된 PENDING 버전 복원 | 복원 성공 |
| RST-002 | 삭제된 REJECTED 버전 복원 | 복원 성공 |
| RST-003 | 삭제된 APPROVED 버전 ADMIN 복원 | 복원 성공 |
| RST-004 | 복원 후 버전 번호 확인 | 기존 번호 유지 |
| RST-005 | 복원 후 파일 참조 확인 | 기존 Runtime 참조 유지 |
| RST-006 | 복원 후 검증 이력 확인 | 기존 이력 보존 |

### 10.2 권한 테스트

| ID | 시나리오 | 기대 결과 |
|---|---|---|
| RST-101 | PENDING 제출자 복원 | 성공 |
| RST-102 | PENDING PM 복원 | 성공 |
| RST-103 | PENDING 일반 사용자 복원 | 권한 정책에 따라 제한 |
| RST-104 | APPROVED PM 복원 | 실패 |
| RST-105 | APPROVED ADMIN 복원 | 성공 |
| RST-106 | 미인증 사용자 복원 | 인증 오류 |

### 10.3 대표 정보 동기화 테스트

| ID | 시나리오 | 기대 결과 |
|---|---|---|
| RST-201 | 최신 버전 복원 | Evidence 대표 정보 갱신 |
| RST-202 | 과거 버전 복원 | Evidence 대표 정보 유지 |
| RST-203 | 최신 버전 삭제 후 과거 버전 복원 | 최신 활성 버전 기준으로 동기화 |
| RST-204 | 여러 버전 중 중간 버전 복원 | 더 높은 활성 버전 유지 |

### 10.4 예외 및 동시성 테스트

| ID | 시나리오 | 기대 결과 |
|---|---|---|
| RST-301 | 이미 활성인 버전 복원 | 실패 |
| RST-302 | 존재하지 않는 버전 복원 | 실패 |
| RST-303 | 다른 Evidence의 버전 번호로 요청 | 실패 |
| RST-304 | 복원 중 DB 오류 | 전체 롤백 |
| RST-305 | 동일 버전 동시 복원 | 하나만 성공 |
| RST-306 | 복원과 신규 버전 생성 동시 실행 | 최신 버전 일관성 유지 |

## 11. 구현 순서

1. `EvidenceVersion`의 현재 삭제 및 상태 변경 메서드 확인
2. 복원 API Request 및 Controller 계약 설계
3. 복원 권한 검증 메서드 구현
4. Evidence 잠금 및 복원 트랜잭션 구현
5. 최신 활성 버전 판단 및 Evidence 동기화 구현
6. 복원 성공·실패 테스트 작성
7. 기존 삭제 및 버전 생성 테스트 회귀 확인
8. API 문서 및 Feature 문서 갱신

기존 삭제 정책과 버전 생성 로직은 불필요하게 변경하지 않는다.

## 12. 완료 기준

다음 항목을 모두 만족해야 Phase 1을 완료한 것으로 판단한다.

- [ ] 삭제된 EvidenceVersion을 복원할 수 있다.
- [ ] 활성 상태의 버전은 복원할 수 없다.
- [ ] 기존 권한 정책이 복원 API에도 적용된다.
- [ ] 버전 번호가 변경되지 않는다.
- [ ] Runtime 파일 참조가 유지된다.
- [ ] 검증 이력이 유지된다.
- [ ] 최신 활성 버전 기준으로 Evidence 대표 정보가 동기화된다.
- [ ] 복원 작업은 트랜잭션으로 처리된다.
- [ ] Evidence 잠금을 통해 동시성 문제가 방지된다.
- [ ] 정상·권한·예외·동시성 테스트가 통과한다.
- [ ] 기존 Evidence 생성·삭제·검증 기능의 회귀 테스트가 통과한다.

Business Event를 통한 완전한 복원 이력 추적은 Phase 2의 완료 기준으로 별도 관리한다.

## 13. 후속 단계

Phase 1 이후에는 다음 단계로 이어진다.

```text
Phase 1
Evidence Version Restore
        |
        v
Phase 2
Business Event
        |
        v
Phase 3
Context / Knowledge
        |
        v
Phase 4
Neo4j Context Graph
        |
        v
Phase 5
Spring AI Graph Search
        |
        v
Phase 6
Integration Test & Portfolio
```

이번 단계는 Evidence Version의 변경 이력을 안전하게 복원할 수 있는 기반을 만드는 작업이다.

다음 단계에서는 복원뿐 아니라 생성·검증·삭제·승인 등의 주요 업무 행위를 Business Event로 연결한다.

---

## 최종 설계 방향

Evidence Version Restore는 기존 PMIS의 Evidence 구조를 유지하면서 복원 기능을 추가한다.

MariaDB의 기존 업무 데이터가 원본이며, 복원은 기존 데이터를 다시 활성화하는 작업이다.

복원 이력은 향후 Business Event로 확장하여 Context와 Knowledge가 업무 행위의 배경과 결과를 연결할 수 있도록 한다.

**이번 구현의 핵심은 데이터를 되살리는 것뿐 아니라, 복원 이후에도 어떤 버전이 현재 Evidence의 대표 정보인지 일관되게 유지하는 것이다.**