# Java 개발환경 구축 가이드

- **문서 경로:** `docs/development/environment/003_java_setup.md`
- **문서 버전:** 1.1
- **작성일:** 2026-11-06
- **기준 Java 버전:** JDK 21
- **기준 Gradle 버전:** Gradle Wrapper 9.5.1

---

## 1. 목적

본 문서는 PMIS 백엔드 개발에 필요한 Java 및 Gradle 환경을 구축하는 방법을 설명한다.

PMIS 백엔드는 Java 21을 사용하지만, 기존 개발 프로젝트와의 호환성을 위해 시스템 기본 Java 환경은 JDK 17로 유지한다.

PMIS 개발 시에만 프로젝트 전용 스크립트를 통해 JDK 21을 활성화하는 것을 원칙으로 한다.

## 2. Java 설치 현황

### 2.1 현재 개발 PC의 Java 설치 정보

| 설치 경로 | Java 버전 | 용도 |
|---|---|---|
| `C:\Program Files\Java\jdk-11.0.14` | JDK 11 | 기존 설치 환경 |
| `C:\Program Files\Java\jdk-17` | JDK 17.0.13 | 시스템 기본 Java |
| `C:\Program Files\Java\jdk-21` | JDK 21.0.1 | PMIS 개발용 Java |

PMIS 백엔드의 기준 Java 버전은 JDK 21이다.

※ 위 버전은 현재 개발 PC에서 확인한 결과다. 신규 PC에서는 JDK 21 LTS의 최신 보안 업데이트 버전을 설치하는 것을 권장한다. Java Toolchain은 Java 21을 기준으로 동작하므로 세부 패치 버전은 달라도 된다.

### 2.2 Java 버전 확인

PowerShell에서 다음 명령어를 실행한다.

```powershell
java -version
javac -version
```

설치 경로별 Java 실행 파일을 직접 확인할 수도 있다.

```powershell
& "C:\Program Files\Java\jdk-21\bin\java.exe" -version
& "C:\Program Files\Java\jdk-21\bin\javac.exe" -version
```

## 3. JDK 21 설치

신규 PC에 JDK 21이 설치되어 있지 않은 경우 JDK 21 LTS 배포판을 설치한다.

설치 시 다음 사항을 확인한다.

1. Windows x64 환경에 맞는 설치 파일을 선택한다.
2. JDK 21 설치를 진행한다.
3. 설치 완료 후 실제 설치 경로를 확인한다.
4. 프로젝트 환경 설정 스크립트의 JDK 경로와 일치하는지 확인한다.

현재 PMIS 개발 PC의 JDK 경로는 다음과 같다.

```text
C:\Program Files\Java\jdk-21
```

※ 신규 PC에서 설치 경로가 다르면 `scripts/use-pmis-java21.ps1`의 `$jdkPath` 값을 실제 설치 경로에 맞게 변경한다.

## 4. Java 환경변수 관리

### 4.1 전역 JAVA_HOME 유지

기존 프로젝트에서 JDK 17을 사용하므로 시스템 및 사용자 전체의 `JAVA_HOME`을 JDK 21로 변경하지 않는다.

현재 개발 PC의 기본 `JAVA_HOME`은 다음과 같다.

```text
C:\Program Files\Java\jdk-17
```

시스템 및 사용자 전체의 `JAVA_HOME`과 `Path`는 기존 설정을 유지한다.

### 4.2 PMIS 전용 Java 활성화

프로젝트 저장소 루트에서 다음 명령어를 실행한다.

```powershell
. .\scripts\use-pmis-java21.ps1
```

해당 스크립트는 다음 작업을 수행한다.

- JDK 21 설치 여부 확인
- 현재 PowerShell 세션의 `JAVA_HOME` 설정
- JDK 21의 `bin` 디렉터리를 현재 세션의 `Path` 앞에 추가
- Java 버전 출력

설정은 현재 PowerShell 세션에만 적용되며 시스템 전체 환경변수는 변경하지 않는다.

### 4.3 활성화 결과 확인

```powershell
$env:JAVA_HOME
java -version
javac -version
```

실제 실행 파일 경로도 확인한다.

```powershell
(Get-Command java).Source
(Get-Command javac).Source
```

정상적인 경우 다음과 같이 확인된다.

```text
JAVA_HOME: C:\Program Files\Java\jdk-21

java version "21.0.1"
javac 21.0.1
```

실제 실행 파일 경로도 PMIS 전용 JDK 21의 `bin` 디렉터리를 가리켜야 한다.

## 5. Gradle 환경

### 5.1 Gradle Wrapper 사용

PMIS 백엔드는 시스템에 별도로 설치된 Gradle 대신 프로젝트에 포함된 Gradle Wrapper를 사용한다.

설정 파일:

```text
pmis-backend/gradle/wrapper/gradle-wrapper.properties
```

현재 설정:

```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-9.5.1-bin.zip
```

신규 PC에서도 별도의 Gradle 설치 없이 프로젝트의 Wrapper를 사용한다.

### 5.2 Java Toolchain 설정

`pmis-backend/build.gradle.kts`에서 Java Toolchain을 다음과 같이 지정한다.

```kotlin
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
```

이를 통해 백엔드 컴파일에 Java 21을 사용하도록 설정한다.

※ 실제 프로젝트의 다른 Gradle 설정은 유지하고 Java Toolchain 설정을 확인한다.

### 5.3 Gradle 버전 확인

PMIS 전용 Java 환경을 활성화한 후 실행한다.

```powershell
.\pmis-backend\gradlew --version
```

현재 확인된 결과:

| 항목 | 버전 |
|---|---|
| Gradle | 9.5.1 |
| Launcher JVM | Java 21.0.1 |
| Daemon JVM | JDK 21 |
| OS | Windows 10 amd64 |

Launcher JVM은 Gradle 실행에 사용되는 JVM이고, Daemon JVM은 Gradle 빌드 작업을 수행하는 데몬의 JVM이다.

두 항목 모두 PMIS의 Java 21 환경을 사용하는지 확인한다.

## 6. 백엔드 빌드 검증

프로젝트 루트에서 다음 명령어를 실행한다.

```powershell
.\pmis-backend\gradlew -p .\pmis-backend clean build
```

정상적으로 빌드되면 다음 메시지가 출력된다.

```text
BUILD SUCCESSFUL
```

현재 개발 PC에서는 전체 백엔드 빌드가 정상적으로 완료되었다.

빌드 과정에서 기존 코드의 Deprecated API 사용 경고가 출력될 수 있다. 이는 빌드 실패와 구분하여 관리한다.

## 7. Visual Studio Code Java 설정

PMIS 백엔드 개발을 위해 다음 확장을 사용한다.

| 확장 | 용도 |
|---|---|
| Red Hat Java | Java 언어 지원 |
| Gradle for Java | Gradle 프로젝트 관리 |
| Spring Boot Extension Pack | Spring Boot 개발 지원 |
| Java Debugger | Java 디버깅 |
| Java Test Runner | Java 테스트 실행 |

VS Code Java 확장의 내부 실행 JRE와 PMIS 백엔드 실행 JDK는 구분해야 한다.

VS Code 확장은 자체적으로 사용하는 Java Runtime을 별도로 포함할 수 있다. 따라서 확장이 Java 25 등의 별도 Runtime을 사용하더라도 PMIS 백엔드의 Gradle 실행 환경이 JDK 21로 정상 설정되어 있다면 서로 다른 용도의 Java 환경이다.

PMIS 백엔드의 Java 버전은 다음 항목을 기준으로 확인한다.

- `JAVA_HOME`
- `java -version`
- `javac -version`
- Gradle Launcher JVM 및 Daemon JVM
- Java Toolchain 설정

## 8. 문제 해결

### 8.1 Java 버전이 17로 출력되는 경우

PMIS 전용 환경 설정 스크립트를 다시 실행한다.

```powershell
. .\scripts\use-pmis-java21.ps1
```

이후 다음 명령어로 확인한다.

```powershell
$env:JAVA_HOME
java -version
(Get-Command java).Source
```

### 8.2 JAVA_HOME 경로를 찾을 수 없는 경우

JDK 설치 경로를 확인한다.

```powershell
Test-Path "C:\Program Files\Java\jdk-21\bin\java.exe"
```

결과가 `False`이면 실제 설치 경로를 확인하고 환경 설정 스크립트의 `$jdkPath`를 수정한다.

### 8.3 Gradle이 다른 Java 버전을 사용하는 경우

다음 항목을 순서대로 확인한다.

1. PMIS Java 환경 설정 스크립트 실행 여부
2. `JAVA_HOME` 값
3. `java -version` 결과
4. `javac -version` 결과
5. `Get-Command java` 실행 경로
6. `.\pmis-backend\gradlew --version`의 Launcher JVM 및 Daemon JVM
7. Gradle 프로젝트의 Java Toolchain 설정

### 8.4 Gradle 빌드가 실패하는 경우

다음 명령어로 상세 로그를 확인한다.

```powershell
.\pmis-backend\gradlew -p .\pmis-backend clean build --stacktrace
```

오류 메시지와 Java 버전, Gradle 버전을 함께 확인하여 원인을 구분한다.

---

## 9. 확인 완료 기준

- [ ] JDK 21 설치 확인
- [ ] `use-pmis-java21.ps1` 실행 확인
- [ ] `JAVA_HOME`이 JDK 21로 설정되었는지 확인
- [ ] `java -version`에서 Java 21 확인
- [ ] `javac -version`에서 Java 21 확인
- [ ] Java 실행 파일 경로 확인
- [ ] Gradle Wrapper 9.5.1 확인
- [ ] Gradle Launcher JVM 및 Daemon JVM 확인
- [ ] Java Toolchain 21 설정 확인
- [ ] PMIS 백엔드 `clean build` 성공