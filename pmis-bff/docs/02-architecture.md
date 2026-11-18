# PMIS BFF 시스템 아키텍처

## 1. 문서 목적

본 문서는 PMIS의 BFF(Backend For Frontend) 도입에 따른 시스템 구성, 애플리케이션 간 책임, 요청 처리 흐름 및 보안 경계를 정의한다.

PMIS는 React Frontend, Node.js BFF, Spring Boot Backend로 구성하며, 각 애플리케이션의 역할을 분리하여 유지보수성과 보안성을 확보하는 것을 목적으로 한다.

본 문서는 현재 구현된 기본 아키텍처와 향후 인증·세션 기능을 포함한 목표 아키텍처를 함께 정의한다.

## 2. 아키텍처 설계 원칙

PMIS BFF는 다음 설계 원칙을 따른다.

### 2.1 역할 분리

- React는 사용자 인터페이스와 화면 상태를 관리한다.
- Node.js BFF는 브라우저와 Backend 사이의 요청 중계 및 브라우저 중심 기능을 담당한다.
- Spring Boot는 인증·인가 및 업무 도메인 로직을 담당한다.
- PostgreSQL은 업무 데이터의 영속성을 담당한다.

### 2.2 Backend 중심의 업무 처리

BFF는 업무 규칙을 직접 구현하지 않는다.

업무 데이터의 생성·수정·삭제 및 업무 규칙 검증은 Spring Boot Backend에서 수행한다.

BFF는 Backend API를 호출하고 브라우저에 적합한 형태로 요청과 응답을 처리한다.

### 2.3 인증 책임과 브라우저 보안 분리

브라우저에 인증 토큰을 직접 노출하지 않는 구조를 목표로 한다.

BFF는 브라우저와의 인증 쿠키 및 세션 처리를 담당하고, Spring Boot는 최종적인 인증·인가 검증을 수행한다.

### 2.4 독립적인 애플리케이션 관리

Frontend, BFF, Backend는 하나의 Git 저장소에서 관리하되 각각 독립적인 애플리케이션으로 구성한다.

각 애플리케이션은 별도의 실행 환경과 빌드 절차를 가진다.

## 3. 전체 시스템 구성

### 3.1 논리 아키텍처

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
│               React Frontend                  │
│                                               │
│  - 화면 구성                                  │
│  - 사용자 입력 및 화면 상태 관리              │
│  - BFF API 호출                               │
└───────────────────────┬───────────────────────┘
                        │
                        │ Same-Origin API Request
                        ▼
┌───────────────────────────────────────────────┐
│                Node.js BFF                    │
│                                               │
│  - 브라우저 요청 처리                         │
│  - 인증 쿠키 및 세션 관리                     │
│  - CSRF 및 Origin 검증                        │
│  - 요청 검증 및 API 중계                      │
│  - Backend 응답 처리                          │
└───────────────────────┬───────────────────────┘
                        │
                        │ Internal API Request
                        ▼
┌───────────────────────────────────────────────┐
│             Spring Boot Backend               │
│                                               │
│  - 인증 및 인가 검증                          │
│  - 업무 도메인 로직                           │
│  - 데이터 검증 및 처리                        │
│  - 데이터 접근 계층                           │
└───────────────────────┬───────────────────────┘
                        │
                        │ SQL
                        ▼
┌───────────────────────────────────────────────┐
│                 PostgreSQL                    │
│                                               │
│  - 업무 데이터 저장                           │
│  - 기준정보 및 시스템 데이터 관리            │
└───────────────────────────────────────────────┘
```

### 3.2 애플리케이션 구성

| 구분 | 애플리케이션 | 기술 | 주요 책임 |
|---|---|---|---|
| Frontend | pmis-frontend | React | 사용자 화면 및 UI 상태 관리 |
| BFF | pmis-bff | Node.js, TypeScript, Fastify | 브라우저 요청 및 인증 쿠키 처리 |
| Backend | pmis-backend | Spring Boot | 인증·인가 및 업무 API |
| Database | PostgreSQL | PostgreSQL | 데이터 영속성 |

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

Frontend는 인증 토큰을 직접 저장하거나 관리하지 않는 구조로 전환한다.

인증 상태는 BFF의 사용자 정보 조회 API 등을 통해 확인하는 것을 목표로 한다.

### 4.2 Node.js BFF

Node.js BFF는 브라우저와 Spring Boot Backend 사이의 중간 계층이다.

주요 책임은 다음과 같다.

- 브라우저 요청 수신
- 인증 쿠키 처리
- 세션 및 토큰 관리
- 요청 Origin 검증
- CSRF 방어
- 요청 데이터 및 파라미터 검증
- 허용된 Backend API 호출
- Backend 인증 정보 전달
- Backend 응답 및 오류 처리
- 브라우저에 적합한 API 응답 제공

BFF는 허용된 Backend API만 호출하도록 제한한다.

임의의 URL을 전달받아 호출하는 범용 Proxy 형태로 구현하지 않는다.

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

BFF를 통해 요청이 전달되더라도 Backend는 인증과 권한을 독립적으로 검증해야 한다.

BFF를 통과했다는 사실만으로 요청을 신뢰하지 않는다.

### 4.4 PostgreSQL

PostgreSQL은 PMIS 업무 데이터를 저장하는 데이터베이스이다.

데이터베이스 접근은 Spring Boot Backend를 통해서만 수행한다.

React와 BFF는 데이터베이스에 직접 접근하지 않는다.

## 5. 요청 처리 아키텍처

### 5.1 일반 업무 API 요청

일반적인 업무 조회 요청의 흐름은 다음과 같다.

```text
1. 사용자가 React 화면에서 업무 조회 요청
                  |
                  v
2. React가 BFF API 호출
                  |
                  v
3. BFF가 요청 Origin 및 입력값 검증
                  |
                  v
4. BFF가 Spring Boot API 호출
                  |
                  v
5. Spring Boot가 인증 및 권한 검증
                  |
                  v
6. Spring Boot가 업무 로직 수행
                  |
                  v
7. Spring Boot가 PostgreSQL 조회
                  |
                  v
8. Spring Boot가 API 응답 반환
                  |
                  v
9. BFF가 응답 처리 후 브라우저에 전달
                  |
                  v
10. React가 화면 갱신
```

### 5.2 API 요청 경로

브라우저는 BFF를 통해 API를 호출한다.

개발 환경에서는 Frontend와 BFF가 별도 포트로 실행될 수 있다.

운영 환경에서는 Reverse Proxy를 활용하여 동일 Origin으로 요청을 처리하는 구성을 목표로 한다.

예시:

| 구분 | URL |
|---|---|
| Frontend | `https://pmis.example.com/` |
| BFF API | `https://pmis.example.com/api/` |
| Backend API | 내부 네트워크에서만 접근 가능한 별도 주소 |

운영 환경에서 브라우저가 Spring Boot Backend에 직접 접근하지 않도록 네트워크 및 Reverse Proxy 구성을 적용한다.

## 6. 인증 및 세션 아키텍처

### 6.1 인증 처리 방향

기존 Spring Boot JWT 인증 구조를 기반으로 BFF를 연계한다.

목표 인증 흐름은 다음과 같다.

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
   | 사용자 인증 요청
   v
Spring Boot Backend
   |
   | 사용자 인증 및 토큰 발급
   v
Node.js BFF
   |
   | 인증 정보 관리
   | HttpOnly Cookie 발급
   v
Browser
```

브라우저는 인증 토큰을 직접 보관하지 않고 BFF가 발급한 인증 쿠키를 사용한다.

BFF는 인증 정보와 토큰을 서버 측에서 관리하는 구조를 목표로 한다.

### 6.2 인증 토큰 관리

목표 구조에서는 다음 정책을 적용한다.

| 항목 | 관리 방향 |
|---|---|
| Access Token | BFF 서버 측에서 관리 |
| Refresh Token | BFF 서버 측에서 관리 |
| 인증 쿠키 | 브라우저에 HttpOnly 쿠키로 전달 |
| 토큰 검증 | Spring Boot Backend에서 최종 검증 |
| 토큰 갱신 | BFF가 Backend와 연계하여 처리 |
| 로그아웃 | 세션 및 인증 정보 폐기 |

실제 토큰 저장 방식, JWT 검증 키 공유 방식 및 세션 저장소는 인증 설계 단계에서 확정한다.

### 6.3 쿠키 보안

인증 쿠키에는 다음 보안 속성을 적용하는 것을 원칙으로 한다.

| 속성 | 목적 |
|---|---|
| HttpOnly | JavaScript에서 쿠키 직접 접근 방지 |
| Secure | HTTPS 연결에서만 쿠키 전송 |
| SameSite | 교차 사이트 요청에서 쿠키 전송 제한 |
| Path | 쿠키 적용 경로 제한 |

SameSite 설정만으로 CSRF 방어가 완성되는 것은 아니므로 Origin 검증 및 CSRF 방어 정책을 함께 적용한다.

HttpOnly 쿠키 역시 XSS로 인한 인증된 요청 실행 자체를 방지하지는 못하므로 Frontend의 출력 인코딩과 CSP 등 추가적인 보안 대책이 필요하다.

### 6.4 다중 세션 관리

PMIS는 사용자별 최대 동시 세션 수를 관리자가 설정할 수 있는 구조를 목표로 한다.

최대 세션 수를 초과하는 경우 신규 로그인을 자동 거부하거나 기존 세션을 자동 종료하지 않는다.

인증 성공 후 기존 활성 세션 목록을 사용자에게 제시하고, 사용자가 종료할 세션을 직접 선택하도록 한다.

선택된 세션을 폐기한 뒤 신규 로그인을 허용한다.

이 정책을 구현하려면 Backend에 세션 단위 식별 및 관리 기능이 필요하며, 동시 로그인 요청에 대해서도 최대 세션 수가 초과되지 않도록 원자적인 처리가 필요하다.

### 6.5 인증 및 세션 API

인증 기능은 다음 API를 중심으로 설계한다.

| API | 기능 |
|---|---|
| `POST /api/auth/login` | 로그인 및 인증 처리 |
| `GET /api/auth/me` | 현재 로그인 사용자 정보 조회 |
| `POST /api/auth/refresh` | 인증 토큰 갱신 |
| `POST /api/auth/logout` | 현재 세션 로그아웃 |
| `GET /api/auth/sessions` | 활성 세션 목록 조회 |
| `DELETE /api/auth/sessions/{sessionId}` | 지정 세션 종료 |

위 API는 목표 API 구성안이며, 실제 구현 및 URL은 Backend와 BFF의 API 계약을 확정한 후 결정한다.

## 7. 보안 아키텍처

### 7.1 네트워크 접근 통제

운영 환경에서는 다음 접근 원칙을 적용한다.

- 외부 브라우저는 Frontend 및 BFF를 통해 서비스를 이용한다.
- Spring Boot Backend는 내부 네트워크에서 접근하도록 구성한다.
- Backend API는 BFF 또는 허용된 내부 시스템에서만 접근할 수 있도록 제한한다.
- PostgreSQL은 Backend에서만 접근할 수 있도록 제한한다.

### 7.2 API 접근 통제

BFF는 Backend 호출 대상을 사전에 정의된 API로 제한한다.

사용자가 전달한 임의의 URL을 그대로 Backend 호출에 사용하지 않는다.

이를 통해 SSRF 및 비인가 내부 시스템 접근 위험을 줄인다.

### 7.3 인증 및 인가 검증

인증 및 인가의 최종 책임은 Spring Boot Backend에 있다.

BFF는 브라우저 요청을 처리하고 인증 정보를 전달하지만, Backend의 권한 검증을 대체하지 않는다.

### 7.4 CSRF 및 CORS

브라우저가 인증 쿠키를 자동으로 전송하는 구조에서는 CSRF 방어가 필요하다.

다음 항목을 적용한다.

- 허용된 Origin 검증
- CSRF 방어 정책 적용
- 쿠키 SameSite 속성 설정
- 필요 시 CSRF Token 검증
- 허용된 CORS Origin만 등록
- 운영 환경에서 불필요한 교차 출처 요청 제한

운영 환경에서 Frontend와 BFF를 동일 Origin으로 서비스하는 경우 CORS 사용 필요성을 별도로 검토한다.

### 7.5 보안 책임 구분

| 보안 항목 | 담당 |
|---|---|
| 브라우저 인증 쿠키 | BFF |
| Origin 및 CSRF 검증 | BFF |
| 요청 입력값 검증 | BFF 및 Backend |
| JWT 검증 | Spring Boot Backend |
| 업무 권한 검증 | Spring Boot Backend |
| 데이터 접근 통제 | Spring Boot Backend 및 DB |
| 네트워크 접근 통제 | 인프라 및 배포 환경 |

## 8. 개발 및 운영 환경 구성

### 8.1 개발 환경

개발 환경에서는 각 애플리케이션을 독립적으로 실행한다.

| 구성 요소 | 실행 방식 |
|---|---|
| React Frontend | 개발 서버 |
| Node.js BFF | Fastify 개발 서버 |
| Spring Boot Backend | Spring Boot 애플리케이션 |
| PostgreSQL | 로컬 또는 Docker 환경 |

현재 BFF 개발 환경의 기본 설정은 다음과 같다.

```env
NODE_ENV=development
HOST=127.0.0.1
PORT=4000
FRONTEND_ORIGIN=http://localhost:5173
BACKEND_API_URL=http://127.0.0.1:8765
```

각 환경의 실제 Backend 주소 및 포트는 `.env`에서 별도로 관리한다.

### 8.2 운영 환경

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
                      v
             Spring Boot Backend
                      |
                      v
                 PostgreSQL
```

실제 운영 배포 구조, 이중화, 세션 저장소 및 네트워크 구간은 인프라 설계 단계에서 확정한다.

## 9. 프로젝트 디렉터리 아키텍처

BFF는 PMIS 모노레포 내 독립 애플리케이션으로 관리한다.

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
    ├── docs/
    │   └── api/
    │
    ├── src/
    │   ├── config/
    │   ├── plugins/
    │   ├── routes/
    │   ├── schemas/
    │   ├── services/
    │   ├── types/
    │   ├── utils/
    │   └── server.ts
    │
    ├── .env
    ├── .env.example
    ├── package.json
    ├── package-lock.json
    └── tsconfig.json
```

각 디렉터리의 역할은 다음과 같다.

| 디렉터리 | 역할 |
|---|---|
| `config` | 환경 변수 및 애플리케이션 설정 |
| `plugins` | Fastify 플러그인 등록 및 공통 기능 |
| `routes` | API 라우트 정의 |
| `schemas` | 요청 및 응답 데이터 스키마 |
| `services` | Backend 연계 및 업무 흐름 처리 |
| `types` | TypeScript 타입 정의 |
| `utils` | 공통 유틸리티 |
| `docs/api` | API 명세 및 OpenAPI 문서 |

## 10. 현재 구현 상태와 목표 아키텍처

현재 구현 상태와 목표 아키텍처를 구분한다.

| 항목 | 현재 상태 | 목표 상태 |
|---|---|---|
| Node.js 프로젝트 | 구성 완료 | 유지 |
| Fastify 서버 | 구성 완료 | 유지 및 확장 |
| 환경 변수 관리 | 구성 완료 | 환경별 설정 확장 |
| Health Check | 구현 완료 | 운영 모니터링 확장 |
| Helmet | 등록 완료 | 보안 설정 구체화 |
| CORS | 등록 완료 | 운영 Origin 정책 확정 |
| Cookie | 패키지 설치 완료, 기능 미구현 | 인증 쿠키 처리 |
| 인증 API | 미구현 | 로그인 및 인증 상태 관리 |
| 세션 관리 | 미구현 | 다중 세션 관리 |
| Backend API 연계 | 미구현 | 허용된 API 중계 |
| CSRF 방어 | 미구현 | Origin 및 CSRF 검증 |
| OpenAPI | 미작성 | API 명세 관리 |

현재 구현되지 않은 기능은 향후 설계 및 개발 작업으로 관리한다.

## 11. 향후 아키텍처 확장 방향

기본 BFF 구성을 완료한 이후 다음 순서로 확장한다.

1. Frontend와 BFF 간 API 계약 정의
2. BFF와 Spring Boot 간 API 계약 정의
3. 기존 Spring Boot JWT 인증 구조 분석 및 연계 방안 확정
4. 인증 쿠키 및 토큰 관리 설계
5. 사용자 세션 관리 구조 설계
6. 로그인·로그아웃·토큰 갱신 API 구현
7. Backend API Proxy 및 요청 검증 구현
8. Frontend 인증 상태 관리 구조 변경
9. CSRF 및 보안 정책 적용
10. 통합 테스트 및 운영 배포 구조 검증

아키텍처 변경 사항은 본 문서와 API 명세에 함께 반영한다.