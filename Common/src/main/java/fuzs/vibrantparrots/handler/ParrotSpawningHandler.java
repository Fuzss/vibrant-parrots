package fuzs.vibrantparrots.handler;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.util.v1.EntityHelper;
import fuzs.vibrantparrots.init.ModRegistry;
import fuzs.vibrantparrots.world.entity.animal.parrot.VibrantParrot;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.OptionalInt;
import java.util.Set;

public class ParrotSpawningHandler {
    private static final int EGG_DROP_TIME = 0;
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

    private static boolean getSpawnAsCustomEntityOdds(ServerLevel serverLevel) {
        int parrotVariants = serverLevel.registryAccess().lookupOrThrow(ModRegistry.PARROT_VARIANT_REGISTRY).size();
        return serverLevel.getRandom().nextFloat() < parrotVariants / (float) (parrotVariants
                + VANILLA_PARROT_VARIANTS);
    }

    private static DifficultyInstance getCurrentDifficultyAt(ServerLevel serverLevel, BlockPos blockPos) {
        return new DifficultyInstance(serverLevel.getDifficulty(),
                serverLevel.getDayTime(),
                0L,
                serverLevel.getMoonBrightness(blockPos));
    }

    public static void tickEggLayTime(Parrot parrot, ServerLevel serverLevel) {
        if (parrot.isAlive() && !parrot.isBaby() && parrot.isInSittingPose()) {
            ModRegistry.EGG_LAY_TIME_ATTACHMENT_TYPE.getOrDefault(parrot, OptionalInt.empty())
                    .ifPresent((int eggLayTime) -> {
                        eggLayTime = tickEggLayTime(parrot, serverLevel, eggLayTime);
                        ModRegistry.EGG_LAY_TIME_ATTACHMENT_TYPE.set(parrot,
                                eggLayTime > EGG_DROP_TIME ? OptionalInt.of(eggLayTime) : OptionalInt.empty());
                    });
        }
    }

    /**
     * @see Chicken#aiStep()
     */
    private static int tickEggLayTime(Parrot parrot, ServerLevel serverLevel, int eggLayTime) {
        if (--eggLayTime == EGG_DROP_TIME) {
            if (parrot.dropFromGiftLootTable(serverLevel, ModRegistry.PARROT_LAY_LOOT_TABLE, parrot::spawnAtLocation)) {
                parrot.playSound(SoundEvents.CHICKEN_EGG,
                        1.0F,
                        (parrot.getRandom().nextFloat() - parrot.getRandom().nextFloat()) * 0.2F + 1.0F);
                parrot.gameEvent(GameEvent.ENTITY_PLACE);
            }
        }

        return eggLayTime;
    }
}
