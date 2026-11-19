# PMIS BFF 설치 및 실행 가이드

- 문서 버전: v1.0
- 대상 프로젝트: PMIS BFF
- 실행 환경: Windows / Node.js
- 관리 기준: 개발 PC 및 집 PC 공통

## 1. 문서 목적

본 문서는 PMIS BFF(Node.js 기반 Backend For Frontend)의 개발 환경 구성, 패키지 설치, 환경 변수 설정, 빌드 및 실행 방법을 정의한다.

개발 PC와 집 PC에서 동일한 Git 저장소를 이용하여 BFF 개발 환경을 재현하는 것을 목적으로 한다.

또한 BFF의 세션 저장소인 MemorySessionStore 및 RedisSessionStore의 구성 방법과 실행 환경별 적용 기준을 정의한다.

## 2. 시스템 구성

PMIS는 다음과 같이 구성한다.

| 구성 요소 | 기술 | 역할 |
|---|---|---|
| Frontend | React, TypeScript | 사용자 화면 및 브라우저 요청 처리 |
| BFF | Node.js, TypeScript, Fastify | 브라우저 요청 처리 및 Backend API 연계 |
| Backend | Spring Boot | 인증·인가 및 업무 API 처리 |
| Database | PostgreSQL | 업무 데이터 저장 |
| Session Store | Memory / Redis | BFF 인증 세션 및 토큰 저장 |

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
   +---- HttpOnly Cookie
   |
   v
BFF Session Store
   |
   v
Spring Boot Backend
   |
   v
PostgreSQL
```

BFF는 React와 Spring Boot 사이에서 브라우저 요청을 처리하는 별도 애플리케이션이다.

BFF는 React 프로젝트에 종속된 라이브러리가 아니라 모노레포 내 독립 실행 애플리케이션으로 관리한다.

인증 과정에서 Backend는 사용자 인증 및 Refresh Token 발급·검증을 담당한다. BFF는 Backend에서 전달받은 토큰을 서버 측 SessionStore에 보관하고, 브라우저에는 HttpOnly 쿠키를 발급한다.

브라우저의 JavaScript에서는 Access Token 및 Refresh Token에 직접 접근할 수 없도록 구성한다.

## 3. 개발 환경

### 3.1 필수 소프트웨어

| 항목 | 버전 및 기준 |
|---|---|
| OS | Windows |
| Node.js | 24.19.0 |
| npm | 11.17.0 |
| TypeScript | package.json 및 package-lock.json 기준 |
| Fastify | package.json 및 package-lock.json 기준 |
| Redis Client | redis 6.3.1 |
| Redis Server | Redis 7.x 이상 권장 |
| Git | 프로젝트 저장소 관리 |

Node.js와 npm 버전은 현재 개발 PC에서 확인한 버전이다.

Redis Client는 Node.js에서 Redis Server에 접속하기 위한 라이브러리이며, Redis Server와는 별도로 구분한다.

Redis Client 패키지는 BFF 프로젝트의 package.json 및 package-lock.json으로 관리한다.

### 3.2 설치 버전 확인

PowerShell에서 다음 명령어를 실행한다.

```powershell
node -v
npm -v
git --version
```

Node.js 및 npm이 설치되어 있지 않으면 Node.js 공식 배포판을 설치한다.

설치 후 PowerShell을 새로 실행하여 버전을 확인한다.

### 3.3 Redis 설치 구성

Redis Server는 BFF와 별도로 실행되는 데이터 저장 서버다.

RedisSessionStore를 사용하는 환경에서는 Redis Server가 실행 중이어야 한다.

개발 PC에서는 Docker 및 Redis Server를 실행하지 않고 MemorySessionStore를 사용한다.

집 PC에서는 Docker 기반 Redis Server를 실행하여 RedisSessionStore를 사용한다.

Redis Server의 설치 및 실행 방법은 각 PC의 환경에 맞게 구성한다.

## 4. 프로젝트 위치

BFF 프로젝트는 PMIS 모노레포 내부에 위치한다.

```text
project-delivery-reference/
├── pmis-frontend/
├── pmis-backend/
└── pmis-bff/
    ├── src/
    │   ├── auth/
    │   │   ├── routes/
    │   │   ├── services/
    │   │   └── session/
    │   ├── config/
    │   ├── types/
    │   └── server.ts
    ├── .env
    ├── .env.example
    ├── package.json
    ├── package-lock.json
    └── tsconfig.json
```

주요 소스 디렉터리의 역할은 다음과 같다.

| 디렉터리 | 역할 |
|---|---|
| src/auth/routes | 인증 관련 API 라우트 |
| src/auth/services | Backend 인증 연계 및 인증 처리 서비스 |
| src/auth/session | BFF 세션 모델, SessionStore 및 Cookie 설정 |
| src/config | 환경 변수 및 실행 설정 |
| src/types | Fastify 확장 타입 정의 |

`node_modules`와 `dist`는 설치 및 빌드 과정에서 생성되는 디렉터리이므로 Git 관리 대상에서 제외한다.

## 5. 최초 설치 절차

### 5.1 Git 저장소 내려받기

아직 저장소가 없는 PC에서는 다음과 같이 저장소를 내려받는다.

```powershell
git clone <Git Repository URL>
```

이미 저장소가 존재하는 경우 프로젝트 루트에서 최신 코드를 가져온다.

```powershell
git pull origin develop
```

특정 Feature 작업을 수행하는 경우 해당 Feature 브랜치로 이동한다.

```powershell
git checkout <Feature Branch>
```

설치 가이드는 특정 Feature 브랜치에 종속되지 않으며, 현재 작업에 사용하는 브랜치를 기준으로 적용한다.

### 5.2 BFF 디렉터리 이동

프로젝트 루트에서 다음 명령어를 실행한다.

```powershell
cd pmis-bff
```

### 5.3 패키지 설치

프로젝트 루트가 아닌 pmis-bff 디렉터리에서 실행한다.

```powershell
npm ci
```

`npm ci`는 package-lock.json에 정의된 의존성 버전을 기준으로 패키지를 설치한다.

개발 PC와 집 PC에서 동일한 의존성 구성을 유지하기 위해 일반적인 최초 설치 및 재설치에는 `npm install`보다 `npm ci` 사용을 권장한다.

Redis Client인 `redis` 패키지도 package-lock.json에 포함되어 있으므로 별도로 설치할 필요가 없다.

설치된 Redis Client 버전은 다음 명령어로 확인할 수 있다.

```powershell
npm ls redis
```

### 5.4 환경 변수 파일 생성

`.env.example`을 복사하여 `.env`를 생성한다.

```powershell
Copy-Item .env.example .env
```

`.env` 파일은 각 PC의 실행 환경에 맞게 수정한다.

이미 `.env` 파일이 존재하는 경우 기존 설정을 보존하고 필요한 항목만 확인한다.

## 6. 환경 변수 설정

현재 `.env.example`의 기본 설정은 다음과 같다.

```env
# Common
NODE_ENV=development
HOST=127.0.0.1
PORT=4000
FRONTEND_ORIGIN=http://localhost:5173
BACKEND_API_URL=http://127.0.0.1:8765

# Session Store
SESSION_STORE=memory

# Redis Session Store
# SESSION_STORE=redis
# REDIS_URL=redis://127.0.0.1:6379
```

| 변수 | 설명 | 기본값 |
|---|---|---|
| NODE_ENV | 실행 환경 | development |
| HOST | BFF 바인딩 주소 | 127.0.0.1 |
| PORT | BFF 서비스 포트 | 4000 |
| FRONTEND_ORIGIN | 프론트엔드 Origin | http://localhost:5173 |
| BACKEND_API_URL | Spring Boot API 서버 주소 | http://127.0.0.1:8765 |
| SESSION_STORE | BFF 세션 저장소 선택 | memory |
| REDIS_URL | Redis Server 접속 주소 | Redis 사용 시 필수 |

### 6.1 환경 변수 설정 시 주의사항

- `.env`는 각 PC의 로컬 실행 환경에 맞게 관리한다.
- `.env` 파일은 Git에 커밋하지 않는다.
- `.env.example`은 환경 변수 항목과 기본 설정을 공유하기 위해 Git으로 관리한다.
- 실제 Spring Boot 서버의 주소와 포트는 해당 실행 환경에 맞게 설정한다.
- Redis를 사용하는 경우 REDIS_URL을 설정한다.
- 운영 환경에서는 실제 서비스 주소와 보안 설정을 별도로 적용해야 한다.

현재 `env.ts`에서는 필수 환경 변수 누락 및 포트·URL 형식을 검사한다.

또한 SESSION_STORE 값은 memory 또는 redis만 허용한다.

운영 환경에서는 SESSION_STORE=redis 설정을 필수로 적용하며, Redis URL이 누락된 경우 실행할 수 없다.

### 6.2 SessionStore 설정

BFF는 SessionStore 인터페이스를 기반으로 세션 저장소를 추상화한다.

현재 두 가지 구현체를 제공한다.

| 구분 | MemorySessionStore | RedisSessionStore |
|---|---|---|
| 저장 위치 | BFF 프로세스 메모리 | Redis Server |
| 환경 변수 | SESSION_STORE=memory | SESSION_STORE=redis |
| Redis Server | 불필요 | 필요 |
| 서버 재시작 | 세션 소멸 | Redis TTL까지 유지 |
| 다중 BFF 인스턴스 | 공유 불가 | 공유 가능 |
| 사용 목적 | 개발 및 테스트 | 운영 및 Redis 사용 환경 |

MemorySessionStore는 별도 Redis Server 없이 BFF를 실행할 수 있도록 제공하는 개발용 구현체다.

RedisSessionStore는 Redis Server에 세션 데이터를 저장하고 Refresh Token 만료 시각을 기준으로 TTL을 적용한다.

Redis 연결에 실패하면 자동으로 MemorySessionStore로 전환하지 않는다.

### 6.3 Redis Client 및 Redis Server

BFF에서는 Node.js `redis` 패키지를 Redis Client로 사용한다.

Redis Client는 다음과 같이 관리한다.

- 패키지명: redis
- 설치 버전: 6.3.1
- 설치 방법: npm ci
- 의존성 관리: package.json 및 package-lock.json

Redis Server는 별도 프로세스로 실행해야 한다.

Redis Client 패키지만 설치한 상태에서는 Redis Server에 접속할 수 없으므로 RedisSessionStore를 사용하려면 Redis Server가 실행 중이어야 한다.

Redis 접속 주소는 REDIS_URL에 설정한다.

예시:

```env
SESSION_STORE=redis
REDIS_URL=redis://127.0.0.1:6379
```

Redis TLS 연결을 사용하는 경우 `rediss://` 형식의 접속 주소를 사용한다.

운영 환경에서는 Redis 인증 및 네트워크 접근 제어를 별도로 적용한다.

### 6.4 Redis SessionStore 실행 확인

RedisSessionStore를 사용하려면 다음 조건을 충족해야 한다.

1. Redis Server가 실행 중이어야 한다.
2. REDIS_URL이 올바르게 설정되어 있어야 한다.
3. SESSION_STORE=redis로 설정되어 있어야 한다.
4. BFF 프로세스가 Redis Server에 연결할 수 있어야 한다.

BFF 서버 시작 로그에서 다음 메시지를 확인한다.

```text
Session store initialized
```

로그에 표시되는 sessionStore 값이 redis인지 확인한다.

Redis 연결에 실패한 경우 BFF는 정상 기동되지 않으며, 원인을 확인한 후 Redis Server 및 연결 설정을 점검한다.

### 6.5 세션 저장 정책

BFF는 Backend에서 발급받은 Access Token 및 Refresh Token을 브라우저에 직접 전달하지 않고 서버 측 SessionStore에 저장한다.

브라우저에는 BFF 세션 식별자를 HttpOnly 쿠키로 전달한다.

현재 세션 저장소의 Redis Key Prefix는 다음과 같다.

```text
pmis:bff:session:
```

세션 데이터는 Refresh Token의 JWT 만료 시각을 기준으로 TTL을 설정한다.

Redis SessionStore는 Redis Client 연결을 통해 세션을 조회, 저장 및 삭제한다.

Backend의 인증 세션 데이터와 BFF의 세션 데이터는 서로 다른 책임을 가지며, 각각 별도의 저장 구조로 관리한다.

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

헬스 체크는 BFF 애플리케이션의 기동 상태를 확인하는 용도이며, Backend API 및 Redis Server의 모든 기능이 정상 동작하는지까지 보장하지 않는다.

### 7.3 개발 환경별 SessionStore 실행

개발 PC에서는 다음 설정을 사용한다.

```env
SESSION_STORE=memory
```

이 설정에서는 Redis Server 없이 BFF를 실행할 수 있다.

집 PC에서 RedisSessionStore를 테스트하는 경우 다음 설정을 사용한다.

```env
SESSION_STORE=redis
REDIS_URL=redis://127.0.0.1:6379
```

Redis Server가 정상적으로 실행되고 있는지 확인한 후 BFF를 실행한다.

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
| `npm ls redis` | Redis Client 설치 버전 확인 |
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

Redis Client 패키지를 추가하거나 변경한 경우 `package.json`과 `package-lock.json`을 함께 커밋한다.

Redis Server 자체는 BFF 소스 저장소의 npm 의존성으로 관리하지 않으며, 각 실행 환경에서 별도로 구성한다.

## 11. 개발 PC 및 집 PC 운영 기준

개발 PC와 집 PC는 동일한 Git 저장소를 사용하되, 각 PC의 로컬 실행 환경은 독립적으로 관리한다.

| 구분 | 개발 PC | 집 PC |
|---|---|---|
| 소스 코드 | Git으로 동기화 | Git으로 동기화 |
| 패키지 설치 | npm ci | npm ci |
| 환경 변수 | 로컬 .env | 로컬 .env |
| SessionStore | MemorySessionStore | RedisSessionStore |
| SESSION_STORE | memory | redis |
| Redis Server | 사용하지 않음 | Docker 기반 실행 |
| REDIS_URL | 불필요 | 로컬 Redis 주소 설정 |
| node_modules | 로컬 생성 | 로컬 생성 |
| 빌드 결과 | 로컬 생성 | 로컬 생성 |
| 실행 프로세스 | 독립 실행 | 독립 실행 |

한 PC에서 의존성을 변경한 경우 `package.json`과 `package-lock.json`을 함께 커밋하고, 다른 PC에서는 Git Pull 후 `npm ci`를 실행한다.

각 PC의 `.env` 파일은 Git으로 동기화하지 않는다.

개발 PC에서는 Docker 및 Redis Server 실행을 요구하지 않는다.

집 PC에서는 Docker 기반 Redis Server를 실행하고 RedisSessionStore를 사용하는 방식으로 개발 및 검증한다.

운영 환경에서는 RedisSessionStore를 사용하며, Redis 연결 실패 시 MemorySessionStore로 자동 전환하지 않는다.

## 12. 현재 구현 범위 및 향후 작업

### 12.1 현재 구현 완료 범위

현재 BFF는 다음 항목까지 구현되어 있다.

- Node.js 및 TypeScript 프로젝트 초기화
- Fastify 서버 구성
- 환경 변수 로딩 및 검증
- Helmet 보안 헤더 플러그인 등록
- CORS 플러그인 등록
- Cookie 플러그인 등록
- `/health` 헬스 체크 API 구성
- MemorySessionStore 구현
- RedisSessionStore 구현
- Redis Client 연결 및 종료 처리
- SessionStore Factory 구성
- 환경 변수에 따른 SessionStore 선택
- 운영 환경 Redis 사용 필수 검증
- Backend 로그인 API 연계
- Backend 다중 세션 선택 로그인 API 연계
- BFF 인증 서비스 구성
- BFF 세션 저장
- HttpOnly 세션 쿠키 발급
- Refresh Token 만료 시각 기반 BFF 세션 TTL 적용
- TypeScript 빌드 및 개발 서버 실행
- Git 제외 파일 설정

### 12.2 현재 테스트 확인 범위

개발 환경에서 다음 항목을 확인했다.

- BFF 서버 정상 기동
- `/health` API 정상 응답
- Backend 일반 로그인 API 연계
- Backend 사용자 인증 성공
- Backend 사용자 세션 생성 확인
- BFF MemorySessionStore 세션 저장
- `pmis_session` 쿠키 발급
- HttpOnly 속성 확인
- 쿠키 Path 및 만료 시간 확인

RedisSessionStore의 운영 환경 연결 및 장기 세션 유지 검증은 별도 환경에서 추가 확인해야 한다.

### 12.3 향후 작업

다음 항목은 후속 설계 및 구현 대상으로 관리한다.

- BFF `/me` 사용자 인증 확인 API
- Cookie 기반 인증 미들웨어
- Refresh Token 갱신 및 회전 처리
- 동시 Refresh 요청 제어
- 로그아웃 및 Backend 세션 종료 연계
- Frontend BFF 인증 연동
- Frontend Access Token 및 Refresh Token 제거
- CSRF 및 Origin 검증
- 인증 및 업무 API 프록시
- 요청 검증 및 공통 오류 응답 표준화
- API 명세 및 OpenAPI 문서
- 운영 환경 배포 및 실행 설정
- Redis 장애 및 복구 시나리오 검증

현재 설치 가이드는 BFF의 개발 환경 구성과 현재 구현된 인증 및 세션 저장 기반을 설명한다.

후속 작업으로 분류된 항목은 구현 완료를 의미하지 않는다.