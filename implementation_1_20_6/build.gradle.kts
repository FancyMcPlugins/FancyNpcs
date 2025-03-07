plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.14"
}

val minecraftVersion = "1.20.6"

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")

    compileOnly(project(":api"))
    compileOnly("de.oliver:FancyLib:35")
    compileOnly("org.lushplugins:ChatColorHandler:5.1.3")
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