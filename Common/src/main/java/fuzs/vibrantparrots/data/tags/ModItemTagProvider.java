package fuzs.vibrantparrots.data.tags;

import fuzs.puzzleslib.common.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.common.api.data.v2.tags.AbstractTagProvider;
import fuzs.vibrantparrots.init.ModRegistry;
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
                .add(ModRegistry.WHITE_PARROT_EGG_ITEM.value(),
                        ModRegistry.ORANGE_PARROT_EGG_ITEM.value(),
                        ModRegistry.MAGENTA_PARROT_EGG_ITEM.value(),
                        ModRegistry.LIGHT_BLUE_PARROT_EGG_ITEM.value(),
                        ModRegistry.YELLOW_PARROT_EGG_ITEM.value(),
                        ModRegistry.LIME_PARROT_EGG_ITEM.value(),
                        ModRegistry.PINK_PARROT_EGG_ITEM.value(),
                        ModRegistry.GRAY_PARROT_EGG_ITEM.value(),
                        ModRegistry.LIGHT_GRAY_PARROT_EGG_ITEM.value(),
                        ModRegistry.CYAN_PARROT_EGG_ITEM.value(),
                        ModRegistry.PURPLE_PARROT_EGG_ITEM.value(),
                        ModRegistry.BLUE_PARROT_EGG_ITEM.value(),
                        ModRegistry.BROWN_PARROT_EGG_ITEM.value(),
                        ModRegistry.GREEN_PARROT_EGG_ITEM.value(),
                        ModRegistry.RED_PARROT_EGG_ITEM.value(),
                        ModRegistry.BLACK_PARROT_EGG_ITEM.value());
        this.tag(ItemTags.EGGS).addTag(ModRegistry.PARROT_EGGS_ITEM_TAG);
    }
}
