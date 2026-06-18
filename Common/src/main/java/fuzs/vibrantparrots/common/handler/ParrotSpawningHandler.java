package fuzs.vibrantparrots.common.handler;

import fuzs.vibrantparrots.common.init.ModRegistry;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.VibrantParrot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.parrot.Parrot;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public class ParrotSpawningHandler {
    private static final int VANILLA_PARROT_VARIANTS = Parrot.Variant.values().length;
    private static final Set<EntitySpawnReason> VALID_SPAWN_REASONS = Set.of(EntitySpawnReason.NATURAL,
            EntitySpawnReason.CHUNK_GENERATION,
            EntitySpawnReason.SPAWNER,
            EntitySpawnReason.TRIAL_SPAWNER,
            EntitySpawnReason.SPAWN_ITEM_USE,
            EntitySpawnReason.DISPENSER);

    public static void onEntityLoad(Entity entity, ServerLevel serverLevel, boolean isLoadedFromDisk, @Nullable EntitySpawnReason entitySpawnReason) {
        if (!isLoadedFromDisk && entitySpawnReason != null && VALID_SPAWN_REASONS.contains(entitySpawnReason)) {
            if (entity.getType() == EntityTypes.PARROT && entity instanceof Mob mob && getSpawnAsCustomEntityOdds(
                    serverLevel)) {
                mob.convertTo(ModRegistry.PARROT_ENTITY_TYPE.value(),
                        ConversionParams.single(mob, false, false),
                        (VibrantParrot parrot) -> {
                            DifficultyInstance difficulty = serverLevel.getCurrentDifficultyAt(mob.blockPosition());
                            parrot.finalizeSpawn(serverLevel, difficulty, entitySpawnReason, null);
                        });
            }
        }
    }

    public static boolean getSpawnAsCustomEntityOdds(ServerLevel serverLevel) {
        int parrotVariants = serverLevel.registryAccess().lookupOrThrow(ModRegistry.PARROT_VARIANT_REGISTRY).size();
        return serverLevel.getRandom().nextFloat() < parrotVariants / (float) (parrotVariants
                + VANILLA_PARROT_VARIANTS);
    }
}
