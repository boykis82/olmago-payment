import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.6"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
    kotlin("plugin.allopen") version "1.6.21"
    kotlin("plugin.noarg") version "1.6.21"
}

group = "team.caltech"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

object VERSION {
    const val kotestVersion = "5.5.4"
}

repositories {
	mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // kotest
    testImplementation("io.kotest:kotest-runner-junit5:${VERSION.kotestVersion}")
    testImplementation("io.kotest:kotest-assertions-core:${VERSION.kotestVersion}")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")

    // mockk
    testImplementation("io.mockk:mockk:1.13.2")

    // springmockk
    testImplementation("com.ninja-squad:springmockk:3.1.2")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

noArg {
    annotation("javax.persistence.Entity")
}