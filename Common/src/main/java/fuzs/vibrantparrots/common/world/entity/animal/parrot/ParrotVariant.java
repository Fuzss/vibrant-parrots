package fuzs.vibrantparrots.common.world.entity.animal.parrot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.puzzleslib.api.core.v2.ClientAsset;
import fuzs.puzzleslib.api.util.v1.CompoundTagHelper;
import fuzs.vibrantparrots.common.init.ModRegistry;
import net.minecraft.advancements.critereon.EntitySubPredicates;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * @see net.minecraft.world.entity.animal.frog.FrogVariant
 */
public record ParrotVariant(ClientAsset.ResourceTexture assetInfo) {
    public static final Codec<ParrotVariant> DIRECT_CODEC = RecordCodecBuilder.create((RecordCodecBuilder.Instance<ParrotVariant> instance) -> instance.group(
                    ClientAsset.ResourceTexture.DEFAULT_FIELD_CODEC.forGetter(ParrotVariant::assetInfo))
            .apply(instance, ParrotVariant::new));
    public static final Codec<ParrotVariant> NETWORK_CODEC = RecordCodecBuilder.create((RecordCodecBuilder.Instance<ParrotVariant> instance) -> instance.group(
                    ClientAsset.ResourceTexture.DEFAULT_FIELD_CODEC.forGetter(ParrotVariant::assetInfo))
            .apply(instance, ParrotVariant::new));
    public static final Codec<Holder<ParrotVariant>> CODEC = RegistryFixedCodec.create(ModRegistry.PARROT_VARIANT_REGISTRY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ParrotVariant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(
            ModRegistry.PARROT_VARIANT_REGISTRY);
    public static final EntitySubPredicates.EntityVariantPredicateType<Holder<ParrotVariant>> PREDICATE_TYPE = EntitySubPredicates.EntityVariantPredicateType.create(
            CODEC,
            (Entity entity) -> entity instanceof VibrantParrot parrot ? Optional.of(parrot.getParrotVariant()) :
                    Optional.empty());

    /**
     * @see Player#extractParrotVariant(CompoundTag)
     */
    public static Optional<Holder<ParrotVariant>> extractParrotVariant(Entity entity, CompoundTag tag) {
        return CompoundTagHelper.read(tag, "id", BuiltInRegistries.ENTITY_TYPE.byNameCodec())
                .filter((EntityType<?> type) -> type == ModRegistry.PARROT_ENTITY_TYPE.value())
                .flatMap((EntityType<?> type) -> {
                    return VariantUtils.readVariant(tag, ModRegistry.PARROT_VARIANT_REGISTRY, entity.registryAccess());
                });
    }
}
