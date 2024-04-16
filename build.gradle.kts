import java.io.BufferedReader
import java.io.InputStreamReader

plugins {
    id("java-library")
    id("maven-publish")

    id("xyz.jpenilla.run-paper") version "2.2.2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

runPaper.folia.registerTask()

allprojects {
    group = "de.oliver"
    val buildId = System.getenv("BUILD_ID")
    version = "2.0.11" + (if (buildId != null) ".$buildId" else "")
    description = "Simple, lightweight and fast NPC plugin using packets"

    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://papermc.io/repo/repository/maven-public/")
        maven(url = "https://repo.fancyplugins.de/releases")
        maven(url = "https://repo.smrt-1.com/releases")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${findProperty("minecraftVersion")}-R0.1-SNAPSHOT")

    implementation(project(":api"))
    implementation(project(":implementation_1_20_4", configuration = "reobf"))
    implementation(project(":implementation_1_20_2", configuration = "reobf"))
    implementation(project(":implementation_1_20_1", configuration = "reobf"))
    implementation(project(":implementation_1_20", configuration = "reobf"))
    implementation(project(":implementation_1_19_4", configuration = "reobf"))

    implementation("de.oliver:FancyLib:${findProperty("fancyLibVersion")}")
    implementation("me.dave:ChatColorHandler:${findProperty("chatcolorhandlerVersion")}")

    compileOnly("com.intellectualsites.plotsquared:plotsquared-core:${findProperty("plotsquaredVersion")}")
}

tasks {
    runServer {
        minecraftVersion(findProperty("minecraftVersion").toString())
//        minecraftVersion("1.20.1")

        downloadPlugins {
            hangar("ViaVersion", "4.9.4-SNAPSHOT+280")
            hangar("ViaBackwards", "4.9.3-SNAPSHOT+155")
            hangar("PlaceholderAPI", "2.11.5")
        }
    }

    shadowJar {
        archiveClassifier.set("")

        dependsOn(":api:shadowJar")

        relocate("me.dave.chatcolorhandler", "de.oliver.fancynpcs.libs.chatcolorhandler")
        relocate("io.sentry", "de.oliver.fancynpcs.libs.sentry")
    }

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

        val props = mapOf(
            "description" to project.description,
            "version" to project.version,
            "hash" to getCurrentCommitHash(),
            "build" to (System.getenv("BUILD_ID") ?: "").ifEmpty { "undefined" }
        )

        inputs.properties(props)

        filesMatching("plugin.yml") {
            expand(props)
        }

        filesMatching("version.yml") {
            expand(props)
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

fun getCurrentCommitHash(): String {
    val process = ProcessBuilder("git", "rev-parse", "HEAD").start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    val commitHash = reader.readLine()
    reader.close()
    process.waitFor()
    if (process.exitValue() == 0) {
        return commitHash ?: ""
    } else {
        throw IllegalStateException("Failed to retrieve the commit hash.")
    }
}
