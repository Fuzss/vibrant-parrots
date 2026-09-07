package fuzs.vibrantparrots.common.data.tags;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import fuzs.vibrantparrots.common.init.ModRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class ModItemTagsProvider extends AbstractTagProvider<Item> {

    public ModItemTagsProvider(DataProviderContext context) {
        super(Registries.ITEM, context);
    }

    @Override
    public void addTags(HolderLookup.Provider registries) {
        this.tag(ModRegistry.PARROT_EGGS_ITEM_TAG)
                .addAll(ModRegistry.PARROT_EGG_ITEM.map(Holder.Reference::key).asList());
        this.tag("c:eggs").addTag(ModRegistry.PARROT_EGGS_ITEM_TAG);
    }
}
