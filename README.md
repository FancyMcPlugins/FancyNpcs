<div align="center">

![Banner](https://github.com/FancyMcPlugins/FancyNpcs/blob/main/images/banner.png?raw=true)

[![GitHub Release](https://img.shields.io/github/v/release/FancyMcPlugins/FancyNpcs?logo=github&labelColor=%2324292F&color=%23454F5A)](https://github.com/FancyMcPlugins/FancyNpcs/releases/latest)
[![Supports Folia](https://img.shields.io/badge/folia-supported-%23F9D879?labelColor=%2313154E&color=%234A44A6)](https://papermc.io/software/folia)
[![Discord](https://img.shields.io/discord/899740810956910683?cacheSeconds=3600&logo=discord&logoColor=white&label=%20&labelColor=%235865F2&color=%23707BF4)](https://discord.gg/ZUgYCEJUEx)
[![GitHub Downloads](https://img.shields.io/github/downloads/FancyMcPlugins/FancyNpcs/total?logo=github&labelColor=%2324292F&color=%23454F5A)](https://github.com/FancyMcPlugins/FancyNpcs/releases/latest)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/fancynpcs?logo=modrinth&logoColor=white&label=downloads&labelColor=%23139549&color=%2318c25f)](https://modrinth.com/plugin/fancynpcs)
[![CodeFactor Grade](https://img.shields.io/codefactor/grade/github/FancyMcPlugins/FancyNpcs?logo=codefactor&logoColor=white&label=%20)](https://www.codefactor.io/repository/github/fancymcplugins/fancynpcs/issues/main)

[![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg)](https://modrinth.com/plugin/fancynpcs)
[![Hangar](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/hangar_vector.svg)](https://hangar.papermc.io/Oliver/FancyNpcs)

<br />

Simple, lightweight and feature-rich NPC plugin for **[Paper](https://papermc.io/software/paper)** (
and [Folia](https://papermc.io/software/folia)) servers using packets.

</div>

## Features

With this plugin you can create NPCs with customizable properties like:

- **Type** (Cow, Pig, Player, etc.)
- **Skin** (from username or texture URL)
- **Glowing** (in all colors)
- **Attributes** (pose, visibility, variant, etc.)
- **Equipment** (eg. holding a diamond sword and wearing leather armor)
- **Interactions** (player commands, console commands, messages)
- ...and much more!

Check out **[images section](#images)** down below.

<br />

## Installation

Paper **1.19.4** - **1.21.1** with **Java 21** (or higher) is required. Plugin should also work on **Paper** forks.

**Spigot** is **not** supported.

### Download (Stable)

- **[Hangar](https://hangar.papermc.io/Oliver/FancyNpcs)**
- **[Modrinth](https://modrinth.com/plugin/fancynpcs)**
- **[GitHub Releases](https://github.com/FancyMcPlugins/FancyNpcs/releases)**

### Download (Development Builds)

- **[Jenkins CI](https://jenkins.fancyplugins.de/job/FancyNpcs/)**
- **[FancyPlugins Website](https://fancyplugins.de/FancyNpcs/download)**

<br />

## Documentation

Official documentation is hosted **[here](https://fancyplugins.de/docs/fancynpcs.html)**. Quick reference:

- **[Getting Started](https://fancyplugins.de/docs/fn-getting-started.html)**
- **[Command Reference](https://fancyplugins.de/docs/fn-commands.html)**
- **[Using API](https://fancyplugins.de/docs/fn-api.html)**

**Have more questions?** Feel free to ask them on our **[Discord](https://discord.gg/ZUgYCEJUEx)** server.

<br />

## Developer API

More information can be found in **[Documentation](https://fancyplugins.de/docs/fn-api.html)**
and [Javadocs](https://fancyplugins.de/javadocs/fancynpcs/).

### Maven

```xml

<repository>
    <id>fancyplugins-releases</id>
    <name>FancyPlugins Repository</name>
    <url>https://repo.fancyplugins.de/releases</url>
</repository>
```

```xml

<dependency>
    <groupId>de.oliver</groupId>
    <artifactId>FancyNpcs</artifactId>
    <version>[VERSION]</version>
    <scope>provided</version>
</dependency>
```

### Gradle

```groovy
repositories {
    maven("https://repo.fancyplugins.de/releases")
}

dependencies {
    compileOnly("de.oliver:FancyNpcs:[VERSION]")
}
```

<br />

## Building

Follow these steps to build the plugin locally:

```shell
# Cloning repository.
$ git clone https://github.com/FancyMcPlugins/FancyNpcs.git
# Entering cloned repository.
$ cd FancyNpcs
# Compiling and building artifacts.
$ gradlew shadowJar
# Once successfully built, plugin .jar can be found in /build/libs directory.
```

<br />

## Images

Images showcasing the plugin, sent to us by our community.

![Screenshot 1](https://github.com/FancyMcPlugins/FancyNpcs/blob/main/images/screenshots/niceron1.jpeg?raw=true)  
<sup>Provided by [Explorer's Eden](https://explorerseden.eu/)</sup>

![Screenshot 2](https://github.com/FancyMcPlugins/FancyNpcs/blob/main/images/screenshots/niceron2.jpeg?raw=true)  
<sup>Provided by [Explorer's Eden](https://explorerseden.eu/)</sup>

![Screenshot 3](https://github.com/FancyMcPlugins/FancyNpcs/blob/main/images/screenshots/niceron3.jpeg?raw=true)  
<sup>Provided by [Explorer's Eden](https://explorerseden.eu/)</sup>

![Screenshot 4](https://github.com/FancyMcPlugins/FancyNpcs/blob/main/images/screenshots/dave1.jpeg?raw=true)  
<sup>Provided by [Beacon's Quest](https://www.beaconsquest.net/)</sup>

![Screenshot 5](https://github.com/FancyMcPlugins/FancyNpcs/blob/main/images/screenshots/oliver1.jpeg?raw=true)  
<sup>Provided by [@OliverSchlueter](https://github.com/OliverSchlueter)</sup>

![Screenshot 6](https://github.com/FancyMcPlugins/FancyNpcs/blob/main/images/screenshots/oliver2.jpeg?raw=true)  
<sup>Provided by [@OliverSchlueter](https://github.com/OliverSchlueter)</sup>

![Screenshot 7](https://github.com/FancyMcPlugins/FancyNpcs/blob/main/images/screenshots/grabsky1.jpeg?raw=true)  
<sup>Provided by [@Grabsky](https://github.com/Grabsky)</sup>
