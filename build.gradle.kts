plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.4"
    id("xyz.jpenilla.run-paper") version "2.0.1" // Adds runServer and runMojangMappedServer tasks for testing
    id("maven-publish")
    id ("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "de.oliver"
version = "1.1.5-beta"
description = "NPC plugin"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories{
    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    paperweight.foliaDevBundle("1.19.4-R0.1-SNAPSHOT")
    implementation("com.github.FancyMcPlugins:FancyLib:225ba14e03")
}

tasks {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                from(project.components["java"])
            }
        }
    }

    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}
