plugins {
    java
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
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
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // ------------------------------------------------------------------------
    // Database
    // ------------------------------------------------------------------------
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

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
    // Test
    // ------------------------------------------------------------------------
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}