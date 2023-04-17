plugins {
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    kotlin("kapt") version "1.8.20"

    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "net.cherrycave"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.velocitypowered", "velocity-api", "3.1.1")
    kapt("com.velocitypowered", "velocity-api", "3.1.1")

    compileOnly("net.kyori", "adventure-text-minimessage", "4.13.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
}

kotlin {
    jvmToolchain(17)
}

tasks.build {
    dependsOn(tasks.shadowJar.get())
}
