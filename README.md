<div align="center">
  <img height="150px" src="https://github.com/CelestialArtistry/.github/raw/main/img/logo.jpg" alt="Logo">
  <h1>NeoTenet 1.21.1</h1>

  ### Bukkit/Spigot API Implementation for NeoForge

  [![](https://img.shields.io/github/stars/TaiyitstMC/Taiyitst.svg?label=Stars&logo=github)](https://github.com/TaiyitstMC/Taiyitst/stargazers)
  [![](https://img.shields.io/badge/JDK-21.0.3-brightgreen.svg?colorB=469C00&logo=java)](https://www.azul.com/downloads/?version=java-21-lts#zulu)
  [![](https://img.shields.io/badge/Gradle-8.13-brightgreen.svg?colorB=469C00&logo=gradle)](https://docs.gradle.org/8.13/release-notes.html)
  [![](https://img.shields.io/discord/311256119005937665.svg?color=%237289da&label=Discord&logo=discord&logoColor=%237289da)](https://discord.gg/mohistmc)
</div>

---

## 🎯 Overview

NeoTenet is a cutting-edge implementation of the Bukkit/Spigot API for NeoForge, designed to bridge the gap between modded and plugin-based Minecraft servers. It enables server administrators to run both NeoForge mods and Bukkit plugins simultaneously on Minecraft 1.21.1.

| Version | Support | Stability | Mod Compatibility | Plugin Compatibility |
|:-------:|:-------:|:---------:|:-----------------:|:--------------------:|
| 1.21.1  | Active  | Poor      | Poor              | Poor                 |

> ⚠️ **Disclaimer**: This project is still in early development. Expect bugs and compatibility issues.

## 🔄 Upstream Update Progress

Current synced commit:
- [**NeoForge 1.21.1**] - [ceff3d](https://github.com/neoforged/NeoForge/commit/ceff3d20430e6a6653a83adae68ca11464e6335b)

## 🛠️ Build Instructions

Follow these steps to build NeoTenet from source:

```bash
# Step 1: Setup the project
./gradlew setup

# Step 2: Build the launcher JAR
./gradlew launcherJar

# Step 3: Find your built JAR
# Location: neotenetlauncher/build/libs
```

## ▶️ Running

NeoTenet is compilable and runnable, but plugin compatibility is currently limited. We're actively working to improve this.

## 🧩 NMS Support

NeoTenet features full support for Spigot's net.minecraft.server (NMS) classes:

- Classes and fields automatically remap to their intermediary counterparts at runtime
- Plugins remain unaffected by the remapping process
- No need to worry about plugin files being altered or made unsafe

## 📦 Usage

Getting started with NeoTenet is simple:

1. Download the launcher from [GitHub releases](https://github.com/TaiyitstMC/Taiyitst/releases)
2. Run the launcher just like you would with a standard Spigot JAR file:
   ```bash
   java -jar neotenet-launcher.jar
   ```

## 💬 Community

Join our growing community to get support, share ideas, and contribute to the project:

[Discord Server](https://discord.gg/stTgbjkJ) • [GitHub Issues](https://github.com/TaiyitstMC/Taiyitst/issues)

## 🔗 Upstream Projects

NeoTenet builds upon the excellent work of these open-source projects:

- [**Bukkit**](https://hub.spigotmc.org/stash/scm/spigot/bukkit.git) - Plugin support foundation
- [**CraftBukkit**](https://hub.spigotmc.org/stash/scm/spigot/craftbukkit.git) - Plugin support implementation
- [**Spigot**](https://hub.spigotmc.org/stash/scm/spigot/spigot.git) - Plugin support enhancements
- [**TENET**](https://github.com/Rz-C/TENET) - Core architectural concepts
- [**Arclight**](https://github.com/IzzelAliz/Arclight.git) - Technical inspiration

## 🙏 Special Thanks

We extend our heartfelt gratitude to our contributors and supporters:

- [Rz-C](https://github.com/Rz-C) - nicknamed as T Shi Huang, the Project sponsor and contributor
Without their generous support, this project would not be possible.