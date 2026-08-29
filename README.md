# Slimefun (English Build)

Looking for English (or Original) Version? [**Click here**](https://github.com/Slimefun/Slimefun4/)

Want to download directly? [**Click here**](https://github.com/SlimefunGuguProject/Slimefun4/blob/master/README.md#floppy_disk-download-slimefun4)

Join our QQ group: 807302496

Download [SlimeGlue](https://github.com/Xzavier0722/SlimeGlue/) to ensure compatibility between Slimefun and other protection plugins

### Subscription Plan

⚡ Localizing this project takes a lot of work - please consider supporting us on [afdian](https://afdian.net/a/nora1ncity)

Joining the afdian subscription plan gets you early access to the latest Slimefun builds, as well as priority technical support.
Note: **Non-annual plan** users can get automatically synced source code 15 days after the last public repository update - it is not closed source. We fully comply with the GPLv3 license :)

Learn more about the subscription plan: [Subscription Plan](https://builds.guizhanss.com/sf-subscription)

![Current Version](https://img.shields.io/github/v/release/SlimefunGuguProject/Slimefun4?include_prereleases)
![Build Status](https://builds.guizhanss.com/SlimefunGuguProject/Slimefun4/master/badge.svg)
[![Issues](https://img.shields.io/github/issues/SlimefunGuguProject/Slimefun4.svg?style=popout)](https://github.com/SlimefunGuguProject/Slimefun4/issues)
![Downloads](https://img.shields.io/github/downloads/SlimefunGuguProject/Slimefun4/total)

After using this build, you are **forbidden** from opening new issues on the official issue tracker!
If you insist on doing so, please reproduce it on the **official** dev build and submit the issue in **English**.
Otherwise, please open an issue here or discuss it in our group.

Slimefun is a plugin dedicated to providing a mod-like experience. It offers virtually everything you could imagine, from jetpacks to magical altars!

Slimefun lets every player decide for themselves whether to pursue magic or technology.
From wands to nuclear reactors, we have it all.
On top of that, we also have magical altars, energy networks, and even item transport systems.

This project started in 2013 and is still growing today.
It has grown from a small standalone plugin into a community with thousands of participants and hundreds of contributors.
Slimefun currently adds more than **500 new items/recipes**
([see the history of Slimefun](https://slimefun-wiki.guizhanss.cn/Slimefun-in-a-nutshell)).

On top of that, Slimefun also has a huge variety of addons to choose from!<br>
Check out the [addon list](https://slimefun-wiki.guizhanss.cn/Addons) to find the addon you're looking for!

### Navigation

* **[Download Slimefun 4](#floppy_disk-download-slimefun4)**
* **[Discord Server](#discord)**
* **[Bug Reports](https://github.com/SlimefunGuguProject/Slimefun4/issues)**
* **[Official Wiki](https://github.com/Slimefun/Slimefun4/wiki)**
* **[Unofficial Chinese Wiki](https://slimefun-wiki.guizhanss.cn/)**
* **[FAQ](https://slimefun-wiki.guizhanss.cn/FAQ)**

## :floppy_disk: Download Slimefun4

(See also: [How to install Slimefun](https://slimefun-wiki.guizhanss.cn/Installing-Slimefun))

Slimefun 4 can be **downloaded for free** from the [Guizhan build server](https://builds.guizhanss.com).

Here's the difference between the two build channels:

|                                 | Beta (latest updates)                                                                     | "Stable"                                                                               |
|---------------------------------|---------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| **Supported Minecraft Versions** | :video_game: 1.18.X - 1.20.X                                                              | :video_game: 1.16.X - 1.20.X                                                          |
| **Java Version**                | :computer: **Java 21+**                                                                   | :computer: **Java 17+**                                                               |
| **Auto-update System**          | :heavy_check_mark:                                                                        | :x:                                                                                    |
| **Frequent Updates**            | :heavy_check_mark:                                                                        | :x:                                                                                    |
| **Access to Latest Content**    | :heavy_check_mark:                                                                        | :x:                                                                                    |
| **Discord Support**             | :heavy_check_mark:                                                                        | :x:                                                                                    |
| **Bug Reports**                 | :heavy_check_mark:                                                                        | :x:                                                                                    |
| **Tested Before Release**       | :x:                                                                                        | :heavy_check_mark:                                                                     |
| **Changelog Available**         | :x:                                                                                        | :memo: **[Changelog (English)](https://github.com/Slimefun/Slimefun4/blob/master/CHANGELOG.md)** |
| **Download Link**               | :package: **[Download Latest](https://builds.guizhanss.com/SlimefunGuguProject/Slimefun4/master)** | :package: **[Download "Stable"](https://builds.guizhanss.com/SlimefunGuguProject/Slimefun4/release)** |

**! We recommend using the latest beta build, so you get the newest content updates and bug fixes!**

## :computer: (Development) Adding the dependency

First, add our private repository:

Maven:

```xml
<repository>
    <id>gugu-maven-repo</id>
    <url>https://maven.norain.city/releases</url>
</repository>

```

Dev build:
```xml
<repository>
    <id>gugu-maven-repo</id>
    <url>https://maven.norain.city/snapshots</url>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>
```

Gradle: add the following to your `build.gradle.kts`:

```kotlin
repositories {
    maven {
        url "https://maven.norain.city/releases"
    }
}
```

For the dev build:
```kotlin
repositories {
    maven {
        url "https://maven.norain.city/snapshots"
    }
}
```

Next, add Slimefun 4 as a dependency:
Maven:

```xml
<dependency>
    <groupId>com.github.SlimefunGuguProject</groupId>
    <artifactId>Slimefun4</artifactId>
    <version>DEV-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

Gradle:

```kotlin
dependencies {
    compileOnly("com.github.SlimefunGuguProject:Slimefun4:DEV-SNAPSHOT")
}
```


## :computer: How to build
To build Slimefun4, you must first install [Git](https://git-scm.com/)

Then run `git clone https://github.com/SlimefunGuguProject/Slimefun4.git` to clone the repository locally.

Finally, build it:
- On Windows: `./gradlew.bat build`
- On Unix-like systems: `./gradlew build`

## :framed_picture: Screenshots

So, what does Slimefun actually look like?<br>
We had some folks from our [Discord server](#discord) send us a few screenshots:
| Reactors & Electricity | Amazing Automated Factory | Magic Lab |
| :-------------------------------------------: | :--------------------------------------: | :----------------------------------------: |
| ![](https://raw.githubusercontent.com/Slimefun/Slimefun-Wiki/master/images/showcase1.png) | ![](https://raw.githubusercontent.com/Slimefun/Slimefun-Wiki/master/images/showcase6.png) | ![](https://raw.githubusercontent.com/Slimefun/Slimefun-Wiki/master/images/showcase5.png) |
| *Screenshot by HamtaBot#0001* | *Screenshot by Piͭxͪeͤl (mnb)#5049* | *Screenshot by Kilaruna#4981* |
| ![](https://raw.githubusercontent.com/Slimefun/Slimefun-Wiki/master/images/showcase4.png) | ![](https://raw.githubusercontent.com/Slimefun/Slimefun-Wiki/master/images/showcase3.png) | ![](https://raw.githubusercontent.com/Slimefun/Slimefun-Wiki/master/images/showcase2.png) |
| *Screenshot by GalaxyKat11#3816* | *Screenshot by TamThan#7987* | *Screenshot by Kilaruna#4981* |

## :headphones: Community

### QQ Groups

Discussion QQ group: 807302496 (Slimefun localized build discussion)
Addon discussion QQ group: 205679802 (Slimefun addon discussion, and update notifications from the localization team)

### KOOK Channel

Feel free to join our KOOK [Slime Technology Community](https://kook.top/oqZwh8) server

### Official Discord

You can find Slimefun's community server on Discord and chat with **over 7000** users from all around the world.
Click the logo below to join the Discord server to report issues, share feedback, or discuss anything about this plugin.
The official Slimefun team frequently hosts community events - join us to learn more.
**Note**: the official Slimefun Discord server does **not**
accept any kind of bug reports - please use the [issue tracker](https://github.com/SlimefunGuguProject/Slimefun4/issues) instead!

Before joining, please read the official Discord server's [important rules](https://github.com/Slimefun/Slimefun4/wiki/Discord-Rules).
Anyone who doesn't follow these rules may be kicked or even banned from the server.

<p align="center">
  <a href="https://discord.gg/slimefun">
    <img src="https://discordapp.com/api/guilds/565557184348422174/widget.png?style=banner3" alt="Discord Invite"/>
  </a>
</p>

## :open_book: Wiki

Slimefun has a (detailed and frequently maintained - *ahem*) Wiki for new players,
and you're welcome to help out by contributing to it.
Official Wiki: https://github.com/Slimefun/Slimefun4/wiki
Unofficial Chinese Wiki: https://slimefun-wiki.guizhanss.cn/

#### :star: Useful Articles (Chinese)

* [What is Slimefun?](https://slimefun-wiki.guizhanss.cn/Slimefun-in-a-nutshell)
* [How to install Slimefun](https://slimefun-wiki.guizhanss.cn/Installing-Slimefun)
* [Slimefun 4 addon list](https://slimefun-wiki.guizhanss.cn/Addons)
* [Slimefun 4 addon development tutorial](https://slimefun-wiki.guizhanss.cn/Developer-Guide)
* [Getting Started](https://slimefun-wiki.guizhanss.cn/Getting-Started)
* [FAQ](https://slimefun-wiki.guizhanss.cn/FAQ)
* [Common issues while using the plugin](https://slimefun-wiki.guizhanss.cn/Common-Issues)
* [Help us expand the Wiki!](https://slimefun-wiki.guizhanss.cn/Expanding-the-Wiki)
* [Help us translate Slimefun!](https://slimefun-wiki.guizhanss.cn/Translating-Slimefun)

This Wiki is maintained by @ybw0014 - if you find any missing articles, please report it on the Wiki's Issues page.

## :handshake: Contributing to the project

Slimefun 4 is a project open-sourced under the [GNU GPLv3 license](https://github.com/Slimefun/Slimefun4/blob/master/LICENSE).
Over 100 people have already contributed to this project, and they're all amazing.
We encourage contributing to Slimefun 4 by submitting PRs - your contributions keep the plugin alive <3.

## :exclamation: Disclaimer

Slimefun4 uses several systems to collect usage data, and uses auto-updates to keep you informed about the latest developments of this project.
We do not collect any personal information from you, but some services may still collect or download certain forms of data.

You can opt out of data collection and disable auto-updates at any time.

<details>
  <summary>Auto-update</summary>

This build of Slimefun uses the GitHub API + GuizhanBuild API to check for and download updates.
This feature is enabled by default, but you can disable it in `/plugins/Slimefun/config.yml`.
We strongly recommend keeping auto-update enabled, so you always get the latest features and fixes.

</details>

<details>
  <summary>Metrics & Statistics</summary>

Slimefun4 uses [bStats](https://bstats.org/plugin/bukkit/Slimefun/4574) to collect anonymous information about the plugin.
This is purely for statistical purposes, since we're interested in how servers and players use the plugin.
All publicly available data is anonymous, and we are absolutely unable to trace reported data back to any specific server or player.
All collected data is publicly accessible at: https://bstats.org/plugin/bukkit/Slimefun/4574

You can also disable data collection under `/plugins/bStats/config.yml`.
Learn more in the [bStats Privacy Policy](https://bstats.org/privacy-policy).

</details>

<details>
  <summary>GitHub Integration</summary>

Slimefun4 uses the [GitHub API](https://api.github.com/) to collect usage data about this open-source project.
Rest assured, your Minecraft server information is never sent to GitHub.

This information includes, but is not limited to:

* The list of collaborators, their usernames, and profile links (from the `Slimefun/Slimefun4`, `Slimefun/Slimefun-Wiki`, and `Slimefun/Resourcepack` repositories)
* The number of open issues in the repository
* The number of pending pull requests in the repository
* The repository's star count
* The repository's fork count
* The repository's code size
* The date of the most recent commit to the repository

  </details>

Additionally, the plugin uses [textures.minecraft.net](https://www.minecraft.net/en-us) to fetch contributors' Minecraft skins.
Please note: Slimefun is not affiliated with `Mojang Studios` or Minecraft.
