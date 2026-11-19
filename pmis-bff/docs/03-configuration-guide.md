# PMIS BFF 환경 설정 가이드

## 1. 문서 목적

본 문서는 PMIS BFF(Node.js 기반 Backend For Frontend)의 환경 변수, 서버 실행 설정 및 환경별 설정 관리 기준을 정의한다.

개발 PC와 집 PC에서 동일한 애플리케이션을 실행할 수 있도록 공통 설정 항목과 개별 환경 설정 항목을 구분하여 관리한다.

또한 BFF의 인증 세션 저장소인 MemorySessionStore와 RedisSessionStore의 설정 및 운영 기준을 정의한다.

## 2. 설정 관리 구조

BFF의 환경 설정은 다음 파일을 기준으로 관리한다.

| 파일 | 역할 | Git 관리 |
|---|---|---|
| `.env` | 실제 실행 환경 변수 | 제외 |
| `.env.example` | 환경 변수 예시 및 기본값 | 포함 |
| `src/config/env.ts` | 환경 변수 로딩 및 검증 | 포함 |
| `src/server.ts` | Fastify 서버 및 플러그인 설정 | 포함 |
| `src/auth/session/session-store-factory.ts` | 세션 저장소 생성 및 연결 관리 | 포함 |
| `package.json` | 실행 스크립트 및 의존성 관리 | 포함 |
| `package-lock.json` | 패키지 의존성 버전 관리 | 포함 |

환경 변수는 `dotenv`를 사용하여 `.env` 파일에서 로딩한다.

환경 설정을 코드에 직접 하드코딩하지 않고 환경 변수로 분리하여 관리한다.

세션 저장소는 `SESSION_STORE` 설정에 따라 메모리 또는 Redis 방식으로 생성한다.

## 3. 환경 변수 설정

### 3.1 기본 환경 변수

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

개발 PC에서는 MemorySessionStore를 사용하고, Redis를 사용하는 환경에서는 `SESSION_STORE=redis`와 `REDIS_URL`을 설정한다.

### 3.2 환경 변수 상세

| 환경 변수 | 필수 여부 | 기본 예시 | 설명 |
|---|---|---|---|
| NODE_ENV | 선택 | development | 애플리케이션 실행 환경 |
| HOST | 필수 | 127.0.0.1 | BFF 서버 바인딩 주소 |
| PORT | 필수 | 4000 | BFF 서버 포트 |
| FRONTEND_ORIGIN | 필수 | http://localhost:5173 | 허용할 Frontend Origin |
| BACKEND_API_URL | 필수 | http://127.0.0.1:8765 | Spring Boot Backend API 주소 |
| SESSION_STORE | 필수 | memory | 세션 저장소 유형 |
| REDIS_URL | 조건부 필수 | redis://127.0.0.1:6379 | Redis 세션 저장소 접속 주소 |

### 3.3 환경 변수 적용 기준

현재 `NODE_ENV`는 환경 변수에 값이 없으면 `development`로 처리한다.

나머지 필수 환경 변수가 누락되거나 유효하지 않으면 애플리케이션 설정 과정에서 오류가 발생한다.

`SESSION_STORE`는 다음 두 값만 허용한다.

- `memory`
- `redis`

`SESSION_STORE=redis`인 경우 `REDIS_URL`이 필수이다.

`SESSION_STORE=memory`인 경우 Redis 접속 주소는 사용하지 않는다.

운영 환경에서는 `SESSION_STORE=redis` 설정이 필수이며, 메모리 기반 세션 저장소를 사용할 수 없다.

## 4. 환경 변수별 상세 설정

### 4.1 NODE_ENV

애플리케이션의 실행 환경을 지정한다.

허용 값:

- `development`
- `production`
- `test`

설정 예시:

```env
NODE_ENV=development
```

현재 코드에서는 환경 변수 값이 없으면 `development`로 처리한다.

위 세 가지 값 이외의 환경 값이 설정되면 오류가 발생한다.

운영 환경에서는 다음과 같이 설정한다.

```env
NODE_ENV=production
```

운영 환경에서는 Redis 세션 저장소 사용이 강제된다.

### 4.2 HOST

Fastify 서버가 요청을 수신할 네트워크 인터페이스 주소를 지정한다.

설정 예시:

```env
HOST=127.0.0.1
```

| 설정값 | 설명 |
|---|---|
| 127.0.0.1 | 로컬 PC에서만 접근 |
| 0.0.0.0 | 모든 네트워크 인터페이스에서 수신 |

개발 환경에서는 로컬 접근을 기본으로 한다.

운영 환경에서 외부 또는 다른 서버의 접근이 필요한 경우에는 네트워크 구성과 접근 통제 정책을 함께 검토해야 한다.

현재 코드에서는 HOST의 누락 여부를 검사하지만 주소 형식 자체를 별도로 검증하지는 않는다.

### 4.3 PORT

BFF 서버가 사용할 TCP 포트를 지정한다.

설정 예시:

```env
PORT=4000
```

현재 코드의 검증 기준은 다음과 같다.

- 정수여야 한다.
- 최소값은 1이다.
- 최대값은 65535이다.
- 필수 값이므로 누락할 수 없다.

유효하지 않은 포트가 설정되면 서버가 정상적으로 시작되지 않는다.

### 4.4 FRONTEND_ORIGIN

BFF가 허용할 Frontend의 Origin을 지정한다.

설정 예시:

```env
FRONTEND_ORIGIN=http://localhost:5173
```

현재 Fastify CORS 설정은 다음과 같다.

```ts
await app.register(cors, {
  origin: env.frontendOrigin,
  credentials: true,
});
```

설정 의미:

- 지정된 Frontend Origin을 허용한다.
- 브라우저의 Credential 포함 요청을 허용한다.
- 다른 Origin은 허용 대상에서 제외한다.

현재 설정은 개발 환경에서 React 개발 서버와 BFF를 별도 포트로 실행하는 구조를 기준으로 한다.

운영 환경에서 Frontend와 BFF를 동일 Origin으로 서비스하는 경우 CORS 설정의 필요성과 적용 범위를 다시 검토한다.

현재 환경 변수 검증 코드에서는 HTTP 또는 HTTPS URL만 허용하며, URL의 Origin을 반환한다.

따라서 URL에 경로를 포함하더라도 경로 부분은 유지되지 않는다.

예를 들어 다음 설정은:

```env
FRONTEND_ORIGIN=http://localhost:5173/example
```

다음 Origin으로 정규화된다.

```text
http://localhost:5173
```

### 4.5 BACKEND_API_URL

Spring Boot Backend API 서버의 기본 주소를 지정한다.

설정 예시:

```env
BACKEND_API_URL=http://127.0.0.1:8765
```

BFF가 Backend API를 호출할 때 사용하는 기준 주소이다.

개발 PC와 집 PC의 Backend 실행 주소 및 포트가 다르면 각 PC의 `.env`에서 개별 설정한다.

운영 환경에서는 내부 네트워크에서 접근 가능한 Backend 주소를 사용한다.

현재 환경 설정 코드에서는 HTTP 또는 HTTPS URL만 허용하며 URL의 Origin을 반환한다.

따라서 URL에 경로를 포함하더라도 경로 부분은 유지되지 않는다.

예를 들어 다음 설정은:

```env
BACKEND_API_URL=http://127.0.0.1:8765/api
```

다음 Origin으로 정규화된다.

```text
http://127.0.0.1:8765
```

Backend API의 구체적인 경로는 API 연계 구현에서 별도로 관리한다.

### 4.6 SESSION_STORE

BFF에서 사용할 세션 저장소 유형을 지정한다.

설정 예시:

```env
SESSION_STORE=memory
```

허용 값:

| 설정값 | 저장소 | 설명 |
|---|---|---|
| memory | MemorySessionStore | BFF 프로세스 메모리에 세션 저장 |
| redis | RedisSessionStore | Redis 서버에 세션 저장 |

현재 `session-store-factory.ts`에서 설정값에 따라 세션 저장소 구현체를 생성한다.

MemorySessionStore는 Redis 서버 없이 실행할 수 있어 개발 환경에서 사용한다.

RedisSessionStore는 별도의 Redis 서버 연결이 필요하다.

운영 환경에서는 세션 데이터의 지속성과 여러 BFF 인스턴스 간 공유를 위해 Redis 사용을 필수로 한다.

현재 환경 설정 코드에서는 운영 환경에서 `SESSION_STORE=memory`가 설정되면 오류를 발생시킨다.

```text
SESSION_STORE=redis is required in production
```

### 4.7 REDIS_URL

RedisSessionStore에서 사용할 Redis 서버의 접속 주소를 지정한다.

설정 예시:

```env
SESSION_STORE=redis
REDIS_URL=redis://127.0.0.1:6379
```

Redis 접속 주소는 다음 프로토콜을 허용한다.

- `redis://`
- `rediss://`

`rediss://`는 TLS 연결을 사용하는 Redis 접속 주소이다.

`SESSION_STORE=redis`인 경우 반드시 설정해야 한다.

`SESSION_STORE=memory`인 경우에는 Redis 접속 주소를 사용하지 않는다.

현재 BFF Redis 세션 저장소는 다음 키 Prefix를 사용한다.

```text
pmis:bff:session:
```

Redis에는 BFF 세션 정보가 저장되며, Backend에서 사용하는 임시 로그인 정보 저장 영역과 구분한다.

세션 저장 시 Refresh Token의 만료 시각을 기준으로 TTL을 설정한다.

세션 만료 이후에는 Redis에서 해당 세션 데이터가 만료되도록 처리한다.

실제 Redis 서버의 인증 정보 및 접속 주소는 `.env`에서 관리하며 Git에 커밋하지 않는다.

## 5. 환경 변수 검증

환경 변수는 `src/config/env.ts`에서 검증한다.

### 5.1 환경 변수 로딩

```ts
import "dotenv/config";
```

`dotenv/config`를 통해 애플리케이션 시작 시 `.env` 파일을 로딩한다.

### 5.2 필수 문자열 검증

`getRequiredString()` 함수에서 다음 항목을 검사한다.

- 환경 변수 존재 여부
- 공백 제거 후 값이 비어 있는지 여부

필수 환경 변수가 없거나 빈 문자열이면 오류를 발생시킨다.

### 5.3 PORT 검증

`getPort()` 함수에서 다음 조건을 검사한다.

- 숫자 변환 가능 여부
- 정수 여부
- 1~65535 범위 여부

검증 실패 시 다음 오류가 발생한다.

```text
PORT must be an integer between 1 and 65535
```

### 5.4 URL 검증

`getUrl()` 함수에서 다음 조건을 검사한다.

- URL 형식이 유효한지 여부
- HTTP 또는 HTTPS 프로토콜인지 여부

검증 실패 시 환경 변수 이름을 포함한 오류를 발생시킨다.

현재 URL 설정은 Origin 기준으로 정규화한다.

### 5.5 NODE_ENV 검증

허용된 환경 값 이외의 값이 입력되면 오류를 발생시킨다.

```text
NODE_ENV must be development, production, or test
```

### 5.6 SESSION_STORE 검증

`getSessionStore()` 함수에서 다음 조건을 검사한다.

- 환경 변수 존재 여부
- `memory` 또는 `redis` 중 하나인지 여부

검증 실패 시 다음 오류가 발생한다.

```text
SESSION_STORE must be either memory or redis
```

추가로 운영 환경에서는 Redis 사용을 강제한다.

### 5.7 REDIS_URL 검증

`getRedisUrl()` 함수에서 Redis 사용 여부에 따라 접속 주소를 처리한다.

- `SESSION_STORE=memory`이면 `null`을 반환한다.
- `SESSION_STORE=redis`이면 `REDIS_URL`을 필수로 확인한다.
- Redis URL 형식과 프로토콜을 검증한다.
- `redis://` 또는 `rediss://` 이외의 프로토콜은 허용하지 않는다.

검증 실패 시 다음 오류가 발생한다.

```text
REDIS_URL must be a valid Redis or Redis TLS URL
```

### 5.8 설정 객체

검증이 완료된 환경 변수는 `env` 객체로 제공한다.

```ts
export const env = Object.freeze({
  nodeEnv,
  host: getRequiredString("HOST"),
  port: getPort(),
  frontendOrigin: getUrl("FRONTEND_ORIGIN"),
  backendApiUrl: getUrl("BACKEND_API_URL"),
  sessionStore,
  redisUrl: getRedisUrl(sessionStore),
});
```

`Object.freeze()`를 적용하여 실행 중 설정 객체가 변경되지 않도록 한다.

## 6. Fastify 서버 설정

현재 `src/server.ts`에서 Fastify 서버와 플러그인을 구성한다.

### 6.1 Fastify 초기화

```ts
const app = Fastify({
  logger: true,
});
```

Fastify 기본 로거를 활성화한다.

서버 시작 및 HTTP 요청 처리 과정에서 발생하는 로그를 확인할 수 있다.

운영 환경에서는 인증 정보, 비밀번호, Access Token, Refresh Token 등 민감 정보가 로그에 기록되지 않도록 별도 마스킹 정책을 적용해야 한다.

### 6.2 Session Store 초기화

서버 시작 시 `createSessionStore()`를 호출하여 세션 저장소를 생성한다.

설정된 `SESSION_STORE` 값에 따라 다음과 같이 처리한다.

- `memory`: MemorySessionStore 생성
- `redis`: Redis 클라이언트 생성 및 연결 후 RedisSessionStore 생성

Redis 연결이 필요한 환경에서 연결에 실패하면 서버 시작이 정상적으로 완료되지 않는다.

Redis 연결 실패 시 MemorySessionStore로 자동 전환하지 않는다.

이를 통해 운영 환경에서 Redis 연결 오류를 숨기지 않도록 한다.

### 6.3 Fastify 세션 저장소 등록

생성된 세션 저장소는 Fastify 인스턴스에 등록한다.

```ts
app.decorate("sessionStore", sessionStoreRuntime.store);
```

인증 서비스도 동일한 세션 저장소를 사용하도록 등록한다.

```ts
app.decorate(
  "authService",
  createAuthService(sessionStoreRuntime.store),
);
```

### 6.4 Helmet

현재 Helmet 플러그인을 등록한다.

```ts
await app.register(helmet);
```

HTTP 보안 관련 응답 헤더를 설정하기 위한 목적이다.

운영 환경에서는 서비스 구성에 맞게 CSP 등 세부 보안 정책을 추가 검토한다.

### 6.5 CORS

현재 CORS 플러그인을 다음과 같이 등록한다.

```ts
await app.register(cors, {
  origin: env.frontendOrigin,
  credentials: true,
});
```

허용 Origin과 Credential 처리 정책은 환경 변수로 관리한다.

운영 환경에서는 실제 서비스 Origin을 기준으로 제한한다.

### 6.6 Cookie 플러그인

현재 `@fastify/cookie` 플러그인을 등록한다.

```ts
await app.register(cookie);
```

Cookie 플러그인은 브라우저 쿠키를 처리하기 위한 기반 기능이다.

현재 로그인 성공 시 BFF 세션 식별자를 HttpOnly 쿠키로 발급하는 기능이 구현되어 있다.

쿠키 이름은 다음과 같다.

```text
pmis_session
```

쿠키에는 BFF 세션 식별자가 저장되며 Access Token과 Refresh Token을 브라우저에 직접 전달하지 않는다.

### 6.7 서버 실행

서버는 환경 변수의 HOST와 PORT를 사용하여 실행한다.

```ts
await app.listen({
  port: env.port,
  host: env.host,
});
```

환경 변수에 설정된 주소와 포트를 기준으로 서버가 실행된다.

### 6.8 서버 종료 및 Redis 연결 정리

서버 종료 시 Fastify의 `onClose` Hook을 통해 세션 저장소의 종료 처리를 수행한다.

RedisSessionStore를 사용하는 경우 Redis 클라이언트 연결을 정상적으로 종료한다.

MemorySessionStore를 사용하는 경우 별도의 외부 연결 종료 작업은 필요하지 않다.

## 7. 세션 저장소 운영 설정

### 7.1 MemorySessionStore

MemorySessionStore는 BFF 프로세스의 메모리에 세션 정보를 저장한다.

특징:

- Redis 서버가 필요하지 않다.
- 별도의 외부 인프라 없이 실행할 수 있다.
- BFF 프로세스가 재시작되면 저장된 세션이 사라진다.
- 여러 BFF 인스턴스 간 세션을 공유할 수 없다.
- 세션 만료 시각을 기준으로 만료된 세션을 조회하지 않도록 처리한다.

개발 PC의 기본 세션 저장소로 사용한다.

### 7.2 RedisSessionStore

RedisSessionStore는 Redis 서버에 세션 정보를 저장한다.

특징:

- 별도의 Redis 서버가 필요하다.
- Redis TTL을 이용하여 세션 만료를 관리한다.
- 여러 BFF 인스턴스가 동일한 Redis를 사용하면 세션 정보를 공유할 수 있다.
- BFF 프로세스 재시작 이후에도 Redis에 남아 있는 세션 정보를 사용할 수 있다.
- Redis 연결이 불가능하면 세션 저장소를 초기화할 수 없다.

운영 환경 및 Redis 기반 통합 테스트 환경에서 사용한다.

### 7.3 세션 만료 정책

BFF 세션의 만료 시각은 Backend에서 발급한 Refresh Token의 JWT `exp` 값을 기준으로 계산한다.

BFF는 해당 값을 세션 저장소의 TTL 설정에 활용한다.

BFF는 Refresh Token의 서명을 검증하는 인증 주체가 아니다.

실제 JWT 유효성 검증과 인증 판단은 Spring Boot Backend가 담당한다.

### 7.4 세션 저장소 비교

| 구분 | MemorySessionStore | RedisSessionStore |
|---|---|---|
| 저장 위치 | BFF 프로세스 메모리 | Redis 서버 |
| Redis 서버 | 불필요 | 필요 |
| 세션 만료 | 만료 시각 확인 | Redis TTL 및 만료 시각 확인 |
| BFF 재시작 | 세션 소멸 | Redis 데이터 유지 시 세션 유지 |
| 다중 인스턴스 | 세션 공유 불가 | 동일 Redis 사용 시 공유 가능 |
| 사용 환경 | 개발 PC | 집 PC 및 운영 환경 |

### 7.5 Redis 서버 운영 시 주의사항

Redis는 BFF 세션 데이터를 저장하는 별도 인프라로 관리한다.

다음 사항을 준수한다.

- Redis 접속 주소와 인증 정보는 환경 변수로 관리한다.
- Redis 포트를 외부에 불필요하게 공개하지 않는다.
- Backend의 임시 로그인 데이터와 BFF 세션 데이터의 키 영역을 구분한다.
- Redis 장애 시 BFF가 세션 저장소 오류를 정상적으로 처리하도록 한다.
- 운영 환경에서는 Redis 백업 및 장애 복구 정책을 별도로 수립한다.

Redis 서버의 실제 실행 방식과 이중화 구성은 배포 환경에 따라 결정한다.

## 8. 개발 환경 설정

### 8.1 개발 PC

개발 PC의 `.env` 예시:

```env
NODE_ENV=development
HOST=127.0.0.1
PORT=4000
FRONTEND_ORIGIN=http://localhost:5173
BACKEND_API_URL=http://127.0.0.1:8765

SESSION_STORE=memory
```

개발 PC에서는 MemorySessionStore를 사용한다.

Redis 서버 또는 Docker를 실행하지 않아도 BFF의 기본 로그인 기능과 세션 저장 기능을 개발할 수 있다.

단, Backend의 Redis 기반 임시 로그인 정보 저장 기능이 필요한 세션 선택 로그인은 해당 개발 환경의 Redis 활성화 여부에 따라 실행 가능 여부가 달라진다.

### 8.2 집 PC

집 PC에서는 RedisSessionStore를 사용하는 설정을 적용한다.

`.env` 설정 예시:

```env
NODE_ENV=development
HOST=127.0.0.1
PORT=4000
FRONTEND_ORIGIN=http://localhost:5173
BACKEND_API_URL=http://127.0.0.1:8765

SESSION_STORE=redis
REDIS_URL=redis://127.0.0.1:6379
```

Redis 서버가 먼저 실행되어 있어야 한다.

Redis 서버가 실행되지 않았거나 접속할 수 없는 경우 BFF 서버의 초기화가 실패한다.

### 8.3 개발 PC와 집 PC 운영 기준

| 구분 | 개발 PC | 집 PC |
|---|---|---|
| 소스 코드 | Git으로 동기화 | Git으로 동기화 |
| 패키지 설치 | npm ci | npm ci |
| 환경 변수 | 로컬 `.env` | 로컬 `.env` |
| 세션 저장소 | MemorySessionStore | RedisSessionStore |
| SESSION_STORE | memory | redis |
| Redis 서버 | 사용하지 않음 | 사용 |
| Docker | 필요 없음 | Redis 실행에 사용 |
| 세션 공유 | 단일 프로세스 | Redis를 통한 공유 가능 |

각 PC의 `.env` 파일은 독립적으로 관리하며 Git을 통해 동기화하지 않는다.

### 8.4 개발 서버 실행

```powershell
npm run dev
```

개발 모드에서는 `tsx watch`를 사용한다.

소스 코드 변경을 감지하여 서버를 재시작한다.

## 9. 빌드 및 실행 설정

### 9.1 TypeScript 빌드

```powershell
npm run build
```

`tsconfig.json`에 정의된 설정에 따라 `src`의 TypeScript 파일을 `dist`에 JavaScript 파일로 출력한다.

빌드 과정에서는 TypeScript 타입 검사도 수행한다.

### 9.2 운영 방식 실행

```powershell
npm start
```

`npm start`는 다음 파일을 실행한다.

```text
dist/server.js
```

따라서 운영 방식으로 실행하기 전에는 빌드를 완료해야 한다.

```powershell
npm run build
npm start
```

### 9.3 주요 npm 명령어

| 명령어 | 설명 |
|---|---|
| `npm ci` | package-lock.json 기준 의존성 설치 |
| `npm run dev` | 개발 모드 서버 실행 |
| `npm run build` | TypeScript 빌드 |
| `npm start` | 빌드 결과 실행 |

## 10. 설정 파일 보안 및 Git 관리

### 10.1 `.env` 관리

`.env`에는 각 실행 환경의 실제 주소 및 설정값이 포함될 수 있으므로 Git에 커밋하지 않는다.

현재 `.gitignore`에는 다음 규칙이 적용되어 있다.

```gitignore
/pmis-bff/.env
/pmis-bff/.env.*
!/pmis-bff/.env.example
```

Redis 접속 주소, 비밀번호 및 기타 민감 정보도 `.env`에서 관리한다.

### 10.2 `.env.example` 관리

`.env.example`은 다른 개발자가 동일한 환경 변수 항목을 확인하고 실행 환경을 구성할 수 있도록 Git에서 관리한다.

실제 비밀번호, 인증 키, 토큰 및 개인정보를 포함하지 않는다.

Redis 접속 주소는 예시 주소만 기재하고 실제 운영 접속 정보를 포함하지 않는다.

### 10.3 환경별 설정 원칙

- 공통 환경 변수 항목은 `.env.example`에서 관리한다.
- 실제 실행값은 각 PC의 `.env`에서 관리한다.
- 환경 변수 이름을 변경하면 코드와 `.env.example`을 함께 수정한다.
- 새로운 환경 변수를 추가하면 필수 여부와 기본값을 문서에 반영한다.
- 인증 키와 비밀 정보는 소스 코드에 직접 입력하지 않는다.
- Redis 접속 정보는 실제 실행 환경의 `.env`에서 관리한다.
- 개발 PC와 집 PC의 세션 저장소 설정은 각 환경의 목적에 맞게 유지한다.

## 11. 향후 추가 설정 항목

현재 기본 서버, 세션 저장소 및 로그인 쿠키 발급 기능이 구현된 상태이다.

향후 인증 및 보안 기능을 확장할 때 다음 설정 항목을 검토한다.

| 항목 | 목적 | 현재 상태 |
|---|---|---|
| COOKIE_SECRET | 쿠키 서명 및 관련 보안 설정 | 현재 미사용 |
| JWT_VERIFY_KEY | JWT 서명 검증 관련 설정 | 현재 BFF에서 미사용 |
| CSRF_SECRET | CSRF Token 관련 설정 | CSRF 구현 시 검토 |
| LOG_LEVEL | 로그 상세 수준 제어 | 현재 별도 환경 변수 미적용 |
| TRUST_PROXY | Reverse Proxy 신뢰 설정 | 운영 배포 구조 확정 시 검토 |

위 항목은 향후 검토 대상이며 현재 환경 변수에 구현되어 있거나 사용 중인 설정은 아니다.

`SESSION_STORE`와 `REDIS_URL`은 이미 구현된 설정이므로 향후 추가 설정 항목으로 분류하지 않는다.

각 설정의 구체적인 이름과 값은 실제 구현 및 보안 설계 단계에서 확정한다.

## 12. 설정 변경 절차

환경 설정 변경 시 다음 절차를 따른다.

1. 변경 목적 및 적용 환경 확인
2. 환경 변수 추가 또는 수정
3. `src/config/env.ts` 검증 로직 반영
4. 관련 설정 및 세션 저장소 코드 반영
5. `.env.example` 갱신
6. 관련 설정 문서 갱신
7. 개발 서버 실행 및 빌드 확인
8. 헬스 체크 및 관련 기능 검증
9. Git 변경 사항 검토 후 커밋

환경 변수 변경이 기존 개발 환경에 영향을 주는 경우 개발 PC와 집 PC 양쪽의 `.env` 설정을 확인한다.

Redis 관련 설정 변경 시 다음 항목도 함께 확인한다.

- Redis 서버 실행 여부
- Redis 접속 주소 및 연결 상태
- 세션 저장 및 조회 정상 여부
- 세션 만료 및 TTL 적용 여부
- BFF 종료 시 Redis 연결 정리 여부

## 13. 관련 문서

| 문서 | 내용 |
|---|---|
| `01-installation-guide.md` | BFF 설치 및 실행 절차 |
| `02-architecture.md` | 전체 시스템 아키텍처 및 책임 구분 |
| `03-configuration-guide.md` | 환경 변수 및 실행 설정 |
| `04-security-design.md` | 인증 및 보안 설계 |
| `api/openapi.yaml` | API 명세 |