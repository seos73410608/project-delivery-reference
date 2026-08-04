plugins {
    java
    id("org.springframework.boot") version "3.5.5"
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
    implementation("org.springframework.boot:spring-boot-starter-web")
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
    // Spring AI (향후 사용)
    // ------------------------------------------------------------------------
    // implementation(platform("org.springframework.ai:spring-ai-bom:1.0.1"))
    // implementation("org.springframework.ai:spring-ai-starter-model-openai")
    // implementation("org.springframework.ai:spring-ai-starter-model-ollama")

    // ------------------------------------------------------------------------
    // Test
    // ------------------------------------------------------------------------
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}