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

    maven {
        url = uri("https://maven.stckoverflw.net/private")

        credentials {
            username = "stckoverflw"
            password = "O3OJSlysptIRrNyLQoIODQ8WJdaxOrCYYVo0X7bu3yeY8FnRP5iemu4gju87f4aC"
        }
    }
}

dependencies {
    compileOnly("com.velocitypowered", "velocity-api", "3.1.1")
    kapt("com.velocitypowered", "velocity-api", "3.1.1")

    compileOnly("net.kyori", "adventure-text-minimessage", "4.13.1")

    val ktorVersion = "2.3.0"

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")

    implementation("io.github.oshai:kotlin-logging-jvm:4.0.0-beta-22")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    implementation("net.cherrycave:birgid:0.4.1")
}

kotlin {
    jvmToolchain(17)
}

tasks.build {
    dependsOn(tasks.shadowJar.get())
}
