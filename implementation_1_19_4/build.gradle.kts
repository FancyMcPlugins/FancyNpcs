plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

val minecraftVersion = "1.19.4"

dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")

    compileOnly(project(":api"))
    compileOnly("de.oliver:FancyLib:${findProperty("fancyLibVersion")}")
    compileOnly("me.dave:ChatColorHandler:${findProperty("chatcolorhandlerVersion")}")
}


tasks {
    named("assemble") {
        dependsOn(named("reobfJar"))
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release = 17
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}