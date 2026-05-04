package fuzs.vibrantparrots.data.tags;

import fuzs.puzzleslib.common.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.common.api.data.v2.tags.AbstractTagProvider;
import fuzs.vibrantparrots.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypeTagProvider extends AbstractTagProvider<DamageType> {

    public ModDamageTypeTagProvider(DataProviderContext context) {
        super(Registries.DAMAGE_TYPE, context);
    }

    @Override
    public void addTags(HolderLookup.Provider registries) {
        this.tag(ModRegistry.DISMOUNTS_PARROTS_DAMAGE_TYPE_TAG).addTag(DamageTypeTags.PANIC_CAUSES);
    }
}
