# CMDB 개요

## 개요

CMDB(Configuration Management Database)는 프로젝트 및 운영 환경에서
관리되는 구성 항목(Configuration Item, CI)의 정보를 체계적으로 저장하고
관리하기 위한 데이터 관리 체계이다.

서버, 운영체제, 데이터베이스, Middleware, Software 등 IT 구성 요소와
구성 요소 간 관계 정보를 관리하여 시스템 운영 및 변경 관리를 지원한다.

---

## 목적

- IT 구성 항목(CI) 체계적 관리
- 시스템 구성 정보 가시화
- 구성 요소 간 관계 관리
- 변경 영향 분석 지원
- 장애 및 운영 대응 효율 향상

---

## CMDB 관리 대상

| 구분 | 관리 대상 |
|---|---|
| Hardware | Server, Storage, Network |
| Operating System | Linux, Unix, Windows |
| Database | Oracle, PostgreSQL, Tibero, MariaDB |
| Middleware | JEUS, WebtoB, Tomcat |
| Software | Application, Utility, Package |
| Service | 업무 서비스 및 시스템 기능 |

---

## CMDB 구성 개념

```text
Configuration Item (CI)

        ↓

Server
        ↓
OS
        ↓
Middleware
        ↓
Application
        ↓
Database
        ↓
Service