# PMIS 데이터베이스 구축 가이드

- **문서 경로:** `docs/development/environment/005_database_setup.md`
- **문서 버전:** 1.3
- **작성일:** 2026-11-10
- **지원 DBMS:** MariaDB / PostgreSQL
- **데이터베이스명:** `pmis`
- **MariaDB 기본 포트:** 3306
- **PostgreSQL 기본 포트:** 5432

---

## 1. 목적

본 문서는 PMIS 백엔드에서 사용하는 데이터베이스 환경을 구축하고 연결하는 방법을 설명한다.

PMIS는 MariaDB와 PostgreSQL을 지원하며, Spring Profile을 이용해 데이터베이스 종류와 실행 환경을 독립적으로 선택한다.

주요 내용은 다음과 같다.

- MariaDB 및 PostgreSQL 설치와 접속 확인
- PMIS 데이터베이스 생성
- 개발 및 운영 DB 계정 정책
- 데이터베이스 권한 분리
- Spring Boot 데이터베이스 연결 설정
- JPA 테이블 생성 및 검증 정책
- 데이터베이스 백업 및 복원
- 데이터베이스 연결 문제 해결

## 2. 데이터베이스 구성

### 2.1 지원 데이터베이스

| 항목 | MariaDB | PostgreSQL |
|---|---|---|
| 데이터베이스명 | `pmis` | `pmis` |
| 기본 포트 | 3306 | 5432 |
| JDBC Driver | `org.mariadb.jdbc.Driver` | `org.postgresql.Driver` |
| JDBC URL | `jdbc:mariadb://localhost:3306/pmis` | `jdbc:postgresql://localhost:5432/pmis` |
| 개발 프로필 | `dev,mariadb` | `dev,postgresql` |
| 운영 프로필 | `prod,mariadb` | `prod,postgresql` |

데이터베이스 종류와 실행 환경은 서로 독립적으로 선택한다.

예를 들어 다음과 같이 사용할 수 있다.

- `dev,postgresql`: PostgreSQL 개발 환경
- `prod,postgresql`: PostgreSQL 운영 검증 환경
- `dev,mariadb`: MariaDB 개발 환경
- `prod,mariadb`: MariaDB 운영 검증 환경

### 2.2 기존 개발 PC의 MariaDB 설치 현황

| 항목 | 확인 결과 |
|---|---|
| 데이터베이스 | MariaDB |
| 버전 | 10.6.5 |
| 설치 경로 | `C:\Program Files\MariaDB 10.6` |
| CLI 실행 파일 | `bin\mariadb.exe` |
| 데이터베이스명 | `pmis` |
| 접속 주소 | `localhost` |
| 접속 포트 | 3306 |
| 기존 접속 계정 | root |

기존 개발 PC에서는 MariaDB와 MySQL Windows 서비스가 모두 실행 중인 것을 확인했다.

※ MariaDB 10.6.5는 기존 PC에서 확인한 환경이다. 신규 PC에서는 설치 시점에 지원 중인 MariaDB LTS 버전을 검토한다.

### 2.3 PostgreSQL 설치 현황

| 항목 | 확인 결과 |
|---|---|
| 데이터베이스 | PostgreSQL |
| 버전 | 18.6 |
| 설치 형태 | Windows Native |
| 설치 경로 | `C:\Program Files\PostgreSQL\18` |
| CLI 실행 파일 | `bin\psql.exe` |
| 데이터베이스명 | `pmis` |
| 접속 주소 | `localhost` |
| 접속 포트 | 5432 |

PostgreSQL 설치 및 서비스 상태는 실제 설치 경로와 서비스명을 기준으로 확인한다.

## 3. 데이터베이스 설치 및 접속 확인

### 3.1 MariaDB 설치 확인

기존 개발 PC의 MariaDB CLI 경로를 기준으로 다음 명령어를 실행한다.

```powershell
& "C:\Program Files\MariaDB 10.6\bin\mariadb.exe" --version
```

서비스 확인:

```powershell
Get-Service | Where-Object {
    $_.Name -match "MariaDB|MySQL"
}
```

포트 확인:

```powershell
Get-NetTCPConnection -LocalPort 3306 -State Listen
```

신규 PC에서는 실제 서비스명과 설치 경로를 확인한 후 명령어를 적용한다.

### 3.2 PostgreSQL 설치 확인

설치 버전 확인:

```powershell
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" --version
```

서비스 확인:

```powershell
Get-Service | Where-Object {
    $_.Name -match "postgres"
}
```

포트 확인:

```powershell
Get-NetTCPConnection -LocalPort 5432 -State Listen
```

PostgreSQL 접속 예시:

```powershell
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -h localhost -p 5432 -d postgres
```

비밀번호 입력은 CLI 프롬프트에서 진행한다. 비밀번호를 명령어에 직접 입력하지 않는다.

## 4. PMIS 데이터베이스 생성

### 4.1 MariaDB 데이터베이스 생성

먼저 관리자 계정으로 MariaDB에 접속한다.

데이터베이스 존재 여부 확인:

```sql
SHOW DATABASES LIKE 'pmis';
```

데이터베이스가 없는 경우에만 생성한다.

```sql
CREATE DATABASE pmis
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

문자셋 확인:

```sql
SELECT SCHEMA_NAME, DEFAULT_CHARACTER_SET_NAME, DEFAULT_COLLATION_NAME
FROM information_schema.SCHEMATA
WHERE SCHEMA_NAME = 'pmis';
```

### 4.2 PostgreSQL 데이터베이스 생성

먼저 관리자 계정으로 PostgreSQL에 접속한다.

데이터베이스 존재 여부 확인:

```sql
SELECT datname FROM pg_database WHERE datname = 'pmis';
```

데이터베이스가 없는 경우에만 생성한다.

```sql
CREATE DATABASE pmis;
```

데이터베이스 소유권은 별도의 소유자 역할을 사용하는 것을 권장한다.

신규 데이터베이스를 생성할 때는 다음과 같이 지정할 수 있다.

```sql
CREATE DATABASE pmis OWNER pmis_owner;
```

이미 데이터베이스가 존재한다면 재생성하지 않는다.

## 5. 데이터베이스 계정 및 권한 정책

### 5.1 계정 분리 원칙

PMIS는 개발 중 스키마 변경 권한과 애플리케이션 실행 권한을 분리한다.

| 역할 | 용도 | 주요 권한 |
|---|---|---|
| Owner | DB 객체 소유 및 권한 관리 | 객체 소유 및 권한 관리 |
| Migrator | 개발 및 스키마 변경 | DDL 및 개발 작업 |
| App | 애플리케이션 실행 | CRUD |
| Readonly | 조회 및 분석 | SELECT |

개발 계정과 운영 애플리케이션 계정을 구분한다.

- 개발 환경: `ddl-auto: update`
- 운영 환경: `ddl-auto: validate`
- 운영 애플리케이션 계정에는 CREATE 권한을 부여하지 않는다.
- 스키마 변경은 개발 또는 별도 마이그레이션 작업으로 수행한다.

### 5.2 MariaDB 계정 정책

기존 개발 PC에서는 root 계정을 사용하고 있다.

| 환경 | 계정 | 용도 |
|---|---|---|
| 기존 개발 PC | `root` | 기존 개발 환경 |
| 신규 개발 PC | `pmis_migrator` 권장 | 개발 중 스키마 변경 |
| 운영 실행 | `pmis_app` | 애플리케이션 CRUD |

신규 개발 PC에서 전용 개발 계정을 생성하는 예시:

```sql
CREATE USER 'pmis_migrator'@'localhost' IDENTIFIED BY '별도 비밀번호';
GRANT ALL PRIVILEGES ON pmis.* TO 'pmis_migrator'@'localhost';
```

운영 애플리케이션 계정 생성 예시:

```sql
CREATE USER 'pmis_app'@'localhost' IDENTIFIED BY '별도 비밀번호';
GRANT SELECT, INSERT, UPDATE, DELETE ON pmis.* TO 'pmis_app'@'localhost';
```

권한 확인:

```sql
SHOW GRANTS FOR 'pmis_migrator'@'localhost';
SHOW GRANTS FOR 'pmis_app'@'localhost';
```

주의 사항:

- 기존 PC의 root 설정을 신규 PC의 권장 설정으로 일반화하지 않는다.
- 신규 PC에서는 전용 계정을 사용한다.
- `pmis_app`에는 운영 중 테이블 생성 및 변경 권한을 부여하지 않는다.
- 계정이 이미 존재하면 무조건 재생성하지 말고 기존 권한을 확인한다.
- 위 계정 생성 예시는 신규 구축용이다. 기존 운영 DB에 무조건 실행하지 않는다.

### 5.3 PostgreSQL 계정 정책

PostgreSQL에서는 역할(Role)을 이용해 객체 소유권과 로그인 계정을 분리한다.

| 역할 | 로그인 | 용도 |
|---|---|---|
| `pmis_owner` | NOLOGIN | 데이터베이스 객체 소유 및 권한 관리 |
| `pmis_migrator` | LOGIN | 개발 및 마이그레이션 |
| `pmis_app` | LOGIN | 애플리케이션 CRUD |
| `pmis_readonly` | LOGIN | 조회 전용 |

개발 계정은 Owner 역할의 구성원으로 관리한다.

```sql
GRANT pmis_owner TO pmis_migrator;
```

현재 개발 환경에서는 `pmis_migrator`가 개발 중 테이블을 생성하고 변경하는 계정으로 사용된다.

기존 객체의 소유자가 `pmis_migrator`로 생성된 경우도 있으므로, 실제 객체 소유권은 다음 명령으로 확인한다.

```sql
SELECT schemaname, tablename, tableowner
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY tablename;
```

장기적으로는 마이그레이션 작업에서 `SET ROLE pmis_owner`를 사용하여 객체 소유권을 Owner 역할로 일관되게 관리하는 방식을 권장한다.

### 5.4 PostgreSQL 애플리케이션 권한

`pmis_app`은 애플리케이션 실행에 필요한 권한만 갖도록 한다.

기존 테이블 CRUD 권한:

```sql
GRANT SELECT, INSERT, UPDATE, DELETE
ON ALL TABLES IN SCHEMA public
TO pmis_app;
```

기존 시퀀스 권한:

```sql
GRANT USAGE, SELECT
ON ALL SEQUENCES IN SCHEMA public
TO pmis_app;
```

스키마 접근 권한:

```sql
GRANT USAGE
ON SCHEMA public
TO pmis_app;
```

운영 애플리케이션 계정의 CREATE 권한 제거:

```sql
REVOKE CREATE
ON SCHEMA public
FROM pmis_app;
```

권한 확인:

```sql
SELECT has_schema_privilege('pmis_app', 'public', 'USAGE') AS can_use,
       has_schema_privilege('pmis_app', 'public', 'CREATE') AS can_create;
```

정상적인 운영 권한 분리 결과:

| 권한 | 기대 결과 |
|---|---|
| USAGE | true |
| CREATE | false |

테이블 CRUD 권한 확인:

```sql
SELECT grantee, privilege_type
FROM information_schema.role_table_grants
WHERE table_schema = 'public'
  AND grantee = 'pmis_app'
ORDER BY grantee, privilege_type;
```

### 5.5 PostgreSQL 신규 객체 기본 권한

향후 개발 계정이 생성하는 테이블에도 애플리케이션 권한이 적용되도록 기본 권한을 설정한다.

현재 객체 생성 주체가 `pmis_migrator`인 경우:

```sql
ALTER DEFAULT PRIVILEGES
FOR ROLE pmis_migrator
IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE
ON TABLES TO pmis_app;
```

시퀀스 기본 권한:

```sql
ALTER DEFAULT PRIVILEGES
FOR ROLE pmis_migrator
IN SCHEMA public
GRANT USAGE, SELECT
ON SEQUENCES TO pmis_app;
```

Owner 역할로 객체를 생성하는 경우에는 `pmis_owner`에 대한 기본 권한도 별도로 설정해야 한다.

```sql
ALTER DEFAULT PRIVILEGES
FOR ROLE pmis_owner
IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE
ON TABLES TO pmis_app;
```

```sql
ALTER DEFAULT PRIVILEGES
FOR ROLE pmis_owner
IN SCHEMA public
GRANT USAGE, SELECT
ON SEQUENCES TO pmis_app;
```

기본 권한은 객체를 생성하는 역할에 따라 적용된다. 따라서 실제 DDL 실행 계정과 `SET ROLE` 사용 여부를 확인해야 한다.

## 6. Spring Boot 데이터베이스 연결 설정

### 6.1 설정 파일 구성

PMIS 백엔드의 DB 연결 설정 파일은 다음과 같이 분리한다.

```text
pmis-backend/
└── src/
    └── main/
        └── resources/
            ├── application.yaml
            ├── application-dev.yaml
            ├── application-prod.yaml
            ├── application-mariadb.yaml
            └── application-postgresql.yaml
```

환경 프로필과 데이터베이스 프로필은 독립적으로 선택한다.

### 6.2 기본 프로필

`application.yaml`:

```yaml
spring:
  application:
    name: pmis

  profiles:
    default: dev,postgresql
```

별도 환경변수가 없을 때 기본 실행 환경은 `dev,postgresql`이다.

실행 환경은 다음 환경변수로 변경할 수 있다.

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev,postgresql"
```

운영 검증 환경:

```powershell
$env:SPRING_PROFILES_ACTIVE = "prod,postgresql"
```

### 6.3 개발 및 운영 JPA 설정

`application-dev.yaml`:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.orm.jdbc.bind: TRACE
```

`application-prod.yaml`:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

logging:
  level:
    org.hibernate.SQL: WARN
    org.hibernate.orm.jdbc.bind: WARN
```

| 항목 | 개발 | 운영 |
|---|---|---|
| `ddl-auto` | update | validate |
| SQL 출력 | true | false |
| SQL 로그 | DEBUG | WARN |
| DB 계정 | Migrator | App |

`update`는 개발 중 Entity 변경에 따라 스키마를 갱신할 수 있다.

`validate`는 기존 스키마와 Entity 매핑의 일치 여부를 검증하며, 테이블을 자동 생성하거나 변경하지 않는다.

운영 환경에서 `pmis_app`을 사용하면 애플리케이션이 스키마를 직접 변경하지 않도록 권한과 설정을 함께 통제한다.

### 6.4 MariaDB 연결 설정

`application-mariadb.yaml`:

```yaml
spring:
  datasource:
    url: ${PMIS_MARIADB_URL:jdbc:mariadb://localhost:3306/pmis}
    username: ${PMIS_DB_USERNAME}
    password: ${PMIS_DB_PASSWORD}
    driver-class-name: org.mariadb.jdbc.Driver
```

### 6.5 PostgreSQL 연결 설정

`application-postgresql.yaml`:

```yaml
spring:
  datasource:
    url: ${PMIS_POSTGRESQL_URL:jdbc:postgresql://localhost:5432/pmis}
    username: ${PMIS_DB_USERNAME}
    password: ${PMIS_DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
```

### 6.6 환경변수 관리

프로젝트 루트의 `.env` 파일에서 데이터베이스별, 환경별 접속 정보를 관리한다.

```dotenv
# MariaDB
PMIS_MARIADB_URL=jdbc:mariadb://localhost:3306/pmis
PMIS_MARIADB_DEV_USERNAME=root
PMIS_MARIADB_DEV_PASSWORD=
PMIS_MARIADB_PROD_USERNAME=pmis_app
PMIS_MARIADB_PROD_PASSWORD=

# PostgreSQL
PMIS_POSTGRESQL_URL=jdbc:postgresql://localhost:5432/pmis
PMIS_POSTGRESQL_DEV_USERNAME=pmis_migrator
PMIS_POSTGRESQL_DEV_PASSWORD=
PMIS_POSTGRESQL_PROD_USERNAME=pmis_app
PMIS_POSTGRESQL_PROD_PASSWORD=

# Application
JWT_SECRET=
RUNTIME_API_BASE_URL=http://127.0.0.1:8000
RUNTIME_API_KEY=
SERVER_PORT=8765
```

위 값은 변수 구성 예시다. 실제 비밀번호와 JWT Secret은 로컬 환경에서 별도로 설정한다.

`load-pmis-env.ps1` 스크립트가 활성 프로필을 확인하고 해당 DB 및 환경에 맞는 계정 정보를 `PMIS_DB_USERNAME`, `PMIS_DB_PASSWORD`로 전달한다.

비밀번호 값은 콘솔이나 문서에 출력하지 않는다.

환경변수 로딩 절차는 009 PMIS 실행 가이드를 참고한다.

## 7. JPA 테이블 생성 및 검증

### 7.1 데이터베이스와 테이블 생성의 차이

데이터베이스 생성과 테이블 생성은 별도 작업이다.

1. MariaDB 또는 PostgreSQL 서버 설치
2. `pmis` 데이터베이스 생성
3. 데이터베이스 계정 및 권한 설정
4. Spring Boot 데이터베이스 연결
5. JPA Entity 및 DDL 설정에 따른 테이블 생성 또는 검증

신규 PC에서 16개 테이블을 SQL로 직접 생성하도록 안내하지 않는다.

테이블 생성 및 변경 여부는 다음 항목에 영향을 받는다.

- `ddl-auto` 설정
- Entity 매핑
- 데이터베이스 연결 계정
- 데이터베이스 객체 및 스키마 권한
- 기존 테이블과 Entity의 상태

### 7.2 개발 환경

개발 환경에서는 `dev` 프로필과 Migrator 계정을 사용한다.

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev,postgresql"
..\scripts\load-pmis-env.ps1
```

정상적인 PostgreSQL 개발 환경 출력 예시:

```text
Active profiles: dev, postgresql
Environment: dev
Database: postgresql
Database username: pmis_migrator
Hibernate DDL mode: update
```

### 7.3 운영 검증 환경

운영 검증 환경에서는 `prod` 프로필과 App 계정을 사용한다.

```powershell
$env:SPRING_PROFILES_ACTIVE = "prod,postgresql"
..\scripts\load-pmis-env.ps1
```

정상적인 PostgreSQL 운영 환경 출력 예시:

```text
Active profiles: prod, postgresql
Environment: prod
Database: postgresql
Database username: pmis_app
Hibernate DDL mode: validate
```

### 7.4 테이블 확인

MariaDB:

```sql
SHOW TABLES;
```

PostgreSQL:

```sql
SELECT tablename
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY tablename;
```

기존 테이블 수와 신규 환경의 테이블 수가 다를 경우 Entity 및 JPA 설정을 기준으로 확인한다.

## 8. 데이터베이스 백업 및 복원

### 8.1 MariaDB 백업

```powershell
& "C:\Program Files\MariaDB 10.6\bin\mariadb-dump.exe" `
    -u root -p `
    --result-file="pmis_backup.sql" `
    pmis
```

신규 PC에서는 실제 설치 경로와 백업 권한을 확인한 후 실행한다.

### 8.2 MariaDB 복원

```powershell
& "C:\Program Files\MariaDB 10.6\bin\mariadb.exe" `
    -u root -p pmis
```

MariaDB 접속 후:

```sql
SOURCE C:/backup/pmis_backup.sql;
```

### 8.3 PostgreSQL 백업

PostgreSQL의 `pg_dump`를 사용한다.

```powershell
& "C:\Program Files\PostgreSQL\18\bin\pg_dump.exe" `
    -U postgres `
    -h localhost `
    -p 5432 `
    -d pmis `
    -F c `
    -f "pmis_backup.dump"
```

### 8.4 PostgreSQL 복원

복원 대상 데이터베이스를 준비한 후 다음 명령을 실행한다.

```powershell
& "C:\Program Files\PostgreSQL\18\bin\pg_restore.exe" `
    -U postgres `
    -h localhost `
    -p 5432 `
    -d pmis `
    "pmis_backup.dump"
```

복원 작업은 대상 데이터베이스와 기존 데이터에 영향을 줄 수 있으므로 사전에 백업 및 복원 범위를 확인한다.

### 8.5 백업 및 복원 확인 사항

- 백업 파일 생성 여부
- 백업 파일 크기
- 데이터베이스 종류 및 버전
- 복원 대상 데이터베이스
- 계정 및 권한
- 테이블과 데이터 건수
- 복원 후 데이터 조회 가능 여부

실제 데이터가 포함된 백업 파일은 Git 저장소에 무분별하게 커밋하지 않는다.

## 9. 문제 해결

### 9.1 데이터베이스 접속 실패

오류 예시:

```text
Connection refused
```

확인 사항:

1. DB 서비스 실행 여부
2. 접속 주소 및 포트
3. 방화벽 및 포트 접근 여부
4. JDBC URL 설정
5. 활성 Spring Profile

### 9.2 Access denied 또는 권한 오류

MariaDB:

```text
Access denied for user
```

PostgreSQL:

```text
permission denied for table
```

확인 사항:

1. DB 사용자명과 비밀번호
2. 실제 활성 프로필
3. 해당 계정의 데이터베이스 접근 권한
4. 스키마 USAGE 권한
5. 테이블 CRUD 권한
6. 시퀀스 사용 권한
7. 객체 생성 주체와 기본 권한 설정

PostgreSQL에서 `pmis_app`의 CREATE 권한이 없는 것은 운영 환경의 정상적인 보안 정책이다.

개발 중 스키마 변경 오류가 발생하면 운영 계정 대신 Migrator 계정이 선택되었는지 확인한다.

### 9.3 JPA 테이블이 생성되지 않는 경우

다음 항목을 확인한다.

1. Spring Profile
2. `ddl-auto` 설정
3. DB 연결 성공 여부
4. Entity 매핑
5. DB 계정 권한
6. 스키마 접근 권한
7. 백엔드 실행 로그

`validate` 환경에서는 테이블이 자동 생성되지 않는다.

### 9.4 포트 충돌

MariaDB:

```powershell
Get-NetTCPConnection -LocalPort 3306 -State Listen
```

PostgreSQL:

```powershell
Get-NetTCPConnection -LocalPort 5432 -State Listen
```

기존 DB 서비스의 설정을 확인하지 않고 임의로 중지하거나 포트를 변경하지 않는다.

## 10. 확인 완료 기준

### MariaDB

- [ ] MariaDB 설치 및 버전 확인
- [ ] MariaDB 서비스 실행 확인
- [ ] 3306 포트 확인
- [ ] `pmis` 데이터베이스 존재 확인
- [ ] 문자셋 및 Collation 확인
- [ ] 개발 계정 권한 확인
- [ ] 운영 App 계정 권한 확인
- [ ] Spring Boot 연결 확인
- [ ] JPA 테이블 생성 및 검증 확인

### PostgreSQL

- [ ] PostgreSQL 설치 및 버전 확인
- [ ] PostgreSQL 서비스 실행 확인
- [ ] 5432 포트 확인
- [ ] `pmis` 데이터베이스 존재 확인
- [ ] Owner 역할 확인
- [ ] Migrator 계정 확인
- [ ] App 계정 확인
- [ ] Readonly 계정 확인
- [ ] App 테이블 CRUD 권한 확인
- [ ] App 시퀀스 권한 확인
- [ ] App 스키마 USAGE 권한 확인
- [ ] App 스키마 CREATE 권한 없음 확인
- [ ] 개발 프로필 `update` 기동 확인
- [ ] 운영 프로필 `validate` 기동 확인

---

**보안 원칙:** 데이터베이스 비밀번호와 JWT Secret은 소스코드, 문서, Git 커밋에 평문으로 포함하지 않는다. 개발 중 스키마 변경 권한과 운영 애플리케이션의 CRUD 권한을 분리하며, 운영 애플리케이션 계정에는 불필요한 DDL 권한을 부여하지 않는다.