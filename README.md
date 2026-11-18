# Creeper Hole Infill (Folia)

A Folia plugin that gradually restores blocks removed by creeper explosions. Creeper damage to entities is preserved. The behavior adapts Vane's `WorldRebuild` feature, with region-owned block changes and restoration tasks scheduled through Folia's region scheduler. Vane is licensed under MIT; see [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

## Build

Requires Java 25 and Maven. Build the plugin with:

```sh
mvn package
```

The plugin jar is created at `target/creeper-hole-infill-folia-1.0.0.jar`. Put it in the server's `plugins/` directory and start a Folia 26.3 server. Configuration is generated at `plugins/CreeperHoleInfill/config.yml`.

## Configuration

- `delay`: milliseconds before the first block is restored (default `2000`).
- `delay-falloff`: exponential reduction in the interval as blocks are restored (default `0.175`).
- `minimum-delay`: minimum interval in milliseconds between restored blocks (default `50`). Values are rounded to server ticks.
- `particles.enabled`: show block-colored fragments and end-rod sparkles as blocks return (default `true`).

Only blocks owned by the region processing the explosion are rebuilt by this plugin. Blocks outside that region remain in the vanilla explosion block list and are handled by Folia normally. Restored blocks are skipped if another player or plugin has changed their location since the explosion.
