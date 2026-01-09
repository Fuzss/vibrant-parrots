package fuzs.vibrantparrots.world.entity.animal.parrot;

import com.mojang.datafixers.util.Either;
import fuzs.vibrantparrots.init.ModRegistry;
import fuzs.vibrantparrots.init.ParrotVariants;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
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
    public static final String TAG_EGG_LAY_TIME = "egg_lay_time";
    public static final UniformInt EGG_LAY_TIME = TimeUtil.rangeOfSeconds(600, 1200);
    public static final int DEFAULT_EGG_LAY_TIME = -1;

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
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0, Parrot.class) {
            @Override
            public boolean canUse() {
                return super.canUse() && !ModParrot.this.hasEgg();
            }
        });
        registerGoals(this);
    }

    public static void registerGoals(Parrot parrot) {
        parrot.goalSelector.removeAllGoals((Goal goal) -> goal instanceof LandOnOwnersShoulderGoal);
        parrot.goalSelector.addGoal(3, new LandOnOwnersShoulderGoal(parrot) {
            @Override
            public boolean canUse() {
                return super.canUse() && !parrot.isBaby();
            }
        });
        parrot.goalSelector.addGoal(1, new TemptGoal(parrot, 1.0, parrot::isFood, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && !parrot.isOrderedToSit();
            }
        });
        parrot.goalSelector.addGoal(1, new FollowParentGoal(parrot, 1.1) {
            @Override
            public boolean canUse() {
                return super.canUse() && !parrot.isOrderedToSit();
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
        InteractionResult interactionResult = mobInteract(this, player, interactionHand);
        return interactionResult != null ? interactionResult : super.mobInteract(player, interactionHand);
    }

    public static @Nullable InteractionResult mobInteract(Parrot parrot, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (parrot.isTame() && parrot.isFood(itemStack) && !itemStack.is(ItemTags.PARROT_POISONOUS_FOOD)) {
            if (parrot.isBaby() || parrot.getAge() == DEFAULT_AGE && parrot.canFallInLove()) {
                return animalInteract(parrot, player, interactionHand);
            }
        }

        return null;
    }

    /**
     * @see Animal#mobInteract(Player, InteractionHand)
     */
    private static InteractionResult animalInteract(Parrot parrot, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (parrot.isFood(itemStack)) {
            int age = parrot.getAge();
            if (player instanceof ServerPlayer serverPlayer) {
                if (age == 0 && parrot.canFallInLove()) {
                    parrot.usePlayerItem(player, interactionHand, itemStack);
                    parrot.setInLove(serverPlayer);
                    parrot.playEatingSound();
                    return InteractionResult.SUCCESS_SERVER;
                }
            }

            if (parrot.isBaby()) {
                parrot.usePlayerItem(player, interactionHand, itemStack);
                parrot.ageUp(getSpeedUpSecondsWhenFeeding(-age), true);
                parrot.playEatingSound();
                return InteractionResult.SUCCESS;
            }

            if (parrot.level().isClientSide()) {
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);
        if (this.isAlive() && !this.isBaby() && this.isInSittingPose() && --this.eggTime == 0) {
            layParrotEgg(this, serverLevel);
        }
    }

    /**
     * @see Chicken#aiStep()
     */
    public static void layParrotEgg(Parrot parrot, ServerLevel serverLevel) {
        if (parrot.dropFromGiftLootTable(serverLevel, ModRegistry.PARROT_LAY_LOOT_TABLE, parrot::spawnAtLocation)) {
            parrot.playSound(SoundEvents.CHICKEN_EGG,
                    1.0F,
                    (parrot.getRandom().nextFloat() - parrot.getRandom().nextFloat()) * 0.2F + 1.0F);
            parrot.gameEvent(GameEvent.ENTITY_PLACE);
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

    @Override
    public boolean canMate(Animal otherAnimal) {
        return canMate(this, otherAnimal);
    }

    /**
     * @see net.minecraft.world.entity.animal.wolf.Wolf#canMate(Animal)
     */
    public static boolean canMate(Parrot parrot, Animal otherAnimal) {
        if (otherAnimal == parrot) {
            return false;
        } else if (!parrot.isTame()) {
            return false;
        } else if (!(otherAnimal instanceof Parrot otherParrot)) {
            return false;
        } else if (!otherParrot.isTame()) {
            return false;
        } else if (otherParrot.isInSittingPose()) {
            return false;
        } else {
            return parrot.isInLove() && otherParrot.isInLove();
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
