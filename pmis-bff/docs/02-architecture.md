# PMIS BFF 시스템 아키텍처

- 문서 버전: v1.0
- 대상 프로젝트: PMIS
- 대상 시스템: React Frontend / Node.js BFF / Spring Boot Backend
- 문서 목적: 시스템 구성, 애플리케이션 책임, 인증·세션 구조 및 보안 경계 정의

## 1. 문서 목적

본 문서는 PMIS의 BFF(Backend For Frontend) 도입에 따른 시스템 구성, 애플리케이션 간 책임, 요청 처리 흐름 및 보안 경계를 정의한다.

PMIS는 React Frontend, Node.js BFF, Spring Boot Backend로 구성하며, 각 애플리케이션의 역할을 분리하여 유지보수성과 보안성을 확보하는 것을 목적으로 한다.

본 문서는 현재 구현된 기본 아키텍처와 인증·세션 기능의 현재 구현 상태 및 향후 목표 아키텍처를 함께 정의한다.

현재 구현된 기능과 향후 개발할 기능을 구분하여 관리하며, 구현되지 않은 기능을 완료된 기능으로 간주하지 않는다.

## 2. 아키텍처 설계 원칙

PMIS BFF는 다음 설계 원칙을 따른다.

### 2.1 역할 분리

- React는 사용자 인터페이스와 화면 상태를 관리한다.
- Node.js BFF는 브라우저와 Backend 사이의 요청 중계 및 브라우저 중심 기능을 담당한다.
- Spring Boot는 인증·인가 및 업무 도메인 로직을 담당한다.
- Database는 업무 데이터의 영속성을 담당한다.
- BFF SessionStore는 BFF가 관리하는 인증 세션 및 토큰을 저장한다.

### 2.2 Backend 중심의 업무 처리

BFF는 업무 규칙을 직접 구현하지 않는다.

업무 데이터의 생성·수정·삭제 및 업무 규칙 검증은 Spring Boot Backend에서 수행한다.

BFF는 Backend API를 호출하고 브라우저에 적합한 형태로 요청과 응답을 처리한다.

### 2.3 인증 책임과 브라우저 보안 분리

브라우저에 Access Token 및 Refresh Token을 직접 노출하지 않는 구조를 적용한다.

BFF는 브라우저와의 인증 쿠키 및 세션 처리를 담당하고, Spring Boot는 최종적인 인증·인가 검증을 수행한다.

BFF는 Backend에서 발급한 인증 토큰을 서버 측 세션 저장소에 보관한다.

### 2.4 독립적인 애플리케이션 관리

Frontend, BFF, Backend는 하나의 Git 저장소에서 관리하되 각각 독립적인 애플리케이션으로 구성한다.

각 애플리케이션은 별도의 실행 환경과 빌드 절차를 가진다.

### 2.5 현재 구현과 목표 아키텍처의 구분

아키텍처는 다음 세 가지 상태를 구분하여 관리한다.

| 구분 | 의미 |
|---|---|
| 구현 완료 | 현재 소스 코드에 기능이 구현되어 있고 기본 동작이 확인된 상태 |
| 구현 중 | 설계 또는 개발이 진행 중이며 완료 검증이 남아 있는 상태 |
| 향후 목표 | 아키텍처 방향은 정의했지만 실제 구현되지 않은 상태 |

## 3. 전체 시스템 구성

### 3.1 논리 아키텍처

PMIS의 목표 논리 아키텍처는 다음과 같다.

```text
┌───────────────────────────────────────────────┐
│                    Browser                    │
│                                               │
│              사용자 및 브라우저                │
└───────────────────────┬───────────────────────┘
                        │
                        │ HTTPS
                        ▼
┌───────────────────────────────────────────────┐
│                React Frontend                 │
│                                               │
│  - 화면 구성                                  │
│  - 사용자 입력 및 화면 상태 관리              │
│  - BFF API 호출                               │
│  - 인증 상태에 따른 화면 제어                 │
└───────────────────────┬───────────────────────┘
                        │
                        │ BFF API Request
                        │ HttpOnly Cookie
                        ▼
┌───────────────────────────────────────────────┐
│                  Node.js BFF                  │
│                                               │
│  - 브라우저 요청 처리                         │
│  - 인증 쿠키 처리                             │
│  - 세션 및 토큰 관리                          │
│  - 요청 검증                                  │
│  - Backend API 중계                           │
│  - Backend 응답 처리                          │
└───────────────────┬───────────────────────────┘
                    │
          ┌─────────┴──────────┐
          │                    │
          ▼                    ▼
┌──────────────────┐  ┌─────────────────────────┐
│  BFF SessionStore│  │ Spring Boot Backend    │
│                  │  │                         │
│  MemorySession   │  │ - 인증 및 인가 검증    │
│       또는       │  │ - JWT 검증              │
│  RedisSession    │  │ - 업무 도메인 로직      │
│                  │  │ - 데이터 검증 및 처리   │
└──────────────────┘  └────────────┬────────────┘
                                  │
                                  │ SQL
                                  ▼
                       ┌────────────────────────┐
                       │       Database         │
                       │                        │
                       │ - 업무 데이터 저장     │
                       │ - 기준정보 관리        │
                       └────────────────────────┘
```

SessionStore는 BFF의 인증 세션 및 토큰을 저장하는 구성요소다.

실행 환경에 따라 MemorySessionStore 또는 RedisSessionStore 중 하나를 선택한다.

MemorySessionStore는 개발 및 테스트 환경에서 사용하며, RedisSessionStore는 Redis Server를 이용하는 환경에서 사용한다.

### 3.2 애플리케이션 구성

| 구분 | 애플리케이션 | 기술 | 주요 책임 |
|---|---|---|---|
| Frontend | pmis-frontend | React, TypeScript | 사용자 화면 및 UI 상태 관리 |
| BFF | pmis-bff | Node.js, TypeScript, Fastify | 브라우저 요청 및 인증 쿠키 처리 |
| Backend | pmis-backend | Spring Boot | 인증·인가 및 업무 API |
| Database | Backend 연계 DB | 현재 Backend 설정 기준 | 데이터 영속성 |
| BFF SessionStore | Memory / Redis | Memory / Redis | BFF 세션 및 토큰 저장 |

Database의 실제 제품 및 버전은 Backend의 데이터베이스 설정을 기준으로 관리한다.

### 3.3 BFF SessionStore 구성

BFF는 SessionStore 인터페이스를 통해 세션 저장소를 추상화한다.

| 구성요소 | 역할 |
|---|---|
| SessionStore | 세션 저장소 공통 인터페이스 |
| MemorySessionStore | 프로세스 메모리 기반 세션 저장 |
| RedisSessionStore | Redis 기반 세션 저장 |
| SessionStore Factory | 환경 변수에 따른 저장소 생성 및 초기화 |

현재 구현된 저장소 선택 기준은 다음과 같다.

| 환경 변수 | 선택 구현체 |
|---|---|
| SESSION_STORE=memory | MemorySessionStore |
| SESSION_STORE=redis | RedisSessionStore |

운영 환경에서는 RedisSessionStore를 사용하도록 제한한다.

Redis 연결에 실패한 경우 MemorySessionStore로 자동 전환하지 않는다.

## 4. 애플리케이션별 책임

### 4.1 React Frontend

React는 사용자와 직접 상호작용하는 화면 계층이다.

주요 책임은 다음과 같다.

- 로그인 및 로그아웃 화면 제공
- 사용자 입력 및 화면 상태 관리
- 업무 기능별 화면 구성
- BFF API 호출
- API 응답에 따른 화면 갱신
- 인증 상태에 따른 화면 접근 제어

인증 구조 전환의 목표는 Frontend가 Access Token 및 Refresh Token을 직접 저장하거나 관리하지 않는 것이다.

인증 상태는 향후 BFF의 사용자 정보 조회 API 등을 통해 확인하도록 구성한다.

### 4.2 Node.js BFF

Node.js BFF는 브라우저와 Spring Boot Backend 사이의 중간 계층이다.

주요 책임은 다음과 같다.

- 브라우저 요청 수신
- 인증 쿠키 처리
- 서버 측 세션 및 토큰 관리
- Backend 인증 API 연계
- 요청 Origin 검증
- CSRF 방어
- 요청 데이터 및 파라미터 검증
- 허용된 Backend API 호출
- Backend 응답 및 오류 처리
- 브라우저에 적합한 API 응답 제공

BFF는 허용된 Backend API만 호출하도록 제한한다.

사용자가 임의로 전달한 URL을 그대로 호출하는 범용 Proxy 형태로 구현하지 않는다.

현재 구현된 인증 기능은 Backend 로그인 API와 세션 선택 로그인 API 연계, BFF 세션 저장 및 HttpOnly 쿠키 발급까지다.

쿠키를 이용한 사용자 인증 확인, Refresh 처리 및 로그아웃 연계는 후속 구현 대상으로 관리한다.

### 4.3 Spring Boot Backend

Spring Boot는 PMIS의 핵심 업무 처리 계층이다.

주요 책임은 다음과 같다.

- 사용자 인증 및 인가
- JWT 검증
- 업무 도메인 로직 처리
- 업무 데이터 유효성 검증
- 데이터 등록·조회·수정·삭제
- 트랜잭션 처리
- 데이터베이스 접근
- 사용자별 활성 세션 관리
- Refresh Token 발급 및 회전

BFF를 통해 요청이 전달되더라도 Backend는 인증과 권한을 독립적으로 검증해야 한다.

BFF를 통과했다는 사실만으로 요청을 신뢰하지 않는다.

### 4.4 Database

Database는 PMIS 업무 데이터를 저장하는 데이터베이스다.

데이터베이스 접근은 Spring Boot Backend를 통해서만 수행한다.

React와 BFF는 업무 데이터베이스에 직접 접근하지 않는다.

실제 DBMS 종류와 연결 정보는 Backend의 설정을 기준으로 관리한다.

### 4.5 BFF SessionStore

BFF SessionStore는 BFF가 관리하는 사용자 세션 및 인증 토큰을 저장한다.

주요 저장 정보는 다음과 같다.

| 항목 | 설명 |
|---|---|
| sessionId | BFF 세션 식별자 |
| accessToken | Backend에서 발급한 Access Token |
| refreshToken | Backend에서 발급한 Refresh Token |
| user | 사용자 식별 및 표시 정보 |
| expiresAt | BFF 세션 만료 시각 |
| createdAt | 세션 생성 시각 |
| updatedAt | 세션 갱신 시각 |

현재 BFF 세션은 UUID 기반 식별자를 사용하며, Refresh Token의 JWT 만료 시각을 기준으로 세션 만료 시각을 계산한다.

JWT 만료 시각을 읽는 작업은 BFF 세션 TTL 계산을 위한 것이며, JWT 서명 검증을 의미하지 않는다.

인증 토큰의 유효성 검증은 Backend가 담당한다.

## 5. 요청 처리 아키텍처

### 5.1 일반 업무 API 요청

일반적인 업무 조회 요청의 목표 흐름은 다음과 같다.

```text
1. 사용자가 React 화면에서 업무 조회 요청
                  |
                  v
2. React가 BFF API 호출
                  |
                  v
3. BFF가 쿠키 및 요청 정보 확인
                  |
                  v
4. BFF가 세션 저장소에서 세션 조회
                  |
                  v
5. BFF가 요청 Origin 및 입력값 검증
                  |
                  v
6. BFF가 Spring Boot API 호출
                  |
                  v
7. Spring Boot가 인증 및 권한 검증
                  |
                  v
8. Spring Boot가 업무 로직 수행
                  |
                  v
9. Spring Boot가 Database 조회
                  |
                  v
10. Spring Boot가 API 응답 반환
                  |
                  v
11. BFF가 응답 처리 후 브라우저에 전달
                  |
                  v
12. React가 화면 갱신
```

위 흐름 중 세션 조회 및 인증 쿠키 기반 요청 처리는 현재 후속 구현 대상으로 남아 있다.

### 5.2 API 요청 경로

브라우저는 BFF를 통해 API를 호출하는 것을 원칙으로 한다.

개발 환경에서는 Frontend와 BFF가 별도 포트로 실행될 수 있다.

예시:

| 구분 | 개발 환경 URL |
|---|---|
| Frontend | http://localhost:5173 |
| BFF | http://127.0.0.1:4000 |
| Backend | http://127.0.0.1:8765 |

운영 환경에서는 Reverse Proxy를 활용하여 동일 Origin으로 요청을 처리하는 구성을 목표로 한다.

예시:

| 구분 | URL |
|---|---|
| Frontend | https://pmis.example.com/ |
| BFF API | https://pmis.example.com/api/ |
| Backend API | 내부 네트워크에서만 접근 가능한 별도 주소 |

운영 환경에서 브라우저가 Spring Boot Backend에 직접 접근하지 않도록 네트워크 및 Reverse Proxy 구성을 적용한다.

### 5.3 BFF와 Backend 간 통신

BFF는 Backend API를 호출할 때 Backend에서 정의한 API 계약을 준수한다.

현재 확인된 Backend 인증 API는 다음과 같다.

| API | 기능 |
|---|---|
| POST /api/auth/login | 로그인 |
| POST /api/auth/login/session-selection | 기존 세션 선택 후 로그인 |
| POST /api/auth/refresh | Refresh Token 갱신 |

실제 API 요청·응답 구조 및 오류 코드는 Backend 구현을 기준으로 관리한다.

BFF는 Backend 인증 응답을 처리하고 필요한 세션 정보를 저장한다.

향후 업무 API 중계 기능을 확장할 때도 Backend의 API 계약을 우선한다.

## 6. 인증 및 세션 아키텍처

### 6.1 인증 처리 구조

기존 Spring Boot JWT 인증 구조를 기반으로 BFF를 연계한다.

현재 구현된 로그인 처리 흐름은 다음과 같다.

```text
Browser
   |
   | 로그인 요청
   v
React Frontend
   |
   v
Node.js BFF
   |
   | POST /api/auth/login
   v
Spring Boot Backend
   |
   | 사용자 인증
   | Access Token 및 Refresh Token 발급
   v
Node.js BFF
   |
   | BFF 세션 생성
   | SessionStore 저장
   | HttpOnly Cookie 발급
   v
Browser
```

로그인 성공 시 BFF는 Backend에서 전달받은 인증 토큰과 사용자 정보를 BFF SessionStore에 저장한다.

브라우저에는 BFF 세션 식별자를 담은 HttpOnly 쿠키를 발급한다.

브라우저 JavaScript에서 쿠키 값에 직접 접근할 수 없도록 HttpOnly 속성을 적용한다.

현재 로그인 및 쿠키 발급 흐름은 구현되어 있으며, 이후 쿠키를 이용한 인증 상태 확인과 업무 API 연계는 후속 구현 대상이다.

### 6.2 인증 토큰 관리

현재 인증 토큰 관리 구조는 다음과 같다.

| 항목 | 관리 주체 | 관리 방식 |
|---|---|---|
| Access Token | BFF | SessionStore 저장 |
| Refresh Token | BFF | SessionStore 저장 |
| BFF 세션 식별자 | BFF | UUID 기반 생성 |
| 인증 쿠키 | Browser | HttpOnly 쿠키 |
| JWT 인증 검증 | Spring Boot Backend | Backend 기준 검증 |
| 세션 만료 | BFF 및 Backend | Refresh Token 만료 시각 기준 연계 |

BFF는 Refresh Token의 JWT 만료 시각을 이용하여 세션 TTL을 설정한다.

BFF에서 JWT 서명 검증을 수행하는 것으로 간주하지 않는다.

Backend가 인증 토큰의 최종 유효성을 판단한다.

### 6.3 쿠키 보안

인증 쿠키에는 다음 보안 속성을 적용한다.

| 속성 | 현재 적용 기준 | 목적 |
|---|---|---|
| HttpOnly | 적용 | JavaScript에서 쿠키 직접 접근 방지 |
| Secure | 운영 환경에서 적용 | HTTPS 연결에서만 쿠키 전송 |
| SameSite | Lax | 교차 사이트 요청에서 쿠키 전송 제한 |
| Path | / | 서비스 경로에서 쿠키 사용 |
| Max-Age | 세션 만료 시각 기준 | 쿠키 유효기간 관리 |

SameSite 설정만으로 CSRF 방어가 완성되는 것은 아니므로 Origin 검증 및 CSRF 방어 정책을 별도로 적용해야 한다.

HttpOnly 쿠키 역시 XSS로 인한 인증된 요청 실행 자체를 방지하지는 못하므로 Frontend의 출력 인코딩과 CSP 등 추가적인 보안 대책이 필요하다.

현재 Cookie 플러그인 등록 및 로그인 성공 시 HttpOnly 쿠키 발급은 구현되어 있다.

CSRF Token 검증과 세부 Origin 검증 정책은 후속 구현 대상으로 관리한다.

### 6.4 다중 세션 관리

PMIS는 사용자별 최대 동시 세션 수를 관리자가 설정할 수 있는 구조를 적용한다.

최대 세션 수를 초과하는 경우 신규 로그인을 자동 거부하거나 기존 세션을 자동 종료하지 않는다.

사용자에게 기존 활성 세션 목록을 제시하고, 사용자가 종료할 세션을 직접 선택하도록 한다.

선택된 기존 세션을 폐기한 뒤 신규 로그인을 허용한다.

현재 Backend에는 다음 기능이 구현되어 있다.

- 사용자별 최대 세션 수 설정 조회
- 사용자 활성 세션 조회
- 활성 세션 수 확인
- 최대 세션 수 초과 시 세션 선택 로그인 응답
- 사용자가 선택한 기존 세션 종료 및 신규 로그인 처리
- Refresh Token 회전 및 세션 정보 갱신

BFF는 Backend의 세션 선택 로그인 API를 연계하는 구조로 구성되어 있다.

다만 BFF의 활성 세션 목록 조회 및 개별 세션 종료 API는 별도 구현 대상으로 관리한다.

### 6.5 인증 API 구성

Backend API와 BFF API는 역할을 구분하여 관리한다.

#### 6.5.1 Backend 인증 API

현재 확인된 Backend 인증 API는 다음과 같다.

| API | 기능 | 구현 상태 |
|---|---|---|
| POST /api/auth/login | 로그인 | 구현 완료 |
| POST /api/auth/login/session-selection | 세션 선택 로그인 | 구현 완료 |
| POST /api/auth/refresh | Refresh Token 갱신 | Backend 구현 완료 |

Backend API의 상세 요청·응답 구조는 Backend 소스 및 API 계약을 기준으로 관리한다.

#### 6.5.2 BFF 인증 API

현재 BFF에서 구현된 인증 API는 다음과 같다.

| API | 기능 | 구현 상태 |
|---|---|---|
| POST /api/auth/login | Backend 로그인 연계 및 BFF 세션 생성 | 구현 완료 |
| POST /api/auth/login/session-selection | Backend 세션 선택 로그인 연계 | 구현 완료 |

다음 API는 향후 BFF에서 구현할 대상으로 관리한다.

| API | 기능 | 구현 상태 |
|---|---|---|
| GET /api/auth/me | 현재 로그인 사용자 정보 조회 | 향후 구현 |
| POST /api/auth/refresh | 인증 토큰 갱신 및 BFF 세션 갱신 | 향후 구현 |
| POST /api/auth/logout | BFF 세션 및 Backend 인증 세션 종료 연계 | 향후 구현 |
| GET /api/auth/sessions | 활성 세션 목록 조회 | 향후 구현 |
| DELETE /api/auth/sessions/{sessionId} | 지정 세션 종료 | 향후 구현 |

BFF의 API 경로와 Backend의 API 경로가 동일하더라도 각각 별도 애플리케이션의 API라는 점을 구분한다.

### 6.6 Refresh Token 갱신

Backend는 Refresh Token 갱신 시 기존 Refresh Token을 검증하고 새로운 Access Token 및 Refresh Token을 발급하는 구조를 사용한다.

Refresh Token 회전 시 Backend의 세션 식별자는 유지되며 Refresh Token 저장 정보가 갱신된다.

향후 BFF Refresh 처리에서는 다음 정책을 적용한다.

1. 브라우저의 BFF 세션 쿠키를 확인한다.
2. SessionStore에서 BFF 세션을 조회한다.
3. 기존 Refresh Token으로 Backend Refresh API를 호출한다.
4. Backend가 갱신에 성공한 경우 새 Access Token 및 Refresh Token을 전달받는다.
5. 새 Refresh Token의 만료 시각을 기준으로 세션 만료 시각을 다시 계산한다.
6. SessionStore의 인증 토큰 및 만료 정보를 갱신한다.
7. 갱신 실패 시 세션 만료 및 오류 처리 정책을 적용한다.

동일 세션에서 여러 Refresh 요청이 동시에 발생하면 기존 Refresh Token 회전 충돌이 발생할 수 있으므로 세션 단위 동시 요청 제어를 적용한다.

### 6.7 로그아웃 및 세션 종료

로그아웃은 BFF 세션과 Backend 인증 세션을 구분하여 처리한다.

BFF SessionStore에서 세션을 삭제하고 브라우저 쿠키를 제거하는 것만으로 Backend의 Refresh Token 또는 사용자 세션이 폐기되는 것은 아니다.

따라서 최종 로그아웃 구현 시 다음 사항을 구분한다.

- BFF 세션 데이터 삭제
- 브라우저 인증 쿠키 제거
- Backend 세션 종료 또는 Refresh Token 무효화
- 다른 활성 세션에 미치는 영향 방지

Backend 로그아웃 API가 실제 구현되기 전까지 BFF 쿠키 제거를 Backend 세션까지 완전히 종료한 것으로 간주하지 않는다.

## 7. 보안 아키텍처

### 7.1 네트워크 접근 통제

운영 환경에서는 다음 접근 원칙을 적용한다.

- 외부 브라우저는 Frontend 및 BFF를 통해 서비스를 이용한다.
- Spring Boot Backend는 내부 네트워크에서 접근하도록 구성한다.
- Backend API는 BFF 또는 허용된 내부 시스템에서만 접근할 수 있도록 제한한다.
- 업무 Database는 Backend에서만 접근할 수 있도록 제한한다.
- Redis Server는 BFF에서 필요한 네트워크 경로로만 접근하도록 제한한다.

### 7.2 API 접근 통제

BFF는 Backend 호출 대상을 사전에 정의된 API로 제한한다.

사용자가 전달한 임의의 URL을 그대로 Backend 호출에 사용하지 않는다.

이를 통해 SSRF 및 비인가 내부 시스템 접근 위험을 줄인다.

### 7.3 인증 및 인가 검증

인증 및 인가의 최종 책임은 Spring Boot Backend에 있다.

BFF는 브라우저 요청을 처리하고 인증 정보를 전달하지만, Backend의 권한 검증을 대체하지 않는다.

BFF의 인증 세션이 존재한다는 사실만으로 모든 업무 API 접근을 허용하지 않는다.

### 7.4 CSRF 및 CORS

브라우저가 인증 쿠키를 자동으로 전송하는 구조에서는 CSRF 방어가 필요하다.

다음 항목을 적용한다.

- 허용된 Origin 검증
- CSRF 방어 정책 적용
- 쿠키 SameSite 속성 설정
- 필요 시 CSRF Token 검증
- 허용된 CORS Origin만 등록
- 운영 환경에서 불필요한 교차 출처 요청 제한

현재 BFF에는 CORS 플러그인이 등록되어 있으며 Frontend Origin을 환경 변수로 관리한다.

CSRF 방어 및 Origin 검증의 세부 정책은 후속 구현 대상으로 관리한다.

운영 환경에서 Frontend와 BFF를 동일 Origin으로 서비스하는 경우 CORS 사용 필요성을 별도로 검토한다.

### 7.5 보안 책임 구분

| 보안 항목 | 담당 | 상태 |
|---|---|---|
| 브라우저 인증 쿠키 | BFF | HttpOnly 발급 구현 |
| 서버 측 토큰 저장 | BFF SessionStore | 구현 완료 |
| JWT 검증 | Spring Boot Backend | Backend 담당 |
| 사용자 세션 관리 | Spring Boot Backend | 구현 완료 |
| BFF 세션 조회 | BFF | 후속 구현 |
| Origin 검증 | BFF | 후속 구현 |
| CSRF 방어 | BFF | 후속 구현 |
| 요청 입력값 검증 | BFF 및 Backend | 단계별 적용 |
| 업무 권한 검증 | Spring Boot Backend | Backend 담당 |
| 데이터 접근 통제 | Spring Boot Backend 및 DB | Backend 담당 |
| 네트워크 접근 통제 | 인프라 및 배포 환경 | 운영 설계 대상 |
| Redis 접근 통제 | 인프라 및 BFF | 운영 설계 대상 |

## 8. 개발 및 운영 환경 구성

### 8.1 개발 환경

개발 환경에서는 각 애플리케이션을 독립적으로 실행한다.

| 구성 요소 | 실행 방식 |
|---|---|
| React Frontend | React 개발 서버 |
| Node.js BFF | Fastify 개발 서버 |
| Spring Boot Backend | Spring Boot 애플리케이션 |
| Database | Backend 설정에 따른 DB |
| BFF SessionStore | 개발 PC는 Memory, 집 PC는 Redis |

현재 BFF 개발 환경의 기본 설정은 다음과 같다.

```env
NODE_ENV=development
HOST=127.0.0.1
PORT=4000
FRONTEND_ORIGIN=http://localhost:5173
BACKEND_API_URL=http://127.0.0.1:8765

SESSION_STORE=memory

# Redis 사용 환경
# SESSION_STORE=redis
# REDIS_URL=redis://127.0.0.1:6379
```

각 환경의 실제 Backend 주소 및 포트는 `.env`에서 별도로 관리한다.

### 8.2 개발 PC 및 집 PC의 세션 저장소 정책

개발 PC와 집 PC는 동일한 Git 저장소를 사용하되 실행 환경에 따라 SessionStore를 구분한다.

| 구분 | 개발 PC | 집 PC |
|---|---|---|
| 소스 코드 | Git으로 동기화 | Git으로 동기화 |
| 패키지 설치 | npm ci | npm ci |
| SessionStore | MemorySessionStore | RedisSessionStore |
| SESSION_STORE | memory | redis |
| Redis Server | 사용하지 않음 | Docker 기반 실행 |
| REDIS_URL | 불필요 | 로컬 Redis 주소 |
| `.env` | 로컬 관리 | 로컬 관리 |

개발 PC에서는 Docker 및 Redis Server 실행을 요구하지 않는다.

집 PC에서는 Docker 기반 Redis Server를 실행하고 RedisSessionStore를 사용하는 방식으로 개발 및 검증한다.

### 8.3 MemorySessionStore 특성

MemorySessionStore는 BFF 프로세스 메모리에 세션을 저장한다.

특징은 다음과 같다.

- 별도 Redis Server가 필요하지 않다.
- BFF 프로세스가 종료되면 세션 데이터가 소멸한다.
- 여러 BFF 인스턴스 사이에서 세션을 공유할 수 없다.
- 개발 및 테스트 환경에 적합하다.

### 8.4 RedisSessionStore 특성

RedisSessionStore는 Redis Server에 세션 데이터를 저장한다.

특징은 다음과 같다.

- 별도 Redis Server가 필요하다.
- Redis TTL을 이용하여 세션 만료를 관리한다.
- BFF 프로세스 재시작 후에도 Redis에 저장된 유효 세션을 조회할 수 있다.
- 여러 BFF 인스턴스에서 동일한 Redis를 사용할 수 있는 구조로 확장할 수 있다.
- Redis 장애 시 자동으로 MemorySessionStore로 전환하지 않는다.

운영 환경에서는 Redis 접속 정보, 인증, TLS, 네트워크 접근 제어 및 장애 대응 정책을 별도로 구성한다.

### 8.5 운영 환경

운영 환경에서는 Reverse Proxy를 통해 외부 요청을 통제하고 BFF와 Backend를 내부 네트워크로 연결하는 구성을 목표로 한다.

```text
                Internet
                    |
                    v
              Reverse Proxy
                    |
                    v
              React Frontend
                    |
                    v
                Node.js BFF
                    |
          +---------+---------+
          |                   |
          v                   v
     Redis Server      Spring Boot Backend
                              |
                              v
                           Database
```

실제 운영 배포 구조, 이중화, 세션 저장소 및 네트워크 구간은 인프라 설계 단계에서 확정한다.

## 9. 프로젝트 디렉터리 아키텍처

BFF는 PMIS 모노레포 내 독립 애플리케이션으로 관리한다.

현재 구현 기준의 주요 디렉터리는 다음과 같다.

```text
project-delivery-reference/
│
├── pmis-frontend/
│   └── React Application
│
├── pmis-backend/
│   └── Spring Boot Application
│
└── pmis-bff/
    │
    ├── src/
    │   ├── auth/
    │   │   ├── routes/
    │   │   ├── services/
    │   │   └── session/
    │   │
    │   ├── config/
    │   │
    │   ├── types/
    │   │
    │   └── server.ts
    │
    ├── .env
    ├── .env.example
    ├── package.json
    ├── package-lock.json
    └── tsconfig.json
```

주요 디렉터리의 역할은 다음과 같다.

| 디렉터리 | 역할 |
|---|---|
| src/auth/routes | 인증 API 라우트 |
| src/auth/services | Backend 인증 연계 및 BFF 인증 서비스 |
| src/auth/session | 세션 모델, SessionStore 및 쿠키 설정 |
| src/config | 환경 변수 및 애플리케이션 설정 |
| src/types | Fastify 확장 및 TypeScript 타입 정의 |
| src/server.ts | Fastify 서버 생성 및 초기화 |

세부 디렉터리는 기능 확장에 따라 추가할 수 있다.

Git 관리 대상에는 소스 코드, package.json, package-lock.json, tsconfig.json, .env.example 및 문서가 포함된다.

실행 환경별 `.env`, node_modules 및 빌드 결과물은 Git 관리 대상에서 제외한다.

## 10. 현재 구현 상태와 목표 아키텍처

현재 구현 상태와 목표 아키텍처를 구분한다.

### 10.1 구현 상태

| 항목 | 현재 상태 | 비고 |
|---|---|---|
| Node.js 프로젝트 | 구현 완료 | 독립 애플리케이션 |
| TypeScript | 구현 완료 | strict 설정 |
| Fastify 서버 | 구현 완료 | 서버 초기화 |
| 환경 변수 관리 | 구현 완료 | 필수값 및 형식 검증 |
| Health Check | 구현 완료 | GET /health |
| Helmet | 등록 완료 | 보안 헤더 |
| CORS | 등록 완료 | Frontend Origin 설정 |
| Cookie 플러그인 | 등록 완료 | 쿠키 처리 기반 |
| MemorySessionStore | 구현 완료 | 개발 및 테스트용 |
| RedisSessionStore | 구현 완료 | Redis 기반 |
| SessionStore Factory | 구현 완료 | 환경별 저장소 선택 |
| Redis Client 연결 | 구현 완료 | 연결 및 종료 처리 |
| 운영 환경 Redis 필수 검증 | 구현 완료 | Memory 사용 방지 |
| Backend 로그인 연계 | 구현 완료 | 일반 로그인 |
| Backend 세션 선택 로그인 연계 | 구현 완료 | 다중 세션 처리 |
| BFF 세션 생성 및 저장 | 구현 완료 | UUID 기반 세션 |
| HttpOnly 쿠키 발급 | 구현 완료 | 로그인 성공 시 |
| Refresh Token 기반 세션 TTL | 구현 완료 | JWT exp 기준 |
| BFF 사용자 인증 조회 | 향후 구현 | GET /api/auth/me |
| BFF 인증 미들웨어 | 향후 구현 | 쿠키 기반 세션 조회 |
| BFF Refresh 처리 | 향후 구현 | Backend 연계 및 세션 갱신 |
| BFF 로그아웃 처리 | 향후 구현 | Backend 세션 종료 연계 |
| BFF 세션 목록 및 종료 API | 향후 구현 | 다중 세션 관리 |
| Frontend BFF 인증 전환 | 향후 구현 | 기존 JWT 직접 관리 제거 |
| 업무 API Proxy | 향후 구현 | 허용 API 중심 |
| CSRF 방어 | 향후 구현 | Origin 및 CSRF 검증 |
| OpenAPI | 향후 구현 | API 문서화 |
| 운영 배포 | 향후 구현 | Reverse Proxy 및 인프라 |

### 10.2 현재 검증된 범위

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
- TypeScript 빌드 성공

RedisSessionStore의 실제 운영 환경 연결, Redis 장애 복구, 다중 BFF 인스턴스 환경 및 장기 세션 유지에 대한 검증은 별도 테스트 대상으로 관리한다.

### 10.3 향후 아키텍처 확장 방향

현재 구현된 기능을 기반으로 다음 순서로 확장한다.

1. BFF Cookie 기반 세션 조회 구현
2. BFF 사용자 인증 확인 API 구현
3. Backend Refresh API와 BFF 세션 갱신 연계
4. 동시 Refresh 요청 제어
5. BFF 로그아웃 및 Backend 세션 종료 연계
6. BFF 세션 목록 조회 및 개별 세션 종료 구현
7. Frontend 인증 구조를 BFF Cookie 방식으로 전환
8. 기존 Frontend JWT 직접 저장 및 관리 제거
9. Backend 업무 API Proxy 구현
10. CSRF 및 Origin 검증 정책 적용
11. 공통 오류 응답 및 요청 검증 체계 정리
12. OpenAPI 및 API 계약 문서화
13. 통합 테스트 및 운영 배포 구조 검증

## 11. 아키텍처 변경 관리

아키텍처 변경 시 다음 기준을 적용한다.

### 11.1 변경 대상

다음 항목이 변경되는 경우 본 문서를 갱신한다.

- Frontend, BFF, Backend 간 책임 변경
- 인증 및 인가 처리 방식 변경
- Access Token 및 Refresh Token 관리 방식 변경
- BFF SessionStore 구조 변경
- Backend 인증 API 계약 변경
- BFF 인증 API 추가 또는 변경
- 네트워크 및 Reverse Proxy 구성 변경
- 쿠키 및 CSRF 보안 정책 변경
- 운영 배포 및 이중화 구조 변경

### 11.2 관련 문서 관리

아키텍처 문서와 함께 다음 문서를 관리한다.

| 문서 | 관리 내용 |
|---|---|
| PMIS BFF 시스템 아키텍처 | 전체 구성 및 책임, 인증·세션, 보안 경계 |
| PMIS BFF 설치 및 실행 가이드 | 개발 환경, 패키지, 환경 변수, 빌드 및 실행 |
| BFF API 명세 | BFF 요청·응답 및 오류 계약 |
| Backend API 명세 | Backend 인증 및 업무 API 계약 |
| 인증·세션 설계 문서 | 로그인, 세션 선택, Refresh, 로그아웃 정책 |

아키텍처 문서와 실제 소스 코드 사이에 차이가 발생하지 않도록 기능 구현 완료 시 관련 문서를 함께 갱신한다.

### 11.3 문서 관리 원칙

- 현재 구현된 기능과 목표 기능을 구분한다.
- Backend API와 BFF API를 구분한다.
- 실제 구현되지 않은 API를 구현 완료로 기록하지 않는다.
- 인증 및 세션 정책 변경 시 Backend와 BFF의 책임을 함께 검토한다.
- 보안 정책 변경 시 Frontend, BFF, Backend 간 영향 범위를 검토한다.

본 문서는 PMIS BFF의 시스템 구조와 설계 기준을 관리하는 기준 문서로 사용한다.