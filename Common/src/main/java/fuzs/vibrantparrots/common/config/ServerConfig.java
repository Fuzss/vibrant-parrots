package fuzs.vibrantparrots.common.config;

import com.google.common.collect.ImmutableSet;
import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.entity.MobSpawnType;

import java.util.Set;

public class ServerConfig implements ConfigCore {
    @Config(description = "Types of freshly spawned vanilla parrots that are allowed to turn into one of the mod's custom parrots.")
    final SpawnReasonsConfig spawnReasons = new SpawnReasonsConfig();
    @Config(description = "Keep all parrots sitting on a player's shoulders when that player is falling.")
    public boolean parrotsStayWhenFalling = true;
    @Config(description = "Keep all parrots sitting on a player's shoulders when that player is flying in creative mode.")
    public boolean parrotsStayWhenFlying = true;
    @Config(description = "Keep all parrots sitting on a player's shoulders when that player is touching water. Only eject the parrots when the player is fully submerged.")
    public boolean dismountParrotsOnlyWhenSubmerged = true;
    @Config(description = "Remove all parrots sitting on a player's shoulders when that player is sneaking.")
    public boolean dismountParrotsWhenSneaking = true;
    @Config(description = "The minimum time after breeding two parrots after which one of them will lay an egg for hatching a parrot chick.")
    @Config.IntRange(min = 0)
    public int minEggLayTimeInMinutes = 10;
    @Config(description = "The maximum time after breeding two parrots after which one of them will lay an egg for hatching a parrot chick.")
    @Config.IntRange(min = 0)
    public int maxEggLayTimeInMinutes = 20;

    public Set<MobSpawnType> volatileSpawnReasons = Set.of();

    public int sampleEggLayTime(RandomSource randomSource) {
        return TimeUtil.rangeOfSeconds(this.minEggLayTimeInMinutes * TimeUtil.SECONDS_PER_MINUTE,
                        Math.max(this.maxEggLayTimeInMinutes, this.minEggLayTimeInMinutes) * TimeUtil.SECONDS_PER_MINUTE)
                .sample(randomSource);
    }

    @Override
    public void afterConfigReload() {
        this.volatileSpawnReasons = this.spawnReasons.toSet();
    }

    private static class SpawnReasonsConfig implements ConfigCore {
        @Config(description = "Parrots that spawn naturally in the world, e.g. through world generation or regular mob spawning.")
        public boolean worldGen = true;
        @Config(description = "Parrots spawned by mob spawners, including trial spawners.")
        public boolean spawner = true;
        @Config(description = "Parrots spawned directly by a player, e.g. via a spawn egg, a dispenser or the /summon command.")
        public boolean playerSpawn = true;
        @Config(description = "Parrots spawned by structures, e.g. through a structure spawner or a datapack.")
        public boolean scripted = false;

        public Set<MobSpawnType> toSet() {
            ImmutableSet.Builder<MobSpawnType> builder = ImmutableSet.builder();

            if (this.worldGen) {
                builder.add(MobSpawnType.NATURAL);
                builder.add(MobSpawnType.CHUNK_GENERATION);
            }

            if (this.spawner) {
                builder.add(MobSpawnType.SPAWNER);
                builder.add(MobSpawnType.TRIAL_SPAWNER);
            }

            if (this.playerSpawn) {
                builder.add(MobSpawnType.SPAWN_EGG);
                builder.add(MobSpawnType.COMMAND);
                builder.add(MobSpawnType.DISPENSER);
            }

            if (this.scripted) {
                // Only include this spawn reason here, as others are not applicable or actively conflict with behaviors in this mod.
                // Specifically, 'TRIGGERED' conflicts with the hatching from thrown eggs mechanic.
                builder.add(MobSpawnType.STRUCTURE);
            }

            return builder.build();
        }
    }
}
