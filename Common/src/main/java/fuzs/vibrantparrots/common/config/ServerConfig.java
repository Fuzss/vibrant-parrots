package fuzs.vibrantparrots.common.config;

import com.google.common.collect.ImmutableSet;
import fuzs.puzzleslib.common.api.config.v3.Config;
import fuzs.puzzleslib.common.api.config.v3.ConfigCore;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.entity.EntitySpawnReason;

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

    public Set<EntitySpawnReason> volatileSpawnReasons = Set.of();

    public int sampleEggLayTime(RandomSource randomSource) {
        return TimeUtil.rangeOfSeconds(this.minEggLayTimeInMinutes * TimeUtil.SECONDS_PER_MINUTE,
                (this.maxEggLayTimeInMinutes < this.minEggLayTimeInMinutes ? this.minEggLayTimeInMinutes :
                        this.maxEggLayTimeInMinutes) * TimeUtil.SECONDS_PER_MINUTE).sample(randomSource);
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

        public Set<EntitySpawnReason> toSet() {
            ImmutableSet.Builder<EntitySpawnReason> builder = ImmutableSet.builder();

            if (this.worldGen) {
                builder.add(EntitySpawnReason.NATURAL);
                builder.add(EntitySpawnReason.CHUNK_GENERATION);
                // On Fabric world generation spawns are reported with LOAD (the reason the entity was created with),
                // unlike NeoForge which reports the reason refined by Mob#finalizeSpawn.
                builder.add(EntitySpawnReason.LOAD);
            }

            if (this.spawner) {
                builder.add(EntitySpawnReason.SPAWNER);
                builder.add(EntitySpawnReason.TRIAL_SPAWNER);
            }

            if (this.playerSpawn) {
                builder.add(EntitySpawnReason.SPAWN_ITEM_USE);
                builder.add(EntitySpawnReason.COMMAND);
                builder.add(EntitySpawnReason.DISPENSER);
            }

            if (this.scripted) {
                // Only include this spawn reason here, as others are not applicable or actively conflict with behaviors in this mod.
                // Specifically, 'TRIGGERED' conflicts with the hatching from thrown eggs mechanic.
                builder.add(EntitySpawnReason.STRUCTURE);
            }

            return builder.build();
        }
    }
}
