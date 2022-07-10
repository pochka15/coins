import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.meta.jaxb.ForcedType

plugins {
    id("org.springframework.boot") version "2.6.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("nu.studer.jooq") version "7.1"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    kotlin("plugin.allopen") version "1.6.10"
    kotlin("kapt") version "1.6.10"
    id("org.flywaydb.flyway") version "8.5.13"
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
//    Spring
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-parent
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-parent", version = "2.4.5")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

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
    implementation("org.jooq:jooq:3.16.4")
    implementation("org.postgresql:postgresql:42.3.2")
    // https://mvnrepository.com/artifact/jakarta.xml.bind/jakarta.xml.bind-api
    jooqGenerator("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")
    jooqGenerator("org.postgresql:postgresql:42.3.2")
    jooqGenerator(project(":backend:jooq-generator"))

//    Others
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.5")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
//    Netty was primarily imported to work in pair with the boot-starter-webflux. Maybe it's only necessary for mac M1 (didn't test on other machines)
    implementation("io.netty:netty-all:4.1.74.Final")
    implementation("com.github.scribejava:scribejava-core:8.3.1")
    implementation("com.github.scribejava:scribejava-httpclient-okhttp:8.3.1")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
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

jooq {
    version.set("3.16.4")

    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:${System.getenv("DATABASE_URL")}"
                    user = System.getenv("DATABASE_USERNAME")
                    password = System.getenv("DATABASE_PASSWORD")
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        forcedTypes.addAll(listOf(
                            ForcedType().apply {
                                name = "varchar"
                                includeExpression = ".*"
                                includeTypes = "JSONB?"
                            },
                            ForcedType().apply {
                                name = "varchar"
                                includeExpression = ".*"
                                includeTypes = "INET"
                            }
                        ))
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = false
                        isImmutablePojos = false
                        isFluentSetters = false
                        isDaos = true
                    }
                    target.apply {
                        packageName = "pw.coins.db.generated"
                    }
                    strategy.name = "JooqGenerationStrategy"
                }
            }
        }
    }
}

flyway {
    url = "jdbc:${System.getenv("DATABASE_URL")}"
    user = System.getenv("DATABASE_USERNAME")
    password = System.getenv("DATABASE_PASSWORD")
    table = "schema_history"
}

tasks.getByName<Jar>("jar") {
//    don't generate *.plain.jar by the spring boot
//    https://stackoverflow.com/questions/67663728/spring-boot-2-5-0-generates-plain-jar-file-can-i-remove-it
    enabled = false
}

tasks.named<nu.studer.gradle.jooq.JooqGenerate>("generateJooq") {
    dependsOn("flywayMigrate")

    // declare Flyway migration scripts as inputs on the jOOQ task
    inputs.files(fileTree("src/main/resources/db/migration"))
        .withPropertyName("migrations")
        .withPathSensitivity(PathSensitivity.RELATIVE)

    allInputsDeclared.set(true)
}