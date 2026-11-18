# PMIS BFF 설치 및 실행 가이드

## 1. 문서 목적

본 문서는 PMIS BFF(Node.js 기반 Backend For Frontend)의 개발 환경 구성, 패키지 설치, 환경 변수 설정, 빌드 및 실행 방법을 정의한다.

개발 PC와 집 PC에서 동일한 Git 저장소를 이용하여 BFF 개발 환경을 재현하는 것을 목적으로 한다.

## 2. 시스템 구성

PMIS는 다음과 같이 구성한다.

| 구성 요소 | 기술 | 역할 |
|---|---|---|
| Frontend | React | 사용자 화면 및 브라우저 요청 처리 |
| BFF | Node.js, TypeScript, Fastify | 브라우저 요청 처리 및 백엔드 API 연계 |
| Backend | Spring Boot | 인증·인가 및 업무 API 처리 |
| Database | PostgreSQL | 업무 데이터 저장 |

기본 요청 흐름은 다음과 같다.

```text
Browser
   |
   v
React Frontend
   |
   v
Node.js BFF
   |
   v
Spring Boot Backend
   |
   v
PostgreSQL
```

BFF는 React와 Spring Boot 사이에서 브라우저 요청을 처리하는 별도 애플리케이션이다.

BFF는 React 프로젝트에 종속된 라이브러리가 아니라 모노레포 내 독립 실행 애플리케이션으로 관리한다.

## 3. 개발 환경

### 3.1 필수 소프트웨어

| 항목 | 버전 및 기준 |
|---|---|
| OS | Windows |
| Node.js | 24.19.0 |
| npm | 11.17.0 |
| TypeScript | package.json 및 package-lock.json 기준 |
| Fastify | package.json 및 package-lock.json 기준 |
| Git | 프로젝트 저장소 관리 |

Node.js와 npm 버전은 현재 개발 PC에서 확인한 버전이다.

### 3.2 설치 버전 확인

PowerShell에서 다음 명령어를 실행한다.

```powershell
node -v
npm -v
git --version
```

Node.js 및 npm이 설치되어 있지 않으면 Node.js 공식 배포판을 설치한다.

설치 후 PowerShell을 새로 실행하여 버전을 확인한다.

## 4. 프로젝트 위치

BFF 프로젝트는 PMIS 모노레포 내부에 위치한다.

```text
project-delivery-reference/
├── pmis-frontend/
├── pmis-backend/
└── pmis-bff/
    ├── docs/
    │   └── api/
    ├── src/
    │   ├── config/
    │   ├── plugins/
    │   ├── routes/
    │   ├── schemas/
    │   ├── services/
    │   ├── types/
    │   ├── utils/
    │   └── server.ts
    ├── .env
    ├── .env.example
    ├── package.json
    ├── package-lock.json
    └── tsconfig.json
```

`node_modules`와 `dist`는 설치 및 빌드 과정에서 생성되는 디렉터리이므로 Git 관리 대상에서 제외한다.

## 5. 최초 설치 절차

### 5.1 Git 저장소 내려받기

아직 저장소가 없는 PC에서는 다음과 같이 저장소를 내려받는다.

```powershell
git clone <Git Repository URL>
```

이미 저장소가 존재하는 경우 프로젝트 루트에서 최신 코드를 가져온다.

```powershell
git pull
```

BFF 작업 브랜치로 이동한다.

```powershell
git checkout feature/bff-bootstrap
```

### 5.2 BFF 디렉터리 이동

```powershell
cd pmis-bff
```

### 5.3 패키지 설치

프로젝트 루트가 아닌 `pmis-bff` 디렉터리에서 실행한다.

```powershell
npm ci
```

`npm ci`는 `package-lock.json`에 정의된 의존성 버전을 기준으로 패키지를 설치한다.

개발 PC와 집 PC에서 동일한 의존성 구성을 유지하기 위해 일반적인 최초 설치 및 재설치에는 `npm install`보다 `npm ci` 사용을 권장한다.

### 5.4 환경 변수 파일 생성

`.env.example`을 복사하여 `.env`를 생성한다.

```powershell
Copy-Item .env.example .env
```

`.env` 파일은 각 PC의 실행 환경에 맞게 수정한다.

## 6. 환경 변수 설정

현재 `.env.example`의 기본 설정은 다음과 같다.

```env
NODE_ENV=development
HOST=127.0.0.1
PORT=4000
FRONTEND_ORIGIN=http://localhost:5173
BACKEND_API_URL=http://127.0.0.1:8765
```

| 변수 | 설명 | 기본값 |
|---|---|---|
| NODE_ENV | 실행 환경 | development |
| HOST | BFF 바인딩 주소 | 127.0.0.1 |
| PORT | BFF 서비스 포트 | 4000 |
| FRONTEND_ORIGIN | 프론트엔드 Origin | http://localhost:5173 |
| BACKEND_API_URL | Spring Boot API 서버 주소 | http://127.0.0.1:8765 |

### 6.1 환경 변수 설정 시 주의사항

- `.env`는 각 PC의 로컬 실행 환경에 맞게 관리한다.
- `.env` 파일은 Git에 커밋하지 않는다.
- `.env.example`은 환경 변수 항목과 기본 설정을 공유하기 위해 Git으로 관리한다.
- 실제 Spring Boot 서버의 주소와 포트는 해당 실행 환경에 맞게 설정한다.
- 운영 환경에서는 실제 서비스 주소와 보안 설정을 별도로 적용해야 한다.

현재 `env.ts`에서는 필수 환경 변수 누락 및 포트·URL 형식을 검사한다.

## 7. 개발 서버 실행

### 7.1 개발 모드 실행

```powershell
npm run dev
```

개발 모드에서는 `tsx watch`를 이용하여 TypeScript 소스 파일 변경을 감지하고 서버를 재시작한다.

기본 실행 주소는 다음과 같다.

```text
http://127.0.0.1:4000
```

### 7.2 헬스 체크

브라우저 또는 PowerShell에서 다음 주소를 호출한다.

```text
GET http://127.0.0.1:4000/health
```

PowerShell 실행 명령:

```powershell
Invoke-RestMethod -Uri http://127.0.0.1:4000/health -Method Get
```

정상 응답 예시:

```json
{
  "status": "UP",
  "service": "pmis-bff"
}
```

정상 응답이 확인되면 BFF 서버가 기동된 것이다.

## 8. 빌드 및 실행

### 8.1 TypeScript 빌드

```powershell
npm run build
```

빌드가 성공하면 `dist` 디렉터리에 JavaScript 실행 파일이 생성된다.

현재 TypeScript 빌드 설정은 다음과 같다.

- 입력 디렉터리: `src`
- 출력 디렉터리: `dist`
- 모듈 방식: NodeNext
- TypeScript strict 검사 활성화
- 빌드 오류 발생 시 출력 파일 생성 방지

### 8.2 빌드 결과 실행

```powershell
npm start
```

`npm start`는 빌드된 `dist/server.js`를 실행한다.

따라서 최초 실행 또는 소스 변경 후에는 다음 순서로 실행한다.

```powershell
npm run build
npm start
```

## 9. 주요 npm 명령어

| 명령어 | 설명 |
|---|---|
| `npm ci` | lock 파일 기준 의존성 설치 |
| `npm run dev` | 개발 모드 서버 실행 |
| `npm run build` | TypeScript 빌드 |
| `npm start` | 빌드 결과 실행 |

## 10. Git 관리 기준

다음 항목은 Git 관리 대상에 포함한다.

- `package.json`
- `package-lock.json`
- `tsconfig.json`
- `.env.example`
- `src/`
- `docs/`

다음 항목은 Git 관리 대상에서 제외한다.

- `node_modules/`
- `dist/`
- `.env`
- 로그 파일
- 테스트 및 커버리지 결과물

`package-lock.json`을 함께 관리하여 개발 PC와 집 PC에서 동일한 패키지 의존성을 재현한다.

## 11. 개발 PC 및 집 PC 운영 기준

개발 PC와 집 PC는 동일한 Git 저장소를 사용하되, 각 PC의 로컬 실행 환경은 독립적으로 관리한다.

| 구분 | 개발 PC | 집 PC |
|---|---|---|
| 소스 코드 | Git으로 동기화 | Git으로 동기화 |
| 패키지 설치 | `npm ci` | `npm ci` |
| 환경 변수 | 로컬 `.env` | 로컬 `.env` |
| node_modules | 로컬 생성 | 로컬 생성 |
| 빌드 결과 | 로컬 생성 | 로컬 생성 |
| 실행 프로세스 | 독립 실행 | 독립 실행 |

한 PC에서 의존성을 변경한 경우 `package.json`과 `package-lock.json`을 함께 커밋하고, 다른 PC에서는 Git Pull 후 `npm ci`를 실행한다.

각 PC의 `.env` 파일은 Git으로 동기화하지 않는다.

## 12. 현재 구현 범위 및 향후 작업

현재 BFF 기본 실행 환경은 다음 항목까지 구성되어 있다.

- Node.js 및 TypeScript 프로젝트 초기화
- Fastify 서버 구성
- 환경 변수 로딩 및 검증
- Helmet 보안 헤더 플러그인 등록
- CORS 플러그인 등록
- `/health` 헬스 체크 API 구성
- TypeScript 빌드 및 개발 서버 실행
- Git 제외 파일 설정

다음 항목은 후속 설계 및 구현 대상으로 관리한다.

- Cookie 기반 인증 및 세션 처리
- Spring Boot API 연계
- 인증 및 사용자 정보 API
- 로그아웃 및 세션 종료 처리
- 요청 검증 및 공통 오류 응답
- CSRF 및 Origin 검증
- API 명세 및 OpenAPI 문서
- 운영 환경 배포 및 실행 설정

현재 설치 가이드는 BFF 기본 실행 환경을 구성하기 위한 문서이며, 위 후속 기능의 구현 완료를 의미하지 않는다.
