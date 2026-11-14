# Slimefun 4 (English Edition)

[![Auto Build](https://github.com/mybezo/Slimefun4-Gugu/actions/workflows/auto-build.yml/badge.svg)](https://github.com/mybezo/Slimefun4-Gugu/actions/workflows/auto-build.yml)
[![Latest build](https://img.shields.io/badge/download-latest%20build-brightgreen)](https://github.com/mybezo/Slimefun4-Gugu/releases/tag/exp)
[![Issues](https://img.shields.io/github/issues/mybezo/Slimefun4-Gugu.svg)](https://github.com/mybezo/Slimefun4-Gugu/issues)
[![License: GPL v3](https://img.shields.io/badge/license-GPLv3-blue.svg)](LICENSE)

Slimefun is a Paper plugin that brings a modpack-like experience to vanilla Minecraft:
jetpacks, magic altars, nuclear reactors, energy networks, cargo systems and **more than 500 new items and recipes**.
Every player decides for themselves whether to go down the path of magic or technology.

This repository is an **English edition** of the
[SlimefunGuguProject](https://github.com/SlimefunGuguProject/Slimefun4) fork.
It keeps all of the Gugu fork's improvements (database storage, Paper support, newer Minecraft versions, performance work)
while presenting everything in English, just like the [official Slimefun](https://github.com/Slimefun/Slimefun4).

> [!IMPORTANT]
> This is an **unofficial** build. Please do **not** report bugs to the official Slimefun or SlimefunGuguProject
> trackers or Discord servers. Report them **[here](https://github.com/mybezo/Slimefun4-Gugu/issues)** instead.

## :sparkles: What's different in this edition

- **English everywhere**: item names, lore, menus, chat messages, console logs, config comments and research ranks.
  Item text matches the official Slimefun.
- **Default language is `en`**. Players can still pick any other language (including Chinese) in the guide.
- **Tier-colored energy blocks**: the Energy Connector and capacitors use stained glass, so their tier is easy to spot.

  | Block | Material |
  |---|---|
  | Energy Connector | Red Stained Glass |
  | Small Energy Capacitor | Orange Stained Glass |
  | Medium Energy Capacitor | Yellow Stained Glass |
  | Big Energy Capacitor | Lime Stained Glass |
  | Large Energy Capacitor | Green Stained Glass |
  | Carbonado Edged Energy Capacitor | Cyan Stained Glass |
  | Energized Energy Capacitor | Light Blue Stained Glass |

- **Other new looks**: the Electric Press is a Piston, the Electric Press II a Sticky Piston,
  and the EXP Collector an Enchanting Table.
- **No reactor holograms**: Nuclear and Nether Star Reactors no longer show a floating coolant percentage.
- **Works with existing worlds**: items created with the Chinese build (soulbound items, limited-use items,
  spawners) keep working. Blocks that are already placed keep their old look until they are replaced.

## :floppy_disk: Download

| | Latest build |
|---|---|
| **Minecraft** | :video_game: 1.18.x – 26.3.x |
| **Server software** | :page_facing_up: [Paper](https://papermc.io/downloads/paper) or one of its forks (Spigot/CraftBukkit are **not** supported) |
| **Java** | :computer: Java 21+ |
| **Download** | :package: **[Slimefun.jar from the `exp` pre-release](https://github.com/mybezo/Slimefun4-Gugu/releases/tag/exp)** |

Every push to `master` is built automatically and published to the
[`exp` pre-release](https://github.com/mybezo/Slimefun4-Gugu/releases/tag/exp).
Builds of every branch and pull request can also be downloaded from the
[Actions tab](https://github.com/mybezo/Slimefun4-Gugu/actions/workflows/auto-build.yml): open a run and grab the
**Slimefun Beta Build** artifact.

### Installing

1. Stop your server.
2. Put the `Slimefun-*.jar` into your `plugins/` folder (remove any older Slimefun jar first).
3. Optional but recommended: install [SlimeGlue](https://github.com/Xzavier0722/SlimeGlue) for better
   compatibility with protection plugins.
4. Start the server.

> [!TIP]
> **Switching from the Chinese build?** Your existing `plugins/Slimefun/config.yml` is not overwritten,
> so it may still say `language: zh-CN`. Change it to `language: en` and restart.
> If you still have legacy file-based data, run `/sf migrate confirm` once the server has started.

## :computer: Building from source

You need [Git](https://git-scm.com/) and a Java 21 JDK.

```bash
git clone https://github.com/mybezo/Slimefun4-Gugu.git
cd Slimefun4-Gugu
./gradlew build          # on Windows: .\gradlew.bat build
```

The plugin jar ends up in `build/libs/`. Before opening a pull request, run `./gradlew spotlessApply`
to format the code; see [CONTRIBUTING.md](CONTRIBUTING.md) for the rest of the guidelines.

## :electric_plug: Developing addons

Addons are built against the SlimefunGuguProject API, which this edition shares.
Add their Maven repository and Slimefun as a `provided` / `compileOnly` dependency.

<details>
<summary>Gradle (Kotlin DSL)</summary>

```kotlin
repositories {
    maven("https://maven.norain.city/releases")
    // development builds:
    maven("https://maven.norain.city/snapshots")
}

dependencies {
    compileOnly("com.github.SlimefunGuguProject:Slimefun4:DEV-SNAPSHOT")
}
```

</details>

<details>
<summary>Maven</summary>

```xml
<repositories>
    <repository>
        <id>gugu-maven-repo</id>
        <url>https://maven.norain.city/releases</url>
    </repository>
    <!-- development builds -->
    <repository>
        <id>gugu-maven-snapshots</id>
        <url>https://maven.norain.city/snapshots</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.SlimefunGuguProject</groupId>
        <artifactId>Slimefun4</artifactId>
        <version>DEV-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

</details>

## :framed_picture: Screenshots

| Reactors and Power | Automated Factories | Magic Laboratories |
| :---: | :---: | :---: |
| ![](https://raw.githubusercontent.com/Slimefun/Slimefun-Wiki/master/images/showcase1.png) | ![](https://raw.githubusercontent.com/Slimefun/Slimefun-Wiki/master/images/showcase6.png) | ![](https://raw.githubusercontent.com/Slimefun/Slimefun-Wiki/master/images/showcase5.png) |
| *by HamtaBot#0001* | *by Piͭxͪeͤl (mnb)#5049* | *by Kilaruna#4981* |
| ![](https://raw.githubusercontent.com/Slimefun/Slimefun-Wiki/master/images/showcase4.png) | ![](https://raw.githubusercontent.com/Slimefun/Slimefun-Wiki/master/images/showcase3.png) | ![](https://raw.githubusercontent.com/Slimefun/Slimefun-Wiki/master/images/showcase2.png) |
| *by GalaxyKat11#3816* | *by TamThan#7987* | *by Kilaruna#4981* |

*Screenshots from the official Slimefun community.*

## :open_book: Wiki & help

The official Slimefun Wiki covers almost everything in this edition:

- [What is Slimefun?](https://github.com/Slimefun/Slimefun4/wiki/Slimefun-in-a-nutshell)
- [Installing Slimefun](https://github.com/Slimefun/Slimefun4/wiki/Installing-Slimefun)
- [Getting Started](https://github.com/Slimefun/Slimefun4/wiki/Getting-Started)
- [Addons](https://github.com/Slimefun/Slimefun4/wiki/Addons)
- [Developer Guide](https://github.com/Slimefun/Slimefun4/wiki/Developer-Guide)
- [FAQ](https://github.com/Slimefun/Slimefun4/wiki/FAQ) and [Common Issues](https://github.com/Slimefun/Slimefun4/wiki/Common-Issues)

Features specific to the Gugu fork (database storage, `/sf migrate`, `/sf blockdata`, tick tools, …) are documented
on the [SlimefunGuguProject wiki](https://slimefun-wiki.guizhanss.cn/) (Chinese).

## :handshake: Credits

- [TheBusyBiscuit](https://github.com/TheBusyBiscuit) and all
  [contributors of Slimefun 4](https://github.com/Slimefun/Slimefun4/graphs/contributors): the original plugin.
- [SlimefunGuguProject](https://github.com/SlimefunGuguProject/Slimefun4): the fork this edition is based on.
  Consider supporting them on [Afdian](https://afdian.net/a/nora1ncity).

Contributions are welcome! Fork the repository, make your changes on a branch and open a pull request.

## :exclamation: Disclaimer & privacy

Slimefun is licensed under the [GNU GPLv3](LICENSE). It is not affiliated with Mojang Studios or Minecraft.

<details>
<summary>Auto-updater</summary>

The built-in updater only downloads official SlimefunGuguProject release and beta builds.
It is **inactive** for the builds from this repository (their version is `DEV-SNAPSHOT`), so it will never
replace this English edition with the Chinese one. You can also turn it off any time with `options.auto-update`
in `/plugins/Slimefun/config.yml`.

</details>

<details>
<summary>Metrics (bStats)</summary>

Slimefun uses [bStats](https://bstats.org/plugin/bukkit/Slimefun/4574) to collect anonymous usage statistics.
The data cannot be traced back to a server or player and is public:
https://bstats.org/plugin/bukkit/Slimefun/4574.
You can opt out in `/plugins/bStats/config.yml`. See the [bStats privacy policy](https://bstats.org/privacy-policy).

</details>

<details>
<summary>GitHub integration</summary>

The guide's credits and version pages use the [GitHub API](https://api.github.com/) to show public repository
information (contributors, open issues and pull requests, stars, forks, last commit).
No information about your server is sent to GitHub.
Contributor skins are loaded from [textures.minecraft.net](https://www.minecraft.net/en-us).

</details>
