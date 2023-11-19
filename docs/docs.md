# FancyNpcs

![Latest Version](https://img.shields.io/github/v/release/FancyMcPlugins/FancyNpcs?style=flat-square)
[![SpigotMC Downloads](https://badges.spiget.org/resources/downloads/spigotmc-orange-107306.svg)](https://www.spigotmc.org/resources/npc-plugin-1-19-4.107306/)
[![Downloads](https://img.shields.io/modrinth/dt/fancynpcs?color=00AF5C&label=modrinth&style=flat&logo=modrinth)](https://modrinth.com/plugin/fancynpcs/versions)
![GitHub Downloads](https://img.shields.io/github/downloads/FancyMcPlugins/FancyNpcs/total?logo=GitHub&style=flat-square)

FancyNpcs is a simple and lightweight npc plugin for minecraft servers. It is using packets and therefore it is
blazingly fast.

The plugin is only supported for **1.19.4 - latest version**

It is highly recommended to use Paper or a fork of it - Folia is also supported.

[Go to the commands](FancyNpcs/commands)<br>
[Go to the api](FancyNpcs/api)

# Features

With FancyNpcs you can create NPCs that look like real players. You can edit a bunch of properties like the display name
or skin.

All properties:

- display name
- skin (username or url)
- entity type (cow, pig, blaze etc.)
- equipment (e.g. holding something in the hand)
- glowing (in all colors)
- and many more properties that are entity specific

You can also define some actions that will be executed when a player interacts with the npc:

- message: sends a simple message to the player
- player command: forces the player to execute a command
- server command: executes a command in the console