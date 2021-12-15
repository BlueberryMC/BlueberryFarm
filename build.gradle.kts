plugins {
    kotlin("jvm") version "1.6.0"
    `java-gradle-plugin`
    `maven-publish`
}

group = "net.blueberrymc.blueberryFarm"
version = "1.0.3-SNAPSHOT"

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

publishing {
    repositories {
        maven {
            name = "blueberryRepo"
            credentials(PasswordCredentials::class)
            url = uri(
                if (project.version.toString().endsWith("-SNAPSHOT"))
                    "https://repo.blueberrymc.net/repository/maven-snapshots/"
                else
                    "https://repo.blueberrymc.net/repository/maven-releases/"
            )
        }
    }
}

gradlePlugin {
    plugins {
        create("blueberryFarm") {
            id = "net.blueberrymc.blueberryFarm"
            implementationClass = "net.blueberrymc.blueberryFarm.BlueberryPlugin"
        }
    }
}
