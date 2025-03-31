plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.14"
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    paperweight.paperDevBundle("1.21.5-no-moonrise-SNAPSHOT")

    compileOnly(project(":api"))
    compileOnly("de.oliver:FancyLib:37")
    compileOnly("org.lushplugins:ChatColorHandler:5.1.5")
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