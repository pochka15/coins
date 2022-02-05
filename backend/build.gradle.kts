import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    kotlin("plugin.jpa") version "1.6.10"
    kotlin("plugin.allopen") version "1.6.10"
    kotlin("kapt") version "1.6.10"
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

group = "pw"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    @Suppress("DEPRECATION")
    jcenter()
}

dependencies {
//    Spring boot
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-parent
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-parent", version = "2.4.5")

//    Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

//    Tests
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
        exclude(module = "mockito-core")
    }
    testImplementation("io.mockk:mockk:1.11.0")
    testImplementation("io.kotest:kotest-runner-junit5:4.4.3")
    testImplementation("io.kotest:kotest-assertions-core:4.0.7")
    implementation("org.junit.jupiter:junit-jupiter:5.7.0")

//    Database
    runtimeOnly("com.h2database:h2")
    implementation("org.hibernate.validator:hibernate-validator:6.2.0.Final")
    implementation("au.com.console:kotlin-jpa-specification-dsl:2.0.0")

//    Others
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.5")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// For Gradle applications, Heroku’s buildpack looks for a stage task to create 
// executable artifacts from our code. Luckily for us, the already preconfigured
// Gradle application plugin already comes with a task called installDist which does exactly that.
val stage = tasks.create("stage") {
    dependsOn("build", "clean")
}
tasks.build.configure { 
    mustRunAfter(tasks.clean.name)
}

tasks.test.configure {
    enabled = project.hasProperty("ENABLE_TESTS") 
}
