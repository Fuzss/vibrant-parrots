package fuzs.vibrantparrots.handler;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.util.v1.EntityHelper;
import fuzs.vibrantparrots.init.ModRegistry;
import fuzs.vibrantparrots.world.entity.ai.goal.ParrotBreedGoal;
import fuzs.vibrantparrots.world.entity.animal.parrot.ModParrot;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.Set;

public class ParrotEggHandler {
    private static final int VANILLA_PARROT_VARIANTS = Parrot.Variant.values().length;
    private static final Set<EntitySpawnReason> VALID_SPAWN_REASONS = Set.of(EntitySpawnReason.NATURAL,
            EntitySpawnReason.CHUNK_GENERATION,
            EntitySpawnReason.SPAWNER,
            EntitySpawnReason.TRIAL_SPAWNER,
            EntitySpawnReason.SPAWN_ITEM_USE,
            EntitySpawnReason.DISPENSER);

    public static EventResult onEntityLoad(Entity entity, ServerLevel serverLevel, boolean isNewlySpawned) {
        if (entity.getType() == EntityType.PARROT && entity instanceof Parrot parrot) {
            EntitySpawnReason entitySpawnReason = EntityHelper.getMobSpawnReason(entity);
            if (isNewlySpawned && entitySpawnReason != null && VALID_SPAWN_REASONS.contains(entitySpawnReason)
                    && getSpawnAsCustomEntityOdds(serverLevel)) {
                parrot.convertTo(ModRegistry.PARROT_ENTITY_TYPE.value(),
                        ConversionParams.single(parrot, true, true),
                        (ModParrot mob) -> {
                            DifficultyInstance difficulty = getCurrentDifficultyAt(serverLevel, entity.blockPosition());
                            mob.finalizeSpawn(serverLevel, difficulty, entitySpawnReason, null);
                        });
                return EventResult.INTERRUPT;
            }

            parrot.goalSelector.addGoal(1, new ParrotBreedGoal(parrot, 1.0));
            ModParrot.registerGoals(parrot);
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

    public static void onEndEntityTick(Entity entity) {
        if (entity.level() instanceof ServerLevel serverLevel && entity.getType() == EntityType.PARROT
                && entity instanceof Parrot parrot) {
            if (parrot.isAlive() && !parrot.isBaby() && parrot.isInSittingPose()) {
                ModRegistry.EGG_LAY_TIME_ATTACHMENT_TYPE.apply(parrot, (Optional<Integer> optional) -> {
                    return optional.map((Integer eggTime) -> {
                        if (--eggTime == 0) {
                            ModParrot.layParrotEgg(parrot, serverLevel);
                        }

                        return eggTime;
                    }).filter((Integer eggTime) -> {
                        return eggTime > ModParrot.DEFAULT_EGG_LAY_TIME;
                    });
                });
            }
        }
    }

    public static EventResultHolder<InteractionResult> onUseEntity(Player player, Level level, InteractionHand interactionHand, Entity entity) {
        if (entity.getType() == EntityType.PARROT && entity instanceof Parrot parrot) {
            InteractionResult interactionResult = ModParrot.mobInteract(parrot, player, interactionHand);
            if (interactionResult != null) {
                return EventResultHolder.interrupt(interactionResult);
            }
        }

        return EventResultHolder.pass();
    }
}
