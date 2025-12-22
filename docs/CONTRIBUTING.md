# Contributing to NeoTenet

Thank you for your interest in contributing to NeoTenet! This document provides guidelines and instructions for contributing to the project.

## Code Guidelines

1. **Patch Organization**: Keep patches to Minecraft classes together. If you need a lot of things done, you may either add to relevant forge classes or make a new class. Try not to spread out your patches across multiple disjoint lines, as this makes maintenance of your patches difficult.

2. **Testing**: TODO: Test Mods

3. **Code Style**: Follow the code style of the class you're working in (braces on newlines & spaces instead of tabs in Forge classes, inline brackets in patches, etc).

## Contribution Workflow

Follow these steps to contribute to NeoTenet:

1. **Fork the repository**
   - Create your own fork of the NeoTenet repository on GitHub

2. **Clone your fork**
   - Check out your fork locally on your machine

3. **Set up upstream**
   - Fetch tags from the upstream repository:
     ```bash
     git remote add upstream https://github.com/CelestialArtistry/NeoTenet.git
     git fetch upstream --tags
     ```

4. **Create a branch**
   - Make a new branch for your feature or bug fix

5. **Setup the project**
   - Run the setup command from the project root:
     ```bash
     gradlew setup
     ```

6. **Import into your IDE**
   - Import the project into your IDE (IntelliJ/Eclipse) or reload the Gradle project

7. **Make your changes**
   - Commonly, modify code using mixins. See examples in `src/org/celestial_artistry/neotenet/mixin`
   - If you want to add fields and static methods from CraftBukkit, or other things that mixins cannot do, modify the patched Minecraft sources in `projects/neotenet/src/main/java` as needed
   - The unmodified sources are available in `projects/base/src/main/java` for your reference
   - ⚠️ Do not modify the base sources

8. **Test your changes**
   - Run the game (Runs are available in the IDE)
   - Run `gradlew :tests:runGameTestServer` or `Tests: GameTestServer` from IDE
   - Run `gradlew genPatches` to generate patch-files from the patched sources

9. **Commit and push**
   - Commit your changes with a descriptive message
   - Push your changes to your fork

10. **Create a Pull Request**
    - Open a pull request to the main NeoTenet repository

## Contributor License Agreement

By contributing to NeoTenet, you agree to the following terms:

- You grant CelestialArtistry a license to use your code contributed to the primary codebase (everything **not** under patches) in NeoTenet, under the LGPLv2.1 license.
- You assign copyright ownership of your contributions to the patches codebase (everything under patches) to CelestialArtistry, where it will be licensed under the LGPLv2.1 license.

This is intended as a **legally binding copyright assignment** to the NeoTenet project for contributions under the patches codebase. However, you retain your copyright for all other contributions.