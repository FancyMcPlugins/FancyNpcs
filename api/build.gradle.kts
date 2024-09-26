plugins {
    id("java-library")
    id("maven-publish")
    id("io.github.goooler.shadow") version "8.1.8"
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    compileOnly("de.oliver:FancyLib:${findProperty("fancyLibVersion")}")
    compileOnly("de.oliver.FancyAnalytics:logger:${findProperty("fancyLoggerVersion")}")

    implementation("org.lushplugins:ChatColorHandler:${findProperty("chatcolorhandlerVersion")}")
}

tasks {
    shadowJar {
        archiveClassifier.set("")

        relocate("org.lushplugins.chatcolorhandler", "de.oliver.fancynpcs.libs.chatcolorhandler")
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
                groupId = rootProject.group.toString()
                artifactId = rootProject.name
                version = rootProject.version.toString()
                from(project.components["java"])
            }
        }
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release = 17

    }
}