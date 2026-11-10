# PMIS 실행 가이드

- **문서 경로:** `docs/development/environment/009_pmis_execution_guide.md`
- **문서 버전:** 1.3
- **작성일:** 2026-11-07

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
MariaDB
```

각 구성 요소는 독립적으로 실행되며, 개발 시에는 데이터베이스, 백엔드, 프론트엔드 순서로 실행한다.

백엔드는 MariaDB에 연결하며, JPA 설정과 Entity 매핑에 따라 백엔드 실행 과정에서 데이터베이스 테이블을 자동 생성하거나 갱신할 수 있다.

데이터베이스 연결 정보와 JWT 비밀키 등 민감한 정보는 `application.yaml`에 직접 기록하지 않고 프로젝트 로컬 `.env` 파일에서 관리한다.

## 2. 실행 환경

### 2.1 구성 요소 및 실행 주소

| 구성 요소 | 기술 | 실행 주소 또는 포트 |
|---|---|---|
| 데이터베이스 | MariaDB | `localhost:3306` |
| 백엔드 | Spring Boot | `http://localhost:8765` |
| API 문서 | Swagger UI | `http://localhost:8765/swagger-ui/index.html` |
| 프론트엔드 | React + Vite | `http://localhost:5173` |

### 2.2 프로젝트 디렉터리

```text
project-delivery-reference/
├─ scripts/
│  ├─ load-pmis-env.ps1
│  ├─ use-pmis-java21.ps1
│  └─ use-pmis-node24.ps1
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
| `application.yaml` | Spring Boot 애플리케이션 설정 및 환경변수 참조 |

**주의:** `.env` 파일은 Git에 커밋하지 않는다. `.env.example`을 참고하여 각 개발 PC에서 별도로 생성한다.

### 2.3 실행에 필요한 버전

| 구성 요소 | PMIS 개발 환경 |
|---|---|
| Java | JDK 21 |
| Gradle | 9.5.1 |
| Node.js | 24.19.0 |
| npm | 11.17.0 |
| MariaDB | 기존 PC 10.6.5 |
| 백엔드 포트 | 8765 |
| 프론트엔드 포트 | 5173 |
| 데이터베이스 포트 | 3306 |

신규 PC에서는 기존 PC의 버전을 무조건 그대로 설치하기보다 각 구성 요소의 지원 상태와 프로젝트 호환성을 확인한다.

## 3. 실행 전 확인 사항

### 3.1 프로젝트 저장소 이동

PowerShell을 실행하고 프로젝트 저장소로 이동한다.

```powershell
cd C:\SeoS\project-delivery-reference
```

현재 Git 브랜치와 변경 사항을 확인한다.

```powershell
git branch --show-current
git status
```

### 3.2 데이터베이스 실행 확인

PMIS 백엔드는 MariaDB 데이터베이스를 사용한다.

기본 데이터베이스 연결 정보는 다음과 같다.

```text
Database: pmis
Host: localhost
Port: 3306
Username: .env에 설정된 계정
Password: .env에 설정된 비밀번호
```

Windows 서비스 상태를 확인한다.

```powershell
Get-Service MariaDB, MySQL
```

기존 개발 PC에서는 `MariaDB`와 `MySQL` 서비스가 모두 실행 중인 것을 확인했다.

신규 PC에서는 실제 설치된 서비스명을 먼저 확인하고 해당 서비스의 실행 상태를 점검한다.

데이터베이스 접속 및 `pmis` 데이터베이스 존재 여부를 확인한다.

기존 개발 PC의 MariaDB 설치 경로를 사용하는 경우:

```powershell
& "C:\Program Files\MariaDB 10.6\bin\mariadb.exe" `
    -u root -p `
    -e "SHOW DATABASES;"
```

비밀번호 입력 후 목록에서 `pmis`가 있는지 확인한다.

신규 PC에서는 실제 설치 경로와 데이터베이스 계정에 맞게 명령어를 조정한다.

참고:

- 새 PC에서는 MariaDB 설치 경로와 서비스 이름이 다를 수 있다.
- `pmis` 데이터베이스가 없다면 `005_database_setup.md`의 데이터베이스 생성 절차를 먼저 진행한다.
- 데이터베이스 생성과 테이블 생성은 별개의 작업이다.
- 기존 개발 PC의 데이터베이스 계정 설정을 신규 PC에 그대로 복사하지 않는다.
- 신규 PC에서는 `005_database_setup.md`의 권장 절차에 따라 PMIS 전용 데이터베이스 계정을 사용하는 것을 권장한다.

### 3.3 환경설정 파일 확인

백엔드 실행 전에 다음 파일이 준비되어 있는지 확인한다.

```text
pmis-backend/.env
pmis-backend/.env.example
scripts/load-pmis-env.ps1
```

`.env.example`은 환경변수 구성 예시이며, 실제 실행에는 `.env` 파일이 필요하다.

`.env` 파일이 없는 신규 PC에서는 `.env.example`을 참고하여 `.env`를 생성하고 실제 DB 비밀번호와 JWT Secret을 설정한다.

실제 비밀정보는 Git에 포함하거나 외부에 공유하지 않는다.

## 4. 백엔드 실행

백엔드는 Spring Boot와 Gradle Wrapper를 사용한다.

### 4.1 PMIS 환경변수 로딩

새 PowerShell 터미널을 열고 프로젝트 루트로 이동한다.

```powershell
cd C:\SeoS\project-delivery-reference
```

PMIS 환경변수를 로딩한다.

```powershell
. .\scripts\load-pmis-env.ps1
```

정상적으로 로딩되면 다음 메시지가 출력된다.

```text
PMIS environment variables loaded successfully.
```

이 스크립트는 `pmis-backend/.env` 파일을 읽어 현재 PowerShell 프로세스에 환경변수를 설정한다.

필수 환경변수는 다음과 같다.

- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`

필수 환경변수가 누락되거나 비어 있으면 스크립트에서 오류가 발생한다.

**주의사항:**

- 새 PowerShell 터미널을 열 때마다 환경변수를 다시 로딩해야 한다.
- 환경변수 값 자체를 출력하거나 화면에 공유하지 않는다.
- `.env` 파일은 현재 개발 PC에서만 관리하며 Git에 커밋하지 않는다.
- 환경변수 로딩은 현재 PowerShell 세션에 적용되며 Windows 시스템 전체의 환경변수를 변경하지 않는다.

**새 JWT_SECRET 생성방법:**

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
- Base64 인코딩 결과는 88자이다.
- 생성된 값은 클립보드에 복사되므로 .env의 JWT_SECRET에 저장한다.
- JWT_SECRET은 외부에 공개하거나 Git 저장소에 커밋하지 않는다.
- 기존 JWT_SECRET 교체 시 기존 토큰은 검증되지 않을 수 있으므로 개발환경의 인증 상태를 확인한다.

### 4.2 JDK 21 활성화

PMIS 전용 JDK 21 환경을 활성화한다.

```powershell
. .\scripts\use-pmis-java21.ps1
```

Java 버전을 확인한다.

```powershell
java -version
javac -version
```

기존 개발 PC에서 확인한 버전:

```text
java version "21.0.1"
javac 21.0.1
```

이 스크립트는 현재 PowerShell 세션에만 적용한다.

다른 프로젝트에서 사용하는 시스템 기본 JDK 17 설정은 변경하지 않는다.

### 4.3 Gradle 환경 확인

Gradle Wrapper 버전을 확인한다.

```powershell
.\pmis-backend\gradlew.bat --version
```

확인 항목:

- Gradle: 9.5.1
- Launcher JVM: JDK 21
- Daemon JVM: JDK 21

Gradle Wrapper를 사용하므로 별도의 시스템 Gradle 설치는 필요하지 않다.

### 4.4 데이터베이스 연결 환경변수 확인

환경변수가 정상적으로 설정되었는지 확인할 수 있다.

```powershell
Write-Host "DB URL 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:SPRING_DATASOURCE_URL))"
Write-Host "DB Username 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:SPRING_DATASOURCE_USERNAME))"
Write-Host "DB Password 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:SPRING_DATASOURCE_PASSWORD))"
Write-Host "JWT Secret 설정됨: $(-not [string]::IsNullOrWhiteSpace($env:JWT_SECRET))"
```

정상적으로 설정된 경우 각 항목이 `True`로 표시된다.

실제 비밀번호와 JWT Secret 값은 출력하지 않는다.

추가 환경변수는 다음과 같다.

| 환경변수 | 용도 | 기본값 또는 필수 여부 |
|---|---|---|
| `SPRING_DATASOURCE_URL` | MariaDB JDBC URL | `jdbc:mariadb://localhost:3306/pmis` |
| `SPRING_DATASOURCE_USERNAME` | DB 사용자 | `root` |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호 | 필수 |
| `JWT_SECRET` | JWT 서명 비밀키 | 필수 |
| `RUNTIME_API_BASE_URL` | Automation Runtime 주소 | `http://127.0.0.1:8000` |
| `RUNTIME_API_KEY` | Runtime API 인증 키 | 선택 |
| `SERVER_PORT` | 백엔드 포트 | `8765` |

Runtime API 인증이 활성화된 환경에서는 해당 시스템의 인증 정책에 맞춰 `RUNTIME_API_KEY`도 설정해야 한다.

### 4.5 백엔드 실행

백엔드 디렉터리로 이동한다.

```powershell
cd .\pmis-backend
```

Spring Boot 애플리케이션을 실행한다.

```powershell
.\gradlew.bat bootRun
```

정상적으로 실행되면 Spring Boot 로그에서 애플리케이션 시작 완료 메시지를 확인한다.

기본 실행 주소:

```text
http://localhost:8765
```

정상 기동 시 다음과 유사한 메시지가 출력된다.

```text
Tomcat started on port 8765 (http)
Started PmisApplication
```

백엔드 실행 과정에서 다음 작업이 진행된다.

1. Spring Boot 애플리케이션 초기화
2. 환경변수 기반 설정 로딩
3. MariaDB 데이터베이스 연결
4. JPA 초기화 및 Entity 매핑 처리
5. 웹 서버 시작

현재 개발 설정의 JPA `ddl-auto` 값은 `update`이다. 따라서 백엔드 실행 과정에서 Entity 매핑에 따라 테이블이 생성되거나 변경될 수 있다.

기존 DB 스키마를 변경하지 않고 실행 여부만 확인해야 하는 경우에는 다음과 같이 실행할 수 있다.

```powershell
.\gradlew.bat bootRun --args="--spring.jpa.hibernate.ddl-auto=none"
```

이 옵션은 해당 실행에만 적용되며 `application.yaml`의 기본 설정을 변경하지 않는다.

**주의:** 데이터베이스 스키마 변경이 허용되지 않는 환경에서는 실행 전에 DDL 설정과 데이터베이스 백업 여부를 확인한다.

백엔드 실행 성공 메시지만으로 모든 API와 데이터베이스 기능이 정상 동작한다고 판단하지 않는다. 실제 API 호출과 데이터베이스 연결 결과도 확인한다.

백엔드를 종료하려면 해당 PowerShell 터미널에서 `Ctrl + C`를 누른다.

### 4.6 백엔드 빌드 및 테스트

백엔드 디렉터리에서 다음 명령어로 빌드와 테스트를 수행한다.

```powershell
.\gradlew.bat clean build
```

빌드 성공 여부를 확인한다.

```text
BUILD SUCCESSFUL
```

테스트 실패가 발생하면 실패한 테스트와 오류 로그를 확인하고 원인을 해결한 후 다시 빌드한다.

※ `clean build`는 기존 빌드 결과물을 삭제한 후 다시 빌드한다. 실행 중인 애플리케이션이 있다면 먼저 종료하는 것을 권장한다.

## 5. 프론트엔드 실행

프론트엔드는 React, TypeScript, Vite 기반으로 구성되어 있다.

### 5.1 Node.js 24 활성화

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

### 5.2 의존성 설치

새 PC에서 처음 실행하거나 의존성을 다시 설치해야 하는 경우 프로젝트 루트에서 실행한다.

```powershell
npm ci --prefix .\pmis-frontend
```

`package-lock.json`을 기준으로 의존성을 설치한다.

이미 `node_modules`가 정상적으로 설치되어 있다면 매번 실행할 필요는 없다.

### 5.3 프론트엔드 실행

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

### 5.4 프론트엔드 빌드

배포용 빌드가 정상적으로 수행되는지 확인하려면 다음 명령어를 실행한다.

```powershell
npm run build --prefix .\pmis-frontend
```

빌드 결과물은 다음 디렉터리에 생성된다.

```text
pmis-frontend/dist/
```

### 5.5 프론트엔드 코드 검사

ESLint 검사를 실행한다.

```powershell
npm run lint --prefix .\pmis-frontend
```

기존 개발 PC에서 확인된 프론트엔드 코드에는 ESLint 오류와 경고가 존재한다.

기존 확인 결과:

- 오류: 29건
- 경고: 3건

따라서 검사 결과가 실패하더라도 Node.js 설치 문제로 단정하지 말고 실제 코드 검사 결과를 확인해야 한다.

## 6. 전체 시스템 실행 순서

PMIS를 실행할 때는 다음 순서를 권장한다.

### 6.1 터미널 1: 데이터베이스

- MariaDB 서비스 실행 여부 확인
- `pmis` 데이터베이스 존재 여부 확인
- 백엔드 접속 계정 및 설정 확인

신규 PC에서는 `005_database_setup.md`의 절차에 따라 데이터베이스와 전용 계정을 먼저 준비한다.

### 6.2 터미널 2: 백엔드

새 PowerShell 터미널을 열고 다음 순서대로 실행한다.

```powershell
cd C:\SeoS\project-delivery-reference

. .\scripts\load-pmis-env.ps1

. .\scripts\use-pmis-java21.ps1

cd .\pmis-backend

.\gradlew.bat bootRun
```

백엔드가 정상적으로 실행된 상태를 유지한다.

데이터베이스 연결과 JPA 초기화가 정상적으로 완료되었는지 확인한다.

### 6.3 터미널 3: 프론트엔드

새 PowerShell 터미널을 열고 다음 순서대로 실행한다.

```powershell
cd C:\SeoS\project-delivery-reference

. .\scripts\use-pmis-node24.ps1

npm run dev --prefix .\pmis-frontend
```

프론트엔드가 정상적으로 실행된 상태를 유지한다.

### 6.4 브라우저 접속

프론트엔드:

```text
http://localhost:5173
```

백엔드 API 문서:

```text
http://localhost:8765/swagger-ui/index.html
```

프론트엔드와 백엔드는 별도의 개발 서버로 실행된다. 브라우저에서 프론트엔드 화면이 표시되는 것과 백엔드 API 통신이 성공하는 것은 별도로 확인해야 한다.

## 7. 실행 확인

### 7.1 백엔드 확인

브라우저에서 Swagger UI에 접속한다.

```text
http://localhost:8765/swagger-ui/index.html
```

Swagger 화면이 정상적으로 표시되는지 확인한다.

API 테스트 시에는 각 API의 인증 요구 사항과 요청 형식에 맞게 실행한다.

### 7.2 데이터베이스 확인

MariaDB에 접속하여 다음 SQL을 실행한다.

```sql
USE pmis;

SHOW TABLES;
```

JPA 초기화 이후 필요한 테이블이 생성되었는지 확인한다.

기존 개발 PC에서 확인한 테이블 목록은 `005_database_setup.md`를 참고한다.

### 7.3 프론트엔드 확인

브라우저에서 다음 주소에 접속한다.

```text
http://localhost:5173
```

다음 항목을 확인한다.

- PMIS 화면 표시
- 사이드바 및 메뉴 표시
- 대시보드 화면 접근
- 브라우저 개발자 도구의 Console 오류 확인
- 백엔드 API 호출 및 응답 확인

### 7.4 전체 실행 확인 체크리스트

- [ ] MariaDB 서비스 실행 확인
- [ ] `pmis` 데이터베이스 접속 확인
- [ ] `.env` 파일 존재 확인
- [ ] PMIS 환경변수 로딩 확인
- [ ] JDK 21 활성화 확인
- [ ] Gradle 9.5.1 확인
- [ ] Spring Boot 백엔드 실행 확인
- [ ] 데이터베이스 연결 및 JPA 초기화 확인
- [ ] Swagger UI 접속 확인
- [ ] Node.js 24 활성화 확인
- [ ] npm 의존성 설치 확인
- [ ] Vite 개발 서버 실행 확인
- [ ] 브라우저에서 PMIS 화면 확인
- [ ] 프론트엔드와 백엔드 간 API 통신 확인

## 8. 문제 해결

### 8.1 백엔드 실행 시 데이터베이스 연결 오류

확인 사항:

1. MariaDB 서비스 실행 여부
2. MariaDB 접속 포트 `3306`
3. `pmis` 데이터베이스 존재 여부
4. 백엔드의 JDBC URL
5. 데이터베이스 사용자 및 비밀번호

오류 메시지에 `Connection refused`, `Access denied` 등이 포함되어 있는지 확인한다.

### 8.2 환경변수 로딩 오류

다음 메시지가 출력되면 `.env` 파일을 확인한다.

```text
PMIS .env file not found.
```

확인 사항:

1. 프로젝트 루트에서 스크립트를 실행했는지 확인
2. `pmis-backend/.env` 파일이 존재하는지 확인
3. 파일명이 `.env`인지 확인
4. `.env.example`만 있고 실제 `.env` 파일이 누락된 것은 아닌지 확인

환경변수를 다시 로딩한다.

```powershell
. .\scripts\load-pmis-env.ps1
```

정상적으로 실행되면 다음 메시지가 출력된다.

```text
PMIS environment variables loaded successfully.
```

### 8.3 필수 환경변수 누락 오류

다음과 같은 오류가 발생하면 필수 환경변수를 확인한다.

```text
SPRING_DATASOURCE_PASSWORD is missing in PMIS environment.
```

또는

```text
JWT_SECRET is missing in PMIS environment.
```

확인 사항:

1. `.env` 파일에 해당 항목이 존재하는지 확인
2. 환경변수 값이 비어 있지 않은지 확인
3. `.env` 파일을 수정했다면 환경변수를 다시 로딩했는지 확인

비밀번호와 JWT Secret의 실제 값을 화면에 출력하지 않는다.

### 8.4 Java 버전 오류

다음 명령어로 현재 PowerShell의 Java 버전을 확인한다.

```powershell
java -version
```

JDK 17이 표시된다면 PMIS 전용 환경을 다시 활성화한다.

```powershell
. .\scripts\use-pmis-java21.ps1
```

### 8.5 Node.js 버전 오류

```powershell
node -v
```

Node.js 20이 표시된다면 PMIS 전용 환경을 다시 활성화한다.

```powershell
. .\scripts\use-pmis-node24.ps1
```

### 8.6 포트 사용 중 오류

백엔드 또는 프론트엔드 실행 시 포트 충돌이 발생하면 다음 명령어로 점유 프로세스를 확인한다.

백엔드 포트:

```powershell
Get-NetTCPConnection -LocalPort 8765 -State Listen
```

프론트엔드 포트:

```powershell
Get-NetTCPConnection -LocalPort 5173 -State Listen
```

데이터베이스 포트:

```powershell
Get-NetTCPConnection -LocalPort 3306 -State Listen
```

프로세스를 확인한 후 기존 실행 인스턴스가 있는지 점검한다.

원인을 확인하지 않고 다른 프로세스를 강제로 종료하지 않는다.

### 8.7 프론트엔드에서 백엔드 API 호출 실패

확인 사항:

1. 백엔드가 정상적으로 실행 중인지 확인
2. 브라우저 개발자 도구의 Network 탭 확인
3. API 요청 URL 확인
4. CORS 설정 확인
5. JWT 인증 및 권한 오류 여부 확인
6. 프론트엔드 API 기본 주소 설정 확인

프론트엔드의 API 기본 주소 설정은 실제 프로젝트 코드와 환경 설정을 확인한 후 변경한다.

### 8.8 JPA 테이블 자동 생성 실패

확인 사항:

1. MariaDB 연결 성공 여부
2. `pmis` 데이터베이스 존재 여부
3. 데이터베이스 계정의 테이블 생성 권한
4. JPA DDL 설정
5. Entity 매핑 및 백엔드 실행 로그

데이터베이스 자체가 생성되었다고 해서 테이블까지 생성된 것은 아니다.

백엔드 실행 후 JPA 초기화가 정상적으로 완료되었는지 확인한다.

JPA 자동 생성 설정이 활성화되어 있지 않거나 Entity 매핑에 문제가 있는 경우에는 테이블이 자동 생성되지 않을 수 있다.

## 9. 개발 시 유의사항

- 데이터베이스, 백엔드, 프론트엔드는 각각 별도 터미널에서 실행한다.
- PMIS 백엔드 실행에는 JDK 21을 사용한다.
- PMIS 프론트엔드 실행에는 Node.js 24를 사용한다.
- 다른 프로젝트의 시스템 기본 Java 17 및 Node.js 20 설정은 유지한다.
- 백엔드 실행 전 `.env` 환경변수를 로딩한다.
- 새 PowerShell 터미널을 열 때마다 필요한 환경설정 스크립트를 실행한다.
- `.env` 파일은 로컬에서만 관리하고 Git에 커밋하지 않는다.
- `.env.example`은 환경변수 구성 예시로 저장소에서 관리한다.
- 백엔드와 프론트엔드 실행 로그를 구분해서 확인한다.
- 코드 수정 후에는 빌드 및 테스트 결과를 확인한다.
- DB 비밀번호, API Key, JWT 비밀키 등 인증 정보는 저장소에 노출하지 않는다.
- 개발 서버와 배포 서버의 설정을 혼동하지 않는다.
- 신규 PC에서는 `005_database_setup.md`의 데이터베이스 구축 절차를 먼저 완료한다.

---

**보안 원칙:** 데이터베이스 비밀번호, API Key, JWT 비밀키 등 인증 정보는 소스코드, 문서, Git 커밋에 평문으로 포함하지 않는다. 실제 인증 정보는 프로젝트 로컬 `.env` 또는 별도의 안전한 비밀정보 관리 방식을 적용한다. `.env` 파일은 Git 추적 대상에서 제외하며, 신규 PC에서는 `.env.example`을 참고하여 각 PC에 맞게 별도로 구성한다.