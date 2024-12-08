plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.7.7"
}

val minecraftVersion = "1.20.6"

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")

    compileOnly(project(":api"))
    compileOnly("de.oliver:FancyLib:33")
    compileOnly("org.lushplugins:ChatColorHandler:5.1.0")
}


tasks {
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
    }
}