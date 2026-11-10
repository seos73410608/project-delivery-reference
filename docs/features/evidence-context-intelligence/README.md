# Evidence Context Intelligence

## 1. Feature 개요

Evidence Version의 생성·검증·삭제·복원 이력을 출발점으로, 프로젝트 수행 과정에서 발생하는 업무 경험을 Business Event, Context, Knowledge로 연결하고 재사용할 수 있도록 확장한다.

본 Feature는 PMIS를 단순한 프로젝트 관리 시스템에서 **업무 경험을 축적하고 재사용할 수 있는 플랫폼**으로 발전시키기 위한 첫 번째 구현 단계다.

## 2. 핵심 목표

- Evidence Version의 삭제 및 복원 이력을 안전하게 관리한다.
- 주요 업무 행위를 Business Event로 기록한다.
- 여러 업무 이벤트와 원본 자료를 Context로 연결한다.
- 검토 및 승인된 경험을 Knowledge로 관리한다.
- Neo4j를 활용해 프로젝트 간 관계와 경험을 탐색한다.
- Spring AI를 통해 자연어 기반 경험 검색과 분석을 지원한다.

## 3. 아키텍처 원칙

| 구분 | 기술 및 원칙 | 책임 |
|---|---|---|
| Backend | Spring Boot | 업무 도메인과 비즈니스 규칙 |
| RDB | MariaDB | 업무 데이터의 원본 및 트랜잭션 관리 |
| Graph DB | Neo4j | 업무 객체와 경험 간 관계 탐색 |
| AI Framework | Spring AI | 검색, 분석, 추론 및 응답 생성 |
| Frontend | React | 업무 관리 및 AI 검색 인터페이스 |

핵심 원칙:

1. MariaDB를 업무 데이터의 System of Record로 유지한다.
2. Neo4j는 관계 탐색을 위한 저장소로 활용한다.
3. Spring AI는 검색과 분석을 지원하며 업무 데이터의 소유자가 아니다.
4. AI 생성 결과를 자동으로 Knowledge로 확정하지 않는다.
5. 검토 및 승인된 Knowledge만 조직의 재사용 가능한 경험으로 인정한다.

## 4. 업무 경험의 생명주기

`Work → Business Event → Context → Knowledge → Reuse`

- **Work**: 실제 프로젝트 업무 수행
- **Business Event**: 업무 과정에서 발생한 주요 행위와 사실 기록
- **Context**: 이벤트, 원본 자료, 판단 근거, 결과를 연결
- **Knowledge**: 검토 및 승인된 재사용 가능 경험
- **Reuse**: 다른 프로젝트에서 기존 경험을 검색하고 활용

## 5. 구현 단계

| 단계 | 구현 항목 | 상태 |
|---|---|---|
| 1 | Evidence Version Restore | 계획 |
| 2 | Business Event | 계획 |
| 3 | Context / Knowledge | 계획 |
| 4 | Neo4j Context Graph | 계획 |
| 5 | Spring AI Graph Search | 계획 |
| 6 | 통합 테스트 및 포트폴리오 문서화 | 계획 |

각 단계는 독립적으로 설계하고 테스트한 후 다음 단계로 진행한다.

## 6. 데이터 신뢰성 및 통제 원칙

- MariaDB의 원본 업무 데이터와 Neo4j의 관계 데이터 역할을 분리한다.
- 관계 데이터는 원본 업무 데이터를 대체하지 않는다.
- AI 검색 결과는 원본 데이터와 출처를 추적할 수 있어야 한다.
- Knowledge 승인 전에는 검증되지 않은 경험을 공식 지식으로 취급하지 않는다.
- 검색 시 사용자 권한과 프로젝트 접근 범위를 확인한다.
- 기존 Evidence Version의 검증 이력과 Runtime 파일 참조를 보존한다.

## 7. 범위 제외

이번 Feature에서는 다음 항목을 구현 범위에 포함하지 않는다.

- PMIS 전체 데이터의 Neo4j 이전
- 기존 시스템 전체를 Event Sourcing 구조로 전환
- 별도 AI Runtime 플랫폼 구축
- 물류 AI 프로젝트와의 통합
- AI의 무단 업무 데이터 변경
- AI 생성 결과의 자동 Knowledge 승인
- 모든 문서의 무조건적인 벡터화

## 8. 완료 기준

- 각 단계의 구현 및 테스트 결과가 문서화되어야 한다.
- MariaDB 원본 데이터와 관계 데이터의 정합성을 검증해야 한다.
- 업무 이벤트에서 Context 및 Knowledge로 이어지는 흐름을 검증해야 한다.
- 승인된 Knowledge를 다른 프로젝트에서 검색하고 출처를 확인할 수 있어야 한다.
- AI 검색 결과의 권한 통제와 근거 추적을 검증해야 한다.

## 9. 문서 관리

| 디렉터리 | 관리 대상 |
|---|---|
| `01-architecture` | 아키텍처 설계 및 기술 선택 근거 |
| `02-domain` | Business Event, Context, Knowledge 도메인 |
| `03-data` | MariaDB 및 Neo4j 데이터 모델 |
| `04-implementation` | 단계별 구현 기록 |
| `05-testing` | 테스트 전략 및 결과 |
| `06-portfolio` | 기술적 의사결정과 구현 성과 |

> 본 문서는 Feature의 전체 방향을 정의한다. 상세 설계와 구현 내용은 각 하위 문서에서 관리하며, 실제 구현 결과와 계획이 달라질 경우 문서를 갱신한다.