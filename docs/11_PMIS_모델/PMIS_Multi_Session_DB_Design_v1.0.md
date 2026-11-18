# PMIS 멀티 세션 DB 설계서 v1.0

-   **문서 상태:** 설계 결정 반영
-   **작성 기준일:** 2026-11-18
-   **대상 시스템:** PMIS Backend
-   **DBMS 기준:** PostgreSQL
-   **범위:** 멀티 세션 로그인 관리 및 관련 설정·이력 데이터 모델
-   **구현 상태:** 설계 문서 בלבד. Migration 및 애플리케이션 코드는 아직
    변경하지 않음.

------------------------------------------------------------------------

## 1. 목적 및 설계 범위

본 문서는 PMIS의 사용자별 다중 로그인 세션 관리 기능을 구현하기 위한
데이터베이스 설계를 정의한다.

이번 범위는 다음 사항을 포함한다.

1.  사용자별 최대 동시 로그인 세션 수 관리
2.  세션별 Refresh Token 관리
3.  세션 종료 및 종료 이력 보존
4.  세션 이력 보존 기간의 관리자 설정
5.  만료 세션 및 보존 기간이 지난 이력의 정리 정책
6.  기존 `refresh_tokens` 구조에서 신규 세션 구조로의 전환 방향

전체 PMIS 감사 시스템과 데이터 분리보관 기능은 이번 구현 범위에 포함하지
않는다. 다만 향후 공통 설정 및 감사·데이터 생명주기 기능과 연결할 수
있도록 확장 가능성을 고려한다.

## 2. 확정된 설계 결정

  항목                         확정 내용
  ---------------------------- ----------------------------------------------
  기본 최대 동시 세션 수       사용자당 5개
  최대 세션 수 설정 위치       `user_security_settings`
  Refresh Token 관리           `user_sessions`에 통합
  Refresh Token 저장 방식      원문이 아닌 해시 저장
  Refresh Token 해시 컬럼      `NOT NULL`
  세션 이력 사용자 관계        `users.id` FK 유지
  만료 세션 정리 주기          5분
  종료 세션 이력               `user_session_histories`에 보존
  이력 보존 기간               관리자 설정
  이력 보존 기간 초기 제안값   365일
  보존 기간 설정 위치          공통 `system_settings`
  분리보관                     별도 공통 데이터 생명주기 기능으로 향후 확장

### 2.1 로그인 제한 정책

-   사용자별 최대 동시 세션 수는 관리자가 설정한다.
-   신규 로그인으로 최대 세션 수를 초과하는 경우 기존 세션을 자동
    종료하지 않는다.
-   신규 로그인을 일괄 거부하는 대신 기존 활성 세션 목록을 사용자에게
    제시한다.
-   사용자가 종료할 세션을 직접 선택한다.
-   선택한 세션을 종료한 뒤 신규 세션을 생성한다.
-   로그인 요청이 동시에 발생하더라도 최대 세션 수를 초과하지 않도록
    사용자 단위 잠금 및 트랜잭션으로 제어한다.

## 3. 전체 데이터 모델

이번 설계 대상 테이블은 다음 5개다.

  테이블                        역할
  ----------------------------- ----------------------------------------
  `user_security_settings`      사용자별 최대 동시 세션 수
  `user_sessions`               현재 로그인 세션 및 Refresh Token 상태
  `user_session_histories`      종료된 로그인 세션 이력
  `system_setting_categories`   공통 시스템 설정 분류
  `system_settings`             공통 시스템 설정값

기존 `users` 및 `BaseEntity` 구조는 유지하는 것을 전제로 한다.

### 3.1 관계 요약

-   `users` 1 : 0..1 `user_security_settings`
-   `users` 1 : N `user_sessions`
-   `users` 1 : N `user_session_histories`
-   `system_setting_categories` 1 : N `system_settings`

### 3.2 ERD

``` mermaid
erDiagram
    USERS ||--o| USER_SECURITY_SETTINGS : has
    USERS ||--o{ USER_SESSIONS : owns
    USERS ||--o{ USER_SESSION_HISTORIES : generates
    SYSTEM_SETTING_CATEGORIES ||--o{ SYSTEM_SETTINGS : classifies

    USERS {
        bigint id PK
    }

    USER_SECURITY_SETTINGS {
        bigint id PK
        bigint user_id FK
        integer max_session_count
        timestamp created_at
        timestamp updated_at
    }

    USER_SESSIONS {
        bigint id PK
        uuid session_id UK
        bigint user_id FK
        varchar status
        varchar refresh_token_hash
        timestamp refresh_token_expires_at
        timestamp created_at
        timestamp updated_at
        timestamp last_accessed_at
    }

    USER_SESSION_HISTORIES {
        bigint id PK
        uuid session_id
        bigint user_id FK
        timestamp created_at
        timestamp last_accessed_at
        timestamp ended_at
        varchar end_reason
    }

    SYSTEM_SETTING_CATEGORIES {
        bigint id PK
        varchar category_key UK
        varchar category_name
    }

    SYSTEM_SETTINGS {
        bigint id PK
        bigint category_id FK
        varchar setting_key UK
        varchar setting_value
        varchar data_type
        varchar description
        boolean editable
        timestamp created_at
        timestamp updated_at
    }
```

## 4. 테이블 상세 명세

### 4.1 `user_security_settings`

사용자별 동시 로그인 허용 개수를 관리한다.

  ----------------------------------------------------------------------------------
  컬럼                  PostgreSQL                  필수 제약조건      설명
                        타입                                           
  --------------------- -------------- ----------------- ------------- -------------
  `id`                  BIGINT                         Y PK            내부 식별자

  `user_id`             BIGINT                         Y FK, UNIQUE    `users.id`
                                                                       참조

  `max_session_count`   INTEGER                        Y CHECK         최대 동시
                                                         (`>= 1`)      세션 수

  `created_at`          TIMESTAMP(6)                   Y               생성 일시

  `updated_at`          TIMESTAMP(6)                   N               수정 일시
  ----------------------------------------------------------------------------------

**설계 규칙**

-   사용자 한 명당 설정 레코드는 최대 한 건이다.
-   `max_session_count`는 1 이상의 정수만 허용한다.
-   신규 사용자 생성 시 기본 설정을 생성한다.
-   기본 최대 동시 세션 수는 5개다.
-   관리자가 사용자별 값을 변경할 수 있도록 한다.
-   기존 사용자는 Migration 시 기본값 5로 초기화하는 방향이다.

초기 데이터 적재 개념 예시:

``` sql
INSERT INTO user_security_settings (
    user_id,
    max_session_count,
    created_at
)
SELECT
    id,
    5,
    CURRENT_TIMESTAMP
FROM users;
```

실제 Migration에서는 프로젝트의 ID 생성 전략과 기존 테이블 정의에 맞춰
`id` 생성 방식을 반영해야 한다.

### 4.2 `user_sessions`

현재 유효한 로그인 세션을 관리하는 핵심 테이블이다. Refresh Token 상태도
세션별로 이 테이블에서 관리한다.

  -----------------------------------------------------------------------------------------
  컬럼                         PostgreSQL                  필수 제약조건      설명
                               타입                                           
  ---------------------------- -------------- ----------------- ------------- -------------
  `id`                         BIGINT                         Y PK            내부 식별자

  `session_id`                 UUID                           Y UNIQUE        외부 세션
                                                                              식별자

  `user_id`                    BIGINT                         Y FK            `users.id`
                                                                              참조

  `status`                     VARCHAR(20)                    Y               현재 세션
                                                                              상태

  `refresh_token_hash`         VARCHAR(255)                   Y NOT NULL      현재 Refresh
                                                                              Token의 해시

  `refresh_token_expires_at`   TIMESTAMP(6)                   Y               Refresh Token
                                                                              만료 일시

  `created_at`                 TIMESTAMP(6)                   Y               로그인 세션
                                                                              생성 일시

  `updated_at`                 TIMESTAMP(6)                   N               마지막 토큰
                                                                              갱신 등 수정
                                                                              일시

  `last_accessed_at`           TIMESTAMP(6)                   N               마지막 인증
                                                                              요청 일시
  -----------------------------------------------------------------------------------------

**인덱스 제안**

-   UNIQUE (`session_id`)
-   INDEX (`user_id`, `status`)
-   INDEX (`refresh_token_expires_at`)

**설계 규칙**

-   사용자 한 명이 여러 세션을 가질 수 있다.
-   각 세션은 고유한 `session_id`를 가진다.
-   Access Token과 Refresh Token 모두 JWT Claim에 `sessionId`를
    포함한다.
-   Refresh Token 원문은 DB에 저장하지 않는다.
-   `refresh_token_hash`는 필수이며 활성 세션 생성 시 반드시 값이
    존재해야 한다.
-   Refresh Token 회전 시 해시와 만료 일시를 갱신한다.
-   세션 종료 시 종료 이력을 저장한 뒤 현재 세션 레코드를 삭제한다.
-   인증 요청에서는 JWT의 `sessionId`에 해당하는 세션이 존재하고
    유효한지 확인한다.

**상태 값**

현재 세션 테이블에는 원칙적으로 `ACTIVE` 상태의 세션만 둔다. 종료된
세션은 `user_session_histories`로 이동한 뒤 현재 세션 테이블에서
제거한다.

### 4.3 `user_session_histories`

종료된 세션의 이력을 보존한다.

  컬럼                 PostgreSQL 타입     필수 제약조건   설명
  -------------------- ----------------- ------ ---------- ------------------
  `id`                 BIGINT                 Y PK         이력 식별자
  `session_id`         UUID                   Y            원본 세션 식별자
  `user_id`            BIGINT                 Y FK         `users.id` 참조
  `created_at`         TIMESTAMP(6)           Y            로그인 일시
  `last_accessed_at`   TIMESTAMP(6)           N            마지막 접근 일시
  `ended_at`           TIMESTAMP(6)           Y            세션 종료 일시
  `end_reason`         VARCHAR(30)            Y            종료 사유

**인덱스 제안**

-   INDEX (`user_id`, `ended_at`)
-   INDEX (`session_id`)

**종료 사유 코드**

  코드                 설명
  -------------------- --------------------------------------
  `USER_LOGOUT`        사용자가 직접 로그아웃
  `USER_SELECTED`      사용자가 다른 세션을 종료하도록 선택
  `ADMIN_REVOKED`      관리자가 세션 종료
  `TOKEN_EXPIRED`      Refresh Token 만료
  `SECURITY_REVOKED`   보안상 강제 종료

**사용자 FK 정책**

-   `user_id`는 `users.id` FK를 유지한다.
-   사용자 삭제 시 이력 보존과 FK가 충돌하지 않도록 사용자 비활성화 또는
    삭제 제한 정책을 별도로 적용해야 한다.
-   사용자 레코드를 물리 삭제하는 경우 이력 보존 정책을 우회하는 연쇄
    삭제(CASCADE)는 사용하지 않는 방향을 권장한다.

### 4.4 `system_setting_categories`

공통 시스템 설정을 영역별로 분류한다.

  컬럼              PostgreSQL 타입     필수 제약조건   설명
  ----------------- ----------------- ------ ---------- -------------
  `id`              BIGINT                 Y PK         분류 식별자
  `category_key`    VARCHAR(50)            Y UNIQUE     분류 코드
  `category_name`   VARCHAR(100)           Y            분류명
  `description`     VARCHAR(500)           N            설명

초기 분류 코드 제안:

-   `SECURITY`
-   `DATA_RETENTION`
-   `DATA_ARCHIVE`
-   `SYSTEM_OPERATION`

초기 구현에서 실제 사용하는 분류만 등록해도 된다. 나머지는 해당 기능을
구현할 때 추가한다.

### 4.5 `system_settings`

PMIS 공통 시스템 설정값을 관리한다.

  컬럼              PostgreSQL 타입     필수 제약조건   설명
  ----------------- ----------------- ------ ---------- -----------------------
  `id`              BIGINT                 Y PK         설정 식별자
  `category_id`     BIGINT                 Y FK         설정 분류
  `setting_key`     VARCHAR(100)           Y UNIQUE     설정 코드
  `setting_value`   VARCHAR(1000)          Y            설정값
  `data_type`       VARCHAR(20)            Y            값의 자료형
  `description`     VARCHAR(500)           N            설정 설명
  `editable`        BOOLEAN                Y            관리자 변경 가능 여부
  `created_at`      TIMESTAMP(6)           Y            생성 일시
  `updated_at`      TIMESTAMP(6)           N            수정 일시

**초기 설정 데이터**

  ----------------------------------------------------------------------------------------------
  분류          설정 코드                                     초기값 자료형        설명
  ------------- ---------------------------------- ----------------- ------------- -------------
  `SECURITY`    `SESSION_HISTORY_RETENTION_DAYS`               `365` `NUMBER`      세션 이력
                                                                                   보존 일수

  ----------------------------------------------------------------------------------------------

-   초기값 365일은 설계 초기값이다.
-   관리자가 변경할 수 있다.
-   설정 변경은 정책값을 변경하는 행위이며, 실제 데이터 삭제는 별도 정리
    작업이 수행한다.
-   설정값은 애플리케이션에서 자료형과 허용 범위를 검증해야 한다.

## 5. 세션 처리 정책

### 5.1 로그인 및 세션 생성

1.  사용자명과 비밀번호를 검증한다.
2.  사용자 레코드를 잠금 대상으로 조회한다.
3.  사용자별 `max_session_count`를 조회한다.
4.  유효한 활성 세션 수를 확인한다.
5.  허용 범위 내라면 신규 세션을 생성한다.
6.  최대 세션 수를 초과하면 기존 활성 세션 목록을 반환하고 사용자의 종료
    대상 선택을 요청한다.

최대 세션 수 확인과 신규 세션 생성은 하나의 트랜잭션 및 사용자 단위
잠금으로 동시성을 제어해야 한다.

### 5.2 최대 세션 수 초과 처리

-   기존 세션을 자동으로 종료하지 않는다.
-   신규 로그인을 자동으로 영구 거부하지 않는다.
-   사용자에게 기존 세션 목록을 제시한다.
-   사용자가 직접 종료할 세션을 선택한다.
-   선택한 세션이 해당 사용자 소유인지, 현재도 유효한지 재검증한다.
-   종료할 세션을 이력으로 이동하고 신규 세션을 생성한다.
-   동시 로그인 상황에 대비해 완료 시점에도 세션 수를 다시 검증한다.

로그인 완료 요청을 보호하기 위한 단기·일회성 pending-login 상태 또는
이에 준하는 안전한 거래 자격 증명이 필요하다. 단순히 예측 가능한 UUID를
로그인 완료 권한으로 사용해서는 안 된다.

### 5.3 세션 종료

세션 종료는 다음 작업을 동일 트랜잭션에서 수행한다.

1.  종료 대상 세션 조회 및 소유권 확인
2.  종료 이력 `user_session_histories` INSERT
3.  현재 세션 `user_sessions` DELETE
4.  트랜잭션 COMMIT

이력 INSERT 또는 세션 DELETE 중 하나라도 실패하면 전체 작업을
ROLLBACK한다.

### 5.4 JWT 인증 검증

Access Token이 만료되지 않았다는 사실만으로 인증을 허용해서는 안 된다.

인증 요청마다 최소한 다음을 검증한다.

1.  JWT 서명 및 만료
2.  JWT의 `sessionId`
3.  해당 `sessionId`의 세션 레코드 존재 여부
4.  세션의 활성 상태
5.  Refresh Token 만료 여부
6.  사용자 계정 활성화 여부

세션이 삭제되거나 유효하지 않으면 해당 세션의 Access Token도 거부해야
한다. 이 검증은 정리 스케줄러 실행 주기와 독립적으로 동작해야 한다.

## 6. 만료 세션 및 이력 정리 정책

### 6.1 만료 세션 정리

**실행 주기: 5분**

처리 흐름:

``` text
Scheduler 실행 (5분 주기)
    |
    v
Refresh Token 만료 세션 조회
    |
    v
대상 세션 잠금 및 재검증
    |
    v
user_session_histories INSERT
    |
    v
user_sessions DELETE
    |
    v
COMMIT
```

-   정리 배치가 실행되기 전에도 만료 세션은 인증에 성공해서는 안 된다.
-   배치에서는 대상 세션을 다시 검증해 중복 종료를 방지한다.
-   대량 데이터에 대비해 배치 크기를 제한하는 방식을 고려한다.
-   이력 INSERT와 현재 세션 DELETE는 같은 트랜잭션에서 처리한다.

### 6.2 세션 이력 보존 기간 정리

`system_settings`의 `SESSION_HISTORY_RETENTION_DAYS` 값을 읽어 보존
기간을 계산한다.

개념적인 삭제 기준:

``` sql
ended_at < CURRENT_TIMESTAMP
             - retention_days * INTERVAL '1 day'
```

실제 구현에서는 삭제 대상 조회, 배치 크기, 트랜잭션 범위 및 삭제 실행
결과 기록 방식을 구체화해야 한다.

세션 만료 정리와 세션 이력 보존 기간 만료 정리는 서로 다른 작업으로
분리한다.

## 7. 인덱스 및 제약조건 요약

  테이블                        대상                         제약조건 / 인덱스
  ----------------------------- ---------------------------- -------------------
  `user_security_settings`      `user_id`                    UNIQUE, FK
  `user_security_settings`      `max_session_count`          CHECK (`>= 1`)
  `user_sessions`               `session_id`                 UNIQUE
  `user_sessions`               `user_id`                    FK
  `user_sessions`               (`user_id`, `status`)        INDEX
  `user_sessions`               `refresh_token_expires_at`   INDEX
  `user_session_histories`      `user_id`                    FK
  `user_session_histories`      (`user_id`, `ended_at`)      INDEX
  `user_session_histories`      `session_id`                 INDEX
  `system_setting_categories`   `category_key`               UNIQUE
  `system_settings`             `category_id`                FK
  `system_settings`             `setting_key`                UNIQUE

## 8. 기존 구조에서의 전환 방향

현재 기존 `refresh_tokens` 구조는 사용자당 하나의 Refresh Token만
허용한다.

신규 구조에서는 Refresh Token을 세션별로 관리한다.

  기존 구조                       신규 구조
  ------------------------------- ------------------------------
  `refresh_tokens` 사용자당 1개   `user_sessions` 사용자당 N개
  `user_id` UNIQUE                `user_id` 일반 FK
  Refresh Token 원문 저장         Refresh Token 해시 저장
  사용자 기준 토큰 갱신           세션 기준 토큰 갱신

기존 Refresh Token을 신규 세션으로 임의 이관하지 않는 방향을 권장한다.
신규 체계 전환 시 기존 Refresh Token을 무효화하고 사용자에게 재로그인을
요구하는 방안을 Migration 계획에서 확정한다.

기존 Flyway Migration 파일은 수정하지 않고 신규 버전 Migration으로
변경한다.

## 9. 감사(Audit) 및 데이터 생명주기 확장 고려사항

이번 구현에서는 전체 공통 감사 시스템을 구현하지 않는다. 다만 세션 관련
행위를 추후 공통 Audit Event와 연결할 수 있도록 이벤트 정보를 명확히
유지한다.

세션 관련 감사 대상 후보:

-   로그인 성공 및 실패
-   세션 생성
-   사용자 선택에 의한 세션 종료
-   로그아웃
-   관리자 세션 종료
-   Refresh Token 회전 및 재사용 탐지
-   세션 만료 정리
-   세션 이력 보존 기간 변경
-   세션 이력 삭제 작업

`user_session_histories`는 세션 종료 이력이며, 공통 `audit_events`와는
목적이 다르다. 향후 공통 감사 기능이 구현되면 두 기록을 연계하되 동일
테이블로 무리하게 통합하지 않는다.

분리보관 정책 및 실행 이력은 향후 공통 데이터 생명주기 기능에서 별도
설계한다. 현재 `system_settings`는 공통 설정 기반으로 활용하되, 분리보관
처리 자체를 이번 멀티 세션 구현에 포함하지 않는다.

## 10. 구현 단계

1.  **Flyway Migration V12 설계 및 작성**
    -   신규 테이블 생성
    -   PK/FK/UNIQUE/CHECK 및 인덱스
    -   기존 사용자별 `user_security_settings` 초기 데이터
    -   공통 설정 분류 및 세션 이력 보존 기간 초기 데이터
    -   기존 `refresh_tokens` 전환 및 무효화 순서 확정
2.  **Entity / Repository**
    -   `UserSecuritySetting`
    -   `UserSession`
    -   `UserSessionHistory`
    -   공통 설정 Entity 및 Repository
3.  **Session Service**
    -   세션 생성·조회·종료
    -   사용자 단위 동시성 제어
    -   이력 이동 트랜잭션
4.  **JWT 인증 변경**
    -   JWT `sessionId` Claim
    -   요청별 세션 유효성 검증
    -   Refresh Token 해시 검증 및 회전
5.  **로그인 API 변경**
    -   세션 제한 응답
    -   기존 세션 목록 제공
    -   사용자 선택 세션 종료 후 신규 로그인 완료
6.  **정리 Scheduler**
    -   5분 주기 만료 세션 정리
    -   관리자 설정 기반 세션 이력 보존 기간 정리

## 11. 구현 전 별도 검토가 필요한 사항

아래 항목은 현재 설계에서 구현 방향을 제시했지만, Migration 및 서비스
구현 전에 구체화해야 한다.

-   기존 `refresh_tokens` 데이터와 테이블을 언제, 어떤 순서로 폐기할지
-   Refresh Token 해시 알고리즘 및 토큰 회전·재사용 탐지 정책
-   pending-login 거래 상태의 저장 위치, 만료 시간 및 일회성 보장
-   사용자 잠금과 세션 생성·종료의 정확한 트랜잭션 경계
-   세션 이력 보존 기간 설정 변경 시 기존 이력에도 새 정책을 적용할지
-   세션 이력 정리 작업의 실행 증적을 공통 Audit 기능과 어떻게 연계할지
-   사용자 물리 삭제 제한 및 비활성화 정책
-   세션 정리 배치의 동시 실행 방지 및 재처리 정책

------------------------------------------------------------------------

## 12. 설계 요약

본 설계는 사용자별 최대 5개 동시 세션을 기본값으로 하며, 사용자가 직접
기존 세션 종료 대상을 선택하는 방식이다. Refresh Token은 세션 단위로
해시 저장하고, Access Token도 세션 상태를 확인하여 폐기된 세션의 인증을
차단한다.

종료된 세션은 사용자 FK를 유지한 이력 테이블로 이동한다. 만료 세션
정리는 5분 주기로 수행하며, 세션 이력의 보존 기간은 공통 시스템 설정에서
관리한다.

**이 문서는 DB 설계 기준 문서이며, Flyway Migration 및 애플리케이션
구현은 아직 수행하지 않았다.**
