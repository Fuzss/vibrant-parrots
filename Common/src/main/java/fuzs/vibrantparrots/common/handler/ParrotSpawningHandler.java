package fuzs.vibrantparrots.common.handler;

import fuzs.vibrantparrots.common.VibrantParrots;
import fuzs.vibrantparrots.common.config.ServerConfig;
import fuzs.vibrantparrots.common.init.ModRegistry;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.VibrantParrot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.parrot.Parrot;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class ParrotSpawningHandler {
    private static final int VANILLA_PARROT_VARIANTS = Parrot.Variant.values().length;

    public static void onEntityLoad(Entity entity, ServerLevel serverLevel, boolean isLoadedFromDisk, @Nullable EntitySpawnReason spawnReason) {
        if (!isLoadedFromDisk && isValidForParrotConversion(entity, serverLevel, spawnReason)
                && entity instanceof Mob mob) {
            EntitySpawnReason validatedSpawnReason = validateSpawnReason(spawnReason);
            mob.convertTo(ModRegistry.PARROT_ENTITY_TYPE.value(),
                    ConversionParams.single(mob, false, false),
                    (VibrantParrot parrot) -> {
                        DifficultyInstance difficulty = serverLevel.getCurrentDifficultyAt(mob.blockPosition());
                        parrot.finalizeSpawn(serverLevel, difficulty, validatedSpawnReason, null);
                    });
        }
    }

    private static boolean isValidForParrotConversion(Entity entity, ServerLevel serverLevel, @Nullable EntitySpawnReason spawnReason) {
        if (entity.getType() != EntityTypes.PARROT) {
            return false;
        }
        if (spawnReason == null || !VibrantParrots.CONFIG.get(ServerConfig.class).volatileSpawnReasons.contains(
                spawnReason)) {
            return false;
        }

        return getSpawnAsCustomEntityOdds(serverLevel);
    }

    private static EntitySpawnReason validateSpawnReason(@Nullable EntitySpawnReason spawnReason) {
        Objects.requireNonNull(spawnReason, "spawn reason is null");
        return spawnReason == EntitySpawnReason.LOAD ? EntitySpawnReason.NATURAL : spawnReason;
    }

    public static boolean getSpawnAsCustomEntityOdds(ServerLevel serverLevel) {
        int parrotVariants = serverLevel.registryAccess().lookupOrThrow(ModRegistry.PARROT_VARIANT_REGISTRY).size();
        return serverLevel.getRandom().nextFloat() < parrotVariants / (float) (parrotVariants
                + VANILLA_PARROT_VARIANTS);
    }
}
