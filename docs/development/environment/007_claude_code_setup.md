# Claude Code 설치 및 설정 가이드

- **문서 경로:** `docs/development/environment/007_claude_code_setup.md`
- **문서 버전:** 1.1
- **작성일:** 2026-11-06
- **대상 IDE:** Visual Studio Code 1.145.1
- **운영체제:** Windows 10 Pro
- **확장 프로그램:** Claude Code 2.1.318

---

## 1. 문서 목적

본 문서는 PMIS 프로젝트 개발환경에서 Visual Studio Code(VS Code)의 Claude Code 확장 프로그램을 설치하고 설정하는 방법을 설명한다.

Claude Code는 AI 기반 개발 보조 도구로, 프로젝트 소스 코드 분석, 코드 작성, 수정 및 개발 작업 지원 등에 활용할 수 있다.

단, 확장 프로그램 설치와 실제 서비스 사용 권한은 별개이므로 계정 인증 및 사용 가능 여부를 구분해서 확인해야 한다.

주요 내용은 다음과 같다.

- Claude Code 확장 프로그램 설치
- VSIX 설치 파일을 이용한 설치
- 계정 인증 및 서비스 사용 권한 확인
- PMIS 프로젝트에서의 활용 절차
- 설치 파일 및 인증 정보 관리
- AI 개발 도구 사용 시 보안 주의사항

## 2. 현재 PC 설치 환경

### 2.1 확인된 설치 환경

| 항목 | 확인된 환경 |
|---|---|
| 운영체제 | Windows 10 Pro |
| 개발 도구 | Visual Studio Code |
| VS Code 버전 | 1.145.1 |
| 확장 프로그램 | Claude Code |
| 확장 프로그램 ID | `anthropic.claude-code` |
| 설치 버전 | 2.1.318 |
| 설치 파일 | `Tools/claude-code-uncompressed.vsix` |

현재 PC에는 Claude Code 확장 프로그램이 설치되어 있다.

다만 확장 프로그램 설치 여부가 Claude Code 서비스의 실제 사용 권한을 의미하지는 않는다.

### 2.2 설치 파일 보관 목적

프로젝트에서는 다른 PC에서도 동일한 확장 프로그램을 설치할 수 있도록 VSIX 설치 파일을 보관한다.

설치 파일:

```text
Tools/claude-code-uncompressed.vsix
```

파일 크기는 약 151MiB이며, 일반 GitHub 저장소 파일 업로드 제한인 100MB를 초과한다.

따라서 GitHub 일반 Git 커밋 대상으로 취급하지 않고 Git LFS 또는 GitHub Releases 등의 별도 배포 방식을 적용한다.

해당 파일은 재현 가능한 개발환경 구축을 위해 의도적으로 보관하는 설치 자산이다.

## 3. Claude Code 확장 프로그램 설치

### 3.1 VS Code 마켓플레이스에서 설치

신규 PC에서 인터넷 연결이 가능하다면 VS Code Marketplace를 통해 설치할 수 있다.

1. VS Code를 실행한다.
2. 확장 프로그램 메뉴를 연다.
   - 단축키: `Ctrl + Shift + X`
3. 검색창에 `Claude Code`를 입력한다.
4. 게시자가 Anthropic인지 확인한다.
5. Claude Code 확장 프로그램을 설치한다.

확장 프로그램 ID:

```text
anthropic.claude-code
```

공식 확장 프로그램:

[Claude Code - Visual Studio Marketplace](https://marketplace.visualstudio.com/items?itemName=anthropic.claude-code)

### 3.2 VSIX 파일을 이용한 설치

인터넷 연결이 제한되거나 동일한 설치 파일을 재사용해야 하는 경우 VSIX 파일을 이용할 수 있다.

프로젝트에서 보관하는 설치 파일:

```text
Tools/claude-code-uncompressed.vsix
```

설치 방법:

1. VS Code를 실행한다.
2. 확장 프로그램 메뉴를 연다.
3. 확장 프로그램 메뉴의 `...` 버튼을 클릭한다.
4. `Install from VSIX...`를 선택한다.
5. 프로젝트의 `Tools/claude-code-uncompressed.vsix` 파일을 선택한다.
6. 설치가 완료되면 VS Code를 다시 로드한다.

※ VSIX 설치는 확장 프로그램을 로컬에 설치하는 과정이다. 실제 Claude Code 서비스 사용을 위한 네트워크 연결과 계정 인증까지 대체하지는 않는다.

### 3.3 설치 파일 확인

PowerShell에서 프로젝트 루트 기준으로 다음 명령어를 실행한다.

```powershell
Test-Path .\Tools\claude-code-uncompressed.vsix
```

파일 크기 확인:

```powershell
Get-Item .\Tools\claude-code-uncompressed.vsix |
    Select-Object Name, Length
```

설치 파일이 존재하지 않는다면 Git LFS 또는 GitHub Releases 등 프로젝트에서 지정한 배포 경로를 통해 확보한다.

### 3.4 설치 여부 확인

PowerShell에서 다음 명령어를 실행한다.

```powershell
code --list-extensions --show-versions
```

출력 목록에서 다음 항목을 확인한다.

```text
anthropic.claude-code@2.1.318
```

버전은 설치 시점과 업데이트 여부에 따라 달라질 수 있다.

## 4. Claude Code 실행 및 인증

### 4.1 확장 프로그램 실행

1. VS Code에서 PMIS 프로젝트 저장소를 연다.
2. 명령 팔레트를 실행한다.
   - 단축키: `Ctrl + Shift + P`
3. Claude Code 관련 명령을 검색하거나 확장 프로그램 아이콘을 통해 실행한다.
4. 인증 화면이 표시되면 안내에 따라 진행한다.

확장 프로그램의 명령어와 화면 구성은 버전에 따라 달라질 수 있다.

### 4.2 계정 인증과 사용 권한 구분

Claude Code 확장 프로그램을 설치했다고 해서 모든 계정에서 바로 사용할 수 있는 것은 아니다.

실제 사용 가능 여부는 다음 항목에 따라 달라진다.

- Anthropic 계정 인증 상태
- 해당 계정에 부여된 Claude Code 사용 권한
- 선택한 인증 방식 및 서비스 이용 조건
- 계정 또는 API 사용에 필요한 결제 및 이용 설정

현재 PC에서는 다음 상태를 확인했다.

| 항목 | 확인 결과 |
|---|---|
| Claude Code VS Code 확장 프로그램 | 설치 완료 |
| Anthropic 계정 | Free 계정 |
| 구독 기반 Claude Code 사용 권한 | 확인되지 않음 |
| 실제 AI 개발 기능 사용 | 사용 권한 확인 필요 |

따라서 현재 상태를 다음과 같이 구분한다.

1. VS Code 확장 프로그램 설치: 완료
2. 확장 프로그램 실행: 확인 가능
3. Anthropic 계정 인증: 계정별 확인 필요
4. Claude Code 서비스 사용 권한: 현재 계정에서는 확인되지 않음

계정에 사용 권한이 없는 경우 확장 프로그램을 설치했더라도 정상적인 AI 개발 지원 기능을 이용하지 못할 수 있다.

※ 계정의 요금제나 서비스 정책은 변경될 수 있으므로 실제 사용 시 Anthropic 공식 안내와 인증 화면에 표시되는 이용 조건을 확인한다.

공식 사이트:

[Anthropic](https://www.anthropic.com/)

[Claude Code 공식 문서](https://code.claude.com/docs/en/overview)

## 5. PMIS 프로젝트에서 사용하기

Claude Code를 사용할 수 있는 계정과 환경이 준비되면 다음 순서로 진행한다.

1. VS Code에서 PMIS 저장소를 연다.
2. 작업할 브랜치를 확인한다.
3. Claude Code를 실행한다.
4. 작업할 기능과 수정 범위를 구체적으로 설명한다.
5. 변경된 파일과 코드를 검토한다.
6. 빌드 및 테스트를 수행한다.
7. 검증이 완료된 변경 사항만 Git에 커밋한다.

Git 브랜치 운영 기준:

```text
main
 └─ develop
     ├─ feature/*
     ├─ fix/*
     └─ release/*
```

Claude Code가 생성하거나 수정한 코드도 기존 PMIS 개발 절차에 따라 검토하고 테스트한다.

AI가 제안한 변경 사항을 검토 없이 바로 커밋하거나 병합하지 않는다.

### 5.1 작업 요청 시 유의사항

AI 개발 도구에 작업을 요청할 때는 다음 내용을 명확히 전달한다.

- 작업 목적과 요구사항
- 수정 대상 파일 및 디렉터리
- 기존 프로젝트 아키텍처와 개발 규칙
- 변경하지 않아야 하는 범위
- 빌드 및 테스트 기준
- Git 커밋 여부와 브랜치 정책

특히 PMIS는 백엔드와 프론트엔드가 연동되는 프로젝트이므로 API 계약, 데이터 구조 및 기존 공통 처리 방식에 영향을 주는 변경은 별도로 검토한다.

## 6. 설치 및 사용 시 주의사항

### 6.1 계정 및 인증 정보

- Anthropic 계정 인증 정보와 비밀번호를 프로젝트 저장소에 저장하지 않는다.
- API Key를 사용하는 경우 소스 코드나 설정 파일에 직접 기록하지 않는다.
- API Key와 인증 토큰을 Git에 커밋하지 않는다.
- 인증 정보가 포함된 화면이나 로그를 외부에 공유하지 않는다.
- 공용 PC에서는 작업 종료 후 계정 인증 상태와 접근 권한을 확인한다.

### 6.2 프로젝트 코드 및 데이터 보호

- Claude Code가 수정한 파일을 직접 확인한다.
- 기존 프로젝트 아키텍처와 개발 규칙을 유지한다.
- 빌드 및 테스트 결과를 확인한 후 커밋한다.
- 기존 코드와 설정을 임의로 삭제하거나 변경하지 않도록 작업 범위를 명확히 전달한다.
- 운영환경의 비밀번호, API Key, 개인정보 및 실제 업무 데이터를 AI 프롬프트에 포함하지 않는다.
- 외부 서비스에 전달해도 되는 정보인지 확인한 후 프로젝트 자료를 제공한다.

### 6.3 설치 파일 관리

Claude Code VSIX 파일은 다른 PC에서 동일한 확장 프로그램을 설치할 수 있도록 보관하는 설치 자산이다.

현재 파일은 약 151MiB로 GitHub 일반 파일 업로드 제한을 초과하므로 별도 배포 방식을 적용해야 한다.

가능한 배포 방식:

| 방식 | 설명 |
|---|---|
| Git LFS | 대용량 파일을 별도 저장소 방식으로 관리 |
| GitHub Releases | 버전별 설치 파일 배포 |
| 별도 파일 배포 위치 | 프로젝트 외부에서 설치 파일 관리 |

Git LFS를 사용하는 경우 저장 용량 및 대역폭 제한을 확인한다.

GitHub Releases 또는 별도 배포 위치를 사용하는 경우 신규 PC에서도 설치 파일을 내려받을 수 있도록 배포 경로와 버전을 문서화한다.

설치 파일을 단순히 로컬 PC에만 보관하고 저장소에는 경로만 남기는 방식으로는 다른 PC에서 재현 가능한 설치가 보장되지 않는다.

## 7. 설치 확인 체크리스트

- [ ] VS Code 설치 및 실행 확인
- [ ] Claude Code 확장 프로그램 설치 확인
- [ ] 확장 프로그램 ID 확인
- [ ] 설치 버전 확인
- [ ] VSIX 설치 파일 확보 및 설치 확인
- [ ] Claude Code 실행 확인
- [ ] 계정 인증 상태 확인
- [ ] 서비스 사용 권한 확인
- [ ] PMIS 프로젝트 저장소에서 실행 확인
- [ ] 인증 정보 및 API Key의 저장소 미포함 확인
- [ ] 신규 PC에서 설치 파일을 확보할 수 있는 배포 경로 확인

---

## 8. 참고 경로

| 구분 | 경로 |
|---|---|
| 프로젝트 저장소 | `C:\SeoS\project-delivery-reference` |
| VS Code 확장 프로그램 설치 파일 | `Tools/claude-code-uncompressed.vsix` |
| VS Code 확장 프로그램 ID | `anthropic.claude-code` |
| VS Code Marketplace | https://marketplace.visualstudio.com/items?itemName=anthropic.claude-code |
| Claude Code 공식 문서 | https://code.claude.com/docs/en/overview |

---

**관리 원칙:** Claude Code 확장 프로그램 설치와 서비스 사용 권한을 별도로 관리한다. VSIX 파일은 재현 가능한 개발환경 구축을 위한 설치 자산으로 유지하며, GitHub 일반 파일 제한을 고려하여 별도 배포 방식을 적용한다. AI가 생성하거나 수정한 코드는 기존 PMIS 개발 절차에 따라 검토하고 테스트한 후 반영한다.