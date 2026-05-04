package fuzs.vibrantparrots.handler;

import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.common.api.util.v1.EntityHelper;
import fuzs.vibrantparrots.init.ModRegistry;
import fuzs.vibrantparrots.world.entity.animal.parrot.VibrantParrot;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.parrot.Parrot;

import java.util.Set;

public class ParrotSpawningHandler {
    private static final int VANILLA_PARROT_VARIANTS = Parrot.Variant.values().length;
    private static final Set<EntitySpawnReason> VALID_SPAWN_REASONS = Set.of(EntitySpawnReason.NATURAL,
            EntitySpawnReason.CHUNK_GENERATION,
            EntitySpawnReason.SPAWNER,
            EntitySpawnReason.TRIAL_SPAWNER,
            EntitySpawnReason.SPAWN_ITEM_USE,
            EntitySpawnReason.DISPENSER);

    public static EventResult onEntityLoad(Entity entity, ServerLevel serverLevel, boolean isNewlySpawned) {
        if (entity.getType() == EntityType.PARROT && entity instanceof Mob mob) {
            EntitySpawnReason entitySpawnReason = EntityHelper.getMobSpawnReason(mob);
            if (isNewlySpawned && entitySpawnReason != null && VALID_SPAWN_REASONS.contains(entitySpawnReason)
                    && getSpawnAsCustomEntityOdds(serverLevel)) {
                mob.convertTo(ModRegistry.PARROT_ENTITY_TYPE.value(),
                        ConversionParams.single(mob, true, true),
                        (VibrantParrot parrot) -> {
                            DifficultyInstance difficulty = getCurrentDifficultyAt(serverLevel, mob.blockPosition());
                            parrot.finalizeSpawn(serverLevel, difficulty, entitySpawnReason, null);
                        });
                return EventResult.INTERRUPT;
            }
        }

        return EventResult.PASS;
    }

    public static boolean getSpawnAsCustomEntityOdds(ServerLevel serverLevel) {
        int parrotVariants = serverLevel.registryAccess().lookupOrThrow(ModRegistry.PARROT_VARIANT_REGISTRY).size();
        return serverLevel.getRandom().nextFloat() < parrotVariants / (float) (parrotVariants
                + VANILLA_PARROT_VARIANTS);
    }

    private static DifficultyInstance getCurrentDifficultyAt(ServerLevel serverLevel, BlockPos blockPos) {
        return new DifficultyInstance(serverLevel.getDifficulty(),
                serverLevel.getOverworldClockTime(),
                0L,
                serverLevel.getMoonBrightness(blockPos));
    }
}
