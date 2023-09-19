plugins {
    id("java-library")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")

    compileOnly("de.oliver:FancyLib:1.0.5.1")

    compileOnly("me.clip:placeholderapi:${findProperty("placeholderapiVersion")}")
}

tasks {
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(17)
    }
}