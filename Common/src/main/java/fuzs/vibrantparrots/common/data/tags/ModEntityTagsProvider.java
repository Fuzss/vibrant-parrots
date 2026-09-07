package fuzs.vibrantparrots.common.data.tags;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import fuzs.vibrantparrots.common.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

public class ModEntityTagsProvider extends AbstractTagProvider<EntityType<?>> {

    public ModEntityTagsProvider(DataProviderContext context) {
        super(Registries.ENTITY_TYPE, context);
    }

    @Override
    public void addTags(HolderLookup.Provider registries) {
        this.tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(ModRegistry.PARROT_ENTITY_TYPE);
        this.tag(ModRegistry.PARROTS_ENTITY_TAG).add(EntityType.PARROT).add(ModRegistry.PARROT_ENTITY_TYPE);
    }
}
