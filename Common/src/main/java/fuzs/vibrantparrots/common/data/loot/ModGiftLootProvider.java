package fuzs.vibrantparrots.common.data.loot;

import com.mojang.datafixers.util.Either;
import fuzs.puzzleslib.common.api.data.v2.AbstractLootProvider;
import fuzs.puzzleslib.common.api.data.v2.core.DataProviderContext;
import fuzs.vibrantparrots.common.init.ModRegistry;
import fuzs.vibrantparrots.common.init.ParrotVariants;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.ParrotVariant;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class ModGiftLootProvider extends AbstractLootProvider.Simple {

    public ModGiftLootProvider(DataProviderContext context) {
        super(LootContextParamSets.GIFT, context);
    }

    @Override
    public void addLootTables() {
        HolderGetter<ParrotVariant> parrotVariantLookup = this.registries()
                .lookupOrThrow(ModRegistry.PARROT_VARIANT_REGISTRY);
        this.add(ModRegistry.PARROT_LAY_LOOT_TABLE,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(AlternativesEntry.alternatives(LootItem.lootTableItem(ModRegistry.WHITE_PARROT_EGG_ITEM.value())
                                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity()
                                                                .components(DataComponentMatchers.Builder.components()
                                                                        .exact(DataComponentExactPredicate.expect(ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value(),
                                                                                Either.right(parrotVariantLookup.getOrThrow(
                                                                                        ParrotVariants.WHITE))))
                                                                        .build()))),
                                        LootItem.lootTableItem(ModRegistry.ORANGE_PARROT_EGG_ITEM.value())
                                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity()
                                                                .components(DataComponentMatchers.Builder.components()
                                                                        .exact(DataComponentExactPredicate.expect(
                                                                                ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value(),
                                                                                Either.right(parrotVariantLookup.getOrThrow(
                                                                                        ParrotVariants.ORANGE))))
                                                                        .build()))),
                                        LootItem.lootTableItem(ModRegistry.MAGENTA_PARROT_EGG_ITEM.value())
                                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity()
                                                                .components(DataComponentMatchers.Builder.components()
                                                                        .exact(DataComponentExactPredicate.expect(
                                                                                ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value(),
                                                                                Either.right(parrotVariantLookup.getOrThrow(
                                                                                        ParrotVariants.MAGENTA))))
                                                                        .build()))),
                                        LootItem.lootTableItem(ModRegistry.LIGHT_BLUE_PARROT_EGG_ITEM.value())
                                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity()
                                                                .components(DataComponentMatchers.Builder.components()
                                                                        .exact(DataComponentExactPredicate.expect(
                                                                                DataComponents.PARROT_VARIANT,
                                                                                Parrot.Variant.YELLOW_BLUE))
                                                                        .build()))),
                                        LootItem.lootTableItem(ModRegistry.YELLOW_PARROT_EGG_ITEM.value())
                                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity()
                                                                .components(DataComponentMatchers.Builder.components()
                                                                        .exact(DataComponentExactPredicate.expect(
                                                                                ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value(),
                                                                                Either.right(parrotVariantLookup.getOrThrow(
                                                                                        ParrotVariants.YELLOW))))
                                                                        .build()))),
                                        LootItem.lootTableItem(ModRegistry.LIME_PARROT_EGG_ITEM.value())
                                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity()
                                                                .components(DataComponentMatchers.Builder.components()
                                                                        .exact(DataComponentExactPredicate.expect(
                                                                                DataComponents.PARROT_VARIANT,
                                                                                Parrot.Variant.GREEN))
                                                                        .build()))),
                                        LootItem.lootTableItem(ModRegistry.PINK_PARROT_EGG_ITEM.value())
                                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity()
                                                                .components(DataComponentMatchers.Builder.components()
                                                                        .exact(DataComponentExactPredicate.expect(
                                                                                ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value(),
                                                                                Either.right(parrotVariantLookup.getOrThrow(
                                                                                        ParrotVariants.PINK))))
                                                                        .build()))),
                                        LootItem.lootTableItem(ModRegistry.GRAY_PARROT_EGG_ITEM.value())
                                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity()
                                                                .components(DataComponentMatchers.Builder.components()
                                                                        .exact(DataComponentExactPredicate.expect(
                                                                                ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value(),
                                                                                Either.right(parrotVariantLookup.getOrThrow(
                                                                                        ParrotVariants.GRAY))))
                                                                        .build()))),
                                        LootItem.lootTableItem(ModRegistry.LIGHT_GRAY_PARROT_EGG_ITEM.value())
                                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity()
                                                                .components(DataComponentMatchers.Builder.components()
                                                                        .exact(DataComponentExactPredicate.expect(
                                                                                DataComponents.PARROT_VARIANT,
                                                                                Parrot.Variant.GRAY))
                                                                        .build()))),
                                        LootItem.lootTableItem(ModRegistry.CYAN_PARROT_EGG_ITEM.value())
                                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity()
                                                                .components(DataComponentMatchers.Builder.components()
                                                                        .exact(DataComponentExactPredicate.expect(
                                                                                ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value(),
                                                                                Either.right(parrotVariantLookup.getOrThrow(
                                                                                        ParrotVariants.CYAN))))
                                                                        .build()))),
                                        LootItem.lootTableItem(ModRegistry.PURPLE_PARROT_EGG_ITEM.value())
                                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity()
                                                                .components(DataComponentMatchers.Builder.components()
                                                                        .exact(DataComponentExactPredicate.expect(
                                                                                ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value(),
                                                                                Either.right(parrotVariantLookup.getOrThrow(
                                                                                        ParrotVariants.PURPLE))))
                                                                        .build()))),
                                        LootItem.lootTableItem(ModRegistry.BLUE_PARROT_EGG_ITEM.value())
                                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity()
                                                                .components(DataComponentMatchers.Builder.components()
                                                                        .exact(DataComponentExactPredicate.expect(
                                                                                DataComponents.PARROT_VARIANT,
                                                                                Parrot.Variant.BLUE))
                                                                        .build()))),
                                        LootItem.lootTableItem(ModRegistry.BROWN_PARROT_EGG_ITEM.value())
                                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity()
                                                                .components(DataComponentMatchers.Builder.components()
                                                                        .exact(DataComponentExactPredicate.expect(
                                                                                ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value(),
                                                                                Either.right(parrotVariantLookup.getOrThrow(
                                                                                        ParrotVariants.BROWN))))
                                                                        .build()))),
                                        LootItem.lootTableItem(ModRegistry.GREEN_PARROT_EGG_ITEM.value())
                                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity()
                                                                .components(DataComponentMatchers.Builder.components()
                                                                        .exact(DataComponentExactPredicate.expect(
                                                                                ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value(),
                                                                                Either.right(parrotVariantLookup.getOrThrow(
                                                                                        ParrotVariants.GREEN))))
                                                                        .build()))),
                                        LootItem.lootTableItem(ModRegistry.RED_PARROT_EGG_ITEM.value())
                                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity()
                                                                .components(DataComponentMatchers.Builder.components()
                                                                        .exact(DataComponentExactPredicate.expect(
                                                                                DataComponents.PARROT_VARIANT,
                                                                                Parrot.Variant.RED_BLUE))
                                                                        .build()))),
                                        LootItem.lootTableItem(ModRegistry.BLACK_PARROT_EGG_ITEM.value())
                                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity()
                                                                .components(DataComponentMatchers.Builder.components()
                                                                        .exact(DataComponentExactPredicate.expect(
                                                                                ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value(),
                                                                                Either.right(parrotVariantLookup.getOrThrow(
                                                                                        ParrotVariants.BLACK))))
                                                                        .build())))))));
    }
}
