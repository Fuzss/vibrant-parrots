package fuzs.vibrantparrots.common.handler;

import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.util.v1.EntityHelper;
import fuzs.vibrantparrots.common.init.ModRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Optional;
import java.util.OptionalInt;

public class ParrotBehaviorHandler {
    private static final int EGG_DROP_TIME = 0;

    public static void handleEggTime(Parrot parrot, ServerLevel serverLevel) {
        if (parrot.isAlive() && !parrot.isBaby() && parrot.isInSittingPose()) {
            ModRegistry.EGG_TIME_ATTACHMENT_TYPE.apply(parrot, (OptionalInt optional) -> {
                if (optional.isPresent()) {
                    int eggTime = optional.getAsInt() - 1;
                    applyEggTime(parrot, serverLevel, eggTime);
                    if (eggTime > EGG_DROP_TIME) {
                        return OptionalInt.of(eggTime);
                    }
                }

                return OptionalInt.empty();
            });
        }
    }

    /**
     * @see Chicken#aiStep()
     */
    private static void applyEggTime(Parrot parrot, ServerLevel serverLevel, int eggTime) {
        if (eggTime == EGG_DROP_TIME) {
            if (EntityHelper.dropFromGiftLootTable(parrot,
                    serverLevel,
                    ModRegistry.PARROT_LAY_LOOT_TABLE,
                    (ServerLevel level, ItemStack item) -> parrot.spawnAtLocation(item))) {
                parrot.playSound(SoundEvents.CHICKEN_EGG,
                        1.0F,
                        (parrot.getRandom().nextFloat() - parrot.getRandom().nextFloat()) * 0.2F + 1.0F);
                parrot.gameEvent(GameEvent.ENTITY_PLACE);
            }
        } else if (eggTime % 10 == 0) {
            double xd = parrot.getRandom().nextGaussian() * 0.02;
            double xy = parrot.getRandom().nextGaussian() * 0.02;
            double xz = parrot.getRandom().nextGaussian() * 0.02;
            serverLevel.sendParticles(ParticleTypes.HEART,
                    parrot.getRandomX(1.0),
                    parrot.getRandomY() + 0.5,
                    parrot.getRandomZ(1.0),
                    1,
                    xd,
                    xy,
                    xz,
                    0.0);
        }
    }

    public static EventResultHolder<InteractionResult> onUseEntity(Player player, Level level, InteractionHand interactionHand, Entity entity) {
        if (entity instanceof Parrot parrot && entity instanceof Bucketable bucketable && parrot.isTame()
                && parrot.isOwnedBy(player)) {
            return bucketMobPickup(player, interactionHand, parrot, bucketable).map(EventResultHolder::interrupt)
                    .orElseGet(EventResultHolder::pass);
        } else {
            return EventResultHolder.pass();
        }
    }

    /**
     * @see Bucketable#bucketMobPickup(Player, InteractionHand, LivingEntity)
     */
    private static Optional<InteractionResult> bucketMobPickup(Player player, InteractionHand hand, LivingEntity entity, Bucketable bucketable) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(ModRegistry.BIRD_CAGE_ITEM) && entity.isAlive()) {
            entity.playSound(bucketable.getPickupSound(), 1.0F, 1.0F);
            ItemStack itemStack2 = bucketable.getBucketItemStack();
            bucketable.saveToBucketTag(itemStack2);
            ItemStack itemStack3 = ItemUtils.createFilledResult(itemStack, player, itemStack2, false);
            player.setItemInHand(hand, itemStack3);
            entity.discard();
            return Optional.of(InteractionResult.SUCCESS);
        } else {
            return Optional.empty();
        }
    }
}
