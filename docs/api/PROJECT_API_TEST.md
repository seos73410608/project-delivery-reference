# PMIS Project CRUD API Test Guide

> Version: **1.0**
> Last Updated: **2026-08-06**
> Sprint: **Sprint 2 - Project CRUD Foundation**

---

# 1. Purpose

본 문서는 Project CRUD API의 정상 동작을 검증하기 위한 테스트 절차를 정의한다.

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

| Role  | ID    | Password |
| ----- | ----- | -------- |
| ADMIN | admin | admin123 |
| PM    | pm    | pm1234   |
| USER  | user  | user1234 |

---

# 3. Authentication Test

## STEP 1

Access Token 발급

```
GET
/api/test/token
```

Expected

* HTTP 200
* Access Token 반환

---

## STEP 2

Swagger 우측 상단

```
Authorize
```

선택

입력

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

* HTTP 201
* success=true
* 프로젝트 생성 완료

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

* HTTP 200
* 프로젝트 정보 반환

---

## 4.3 프로젝트 목록 조회

```
GET /api/projects
```

권장 요청

```
page=0
size=10
```

또는

```
page=0
size=10
sort=id,desc
```

Expected

* HTTP 200
* Page<ProjectResponse>

주의

Swagger의

```
sort=["string"]
```

값은 제거한다.

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

* HTTP 200
* 수정 완료

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

* HTTP 200
* 삭제 완료

---

# 5. Validation Test

## 중복 프로젝트 코드

동일한 projectCode로 생성

Expected

* HTTP 400

---

## 존재하지 않는 프로젝트 조회

```
GET /api/projects/9999
```

Expected

* HTTP 404

---

## 존재하지 않는 프로젝트 수정

Expected

* HTTP 404

---

## 존재하지 않는 프로젝트 삭제

Expected

* HTTP 404

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

잘못된 JWT

Expected

```
401 Unauthorized
```

---

권한 부족

(추후 권한 정책 적용 후)

Expected

```
403 Forbidden
```

---

# 7. Regression Checklist

| Test Item         | Result |
| ----------------- | ------ |
| Swagger 실행        | ☐      |
| Token 발급          | ☐      |
| Swagger Authorize | ☐      |
| 프로젝트 생성           | ☐      |
| 프로젝트 조회           | ☐      |
| 프로젝트 목록           | ☐      |
| 프로젝트 수정           | ☐      |
| 프로젝트 삭제           | ☐      |
| Validation 확인     | ☐      |
| Unauthorized 확인   | ☐      |

---

# Test Result

| Date       | Tester        | Result |
| ---------- | ------------- | ------ |
| 2026-08-06 | Seo Seokhyeon | PASS   |
