# Contributing Guide

You must read this contributing guide before contributing code to this build of Slimefun.

# Setting up your environment

We provide an automated formatting-check system; please run `./gradlew spotlessApply` to initialize it.

This project already provides a `.editorconfig` to control code style. If you have your own code style preferences, please switch to this repository's style configuration before contributing.

# Setting up your branch

Before starting your contribution, please make sure your code is based on the `dev` branch.

# Commit message convention

This project **requires** the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) commit message convention.

> In short, your commit message needs to include the following:
>
> \<type\>[optional scope]: \<description\>
>
> For example, a commit that adds a new feature should look like: feat(item): add new item to Slimefun

If your commit resolves or addresses an issue, please state this explicitly outside of the main commit message.

> e.g. resolves #114514, fix #114514, etc.

If it's a fix, state it in the main commit message; there's no need to repeat it elsewhere.

The type prefixes we support follow this regex: `(feat(ure)?|fix|docs|style|refactor|ci|chore|perf|build|test|revert|trans)`

Additionally, if the commit is translation-related, the type should be `trans`.

# Code style

**!! This project uses 4-space indentation !!**

Please don't over-compress your code for the sake of fewer characters - Slimefun won't run any faster because you used fewer spaces.

We use Spotless as our code formatting tool. Before committing, you **must** run `./gradlew spotlessCheck spotlessApply` to auto-format your code, otherwise the format checker will block your PR.

# Types of contributions

You can submit fixes, new content, and API changes.

Downstream code now supports submitting API-related code, and developers can depend on this build of Slimefun via jitpack.
