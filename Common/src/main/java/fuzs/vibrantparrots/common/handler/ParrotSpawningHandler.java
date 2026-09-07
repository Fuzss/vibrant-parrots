package fuzs.vibrantparrots.common.handler;

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

import java.util.Set;

public class ParrotSpawningHandler {
    private static final int VANILLA_PARROT_VARIANTS = Parrot.Variant.values().length;
    private static final Set<MobSpawnType> VALID_SPAWN_REASONS = Set.of(MobSpawnType.NATURAL,
            MobSpawnType.CHUNK_GENERATION,
            MobSpawnType.SPAWNER,
            MobSpawnType.TRIAL_SPAWNER,
            MobSpawnType.SPAWN_EGG,
            MobSpawnType.DISPENSER);

    public static void onEntityLoad(Entity entity, ServerLevel serverLevel, boolean isLoadedFromDisk, @Nullable MobSpawnType entitySpawnReason) {
        if (!isLoadedFromDisk && entitySpawnReason != null && VALID_SPAWN_REASONS.contains(entitySpawnReason)) {
            if (entity.getType() == EntityType.PARROT && entity instanceof Mob mob && getSpawnAsCustomEntityOdds(
                    serverLevel)) {
                VibrantParrot parrot = mob.convertTo(ModRegistry.PARROT_ENTITY_TYPE.value(), false);
                if (parrot != null) {
                    DifficultyInstance difficulty = serverLevel.getCurrentDifficultyAt(mob.blockPosition());
                    parrot.finalizeSpawn(serverLevel, difficulty, entitySpawnReason, null);
                }
            }
        }
    }

    public static boolean getSpawnAsCustomEntityOdds(ServerLevel serverLevel) {
        int parrotVariants = serverLevel.registryAccess().registryOrThrow(ModRegistry.PARROT_VARIANT_REGISTRY).size();
        return serverLevel.getRandom().nextFloat() < parrotVariants / (float) (parrotVariants
                + VANILLA_PARROT_VARIANTS);
    }
}
