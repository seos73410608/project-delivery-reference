# 아키텍처 개요 (Architecture Overview)

- 상태: Proposed
- 버전: 1.0
- 작성일: 2026-11-09
- Feature: Evidence Context Intelligence

## 1. 문서 목적

본 문서는 Evidence Context Intelligence Feature의 전체 아키텍처와 컴포넌트별 책임, 데이터 흐름 및 보안 구조를 정의한다.

기존 PMIS의 Evidence, Issue, Project 도메인을 기반으로 업무 수행 과정에서 발생하는 Business Event를 기록하고, 관련 업무 데이터와 Event를 Context로 연결한다.

검토 및 승인된 Context는 Knowledge로 관리하며, 향후 Neo4j와 Spring AI를 활용하여 프로젝트 간 경험을 검색하고 재사용할 수 있도록 확장한다.

본 문서는 전체 아키텍처 방향과 컴포넌트 간 책임을 설명한다. 세부 Entity, API, 데이터베이스 스키마 및 구현 방법은 후속 설계 문서에서 구체화한다.

## 2. 아키텍처 적용 범위

### 2.1 현재 구현된 구조

현재 PMIS에서 확인된 기술 및 도메인 구조는 다음과 같다.

| 구성 요소 | 현재 구현 |
|---|---|
| Frontend | React |
| Backend | Spring Boot 3.5.5 |
| 인증 | Spring Security + JWT |
| 권한 | Role 기반 접근 통제 |
| ORM | Spring Data JPA |
| RDB | MariaDB |
| 증적 관리 | Evidence / EvidenceVersion |
| 이슈 관리 | Issue |
| 프로젝트 관리 | Project |
| 파일 연계 | Automation Runtime Integration |

현재 `build.gradle.kts`에서 Spring AI 의존성 선언은 주석 처리되어 있으며, Neo4j 의존성은 확인되지 않았다.

따라서 Spring AI와 Neo4j는 현재 구현된 기능이 아니라 이번 Feature에서 단계적으로 추가할 대상으로 구분한다.

### 2.2 이번 Feature의 구현 범위

이번 Feature는 다음 6단계로 진행한다.

1. Evidence Version 복원
2. Business Event
3. Context / Knowledge
4. Neo4j Context Graph
5. Spring AI Graph Search
6. 통합 테스트 및 포트폴리오 문서화

각 단계는 독립적으로 구현하고 검증할 수 있어야 한다. 기존 PMIS 기능을 불필요하게 재작성하지 않고 현재 도메인 구조를 확장한다.

### 2.3 향후 확장 방향

PMIS를 ECP의 첫 번째 업무 도메인으로 확장할 수 있도록 다음 원칙을 적용한다.

- 업무 도메인의 원본 데이터는 MariaDB에서 관리한다.
- Business Event는 업무 행위의 사실을 기록한다.
- Context는 관련 업무 사실과 근거를 연결한다.
- Knowledge는 검토 및 승인된 경험을 관리한다.
- Neo4j는 업무 데이터 간 관계 탐색을 담당한다.
- Spring AI는 검색, 분석 및 자연어 응답을 지원한다.

## 3. 전체 논리 아키텍처

```text
+------------------------------------------------------+
|                    React Frontend                    |
|                                                      |
|  Evidence | Issue | Project | Context | Knowledge    |
|                         AI Search                    |
+---------------------------+--------------------------+
                            |
                            v
+------------------------------------------------------+
|                  Spring Boot Backend                 |
|                                                      |
|  기존 업무 도메인                                      |
|  +-----------+  +--------+  +---------+               |
|  | Evidence  |  | Issue  |  | Project |               |
|  +-----------+  +--------+  +---------+               |
|                                                      |
|  신규 Feature 도메인                                   |
|  +----------------+  +----------------+               |
|  | Business Event |  | Context        |               |
|  +----------------+  +----------------+               |
|  | Knowledge      |  | Context Builder|               |
|  +----------------+  +----------------+               |
|                                                      |
|  AI 검색 영역                                         |
|  +-----------------------------------------------+   |
|  | Spring AI                                      |   |
|  | Query Analysis / Retrieval / Response          |   |
|  +-----------------------------------------------+   |
+----------------------+-------------------------------+
                       |
          +------------+-------------+
          |                          |
          v                          v
+----------------------+  +---------------------------+
|       MariaDB        |  |           Neo4j           |
|                      |  |                           |
| Project              |  | Project Relationships    |
| Evidence             |  | Evidence Relationships   |
| EvidenceVersion      |  | Business Event Graph     |
| Issue                |  | Context Graph            |
| BusinessEvent        |  | Knowledge Relationships  |
| Context              |  |                           |
| Knowledge            |  | 재구성 가능한 관계 Projection |
+----------------------+  +---------------------------+
```

위 그림은 목표 아키텍처를 나타낸다. Neo4j와 Spring AI는 아직 현재 애플리케이션에 구현되어 있지 않다.

## 4. 구성 요소별 책임

### 4.1 React Frontend

주요 책임:

- 기존 PMIS 업무 기능 제공
- Evidence Version 관리
- Business Event 이력 조회
- Context 생성 및 검토
- Knowledge 승인 및 관리
- 자연어 기반 경험 검색

Frontend는 Backend API를 통해 업무를 처리하며 MariaDB나 Neo4j에 직접 접근하지 않는다.

### 4.2 Spring Boot Backend

Spring Boot는 전체 애플리케이션의 중심 계층이다.

주요 책임:

- 업무 도메인 규칙 처리
- 사용자 인증 및 권한 검증
- 트랜잭션 관리
- Business Event 생성
- Context 및 Knowledge 생명주기 관리
- 원본 업무 데이터 조회
- Graph 및 AI 연계

모든 업무 데이터 변경은 권한이 검증된 도메인 서비스를 통해 수행한다.

### 4.3 MariaDB

MariaDB는 업무 데이터의 원본 저장소(System of Record)다.

주요 관리 대상:

- Project
- Evidence
- EvidenceVersion
- Issue 및 기타 업무 데이터
- Business Event
- Context
- Knowledge
- 승인 및 생명주기 상태
- 사용자 및 권한 관련 데이터

업무 데이터의 변경은 Spring Boot 도메인 서비스와 MariaDB 트랜잭션을 통해 관리한다.

Neo4j 또는 AI 검색 결과가 원본 업무 데이터의 신뢰성을 대체해서는 안 된다.

### 4.4 Business Event

Business Event는 의미 있는 업무 행위를 변경 불가능한 과거의 사실로 기록한다.

이벤트 예시:

- `EVIDENCE_VERSION_CREATED`
- `EVIDENCE_VERSION_DELETED`
- `EVIDENCE_VERSION_RESTORED`
- `EVIDENCE_VERIFIED`
- `ISSUE_CREATED`
- `ISSUE_STATUS_CHANGED`
- `KNOWLEDGE_APPROVED`

개념적 속성:

| 속성 | 설명 |
|---|---|
| eventType | 이벤트 유형 |
| aggregateType | 업무 객체 유형 |
| aggregateId | 업무 객체 식별자 |
| projectId | 관련 프로젝트 식별자 |
| actorId | 행위를 수행한 사용자 식별자 |
| occurredAt | 실제 업무 행위 발생 시각 |
| reason | 행위 사유 |
| sourceReference | 원본 업무 데이터 참조 |

실제 데이터 구조는 후속 도메인 설계 단계에서 확정한다.

Business Event는 기존 업무 테이블을 대체하거나 전체 시스템을 Event Sourcing 방식으로 전환하기 위한 것이 아니다.

중요한 업무 변경과 해당 Event는 우선 동일한 MariaDB 트랜잭션에서 저장하는 방향으로 설계한다.

행위자 정보는 클라이언트 요청값이 아니라 인증된 `UserPrincipal`을 기준으로 기록한다.

기존 `BaseEntity.createdAt`은 레코드 생성 시각이므로 Business Event의 실제 발생 시각인 `occurredAt`과 구분한다.

### 4.5 Context Builder

Context Builder는 서로 관련된 업무 사실과 원본 데이터를 연결한다.

주요 책임:

- 관련 Business Event 수집
- 원본 도메인 데이터 조회
- Evidence, Issue, Decision, Action 참조 연결
- 원본 출처 및 근거 보존
- 업무 배경, 판단, 사유 및 결과 구성
- 검토를 위한 Context 후보 생성

Context Builder가 독립적으로 권위 있는 업무 사실을 만들어서는 안 된다.

AI는 정보의 구조화와 요약을 지원할 수 있지만, 생성된 내용에는 원본 출처와 검토 상태가 유지되어야 한다.

### 4.6 Context

Context는 연결된 업무 경험을 표현한다.

Context에 포함될 수 있는 정보:

- 프로젝트 배경
- 관련 Business Event
- Evidence 및 Evidence Version
- 이슈와 해결 과정
- 의사결정 및 실행 조치
- 검증 결과
- 원본 데이터 참조
- 적용 조건 및 한계

Context는 단순한 AI 요약이나 문서의 벡터 변환 결과가 아니다.

어떤 상황에서 왜 특정 조치를 수행했으며, 그 결과가 어떠했는지 이해할 수 있도록 업무 사실과 관계를 보존하는 것이 목적이다.

### 4.7 Knowledge

Knowledge는 검토 및 승인된 조직의 경험을 의미한다.

제안하는 생명주기:

`DRAFT → REVIEW → APPROVED → DEPRECATED`

Knowledge는 다음 정보를 보존해야 한다.

- 원본 Context
- 관련 Project
- 근거가 되는 Business Event
- 원본 Evidence 및 기타 업무 데이터
- 작성자 및 검토자
- 승인 정보
- 적용 조건
- 제한 사항 및 예외 조건

AI가 생성한 결과가 자동으로 승인된 Knowledge가 되어서는 안 된다.

승인된 Knowledge만 조직의 재사용 대상으로 취급하며, 실제 조회 시에는 접근 권한을 적용한다.

### 4.8 Neo4j Context Graph

Neo4j는 업무 데이터 간 관계 탐색을 담당한다.

Project, Evidence, Issue, Business Event, Context, Knowledge의 원본 저장소가 아니다.

노드 후보:

- Project
- Evidence
- EvidenceVersion
- Issue
- BusinessEvent
- Context
- Knowledge

관계 후보:

- `HAS_EVIDENCE`
- `HAS_ISSUE`
- `GENERATED_EVENT`
- `RELATED_TO`
- `SUPPORTED_BY`
- `DERIVED_FROM`
- `APPROVED_AS`
- `REUSED_IN`

Neo4j에는 관계 탐색에 필요한 식별자, 관계 정보 및 최소한의 속성을 저장하는 것을 기본 원칙으로 한다.

원본 업무 데이터는 MariaDB에서 조회한다.

Graph 데이터는 원본 데이터로부터 재구성할 수 있어야 한다.

Graph 탐색의 실제 효용은 기존 MariaDB JOIN 방식과 비교하여 검증한다. Graph DB를 도입했다는 이유만으로 불필요하게 복잡한 관계 모델을 만들지 않는다.

### 4.9 Spring AI

Spring AI는 애플리케이션의 AI 연계 프레임워크 역할을 담당한다.

주요 책임:

- 자연어 질의 처리
- 질의 및 의도 분석
- 검색 흐름 구성
- Context 구성 지원
- LLM 연계
- 검색 결과 기반 응답 생성
- 출처 정보 제공

Spring AI는 PMIS 업무 데이터의 원본 저장소가 아니다.

승인된 Knowledge를 독립적으로 결정하거나, 권한 검증 없이 업무 데이터를 변경하지 않는다.

이번 Feature에서 Spring AI는 별도의 AI Runtime 플랫폼을 의미하지 않는다.

## 5. 업무 데이터 흐름

### 5.1 Business Event 생성

Evidence Version 삭제 및 복원을 예로 든다.

```text
사용자 업무 요청
       |
       v
EvidenceService
       |
       +---- 인증 및 권한 검증
       |
       +---- EvidenceVersion 변경
       |
       +---- BusinessEvent 저장
       |
       v
MariaDB Transaction Commit
```

업무 변경과 Business Event 저장은 함께 성공하거나 함께 실패해야 한다.

Evidence Version 복원 시 기존 버전의 식별자, 버전 번호, Runtime 파일 참조 및 검증 이력을 보존한다.

복원은 새로운 업무 행위이므로 별도의 Business Event를 생성한다.

### 5.2 Context 구성

```text
Business Events
       |
       v
관련 원본 데이터 조회
       |
       +---- Project
       +---- Evidence / EvidenceVersion
       +---- Issue
       +---- Decision / Action
       |
       v
Context Builder
       |
       v
Context Candidate
       |
       v
담당자 검토
       |
       v
Approved Context / Knowledge
```

Context는 원본 업무 데이터와의 추적 관계를 보존해야 한다.

모든 Business Event가 Context가 되는 것은 아니며, 모든 Context가 Knowledge로 승인되는 것도 아니다.

### 5.3 Graph Projection

```text
MariaDB 원본 데이터
        |
        v
Graph Projection / Synchronization
        |
        v
Neo4j Context Graph
```

MariaDB가 원본 데이터의 기준이다.

Neo4j는 파생된 관계 Projection이며, Graph 동기화 실패가 원본 업무 데이터의 상태를 변경해서는 안 된다.

구체적인 동기화 및 복구 방식은 구현 단계에서 결정한다.

비동기 동기화를 도입하고 신뢰성 있는 이벤트 전달이 필요해지면 Outbox Pattern을 검토할 수 있다.

## 6. AI 검색 및 경험 재사용 흐름

목표 검색 흐름은 다음과 같다.

```text
자연어 질문
     |
     v
Spring AI 질의 분석
     |
     v
사용자 인증
     |
     v
프로젝트 / 데이터 접근 범위 검증
     |
     v
Neo4j 관계 탐색
     |
     v
MariaDB 원본 데이터 조회
     |
     v
원본 데이터별 권한 재검증
     |
     v
Context 구성
     |
     v
Spring AI 응답 생성
     |
     v
응답 + 출처 정보
```

그래프 탐색 전에 사용자의 접근 범위를 검증해야 한다.

권한 제한 없이 그래프를 먼저 검색한 뒤 결과만 필터링하는 방식에 의존해서는 안 된다.

MariaDB에서 원본 데이터를 조회할 때도 해당 데이터에 대한 접근 권한을 다시 확인해야 한다.

AI 응답은 근거가 되는 Knowledge와 원본 데이터를 식별할 수 있어야 하며, 사용자가 응답의 근거를 검증할 수 있어야 한다.

AI 검색은 과거 경험을 발견하도록 지원하는 기능이지, 사용자를 대신하여 권위 있는 업무 결정을 내리는 기능이 아니다.

## 7. 보안 및 권한 관리

### 7.1 현재 보안 구조

현재 PMIS에서 확인된 보안 기능:

- JWT 기반 인증
- `UserPrincipal`을 통한 사용자 식별
- USER / PM / ADMIN 역할 기반 API 접근 통제
- Task 담당자 기반 관리 권한
- PM 및 ADMIN의 주요 업무 관리 권한

현재 확인한 Project API는 역할 기반 접근 통제를 사용한다.

`ProjectService`의 프로젝트 검색과 `ProjectDetailService`의 상세 조회에서는 프로젝트 참여자 기준 권한 검증이 확인되지 않았다.

따라서 프로젝트별 접근 격리 기능은 현재 구현된 것으로 간주하지 않는다.

### 7.2 목표 보안 요건

프로젝트 간 Context 및 Knowledge 검색을 제공하기 전에 다음 항목을 정의해야 한다.

- 프로젝트 참여 및 접근 범위
- 역할별 접근 권한
- Context 및 Knowledge 조회 권한
- 원본 업무 데이터 접근 권한
- 프로젝트 간 경험 재사용 권한
- AI 검색 시점의 권한 검증

Graph 검색 계층에는 권한이 확인된 검색 범위만 전달해야 한다.

MariaDB 원본 데이터 조회 시에도 데이터별 접근 권한을 재검증해야 한다.

이는 현재 구현된 기능이 아니라 향후 설계 및 구현이 필요한 항목이다.

## 8. 트랜잭션 및 데이터 정합성

### 8.1 MariaDB 트랜잭션

업무 변경과 해당 Business Event는 우선 동일한 MariaDB 트랜잭션에서 저장한다.

이를 통해 업무 처리 결과와 업무 이력의 정합성을 유지한다.

### 8.2 Neo4j 동기화

Neo4j는 파생 Projection이므로 별도로 동기화할 수 있다.

구현 시 다음 사항을 고려한다.

- 동기화 실패
- 재시도 및 복구
- 중복 처리
- 이벤트 처리 순서
- Graph 재구성
- 원본 데이터 삭제 또는 폐기 상태

초기 구현에서는 실제 필요성이 확인되지 않은 분산 트랜잭션 복잡성을 도입하지 않는다.

### 8.3 원본 추적성

모든 Context와 Knowledge는 원본 업무 데이터의 참조 정보를 유지해야 한다.

Graph 관계나 AI 생성 문장만으로 권위 있는 업무 사실을 증명할 수는 없다.

## 9. 구현 단계

| 단계 | 구현 범위 | 기대 결과 |
|---|---|---|
| 1 | Evidence Version Restore | 삭제된 버전 복원 및 생명주기 검증 |
| 2 | Business Event | 업무 행위 이력 기록 |
| 3 | Context / Knowledge | Context 구성 및 승인 생명주기 |
| 4 | Neo4j Context Graph | 관계 Projection 및 탐색 |
| 5 | Spring AI Graph Search | 자연어 기반 경험 검색 |
| 6 | 통합 테스트 / 문서화 | End-to-End 검증 및 포트폴리오 결과물 |

각 단계는 독립적인 구현 및 테스트 기준을 가져야 한다.

## 10. 아키텍처 설계 제약

이번 Feature에는 다음 제약을 적용한다.

1. MariaDB는 업무 데이터의 System of Record로 유지한다.
2. 기존 PMIS 도메인 로직을 Graph 로직으로 대체하지 않는다.
3. Neo4j는 관계 Projection을 저장하며 원본 업무 데이터의 권한을 갖지 않는다.
4. Spring AI는 검색과 분석을 지원하며 업무 데이터의 소유자가 아니다.
5. AI 생성 결과를 자동으로 승인된 Knowledge로 전환하지 않는다.
6. 검토 및 승인된 Knowledge만 조직의 재사용 대상으로 취급한다.
7. 모든 검색은 권한과 원본 출처 추적성을 보장해야 한다.
8. 전체 시스템의 Event Sourcing 전환은 범위에서 제외한다.
9. 모든 문서를 일괄적으로 벡터화하는 작업은 범위에서 제외한다.
10. AI Runtime 및 물류 AI 연계는 범위에서 제외한다.

## 11. 완료 기준

다음 End-to-End 흐름을 검증하면 본 Feature의 아키텍처가 실제로 동작한다고 판단한다.

1. Evidence Version을 생성한다.
2. 삭제 사유를 기록하고 해당 버전을 삭제한다.
3. 삭제 Business Event가 기록된다.
4. 권한이 있는 사용자가 삭제된 버전을 복원한다.
5. 복원 Business Event가 기록된다.
6. 기존 파일 참조 및 검증 이력이 유지된다.
7. 관련 Event와 원본 업무 데이터가 Context로 연결된다.
8. 검토자가 Context를 Knowledge로 승인한다.
9. 권한이 있는 사용자가 자연어로 과거 경험을 검색한다.
10. 승인된 Knowledge와 관련 원본 데이터가 검색된다.
11. AI 응답에 출처 추적성과 접근 권한이 반영된다.

본 Feature는 기술 간 연동뿐 아니라 업무 경험의 생성, 검증, 승인 및 재사용 과정 전체의 신뢰성을 입증해야 한다.

---

**문서 끝**