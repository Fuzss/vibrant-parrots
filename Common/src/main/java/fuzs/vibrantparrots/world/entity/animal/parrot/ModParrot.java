package fuzs.vibrantparrots.world.entity.animal.parrot;

import fuzs.vibrantparrots.init.ModRegistry;
import fuzs.vibrantparrots.init.ParrotVariants;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData spawnGroupData) {
        VariantUtils.selectVariantToSpawn(SpawnContext.create(level, this.blockPosition()),
                ModRegistry.PARROT_VARIANT_REGISTRY).ifPresent(this::setParrotVariant);
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ModRegistry.PARROT_FOOD_ITEM_TAG);
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
        if (this.eggTime > DEFAULT_EGG_LAY_TIME) {
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

    @Nullable
    @Override
    public <T> T get(DataComponentType<? extends T> component) {
        return component == ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value() ?
                castComponentValue((DataComponentType<T>) component, new EitherHolder<>(this.getParrotVariant())) :
                super.get(component);
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
                    value).unwrap(this.registryAccess());
            if (optional.isPresent()) {
                this.setParrotVariant(optional.get());
                return true;
            } else {
                return false;
            }
        } else {
            return super.applyImplicitComponent(component, value);
        }
    }
}
