package fuzs.vibrantparrots.common.init;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import fuzs.puzzleslib.common.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.common.api.attachment.v4.DataAttachmentType;
import fuzs.puzzleslib.common.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.common.api.init.v3.tags.TagFactory;
import fuzs.puzzleslib.common.api.network.v4.PlayerSet;
import fuzs.vibrantparrots.common.VibrantParrots;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.ParrotVariant;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.VibrantParrot;
import fuzs.vibrantparrots.common.world.entity.projectile.throwableitemprojectile.ThrownParrotEgg;
import fuzs.vibrantparrots.common.world.item.ParrotCageItem;
import fuzs.vibrantparrots.common.world.item.ParrotEggItem;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Optional;
import java.util.OptionalInt;

public class ModRegistry {
    public static final ResourceKey<Registry<ParrotVariant>> PARROT_VARIANT_REGISTRY = ResourceKey.createRegistryKey(
            VibrantParrots.id("parrot_variant"));
    public static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder().add(PARROT_VARIANT_REGISTRY,
            ParrotVariants::bootstrap);

    static final RegistryManager REGISTRIES = RegistryManager.from(VibrantParrots.MOD_ID);
    public static final Holder.Reference<DataComponentType<Either<Parrot.Variant, Holder<ParrotVariant>>>> PARROT_VARIANT_DATA_COMPONENT_TYPE = REGISTRIES.registerDataComponentType(
            "parrot/variant",
            (DataComponentType.Builder<Either<Parrot.Variant, Holder<ParrotVariant>>> builder) -> {
                return builder.persistent(Codec.either(Parrot.Variant.CODEC, ParrotVariant.CODEC))
                        .networkSynchronized(ByteBufCodecs.either(Parrot.Variant.STREAM_CODEC,
                                ParrotVariant.STREAM_CODEC));
            });
    /**
     * @see DataComponents#ENTITY_DATA
     */
    public static final Holder.Reference<DataComponentType<EntityType<?>>> ENTITY_TYPE_DATA_COMPONENT_TYPE = REGISTRIES.registerDataComponentType(
            "entity_type",
            (DataComponentType.Builder<EntityType<?>> builder) -> {
                return builder.persistent(EntityType.CODEC);
            });
    public static final Holder.Reference<EntityDataSerializer<Holder<ParrotVariant>>> PARROT_VARIANT_ENTITY_DATA_SERIALIZER = REGISTRIES.registerEntityDataSerializer(
            "parrot/variant",
            () -> {
                return EntityDataSerializer.forValueType(ParrotVariant.STREAM_CODEC);
            });
    /**
     * @see EntityType#PARROT
     */
    public static final Holder.Reference<EntityType<VibrantParrot>> PARROT_ENTITY_TYPE = REGISTRIES.register(Registries.ENTITY_TYPE,
            "parrot",
            () -> {
                return EntityType.Builder.of(VibrantParrot::new, MobCategory.CREATURE)
                        .sized(0.5F, 0.9F)
                        .eyeHeight(0.54F)
                        .passengerAttachments(0.4625F)
                        .clientTrackingRange(8)
                        .build(EntityTypes.PARROT.builtInRegistryHolder().key());
            });
    /**
     * @see EntityType#EGG
     */
    public static final Holder.Reference<EntityType<ThrownParrotEgg>> PARROT_EGG_ENTITY_TYPE = REGISTRIES.registerEntityType(
            "parrot_egg",
            () -> {
                return EntityType.Builder.<ThrownParrotEgg>of(ThrownParrotEgg::new, MobCategory.MISC)
                        .noLootTable()
                        .sized(0.25F, 0.25F)
                        .clientTrackingRange(4)
                        .updateInterval(10);
            });
    public static final Holder.Reference<Item> BIRD_CAGE_ITEM = REGISTRIES.registerItem("bird_cage",
            () -> new Item.Properties().stacksTo(16));
    public static final Holder.Reference<Item> PARROT_CAGE_ITEM = REGISTRIES.whenOnFabricLike()
            .registerItem("parrot_cage", ParrotCageItem::new, ModRegistry::parrotCageProperties);
    public static final Holder.Reference<Item> WHITE_PARROT_EGG_ITEM = REGISTRIES.registerItem("white_parrot_egg",
            ParrotEggItem::new,
            () -> parrotEggProperties(ParrotVariants.WHITE));
    public static final Holder.Reference<Item> ORANGE_PARROT_EGG_ITEM = REGISTRIES.registerItem("orange_parrot_egg",
            ParrotEggItem::new,
            () -> parrotEggProperties(ParrotVariants.ORANGE));
    public static final Holder.Reference<Item> MAGENTA_PARROT_EGG_ITEM = REGISTRIES.registerItem("magenta_parrot_egg",
            ParrotEggItem::new,
            () -> parrotEggProperties(ParrotVariants.MAGENTA));
    public static final Holder.Reference<Item> LIGHT_BLUE_PARROT_EGG_ITEM = REGISTRIES.registerItem(
            "light_blue_parrot_egg",
            ParrotEggItem::new,
            () -> parrotEggProperties(Parrot.Variant.YELLOW_BLUE));
    public static final Holder.Reference<Item> YELLOW_PARROT_EGG_ITEM = REGISTRIES.registerItem("yellow_parrot_egg",
            ParrotEggItem::new,
            () -> parrotEggProperties(ParrotVariants.YELLOW));
    public static final Holder.Reference<Item> LIME_PARROT_EGG_ITEM = REGISTRIES.registerItem("lime_parrot_egg",
            ParrotEggItem::new,
            () -> parrotEggProperties(Parrot.Variant.GREEN));
    public static final Holder.Reference<Item> PINK_PARROT_EGG_ITEM = REGISTRIES.registerItem("pink_parrot_egg",
            ParrotEggItem::new,
            () -> parrotEggProperties(ParrotVariants.PINK));
    public static final Holder.Reference<Item> GRAY_PARROT_EGG_ITEM = REGISTRIES.registerItem("gray_parrot_egg",
            ParrotEggItem::new,
            () -> parrotEggProperties(ParrotVariants.GRAY));
    public static final Holder.Reference<Item> LIGHT_GRAY_PARROT_EGG_ITEM = REGISTRIES.registerItem(
            "light_gray_parrot_egg",
            ParrotEggItem::new,
            () -> parrotEggProperties(Parrot.Variant.GRAY));
    public static final Holder.Reference<Item> CYAN_PARROT_EGG_ITEM = REGISTRIES.registerItem("cyan_parrot_egg",
            ParrotEggItem::new,
            () -> parrotEggProperties(ParrotVariants.CYAN));
    public static final Holder.Reference<Item> PURPLE_PARROT_EGG_ITEM = REGISTRIES.registerItem("purple_parrot_egg",
            ParrotEggItem::new,
            () -> parrotEggProperties(ParrotVariants.PURPLE));
    public static final Holder.Reference<Item> BLUE_PARROT_EGG_ITEM = REGISTRIES.registerItem("blue_parrot_egg",
            ParrotEggItem::new,
            () -> parrotEggProperties(Parrot.Variant.BLUE));
    public static final Holder.Reference<Item> BROWN_PARROT_EGG_ITEM = REGISTRIES.registerItem("brown_parrot_egg",
            ParrotEggItem::new,
            () -> parrotEggProperties(ParrotVariants.BROWN));
    public static final Holder.Reference<Item> GREEN_PARROT_EGG_ITEM = REGISTRIES.registerItem("green_parrot_egg",
            ParrotEggItem::new,
            () -> parrotEggProperties(ParrotVariants.GREEN));
    public static final Holder.Reference<Item> RED_PARROT_EGG_ITEM = REGISTRIES.registerItem("red_parrot_egg",
            ParrotEggItem::new,
            () -> parrotEggProperties(Parrot.Variant.RED_BLUE));
    public static final Holder.Reference<Item> BLACK_PARROT_EGG_ITEM = REGISTRIES.registerItem("black_parrot_egg",
            ParrotEggItem::new,
            () -> parrotEggProperties(ParrotVariants.BLACK));
    public static final Holder.Reference<CreativeModeTab> CREATIVE_MODE_TAB = REGISTRIES.registerCreativeModeTab(
            PARROT_CAGE_ITEM);
    public static final ResourceKey<LootTable> PARROT_LAY_LOOT_TABLE = REGISTRIES.registerLootTable(
            "gameplay/parrot_lay");

    static final TagFactory TAGS = TagFactory.make(VibrantParrots.MOD_ID);
    public static final TagKey<Item> PARROT_EGGS_ITEM_TAG = TAGS.registerItemTag("parrot_eggs");
    public static final TagKey<EntityType<?>> PARROTS_ENTITY_TAG = TAGS.registerEntityTypeTag("parrots");
    public static final TagKey<DamageType> DISMOUNTS_PARROTS_DAMAGE_TYPE_TAG = TAGS.registerDamageTypeTag(
            "dismounts_parrots");

    public static final DataAttachmentType<Entity, Optional<Holder<ParrotVariant>>> LEFT_SHOULDER_PARROT_ATTACHMENT_TYPE = DataAttachmentRegistry.<Optional<Holder<ParrotVariant>>>entityBuilder()
            .defaultValue(EntityTypes.PLAYER, Optional.empty())
            .networkSynchronized(ParrotVariant.STREAM_CODEC.apply(ByteBufCodecs::optional), PlayerSet::nearEntity)
            .build(VibrantParrots.id("left_shoulder_parrot"));
    public static final DataAttachmentType<Entity, Optional<Holder<ParrotVariant>>> RIGHT_SHOULDER_PARROT_ATTACHMENT_TYPE = DataAttachmentRegistry.<Optional<Holder<ParrotVariant>>>entityBuilder()
            .defaultValue(EntityTypes.PLAYER, Optional.empty())
            .networkSynchronized(ParrotVariant.STREAM_CODEC.apply(ByteBufCodecs::optional), PlayerSet::nearEntity)
            .build(VibrantParrots.id("right_shoulder_parrot"));
    public static final DataAttachmentType<Entity, OptionalInt> EGG_LAY_TIME_ATTACHMENT_TYPE = DataAttachmentRegistry.<OptionalInt>entityBuilder()
            .defaultValue(Parrot.class, OptionalInt.empty())
            .persistent(ExtraCodecs.optionalEmptyMap(ExtraCodecs.NON_NEGATIVE_INT)
                    .xmap((Optional<Integer> optional) -> {
                        return optional.map(OptionalInt::of).orElseGet(OptionalInt::empty);
                    }, (OptionalInt optional) -> {
                        return optional.isPresent() ? Optional.of(optional.getAsInt()) : Optional.empty();
                    }))
            .build(VibrantParrots.id("egg_lay_time"));

    public static void bootstrap() {
        // NO-OP
    }

    public static Item.Properties parrotCageProperties() {
        return new Item.Properties().stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY);
    }

    private static Item.Properties parrotEggProperties(ResourceKey<ParrotVariant> parrotVariant) {
        return parrotEggProperties().delayedComponent(PARROT_VARIANT_DATA_COMPONENT_TYPE.value(),
                (HolderLookup.Provider context) -> {
                    return Either.right(context.lookupOrThrow(PARROT_VARIANT_REGISTRY).getOrThrow(parrotVariant));
                });
    }

    private static Item.Properties parrotEggProperties(Parrot.Variant parrotVariant) {
        return parrotEggProperties().component(PARROT_VARIANT_DATA_COMPONENT_TYPE.value(), Either.left(parrotVariant));
    }

    private static Item.Properties parrotEggProperties() {
        return new Item.Properties().stacksTo(16);
    }
}
