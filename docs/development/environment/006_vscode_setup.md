# Visual Studio Code 개발환경 구축 가이드

- **문서 경로:** `docs/development/environment/006_vscode_setup.md`
- **문서 버전:** 1.1
- **작성일:** 2026-11-06
- **기준 IDE:** Visual Studio Code 1.145.1
- **운영체제:** Windows 10 Pro

---

## 1. 목적

본 문서는 PMIS 개발을 위한 Visual Studio Code 설치 및 프로젝트 개발환경 설정 방법을 설명한다.

VS Code를 이용하여 Spring Boot 백엔드와 React 프론트엔드를 통합 개발할 수 있도록 기본 설정과 주요 확장을 구성한다.

주요 내용은 다음과 같다.

- Visual Studio Code 설치 및 버전 확인
- PMIS 프로젝트 작업공간 구성
- Java 및 프론트엔드 개발 확장 설치
- Java 21 및 Node.js 24 연동
- VS Code 설정 및 테마 관리
- 설치된 확장 목록 확인 및 관리

## 2. Visual Studio Code 설치

### 2.1 현재 개발 PC 설치 정보

| 항목 | 확인 결과 |
|---|---|
| IDE | Visual Studio Code |
| 버전 | 1.145.1 |
| 설치 경로 | `C:\Program Files\Microsoft VS Code` |
| 실행 파일 | `code.cmd` |
| 아키텍처 | x64 |

### 2.2 설치 파일 확보

신규 PC에서는 공식 사이트에서 Windows용 VS Code 설치 파일을 내려받는다.

공식 사이트:

[Visual Studio Code 공식 사이트](https://code.visualstudio.com/)

설치 시 다음 항목을 확인한다.

1. Windows x64 설치 파일 선택
2. VS Code 설치
3. PATH 등록 옵션 활성화 권장
4. 설치 완료 후 VS Code 실행

### 2.3 설치 확인

PowerShell에서 다음 명령어를 실행한다.

```powershell
code --version
```

현재 개발 PC에서 확인한 결과:

```text
1.145.1
6bab64e2d12b04d04c0d99c27ff166023efd1dda
x64
```

실행 파일 경로 확인:

```powershell
(Get-Command code).Source
```

신규 PC에서는 설치한 VS Code 버전과 실행 파일 경로를 확인한다.

## 3. PMIS 프로젝트 열기

### 3.1 프로젝트 루트 열기

PowerShell에서 프로젝트 저장소 루트로 이동한다.

```powershell
cd C:\SeoS\project-delivery-reference
```

VS Code 실행:

```powershell
code .
```

프로젝트 루트를 열면 다음 디렉터리를 하나의 작업공간에서 관리할 수 있다.

- `pmis-backend`
- `pmis-frontend`
- `docs`
- `scripts`
- `Tools`

### 3.2 프로젝트 설정 파일

프로젝트별 VS Code 설정은 다음 경로에서 관리한다.

```text
.vscode/
└── settings.json
```

현재 개발 PC에서 확인한 주요 설정:

```json
{
  "java.configuration.updateBuildConfiguration": "automatic"
}
```

해당 설정은 Java 프로젝트의 빌드 구성 변경을 자동으로 반영하기 위한 것이다.

개인별 테마, 폰트, 편집기 설정 등은 사용자 설정과 프로젝트 공통 설정을 구분하여 관리한다.

프로젝트 공통 설정에는 특정 PC에만 존재하는 경로나 개인별 환경설정을 무분별하게 포함하지 않는다.

## 4. VS Code 확장 설치

### 4.1 설치된 확장 현황

2026-11-06 기준 현재 개발 PC에는 총 48개의 VS Code 확장이 설치되어 있다.

PMIS 개발에 직접 필요한 확장과 선택 확장을 구분한다.

모든 설치 확장을 신규 PC에 동일하게 설치할 필요는 없다.

### 4.2 PMIS 주요 확장

현재 개발 PC에서 확인한 주요 확장과 버전은 다음과 같다.

| 확장 ID | 확장 이름 | 확인 버전 | 용도 |
|---|---|---:|---|
| `redhat.java` | Language Support for Java by Red Hat | 1.58.0 | Java 언어 지원 |
| `vscjava.vscode-java-pack` | Extension Pack for Java | 0.31.2 | Java 개발 통합 지원 |
| `vscjava.vscode-gradle` | Gradle for Java | 3.18.2026102002 | Gradle 빌드 관리 |
| `vmware.vscode-boot-dev-pack` | Spring Boot Extension Pack | 0.2.2 | Spring Boot 개발 지원 |
| `dbaeumer.vscode-eslint` | ESLint | 3.0.34 | 프론트엔드 정적 검사 |
| `esbenp.prettier-vscode` | Prettier | 12.4.0 | 코드 포맷팅 |

### 4.3 선택 확장

다음 확장은 개발 편의성에 따라 선택적으로 설치한다.

| 확장 ID | 용도 |
|---|---|
| `ms-ceintl.vscode-language-pack-ko` | 한국어 UI |
| `pkief.material-icon-theme` | 파일 아이콘 테마 |
| `sdras.night-owl` | 코드 색상 테마 |
| `gruntfuggly.todo-tree` | TODO 주석 관리 |
| `mechatroner.rainbow-csv` | CSV 파일 보기 |
| `streetsidesoftware.code-spell-checker` | 영문 맞춤법 검사 |
| `ritwickdey.liveserver` | 정적 웹 페이지 미리보기 |
| `oderwat.indent-rainbow` | 들여쓰기 시각화 |
| `shardulm94.trailing-spaces` | 후행 공백 확인 |

Python, Docker, Go, Vue, Svelte 등 다른 프로젝트용 확장은 PMIS 필수 설치 대상에서 제외한다.

Claude Code 확장의 설치 및 사용 조건은 007번 문서에서 별도로 관리한다.

## 5. 확장 설치 방법

### 5.1 VS Code 확장 메뉴

1. VS Code 실행
2. 왼쪽 Extensions 메뉴 선택
3. 확장 ID 또는 확장 이름 검색
4. Install 선택
5. 설치 완료 여부 확인

### 5.2 명령어를 이용한 설치

PowerShell에서 다음 명령어를 사용할 수 있다.

현재 개발 PC에서 확인한 버전으로 설치하는 예시:

Java 개발 확장:

```powershell
code --install-extension redhat.java@1.58.0
code --install-extension vscjava.vscode-java-pack@0.31.2
code --install-extension vscjava.vscode-gradle@3.18.2026102002
code --install-extension vmware.vscode-boot-dev-pack@0.2.2
```

프론트엔드 개발 확장:

```powershell
code --install-extension dbaeumer.vscode-eslint@3.0.34
code --install-extension esbenp.prettier-vscode@12.4.0
```

선택 확장:

```powershell
code --install-extension ms-ceintl.vscode-language-pack-ko
code --install-extension pkief.material-icon-theme
```

※ 확장 버전은 2026-11-06 현재 개발 PC에서 확인한 값이다. 신규 PC에서 해당 버전을 설치할 수 없는 경우 Marketplace에서 제공하는 버전을 확인하고 설치한다.

※ Extension Pack은 여러 종속 확장을 함께 설치할 수 있으므로 개별 확장의 실제 설치 여부와 버전을 별도로 확인한다.

## 6. Java 및 Gradle 연동

PMIS 백엔드는 JDK 21과 Gradle Wrapper를 사용한다.

### 6.1 Java 환경 활성화

PMIS 프로젝트 루트에서 다음 명령어를 실행한다.

```powershell
. .\scripts\use-pmis-java21.ps1
```

해당 스크립트는 현재 PowerShell 세션에서 PMIS 개발용 Java 21 환경을 활성화하기 위한 것이다.

기존 개발 PC에서는 다른 프로젝트에서 JDK 17을 사용하므로 시스템 또는 사용자 전역 `JAVA_HOME`은 JDK 17로 유지한다.

PMIS 작업 시에만 프로젝트 전용 스크립트로 JDK 21을 활성화한다.

### 6.2 Gradle 실행 확인

```powershell
.\pmis-backend\gradlew --version
```

Gradle 버전과 Launcher JVM, Daemon JVM이 PMIS 기준 환경과 일치하는지 확인한다.

현재 PMIS 백엔드는 Gradle 9.5.1과 Java 21을 기준으로 구성되어 있다.

### 6.3 VS Code Java Runtime 구분

VS Code Java 확장이 내부적으로 사용하는 Java Runtime과 PMIS 백엔드 실행에 사용하는 JDK는 서로 다를 수 있다.

현재 개발 PC에서도 VS Code Java 확장의 내부 Runtime 경로가 별도로 존재하는 것을 확인했다.

따라서 Java 확장 내부 Runtime 버전만으로 PMIS 백엔드의 Java 실행환경을 판단하지 않는다.

실제 프로젝트 실행환경은 다음 항목으로 확인한다.

- `JAVA_HOME`
- `java -version`
- `gradlew --version`
- Gradle Toolchain 설정

## 7. 프론트엔드 개발환경 연동

### 7.1 Node.js 환경 활성화

PMIS 프로젝트 루트에서 다음 명령어를 실행한다.

```powershell
. .\scripts\use-pmis-node24.ps1
```

해당 스크립트는 현재 PowerShell 세션에서 PMIS 개발용 Node.js 24 환경을 활성화하기 위한 것이다.

기존 개발 PC의 시스템 기본 Node.js 20 환경은 유지하고 PMIS 작업 시에만 Node.js 24를 사용한다.

### 7.2 프론트엔드 실행

```powershell
npm run dev --prefix .\pmis-frontend
```

### 7.3 프론트엔드 빌드

```powershell
npm run build --prefix .\pmis-frontend
```

### 7.4 ESLint 및 Prettier

- ESLint: 코드 오류 및 규칙 위반 검사
- Prettier: 코드 포맷팅

두 확장은 서로 다른 목적을 가진다.

프론트엔드 개발 시 ESLint와 Prettier를 각각의 목적에 맞게 사용한다.

### 7.5 Java와 Node.js를 같은 PowerShell 세션에서 사용하는 경우

백엔드와 프론트엔드 작업을 동일한 PowerShell 세션에서 진행해야 한다면 두 환경 활성화 스크립트를 모두 실행한다.

```powershell
. .\scripts\use-pmis-java21.ps1
. .\scripts\use-pmis-node24.ps1
```

각 스크립트는 프로젝트 개발용 환경을 현재 세션에 적용하며, 시스템 전역 환경변수를 변경하는 용도로 사용하지 않는다.

## 8. VS Code 테마 설정

테마는 개인별 개발 편의에 따라 선택한다.

설정 방법:

1. `Ctrl + Shift + P` 입력
2. `Preferences: Color Theme` 검색
3. 원하는 테마 선택

현재 개발 PC에서는 `Dark Modern` 테마를 사용하도록 설정했다.

테마는 프로젝트 실행에 영향을 주지 않는 개인 설정으로 관리한다.

## 9. 설치된 확장 목록 확인 및 백업

### 9.1 설치된 확장 목록 확인

```powershell
code --list-extensions --show-versions
```

### 9.2 확장 목록 파일 저장

```powershell
code --list-extensions --show-versions |
    Out-File .\vscode-extensions.txt -Encoding utf8
```

이 파일은 현재 PC의 전체 확장 설치 현황을 기록하는 용도다.

전체 확장 목록에는 PMIS와 무관한 다른 프로젝트용 확장도 포함될 수 있다.

따라서 신규 PC에서는 전체 목록을 무조건 설치하지 않고 4장에서 정의한 PMIS 주요 확장과 필요한 선택 확장을 기준으로 설치한다.

`vscode-extensions.txt`는 로컬 환경 확인용 파일이며, 프로젝트에서 별도로 관리하기로 결정하지 않는 한 Git 커밋 대상에 포함하지 않는다.

## 10. 문제 해결

### 10.1 `code` 명령어를 찾을 수 없는 경우

다음 명령어로 VS Code 실행 파일 경로를 확인한다.

```powershell
Test-Path "C:\Program Files\Microsoft VS Code\bin\code.cmd"
```

설치되어 있지만 명령어가 인식되지 않으면 VS Code 설치 경로가 PATH에 등록되어 있는지 확인한다.

### 10.2 Java 프로젝트가 인식되지 않는 경우

다음 항목을 확인한다.

1. Java 확장 설치 여부
2. `pmis-backend` 프로젝트 경로
3. JDK 21 활성화 여부
4. Gradle Wrapper 실행 여부
5. VS Code Java 프로젝트 가져오기 상태

### 10.3 확장 API 관련 경고

VS Code 또는 확장 업데이트 과정에서 API 호환성 경고가 출력될 수 있다.

경고가 발생했다고 해서 반드시 프로젝트 개발환경에 문제가 있는 것은 아니다.

확장 버전과 실제 기능 동작 여부를 확인하고 필요한 경우 해당 확장을 업데이트한다.

### 10.4 Java Runtime 버전 관련 혼동

VS Code Java 확장이 사용하는 내부 Runtime과 PMIS 백엔드 실행용 JDK가 다를 수 있다.

Java 확장 내부 Runtime의 버전이 JDK 21과 다르다는 이유만으로 PMIS 실행환경에 문제가 있다고 판단하지 않는다.

실제 백엔드 실행환경은 프로젝트 전용 Java 활성화 스크립트와 Gradle 실행 결과를 기준으로 확인한다.

---

## 11. 확인 완료 기준

- [ ] VS Code 설치 확인
- [ ] VS Code 버전 확인
- [ ] `code .` 명령어로 프로젝트 열기 확인
- [ ] `.vscode/settings.json` 확인
- [ ] Java 개발 확장 설치 확인
- [ ] Gradle 확장 설치 확인
- [ ] Spring Boot 확장 설치 확인
- [ ] ESLint 설치 확인
- [ ] Prettier 설치 확인
- [ ] Java 21 및 Gradle 연동 확인
- [ ] Node.js 24 및 프론트엔드 연동 확인
- [ ] 확장 목록 출력 확인

---

**관리 원칙:** VS Code 확장은 프로젝트 개발에 필요한 필수 확장과 개인별 선택 확장을 구분한다. 확장 버전 및 설치 목록은 개발환경 변경 시 갱신한다. 시스템 전역 Java 및 Node.js 설정은 다른 프로젝트와의 호환성을 고려하여 유지하고, PMIS 전용 환경은 프로젝트별 활성화 스크립트로 관리한다.