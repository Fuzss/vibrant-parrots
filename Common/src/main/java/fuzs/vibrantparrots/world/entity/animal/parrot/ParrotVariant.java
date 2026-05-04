package fuzs.vibrantparrots.world.entity.animal.parrot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.puzzleslib.common.api.util.v1.ValueSerializationHelper;
import fuzs.vibrantparrots.init.ModRegistry;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.variant.*;
import net.minecraft.world.level.storage.ValueInput;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.List;
import java.util.Optional;

/**
 * @see net.minecraft.world.entity.animal.frog.FrogVariant
 */
public record ParrotVariant(ClientAsset.ResourceTexture assetInfo,
                            SpawnPrioritySelectors spawnConditions) implements PriorityProvider<SpawnContext, SpawnCondition> {
    public static final Codec<ParrotVariant> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ClientAsset.ResourceTexture.DEFAULT_FIELD_CODEC.forGetter(ParrotVariant::assetInfo),
                    SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(ParrotVariant::spawnConditions))
            .apply(instance, ParrotVariant::new));
    public static final Codec<ParrotVariant> NETWORK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ClientAsset.ResourceTexture.DEFAULT_FIELD_CODEC.forGetter(ParrotVariant::assetInfo))
            .apply(instance, ParrotVariant::new));
    public static final Codec<Holder<ParrotVariant>> CODEC = RegistryFixedCodec.create(ModRegistry.PARROT_VARIANT_REGISTRY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ParrotVariant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(
            ModRegistry.PARROT_VARIANT_REGISTRY);

    private ParrotVariant(ClientAsset.ResourceTexture assetInfo) {
        this(assetInfo, SpawnPrioritySelectors.EMPTY);
    }

    @Override
    public List<Selector<SpawnContext, SpawnCondition>> selectors() {
        return this.spawnConditions.selectors();
    }

    /**
     * @see Player#extractParrotVariant(CompoundTag)
     */
    public static Optional<Holder<ParrotVariant>> extractParrotVariant(Entity entity, CompoundTag compoundTag) {
        if (!compoundTag.isEmpty()) {
            EntityType<?> entityType = compoundTag.read("id", EntityType.CODEC).orElse(null);
            if (entityType == ModRegistry.PARROT_ENTITY_TYPE.value()) {
                MutableObject<Optional<Holder<ParrotVariant>>> mutableObject = new MutableObject<>(Optional.empty());
                ValueSerializationHelper.load(entity.problemPath(),
                        entity.registryAccess(),
                        compoundTag,
                        (ValueInput valueInput) -> {
                            Optional<Holder<ParrotVariant>> holder = VariantUtils.readVariant(valueInput,
                                    ModRegistry.PARROT_VARIANT_REGISTRY);
                            mutableObject.setValue(holder);
                        });
                return mutableObject.get();
            }
        }

        return Optional.empty();
    }
}
