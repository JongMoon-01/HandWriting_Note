plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":document"))
    implementation(project(":annotation"))
    implementation(project(":user"))
    implementation(project(":export"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    runtimeOnly("org.postgresql:postgresql")

    // Spring Boot 4.0.x가 관리하는 Flyway(11.14.1)는 PostgreSQL 18 감지 실패 버그가 있어
    // (spring-projects/spring-boot#49012) 버전을 명시적으로 고정해서 오버라이드.
    implementation("org.flywaydb:flyway-core:12.9.0")
    implementation("org.flywaydb:flyway-database-postgresql:12.9.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
