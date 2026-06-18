package fuzs.vibrantparrots.common.data.tags;

import fuzs.puzzleslib.common.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.common.api.data.v2.tags.AbstractTagProvider;
import fuzs.vibrantparrots.common.init.ModRegistry;
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
                .add(ModRegistry.WHITE_PARROT_EGG_ITEM,
                        ModRegistry.ORANGE_PARROT_EGG_ITEM,
                        ModRegistry.MAGENTA_PARROT_EGG_ITEM,
                        ModRegistry.LIGHT_BLUE_PARROT_EGG_ITEM,
                        ModRegistry.YELLOW_PARROT_EGG_ITEM,
                        ModRegistry.LIME_PARROT_EGG_ITEM,
                        ModRegistry.PINK_PARROT_EGG_ITEM,
                        ModRegistry.GRAY_PARROT_EGG_ITEM,
                        ModRegistry.LIGHT_GRAY_PARROT_EGG_ITEM,
                        ModRegistry.CYAN_PARROT_EGG_ITEM,
                        ModRegistry.PURPLE_PARROT_EGG_ITEM,
                        ModRegistry.BLUE_PARROT_EGG_ITEM,
                        ModRegistry.BROWN_PARROT_EGG_ITEM,
                        ModRegistry.GREEN_PARROT_EGG_ITEM,
                        ModRegistry.RED_PARROT_EGG_ITEM,
                        ModRegistry.BLACK_PARROT_EGG_ITEM);
        this.tag(ItemTags.EGGS).addTag(ModRegistry.PARROT_EGGS_ITEM_TAG);
    }
}
