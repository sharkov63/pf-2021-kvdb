plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "ru.spbu.math-cs"
version = "2.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}