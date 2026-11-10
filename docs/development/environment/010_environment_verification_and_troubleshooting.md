# PMIS 개발 환경 검증 및 문제 해결

- **문서 경로:** `docs/development/environment/010_environment_verification_and_troubleshooting.md`
- **문서 버전:** 1.3
- **작성일:** 2026-11-10

---

## 1. 문서 목적

이 문서는 PMIS 개발 환경의 설치 상태, 환경 설정, 데이터베이스 연결, 백엔드 및 프론트엔드 실행 상태를 검증하고 주요 오류의 원인과 해결 방법을 정리한다.

단순히 개발 도구가 설치되어 있는지만 확인하는 것이 아니라, 실제 PMIS 프로젝트를 빌드하고 실행할 수 있는지까지 검증하는 것을 목적으로 한다.

검증 대상은 다음과 같다.

- Windows 및 PowerShell
- Java JDK 17 / 21
- Gradle
- Node.js 20 / 24
- PostgreSQL
- MariaDB
- VS Code 및 Claude Code
- Git 및 GitHub SSH 인증
- Git LFS
- 환경변수 및 민감정보 관리
- PMIS 백엔드
- PMIS 프론트엔드
- 프론트엔드와 백엔드 간 API 통신

검증 결과는 다음 기준으로 구분한다.

| 구분 | 의미 |
|---|---|
| 설치 확인 | 프로그램 또는 개발 도구가 설치되어 있음 |
| 환경 확인 | 지정된 버전과 설정을 사용할 수 있음 |
| 연결 확인 | 데이터베이스 및 외부 구성 요소에 연결할 수 있음 |
| 실행 확인 | 프로그램 또는 서버가 정상적으로 실행됨 |
| 기능 확인 | 실제 API 호출 및 데이터 처리가 정상 동작함 |

## 2. 전체 검증 순서

다음 순서로 검증한다.

```text
1. Windows / PowerShell
          |
          v
2. 프로젝트 경로 및 필수 파일
          |
          v
3. Java / Gradle
          |
          v
4. Node.js / npm
          |
          v
5. PostgreSQL / MariaDB
          |
          v
6. VS Code / Claude Code
          |
          v
7. Git / GitHub SSH / Git LFS
          |
          v
8. 환경변수 및 민감정보
          |
          v
9. PMIS 백엔드 빌드 및 실행
          |
          v
10. PMIS 프론트엔드 빌드 및 실행
          |
          v
11. 전체 시스템 통합 확인
```

## 3. 운영체제 및 프로젝트 경로 검증

### 3.1 Windows 버전 확인

PowerShell에서 실행한다.

```powershell
Get-ComputerInfo | Select-Object WindowsProductName, WindowsVersion, OsBuildNumber
```

기존 개발 PC에서 확인한 환경:

| 항목 | 확인 결과 |
|---|---|
| 운영체제 | Windows 10 Pro |
| Windows Version | 2009 |
| OS Build | 19045 |

다른 PC에서는 Windows 버전과 빌드 번호가 다를 수 있다.

### 3.2 PowerShell 버전 확인

```powershell
$PSVersionTable
```

기존 개발 PC에서 확인한 버전은 Windows PowerShell 5.1이다.

본 문서의 PowerShell 명령어는 Windows PowerShell 5.1을 기준으로 작성한다.

### 3.3 프로젝트 디렉터리 확인

프로젝트 루트로 이동한다.

```powershell
cd C:\SeoS\project-delivery-reference
```

현재 경로를 확인한다.

```powershell
Get-Location
```

주요 디렉터리를 확인한다.

```powershell
Get-ChildItem -Directory
```

주요 프로젝트 구성:

```text
project-delivery-reference/
├── docs/
├── pmis/
├── pmis-backend/
├── pmis-frontend/
├── scripts/
├── Tools/
└── ...
```

프로젝트 루트 경로는 PC 환경에 따라 달라질 수 있다.

### 3.4 프로젝트 필수 파일 확인

프로젝트 루트에서 실행한다.

```powershell
Test-Path .\scripts\load-pmis-env.ps1
Test-Path .\scripts\use-pmis-java21.ps1
Test-Path .\scripts\use-pmis-node24.ps1
Test-Path .\pmis-backend\gradlew.bat
Test-Path .\pmis-backend\build.gradle.kts
Test-Path .\pmis-backend\application.yaml
Test-Path .\pmis-backend\.env
Test-Path .\pmis-backend\.env.example
Test-Path .\pmis-frontend\package.json
Test-Path .\pmis-frontend\package-lock.json
```

기존 개발 PC에서는 실제 `.env` 파일이 존재해야 한다.

신규 PC에서는 `.env.example`을 참고하여 `.env` 파일을 별도로 생성한다.

실제 `.env` 파일은 Git에 포함하지 않는다.

## 4. Java 및 Gradle 검증

### 4.1 시스템 기본 Java 확인

새 PowerShell 터미널에서 실행한다.

```powershell
$env:JAVA_HOME
java -version
javac -version
```

기존 개발 PC의 시스템 기본 JDK:

| 항목 | 버전 |
|---|---|
| Java | 17 |
| javac | 17.0.13 |

다른 프로젝트와의 호환성을 위해 시스템 기본 JDK 17 설정은 유지한다.

PMIS 실행을 위해 시스템 전역 Java 설정을 변경하지 않는다.

### 4.2 PMIS 전용 JDK 21 활성화

프로젝트 루트에서 실행한다.

```powershell
. .\scripts\use-pmis-java21.ps1
```

버전을 확인한다.

```powershell
java -version
javac -version
```

현재 확인된 PMIS 전용 환경:

| 항목 | 버전 |
|---|---|
| Java | 21.0.13 |
| JDK | 21 |

이 스크립트는 현재 PowerShell 세션에만 적용된다.

다른 프로젝트에서 사용하는 시스템 기본 JDK 17 설정은 변경하지 않는다.

### 4.3 Gradle 버전 확인

프로젝트 루트에서 실행한다.

```powershell
.\pmis-backend\gradlew.bat --version
```

현재 확인된 환경:

| 항목 | 버전 |
|---|---|
| Gradle Wrapper | 9.5.1 |
| Java | JDK 21 |

Gradle Wrapper를 사용하므로 별도의 시스템 Gradle 설치는 필요하지 않다.

### 4.4 백엔드 빌드 검증

프로젝트 루트에서 실행한다.

```powershell
.\pmis-backend\gradlew.bat -p .\pmis-backend clean build
```

정상 결과:

```text
BUILD SUCCESSFUL
```

빌드 실패 시 다음 항목을 구분하여 확인한다.

- Java 버전 불일치
- Gradle 의존성 다운로드 실패
- 컴파일 오류
- 테스트 실패
- 데이터베이스 연결 또는 Spring Context 초기화 실패

`clean build`는 기존 빌드 결과물을 삭제한 후 다시 빌드한다.

## 5. Node.js 및 npm 검증

### 5.1 시스템 기본 Node.js 확인

새 PowerShell 터미널에서 실행한다.

```powershell
node -v
npm -v
```

기존 개발 PC의 시스템 기본 환경:

| 항목 | 버전 |
|---|---|
| Node.js | 20.12.2 |
| npm | 10.5.0 |

### 5.2 PMIS 전용 Node.js 24 활성화

프로젝트 루트에서 실행한다.

```powershell
. .\scripts\use-pmis-node24.ps1
```

버전을 확인한다.

```powershell
node -v
npm -v
```

현재 확인된 PMIS 전용 환경:

| 항목 | 버전 |
|---|---|
| Node.js | 24.19.0 |
| npm | 11.17.0 |

이 스크립트는 현재 PowerShell 세션에만 적용된다.

시스템 기본 Node.js 20 설정은 변경하지 않는다.

### 5.3 프론트엔드 의존성 확인

프로젝트 루트에서 실행한다.

```powershell
Test-Path .\pmis-frontend\node_modules
Test-Path .\pmis-frontend\package-lock.json
```

의존성이 설치되어 있지 않거나 재설치가 필요한 경우:

```powershell
npm ci --prefix .\pmis-frontend
```

`npm ci`는 `package-lock.json`을 기준으로 의존성을 설치한다.

### 5.4 프론트엔드 빌드 검증

```powershell
npm run build --prefix .\pmis-frontend
```

정상적으로 완료되면 다음 경로에 빌드 결과물이 생성된다.

```text
pmis-frontend/dist/
```

빌드 결과 확인:

```powershell
Test-Path .\pmis-frontend\dist
```

정상적으로 빌드되었다면 `True`가 반환된다.

### 5.5 프론트엔드 코드 검사

ESLint 검사를 실행한다.

```powershell
npm run lint --prefix .\pmis-frontend
```

ESLint 검사 결과는 Node.js 설치 여부와 별개의 항목이다.

검사 실패 시 다음 항목을 구분한다.

- Node.js 및 npm 버전 문제
- 패키지 의존성 문제
- ESLint 설정 문제
- 실제 소스 코드 오류 및 경고

기존 개발 PC에서 확인한 오류 및 경고 건수는 당시 소스 기준의 기록이며, 현재 결과와 다를 수 있다.

## 6. 데이터베이스 검증

PMIS는 PostgreSQL과 MariaDB를 각각 데이터베이스 프로파일로 선택할 수 있도록 구성한다.

환경 프로파일과 데이터베이스 프로파일은 독립적으로 선택한다.

| 구분 | 프로파일 | 용도 |
|---|---|---|
| 개발 환경 | `dev` | 개발 및 스키마 변경 확인 |
| 운영 설정 검증 | `prod` | 런타임 권한 및 스키마 검증 |
| PostgreSQL | `postgresql` | PostgreSQL 연결 |
| MariaDB | `mariadb` | MariaDB 연결 |

### 6.1 데이터베이스 포트 확인

| 데이터베이스 | 기본 포트 |
|---|---:|
| PostgreSQL | 5432 |
| MariaDB | 3306 |

PowerShell에서 포트 연결 상태를 확인한다.

PostgreSQL:

```powershell
Test-NetConnection localhost -Port 5432
```

MariaDB:

```powershell
Test-NetConnection localhost -Port 3306
```

`TcpTestSucceeded`가 `True`이면 해당 포트로 TCP 연결이 가능하다는 의미다.

포트 연결 성공만으로 데이터베이스 인증 및 SQL 실행까지 정상이라고 판단하지 않는다.

### 6.2 PostgreSQL 서비스 확인

PostgreSQL 서비스 이름을 확인한다.

```powershell
Get-Service | Where-Object { $_.Name -match "postgres" }
```

설치된 서비스의 상태를 확인한다.

```powershell
Get-Service | Where-Object { $_.Name -match "postgres" } | Format-Table Name, Status
```

PostgreSQL 클라이언트 버전을 확인한다.

```powershell
psql --version
```

`psql` 명령을 찾을 수 없다면 PostgreSQL 설치 경로의 `bin` 디렉터리가 PATH에 등록되어 있는지 확인한다.

### 6.3 PostgreSQL 데이터베이스 접속 확인

PostgreSQL 관리자 계정 또는 허용된 접속 계정으로 접속한다.

```powershell
psql -h localhost -p 5432 -U postgres -d pmis
```

접속 후 다음 SQL을 실행한다.

```sql
SELECT current_database(), current_user;
```

테이블 목록을 확인한다.

```sql
\dt
```

스키마 권한 확인은 권한 점검용 계정으로 실행한다.

```sql
SELECT
    has_schema_privilege('pmis_app', 'public', 'USAGE') AS has_usage,
    has_schema_privilege('pmis_app', 'public', 'CREATE') AS has_create;
```

현재 확인된 PostgreSQL 런타임 계정 정책:

| 계정 | 용도 | 권한 정책 |
|---|---|---|
| `pmis_owner` | 스키마 소유 및 권한 기준 | NOLOGIN |
| `pmis_migrator` | 개발 및 스키마 변경 | 개발용 변경 권한 |
| `pmis_app` | 애플리케이션 실행 | 런타임 CRUD |
| `pmis_readonly` | 조회 전용 | 읽기 권한 |

현재 PostgreSQL의 `pmis_app`은 `public` 스키마의 USAGE 권한을 가지고 있으며 CREATE 권한은 제한되어 있다.

기존 테이블에 대한 CRUD 권한과 시퀀스 사용 권한도 별도로 확인한다.

계정 생성, 권한 부여 및 기본 권한 설정에 관한 상세 내용은 다음 문서를 참고한다.

`005_database_setup.md`

### 6.4 MariaDB 서비스 확인

설치된 MariaDB 및 MySQL 서비스를 확인한다.

```powershell
Get-Service | Where-Object { $_.Name -match "MariaDB|MySQL" }
```

기존 개발 PC의 MariaDB 설치 경로:

```powershell
& "C:\Program Files\MariaDB 10.6\bin\mariadb.exe" --version
```

기존 개발 PC에서 확인된 버전:

```text
MariaDB 10.6.5
```

신규 PC에서는 실제 설치 경로와 버전을 확인한다.

### 6.5 MariaDB 데이터베이스 접속 확인

기존 개발 PC에서 관리자 계정으로 접속하는 예시:

```powershell
& "C:\Program Files\MariaDB 10.6\bin\mariadb.exe" -u root -p
```

비밀번호를 입력한 후 다음 SQL을 실행한다.

```sql
SELECT VERSION(), DATABASE(), CURRENT_USER();
```

PMIS 데이터베이스를 확인한다.

```sql
SHOW DATABASES;
```

데이터베이스에 접속한다.

```sql
USE pmis;
```

테이블 목록을 확인한다.

```sql
SHOW TABLES;
```

기존 개발 PC의 `root` 계정 사용 방식과 신규 PC의 전용 계정 사용 방식은 구분한다.

신규 PC에서는 `005_database_setup.md`의 계정 및 권한 정책을 따른다.

### 6.6 데이터베이스 생성과 테이블 생성 구분

다음 두 작업은 서로 다르다.

| 구분 | 설명 |
|---|---|
| 데이터베이스 생성 | PostgreSQL 또는 MariaDB에 `pmis` 데이터베이스 생성 |
| 테이블 생성 및 변경 | JPA Entity 매핑 및 개발용 스키마 관리 정책에 따른 테이블 생성 또는 변경 |

개발 환경에서는 `dev` 프로파일의 `ddl-auto: update` 설정에 따라 JPA Entity를 기준으로 테이블 생성 및 변경이 수행될 수 있다.

따라서 테이블 16개를 수동으로 각각 생성하는 방식으로 환경을 구축하지 않는다.

실제 테이블 구성은 현재 Entity 및 데이터베이스 스키마를 기준으로 확인한다.

운영 설정 검증 환경에서는 `prod` 프로파일의 `ddl-auto: validate`를 사용한다. 이 설정은 스키마를 자동 변경하지 않고 Entity와 기존 데이터베이스 스키마의 호환성을 검증한다.

### 6.7 데이터베이스 연결 실패 시 확인 항목

다음 순서로 확인한다.

1. 데이터베이스 서비스 실행 여부
2. 데이터베이스 포트 사용 여부
3. `pmis` 데이터베이스 존재 여부
4. JDBC URL 및 포트
5. 데이터베이스 계정
6. 비밀번호 설정 여부
7. 스키마 및 테이블 권한
8. 현재 선택된 Spring 프로파일
9. 환경변수 로딩 여부

## 7. VS Code 및 Claude Code 검증

### 7.1 VS Code 버전 확인

```powershell
code --version
```

기존 개발 PC에서 확인한 버전:

```text
1.145.1
```

`code` 명령을 찾을 수 없다면 VS Code 설치 여부와 PATH 설정을 확인한다.

### 7.2 확장 프로그램 확인

```powershell
code --list-extensions --show-versions
```

PMIS 주요 확장 프로그램:

| 확장 프로그램 | ID |
|---|---|
| Extension Pack for Java | `vscjava.vscode-java-pack` |
| Gradle for Java | `vscjava.vscode-gradle` |
| Spring Boot Extension Pack | `vmware.vscode-boot-dev-pack` |
| ESLint | `dbaeumer.vscode-eslint` |
| Prettier | `esbenp.prettier-vscode` |
| Claude Code | `anthropic.claude-code` |

현재 설치된 버전과 다른 PC의 버전은 달라질 수 있다.

신규 PC에서는 `006_vscode_setup.md`를 참고한다.

### 7.3 Claude Code 확인

다음 항목을 각각 확인한다.

- [ ] Claude Code 확장 프로그램 설치
- [ ] 확장 프로그램 실행
- [ ] 계정 인증
- [ ] 실제 서비스 사용 권한

확장 프로그램 설치만으로 서비스 사용이 보장되는 것은 아니다.

계정 인증과 서비스 사용 권한은 별도로 확인한다.

## 8. Git 및 GitHub 검증

### 8.1 Git 버전 확인

```powershell
git --version
```

기존 개발 PC에서 확인한 버전:

```text
git version 2.34.0.windows.1
```

### 8.2 Git 사용자 정보 확인

PMIS 저장소에서 실행한다.

```powershell
git config --local --list
```

현재 저장소에 설정된 사용자 이름과 이메일이 의도한 값인지 확인한다.

### 8.3 SSH 인증 확인

PMIS 포트폴리오 계정:

```powershell
ssh -T git@github-portfolio
```

개인 GitHub 계정:

```powershell
ssh -T git@github-personal
```

정상적인 인증 결과 예시:

```text
Hi <GitHub 계정>! You've successfully authenticated, but GitHub does not provide shell access.
```

실제 연결된 GitHub 계정이 의도한 계정과 일치하는지 확인한다.

### 8.4 원격 저장소 확인

```powershell
git remote -v
```

PMIS 포트폴리오 저장소의 원격 주소가 올바르게 설정되어 있는지 확인한다.

예시:

```text
origin  git@github-portfolio:seos73410608/project-delivery-reference.git (fetch)
origin  git@github-portfolio:seos73410608/project-delivery-reference.git (push)
```

### 8.5 브랜치 및 작업 상태 확인

```powershell
git branch --show-current
git branch
git status
```

현재 작업 브랜치와 변경 사항이 의도한 상태인지 확인한다.

### 8.6 Git LFS 확인

Git LFS 설치 여부를 확인한다.

```powershell
git lfs version
```

저장소의 추적 설정을 확인한다.

```powershell
git lfs track
```

필요한 대용량 파일을 확보한다.

```powershell
git lfs pull
```

Git LFS 또는 GitHub Releases 등 별도 배포 경로를 사용하는 파일은 저장소 복제만으로 확보되지 않을 수 있다.

신규 PC에서 실제 파일이 존재하고 사용할 수 있는지 별도로 확인한다.

## 9. 환경변수 및 민감정보 검증

### 9.1 환경설정 파일 확인

프로젝트 루트에서 실행한다.

```powershell
Test-Path .\pmis-backend\.env
Test-Path .\pmis-backend\.env.example
Test-Path .\scripts\load-pmis-env.ps1
```

기존 개발 PC에서는 실제 `.env` 파일이 존재해야 한다.

신규 PC에서는 `.env.example`을 참고하여 `.env` 파일을 생성한다.

실제 비밀번호 및 JWT Secret은 Git에 포함하지 않는다.

### 9.2 `.env` 파일의 Git 제외 여부 확인

프로젝트 루트에서 실행한다.

```powershell
git check-ignore .\pmis-backend\.env
```

정상적으로 Git 제외 설정이 되어 있다면 `.env` 경로가 출력된다.

`.env.example`의 Git 관리 여부를 확인한다.

```powershell
git check-ignore .\pmis-backend\.env.example
```

정상적인 경우 출력이 없어야 한다.

Git 상태에서도 확인한다.

```powershell
git status --short
```

실제 `.env` 파일이 Git 변경 목록에 표시되지 않아야 한다.

**주의:** `.env`를 Git에서 제외하더라도 과거 커밋에 이미 포함된 민감정보가 자동으로 삭제되는 것은 아니다. 과거에 노출된 인증 정보는 교체하고 저장소 이력에 대한 별도 보안 조치를 검토한다.

### 9.3 Spring 프로파일 선택

PMIS는 실행 환경과 데이터베이스 종류를 독립적으로 선택한다.

| 실행 목적 | Spring 프로파일 |
|---|---|
| PostgreSQL 개발 | `dev,postgresql` |
| PostgreSQL 운영 설정 검증 | `prod,postgresql` |
| MariaDB 개발 | `dev,mariadb` |
| MariaDB 운영 설정 검증 | `prod,mariadb` |

`application.yaml`의 기본 프로파일은 다음과 같다.

```yaml
spring:
  profiles:
    default: dev,postgresql
```

따라서 별도 프로파일을 지정하지 않으면 `dev,postgresql`이 기본으로 적용된다.

실행 목적에 맞게 `SPRING_PROFILES_ACTIVE`를 먼저 지정한 후 환경변수 로딩 스크립트를 실행한다.

### 9.4 환경변수 로딩

프로젝트 루트로 이동한다.

```powershell
cd C:\SeoS\project-delivery-reference
```

PostgreSQL 개발 환경 예시:

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev,postgresql"
. .\scripts\load-pmis-env.ps1
```

정상적으로 실행되면 다음과 유사한 메시지가 출력된다.

```text
PMIS environment variables loaded successfully.
Active profiles: dev, postgresql
Environment: dev
Database: postgresql
Database username: pmis_migrator
Hibernate DDL mode: update
```

PostgreSQL 운영 설정 검증 환경 예시:

```powershell
$env:SPRING_PROFILES_ACTIVE = "prod,postgresql"
. .\scripts\load-pmis-env.ps1
```

정상적으로 실행되면 다음과 유사한 메시지가 출력된다.

```text
PMIS environment variables loaded successfully.
Active profiles: prod, postgresql
Environment: prod
Database: postgresql
Database username: pmis_app
Hibernate DDL mode: validate
```

위 `prod` 프로파일은 로컬에서 운영 런타임 설정과 권한을 검증하기 위한 구성이다. 실제 서비스 운영 배포를 의미하지 않는다.

### 9.5 데이터베이스 계정 및 환경변수 확인

현재 환경변수 로딩 스크립트는 선택한 프로파일에 맞는 DB 계정 정보를 `.env`에서 읽어 공통 환경변수로 전달한다.

| 구분 | 개발 프로파일 | 운영 설정 검증 프로파일 |
|---|---|---|
| PostgreSQL | `PMIS_POSTGRESQL_DEV_USERNAME/PASSWORD` | `PMIS_POSTGRESQL_PROD_USERNAME/PASSWORD` |
| MariaDB | `PMIS_MARIADB_DEV_USERNAME/PASSWORD` | `PMIS_MARIADB_PROD_USERNAME/PASSWORD` |
| 공통 전달 변수 | `PMIS_DB_USERNAME/PASSWORD` | `PMIS_DB_USERNAME/PASSWORD` |

환경변수의 실제 값은 출력하지 않고 설정 여부만 확인한다.

```powershell
Write-Host "DB Username 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:PMIS_DB_USERNAME))"
Write-Host "DB Password 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:PMIS_DB_PASSWORD))"
Write-Host "JWT Secret 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:JWT_SECRET))"
```

현재 선택된 프로파일도 확인한다.

```powershell
Write-Host "Active Profiles: $env:SPRING_PROFILES_ACTIVE"
```

다음 항목도 필요에 따라 설정 여부를 확인한다.

| 환경변수 | 용도 |
|---|---|
| `PMIS_POSTGRESQL_URL` | PostgreSQL JDBC URL |
| `PMIS_MARIADB_URL` | MariaDB JDBC URL |
| `PMIS_DB_USERNAME` | 선택된 DB 계정 |
| `PMIS_DB_PASSWORD` | 선택된 DB 비밀번호 |
| `JWT_SECRET` | JWT 서명 비밀키 |
| `RUNTIME_API_BASE_URL` | Automation Runtime 주소 |
| `RUNTIME_API_KEY` | Runtime API 인증 키 |
| `SERVER_PORT` | 백엔드 서버 포트 |

환경변수의 실제 비밀번호 및 JWT Secret을 화면이나 로그에 출력하지 않는다.

### 9.6 환경변수 변경 후 재로딩

`.env` 파일을 수정한 경우 현재 PowerShell 세션에 변경된 값을 다시 적용해야 한다.

프로파일을 먼저 설정하고 로딩한다.

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev,postgresql"
. .\scripts\load-pmis-env.ps1
```

다른 프로파일로 전환할 때도 동일한 순서를 적용한다.

환경변수는 현재 PowerShell 프로세스에 적용되므로 새 터미널을 열면 다시 로딩해야 한다.

### 9.7 환경변수 관리 보안 확인

- [ ] 실제 DB 비밀번호가 `.env`에 설정됨
- [ ] JWT Secret이 `.env`에 설정됨
- [ ] `.env`가 Git 추적 대상에서 제외됨
- [ ] `.env.example`에 실제 비밀정보가 없음
- [ ] 실행 프로파일을 먼저 지정함
- [ ] 환경변수 로딩 스크립트 실행 성공
- [ ] DB Username 및 Password 설정 여부 확인
- [ ] 비밀번호 및 JWT Secret이 출력되지 않음

`.env`는 로컬 PC에 평문으로 저장되므로 PC 접근 권한과 파일 접근 권한을 안전하게 관리한다.

## 10. PMIS 백엔드 검증

### 10.1 PostgreSQL 개발 환경 실행

새 PowerShell 터미널에서 실행한다.

```powershell
cd C:\SeoS\project-delivery-reference
```

개발 프로파일을 지정하고 환경변수를 로딩한다.

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev,postgresql"
. .\scripts\load-pmis-env.ps1
```

PMIS 전용 JDK 21을 활성화한다.

```powershell
. .\scripts\use-pmis-java21.ps1
```

백엔드 디렉터리로 이동한다.

```powershell
cd .\pmis-backend
```

백엔드를 실행한다.

```powershell
.\gradlew.bat bootRun
```

### 10.2 PostgreSQL 운영 설정 검증 환경 실행

새 PowerShell 터미널에서 실행한다.

```powershell
cd C:\SeoS\project-delivery-reference
```

운영 설정 검증 프로파일을 지정하고 환경변수를 로딩한다.

```powershell
$env:SPRING_PROFILES_ACTIVE = "prod,postgresql"
. .\scripts\load-pmis-env.ps1
```

JDK 21을 활성화하고 백엔드를 실행한다.

```powershell
. .\scripts\use-pmis-java21.ps1
cd .\pmis-backend
.\gradlew.bat bootRun
```

`prod` 프로파일에서는 `ddl-auto: validate`가 적용된다.

따라서 기존 스키마를 자동 변경하지 않고 Entity와 데이터베이스 스키마의 호환성을 확인한다.

### 10.3 MariaDB 프로파일 실행

MariaDB를 사용하는 경우 프로파일을 다음과 같이 선택한다.

개발 환경:

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev,mariadb"
. .\scripts\load-pmis-env.ps1
```

운영 설정 검증 환경:

```powershell
$env:SPRING_PROFILES_ACTIVE = "prod,mariadb"
. .\scripts\load-pmis-env.ps1
```

이후 JDK 21 활성화 및 `bootRun` 명령은 PostgreSQL 실행 방법과 동일하다.

MariaDB 프로파일은 별도 DB 계정과 권한 설정 및 실제 연결 테스트를 완료한 후 정상 동작 여부를 판단한다.

### 10.4 백엔드 실행 로그 확인

정상 기동 시 다음과 유사한 메시지를 확인한다.

```text
Tomcat started on port 8765 (http)
Started PmisApplication
```

실행 로그에서 다음 항목을 점검한다.

- Spring Boot 초기화 완료
- 활성 프로파일 확인
- 환경변수 기반 설정 로딩
- 데이터베이스 연결 성공
- Hibernate 및 JPA 초기화 완료
- 웹 서버 시작 완료
- 포트 `8765`에서 서버 실행

### 10.5 백엔드 포트 확인

```powershell
Get-NetTCPConnection -LocalPort 8765 -State Listen
```

백엔드 실행 중 해당 포트에서 `LISTEN` 상태가 확인되는지 점검한다.

### 10.6 Swagger UI 접속 확인

브라우저에서 다음 주소로 접속한다.

```text
http://localhost:8765/swagger-ui/index.html
```

Swagger UI가 정상적으로 표시되는지 확인한다.

### 10.7 HTTP 응답 확인

PowerShell에서 실행한다.

```powershell
Invoke-WebRequest -Uri "http://localhost:8765/swagger-ui/index.html" -UseBasicParsing
```

HTTP 응답 상태와 페이지 제공 여부를 확인한다.

Swagger UI 접속 성공은 웹 서버와 API 문서 제공 여부를 확인하는 것이며, 모든 업무 API의 정상 동작까지 보장하지는 않는다.

### 10.8 API 기능 확인

Swagger UI에서 실제 구현된 API를 호출한다.

PostgreSQL 운영 설정 검증 환경에서 다음 API의 응답을 확인한 이력이 있다.

```text
GET /api/projects/dashboard
```

검증 시 확인할 항목:

- HTTP 응답 상태
- API 응답의 성공 여부
- 응답 JSON 구조
- 데이터베이스 조회 결과
- 인증 및 권한 정책 적용 여부

실제 API의 인증 정책과 구현 상태에 따라 검증 방법은 달라질 수 있다.

## 11. PMIS 프론트엔드 검증

### 11.1 프론트엔드 실행

별도의 PowerShell 터미널에서 실행한다.

```powershell
cd C:\SeoS\project-delivery-reference
```

PMIS 전용 Node.js 24를 활성화한다.

```powershell
. .\scripts\use-pmis-node24.ps1
```

프론트엔드를 실행한다.

```powershell
npm run dev --prefix .\pmis-frontend
```

### 11.2 브라우저 접속

다음 주소로 접속한다.

```text
http://localhost:5173
```

PMIS 화면이 정상적으로 표시되는지 확인한다.

### 11.3 프론트엔드 포트 확인

```powershell
Get-NetTCPConnection -LocalPort 5173 -State Listen
```

개발 서버가 정상적으로 실행 중인지 확인한다.

### 11.4 브라우저 개발자 도구 확인

브라우저에서 `F12`를 눌러 개발자 도구를 연다.

다음 항목을 확인한다.

- Console 탭의 JavaScript 오류
- Network 탭의 API 요청 결과
- 백엔드 연결 실패 여부
- CORS 오류 여부
- 인증 및 권한 오류 여부

## 12. 전체 시스템 통합 검증

### 12.1 프론트엔드와 백엔드 통신 확인

프론트엔드 화면이 표시되는 것만으로 전체 시스템이 정상 동작한다고 판단하지 않는다.

다음 순서로 확인한다.

1. 백엔드가 정상적으로 실행 중인지 확인한다.
2. 프론트엔드가 정상적으로 실행 중인지 확인한다.
3. 프론트엔드에서 실제 백엔드 API를 호출한다.
4. 브라우저 Network 탭에서 요청 URL과 응답 상태를 확인한다.
5. 응답 데이터가 화면에 정상적으로 반영되는지 확인한다.

### 12.2 인증 및 권한 확인

인증 기능이 적용된 API를 사용하는 경우 다음 항목을 확인한다.

- 로그인 또는 토큰 발급 기능
- 인증이 필요한 API의 정상 접근
- 인증 정보가 없는 요청의 처리
- 권한이 없는 사용자의 접근 제한
- JWT 만료 및 갱신 처리

실제 구현된 API와 보안 정책에 따라 검증한다.

### 12.3 데이터베이스별 통합 테스트

DB 프로파일별로 다음 항목을 확인한다.

| 검증 항목 | PostgreSQL | MariaDB |
|---|---|---|
| DB 서비스 실행 | 확인 | 확인 |
| DB 접속 | 확인 | 확인 |
| 계정 인증 | 확인 | 확인 |
| 스키마 접근 | 확인 | 확인 |
| 백엔드 기동 | 확인 | 확인 |
| JPA 초기화 | 확인 | 확인 |
| API 데이터 조회 | 확인 | 확인 |
| API 데이터 변경 | 확인 | 확인 |

한 데이터베이스에서 성공한 테스트를 다른 데이터베이스에서도 성공한 것으로 간주하지 않는다.

### 12.4 통합 검증 결과 기록

| 검증 항목 | 결과 | 비고 |
|---|---|---|
| Java 21 활성화 | 확인 필요 | PMIS 전용 |
| Gradle 빌드 | 확인 필요 | Gradle Wrapper |
| Node.js 24 활성화 | 확인 필요 | PMIS 전용 |
| 프론트엔드 빌드 | 확인 필요 | Vite |
| 환경변수 로딩 | 확인 필요 | 프로파일별 확인 |
| `.env` Git 제외 | 확인 필요 | 민감정보 관리 |
| PostgreSQL 연결 | 확인 필요 | 5432 |
| MariaDB 연결 | 확인 필요 | 3306 |
| 백엔드 실행 | 확인 필요 | 8765 |
| Swagger UI 접속 | 확인 필요 | API 문서 |
| 프론트엔드 실행 | 확인 필요 | 5173 |
| 브라우저 화면 표시 | 확인 필요 | UI |
| API 통신 | 확인 필요 | Network |
| 인증 및 권한 처리 | 확인 필요 | JWT |

## 13. 전체 환경 최종 체크리스트

신규 PC 또는 개발 환경 변경 후 다음 항목을 확인한다.

### 운영체제 및 개발 도구

- [ ] Windows 및 PowerShell 확인
- [ ] 프로젝트 디렉터리 확인
- [ ] 필수 프로젝트 파일 확인
- [ ] VS Code 설치 및 실행 확인
- [ ] PMIS 주요 확장 프로그램 설치 확인
- [ ] Claude Code 확장 프로그램 설치 및 실행 확인
- [ ] Claude Code 계정 인증 및 서비스 사용 권한 확인

### Java 및 Gradle

- [ ] 시스템 기본 JDK 17 유지
- [ ] PMIS JDK 21 활성화
- [ ] Gradle Wrapper 9.5.1 확인
- [ ] 백엔드 `clean build` 성공

### Node.js 및 npm

- [ ] 시스템 기본 Node.js 20 유지
- [ ] PMIS Node.js 24 활성화
- [ ] npm 버전 확인
- [ ] 프론트엔드 의존성 설치
- [ ] 프론트엔드 빌드 성공
- [ ] ESLint 검사 결과 확인

### 데이터베이스

- [ ] PostgreSQL 서비스 및 포트 확인
- [ ] PostgreSQL `pmis` 데이터베이스 접속 확인
- [ ] PostgreSQL 계정 및 권한 확인
- [ ] MariaDB 서비스 및 포트 확인
- [ ] MariaDB `pmis` 데이터베이스 접속 확인
- [ ] MariaDB 계정 및 권한 확인
- [ ] DB 프로파일별 백엔드 연결 확인
- [ ] JPA 스키마 검증 확인

### 환경변수 및 민감정보

- [ ] `.env` 파일 생성
- [ ] `.env.example` 구성 확인
- [ ] 실행 프로파일 지정
- [ ] 환경변수 로딩 스크립트 실행 성공
- [ ] DB Username 설정 여부 확인
- [ ] DB Password 설정 여부 확인
- [ ] JWT Secret 설정 여부 확인
- [ ] `.env` Git 제외 확인
- [ ] `.env.example` Git 관리 대상 확인
- [ ] 민감정보 출력 및 노출 여부 확인

### Git 및 GitHub

- [ ] Git 설치 및 버전 확인
- [ ] Git 사용자 정보 설정
- [ ] 개인 GitHub 계정 SSH 인증
- [ ] PMIS 포트폴리오 계정 SSH 인증
- [ ] PMIS 원격 저장소 연결 확인
- [ ] Git 브랜치 및 작업 상태 확인
- [ ] Git LFS 설치 및 추적 설정 확인
- [ ] 대용량 설치 자산 확보 확인

### PMIS 실행 및 통합

- [ ] 백엔드 실행 성공
- [ ] 백엔드 `8765` 포트 실행 확인
- [ ] Swagger UI 접속 성공
- [ ] 실제 API 호출 성공
- [ ] 프론트엔드 실행 성공
- [ ] 프론트엔드 `5173` 포트 실행 확인
- [ ] 브라우저에서 PMIS 화면 표시
- [ ] 프론트엔드와 백엔드 간 API 통신 확인
- [ ] 인증 및 권한 처리 확인

## 14. 주요 문제 해결 가이드

### 14.1 Java 버전이 맞지 않는 경우

증상:

- Gradle 실행 시 Java 버전 오류
- Spring Boot 실행 실패
- JDK 17이 표시됨

해결:

```powershell
. .\scripts\use-pmis-java21.ps1
java -version
javac -version
```

PMIS 전용 JDK 21이 활성화되었는지 확인한다.

### 14.2 Node.js 버전이 맞지 않는 경우

증상:

- npm 명령 실행 실패
- Vite 실행 오류
- Node.js 버전 불일치

해결:

```powershell
. .\scripts\use-pmis-node24.ps1
node -v
npm -v
```

PMIS 전용 Node.js 24가 활성화되었는지 확인한다.

### 14.3 환경변수 파일을 찾을 수 없는 경우

증상:

```text
PMIS .env file not found.
```

해결:

1. 프로젝트 루트에서 스크립트를 실행했는지 확인한다.
2. `pmis-backend/.env` 파일이 존재하는지 확인한다.
3. 파일명이 `.env`인지 확인한다.
4. 신규 PC에서는 `.env.example`을 참고하여 `.env`를 생성했는지 확인한다.

환경변수 로딩 명령:

```powershell
cd C:\SeoS\project-delivery-reference
$env:SPRING_PROFILES_ACTIVE = "dev,postgresql"
. .\scripts\load-pmis-env.ps1
```

### 14.4 환경변수 로딩 실패

증상:

- 필수 환경변수 누락 오류
- DB 계정 정보 누락
- 프로파일 설정 오류

해결:

1. `.env` 파일에 필요한 항목이 존재하는지 확인한다.
2. 선택한 프로파일에 맞는 DB 계정 정보가 설정되어 있는지 확인한다.
3. `SPRING_PROFILES_ACTIVE` 값이 올바른지 확인한다.
4. 프로파일을 먼저 지정한 다음 로딩 스크립트를 실행한다.
5. 환경변수 설정 여부를 실제 값 노출 없이 확인한다.

```powershell
Write-Host "DB Username 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:PMIS_DB_USERNAME))"
Write-Host "DB Password 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:PMIS_DB_PASSWORD))"
Write-Host "JWT Secret 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:JWT_SECRET))"
```

비밀번호와 JWT Secret의 실제 값을 화면에 출력하지 않는다.

### 14.5 데이터베이스 연결 실패

증상:

```text
Connection refused
Access denied
Unknown database
```

해결 순서:

1. 데이터베이스 서비스 실행 여부
2. 선택한 DB 포트 확인
3. `pmis` 데이터베이스 존재 여부
4. JDBC URL 확인
5. 현재 선택된 DB 프로파일 확인
6. 계정 및 비밀번호 확인
7. 스키마 및 테이블 권한 확인
8. 환경변수 재로딩

포트 기준:

| DB | 포트 |
|---|---:|
| PostgreSQL | 5432 |
| MariaDB | 3306 |

### 14.6 PostgreSQL 권한 오류

증상:

```text
permission denied for schema public
permission denied for table
permission denied for sequence
```

확인 항목:

- 스키마 USAGE 권한
- 테이블 CRUD 권한
- 시퀀스 사용 권한
- 기본 권한 설정
- 현재 접속 계정
- 개발 계정과 런타임 계정의 역할 구분

개발 및 운영 런타임 계정에 동일한 권한을 부여하지 않는다.

계정 및 권한 설정은 `005_database_setup.md`를 참고한다.

### 14.7 Hibernate 스키마 검증 실패

증상:

```text
Schema-validation: missing table
Schema-validation: missing column
```

주요 원인:

- Entity와 실제 DB 테이블 구조 불일치
- 테이블 또는 컬럼 미생성
- 잘못된 데이터베이스 접속
- 잘못된 Spring 프로파일
- 스키마 변경이 개발 DB에 반영되지 않음

확인 순서:

1. 현재 활성 프로파일 확인
2. JDBC URL 및 DB 이름 확인
3. Entity와 실제 테이블 구조 비교
4. 개발 환경에서 스키마 변경 필요 여부 확인
5. 운영 설정 검증 환경에서는 스키마를 임의 변경하지 않고 차이를 확인

`prod` 프로파일의 `validate`는 스키마를 자동 생성하거나 수정하지 않는다.

### 14.8 백엔드 포트 충돌

증상:

- 백엔드 실행 시 포트 사용 중 오류
- `8765` 포트 바인딩 실패

확인:

```powershell
Get-NetTCPConnection -LocalPort 8765 -State Listen
```

해당 포트를 사용 중인 프로세스를 확인하고 기존 실행 인스턴스 여부를 점검한다.

### 14.9 프론트엔드 포트 충돌

증상:

- Vite 개발 서버 실행 시 포트 충돌

확인:

```powershell
Get-NetTCPConnection -LocalPort 5173 -State Listen
```

기존 Vite 개발 서버가 실행 중인지 확인한다.

### 14.10 GitHub SSH 인증 실패

증상:

```text
Permission denied (publickey)
```

확인 항목:

1. SSH 개인 키 파일 존재 여부
2. 공개 키가 올바른 GitHub 계정에 등록되어 있는지
3. SSH config의 Host 별칭 확인
4. `IdentityFile` 경로 확인
5. 원격 저장소 주소 확인

인증 테스트:

```powershell
ssh -T git@github-portfolio
```

개인 계정 인증 테스트:

```powershell
ssh -T git@github-personal
```

### 14.11 Git LFS 파일 다운로드 실패

증상:

- 저장소 복제 후 대용량 설치 파일이 존재하지 않음
- Git LFS 관련 오류 발생
- 실제 파일 대신 포인터 파일이 표시됨

확인:

```powershell
git lfs version
git lfs pull
git lfs status
```

Git LFS 설치 여부, GitHub 계정 접근 권한, 저장 용량 및 다운로드 한도를 확인한다.

GitHub Releases 등 별도 배포 경로를 사용하는 경우 해당 위치에서 파일을 직접 확보한다.

### 14.12 프론트엔드 API 통신 실패

증상:

- 브라우저 화면은 표시되지만 데이터 조회 실패
- Network 탭에서 API 요청 실패
- CORS 오류 발생

확인 항목:

1. 백엔드 실행 여부
2. 백엔드 API 주소 및 포트 `8765`
3. 프론트엔드 API 기본 주소 설정
4. 백엔드 CORS 설정
5. JWT 인증 및 권한 설정
6. 실제 API 경로와 HTTP 메서드

브라우저 Network 탭에서 요청 URL, 상태 코드, 응답 내용을 확인한다.

### 14.13 ESLint 오류 발생

ESLint 오류가 발생하면 다음 사항을 구분한다.

- Node.js 및 npm 설치 문제
- 패키지 의존성 문제
- ESLint 설정 문제
- 실제 소스 코드 품질 문제

빌드 성공 여부와 ESLint 검사 결과는 별개의 검증 항목이다.

### 14.14 VS Code 또는 Claude Code 실행 문제

확인 사항:

1. VS Code 설치 및 실행 여부
2. 확장 프로그램 설치 여부
3. 확장 프로그램 버전 확인
4. Claude Code 계정 인증 여부
5. 해당 계정의 서비스 사용 권한 여부

확장 프로그램이 설치되어 있어도 계정 인증이나 사용 권한이 없으면 실제 서비스를 이용할 수 없다.

## 15. 환경 구축 완료 기준

다음 조건을 충족하면 PMIS 개발 환경 구축이 완료된 것으로 판단한다.

1. 시스템 기본 Java 17 및 Node.js 20 환경이 유지된다.
2. PMIS 전용 JDK 21 및 Node.js 24 환경을 활성화할 수 있다.
3. Gradle Wrapper를 통해 백엔드 빌드가 성공한다.
4. 프론트엔드 의존성 설치 및 빌드가 성공한다.
5. `.env` 파일을 통해 선택한 프로파일의 환경변수를 로딩할 수 있다.
6. DB 비밀번호와 JWT Secret이 Git 추적 대상에서 제외된다.
7. 선택한 데이터베이스의 `pmis` 데이터베이스에 접속할 수 있다.
8. DB 계정별 권한 정책이 적용되어 있다.
9. 백엔드가 `8765` 포트에서 정상 실행된다.
10. JPA 초기화 및 스키마 검증이 정상적으로 완료된다.
11. Swagger UI에 접속할 수 있다.
12. 프론트엔드가 `5173` 포트에서 정상 실행된다.
13. 브라우저에서 PMIS 화면을 확인할 수 있다.
14. 프론트엔드와 백엔드 간 API 통신을 확인할 수 있다.
15. 인증 및 권한 처리를 확인할 수 있다.

PostgreSQL과 MariaDB는 각각 별도의 검증 결과를 기록한다. 한 DB에서 확인한 결과를 다른 DB의 검증 완료로 간주하지 않는다.

또한 Claude Code 서비스 사용 권한과 GitHub 인증은 별도로 확인한다. Git LFS 또는 별도 배포 경로로 관리하는 설치 자산은 신규 PC에서 실제로 확보할 수 있는지 확인한다.

## 16. 관련 문서

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
| `010_environment_verification_and_troubleshooting.md` | 환경 검증 및 문제 해결 |

---

**관리 원칙:** 개발 환경 검증은 프로그램 설치 여부만으로 완료되지 않는다. Java, Node.js, 데이터베이스, Git 및 개발 도구의 설정을 확인하고, 프로파일별 환경변수와 DB 권한을 검증한 후 PMIS 백엔드와 프론트엔드의 실제 실행 및 API 통신까지 확인해야 한다.