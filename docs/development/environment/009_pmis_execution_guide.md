# PMIS 실행 가이드

- **문서 경로:** `docs/development/environment/009_pmis_execution_guide.md`
- **문서 버전:** 1.4
- **작성일:** 2026-11-10

---

## 1. 문서 목적

본 문서는 PMIS 프로젝트의 백엔드와 프론트엔드를 로컬 개발 환경에서 실행하는 방법을 설명한다.

PMIS는 다음과 같은 구조로 동작한다.

```text
사용자 브라우저
      |
      v
React + TypeScript + Vite
      |
      v
Spring Boot REST API
      |
      v
MariaDB 또는 PostgreSQL
```

각 구성 요소는 독립적으로 실행되며, 기본 실행 순서는 다음과 같다.

1. 데이터베이스 서비스 실행 및 접속 확인
2. 백엔드 환경변수 설정 및 Spring Boot 실행
3. 프론트엔드 환경 설정 및 Vite 실행
4. 브라우저에서 화면 및 API 통신 확인

PMIS 백엔드는 Spring Profile을 이용하여 실행 환경과 데이터베이스 종류를 독립적으로 선택한다.

- 실행 환경: `dev`, `prod`
- 데이터베이스: `mariadb`, `postgresql`

데이터베이스 연결 정보와 JWT 비밀키 등 민감한 정보는 `application.yaml`에 직접 기록하지 않고 프로젝트 로컬 `.env` 파일에서 관리한다.

## 2. 실행 환경

### 2.1 구성 요소 및 실행 주소

| 구성 요소 | 기술 | 실행 주소 또는 포트 |
|---|---|---|
| MariaDB | MariaDB | `localhost:3306` |
| PostgreSQL | PostgreSQL | `localhost:5432` |
| 백엔드 | Spring Boot | `http://localhost:8765` |
| API 문서 | Swagger UI | `http://localhost:8765/swagger-ui/index.html` |
| 프론트엔드 | React + Vite | `http://localhost:5173` |

MariaDB와 PostgreSQL은 선택적으로 사용한다. 두 DBMS를 동시에 실행할 필요는 없다.

### 2.2 프로젝트 디렉터리

```text
project-delivery-reference/
├─ scripts/
│  ├─ load-pmis-env.ps1
│  ├─ use-pmis-java21.ps1
│  └─ use-pmis-node24.ps1
├─ docs/
│  └─ development/
│     └─ environment/
├─ pmis-backend/
│  ├─ .env
│  ├─ .env.example
│  ├─ gradlew
│  ├─ build.gradle.kts
│  └─ src/
└─ pmis-frontend/
   ├─ package.json
   └─ package-lock.json
```

주요 환경설정 파일의 역할은 다음과 같다.

| 파일 | 역할 |
|---|---|
| `pmis-backend/.env` | 실제 로컬 환경변수 및 민감정보 저장 |
| `pmis-backend/.env.example` | 환경변수 항목 및 기본 설정 예시 |
| `scripts/load-pmis-env.ps1` | `.env` 파일을 현재 PowerShell 세션에 로딩 |
| `scripts/use-pmis-java21.ps1` | PMIS 전용 JDK 21 환경 활성화 |
| `scripts/use-pmis-node24.ps1` | PMIS 전용 Node.js 24 환경 활성화 |
| `application.yaml` | 공통 Spring Boot 설정 |
| `application-dev.yaml` | 개발 환경 JPA 및 로그 설정 |
| `application-prod.yaml` | 운영 환경 JPA 및 로그 설정 |
| `application-mariadb.yaml` | MariaDB 연결 설정 |
| `application-postgresql.yaml` | PostgreSQL 연결 설정 |

**주의사항**

- `.env` 파일은 `pmis-backend/` 디렉터리에 둔다.
- `.env` 파일은 Git에 커밋하지 않는다.
- `.env.example`을 참고하여 각 개발 PC에서 `.env`를 별도로 구성한다.
- 실제 비밀번호와 JWT Secret은 문서나 소스코드에 기록하지 않는다.

### 2.3 실행에 필요한 버전

| 구성 요소 | PMIS 개발 환경 |
|---|---|
| Java | JDK 21 |
| Gradle | 9.5.1 |
| Node.js | 24.19.0 |
| npm | 11.17.0 |
| MariaDB | 기존 PC 확인 버전 10.6.5 |
| PostgreSQL | 기존 PC 확인 버전 18.6 |
| 백엔드 포트 | 8765 |
| 프론트엔드 포트 | 5173 |
| MariaDB 포트 | 3306 |
| PostgreSQL 포트 | 5432 |

신규 PC에서는 기존 PC의 DBMS 버전을 무조건 그대로 설치하기보다 지원 상태와 프로젝트 호환성을 확인한다.

## 3. Spring Profile 및 데이터베이스 선택

### 3.1 프로필 구성 원칙

Spring Profile은 두 가지 차원으로 구성한다.

| 구분 | 프로필 | 역할 |
|---|---|---|
| 실행 환경 | `dev` | 개발 환경 |
| 실행 환경 | `prod` | 운영 및 운영 검증 환경 |
| DB 종류 | `mariadb` | MariaDB 연결 |
| DB 종류 | `postgresql` | PostgreSQL 연결 |

두 종류의 프로필을 조합하여 실행한다.

| 조합 | DB 계정 | Hibernate DDL | 용도 |
|---|---|---|---|
| `dev,mariadb` | MariaDB 개발 계정 | update | MariaDB 개발 |
| `prod,mariadb` | MariaDB 운영 계정 | validate | MariaDB 운영 검증 |
| `dev,postgresql` | `pmis_migrator` | update | PostgreSQL 개발 |
| `prod,postgresql` | `pmis_app` | validate | PostgreSQL 운영 검증 |

현재 PostgreSQL의 개발 및 운영 프로필은 실제 기동을 확인했다.

MariaDB 프로필은 설정과 계정 준비 후 별도로 실행 검증해야 한다.

### 3.2 프로필 선택

PowerShell에서 다음과 같이 실행 환경과 DB 종류를 지정한다.

PostgreSQL 개발:

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev,postgresql"
```

PostgreSQL 운영 검증:

```powershell
$env:SPRING_PROFILES_ACTIVE = "prod,postgresql"
```

MariaDB 개발:

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev,mariadb"
```

MariaDB 운영 검증:

```powershell
$env:SPRING_PROFILES_ACTIVE = "prod,mariadb"
```

프로필을 별도로 지정하지 않으면 현재 `application.yaml`의 기본 프로필을 사용한다.

현재 기본 프로필:

```yaml
spring:
  profiles:
    default: dev,postgresql
```

### 3.3 실행 환경별 JPA 설정

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

**개발 환경**

`ddl-auto: update`를 사용한다.

개발 중 Entity 및 JPA 매핑 변경에 따라 Hibernate가 데이터베이스 스키마를 생성하거나 갱신할 수 있다.

**운영 환경**

`ddl-auto: validate`를 사용한다.

기존 DB 스키마와 Entity 매핑의 일치 여부를 확인하며 테이블이나 컬럼을 자동 생성하거나 변경하지 않는다.

운영 DB 스키마 변경은 별도 마이그레이션 절차를 통해 관리한다.

## 4. 실행 전 확인 사항

### 4.1 프로젝트 저장소 이동

PowerShell을 실행하고 프로젝트 저장소로 이동한다.

```powershell
cd C:\SeoS\project-delivery-reference
```

현재 Git 브랜치와 변경 사항을 확인한다.

```powershell
git branch --show-current
git status
```

### 4.2 데이터베이스 실행 확인

사용할 DBMS에 따라 해당 서비스와 포트를 확인한다.

**MariaDB 서비스 확인**

```powershell
Get-Service | Where-Object {
    $_.Name -match "MariaDB|MySQL"
}
```

MariaDB 포트 확인:

```powershell
Get-NetTCPConnection -LocalPort 3306 -State Listen
```

기존 개발 PC에서 확인한 MariaDB 설치 경로:

```text
C:\Program Files\MariaDB 10.6
```

MariaDB 접속 예시:

```powershell
& "C:\Program Files\MariaDB 10.6\bin\mariadb.exe" -u root -p -e "SHOW DATABASES;"
```

신규 PC에서는 실제 설치 경로와 계정에 맞게 실행한다.

**PostgreSQL 서비스 확인**

```powershell
Get-Service | Where-Object {
    $_.Name -match "postgres"
}
```

PostgreSQL 포트 확인:

```powershell
Get-NetTCPConnection -LocalPort 5432 -State Listen
```

기존 개발 PC에서 확인한 PostgreSQL 설치 경로:

```text
C:\Program Files\PostgreSQL\18
```

PostgreSQL 접속 예시:

```powershell
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -h localhost -p 5432 -d postgres
```

비밀번호는 CLI 프롬프트에서 입력한다.

### 4.3 데이터베이스 및 테이블 확인

사용할 DBMS에 `pmis` 데이터베이스가 존재하는지 확인한다.

데이터베이스가 없다면 `005_database_setup.md`의 데이터베이스 생성 절차를 먼저 진행한다.

데이터베이스 생성과 JPA 테이블 생성은 별개의 작업이다.

신규 PC에서 테이블 16개를 SQL로 직접 생성하도록 안내하지 않는다. Entity 매핑과 Hibernate DDL 설정에 따라 필요한 테이블을 생성하거나 검증한다.

### 4.4 환경설정 파일 확인

백엔드 실행 전에 다음 파일이 준비되어 있는지 확인한다.

```text
pmis-backend/.env
pmis-backend/.env.example
scripts/load-pmis-env.ps1
```

`.env.example`은 환경변수 구성 예시다.

실제 실행에는 `pmis-backend/.env`가 필요하다.

신규 PC에서는 `.env.example`을 참고하여 `.env`를 만들고 실제 DB 비밀번호 및 JWT Secret을 설정한다.

실제 비밀정보는 Git에 포함하거나 외부에 공유하지 않는다.

## 5. 백엔드 실행

백엔드는 Spring Boot와 Gradle Wrapper를 사용한다.

### 5.1 PMIS 환경변수 로딩

새 PowerShell 터미널을 열고 프로젝트 루트로 이동한다.

```powershell
cd C:\SeoS\project-delivery-reference
```

먼저 사용할 Spring Profile을 지정한다.

예시: PostgreSQL 개발 환경

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev,postgresql"
```

이후 환경변수 로딩 스크립트를 실행한다.

```powershell
..\scripts\load-pmis-env.ps1
```

위 명령은 `pmis-backend` 디렉터리에서 실행하는 경우의 상대 경로다.

프로젝트 루트에서 실행할 경우 다음 명령을 사용한다.

```powershell
.\scripts\load-pmis-env.ps1
```

스크립트는 `pmis-backend/.env`를 읽어 현재 PowerShell 프로세스에 환경변수를 설정한다.

다음 작업을 수행한다.

1. `.env` 파일 로딩
2. 기존 DB 연결 관련 환경변수 초기화
3. 활성 Spring Profile 확인
4. 실행 환경과 DB 종류 구분
5. 해당 환경의 DB 계정 정보 선택
6. 공통 DB 환경변수 설정
7. 필수 환경변수 설정 여부 확인

정상적으로 로딩되면 다음과 유사한 메시지가 출력된다.

```text
PMIS environment variables loaded successfully.
Active profiles: dev, postgresql
Environment: dev
Database: postgresql
Database username: pmis_migrator
Hibernate DDL mode: update
```

운영 검증 환경의 출력 예시:

```text
PMIS environment variables loaded successfully.
Active profiles: prod, postgresql
Environment: prod
Database: postgresql
Database username: pmis_app
Hibernate DDL mode: validate
```

**주의사항**

- 프로필을 먼저 지정하고 환경변수 로딩 스크립트를 실행한다.
- 새 PowerShell 터미널을 열 때마다 환경변수를 다시 로딩한다.
- `.env` 수정 후에도 환경변수를 다시 로딩해야 한다.
- 환경변수 로딩은 현재 PowerShell 세션에 적용된다.
- Windows 시스템 전체의 환경변수를 변경하지 않는다.
- 비밀번호 및 JWT Secret의 실제 값은 출력하거나 공유하지 않는다.

### 5.2 환경변수 구성

`.env` 파일은 다음과 같은 구조로 관리한다.

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

위 내용은 변수 구성 예시다. 실제 비밀번호와 JWT Secret은 로컬 환경에서 별도로 입력한다.

DB 계정 정책:

| DBMS | 개발 계정 | 운영 계정 |
|---|---|---|
| MariaDB | 기존 PC root 또는 신규 PC 개발 계정 | `pmis_app` |
| PostgreSQL | `pmis_migrator` | `pmis_app` |

MariaDB의 기존 PC root 계정은 기존 환경에서 확인된 설정이다. 신규 PC에서는 005 문서의 전용 계정 정책을 따른다.

PostgreSQL의 `pmis_app`은 애플리케이션 CRUD 권한을 사용하며 스키마 CREATE 권한을 갖지 않는다.

### 5.3 공통 DB 환경변수 확인

백엔드 DB 연결 설정에서는 다음 공통 변수를 사용한다.

| 환경변수 | 용도 |
|---|---|
| `PMIS_DB_USERNAME` | 현재 프로필에서 선택된 DB 사용자 |
| `PMIS_DB_PASSWORD` | 현재 프로필에서 선택된 DB 비밀번호 |
| `JWT_SECRET` | JWT 서명 비밀키 |
| `RUNTIME_API_BASE_URL` | Automation Runtime 주소 |
| `RUNTIME_API_KEY` | Runtime API 인증 키 |
| `SERVER_PORT` | 백엔드 포트 |

`PMIS_DB_USERNAME`과 `PMIS_DB_PASSWORD`는 `load-pmis-env.ps1`이 DB 종류 및 실행 환경에 따라 선택한다.

DB 연결 설정은 다음 파일에서 관리한다.

- `application-mariadb.yaml`
- `application-postgresql.yaml`

따라서 기존 `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`를 직접 설정하는 방식과 혼동하지 않는다.

비밀번호 및 JWT Secret 확인 시에는 값 자체가 아니라 설정 여부만 확인한다.

```powershell
Write-Host "DB Username 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:PMIS_DB_USERNAME))"
Write-Host "DB Password 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:PMIS_DB_PASSWORD))"
Write-Host "JWT Secret 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:JWT_SECRET))"
```

### 5.4 새 JWT_SECRET 생성

새로운 개발환경을 구성하거나 JWT_SECRET을 교체해야 하는 경우 PowerShell에서 다음 명령을 실행한다.

```powershell
$rng = [Security.Cryptography.RandomNumberGenerator]::Create()
try {
    $bytes = New-Object byte[] 64
    $rng.GetBytes($bytes)
    $jwtSecret = [Convert]::ToBase64String($bytes)
}
finally {
    $rng.Dispose()
}

$jwtSecret.Length
Set-Clipboard -Value $jwtSecret
```

- 암호학적으로 안전한 난수 64바이트를 생성한다.
- Base64 인코딩 결과는 88자다.
- 생성된 값은 클립보드에 복사된다.
- `.env`의 `JWT_SECRET`에 저장한다.
- JWT_SECRET은 외부에 공개하거나 Git 저장소에 커밋하지 않는다.
- 기존 JWT_SECRET 교체 시 기존 토큰이 더 이상 검증되지 않을 수 있으므로 개발환경의 인증 상태를 확인한다.

### 5.5 JDK 21 활성화

PMIS 전용 JDK 21 환경을 활성화한다.

```powershell
. .\scripts\use-pmis-java21.ps1
```

Java 버전을 확인한다.

```powershell
java -version
javac -version
```

현재 개발 PC에서 확인한 JDK:

```text
Java 21.0.13
```

이 스크립트는 현재 PowerShell 세션에만 적용한다.

다른 프로젝트에서 사용하는 시스템 기본 JDK 17 설정은 변경하지 않는다.

### 5.6 Gradle 환경 확인

프로젝트 루트에서 Gradle Wrapper 버전을 확인한다.

```powershell
.\pmis-backend\gradlew.bat --version
```

확인 항목:

- Gradle: 9.5.1
- Launcher JVM: JDK 21
- Daemon JVM: JDK 21

Gradle Wrapper를 사용하므로 별도의 시스템 Gradle 설치는 필요하지 않다.

### 5.7 백엔드 실행

프로젝트 루트에서 다음 순서로 실행한다.

```powershell
cd C:\SeoS\project-delivery-reference

$env:SPRING_PROFILES_ACTIVE = "dev,postgresql"

.\scripts\load-pmis-env.ps1

. .\scripts\use-pmis-java21.ps1

cd .\pmis-backend

.\gradlew.bat bootRun
```

위 예시는 PostgreSQL 개발 환경이다.

MariaDB 개발 환경을 실행하려면 프로필 지정 부분을 다음과 같이 변경한다.

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev,mariadb"
```

PostgreSQL 운영 검증 환경:

```powershell
$env:SPRING_PROFILES_ACTIVE = "prod,postgresql"
```

운영 검증 환경은 실제 운영 서버가 아닌 로컬 개발 PC에서 설정과 권한을 검증하는 용도로 사용할 수 있다.

정상 기동 시 다음과 유사한 메시지가 출력된다.

```text
Tomcat started on port 8765 (http)
Started PmisApplication
```

백엔드 실행 과정:

1. Spring Boot 애플리케이션 초기화
2. 활성 Spring Profile 확인
3. `.env` 기반 DB 연결 설정 로딩
4. 데이터베이스 연결
5. JPA 초기화 및 Entity 매핑 처리
6. 개발 환경에서는 필요한 경우 스키마 갱신
7. 운영 환경에서는 스키마 검증
8. 웹 서버 시작

애플리케이션 시작 메시지만으로 모든 API와 데이터베이스 기능이 정상 동작한다고 판단하지 않는다. 실제 API 호출과 데이터 처리 결과도 확인한다.

### 5.8 기존 DB 스키마 변경 없이 실행

개발 환경에서 기존 DB 스키마를 변경하지 않고 애플리케이션 기동만 확인해야 하는 경우 다음과 같이 실행할 수 있다.

```powershell
.\gradlew.bat bootRun --args="--spring.jpa.hibernate.ddl-auto=none"
```

이 옵션은 해당 실행에만 적용되며 `application-dev.yaml`의 기본 설정을 변경하지 않는다.

단, 애플리케이션 초기화 과정에서 실행되는 별도 데이터 초기화 코드나 SQL은 이 설정과 별개로 동작할 수 있다.

### 5.9 백엔드 빌드 및 테스트

백엔드 디렉터리에서 다음 명령어로 빌드와 테스트를 수행한다.

```powershell
.\gradlew.bat clean build
```

빌드 성공 시 다음 메시지를 확인한다.

```text
BUILD SUCCESSFUL
```

테스트 실패가 발생하면 실패한 태스크와 실제 오류 로그를 확인한다.

`bootRun` 실패와 `test` 태스크 실패는 구분해서 기록한다.

`clean build`는 기존 빌드 결과물을 삭제한 후 다시 빌드한다. 실행 중인 애플리케이션이 있다면 먼저 종료하는 것을 권장한다.

## 6. 프론트엔드 실행

프론트엔드는 React, TypeScript, Vite 기반으로 구성되어 있다.

### 6.1 Node.js 24 활성화

새 PowerShell 터미널을 열고 프로젝트 루트로 이동한다.

```powershell
cd C:\SeoS\project-delivery-reference
```

PMIS 전용 Node.js 24 환경을 활성화한다.

```powershell
. .\scripts\use-pmis-node24.ps1
```

Node.js와 npm 버전을 확인한다.

```powershell
node -v
npm -v
```

기존 개발 PC에서 확인한 버전:

```text
v24.19.0
11.17.0
```

이 스크립트 역시 현재 PowerShell 세션에만 적용한다.

시스템 기본 Node.js 20 설정은 변경하지 않는다.

### 6.2 의존성 설치

새 PC에서 처음 실행하거나 의존성을 다시 설치해야 하는 경우 프로젝트 루트에서 실행한다.

```powershell
npm ci --prefix .\pmis-frontend
```

`package-lock.json`을 기준으로 의존성을 설치한다.

이미 `node_modules`가 정상적으로 설치되어 있다면 매번 실행할 필요는 없다.

### 6.3 프론트엔드 실행

프로젝트 루트에서 실행한다.

```powershell
npm run dev --prefix .\pmis-frontend
```

정상적으로 실행되면 Vite 개발 서버 주소가 출력된다.

```text
http://localhost:5173
```

브라우저에서 해당 주소에 접속한다.

프론트엔드를 종료하려면 해당 PowerShell 터미널에서 `Ctrl + C`를 누른다.

### 6.4 프론트엔드 빌드

배포용 빌드가 정상적으로 수행되는지 확인하려면 다음 명령어를 실행한다.

```powershell
npm run build --prefix .\pmis-frontend
```

빌드 결과물:

```text
pmis-frontend/dist/
```

### 6.5 프론트엔드 코드 검사

ESLint 검사를 실행한다.

```powershell
npm run lint --prefix .\pmis-frontend
```

기존 개발 PC에서 확인된 프론트엔드 코드에는 ESLint 오류와 경고가 존재한다.

기존 확인 결과:

- 오류: 29건
- 경고: 3건

검사 결과가 실패하더라도 Node.js 설치 문제로 단정하지 않는다.

다음 항목을 구분한다.

- Node.js 및 npm 설치 문제
- ESLint 설정 문제
- 실제 소스 코드 품질 문제

프론트엔드 빌드 성공 여부와 ESLint 검사 결과는 별도로 관리한다.

## 7. 전체 시스템 실행 순서

PMIS를 실행할 때는 다음 순서를 권장한다.

### 7.1 터미널 1: 데이터베이스

사용할 DBMS의 서비스 실행 여부와 `pmis` 데이터베이스 존재 여부를 확인한다.

PostgreSQL을 사용하는 경우:

- PostgreSQL 서비스 실행 확인
- `5432` 포트 확인
- `pmis` 데이터베이스 확인

MariaDB를 사용하는 경우:

- MariaDB 서비스 실행 확인
- `3306` 포트 확인
- `pmis` 데이터베이스 확인

신규 PC에서는 `005_database_setup.md`의 절차에 따라 데이터베이스와 전용 계정을 먼저 준비한다.

### 7.2 터미널 2: 백엔드

새 PowerShell 터미널을 열고 다음 순서대로 실행한다.

PostgreSQL 개발 환경 예시:

```powershell
cd C:\SeoS\project-delivery-reference

$env:SPRING_PROFILES_ACTIVE = "dev,postgresql"

.\scripts\load-pmis-env.ps1

. .\scripts\use-pmis-java21.ps1

cd .\pmis-backend

.\gradlew.bat bootRun
```

백엔드가 정상적으로 실행된 상태를 유지한다.

데이터베이스 연결과 JPA 초기화가 정상적으로 완료되었는지 확인한다.

운영 검증 환경을 테스트할 때는 `dev,postgresql`을 `prod,postgresql`로 변경한다.

### 7.3 터미널 3: 프론트엔드

새 PowerShell 터미널을 열고 다음 순서대로 실행한다.

```powershell
cd C:\SeoS\project-delivery-reference

. .\scripts\use-pmis-node24.ps1

npm run dev --prefix .\pmis-frontend
```

프론트엔드가 정상적으로 실행된 상태를 유지한다.

### 7.4 브라우저 접속

프론트엔드:

```text
http://localhost:5173
```

백엔드 API 문서:

```text
http://localhost:8765/swagger-ui/index.html
```

백엔드 기본 주소:

```text
http://localhost:8765
```

프론트엔드와 백엔드는 별도의 개발 서버로 실행된다.

브라우저에서 프론트엔드 화면이 표시되는 것과 백엔드 API 통신이 성공하는 것은 별도로 확인해야 한다.

## 8. 실행 확인

### 8.1 백엔드 기동 확인

백엔드 로그에서 다음 메시지를 확인한다.

```text
Tomcat started on port 8765 (http)
Started PmisApplication
```

정상 기동 여부는 실제 애플리케이션 시작 메시지를 기준으로 판단한다.

### 8.2 Swagger UI 접속 확인

브라우저에서 다음 주소로 접속한다.

```text
http://localhost:8765/swagger-ui/index.html
```

Swagger UI가 정상적으로 표시되는지 확인한다.

API 테스트 시에는 각 API의 인증 요구 사항과 요청 형식에 맞게 실행한다.

### 8.3 API 호출 확인

Swagger UI에서 인증이 필요한 API를 테스트하는 경우 다음을 확인한다.

- JWT 발급 성공 여부
- Swagger Authorize 적용 여부
- API 요청 성공 여부
- HTTP 응답 상태
- JSON 응답 구조
- 실제 데이터 조회 결과

예를 들어 프로젝트 대시보드 API를 호출할 수 있다.

```text
GET http://localhost:8765/api/projects/dashboard
```

정상 응답 예시:

```json
{
  "success": true,
  "code": "SUCCESS",
  "data": {
    "totalProjects": 0,
    "projectStatusCount": {
      "PLANNING": 0,
      "IN_PROGRESS": 0,
      "ON_HOLD": 0,
      "COMPLETED": 0
    },
    "projectPriorityCount": {
      "LOW": 0,
      "MEDIUM": 0,
      "HIGH": 0
    },
    "recentProjects": 0,
    "upcomingDeadlineProjects": 0
  }
}
```

위 결과는 프로젝트 데이터가 없는 상태에서의 응답 예시다. 실제 응답은 DB 데이터와 API 구현 상태에 따라 달라진다.

### 8.4 데이터베이스 확인

**MariaDB**

```sql
USE pmis;

SHOW TABLES;
```

**PostgreSQL**

```sql
SELECT tablename
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY tablename;
```

JPA 초기화 이후 필요한 테이블이 생성되었는지 확인한다.

기존 테이블 목록 및 데이터베이스 구성은 `005_database_setup.md`를 참고한다.

### 8.5 프론트엔드 확인

브라우저에서 다음 주소에 접속한다.

```text
http://localhost:5173
```

다음 항목을 확인한다.

- PMIS 화면 표시
- 사이드바 및 메뉴 표시
- 대시보드 화면 접근
- 브라우저 개발자 도구의 Console 오류 확인
- Network 탭의 API 호출 결과 확인
- API 응답 데이터가 화면에 정상 반영되는지 확인

### 8.6 포트 사용 여부 확인

백엔드:

```powershell
Get-NetTCPConnection -LocalPort 8765 -State Listen
```

프론트엔드:

```powershell
Get-NetTCPConnection -LocalPort 5173 -State Listen
```

MariaDB:

```powershell
Get-NetTCPConnection -LocalPort 3306 -State Listen
```

PostgreSQL:

```powershell
Get-NetTCPConnection -LocalPort 5432 -State Listen
```

### 8.7 전체 실행 확인 체크리스트

- [ ] 사용할 DBMS 서비스 실행 확인
- [ ] `pmis` 데이터베이스 접속 확인
- [ ] `.env` 파일 존재 확인
- [ ] Spring Profile 지정 확인
- [ ] 환경변수 로딩 확인
- [ ] DB 계정 선택 결과 확인
- [ ] JDK 21 활성화 확인
- [ ] Gradle 9.5.1 확인
- [ ] Spring Boot 백엔드 실행 확인
- [ ] 데이터베이스 연결 및 JPA 초기화 확인
- [ ] Swagger UI 접속 확인
- [ ] 실제 API 호출 확인
- [ ] Node.js 24 활성화 확인
- [ ] npm 의존성 설치 확인
- [ ] Vite 개발 서버 실행 확인
- [ ] 브라우저에서 PMIS 화면 확인
- [ ] 프론트엔드와 백엔드 간 API 통신 확인

## 9. 문제 해결

### 9.1 백엔드 실행 시 데이터베이스 연결 오류

오류 예시:

```text
Connection refused
```

확인 사항:

1. 선택한 DBMS 서비스 실행 여부
2. MariaDB `3306` 또는 PostgreSQL `5432` 포트
3. `pmis` 데이터베이스 존재 여부
4. JDBC URL
5. 활성 Spring Profile
6. DB 사용자 및 비밀번호
7. DB 계정의 권한

MariaDB와 PostgreSQL 중 현재 어떤 DBMS 프로필을 사용하고 있는지 먼저 확인한다.

### 9.2 환경변수 로딩 오류

다음 메시지가 출력되면 `.env` 파일을 확인한다.

```text
PMIS .env file not found.
```

확인 사항:

1. 프로젝트 루트에서 스크립트를 실행했는지 확인
2. `pmis-backend/.env` 파일이 존재하는지 확인
3. 파일명이 `.env`인지 확인
4. `.env.example`만 있고 실제 `.env` 파일이 누락된 것은 아닌지 확인

환경변수 재로딩:

```powershell
cd C:\SeoS\project-delivery-reference

.\scripts\load-pmis-env.ps1
```

### 9.3 필수 환경변수 누락 오류

예시:

```text
JWT_SECRET is missing in PMIS environment.
```

확인 사항:

1. `.env` 파일에 해당 항목이 존재하는지 확인
2. 환경변수 값이 비어 있지 않은지 확인
3. `.env` 파일을 수정했다면 환경변수를 다시 로딩했는지 확인
4. 현재 PowerShell 세션에서 올바른 프로필을 지정했는지 확인

DB 계정 설정 여부 확인:

```powershell
Write-Host "DB Username 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:PMIS_DB_USERNAME))"
Write-Host "DB Password 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:PMIS_DB_PASSWORD))"
Write-Host "JWT Secret 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:JWT_SECRET))"
```

비밀번호와 JWT Secret의 실제 값을 화면에 출력하지 않는다.

### 9.4 DB 계정이 잘못 선택되는 경우

다음 사항을 확인한다.

1. `SPRING_PROFILES_ACTIVE`가 올바르게 지정되었는지 확인
2. 프로필 지정 후 `load-pmis-env.ps1`을 실행했는지 확인
3. 스크립트 출력의 Environment 확인
4. 스크립트 출력의 Database 확인
5. Database username 확인

예를 들어 PostgreSQL 개발 환경에서는 다음과 같이 출력되어야 한다.

```text
Environment: dev
Database: postgresql
Database username: pmis_migrator
Hibernate DDL mode: update
```

운영 검증 환경에서는 다음과 같이 출력되어야 한다.

```text
Environment: prod
Database: postgresql
Database username: pmis_app
Hibernate DDL mode: validate
```

기존 PowerShell 세션에 남아 있는 DB 연결 환경변수가 새 프로필 선택에 영향을 주지 않도록 환경변수 로딩 스크립트를 사용한다.

### 9.5 PostgreSQL 테이블 권한 오류

오류 예시:

```text
permission denied for table users
```

확인 사항:

1. 실제 DB 접속 계정
2. 해당 계정의 테이블 CRUD 권한
3. `public` 스키마 USAGE 권한
4. 시퀀스 USAGE 및 SELECT 권한
5. 실제 테이블 소유자
6. 기본 권한 설정

운영 계정 `pmis_app`은 애플리케이션 실행에 필요한 CRUD 권한을 사용한다.

개발 중 스키마 변경 오류가 발생하면 개발 계정 `pmis_migrator`가 선택되었는지 확인한다.

### 9.6 PostgreSQL CREATE 권한 오류 또는 DDL 실행 실패

운영 환경의 `pmis_app`은 `public` 스키마 CREATE 권한이 없는 것이 정상이다.

다음 명령으로 권한을 확인한다.

```sql
SELECT has_schema_privilege('pmis_app', 'public', 'USAGE') AS can_use, has_schema_privilege('pmis_app', 'public', 'CREATE') AS can_create;
```

운영 환경의 기대 결과:

```text
can_use | can_create
--------+-----------
t       | f
```

개발 환경에서 Entity 변경에 따른 DDL 작업이 필요하면 `dev,postgresql` 프로필과 `pmis_migrator` 계정을 사용한다.

운영 애플리케이션 계정에 CREATE 권한을 추가하여 오류를 우회하지 않는다.

### 9.7 JPA 테이블 생성 또는 검증 실패

확인 사항:

1. 활성 Spring Profile
2. `ddl-auto` 설정
3. DB 연결 성공 여부
4. Entity 매핑
5. DB 계정 권한
6. 스키마 접근 권한
7. 실제 DB 객체 소유자 및 권한
8. 백엔드 실행 로그

`update`는 개발 중 스키마 변경을 지원한다.

`validate`는 스키마와 Entity 매핑의 일치 여부를 검증하며 테이블을 생성하지 않는다.

데이터베이스 자체가 생성되었다고 해서 테이블까지 생성된 것은 아니다.

### 9.8 Java 버전 오류

현재 PowerShell의 Java 버전을 확인한다.

```powershell
java -version
javac -version
```

JDK 17이 표시된다면 PMIS 전용 환경을 다시 활성화한다.

```powershell
. .\scripts\use-pmis-java21.ps1
```

### 9.9 Node.js 버전 오류

```powershell
node -v
npm -v
```

Node.js 20이 표시된다면 PMIS 전용 환경을 다시 활성화한다.

```powershell
. .\scripts\use-pmis-node24.ps1
```

### 9.10 포트 사용 중 오류

백엔드:

```powershell
Get-NetTCPConnection -LocalPort 8765 -State Listen
```

프론트엔드:

```powershell
Get-NetTCPConnection -LocalPort 5173 -State Listen
```

MariaDB:

```powershell
Get-NetTCPConnection -LocalPort 3306 -State Listen
```

PostgreSQL:

```powershell
Get-NetTCPConnection -LocalPort 5432 -State Listen
```

프로세스를 확인한 후 기존 실행 인스턴스가 있는지 점검한다.

원인을 확인하지 않고 다른 프로세스를 강제로 종료하지 않는다.

### 9.11 프론트엔드에서 백엔드 API 호출 실패

확인 사항:

1. 백엔드가 정상적으로 실행 중인지 확인
2. 브라우저 개발자 도구의 Network 탭 확인
3. API 요청 URL 확인
4. CORS 설정 확인
5. JWT 인증 및 권한 오류 여부 확인
6. 프론트엔드 API 기본 주소 설정 확인

프론트엔드의 API 기본 주소 설정은 실제 프로젝트 코드와 환경 설정을 확인한 후 변경한다.

### 9.12 GitHub SSH 인증 실패

증상:

```text
Permission denied (publickey)
```

확인 사항:

1. SSH 개인 키 파일 존재 여부
2. 공개 키가 올바른 GitHub 계정에 등록되어 있는지
3. SSH config의 Host 별칭 확인
4. `IdentityFile` 경로 확인
5. 원격 저장소 주소 확인

포트폴리오 계정 인증 테스트:

```powershell
ssh -T git@github-portfolio
```

개인 계정 인증 테스트:

```powershell
ssh -T git@github-personal
```

### 9.13 Git LFS 파일 다운로드 실패

확인:

```powershell
git lfs version
git lfs pull
git lfs status
```

Git LFS가 정상 설치되었는지, GitHub 계정에 접근 권한이 있는지, 저장 용량 및 다운로드 한도에 문제가 없는지 확인한다.

GitHub Releases 등 별도 배포 경로를 사용하는 경우에는 해당 배포 위치에서 파일을 직접 확보한다.

### 9.14 ESLint 오류 발생

ESLint 오류가 발생하면 다음 사항을 구분한다.

- Node.js 및 npm 설치 문제
- ESLint 설정 문제
- 실제 소스 코드 품질 문제

프론트엔드 빌드 성공 여부와 ESLint 검사 결과는 별도의 검증 항목이다.

## 10. 개발 시 유의사항

- 데이터베이스, 백엔드, 프론트엔드는 각각 별도 터미널에서 실행한다.
- PMIS 백엔드에는 JDK 21을 사용한다.
- PMIS 프론트엔드에는 Node.js 24를 사용한다.
- 다른 프로젝트의 시스템 기본 Java 17 및 Node.js 20 설정은 유지한다.
- 백엔드 실행 전 Spring Profile을 지정한다.
- 프로필 지정 후 `.env` 환경변수를 로딩한다.
- 새 PowerShell 터미널을 열 때마다 필요한 환경설정 스크립트를 실행한다.
- 개발 환경은 `ddl-auto: update`를 사용한다.
- 운영 환경은 `ddl-auto: validate`를 사용한다.
- 운영 애플리케이션 계정에는 불필요한 DDL 권한을 부여하지 않는다.
- `.env` 파일은 로컬에서만 관리하고 Git에 커밋하지 않는다.
- `.env.example`은 환경변수 구성 예시로 저장소에서 관리한다.
- 백엔드와 프론트엔드 실행 로그를 구분해서 확인한다.
- 코드 수정 후에는 빌드 및 테스트 결과를 확인한다.
- DB 비밀번호, API Key, JWT 비밀키 등 인증 정보는 저장소에 노출하지 않는다.
- 개발 서버와 운영 서버의 설정을 혼동하지 않는다.
- 신규 PC에서는 `005_database_setup.md`의 데이터베이스 구축 절차를 먼저 완료한다.

## 11. 관련 문서

| 파일명 | 설명 |
|---|---|
| `001_development_environment_overview.md` | 개발 환경 개요 |
| `002_windows_setup.md` | Windows 설정 |
| `003_java_setup.md` | Java 설치 및 설정 |
| `004_nodejs_setup.md` | Node.js 설치 및 설정 |
| `005_database_setup.md` | MariaDB 및 PostgreSQL 설치와 계정 정책 |
| `006_vscode_setup.md` | VS Code 설치 및 설정 |
| `007_claude_code_setup.md` | Claude Code 설치 및 설정 |
| `008_git_github_setup.md` | Git 및 GitHub 설정 |
| `010_environment_verification_and_troubleshooting.md` | 환경 검증 및 문제 해결 |

---

**보안 및 운영 원칙:** 데이터베이스 비밀번호, API Key, JWT 비밀키 등 인증 정보는 소스코드, 문서, Git 커밋에 평문으로 포함하지 않는다. 개발 중 스키마 변경 권한과 운영 애플리케이션의 CRUD 권한을 분리한다. 운영 애플리케이션 계정에는 불필요한 DDL 권한을 부여하지 않으며, 운영 DB 스키마 변경은 별도 검토 및 마이그레이션 절차를 통해 수행한다.