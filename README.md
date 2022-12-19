# paperweight-test-plugin

jmp's test plugin for [`paperweight-userdev`](https://github.com/PaperMC/paperweight/tree/main/paperweight-userdev) development

(also serves as an example until more thorough documentation is created)

### notes (read these)

- `build.gradle.kts` and `settings.gradle.kts` both contain important configuration.
- `paperweight-userdev` automatically detects shadow and will use `shadowJar` as input for `reobfJar`. This means no extra configuration is required to use `paperweight-userdev` with shadow. See the `shadow` branch on this repository for an exmaple usage of shadow with `paperweight-userdev`.
- The `plugin-yml` and `run-paper` Gradle plugins are both optional, however I use them in almost all my plugin projects and recommend at least trying them out. `plugin-yml` auto-generates your plugin.yml file from configuration in the build file, and `run-paper` allows for launching a test server with your plugin through the `runServer` and `runMojangMappedServer` tasks.
- Due to a [gradle bug](https://github.com/gradle/gradle/issues/17559), independently applying `paperweight-userdev` to multiple projects in a build can result in errors. To work around this, apply `paperweight-userdev` to the root project with `apply false` (i.e., `id("...") version "..." apply false` in Kotlin DSL), and then when applying `paperweight-userdev` to subprojects don't include a version specification. A more advanced solution would involve adding `paperweight-userdev` as a dependency to your build logic, see [`reflection-remapper`](https://github.com/jpenilla/reflection-remapper) and the [`source-remap`](https://github.com/PaperMC/paperweight-test-plugin/tree/source-remap) branch on this repo for examples of this.
- The [`source-remap`](https://github.com/PaperMC/paperweight-test-plugin/tree/source-remap) branch on this repo has a special `remapPluginSources` task to remap the source code in `src/main/java` from spigot to Mojang mappings, outputting remapped source in `/src/main/mojangMappedJava`. Note that this will only remap your code, not update it from a prior version. Meaning you must be using the dev bundle for the Minecraft version your source code is for when remapping.
- `paperweight-userdev` doesn't provide any utilities for doing reflection. [`reflection-remapper`](https://github.com/jpenilla/reflection-remapper) is a companion library to `paperweight-userdev` assisting with reflection on remapped code.
