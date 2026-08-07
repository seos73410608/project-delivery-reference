# Frontend UI Design Standard

## 1. 문서 정보

| 항목 | 내용 |
|------|------|
| 시스템 | PMIS (Project Management Information System) |
| 문서명 | Frontend UI Design Standard |
| 문서번호 | FA-004 |
| 작성일 | 2026-08-07 |
| 버전 | 1.0 |

---

# 2. 목적

본 문서는 PMIS Frontend의 사용자 인터페이스(UI) 표준을 정의한다.

모든 화면은 동일한 디자인 원칙과 레이아웃을 적용하여 일관된 사용자 경험(User Experience)을 제공한다.

---

# 3. UI 설계 원칙

PMIS는 업무 시스템(Business Application)이다.

화려한 디자인보다 다음을 우선한다.

- 정보 전달
- 사용성
- 일관성
- 가독성
- 유지보수성

---

# 4. 화면 구성

모든 화면은 동일한 Layout을 사용한다.

```
+--------------------------------------------------------------+
| Header                                                       |
+----------------------+---------------------------------------+
| Sidebar              | Breadcrumb                            |
|                      +---------------------------------------+
|                      |                                       |
|                      |              Content                  |
|                      |                                       |
|                      |                                       |
|                      |                                       |
+----------------------+---------------------------------------+
```

---

# 5. Header

Header는 시스템 전체에서 공통으로 사용한다.

구성 요소

- PMIS Logo
- 시스템명
- 현재 로그인 사용자
- 알림(Notification)
- 사용자 메뉴
- 로그아웃

권장 높이

```
64px
```

---

# 6. Sidebar

Sidebar는 모든 업무 메뉴를 제공한다.

기본 폭

```
240px
```

접기(Collapse) 기능을 제공한다.

메뉴 예시

```
Dashboard

Project

Task

Schedule

Issue

Risk

CMDB

Report

Admin
```

---

# 7. Breadcrumb

현재 위치를 표시한다.

예)

```
Dashboard

Project > Project List

Project > Project Detail

Issue > Issue Detail
```

Breadcrumb는 Header 아래에 위치한다.

---

# 8. Content 영역

Content는 업무 화면이 표시되는 영역이다.

모든 화면은 동일한 여백을 사용한다.

권장 Padding

```
24px
```

---

# 9. 화면 구성 패턴

목록 화면은 다음 구조를 따른다.

```
Page Title

↓

Search Area

↓

Action Button

↓

Table

↓

Pagination
```

예)

```
Project Management

----------------------------------

[검색조건]

[검색]

----------------------------------

[프로젝트 등록]

----------------------------------

Project Table

----------------------------------

Pagination
```

---

# 10. Detail 화면

상세 화면은 다음 구조를 따른다.

```
Title

↓

기본 정보

↓

업무 정보

↓

첨부파일

↓

이력

↓

버튼
```

---

# 11. Form 디자인

입력 항목은 Label을 반드시 포함한다.

예)

```
Project Name

[__________________]

Manager

[__________________]
```

Placeholder는 설명이 필요한 경우에만 사용한다.

---

# 12. Button

Primary Button

- 저장
- 등록
- 확인

Secondary Button

- 취소
- 닫기

Danger Button

- 삭제

버튼 위치

```
오른쪽 정렬
```

---

# 13. Table

모든 업무 목록은 Table을 사용한다.

기본 기능

- 정렬
- 페이징
- 검색
- 선택

권장 구성

```
번호

프로젝트명

담당자

상태

등록일
```

---

# 14. Dialog

Dialog는 다음 경우에 사용한다.

- 등록
- 수정
- 삭제 확인
- 상세 조회

삭제 시 확인 메시지를 반드시 표시한다.

---

# 15. Card

Dashboard는 Card 기반으로 구성한다.

예)

```
+----------------------+

Project

120

+----------------------+
```

---

# 16. Dashboard

Dashboard는 프로젝트 현황을 요약한다.

예)

- 프로젝트 수
- 진행률
- 일정 현황
- 이슈 현황
- 리스크 현황
- 검수 현황

---

# 17. 색상

Primary Color

```
Blue
```

Success

```
Green
```

Warning

```
Orange
```

Danger

```
Red
```

Background

```
Light Gray
```

---

# 18. 아이콘

Material UI Icon을 사용한다.

예)

```
Dashboard

Folder

Assignment

BugReport

CalendarMonth

Assessment

Settings
```

---

# 19. 반응형

Desktop 우선으로 개발한다.

지원 범위

- Desktop
- Notebook

Mobile은 향후 지원한다.

---

# 20. 권한 UI

권한에 따라 메뉴를 제어한다.

예)

USER

```
Dashboard

Project
```

PM

```
Dashboard

Project

Issue

Schedule
```

ADMIN

```
Dashboard

Project

Task

Issue

CMDB

Admin
```

Frontend는 메뉴만 제어하며,

실제 권한 검증은 Backend에서 수행한다.

---

# 21. Loading

API 호출 시 Loading Indicator를 표시한다.

사용자가 시스템 상태를 인지할 수 있도록 한다.

---

# 22. Error 화면

오류 발생 시 사용자 친화적인 메시지를 제공한다.

예)

```
데이터를 불러오는 중 오류가 발생했습니다.

[다시 시도]
```

---

# 23. Empty 화면

조회 결과가 없는 경우

```
검색 결과가 없습니다.
```

를 표시한다.

빈 Table은 표시하지 않는다.

---

# 24. 접근성

모든 버튼은 명확한 이름을 사용한다.

색상만으로 상태를 표현하지 않는다.

키보드 접근이 가능하도록 개발한다.

---

# 25. UI 표준

모든 화면은 다음 순서를 따른다.

```
Header

↓

Sidebar

↓

Breadcrumb

↓

Page Title

↓

Search Area

↓

Content

↓

Action Button
```

---

# 26. 결론

PMIS Frontend는 업무 중심(Business-Oriented) UI를 목표로 한다.

모든 화면은 동일한 Layout과 디자인 원칙을 적용하여 사용자가 기능을 쉽게 이해하고 사용할 수 있도록 설계한다.

UI는 단순한 디자인이 아닌 업무 효율성과 유지보수성을 고려한 표준으로 관리한다.