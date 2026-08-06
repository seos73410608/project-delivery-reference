# PMIS Project API Test Guide

> Version: **1.1**  
> Last Updated: **2026-08-06**  
> Sprint: **Sprint 3 - Project Search**

---

# 1. Purpose

본 문서는 Project API(Project CRUD + Search)의 정상 동작을 검증하기 위한 테스트 절차를 정의한다.

Swagger(OpenAPI)를 이용하여 기능을 검증하며,
이후 기능 개발 시 Regression Test 문서로 사용한다.

---

# 2. Test Environment

## Application

```
Spring Boot
```

```
http://localhost:8080
```

---

## Swagger

```
http://localhost:8080/swagger-ui/index.html
```

---

## Test Account

| Role | ID | Password |
|------|----|----------|
| ADMIN | admin | admin123 |
| PM | pm | pm1234 |
| USER | user | user1234 |

---

# 3. Authentication Test

## STEP 1

Access Token 발급

```
GET /api/test/token
```

Expected

- HTTP 200
- Access Token 반환

---

## STEP 2

Swagger 우측 상단

```
Authorize
```

선택 후 입력

```
Bearer {AccessToken}
```

Expected

```
Authorized
```

---

# 4. Project API Test

---

## 4.1 프로젝트 생성

```
POST /api/projects
```

Example

```json
{
  "projectCode": "PMIS-2026-001",
  "projectName": "PMIS 구축",
  "customerName": "OpenAI",
  "projectManager": "홍길동",
  "description": "PMIS Backend 구축",
  "startDate": "2026-08-10",
  "endDate": "2026-12-31",
  "priority": "HIGH"
}
```

Expected

- HTTP 201
- success=true
- 프로젝트 생성 완료
- status = PLANNING 자동 저장

---

## 4.2 프로젝트 단건 조회

```
GET /api/projects/{id}
```

Example

```
GET /api/projects/1
```

Expected

- HTTP 200
- 프로젝트 정보 반환

---

## 4.3 프로젝트 검색

```
GET /api/projects
```

### 전체 조회

```
page=0
size=10
sort=id,desc
```

Expected

- HTTP 200
- 전체 프로젝트 조회

---

### 프로젝트 코드 검색

```
projectCode=PMIS
```

Expected

- projectCode에 "PMIS"가 포함된 프로젝트 조회

---

### 프로젝트명 검색

```
projectName=PMIS
```

Expected

- projectName Like 검색

---

### 고객사 검색

```
customerName=OpenAI
```

Expected

- 고객사 Like 검색

---

### PM 검색

```
projectManager=홍길동
```

Expected

- projectManager Like 검색

---

### 상태 검색

```
status=PLANNING
```

또는

```
status=IN_PROGRESS
```

Expected

- 해당 상태 프로젝트 조회

---

### 우선순위 검색

```
priority=HIGH
```

Expected

- HIGH 프로젝트만 조회

---

### 시작일 검색

```
startDateFrom=2026-01-01
startDateTo=2026-12-31
```

Expected

- 시작일 범위 검색

---

### 종료일 검색

```
endDateFrom=2026-10-01
endDateTo=2027-01-31
```

Expected

- 종료일 범위 검색

---

### 복합 검색

Example

```
projectName=PMIS
customerName=OpenAI
status=PLANNING
priority=HIGH
page=0
size=10
sort=id,desc
```

Expected

- 모든 조건을 만족하는 프로젝트만 조회

---

### Swagger 주의사항

Swagger 기본값

```
sort=["string"]
```

은 제거한다.

권장

```
sort=id,desc
```

또는 sort 미입력

---

## 4.4 프로젝트 수정

```
PUT /api/projects/{id}
```

Example

```json
{
  "projectName": "PMIS 구축 프로젝트",
  "customerName": "OpenAI Korea",
  "projectManager": "김PM",
  "description": "프로젝트 수정 테스트",
  "startDate": "2026-08-10",
  "endDate": "2027-01-31",
  "status": "IN_PROGRESS",
  "priority": "HIGH"
}
```

Expected

- HTTP 200
- 수정 완료

---

## 4.5 프로젝트 삭제

```
DELETE /api/projects/{id}
```

Example

```
DELETE /api/projects/1
```

Expected

- HTTP 200
- 삭제 완료

---

# 5. Validation Test

## 프로젝트 코드 중복

동일한 projectCode로 생성

Expected

- HTTP 400

---

## 존재하지 않는 프로젝트 조회

```
GET /api/projects/9999
```

Expected

- HTTP 404

---

## 존재하지 않는 프로젝트 수정

Expected

- HTTP 404

---

## 존재하지 않는 프로젝트 삭제

Expected

- HTTP 404

---

## 검색 결과 없음

Example

```
projectName=NOT_EXIST_PROJECT
```

Expected

- HTTP 200
- content=[]
- totalElements=0

---

# 6. Security Test

인증 없이

```
POST /api/projects
```

Expected

```
401 Unauthorized
```

---

인증 없이

```
GET /api/projects
```

Expected

```
401 Unauthorized
```

---

잘못된 JWT

Expected

```
401 Unauthorized
```

---

USER 권한으로 프로젝트 생성

Expected

```
403 Forbidden
```

---

USER 권한으로 프로젝트 수정

Expected

```
403 Forbidden
```

---

PM 권한으로 프로젝트 삭제

Expected

```
403 Forbidden
```

---

ADMIN 권한으로 프로젝트 삭제

Expected

```
200 OK
```

---

# 7. Regression Checklist

| Test Item | Result |
|-----------|--------|
| Swagger 실행 | ☐ |
| Token 발급 | ☐ |
| Swagger Authorize | ☐ |
| 프로젝트 생성 | ☐ |
| 프로젝트 단건 조회 | ☐ |
| 프로젝트 전체 조회 | ☐ |
| 프로젝트 코드 검색 | ☐ |
| 프로젝트명 검색 | ☐ |
| 고객사 검색 | ☐ |
| PM 검색 | ☐ |
| 상태 검색 | ☐ |
| 우선순위 검색 | ☐ |
| 시작일 검색 | ☐ |
| 종료일 검색 | ☐ |
| 복합 검색 | ☐ |
| 프로젝트 수정 | ☐ |
| 프로젝트 삭제 | ☐ |
| Validation 확인 | ☐ |
| Unauthorized 확인 | ☐ |
| Role 권한 확인 | ☐ |

---

# Test Result

| Date | Tester | Sprint | Result |
|------|--------|--------|--------|
| 2026-08-06 | Seo Seokhyeon | Project CRUD | PASS |
| 2026-08-06 | Seo Seokhyeon | Project Search | PASS |