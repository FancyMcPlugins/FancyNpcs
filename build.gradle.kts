plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("xyz.jpenilla.run-paper") version "2.0.1" // Adds runServer and runMojangMappedServer tasks for testing
    id("maven-publish")
    id ("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "de.oliver"
version = findProperty("version")!!
description = findProperty("description").toString()

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://jitpack.io")
    }

    dependencies {
        compileOnly("net.kyori:adventure-text-minimessage:4.13.1")
        compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

        //implementation("net.byteflux:libby-bukkit:1.2.0")
        compileOnly("com.github.FancyMcPlugins:FancyLib:f2a7b13071")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    tasks{
        shadowJar{
            archiveClassifier.set("")
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories{
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://repo.fancyplugins.de/releases")
}

dependencies {
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")

    implementation(project(":api"))

    implementation(project(":implementation:folia", configuration = "reobf"))
    compileOnly(project(":implementation:folia"))

    implementation(project(":implementation:nms_base", configuration = "reobf"))
    compileOnly(project(":implementation:nms_base"))

    implementation(project(":implementation:nms_1_19_R3", configuration = "reobf"))
    compileOnly(project(":implementation:nms_1_19_R3"))

    implementation(project(":implementation:nms_1_19_R2", configuration = "reobf"))
    compileOnly(project(":implementation:nms_1_19_R2"))

    implementation("com.github.FancyMcPlugins:FancyLib:f2a7b13071")
}

tasks {
    publishing {
        repositories {
            maven {
                name = "fancypluginsReleases"
                url = uri("https://repo.fancyplugins.de/releases")
                credentials(PasswordCredentials::class)
                authentication {
                    isAllowInsecureProtocol = true
                    create<BasicAuthentication>("basic")
                }
            }

            maven {
                name = "fancypluginsSnapshots"
                url = uri("https://repo.fancyplugins.de/snapshots")
                credentials(PasswordCredentials::class)
                authentication {
                    isAllowInsecureProtocol = true
                    create<BasicAuthentication>("basic")
                }
            }
        }
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
//    assemble {
//        dependsOn(reobfJar)
//    }

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
