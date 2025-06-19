plugins {
    // Apply the shared build logic from a convention plugin.
    id("buildsrc.convention.kotlin-jvm")
    
    // Apply Spring Boot plugin
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
}

dependencies {
    // Spring Boot starter for web applications
    implementation("org.springframework.boot:spring-boot-starter-web")
    
    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}