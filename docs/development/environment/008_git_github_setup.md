# Git 및 GitHub 설정 가이드

- **문서 경로:** `docs/development/environment/008_git_github_setup.md`
- **문서 버전:** 1.1
- **작성일:** 2026-11-06
- **운영체제:** Windows 10 Pro
- **터미널:** PowerShell 5.1
- **Git 버전:** 2.34.0.windows.1
- **GitHub 연결 방식:** SSH

---

## 1. 문서 목적

본 문서는 PMIS 프로젝트 개발을 위한 Git 설치, GitHub 계정 연결, SSH 인증 및 저장소 관리 방법을 설명한다.

현재 개발 환경에서는 GitHub 계정 2개를 사용하므로 SSH Host 별칭을 이용해 계정별 인증 키를 구분한다.

PMIS 저장소는 포트폴리오용 GitHub 계정을 사용한다.

주요 내용은 다음과 같다.

- Git 설치 및 사용자 정보 설정
- GitHub 계정별 SSH 키 관리
- SSH 인증 및 연결 확인
- PMIS 저장소 복제 및 원격 저장소 설정
- Git Flow 기반 브랜치 운영
- 커밋 메시지 규칙
- Git LFS 및 대용량 설치 파일 관리
- Git 및 GitHub 보안 관리

## 2. 현재 PC 설치 환경

| 항목 | 확인된 환경 |
|---|---|
| 운영체제 | Windows 10 Pro |
| 터미널 | PowerShell 5.1 |
| Git | 2.34.0.windows.1 |
| GitHub 연결 방식 | SSH |
| PMIS 저장소 | `seos73410608/project-delivery-reference` |
| PMIS 원격 저장소 별칭 | `github-portfolio` |

### 2.1 Git 설치 확인

PowerShell에서 다음 명령어를 실행한다.

```powershell
git --version
```

현재 개발 PC에서 확인한 결과:

```text
git version 2.34.0.windows.1
```

Git이 설치되어 있지 않다면 Git 공식 사이트에서 Windows용 설치 프로그램을 내려받아 설치한다.

- 공식 사이트: https://git-scm.com/downloads

설치 후 PowerShell을 다시 실행하고 버전을 확인한다.

## 3. Git 기본 사용자 정보 설정

Git 커밋에 기록될 사용자 이름과 이메일을 설정한다.

### 3.1 현재 PMIS 저장소 설정 확인

PMIS 저장소 루트에서 다음 명령어를 실행한다.

```powershell
git config --local user.name
git config --local user.email
```

현재 개발 PC에서 확인된 값:

```text
user.name=seos73410608
user.email=seos73410608@gmail.com
```

### 3.2 신규 PC 설정

새 PC에서 PMIS 저장소를 복제한 후 다음 명령어로 설정할 수 있다.

```powershell
git config --local user.name "seos73410608"
git config --local user.email "seos73410608@gmail.com"
```

설정 확인:

```powershell
git config --local --list
```

`--local` 옵션은 현재 저장소에만 설정을 적용한다. 다른 프로젝트의 Git 사용자 정보에 영향을 주지 않는다.

## 4. GitHub 계정 2개를 위한 SSH 설정

### 4.1 계정별 SSH 키 구성

현재 PC에서는 다음과 같이 계정별 SSH 키를 분리해 사용한다.

| 구분 | SSH Host 별칭 | 키 파일 |
|---|---|---|
| 개인 GitHub 계정 | `github-personal` | `~/.ssh/id_rsa` |
| PMIS 포트폴리오 계정 | `github-portfolio` | `~/.ssh/id_ed25519_pmis` |

SSH 개인 키 파일은 외부에 공개하거나 GitHub 저장소에 업로드하지 않는다.

새 PC에서는 기존 개인 키를 안전하게 이전하거나 새로운 SSH 키를 생성해 각 GitHub 계정에 등록해야 한다.

기존 개인 키를 이전하는 경우에도 개인 키 파일과 암호를 안전하게 관리한다. 개인 키를 메신저, 이메일 또는 GitHub 저장소에 올리지 않는다.

### 4.2 SSH 키 생성

새 PC에서 SSH 키가 없다면 PowerShell에서 계정별로 다음 명령어를 실행한다.

개인 계정용:

```powershell
ssh-keygen -t rsa -b 4096 `
    -C "개인 GitHub 계정 이메일" `
    -f "$env:USERPROFILE\.ssh\id_rsa"
```

PMIS 포트폴리오 계정용:

```powershell
ssh-keygen -t ed25519 `
    -C "PMIS 포트폴리오 GitHub 계정 이메일" `
    -f "$env:USERPROFILE\.ssh\id_ed25519_pmis"
```

키 생성 과정에서 개인 키 암호를 설정할 수 있다.

기존 키 파일이 이미 존재한다면 덮어쓰지 않도록 주의한다.

기존 키를 사용하고 있다면 새로 생성하지 않고 기존 키의 안전한 이전 여부를 먼저 확인한다.

### 4.3 공개 키를 GitHub에 등록

각 키의 공개 키 파일(`.pub`) 내용을 확인한다.

```powershell
Get-Content "$env:USERPROFILE\.ssh\id_rsa.pub"
Get-Content "$env:USERPROFILE\.ssh\id_ed25519_pmis.pub"
```

출력된 공개 키를 해당 GitHub 계정에 등록한다.

GitHub 메뉴:

1. GitHub 로그인
2. Settings
3. SSH and GPG keys
4. New SSH key
5. 공개 키 등록

공개 키만 등록한다. `.pub` 확장자가 없는 개인 키는 절대 공유하거나 등록하지 않는다.

공식 안내:

[GitHub SSH 연결 공식 문서](https://docs.github.com/en/authentication/connecting-to-github-with-ssh)

### 4.4 SSH config 설정

다음 경로에 `config` 파일을 생성하거나 수정한다.

```text
C:\Users\<사용자계정>\.ssh\config
```

현재 PC에서 사용 중인 설정:

```sshconfig
# Personal GitHub account
Host github-personal
    HostName github.com
    User git
    IdentityFile ~/.ssh/id_rsa
    IdentitiesOnly yes

# PMIS portfolio GitHub account
Host github-portfolio
    HostName github.com
    User git
    IdentityFile ~/.ssh/id_ed25519_pmis
    IdentitiesOnly yes
```

각 설정의 의미는 다음과 같다.

| 항목 | 설명 |
|---|---|
| `Host` | Git 명령어에서 사용할 SSH 별칭 |
| `HostName` | 실제 접속할 서버 |
| `User` | SSH 접속 사용자 |
| `IdentityFile` | 계정별 SSH 개인 키 경로 |
| `IdentitiesOnly` | 지정한 키를 우선 사용하도록 제한 |

새 PC에서 키 파일명을 다르게 사용한다면 `IdentityFile` 경로도 함께 변경한다.

※ `~/.ssh/config`는 SSH가 사용하는 설정 파일이며, 파일명에 확장자를 붙이지 않는다.

## 5. SSH 인증 확인

계정별로 SSH 연결을 확인한다.

개인 계정:

```powershell
ssh -T git@github-personal
```

PMIS 포트폴리오 계정:

```powershell
ssh -T git@github-portfolio
```

정상적으로 인증되면 다음과 유사한 메시지가 표시된다.

```text
Hi seos73410608! You've successfully authenticated, but GitHub does not provide shell access.
```

GitHub는 SSH 인증 성공 후에도 셸 접속 기능을 제공하지 않으므로 위 메시지는 정상적인 인증 결과다.

최초 연결 시 호스트 신뢰 여부를 확인하는 메시지가 표시될 수 있다.

이때는 접속 대상이 `github.com`인지 확인하고 GitHub 공식 문서에서 안내하는 SSH 호스트 키 지문과 비교한 후 진행한다.

SSH 개인 키에 암호를 설정했다면 인증 과정에서 암호 입력을 요청할 수 있다. 개인 키 암호를 코드나 설정 파일에 기록하지 않는다.

## 6. PMIS 저장소 연결

### 6.1 저장소 복제

PMIS 저장소:

```text
seos73410608/project-delivery-reference
```

복제 명령어:

```powershell
git clone git@github-portfolio:seos73410608/project-delivery-reference.git
```

위 명령어에서 `github-portfolio`는 SSH config에 정의한 Host 별칭이다.

복제 후 저장소 디렉터리로 이동한다.

```powershell
cd .\project-delivery-reference
```

### 6.2 원격 저장소 확인

```powershell
git remote -v
```

정상적인 설정 예시:

```text
origin  git@github-portfolio:seos73410608/project-delivery-reference.git (fetch)
origin  git@github-portfolio:seos73410608/project-delivery-reference.git (push)
```

원격 저장소 주소에 `github-portfolio`가 포함되어 있는지 확인한다.

### 6.3 원격 저장소 주소 변경

기존 저장소의 원격 주소가 HTTPS 또는 다른 SSH 주소로 설정되어 있다면 다음 명령어로 변경할 수 있다.

```powershell
git remote set-url origin git@github-portfolio:seos73410608/project-delivery-reference.git
```

변경 후 `git remote -v`로 확인한다.

## 7. Git 브랜치 운영 전략

PMIS 프로젝트는 Git Flow 방식으로 브랜치를 관리한다.

| 브랜치 | 용도 |
|---|---|
| `main` | 안정화된 기준 버전 |
| `develop` | 개발 통합 브랜치 |
| `feature/*` | 기능 개발 |
| `fix/*` | 버그 수정 |
| `release/*` | 배포 준비 |

일반적인 기능 개발 흐름:

```text
main
 └─ develop
     ├─ feature/*
     ├─ fix/*
     └─ release/*
```

### 7.1 작업 시작 전 최신 develop 반영

PMIS 저장소 루트에서 다음 명령어를 실행한다.

```powershell
git switch develop
git pull origin develop
```

### 7.2 기능 브랜치 생성

```powershell
git switch -c feature/기능명
```

예시:

```powershell
git switch -c feature/development-environment-setup
```

### 7.3 작업 후 변경 사항 확인

```powershell
git status
git diff
```

변경 파일과 내용을 확인한 후 커밋을 진행한다.

작업이 완료되면 원격 저장소에 브랜치를 푸시하고 Pull Request를 통해 `develop`에 병합한다.

기능 브랜치를 직접 `main`에 병합하지 않는다.

## 8. 커밋 메시지 규칙

PMIS 프로젝트는 다음 커밋 접두사를 사용한다.

| 접두사 | 용도 |
|---|---|
| `feat` | 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 코드 구조 개선 |
| `style` | 코드 스타일 변경 |
| `test` | 테스트 추가 및 수정 |
| `docs` | 문서 변경 |
| `build` | 빌드 및 의존성 설정 |
| `chore` | 기타 유지보수 |
| `perf` | 성능 개선 |
| `security` | 보안 관련 변경 |

커밋 예시:

```powershell
git add docs/development/environment/
git status
git commit -m "docs: add development environment setup guide"
```

원격 저장소에 브랜치를 푸시한다.

```powershell
git push -u origin feature/브랜치명
```

※ `git add .`를 사용할 수도 있지만, 커밋 전에 `git status`로 변경 파일을 확인하고 비밀번호, 인증 정보, 개인 키 및 불필요한 파일이 포함되지 않았는지 검토한다.

## 9. Git LFS 및 대용량 설치 파일 관리

### 9.1 대용량 파일 관리 필요성

GitHub 일반 Git 저장소에는 파일당 100MB 제한이 있다.

PMIS 개발환경에서는 다음과 같은 대용량 설치 자산을 관리한다.

| 파일 또는 자산 | 경로 | 관리 목적 |
|---|---|---|
| Node.js 24 설치 패키지 | `Tools/node-v24.19.0-win-x64` | PMIS Node.js 환경 재현 |
| Claude Code VSIX | `Tools/claude-code-uncompressed.vsix` | VS Code 확장 프로그램 재설치 |

특히 Claude Code VSIX 파일은 약 151MiB로 일반 Git 파일 업로드 제한을 초과한다.

따라서 해당 파일을 일반 Git 객체로 직접 커밋하지 않는다.

### 9.2 Git LFS 설치 및 확인

Git LFS는 대용량 파일을 별도 저장 방식으로 관리하는 Git 확장 도구다.

공식 사이트:

[Git LFS](https://git-lfs.com/)

설치 후 PowerShell에서 다음 명령어를 실행한다.

```powershell
git lfs version
```

PMIS 저장소에서 Git LFS를 초기화한다.

```powershell
git lfs install
```

### 9.3 대용량 파일 추적 설정

Git LFS로 관리할 파일은 실제 파일을 Git에 추가하기 전에 추적 규칙을 설정해야 한다.

예시:

```powershell
git lfs track "*.vsix"
```

추적 설정이 완료되면 저장소 루트에 `.gitattributes` 파일이 생성되거나 변경된다.

설정 확인:

```powershell
git lfs track
git status
```

`.gitattributes`는 Git에 커밋하여 다른 PC에서도 동일한 추적 규칙을 적용하도록 관리한다.

※ Node.js 설치 패키지는 압축 파일인지 압축 해제된 디렉터리인지에 따라 관리 방식이 달라진다. 개별 파일 크기를 확인하고 100MB를 초과하는 파일만 Git LFS 또는 별도 배포 경로로 관리한다.

### 9.4 신규 PC에서 LFS 파일 확보

신규 PC에서 Git LFS가 설치되어 있지 않다면 먼저 설치한다.

저장소 복제 후 다음 명령어를 실행한다.

```powershell
git lfs install
git lfs pull
```

Git LFS 파일이 정상적으로 내려받아졌는지 확인한다.

Git LFS 저장 용량과 다운로드 대역폭 제한도 함께 확인한다.

### 9.5 GitHub Releases를 이용한 배포

Git LFS 대신 GitHub Releases를 이용해 설치 파일을 배포할 수도 있다.

이 경우 다음 사항을 관리한다.

- 설치 파일 버전
- 배포 파일명
- 다운로드 위치
- 신규 PC 설치 방법
- 파일 무결성 확인 방법

GitHub Releases에 등록한 파일은 일반적인 저장소 복제만으로 자동 다운로드되지 않는다.

따라서 신규 PC 구축 가이드에 별도의 다운로드 절차를 기록해야 한다.

※ 대용량 파일이 이미 일반 Git 커밋에 포함되어 GitHub 업로드가 거부된 경우 단순히 `.gitignore`에 추가하는 것만으로 기존 Git 이력이 제거되지는 않는다. 기존 이력 정리가 필요한 경우 별도의 Git 이력 정리 작업을 수행한다.

## 10. 보안 및 주의사항

- SSH 개인 키는 GitHub 저장소에 커밋하지 않는다.
- SSH 공개 키와 개인 키를 혼동하지 않는다.
- 계정 비밀번호, SSH 개인 키 암호, API Key를 문서나 소스 코드에 기록하지 않는다.
- 계정별 SSH Host 별칭을 사용해 인증 계정을 명확히 구분한다.
- PMIS 저장소의 원격 주소는 `github-portfolio` 별칭을 사용한다.
- `git config --local`을 우선 사용해 프로젝트별 커밋 정보를 관리한다.
- `git push` 전에 현재 브랜치와 원격 저장소 주소를 확인한다.
- 실제 데이터베이스 백업 파일과 개인정보가 포함된 파일을 저장소에 커밋하지 않는다.
- 대용량 설치 파일은 Git LFS 또는 별도 배포 경로를 이용한다.
- `.env` 등 인증 정보가 포함된 파일은 Git 추적 대상에서 제외한다.

## 11. 설치 및 연결 확인 체크리스트

- [ ] Git 설치 및 버전 확인
- [ ] Git 사용자 이름과 이메일 설정
- [ ] SSH 키 생성 또는 안전한 이전
- [ ] 공개 키를 해당 GitHub 계정에 등록
- [ ] SSH config 설정
- [ ] 개인 GitHub 계정 SSH 인증 확인
- [ ] PMIS 포트폴리오 계정 SSH 인증 확인
- [ ] PMIS 저장소 복제 확인
- [ ] `git remote -v`에서 `github-portfolio` 확인
- [ ] 브랜치 생성 및 전환 확인
- [ ] 커밋 및 푸시 확인
- [ ] Git LFS 설치 및 버전 확인
- [ ] 대용량 파일 관리 방식 확인
- [ ] 신규 PC에서 설치 자산 확보 가능 여부 확인

---

**관리 원칙:** PMIS 저장소는 포트폴리오용 GitHub 계정을 사용하며, 개인 GitHub 계정과 SSH 인증 키를 분리한다. Git Flow 기반 브랜치 운영과 커밋 메시지 규칙을 준수하고, 대용량 설치 자산은 Git LFS 또는 별도 배포 경로를 통해 관리한다. 인증 정보와 개인 키는 저장소에 포함하지 않는다.