# Undying

Forge 1.20.1 zombie apocalypse mod. No new mobs, no new blocks, no quests.
Vanilla zombies, rewritten to behave like infected people.

## Design

Three systems, layered:

**Senses** — Zombies have no idea where you are. The world emits Stimuli
(sound, scent, light) and zombies walk toward the strongest one they can
perceive. Vanilla targeting still requires line of sight, so a zombie has to
physically arrive and look at you before it locks on. Mining, sprinting,
explosions and bleeding all give you away. Torchlight gives away your base.

**Pressure** — Zombies do not burn in daylight. They break blocks softer than
the hardness limit, so a dirt hut falls and a stone bunker holds. They do not
place blocks: towering is a game mechanic, not an apocalypse.

**Infection** — A bite starts a countdown that eats max health. Die infected
and a zombie stands up wearing your name. Villagers catch it too, which is
what makes the world feel like it is collapsing without you.

## Performance

Written for weak hardware first.

- `pathingBudgetPerTick` caps how many zombies compute a path per tick. This is
  the single biggest FPS lever — lower it before touching anything else.
- Sense queries are staggered across zombies by entity ID, never all on one tick.
- Stimuli live in memory, not as entities and not in NBT. No tick cost, no save cost.
- Block-breaking scans two positions, not a volume, and skips block entities.

## Building

    ./gradlew build

Output lands in `build/libs/undying-1.0.0.jar`.

Requires JDK 17. This is Forge 1.20.1; it will not run on Fabric.

The first build downloads Forge and decompiles Minecraft, so it needs network
access to `maven.minecraftforge.net` and `libraries.minecraft.net` and takes a
few minutes. Later builds are incremental.

## Config

`config/undying-common.toml`, generated on first launch. Every value is
commented in-file.
