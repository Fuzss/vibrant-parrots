package fuzs.vibrantparrots.fabric;

import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.vibrantparrots.common.VibrantParrots;
import net.fabricmc.api.ModInitializer;

public class VibrantParrotsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(VibrantParrots.MOD_ID, VibrantParrots::new);
    }
}
