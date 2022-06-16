plugins {
    java
    kotlin("jvm") version "1.6.21"
    id("org.jetbrains.intellij") version "1.6.0"
}

group = "online.viestudio"
version = "1.0.3"

repositories {
    mavenCentral()
}

intellij {
    version.set("2022.1.2")
    type.set("IC")

    plugins.set(listOf("org.jetbrains.kotlin", "org.jetbrains.plugins.gradle", "com.intellij.java"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    build {
        buildDir.resolve("idea-sandbox/plugins").deleteRecursively()
    }

    patchPluginXml {
        sinceBuild.set("221.4842.29")
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
