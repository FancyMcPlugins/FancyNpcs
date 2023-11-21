# Updating to a new Minecraft version

## Pre-release phase

While mojang is still publishing pre-releases, check in the mc source for potential problems & breaking changes.

## Preparing

Do this before paper released the first dev bundle.

1. copy the ``implementation_(PREVIOUS VERSION)`` module
2. paste the module and change the version ('1_20_2' -> '1_20_3')
3. add ``include("implementation_(NEW VERSION)")`` to settings.gradle.kts
4. add ``implementation(project(":implementation_(NEW VERSION)", configuration = "reobf"))`` in build.gradle.kts
5. add the new version in FancyNpcs#onLoad and FancyNpcs#SUPPORTED_VERSIONS
6. add the new version in AttributeManagerImpl#init
7. update the latest version in README

## Actually updating

1. update ``mcVersion`` in build.gradle.kts
2. update ``minecraftVersion`` in the new implementation module
3. test if it compiles
4. fix compile & mapping errors
5. [publish new plugin version](releases.md) and hope nothing is broke