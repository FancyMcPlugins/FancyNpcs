plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.6"
}

val minecraftVersion = "1.19.4"

dependencies {
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")

    compileOnly("de.oliver:FancyLib:36")
    compileOnly("de.oliver.FancyAnalytics:logger:0.0.6")

    implementation("org.lushplugins:ChatColorHandler:5.1.3")
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