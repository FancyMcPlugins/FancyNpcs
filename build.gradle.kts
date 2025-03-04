import net.minecrell.pluginyml.paper.PaperPluginDescription
import java.io.BufferedReader
import java.io.InputStreamReader

plugins {
    id("java-library")
    id("maven-publish")

    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "8.3.6"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("com.modrinth.minotaur") version "2.+"
}

runPaper.folia.registerTask()

val supportedVersions =
    listOf(
        "1.19.4",
        "1.20",
        "1.20.1",
        "1.20.2",
        "1.20.3",
        "1.20.4",
        "1.20.5",
        "1.20.6",
        "1.21",
        "1.21.1",
        "1.21.2",
        "1.21.3",
        "1.21.4"
    )

allprojects {
    group = "de.oliver"
    val buildId = System.getenv("BUILD_ID")
    version = "2.4.2" + (if (buildId != null) ".$buildId" else "")
    description = "Simple, lightweight and fast NPC plugin using packets"

    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://repo.papermc.io/repository/maven-public/")
        maven(url = "https://repo.fancyplugins.de/releases")
        maven(url = "https://repo.lushplugins.org/releases")
        maven(url = "https://repo.inventivetalent.org/repository/maven-snapshots/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    implementation(project(":api"))
    implementation(project(":implementation_1_21_4"))
    implementation(project(":implementation_1_21_3"))
    implementation(project(":implementation_1_21_1"))
    implementation(project(":implementation_1_20_6"))
    implementation(project(":implementation_1_20_4", configuration = "reobf"))
    implementation(project(":implementation_1_20_2", configuration = "reobf"))
    implementation(project(":implementation_1_20_1", configuration = "reobf"))
    implementation(project(":implementation_1_20", configuration = "reobf"))
    implementation(project(":implementation_1_19_4", configuration = "reobf"))

    implementation("de.oliver:FancyLib:35")
    compileOnly("org.lushplugins:ChatColorHandler:5.1.2")
    implementation("de.oliver.FancyAnalytics:api:0.1.6")
    implementation("de.oliver.FancyAnalytics:logger:0.0.6")
    implementation("org.incendo:cloud-core:2.1.0-SNAPSHOT")
    implementation("org.incendo:cloud-paper:2.0.0-SNAPSHOT")
    implementation("org.incendo:cloud-annotations:2.1.0-SNAPSHOT")
    annotationProcessor("org.incendo:cloud-annotations:2.1.0-SNAPSHOT")
    implementation("org.mineskin:java-client-jsoup:3.0.2-SNAPSHOT")

    compileOnly("com.intellectualsites.plotsquared:plotsquared-core:7.5.1")
}

paper {
    main = "de.oliver.fancynpcs.FancyNpcs"
    bootstrapper = "de.oliver.fancynpcs.loaders.FancyNpcsBootstrapper"
    loader = "de.oliver.fancynpcs.loaders.FancyNpcsLoader"
    foliaSupported = true
    version = rootProject.version.toString()
    description = "Simple, lightweight and fast NPC plugin using packets"
    apiVersion = "1.19"
    serverDependencies {
        register("PlaceholderAPI") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("MiniPlaceholders") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("PlotSquared") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

tasks {
    runServer {
        minecraftVersion("1.21.4")

        downloadPlugins {
            hangar("ViaVersion", "5.2.1")
            hangar("ViaBackwards", "5.2.1")
            hangar("PlaceholderAPI", "2.11.6")
//            modrinth("multiverse-core", "4.3.11")
        }
    }

    shadowJar {
        relocate("org.incendo", "de.oliver")
        relocate("org.lushplugins.chatcolorhandler", "de.oliver.fancynpcs.libs.chatcolorhandler")
        archiveClassifier.set("")
        dependsOn(":api:shadowJar")
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
        options.release = 21
        // For cloud-annotations, see https://cloud.incendo.org/annotations/#command-components
        options.compilerArgs.add("-parameters")
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

        filesMatching("paper-plugin.yml") {
            expand(props)
        }

        filesMatching("version.yml") {
            expand(props)
        }
    }
}

tasks.publishAllPublicationsToHangar {
    dependsOn("shadowJar")
}

tasks.modrinth {
    dependsOn("shadowJar")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
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

fun getLastCommitMessage(): String {
    val process = ProcessBuilder("git", "log", "-1", "--pretty=%B").start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    val commitMessage = reader.readLine()
    reader.close()
    process.waitFor()
    if (process.exitValue() == 0) {
        println("Commit message: $commitMessage")
        return commitMessage ?: ""
    } else {
        throw IllegalStateException("Failed to retrieve the commit message.")
    }
}

hangarPublish {
    publications.register("plugin") {
        version = project.version as String
        id = "FancyNpcs"
        channel = "Alpha"

        apiKey.set(System.getenv("HANGAR_PUBLISH_API_TOKEN"))

        platforms {
            paper {
                jar = tasks.shadowJar.flatMap { it.archiveFile }
                platformVersions.set(supportedVersions)
            }
        }

        changelog = getLastCommitMessage()
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_PUBLISH_API_TOKEN"))
    projectId.set("fancynpcs")
    versionNumber.set(project.version.toString())
    versionType.set("alpha")
    uploadFile.set(file("build/libs/${project.name}-${project.version}.jar"))
    gameVersions.addAll(supportedVersions)
    loaders.add("paper")
    loaders.add("folia")
    changelog.set(getLastCommitMessage())
}