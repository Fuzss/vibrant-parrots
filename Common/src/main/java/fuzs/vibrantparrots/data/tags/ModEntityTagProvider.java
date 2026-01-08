package fuzs.vibrantparrots.data.tags;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import fuzs.vibrantparrots.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

public class ModEntityTagProvider extends AbstractTagProvider<EntityType<?>> {

    public ModEntityTagProvider(DataProviderContext context) {
        super(Registries.ENTITY_TYPE, context);
    }

    @Override
    public void addTags(HolderLookup.Provider registries) {
        this.tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(ModRegistry.PARROT_ENTITY_TYPE.value());
        this.tag(EntityTypeTags.FOLLOWABLE_FRIENDLY_MOBS).add(ModRegistry.PARROT_ENTITY_TYPE.value());
        this.tag(ModRegistry.PARROTS_ENTITY_TAG).add(EntityType.PARROT, ModRegistry.PARROT_ENTITY_TYPE.value());
    }
}
