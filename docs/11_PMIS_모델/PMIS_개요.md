```markdown
# PMIS 개요

## 개요

PMIS(Project Management Information System)는
IT 프로젝트 수행 과정에서 발생하는 프로젝트 관리 데이터를 통합하고,
일정, 작업, 이슈, 변경, 구성 정보 및 산출물을 체계적으로 관리하기 위한
웹 기반 프로젝트 관리 시스템이다.

기존 문서 기반 PMO 관리 방식에서 발생하는
데이터 분산, 현황 파악 어려움, 반복적인 보고서 작성 문제를 개선하고,
프로젝트 수행 정보를 표준화된 구조로 관리하는 것을 목적으로 한다.

---

# 목적

PMIS 구축 목적은 프로젝트 관리 업무를 시스템화하여
프로젝트 진행 현황과 주요 관리 정보를 효과적으로 관리하는 것이다.

주요 목적:

- 프로젝트 관리 데이터 통합
- PMO 업무 프로세스 표준화
- 프로젝트 현황 실시간 관리
- 이슈 및 위험 요소 체계적 관리
- 변경 사항 추적 관리
- 보고서 작성 및 관리 효율화
- 데이터 기반 의사결정 지원

---

# PMIS 개발 방향

PMIS는 프로젝트 관리 업무를 지원하는 독립적인 웹 시스템으로 개발한다.

~~~text
PMIS

Frontend
(Web Dashboard)

        |

Backend
(Spring Boot)

        |

Database
(MariaDB)
~~~

시스템 구성:

| 구성 요소 | 역할 |
|---|---|
| Frontend | 프로젝트 관리 화면 및 Dashboard 제공 |
| Backend | 프로젝트 관리 업무 로직 처리 |
| Database | 프로젝트 관리 데이터 저장 및 관리 |

---

# 관리 대상 영역

PMIS는 프로젝트 수행 과정에서 발생하는 주요 관리 영역을 통합 관리한다.

~~~text
PMIS

├── Project Management
│
├── WBS Management
│
├── Schedule Management
│
├── Issue Management
│
├── Risk Management
│
├── Change Management
│
├── CMDB Management
│
├── Report Management
│
└── User / Permission Management
~~~

---

# 주요 기능

## 프로젝트 관리

프로젝트 기본 정보와 참여 인력을 관리한다.

관리 항목:

- 프로젝트 정보
- 수행 기간
- 참여 조직
- 담당자
- 프로젝트 상태

---

## WBS 및 일정 관리

프로젝트 작업 구조와 진행 현황을 관리한다.

관리 항목:

- Phase
- Task
- 일정
- 담당자
- 진척률
- 마일스톤

---

## Issue / Risk 관리

프로젝트 수행 과정에서 발생하는 이슈와 위험 요소를 관리한다.

관리 항목:

- Issue 등록
- Risk 관리
- 영향도 분석
- 대응 계획
- 조치 현황

---

## Change Management

프로젝트 변경 사항을 체계적으로 관리한다.

관리 항목:

- 변경 요청
- 변경 영향 분석
- 승인 상태
- 변경 이력

---

## CMDB 관리

프로젝트 구축 대상 시스템 구성 정보를 관리한다.

관리 항목:

- 서버
- 운영체제
- 데이터베이스
- 미들웨어
- 시스템 관계 정보

---

## Report Management

프로젝트 관리 데이터를 기반으로 보고서를 관리한다.

생성 대상:

- 주간 보고서
- 월간 보고서
- 이슈 보고서
- 검수 보고서
- 종료 보고서

---

# 시스템 구성

PMIS 전체 구성은 다음과 같다.

~~~text
User

 |

Web Browser

 |

Frontend

 |

REST API

 |

Spring Boot

 |

MariaDB

 |

File Storage
~~~

---

# 데이터 관리 구조

PMIS 데이터는 도메인 기반으로 관리한다.

~~~text
MariaDB

├── Project Domain
│
├── WBS Domain
│
├── Issue Domain
│
├── CMDB Domain
│
├── User Domain
│
└── Report Domain
~~~

---

# 사용자 및 권한 관리

PMIS는 사용자 역할에 따른 접근 권한을 관리한다.

주요 역할:

- PMO 관리자
- 프로젝트 관리자
- 담당자
- 조회 사용자

관리 기능:

- 사용자 관리
- 역할 관리
- 메뉴 권한 관리
- 데이터 접근 제어

---

# 기대 효과

PMIS 구축을 통해 다음 효과를 기대한다.

- 프로젝트 관리 업무 표준화
- PMO 운영 효율 향상
- 프로젝트 현황 가시성 확보
- 이슈 및 변경 추적성 확보
- 산출물 관리 체계화
- 보고 업무 효율 향상
- 프로젝트 품질 향상

---

# 향후 확장 방향

PMIS는 프로젝트 관리 데이터를 기반으로
향후 다양한 분석 및 자동화 기능으로 확장 가능하다.

확장 방향:

- 프로젝트 지표 분석
- 일정 지연 분석
- 위험 예측
- 통합 Dashboard 강화
- AI 기반 프로젝트 관리 지원

---

# PMIS 전체 구조

~~~text
PMIS

├── Project Management
├── WBS Management
├── Schedule Management
├── Issue / Risk Management
├── Change Management
├── CMDB Management
├── Dashboard
├── Report Management
└── Security Management
~~~
```