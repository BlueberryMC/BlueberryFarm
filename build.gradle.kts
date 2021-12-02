plugins {
    kotlin("jvm") version "1.6.0"
    `java-gradle-plugin`
    `maven-publish`
}

group = "net.blueberrymc.blueberryFarm"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
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
