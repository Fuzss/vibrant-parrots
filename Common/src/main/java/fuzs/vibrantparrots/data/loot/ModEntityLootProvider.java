package fuzs.vibrantparrots.data.loot;

import fuzs.puzzleslib.api.data.v2.AbstractLootProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.vibrantparrots.init.ModRegistry;

public class ModEntityLootProvider extends AbstractLootProvider.EntityTypes {

    public ModEntityLootProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addLootTables() {
        this.skipValidation(ModRegistry.PARROT_ENTITY_TYPE.value());
    }
}
