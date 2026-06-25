package fuzs.vibrantparrots.common.data.client;

import fuzs.puzzleslib.common.api.client.data.v2.AbstractModelProvider;
import fuzs.puzzleslib.common.api.data.v2.core.DataProviderContext;
import fuzs.vibrantparrots.common.init.ModRegistry;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

public class ModModelProvider extends AbstractModelProvider {

    public ModModelProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addItemModels(ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.generateFlatItem(ModRegistry.BIRD_CAGE_ITEM.value(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModRegistry.PARROT_CAGE_ITEM.value(), ModelTemplates.FLAT_ITEM);
        ModRegistry.PARROT_EGG_ITEM.forEach((Holder.Reference<Item> item) -> {
            itemModelGenerators.generateFlatItem(item.value(), ModelTemplates.FLAT_ITEM);
        });
    }
}
