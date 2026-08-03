# CI 관리 모델

## 개요

CI(Configuration Item) 관리 모델은 CMDB에서 관리하는 구성 항목의
유형과 속성, 관리 기준을 정의하는 체계이다.

프로젝트 구축 및 운영 과정에서 생성되는 IT 구성 요소를 표준화된
형태로 관리하여 구성 정보의 일관성과 추적성을 확보한다.

---

## 목적

- 관리 대상 구성 요소 표준화
- CI 유형별 관리 속성 정의
- 구성 정보 추적성 확보
- 구성 요소 간 관계 관리 기반 마련
- 변경 영향 분석 지원

---

## CI 관리 구조

CMDB에서는 IT 구성 요소를 유형별 CI로 구분하여 관리한다.

```text
CI

├── Hardware
│   ├── Server
│   ├── Storage
│   └── Network
│
├── Platform
│   ├── Operating System
│   ├── Database
│   └── Middleware
│
├── Software
│   ├── Application
│   └── Utility
│
└── Service
    └── Business Service