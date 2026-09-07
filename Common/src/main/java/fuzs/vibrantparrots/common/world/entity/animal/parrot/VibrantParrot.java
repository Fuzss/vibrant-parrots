package fuzs.vibrantparrots.common.world.entity.animal.parrot;

import fuzs.puzzleslib.api.entity.v1.VariantUtils;
import fuzs.vibrantparrots.common.init.ModRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

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
        Registry<ParrotVariant> registry = this.registryAccess().registryOrThrow(ModRegistry.PARROT_VARIANT_REGISTRY);
        builder.define(DATA_VARIANT_ID, registry.getAny().orElseThrow());
    }

    @Override
    protected Component getTypeName() {
        return EntityType.PARROT.getDescription();
    }

    @Override
    protected ResourceKey<LootTable> getDefaultLootTable() {
        return EntityType.PARROT.getDefaultLootTable();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData spawnGroupData) {
        VariantUtils.selectVariantToSpawn(level, ModRegistry.PARROT_VARIANT_REGISTRY).ifPresent(this::setParrotVariant);
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag valueInput) {
        super.readAdditionalSaveData(valueInput);
        VariantUtils.readVariant(valueInput, ModRegistry.PARROT_VARIANT_REGISTRY, this.registryAccess())
                .ifPresent(this::setParrotVariant);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.remove("Variant");
        VariantUtils.writeVariant(valueOutput, this.getParrotVariant());
    }

    @Override
    public Variant getVariant() {
        return Variant.RED_BLUE;
    }

    public void setParrotVariant(Holder<ParrotVariant> variant) {
        this.entityData.set(DATA_VARIANT_ID, variant);
    }

    public Holder<ParrotVariant> getParrotVariant() {
        return this.entityData.get(DATA_VARIANT_ID);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob otherParent) {
        VibrantParrot parrot = ModRegistry.PARROT_ENTITY_TYPE.value().create(serverLevel);
        if (parrot != null) {
            parrot.setParrotVariant(this.getParrotVariant());
        }

        return parrot;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Items.PARROT_SPAWN_EGG);
    }
}
