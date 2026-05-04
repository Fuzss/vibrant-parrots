package fuzs.vibrantparrots.common.world.item;

import fuzs.vibrantparrots.common.handler.ParrotSpawningHandler;
import fuzs.vibrantparrots.common.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class ParrotCageItem extends MobBucketItem {

    public ParrotCageItem(Properties properties) {
        // We use lava as it prevents the entity from being placed into waterloggable blocks.
        // The fluid is not placed, we prevent that via overriding #emptyContents.
        super(EntityType.PARROT, Fluids.LAVA, SoundEvents.BUCKET_EMPTY, properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        InteractionResult interactionResult = super.use(level, player, interactionHand);
        if (interactionResult instanceof InteractionResult.Success success) {
            if (success.heldItemTransformedTo() != null && success.heldItemTransformedTo().is(Items.BUCKET)) {
                return success.heldItemTransformedTo(new ItemStack(ModRegistry.CAGE_ITEM));
            }
        }

        return interactionResult;
    }

    @Override
    public void checkExtraContent(@Nullable LivingEntity livingEntity, Level level, ItemStack itemStack, BlockPos blockPos) {
        if (level instanceof ServerLevel serverLevel) {
            this.type = this.getType(serverLevel, itemStack);
            super.checkExtraContent(livingEntity, level, itemStack, blockPos);
        }
    }

    protected EntityType<? extends Mob> getType(ServerLevel serverLevel, ItemStack itemStack) {
        EntityType<?> entityType = itemStack.get(ModRegistry.ENTITY_TYPE_DATA_COMPONENT_TYPE.value());
        if (entityType != null) {
            return (EntityType<Mob>) entityType;
        } else {
            return ParrotSpawningHandler.getSpawnAsCustomEntityOdds(serverLevel) ?
                    ModRegistry.PARROT_ENTITY_TYPE.value() : EntityType.PARROT;
        }
    }

    @Override
    public boolean emptyContents(@Nullable LivingEntity livingEntity, Level level, BlockPos blockPos, @Nullable BlockHitResult hitResult) {
        return true;
    }

    @Override
    public Fluid getContent() {
        return Fluids.EMPTY;
    }
}
