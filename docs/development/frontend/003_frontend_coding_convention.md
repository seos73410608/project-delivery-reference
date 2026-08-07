# Frontend Coding Convention

## 1. 문서 정보

| 항목 | 내용 |
|------|------|
| 시스템 | PMIS (Project Management Information System) |
| 문서명 | Frontend Coding Convention |
| 문서번호 | FA-003 |
| 작성일 | 2026-08-07 |
| 버전 | 1.0 |

---

# 2. 목적

본 문서는 PMIS Frontend 개발 시 적용하는 코딩 규칙을 정의한다.

모든 개발자는 동일한 규칙을 적용하여 일관된 코드 품질을 유지하도록 한다.

---

# 3. 기본 원칙

Frontend는 다음 원칙을 따른다.

- 가독성이 높은 코드 작성
- 일관된 네이밍 사용
- 재사용 가능한 Component 개발
- Feature 중심 개발
- TypeScript 적극 활용
- Business Logic 최소화
- Component 단일 책임(SRP)

---

# 4. 네이밍 규칙

## Component

PascalCase를 사용한다.

```
Header.tsx

Sidebar.tsx

ProjectTable.tsx

ProjectSearchForm.tsx
```

---

## Page

모든 Page는 Page 접미사를 사용한다.

```
DashboardPage.tsx

ProjectPage.tsx

IssuePage.tsx

LoginPage.tsx
```

---

## Layout

Layout은 Layout 접미사를 사용한다.

```
MainLayout.tsx

LoginLayout.tsx
```

---

## Hook

Hook은 use 접두사를 사용한다.

```
useAuth.ts

usePagination.ts

useApi.ts
```

---

## API

API는 camelCase를 사용한다.

```
authApi.ts

projectApi.ts

issueApi.ts
```

---

## Service

Service는 camelCase를 사용한다.

```
tokenService.ts

storageService.ts
```

---

## Utility

Utility는 camelCase를 사용한다.

```
dateUtil.ts

numberUtil.ts

validationUtil.ts
```

---

# 5. Component 작성 규칙

모든 Component는 함수형 Component(Function Component)를 사용한다.

예)

```tsx
function Header() {
    return (
        <header>
            Header
        </header>
    );
}

export default Header;
```

Arrow Function과 Function Declaration을 혼용하지 않는다.

PMIS에서는 Function Declaration을 기본으로 사용한다.

---

# 6. TypeScript 사용 원칙

모든 Props는 interface로 정의한다.

예)

```tsx
interface ProjectTableProps {
    projects: Project[];
}

function ProjectTable(props: ProjectTableProps) {
    return <div></div>;
}
```

가능한 any 타입은 사용하지 않는다.

---

# 7. Import 순서

Import는 다음 순서를 따른다.

```tsx
React

↓

외부 라이브러리

↓

공통 Component

↓

Feature Component

↓

Type

↓

CSS
```

예)

```tsx
import { useEffect } from "react";

import { Button } from "@mui/material";

import Header from "@/components/layout/Header";

import ProjectTable from "../components/ProjectTable";

import { Project } from "../types/Project";

import "./ProjectPage.css";
```

---

# 8. Component 크기

하나의 Component는 하나의 역할만 수행한다.

권장

- 200줄 이하

최대

- 300줄 이하

300줄 이상이 되면 분리를 검토한다.

---

# 9. Page 작성 원칙

Page는 화면을 조립하는 역할만 수행한다.

예)

```
ProjectPage

↓

SearchForm

↓

ProjectTable

↓

Pagination
```

Business Logic은 Service 또는 Hook으로 분리한다.

---

# 10. API 작성 규칙

API는 Controller 단위로 작성한다.

예)

```
projectApi.ts

authApi.ts

scheduleApi.ts

issueApi.ts
```

한 파일에 여러 업무 API를 혼합하지 않는다.

---

# 11. State 관리

State는 필요한 범위에서만 관리한다.

우선순위

1. Component State
2. Custom Hook
3. Context
4. Global Store

처음부터 전역 상태를 사용하지 않는다.

---

# 12. CSS 작성 원칙

공통 스타일은 styles에서 관리한다.

Component 전용 스타일은 Component 내부에서 관리한다.

Inline Style 사용은 최소화한다.

---

# 13. Error 처리

API 호출 시 반드시 Error를 처리한다.

예)

```tsx
try {

} catch (error) {

}
```

사용자에게 오류 메시지를 표시한다.

---

# 14. 주석 작성

무엇(What)보다 왜(Why)를 설명한다.

좋은 예)

```tsx
// JWT 만료 시 로그인 화면으로 이동한다.
```

좋지 않은 예)

```tsx
// 버튼 클릭
```

코드만 봐도 알 수 있는 내용은 주석으로 작성하지 않는다.

---

# 15. TODO 규칙

미완성 기능은 TODO를 사용한다.

예)

```tsx
// TODO
// Refresh Token 자동 갱신 구현 예정
```

---

# 16. 금지 사항

다음 사항은 금지한다.

- any 남용
- 하나의 Component에서 여러 역할 수행
- 중복 코드 작성
- 직접 DOM 조작
- Business Logic을 Page에 작성
- API 호출을 Component 여러 곳에서 중복 작성

---

# 17. Git Commit 규칙

Frontend Commit Prefix

```
feat(frontend)

fix(frontend)

refactor(frontend)

style(frontend)

docs(frontend)
```

예)

```
feat(frontend): add dashboard page

feat(frontend): implement project list

fix(frontend): resolve sidebar navigation

docs(frontend): update coding convention
```

---

# 18. 결론

PMIS Frontend는 일관된 코딩 규칙을 적용하여 유지보수성과 확장성을 확보한다.

모든 개발은 본 문서의 규칙을 기준으로 수행하며, 새로운 기능을 추가할 때에도 동일한 개발 패턴을 유지한다.