package fuzs.vibrantparrots.neoforge;

import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.vibrantparrots.common.VibrantParrots;
import fuzs.vibrantparrots.common.data.ModRecipeProvider;
import fuzs.vibrantparrots.common.data.loot.ModGiftLootProvider;
import fuzs.vibrantparrots.common.data.tags.ModDamageTypeTagProvider;
import fuzs.vibrantparrots.common.data.tags.ModEntityTagProvider;
import fuzs.vibrantparrots.common.data.tags.ModItemTagProvider;
import fuzs.vibrantparrots.common.init.ModRegistry;
import fuzs.vibrantparrots.neoforge.init.NeoForgeModRegistry;
import net.neoforged.fml.common.Mod;

@Mod(VibrantParrots.MOD_ID)
public class VibrantParrotsNeoForge {

    public VibrantParrotsNeoForge() {
        NeoForgeModRegistry.bootstrap();
        ModConstructor.construct(VibrantParrots.MOD_ID, VibrantParrots::new);
        DataProviderHelper.registerDataProviders(VibrantParrots.MOD_ID,
                ModRegistry.REGISTRY_SET_BUILDER,
                ModItemTagProvider::new,
                ModEntityTagProvider::new,
                ModDamageTypeTagProvider::new,
                ModGiftLootProvider::new,
                ModRecipeProvider::new);
    }
}
