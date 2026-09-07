package fuzs.vibrantparrots.common.handler;

import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.vibrantparrots.common.init.ModRegistry;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
            if (dropFromGiftLootTable(parrot,
                    serverLevel,
                    ModRegistry.PARROT_LAY_LOOT_TABLE,
                    (ServerLevel level, ItemStack item) -> parrot.spawnAtLocation(item))) {
                parrot.playSound(SoundEvents.CHICKEN_EGG,
                        1.0F,
                        (parrot.getRandom().nextFloat() - parrot.getRandom().nextFloat()) * 0.2F + 1.0F);
                parrot.gameEvent(GameEvent.ENTITY_PLACE);
            }
        }

        return eggLayTime;
    }

    /**
     * Copied from Minecraft 26.1.
     */
    @Deprecated
    public static boolean dropFromGiftLootTable(Entity entity, ServerLevel level, ResourceKey<LootTable> key, BiConsumer<ServerLevel, ItemStack> dropConsumer) {
        return dropFromLootTable(level,
                key,
                params -> params.withParameter(LootContextParams.ORIGIN, entity.position())
                        .withParameter(LootContextParams.THIS_ENTITY, entity)
                        .create(LootContextParamSets.GIFT),
                dropConsumer);
    }

    /**
     * Copied from Minecraft 26.1.
     */
    @Deprecated
    public static boolean dropFromShearingLootTable(Entity entity, ServerLevel level, ResourceKey<LootTable> key, BiConsumer<ServerLevel, ItemStack> dropConsumer) {
        return dropFromLootTable(level,
                key,
                params -> params.withParameter(LootContextParams.ORIGIN, entity.position())
                        .withParameter(LootContextParams.THIS_ENTITY, entity)
                        .create(LootContextParamSets.SHEARING),
                dropConsumer);
    }

    /**
     * Copied from Minecraft 26.1.
     */
    @Deprecated
    public static boolean dropFromLootTable(ServerLevel level, ResourceKey<LootTable> key, Function<LootParams.Builder, LootParams> paramsBuilder, BiConsumer<ServerLevel, ItemStack> dropConsumer) {
        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(key);
        LootParams params = paramsBuilder.apply(new LootParams.Builder(level));
        List<ItemStack> drops = lootTable.getRandomItems(params);
        if (!drops.isEmpty()) {
            drops.forEach(stack -> dropConsumer.accept(level, stack));
            return true;
        } else {
            return false;
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
