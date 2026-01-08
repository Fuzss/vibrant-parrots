package fuzs.vibrantparrots.init;

import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.init.v3.tags.TagFactory;
import fuzs.vibrantparrots.VibrantParrots;
import fuzs.vibrantparrots.world.entity.animal.parrot.ModParrot;
import fuzs.vibrantparrots.world.entity.animal.parrot.ParrotVariant;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.Item;

public class ModRegistry {
    public static final ResourceKey<Registry<ParrotVariant>> PARROT_VARIANT_REGISTRY = ResourceKey.createRegistryKey(
            VibrantParrots.id("parrot_variant"));
    public static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder().add(PARROT_VARIANT_REGISTRY,
            ParrotVariants::bootstrap);

    static final RegistryManager REGISTRIES = RegistryManager.from(VibrantParrots.MOD_ID);
    /**
     * @see EntityType#PARROT
     */
    public static final Holder.Reference<EntityType<ModParrot>> PARROT_ENTITY_TYPE = REGISTRIES.register(Registries.ENTITY_TYPE,
            "parrot",
            () -> EntityType.Builder.of(ModParrot::new, MobCategory.CREATURE)
                    .sized(0.5F, 0.9F)
                    .eyeHeight(0.54F)
                    .passengerAttachments(0.4625F)
                    .clientTrackingRange(8)
                    .build(EntityType.PARROT.builtInRegistryHolder().key()));
    public static final Holder.Reference<DataComponentType<EitherHolder<ParrotVariant>>> PARROT_VARIANT_DATA_COMPONENT_TYPE = REGISTRIES.registerDataComponentType(
            "parrot/variant",
            builder -> builder.persistent(EitherHolder.codec(PARROT_VARIANT_REGISTRY, ParrotVariant.CODEC))
                    .networkSynchronized(EitherHolder.streamCodec(PARROT_VARIANT_REGISTRY,
                            ParrotVariant.STREAM_CODEC)));
    public static final Holder.Reference<EntityDataSerializer<Holder<ParrotVariant>>> PARROT_VARIANT_ENTITY_DATA_SERIALIZER = REGISTRIES.registerEntityDataSerializer(
            "parrot/variant",
            () -> EntityDataSerializer.forValueType(ParrotVariant.STREAM_CODEC));

    static final TagFactory TAGS = TagFactory.make(VibrantParrots.MOD_ID);
    public static final TagKey<Item> PARROT_FOOD_ITEM_TAG = TAGS.registerItemTag("parrot_food");

    public static void bootstrap() {
        // NO-OP
    }
}
