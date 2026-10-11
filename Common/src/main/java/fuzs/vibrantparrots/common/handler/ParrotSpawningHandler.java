package fuzs.vibrantparrots.common.handler;

import fuzs.vibrantparrots.common.VibrantParrots;
import fuzs.vibrantparrots.common.config.ServerConfig;
import fuzs.vibrantparrots.common.init.ModRegistry;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.VibrantParrot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Parrot;
import org.jetbrains.annotations.Nullable;

public class ParrotSpawningHandler {
    private static final int VANILLA_PARROT_VARIANTS = Parrot.Variant.values().length;

    public static void onEntityLoad(Entity entity, ServerLevel serverLevel, boolean isLoadedFromDisk, @Nullable MobSpawnType spawnReason) {
        if (!isLoadedFromDisk && isValidForParrotConversion(entity, serverLevel, spawnReason)
                && entity instanceof Mob mob) {
            VibrantParrot parrot = mob.convertTo(ModRegistry.PARROT_ENTITY_TYPE.value(), false);
            if (parrot != null) {
                DifficultyInstance difficulty = serverLevel.getCurrentDifficultyAt(mob.blockPosition());
                parrot.finalizeSpawn(serverLevel, difficulty, spawnReason, null);
            }
        }
    }

    private static boolean isValidForParrotConversion(Entity entity, ServerLevel serverLevel, @Nullable MobSpawnType spawnReason) {
        if (entity.getType() != EntityType.PARROT) {
            return false;
        }
        if (spawnReason == null || !VibrantParrots.CONFIG.get(ServerConfig.class).volatileSpawnReasons.contains(
                spawnReason)) {
            return false;
        }

        return getSpawnAsCustomEntityOdds(serverLevel);
    }

    public static boolean getSpawnAsCustomEntityOdds(ServerLevel serverLevel) {
        int parrotVariants = serverLevel.registryAccess().registryOrThrow(ModRegistry.PARROT_VARIANT_REGISTRY).size();
        return serverLevel.getRandom().nextFloat() < parrotVariants / (float) (parrotVariants
                + VANILLA_PARROT_VARIANTS);
    }
}
