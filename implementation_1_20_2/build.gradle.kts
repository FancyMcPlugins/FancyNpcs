plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.7.5"
}

val minecraftVersion = "1.20.2"

dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")

    compileOnly(project(":api"))
    compileOnly("de.oliver:FancyLib:33")
    compileOnly("org.lushplugins:ChatColorHandler:5.1.0")
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