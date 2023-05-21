group = "de.oliver"
version = rootProject.version

plugins {
    id("io.papermc.paperweight.userdev") version "1.5.5"
}

dependencies {
    paperweight.paperDevBundle("1.19.3-R0.1-SNAPSHOT")
    compileOnly(project(":implementation:nms_base"))
}