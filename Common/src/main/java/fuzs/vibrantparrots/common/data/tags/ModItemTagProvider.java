package fuzs.vibrantparrots.common.data.tags;

import fuzs.puzzleslib.common.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.common.api.data.v2.tags.AbstractTagProvider;
import fuzs.vibrantparrots.common.init.ModRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

public class ModItemTagProvider extends AbstractTagProvider<Item> {

    public ModItemTagProvider(DataProviderContext context) {
        super(Registries.ITEM, context);
    }

    @Override
    public void addTags(HolderLookup.Provider registries) {
        this.tag(ModRegistry.PARROT_EGGS_ITEM_TAG)
                .addAll(ModRegistry.PARROT_EGG_ITEM.map(Holder.Reference::key).asList());
        this.tag(ItemTags.EGGS).addTag(ModRegistry.PARROT_EGGS_ITEM_TAG);
    }
}
