# PMIS 개요

## 개요

PMIS(Project Management Information System)는
IT 프로젝트 수행 과정에서 발생하는 일정, 작업, 이슈, 변경,
구성 정보 및 산출물을 통합 관리하기 위한 프로젝트 관리 시스템이다.

기존 문서 기반 PMO 관리 방식에서 발생하는
데이터 분산, 현황 파악 어려움, 반복적인 보고서 작성 문제를 개선하고,
프로젝트 정보를 체계적으로 관리하는 것을 목적으로 한다.

---

## 목적

- 프로젝트 관리 데이터 통합
- PMO 업무 표준화
- 프로젝트 현황 실시간 관리
- 보고서 생성 자동화
- 데이터 기반 의사결정 지원

---

## PMIS 개발 방향

PMIS는 별도 시스템으로 개발한다.

```text
PMIS

Frontend
    |
    |
Backend
(Spring Boot)
    |
    |
Database
(MariaDB)