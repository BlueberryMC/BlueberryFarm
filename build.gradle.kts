plugins {
    kotlin("jvm") version "1.8.21"
    `java-gradle-plugin`
    `maven-publish`
}

group = "net.blueberrymc.blueberryfarm"
version = "2.1.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.code.gson:gson:2.10")
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
        create("blueberryfarm") {
            id = "net.blueberrymc.blueberryfarm"
            implementationClass = "net.blueberrymc.blueberryfarm.BlueberryPlugin"
        }
    }
}
