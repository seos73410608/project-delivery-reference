# PMIS Development Environment Overview

- **문서 경로:** `docs/development/environment/001_development_environment_overview.md`
- **문서 버전:** 1.1
- **작성일:** 2026-11-06
- **대상 프로젝트:** PMIS (Project Management Information System)
- **기준 운영체제:** Windows 10 Pro

---

## 1. 문서 목적

본 문서는 PMIS 개발환경을 신규 PC에서도 동일하게 구축하고 실행할 수 있도록 개발환경 구성 요소, 버전, 설치 순서 및 관련 문서의 기준을 정의한다.

개발환경 구축 과정에서 발생할 수 있는 버전 충돌과 설정 오류를 줄이고, 재현 가능한 개발환경을 확보하는 것을 목적으로 한다.

## 2. 개발환경 구성

PMIS는 프론트엔드와 백엔드가 분리된 웹 애플리케이션 구조로 구성한다.

| 구분 | 기술 및 도구 | 용도 |
|---|---|---|
| OS | Windows 10 Pro | 개발 운영체제 |
| IDE | Visual Studio Code 1.145.1 | 통합 개발환경 |
| Backend | Java 21, Spring Boot | REST API 서버 |
| Build Tool | Gradle Wrapper 9.5.1 | 백엔드 빌드 및 의존성 관리 |
| Frontend | React, TypeScript, Vite | 웹 사용자 인터페이스 |
| Runtime | Node.js 24.19.0 | 프론트엔드 개발 및 빌드 |
| Package Manager | npm 11.17.0 | 프론트엔드 패키지 관리 |
| Database | MariaDB 10.6.5 | 데이터 저장소 |
| Version Control | Git 2.34.0 | 소스코드 형상관리 |
| Remote Repository | GitHub | 원격 저장소 |
| AI Coding Assistant | Claude Code VS Code Extension | AI 기반 개발 지원 |
| Shell | Windows PowerShell 5.1 | 개발환경 명령 실행 |

※ 위 버전은 2026-11-06 기준 개발 PC에서 확인한 정보다. 신규 PC 구축 시에는 프로젝트에서 지정한 버전을 우선 적용한다.

## 3. 프로젝트 디렉터리 구조

프로젝트 저장소의 주요 디렉터리는 다음과 같다.

```text
project-delivery-reference/
├── .vscode/                 # VS Code 프로젝트 설정
├── docs/                    # 프로젝트 문서
│   └── development/
│       └── environment/     # 개발환경 구축 매뉴얼
├── http/                    # API 테스트 관련 파일
├── pmis/                    # 기존 프로젝트 영역
├── pmis-backend/            # Spring Boot 백엔드
├── pmis-frontend/           # React 프론트엔드
├── scripts/                 # 개발환경 실행 스크립트
├── Tools/                   # 프로젝트 관리 도구 및 설치 파일
└── README.md
```

## 4. Java 및 Node.js 버전 관리 정책

PMIS 개발환경은 다른 프로젝트와의 실행환경 충돌을 방지하기 위해 프로젝트 전용 버전을 사용한다.

### 4.1 Java

- 시스템 기본 Java는 JDK 17을 유지한다.
- PMIS 백엔드는 JDK 21을 사용한다.
- JDK 21 설정은 PMIS 전용 PowerShell 세션에서 적용한다.
- 시스템 전체 또는 사용자 전체의 `JAVA_HOME`을 변경하지 않는다.
- Gradle은 프로젝트에 포함된 Wrapper를 사용한다.

관련 스크립트:

```powershell
. .\scripts\use-pmis-java21.ps1
```

### 4.2 Node.js

- 시스템 기본 Node.js는 20.12.2를 사용한다.
- PMIS 프론트엔드는 프로젝트에 포함된 Node.js 24.19.0을 사용한다.
- Node.js 설정은 PMIS 전용 PowerShell 세션에서 적용한다.
- 시스템 전체의 Node.js 설치 및 PATH를 변경하지 않는다.
- npm은 PMIS 전용 Node.js 환경에 포함된 버전을 사용한다.

관련 스크립트:

```powershell
. .\scripts\use-pmis-node24.ps1
```

## 5. 개발환경 구축 순서

신규 PC에서는 다음 순서로 개발환경을 구축한다.

| 순서 | 문서 | 주요 작업 |
|---|---|---|
| 1 | 002_windows_setup.md | Windows 기본 환경 확인 |
| 2 | 003_java_setup.md | JDK 설치 및 Java 환경 설정 |
| 3 | 004_nodejs_setup.md | Node.js 환경 설정 |
| 4 | 005_database_setup.md | MariaDB 설치 및 데이터베이스 구성 |
| 5 | 006_vscode_setup.md | VS Code 및 확장 설치 |
| 6 | 007_claude_code_setup.md | Claude Code 확장 설치 및 인증 조건 확인 |
| 7 | 008_git_github_setup.md | Git 및 GitHub SSH 인증 |
| 8 | 009_pmis_execution_guide.md | PMIS 백엔드·프론트엔드 실행 |
| 9 | 010_environment_verification_and_troubleshooting.md | 환경 검증 및 문제 해결 |

## 6. 개발환경 실행 원칙

1. 모든 작업은 프로젝트 저장소 루트 디렉터리를 기준으로 수행한다.
2. 백엔드와 프론트엔드는 각각 독립적으로 실행할 수 있어야 한다.
3. Java와 Node.js는 프로젝트 전용 스크립트를 통해 활성화한다.
4. Gradle은 프로젝트에 포함된 Wrapper를 사용한다.
5. Git 작업은 정해진 브랜치 전략과 커밋 메시지 규칙을 준수한다.
6. 비밀번호, API Key, SSH 개인키 등 민감한 정보는 Git 저장소에 포함하지 않는다.
7. 실제 확인되지 않은 설정은 확정된 환경 구성으로 기록하지 않는다.
8. 신규 PC에서는 데이터베이스를 준비한 후 백엔드를 실행한다. 프로젝트의 JPA 설정에 따라 필요한 테이블이 자동 생성된다.

## 7. 서비스 실행 및 포트 구성

PMIS의 기본 실행 포트는 다음과 같다.

| 구분 | 포트 | 접속 주소 |
|---|---:|---|
| Backend API | 8765 | `http://localhost:8765` |
| Swagger UI | 8765 | `http://localhost:8765/swagger-ui/index.html` |
| Frontend | 5173 | `http://localhost:5173` |
| MariaDB | 3306 | `localhost:3306` |

※ 포트는 현재 프로젝트의 개발환경 기준이다. 설정 변경 시 관련 문서도 함께 갱신한다.

기본 실행 순서는 다음과 같다.

1. MariaDB 서비스 및 `pmis` 데이터베이스 준비
2. PMIS 전용 Java 환경 활성화 후 백엔드 실행
3. PMIS 전용 Node.js 환경 활성화 후 프론트엔드 실행

## 8. Git 브랜치 및 커밋 정책

### 8.1 브랜치 전략

Git Flow를 기반으로 운영한다.

```text
main
 └── develop
      ├── feature/*
      ├── fix/*
      └── release/*
```

- `main`: 안정화된 배포 기준 브랜치
- `develop`: 개발 통합 브랜치
- `feature/*`: 기능 개발 브랜치
- `fix/*`: 오류 수정 브랜치
- `release/*`: 릴리스 준비 브랜치

### 8.2 커밋 메시지 규칙

| Prefix | 용도 |
|---|---|
| feat | 기능 추가 |
| fix | 오류 수정 |
| refactor | 코드 구조 개선 |
| style | 코드 스타일 변경 |
| test | 테스트 추가 및 수정 |
| docs | 문서 변경 |
| build | 빌드 및 의존성 변경 |
| chore | 기타 유지보수 |
| perf | 성능 개선 |
| security | 보안 관련 변경 |

## 9. 보안 및 환경설정 관리

다음 정보는 소스코드 저장소에 평문으로 포함하지 않는다.

- 데이터베이스 비밀번호
- API 인증 키
- SSH 개인키 및 개인키 암호
- 외부 서비스 인증 정보

신규 PC에서는 개발환경 구축 과정에서 각자의 인증 정보를 안전하게 설정한다.

현재 PC의 기존 설정과 신규 PC에 권장하는 보안 설정은 별도 문서에서 구분하여 설명한다.

현재 백엔드 설정에 인증 정보가 직접 기재되어 있는 경우, 이를 신규 PC의 권장 설정으로 간주하지 않는다. Git에 커밋하기 전 환경변수 등 안전한 설정 방식으로 분리하고 민감정보가 저장소에 포함되지 않았는지 확인한다.

Claude Code는 VS Code 확장 설치와 실제 계정 인증 및 사용 권한을 구분한다. 확장이 설치되어 있더라도 계정의 구독 또는 사용 권한에 따라 실제 기능 사용이 제한될 수 있다.

## 10. 관련 문서

- `002_windows_setup.md`
- `003_java_setup.md`
- `004_nodejs_setup.md`
- `005_database_setup.md`
- `006_vscode_setup.md`
- `007_claude_code_setup.md`
- `008_git_github_setup.md`
- `009_pmis_execution_guide.md`
- `010_environment_verification_and_troubleshooting.md`

---

**문서 관리:** 개발환경 구성 변경 시 본 문서와 관련 구축 매뉴얼을 함께 검토하고 갱신한다.