plugins {
    kotlin("jvm") version "1.4.10"
    application
}

version = "1.0-SNAPSHOT"


repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.exposed", "exposed-core", "0.25.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.25.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.25.1")
    implementation("org.jetbrains.exposed", "exposed-jodatime", "0.24.1")
    implementation("org.postgresql:postgresql:42.2.2")
    implementation("org.slf4j:slf4j-simple:1.6.1")
}

application {
    mainClassName = "MainKt"
}