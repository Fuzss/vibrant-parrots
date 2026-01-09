package fuzs.vibrantparrots.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import fuzs.vibrantparrots.VibrantParrots;
import fuzs.vibrantparrots.config.ServerConfig;
import fuzs.vibrantparrots.handler.ParrotSpawningHandler;
import fuzs.vibrantparrots.init.ModRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.parrot.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(Parrot.class)
abstract class ParrotMixin extends ShoulderRidingEntity {

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
            ParrotSpawningHandler.tickEggLayTime(Parrot.class.cast(this), serverLevel);
        }
    }

    @ModifyReturnValue(method = "isFood", at = @At("TAIL"))
    public boolean isFood(boolean isFood, ItemStack itemStack) {
        return itemStack.is(ItemTags.PARROT_FOOD);
    }

    @Inject(method = "mobInteract",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/parrot/Parrot;isFlying()Z"),
            cancellable = true)
    public void mobInteract(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> callback, @Local ItemStack itemInHand) {
        if (this.isFood(itemInHand)) {
            if (this.isTame() && (this.isBaby() || this.getAge() == DEFAULT_AGE && this.canFallInLove())) {
                callback.setReturnValue(super.mobInteract(player, interactionHand));
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
        return this.getAge() < DEFAULT_AGE;
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel serverLevel, Animal otherAnimal) {
        this.finalizeSpawnChildFromBreeding(serverLevel, otherAnimal, null);
        int eggLayTime = VibrantParrots.CONFIG.get(ServerConfig.class).sampleEggLayTime(this.getRandom());
        ModRegistry.EGG_LAY_TIME_ATTACHMENT_TYPE.set(this, OptionalInt.of(eggLayTime));
        this.setOrderedToSit(true);
    }

    @ModifyReturnValue(method = "canMate", at = @At("TAIL"))
    public boolean canMate(boolean canMate, Animal otherAnimal) {
        if (otherAnimal == this) {
            return false;
        } else if (!this.isTame()) {
            return false;
        } else if (!(otherAnimal instanceof Parrot otherParrot)) {
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
    public @Nullable AgeableMob getBreedOffspring(@Nullable AgeableMob breedOffspring, ServerLevel serverLevel, AgeableMob otherParent) {
        Parrot parrot = EntityType.PARROT.create(serverLevel, EntitySpawnReason.BREEDING);
        if (parrot != null) {
            parrot.setComponent(DataComponents.PARROT_VARIANT, this.getVariant());
        }

        return parrot;
    }

    @Shadow
    public abstract Parrot.Variant getVariant();
}
