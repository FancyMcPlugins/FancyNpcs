plugins {
    id("java")
}

group = "de.oliver"
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
}