# PMIS 개발 환경 문서 최종 일관성 점검표

- **대상:** `docs/development/environment/001~010`
- **점검 기준일:** 2026-11-06
- **목적:** 개발 환경 구축 문서 10개의 버전, 상호 참조, 명령어 및 설정 일관성 검증
- **상태:** 최종 검수 기준

---

## 1. 문서별 최종 버전 확인

| 번호 | 파일명 | 최종 목표 버전 | 확인 사항 |
|---|---|---:|---|
| 001 | `001_development_environment_overview.md` | 1.1 | 개발 환경 전체 개요 |
| 002 | `002_windows_setup.md` | 1.1 | Windows 및 PowerShell |
| 003 | `003_java_setup.md` | 1.1 | Java 17 / 21 구분 |
| 004 | `004_nodejs_setup.md` | 1.1 | Node.js 20 / 24 구분 |
| 005 | `005_database_setup.md` | 1.2 | MariaDB 및 DB 계정 정책 |
| 006 | `006_vscode_setup.md` | 1.1 | VS Code 및 확장 프로그램 |
| 007 | `007_claude_code_setup.md` | 1.1 | Claude Code 설치 및 사용 권한 |
| 008 | `008_git_github_setup.md` | 1.1 | Git 및 GitHub SSH |
| 009 | `009_pmis_execution_guide.md` | 1.1 | PMIS 실행 절차 |
| 010 | `010_environment_verification_and_troubleshooting.md` | 1.1 | 환경 검증 및 문제 해결 |

### 버전 관리 확인

- [ ] 각 문서의 제목과 파일명이 일치한다.
- [ ] 문서 상단에 파일 경로, 문서 버전, 작성일을 표기한다.
- [ ] 001~010의 버전이 위 표와 일치한다.
- [ ] 005는 1.2, 나머지 문서는 1.1을 기준으로 관리한다.
- [ ] 문서 수정 시 해당 문서의 버전과 작성일을 함께 갱신한다.

## 2. 프로젝트 경로 및 디렉터리 일관성

### 프로젝트 루트

```text
C:\SeoS\project-delivery-reference
```

### 주요 디렉터리

```text
project-delivery-reference/
├─ .vscode/
├─ docs/
│  └─ development/
│     └─ environment/
├─ http/
├─ pmis/
├─ pmis-backend/
├─ pmis-frontend/
├─ scripts/
│  ├─ use-pmis-java21.ps1
│  └─ use-pmis-node24.ps1
└─ Tools/
```

### 경로 검증

- [ ] 모든 문서에서 프로젝트 루트 경로가 일치한다.
- [ ] Java 활성화 스크립트 경로가 `scripts/use-pmis-java21.ps1`로 통일되어 있다.
- [ ] Node.js 활성화 스크립트 경로가 `scripts/use-pmis-node24.ps1`로 통일되어 있다.
- [ ] 백엔드 경로가 `pmis-backend/`로 통일되어 있다.
- [ ] 프론트엔드 경로가 `pmis-frontend/`로 통일되어 있다.
- [ ] 문서 간 상대 경로와 파일명이 실제 구조에 맞는다.

## 3. Java 및 Node.js 버전 정책

### Java

| 구분 | 버전 | 적용 범위 |
|---|---|---|
| 시스템 기본 JDK | 17 | 다른 프로젝트 |
| PMIS 전용 JDK | 21 | PMIS 백엔드 |
| Gradle | 9.5.1 | PMIS 백엔드 빌드 |

PMIS 환경 활성화:

```powershell
. .\scripts\use-pmis-java21.ps1
```

검증:

```powershell
java -version
javac -version
.\pmis-backend\gradlew --version
```

- [ ] 시스템 및 사용자 전역 `JAVA_HOME`을 JDK 21로 변경하지 않는다.
- [ ] JDK 17은 다른 프로젝트의 기본 환경으로 유지한다.
- [ ] PMIS 작업 시에만 프로젝트 전용 스크립트로 JDK 21을 활성화한다.
- [ ] Gradle Toolchain은 Java 21을 사용한다.
- [ ] Gradle Wrapper 버전은 9.5.1이다.
- [ ] Launcher JVM과 Daemon JVM이 JDK 21인지 확인한다.

### Node.js

| 구분 | 버전 | 적용 범위 |
|---|---|---|
| 시스템 기본 Node.js | 20.12.2 | 일반 개발 환경 |
| 시스템 기본 npm | 10.5.0 | 일반 개발 환경 |
| PMIS 전용 Node.js | 24.19.0 | PMIS 프론트엔드 |
| PMIS 전용 npm | 11.17.0 | PMIS 프론트엔드 |

PMIS 환경 활성화:

```powershell
. .\scripts\use-pmis-node24.ps1
```

검증:

```powershell
node -v
npm -v
```

- [ ] 시스템 기본 Node.js 20을 유지한다.
- [ ] PMIS 작업 시 Node.js 24를 활성화한다.
- [ ] Java와 Node.js 활성화 스크립트는 서로 독립적으로 동작한다.
- [ ] 같은 PowerShell 세션에서 두 스크립트를 모두 활성화할 수 있다.
- [ ] 활성화 스크립트는 현재 PowerShell 세션에만 적용된다.

## 4. MariaDB 설정 일관성

### 데이터베이스 접속 정보

| 항목 | 설정 |
|---|---|
| DBMS | MariaDB |
| 데이터베이스 | `pmis` |
| 포트 | 3306 |
| JDBC URL | `jdbc:mariadb://localhost:3306/pmis` |
| 기존 PC 계정 | `root` |
| 신규 PC 권장 계정 | `pmis_app` |

### 기존 PC와 신규 PC 구분

| 항목 | 기존 개발 PC | 신규 개발 PC |
|---|---|---|
| MariaDB 버전 | 10.6.5 | 지원 중인 버전 권장 |
| 서비스 | MariaDB, MySQL | 실제 설치된 서비스 확인 |
| DB 계정 | 기존 root 설정 | 전용 `pmis_app` |
| 인증 정보 | 기존 로컬 설정 | 환경변수 등 별도 관리 |
| 테이블 | 기존 개발 데이터 | JPA 설정에 따라 생성 |

- [ ] 기존 PC의 `root` 설정과 신규 PC의 `pmis_app` 설정을 혼동하지 않는다.
- [ ] 신규 PC에서는 005 문서의 전용 계정 생성 절차를 따른다.
- [ ] DB 비밀번호를 문서나 명령어 예시에 평문으로 기록하지 않는다.
- [ ] 환경변수 이름이 백엔드 설정과 일치하는지 확인한다.
- [ ] `pmis` 데이터베이스 생성과 JPA 테이블 생성을 구분한다.
- [ ] JPA 자동 테이블 생성은 DDL 설정, Entity 매핑, DB 권한을 전제로 설명한다.
- [ ] 신규 PC에서 16개 테이블을 수동으로 생성하도록 안내하지 않는다.
- [ ] MariaDB 10.6.5는 기존 PC의 확인된 환경으로만 기록하고 신규 설치 권장 버전으로 사용하지 않는다.

## 5. VS Code 및 Claude Code 설정

### VS Code

현재 확인된 버전:

```text
Visual Studio Code 1.145.1
```

주요 확장 프로그램:

| 확장 프로그램 | ID |
|---|---|
| Extension Pack for Java | `vscjava.vscode-java-pack` |
| Gradle for Java | `vscjava.vscode-gradle` |
| Spring Boot Extension Pack | `vmware.vscode-boot-dev-pack` |
| ESLint | `dbaeumer.vscode-eslint` |
| Prettier | `esbenp.prettier-vscode` |

- [ ] VS Code 버전과 확장 프로그램 ID가 일치한다.
- [ ] 새 PC에는 PMIS 개발에 필요한 확장 프로그램을 우선 설치하도록 안내한다.
- [ ] VS Code 내부 Java 실행 환경과 PMIS 백엔드 JDK 21을 구분한다.
- [ ] Java 확장 프로그램 내부 JRE를 PMIS의 JDK로 오인하지 않는다.

### Claude Code

- [ ] 확장 프로그램 설치와 계정 인증을 구분한다.
- [ ] 계정 인증과 실제 서비스 사용 권한을 구분한다.
- [ ] 현재 PC에서 확인된 계정의 사용 권한을 모든 사용자에게 일반화하지 않는다.
- [ ] Claude Code VSIX 파일은 의도적으로 보관하는 설치 자산으로 취급한다.
- [ ] VSIX 파일을 저장소에서 내려받을 수 있는 배포 경로를 확보한다.

## 6. Git 및 GitHub 설정 일관성

### Git 환경

| 항목 | 설정 |
|---|---|
| Git 버전 | 2.34.0.windows.1 |
| GitHub 저장소 | `seos73410608/project-delivery-reference` |
| 원격 저장소 이름 | `origin` |
| PMIS SSH Host | `github-portfolio` |
| Git 사용자 이름 | `seos73410608` |
| Git 이메일 | `seos73410608@gmail.com` |

### SSH 계정 분리

| 계정 | Host 별칭 | 개인 키 |
|---|---|---|
| 개인 계정 | `github-personal` | `~/.ssh/id_rsa` |
| 포트폴리오 계정 | `github-portfolio` | `~/.ssh/id_ed25519_pmis` |

PMIS 원격 저장소:

```text
git@github-portfolio:seos73410608/project-delivery-reference.git
```

- [ ] SSH Host 별칭이 008과 010에서 일치한다.
- [ ] 개인 계정과 포트폴리오 계정의 키 파일을 구분한다.
- [ ] 신규 PC에서 기존 키를 안전하게 이전하거나 계정별 새 키를 생성한다.
- [ ] 공개 키만 GitHub에 등록한다.
- [ ] 개인 키와 개인 키 암호를 공유하지 않는다.
- [ ] `git config --local`을 사용해 저장소별 사용자 정보를 설정한다.
- [ ] PMIS 원격 저장소 주소에 `github-portfolio`를 사용한다.
- [ ] 개인 계정과 포트폴리오 계정의 SSH 인증 결과를 각각 확인한다.
- [ ] 개인 계정 인증 예시에는 실제 개인 계정의 GitHub 사용자명을 사용한다.
- [ ] Git 작업 전 브랜치와 원격 저장소 주소를 확인한다.

### Git 브랜치 및 커밋

- [ ] `main`, `develop`, `feature/*`, `fix/*`, `release/*` 정책이 일치한다.
- [ ] 기능 개발은 `develop`에서 `feature/*` 브랜치를 생성하는 흐름으로 설명한다.
- [ ] 커밋 접두사가 008 문서의 규칙과 다른 문서의 예시에서 충돌하지 않는다.
- [ ] `git push -u origin feature/브랜치명` 형식을 사용한다.

## 7. PMIS 실행 설정 일관성

### 포트 정책

| 구성 요소 | 포트 | 주소 |
|---|---:|---|
| MariaDB | 3306 | `localhost:3306` |
| PMIS 백엔드 | 8765 | `http://localhost:8765` |
| Swagger UI | 8765 | `http://localhost:8765/swagger-ui/index.html` |
| PMIS 프론트엔드 | 5173 | `http://localhost:5173` |

- [ ] 백엔드 포트는 모든 문서에서 8765로 통일한다.
- [ ] Swagger 주소는 8765 포트를 사용한다.
- [ ] 프론트엔드 포트는 5173으로 통일한다.
- [ ] MariaDB 포트는 3306으로 통일한다.
- [ ] 과거 포트 8080을 현재 실행 주소로 안내하지 않는다.
- [ ] 포트 점유 확인 명령어가 실제 포트와 일치한다.

백엔드 포트 확인:

```powershell
Get-NetTCPConnection -LocalPort 8765 -State Listen
```

프론트엔드 포트 확인:

```powershell
Get-NetTCPConnection -LocalPort 5173 -State Listen
```

### 백엔드 실행

```powershell
cd C:\SeoS\project-delivery-reference

. .\scripts\use-pmis-java21.ps1

cd .\pmis-backend

.\gradlew bootRun
```

빌드:

```powershell
.\gradlew clean build
```

### 프론트엔드 실행

```powershell
cd C:\SeoS\project-delivery-reference

. .\scripts\use-pmis-node24.ps1

npm ci --prefix .\pmis-frontend

npm run dev --prefix .\pmis-frontend
```

빌드:

```powershell
npm run build --prefix .\pmis-frontend
```

- [ ] 009와 010의 실행 명령어가 동일한 프로젝트 경로를 사용한다.
- [ ] Gradle 명령어가 실행 위치에 맞게 작성되어 있다.
- [ ] npm 명령어가 프로젝트 루트 기준으로 작성되어 있다.
- [ ] 백엔드와 프론트엔드를 별도 터미널에서 실행하도록 안내한다.
- [ ] 데이터베이스 → 백엔드 → 프론트엔드 순서가 일치한다.
- [ ] 신규 PC에서 환경변수 설정 후 백엔드를 실행하도록 안내한다.
- [ ] 기존 PC의 DB 접속 설정과 신규 PC의 환경변수 설정을 구분한다.

## 8. 명령어 및 설정 충돌 집중 점검

다음 항목은 최종 문서 수정 시 우선적으로 확인해야 한다.

| 번호 | 점검 대상 | 확인 및 수정 기준 |
|---|---|---|
| 1 | 009·010의 URL 예시 | PowerShell 코드 블록에는 Markdown 링크가 아닌 순수 URL을 사용한다. |
| 2 | 009·010의 DB 계정 | 기존 PC root와 신규 PC pmis_app을 구분한다. |
| 3 | 009의 환경변수 확인 | 비밀번호 값은 출력하지 않고 설정 여부만 확인한다. |
| 4 | 010의 MariaDB 서비스 확인 | 신규 PC에서 서비스 이름이 다를 수 있음을 반영한다. |
| 5 | 008의 SSH 인증 예시 | 개인 계정과 포트폴리오 계정의 예상 사용자명을 구분한다. |
| 6 | 008의 SSH 키 생성 | 기존 키 덮어쓰기 방지와 계정별 파일 경로를 명확히 한다. |
| 7 | 010의 시스템 버전 확인 | 시스템 기본 버전 확인과 PMIS 전용 버전 활성화를 구분한다. |
| 8 | 009·010의 백엔드 주소 | `localhost:8765`를 사용한다. |
| 9 | 009·010의 프론트엔드 주소 | `localhost:5173`을 사용한다. |
| 10 | 001~010의 Java 정책 | 시스템 기본 17, PMIS 전용 21을 유지한다. |
| 11 | 001~010의 Node 정책 | 시스템 기본 20, PMIS 전용 24를 유지한다. |
| 12 | JPA 테이블 생성 설명 | DB 생성과 테이블 자동 생성을 구분한다. |

### 환경변수 확인 예시

다음 명령어는 비밀번호를 화면에 출력하지 않고 환경변수 존재 여부만 확인한다.

```powershell
Test-Path Env:SPRING_DATASOURCE_URL
Test-Path Env:SPRING_DATASOURCE_USERNAME
Test-Path Env:SPRING_DATASOURCE_PASSWORD
```

환경변수 이름과 적용 방식은 실제 `application.yaml` 또는 `application.yml` 설정과 일치해야 한다.

### PowerShell 명령어 작성 기준

- [ ] PowerShell 5.1에서 실행 가능한 명령어를 사용한다.
- [ ] 경로에 공백이 있으면 큰따옴표로 감싼다.
- [ ] 줄바꿈 연결은 PowerShell의 백틱을 사용하되 불필요한 사용을 줄인다.
- [ ] 경로 예시는 Windows PowerShell에서 실제로 해석되는 형태로 작성한다.
- [ ] Markdown 링크 문법을 명령어 안에 넣지 않는다.

## 9. GitHub 대용량 파일 배포 점검

현재 프로젝트에는 개발 환경 재현을 위한 설치 자산이 포함되어 있다.

| 파일 또는 자산 | 확인된 상태 | 관리 기준 |
|---|---|---|
| Claude Code VSIX | 약 158MB | GitHub 일반 파일 제한 고려 |
| Node.js 24 패키지 | `Tools/node-v24.19.0-win-x64` | 실제 파일 크기 및 배포 방식 확인 |

- [ ] Claude Code VSIX 파일을 임의로 삭제하지 않는다.
- [ ] Node.js 24 패키지를 임의로 삭제하지 않는다.
- [ ] GitHub 일반 Git 파일의 100MB 제한을 고려한다.
- [ ] 100MB 초과 파일은 Git LFS 또는 GitHub Releases 등 별도 배포 방식을 검토한다.
- [ ] Git LFS 사용 시 저장 용량과 전송량 제한을 확인한다.
- [ ] 저장소에 파일 경로만 남고 실제 파일은 다운로드되지 않는 상황을 방지한다.
- [ ] 신규 PC에서 설치 자산을 실제로 확보할 수 있는지 검증한다.

## 10. 문서 간 상호 참조 점검

| 출발 문서 | 참조 대상 | 참조 목적 |
|---|---|---|
| 001 | 002~010 | 전체 환경 구축 절차 |
| 002 | 003, 004 | 프로젝트별 Java 및 Node 환경 |
| 003 | 006, 009, 010 | Java 개발 및 실행 환경 |
| 004 | 006, 009, 010 | Node.js 개발 및 실행 환경 |
| 005 | 009, 010 | 데이터베이스 실행 및 검증 |
| 006 | 007, 009 | IDE 및 개발 도구 |
| 007 | 006, 010 | Claude Code 설치 및 검증 |
| 008 | 010 | GitHub 인증 및 저장소 검증 |
| 009 | 005, 010 | PMIS 실행 및 문제 해결 |
| 010 | 001~009 | 전체 환경 검증 및 관련 절차 |

- [ ] 문서에서 참조하는 파일이 실제로 존재한다.
- [ ] 파일명에 오타가 없다.
- [ ] 상대 경로와 문서 경로가 일치한다.
- [ ] 이미 삭제되었거나 이름이 변경된 파일을 참조하지 않는다.
- [ ] 다른 문서의 설명과 상충하는 설정이 없다.

## 11. 최종 빌드 및 실행 검증 기준

### 백엔드

```powershell
cd C:\SeoS\project-delivery-reference

. .\scripts\use-pmis-java21.ps1

.\pmis-backend\gradlew -p .\pmis-backend clean build
```

- [ ] JDK 21 활성화 성공
- [ ] Gradle 9.5.1 확인
- [ ] 백엔드 clean build 성공
- [ ] MariaDB 연결 성공
- [ ] JPA 초기화 성공

### 프론트엔드

```powershell
cd C:\SeoS\project-delivery-reference

. .\scripts\use-pmis-node24.ps1

npm ci --prefix .\pmis-frontend

npm run build --prefix .\pmis-frontend
```

- [ ] Node.js 24 활성화 성공
- [ ] npm 11.17.0 확인
- [ ] 의존성 설치 성공
- [ ] Vite 빌드 성공
- [ ] `pmis-frontend/dist/` 생성 확인

### ESLint 별도 확인

```powershell
npm run lint --prefix .\pmis-frontend
```

기존 PC에서는 ESLint 오류 29건과 경고 3건이 확인된 이력이 있다.

ESLint 검사 실패를 Node.js 설치 실패 또는 프론트엔드 빌드 실패와 동일하게 취급하지 않는다.

- [ ] 빌드 결과와 ESLint 검사 결과를 구분한다.
- [ ] 기존 코드 품질 이슈를 환경 구축 실패로 오인하지 않는다.

### 전체 시스템

- [ ] MariaDB `pmis` 접속 성공
- [ ] 백엔드 `8765` 포트 실행 성공
- [ ] Swagger UI 접속 성공
- [ ] 프론트엔드 `5173` 포트 실행 성공
- [ ] 브라우저에서 PMIS 화면 표시
- [ ] 프론트엔드와 백엔드 API 통신 확인
- [ ] 브라우저 Console 및 Network 오류 확인

## 12. 최종 확정 전 필수 수정 항목

다음 항목은 문서 전체를 최종 확정하기 전에 반드시 확인한다.

- [ ] 008에 문서 경로, 버전, 작성일 메타데이터 추가
- [ ] 008의 개인 계정과 포트폴리오 계정 SSH 인증 예시 구분
- [ ] 008의 SSH 키 생성 시 기존 키 덮어쓰기 방지 안내
- [ ] 009의 실행 명령어에 Markdown 링크가 포함되지 않았는지 확인
- [ ] 009의 신규 PC DB 계정과 기존 PC DB 계정 구분
- [ ] 009의 환경변수 확인 절차 보완
- [ ] 010에 문서 경로, 버전, 작성일 메타데이터 추가
- [ ] 010의 `Invoke-WebRequest` URL을 순수 URL로 수정
- [ ] 010의 신규 PC MariaDB 서비스명 및 설치 경로 안내 보완
- [ ] 001~010의 모든 포트가 3306 / 8765 / 5173으로 통일되었는지 확인
- [ ] 001~010의 Java 및 Node.js 버전 정책 확인
- [ ] 대용량 설치 자산의 신규 PC 배포 경로 확인

---

## 13. 최종 검수 결과

| 검수 구분 | 결과 |
|---|---|
| 문서 버전 | [ ] 완료 |
| 프로젝트 경로 | [ ] 완료 |
| Java / Node.js 정책 | [ ] 완료 |
| MariaDB 설정 | [ ] 완료 |
| VS Code / Claude Code | [ ] 완료 |
| Git / GitHub SSH | [ ] 완료 |
| PMIS 실행 설정 | [ ] 완료 |
| 명령어 및 설정 충돌 | [ ] 완료 |
| 대용량 파일 배포 | [ ] 완료 |
| 문서 간 상호 참조 | [ ] 완료 |
| 백엔드 / 프론트엔드 빌드 | [ ] 완료 |
| 전체 시스템 통합 확인 | [ ] 완료 |

**최종 확정 기준**

위 항목의 필수 수정 사항을 반영하고, 실제 개발 PC에서 백엔드와 프론트엔드의 빌드 및 실행을 확인한 후 001~010 문서를 최종 확정한다.

문서 검수 완료와 신규 PC에서의 재현 검증 완료는 별개의 상태로 관리한다.