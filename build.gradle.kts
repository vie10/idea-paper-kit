plugins {
    java
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.intellij") version "1.7.0"
}

group = "online.viestudio"
version = "1.1.0"

repositories {
    mavenCentral()
}

intellij {
    version.set("2022.2")
    type.set("IC")

    plugins.set(listOf("org.jetbrains.kotlin", "org.jetbrains.plugins.gradle", "com.intellij.java"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    build {
        buildDir.resolve("idea-sandbox/plugins").deleteRecursively()
    }

    patchPluginXml {
        sinceBuild.set("222.3345.118")
        untilBuild.set("231.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
