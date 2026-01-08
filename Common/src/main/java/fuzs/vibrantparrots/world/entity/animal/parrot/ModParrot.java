package fuzs.vibrantparrots.world.entity.animal.parrot;

import com.mojang.datafixers.util.Either;
import fuzs.vibrantparrots.init.ModRegistry;
import fuzs.vibrantparrots.init.ParrotVariants;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class ModParrot extends Parrot {
    private static final EntityDataAccessor<Holder<ParrotVariant>> DATA_VARIANT_ID = SynchedEntityData.defineId(
            ModParrot.class,
            ModRegistry.PARROT_VARIANT_ENTITY_DATA_SERIALIZER.value());
    private static final String TAG_EGG_LAY_TIME = "egg_lay_time";
    private static final UniformInt EGG_LAY_TIME = TimeUtil.rangeOfSeconds(600, 1200);
    private static final int DEFAULT_EGG_LAY_TIME = -1;

    private int eggTime = DEFAULT_EGG_LAY_TIME;

    public ModParrot(EntityType<? extends Parrot> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT_ID, VariantUtils.getDefaultOrAny(this.registryAccess(), ParrotVariants.WHITE));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.removeAllGoals((Goal goal) -> goal instanceof LandOnOwnersShoulderGoal);
        this.goalSelector.addGoal(3, new LandOnOwnersShoulderGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && !ModParrot.this.isBaby();
            }
        });
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0) {
            @Override
            public boolean canUse() {
                return super.canUse() && !ModParrot.this.hasEgg();
            }
        });
        this.goalSelector.addGoal(1, new TemptGoal(this, 1.0, this::isFood, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && !ModParrot.this.isOrderedToSit();
            }
        });
        this.goalSelector.addGoal(1, new FollowParentGoal(this, 1.1) {
            @Override
            public boolean canUse() {
                return super.canUse() && !ModParrot.this.isOrderedToSit();
            }
        });
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData spawnGroupData) {
        VariantUtils.selectVariantToSpawn(SpawnContext.create(level, this.blockPosition()),
                ModRegistry.PARROT_VARIANT_REGISTRY).ifPresent(this::setParrotVariant);
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (this.isTame() && this.isFood(itemStack) && !itemStack.is(ItemTags.PARROT_POISONOUS_FOOD)) {
            if (this.isBaby() || this.getAge() == DEFAULT_AGE && this.canFallInLove()) {
                return this.animalInteract(player, interactionHand);
            }
        }

        return super.mobInteract(player, interactionHand);
    }

    /**
     * @see Animal#mobInteract(Player, InteractionHand)
     */
    private InteractionResult animalInteract(Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (this.isFood(itemStack)) {
            int age = this.getAge();
            if (player instanceof ServerPlayer serverPlayer) {
                if (age == 0 && this.canFallInLove()) {
                    this.usePlayerItem(player, interactionHand, itemStack);
                    this.setInLove(serverPlayer);
                    this.playEatingSound();
                    return InteractionResult.SUCCESS_SERVER;
                }
            }

            if (this.isBaby()) {
                this.usePlayerItem(player, interactionHand, itemStack);
                this.ageUp(getSpeedUpSecondsWhenFeeding(-age), true);
                this.playEatingSound();
                return InteractionResult.SUCCESS;
            }

            if (this.level().isClientSide()) {
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void tame(Player player) {
        super.tame(player);
        this.navigation.stop();
        this.setOrderedToSit(true);
    }

    /**
     * @see Animal#aiStep()
     * @see Chicken#aiStep()
     */
    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);
        if (this.isInLove() && this.getInLoveTime() % 10 == 0) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            serverLevel.sendParticles(ParticleTypes.HEART,
                    this.getRandomX(1.0),
                    this.getRandomY() + 0.5,
                    this.getRandomZ(1.0),
                    1,
                    d,
                    e,
                    f,
                    0.0);
        }

        if (this.isAlive() && !this.isBaby() && --this.eggTime == 0) {
            if (this.dropFromGiftLootTable(serverLevel, ModRegistry.PARROT_LAY_LOOT_TABLE, this::spawnAtLocation)) {
                this.playSound(SoundEvents.CHICKEN_EGG,
                        1.0F,
                        (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.gameEvent(GameEvent.ENTITY_PLACE);
            }
        }
    }

    @Override
    public boolean isBaby() {
        return this.getAge() < DEFAULT_AGE;
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel level, Animal mate) {
        this.finalizeSpawnChildFromBreeding(level, mate, null);
        this.eggTime = EGG_LAY_TIME.sample(this.getRandom());
    }

    public boolean hasEgg() {
        return this.eggTime > DEFAULT_EGG_LAY_TIME;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.PARROT_FOOD);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.eggTime = valueInput.getIntOr(TAG_EGG_LAY_TIME, DEFAULT_EGG_LAY_TIME);
        VariantUtils.readVariant(valueInput, ModRegistry.PARROT_VARIANT_REGISTRY).ifPresent(this::setParrotVariant);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.discard("Variant");
        if (this.hasEgg()) {
            valueOutput.putInt(TAG_EGG_LAY_TIME, this.eggTime);
        }

        VariantUtils.writeVariant(valueOutput, this.getParrotVariant());
    }

    @Override
    public Variant getVariant() {
        return Variant.DEFAULT;
    }

    public void setParrotVariant(Holder<ParrotVariant> variant) {
        this.entityData.set(DATA_VARIANT_ID, variant);
    }

    public Holder<ParrotVariant> getParrotVariant() {
        return this.entityData.get(DATA_VARIANT_ID);
    }

    /**
     * @see net.minecraft.world.entity.animal.wolf.Wolf#canMate(Animal)
     */
    @Override
    public boolean canMate(Animal otherAnimal) {
        if (otherAnimal == this) {
            return false;
        } else if (!this.isTame()) {
            return false;
        } else if (!(otherAnimal instanceof ModParrot parrot)) {
            return false;
        } else if (!parrot.isTame()) {
            return false;
        } else if (parrot.isInSittingPose()) {
            return false;
        } else {
            return this.isInLove() && parrot.isInLove();
        }
    }

    @Nullable
    @Override
    public <T> T get(DataComponentType<? extends T> component) {
        if (component == ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value()) {
            return castComponentValue((DataComponentType<T>) component,
                    Either.right(new EitherHolder<>(this.getParrotVariant())));
        } else if (component == DataComponents.PARROT_VARIANT) {
            return null;
        } else {
            return super.get(component);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentGetter) {
        this.applyImplicitComponentIfPresent(componentGetter, ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value());
        super.applyImplicitComponents(componentGetter);
    }

    @Override
    protected <T> boolean applyImplicitComponent(DataComponentType<T> component, T value) {
        if (component == ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value()) {
            Optional<Holder<ParrotVariant>> optional = castComponentValue(ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value(),
                    value).right().flatMap((EitherHolder<ParrotVariant> either) -> {
                return either.unwrap(this.registryAccess());
            });
            if (optional.isPresent()) {
                this.setParrotVariant(optional.get());
                return true;
            } else {
                return false;
            }
        } else if (component == DataComponents.PARROT_VARIANT) {
            return false;
        } else {
            return super.applyImplicitComponent(component, value);
        }
    }
}
