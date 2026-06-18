package fuzs.vibrantparrots.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.vibrantparrots.common.VibrantParrots;
import fuzs.vibrantparrots.common.config.ServerConfig;
import fuzs.vibrantparrots.common.handler.ParrotBehaviorHandler;
import fuzs.vibrantparrots.common.init.ModRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.parrot.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.OptionalInt;

@Mixin(Parrot.class)
abstract class ParrotMixin extends ShoulderRidingEntity implements Bucketable {

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
                return super.canUse() && ModRegistry.EGG_LAY_TIME_ATTACHMENT_TYPE.getOrDefault(this.animal,
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
            ParrotBehaviorHandler.tickEggLayTime(Parrot.class.cast(this), serverLevel);
        }
    }

    @ModifyReturnValue(method = "isFood", at = @At("TAIL"))
    public boolean isFood(boolean isFood, ItemStack itemStack) {
        return itemStack.is(ItemTags.PARROT_FOOD);
    }

    @Inject(method = "mobInteract",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/parrot/Parrot;isFlying()Z"),
            cancellable = true)
    public void mobInteract$1(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> callback) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (this.isFood(itemInHand)) {
            if (this.isTame() && (this.isBaby() || this.getAge() == DEFAULT_AGE && this.canFallInLove())) {
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

    @ModifyReturnValue(method = "canBeABaby", at = @At("TAIL"))
    public boolean canBeABaby(boolean canBeABaby) {
        return true;
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel level, Animal partner) {
        this.finalizeSpawnChildFromBreeding(level, partner, null);
        int eggLayTime = VibrantParrots.CONFIG.get(ServerConfig.class).sampleEggLayTime(this.getRandom());
        ModRegistry.EGG_LAY_TIME_ATTACHMENT_TYPE.set(this, OptionalInt.of(eggLayTime));
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
        Parrot parrot = EntityTypes.PARROT.create(level, EntitySpawnReason.BREEDING);
        if (parrot != null) {
            parrot.setComponent(DataComponents.PARROT_VARIANT, this.getVariant());
        }

        return parrot;
    }

    @Shadow
    public abstract Parrot.Variant getVariant();

    @Override
    public boolean fromBucket() {
        return false;
    }

    @Override
    public void setFromBucket(boolean fromBucket) {
        // NO-OP
    }

    /**
     * @see TamableAnimal#addAdditionalSaveData(ValueOutput)
     */
    @Override
    public void saveToBucketTag(ItemStack bucket) {
        Bucketable.saveDefaultDataToBucketTag(this, bucket);
        bucket.set(ModRegistry.ENTITY_TYPE_DATA_COMPONENT_TYPE.value(), this.getType());
        bucket.copyFrom(DataComponents.PARROT_VARIANT, this);
        // Just add the custom parrot component here as well, so we do not have to mess with the patched-in interface in the actual mob class.
        bucket.copyFrom(ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value(), this);
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, bucket, (CompoundTag compoundTag) -> {
            compoundTag.putInt("Age", this.getAge());
            compoundTag.storeNullable("Owner", EntityReference.codec(), this.getOwnerReference());
        });
    }

    /**
     * @see TamableAnimal#readAdditionalSaveData(ValueInput)
     */
    @Override
    public void loadFromBucketTag(CompoundTag tag) {
        Bucketable.loadDefaultDataFromBucketTag(this, tag);
        this.setAge(tag.getIntOr("Age", DEFAULT_AGE));
        Optional<EntityReference<LivingEntity>> optional = tag.read("Owner", EntityReference.codec());
        if (optional.isPresent()) {
            this.setOwnerReference(optional.get());
            this.setTame(true, false);
        } else {
            this.setTame(false, true);
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
