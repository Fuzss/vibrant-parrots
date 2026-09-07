package fuzs.vibrantparrots.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.vibrantparrots.common.VibrantParrots;
import fuzs.vibrantparrots.common.config.ServerConfig;
import fuzs.vibrantparrots.common.handler.ParrotBehaviorHandler;
import fuzs.vibrantparrots.common.init.ModRegistry;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.VariantUtils;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.VibrantParrot;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(Parrot.class)
abstract class ParrotMixin extends ShoulderRidingEntity implements VariantHolder<Parrot.Variant>, Bucketable {

    protected ParrotMixin(EntityType<? extends ShoulderRidingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    public void registerGoals(CallbackInfo callback) {
        // The goal priorities are really weird since vanilla reuses most of them multiple times.
        // Ideally, just redo all the goals with proper priorities like other animals.
        this.goalSelector.removeAllGoals((Goal goal) -> goal instanceof LandOnOwnersShoulderGoal);
        this.goalSelector.addGoal(3, new LandOnOwnersShoulderGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && !ParrotMixin.this.isBaby();
            }
        });
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0, Parrot.class) {
            @Override
            public boolean canUse() {
                return super.canUse() && ModRegistry.EGG_TIME_ATTACHMENT_TYPE.getOrDefault(this.animal,
                        OptionalInt.empty()).isEmpty();
            }
        });
        this.goalSelector.addGoal(1, new TemptGoal(this, 1.0, this::isFood, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && !ParrotMixin.this.isOrderedToSit();
            }
        });
        this.goalSelector.addGoal(1, new FollowParentGoal(this, 1.1) {
            @Override
            public boolean canUse() {
                return super.canUse() && !ParrotMixin.this.isOrderedToSit();
            }
        });
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    public void aiStep(CallbackInfo callback) {
        if (this.level() instanceof ServerLevel serverLevel) {
            ParrotBehaviorHandler.handleEggTime(Parrot.class.cast(this), serverLevel);
        }
    }

    @ModifyReturnValue(method = "isFood", at = @At("TAIL"))
    public boolean isFood(boolean isFood, ItemStack itemStack) {
        return itemStack.is(ItemTags.PARROT_FOOD);
    }

    @Inject(method = "mobInteract",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Parrot;isFlying()Z"),
            cancellable = true)
    public void mobInteract$1(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> callback) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (this.isFood(itemInHand)) {
            if (this.isTame() && (this.isBaby() || this.getAge() == 0 && this.canFallInLove())) {
                callback.setReturnValue(super.mobInteract(player, hand));
            } else {
                callback.setReturnValue(InteractionResult.PASS);
            }
        }
    }

    @Override
    public void tame(Player player) {
        super.tame(player);
        this.navigation.stop();
        this.setOrderedToSit(true);
    }

    @ModifyReturnValue(method = "isBaby", at = @At("TAIL"))
    public boolean isBaby(boolean isBaby) {
        return this.getAge() < 0;
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel level, Animal partner) {
        this.finalizeSpawnChildFromBreeding(level, partner, null);
        int eggLayTime = VibrantParrots.CONFIG.get(ServerConfig.class).sampleEggLayTime(this.getRandom());
        ModRegistry.EGG_TIME_ATTACHMENT_TYPE.set(this, OptionalInt.of(eggLayTime));
        this.setOrderedToSit(true);
    }

    @ModifyReturnValue(method = "canMate", at = @At("TAIL"))
    public boolean canMate(boolean canMate, Animal partner) {
        if (partner == this) {
            return false;
        } else if (!this.isTame()) {
            return false;
        } else if (!(partner instanceof Parrot otherParrot)) {
            return false;
        } else if (!otherParrot.isTame()) {
            return false;
        } else if (otherParrot.isInSittingPose()) {
            return false;
        } else {
            return this.isInLove() && otherParrot.isInLove();
        }
    }

    @ModifyReturnValue(method = "getBreedOffspring", at = @At("TAIL"))
    public @Nullable AgeableMob getBreedOffspring(@Nullable AgeableMob breedOffspring, ServerLevel level, AgeableMob partner) {
        Parrot parrot = EntityType.PARROT.create(level);
        if (parrot != null) {
            parrot.setVariant(this.getVariant());
        }

        return parrot;
    }

    @Override
    public boolean fromBucket() {
        return false;
    }

    @Override
    public void setFromBucket(boolean fromBucket) {
        // NO-OP
    }

    /**
     * @see TamableAnimal#addAdditionalSaveData(CompoundTag)
     */
    @Override
    public void saveToBucketTag(ItemStack bucket) {
        Bucketable.saveDefaultDataToBucketTag(this, bucket);
        bucket.set(ModRegistry.ENTITY_TYPE_DATA_COMPONENT_TYPE.value(), this.getType());
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, bucket, (CompoundTag tag) -> {
            tag.putInt("Age", this.getAge());
            if (this.getOwnerUUID() != null) {
                tag.putUUID("Owner", this.getOwnerUUID());
            }

            if (VibrantParrot.class.isInstance(this)) {
                VibrantParrot parrot = VibrantParrot.class.cast(this);
                VariantUtils.writeVariant(tag, parrot.getParrotVariant());
            } else {
                tag.putInt("Variant", this.getVariant().getId());
            }
        });
    }

    /**
     * @see TamableAnimal#readAdditionalSaveData(CompoundTag)
     */
    @Override
    public void loadFromBucketTag(CompoundTag tag) {
        Bucketable.loadDefaultDataFromBucketTag(this, tag);
        this.setAge(tag.getInt("Age"));
        if (tag.hasUUID("Owner")) {
            try {
                this.setOwnerUUID(tag.getUUID("Owner"));
                this.setTame(true, false);
            } catch (Throwable throwable) {
                this.setTame(false, true);
            }
        }

        if (VibrantParrot.class.isInstance(this)) {
            VibrantParrot parrot = VibrantParrot.class.cast(this);
            VariantUtils.readVariant(tag, ModRegistry.PARROT_VARIANT_REGISTRY, this.registryAccess())
                    .ifPresent(parrot::setParrotVariant);
        } else {
            this.setVariant(Parrot.Variant.byId(tag.getInt("Variant")));
        }
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ModRegistry.PARROT_CAGE_ITEM);
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.CHAIN_PLACE;
    }
}
