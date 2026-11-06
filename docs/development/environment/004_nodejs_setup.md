# Node.js 개발환경 구축 가이드

- **문서 경로:** `docs/development/environment/004_nodejs_setup.md`
- **문서 버전:** 1.1
- **작성일:** 2026-11-06
- **기준 Node.js 버전:** 24.19.0
- **기준 npm 버전:** 11.17.0

---

## 1. 목적

본 문서는 PMIS 프론트엔드 개발에 필요한 Node.js 및 npm 환경을 구축하는 방법을 설명한다.

PMIS 프론트엔드는 Node.js 24.19.0을 기준으로 개발한다.

기존 개발 프로젝트와의 호환성을 위해 시스템에 설치된 Node.js 버전은 유지하고, PMIS 프로젝트에서만 별도로 보관한 Node.js 실행환경을 사용한다.

## 2. Node.js 설치 현황

### 2.1 현재 개발 PC의 Node.js 환경

| 구분 | 설치 경로 | 버전 | 용도 |
|---|---|---|---|
| 시스템 Node.js | `C:\Program Files\nodejs` | 20.12.2 | 기존 프로젝트 |
| PMIS Node.js | `Tools\node-v24.19.0-win-x64` | 24.19.0 | PMIS 프론트엔드 |

npm 버전은 다음과 같다.

| 구분 | npm 버전 |
|---|---|
| 시스템 npm | 10.5.0 |
| PMIS npm | 11.17.0 |

※ 위 버전은 2026-11-06 기준 현재 개발 PC에서 확인한 결과다.

## 3. Node.js 설치 파일 구성

### 3.1 PMIS 전용 Node.js 패키지

PMIS 프로젝트에서는 Windows x64용 Node.js 압축 패키지를 사용한다.

프로젝트 내부 경로:

```text
Tools/
└── node-v24.19.0-win-x64/
    ├── node.exe
    ├── npm
    ├── npm.cmd
    ├── npx
    ├── npx.cmd
    └── ...
```

압축 패키지를 사용하므로 별도의 시스템 설치 과정 없이 압축을 해제한 디렉터리에서 Node.js를 실행할 수 있다.

### 3.2 신규 PC 구축 방법

1. PMIS Git 저장소를 내려받는다.
2. `Tools/node-v24.19.0-win-x64` 디렉터리가 존재하는지 확인한다.
3. `node.exe`와 `npm.cmd` 파일이 있는지 확인한다.
4. 프로젝트 전용 환경 설정 스크립트를 실행한다.
5. Node.js 및 npm 버전을 확인한다.

Node.js 패키지가 저장소에 포함되지 않은 경우에는 공식 배포처에서 동일 버전의 Windows x64 ZIP 패키지를 확보한 후 프로젝트에서 지정한 경로에 압축을 해제한다.

공식 배포처:

- [Node.js 공식 다운로드](https://nodejs.org/en/download)

※ 설치 파일과 실행 파일의 버전 및 무결성을 확인한 후 사용한다. 신규 PC에서도 프로젝트에서 지정한 버전을 유지한다.

## 4. PMIS 전용 Node.js 환경 설정

### 4.1 환경 설정 스크립트

PMIS 프로젝트에는 다음 스크립트가 포함되어 있다.

```text
scripts/use-pmis-node24.ps1
```

해당 스크립트는 다음 작업을 수행한다.

- Node.js 24 설치 디렉터리 확인
- 현재 PowerShell 세션의 `Path` 앞부분에 Node.js 경로 추가
- Node.js 버전 출력
- npm 버전 출력

### 4.2 Node.js 환경 활성화

프로젝트 저장소 루트에서 다음 명령어를 실행한다.

```powershell
. .\scripts\use-pmis-node24.ps1
```

정상적으로 활성화되면 다음과 같은 결과가 출력된다.

```text
PMIS Node.js 24 environment activated.

Node.js path: C:\SeoS\project-delivery-reference\Tools\node-v24.19.0-win-x64

Node.js version:
v24.19.0

npm version:
11.17.0
```

### 4.3 환경변수 확인

다음 명령어로 Node.js와 npm 버전을 확인한다.

```powershell
node --version
npm --version
```

실제 실행 파일 경로도 확인한다.

```powershell
(Get-Command node).Source
(Get-Command npm.cmd).Source
```

실행 파일 경로가 프로젝트의 `Tools\node-v24.19.0-win-x64`를 가리키는지 확인한다.

※ 환경 설정은 현재 PowerShell 세션에만 적용된다. 새 PowerShell 창을 열면 스크립트를 다시 실행해야 한다.

## 5. 프론트엔드 패키지 관리

### 5.1 package.json

PMIS 프론트엔드는 `pmis-frontend/package.json`을 기준으로 패키지를 관리한다.

주요 기술 구성:

| 항목 | 기술 |
|---|---|
| UI Library | React 19 |
| Language | TypeScript |
| Build Tool | Vite 8 |
| Router | React Router 7 |
| HTTP Client | Axios |
| Package Manager | npm |

### 5.2 package-lock.json

프로젝트의 `package-lock.json`은 의존성 버전을 일관되게 유지하기 위한 파일이다.

신규 PC에서는 다음 명령어로 기존 의존성 구성을 재현한다.

```powershell
npm ci --prefix .\pmis-frontend
```

- `npm ci`는 `package-lock.json`을 기준으로 의존성을 설치한다.
- 기존 `node_modules`를 재구성한다.
- `package.json`과 `package-lock.json`이 일치해야 한다.
- 일반적인 개발환경 구축에서는 `npm install`보다 `npm ci`를 우선 사용한다.

## 6. 프론트엔드 실행 및 빌드

### 6.1 개발 서버 실행

프로젝트 루트에서 다음 명령어를 실행한다.

```powershell
npm run dev --prefix .\pmis-frontend
```

정상적으로 실행되면 Vite 개발 서버 주소가 출력된다.

기본 주소:

`http://localhost:5173`

개발 서버를 종료하려면 실행 중인 PowerShell 창에서 `Ctrl + C`를 누른다.

### 6.2 프로덕션 빌드

```powershell
npm run build --prefix .\pmis-frontend
```

현재 개발 PC에서 확인한 빌드 결과:

- Vite 8.2.1
- 181 modules transformed
- `dist` 디렉터리 생성
- 빌드 성공

※ 위 결과는 현재 개발 PC에서 수행한 검증 결과이며, 신규 PC에서는 동일한 빌드 명령으로 다시 확인한다.

### 6.3 ESLint 검사

```powershell
npm run lint --prefix .\pmis-frontend
```

현재 개발 PC에서는 기존 소스코드의 ESLint 검사 결과 다음 문제가 확인되었다.

- 오류 29건
- 경고 3건
- 총 32건

이는 Node.js 설치 또는 npm 환경 오류가 아니라 기존 프론트엔드 소스코드의 정적 검사 문제다.

환경 구축 검증과 코드 품질 검증은 구분하여 관리한다.

## 7. Visual Studio Code 설정

PMIS 프론트엔드 개발에 필요한 주요 확장은 다음과 같다.

| 확장 | 용도 |
|---|---|
| ESLint | 코드 정적 검사 |
| Prettier - Code formatter | 코드 포맷팅 |
| TypeScript and JavaScript Language Features | TypeScript/JavaScript 지원 |

VS Code에 설치된 확장과 프로젝트의 npm 의존성은 별도로 관리한다.

## 8. 문제 해결

### 8.1 Node.js 버전이 20으로 출력되는 경우

PMIS 전용 환경 설정 스크립트를 다시 실행한다.

```powershell
. .\scripts\use-pmis-node24.ps1
```

이후 다음 명령어로 확인한다.

```powershell
node --version
npm --version
(Get-Command node).Source
```

실행 파일 경로가 PMIS 전용 Node.js 디렉터리를 가리키는지 확인한다.

### 8.2 Node.js 실행 파일을 찾을 수 없는 경우

다음 명령어로 설치 경로를 확인한다.

```powershell
Test-Path .\Tools\node-v24.19.0-win-x64\node.exe
Test-Path .\Tools\node-v24.19.0-win-x64\npm.cmd
```

결과가 `False`라면 Node.js ZIP 패키지를 다시 확보하고 지정 경로에 압축을 해제한다.

### 8.3 npm ci 실행에 실패하는 경우

다음 항목을 확인한다.

1. PMIS Node.js 환경이 활성화되어 있는지 확인한다.
2. `node --version`과 `npm --version`을 확인한다.
3. `package.json`과 `package-lock.json`이 존재하는지 확인한다.
4. 네트워크 연결과 npm 패키지 레지스트리 접근을 확인한다.
5. 오류 메시지를 확인하여 의존성 충돌이나 패키지 다운로드 문제인지 구분한다.

### 8.4 프론트엔드 빌드에 실패하는 경우

다음 명령어를 실행하여 의존성 설치 및 빌드 상태를 확인한다.

```powershell
npm ci --prefix .\pmis-frontend
npm run build --prefix .\pmis-frontend
```

오류 메시지를 확인하여 Node.js 환경 문제와 소스코드 문제를 구분한다.

### 8.5 ESLint 검사에서 오류가 발생하는 경우

ESLint 오류는 Node.js 설치 여부와 별도로 확인한다.

```powershell
npm run lint --prefix .\pmis-frontend
```

기존 소스코드에서 발생한 오류인지 신규 변경 코드에서 발생한 오류인지 구분하여 관리한다.

---

## 9. 확인 완료 기준

- [ ] PMIS Node.js 24.19.0 패키지 확인
- [ ] `node.exe` 존재 확인
- [ ] `npm.cmd` 존재 확인
- [ ] `use-pmis-node24.ps1` 실행 확인
- [ ] `node --version`에서 24.19.0 확인
- [ ] `npm --version`에서 11.17.0 확인
- [ ] Node.js 및 npm 실행 파일 경로 확인
- [ ] `package.json` 및 `package-lock.json` 확인
- [ ] `npm ci` 실행 확인
- [ ] Vite 개발 서버 실행 확인
- [ ] 프론트엔드 빌드 성공 확인
- [ ] ESLint 검사 결과 별도 확인

---

**참고:** Node.js 시스템 설치 환경은 유지하고 PMIS 전용 Node.js 실행환경을 사용한다. 프로젝트의 Node.js 버전 변경 시 본 문서와 환경 설정 스크립트를 함께 갱신한다.