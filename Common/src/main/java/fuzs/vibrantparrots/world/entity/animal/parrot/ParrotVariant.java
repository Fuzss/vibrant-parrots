package fuzs.vibrantparrots.world.entity.animal.parrot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.vibrantparrots.init.ModRegistry;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.variant.PriorityProvider;
import net.minecraft.world.entity.variant.SpawnCondition;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;

import java.util.List;

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
}
