# Getting Started

## Gradle

```gradle
repositories {
    maven("https://repo.fancyplugins.de/releases")
    ...
}
```

```gradle
dependencies {
    implementation("de.oliver:FancyNpcs:version")
    ...
}
```

## Maven

```maven
<repository>
    <id>fancyplugins-releases</id>
    <name>FancyPlugins Repository</name>
    <url>https://repo.fancyplugins.de/releases</url>
</repository>
```

```maven
<dependency>
    <groupId>de.oliver</groupId>
    <artifactId>FancyNpcs</artifactId>
    <version>VERSION</version>
</dependency>

```