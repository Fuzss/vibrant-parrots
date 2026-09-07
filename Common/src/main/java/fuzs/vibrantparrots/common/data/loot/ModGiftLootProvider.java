package fuzs.vibrantparrots.common.data.loot;

import com.mojang.datafixers.util.Either;
import fuzs.puzzleslib.api.block.v1.ColorCollection;
import fuzs.puzzleslib.api.data.v2.AbstractLootProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.vibrantparrots.common.advancements.criterion.ParrotPredicate;
import fuzs.vibrantparrots.common.init.ModRegistry;
import fuzs.vibrantparrots.common.init.ParrotVariants;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.ParrotVariant;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.List;
import java.util.Optional;

public class ModGiftLootProvider extends AbstractLootProvider.Simple {

    public ModGiftLootProvider(DataProviderContext context) {
        super(LootContextParamSets.GIFT, context);
    }

    @Override
    public void addLootTables() {
        List<LootPoolEntryContainer.Builder<?>> parrotLayBuilders = ColorCollection.<Holder.Reference<Item>, Either<Parrot.Variant, EitherHolder<ParrotVariant>>, LootPoolEntryContainer.Builder<?>>zipMap(
                ModRegistry.PARROT_EGG_ITEM,
                ParrotVariants.VARIANTS,
                (Holder.Reference<Item> item, Either<Parrot.Variant, EitherHolder<ParrotVariant>> variant) -> {
                    ParrotPredicate predicate = new ParrotPredicate(variant.map((Parrot.Variant value) -> {
                        return Optional.of(Either.<Parrot.Variant, Holder<ParrotVariant>>left(value));
                    }, (EitherHolder<ParrotVariant> holder) -> {
                        return holder.unwrap(this.registries())
                                .map(Either::<Parrot.Variant, Holder<ParrotVariant>>right);
                    }));
                    return LootItem.lootTableItem(item.value())
                            .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                    EntityPredicate.Builder.entity().subPredicate(predicate)));
                }).asList();
        this.add(ModRegistry.PARROT_LAY_LOOT_TABLE,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(AlternativesEntry.alternatives(parrotLayBuilders.toArray(LootPoolEntryContainer.Builder[]::new)))));
    }
}
