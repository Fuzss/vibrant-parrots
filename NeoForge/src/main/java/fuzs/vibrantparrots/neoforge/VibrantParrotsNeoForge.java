package fuzs.vibrantparrots.neoforge;

import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v3.core.DataProviderBuilder;
import fuzs.vibrantparrots.common.VibrantParrots;
import fuzs.vibrantparrots.common.data.ModRecipeProvider;
import fuzs.vibrantparrots.common.data.loot.ModGiftLootProvider;
import fuzs.vibrantparrots.common.data.tags.ModDamageTypeTagsProvider;
import fuzs.vibrantparrots.common.data.tags.ModEntityTagsProvider;
import fuzs.vibrantparrots.common.data.tags.ModItemTagsProvider;
import fuzs.vibrantparrots.common.init.ModRegistry;
import fuzs.vibrantparrots.common.init.ParrotVariants;
import fuzs.vibrantparrots.neoforge.init.NeoForgeModRegistry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.fml.common.Mod;

@Mod(VibrantParrots.MOD_ID)
public class VibrantParrotsNeoForge {

    public VibrantParrotsNeoForge() {
        NeoForgeModRegistry.bootstrap();
        ModConstructor.construct(VibrantParrots.MOD_ID, VibrantParrots::new);
        DataProviderBuilder.of(VibrantParrots.MOD_ID)
                .addWorldBootstrap(ModRegistry.PARROT_VARIANT_REGISTRY, ParrotVariants::bootstrap)
                .addProvider(ModItemTagsProvider::new,
                        ModEntityTagsProvider::new,
                        ModDamageTypeTagsProvider::new)
                .addLootProvider(ModGiftLootProvider::new, LootContextParamSets.GIFT)
                .addRecipeProvider(ModRecipeProvider::new);
    }
}
