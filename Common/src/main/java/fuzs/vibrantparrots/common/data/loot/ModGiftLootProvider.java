package fuzs.vibrantparrots.common.data.loot;

import com.mojang.datafixers.util.Either;
import fuzs.puzzleslib.common.api.data.v3.loot.AbstractLootSubProvider;
import fuzs.vibrantparrots.common.init.ModRegistry;
import fuzs.vibrantparrots.common.init.ParrotVariants;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.ParrotVariant;
import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.ColorCollection;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.List;

public class ModGiftLootProvider extends AbstractLootSubProvider {

    public ModGiftLootProvider(LootTableSubProvider.Context output) {
        super(output);
    }

    @Override
    public void generate() {
        HolderGetter<ParrotVariant> parrotVariantLookup = this.output.lookup(ModRegistry.PARROT_VARIANT_REGISTRY);
        List<LootPoolEntryContainer.Builder<?>> parrotLayBuilders = ColorCollection.<Holder.Reference<Item>, Either<Parrot.Variant, ResourceKey<ParrotVariant>>, LootPoolEntryContainer.Builder<?>>zipMap(
                ModRegistry.PARROT_EGG_ITEM,
                ParrotVariants.VARIANTS,
                (Holder.Reference<Item> item, Either<Parrot.Variant, ResourceKey<ParrotVariant>> parrotVariant) -> {
                    DataComponentMatchers.Builder matcher = parrotVariant.map((Parrot.Variant variant) -> {
                        return DataComponentMatchers.Builder.components()
                                .exact(DataComponentExactPredicate.expect(DataComponents.PARROT_VARIANT, variant));
                    }, (ResourceKey<ParrotVariant> key) -> {
                        return DataComponentMatchers.Builder.components()
                                .exact(DataComponentExactPredicate.expect(ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value(),
                                        Either.right(parrotVariantLookup.getOrThrow(key))));
                    });
                    return LootItem.lootTableItem(item.value())
                            .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                    EntityPredicate.Builder.entity().components(matcher.build())));
                }).asList();
        this.output.accept(ModRegistry.PARROT_LAY_LOOT_TABLE,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ContextIntProviders.exactly(1))
                                .add(AlternativesEntry.alternatives(parrotLayBuilders.toArray(LootPoolEntryContainer.Builder[]::new)))));
    }
}
