# MariaDB 데이터베이스 구축 가이드

- **문서 경로:** `docs/development/environment/005_database_setup.md`
- **문서 버전:** 1.2
- **작성일:** 2026-11-06
- **기존 개발 PC 기준 데이터베이스:** MariaDB 10.6.5
- **데이터베이스명:** `pmis`
- **기본 접속 포트:** 3306

---

## 1. 목적

본 문서는 PMIS 백엔드에서 사용하는 MariaDB 데이터베이스 환경을 구축하고 연결하는 방법을 설명한다.

주요 내용은 다음과 같다.

- MariaDB 설치 및 버전 확인
- Windows 데이터베이스 서비스 확인
- PMIS 데이터베이스 생성
- 데이터베이스 계정 및 권한 설정
- Spring Boot 데이터베이스 연결 설정
- JPA를 이용한 테이블 자동 생성
- 데이터베이스 백업 및 복원
- 데이터베이스 연결 문제 해결

## 2. 데이터베이스 환경

### 2.1 기존 개발 PC 설치 현황

| 항목 | 확인 결과 |
|---|---|
| 데이터베이스 | MariaDB |
| 버전 | 10.6.5 |
| 설치 경로 | `C:\Program Files\MariaDB 10.6` |
| CLI 실행 파일 | `bin\mariadb.exe` |
| 데이터베이스명 | `pmis` |
| 접속 주소 | `localhost` |
| 접속 포트 | 3306 |
| 접속 계정 | root |

기존 개발 PC에서는 MariaDB와 MySQL Windows 서비스가 모두 실행 중인 것을 확인했다.

※ 두 서비스가 동시에 실행 중인 상태이므로 신규 PC에서 임의로 서비스를 중지하거나 포트를 변경하지 않는다. 실제 서비스 구성과 포트 사용 여부를 확인한 후 진행한다.

### 2.2 MariaDB 버전 확인

PowerShell에서 다음 명령어를 실행한다.

```powershell
& "C:\Program Files\MariaDB 10.6\bin\mariadb.exe" --version
```

기존 개발 PC에서 확인한 결과:

```text
Ver 15.1 Distrib 10.6.5-MariaDB, for Win64 (AMD64)
```

### 2.3 신규 PC 버전 선정 기준

기존 개발 PC와 동일한 환경을 재현하는 것이 기본 원칙이다.

다만 MariaDB Community Server 10.6 계열은 2026년 7월 6일 유지보수가 종료되었다. 따라서 신규 PC에서는 지원 중인 LTS 버전을 우선 검토한다.

공식 유지보수 정책:

- [MariaDB 10.6 변경 사항 및 유지보수 기간](https://mariadb.com/docs/release-notes/community-server/10.6/what-is-mariadb-106)
- [MariaDB 공식 다운로드](https://mariadb.org/download/)

신규 버전을 사용할 경우 다음 항목을 검증한다.

- MariaDB JDBC Driver 연결
- Spring Boot 데이터베이스 연결
- JPA 테이블 자동 생성
- 주요 SQL 실행
- 데이터 조회 및 저장
- 백업 및 복원

버전 변경은 호환성 검증 후 적용한다.

## 3. MariaDB 설치

### 3.1 설치 파일 확보

신규 PC에 MariaDB가 설치되어 있지 않다면 공식 배포처에서 Windows용 설치 파일을 확보한다.

공식 사이트:

[MariaDB 공식 다운로드](https://mariadb.org/download/)

설치 시 다음 사항을 확인한다.

1. Windows x64 설치 파일 선택
2. MariaDB 서버 설치
3. 관리자 계정 비밀번호 설정
4. 기본 포트 3306 확인
5. Windows 서비스 등록 여부 확인
6. 설치 완료 후 서비스 실행 상태 확인

### 3.2 설치 경로 확인

기존 개발 PC의 설치 경로는 다음과 같다.

```text
C:\Program Files\MariaDB 10.6
```

신규 PC에서는 실제 설치 경로를 확인하고 이후 CLI 명령어에 반영한다.

## 4. Windows 서비스 확인

PowerShell에서 다음 명령어를 실행한다.

```powershell
Get-Service | Where-Object {
    $_.Name -match "MariaDB|MySQL"
}
```

기존 개발 PC에서는 다음 서비스가 실행 중이다.

| 서비스 | 상태 |
|---|---|
| MariaDB | Running |
| MySQL | Running |

신규 PC에서는 설치한 MariaDB 서비스가 `Running` 상태인지 확인한다.

서비스가 실행 중이지 않다면 서비스명과 오류 로그를 확인한 후 원인을 해결한다.

### 4.1 포트 사용 여부 확인

PowerShell에서 다음 명령어를 실행한다.

```powershell
Get-NetTCPConnection -LocalPort 3306 -State Listen
```

3306 포트를 이미 다른 데이터베이스 서비스가 사용하고 있다면 해당 서비스와 포트 구성을 확인한다.

기존 서비스의 설정을 확인하지 않고 임의로 중지하거나 포트를 변경하지 않는다.

## 5. 데이터베이스 접속

### 5.1 MariaDB CLI 실행

기존 개발 PC의 실행 파일 경로:

```text
C:\Program Files\MariaDB 10.6\bin\mariadb.exe
```

PowerShell에서 다음 명령어를 실행한다.

```powershell
& "C:\Program Files\MariaDB 10.6\bin\mariadb.exe" -u root -p
```

비밀번호 입력 프롬프트가 나타나면 설치 시 설정한 관리자 비밀번호를 입력한다.

비밀번호는 화면에 표시되지 않는 것이 정상이다.

### 5.2 서버 버전 및 데이터베이스 확인

MariaDB 접속 후 다음 SQL을 실행한다.

```sql
SELECT VERSION();

SHOW DATABASES;
```

기존 개발 PC에서는 MariaDB 10.6.5 서버와 `pmis` 데이터베이스가 존재하는 것을 확인했다.

## 6. PMIS 데이터베이스 생성

### 6.1 데이터베이스 존재 여부 확인

```sql
SHOW DATABASES LIKE 'pmis';
```

이미 `pmis` 데이터베이스가 존재하면 기존 데이터베이스를 재생성하지 않는다.

### 6.2 신규 데이터베이스 생성

신규 PC에서 데이터베이스가 없는 경우 다음 SQL을 실행한다.

```sql
CREATE DATABASE pmis
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

위 설정은 신규 구축 시 사용하는 문자 인코딩 설정이다.

### 6.3 데이터베이스 문자셋 확인

```sql
SELECT
    SCHEMA_NAME,
    DEFAULT_CHARACTER_SET_NAME,
    DEFAULT_COLLATION_NAME
FROM information_schema.SCHEMATA
WHERE SCHEMA_NAME = 'pmis';
```

기존 데이터베이스를 사용하는 경우에는 문자셋과 Collation을 먼저 확인한다.

## 7. PMIS 데이터베이스 계정 및 권한

### 7.1 기존 개발 PC 설정

현재 백엔드의 데이터베이스 연결 설정은 다음과 같다.

| 항목 | 현재 설정 |
|---|---|
| 데이터베이스 | `pmis` |
| 사용자 | `root` |
| 드라이버 | MariaDB JDBC Driver |
| JDBC URL | `jdbc:mariadb://localhost:3306/pmis` |

현재 설정은 기존 개발 PC의 실제 구성을 기록한 것이다.

### 7.2 신규 PC 권장 설정

신규 PC에서는 root 계정을 애플리케이션 연결에 직접 사용하지 않는 것을 권장한다.

PMIS 전용 데이터베이스 계정을 생성하고 필요한 권한을 부여한다.

먼저 계정 존재 여부를 확인한다.

```sql
SELECT User, Host
FROM mysql.user
WHERE User = 'pmis_app';
```

계정이 없는 경우 다음 SQL을 실행한다.

```sql
CREATE USER 'pmis_app'@'localhost'
IDENTIFIED BY '별도의 안전한 비밀번호';
```

이후 PMIS 데이터베이스에 대한 권한을 부여한다.

```sql
GRANT ALL PRIVILEGES ON pmis.*
TO 'pmis_app'@'localhost';
```

위 SQL의 `별도의 안전한 비밀번호`는 실제 사용할 비밀번호로 대체한다.

`ALL PRIVILEGES`는 개발환경에서 JPA의 테이블 생성 및 변경 작업까지 허용하기 위한 설정이다.

운영환경에서는 마이그레이션 계정과 애플리케이션 실행 계정을 분리하고 필요한 권한만 부여한다.

### 7.3 권한 확인

```sql
SHOW GRANTS FOR 'pmis_app'@'localhost';
```

이미 계정이 존재한다면 무조건 다시 생성하지 말고 기존 권한과 비밀번호 설정을 확인한다.

※ 사용자 생성 및 권한 부여 작업은 해당 작업을 수행할 수 있는 관리자 계정으로 접속해야 한다.

## 8. Spring Boot 데이터베이스 연결 설정

### 8.1 설정 파일 위치

```text
pmis-backend/
└── src/
    └── main/
        └── resources/
            └── application.yaml
```

### 8.2 기존 개발 PC 설정

현재 `application.yaml`에는 다음 연결 정보가 설정되어 있다.

- JDBC URL: `jdbc:mariadb://localhost:3306/pmis`
- 사용자 계정: `root`
- 드라이버: `org.mariadb.jdbc.Driver`

현재 설정에는 데이터베이스 비밀번호가 포함되어 있으므로 문서나 Git 저장소에 실제 비밀번호를 기록하지 않는다.

### 8.3 신규 PC 권장 환경변수 설정

신규 PC에서는 비밀번호를 설정 파일에 직접 기록하지 않고 Spring Boot 환경변수로 전달하는 방식을 권장한다.

`application.yaml`의 권장 구성 예시:

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:mariadb://localhost:3306/pmis}
    username: ${SPRING_DATASOURCE_USERNAME:pmis_app}
    password: ${SPRING_DATASOURCE_PASSWORD}
    driver-class-name: org.mariadb.jdbc.Driver
```

PowerShell에서 현재 세션에 환경변수를 설정하는 예시:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:mariadb://localhost:3306/pmis"
$env:SPRING_DATASOURCE_USERNAME = "pmis_app"
$env:SPRING_DATASOURCE_PASSWORD = "실제 비밀번호"
```

이 설정은 현재 PowerShell 세션에만 적용된다.

환경변수 설정 후 같은 PowerShell 세션에서 백엔드를 실행해야 한다.

※ 위 YAML은 보안 개선을 위한 권장 예시이며, 현재 프로젝트의 실제 설정을 변경한 결과를 의미하지 않는다. 적용 시 기존 설정과의 중복 여부를 확인하고 별도 변경 작업으로 관리한다.

※ `SPRING_DATASOURCE_PASSWORD`는 기본값이 없으므로 환경변수를 설정하지 않으면 데이터베이스 연결이 실패할 수 있다.

## 9. PMIS 테이블 자동 생성 및 확인

### 9.1 테이블 자동 생성 방식

현재 PMIS 백엔드는 Spring Boot와 JPA를 사용하며, 데이터베이스에 연결한 후 백엔드를 실행하면 프로젝트의 JPA 설정에 따라 테이블이 자동 생성되는 구조다.

따라서 신규 PC에서 PMIS 데이터베이스를 생성하고 백엔드를 실행하면 테이블 생성 절차가 진행된다.

신규 PC 구축 시 별도의 SQL을 이용해 16개 테이블을 직접 생성할 필요는 없다.

다만 테이블 자동 생성 여부는 실제 프로젝트의 JPA DDL 설정, Entity 매핑 및 데이터베이스 계정 권한에 영향을 받는다.

### 9.2 PMIS 데이터베이스 접속

기존 개발 PC의 MariaDB CLI 경로를 기준으로 다음 명령어를 실행한다.

신규 PC에서 `pmis_app` 계정을 생성한 경우:

```powershell
& "C:\Program Files\MariaDB 10.6\bin\mariadb.exe" `
    -u pmis_app -p pmis
```

현재 개발 PC의 기존 root 계정을 사용하는 경우:

```powershell
& "C:\Program Files\MariaDB 10.6\bin\mariadb.exe" `
    -u root -p pmis
```

MariaDB CLI가 다른 경로에 설치되어 있다면 실제 설치 경로로 변경한다.

### 9.3 테이블 확인

MariaDB 접속 후 다음 SQL을 실행한다.

```sql
SHOW TABLES;
```

기존 개발 PC에서 확인한 PMIS 테이블은 총 16개다.

| 번호 | 테이블 |
|---:|---|
| 1 | changes |
| 2 | evidence |
| 3 | evidence_ocr_job |
| 4 | evidence_requirements |
| 5 | evidence_verification_history |
| 6 | evidence_version |
| 7 | inspection_items |
| 8 | inspections |
| 9 | issues |
| 10 | projects |
| 11 | refresh_tokens |
| 12 | risks |
| 13 | schedules |
| 14 | tasks |
| 15 | users |
| 16 | wbs |

※ 위 목록은 2026-11-06 현재 개발 PC에서 확인한 결과다. 신규 PC에서는 프로젝트의 Entity 및 JPA 설정에 따라 테이블 구성이 달라질 수 있다.

### 9.4 신규 PC 초기 구축 순서

신규 PC에서는 다음 순서로 진행한다.

1. MariaDB 설치 및 서비스 실행 확인
2. `pmis` 데이터베이스 생성
3. PMIS 전용 계정 생성 및 권한 부여
4. Spring Boot 데이터베이스 연결 설정
5. PMIS 백엔드 실행
6. 백엔드 실행 로그에서 데이터베이스 연결 및 JPA 초기화 확인
7. MariaDB에서 `SHOW TABLES;` 실행
8. 테이블 자동 생성 여부 확인

## 10. 데이터베이스 백업 및 복원

### 10.1 백업

MariaDB의 `mariadb-dump` 명령어를 사용한다.

기존 개발 PC에서 root 계정을 사용하는 경우:

```powershell
& "C:\Program Files\MariaDB 10.6\bin\mariadb-dump.exe" `
    -u root -p `
    --result-file="pmis_backup.sql" `
    pmis
```

신규 PC에서 `pmis_app` 계정을 생성하고 해당 계정으로 백업을 수행하는 경우:

```powershell
& "C:\Program Files\MariaDB 10.6\bin\mariadb-dump.exe" `
    -u pmis_app -p `
    --result-file="pmis_backup.sql" `
    pmis
```

MariaDB CLI 도구의 설치 경로가 다르면 실제 설치 경로로 변경한다.

비밀번호 입력 후 백업이 진행된다.

`pmis_backup.sql` 파일이 생성되었는지 확인한다.

### 10.2 복원

먼저 복원 대상 데이터베이스가 존재하는지 확인한다.

```sql
SHOW DATABASES LIKE 'pmis';
```

복원 대상 데이터베이스가 없다면 6장의 데이터베이스 생성 절차를 진행한다.

현재 개발 PC의 root 계정을 사용하는 경우:

```powershell
& "C:\Program Files\MariaDB 10.6\bin\mariadb.exe" `
    -u root -p pmis
```

신규 PC의 `pmis_app` 계정을 사용하는 경우:

```powershell
& "C:\Program Files\MariaDB 10.6\bin\mariadb.exe" `
    -u pmis_app -p pmis
```

MariaDB 접속 후 `SOURCE` 명령어로 백업 파일을 복원한다.

```sql
SOURCE C:/backup/pmis_backup.sql;
```

위 경로는 예시이므로 실제 백업 파일 경로로 변경한다.

### 10.3 백업 및 복원 시 확인 사항

- 백업 파일 생성 여부
- 백업 파일 크기
- 복원 대상 데이터베이스
- 계정 및 권한
- 테이블과 데이터 건수
- 복원 후 데이터 조회 가능 여부

실제 데이터가 포함된 백업 파일은 Git 저장소에 무분별하게 커밋하지 않는다.

## 11. 문제 해결

### 11.1 데이터베이스 접속 실패

오류 예시:

```text
Connection refused
```

확인 사항:

1. MariaDB 서비스 실행 여부
2. 접속 주소 및 포트
3. 방화벽 및 포트 접근 여부
4. JDBC URL 설정

### 11.2 Access denied 오류

오류 예시:

```text
Access denied for user
```

확인 사항:

1. 사용자 계정 존재 여부
2. 비밀번호 일치 여부
3. 접속 호스트와 계정의 일치 여부
4. 데이터베이스 접근 권한

### 11.3 데이터베이스가 없는 경우

다음 명령어로 확인한다.

```sql
SHOW DATABASES LIKE 'pmis';
```

없다면 6장의 데이터베이스 생성 절차를 진행한다.

### 11.4 테이블이 생성되지 않는 경우

다음 항목을 확인한다.

1. Spring Boot 실행 로그
2. 데이터베이스 연결 성공 여부
3. JPA DDL 설정
4. 데이터베이스 연결 계정 권한
5. Entity 매핑 및 초기화 설정

테이블 자동 생성이 진행되지 않는 경우에는 실제 프로젝트의 JPA 설정과 실행 로그를 확인한다.

### 11.5 3306 포트 충돌

오류가 발생하면 다음 명령어로 포트 사용 상태를 확인한다.

```powershell
Get-NetTCPConnection -LocalPort 3306 -State Listen
```

이미 다른 서비스가 포트를 사용 중이라면 해당 서비스의 구성과 데이터베이스 연결 상태를 확인한다.

기존 데이터베이스 서비스의 설정을 확인하지 않고 임의로 중지하거나 포트를 변경하지 않는다.

## 12. 확인 완료 기준

- [ ] MariaDB 설치 확인
- [ ] MariaDB 서비스 실행 확인
- [ ] MariaDB 버전 확인
- [ ] 데이터베이스 접속 확인
- [ ] `pmis` 데이터베이스 존재 확인
- [ ] 데이터베이스 문자셋 및 Collation 확인
- [ ] PMIS 전용 계정 생성 및 권한 확인
- [ ] Spring Boot 데이터베이스 연결 설정 확인
- [ ] 백엔드에서 데이터베이스 연결 성공
- [ ] JPA 테이블 자동 생성 확인
- [ ] PMIS 테이블 조회 확인
- [ ] 데이터베이스 백업 및 복원 방법 확인

---

**보안 원칙:** 데이터베이스 비밀번호는 소스코드, 문서, Git 커밋에 평문으로 포함하지 않는다. 신규 PC에서는 PMIS 전용 계정을 사용하고 환경변수 또는 별도의 안전한 비밀정보 관리 방식을 적용한다.