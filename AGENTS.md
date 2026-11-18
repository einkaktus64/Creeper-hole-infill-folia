# Repository Guidelines

## Project Structure

This is a standalone Folia plugin. Java sources are under `src/main/java/dev/creeperholeinfill/`; plugin metadata and default settings are in `src/main/resources/`. Maven builds the plugin jar into `target/`.

## Build, Test, and Development

Use Java 25 and Maven. Run `mvn package` to compile and package the plugin. The Folia API is a provided dependency, so it is not bundled into the jar. No automated tests are configured yet.

## Coding Style & Naming

Use Java with four-space indentation, descriptive class and method names, and standard Java naming conventions. Keep world access on its owning region; schedule delayed block operations with Folia's region scheduler. Do not replace Folia schedulers with Bukkit's main-thread scheduler.

## Testing Guidelines

There is no test framework or test suite yet. For gameplay changes, describe manual verification on Folia, especially explosions near region boundaries and blocks changed by players before restoration.

## Commits & Pull Requests

The available Git history contains only `Initial commit`, so no established commit-message convention can be inferred. Use short, imperative commit subjects that describe one change. Pull requests should explain the behavior changed, provide relevant test or manual verification results, and link related issues when applicable. Include screenshots or video only when a change has a visible in-game effect.

## Configuration & Security

Do not commit server credentials, private configuration, or generated server data. Keep any future local configuration out of version control and provide safe example values if configuration files are introduced.
