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
    implementation("org.springframework:spring-context:6.2.7")
    implementation("org.springframework:spring-webmvc:6.2.7")
    implementation("org.aspectj:aspectjweaver:1.9.24")
    implementation("org.apache.tomcat.embed:tomcat-embed-core:10.1.41")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
