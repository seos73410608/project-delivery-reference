# Windows 개발환경 구축 가이드

- **문서 경로:** `docs/development/environment/002_windows_setup.md`
- **문서 버전:** 1.1
- **작성일:** 2026-11-06
- **기준 환경:** Windows 10 Pro / PowerShell 5.1

---

## 1. 목적

본 문서는 PMIS 개발에 필요한 Windows 기본 환경을 확인하고, 프로젝트 실행을 위한 터미널 및 디렉터리 환경을 준비하는 방법을 설명한다.

기존 PC에 다른 개발 프로젝트가 함께 설치되어 있을 수 있으므로 시스템 전체의 환경변수를 임의로 변경하지 않고, PMIS 프로젝트에 필요한 설정을 프로젝트 단위로 관리하는 것을 원칙으로 한다.

## 2. 운영체제 확인

### 2.1 기준 개발환경

현재 개발 PC에서 확인한 운영체제 정보는 다음과 같다.

| 항목 | 확인 결과 |
|---|---|
| 운영체제 | Windows 10 Pro |
| Windows Version | 2009 |
| OS Build | 19045 |
| PowerShell | 5.1 |

### 2.2 운영체제 확인 명령어

PowerShell에서 다음 명령어를 실행한다.

```powershell
Get-ComputerInfo | Select-Object WindowsProductName, WindowsVersion, OsBuildNumber
```

### 2.3 PowerShell 버전 확인

```powershell
$PSVersionTable.PSVersion
```

PMIS 환경 구축 및 실행 명령은 Windows PowerShell 5.1을 기준으로 작성한다.

※ 신규 PC에서는 보안 업데이트가 지원되는 Windows 버전을 사용하는 것을 권장한다. Windows 버전과 지원 상태는 설치 시점에 별도로 확인한다.

## 3. PowerShell 실행

1. Windows 시작 메뉴를 연다.
2. `PowerShell`을 검색한다.
3. Windows PowerShell을 실행한다.
4. 프로젝트 저장소 루트 디렉터리로 이동한다.

현재 개발 PC의 프로젝트 경로:

```powershell
cd C:\SeoS\project-delivery-reference
```

신규 PC에서는 저장소를 내려받은 실제 경로에 맞게 변경한다.

이후 매뉴얼에서 안내하는 상대 경로 명령은 별도 설명이 없는 한 프로젝트 저장소 루트에서 실행한다.

## 4. 프로젝트 디렉터리 확인

프로젝트 루트에서 다음 명령어를 실행한다.

```powershell
Get-ChildItem
```

주요 디렉터리는 다음과 같다.

| 디렉터리 | 설명 |
|---|---|
| `.vscode` | VS Code 프로젝트 설정 |
| `docs` | 프로젝트 문서 |
| `http` | API 테스트 파일 |
| `pmis` | 기존 프로젝트 영역 |
| `pmis-backend` | Spring Boot 백엔드 |
| `pmis-frontend` | React 프론트엔드 |
| `scripts` | 프로젝트 전용 환경 설정 스크립트 |
| `Tools` | 개발 도구 및 설치 파일 |

## 5. 환경변수 관리 원칙

PMIS는 다른 프로젝트와 Java 및 Node.js 실행환경이 충돌하지 않도록 프로젝트 전용 스크립트를 사용한다.

### 5.1 전역 환경변수 유지

기존 PC의 다음 설정은 임의로 변경하지 않는다.

- 시스템 `JAVA_HOME`
- 사용자 `JAVA_HOME`
- 시스템 `Path`
- 사용자 `Path`

특히 기존 프로젝트가 JDK 17 또는 다른 Node.js 버전을 사용하고 있을 수 있으므로 PMIS 전용 설정을 전역 환경변수에 반영하지 않는다.

### 5.2 PMIS 전용 환경 설정

프로젝트 루트에서 필요한 스크립트를 실행한다.

Java 21 활성화:

```powershell
. .\scripts\use-pmis-java21.ps1
```

Node.js 24 활성화:

```powershell
. .\scripts\use-pmis-node24.ps1
```

각 스크립트는 현재 PowerShell 세션에만 적용된다.

Java와 Node.js 설정은 서로 독립적이므로 필요한 작업에 따라 각각 실행할 수 있다. 백엔드와 프론트엔드를 같은 PowerShell 세션에서 실행하는 경우에는 두 스크립트를 모두 실행해도 된다.

새로운 PowerShell 창을 열면 필요한 스크립트를 다시 실행해야 한다.

## 6. PowerShell 실행 정책 확인

PowerShell에서 다음 명령어로 실행 정책을 확인한다.

```powershell
Get-ExecutionPolicy -List
```

프로젝트 스크립트 실행이 정책에 의해 차단되는 경우에만 현재 세션의 실행 정책을 조정한다.

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

- `Process` 범위는 현재 PowerShell 프로세스에만 적용된다.
- PowerShell 창을 종료하면 해당 설정은 사라진다.
- 시스템 또는 사용자 전체의 실행 정책은 임의로 변경하지 않는다.
- 실행 정책 변경 후에도 스크립트가 차단된다면 오류 메시지와 정책 설정을 확인한다.

## 7. 기본 환경 검증

다음 명령어로 프로젝트 경로와 PowerShell 환경을 확인한다.

```powershell
# 현재 경로
Get-Location

# PowerShell 버전
$PSVersionTable.PSVersion

# 프로젝트 디렉터리
Test-Path .\pmis-backend
Test-Path .\pmis-frontend
Test-Path .\scripts
```

정상적인 프로젝트 저장소라면 위 세 디렉터리 확인 결과가 모두 `True`여야 한다.

## 8. 문제 해결

### 8.1 프로젝트 경로를 찾을 수 없는 경우

현재 위치를 확인한다.

```powershell
Get-Location
```

저장소가 설치된 경로로 이동한다.

```powershell
cd <프로젝트 저장소 경로>
```

### 8.2 PowerShell 스크립트 실행이 차단되는 경우

실행 정책을 확인한다.

```powershell
Get-ExecutionPolicy -List
```

필요한 경우 현재 세션에 한해 실행 정책을 조정한 후 다시 실행한다.

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

### 8.3 환경변수가 예상과 다르게 적용되는 경우

현재 PowerShell 세션에서 프로젝트 루트에 있는지 확인한다.

```powershell
Get-Location
```

그 후 필요한 환경 설정 스크립트를 다시 실행한다.

Java:

```powershell
. .\scripts\use-pmis-java21.ps1
```

Node.js:

```powershell
. .\scripts\use-pmis-node24.ps1
```

새로운 PowerShell 창에서 작업하는 경우에도 동일하게 필요한 스크립트를 실행한다.

---

## 9. 확인 완료 기준

- [ ] Windows 운영체제 정보 확인
- [ ] PowerShell 버전 확인
- [ ] 프로젝트 저장소 루트 접근 확인
- [ ] `pmis-backend` 디렉터리 확인
- [ ] `pmis-frontend` 디렉터리 확인
- [ ] `scripts` 디렉터리 확인
- [ ] 프로젝트 전용 환경변수 관리 방식 확인
- [ ] PowerShell 실행 정책 확인