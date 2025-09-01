import org.jmailen.gradle.kotlinter.tasks.InstallPreCommitHookTask
import org.jmailen.gradle.kotlinter.tasks.InstallPrePushHookTask

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("kapt") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jmailen.kotlinter") version "5.1.1"
    id("com.epages.restdocs-api-spec") version "0.18.2"
}

fun getGitHash(): String =
    providers
        .exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
        }.standardOutput.asText
        .get()
        .trim()

group = "kr.hhplus.be"
version = getGitHash()

val restdocsSpecMockMvcVersion = "0.18.2"
val kotlinCoroutineVersion = "1.10.2"
val restAssuredVersion = "5.5.0"
val nimbusJWTVersion = "10.3"
val redissonVersion = "3.44.0"
val kotestVersion = "5.9.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
        jvmToolchain(17)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
    }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutineVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.redisson:redisson-spring-boot-starter:$redissonVersion")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.netty:netty-resolver-dns-native-macos")

    // DB
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("io.rest-assured:kotlin-extensions:$restAssuredVersion")
    testImplementation("com.epages:restdocs-api-spec-mockmvc:$restdocsSpecMockMvcVersion")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutineVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")

    // JWT
    implementation("com.nimbusds:nimbus-jose-jwt:$nimbusJWTVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("user.timezone", "UTC")
}

if (!rootProject.extra.has("install-git-hooks")) {
    rootProject.extra["install-git-hooks"] = true

    val preCommit: TaskProvider<InstallPreCommitHookTask> =
        project.rootProject.tasks.register(
            "installPreCommitHook",
            InstallPreCommitHookTask::class
        ) {
            group = "build setup"
            description = "Installs Kotlinter Git pre-commit hook"
        }

    val prePush: TaskProvider<InstallPrePushHookTask> =
        project.rootProject.tasks.register(
            "installPrePushHook",
            InstallPrePushHookTask::class
        ) {
            group = "build setup"
            description = "Installs Kotlinter Git pre-push hook"
        }

    project.rootProject.tasks.named("installPrePushHook").configure {
        doLast {
            val hookFile = File(project.rootDir, ".git/hooks/pre-push")
            if (hookFile.exists()) {
                hookFile.writeText(
                    """
                    #!/bin/sh
                    set -e

                    GRADLEW=/Users/crispin/Documents/personal/git/pre-study-framework/gradlew
                    
                    ${'$'}GRADLEW test

                    if ! ${'$'}GRADLEW lintKotlin ; then
                        echo 1>&2 "\nlintKotlin found problems, running formatKotlin; commit the result and re-push"
                        ${'$'}GRADLEW formatKotlin
                        exit 1
                    fi
                    """.trimIndent()
                )
                hookFile.setExecutable(true)
            }
        }
    }

    project.rootProject.tasks.named("prepareKotlinBuildScriptModel") {
        dependsOn(preCommit, prePush)
    }
}

openapi3 {
    setServer("http://localhost:8080")
    title = "concert reservation server"
    version = "1.0.0"
    description = "API Documents"
    format = "yml"
}

tasks.register<Copy>("copySwaggerSpec") {
    description = "copy openapi3 document"
    group = JavaBasePlugin.DOCUMENTATION_GROUP

    val inputFile: File = file("build/api-spec/openapi3.yml")
    val outputDir: File = file("src/main/resources/static/docs/swagger-ui")

    inputs.file(inputFile)
    outputs.dir(outputDir)

    from(inputFile)
    into(outputDir)
}

tasks.named("build") {
    dependsOn("test")
    finalizedBy("copySwaggerSpec")
}

tasks.named("test") {
    finalizedBy("openapi3")
}

tasks.test {
    useJUnitPlatform {
        val includeConcurrencyTests: Boolean = project.hasProperty("includeConcurrencyTests")
        if (includeConcurrencyTests) {
            includeTags("concurrency")
        } else {
            excludeTags("concurrency")
        }
    }
}

tasks.named("bootJar") { enabled = true }

tasks.named("jar") { enabled = false }
