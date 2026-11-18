# PMIS BFF 환경 설정 가이드

## 1. 문서 목적

본 문서는 PMIS BFF(Node.js 기반 Backend For Frontend)의 환경 변수, 서버 실행 설정 및 환경별 설정 관리 기준을 정의한다.

개발 PC와 집 PC에서 동일한 애플리케이션을 실행할 수 있도록 공통 설정 항목과 개별 환경 설정 항목을 구분하여 관리한다.

## 2. 설정 관리 구조

BFF의 환경 설정은 다음 파일을 기준으로 관리한다.

| 파일 | 역할 | Git 관리 |
|---|---|---|
| `.env` | 실제 실행 환경 변수 | 제외 |
| `.env.example` | 환경 변수 예시 및 기본값 | 포함 |
| `src/config/env.ts` | 환경 변수 로딩 및 검증 | 포함 |
| `src/server.ts` | Fastify 서버 및 플러그인 설정 | 포함 |
| `package.json` | 실행 스크립트 및 의존성 관리 | 포함 |

환경 변수는 `dotenv`를 사용하여 `.env` 파일에서 로딩한다.

환경 설정을 코드에 직접 하드코딩하지 않고 환경 변수로 분리하여 관리한다.

## 3. 환경 변수 설정

### 3.1 기본 환경 변수

현재 `.env.example`에 정의된 기본 설정은 다음과 같다.

```env
NODE_ENV=development
HOST=127.0.0.1
PORT=4000
FRONTEND_ORIGIN=http://localhost:5173
BACKEND_API_URL=http://127.0.0.1:8765
```

### 3.2 환경 변수 상세

| 환경 변수 | 필수 여부 | 기본 예시 | 설명 |
|---|---|---|---|
| NODE_ENV | 선택 | development | 애플리케이션 실행 환경 |
| HOST | 필수 | 127.0.0.1 | BFF 서버 바인딩 주소 |
| PORT | 필수 | 4000 | BFF 서버 포트 |
| FRONTEND_ORIGIN | 필수 | http://localhost:5173 | 허용할 Frontend Origin |
| BACKEND_API_URL | 필수 | http://127.0.0.1:8765 | Spring Boot Backend API 주소 |

현재 `NODE_ENV`는 환경 변수에 값이 없으면 `development`로 처리한다.

나머지 필수 환경 변수가 누락되거나 유효하지 않으면 애플리케이션 설정 과정에서 오류가 발생한다.

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

현재 코드에서는 위 세 가지 값 이외의 환경 값이 설정되면 오류가 발생한다.

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

- 지정된 Frontend Origin만 허용한다.
- 브라우저의 Credential 포함 요청을 허용한다.
- 다른 Origin의 요청은 허용 대상에서 제외한다.

현재 설정은 개발 환경에서 React 개발 서버와 BFF를 별도 포트로 실행하는 구조를 기준으로 한다.

운영 환경에서 Frontend와 BFF를 동일 Origin으로 서비스하는 경우 CORS 설정의 필요성과 적용 범위를 다시 검토한다.

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

Backend API의 기본 경로는 향후 API 연계 구현 시 별도로 관리한다.

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

### 5.6 설정 객체

검증이 완료된 환경 변수는 `env` 객체로 제공한다.

```ts
export const env = Object.freeze({
  nodeEnv,
  host: getRequiredString("HOST"),
  port: getPort(),
  frontendOrigin: getUrl("FRONTEND_ORIGIN"),
  backendApiUrl: getUrl("BACKEND_API_URL"),
});
```

`Object.freeze()`를 적용하여 실행 중 설정 객체가 변경되지 않도록 한다.

## 6. Fastify 서버 설정

현재 `src/server.ts`의 기본 서버 설정은 다음과 같다.

```ts
const app = Fastify({
  logger: true,
});
```

### 6.1 로깅

Fastify 기본 로거를 활성화한다.

서버 시작 및 HTTP 요청 처리 과정에서 발생하는 로그를 확인할 수 있다.

운영 환경에서는 인증 정보, 비밀번호, Access Token, Refresh Token 등 민감 정보가 로그에 기록되지 않도록 별도 마스킹 정책을 적용해야 한다.

### 6.2 Helmet

현재 Helmet 플러그인을 등록한다.

```ts
await app.register(helmet);
```

HTTP 보안 관련 응답 헤더를 설정하기 위한 목적이다.

운영 환경에서는 서비스 구성에 맞게 CSP 등 세부 보안 정책을 추가 검토한다.

### 6.3 CORS

현재 CORS 플러그인을 다음과 같이 등록한다.

```ts
await app.register(cors, {
  origin: env.frontendOrigin,
  credentials: true,
});
```

허용 Origin과 Credential 처리 정책은 환경 변수로 관리한다.

운영 환경에서는 실제 서비스 Origin을 기준으로 제한한다.

### 6.4 서버 실행

서버는 환경 변수의 HOST와 PORT를 사용하여 실행한다.

```ts
await app.listen({
  port: env.port,
  host: env.host,
});
```

환경 변수에 설정된 주소와 포트를 기준으로 서버가 실행된다.

## 7. 개발 환경 설정

### 7.1 개발 PC

개발 PC의 `.env` 예시:

```env
NODE_ENV=development
HOST=127.0.0.1
PORT=4000
FRONTEND_ORIGIN=http://localhost:5173
BACKEND_API_URL=http://127.0.0.1:8765
```

실제 Backend 실행 포트가 다르면 `BACKEND_API_URL`을 수정한다.

### 7.2 집 PC

집 PC도 동일한 환경 변수 항목을 사용한다.

다만 Frontend와 Backend의 실행 주소 및 포트가 다를 경우 `.env`를 해당 환경에 맞게 수정한다.

각 PC의 `.env`는 독립적으로 관리하며 Git을 통해 동기화하지 않는다.

### 7.3 개발 서버 실행

```powershell
npm run dev
```

개발 모드에서는 `tsx watch`를 사용한다.

소스 코드 변경을 감지하여 서버를 재시작한다.

## 8. 빌드 및 실행 설정

### 8.1 TypeScript 빌드

```powershell
npm run build
```

`tsconfig.json`에 정의된 설정에 따라 `src`의 TypeScript 파일을 `dist`에 JavaScript 파일로 출력한다.

### 8.2 운영 방식 실행

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

## 9. 설정 파일 보안 및 Git 관리

### 9.1 `.env` 관리

`.env`에는 각 실행 환경의 실제 주소 및 설정값이 포함될 수 있으므로 Git에 커밋하지 않는다.

현재 `.gitignore`에는 다음 규칙이 적용되어 있다.

```gitignore
/pmis-bff/.env
/pmis-bff/.env.*
!/pmis-bff/.env.example
```

### 9.2 `.env.example` 관리

`.env.example`은 다른 개발자가 동일한 환경 변수 항목을 확인하고 실행 환경을 구성할 수 있도록 Git에서 관리한다.

실제 비밀번호, 인증 키, 토큰 및 개인정보를 포함하지 않는다.

### 9.3 환경별 설정 원칙

- 공통 환경 변수 항목은 `.env.example`에서 관리한다.
- 실제 실행값은 각 PC의 `.env`에서 관리한다.
- 환경 변수 이름을 변경하면 코드와 `.env.example`을 함께 수정한다.
- 새로운 환경 변수를 추가하면 필수 여부와 기본값을 문서에 반영한다.
- 인증 키와 비밀 정보는 소스 코드에 직접 입력하지 않는다.

## 10. 향후 추가 설정 항목

현재 기본 실행 환경을 구성한 상태이며, 인증 및 Backend 연계 기능 구현 시 다음 설정 항목을 추가 검토한다.

| 항목 | 목적 | 적용 시점 |
|---|---|---|
| COOKIE_SECRET | 쿠키 서명 및 관련 보안 설정 | Cookie 기능 구현 시 |
| SESSION_STORE_URL | 세션 저장소 연결 | 세션 저장소 도입 시 |
| JWT_VERIFY_KEY | JWT 검증 관련 설정 | 토큰 검증 구조 확정 시 |
| CSRF_SECRET | CSRF Token 관련 설정 | CSRF 방어 구현 시 |
| LOG_LEVEL | 로그 상세 수준 제어 | 로깅 정책 확정 시 |
| TRUST_PROXY | Reverse Proxy 신뢰 설정 | 운영 배포 구조 확정 시 |

위 항목은 향후 검토 대상이며 현재 환경 변수에 구현되어 있거나 사용 중인 설정은 아니다.

각 설정의 구체적인 이름과 값은 실제 구현 및 보안 설계 단계에서 확정한다.

## 11. 설정 변경 절차

환경 설정 변경 시 다음 절차를 따른다.

1. 변경 목적 및 적용 환경 확인
2. 환경 변수 추가 또는 수정
3. `src/config/env.ts` 검증 로직 반영
4. `.env.example` 갱신
5. 관련 설정 문서 갱신
6. 개발 서버 실행 및 빌드 확인
7. 헬스 체크 및 관련 기능 검증
8. Git 변경 사항 검토 후 커밋

환경 변수 변경이 기존 개발 환경에 영향을 주는 경우 개발 PC와 집 PC 양쪽의 `.env` 설정을 확인한다.

## 12. 관련 문서

| 문서 | 내용 |
|---|---|
| `01-installation-guide.md` | BFF 설치 및 실행 절차 |
| `02-architecture.md` | 전체 시스템 아키텍처 및 책임 구분 |
| `03-configuration-guide.md` | 환경 변수 및 실행 설정 |
| `04-security-design.md` | 인증 및 보안 설계 |
| `api/openapi.yaml` | API 명세 |
