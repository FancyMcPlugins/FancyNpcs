plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.5.5"
}

val minecraftVersion = "1.19.4"

dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")

    implementation(project(":api"))
    implementation("de.oliver:FancyLib:${findProperty("fancyLibVersion")}")
    compileOnly("me.clip:placeholderapi:${findProperty("placeholderapiVersion")}")
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

        options.release.set(17)
    }
}