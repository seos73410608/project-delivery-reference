# ADR-001. PMIS 독립 데이터 플랫폼 및 PostgreSQL 전환

- **문서 유형:** Architecture Decision Record (ADR)
- **상태:** Accepted - Implementation Pending
- **작성일:** 2026-11-10
- **대상 시스템:** PMIS
- **관련 Feature:** Evidence Context Intelligence
- **관련 기술:** Spring Boot, Spring Data JPA, MariaDB, PostgreSQL, pgvector, Neo4j, Spring AI

---

## 1. 결정 배경 (Context)

현재 PMIS는 Spring Boot 기반 백엔드와 React 기반 프론트엔드로 구성되어 있으며, 업무 데이터 저장소로 MariaDB를 사용하고 있다.

한편 MasterRuntime과 Logistics 플랫폼 역시 MariaDB를 사용하고 있다.

초기에는 동일한 DB 엔진을 사용하는 것이 개발 및 운영 측면에서 편리할 수 있으나, 각 플랫폼이 독립적인 업무 영역과 데이터 책임을 갖도록 확장할 경우 데이터 저장소의 독립성도 고려해야 한다.

특히 PMIS는 기존 프로젝트 관리 기능을 넘어 다음과 같은 경험 관리 기능을 확장할 예정이다.

- Business Event: 업무 수행 과정에서 발생한 주요 사건과 행위 기록
- Context: 사건과 관련된 업무 배경, 판단, 근거 및 결과의 연결
- Knowledge: 검토 및 승인된 재사용 가능한 업무 경험
- Vector Search: 의미 기반 경험 검색
- Graph Search: 경험과 업무 객체 사이의 관계 탐색
- AI-assisted Reuse: 검색된 경험을 기반으로 한 AI 활용

이러한 확장을 고려하면 PMIS의 업무 데이터와 Context 및 Knowledge 데이터가 하나의 독립적인 데이터 플랫폼 안에서 관리되는 것이 적절하다.

따라서 PMIS를 기존 MariaDB 기반 구조에서 PostgreSQL 기반 구조로 전환하는 방안을 채택한다.

현재 PMIS의 데이터는 개발 및 테스트 목적으로 생성된 데이터이며, 기존 데이터를 보존하거나 신규 DB로 이관할 필요가 없다.

또한 현재 개발 환경에는 PostgreSQL이 설치되어 있지 않으므로, 이번 전환은 PostgreSQL 설치 및 기반 환경 구축부터 시작해야 한다.

이에 따라 이번 전환은 기존 데이터의 이전 작업이 아니라 PostgreSQL 설치, 신규 Schema 구성, 계정 및 보안 설정, 애플리케이션 호환성 확보를 중심으로 진행한다.

이번 결정은 단순한 DB 엔진 변경이 아니라 PMIS의 데이터 소유권과 운영 경계를 명확하게 정의하기 위한 아키텍처 결정이다.

## 2. 해결해야 할 문제 (Problem Statement)

현재 구조에서는 PMIS, MasterRuntime, Logistics가 동일한 MariaDB 엔진을 사용한다.

그러나 장기적으로 다음과 같은 문제가 발생할 수 있다.

### 2.1 플랫폼 간 운영 종속성

- 동일 DB 인프라 및 운영 정책에 대한 의존성이 커질 수 있다.
- 특정 플랫폼의 DB 변경이나 장애가 다른 플랫폼의 운영에 영향을 줄 수 있다.
- 플랫폼별 독립적인 백업, 복구 및 성능 관리 경계가 불명확해질 수 있다.

단, DB 엔진이 같다는 사실만으로 실제 운영 종속성이 발생하는 것은 아니다. 동일 인스턴스, 계정, 네트워크 및 운영 체계를 공유하는지 여부가 더 직접적인 판단 요소다.

### 2.2 PMIS 데이터 확장성

PMIS는 기존 프로젝트 관리 데이터 외에 다음 데이터를 관리할 예정이다.

- Business Event
- Context
- Knowledge
- Knowledge Chunk
- Embedding
- Search Metadata

특히 Knowledge에 대한 의미 기반 검색을 지원하기 위해 Vector Search 기능이 필요하다.

### 2.3 데이터 소유권 및 통합 경계

MasterRuntime과 Logistics가 PMIS의 내부 업무 테이블을 직접 조회하거나 변경하는 방식은 플랫폼 간 결합도를 높일 수 있다.

각 플랫폼은 자신의 업무 데이터를 소유하고, 다른 플랫폼은 공개된 API 또는 Event를 통해 필요한 정보를 교환해야 한다.

### 2.4 PostgreSQL 기반 환경 부재

현재 개발 환경에는 PostgreSQL이 설치되어 있지 않다.

따라서 DBMS 변경을 위해서는 다음 기반 작업이 선행되어야 한다.

- PostgreSQL 버전 및 설치 방식 결정
- PostgreSQL 설치 및 초기화
- PostgreSQL 서비스 구성 및 기동
- 관리자 및 애플리케이션 계정 구성
- Database 및 Schema 생성
- 네트워크 및 접근 제어 설정
- PMIS Backend 연결 설정

PostgreSQL 설치와 계정 구성은 단순한 개발 편의 작업이 아니라 PMIS 독립 데이터 플랫폼의 기반 환경을 구성하는 작업으로 관리한다.

## 3. 아키텍처 결정 (Decision)

### 3.1 PMIS 데이터 저장소

PMIS의 업무 데이터 저장소를 MariaDB에서 PostgreSQL로 전환한다.

PostgreSQL은 PMIS의 System of Record로 사용한다.

PMIS가 관리하는 다음 데이터는 PostgreSQL을 기준으로 저장하고 관리한다.

- 사용자 및 인증 관련 데이터
- 프로젝트 및 WBS
- 일정 및 작업
- Issue 및 Risk
- Change Management
- Evidence 및 Evidence Version
- Business Event
- Context
- Knowledge
- Vector Search 관련 데이터 및 메타데이터

기존 업무 데이터와 신규 경험 관리 데이터는 동일한 PMIS 데이터 소유권과 권한 체계 안에서 관리한다.

현재 MariaDB에 저장된 개발 및 테스트 데이터는 이관하지 않는다. PostgreSQL에 신규 Schema를 생성하고 필요한 초기 데이터 및 테스트 데이터는 새로 구성한다.

### 3.2 다른 플랫폼의 DB 유지

MasterRuntime과 Logistics는 현재 MariaDB를 유지한다.

이번 PostgreSQL 전환의 범위는 PMIS로 한정한다.

| 플랫폼 | DBMS | 데이터 소유권 |
|---|---|---|
| PMIS | PostgreSQL | PMIS |
| MasterRuntime | MariaDB | MasterRuntime |
| Logistics | MariaDB | Logistics |

각 플랫폼은 독립적인 데이터 저장소와 운영 경계를 갖는다.

### 3.3 플랫폼 간 데이터 통합

플랫폼 간 데이터 통합은 DB 직접 접근이 아닌 API 및 Event 기반으로 수행한다.

기본 원칙은 다음과 같다.

- 다른 플랫폼의 업무 테이블에 직접 접근하지 않는다.
- API를 통해 필요한 업무 데이터를 조회한다.
- Event를 통해 업무 상태 및 주요 변경 사항을 전달한다.
- 플랫폼별 인증 및 접근 권한을 검증한다.
- 데이터 동기화가 필요한 경우 원본 데이터의 소유 플랫폼을 명확히 한다.

### 3.4 Vector Search

PostgreSQL의 pgvector 확장을 Vector Search 구현 후보로 채택한다.

pgvector는 PMIS의 업무 데이터 저장소와 별개의 업무 원장 시스템이 아니다.

PostgreSQL에 저장된 Context 및 Knowledge를 대상으로 의미 기반 검색을 제공하는 검색 인덱스 역할을 수행한다.

주요 원칙은 다음과 같다.

- 원본 Knowledge는 PostgreSQL 업무 데이터로 관리한다.
- Embedding은 검색을 위한 파생 데이터로 취급한다.
- Vector 검색 결과는 원본 데이터의 유효성 및 승인 상태를 재검증한다.
- 검색 시 사용자 및 프로젝트 접근 권한을 적용한다.
- Embedding 모델과 버전, 차원, Chunk 정책을 관리한다.
- 향후 검색 엔진 변경이 가능하도록 Vector Search Layer를 논리적으로 분리한다.

pgvector 도입은 PostgreSQL 전환과 동일한 작업으로 취급하지 않는다. PostgreSQL 전환을 먼저 완료하고 별도 PoC를 통해 검색 품질과 운영 비용을 검증한다.

### 3.5 Graph Search

Neo4j는 Context 및 Knowledge 사이의 관계를 탐색하기 위한 선택적 구성 요소로 검토한다.

도입 여부는 다음 기준으로 판단한다.

- 관계 탐색의 복잡도
- 다단계 연결 탐색의 빈도
- PostgreSQL JOIN 및 Recursive Query 대비 효용성
- 별도 DB 운영에 따른 비용과 복잡도
- 실제 사용자 검색 시나리오의 개선 효과

Neo4j 도입을 PostgreSQL 전환의 필수 조건으로 정의하지 않는다.

### 3.6 AI Integration

Spring AI는 PMIS 애플리케이션 내부의 AI 연동 및 검색·응답 구성 기능에 사용한다.

Spring AI는 데이터의 원본 소유자가 아니며, 승인된 Knowledge를 자동으로 생성하거나 확정하는 권한을 갖지 않는다.

AI가 생성한 결과는 검토 대상이며, 조직의 재사용 지식으로 전환하려면 별도의 검토 및 승인 절차를 거쳐야 한다.

### 3.7 PostgreSQL 설치 및 계정 운영 원칙

PostgreSQL은 PMIS 전용 데이터 저장소로 구성한다.

설치 및 운영 시 다음 원칙을 적용한다.

- PostgreSQL 설치 방식과 실행 환경을 명확히 정의한다.
- PostgreSQL 관리자 계정과 PMIS 애플리케이션 계정을 분리한다.
- PMIS 애플리케이션은 전용 계정을 사용한다.
- 애플리케이션 계정에는 업무 처리에 필요한 최소 권한만 부여한다.
- Schema 변경 및 Migration 실행 권한은 애플리케이션의 일반 실행 권한과 분리하는 것을 원칙으로 한다.
- 계정 비밀번호 및 접속 정보는 소스 코드에 직접 저장하지 않는다.
- 환경변수 또는 별도 Secret 관리 방식을 사용한다.
- PostgreSQL 외부 접근은 기본적으로 제한하고 허용된 Backend 접근 경로만 개방한다.
- 설치, 설정, 계정, 권한 및 운영 절차를 문서화한다.

## 4. 목표 아키텍처 (Target Architecture)

```text
                       External User
                            |
                           HTTPS
                            |
                     WAF / Reverse Proxy
                            |
                     PMIS Frontend
                         React
                            |
                       HTTPS / API
                            |
                 PMIS Backend Network
                      Spring Boot
                            |
            +--------------+--------------+
            |              |              |
        PMIS Domain     Spring AI      Integration
         Services      Integration      Services
            |              |              |
            +--------------+--------------+
                            |
                   PMIS PostgreSQL
                            |
             +--------------+--------------+
             |              |              |
         Business        Context /       Vector
           Data           Knowledge     Search
                            |
                         pgvector

                Optional Graph Database
                         Neo4j
                            |
                   Context Relationships

                    Runtime File Storage
                            |
                    Evidence / Artifacts


      +---------------------+---------------------+
      |                                           |
 MasterRuntime                                Logistics
 Spring Boot                                  Application
      |                                           |
   MariaDB                                     MariaDB
      |                                           |
      +------------ API / Event ------------------+
```

### 4.1 네트워크 및 보안 원칙

- 외부 사용자는 HTTPS를 통해 PMIS Frontend에 접근한다.
- Frontend는 DB에 직접 접근하지 않는다.
- Backend는 내부 네트워크에 배치한다.
- PostgreSQL은 내부 DB 네트워크에 배치한다.
- Backend에서 PostgreSQL로 필요한 포트만 허용한다.
- 사용자 인증 및 업무 권한 검증은 Backend에서 수행한다.
- 프로젝트 단위 데이터 접근 권한을 별도로 정의한다.
- 플랫폼 간 연동은 인증된 API 또는 Event 경로만 허용한다.
- DB 계정은 애플리케이션별 최소 권한 원칙을 적용한다.

별도 DBMS 사용만으로 네트워크 및 보안 격리가 완성되는 것은 아니다. 실제 격리는 인스턴스, 네트워크, 계정, 접근 정책, 백업 및 운영 권한을 별도로 구성하여 달성한다.

### 4.2 PostgreSQL 구성 경계

PostgreSQL은 PMIS Backend에서만 업무 데이터에 접근할 수 있도록 구성한다.

논리적인 접근 구조는 다음과 같다.

```text
PMIS Backend
     |
     | Application DB Account
     |
PostgreSQL
     |
     +-- PMIS Database
           |
           +-- PMIS Schema
                 |
                 +-- Business Tables
                 +-- Context / Knowledge Tables
                 +-- Search-related Tables
```

계정과 권한은 다음과 같이 역할을 분리하는 것을 기본 방향으로 한다.

| 계정 구분 | 용도 | 권한 방향 |
|---|---|---|
| PostgreSQL 관리자 | 초기 설치 및 DB 관리 | 관리 작업 |
| Schema Owner | Schema 및 객체 변경 | DDL 및 객체 관리 |
| PMIS Application | Spring Boot 업무 처리 | 필요한 업무 데이터 CRUD |
| Read Only | 조회 및 분석 | 제한된 SELECT |

실제 계정명, 권한 범위 및 Migration 실행 계정은 PostgreSQL 설치 및 DB 운영 설계 단계에서 확정한다.

## 5. 검토한 대안 (Alternatives)

### Alternative A. MariaDB 유지 및 Spring AI 연동

기존 MariaDB를 유지하고 Spring AI를 연결한다.

장점:

- DB 전환 비용이 없다.
- 기존 Entity 및 Repository 구조를 최대한 유지할 수 있다.
- 초기 개발 속도가 빠르다.

단점:

- PMIS의 PostgreSQL 기반 독립 데이터 플랫폼이라는 방향과 맞지 않는다.
- 향후 Vector Search 구현 시 별도 저장소 도입 여부를 다시 검토해야 한다.
- 이번 Feature 확장 이후 DB를 변경하면 추가적인 데이터 모델 변경 비용이 발생할 수 있다.

평가: 기존 시스템을 최소한으로 유지하는 데는 유리하지만 목표 아키텍처와의 일관성이 낮다.

### Alternative B. MariaDB 업무 데이터 + PostgreSQL Vector Store

기존 PMIS 업무 데이터는 MariaDB에 유지하고 PostgreSQL은 Vector Search 전용으로 도입한다.

장점:

- 기존 업무 데이터의 이전 작업이 필요 없다.
- pgvector를 독립적으로 검증할 수 있다.
- Vector Search 기능을 빠르게 실험할 수 있다.

단점:

- 두 DBMS의 운영 및 백업 체계를 관리해야 한다.
- 업무 데이터와 검색 데이터 사이의 정합성을 별도로 관리해야 한다.
- PMIS 내부 데이터 저장소가 이원화된다.
- 플랫폼 독립화 목적을 충분히 달성하지 못한다.

평가: Vector Search 기술 검증에는 적합할 수 있지만 PMIS의 장기 데이터 플랫폼 방향으로는 우선순위가 낮다.

### Alternative C. PMIS 업무 데이터 PostgreSQL 전환 + pgvector 활용

PMIS의 업무 데이터 저장소를 PostgreSQL로 전환하고 Context 및 Knowledge 확장에 pgvector를 활용한다.

기존 MariaDB의 개발 및 테스트 데이터는 이관하지 않고 신규 PostgreSQL Schema를 구성한다.

장점:

- PMIS의 업무 데이터 저장소를 단일 DBMS 기반으로 관리할 수 있다.
- 업무 데이터와 경험 관리 데이터의 일관된 관리가 가능하다.
- pgvector를 활용하여 별도 Vector DB 없이 의미 기반 검색을 구현할 수 있다.
- 플랫폼 간 DB 운영 종속성을 줄이는 방향으로 설계할 수 있다.
- 기존 개발 데이터 이관 작업 없이 신규 환경에서 구조를 정리할 수 있다.

단점:

- PostgreSQL 설치 및 초기 환경 구성이 필요하다.
- MariaDB 종속 SQL 및 타입을 확인해야 한다.
- Entity 및 Repository의 PostgreSQL 호환성 검증이 필요하다.
- PostgreSQL 운영 및 모니터링 체계를 새로 준비해야 한다.
- 기존 기능의 회귀 테스트와 초기 테스트 데이터 재구성이 필요하다.

평가: 목표 아키텍처와 가장 높은 일관성을 갖는 대안이다.

**최종 선택: Alternative C**

## 6. 전환 범위 (Migration Scope)

### 6.1 포함 범위

#### PostgreSQL 설치 및 환경 구성

- PostgreSQL 설치 환경 및 배포 방식 결정
- PostgreSQL 버전 선정
- PostgreSQL 설치 가이드 작성
- PostgreSQL 설치 및 초기화
- PostgreSQL 서비스 및 프로세스 구성
- 데이터 디렉터리 및 설정 파일 위치 정의
- PostgreSQL 서비스 기동 및 상태 확인
- 서비스 자동 시작 설정
- PostgreSQL 로그 및 상태 확인 방법 정의
- 설치 및 재설치 절차 문서화

#### 데이터베이스 및 계정 구성

- PMIS 전용 Database 생성
- PMIS 전용 Schema 구성
- PostgreSQL 관리자 계정과 애플리케이션 계정 분리
- PMIS 애플리케이션 전용 DB 계정 생성
- Schema Owner 및 Migration 실행 계정의 역할 정의
- Database 및 Schema 권한 설정
- 테이블 및 Sequence 권한 설정
- 최소 권한 원칙 적용
- 계정 비밀번호 및 Secret 관리 정책 수립

#### 네트워크 및 보안 설정

- PostgreSQL Listening Address 설정
- PostgreSQL Port 설정
- pg_hba.conf 접근 제어 설정
- Backend 서버에서 PostgreSQL로의 네트워크 접근 허용
- 불필요한 외부 접속 차단
- 방화벽 정책 및 접근 경로 검증
- 운영 환경의 TLS 적용 필요성 검토

#### PMIS 애플리케이션 연동

- Spring Boot JDBC Driver 변경
- Spring Data JPA 및 Hibernate PostgreSQL 호환성 검토
- Database Connection 설정 변경
- Entity 및 Repository 호환성 검토
- Native SQL 및 DB 종속 기능 변경
- Transaction 및 Lock 검증
- 환경변수 및 Secret 설정 변경

#### Schema 및 기능 검증

- PostgreSQL 신규 Schema 생성
- PK, FK, Unique Constraint 및 Index 구성
- DB Migration 관리 체계 도입
- 초기 데이터 및 테스트 데이터 재구성
- 주요 기능 회귀 테스트
- 백업 및 복구 검증

현재 MariaDB에 저장된 개발 및 테스트 데이터는 이관하지 않는다.

### 6.2 제외 범위

- 기존 MariaDB 개발 데이터의 보존 및 이관
- MasterRuntime DBMS 변경
- Logistics DBMS 변경
- 플랫폼 간 DB 직접 공유
- Neo4j 필수 도입
- pgvector 기반 전체 데이터 재색인
- AI 모델 학습 및 자체 모델 개발
- 기존 업무 기능의 전면 재작성

### 6.3 기존 MariaDB 환경 처리

기존 MariaDB 개발 DB는 PostgreSQL 전환 검증이 완료될 때까지 참고용으로 임시 보존할 수 있다.

신규 기능 개발과 테스트의 기준 DB는 PostgreSQL로 전환한다.

PostgreSQL 전환 및 검증 완료 후 기존 MariaDB 개발 DB의 유지 필요성을 판단한다.

기존 DB를 최종 폐기하기 전에는 Runtime File Storage에 남아 있는 개발용 파일 및 참조 데이터의 정리 필요성도 확인한다.

## 7. 전환 실행 계획 (Migration Plan)

### Phase 1. 사전 영향도 분석

- 기존 MariaDB DDL 수집
- Entity 및 JPA Mapping 확인
- Repository 및 Native Query 조사
- DB 전용 함수 및 SQL 확인
- AUTO_INCREMENT, IDENTITY, SEQUENCE 사용 방식 확인
- 날짜·시간, JSON, 문자열 및 숫자 타입 비교
- ENUM 및 Boolean 타입 처리 방식 확인
- Unique Constraint 및 Collation 차이 검토
- 트랜잭션 및 Lock 사용 방식 확인
- 기존 업무 기능별 테스트 시나리오 확인

산출물:

- PostgreSQL 전환 영향도 분석서
- DB 객체 변환 목록
- Entity 및 Repository 수정 대상 목록
- 테스트 항목 목록

### Phase 2. PostgreSQL 설치 및 기반 환경 구축

- PostgreSQL 버전 및 설치 방식 결정
- 개발 환경 PostgreSQL 설치
- 설치 경로 및 데이터 디렉터리 구성
- PostgreSQL 초기화
- PostgreSQL 서비스 등록 및 기동 확인
- 서비스 자동 시작 설정
- 기본 설정 파일 및 로그 위치 확인
- PostgreSQL 기본 설정 검토
- PostgreSQL 관리자 계정 확인
- PMIS 전용 Database 생성
- PMIS 전용 Schema 생성

산출물:

- PostgreSQL 설치 가이드
- PostgreSQL 환경 구성서
- 설치 및 서비스 기동 검증 결과서
- Database 및 Schema 구성 내역

### Phase 3. 계정, 권한 및 네트워크 보안 구성

- PostgreSQL 관리자 계정과 애플리케이션 계정 분리
- PMIS 전용 애플리케이션 계정 생성
- Schema Owner 및 Migration 계정의 역할 정의
- Database 및 Schema 접근 권한 설정
- 테이블, Sequence 및 Schema 권한 검증
- Listening Address 설정
- Port 및 pg_hba.conf 설정
- Backend 서버 접근 허용 정책 구성
- 불필요한 외부 접속 차단
- 방화벽 및 접속 경로 테스트
- 환경변수 및 Secret 관리 방식 정의
- 애플리케이션 계정의 최소 권한 검증

산출물:

- PostgreSQL 계정 및 권한 구성서
- 네트워크 및 접근 제어 설정서
- DB 접속 및 권한 검증 결과서
- Secret 및 환경변수 구성 가이드

### Phase 4. 신규 Schema 구성

- PostgreSQL DDL 생성
- 기존 MariaDB DDL을 참고하여 PostgreSQL Schema 재구성
- PK 및 Identity 생성 정책 적용
- FK 및 Unique Constraint 구성
- Index 생성 및 검증
- Enum 및 Boolean 타입 매핑 확인
- 날짜·시간 타입과 문자열 비교 정책 검증
- Schema 생성 및 재생성 테스트
- 필요한 초기 데이터 및 테스트 데이터 신규 구성
- Schema Migration Script 버전 관리

산출물:

- PostgreSQL Schema Migration Script
- Schema 구성 및 검증 결과서
- 초기 데이터 및 테스트 데이터 구성 내역

### Phase 5. PMIS 애플리케이션 연동

- Spring Boot DB Driver 변경
- Spring Data JPA 및 Hibernate 설정 확인
- PostgreSQL Connection 설정 변경
- 환경변수 및 Secret 반영
- JPA Mapping 호환성 수정
- Repository 및 Query 수정
- Transaction 및 Lock 검증
- 주요 API 테스트
- Frontend 연동 테스트

산출물:

- PostgreSQL 호환 Backend
- 애플리케이션 DB 설정 가이드
- API 회귀 테스트 결과
- DBMS 전환 기능 검증 결과서

### Phase 6. 안정화 및 운영 준비

- 주요 업무 기능 전체 회귀 테스트
- FK 및 Unique Constraint 검증
- 트랜잭션 및 동시성 테스트
- 성능 및 실행계획 검토
- 백업 및 복구 테스트
- 장애 및 복구 절차 검증
- PostgreSQL 로그 및 상태 점검 방법 확인
- 서비스 재기동 및 자동 시작 검증
- 계정 및 권한 재검증
- 기존 MariaDB 개발 DB의 참조 필요성 검토
- PostgreSQL 기준 개발 환경 전환 확인

산출물:

- 안정화 결과서
- 백업 및 복구 검증서
- PostgreSQL 운영 및 점검 가이드
- PostgreSQL 전환 완료 승인 기록

### Phase 7. 경험 검색 기반 확장

- Business Event
- Context / Knowledge
- pgvector PoC
- Vector Search Layer
- Hybrid Search
- Neo4j 도입 필요성 검토
- Spring AI 연동

산출물:

- Context / Knowledge 데이터 모델
- Vector Search PoC 결과서
- AI 검색 및 활용 검증 결과서

## 8. 주요 위험 및 대응 방안 (Risks)

| 위험 | 영향 | 대응 |
|---|---|---|
| PostgreSQL 설치 환경 차이 | 설치 및 운영 절차 오류 | 설치 환경과 버전 표준화 |
| PostgreSQL 초기 설정 오류 | 서비스 기동 및 접속 장애 | 설정값 검증 및 설치 가이드 작성 |
| 계정 및 권한 설정 오류 | 접속 실패 또는 권한 과다 | 계정 역할 분리 및 최소 권한 검증 |
| 네트워크 접근 제어 오류 | Backend 연결 실패 또는 불필요한 외부 노출 | pg_hba.conf 및 방화벽 검증 |
| MariaDB 종속 SQL 발견 | 기능 오류 | 사전 SQL 조사 및 PostgreSQL 호환성 검증 |
| JPA Mapping 차이 | 저장 및 조회 오류 | Entity 및 Repository 테스트 |
| PK 생성 방식 차이 | 신규 데이터 생성 오류 | Identity 및 Sequence 정책 검증 |
| 날짜·시간 처리 차이 | 시간 정보 해석 오류 | Timezone 및 타입 검증 |
| 문자열 Collation 차이 | 검색 및 Unique 제약 동작 차이 | 대소문자 및 문자열 비교 정책 정의 |
| Schema 변환 오류 | 테이블 및 관계 구성 오류 | DDL 검증 및 Schema 재생성 테스트 |
| 트랜잭션 및 Lock 차이 | 동시성 오류 | 주요 업무 시나리오 검증 |
| PostgreSQL 운영 경험 부족 | 운영 장애 | 백업·복구·모니터링 절차 마련 |
| Vector Index 불일치 | 검색 결과 오류 | 원본 데이터와 Embedding 동기화 |
| 프로젝트 접근 권한 미흡 | 정보 노출 | 검색 전후 권한 검증 및 프로젝트 접근 모델 구현 |

## 9. 전환 완료 기준 (Acceptance Criteria)

다음 조건을 충족해야 PostgreSQL 전환을 완료한 것으로 판단한다.

1. PostgreSQL이 대상 개발 환경에 정상 설치된다.
2. PostgreSQL 서비스가 정상적으로 기동되며 자동 시작 설정이 검증된다.
3. PostgreSQL 설치 및 환경 구성 절차가 문서화된다.
4. PMIS 전용 Database 및 Schema가 정상적으로 생성된다.
5. 관리자 계정과 애플리케이션 계정이 분리된다.
6. PMIS 애플리케이션 계정에 최소 권한이 적용된다.
7. Backend에서 PostgreSQL로의 접속이 정상 동작한다.
8. PMIS의 신규 PostgreSQL Schema가 정상적으로 생성된다.
9. PK, FK, Unique Constraint 및 Index가 의도한 구조로 적용된다.
10. 주요 Entity 및 Repository 기능이 정상 동작한다.
11. 초기 데이터 및 테스트 데이터 생성이 정상적으로 수행된다.
12. 주요 업무 API 회귀 테스트를 통과한다.
13. Evidence 및 Evidence Version 기능이 정상 동작한다.
14. 트랜잭션 및 동시성 관련 주요 시나리오가 검증된다.
15. DB 백업 및 복구 테스트를 완료한다.
16. PostgreSQL을 기준으로 개발 환경이 정상 동작한다.
17. 기존 MariaDB 개발 DB를 참조용으로 유지할지 또는 폐기할지 결정한다.
18. MasterRuntime 및 Logistics와 DB 직접 공유가 발생하지 않는다.

기존 MariaDB 데이터의 이관 및 데이터 건수 비교는 전환 완료 기준에 포함하지 않는다.

## 10. 후속 결정 사항 (Follow-up Decisions)

이번 ADR에서는 PostgreSQL 전환 방향과 기반 환경 구축 범위를 결정하며, 아래 사항은 별도 설계 및 검증 과정에서 확정한다.

### 10.1 설치 및 실행 환경

- PostgreSQL 설치 대상 환경
- Windows, Linux 또는 Container 기반 설치 방식
- PostgreSQL 정확한 버전
- 설치 경로 및 데이터 디렉터리
- 서비스 실행 계정 및 자동 시작 정책
- 개발 및 운영 환경의 구성 차이

### 10.2 Database 및 계정 정책

- PostgreSQL 인스턴스 및 Schema 운영 정책
- Database 및 Schema 명칭
- 관리자 계정 운영 정책
- Schema Owner 및 Migration 실행 계정
- PMIS Application 계정 및 권한 범위
- Read Only 계정 필요 여부
- 비밀번호 및 Secret 관리 방식

### 10.3 네트워크 및 보안 정책

- PostgreSQL Listening Address
- PostgreSQL Port
- pg_hba.conf 접근 제어 정책
- Backend 접근 허용 IP 및 네트워크 범위
- 방화벽 정책
- TLS 적용 여부 및 인증서 관리 방식

### 10.4 애플리케이션 및 DB 운영 정책

- Flyway 또는 Liquibase 선정
- PK 생성 전략
- DB Connection Pool 정책
- 백업 및 복구 정책
- 로그 및 모니터링 정책
- 문자열 Collation 및 대소문자 비교 정책
- PostgreSQL 운영 점검 및 장애 대응 절차

### 10.5 경험 검색 및 AI 정책

- pgvector 버전 및 Index 전략
- Embedding 모델 및 차원
- Vector Search 검색 품질 기준
- Neo4j 도입 여부
- Spring AI 모델 및 Provider 구성
- PMIS 프로젝트 단위 접근 권한 모델
- Runtime File Storage의 개발용 파일 정리 정책

## 11. 최종 결정 요약

PMIS는 MasterRuntime 및 Logistics와 독립적으로 운영되는 업무 플랫폼으로 발전시킨다.

이를 위해 PMIS의 업무 데이터 저장소를 기존 MariaDB에서 PostgreSQL로 전환한다.

PostgreSQL은 PMIS의 System of Record로 사용하고, pgvector는 Context 및 Knowledge의 의미 기반 검색을 위한 확장 기능으로 활용한다.

현재 PostgreSQL이 설치되어 있지 않으므로 설치, 초기 설정, 서비스 구성, 관리자 및 애플리케이션 계정 분리, 권한 및 네트워크 보안 설정을 전환 범위에 포함한다.

기존 MariaDB의 개발 및 테스트 데이터는 이관하지 않는다. PostgreSQL에 신규 Schema를 구성하고 필요한 초기 데이터 및 테스트 데이터는 새로 생성한다.

Neo4j는 관계 탐색의 실제 효용성을 검증한 후 선택적으로 도입한다.

Spring AI는 PMIS 내부에서 검색 및 AI 활용을 지원하되, 업무 데이터와 승인된 Knowledge의 원본 소유권은 PMIS에 유지한다.

이번 결정의 핵심은 특정 DBMS나 AI 기술의 도입이 아니라 **PMIS의 데이터 소유권, 운영 독립성 및 향후 경험 재사용 구조를 확립하는 것**이다.