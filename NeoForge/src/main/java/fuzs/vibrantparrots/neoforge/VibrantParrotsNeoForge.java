package fuzs.vibrantparrots.neoforge;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.vibrantparrots.VibrantParrots;
import fuzs.vibrantparrots.data.loot.ModGiftLootProvider;
import fuzs.vibrantparrots.data.tags.ModDamageTypeTagProvider;
import fuzs.vibrantparrots.data.tags.ModEntityTagProvider;
import fuzs.vibrantparrots.data.tags.ModItemTagProvider;
import fuzs.vibrantparrots.init.ModRegistry;
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
                ModGiftLootProvider::new);
    }
}
