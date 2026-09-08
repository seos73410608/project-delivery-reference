# SCHEDULE Calendar Feature Design

> PMIS (Project Management Information System)  
>
> Version: **1.0**  
>
> Domain: **Schedule Calendar**  
>
> Status: **Design Confirmed / Implementation Ready**  
>
> Last Updated: **2026-09-08**

---

## 1. 문서 목적

본 문서는 PMIS의 **Schedule Calendar Feature에 대한 설계 및 구현 기준 문서**이다.

Schedule Calendar는 독립적인 업무 Domain이 아니라, 기존 **Schedule Domain에서 관리하는 일정 데이터를 Calendar 형태로 시각화하는 Frontend 중심 Feature**이다.

본 문서의 목적은 다음과 같다.

- Schedule과 Calendar의 책임 분리
- Calendar Feature의 역할 정의
- Calendar 데이터 모델 정의
- Schedule 데이터를 Calendar Event로 변환하는 기준 정의
- Calendar 기간 조회 기준 정의
- Backend API 요구사항 정의
- Frontend Component 구조 정의
- Month View 구현 기준 정의
- 향후 Week / Day / Gantt 확장 기반 확보

Frontend 개발자는 본 문서와 `SCHEDULE_DESIGN.md`, Swagger/OpenAPI를 함께 참고하여 Calendar 기능을 구현한다.

### 관련 문서

- `SCHEDULE_DESIGN.md`
- `DEVELOPMENT_ROADMAP.md`
- `CHANGELOG.md`
- `ARCHITECTURE.md`

### 관련 API 문서

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

# 2. Calendar 개요

Schedule Calendar는 프로젝트 일정을 시간 단위로 시각화하는 기능이다.

기존 Schedule Domain이 일정 데이터를 관리한다면 Calendar는 해당 데이터를 사용자에게 직관적으로 보여주는 역할을 담당한다.

```text
Schedule Domain

    │

    │ Schedule Data

    ▼

Calendar Feature

    │

    ├── Month View
    │
    ├── Week View
    │
    ├── Day View
    │
    └── Timeline View
```

Calendar는 새로운 업무 데이터를 생성하거나 별도의 일정 데이터를 관리하지 않는다.

기본 구조는 다음과 같다.

```text
Backend

Schedule

    │

    │ API Response

    ▼

Frontend

ScheduleResponse

    │

    │ Adapter

    ▼

CalendarEvent

    │

    ▼

Calendar UI
```

---

# 3. Calendar와 Schedule 관계

Schedule과 Calendar는 서로 다른 책임을 가진다.

```text
Schedule

→ 일정 데이터 관리


Calendar

→ 일정 데이터 시각화
```

책임 분리는 다음과 같다.

| 영역 | 책임 |
|---|---|
| Schedule Domain | 일정 생성 |
| Schedule Domain | 일정 수정 |
| Schedule Domain | 일정 삭제 |
| Schedule Domain | 일정 조회 |
| Schedule Domain | 날짜 Validation |
| Schedule Domain | 상태 관리 |
| Calendar Feature | 일정 시각화 |
| Calendar Feature | 월 이동 |
| Calendar Feature | Event Rendering |
| Calendar Feature | 날짜별 일정 표시 |
| Calendar Feature | 일정 선택 |

따라서 Calendar는 새로운 Domain Entity를 생성하지 않는다.

```text
Project

    │

    ▼

WBS

    │

    ▼

Schedule

    │

    ▼

Calendar Visualization
```

---

# 4. Calendar Feature 핵심 책임

Schedule Calendar는 다음 책임을 가진다.

- 일정 월별 표시
- Calendar Navigation
- 이전 월 조회
- 다음 월 조회
- 현재 월 조회
- 날짜별 일정 표시
- 여러 날짜에 걸친 일정 표시
- 일정 상태 표현
- 일정 선택
- 일정 상세 화면 연계
- 프로젝트별 일정 표시
- 일정 기간 기반 조회
- Loading 상태 처리
- Empty 상태 처리
- Error 상태 처리

Calendar는 다음 책임을 가지지 않는다.

```text
Schedule CRUD

Schedule Validation

Schedule Persistence

Schedule Entity 관리

Schedule Dependency 관리

Progress Calculation
```

해당 기능은 Schedule Domain 또는 향후 별도 Domain에서 관리한다.

---

# 5. Calendar Architecture

전체 구조는 다음과 같다.

```text
┌──────────────────────────────┐
│        Schedule Backend      │
│                              │
│  ScheduleController          │
│          │                   │
│          ▼                   │
│  ScheduleService             │
│          │                   │
│          ▼                   │
│  ScheduleRepository          │
└──────────────┬───────────────┘
               │
               │ Schedule API
               ▼
┌──────────────────────────────┐
│       Frontend API Layer     │
│                              │
│  scheduleApi.ts              │
└──────────────┬───────────────┘
               │
               │ ScheduleResponse
               ▼
┌──────────────────────────────┐
│       Calendar Adapter       │
│                              │
│  Schedule → CalendarEvent    │
└──────────────┬───────────────┘
               │
               │ CalendarEvent[]
               ▼
┌──────────────────────────────┐
│       Calendar Feature       │
│                              │
│  CalendarHeader              │
│                              │
│  CalendarGrid                │
│                              │
│  CalendarDay                 │
│                              │
│  CalendarEvent               │
└──────────────────────────────┘
```

---

# 6. Calendar Data Flow

Calendar 데이터 흐름은 다음과 같다.

```text
Project 선택

    │

    ▼

Calendar Month 선택

    │

    ▼

Date Range 계산

    │

    ▼

Schedule API 호출

    │

    ▼

ScheduleResponse[]

    │

    ▼

Calendar Event 변환

    │

    ▼

Calendar Rendering
```

예:

```text
Project ID

1


Calendar

2026년 9월


조회 기간

2026-09-01

~

2026-09-30


        ↓


GET /api/schedules

        ↓


ScheduleResponse[]


        ↓


CalendarEvent[]


        ↓


Month View Rendering
```

---

# 7. Calendar Event Model

Frontend에서는 Backend의 Schedule 모델을 Calendar Rendering에 직접 사용하지 않고 필요에 따라 Calendar Event 모델로 변환할 수 있다.

권장 모델:

```typescript
export interface CalendarEvent {

  id: number;

  title: string;

  startDate: string;

  endDate: string;

  status: ScheduleStatus;

  projectId: number;

  wbsId: number;

}
```

필요한 경우 다음 정보를 추가할 수 있다.

```typescript
export interface CalendarEvent {

  id: number;

  title: string;

  startDate: string;

  endDate: string;

  status: ScheduleStatus;

  projectId: number;

  wbsId: number;

  description?: string | null;

}
```

Calendar Event는 Frontend View Model이다.

Backend Entity와 동일할 필요는 없다.

---

# 8. Schedule → Calendar Event 변환

Schedule 데이터를 Calendar Event로 변환한다.

예:

```typescript
export const toCalendarEvent = (
  schedule: ScheduleResponse
): CalendarEvent => {

  return {

    id: schedule.id,

    title: schedule.scheduleName,

    startDate: schedule.startDate,

    endDate: schedule.endDate,

    status: schedule.status,

    projectId: schedule.projectId,

    wbsId: schedule.wbsId,

    description: schedule.description,

  };

};
```

변환 구조:

```text
ScheduleResponse

    │

    ├── id
    │
    ├── scheduleName
    │
    ├── startDate
    │
    ├── endDate
    │
    └── status

            │

            ▼

CalendarEvent

    │

    ├── id
    │
    ├── title
    │
    ├── startDate
    │
    ├── endDate
    │
    └── status
```

---

# 9. Calendar Date Range

Calendar는 현재 화면에 표시되는 기간을 기준으로 Schedule을 조회한다.

예:

```text
Calendar

2026년 9월


Start Date

2026-09-01


End Date

2026-09-30
```

Calendar Date Range:

```text
2026-09-01

~

2026-09-30
```

Frontend는 Calendar Navigation 시 Date Range를 계산한다.

```text
Previous Month

    │

    ▼

Date Range 변경

    │

    ▼

Schedule API 호출

    │

    ▼

Calendar 갱신
```

---

# 10. Calendar Overlap Query

Calendar 기간 조회에서 가장 중요한 기준은 **기간 겹침(Overlap)** 이다.

단순히 Schedule의 시작일이 Calendar 기간에 포함되는지 확인해서는 안 된다.

예:

```text
Calendar

2026-09-01
        │
        ├───────────────────────┤
        │                       │
        ▼                       ▼
2026-09-01                 2026-09-30
```

Schedule:

```text
2026-08-25

        ├───────────────┤

                        2026-09-05
```

이 일정은 Calendar에 표시되어야 한다.

따라서 기간 조건은 다음과 같다.

```text
Schedule Start Date <= Calendar End Date

AND

Schedule End Date >= Calendar Start Date
```

즉:

```text
schedule.startDate <= calendar.endDate

AND

schedule.endDate >= calendar.startDate
```

---

# 11. Overlap Query 예시

Calendar:

```text
2026-09-01

~

2026-09-30
```

다음 일정은 조회 대상이다.

### Case 1

```text
Schedule

2026-09-05

~

2026-09-10
```

포함.

```text
Calendar

01 ───────────────────────────── 30

        05 ─────── 10
```

---

### Case 2

```text
Schedule

2026-08-25

~

2026-09-05
```

포함.

```text
Calendar

01 ───────────────────────────── 30

─────── 08-25 ─── 09-05
```

---

### Case 3

```text
Schedule

2026-09-25

~

2026-10-05
```

포함.

```text
Calendar

01 ───────────────────────────── 30

                         25 ───────
```

---

### Case 4

```text
Schedule

2026-08-01

~

2026-08-20
```

제외.

---

# 12. Backend Calendar API 정책

Calendar는 독립적인 Backend Domain API를 반드시 필요로 하지 않는다.

기존 Schedule 조회 API를 활용하는 것을 기본 원칙으로 한다.

권장 방식:

```http
GET /api/schedules
```

Query Parameter:

```text
projectId

startDate

endDate
```

예:

```http
GET /api/schedules
    ?projectId=1
    &startDate=2026-09-01
    &endDate=2026-09-30
```

Backend는 해당 기간과 겹치는 Schedule을 반환한다.

---

# 13. Calendar API Option

Calendar 구현 방식은 다음 두 가지가 가능하다.

## Option 1. 기존 Schedule Search API 활용

```http
GET /api/schedules
```

예:

```http
GET /api/schedules
    ?projectId=1
    &startDate=2026-09-01
    &endDate=2026-09-30
```

장점:

```text
API 중복 방지

Schedule Domain 중심 구조

Calendar 전용 Backend 불필요

유지보수 단순
```

---

## Option 2. Calendar 전용 조회 API

```http
GET /api/projects/{projectId}/schedules/calendar
```

예:

```http
GET /api/projects/1/schedules/calendar
    ?startDate=2026-09-01
    &endDate=2026-09-30
```

장점:

```text
Calendar 전용 Query 최적화

Calendar 데이터 전용 DTO 가능
```

단점:

```text
API 중복 가능성

Controller 증가

Schedule Search와 기능 중복
```

---

# 14. 권장 API 구조

PMIS V1에서는 **Option 1을 기본으로 사용한다.**

```text
Schedule Search API

        │

        ├── List UI
        │
        ├── Search UI
        │
        └── Calendar UI
```

즉:

```text
Schedule Domain

        │

        ▼

Schedule Search API

        │

    ┌───┼────┐

    │   │    │

    ▼   ▼    ▼

 List Search Calendar
```

Calendar는 새로운 데이터를 요구하지 않는 한 별도 API를 생성하지 않는다.

---

# 15. Schedule Search Request 확장

Calendar 기간 조회를 위해 Search Request에 날짜 조건을 추가할 수 있다.

예:

```text
ScheduleSearchRequest

├── projectId
│
├── wbsId
│
├── keyword
│
├── status
│
├── startDate
│
├── endDate
│
├── sortBy
│
├── direction
│
├── page
│
└── size
```

날짜 조건:

```text
startDate

Calendar 조회 시작일


endDate

Calendar 조회 종료일
```

Backend는 Calendar Overlap 조건을 적용한다.

---

# 16. Month View

Calendar V1의 기본 화면은 Month View로 한다.

예:

```text
                 2026년 9월

       ◀                         ▶

 Sun   Mon   Tue   Wed   Thu   Fri   Sat

  30    31     1     2     3     4     5

               █████████
               프로젝트 계획


   6     7     8     9    10    11    12

         ███████████████████
         서버 설치


  13    14    15    16    17    18    19


  20    21    22    23    24    25    26


  27    28    29    30
```

Month View는 다음 정보를 제공한다.

- 날짜
- 오늘 날짜
- 이전/다음 달 날짜
- Schedule Event
- Event Status
- Multi-day Schedule

---

# 17. Calendar Navigation

Calendar는 기본적으로 다음 Navigation을 제공한다.

```text
Previous Month

Current Month

Next Month

Today
```

구조:

```text
◀

2026년 9월

Today

▶
```

Navigation 흐름:

```text
Next Month Click

        │

        ▼

2026-09

        │

        ▼

2026-10

        │

        ▼

Date Range 변경

        │

        ▼

Schedule 조회

        │

        ▼

Calendar Rendering
```

---

# 18. Previous Month

Previous Month:

```text
Current

2026년 9월


Previous

2026년 8월
```

Frontend 상태:

```typescript
const [currentDate, setCurrentDate] =
  useState(new Date());
```

예:

```text
2026-09

        ↓

Previous Month

        ↓

2026-08
```

---

# 19. Next Month

Next Month:

```text
Current

2026년 9월


Next

2026년 10월
```

예:

```text
2026-09

        ↓

Next Month

        ↓

2026-10
```

---

# 20. Today

Today 버튼은 Calendar를 현재 날짜가 포함된 월로 이동한다.

예:

```text
Current Calendar

2026년 12월


Today Click


        ↓


2026년 9월
```

Today는 일정 데이터를 변경하지 않는다.

---

# 21. Calendar Grid

Month View는 일반적으로 7일 단위 Grid를 사용한다.

```text
Sun

Mon

Tue

Wed

Thu

Fri

Sat
```

구조:

```text
Calendar

    │

    ▼

Calendar Grid

    │

    ├── Week 1
    │
    ├── Week 2
    │
    ├── Week 3
    │
    ├── Week 4
    │
    ├── Week 5
    │
    └── Week 6
```

월별 Calendar는 일반적으로 최대 6개의 Week를 표시할 수 있다.

---

# 22. Calendar Day

Calendar의 하루는 `CalendarDay` Component로 표현한다.

권장 구조:

```text
CalendarDay

├── Date Number
│
├── Today Indicator
│
├── Other Month Indicator
│
└── Events
```

예:

```text
┌───────────────────┐

│ 12                │

│                   │

│ ● 서버 설치       │

│ ● 테스트 수행     │

│                   │

└───────────────────┘
```

---

# 23. Calendar Event

Calendar Event는 Schedule을 시각적으로 표시한다.

```text
Schedule

서버 설치


startDate

2026-09-05


endDate

2026-09-10
```

Calendar:

```text
09-05

████████████████████

서버 설치

████████████████████

09-10
```

Calendar Event의 기본 정보:

```text
Schedule Name

Start Date

End Date

Status
```

---

# 24. Single-day Schedule

하루 일정:

```text
startDate

2026-09-10


endDate

2026-09-10
```

Calendar:

```text
10

● 일정 회의
```

Single-day Schedule은 하나의 날짜에 표시한다.

---

# 25. Multi-day Schedule

여러 날짜에 걸친 Schedule을 지원한다.

예:

```text
Start

2026-09-05


End

2026-09-10
```

Calendar:

```text
05    06    07    08    09    10

████████████████████████████████

        서버 설치

████████████████████████████████
```

Multi-day Schedule은 하나의 연속된 일정으로 표현할 수 있다.

---

# 26. Month Boundary Schedule

일정이 월 경계를 넘을 수 있다.

예:

```text
Start

2026-08-28


End

2026-09-05
```

9월 Calendar에서는 다음과 같이 표시한다.

```text
September

01   02   03   04   05

████████████████████
```

Calendar는 현재 화면에 표시되는 기간만 Rendering한다.

---

# 27. Calendar Event Status

Schedule Status를 Calendar에서 표현한다.

Backend Status:

```text
PLANNED

IN_PROGRESS

COMPLETED

ON_HOLD

CANCELLED
```

Frontend는 Status를 표시용 Label로 변환할 수 있다.

| Status | 표시 |
|---|---|
| PLANNED | 계획 |
| IN_PROGRESS | 진행 중 |
| COMPLETED | 완료 |
| ON_HOLD | 보류 |
| CANCELLED | 취소 |

---

# 28. Status 표시 원칙

Status의 실제 값은 Backend Enum을 유지한다.

```text
Backend

IN_PROGRESS
```

Frontend:

```text
진행 중
```

예:

```typescript
export const SCHEDULE_STATUS_LABEL = {

  PLANNED: '계획',

  IN_PROGRESS: '진행 중',

  COMPLETED: '완료',

  ON_HOLD: '보류',

  CANCELLED: '취소',

};
```

Frontend는 새로운 Status 값을 생성하지 않는다.

---

# 29. Calendar Event Click

사용자가 Event를 클릭하면 해당 Schedule을 선택한다.

```text
Calendar Event Click

        │

        ▼

Schedule ID 확보

        │

        ▼

Schedule Detail
```

예:

```text
서버 설치

Click

    ↓

Schedule ID = 15

    ↓

GET /api/schedules/15

    ↓

Schedule Detail 표시
```

---

# 30. Schedule Detail 연계

Calendar는 Schedule 상세 화면과 연계할 수 있다.

```text
Calendar

    │

    │ Event Click

    ▼

Schedule Detail

    │

    ├── Schedule Name
    │
    ├── WBS
    │
    ├── Start Date
    │
    ├── End Date
    │
    ├── Status
    │
    └── Description
```

표현 방식은 다음 중 하나를 선택할 수 있다.

```text
Option 1

Side Panel


Option 2

Modal


Option 3

Detail Page
```

PMIS V1에서는 기존 Schedule UI 구조에 맞는 방식을 사용한다.

---

# 31. Calendar와 Schedule CRUD

Calendar에서 Schedule CRUD를 직접 제공할지는 별도 UI 정책에 따른다.

V1 기본 정책:

```text
Calendar

→ 조회 중심
```

CRUD:

```text
Schedule List

또는

Schedule Detail
```

따라서 다음 구조를 기본으로 한다.

```text
Schedule List

    │

    ├── Create
    │
    ├── Edit
    │
    └── Delete


Calendar

    │

    └── View
```

---

# 32. Calendar Create 확장

향후 Calendar에서 날짜를 클릭하여 Schedule 생성 기능을 제공할 수 있다.

```text
Calendar Date Click

        │

        ▼

2026-09-15

        │

        ▼

Schedule Create Dialog

        │

        ▼

startDate

2026-09-15
```

그러나 Calendar V1에서는 필수 기능으로 포함하지 않는다.

---

# 33. Calendar Drag & Drop

향후 Drag & Drop을 통해 일정 날짜를 변경할 수 있다.

```text
Before

████ 서버 설치 ████


Drag


        ↓


After

      ████ 서버 설치 ████
```

Backend:

```text
PUT /api/schedules/{id}
```

변경:

```text
startDate

endDate
```

하지만 V1에서는 제외한다.

이유:

```text
UI 복잡도 증가

Date Validation 필요

Backend Update 정책 필요

Multi-day Schedule 처리 필요
```

---

# 34. Calendar Resize

향후 Event Resize 기능을 제공할 수 있다.

예:

```text
Before

████████


Resize

        ↓


██████████████
```

변경:

```text
endDate
```

V1에서는 제외한다.

---

# 35. Project Filter

Calendar는 Project 기준으로 일정을 조회한다.

```text
Project

▼

금융결제원 차세대 인프라 구축
```

선택:

```text
Project ID

1
```

API:

```http
GET /api/schedules?projectId=1
```

Calendar는 선택된 Project의 일정만 표시한다.

---

# 36. WBS Filter

Calendar에서 WBS 기준으로 일정을 필터링할 수 있다.

```text
Project

금융결제원 차세대 인프라 구축


WBS

▼

전체


또는


프로젝트 계획
```

API:

```http
GET /api/schedules
    ?projectId=1
    &wbsId=2
```

WBS Filter는 Calendar V1의 선택 기능으로 관리한다.

---

# 37. Status Filter

Calendar는 Status 기준 필터를 제공할 수 있다.

```text
Status

☑ PLANNED

☑ IN_PROGRESS

☑ COMPLETED

☑ ON_HOLD

☑ CANCELLED
```

또는:

```text
Status

▼

전체
```

API:

```http
GET /api/schedules
    ?projectId=1
    &status=IN_PROGRESS
```

---

# 38. Calendar Filter 구조

권장 구조:

```text
ScheduleCalendar

│

├── Project Filter
│
├── WBS Filter
│
├── Status Filter
│
└── Calendar
```

초기 V1에서는 최소한 다음 조건을 사용한다.

```text
Project

Date Range
```

---

# 39. Frontend Component Structure

권장 구조:

```text
features

└── schedule

    ├── api

    │   └── scheduleApi.ts

    │
    ├── components

    │   ├── ScheduleList.tsx

    │   ├── ScheduleDetail.tsx

    │   ├── ScheduleForm.tsx

    │   │
    │   └── calendar

    │       ├── ScheduleCalendar.tsx

    │       ├── CalendarHeader.tsx

    │       ├── CalendarToolbar.tsx

    │       ├── CalendarGrid.tsx

    │       ├── CalendarWeek.tsx

    │       ├── CalendarDay.tsx

    │       └── CalendarEvent.tsx

    │
    ├── hooks

    │   └── useScheduleCalendar.ts

    │
    ├── types

    │   └── schedule.ts

    │
    ├── utils

    │   └── calendarUtils.ts

    │
    └── pages

        └── SchedulePage.tsx
```

---

# 40. ScheduleCalendar Component

`ScheduleCalendar`는 Calendar Feature의 Root Component이다.

책임:

```text
Calendar State 관리

API 호출

Date Range 관리

Calendar Event 변환

Child Component 연결
```

구조:

```text
ScheduleCalendar

│

├── CalendarHeader
│
├── CalendarToolbar
│
└── CalendarGrid
```

---

# 41. CalendarHeader Component

책임:

```text
현재 연도 표시

현재 월 표시

Previous Month

Next Month

Today
```

예:

```text
┌────────────────────────────────────┐

│                                    │

│  ◀     2026년 9월      Today    ▶ │

│                                    │

└────────────────────────────────────┘
```

---

# 42. CalendarToolbar Component

Calendar의 Filter 및 Action을 관리한다.

예:

```text
┌───────────────────────────────────────────┐

│ Project ▼     WBS ▼      Status ▼         │

└───────────────────────────────────────────┘
```

V1에서는 필요에 따라 단순화할 수 있다.

---

# 43. CalendarGrid Component

Month View의 전체 Grid를 관리한다.

```text
CalendarGrid

│

├── Week Header
│
├── CalendarWeek
│
├── CalendarWeek
│
├── CalendarWeek
│
├── CalendarWeek
│
├── CalendarWeek
│
└── CalendarWeek
```

---

# 44. CalendarWeek Component

Calendar의 한 주를 표현한다.

```text
Sun

Mon

Tue

Wed

Thu

Fri

Sat
```

구조:

```text
CalendarWeek

├── CalendarDay
├── CalendarDay
├── CalendarDay
├── CalendarDay
├── CalendarDay
├── CalendarDay
└── CalendarDay
```

---

# 45. CalendarDay Component

하루의 일정 정보를 표시한다.

```text
CalendarDay

│

├── Date
│
├── Today Indicator
│
└── Event List
```

예:

```text
┌────────────────────┐

│ 15                 │

│                    │

│ ● 프로젝트 계획    │

│ ● 서버 설치        │

│ +2 more            │

└────────────────────┘
```

---

# 46. CalendarEvent Component

하나의 Schedule Event를 표시한다.

기본 정보:

```text
Schedule Name

Status
```

예:

```text
┌───────────────────┐

│ 서버 설치         │

└───────────────────┘
```

Event Click:

```text
Click

↓

Schedule Detail
```

---

# 47. useScheduleCalendar Hook

Calendar 상태와 조회 로직이 복잡해질 경우 Custom Hook으로 분리할 수 있다.

예:

```text
useScheduleCalendar

│

├── currentDate
│
├── dateRange
│
├── schedules
│
├── events
│
├── loading
│
├── error
│
├── previousMonth()
│
├── nextMonth()
│
└── goToday()
```

이 Hook은 UI와 Business Logic을 분리하는 역할을 한다.

---

# 48. Calendar Utility

Calendar 날짜 계산은 별도의 Utility로 관리하는 것을 권장한다.

예:

```text
calendarUtils.ts

├── getMonthStart()
│
├── getMonthEnd()
│
├── getCalendarStart()
│
├── getCalendarEnd()
│
├── getPreviousMonth()
│
├── getNextMonth()
│
├── isSameDay()
│
└── isSameMonth()
```

---

# 49. Calendar Date Range 계산

Month View의 실제 Grid는 해당 월 이외의 날짜를 포함할 수 있다.

예:

```text
September 2026


Sun Mon Tue Wed Thu Fri Sat


30  31   1   2   3   4   5
```

따라서 API 조회 범위를 다음과 같이 결정할 수 있다.

## Option 1

해당 월 기준

```text
2026-09-01

~

2026-09-30
```

## Option 2

실제 Calendar Grid 기준

```text
2026-08-30

~

2026-10-03
```

PMIS V1에서는 Option 1을 기본으로 한다.

```text
Month Start

~

Month End
```

---

# 50. API Client

Frontend API Client 예시:

```typescript
export interface ScheduleCalendarQuery {

  projectId: number;

  startDate: string;

  endDate: string;

  wbsId?: number;

  status?: ScheduleStatus;

}
```

예:

```typescript
export const getCalendarSchedules = async (

  query: ScheduleCalendarQuery

): Promise<ScheduleResponse[]> => {

  const response = await apiClient.get(
    '/schedules',
    {
      params: query,
    }
  );

  return response.data;

};
```

실제 PMIS의 공통 API Client 및 Response 구조를 사용한다.

---

# 51. API Response 처리

PMIS Backend의 실제 API Response 구조를 기준으로 처리한다.

예:

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "조회 성공",
  "data": []
}
```

또는:

```json
[
  {
    "id": 1,
    "scheduleName": "프로젝트 계획"
  }
]
```

또는 Page Response:

```json
{
  "content": [],
  "totalElements": 0
}
```

Frontend는 Swagger/OpenAPI의 실제 Response Schema를 최종 기준으로 사용한다.

---

# 52. Calendar Loading State

Schedule API 호출 중에는 Loading 상태를 표시한다.

```text
Calendar

        ↓

Loading

        ↓

API Response

        ↓

Calendar Rendering
```

예:

```text
┌─────────────────────────────┐

│                             │

│       Loading...            │

│                             │

└─────────────────────────────┘
```

기존 PMIS 공통 `Loading Component`를 우선 사용한다.

---

# 53. Calendar Empty State

해당 기간에 일정이 없는 경우 Empty State를 표시한다.

```text
Calendar

2026년 9월


No Schedule
```

예:

```text
┌─────────────────────────────┐

│                             │

│    등록된 일정이 없습니다.  │

│                             │

└─────────────────────────────┘
```

Calendar Grid 자체는 표시할 수 있다.

---

# 54. Calendar Error State

API 오류 발생 시 Error 상태를 처리한다.

예:

```text
Schedule 조회 실패


[ 다시 시도 ]
```

오류 흐름:

```text
API Request

    │

    ▼

Error

    │

    ▼

Error Message

    │

    ▼

Retry
```

---

# 55. Calendar Error Handling

최소한 다음 HTTP 상태를 고려한다.

```text
400 Bad Request

401 Unauthorized

403 Forbidden

404 Not Found

500 Internal Server Error
```

예:

```text
401

→ Login


403

→ 권한 없음


404

→ Project 또는 Schedule 없음


500

→ 서버 오류
```

공통 API Error Handling 정책을 따른다.

---

# 56. Calendar Validation

Calendar 자체는 입력 데이터가 적기 때문에 Validation은 제한적이다.

기본 Validation:

```text
Project 선택 여부

Date Range 유효성
```

Date Range:

```text
startDate <= endDate
```

Schedule의 업무 Validation은 Backend 책임이다.

---

# 57. Calendar와 Time Zone

PMIS Schedule은 기본적으로 날짜 기반 데이터를 사용한다.

```text
LocalDate
```

예:

```text
2026-09-08
```

Calendar V1에서는 시간(Time)을 관리하지 않는다.

따라서 다음을 기본으로 한다.

```text
Start Date

End Date
```

다음은 V1 범위에서 제외한다.

```text
Start Time

End Time

Time Zone

All Day Event

Hourly Event
```

---

# 58. Calendar와 Schedule Duration

Duration은 Calendar에서 표시용으로 계산할 수 있다.

예:

```text
Start Date

2026-09-01


End Date

2026-09-10


Duration

10 Days
```

단, Duration의 영구 저장 책임은 Calendar에 없다.

```text
Schedule

Date Data

        ↓

Calendar

Display Duration
```

---

# 59. Calendar Event Overflow

하나의 날짜에 일정이 많을 수 있다.

예:

```text
15

● 프로젝트 계획

● 서버 설치

● OS 설치

● Middleware 설치

+3 more
```

V1에서는 일정 표시 개수를 제한할 수 있다.

예:

```text
최대 3개
```

나머지는:

```text
+N more
```

로 표시한다.

---

# 60. More Event

`+N more` 클릭 시 처리 방식은 향후 결정한다.

가능한 방식:

```text
Option 1

Modal


Option 2

Side Panel


Option 3

Day Detail View
```

Calendar V1에서는 단순 표시만 제공할 수 있다.

---

# 61. Calendar Event Sorting

하나의 날짜에 여러 Event가 존재하는 경우 정렬 기준이 필요하다.

권장 우선순위:

```text
1. startDate

2. endDate

3. sortOrder

4. scheduleName
```

실제 Backend 정렬 정책을 우선한다.

---

# 62. Calendar Performance

프로젝트에 일정이 많아질 경우 전체 Schedule을 모두 조회하는 것은 비효율적일 수 있다.

비권장:

```text
GET

All Project Schedules


        ↓


Frontend Filtering
```

권장:

```text
Calendar Date Range

        ↓

Backend Search

        ↓

Required Schedules

        ↓

Calendar Rendering
```

즉:

```text
Server Filtering

+

Frontend Rendering
```

구조를 권장한다.

---

# 63. Calendar Pagination

Calendar는 일반적인 목록 Pagination과 다르게 동작한다.

```text
List

Page 1

Page 2

Page 3
```

Calendar:

```text
September

October

November
```

Calendar Navigation 자체가 시간 단위 Pagination 역할을 한다.

따라서 Calendar 기간 조회에서는 일반적인 Page 기반 UI를 사용하지 않을 수 있다.

---

# 64. Calendar와 Schedule Search

Calendar는 Schedule Search API의 하나의 Consumer이다.

```text
Schedule Search API

        │

        ├───────────────┐
        │               │
        ▼               ▼

Schedule List      Calendar
```

Search API는 여러 Frontend 화면에서 재사용될 수 있다.

---

# 65. Calendar와 Gantt Chart

Calendar와 Gantt Chart는 서로 다른 시각화 방식이다.

```text
Schedule

    │

    ├───────────────┐
    │               │
    ▼               ▼

Calendar         Gantt
```

Calendar:

```text
날짜 중심
```

Gantt:

```text
기간 중심
```

Calendar는 월/주/일 기준으로 일정을 표시한다.

Gantt는 업무의 시작일과 종료일을 시간 축으로 표시한다.

---

# 66. Calendar와 Timeline

Timeline 역시 Schedule 데이터를 사용할 수 있다.

```text
Schedule

    │

    ├── Calendar
    │
    ├── Timeline
    │
    └── Gantt
```

따라서 Schedule Domain은 여러 시각화 Feature의 공통 데이터 공급자가 된다.

---

# 67. Calendar와 Dashboard

Dashboard에서 Calendar 정보를 사용할 수 있다.

예:

```text
Project Dashboard

│

├── Upcoming Schedule
│
├── Today's Schedule
│
├── Delayed Schedule
│
└── Calendar
```

Dashboard는 Calendar 데이터를 직접 관리하지 않는다.

기본 구조:

```text
Schedule

    │

    ▼

Schedule API

    │

    ├── Dashboard
    │
    ├── Calendar
    │
    └── Gantt
```

---

# 68. Calendar V1 범위

Schedule Calendar V1의 기본 범위는 다음과 같다.

### Calendar

- Month View
- Calendar Grid
- Previous Month
- Next Month
- Today
- Current Month 표시

### Schedule

- Project Schedule 조회
- Date Range 기반 Schedule 조회
- Schedule Event 표시
- Single-day Schedule
- Multi-day Schedule
- Month Boundary Schedule

### Interaction

- Event Click
- Schedule 선택
- Schedule Detail 연계

### UI

- Loading
- Empty State
- Error State

---

# 69. Calendar V1 제외 범위

다음 기능은 Calendar V1에서 제외한다.

```text
Week View

Day View

Drag & Drop

Event Resize

Calendar Create

Calendar Edit

Calendar Delete

Schedule Dependency

Baseline

Critical Path

Resource Calendar

Holiday Calendar

Google Calendar Integration

Outlook Calendar Integration

iCalendar

Recurring Event

Time-based Schedule
```

---

# 70. 향후 Calendar 확장

Calendar는 향후 다음 기능으로 확장할 수 있다.

## View

```text
Month View

Week View

Day View

Agenda View
```

## Interaction

```text
Drag & Drop

Resize

Quick Create

Quick Edit
```

## Integration

```text
Dashboard

Gantt

Issue

Risk

Change
```

## External Integration

```text
Google Calendar

Microsoft Outlook

iCalendar
```

---

# 71. Calendar V2

Calendar V2에서 고려할 수 있는 기능:

```text
Week View

Day View

Date Click

Quick Create

Event Drag & Drop

Event Resize

Status Filter

WBS Filter
```

---

# 72. Calendar V3

Calendar V3에서 고려할 수 있는 기능:

```text
Dependency

Baseline

Schedule Delay

Critical Path

Resource Calendar

Holiday

External Calendar Integration
```

---

# 73. Development Flow

Calendar Feature 개발은 다음 순서를 따른다.

```text
Schedule API 확인

        ↓

Calendar Design

        ↓

Feature Branch 생성

        ↓

Calendar Type 정의

        ↓

Date Utility 구현

        ↓

API Client 구현

        ↓

Schedule → Calendar Event 변환

        ↓

Calendar Header 구현

        ↓

Calendar Grid 구현

        ↓

Calendar Day 구현

        ↓

Calendar Event 구현

        ↓

API Integration

        ↓

Loading / Empty / Error

        ↓

Browser Verification

        ↓

Production Build

        ↓

Documentation Update

        ↓

Commit

        ↓

Push

        ↓

Pull Request

        ↓

Merge to develop
```

---

# 74. Feature Branch

Calendar Feature Branch:

```text
feature/schedule-calendar
```

Branch 흐름:

```text
develop

    │

    └── feature/schedule-calendar

              │

              ▼

       Calendar Development

              │

              ▼

       Local Verification

              │

              ▼

       Documentation

              │

              ▼

       Commit

              │

              ▼

       Push

              │

              ▼

       Pull Request

              │

              ▼

       Merge to develop
```

---

# 75. Development Checklist

## Design

- [x] Calendar Feature 책임 정의
- [x] Schedule과 Calendar 책임 분리
- [x] Calendar Event Model 정의
- [x] Date Range 정책 정의
- [x] Overlap Query 정책 정의
- [x] V1 범위 정의

## Backend

- [ ] Schedule Search API 확인
- [ ] Date Range Query 지원 확인
- [ ] Overlap Query 구현 여부 확인
- [ ] Project Filter 확인
- [ ] WBS Filter 확인
- [ ] Status Filter 확인
- [ ] Swagger 확인

## Frontend API

- [ ] Calendar Query Type
- [ ] Schedule API Client
- [ ] Date Range Parameter
- [ ] Response Type 확인
- [ ] API Error Handling

## Frontend Calendar

- [ ] ScheduleCalendar
- [ ] CalendarHeader
- [ ] CalendarToolbar
- [ ] CalendarGrid
- [ ] CalendarWeek
- [ ] CalendarDay
- [ ] CalendarEvent

## Calendar Function

- [ ] Month View
- [ ] Previous Month
- [ ] Next Month
- [ ] Today
- [ ] Date Range 계산
- [ ] Schedule Event 변환
- [ ] Single-day Schedule
- [ ] Multi-day Schedule
- [ ] Month Boundary 처리

## UX

- [ ] Loading
- [ ] Empty State
- [ ] Error State
- [ ] Event Click
- [ ] Schedule Detail

## Verification

- [ ] API Verification
- [ ] Browser Verification
- [ ] Multi-day Schedule 확인
- [ ] Month Boundary 확인
- [ ] Previous Month 확인
- [ ] Next Month 확인
- [ ] Today 확인
- [ ] Production Build

---

# 76. Calendar Backend 확인 항목

Frontend 구현 전에 다음 항목을 확인한다.

```text
1.

GET /api/schedules 존재 여부


2.

projectId 검색 가능 여부


3.

startDate 검색 가능 여부


4.

endDate 검색 가능 여부


5.

Overlap Query 지원 여부


6.

WBS Filter 지원 여부


7.

Status Filter 지원 여부


8.

Pagination Response 여부


9.

ApiResponse Wrapper 여부


10.

Swagger Response Schema
```

---

# 77. Backend 구현 필요성

Calendar 구현을 위해 새로운 Backend Feature가 반드시 필요한 것은 아니다.

다음 조건이 충족되면 Frontend 중심 구현이 가능하다.

```text
Schedule API 존재

        +

Project Filter

        +

Date Range Filter

        +

Overlap Query
```

구조:

```text
Backend

Schedule Search API

        ↓

Frontend

Calendar Feature
```

---

# 78. Backend Feature가 필요한 경우

다음 경우에는 Backend Feature 추가를 고려한다.

```text
Date Range Query 없음


Overlap Query 없음


전체 Schedule 조회만 가능


대량 데이터 Performance 문제


Calendar 전용 집계 데이터 필요
```

이 경우 별도의 Backend Feature를 생성할 수 있다.

예:

```text
feature/schedule-calendar-api
```

하지만 현재 PMIS 구조에서는 기존 Schedule Search API를 확장하는 방식을 우선 고려한다.

---

# 79. Frontend와 Backend 책임

명확한 책임 분리는 다음과 같다.

## Backend

```text
Schedule Data

Schedule Validation

Project Validation

WBS Validation

Date Validation

Overlap Query

Database Query
```

## Frontend

```text
Calendar Navigation

Date Range 계산

Calendar Grid

Event Rendering

Event Click

Loading

Empty State

Error State
```

---

# 80. Calendar Domain 분리 금지 원칙

Calendar를 독립 Domain으로 만들지 않는다.

비권장:

```text
Project

├── WBS
│
├── Schedule
│
└── Calendar
```

권장:

```text
Project

    │

    ▼

Schedule

    │

    ├── List View
    │
    ├── Calendar View
    │
    └── Gantt View
```

Calendar는 Schedule Data의 표현 방식이다.

---

# 81. Schedule Domain 확장 구조

향후 구조는 다음과 같이 확장될 수 있다.

```text
Schedule Domain

│

├── Schedule CRUD
│
├── Schedule Search
│
├── Schedule Detail
│
│
└── Visualization

    │

    ├── List View
    │
    ├── Calendar View
    │
    ├── Timeline View
    │
    └── Gantt View
```

---

# 82. Calendar 설계 원칙

1. Calendar는 독립적인 핵심 Domain이 아니다.

2. Calendar는 Schedule 데이터를 시각화한다.

3. Calendar는 새로운 Schedule 데이터를 저장하지 않는다.

4. Schedule CRUD 책임은 Schedule Domain에 있다.

5. Calendar는 기존 Schedule API를 우선 활용한다.

6. Calendar 조회는 기간 기반으로 수행한다.

7. 기간 조회는 단순 시작일 검색이 아니라 Overlap Query를 사용한다.

8. Backend는 데이터 조회와 Validation을 담당한다.

9. Frontend는 Calendar Rendering을 담당한다.

10. Calendar Event는 Frontend View Model로 관리할 수 있다.

11. Backend Entity를 Frontend UI에 직접 의존시키지 않는다.

12. Calendar V1은 Month View 중심으로 구현한다.

13. Week View와 Day View는 향후 확장한다.

14. Drag & Drop은 V1에서 제외한다.

15. Calendar는 Schedule Detail과 연계할 수 있다.

16. Calendar는 Gantt와 동일한 기능이 아니다.

17. Calendar와 Gantt는 동일한 Schedule 데이터를 활용할 수 있다.

18. 대량 데이터 환경에서는 Backend Date Range Filtering을 사용한다.

19. Frontend API 계약은 Swagger/OpenAPI를 최종 기준으로 한다.

20. 문서와 실제 구현 상태를 항상 동일하게 유지한다.

---

# 83. PMIS 전체 흐름에서 Calendar 위치

PMIS 업무 흐름:

```text
Project

    ↓

WBS

    ↓

Schedule

    │

    ├───────────────┐

    ▼               ▼

Calendar          Gantt

    │               │

    └───────┬───────┘

            ▼

         Dashboard

            │

            ▼

          Report

            │

            ▼

      Project Closure
```

Calendar는 Schedule 데이터를 프로젝트 운영자가 쉽게 확인하기 위한 시각화 계층이다.

---

# 84. Current Development Snapshot

```text
Schedule

├─ Schedule CRUD                 ✓
│
├─ Schedule Search               ✓
│
├─ Date Validation               ✓
│
├─ WBS Association               ✓
│
├─ Schedule Status               ✓
│
└─ Calendar Data Source          →


Calendar

├─ Calendar Design               ✓
│
├─ Month View                    →
│
├─ Calendar Navigation           →
│
├─ Date Range Query              →
│
├─ Event Rendering               →
│
├─ Multi-day Schedule            →
│
├─ Event Click                   →
│
├─ Loading / Empty / Error       →
│
└─ API Integration               →
```

---

# 85. Next Implementation Milestone

## Phase 1 — API 확인

```text
Schedule Swagger 확인

        ↓

Search API 확인

        ↓

Date Range 확인

        ↓

Overlap Query 확인
```

---

## Phase 2 — Frontend Foundation

```text
Calendar Types

        ↓

Calendar Utilities

        ↓

API Client

        ↓

Calendar Event Adapter
```

---

## Phase 3 — Calendar UI

```text
CalendarHeader

        ↓

CalendarGrid

        ↓

CalendarWeek

        ↓

CalendarDay

        ↓

CalendarEvent
```

---

## Phase 4 — Integration

```text
Schedule API

        ↓

Calendar Query

        ↓

ScheduleResponse

        ↓

CalendarEvent

        ↓

Calendar Rendering
```

---

## Phase 5 — Verification

```text
Previous Month

Next Month

Today

Single-day Schedule

Multi-day Schedule

Month Boundary

Empty State

Error State

Browser Test

Production Build
```

---

# 86. Recommended Implementation Order

`feature/schedule-calendar`에서는 다음 순서를 권장한다.

```text
1.

Calendar Type 정의


2.

calendarUtils 구현


3.

Schedule Calendar API Query 구현


4.

CalendarHeader 구현


5.

CalendarGrid 구현


6.

CalendarDay 구현


7.

CalendarEvent 구현


8.

Month View 완성


9.

Previous / Next / Today 구현


10.

Schedule API Integration


11.

Multi-day Schedule 처리


12.

Loading / Empty / Error


13.

Event Click


14.

Browser Verification


15.

npm run build


16.

Documentation Update


17.

Commit


18.

Push


19.

Pull Request


20.

Merge to develop
```

---

# 87. Document History

| Version | Date | Author | Description |
|---|---|---|---|
| 1.0 | 2026-09-08 | Seo Seokhyeon | Initial Schedule Calendar Feature Design |

---

# End of Document