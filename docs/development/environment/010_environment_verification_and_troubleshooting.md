# 개발 환경 검증 및 문제 해결

- **문서 경로:** `docs/development/environment/010_environment_verification_and_troubleshooting.md`
- **문서 버전:** 1.2
- **작성일:** 2026-11-07

---

## 1. 문서 목적

이 문서는 PMIS 개발 환경 구축 완료 후 각 구성 요소가 정상적으로 설치되고 동작하는지 검증하는 방법을 설명한다.

검증 대상은 다음과 같다.

- Windows 및 PowerShell
- Java JDK 17 / 21
- Gradle
- Node.js 20 / 24
- MariaDB
- Visual Studio Code
- Claude Code 확장 프로그램
- Git 및 GitHub SSH 인증
- Git LFS
- PMIS 백엔드
- PMIS 프론트엔드
- 환경변수 및 민감정보 관리
- 프론트엔드와 백엔드 간 API 통신

단순히 프로그램이 설치되어 있는지만 확인하는 것이 아니라, PMIS 프로젝트를 실제로 실행하고 빌드할 수 있는지까지 확인하는 것을 목표로 한다.

검증 결과는 다음과 같이 구분한다.

| 구분 | 의미 |
|---|---|
| 설치 확인 | 프로그램 또는 도구가 설치되어 있음 |
| 환경 확인 | 지정된 버전과 설정을 사용할 수 있음 |
| 실행 확인 | 프로그램 또는 서버가 정상적으로 실행됨 |
| 기능 확인 | 실제 API 호출 및 데이터베이스 연동이 정상 동작함 |

## 2. 전체 검증 순서

다음 순서로 검증한다.

```text
1. Windows / PowerShell
          |
          v
2. Java / Gradle
          |
          v
3. Node.js / npm
          |
          v
4. MariaDB
          |
          v
5. VS Code / Claude Code
          |
          v
6. Git / GitHub SSH / Git LFS
          |
          v
7. 환경변수 및 민감정보 설정
          |
          v
8. PMIS 백엔드 실행 및 빌드
          |
          v
9. PMIS 프론트엔드 실행 및 빌드
          |
          v
10. 전체 시스템 통합 확인
```

## 3. 운영체제 및 프로젝트 경로 검증

### 3.1 Windows 버전 확인

PowerShell에서 실행한다.

```powershell
Get-ComputerInfo | Select-Object WindowsProductName, WindowsVersion, OsBuildNumber
```

현재 확인된 환경:

| 항목 | 확인 결과 |
|---|---|
| 운영체제 | Windows 10 Pro |
| Windows Version | 2009 |
| OS Build | 19045 |

다른 PC에서는 Windows 버전과 빌드 번호가 다를 수 있다.

신규 PC에서는 PMIS 개발 도구와의 호환성을 확인하고, 운영체제의 지원 상태도 고려한다.

### 3.2 PowerShell 버전 확인

```powershell
$PSVersionTable
```

현재 확인된 환경:

```text
PowerShell 5.1
```

본 문서의 PowerShell 명령어는 Windows PowerShell 5.1을 기준으로 작성한다.

### 3.3 프로젝트 디렉터리 확인

```powershell
cd C:\SeoS\project-delivery-reference
Get-Location
```

프로젝트 주요 디렉터리를 확인한다.

```powershell
Get-ChildItem -Directory
```

주요 디렉터리:

```text
.vscode/
docs/
http/
pmis/
pmis-backend/
pmis-frontend/
scripts/
Tools/
```

프로젝트 루트 경로는 PC 환경에 따라 달라질 수 있다.

### 3.4 프로젝트 필수 파일 확인

다음 명령어로 주요 설정 파일과 실행 스크립트의 존재 여부를 확인한다.

```powershell
Test-Path .\scripts\load-pmis-env.ps1
Test-Path .\scripts\use-pmis-java21.ps1
Test-Path .\scripts\use-pmis-node24.ps1
Test-Path .\pmis-backend\gradlew
Test-Path .\pmis-backend\build.gradle.kts
Test-Path .\pmis-backend\.env
Test-Path .\pmis-backend\.env.example
Test-Path .\pmis-frontend\package.json
Test-Path .\pmis-frontend\package-lock.json
```

기존 개발 PC에서는 실제 `.env` 파일도 존재해야 한다.

신규 PC에서는 `.env.example`을 참고하여 `.env`를 별도로 생성한다.

## 4. Java 및 Gradle 검증

### 4.1 시스템 기본 Java 확인

새 PowerShell 터미널에서 다음 명령어를 실행한다.

```powershell
$env:JAVA_HOME
java -version
javac -version
```

현재 PC에서는 시스템 기본 JDK 17을 사용한다.

다른 프로젝트에서 JDK 17을 사용하므로 시스템 전역 `JAVA_HOME`을 JDK 21로 변경하지 않는다.

현재 PC의 JDK 17 컴파일러 버전:

```text
javac 17.0.13
```

### 4.2 PMIS 전용 JDK 21 활성화

프로젝트 루트에서 실행한다.

```powershell
cd C:\SeoS\project-delivery-reference

. .\scripts\use-pmis-java21.ps1
```

버전을 확인한다.

```powershell
java -version
javac -version
```

현재 확인된 PMIS JDK:

```text
Java 21.0.1
javac 21.0.1
```

이 스크립트는 현재 PowerShell 세션에만 적용한다.

다른 프로젝트에서 사용하는 시스템 기본 JDK 17 설정은 변경하지 않는다.

### 4.3 Gradle 버전 확인

프로젝트 루트에서 실행한다.

```powershell
.\pmis-backend\gradlew.bat --version
```

현재 확인된 환경:

| 항목 | 버전 |
|---|---|
| Gradle | 9.5.1 |
| Launcher JVM | JDK 21 |
| Daemon JVM | JDK 21 |

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

빌드 실패 시 오류 메시지와 실패한 테스트를 확인한다.

`clean build`는 기존 빌드 결과물을 삭제한 후 다시 빌드한다.

## 5. Node.js 및 npm 검증

### 5.1 시스템 기본 Node.js 확인

새 PowerShell 터미널에서 실행한다.

```powershell
node -v
npm -v
```

현재 PC의 시스템 기본 환경:

| 항목 | 버전 |
|---|---|
| Node.js | 20.12.2 |
| npm | 10.5.0 |

### 5.2 PMIS 전용 Node.js 24 활성화

프로젝트 루트에서 실행한다.

```powershell
cd C:\SeoS\project-delivery-reference

. .\scripts\use-pmis-node24.ps1
```

버전을 확인한다.

```powershell
node -v
npm -v
```

현재 확인된 PMIS 환경:

| 항목 | 버전 |
|---|---|
| Node.js | 24.19.0 |
| npm | 11.17.0 |

이 스크립트는 현재 PowerShell 세션에만 적용한다.

시스템 기본 Node.js 20 설정은 변경하지 않는다.

### 5.3 프론트엔드 의존성 확인

프로젝트 루트에서 실행한다.

```powershell
Test-Path .\pmis-frontend\node_modules
Test-Path .\pmis-frontend\package-lock.json
```

두 항목 모두 `True`인지 확인한다.

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

빌드 결과물 확인:

```powershell
Test-Path .\pmis-frontend\dist
```

정상적으로 빌드되었다면 `True`가 반환된다.

### 5.5 프론트엔드 코드 검사

ESLint 검사를 실행한다.

```powershell
npm run lint --prefix .\pmis-frontend
```

기존 개발 PC에서 확인된 프론트엔드 코드에는 ESLint 오류와 경고가 존재한다.

당시 확인 결과:

| 구분 | 건수 |
|---|---:|
| 오류 | 29건 |
| 경고 | 3건 |
| 합계 | 32건 |

이 결과는 기존 개발 PC에서 확인한 시점의 기록이며, 소스 코드 변경에 따라 달라질 수 있다.

검사 결과가 실패하더라도 Node.js 설치 문제로 단정하지 말고 실제 코드 검사 결과를 확인해야 한다.

빌드 성공 여부와 ESLint 검사 결과는 별개의 검증 항목이다.

## 6. MariaDB 검증

### 6.1 Windows 서비스 확인

```powershell
Get-Service MariaDB, MySQL
```

현재 PC에서는 `MariaDB`와 `MySQL` 두 서비스가 모두 실행 중인 것을 확인했다.

신규 PC에서는 실제 설치된 서비스 이름을 먼저 확인한다.

```powershell
Get-Service | Where-Object {
    $_.Name -match "MariaDB|MySQL"
}
```

서비스 이름과 상태를 확인한 후 데이터베이스 실행 여부를 판단한다.

### 6.2 MariaDB 버전 확인

기존 개발 PC의 설치 경로:

```powershell
& "C:\Program Files\MariaDB 10.6\bin\mariadb.exe" --version
```

현재 확인된 버전:

```text
MariaDB 10.6.5
```

신규 PC에서는 실제 설치 경로에 맞게 명령어를 변경한다.

### 6.3 데이터베이스 접속 확인

기존 개발 PC에서는 다음 명령어로 접속할 수 있다.

```powershell
& "C:\Program Files\MariaDB 10.6\bin\mariadb.exe" -u root -p
```

비밀번호를 입력한 후 다음 SQL을 실행한다.

```sql
SHOW DATABASES;
```

PMIS 데이터베이스가 존재하는지 확인한다.

```sql
USE pmis;

SHOW TABLES;
```

현재 개발 PC에서 확인된 PMIS 테이블:

```text
changes
evidence
evidence_ocr_job
evidence_requirements
evidence_verification_history
evidence_version
inspection_items
inspections
issues
projects
refresh_tokens
risks
schedules
tasks
users
wbs
```

테이블 구성은 개발 진행에 따라 변경될 수 있다.

### 6.4 신규 PC 데이터베이스 계정 확인

신규 PC에서는 `005_database_setup.md`의 권장 설정에 따라 PMIS 전용 계정인 `pmis_app`을 사용한다.

신규 PC에서 데이터베이스 접속 확인 시 해당 계정과 비밀번호를 사용한다.

기존 개발 PC의 `root` 계정 설정을 신규 PC에 그대로 복사하지 않는다.

### 6.5 백엔드 데이터베이스 연결 확인

백엔드 실행 로그에서 데이터베이스 연결 오류가 없는지 확인한다.

특히 다음 항목을 점검한다.

- JDBC URL
- MariaDB 접속 포트 `3306`
- 데이터베이스 이름 `pmis`
- 데이터베이스 계정
- 비밀번호 및 인증 설정
- 계정의 데이터베이스 접근 권한

데이터베이스 접속 성공과 JPA 테이블 생성 성공은 별도로 확인한다.

## 7. VS Code 및 Claude Code 검증

### 7.1 VS Code 버전 확인

```powershell
code --version
```

현재 확인된 버전:

```text
1.145.1
```

`code` 명령어를 찾을 수 없다는 오류가 발생하면 VS Code 설치 여부와 PATH 설정을 확인한다.

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

신규 PC에서는 `006_vscode_setup.md`를 참고해 PMIS 개발에 필요한 확장 프로그램을 설치한다.

### 7.3 Claude Code 확인

다음 항목을 구분해서 확인한다.

- [ ] Claude Code 확장 프로그램 설치 여부
- [ ] 확장 프로그램 실행 여부
- [ ] 계정 인증 여부
- [ ] 실제 서비스 사용 권한 여부

확장 프로그램 설치만으로 서비스 사용이 보장되는 것은 아니다.

현재 PC에서는 확장 프로그램 설치는 확인했지만, 사용 계정의 Claude Code 구독 기반 사용 권한은 확인되지 않았다.

신규 PC에서도 확장 프로그램 설치와 계정 인증 및 서비스 사용 권한을 별도로 확인한다.

## 8. Git 및 GitHub 검증

### 8.1 Git 버전 확인

```powershell
git --version
```

현재 확인된 버전:

```text
git version 2.34.0.windows.1
```

### 8.2 Git 사용자 정보 확인

PMIS 저장소에서 실행한다.

```powershell
git config --local --list
```

현재 확인된 사용자 정보:

```text
user.name=seos73410608
user.email=seos73410608@gmail.com
```

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
Hi seos73410608! You've successfully authenticated, but GitHub does not provide shell access.
```

두 계정의 인증 결과에서 실제 연결된 GitHub 계정이 의도한 계정과 일치하는지 확인한다.

### 8.4 원격 저장소 확인

```powershell
git remote -v
```

정상적인 설정:

```text
origin  git@github-portfolio:seos73410608/project-delivery-reference.git (fetch)
origin  git@github-portfolio:seos73410608/project-delivery-reference.git (push)
```

PMIS 저장소의 원격 주소에 `github-portfolio` 별칭이 포함되어 있는지 확인한다.

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

PMIS 저장소에서 추적 설정을 확인한다.

```powershell
git lfs track
```

필요한 대용량 파일이 Git LFS 또는 별도 배포 경로를 통해 확보되는지 확인한다.

현재 PMIS 개발환경에서 관리하는 주요 설치 자산:

| 자산 | 경로 | 확인 사항 |
|---|---|---|
| Node.js 24 | `Tools/node-v24.19.0-win-x64` | 신규 PC에서 설치 패키지 확보 가능 여부 |
| Claude Code VSIX | `Tools/claude-code-uncompressed.vsix` | 신규 PC에서 확장 프로그램 설치 파일 확보 가능 여부 |

Git LFS 또는 GitHub Releases로 관리하는 파일은 저장소 복제만으로 확보되지 않을 수 있다.

신규 PC에서 실제 파일이 존재하고 사용할 수 있는지 별도로 확인한다.

## 9. PMIS 환경변수 및 민감정보 검증

### 9.1 환경설정 파일 확인

프로젝트 루트에서 실행한다.

```powershell
Test-Path .\pmis-backend\.env
Test-Path .\pmis-backend\.env.example
Test-Path .\scripts\load-pmis-env.ps1
```

기존 개발 PC에서는 세 항목 모두 `True`인지 확인한다.

신규 PC에서는 `.env` 파일이 없는 경우 `.env.example`을 참고하여 생성한다.

`.env.example`에는 실제 비밀번호나 JWT Secret을 입력하지 않는다.

### 9.2 `.env` 파일의 Git 제외 여부 확인

프로젝트 루트에서 실행한다.

```powershell
git check-ignore .\pmis-backend\.env
```

정상적인 경우 다음과 같이 `.env` 경로가 출력된다.

```text
pmis-backend/.env
```

`.env.example`은 Git에 포함되어야 하므로 다음 명령어로 확인한다.

```powershell
git check-ignore .\pmis-backend\.env.example
```

정상적인 경우 출력이 없어야 한다.

Git 상태에서도 확인한다.

```powershell
git status --short
```

실제 `.env` 파일이 Git 변경 목록에 표시되지 않아야 한다.

**주의:** `.env` 파일을 Git에서 제외하더라도 과거 Git 커밋에 이미 포함된 민감정보가 자동으로 삭제되는 것은 아니다. 기존에 노출된 인증 정보는 필요에 따라 교체하고, 저장소 이력에 대한 별도 보안 조치를 검토한다.

### 9.3 새 PowerShell 세션에서 환경변수 로딩

환경변수 로딩은 현재 PowerShell 프로세스에 적용된다.

따라서 새 PowerShell 터미널을 열 때마다 다시 로딩해야 한다.

새 터미널에서 프로젝트 루트로 이동한다.

```powershell
cd C:\SeoS\project-delivery-reference
```

환경변수 로딩 스크립트를 실행한다.

```powershell
. .\scripts\load-pmis-env.ps1
```

정상적으로 실행되면 다음 메시지가 출력된다.

```text
PMIS environment variables loaded successfully.
```

이 메시지가 출력되면 `.env` 파일에서 필수 환경변수를 읽고 로딩 검증까지 완료한 것이다.

### 9.4 필수 환경변수 설정 확인

환경변수의 실제 값은 출력하지 않고 설정 여부만 확인한다.

```powershell
Write-Host "DB URL 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:SPRING_DATASOURCE_URL))"
Write-Host "DB Username 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:SPRING_DATASOURCE_USERNAME))"
Write-Host "DB Password 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:SPRING_DATASOURCE_PASSWORD))"
Write-Host "JWT Secret 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:JWT_SECRET))"
```

정상적으로 설정된 경우 각 항목이 `True`로 표시된다.

필수 환경변수는 다음과 같다.

| 환경변수 | 용도 | 필수 여부 |
|---|---|---|
| `SPRING_DATASOURCE_URL` | MariaDB JDBC URL | 선택, 기본값 사용 가능 |
| `SPRING_DATASOURCE_USERNAME` | DB 사용자 | 선택, 기본값 사용 가능 |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호 | 필수 |
| `JWT_SECRET` | JWT 서명 비밀키 | 필수 |

다음 환경변수도 필요에 따라 확인한다.

| 환경변수 | 용도 | 기본값 또는 조건 |
|---|---|---|
| `RUNTIME_API_BASE_URL` | Automation Runtime 주소 | `http://127.0.0.1:8000` |
| `RUNTIME_API_KEY` | Runtime API 인증 키 | Runtime 인증 정책에 따라 설정 |
| `SERVER_PORT` | 백엔드 포트 | `8765` |

환경변수의 실제 값을 출력하거나 로그에 노출하지 않는다.

### 9.5 환경변수 변경 후 재로딩

`.env` 파일을 수정한 경우 현재 PowerShell 세션에 변경된 값을 다시 적용해야 한다.

프로젝트 루트에서 실행한다.

```powershell
. .\scripts\load-pmis-env.ps1
```

새 PowerShell 터미널을 열어도 환경변수는 자동으로 전달되지 않으므로 새 터미널에서 동일한 명령어를 실행한다.

### 9.6 환경변수 관리 보안 확인

다음 사항을 확인한다.

- [ ] 실제 DB 비밀번호가 `.env`에 설정되어 있음
- [ ] JWT Secret이 `.env`에 설정되어 있음
- [ ] `.env`가 Git 추적 대상에서 제외됨
- [ ] `.env.example`에 실제 비밀정보가 없음
- [ ] 환경변수 로딩 스크립트 실행 성공
- [ ] 필수 환경변수 설정 여부 확인
- [ ] 비밀번호 및 JWT Secret이 출력되지 않음

`.env`는 로컬 PC에 평문으로 저장되므로 PC 접근 권한과 파일 접근 권한을 안전하게 관리한다.

## 10. PMIS 백엔드 실행 검증

### 10.1 백엔드 실행

새 PowerShell 터미널에서 실행한다.

```powershell
cd C:\SeoS\project-delivery-reference

. .\scripts\load-pmis-env.ps1

. .\scripts\use-pmis-java21.ps1

cd .\pmis-backend

.\gradlew.bat bootRun
```

현재 PMIS 백엔드 포트:

```text
8765
```

백엔드 실행 로그에서 다음 항목을 확인한다.

- Spring Boot 초기화 완료
- 환경변수 기반 설정 로딩
- MariaDB 연결 성공
- JPA 초기화 완료
- 웹 서버 시작 완료
- 포트 `8765`에서 서버 실행

정상 기동 시 다음과 유사한 메시지가 출력된다.

```text
Tomcat started on port 8765 (http)
Started PmisApplication
```

현재 개발 환경의 JPA `ddl-auto` 값은 `update`이다. 따라서 백엔드 실행 과정에서 Entity 매핑에 따라 데이터베이스 테이블이 생성되거나 변경될 수 있다.

기존 DB 스키마를 변경하지 않고 실행 여부만 확인해야 하는 경우 다음과 같이 실행할 수 있다.

```powershell
.\gradlew.bat bootRun --args="--spring.jpa.hibernate.ddl-auto=none"
```

이 옵션은 해당 실행에만 적용되며 `application.yaml`의 기본 설정을 변경하지 않는다.

### 10.2 Swagger UI 접속 확인

브라우저에서 다음 주소로 접속한다.

```text
http://localhost:8765/swagger-ui/index.html
```

Swagger UI가 정상적으로 표시되는지 확인한다.

### 10.3 포트 사용 여부 확인

```powershell
Get-NetTCPConnection -LocalPort 8765 -State Listen
```

백엔드 실행 중 해당 포트에서 `LISTEN` 상태가 확인되는지 점검한다.

### 10.4 HTTP 응답 확인

PowerShell에서 다음 명령어를 실행한다.

```powershell
Invoke-WebRequest -Uri "http://localhost:8765/swagger-ui/index.html" -UseBasicParsing
```

HTTP 응답 상태와 페이지 제공 여부를 확인한다.

Swagger UI 접속 성공은 웹 서버와 문서 페이지 제공 여부를 확인하는 것이며, 모든 업무 API의 정상 동작까지 보장하지는 않는다.

## 11. PMIS 프론트엔드 실행 검증

### 11.1 프론트엔드 실행

별도의 PowerShell 터미널에서 실행한다.

```powershell
cd C:\SeoS\project-delivery-reference

. .\scripts\use-pmis-node24.ps1

npm run dev --prefix .\pmis-frontend
```

현재 프론트엔드 개발 서버 포트:

```text
5173
```

### 11.2 브라우저 접속

다음 주소로 접속한다.

```text
http://localhost:5173
```

화면이 정상적으로 표시되는지 확인한다.

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

다음 항목을 확인한다.

1. 백엔드가 정상적으로 실행 중인지 확인한다.
2. 프론트엔드가 정상적으로 실행 중인지 확인한다.
3. 프론트엔드에서 실제 백엔드 API를 호출한다.
4. 브라우저 Network 탭에서 요청 URL과 응답 상태를 확인한다.
5. 응답 데이터가 화면에 정상적으로 반영되는지 확인한다.

실제 API 호출은 프로젝트에 구현된 인증 및 업무 API 중 적절한 항목을 선택해 확인한다.

### 12.2 인증 및 권한 확인

인증 기능이 적용된 API를 사용하는 경우 다음 항목을 확인한다.

- 로그인 또는 토큰 발급 기능
- 인증이 필요한 API의 정상 접근
- 인증 정보가 없는 요청의 처리
- 권한이 없는 사용자의 접근 제한

실제 구현된 API와 보안 정책에 따라 검증한다.

### 12.3 통합 검증 결과 기록

| 검증 항목 | 결과 |
|---|---|
| 환경변수 로딩 | 확인 필요 |
| 민감정보 Git 제외 | 확인 필요 |
| MariaDB 연결 | 확인 필요 |
| 백엔드 실행 | 확인 필요 |
| Swagger UI 접속 | 확인 필요 |
| 프론트엔드 실행 | 확인 필요 |
| 브라우저 화면 표시 | 확인 필요 |
| 프론트엔드 API 호출 | 확인 필요 |
| API 응답 데이터 표시 | 확인 필요 |
| 인증 및 권한 처리 | 확인 필요 |

## 13. 전체 환경 최종 체크리스트

다음 항목을 이용해 신규 PC의 환경 구축 완료 여부를 확인한다.

**운영체제 및 개발 도구**

- [ ] Windows 및 PowerShell 확인
- [ ] 프로젝트 디렉터리 확인
- [ ] 필수 프로젝트 파일 확인
- [ ] VS Code 설치 및 실행 확인
- [ ] PMIS 주요 확장 프로그램 설치 확인
- [ ] Claude Code 확장 프로그램 설치 및 실행 확인
- [ ] Claude Code 계정 인증 및 서비스 사용 권한 확인

**Java 및 Gradle**

- [ ] 시스템 기본 JDK 17 유지
- [ ] PMIS JDK 21 활성화
- [ ] Gradle 9.5.1 확인
- [ ] 백엔드 `clean build` 성공

**Node.js 및 npm**

- [ ] 시스템 기본 Node.js 20 유지
- [ ] PMIS Node.js 24 활성화
- [ ] npm 버전 확인
- [ ] 프론트엔드 의존성 설치
- [ ] 프론트엔드 빌드 성공
- [ ] ESLint 검사 결과 확인

**데이터베이스**

- [ ] MariaDB 서비스 실행
- [ ] MariaDB 버전 확인
- [ ] `pmis` 데이터베이스 접속
- [ ] 데이터베이스 계정 및 권한 확인
- [ ] 필요한 테이블 생성 및 조회 확인

**환경변수 및 민감정보**

- [ ] `.env` 파일 생성
- [ ] `.env.example` 구성 확인
- [ ] 환경변수 로딩 스크립트 실행 성공
- [ ] DB 비밀번호 설정 여부 확인
- [ ] JWT Secret 설정 여부 확인
- [ ] `.env` Git 제외 확인
- [ ] `.env.example` Git 관리 대상 확인
- [ ] 민감정보 출력 및 노출 여부 확인

**Git 및 GitHub**

- [ ] Git 설치 및 버전 확인
- [ ] Git 사용자 정보 설정
- [ ] 개인 GitHub 계정 SSH 인증
- [ ] PMIS 포트폴리오 계정 SSH 인증
- [ ] PMIS 원격 저장소 연결 확인
- [ ] Git 브랜치 및 작업 상태 확인
- [ ] Git LFS 설치 및 추적 설정 확인
- [ ] 대용량 설치 자산 확보 확인

**PMIS 실행 및 통합**

- [ ] 백엔드 실행 성공
- [ ] 백엔드 `8765` 포트 실행 확인
- [ ] Swagger UI 접속 성공
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

. .\scripts\load-pmis-env.ps1
```

### 14.4 필수 환경변수 누락 오류

증상:

```text
SPRING_DATASOURCE_PASSWORD is missing in PMIS environment.
```

또는

```text
JWT_SECRET is missing in PMIS environment.
```

해결:

1. `.env` 파일에 해당 항목이 존재하는지 확인한다.
2. 환경변수 값이 비어 있지 않은지 확인한다.
3. `.env` 파일을 수정했다면 환경변수를 다시 로딩한다.
4. 환경변수 설정 여부를 값 노출 없이 확인한다.

```powershell
Write-Host "DB Password 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:SPRING_DATASOURCE_PASSWORD))"
Write-Host "JWT Secret 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:JWT_SECRET))"
```

비밀번호와 JWT Secret의 실제 값을 화면에 출력하지 않는다.

### 14.5 데이터베이스 연결 실패

증상:

- `Connection refused`
- `Access denied`
- `Unknown database`

해결 순서:

1. MariaDB 서비스 실행 여부 확인
2. 포트 `3306` 사용 여부 확인
3. `pmis` 데이터베이스 존재 여부 확인
4. JDBC URL 확인
5. 계정과 비밀번호 확인
6. 계정의 데이터베이스 접근 권한 확인
7. `.env` 환경변수 로딩 여부 확인

### 14.6 백엔드 포트 충돌

증상:

- 백엔드 실행 시 포트 사용 중 오류
- `8765` 포트 바인딩 실패

확인:

```powershell
Get-NetTCPConnection -LocalPort 8765 -State Listen
```

해당 포트를 사용 중인 프로세스를 확인하고 기존 실행 인스턴스 여부를 점검한다.

### 14.7 프론트엔드 포트 충돌

증상:

- Vite 개발 서버 실행 시 포트 충돌

확인:

```powershell
Get-NetTCPConnection -LocalPort 5173 -State Listen
```

기존 Vite 개발 서버가 실행 중인지 확인한다.

### 14.8 GitHub SSH 인증 실패

증상:

- `Permission denied (publickey)`
- GitHub 저장소 접근 실패

확인:

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

### 14.9 Git LFS 파일 다운로드 실패

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

Git LFS가 정상 설치되었는지, GitHub 계정에 접근 권한이 있는지, 저장 용량 및 다운로드 한도에 문제가 없는지 확인한다.

GitHub Releases 등 별도 배포 경로를 사용하는 경우에는 해당 배포 위치에서 파일을 직접 확보한다.

### 14.10 프론트엔드 API 통신 실패

증상:

- 브라우저 화면은 표시되지만 데이터 조회 실패
- Network 탭에서 API 요청 실패
- CORS 오류 발생

확인:

1. 백엔드 실행 여부
2. 백엔드 API 주소 및 포트 `8765`
3. 프론트엔드 API 기본 주소 설정
4. 백엔드 CORS 설정
5. JWT 인증 및 권한 설정

실제 API 기본 주소는 프로젝트 설정을 확인한 후 수정한다.

### 14.11 ESLint 오류 발생

기존 개발 PC에서는 ESLint 오류와 경고가 발생한 이력이 있다.

따라서 ESLint 오류가 발생하면 다음 사항을 구분한다.

- Node.js 및 npm 설치 문제
- ESLint 설정 문제
- 실제 소스 코드 품질 문제

빌드 성공 여부와 ESLint 검사 결과는 별개의 검증 항목이다.

### 14.12 VS Code 또는 Claude Code 실행 문제

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
3. `.env` 파일을 통해 필요한 환경변수를 로딩할 수 있다.
4. DB 비밀번호와 JWT Secret이 Git 추적 대상에서 제외된다.
5. MariaDB의 `pmis` 데이터베이스에 접속할 수 있다.
6. 백엔드 Gradle 빌드가 성공한다.
7. 프론트엔드 Vite 빌드가 성공한다.
8. 백엔드가 `8765` 포트에서 정상 실행된다.
9. 프론트엔드가 `5173` 포트에서 정상 실행된다.
10. Swagger UI에 접속할 수 있다.
11. 브라우저에서 PMIS 화면을 확인할 수 있다.
12. 프론트엔드와 백엔드 간 API 통신을 확인할 수 있다.

Claude Code 서비스 사용 권한과 GitHub 인증은 각각 별도로 확인해야 한다. 확장 프로그램 설치만으로 실제 사용 가능 상태를 보장하지 않는다.

또한 Git LFS 또는 별도 배포 경로로 관리하는 설치 자산은 신규 PC에서 실제로 확보할 수 있는지 확인해야 한다.

## 16. 관련 문서

| 파일명 | 설명 |
|---|---|
| `001_development_environment_overview.md` | 개발 환경 개요 |
| `002_windows_setup.md` | Windows 설정 |
| `003_java_setup.md` | Java 설치 및 설정 |
| `004_nodejs_setup.md` | Node.js 설치 및 설정 |
| `005_database_setup.md` | MariaDB 설치 및 설정 |
| `006_vscode_setup.md` | VS Code 설치 및 설정 |
| `007_claude_code_setup.md` | Claude Code 설치 및 설정 |
| `008_git_github_setup.md` | Git 및 GitHub 설정 |
| `009_pmis_execution_guide.md` | PMIS 실행 가이드 |
| `010_environment_verification_and_troubleshooting.md` | 환경 검증 및 문제 해결 |

---

**관리 원칙:** 개발 환경 구축은 단순한 프로그램 설치로 완료되지 않는다. Java, Node.js, MariaDB, Git 및 개발 도구의 설정을 확인하고, 환경변수 및 민감정보 관리 상태를 검증한 후 PMIS 백엔드와 프론트엔드의 실제 실행 및 통합 통신까지 확인해야 한다.