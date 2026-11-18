# PMIS Redis 설치 및 연동 가이드

- 문서 유형: 인프라 설치 및 개발 환경 구성 가이드
- 대상 환경: 집 PC 개발 환경
- Redis 버전: 7.4.6
- Docker 이미지: `redis:7`
- 컨테이너 이름: `redis`

---

## 1. 목적

본 문서는 PMIS Backend에서 로그인 임시 정보를 저장하기 위한 Redis 설치 및 연동 방법을 정의한다.

Redis는 PostgreSQL의 영구 업무 데이터와 구분하여 임시 데이터를 저장하는 용도로 사용한다.

현재 적용 범위는 다음과 같다.

- Redis 서버 설치 및 실행
- PMIS Backend Redis 연결 설정
- Redis 데이터베이스 분리
- 로그인 임시 정보 저장소 구성
- TTL 기반 데이터 자동 만료
- Redis 미설치 개발 환경에서도 Backend 빌드 및 테스트 가능하도록 구성

## 2. 시스템 구성

| 항목 | 설정 |
|---|---|
| 실행 방식 | Docker 컨테이너 |
| 이미지 | `redis:7` |
| 확인된 서버 버전 | Redis 7.4.6 |
| 컨테이너 이름 | `redis` |
| 접속 주소 | `127.0.0.1` |
| 포트 | `6379` |
| 데이터베이스 | `1` |
| 비밀번호 | 미설정 |
| 로그인 임시 정보 TTL | 300초 |
| Key Prefix | `pmis:auth:login-attempt:` |

### PMIS 연결 구조

```text
PMIS Backend
    |
    | 127.0.0.1:6379
    |
    v
Redis Docker Container
    |
    +-- Database 1
          |
          +-- pmis:auth:login-attempt:{loginAttemptId}
```

PostgreSQL은 영구 업무 데이터 저장소이며 Redis는 임시 데이터 저장소다.

## 3. Docker 설치 확인

Docker Desktop이 설치된 집 PC에서 PowerShell을 실행한다.

```powershell
docker --version
docker info
```

Docker가 정상적으로 실행 중인지 확인한다.

## 4. Redis 컨테이너 설치 및 실행

최초 설치 환경에서는 다음 명령으로 Redis 컨테이너를 생성할 수 있다.

```powershell
docker run -d `
  --name redis `
  -p 6379:6379 `
  --restart unless-stopped `
  redis:7
```

이미 동일한 이름의 컨테이너가 존재한다면 중복 생성하지 않는다.

기존 Redis 컨테이너를 재사용할 때는 다음 명령으로 상태를 확인한다.

```powershell
docker ps -a --filter "name=redis"
```

## 5. Redis 정상 동작 확인

### 5.1 컨테이너 상태 확인

```powershell
docker ps --filter "name=redis"
```

### 5.2 Redis 응답 확인

```powershell
docker exec redis redis-cli -n 1 PING
```

정상 응답:

```text
PONG
```

### 5.3 Redis 버전 확인

```powershell
docker exec redis redis-cli INFO server |
  Select-String "redis_version"
```

확인된 버전:

```text
redis_version:7.4.6
```

## 6. PMIS Backend 환경 변수

PMIS Backend의 `.env` 파일에 다음 설정을 추가한다.

```properties
PMIS_REDIS_ENABLED=true
PMIS_REDIS_HOST=127.0.0.1
PMIS_REDIS_PORT=6379
PMIS_REDIS_DATABASE=1
PMIS_AUTH_LOGIN_ATTEMPT_TTL_SECONDS=300
```

`.env` 파일은 로컬 환경 전용으로 사용하며 Git에 커밋하지 않는다.

공통 설정은 `application.yaml`에 기본값을 지정한다.

```yaml
pmis:
  redis:
    enabled: ${PMIS_REDIS_ENABLED:false}
    host: ${PMIS_REDIS_HOST:127.0.0.1}
    port: ${PMIS_REDIS_PORT:6379}
    database: ${PMIS_REDIS_DATABASE:1}

  auth:
    login-attempt:
      ttl-seconds: ${PMIS_AUTH_LOGIN_ATTEMPT_TTL_SECONDS:300}
```

Redis가 비활성화된 환경에서는 Redis 연결 관련 Bean을 생성하지 않도록 구성한다.

## 7. Redis 설정 및 구현 구조

주요 구현 파일:

```text
pmis-backend/
 └─ src/main/java/com/seos/pmis/
     ├─ config/
     │   └─ redis/
     │       ├─ RedisConfig.java
     │       └─ RedisProperties.java
     │
     └─ auth/
         └─ loginattempt/
             ├─ LoginAttempt.java
             ├─ LoginAttemptProperties.java
             ├─ LoginAttemptStore.java
             └─ RedisLoginAttemptStore.java
```

### 주요 역할

| 클래스 | 역할 |
|---|---|
| RedisConfig | Redis 연결 및 Template Bean 구성 |
| RedisProperties | Redis 접속 설정 관리 |
| LoginAttempt | 로그인 임시 정보 모델 |
| LoginAttemptProperties | 임시 정보 TTL 설정 |
| LoginAttemptStore | 임시 저장소 인터페이스 |
| RedisLoginAttemptStore | Redis 기반 저장·조회·삭제 |

### 데이터 만료 정책

로그인 임시 정보는 기본 300초(5분)의 TTL을 적용한다.

Key 형식:

```text
pmis:auth:login-attempt:{loginAttemptId}
```

## 8. 개발 PC 적용 정책

개발 PC에는 Docker 및 Redis 설치를 요구하지 않는다.

| 구분 | 집 PC | 개발 PC |
|---|---|---|
| Redis 서버 | Docker Redis 실행 | 설치하지 않음 |
| Redis 활성화 | `true` | 기본 `false` |
| Redis 연결 | 사용 | 사용하지 않음 |
| Backend 빌드 | 가능 | 가능 |
| 기존 테스트 | 가능 | 가능 |
| Redis 연동 테스트 | 집 PC에서 수행 | 별도 Redis 없이 수행하지 않음 |

공통 Git 설정은 Redis 비활성화를 기본값으로 유지한다.

## 9. 검증 결과

2026-11-19 기준 확인 결과:

| 검증 항목 | 결과 |
|---|---|
| Docker Redis 컨테이너 실행 | 확인 |
| Redis 서버 버전 7.4.6 | 확인 |
| Redis 포트 6379 | 확인 |
| Redis DB 1 PING | 성공 |
| Redis 연결 설정 구현 | 완료 |
| Redis 임시 저장소 구현 | 완료 |
| Redis 저장소 단위 테스트 | 통과 |
| Backend 전체 빌드 | 성공 |
| Spring Boot 기동 | 성공 |
| 애플리케이션 실제 저장·조회 | 별도 검증하지 않음 |

## 10. 운영 및 주의사항

1. 기존 Redis 컨테이너를 임의로 삭제하거나 재생성하지 않는다.
2. Redis 데이터 초기화를 위해 `FLUSHALL`, `FLUSHDB`를 실행하지 않는다.
3. 로컬 `.env` 파일은 Git에 커밋하지 않는다.
4. Redis가 비활성화된 환경에서도 Backend 빌드와 기존 테스트가 가능해야 한다.
5. 현재 Redis 저장소는 구현되어 있으나 기존 AuthService 로그인 처리와의 연결은 별도 개발 범위다.
6. 운영 환경에 적용할 때는 인증, 네트워크 접근 제어, 비밀번호 또는 ACL, 데이터 보호 및 장애 대응 정책을 별도로 수립한다.

---

## 11. 향후 확장 계획

- 사용자별 동시 접속 세션 수 제한
- 로그인 시 기존 활성 세션 확인
- 세션 선택을 위한 임시 로그인 정보 관리
- 선택한 기존 세션 종료 후 신규 로그인 처리

위 기능은 Redis 설치 자체와 구분되는 Backend 인증 및 세션 관리 기능으로 개발한다.
