package fuzs.vibrantparrots.handler;

import fuzs.puzzleslib.common.api.event.v1.core.EventResultHolder;
import fuzs.vibrantparrots.init.ModRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.OptionalInt;

public class ParrotBehaviorHandler {
    private static final int EGG_DROP_TIME = 0;

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

    public static EventResultHolder<InteractionResult> onUseEntity(Player player, Level level, InteractionHand interactionHand, Entity entity, Vec3 hitVector) {
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
        if (itemStack.is(ModRegistry.CAGE_ITEM) && entity.isAlive()) {
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
