package fuzs.vibrantparrots.fabric;

import fuzs.vibrantparrots.common.VibrantParrots;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class VibrantParrotsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(VibrantParrots.MOD_ID, VibrantParrots::new);
    }
}
