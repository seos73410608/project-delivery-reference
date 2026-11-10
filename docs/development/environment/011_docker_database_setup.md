# PMIS Docker 데이터베이스 개발 환경 구축 및 운영 가이드

- **문서 경로:** `docs/development/environment/011_docker_database_setup.md`
- **문서 버전:** 1.0
- **작성일:** 2026-11-11

---

## 1. 문서 목적

이 문서는 PMIS 개발 환경에서 사용하는 Docker 기반 데이터베이스의 설치, 구성, 실행, 연결, 백업 및 복구 방법을 정리한다.

기존 Windows 개발 환경에 설치된 데이터베이스와 별도로 Docker를 이용하여 데이터베이스를 독립적으로 운영하고, 여러 데이터 저장소를 하나의 개발 PC에서 관리하는 것을 목적으로 한다.

본 문서의 주요 대상은 다음과 같다.

- PostgreSQL 18
- pgvector 0.8.7
- Neo4j Community 5.26
- Docker Desktop
- Docker Compose
- 데이터베이스별 영구 데이터 볼륨
- 환경변수 및 인증정보 관리
- 데이터베이스 백업 및 복구
- PMIS 백엔드 연동 준비

본 문서는 Windows 10/11 및 PowerShell 환경을 기준으로 작성한다.

## 2. 전체 구성

### 2.1 데이터베이스 구성

PMIS 개발 환경에서는 데이터 특성과 검색 목적에 따라 데이터베이스를 구분하여 사용한다.

| 구성 요소 | 역할 | Docker 이미지 |
|---|---|---|
| PostgreSQL | PMIS 핵심 업무 데이터 저장 | `pgvector/pgvector:0.8.7-pg18-trixie` |
| pgvector | 벡터 임베딩 및 유사도 검색 | PostgreSQL 확장 |
| Neo4j | 노드 및 관계 기반 그래프 데이터 저장 | `neo4j:5.26-community` |
| MariaDB | 기존 데이터베이스 호환성 및 테스트 | 기존 구성 유지 |
| Redis | 캐시 및 임시 데이터 관리 | 기존 구성 유지 |

pgvector는 별도의 데이터베이스 서버가 아니다.

PostgreSQL 내부에 설치하는 Extension이며, 기존 PostgreSQL 데이터베이스와 동일한 서버 및 저장소를 사용한다.

Neo4j는 PostgreSQL과 독립적으로 실행되는 그래프 데이터베이스다.

### 2.2 전체 아키텍처

```text
Windows PC
    |
    +--------------------------------------+
    | Docker Desktop                       |
    |                                      |
    |  +--------------------------------+  |
    |  | PostgreSQL Container           |  |
    |  | pgvector/pgvector:0.8.7-pg18   |  |
    |  |                                |  |
    |  | PostgreSQL 18.6                |  |
    |  | pgvector 0.8.7                 |  |
    |  |                                |  |
    |  | Host Port : 5433               |  |
    |  | Container Port : 5432          |  |
    |  +--------------------------------+  |
    |                 |                    |
    |                 v                    |
    |       pmis_postgres_data             |
    |                                      |
    |  +--------------------------------+  |
    |  | Neo4j Container                |  |
    |  | neo4j:5.26-community           |  |
    |  |                                |  |
    |  | Neo4j Community 5.26.33        |  |
    |  |                                |  |
    |  | HTTP : 7474                    |  |
    |  | Bolt : 7687                    |  |
    |  +--------------------------------+  |
    |                 |                    |
    |                 v                    |
    |       pmis_neo4j_data                |
    |       pmis_neo4j_logs                |
    |                                      |
    +--------------------------------------+
                  |
                  v
          PMIS Backend
          Spring Boot 3.5
          Java 21
```

### 2.3 Docker 작업 디렉터리

Docker 관련 설정은 프로젝트 소스와 분리하여 관리한다.

```text
C:\Docker\
├── pmis-postgresql\
│   ├── docker-compose.yml
│   ├── .env
│   └── backup\
│       └── pmis_before_pgvector.dump
│
└── pmis-neo4j\
    ├── docker-compose.yml
    ├── .env
    └── .gitignore
```

각 데이터베이스의 설정 및 실행을 독립적으로 관리한다.

## 3. Docker 사전 준비

### 3.1 Docker Desktop 설치 확인

Docker Desktop을 실행한 상태에서 PowerShell을 연다.

Docker 버전을 확인한다.

```powershell
docker --version
```

Docker Compose 버전을 확인한다.

```powershell
docker compose version
```

Docker 엔진 상태를 확인한다.

```powershell
docker info
```

Docker 엔진이 정상적으로 실행 중이어야 한다.

### 3.2 기존 포트 사용 여부 확인

PostgreSQL, MariaDB, Redis 및 다른 데이터베이스가 기존 포트를 사용할 수 있으므로 설치 전에 확인한다.

```powershell
Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue |
    Where-Object {
        $_.LocalPort -in @(5432, 5433, 3306, 6379, 7474, 7687)
    } |
    Select-Object LocalAddress, LocalPort, OwningProcess
```

포트 사용 여부를 확인한 후 Docker Compose의 호스트 포트를 결정한다.

### 3.3 Docker 컨테이너 전체 확인

```powershell
docker ps -a
```

실행 중인 컨테이너:

```powershell
docker ps
```

Docker 볼륨:

```powershell
docker volume ls
```

기존 데이터베이스 컨테이너와 볼륨을 확인한 후 신규 설치를 진행한다.

## 4. PostgreSQL 18 + pgvector 설치

### 4.1 PostgreSQL Docker 작업 디렉터리

작업 디렉터리로 이동한다.

```powershell
New-Item -ItemType Directory -Force -Path C:\Docker\pmis-postgresql

Set-Location C:\Docker\pmis-postgresql
```

현재 구성에서는 PostgreSQL 18과 pgvector가 함께 포함된 이미지를 사용한다.

```text
pgvector/pgvector:0.8.7-pg18-trixie
```

이 이미지는 PostgreSQL 18 기반이며 pgvector 0.8.7 확장을 포함한다.

### 4.2 Docker 환경변수 파일 생성

`.env` 파일을 생성한다.

```text
POSTGRES_DB=pmis
POSTGRES_USER=pmis_admin
POSTGRES_PASSWORD=개별_설정_비밀번호
POSTGRES_PORT=5433
```

실제 비밀번호는 로컬 환경에서 직접 설정한다.

비밀번호를 Git에 커밋하거나 채팅 및 로그에 노출하지 않는다.

### 4.3 Docker Compose 파일

`C:\Docker\pmis-postgresql\docker-compose.yml`

```yaml
services:
  postgres:
    image: pgvector/pgvector:0.8.7-pg18-trixie
    container_name: pmis-postgresql
    restart: unless-stopped

    environment:
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
      POSTGRES_INITDB_ARGS: "--encoding=UTF8"

    ports:
      - "127.0.0.1:${POSTGRES_PORT}:5432"

    volumes:
      - pmis_postgres_data:/var/lib/postgresql

    shm_size: 256mb

    healthcheck:
      test:
        - CMD-SHELL
        - pg_isready -U ${POSTGRES_USER} -d ${POSTGRES_DB}
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s

volumes:
  pmis_postgres_data:
    name: pmis_postgres_data
```

주요 설정:

| 항목 | 설정 |
|---|---|
| 이미지 | `pgvector/pgvector:0.8.7-pg18-trixie` |
| 컨테이너 | `pmis-postgresql` |
| 데이터베이스 | `pmis` |
| 초기 관리자 계정 | `pmis_admin` |
| 호스트 포트 | 5433 |
| 컨테이너 포트 | 5432 |
| 데이터 볼륨 | `pmis_postgres_data` |
| 재시작 정책 | `unless-stopped` |

기존 집 PC에서는 PostgreSQL 15 기반 `edb-postgres`가 호스트 5432 포트를 사용하고 있어 PMIS PostgreSQL은 5433 포트를 사용한다.

### 4.4 Docker Compose 설정 검증

```powershell
docker compose config --quiet

if ($LASTEXITCODE -eq 0) {
    "Docker Compose configuration: VALID"
} else {
    "Docker Compose configuration: INVALID"
}
```

정상 결과:

```text
Docker Compose configuration: VALID
```

### 4.5 Docker 이미지 다운로드

```powershell
docker compose pull
```

이미지 다운로드 상태를 확인한다.

```powershell
docker images
```

### 4.6 PostgreSQL 컨테이너 실행

```powershell
docker compose up -d
```

컨테이너 실행 상태를 확인한다.

```powershell
docker ps --filter "name=pmis-postgresql"
```

정상 상태:

```text
IMAGE
pgvector/pgvector:0.8.7-pg18-trixie

STATUS
Up (healthy)

PORTS
127.0.0.1:5433->5432/tcp
```

### 4.7 PostgreSQL 접속 확인

```powershell
docker exec -it pmis-postgresql psql -U pmis_admin -d pmis
```

접속 후 다음 SQL을 실행한다.

```sql
SELECT version();

SELECT current_database(), current_user;
```

확인 사항:

- PostgreSQL 버전 18.6
- 데이터베이스 `pmis`
- 접속 계정 `pmis_admin`

확장 목록을 확인한다.

```sql
SELECT extname, extversion
FROM pg_extension
ORDER BY extname;
```

초기 상태에서는 `plpgsql` 등이 확인된다.

### 4.8 기존 PostgreSQL 데이터 확인

기존에 생성된 데이터베이스를 사용하는 경우 다음 항목을 확인한다.

```sql
\dt
```

스키마 목록:

```sql
\dn
```

계정 목록:

```sql
\du
```

기존 PMIS 데이터베이스의 테이블, 계정 및 권한이 유지되는지 확인한다.

**주의:** PostgreSQL 이미지 변경 시 기존 Named Volume을 그대로 연결해야 한다.

기존 데이터가 있는 환경에서는 다음 명령을 실행하지 않는다.

```powershell
docker compose down -v
```

`-v` 옵션은 Compose에 연결된 볼륨을 삭제할 수 있으므로 기존 데이터가 있는 환경에서 사용하지 않는다.

## 5. pgvector 확장 설치 및 검증

### 5.1 pgvector 확장 설치 가능 여부 확인

PostgreSQL에 접속한 상태에서 실행한다.

```sql
SELECT
    name,
    default_version,
    installed_version
FROM pg_available_extensions
WHERE name = 'vector';
```

정상 결과 예시:

```text
name    | default_version | installed_version
vector  | 0.8.7           |
```

`installed_version`이 비어 있다면 해당 데이터베이스에는 아직 확장이 설치되지 않은 상태다.

### 5.2 pgvector 확장 활성화

PMIS 데이터베이스에 접속한다.

```powershell
docker exec -it pmis-postgresql psql -U pmis_admin -d pmis
```

다음 SQL을 실행한다.

```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

정상 결과:

```text
CREATE EXTENSION
```

### 5.3 pgvector 설치 버전 확인

```sql
SELECT extname, extversion
FROM pg_extension
WHERE extname = 'vector';
```

정상 결과:

```text
extname | extversion
vector  | 0.8.7
```

### 5.4 벡터 데이터 연산 테스트

실제 벡터 데이터 형식과 거리 연산을 테스트한다.

```sql
SELECT
    '[1,2,3]'::vector AS embedding,
    '[1,2,3]'::vector <-> '[4,5,6]'::vector AS l2_distance;
```

정상 결과:

```text
embedding | l2_distance
[1,2,3]   | 5.196152422706632
```

`<->` 연산자는 유클리드 거리(L2 Distance)를 계산한다.

### 5.5 pgvector 주요 연산자

| 연산자 | 설명 |
|---|---|
| `<->` | 유클리드 거리 |
| `<#>` | 음수 내적 |
| `<=>` | 코사인 거리 |

pgvector는 벡터 저장과 검색 기능을 제공한다.

실제 PMIS 검색 기능에 적용할 때는 임베딩 모델, 벡터 차원, 인덱스 종류 및 검색 성능을 함께 설계해야 한다.

### 5.6 설치 완료 기준

- [x] pgvector 이미지 다운로드
- [x] PostgreSQL 컨테이너 실행
- [x] PostgreSQL 18.6 확인
- [x] 기존 데이터 볼륨 유지
- [x] `vector` 확장 설치
- [x] pgvector 0.8.7 확인
- [x] 벡터 데이터 형식 확인
- [x] 벡터 거리 연산 확인

## 6. Neo4j Community 설치

### 6.1 Neo4j Docker 작업 디렉터리

```powershell
New-Item -ItemType Directory -Force -Path C:\Docker\pmis-neo4j

Set-Location C:\Docker\pmis-neo4j
```

### 6.2 Neo4j 포트 사용 여부 확인

Neo4j에서 기본적으로 사용하는 포트는 다음과 같다.

| 포트 | 용도 |
|---|---|
| 7474 | HTTP 및 Neo4j Browser |
| 7473 | HTTPS |
| 7687 | Bolt 프로토콜 |

사용 여부를 확인한다.

```powershell
Get-NetTCPConnection -LocalPort 7474,7687 -State Listen -ErrorAction SilentlyContinue |
    Select-Object LocalAddress, LocalPort, State, OwningProcess
```

기존 집 PC에서는 7474와 7687 포트를 사용하고 있지 않은 것을 확인했다.

### 6.3 Neo4j 환경변수 파일 생성

`C:\Docker\pmis-neo4j\.env`

```text
NEO4J_AUTH=neo4j/개별_설정_비밀번호
```

Neo4j 초기 관리자 계정은 `neo4j`를 사용한다.

비밀번호는 로컬 환경에서 직접 설정하며, PostgreSQL 비밀번호와 구분하여 관리하는 것을 권장한다.

Neo4j 인증정보를 `.env`에 보관하고 Git에 포함하지 않는다.

### 6.4 Neo4j Docker Compose 파일

`C:\Docker\pmis-neo4j\docker-compose.yml`

```yaml
services:
  neo4j:
    image: neo4j:5.26-community
    container_name: pmis-neo4j
    restart: unless-stopped

    environment:
      NEO4J_AUTH: ${NEO4J_AUTH}

    ports:
      - "127.0.0.1:7474:7474"
      - "127.0.0.1:7687:7687"

    volumes:
      - pmis_neo4j_data:/data
      - pmis_neo4j_logs:/logs

volumes:
  pmis_neo4j_data:
    name: pmis_neo4j_data

  pmis_neo4j_logs:
    name: pmis_neo4j_logs
```

주요 설정:

| 항목 | 설정 |
|---|---|
| 이미지 | `neo4j:5.26-community` |
| 컨테이너 | `pmis-neo4j` |
| 에디션 | Community |
| HTTP 포트 | 7474 |
| Bolt 포트 | 7687 |
| 데이터 볼륨 | `pmis_neo4j_data` |
| 로그 볼륨 | `pmis_neo4j_logs` |
| 재시작 정책 | `unless-stopped` |

포트 바인딩은 `127.0.0.1`을 사용하여 로컬 PC에서만 접근하도록 설정한다.

### 6.5 Git 제외 설정

`C:\Docker\pmis-neo4j\.gitignore`

```text
.env
```

비밀번호가 포함된 `.env` 파일은 Git에 포함하지 않는다.

### 6.6 Docker Compose 설정 검증

```powershell
docker compose config --quiet

if ($LASTEXITCODE -eq 0) {
    "Neo4j Docker Compose configuration: VALID"
} else {
    "Neo4j Docker Compose configuration: INVALID"
}
```

정상 결과:

```text
Neo4j Docker Compose configuration: VALID
```

### 6.7 Neo4j 이미지 다운로드

```powershell
docker compose pull
```

다운로드된 이미지를 확인한다.

```powershell
docker images
```

### 6.8 Neo4j 컨테이너 실행

```powershell
docker compose up -d
```

실행 상태를 확인한다.

```powershell
docker ps --filter "name=pmis-neo4j"
```

정상 결과 예시:

```text
IMAGE
neo4j:5.26-community

STATUS
Up

PORTS
127.0.0.1:7474->7474/tcp
127.0.0.1:7687->7687/tcp
```

현재 Compose 구성에는 별도 Healthcheck를 정의하지 않았으므로 `docker ps`에 `healthy`가 표시되지 않는 것이 정상이다.

### 6.9 Neo4j 초기 기동 로그 확인

```powershell
docker logs --tail 50 pmis-neo4j
```

다음 로그를 확인한다.

```text
Bolt enabled on 0.0.0.0:7687.
HTTP enabled on 0.0.0.0:7474.
Remote interface available at http://localhost:7474/
Started.
```

Neo4j 초기 데이터베이스가 정상적으로 시작되었는지 확인한다.

### 6.10 Neo4j Browser 접속

브라우저에서 다음 주소로 접속한다.

```text
http://localhost:7474
```

로그인 정보:

| 항목 | 값 |
|---|---|
| Username | neo4j |
| Password | `.env`에 설정한 초기 비밀번호 |

정상적으로 로그인되는지 확인한다.

### 6.11 Neo4j Cypher Shell 접속

PowerShell에서 실행한다.

```powershell
docker exec -it pmis-neo4j cypher-shell -u neo4j
```

비밀번호를 입력한다.

정상 접속 결과:

```text
Connected to Neo4j using Bolt protocol
```

### 6.12 Cypher 쿼리 실행 확인

Neo4j Shell에서 실행한다.

```cypher
RETURN 'PMIS Neo4j Connected' AS status, 1 AS query_test;
```

정상 결과:

```text
status                 | query_test
PMIS Neo4j Connected   | 1
```

Neo4j 버전과 에디션은 다음 쿼리로 확인할 수 있다.

```cypher
CALL dbms.components()
YIELD name, versions, edition
RETURN name, versions, edition;
```

Shell을 종료한다.

```cypher
:exit
```

### 6.13 Neo4j 설치 완료 기준

- [x] Neo4j 이미지 다운로드
- [x] Compose 설정 검증
- [x] Neo4j 컨테이너 실행
- [x] 데이터 볼륨 생성
- [x] 로그 볼륨 생성
- [x] Neo4j 5.26.33 기동 확인
- [x] HTTP 포트 활성화
- [x] Bolt 포트 활성화
- [x] 초기 관리자 인증 성공
- [x] Cypher 쿼리 실행 성공

## 7. Docker 컨테이너 통합 관리

### 7.1 PMIS 데이터베이스 컨테이너 확인

```powershell
docker ps
```

이름으로 각각 확인할 수도 있다.

```powershell
docker ps --filter "name=pmis-postgresql"
docker ps --filter "name=pmis-neo4j"
```

### 7.2 Docker 볼륨 확인

```powershell
docker volume ls
```

PMIS 관련 볼륨만 확인한다.

```powershell
docker volume ls --filter "name=pmis_"
```

기대하는 볼륨:

```text
pmis_postgres_data
pmis_neo4j_data
pmis_neo4j_logs
```

### 7.3 PostgreSQL 상태 확인

```powershell
docker compose -f C:\Docker\pmis-postgresql\docker-compose.yml ps
```

### 7.4 Neo4j 상태 확인

```powershell
docker compose -f C:\Docker\pmis-neo4j\docker-compose.yml ps
```

### 7.5 PostgreSQL 중지 및 시작

PostgreSQL 작업 디렉터리에서 실행한다.

중지:

```powershell
docker compose stop
```

시작:

```powershell
docker compose start
```

재시작:

```powershell
docker compose restart
```

### 7.6 Neo4j 중지 및 시작

Neo4j 작업 디렉터리에서 실행한다.

중지:

```powershell
docker compose stop
```

시작:

```powershell
docker compose start
```

재시작:

```powershell
docker compose restart
```

### 7.7 컨테이너 로그 확인

PostgreSQL:

```powershell
docker logs --tail 100 pmis-postgresql
```

Neo4j:

```powershell
docker logs --tail 100 pmis-neo4j
```

실시간 로그:

```powershell
docker logs -f pmis-postgresql
```

```powershell
docker logs -f pmis-neo4j
```

실시간 로그 확인을 종료하려면 `Ctrl+C`를 누른다.

## 8. 데이터 영속성 및 백업

### 8.1 Docker Named Volume 사용 목적

컨테이너와 데이터는 별도로 관리한다.

| 구분 | 역할 |
|---|---|
| Container | 데이터베이스 실행 환경 |
| Named Volume | 데이터베이스 실제 저장 데이터 |
| Docker Image | 데이터베이스 실행 이미지 |

컨테이너를 삭제하고 다시 생성하더라도 동일한 Named Volume을 연결하면 기존 데이터가 유지될 수 있다.

단, 볼륨 자체를 삭제하거나 호환되지 않는 데이터베이스 버전으로 실행하면 데이터 손실 또는 손상이 발생할 수 있다.

### 8.2 PostgreSQL 백업

현재 집 PC의 PMIS PostgreSQL 데이터베이스를 Custom Format으로 백업하는 예시다.

```powershell
New-Item -ItemType Directory -Force -Path C:\Docker\pmis-postgresql\backup

docker exec pmis-postgresql pg_dump `
    -U pmis_admin `
    -d pmis `
    -Fc `
    -f /tmp/pmis_backup.dump
```

컨테이너 내부의 백업 파일을 Windows PC로 복사한다.

```powershell
docker cp pmis-postgresql:/tmp/pmis_backup.dump `
    C:\Docker\pmis-postgresql\backup\pmis_backup.dump
```

백업 파일을 확인한다.

```powershell
Get-Item C:\Docker\pmis-postgresql\backup\pmis_backup.dump |
    Select-Object Name, Length, LastWriteTime
```

백업 아카이브 목록을 확인한다.

```powershell
docker exec pmis-postgresql pg_restore -l /tmp/pmis_backup.dump
```

백업 파일이 정상적으로 생성되었는지 확인한 후 컨테이너 내부 임시 파일을 삭제할 수 있다.

```powershell
docker exec pmis-postgresql rm -f /tmp/pmis_backup.dump
```

### 8.3 PostgreSQL 복구

복구 작업 전 기존 데이터베이스 백업을 별도로 확보한다.

복구 대상 데이터베이스의 상태와 기존 객체를 확인한다.

Custom Format 백업을 복원하는 예시:

```powershell
docker cp `
    C:\Docker\pmis-postgresql\backup\pmis_backup.dump `
    pmis-postgresql:/tmp/pmis_backup.dump
```

복구 명령:

```powershell
docker exec -it pmis-postgresql pg_restore `
    -U pmis_admin `
    -d pmis `
    /tmp/pmis_backup.dump
```

기존 객체와 충돌하는 경우에는 무조건 `--clean`을 추가하지 않는다.

복구 목적, 대상 데이터베이스 및 기존 데이터 보존 여부를 확인한 후 복구 전략을 결정한다.

### 8.4 PostgreSQL 역할 및 권한 백업

`pg_dump`는 기본적으로 데이터베이스 내부 객체를 백업하지만 서버 전체 역할 및 전역 객체까지 모두 백업하지는 않는다.

필요한 경우 다음 명령으로 전역 객체를 별도 백업한다.

```powershell
docker exec pmis-postgresql pg_dumpall `
    -U pmis_admin `
    --globals-only `
    -f /tmp/pmis_globals.sql
```

Windows PC로 복사한다.

```powershell
docker cp pmis-postgresql:/tmp/pmis_globals.sql `
    C:\Docker\pmis-postgresql\backup\pmis_globals.sql
```

역할 및 권한 복구는 기존 계정과의 충돌을 검토한 후 수행한다.

### 8.5 Neo4j 백업

Neo4j는 데이터베이스 파일과 트랜잭션 로그의 일관성을 고려하여 백업해야 한다.

운영 또는 개발 데이터가 중요한 경우 Neo4j의 공식 백업 도구인 `neo4j-admin database dump` 또는 적절한 온라인 백업 기능을 사용한다.

Community Edition에서는 사용 가능한 백업 방식과 실행 조건을 해당 버전의 공식 문서에서 확인한다.

예시로 데이터베이스를 중지한 후 볼륨을 별도로 백업하는 방식을 고려할 수 있다.

```powershell
docker compose stop
```

Docker 볼륨의 실제 저장 위치를 확인한다.

```powershell
docker volume inspect pmis_neo4j_data
```

볼륨 백업은 Docker Desktop의 Linux 컨테이너 환경과 볼륨 저장 방식에 맞는 별도 백업 절차를 적용한다.

백업 완료 후 다시 시작한다.

```powershell
docker compose start
```

**주의:** 단순히 실행 중인 `/data` 디렉터리의 파일을 복사하는 방식은 데이터베이스 일관성을 보장하지 않을 수 있다.

Neo4j 백업 및 복구 절차는 사용 중인 버전과 에디션의 공식 지원 범위를 확인한 후 확정한다.

### 8.6 데이터 볼륨 삭제 금지

다음 명령은 기존 데이터가 있는 환경에서 사용하지 않는다.

```powershell
docker compose down -v
```

또한 다음 명령도 주의한다.

```powershell
docker volume rm pmis_postgres_data
docker volume rm pmis_neo4j_data
```

데이터베이스 컨테이너를 재생성하는 경우에는 Named Volume을 유지한다.

## 9. PMIS 데이터베이스 연동 설계 기준

### 9.1 PostgreSQL과 pgvector 역할

PostgreSQL은 PMIS의 핵심 업무 데이터를 저장한다.

예상 데이터:

- 사용자
- 프로젝트
- 작업 및 일정
- 이슈 및 위험
- 조치 항목
- 회의 및 의사결정
- 증적 및 검수 이력

pgvector는 임베딩 기반 검색을 지원한다.

예상 활용 영역:

- 과거 프로젝트 문서 유사도 검색
- 이슈 및 조치 사례 검색
- 증적 설명 및 검수 의견 검색
- 프로젝트 지식 검색
- 문서 및 업무 내용 의미 기반 검색

pgvector를 도입하더라도 기존 관계형 데이터 모델을 대체하지 않는다.

### 9.2 Neo4j 역할

Neo4j는 데이터 사이의 관계를 중심으로 정보를 저장하고 탐색한다.

예상 노드:

```text
Project
Task
User
Issue
ActionItem
Evidence
Decision
Document
Vendor
```

예상 관계:

```text
(Project)-[:HAS_TASK]->(Task)

(Task)-[:ASSIGNED_TO]->(User)

(Task)-[:HAS_ISSUE]->(Issue)

(Issue)-[:RESOLVED_BY]->(ActionItem)

(Evidence)-[:SUPPORTS]->(Task)

(Decision)-[:AFFECTS]->(Project)

(Project)-[:HAS_VENDOR]->(Vendor)
```

실제 도입 시에는 기존 PMIS 관계형 데이터와 중복 저장되는 정보를 최소화한다.

Neo4j는 관계 탐색 및 분석에 필요한 데이터만 선별하여 관리하는 것을 권장한다.

### 9.3 pgvector와 Neo4j의 역할 분리

| 구분 | PostgreSQL + pgvector | Neo4j |
|---|---|---|
| 핵심 목적 | 업무 데이터 및 유사도 검색 | 관계 탐색 |
| 저장 구조 | 테이블 및 벡터 | 노드 및 관계 |
| 검색 방식 | SQL 및 벡터 거리 연산 | Cypher |
| 활용 사례 | 유사한 이슈 및 문서 검색 | 이슈의 영향 관계 분석 |
| 데이터 기준 | 업무 데이터의 원본 저장소 | 관계 분석용 데이터 |

두 데이터베이스는 상호 보완적으로 사용할 수 있다.

### 9.4 Spring Boot 연동 기준

PostgreSQL은 기존 Spring Data JPA 및 JDBC 구성을 유지한다.

pgvector는 PostgreSQL 연결을 기반으로 SQL 또는 지원 라이브러리를 통해 접근할 수 있다.

Neo4j는 향후 다음 기술을 검토한다.

- Neo4j Java Driver
- Spring Data Neo4j
- Cypher Query

Neo4j 연동을 위해 기존 PostgreSQL 프로파일과 설정을 무리하게 통합하지 않는다.

필요한 시점에 별도의 Neo4j 연결 설정과 인증정보를 추가한다.

### 9.5 데이터 동기화 원칙

PostgreSQL과 Neo4j에 동일한 업무 데이터를 무조건 중복 저장하지 않는다.

권장 원칙:

1. PostgreSQL을 핵심 업무 데이터의 기준 저장소로 사용한다.
2. Neo4j에는 관계 분석에 필요한 데이터를 선별하여 반영한다.
3. Neo4j 데이터는 PostgreSQL의 식별자를 참조할 수 있도록 구성한다.
4. 동기화 실패 시 재처리할 수 있도록 설계한다.
5. 데이터 변경 이력과 동기화 상태를 관리한다.
6. 실제 사용 사례가 확인된 이후 Neo4j 연동 범위를 확대한다.

## 10. 주요 문제 해결

### 10.1 Docker 엔진 연결 실패

증상:

```text
Cannot connect to the Docker daemon
```

확인:

```powershell
docker info
```

해결:

1. Docker Desktop 실행 여부 확인
2. Docker 엔진 시작 상태 확인
3. WSL2 기반 엔진 상태 확인
4. Docker 명령 재실행

### 10.2 포트 충돌

증상:

```text
port is already allocated
```

확인:

```powershell
Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue
```

해결:

1. 기존 프로그램의 포트 사용 여부 확인
2. Docker 컨테이너 포트 매핑 확인
3. 필요한 경우 호스트 포트 변경
4. PMIS JDBC URL 및 Neo4j 접속 URL 변경 여부 확인

### 10.3 PostgreSQL 컨테이너 실행 실패

확인:

```powershell
docker logs --tail 100 pmis-postgresql
```

```powershell
docker inspect pmis-postgresql
```

확인 항목:

- 환경변수 누락
- 포트 충돌
- 이미지 다운로드 실패
- 데이터 볼륨 연결 오류
- PostgreSQL 데이터 디렉터리 호환성
- 데이터베이스 초기화 오류

### 10.4 pgvector 확장을 찾을 수 없는 경우

증상:

```text
extension "vector" is not available
```

확인:

```sql
SELECT name, default_version
FROM pg_available_extensions
WHERE name = 'vector';
```

해결:

1. 실행 중인 이미지 확인
2. pgvector가 포함된 이미지인지 확인
3. 이미지 버전 확인
4. 컨테이너 로그 확인
5. 확장 설치 가능 여부 재확인

현재 PMIS 개발 환경에서는 다음 이미지를 사용한다.

```text
pgvector/pgvector:0.8.7-pg18-trixie
```

### 10.5 PostgreSQL 데이터베이스 연결 실패

확인:

```powershell
Test-NetConnection localhost -Port 5433
```

Docker 컨테이너 확인:

```powershell
docker ps --filter "name=pmis-postgresql"
```

JDBC URL 예시:

```text
jdbc:postgresql://localhost:5433/pmis
```

컨테이너 내부에서는 PostgreSQL 기본 포트 5432를 사용한다.

### 10.6 Neo4j 초기 인증 실패

증상:

```text
The client is unauthorized due to authentication failure.
```

확인:

```powershell
docker logs --tail 100 pmis-neo4j
```

확인 항목:

- `.env`의 `NEO4J_AUTH`
- 초기 관리자 계정
- 최초 데이터베이스 초기화 여부
- 기존 데이터 볼륨에 저장된 인증 상태

Neo4j의 초기 비밀번호 환경변수는 최초 데이터베이스 초기화 시 적용된다.

기존 데이터 볼륨이 존재하는 경우 `.env`의 비밀번호를 변경하는 것만으로 기존 계정 비밀번호가 자동 변경되지는 않는다.

기존 계정 비밀번호는 Neo4j 인증 및 사용자 관리 절차에 따라 변경한다.

### 10.7 Neo4j Browser 접속 실패

확인:

```powershell
Test-NetConnection localhost -Port 7474
```

```powershell
docker logs --tail 100 pmis-neo4j
```

브라우저 주소:

```text
http://localhost:7474
```

### 10.8 Neo4j Bolt 연결 실패

확인:

```powershell
Test-NetConnection localhost -Port 7687
```

컨테이너 내부 접속 테스트:

```powershell
docker exec -it pmis-neo4j cypher-shell -u neo4j
```

PMIS 애플리케이션 연결 주소:

```text
bolt://localhost:7687
```

### 10.9 Docker 컨테이너는 실행되지만 데이터가 보이지 않는 경우

확인 항목:

1. 컨테이너에 연결된 Named Volume
2. 실제 볼륨 이름
3. 기존 컨테이너와 신규 컨테이너의 볼륨 연결 상태
4. 데이터베이스 이름
5. 접속 계정
6. 데이터베이스 초기화 여부

볼륨 확인:

```powershell
docker volume ls
```

볼륨 상세 확인:

```powershell
docker volume inspect pmis_postgres_data
docker volume inspect pmis_neo4j_data
```

데이터가 보이지 않는다는 이유로 기존 볼륨을 삭제하지 않는다.

## 11. 보안 및 운영 관리 원칙

### 11.1 인증정보 관리

- [ ] PostgreSQL 비밀번호를 `.env`에서 관리한다.
- [ ] Neo4j 비밀번호를 `.env`에서 관리한다.
- [ ] 실제 인증정보를 Git에 포함하지 않는다.
- [ ] `.env` 파일의 접근 권한을 관리한다.
- [ ] 비밀번호를 로그 및 콘솔에 출력하지 않는다.
- [ ] 데이터베이스 계정별 권한을 구분한다.

### 11.2 네트워크 접근 제한

현재 Compose 구성에서는 다음과 같이 로컬 인터페이스에만 포트를 바인딩한다.

```yaml
ports:
  - "127.0.0.1:5433:5432"
```

```yaml
ports:
  - "127.0.0.1:7474:7474"
  - "127.0.0.1:7687:7687"
```

이 설정은 외부 네트워크에서 데이터베이스에 직접 접근하는 것을 제한하기 위한 것이다.

운영 서버에 배포할 때는 별도의 방화벽, 인증, TLS 및 네트워크 접근 정책을 적용한다.

### 11.3 이미지 버전 관리

개발 환경의 재현성을 위해 Docker 이미지 태그를 고정한다.

현재 사용 버전:

| 구성 요소 | 이미지 |
|---|---|
| PostgreSQL + pgvector | `pgvector/pgvector:0.8.7-pg18-trixie` |
| Neo4j | `neo4j:5.26-community` |

이미지 버전 변경 시 다음 사항을 검토한다.

1. 기존 데이터 볼륨과의 호환성
2. 데이터베이스 업그레이드 경로
3. 백업 확보 여부
4. 애플리케이션 드라이버 호환성
5. 확장 기능 호환성
6. 기존 기능 테스트 결과

이미지 태그를 무조건 최신 버전으로 변경하지 않는다.

### 11.4 백업 관리

- [ ] PostgreSQL 정기 백업
- [ ] PostgreSQL 백업 파일 무결성 확인
- [ ] PostgreSQL 복구 절차 확인
- [ ] Neo4j 백업 방식 확인
- [ ] Neo4j 복구 절차 확인
- [ ] Docker 볼륨 삭제 방지
- [ ] 백업 파일 별도 보관

백업을 생성한 사실만으로 복구 가능성이 검증된 것은 아니다.

실제 복구 테스트를 통해 백업의 유효성을 확인한다.

## 12. 전체 설치 및 검증 체크리스트

### Docker 기본 환경

- [x] Docker Desktop 실행 확인
- [x] Docker Compose 사용 확인
- [x] 데이터베이스 포트 사용 여부 확인
- [x] Docker 작업 디렉터리 생성

### PostgreSQL 및 pgvector

- [x] PostgreSQL 이미지 다운로드
- [x] pgvector 이미지 다운로드
- [x] Docker Compose 설정 검증
- [x] PostgreSQL 컨테이너 실행
- [x] PostgreSQL 18.6 확인
- [x] PostgreSQL `pmis` DB 접속
- [x] 기존 데이터 볼륨 유지
- [x] 기존 데이터 백업
- [x] pgvector 확장 설치
- [x] pgvector 0.8.7 확인
- [x] 벡터 데이터 형식 확인
- [x] 벡터 거리 연산 확인

### Neo4j

- [x] Neo4j 이미지 다운로드
- [x] Docker Compose 설정 검증
- [x] Neo4j 컨테이너 실행
- [x] 데이터 볼륨 생성
- [x] 로그 볼륨 생성
- [x] Neo4j 5.26.33 확인
- [x] HTTP 접속 포트 확인
- [x] Bolt 접속 포트 확인
- [x] 초기 관리자 인증 확인
- [x] Cypher Shell 접속 확인
- [x] Cypher 쿼리 실행 확인

### 향후 PMIS 연동

- [ ] PostgreSQL 연결 설정 검증
- [ ] pgvector를 이용한 임베딩 저장 기능
- [ ] 벡터 유사도 검색 기능
- [ ] Neo4j Java Driver 연동
- [ ] Neo4j 연결 설정 및 인증정보 관리
- [ ] PMIS 노드 및 관계 모델 정의
- [ ] PostgreSQL과 Neo4j 간 데이터 동기화 설계
- [ ] 데이터베이스별 통합 테스트

## 13. 현재 집 PC 설치 결과

2026-11-11 기준 집 PC에서 확인한 설치 결과다.

| 항목 | 설치 버전 | 상태 |
|---|---|---|
| PostgreSQL | 18.6 | 정상 실행 |
| pgvector | 0.8.7 | 확장 설치 및 연산 확인 |
| Neo4j Community | 5.26.33 | 정상 실행 및 쿼리 확인 |
| PostgreSQL 컨테이너 | pmis-postgresql | healthy |
| Neo4j 컨테이너 | pmis-neo4j | Up |
| PostgreSQL 포트 | 5433 | 로컬 바인딩 |
| Neo4j HTTP | 7474 | 로컬 바인딩 |
| Neo4j Bolt | 7687 | 로컬 바인딩 |

### 13.1 실제 접속 정보

**PostgreSQL**

```text
Host: localhost
Port: 5433
Database: pmis
Username: pmis_admin
Image: pgvector/pgvector:0.8.7-pg18-trixie
```

**Neo4j**

```text
Browser: http://localhost:7474
Bolt: bolt://localhost:7687
Username: neo4j
Image: neo4j:5.26-community
```

비밀번호는 각 Docker 작업 디렉터리의 `.env`에서 관리한다.

### 13.2 최종 검증 결과

PostgreSQL:

```text
PostgreSQL 18.6
pgvector 0.8.7
CREATE EXTENSION 성공
벡터 거리 연산 성공
```

Neo4j:

```text
Neo4j 5.26.33
Bolt 연결 성공
HTTP 서비스 활성화
Cypher Shell 인증 성공
Cypher 쿼리 실행 성공
```

현재 두 데이터베이스는 Docker 기반으로 독립 실행되며, 데이터 저장소도 각각 별도의 Named Volume으로 관리한다.

## 14. 관련 문서

| 파일명 | 설명 |
|---|---|
| `001_development_environment_overview.md` | 개발 환경 개요 |
| `002_windows_setup.md` | Windows 설정 |
| `003_java_setup.md` | Java 설치 및 설정 |
| `004_nodejs_setup.md` | Node.js 설치 및 설정 |
| `005_database_setup.md` | PostgreSQL 및 MariaDB 설치와 계정 설정 |
| `006_vscode_setup.md` | VS Code 설치 및 설정 |
| `007_claude_code_setup.md` | Claude Code 설치 및 설정 |
| `008_git_github_setup.md` | Git 및 GitHub 설정 |
| `009_pmis_execution_guide.md` | PMIS 실행 가이드 |
| `010_environment_verification_and_troubleshooting.md` | 개발 환경 검증 및 문제 해결 |
| `011_docker_database_setup.md` | Docker 기반 PostgreSQL, pgvector 및 Neo4j 구축 |

---

**관리 원칙:** Docker 기반 데이터베이스는 애플리케이션 실행 환경과 데이터 저장소를 분리하여 관리한다. 컨테이너를 재생성하더라도 기존 데이터가 유지되도록 Named Volume을 사용하며, 데이터베이스별 백업 및 복구 절차를 마련한다. pgvector와 Neo4j는 PMIS의 검색 및 관계 분석 기능을 확장하기 위한 선택적 데이터 저장 기술로 도입하고, 실제 업무 활용성과 운영 비용을 검토한 후 애플리케이션 연동 범위를 결정한다.