group = "de.oliver"
version = rootProject.version

subprojects {
    dependencies {
        compileOnly(project(":api"))
    }
}