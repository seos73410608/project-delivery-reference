# CMDB 데이터 모델

## 개요

CMDB(Configuration Management Database) 데이터 모델은
프로젝트 및 운영 환경에서 관리되는 구성 항목(Configuration Item)을
체계적으로 관리하기 위한 PMIS 데이터 모델이다.

서버, 운영체제, 데이터베이스, 미들웨어, 소프트웨어 등의
구성 정보를 통합 관리하고 구성 요소 간 관계를 추적하는 것을 목적으로 한다.

---

## 목적

- IT 구성 정보 표준화
- 시스템 구성 현황 관리
- 구성 요소 간 관계 추적
- 변경 영향 분석 지원
- 운영 인수인계 기반 확보

---

## CMDB 관리 구조

```text
Configuration Item (CI)

├── Hardware
│
├── OS
│
├── Database
│
├── Middleware
│
├── Application
│
└── Software