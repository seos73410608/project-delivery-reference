
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.flywaydb:flyway-database-postgresql:11.7.2")
    }
}
plugins {
    java
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"

    // Flyway Gradle Plugin - Database Migration 관리
    id("org.flywaydb.flyway") version "11.7.2"
}

group = "com.seos"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {

    // ------------------------------------------------------------------------
    // Spring Boot
    // ------------------------------------------------------------------------
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // ------------------------------------------------------------------------
    // Reactive Streams
    // Runtime API multipart upload 시 필요한 Publisher 클래스 제공
    // ------------------------------------------------------------------------
    implementation("org.reactivestreams:reactive-streams:1.0.4")

    // ------------------------------------------------------------------------
    // Database
    // ------------------------------------------------------------------------
    // MariaDB - 기존 환경 유지
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    // PostgreSQL - PMIS 독립 데이터 플랫폼
    runtimeOnly("org.postgresql:postgresql")

    // ------------------------------------------------------------------------
    // Flyway - Database Migration
    // ------------------------------------------------------------------------
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")

    // ------------------------------------------------------------------------
    // Lombok
    // ------------------------------------------------------------------------
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // ------------------------------------------------------------------------
    // Configuration Processor
    // ------------------------------------------------------------------------
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // ------------------------------------------------------------------------
    // Development
    // ------------------------------------------------------------------------
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // ------------------------------------------------------------------------
    // JWT
    // ------------------------------------------------------------------------
    implementation("io.jsonwebtoken:jjwt-api:0.12.7")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.7")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.7")

    // ------------------------------------------------------------------------
    // MapStruct
    // ------------------------------------------------------------------------
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    // ------------------------------------------------------------------------
    // Spring AI (향후 사용)
    // ------------------------------------------------------------------------
    // implementation(platform("org.springframework.ai:spring-ai-bom:1.0.1"))
    // implementation("org.springframework.ai:spring-ai-starter-model-openai")
    // implementation("org.springframework.ai:spring-ai-starter-model-ollama")

    // ------------------------------------------------------------------------
    // Swagger
    // ------------------------------------------------------------------------
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")

    // ------------------------------------------------------------------------
    // Test
    // ------------------------------------------------------------------------
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// ------------------------------------------------------------------------
// Flyway Configuration
// ------------------------------------------------------------------------
val flywayTestProfile = System.getenv("SPRING_PROFILES_ACTIVE")
    ?.split(",")
    ?.any { it.trim().equals("test", ignoreCase = true) } == true

flyway {
    // PMIS 환경 변수 사용
    url = System.getenv("PMIS_POSTGRESQL_URL") ?: ""

    // 테스트 환경은 전용 Migration 계정 사용
    user = if (flywayTestProfile) {
        System.getenv("PMIS_TEST_MIGRATOR_USERNAME") ?: ""
    } else {
        System.getenv("PMIS_DB_USERNAME") ?: ""
    }

    password = if (flywayTestProfile) {
        System.getenv("PMIS_TEST_MIGRATOR_PASSWORD") ?: ""
    } else {
        System.getenv("PMIS_DB_PASSWORD") ?: ""
    }

    // PostgreSQL Schema
    schemas = arrayOf("public")
    defaultSchema = "public"

    // Migration SQL 위치
    locations = arrayOf("filesystem:src/main/resources/db/migration")

    // 기존 DB를 Version 1로 등록
    baselineVersion = "1"
    baselineDescription = "Existing PMIS schema"

    // 실수로 전체 스키마를 삭제하는 작업 방지
    cleanDisabled = true
}

tasks.withType<Test> {
    useJUnitPlatform()
}
