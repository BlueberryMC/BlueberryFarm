plugins {
    kotlin("jvm") version "1.6.0"
    `java-gradle-plugin`
    `maven-publish`
}

group = "net.blueberrymc.blueberryFarm"
version = "1.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib", "1.6.0"))
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("io.sigpipe:jbsdiff:1.0")
}

gradlePlugin {
    plugins {
        create("blueberryFarm") {
            id = "net.blueberrymc.blueberryFarm"
            implementationClass = "net.blueberrymc.blueberryFarm.BlueberryPlugin"
        }
    }
}
