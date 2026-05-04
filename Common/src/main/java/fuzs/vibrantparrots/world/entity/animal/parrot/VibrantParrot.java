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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class VibrantParrot extends Parrot {
    private static final EntityDataAccessor<Holder<ParrotVariant>> DATA_VARIANT_ID = SynchedEntityData.defineId(
            VibrantParrot.class,
            ModRegistry.PARROT_VARIANT_ENTITY_DATA_SERIALIZER.value());

    public VibrantParrot(EntityType<? extends Parrot> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT_ID, VariantUtils.getDefaultOrAny(this.registryAccess(), ParrotVariants.DEFAULT));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData spawnGroupData) {
        VariantUtils.selectVariantToSpawn(SpawnContext.create(level, this.blockPosition()),
                ModRegistry.PARROT_VARIANT_REGISTRY).ifPresent(this::setParrotVariant);
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        VariantUtils.readVariant(valueInput, ModRegistry.PARROT_VARIANT_REGISTRY).ifPresent(this::setParrotVariant);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.discard("Variant");
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
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob otherParent) {
        VibrantParrot parrot = ModRegistry.PARROT_ENTITY_TYPE.value().create(serverLevel, EntitySpawnReason.BREEDING);
        if (parrot != null) {
            parrot.setParrotVariant(this.getParrotVariant());
        }

        return parrot;
    }

    @Nullable
    @Override
    public <T> T get(DataComponentType<? extends T> component) {
        if (component == ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value()) {
            return castComponentValue((DataComponentType<T>) component, Either.right(this.getParrotVariant()));
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
                    value).right();
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
