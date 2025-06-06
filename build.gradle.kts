plugins {
    kotlin("jvm") version "2.1.20"
}

group = "com.galacsh"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://docs.spring.io/spring-boot/appendix/dependency-versions/coordinates.html
    implementation("org.springframework:spring-core:6.2.7")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
